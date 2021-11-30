
package basicbackbonegame2d.Scenes.Menu.ItemSlot0;

import basicbackbonegame2d.Scene;

public class ItemSlot0 extends Scene {

    /* Enum of avilable images for this scene */
    public enum imagePathMap {
        /*
         * Menu will use the scene's swapImage and setActiveState to control which
         * image, if any, is drwan for this item.
         */
        DEFAULT("resources/images/KeyIcon.png");

        public String str;

        imagePathMap(String s) {
            str = s;
        }
    }

    public ItemSlot0() {
        /* Basic initialization params */
        sceneName = "ItemSlot0";
        isSubscene = true;
        xLoc = 225;
        yLoc = 200;
        width = 50;
        height = 50;

        /* Initialize this scene's image */
        imagePath = imagePathMap.DEFAULT.str;

        /* Reset screen - if top level scene */

        /* Create any subscenes and add to array */

        /* Add any starting transitions */

        /* Standard scene drawing routines for top level scenes */
    }

}