
package example.Scenes.S_Room2.S_Bauble;

import basicbackbonegame2d.Scene;

public class S_Bauble extends Scene {

    /* Enum of avilable images for this scene */
    public enum imagePathMap {
        BAUBLE("resources/images/Bauble.png");

        public String str;

        imagePathMap(String s) {
            str = s;
        }
    }

    public S_Bauble() {
        /* Basic initialization params */
        sceneName = "S_Bauble";
        isSubscene = true;
        animationType = Scene.AnimationType.NO_ANIMATION;
        xLoc = 117;
        yLoc = 293;
        width = 25;
        height = 25;

        /* Initialize this scene's image */
        imagePath = imagePathMap.BAUBLE.str;

        /* Reset screen - if top level scene */

        /* Create any subscenes and add to array */

        /* Add any starting transitions */

        /* Standard scene drawing routines for top level scenes */
    }

}