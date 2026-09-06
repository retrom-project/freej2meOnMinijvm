package org.mini.awt;

/** Integer translation blit; keeps source-over alpha and destination clipping. */
public final class ArgbBlitter {
    private ArgbBlitter() {}

    public static void draw(int[] source, int sourceWidth, int sourceHeight,
                            int[] destination, int destinationWidth,
                            int x, int y, int clipLeft, int clipTop, int clipRight, int clipBottom) {
        int left = Math.max(clipLeft, x);
        int top = Math.max(clipTop, y);
        int right = (int) Math.min((long) clipRight, (long) x + sourceWidth);
        int bottom = (int) Math.min((long) clipBottom, (long) y + sourceHeight);
        if (left >= right || top >= bottom) return;
        if (NativePixels.blit(source, (top - y) * sourceWidth + left - x, sourceWidth,
                destination, top * destinationWidth + left, destinationWidth, right - left, bottom - top, true)) return;
        for (int row = top; row < bottom; row++) {
            int src = (row - y) * sourceWidth + left - x;
            int dst = row * destinationWidth + left;
            for (int col = left; col < right; col++, src++, dst++) {
                int pixel = source[src];
                int alpha = pixel >>> 24;
                if (alpha == 255) destination[dst] = pixel;
                else if (alpha != 0) destination[dst] = ArgbPixelCodec.sourceOver(pixel, destination[dst]);
            }
        }
    }

}
