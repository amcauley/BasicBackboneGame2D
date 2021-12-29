
package example.Scenes.S_Room2;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Jukebox;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.SceneManager;
import basicbackbonegame2d.StateInfo;
import basicbackbonegame2d.Top;
import example.Scenes.S_Room2.S_Bauble.S_Bauble;
import example.Scenes.S_Room2.S_Key_In_Door.S_Key_In_Door;

public class S_Room2 extends Scene {

    static StateInfo stateInfo;

    public static StateInfo getStateInfo() {
        if (stateInfo == null) {
            stateInfo = new StateInfo_Room2();
        }
        return stateInfo;
    }

    /* Enum for tracking/defining state info for this scene. */
    enum StateMap {
        KEY_IN_DOOR(0); // Key is in the door

        int idx;

        StateMap(int i) {
            idx = i;
        }
    }

    public static class StateInfo_Room2 extends StateInfo {

        public StateInfo_Room2() {
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];

            /* Initialize state values. */
            vals[StateMap.KEY_IN_DOOR.idx] = 0; // Default to no key in door
        }

        @Override
        public String saveState() {
            String strOut = "";
            for (StateMap st : StateMap.values()) {
                strOut += String.valueOf(stateInfo.vals[st.idx]) + " ";
            }
            return strOut;
        }

        /* Use stateMap enum to map in values from input string */
        @Override
        public void loadState(String str) {
            String[] strVals = str.split(" ");
            int idx = 0;
            for (StateMap st : StateMap.values()) {
                stateInfo.vals[st.idx] = Integer.valueOf(strVals[idx++]);
            }
        }

    }

    /* Use an enum to better track subscenes of this scene */
    enum SubSceneMap {
        KEY_IN_DOOR(0), // Key in door
        BAUBLE(1);

        int idx;

        SubSceneMap(int i) {
            idx = i;
        }
    }

    /* Enum of avilable images for this scene */
    public enum imagePathMap {
        DARK("resources/images/Room2_dark.jpg"), LIGHT("resources/images/Room2_light.jpg");

        public String str;

        imagePathMap(String s) {
            str = s;
        }
    }

    public S_Room2() {
        /* Basic initialization params */
        sceneName = "S_Room2";
        isSubscene = false;
        animationType = Scene.AnimationType.NO_ANIMATION;
        width = 400;
        height = 400;

        /* Create any subscenes and add to array */
        numSubScenes = SubSceneMap.values().length;
        subScenes = new Scene[numSubScenes];

        subScenes[SubSceneMap.KEY_IN_DOOR.idx] = subSceneRel(new S_Key_In_Door(), 198, 227);
        subScenes[SubSceneMap.BAUBLE.idx] = subSceneRel(new S_Bauble(), 117, 293);

        /* Initialize this scene's image */
        if (SceneManager.sceneTable.get(SceneManager.TOP).vals[Top.StateMap.ROOM2_HAS_PWR.idx] == 0) {
            imagePath = imagePathMap.DARK.str;
            subScenes[SubSceneMap.KEY_IN_DOOR.idx].setActiveState(false);
            subScenes[SubSceneMap.BAUBLE.idx].setActiveState(false);
        } else {
            imagePath = imagePathMap.LIGHT.str;
            /*
             * If key is in door, set key in door subscene to active and add transition to
             * win screen.
             */
            if (stateInfo.vals[StateMap.KEY_IN_DOOR.idx] == 1) {
                // subScenes[SubSceneMap.KEY_IN_DOOR.idx].swapImage(S_Key_In_Door.imagePathMap.KEY_IN_DOOR.str);
                subScenes[SubSceneMap.KEY_IN_DOOR.idx].setActiveState(false);
                addTransitionRel(SceneManager.S_WIN, 193, 164, 76, 122, Jukebox.Sounds.NONE);
            }

            /* Set bauble invisible if it's been obtained already. */
            if (SceneManager.sceneTable.get(SceneManager.TOP).vals[Top.StateMap.HAS_BAUBLE.idx] == 1) {
                subScenes[SubSceneMap.BAUBLE.idx].setActiveState(false);
            }

        }

        /* Add any starting transitions */
        addTransitionRel(SceneManager.S_ROOM1, 20, 168, 53, 180, Jukebox.Sounds.DOOR0);

        /* Start BG music. */
        g.jukebox.play(Jukebox.Sounds.BG_MUSIC0, true);
    }

    @Override
    public int uniqueActionHandler(BasicBackboneGame2D g, BasicBackboneGame2D.MouseActions evtType, int evtX,
            int evtY) {

        /* Handle key */
        if ((evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON)
                && subScenes[SubSceneMap.KEY_IN_DOOR.idx].isHit(evtX, evtY)
                && (SceneManager.sceneTable.get(SceneManager.TOP).vals[Top.StateMap.HAS_KEY.idx] == 1)) {

            stateInfo.vals[StateMap.KEY_IN_DOOR.idx] = 1;
            /*
             * Set subscene inactive, revealing the background that has the key drawn on it
             * already. If we just swapped images, then we would get the wrong cursor when
             * hovering over the key - we want the transition cursor, since the key can't be
             * interacted with any more.
             */
            subScenes[SubSceneMap.KEY_IN_DOOR.idx].setActiveState(false);

            addTransitionRel(SceneManager.S_WIN, 193, 164, 76, 122, Jukebox.Sounds.NONE);

            refresh();

            /*
             * Cease further processing to prevent cursor type from changing again in
             * original calling routine.
             */
            return 1;
        }

        if ((evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON)
                && subScenes[SubSceneMap.BAUBLE.idx].isHit(evtX, evtY)) {

            /* Set bauble in scene to non-active so it won't be drawn. */
            subScenes[SubSceneMap.BAUBLE.idx].setActiveState(false);

            /* Update state to reflect the fact that bauble is taken. */
            SceneManager.sceneTable.get(SceneManager.TOP).vals[Top.StateMap.HAS_BAUBLE.idx] = 1;

            /* Refresh the screen. */
            refresh();
        }

        return 0;
    }

}
