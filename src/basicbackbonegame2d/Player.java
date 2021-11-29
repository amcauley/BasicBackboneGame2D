package basicbackbonegame2d;

import java.awt.geom.Point2D.Double;

// Player class is derived from the scene class for now.
// TODO: They should probably both derive from some common drawable/animatable base class.
public class Player extends Scene {

    // Player's view of any obstacles on the map.
    Obstacle obstacle;

    // Scaling map for the player.
    ScaleMap scaleMap;

    double scale;

    Pathing path;

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
        xLoc = 130; // TODO: Locations should be normalized to the range [0, 1]. Currently their in
                    // native format, i.e. ignoring padding and scaling.
        yLoc = 150;
        width = 32; /* For animations, this is the size of a single frame. */
        height = 32;
        depth = 100;
        scale = 1.0;

        /* Initialize this scene's image */
        imagePath = imagePathMap.PLAYER.str;

        path = new Pathing();
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
            if (obstacle == null) {
                return;
            } else if (obstacle.isClear(GameFrame.frameToNormalizedX(evtX), GameFrame.frameToNormalizedY(evtY))) {
                // setLoc(GameFrame.getNativeX(evtX), GameFrame.getNativeY(evtY));
                path.generatePath(obstacle, GameFrame.nativeToNormalizedX(xLoc), GameFrame.nativeToNormalizedY(yLoc),
                        GameFrame.frameToNormalizedX(evtX), GameFrame.frameToNormalizedY(evtY), 0.1);
                // System.out.println(path.path);
            }
        }
    }

    public void setObstacle(Obstacle o) {
        obstacle = o;
    }

    public void setScaleMap(ScaleMap s) {
        scaleMap = s;
    }

    public void setDepth(int d) {
        depth = d;
    }

    public void onTick() {
        // System.out.println("Player tick");
        double xLocNormalized = GameFrame.nativeToNormalizedX(xLoc);
        double yLocNormalized = GameFrame.nativeToNormalizedY(yLoc);

        // Avoid precision errors with converting formats unless there's actual movement
        // required. Otherwise the player can glide around on their own.
        if (path.hasNext()) {
            Double nextLocation = path.getNext(xLocNormalized, yLocNormalized, 0.05);
            // System.out.println("Player @ (" + xLocNormalized + ", " + yLocNormalized +
            // "), moving to " + nextLocation);
            setLoc(GameFrame.normalizedToNativeX(nextLocation.x), GameFrame.normalizedToNativeY(nextLocation.y));

            if (scaleMap != null) {
                scale = scaleMap.getScalingFactor(nextLocation.x, nextLocation.y);
                // TODO: Refactor scaleMap into depthMap.
                // Will probably need auxilliary JSON to describe how the values map to scale
                // and depth. Ex. what are min and max values, is there any offset on top of
                // linear scaling, do we even want linear scaling, which image corresponds to
                // the actual map, etc.
                setDepth(10 + (int) (scale * 20));
            }
        }
    }
}
