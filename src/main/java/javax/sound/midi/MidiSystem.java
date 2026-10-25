package javax.sound.midi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MidiSystem {
    public static Sequencer getSequencer() throws MidiUnavailableException {
        return new Sequencer();
    }

    public static int write(Sequence sequence, int fileType, OutputStream output) throws IOException {
        if (fileType != 0 && fileType != 1) throw new IllegalArgumentException("Unsupported MIDI file type");
        Track[] tracks = sequence.getTracks();
        if (tracks.length == 0 || tracks.length > 65535) throw new IllegalArgumentException("Invalid MIDI track count");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream file = new DataOutputStream(bytes);
        file.writeInt(0x4d546864);
        file.writeInt(6);
        file.writeShort(fileType);
        file.writeShort(fileType == 0 ? 1 : tracks.length);
        float division = sequence.getDivisionType();
        file.writeShort(division == Sequence.PPQ ? sequence.getResolution() :
            ((-(division == Sequence.SMPTE_30DROP ? 29 : (int) division) & 255) << 8) | sequence.getResolution());
        if (fileType == 0) writeTrack(file, tracks);
        else for (Track track : tracks) writeTrack(file, new Track[] {track});
        byte[] result = bytes.toByteArray();
        output.write(result);
        return result.length;
    }

    private static void writeTrack(DataOutputStream file, Track[] tracks) throws IOException {
        ArrayList<MidiEvent> events = new ArrayList<MidiEvent>();
        long end = 0;
        for (Track track : tracks) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                if (event.getTick() < 0) throw new IllegalArgumentException("Negative MIDI tick");
                end = Math.max(end, event.getTick());
                if (event.getMessage() instanceof MetaMessage && ((MetaMessage) event.getMessage()).getType() == 0x2f) continue;
                events.add(event);
            }
        }
        Collections.sort(events, new Comparator<MidiEvent>() {
            public int compare(MidiEvent a, MidiEvent b) { return Long.compare(a.getTick(), b.getTick()); }
        });
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        long tick = 0;
        for (MidiEvent event : events) {
            writeVariable(bytes, event.getTick() - tick);
            bytes.write(event.getMessage().getMessage());
            tick = event.getTick();
        }
        writeVariable(bytes, end - tick);
        bytes.write(new byte[] {(byte) 0xff, 0x2f, 0});
        file.writeInt(0x4d54726b);
        file.writeInt(bytes.size());
        bytes.writeTo(file);
    }

    private static void writeVariable(OutputStream output, long value) throws IOException {
        if (value < 0 || value > 0x0fffffffL) throw new IllegalArgumentException("MIDI delta exceeds SMF limit");
        long encoded = value & 127;
        while ((value >>>= 7) != 0) encoded = (encoded << 8) | (value & 127) | 128;
        while (true) {
            output.write((int) encoded & 255);
            if ((encoded & 128) == 0) break;
            encoded >>>= 8;
        }
    }
}
