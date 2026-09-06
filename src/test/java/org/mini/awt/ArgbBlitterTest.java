package org.mini.awt;

import java.util.Random;

public final class ArgbBlitterTest {
    public static void main(String[] args) {
        int[] transparent = {0};
        ArgbBlitter.draw(new int[] {0x8040a0f0}, 1, 1, transparent, 1, 0, 0, 0, 0, 1, 1);
        if (transparent[0] != 0x8040a0f0) throw new AssertionError("straight ARGB was premultiplied twice: " + Integer.toHexString(transparent[0]));
        Random random = new Random(4873);
        for (int trial = 0; trial < 600; trial++) {
            int w = 1 + random.nextInt(17), h = 1 + random.nextInt(19);
            int sw = 1 + random.nextInt(21), sh = 1 + random.nextInt(23);
            int[] source = new int[sw * sh], expected = new int[w * h];
            for (int i = 0; i < source.length; i++) source[i] = (i % 3 == 0 ? 0xff000000 : i % 3 == 1 ? 0 : 0x80000000) | random.nextInt(0x1000000);
            for (int i = 0; i < expected.length; i++) expected[i] = random.nextInt();
            int[] actual = expected.clone();
            int x = random.nextInt(45) - 22, y = random.nextInt(45) - 22;
            int left = random.nextInt(w), top = random.nextInt(h);
            int right = left + random.nextInt(w - left + 1), bottom = top + random.nextInt(h - top + 1);
            // Independent inverse mapping and normalized floating-point source-over reference.
            for (int dy = top; dy < bottom; dy++) for (int dx = left; dx < right; dx++) {
                int sx = dx - x, sy = dy - y;
                if (sx < 0 || sy < 0 || sx >= sw || sy >= sh) continue;
                int color = source[sy * sw + sx], alpha = color >>> 24;
                int old = expected[dy * w + dx];
                double a = alpha / 255.0, oldAlpha = (old >>> 24) / 255.0;
                double outAlpha = a + oldAlpha * (1 - a);
                if (alpha == 0) continue;
                int result = ((int) Math.round(outAlpha * 255)) << 24;
                for (int shift = 0; shift <= 16; shift += 8) {
                    double channel = (((color >>> shift) & 255) * a + ((old >>> shift) & 255) * oldAlpha * (1 - a)) / outAlpha;
                    result |= ((int) Math.round(channel)) << shift;
                }
                expected[dy * w + dx] = result;
            }
            ArgbBlitter.draw(source, sw, sh, actual, w, x, y, left, top, right, bottom);
            for (int i = 0; i < actual.length; i++) for (int shift = 0; shift <= 24; shift += 8) {
                if (Math.abs(((actual[i] >>> shift) & 255) - ((expected[i] >>> shift) & 255)) > 1)
                    throw new AssertionError("translation/clip/alpha trial " + trial);
            }
        }
        int[] destination = {0xff123456};
        ArgbBlitter.draw(new int[] {0xffffffff}, 1, 1, destination, 1, Integer.MAX_VALUE, 0, 0, 0, 1, 1);
        if (destination[0] != 0xff123456) throw new AssertionError("offscreen overflow");
        System.out.println("ARGB blit: 600 translated, clipped and alpha cases passed.");
    }
}
