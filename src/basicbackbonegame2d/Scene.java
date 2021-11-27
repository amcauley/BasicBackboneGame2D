
package basicbackbonegame2d;

import static java.awt.MouseInfo.getPointerInfo;
import java.awt.Point;
import java.util.List;

import basicbackbonegame2d.SceneManager.SceneList;

import java.util.ArrayList;

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
    static public GameScreen screen = new GameScreen();

    public String sceneName = "default";

    public boolean isSubscene; // flag indicating that this scene is a subscene.
    public int xLoc; // x location (ex top left corner for rectangular scenes)
    public int yLoc; // y location
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

    /* Dynamic array of Transitions */
    public List<Transition> transitions = new ArrayList<>();

    // Map of obstacles in this scenes.
    // This can be uninitialized if the scene doesn't have any.
    public Obstacle obstacle;

    /* Default scene constructor */
    public Scene() {
        numSubScenes = 0;
        isActive = true;
        depth = DEFAULT_DEPTH;
        animationType = AnimationType.NO_ANIMATION;
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
            screen.addAnimationToDrawList(imagePath, xLoc, yLoc, width, height, animationType, depth, id);
        } else {
            screen.addImgToDrawList(imagePath, xLoc, yLoc, depth, id);
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

            // The player should make sure the player gets drawn.
            // This can later be expanded to selectively hide the player in particular
            // scenes or situations.
            g.player.updateDrawList();
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
                actionHandler(g, BasicBackboneGame2D.MouseActions.MOVEMENT, mouseLoc.x - screenLoc.x,
                        mouseLoc.y - screenLoc.y);
            }
        }
    }

    public void setLoc(int x, int y) {
        xLoc = x;
        yLoc = y;
    }

    public void setLoc(int x, int y, int depth_) {
        setLoc(x, y);
        depth = depth_;
    }

    public void draw() {
        // System.out.println("Draw");
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
        x = GameFrame.getNativeX(x);
        y = GameFrame.getNativeY(y);

        /* x/y locations were based on nominal scaling, so no conversion needed. */
        x -= xLoc;
        y -= yLoc;

        return isActive() && (x >= 0) && (x < width) && (y >= 0) && (y < height);
    }

    /*
     * Transition inner class - has isHit() and activate(). isHit() will return true
     * if the input (x,y) coords are within the transition object. activate() will
     * change the topLvlScene of the BasicBackboneGame2D class to the new scene
     * we're transitioning to.
     */
    public class Transition {

        SceneList sceneId;
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
        public Transition(SceneList sId, int x, int y, int w, int h, Jukebox.Sounds s) {
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
            x = (int) ((float) (x - GameFrame.xPad) / GameFrame.scale);
            y = (int) ((float) (y - GameFrame.yPad) / GameFrame.scale);

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

        /* Handle the event. */
        // System.out.println(sceneName + ": evt " + evtType + ", (" + evtX + "," + evtY
        // + ")");

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
}
