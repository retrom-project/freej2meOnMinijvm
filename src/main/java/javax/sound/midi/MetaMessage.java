package javax.sound.midi;

public class MetaMessage extends MidiMessage {
    private int payloadOffset;
    public MetaMessage() { super(new byte[] {(byte) 0xff, 0, 0}); payloadOffset = 3; }
    protected MetaMessage(byte[] data) {
        super(data);
        payloadOffset = 2;
        while (payloadOffset < data.length && (data[payloadOffset++] & 0x80) != 0) { }
    }
    public void setMessage(int type, byte[] payload, int size) throws InvalidMidiDataException {
        if (type < 0 || type > 127 || size < 0 || size > 0x0fffffff || size > (payload == null ? 0 : payload.length))
            throw new InvalidMidiDataException("Invalid MIDI meta message");
        int bytes = 1;
        for (int v = size; (v >>>= 7) != 0;) bytes++;
        payloadOffset = 2 + bytes;
        byte[] message = new byte[payloadOffset + size];
        message[0] = (byte) 0xff;
        message[1] = (byte) type;
        int v = size;
        for (int i = payloadOffset - 1; i >= 2; i--) {
            message[i] = (byte) ((v & 127) | (i == payloadOffset - 1 ? 0 : 0x80));
            v >>>= 7;
        }
        if (size > 0) System.arraycopy(payload, 0, message, payloadOffset, size);
        setMessage(message, message.length);
    }
    public int getType() { return length > 1 ? data[1] & 127 : 0; }
    public byte[] getData() {
        byte[] payload = new byte[length - payloadOffset];
        System.arraycopy(data, payloadOffset, payload, 0, payload.length);
        return payload;
    }
    public Object clone() { return new MetaMessage(getMessage()); }
}
