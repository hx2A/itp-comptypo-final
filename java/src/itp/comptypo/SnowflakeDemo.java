package itp.comptypo;

import processing.core.PApplet;

public class SnowflakeDemo extends HolidayAnimator {

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new SnowflakeDemo());
    }

    public SnowflakeDemo() {
        super(true, "Hy");
    }

    public void addSprites(float t) {
        if (t == 0) {
            shapes.add(new SnowPile(0, 150, 0.1f, 0.3f));
            shapes.add(new SnowPile(50, -200, -0.2f, 0.35f));
            for (int i = 0; i < 20; i++) {
                shapes.add(new Snowflake(random(width)));
            }
        }
    }
}
