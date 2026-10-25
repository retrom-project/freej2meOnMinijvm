package java.awt;

public class Rectangle {
    public int x, y, width, height;

    public Rectangle() {

    }

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean intersects(Rectangle other) {
        return width > 0 && height > 0 && other.width > 0 && other.height > 0 &&
            (long) x + width > other.x && (long) other.x + other.width > x &&
            (long) y + height > other.y && (long) other.y + other.height > y;
    }
}
