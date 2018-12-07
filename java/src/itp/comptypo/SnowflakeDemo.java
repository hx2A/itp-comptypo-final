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

    public void initSprites() {
        sprites.add(new Snowflake());
    }
}
