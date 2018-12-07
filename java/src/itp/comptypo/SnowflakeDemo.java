package itp.comptypo;

import processing.core.PApplet;

public class SnowflakeDemo extends HolidayAnimator {

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new SnowflakeDemo(false));
    }

    public SnowflakeDemo(boolean makePrint) {
        super(makePrint);
    }

    public void initSprites() {
        sprites.add(new Snowflake());
    }
}
