package org.mini.awt;

/** Converts Java's packed ARGB integers to miniJVM ImageMutable RGBA bytes. */
public final class ArgbPixelCodec {
    private ArgbPixelCodec() { }

    public static int read(byte[] rgba, int offset) {
        return ((rgba[offset + 3] & 0xff) << 24)
                | ((rgba[offset] & 0xff) << 16)
                | ((rgba[offset + 1] & 0xff) << 8)
                | (rgba[offset + 2] & 0xff);
    }

    public static void write(byte[] rgba, int offset, int argb) {
        rgba[offset] = (byte) ((argb >>> 16) & 0xff);
        rgba[offset + 1] = (byte) ((argb >>> 8) & 0xff);
        rgba[offset + 2] = (byte) (argb & 0xff);
        rgba[offset + 3] = (byte) ((argb >>> 24) & 0xff);
    }

    /** Source-over on straight (unpremultiplied) ARGB pixels. */
    public static int sourceOver(int source, int destination) {
        int alpha = source >>> 24;
        if (alpha == 0) return destination;
        int destinationAlpha = destination >>> 24;
        if (alpha == 255 || destinationAlpha == 0) return source;
        int inverse = 255 - alpha;
        int destinationWeight = destinationAlpha * inverse;
        int sourceWeight = alpha * 255;
        int weight = sourceWeight + destinationWeight;
        int red = (((source >>> 16) & 255) * sourceWeight + ((destination >>> 16) & 255) * destinationWeight + weight / 2) / weight;
        int green = (((source >>> 8) & 255) * sourceWeight + ((destination >>> 8) & 255) * destinationWeight + weight / 2) / weight;
        int blue = ((source & 255) * sourceWeight + (destination & 255) * destinationWeight + weight / 2) / weight;
        int resultAlpha = (weight + 127) / 255;
        return (resultAlpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
