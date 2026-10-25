package javax.sound.midi;

import java.util.ArrayList;

public class Track {
    private final ArrayList<MidiEvent> events = new ArrayList<MidiEvent>();
    private final MidiEvent end;
    Track() {
        MetaMessage message = new MetaMessage();
        try { message.setMessage(0x2f, new byte[0], 0); }
        catch (InvalidMidiDataException impossible) { throw new IllegalStateException(impossible.toString()); }
        end = new MidiEvent(message, 0);
        events.add(end);
    }
    public synchronized boolean add(MidiEvent event) {
        if (event == null || events.contains(event)) return false;
        end.setTick(Math.max(end.getTick(), event.getTick()));
        if (event.getMessage() instanceof MetaMessage && ((MetaMessage) event.getMessage()).getType() == 0x2f) return true;
        int i = 0;
        while (i < events.size() - 1 && events.get(i).getTick() <= event.getTick()) i++;
        events.add(i, event);
        return true;
    }
    public synchronized boolean remove(MidiEvent event) { return events.remove(event); }
    public synchronized MidiEvent get(int index) { return events.get(index); }
    public synchronized int size() { return events.size(); }
    public synchronized long ticks() {
        long ticks = 0;
        for (MidiEvent event : events) ticks = Math.max(ticks, event.getTick());
        return ticks;
    }
}
