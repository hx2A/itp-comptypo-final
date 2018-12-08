package itp.comptypo;

import processing.core.PApplet;

public class SnowPileDemo extends HolidayAnimator {

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new SnowPileDemo());
    }

    public SnowPileDemo() {
        super("y");
    }

    public void addSprites(float t) {
        if (t == 0) {
            shapes.add(new SnowPile(0, 20, 0.1f, 0.3f));
        }
    }
}
