package itp.comptypo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import camera3D.Camera3D;

public class HappyHolidays extends PApplet {

    private boolean makePrint;
    private float sizeFactor;
    private float speedFactor;
    private int w;
    private int h;
    private float t;
    private float dt;

    private PApplet p;

    JSONObject fontPathData;
    JSONObject snowflakePathData;

    int fillColor;
    int backgroundColor;

    private static final String PHRASE1 = "Hiya";
    private static final int[] PHRASE1_OFFSETS = new int[] { -100, -50, 0, 50 };

    private static final float X_NOISE_SCALING = 80;
    private static final float Y_NOISE_SCALING = 1000;
    private static final float RAD_NOISE_SCALING = 400;
    private static final float LETTER_DISBAND_LEVEL = 0.75f;

    List<Sprite> sprites;

    public static void main(String[] args) {
        String[] a = { "Happy Holidays" };
        PApplet.runSketch(a, new HappyHolidays(false));
    }

    public HappyHolidays(boolean makePrint) {
        this.p = this;
        this.makePrint = makePrint;
        if (makePrint) {
            sizeFactor = 0.83f;
            t = 0;
        } else {
            sizeFactor = 0.4f; // 0.8f;
            t = 0;
        }

        speedFactor = 3f;

        w = (int) (1795 * sizeFactor);
        h = (int) (1287 * sizeFactor);
    }

    public void settings() {
        size(w, h, P3D);
    }

    public void setup() {
        backgroundColor = color(240, 240, 240);
        fillColor = color(192);

        Camera3D camera3D = new Camera3D(this);
        camera3D.renderRegular();
        // camera3D.renderDuboisRedCyanAnaglyph().setDivergence(1f);
        camera3D.reportStats();
        camera3D.setBackgroundColor(backgroundColor);
        frameRate(30);

        noiseSeed(0);
        randomSeed(0);

        this.fill(fillColor);
        this.stroke(0);
        this.strokeWeight(2);

        t = 0;

        sprites = new ArrayList<Sprite>();
    }

    public void loadFontPathData() {
        fontPathData = new JSONObject();
        snowflakePathData = new JSONObject();

        for (char c : "Hayi".toCharArray()) {
            // for (char c : "Hapyolids".toCharArray()) {
            System.out.println(c);
            JSONObject charData = loadJSONObject("font_paths/path_data_" + c
                    + ".json");
            fontPathData.setJSONObject("" + c, charData);

            Iterator<?> it = getJSONObj(charData, "snowflakes").keyIterator();
            if (c == 'i') {
                continue;
            }
            while (it.hasNext()) {
                String key = (String) it.next();
                if (!snowflakePathData.hasKey(key)) {
                    snowflakePathData.setJSONObject(
                            key,
                            getJSONObj(
                                    getJSONObj(
                                            getJSONObj(charData, "snowflakes"),
                                            key), "100"));
                }
            }

        }
        System.out.println(snowflakePathData.keys());
    }

    public void preDraw() {
        // this is here because Processing rejects setup methods that take more
        // than 500 ms :(
        if (fontPathData == null) {
            loadFontPathData();
            // sprites.add(new Snowflake((int)
            // (random(snowflakePathData.size()))));
            // sprites.add(new Snowflake(12));
            // sprites.add(new Letter('y'));
            sprites.add(new Phrase(PHRASE1, PHRASE1_OFFSETS));
        }

        t += 0.01;

        // filter out the dead sprites
        sprites = sprites.stream().filter(s -> s.isAlive())
                .collect(Collectors.toList());

        for (int i = 0; i < sprites.size(); i++) {
            sprites.get(i).update();
        }
    }

    public void draw() {
        for (Sprite s : sprites) {
            s.draw();
        }
    }

    abstract class Sprite {
        protected boolean alive;
        protected float xPos;
        protected float yPos;
        protected float zPos;
        protected float xPosSalt;
        protected float yPosSalt;
        protected float rads;
        protected float scale;
        protected boolean twoSolids;

        public Sprite() {
            alive = true;
            xPosSalt = p.random(1000);
            yPosSalt = p.random(1000);
        }

        public abstract void update();

        public boolean isAlive() {
            return alive;
        }

        public void draw(JSONArray paths, JSONObject stats) {
            p.pushMatrix();
            p.translate(xPos, yPos, zPos);
            p.rotate(rads);
            p.scale(scale * sizeFactor);
            p.translate(-getJSONFloatVal(stats, "centerX"),
                    -getJSONFloatVal(stats, "centerY"), 0);
            for (int i = 0; i < paths.size(); i++) {
                if (i == 0 || twoSolids) {
                    p.fill(fillColor);
                } else {
                    p.fill(backgroundColor);
                }
                p.beginShape();
                JSONArray path = getJSONArray(paths, i);
                for (int j = 0; j < path.size(); j++) {
                    int[] coords = getJSONArray(path, j).getIntArray();
                    p.vertex(coords[0], coords[1], 0);
                }
                p.endShape();
            }
            p.popMatrix();
        }

        public abstract void draw();
    }

    class Snowflake extends Sprite {
        public int num;
        private JSONArray paths;
        private JSONObject stats;

