
package basicbackbonegame2d.Scenes.Menu.Load;

import basicbackbonegame2d.Scene;

public class Load extends Scene {

    /* Enum of avilable images for this scene */
    public enum imagePathMap {
        LOAD("resources/images/Load.jpg");

        public String str;

        imagePathMap(String s) {
            str = s;
        }
    }

    public Load() {
        /* Basic initialization params */
        sceneName = "Load";
        isSubscene = true;
        xLoc = 51;
        yLoc = 226;
        width = 98;
        height = 48;

        /* Initialize this scene's image */
        imagePath = imagePathMap.LOAD.str;

        /* Reset screen - if top level scene */

        /* Create any subscenes and add to array */

        /* Add any starting transitions */

        /* Standard scene drawing routines for top level scenes */
    }

}