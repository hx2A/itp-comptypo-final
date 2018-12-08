package itp.comptypo;

import processing.core.PApplet;

public class SnowflakeDemo extends HolidayAnimator {

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new SnowflakeDemo());
    }

    public SnowflakeDemo() {
        super(false, "Hy");
    }

    public void addSprites(float t) {
        if (t == 0) {
            for (int i = 0; i < 20; i++) {
                shapes.add(new Snowflake(random(width)));
            }
        }
    }
}
