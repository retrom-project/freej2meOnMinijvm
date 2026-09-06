package org.mini.awt;

import org.mini.gl.GLMath;

/** Optional bulk operations; older desktop native libraries retain the Java paths. */
public final class NativePixels {
    private static volatile boolean blitAvailable = true;
    private static volatile boolean bytesAvailable = true;
    private NativePixels() {}

    public static boolean blit(int[] source, int sourceOffset, int sourceStride,
                               int[] destination, int destinationOffset, int destinationStride,
                               int width, int height, boolean processAlpha) {
        if (!blitAvailable) return false;
        try { return GLMath.img_argb_blit(source, sourceOffset, sourceStride, destination,
            destinationOffset, destinationStride, width, height, processAlpha); }
        catch (LinkageError unavailable) { blitAvailable = false; return false; }
    }

    public static boolean convert(int[] argb, byte[] rgba, int count, boolean toRgba) {
        if (!bytesAvailable) return false;
        try { return GLMath.img_argb_bytes(argb, rgba, count, toRgba); }
        catch (LinkageError unavailable) { bytesAvailable = false; return false; }
    }
}
