package itp.comptypo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;
import camera3D.Camera3D;

public abstract class HolidayAnimator extends PApplet {

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

    private static final String CHARACTERS = "Hiya.";

    private static final float X_NOISE_SCALING = 80;
    private static final float Y_NOISE_SCALING = 1000;
    private static final float RAD_NOISE_SCALING = 400;
    private static final float LETTER_DISBAND_LEVEL = 0.75f;

    List<Sprite> sprites;

    public HolidayAnimator(boolean makePrint) {
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
        // camera3D.reportStats();
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

    private void loadFontPathData() {
        fontPathData = new JSONObject();
        snowflakePathData = new JSONObject();

        for (char c : CHARACTERS.toCharArray()) {
            System.out.println(c);
            JSONObject charData = loadJSONObject("font_paths/path_data_" + c
                    + ".json");
            fontPathData.setJSONObject("" + c, charData);

            Iterator<?> it = charData.keyIterator();
            if (c == 'i' || c == '.') {
                continue;
            }
            while (it.hasNext()) {
                String key = (String) it.next();
                if (!snowflakePathData.hasKey(key)) {
                    snowflakePathData.setJSONObject(key,
                            getJSONObj(getJSONObj(charData, key), "100"));
                }
            }
        }
        System.out.println(snowflakePathData.keys());
    }

    public abstract void initSprites();

    public void preDraw() {
        // this is here because Processing rejects setup methods that take more
        // than 500 ms :(
        if (fontPathData == null) {
            loadFontPathData();
            initSprites();
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

    private abstract class Sprite {
        protected boolean alive;
        protected PVector pos;
        protected float xSalt;
        protected float ySalt;
        protected float rads;
        protected float scale;

        public Sprite() {
            alive = true;
            xSalt = p.random(1000);
            ySalt = p.random(1000);
        }

        public abstract void update();

        public boolean isAlive() {
            return alive;
        }

        protected PVector flakeMovement(PVector pos) {
            PVector movement = new PVector();

            movement.x = 50
                    * sizeFactor
                    * (noise(pos.x / X_NOISE_SCALING + xSalt, pos.y
                            / X_NOISE_SCALING + ySalt, t) - 0.5f);
            movement.y = -2
                    * sizeFactor
                    - 3
                    * noise(pos.x / Y_NOISE_SCALING + xSalt, pos.y
                            / Y_NOISE_SCALING + ySalt, t + 1000);

            return movement;
        }

        protected float flakeRotation(PVector pos) {
            return 0.3f * (noise(pos.x / RAD_NOISE_SCALING + xSalt, pos.y
                    / RAD_NOISE_SCALING + ySalt, t) - 0.5f);
        }

        public void draw(JSONArray paths, JSONObject stats) {
            p.pushMatrix();
            p.translate(pos.x, pos.y, pos.z);
            p.rotate(rads);
            p.scale(scale * sizeFactor);
            p.translate(-getJSONFloatVal(stats, "centerX"),
                    -getJSONFloatVal(stats, "centerY"), 0);
            for (int i = 0; i < paths.size(); i++) {
                if (i == 0) {
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

    protected class Snowflake extends Sprite {
        private JSONArray paths;
        private JSONObject stats;

        public Snowflake() {
            int num = (int) (p.random(snowflakePathData.size()));
            JSONObject pathData = getJSONObj(snowflakePathData, num);
            paths = getJSONArray(pathData, "paths");
            stats = getJSONObj(pathData, "stats");

            pos = new PVector(width / 2f, height + 100, 0);
            scale = 1.8f;
        }

        public void update() {
            pos.add(flakeMovement(pos));
            rads += flakeRotation(pos);

            if (pos.y < -100) {
                alive = false;
            }
        }

        public void draw() {
            draw(paths, stats);
        }
    }

    protected class Letter extends Sprite {
        private String snowflakeIndex;
        private JSONObject morphData;
        protected PVector offset;
        private int state;

        public Letter(char c, int xOffset, int yOffset) {
            offset = new PVector(xOffset, yOffset, 0);
            pos = new PVector(width / 2, height + 100, 0);
            scale = 1.8f;
            state = 0;

            // pick a random snowflake to morph into
            JSONObject charData = getJSONObj(fontPathData, "" + c);
            Set<?> snowflakeKeys = charData.keys();
            String[] possibleSnowflakes = (String[]) snowflakeKeys
                    .toArray(new String[snowflakeKeys.size()]);
            snowflakeIndex = possibleSnowflakes[(int) p
                    .random(possibleSnowflakes.length)];
            morphData = getJSONObj(charData, snowflakeIndex);
        }

        public void update() {
            state = min(state + 2, 100);
            pos.add(flakeMovement(pos));
            rads += flakeRotation(pos);

            if (pos.y < -100) {
                alive = false;
            }
        }

        public void draw() {
            JSONObject data;
            data = getJSONObj(morphData, Integer.toString(state));
            JSONArray paths = getJSONArray(data, "paths");
            JSONObject stats = getJSONObj(data, "stats");

            draw(paths, stats);
        }
    }

    protected class Phrase extends Sprite {
        private List<Letter> letters;

        public Phrase(String phrase, int[] xOffsets, int[] yOffsets) {
            alive = true;

            pos = new PVector(width / 2, height + 100, 0);

            letters = new ArrayList<Letter>();
            for (int i = 0; i < phrase.length(); i++) {
                letters.add(new Letter(phrase.charAt(i), xOffsets[i],
                        yOffsets[i]));
            }
        }

        private void disband() {
            for (Letter letter : letters) {
                sprites.add(letter);
            }
            alive = false;
        }

        public void update() {
            pos.y += -2 * sizeFactor;

            for (Letter letter : letters) {
                letter.pos = PVector.add(pos, letter.offset);
            }

            if (pos.y < LETTER_DISBAND_LEVEL * height) {
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
}
