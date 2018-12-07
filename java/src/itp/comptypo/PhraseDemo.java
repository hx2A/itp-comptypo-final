package itp.comptypo;

import processing.core.PApplet;

public class PhraseDemo extends HolidayAnimator {

    private static final String PHRASE1 = "Hi.ya";
    private static final int[] PHRASE1_X_OFFSETS = new int[] { -100, -50, -50,
            0, 50 };
    private static final int[] PHRASE1_Y_OFFSETS = new int[] { 0, 0, -50, 0, 0 };

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new PhraseDemo(false));
    }

    public PhraseDemo(boolean makePrint) {
        super(makePrint);
    }

    public void initSprites() {
        sprites.add(new Phrase(PHRASE1, PHRASE1_X_OFFSETS,
                PHRASE1_Y_OFFSETS));
    }
}
