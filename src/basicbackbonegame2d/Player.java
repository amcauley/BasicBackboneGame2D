package basicbackbonegame2d;

// Player class is derived from the scene class for now.
// TODO: They should probably both derive from some common drawable/animatable base class.
public class Player extends Scene {

    // Player's view of any obstacles on the map.
    Obstacle obstacle;

    // Scaling map for the player.
    ScaleMap scaleMap;

    double scale;

    /* Enum of avilable images for this scene */
    public enum imagePathMap {
        PLAYER("resources/images/Player.bmp");

        public String str;

        imagePathMap(String s) {
            str = s;
        }
    }

    public Player() {
        /* Basic initialization params */
        sceneName = "Player";
        isSubscene = true;
        animationType = Scene.AnimationType.ANIMATED_WITH_LOOP;
        xLoc = 130; // TODO: Locations should be normalized to the range [0, 1].
        yLoc = 150;
        width = 32; /* For animations, this is the size of a single frame. */
        height = 32;
        depth = 100;
        scale = 1.0;

        /* Initialize this scene's image */
        imagePath = imagePathMap.PLAYER.str;
    }

    @Override
    public void updateDrawList() {
        /*
         * ID should be unique to each (sub)scene image. Append imagePath to sceneName
         * in case scene uses multiple images. imagePath isn't enough by itself in case
         * multiple (sub)scenes use the same image.
         */
        String id = sceneName + "_" + imagePath;
        screen.addAnimationToDrawList(imagePath, xLoc, yLoc, width, height, animationType, depth, scale, id);
    }

    @Override
    public void actionHandler(BasicBackboneGame2D g, BasicBackboneGame2D.MouseActions evtType, int evtX, int evtY) {
        if (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) {
            if (scaleMap != null) {
                scale = scaleMap.getScalingFactor(GameFrame.getNormalizedX(evtX), GameFrame.getNormalizedY(evtY));
            }

            if (obstacle == null) {
                return;
            } else if (obstacle.isClear(GameFrame.getNormalizedX(evtX), GameFrame.getNormalizedY(evtY))) {
                setLoc(GameFrame.getNativeX(evtX), GameFrame.getNativeY(evtY));
            }
        }
    }

    public void setObstacle(Obstacle o) {
        obstacle = o;
    }

    public void setScaleMap(ScaleMap s) {
        scaleMap = s;
    }
}
