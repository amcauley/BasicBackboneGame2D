
package basicbackbonegame2d;

import static java.awt.MouseInfo.getPointerInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    public enum AnimationType {
        NO_ANIMATION, ANIMATED_NO_LOOP, ANIMATED_WITH_LOOP;
    }

    public static final int DEFAULT_DEPTH = 10;

    /*
     * Reference to top level game object. Used by Transition class to switch
     * topLvlScene.
     */
    static public BasicBackboneGame2D g;

    /* Screen on which all scenes will draw */
    public GameScreen screen;

    public String sceneName = "default";

    public boolean isSubscene; // flag indicating that this scene is a subscene.
    public int locX; // x location (ex top left corner for rectangular scenes)
    public int locY; // y location
    public int width; // width
    public int height; // height
    public AnimationType animationType; // flag indicating the scene is an animation

    public int depth; // depth of (sub)scene. Higher numbers are drawn over lower numbers.

    boolean isActive; // should this subscene be drawn?

    /* File location of this scene's active image. */
    public String imagePath;

    /* Array of subscenes */
    public Scene subScenes[];
    public int numSubScenes;

    public boolean showPlayer;

    public static enum CameraType {
        GLOBAL, PLAYER
    }

    public CameraType cameraType;

    // Viewport overrides. These will be 0 unless a scene sets type PLAYER and
    // overrides them.
    public int cameraViewportWidth;
    public int cameraViewportHeight;

    /* Dynamic array of Transitions */
    public List<Transition> transitions = new ArrayList<>();

    // Map of obstacles in this scenes.
    // This can be uninitialized if the scene doesn't have any.
    public Obstacle obstacle;

    // Scaling applied to the Player. This represents depth in the scene.
    // Can be uninitialized if not applicable to a scene.
    public ScaleMap scaleMap;

    /* Default scene constructor */
    public Scene() {
        numSubScenes = 0;

        depth = DEFAULT_DEPTH;

        animationType = AnimationType.NO_ANIMATION;

        isActive = true;
        showPlayer = false;

        cameraType = CameraType.GLOBAL;
        cameraViewportWidth = 0;
        cameraViewportHeight = 0;
    }

    public void setGameScreen(GameScreen gameScreen) {
        screen = gameScreen;
        // Update subscenes with the same screen object.
        for (int scnIdx = 0; scnIdx < numSubScenes; scnIdx++) {
            subScenes[scnIdx].setGameScreen(screen);
        }
    }

    // Update the draw list with this scene's entries (and entries from sub-scenes).
    // Individual scenes can override this if needed.
    public void updateDrawList() {
        /*
         * ID should be unique to each (sub)scene image. Append imagePath to sceneName
         * in case scene uses multiple images. imagePath isn't enough by itself in case
         * multiple (sub)scenes use the same image.
         */
        String id = sceneName + "_" + imagePath;

        /* Recurse to add images/animations to newDrawList */
        if (animationType != Scene.AnimationType.NO_ANIMATION) {
            screen.addAnimationToDrawList(imagePath, locX, locY, width, height, animationType, depth, 1.0, id);
        } else {
            screen.addImgToDrawList(imagePath, locX, locY, depth, id);
        }
        for (int scnIdx = 0; scnIdx < numSubScenes; scnIdx++) {
            if (subScenes[scnIdx].isActive()) {
                subScenes[scnIdx].updateScreen(false);
            }
        }
    }

    /*
     * Actually update screen with the image in this scene, as well as any images
     * from any subscenes.
     */
    final public void updateScreen(boolean topLvlCall) {

        if (topLvlCall) {
            screen.clearNewDrawList();

            // The scene should make sure the player gets drawn.
            if (showPlayer) {
                g.player.updateDrawList();
            }

            // Set the viewport of the scene.
            if (cameraType == CameraType.PLAYER) {
                screen.setViewport(g.player.getLocX(), g.player.getLocY(), cameraViewportWidth, cameraViewportHeight);
            } else if (cameraType == CameraType.GLOBAL) {
                screen.setViewport((int) (getLocX() + width / 2.0), (int) (getLocY() + height / 2.0), width, height);
            }
        }

        updateDrawList();

        if (topLvlCall) {

            screen.submitNewDrawList();

            /*
             * Update cursor as well by issuing a dummy movement event at the current
             * location.
             */
            if (!isSubscene) {
                Point mouseLoc = getPointerInfo().getLocation();
                Point screenLoc = screen.getLocationOnScreen();
                if ((mouseLoc.x > 600) && (mouseLoc.x < 615) && (mouseLoc.y > 280) && (mouseLoc.y < 300)) {
                    Log.error("pause");
                }
                int mvtX = (int) screen.windowToSceneX(mouseLoc.x - screenLoc.x);
                int mvtY = (int) screen.windowToSceneY(mouseLoc.y - screenLoc.y);

                Log.trace("mouseLoc (" + mouseLoc.x + "," + mouseLoc.y + "), screenLoc (" + screenLoc.x + ","
                        + screenLoc.y + "), mvt (" + mvtX + "," + mvtY + ")");

                actionHandler(g, BasicBackboneGame2D.MouseActions.MOVEMENT, mvtX, mvtY);
            }
        }
    }

    public void setLoc(int x, int y, int depth_) {
        locX = x;
        locY = y;
        depth = depth_;
    }

    public void setLoc(int x, int y) {
        setLoc(x, y, depth);
    }

    public int getLocX() {
        return locX;
    }

    public int getLocY() {
        return locY;
    }

    // Shift the scene location to the specified position and depth.
    // Additionally, check what the offset is between the old and new locations,
    // and apply this offset (R)ecursively to all subscenes.
    public void setLocR(int x, int y, int depth_) {
        int deltaX = x - locX;
        int deltaY = y - locY;
        int deltaD = depth_ - depth;

        setLoc(x, y, depth_);

        for (int scnIdx = 0; scnIdx < numSubScenes; scnIdx++) {
            Scene ss = subScenes[scnIdx];
            ss.setLocR(ss.locX + deltaX, ss.locY + deltaY, ss.depth + deltaD);
        }
    }

    public void setLocR(int x, int y) {
        setLocR(x, y, depth);
    }

    // Helper function for placing a subscene at the specified location relative its
    // parent. The function will shift the subscene and any of its subscenes (and
    // any of their subscenes, etc.) accordingly. This is mainly helpful for
    // wrapping new scenes: Scene subscene = subSceneRel(new SubScene, x, y, depth);
    public Scene subSceneRel(Scene s, int x, int y, int depth_) {
        s.setLocR(s.locX + x, s.locY + y, s.depth + depth_);
        return s;
    }

    public Scene subSceneRel(Scene s, int x, int y) {
        return subSceneRel(s, x, y, depth);
    }

    public void addTransitionRel(int sId, int x, int y, int w, int h, Jukebox.Sounds s) {
        transitions.add(new Transition(sId, locX + x, locY + y, w, h, s));
    }

    public void draw() {
        Log.trace("Draw");
        screen.repaint();
    }

    public void refresh() {
        updateScreen(true);
        draw();
    }

    public void setActiveState(boolean b) {
        isActive = b;
    }

    public boolean isActive() {
        return isActive;
    }

    public void swapImage(String newImagePath) {
        imagePath = newImagePath;
    }

    /* Add a transition to this scene. */
    final public void addTransition(Transition t) {
        transitions.add(t);
    }

    /* Is the (x,y) location within this scene? */
    public boolean isHit(int x, int y) {

        /*
         * Undo any scaling/padding on the location - all checks are based on nominal
         * scaling.
         */
        // x = GameFrame.frameToNativeX(x);
        // y = GameFrame.frameToNativeY(y);

        /* x/y locations were based on nominal scaling, so no conversion needed. */
        x -= locX;
        y -= locY;

        return isActive() && (x >= 0) && (x < width) && (y >= 0) && (y < height);
    }

    /*
     * Transition inner class - has isHit() and activate(). isHit() will return true
     * if the input (x,y) coords are within the transition object. activate() will
     * change the topLvlScene of the BasicBackboneGame2D class to the new scene
     * we're transitioning to.
     */
    public class Transition {

        int sceneId;
        int xLoc;
        int yLoc;
        int width;
        int height;

        /* Sound to play on click activation. Set to NONE for no sound. */
        Jukebox.Sounds sound;

        /*
         * Currently sceneId is only defined here and referenced as a magic number from
         * any scenes that call for the transition. Possible area of
         * cleanup/simplification later on.
         */
        public Transition(int sId, int x, int y, int w, int h, Jukebox.Sounds s) {
            sceneId = sId;
            xLoc = x;
            yLoc = y;
            width = w;
            height = h;
            sound = s;
        }

        /* isHit will return false if the (sub)scene is inactive. */
        public boolean isHit(int x, int y) {
            /*
             * Undo any scaling on the locations - all checks are based on nominal scaling.
             */
            // x = (int) ((float) (x - GameFrame.xPad) / GameFrame.scale);
            // y = (int) ((float) (y - GameFrame.yPad) / GameFrame.scale);

            /* x/y locations were based on nominal scaling, so no conversion needed. */
            x -= xLoc;
            y -= yLoc;
            return (x >= 0) && (x < width) && (y >= 0) && (y < height);
        }

        public void activate() {
            /* Play sound, no looping. */
            g.jukebox.play(sound, false);

            /*
             * Scene switching duty is handled within the SceneManager.java file in order to
             * keep all scene enums and handoffs in one location. Makes manual editing
             * easier.
             */
            SceneManager.switchScene(g, sceneId);
        }
    }

    /*
     * This will be overridden by each individual scene to provide custom action
     * handling. return value indicates if further action processing should be
     * stopped.
     */
    public int uniqueActionHandler(BasicBackboneGame2D g, BasicBackboneGame2D.MouseActions evtType, int evtX,
            int evtY) {
        return 0;
    }

    /* Base action handler, common to all scenes */
    public void actionHandler(BasicBackboneGame2D g, BasicBackboneGame2D.MouseActions evtType, int evtX, int evtY) {

        // Movement is generated, surprisingly, even if the mouse is stationary.
        // Only record movement events in the trace logs, i.e. when we don't care about
        // flooding the logs.
        String s = sceneName + ": evt " + evtType + ", (" + evtX + "," + evtY + ")";
        if (evtType == BasicBackboneGame2D.MouseActions.MOVEMENT) {
            Log.trace(s);
        } else {
            Log.debug(s);
        }

        /*
         * Handling order: 1) Transitions 2) Custom Scene Handling 3) Recurse Through
         * Subscenes
         */

        boolean hit = false; // Flag indicating if any subscene or transition returned true on their
                             // isHit() methods. If not, we'll use the default cursor.

        for (int tIdx = 0; tIdx < transitions.size(); tIdx++) {
            Transition tt = transitions.get(tIdx);
            if (tt.isHit(evtX, evtY)) {
                if (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) {
                    tt.activate();
                    /* Scene has switched, don't process any more in this scene. Do save, though. */
                    g.sm.saveState();
                    return;
                } else if (evtType == BasicBackboneGame2D.MouseActions.MOVEMENT) {
                    if (!isSubscene) {
                        screen.updateCursor(GameScreen.CursorType.TRANSITION);
                    }
                }
                hit = true;
            }
        }

        /*
         * Custom handling for this scene. This gives each scene a chance to custom
         * handle events before falling back on the default handling (other than
         * transitions, which are handled first).
         */
        if (uniqueActionHandler(g, evtType, evtX, evtY) != 0) {
            /* If return int is non-zero, further recursion/processing should be skipped. */
            return;
        }

        /* Check if any subscenes are hit and handle it if they are. */
        for (int scnIdx = 0; scnIdx < numSubScenes; scnIdx++) {
            Scene ss = subScenes[scnIdx];
            if (ss.isHit(evtX, evtY)) {
                ss.actionHandler(g, evtType, evtX, evtY);
                if (!isSubscene) {
                    screen.updateCursor(GameScreen.CursorType.INSPECTION);
                }
                hit = true;
            }
        }

        if ((!hit) && (!isSubscene)) {
            screen.updateCursor(GameScreen.CursorType.DEFAULT);
        }

        /*
         * Save after handling any left clicks that hit something, which could have
         * updated state.
         */
        if (hit && (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) && (!isSubscene)) {
            g.sm.saveState();
        }

    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public ScaleMap getScaleMap() {
        return scaleMap;
    }
}
