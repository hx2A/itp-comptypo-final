package itp.comptypo;

import java.util.ArrayList;
import java.util.Collections;
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

    protected float sizeFactor;
    private int w;
    private int h;
    private float t;
    private float dt;

    private PApplet p;

    private String charactersToLoad;
    private JSONObject fontPathData;
    private JSONObject snowflakePathData;

    int fillColor;
    int backgroundColor;

    private static final float X_NOISE_SCALING = 400;
    private static final float Y_NOISE_SCALING = 400;
    private static final float Z_NOISE_SCALING = 400;
    private static final float RAD_NOISE_SCALING = 400;
    private static final float LETTER_DISBAND_LEVEL = 0.75f;

    List<Shape> shapes;

    public HolidayAnimator(String charactersToLoad) {
        this(true, charactersToLoad, 1);
    }

    public HolidayAnimator(String charactersToLoad, float dt) {
        this(true, charactersToLoad, dt);
    }

    public HolidayAnimator(boolean reducedSize, String charactersToLoad) {
        this(reducedSize, charactersToLoad, 1);
    }

    public HolidayAnimator(boolean reducedSize, String charactersToLoad,
            float dt) {
        this.p = this;
        this.charactersToLoad = charactersToLoad;
        this.dt = dt;

        if (reducedSize) {
            sizeFactor = 0.75f; // 0.8f;
        } else {
            sizeFactor = 1;
        }

        w = (int) (1795 * sizeFactor);
        h = (int) (1287 * sizeFactor);
    }

    public void settings() {
        size(w, h, P3D);
    }

    public void setup() {
        backgroundColor = color(240, 240, 240);
        fillColor = color(192);
        t = 0;

        Camera3D camera3D = new Camera3D(this);
//        camera3D.renderRegular();
        camera3D.reportStats();
         camera3D.renderDuboisRedCyanAnaglyph().setDivergence(1f);
        camera3D.setBackgroundColor(backgroundColor);
        frameRate(30);

        noiseSeed(0);
        randomSeed(0);

        this.fill(fillColor);
        this.stroke(0);
        this.strokeWeight(2);

        shapes = new ArrayList<Shape>();
    }

    private void loadFontPathData() {
        fontPathData = new JSONObject();
        snowflakePathData = new JSONObject();

        for (char c : charactersToLoad.toCharArray()) {
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
    }

    public abstract void addSprites(float t);

    public void initExtras() {

    }

    public void drawExtra() {

    }

    public void preDraw() {
        // this is here because Processing rejects setup methods that take more
        // than 500 ms :(
        if (fontPathData == null) {
            loadFontPathData();
            initExtras();
        } else {
            t += dt;
        }

        addSprites(t);

        // filter out the dead sprites
        shapes = shapes.stream().filter(s -> s.isAlive())
                .collect(Collectors.toList());

        for (int i = 0; i < shapes.size(); i++) {
            shapes.get(i).update();
        }

        Collections.sort(shapes);
    }

    public void draw() {
        for (Shape s : shapes) {
            s.draw();
        }

        drawExtra();
    }

    private abstract class Shape implements Comparable<Shape> {
        protected boolean alive;
        protected PVector pos;
        protected float xSalt;
        protected float ySalt;
        protected float rads;
        protected float scale;

        public Shape() {
            alive = true;
            xSalt = p.random(10000);
            ySalt = p.random(10000);
            scale = 1;
        }

        public abstract void update();

        public boolean isAlive() {
            return alive;
        }

        protected PVector flakeMovement(PVector pos) {
            PVector movement = new PVector();

            movement.x = (30f * dt * sizeFactor)
                    * (noise(sizeFactor * pos.x / X_NOISE_SCALING + xSalt,
                            sizeFactor * pos.y / X_NOISE_SCALING + ySalt, t
                                    / X_NOISE_SCALING) - 0.5f);
            movement.y = (-1f * dt * sizeFactor)
                    - (6f * dt * sizeFactor)
                    * noise(sizeFactor * pos.x / Y_NOISE_SCALING + xSalt,
                            sizeFactor * pos.y / Y_NOISE_SCALING + ySalt, t
                                    / Y_NOISE_SCALING + 1000);
            movement.z = (10f * dt * sizeFactor)
                    * (noise(sizeFactor * pos.x / Z_NOISE_SCALING + xSalt,
                            sizeFactor * pos.y / Z_NOISE_SCALING + ySalt, t
                                    / Z_NOISE_SCALING) - 0.5f);

            return movement;
        }

        protected float flakeRotation(PVector pos) {
            return (0.3f * dt)
                    * (noise(sizeFactor * pos.x / RAD_NOISE_SCALING + xSalt,
                            sizeFactor * pos.y / RAD_NOISE_SCALING + ySalt, t
                                    / RAD_NOISE_SCALING) - 0.5f);
        }

        public void draw(JSONArray paths, JSONObject stats) {
            p.pushMatrix();
            p.translate(pos.x, pos.y, pos.z);
            p.rotate(rads);
            p.scale(scale * sizeFactor);
            p.translate(-getJSONFloatVal(stats, "centerX"),
                    -getJSONFloatVal(stats, "centerY"), 0);

            p.beginShape();

            // draw the outer path
            JSONArray path = getJSONArray(paths, 0);
            for (int j = 0; j < path.size(); j++) {
                int[] coords = getJSONArray(path, j).getIntArray();
                p.vertex(coords[0], coords[1], 0);
            }
            // loop back to beginning
            int[] coords = getJSONArray(path, 0).getIntArray();
            p.vertex(coords[0], coords[1], 0);

            // if there is a second path this is a contour
            if (paths.size() == 2) {
                p.beginContour();
                path = getJSONArray(paths, 1);
                for (int j = path.size() - 1; j >= 0; j--) {
                    coords = getJSONArray(path, j).getIntArray();
                    p.vertex(coords[0], coords[1], 0);
                }
                // loop back to beginning
                coords = getJSONArray(path, path.size() - 1).getIntArray();
                p.vertex(coords[0], coords[1], 0);
                p.endContour();
            }
            p.endShape();
            p.popMatrix();
        }

        public abstract void draw();

        public int compareTo(Shape s) {
            if (pos.z > s.pos.z)
                return 1;
            else if (pos.z < s.pos.z)
                return -1;
            return 0;
        }
    }

    protected class SnowPile extends Shape {

        private float bumpPosition;
        private float bumpHeight;
        
        public SnowPile(float yPos, float zPos, float bumpPosition, float bumpHeight) {
            this.bumpPosition = bumpPosition;
            this.bumpHeight = bumpHeight;
            pos = new PVector(width / 2, yPos + height, zPos);
            scale = 0.9f;
        }

        public void update() {

        }

        public void draw() {
            p.pushMatrix();
            p.translate(pos.x, pos.y, pos.z);
            p.rotate(rads);
            p.scale(scale * sizeFactor);

            p.beginShape();
            p.vertex(-width, -0.1f * height, 0);

            // curve
            p.curveVertex(-width, -0.1f * height, 0);
            p.curveVertex(-0.8f * width, -0.15f * height, 0);
            p.curveVertex(bumpPosition * width, -bumpHeight * height, 0);
            p.curveVertex(0.8f * width, -0.15f * height, 0);
            p.curveVertex(width, -0.1f * height, 0);

            // rest of shape
            p.vertex(width, -0.1f * height, 0);
            p.vertex(width, 0.5f * height, 0);
            p.vertex(-width, 0.5f * height, 0);
            p.vertex(-width, -0.1f * height, 0);

            p.endShape();

            p.popMatrix();
        }

    }

    protected class Snowflake extends Shape {
        private JSONArray paths;
        private JSONObject stats;

        public Snowflake() {
            this(width / 2f);
        }

        public Snowflake(float startX) {
            int num = (int) (p.random(snowflakePathData.size()));
            JSONObject pathData = getJSONObj(snowflakePathData, num);
            paths = getJSONArray(pathData, "paths");
            stats = getJSONObj(pathData, "stats");

            pos = new PVector(width / 2f, 1.1f * height, 0);
            scale = p.random(0.8f, 1.5f);
        }

        public void update() {
            pos.add(flakeMovement(pos));
            rads += flakeRotation(pos);

            if (pos.y < -500) {
                alive = false;
            }
        }

        public void draw() {
            draw(paths, stats);
        }
    }

    protected class Letter extends Shape {
        private String snowflakeIndex;
        private JSONObject morphData;
        protected PVector offset;
        private int state;

        public Letter(char c, float xOffset, float yOffset, float zOffset) {
            offset = new PVector(xOffset, yOffset, zOffset);
            pos = new PVector(width / 2, 1.1f * height, 0);
            scale = 1.5f;
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

            if (pos.y < -500) {
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

    protected class Phrase extends Shape {
        private List<Letter> letters;

        public Phrase(String phrase, int[] xOffsets, int[] yOffsets) {
            this(phrase, xOffsets, yOffsets, (int) (1.1f * height));
        }

        public Phrase(String phrase, int[] xOffsets, int[] yOffsets, int yStart) {
            alive = true;

            pos = new PVector(width / 2, yStart, 0);

            letters = new ArrayList<Letter>();
            for (int i = 0; i < phrase.length(); i++) {
                letters.add(new Letter(phrase.charAt(i), xOffsets[i],
                        yOffsets[i],
                        20 * (i / ((float) phrase.length()) - 0.5f)));
            }
        }

        private void disband() {
            for (Letter letter : letters) {
                shapes.add(letter);
            }
            alive = false;
        }

        public void update() {
            pos.y += -2 * dt * sizeFactor;

            for (Letter letter : letters) {
                letter.pos = PVector.add(pos,
                        PVector.mult(letter.offset, sizeFactor));
            }

            if (pos.y < LETTER_DISBAND_LEVEL * height) {
                disband();
            }
        }

        public void draw() {
            Collections.sort(letters);

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