        public Snowflake(int num) {
            this.num = num;
            JSONObject pathData = getJSONObj(snowflakePathData, num);
            paths = getJSONArray(pathData, "paths");
            stats = getJSONObj(pathData, "stats");

            xPos = width / 2;
            yPos = height + 100;
            zPos = 0;
            scale = 1.8f;
        }

        public void update() {
            xPos += 20
                    * sizeFactor
                    * (noise(xPos / X_NOISE_SCALING + xPosSalt, yPos
                            / X_NOISE_SCALING + yPosSalt, t) - 0.5f);
            yPos -= 3
                    * sizeFactor
                    + 2
                    * (noise(xPos / Y_NOISE_SCALING + xPosSalt, yPos
                            / Y_NOISE_SCALING + yPosSalt, t + 1000) - 0.5f);
            rads += 0.1 * (noise(xPos / RAD_NOISE_SCALING + xPosSalt, yPos
                    / RAD_NOISE_SCALING + yPosSalt, t) - 0.5f);

            if (yPos < -100) {
                alive = false;
            }
        }

        public void draw() {
            draw(paths, stats);
        }
    }

    class Letter extends Sprite {
        public char c;
        private String snowflakeIndex;
        private JSONObject morphData;
        private int state;

        public Letter(char c) {
            this.c = c;

            xPos = width / 2;
            yPos = height + 100;
            zPos = 0;
            scale = 1.8f;
            state = 0;

            JSONObject charData = getJSONObj(fontPathData, "" + c);
            twoSolids = getJSONBooleanVal(charData, "twoSolids");
            Set<?> snowflakeKeys = getJSONObj(charData, "snowflakes").keys();
            String[] possibleSnowflakes = (String[]) snowflakeKeys
                    .toArray(new String[snowflakeKeys.size()]);

            snowflakeIndex = possibleSnowflakes[(int) p
                    .random(possibleSnowflakes.length)];
            morphData = getJSONObj(getJSONObj(charData, "snowflakes"),
                    snowflakeIndex);
        }

        public void update() {

            state = min(state + 2, 100);
            xPos += 50
                    * sizeFactor
                    * (noise(xPos / X_NOISE_SCALING + xPosSalt, yPos
                            / X_NOISE_SCALING + yPosSalt, t) - 0.5f);
            yPos -= 3
                    * sizeFactor
                    + 2
                    * (noise(xPos / Y_NOISE_SCALING + xPosSalt, yPos
                            / Y_NOISE_SCALING + yPosSalt, t + 1000) - 0.5f);
            rads += 0.1 * (noise(xPos / RAD_NOISE_SCALING + xPosSalt, yPos
                    / RAD_NOISE_SCALING + yPosSalt, t) - 0.5f);

            if (yPos < -100) {
                alive = false;
            }
        }

        public void draw() {
            JSONObject data;
            if (c == 'i' && state == 100) {
                data = getJSONObj(snowflakePathData, snowflakeIndex);
            } else {
                data = getJSONObj(morphData, Integer.toString(state));
            }
            JSONArray paths = getJSONArray(data, "paths");
            JSONObject stats = getJSONObj(data, "stats");

            draw(paths, stats);
        }
    }

    private class Phrase extends Sprite {
        private List<Letter> letters;
        private int[] offsets;

        public Phrase(String phrase, int[] offsets) {
            this.offsets = offsets;
            alive = true;

            xPos = width / 2;
            yPos = height + 100;
            zPos = 0;

            letters = new ArrayList<Letter>();
            for (int i = 0; i < phrase.length(); i++) {
                Letter newLetter = new Letter(phrase.charAt(i));
                newLetter.xPos = xPos + offsets[i];
                newLetter.yPos = yPos;
                newLetter.zPos = zPos;
                letters.add(newLetter);
            }
        }

        private void disband() {
            for (Letter letter : letters) {
                sprites.add(letter);
            }
            alive = false;
        }

        public void update() {
            yPos -= 3 * sizeFactor;
            xPos += 0;
            zPos += 0;

            for (int i = 0; i < letters.size(); i++) {
                // set letter positions directly
                Letter letter = letters.get(i);
                letter.xPos = xPos + offsets[i];
                letter.yPos = yPos;
                letter.zPos = zPos;
            }

            if (yPos < LETTER_DISBAND_LEVEL * height) {
                disband();
            }
        }

        public void draw() {
            for (Letter letter : letters) {
                letter.draw();
            }
        }
    }

    private JSONObject getJSONObj(JSONObject obj, String key) {
        return (JSONObject) obj.getJSONObject(key);
    }

    private JSONArray getJSONArray(JSONObject obj, String key) {
        return (JSONArray) obj.getJSONArray(key);
    }

    private JSONObject getJSONObj(JSONObject obj, Integer key) {
        return (JSONObject) obj.getJSONObject(Integer.toString(key));
    }

    private JSONArray getJSONArray(JSONArray arr, Integer index) {
        return (JSONArray) arr.getJSONArray(index);
    }

    private float getJSONFloatVal(JSONObject obj, String key) {
        return ((Double) obj.get(key)).floatValue();
    }

    private boolean getJSONBooleanVal(JSONObject obj, String key) {
        return ((Boolean) obj.get(key)).booleanValue();
    }
}
