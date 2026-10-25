package com.ebsee.emu;

import java.awt.Rectangle;

/** Run on miniJVM too: the host JDK's Rectangle must not hide a missing method. */
public final class RectangleCompatibilityTest {
    public static void main(String[] args) {
        int[] values = {-2, 0, 2, Integer.MAX_VALUE};
        for (int x : values) for (int y : values) for (int w : values) for (int h : values) {
            Rectangle a = new Rectangle(x, y, w, h), b = new Rectangle(0, 0, 3, 3);
            boolean expected = w > 0 && h > 0 && (long) x < 3 && (long) y < 3 && (long) x + w > 0 && (long) y + h > 0;
            if (a.intersects(b) != expected || b.intersects(a) != expected) throw new AssertionError("Rectangle overlap, empty or overflow boundary");
        }
        System.out.println("RECTANGLE_INTERSECTION_VERIFIED");
    }
}
