package basicbackbonegame2d.Scenes.S_Room1.S_Clock;

import basicbackbonegame2d.Scene;

public class S_Clock extends Scene {

    /* Enum of avilable images for this scene */
    public enum imagePathMap {
        CLOCK("resources/images/Clock.png");

        public String str;

        imagePathMap(String s) {
            str = s;
        }
    }

    public S_Clock() {
        /* Basic initialization params */
        sceneName = "S_Clock";
        isSubscene = true;
        animationType = Scene.AnimationType.ANIMATED_WITH_LOOP;
        xLoc = 135;
        yLoc = 135;
        width = 25; /* For animations, this is the size of a single frame. */
        height = 25;

        /* Initialize this scene's image */
        imagePath = imagePathMap.CLOCK.str;

        /* Reset screen - if top level scene */

        /* Create any subscenes and add to array */

        /* Add any starting transitions */

        /* Standard scene drawing routines for top level scenes */
    }

}