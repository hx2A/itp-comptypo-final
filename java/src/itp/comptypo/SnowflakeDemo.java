package itp.comptypo;

import itp.comptypo.HolidayAnimator.SnowPile;
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
            for (int i = 0; i < 20; i++) {
                int[] moveTimes = new int[] { 250, 500, 1000 };
                shapes.add(new SnowPile(0, 150, 0.1f, 0.3f, moveTimes));
                shapes.add(new SnowPile(50, -200, -0.2f, 0.35f, moveTimes));
                shapes.add(new Snowflake(random(width)));
            }
        }
    }
}
