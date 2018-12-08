package itp.comptypo;

import processing.core.PApplet;

public class LetterDemo extends HolidayAnimator {

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new LetterDemo());
    }

    public LetterDemo() {
        super("F-");
    }

    public void addSprites(float t) {
        if (t == 0) {
            shapes.add(new Letter('F', 0, 0, 0, 600));
            shapes.add(new Letter('-', 0, 0, 0, 500));
        }
    }
}
