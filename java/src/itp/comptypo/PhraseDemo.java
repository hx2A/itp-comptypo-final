package itp.comptypo;

import processing.core.PApplet;

public class PhraseDemo extends HolidayAnimator {

//    private static final String PHRASE1 = "Hi.ya";
//    private static final int[] PHRASE1_X_OFFSETS = new int[] { -100, -50, -50,
//            0, 50 };
//    private static final int[] PHRASE1_Y_OFFSETS = new int[] { 0, 0, -50, 0, 0 };

    private static final String PHRASE1 = "HappyHoli.days";
    private static final int[] PHRASE1_X_OFFSETS = new int[] { -538, -425,
            -350, -260, -175, 80, 190, 290, 305, 342, 388, 485, 555, 650 };
    private static final int[] PHRASE1_Y_OFFSETS = new int[] { -72, -50, -10,
            -10, -10, -72, -50, -90, -50, -128, -90, -50, -10, -60 };
    
    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new PhraseDemo());
    }

    public PhraseDemo() {
//        super("Hiya.");
        super(true, "Hapyoli.ds");
    }

    public void addSprites(float t) {
        if (t == 0) {
            int[] moveTimes = new int[] { 250, 500, 1000 };
            shapes.add(new SnowPile(0, 150, 0.1f, 0.3f, moveTimes));
            shapes.add(new SnowPile(50, -200, -0.2f, 0.35f, moveTimes));
            shapes.add(new Phrase(PHRASE1, PHRASE1_X_OFFSETS,
                    PHRASE1_Y_OFFSETS));
        }
    }
}
