
package example.Scenes.Menu.New_Game;

import basicbackbonegame2d.Scene;

public class New_Game extends Scene {

    /* Enum of avilable images for this scene */
    public enum imagePathMap {
        NEW_GAME("resources/images/New_Game.jpg");

        public String str;

        imagePathMap(String s) {
            str = s;
        }
    }

    public New_Game() {
        /* Basic initialization params */
        sceneName = "New_Game";
        isSubscene = true;
        xLoc = 51;
        yLoc = 326;
        width = 98;
        height = 48;

        /* Initialize this scene's image */
        imagePath = imagePathMap.NEW_GAME.str;

        /* Reset screen - if top level scene */

        /* Create any subscenes and add to array */

        /* Add any starting transitions */

        /* Standard scene drawing routines for top level scenes */
    }

}