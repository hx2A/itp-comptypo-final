package itp.comptypo;

import processing.core.PApplet;

public class SequencedAnimation extends HolidayAnimator {

    private static final int FRAME_RATE = 30;
    private static final int SNOWFLAKE_SUPPRESSION_TIME = 4 * FRAME_RATE;

    private int snowflakeSuppressionCounter = 0;

    private static final String HH_PHRASE = "HappyHoli.days";
    private static final int[] HH_X_OFFSETS = new int[] { -538, -425, -350,
            -260, -175, 80, 190, 290, 305, 342, 388, 485, 555, 650 };
    private static final int[] HH_Y_OFFSETS = new int[] { -72, -50, -10, -10,
            -10, -72, -50, -90, -50, -128, -90, -50, -10, -60 };

    private static final String HNY_PHRASE = "HappyNewYear";
    private static final int[] HNY_X_OFFSETS = new int[] { -648, -535, -460,
            -370, -285, -39, 70, 185, 460, 583, 673, 758 };
    private static final int[] HNY_Y_OFFSETS = new int[] { -76, -54, -10, -10,
            -10, -90, -55, -55, -45, -55, -54, -64 };

    private static final String HHAN_PHRASE = "HappyHanukkah";
    private static final int[] HHAN_X_OFFSETS = new int[] { -613, -500, -425,
            -335, -250, 10, 120, 220, 318, 420, 518, 618, 720 };
    private static final int[] HHAN_Y_OFFSETS = new int[] { -76, -54, -10, -10,
            -10, -76, -54, -54, -54, -92, -92, -54, -92 };

    private static final String HF_PHRASE = "HappyF-esti.vus";
    private static final int[] HF_X_OFFSETS = new int[] { -492, -379, -306,
            -214, -129, 83, 145, 185, 252, 345, 355, 390, 433, 530, 610 };
    private static final int[] HF_Y_OFFSETS = new int[] { -76, -54, -10, -10,
            -10, -80, -150, -55, -62, -92, -52, -128, -55, -55, -60 };

    private static final String FN_PHRASE = "F-eli.zNavi.dad";
    private static final int[] FN_X_OFFSETS = new int[] { -492, -430, -392,
            -308, -290, -255, -262, -20, 100, 200, 270, 308, 350, 450, 555 };
    private static final int[] FN_Y_OFFSETS = new int[] { -80, -150, -55, -90,
            -52, -128, -20, -90, -54, -55, -52, -128, -92, -55, -92 };

    private static final String MC_PHRASE = "MerryChri.stmas";
    private static final int[] MC_X_OFFSETS = new int[] { -585, -418, -345,
            -270, -208, 28, 145, 228, 290, 328, 345, 440, 503, 630, 715 };
    private static final int[] MC_Y_OFFSETS = new int[] { -85, -55, -64, -64,
            -10, -85, -92, -64, -52, -128, -60, -92, -55, -51, -60 };

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new SequencedAnimation());
    }

    public SequencedAnimation() {
        super(true, "CFHMNYadehiklmnoprstuvwyz.-");
    }

    public void addSprites(float t) {
        float snowflakeProbability;

        if (t == 0) {
            int[] moveTimes1 = new int[] { 29 * FRAME_RATE, 59 * FRAME_RATE };
            shapes.add(new SnowPile(-10, 150, -0.15f, 0.30f, moveTimes1));

            int[] moveTimes2 = new int[] { 14 * FRAME_RATE, 44 * FRAME_RATE,
                    74 * FRAME_RATE, 89 * FRAME_RATE, 104 * FRAME_RATE };
            shapes.add(new SnowPile(50, -200, 0.1f, 0.35f, moveTimes2));

            shapes.add(new Phrase(HH_PHRASE, HH_X_OFFSETS, HH_Y_OFFSETS,
                    (int) (0.7f * height), 0, FRAME_RATE * 10));
        }

        if (t > 10 * FRAME_RATE && t < 110 * FRAME_RATE) {
            if (snowflakeSuppressionCounter > 0) {
                snowflakeSuppressionCounter--;
                snowflakeProbability = 0.02f;
            } else {
                snowflakeProbability = 0.06f;
            }
            if (random(1) < snowflakeProbability) {
                shapes.add(new Snowflake(random(width)));
            }
        }

        if (t == 12 * FRAME_RATE) {
            shapes.add(new Phrase(HNY_PHRASE, HNY_X_OFFSETS, HNY_Y_OFFSETS));
            snowflakeSuppressionCounter = SNOWFLAKE_SUPPRESSION_TIME;
        }

        if (t == 27 * FRAME_RATE) {
            shapes.add(new Phrase(FN_PHRASE, FN_X_OFFSETS, FN_Y_OFFSETS));
            snowflakeSuppressionCounter = SNOWFLAKE_SUPPRESSION_TIME;
        }

        if (t == 42 * FRAME_RATE) {
            shapes.add(new Phrase(HF_PHRASE, HF_X_OFFSETS, HF_Y_OFFSETS));
            snowflakeSuppressionCounter = SNOWFLAKE_SUPPRESSION_TIME;
        }

        if (t == 57 * FRAME_RATE) {
            shapes.add(new Phrase(HHAN_PHRASE, HHAN_X_OFFSETS, HHAN_Y_OFFSETS));
            snowflakeSuppressionCounter = SNOWFLAKE_SUPPRESSION_TIME;
        }

        if (t == 72 * FRAME_RATE) {
            shapes.add(new Phrase(MC_PHRASE, MC_X_OFFSETS, MC_Y_OFFSETS));
            snowflakeSuppressionCounter = SNOWFLAKE_SUPPRESSION_TIME;
        }

        if (t == 87 * FRAME_RATE) {
            shapes.add(new Phrase(HNY_PHRASE, HNY_X_OFFSETS, HNY_Y_OFFSETS));
            snowflakeSuppressionCounter = SNOWFLAKE_SUPPRESSION_TIME;
        }

        if (t == 102 * FRAME_RATE) {
            shapes.add(new Phrase(HH_PHRASE, HH_X_OFFSETS, HH_Y_OFFSETS));
            snowflakeSuppressionCounter = SNOWFLAKE_SUPPRESSION_TIME;
        }

        if (t == 126 * FRAME_RATE) {
            exit();
        }
    }
}
