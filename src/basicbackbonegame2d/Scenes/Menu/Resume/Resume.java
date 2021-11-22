
package basicbackbonegame2d.Scenes.Menu.Resume;

import basicbackbonegame2d.Scene;

public class Resume extends Scene {

    /* Enum of avilable images for this scene */
    public enum imagePathMap {
        RESUME("resources/images/Resume.jpg");

        public String str;

        imagePathMap(String s) {
            str = s;
        }
    }

    public Resume() {
        /* Basic initialization params */
        sceneName = "Resume";
        isSubscene = true;
        xLoc = 51;
        yLoc = 26;
        width = 98;
        height = 48;

        /* Initialize this scene's image */
        imagePath = imagePathMap.RESUME.str;

        /* Reset screen - if top level scene */

        /* Create any subscenes and add to array */

        /* Add any starting transitions */

        /* Standard scene drawing routines for top level scenes */
    }

}