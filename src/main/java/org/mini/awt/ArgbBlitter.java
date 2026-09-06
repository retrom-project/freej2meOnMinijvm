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
        for (int row = top; row < bottom; row++) {
            int src = (row - y) * sourceWidth + left - x;
            int dst = row * destinationWidth + left;
            for (int col = left; col < right; col++, src++, dst++) {
                int pixel = source[src];
                int alpha = pixel >>> 24;
                if (alpha == 255) destination[dst] = pixel;
                else if (alpha != 0) destination[dst] = blend(pixel, destination[dst], alpha);
            }
        }
    }

    private static int blend(int source, int destination, int alpha) {
        int inverse = 255 - alpha;
        int red = (((source >>> 16) & 255) * alpha + ((destination >>> 16) & 255) * inverse) / 255;
        int green = (((source >>> 8) & 255) * alpha + ((destination >>> 8) & 255) * inverse) / 255;
        int blue = ((source & 255) * alpha + (destination & 255) * inverse) / 255;
        int resultAlpha = alpha + ((destination >>> 24) * inverse) / 255;
        return (resultAlpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
