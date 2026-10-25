package javax.sound.midi;

public abstract class MidiMessage implements Cloneable {
    protected byte[] data;
    protected int length;

    protected MidiMessage(byte[] data) {
        this.data = data;
        this.length = data.length;
    }

    protected void setMessage(byte[] message, int size) throws InvalidMidiDataException {
        if (size < 0 || size > message.length) throw new InvalidMidiDataException("Invalid MIDI message length");
        data = new byte[size];
        System.arraycopy(message, 0, data, 0, size);
        length = size;
    }

    public byte[] getMessage() {
        byte[] result = new byte[length];
        System.arraycopy(data, 0, result, 0, length);
        return result;
    }
    public int getStatus() { return length == 0 ? 0 : data[0] & 255; }
    public int getLength() { return length; }
    public abstract Object clone();
}
