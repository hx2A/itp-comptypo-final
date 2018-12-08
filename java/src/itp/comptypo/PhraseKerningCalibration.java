package itp.comptypo;

import processing.core.PApplet;

public class PhraseKerningCalibration extends HolidayAnimator {

    private static final String PHRASE1 = "HappyHoli.days";
    private static final int[] PHRASE1_X_OFFSETS = new int[] { -538, -425,
            -350, -260, -175, 80, 190, 290, 305, 342, 388, 485, 555, 650 };
    private static final int[] PHRASE1_Y_OFFSETS = new int[] { -72, -50, -10,
            -10, -10, -72, -50, -90, -50, -128, -90, -50, -10, -60 };

    private static final String PHRASE2 = "HappyNewYear";
    private static final int[] PHRASE2_X_OFFSETS = new int[] { -648, -535,
            -460, -370, -285, -39, 70, 185, 460, 583, 673, 758 };
    private static final int[] PHRASE2_Y_OFFSETS = new int[] { -76, -54, -10,
            -10, -10, -90, -55, -55, -45, -55, -54, -64 };

    private static final String PHRASE3 = "HappyHanukkah";
    private static final int[] PHRASE3_X_OFFSETS = new int[] { -613, -500,
            -425, -335, -250, 10, 120, 220, 318, 420, 518, 618, 720 };
    private static final int[] PHRASE3_Y_OFFSETS = new int[] { -76, -54, -10,
            -10, -10, -76, -54, -54, -54, -92, -92, -54, -92 };

    private static final String PHRASE4 = "HappyF-esti.vus";
    private static final int[] PHRASE4_X_OFFSETS = new int[] { -492, -379,
            -306, -214, -129, 83, 145, 185, 252, 345, 355, 390, 433, 530, 610 };
    private static final int[] PHRASE4_Y_OFFSETS = new int[] { -76, -54, -10,
            -10, -10, -80, -150, -55, -62, -92, -52, -128, -55, -55, -60 };

    private static final String PHRASE5 = "F-eli.zNavi.dad";
    private static final int[] PHRASE5_X_OFFSETS = new int[] { -492, -430,
            -392, -308, -290, -255, -262, -20, 100, 200, 270, 308, 350, 450,
            555 };
    private static final int[] PHRASE5_Y_OFFSETS = new int[] { -80, -150, -55,
            -90, -52, -128, -20, -90, -54, -55, -52, -128, -92, -55, -92 };

    private static final String PHRASE6 = "MerryChri.stmas";
    private static final int[] PHRASE6_X_OFFSETS = new int[] { -585, -418,
            -345, -270, -208, 28, 145, 228, 290, 328, 345, 440, 503, 630, 715 };
    private static final int[] PHRASE6_Y_OFFSETS = new int[] { -85, -55, -64,
            -64, -10, -85, -92, -64, -52, -128, -60, -92, -55, -51, -60 };

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new PhraseKerningCalibration());
    }

    public PhraseKerningCalibration() {
        // super(true, "Hapyoli.ds");
        super(true, "MeryChi.stma");
    }

    @Override
    public void initExtras() {
        textFont(createFont("../fonts/BellaFashion.ttf", 150 * sizeFactor));
        textAlign(CENTER);
    }

    @Override
    public void drawExtra() {
        pushStyle();
        fill(color(255, 0, 0));
        // text("Happy Holidays", width / 2, 0.8f * height);
        // text("Happy New Year", width / 2, 0.8f * height);
        // text("Happy Hanukkah", width / 2, 0.8f * height);
        // text("Happy Festivus", width / 2, 0.8f * height);
        text("Merry Christmas", width / 2, 0.8f * height);
        // text("Feliz Navidad", width / 2, 0.8f * height);

        popStyle();
    }

    public void addSprites(float t) {
        if (t == 0) {
            shapes.add(new Phrase(PHRASE6, PHRASE6_X_OFFSETS,
                    PHRASE6_Y_OFFSETS, (int) (0.8f * height), 0,
                    Integer.MAX_VALUE));
        }
    }
}
