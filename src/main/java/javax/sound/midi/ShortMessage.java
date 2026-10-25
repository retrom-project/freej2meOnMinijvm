package javax.sound.midi;

public class ShortMessage extends MidiMessage {
    public static final int NOTE_OFF = 0x80, NOTE_ON = 0x90, POLY_PRESSURE = 0xa0,
        CONTROL_CHANGE = 0xb0, PROGRAM_CHANGE = 0xc0, CHANNEL_PRESSURE = 0xd0, PITCH_BEND = 0xe0;
    public static final int MIDI_TIME_CODE = 0xf1, SONG_POSITION_POINTER = 0xf2,
        SONG_SELECT = 0xf3, TUNE_REQUEST = 0xf6, END_OF_EXCLUSIVE = 0xf7,
        TIMING_CLOCK = 0xf8, START = 0xfa, CONTINUE = 0xfb, STOP = 0xfc,
        ACTIVE_SENSING = 0xfe, SYSTEM_RESET = 0xff;

    public ShortMessage() { super(new byte[] {(byte) NOTE_ON, 64, 127}); }
    protected ShortMessage(byte[] data) { super(data); }
    public ShortMessage(int status) throws InvalidMidiDataException { this(); setMessage(status); }
    public ShortMessage(int status, int data1, int data2) throws InvalidMidiDataException {
        this(); setMessage(status, data1, data2);
    }
    public ShortMessage(int command, int channel, int data1, int data2) throws InvalidMidiDataException {
        this(); setMessage(command, channel, data1, data2);
    }
    public void setMessage(int status) throws InvalidMidiDataException {
        if (getDataLength(status) != 0) throw new InvalidMidiDataException("MIDI data bytes required");
        setMessage(status, 0, 0);
    }
    public void setMessage(int command, int channel, int data1, int data2) throws InvalidMidiDataException {
        if (channel < 0 || channel > 15 || command < 0x80 || command > 0xe0 || (command & 15) != 0)
            throw new InvalidMidiDataException("Invalid MIDI command/channel");
        setMessage(command | channel, data1, data2);
    }
    public void setMessage(int status, int data1, int data2) throws InvalidMidiDataException {
        int count = getDataLength(status);
        if (count > 0 && (data1 < 0 || data1 > 127) || count > 1 && (data2 < 0 || data2 > 127))
            throw new InvalidMidiDataException("Invalid MIDI data byte");
        byte[] message = {(byte) status, (byte) data1, (byte) data2};
        setMessage(message, count + 1);
    }
    protected final int getDataLength(int status) throws InvalidMidiDataException {
        if (status >= 0x80 && status <= 0xef) return (status & 0xe0) == 0xc0 ? 1 : 2;
        switch (status) {
            case 0xf1: case 0xf3: return 1;
            case 0xf2: return 2;
            case 0xf6: case 0xf7: case 0xf8: case 0xfa: case 0xfb: case 0xfc: case 0xfe: case 0xff: return 0;
            default: throw new InvalidMidiDataException("Invalid MIDI status: " + status);
        }
    }
    public int getChannel() { return getStatus() & 15; }
    public int getCommand() { return getStatus() & 0xf0; }
    public int getData1() { return length > 1 ? data[1] & 127 : 0; }
    public int getData2() { return length > 2 ? data[2] & 127 : 0; }
    public Object clone() { return new ShortMessage(getMessage()); }
}
