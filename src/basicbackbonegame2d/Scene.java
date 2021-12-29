
package basicbackbonegame2d;

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
        GLOBAL, // The camera will show the entire top level scene at once
        PLAYER, // The camera's viewport follows the player around the scene
        PLAYER_CONSTRAINED // Similar to PLAYER, but the viewport won't show anything outside the bounds of
                           // the scene
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
            if (cameraType == CameraType.GLOBAL) {
                screen.setViewport((int) (getLocX() + width / 2.0), (int) (getLocY() + height / 2.0), width, height);
            } else if (cameraType == CameraType.PLAYER) {
                screen.setViewport(g.player.getLocX(), g.player.getLocY(), cameraViewportWidth, cameraViewportHeight);
            } else if (cameraType == CameraType.PLAYER_CONSTRAINED) {
                int xx = Math.min((int) (width - cameraViewportWidth / 2.0),
                        Math.max((int) (cameraViewportWidth / 2.0), g.player.getLocX()));
                int yy = Math.min((int) (height - cameraViewportHeight / 2.0),
                        Math.max((int) (cameraViewportHeight / 2.0), g.player.getLocY()));
                screen.setViewport(xx, yy, cameraViewportWidth, cameraViewportHeight);
            }
        }

        updateDrawList();

        if (topLvlCall) {
            screen.submitNewDrawList();
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
        transitions.add(new Transition(g, sId, locX + x, locY + y, w, h, s));
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
     * This will be overridden by each individual scene to provide custom action
     * handling. return value indicates if further action processing should be
     * stopped.
     */
    public int uniqueActionHandler(BasicBackboneGame2D g, BasicBackboneGame2D.MouseActions evtType, int evtX,
            int evtY) {
        return 0;
    }

    // Return a list of any of any (sub)scenes where the provided location is a hit.
    // Event positions are in scene units.
    // Include this scene itself as the first entry if it's hit.
    // The "R" suffix is a reminder that this is recursive.
    public ArrayList<Scene> getHitScenesR(int evtX, int evtY) {
        ArrayList<Scene> scenes = new ArrayList<Scene>();

        // If this scene itself is hit, it'll be the first entry.
        if (isHit(evtX, evtY)) {
            scenes.add(this);
        }

        /* Check if any subscenes are hit and handle it if they are. */
        for (int scnIdx = 0; scnIdx < numSubScenes; scnIdx++) {
            Scene ss = subScenes[scnIdx];

            ArrayList<Scene> subSceneHits = ss.getHitScenesR(evtX, evtY);
            if (!subSceneHits.isEmpty()) {
                scenes.addAll(subSceneHits);
            }
        }

        return scenes;
    }

    // Get a list of any transitions in this scene that are hit.
    // Don't recurse into any subscenes.
    public ArrayList<Transition> getHitTransitions(int evtX, int evtY) {
        ArrayList<Transition> transitionHits = new ArrayList<Transition>();

        for (int tIdx = 0; tIdx < transitions.size(); tIdx++) {
            Transition tt = transitions.get(tIdx);

            if (tt.isHit(evtX, evtY)) {
                transitionHits.add(tt);
            }
        }

        return transitionHits;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public ScaleMap getScaleMap() {
        return scaleMap;
    }
}
