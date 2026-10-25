package javax.sound.midi;

import java.util.ArrayList;

public class Sequence {
    public static final float PPQ = 0.0f, SMPTE_24 = 24.0f, SMPTE_25 = 25.0f,
        SMPTE_30DROP = 29.97f, SMPTE_30 = 30.0f;
    protected float divisionType;
    protected int resolution;
    protected final ArrayList<Track> tracks = new ArrayList<Track>();
    public Sequence(float divisionType, int resolution) throws InvalidMidiDataException {
        if (divisionType != PPQ && divisionType != SMPTE_24 && divisionType != SMPTE_25 &&
            divisionType != SMPTE_30DROP && divisionType != SMPTE_30)
            throw new InvalidMidiDataException("Invalid MIDI division type");
        if (resolution <= 0 || resolution > (divisionType == PPQ ? 0x7fff : 255))
            throw new InvalidMidiDataException("Invalid MIDI resolution");
        this.divisionType = divisionType;
        this.resolution = resolution;
    }
    public Sequence(float divisionType, int resolution, int count) throws InvalidMidiDataException {
        this(divisionType, resolution);
        for (int i = 0; i < count; i++) createTrack();
    }
    public float getDivisionType() { return divisionType; }
    public int getResolution() { return resolution; }
    public Track createTrack() { Track track = new Track(); tracks.add(track); return track; }
    public boolean deleteTrack(Track track) { return tracks.remove(track); }
    public Track[] getTracks() { return tracks.toArray(new Track[tracks.size()]); }
    public long getTickLength() {
        long ticks = 0;
        for (Track track : tracks) ticks = Math.max(ticks, track.ticks());
        return ticks;
    }
}
