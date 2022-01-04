package basicbackbonegame2d;

/*
 * Transition class - has isHit() and activate(). isHit() will return true
 * if the input (x,y) coords are within the transition object. activate() will
 * change the topLvlScene of the BasicBackboneGame2D class to the new scene
 * we're transitioning to.
 */
public class Transition {

    BasicBackboneGame2D bbg;

    int sceneId;
    int xLoc;
    int yLoc;
    int width;
    int height;

    /* Sound to play on click activation. Set to NONE for no sound. */
    Jukebox.Sounds sound;

    public Transition(BasicBackboneGame2D bbg_, int sId, int x, int y, int w, int h, Jukebox.Sounds s) {
        bbg = bbg_;
        sceneId = sId;
        xLoc = x;
        yLoc = y;
        width = w;
        height = h;
        sound = s;
    }

    /* isHit will return false if the (sub)scene is inactive. */
    public boolean isHit(int x, int y) {
        /* x/y locations are based on nominal scaling */
        x -= xLoc;
        y -= yLoc;
        return (x >= 0) && (x < width) && (y >= 0) && (y < height);
    }

    public void activate() {
        /* Play sound, no looping. */
        bbg.jukebox.play(sound, false);

        /*
         * Scene switching duty is handled within the SceneManager.java file in order to
         * keep all scene enums and handoffs in one location. Makes manual editing
         * easier.
         */
        bbg.sm.switchScene(bbg, sceneId);
    }
}
