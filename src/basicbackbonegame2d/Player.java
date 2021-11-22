package basicbackbonegame2d;

// Player class is derived from the scene class for now.
// TODO: They should probably both derive from some common drawable/animatable base class.
public class Player extends Scene {

    /* Enum of avilable images for this scene */
    public enum imagePathMap {
        PLAYER("resources/images/Clock.png");

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
        width = 25; /* For animations, this is the size of a single frame. */
        height = 25;
        depth = 100;

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
        screen.addAnimationToDrawList(imagePath, xLoc, yLoc, width, height, animationType, depth, id);
    }

}
