package javax.sound.midi;

public class MidiEvent {
    private final MidiMessage message;
    private long tick;
    public MidiEvent(MidiMessage message, long tick) {
        if (message == null) throw new NullPointerException("message");
        this.message = message;
        this.tick = tick;
    }
    public MidiMessage getMessage() { return message; }
    public long getTick() { return tick; }
    public void setTick(long tick) { this.tick = tick; }
}
