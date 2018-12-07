package itp.comptypo;

import processing.core.PApplet;

public class LetterDemo extends HolidayAnimator {

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new LetterDemo());
    }

    public LetterDemo() {
        super("y");
    }

    public void initSprites() {
         sprites.add(new Letter('y', 0, 0));
    }
}
