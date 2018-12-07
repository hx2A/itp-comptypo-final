package itp.comptypo;

import processing.core.PApplet;

public class PhraseKerningCalibration extends HolidayAnimator {

    private static final String PHRASE1 = "HappyHoli.days";
    private static final int[] PHRASE1_X_OFFSETS = new int[] { -200, -169,
            -138, -107, -76, -46, -15, 15, 46, 76, 107, 138, 169, 200 };
    private static final int[] PHRASE1_Y_OFFSETS = new int[] { 0, 0, 0, 0, 0,
            0, 0, 0, 0, -50, 0, 0, 0, 0 };

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new PhraseKerningCalibration());
    }

    public PhraseKerningCalibration() {
        super("Hapyoli.ds", 0);
    }

    public void initSprites() {
        sprites.add(new Phrase(PHRASE1, PHRASE1_X_OFFSETS, PHRASE1_Y_OFFSETS, height - 100));
    }
}
