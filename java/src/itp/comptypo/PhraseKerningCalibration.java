package itp.comptypo;

import processing.core.PApplet;

public class PhraseKerningCalibration extends HolidayAnimator {

    // private static final String PHRASE1 = "Happy";
    // private static final int[] PHRASE1_X_OFFSETS = new int[] { -538, -425,
    // -350, -260, -175 };
    // private static final int[] PHRASE1_Y_OFFSETS = new int[] { -72, -50, -10,
    // -10, -10 };

    private static final String PHRASE1 = "HappyHoli.days";
    private static final int[] PHRASE1_X_OFFSETS = new int[] { -538, -425,
            -350, -260, -175, 80, 190, 290, 305, 342, 388, 485, 555, 650 };
    private static final int[] PHRASE1_Y_OFFSETS = new int[] { -72, -50, -10,
            -10, -10, -72, -50, -90, -50, -128, -90, -50, -10, -60 };

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new PhraseKerningCalibration());
    }

    public PhraseKerningCalibration() {
        super(true, "Hapyoli.ds", 0);
    }

    @Override
    public void initExtras() {
        textFont(createFont("../fonts/BellaFashion.ttf", 150 * sizeFactor));
        textAlign(CENTER);
    }

//    @Override
//    public void drawExtra() {
//        pushStyle();
//        fill(color(255, 0, 0));
//        text("Happy Holidays", width / 2, 0.8f * height);
//        popStyle();
//    }

    public void addSprites(float t) {
        if (t == 0) {
            shapes.add(new Phrase(PHRASE1, PHRASE1_X_OFFSETS,
                    PHRASE1_Y_OFFSETS, (int) (0.8f * height)));
        }
    }
}
