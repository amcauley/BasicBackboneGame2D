
package example.Scenes.S_Win;

import basicbackbonegame2d.GameFrame;
import basicbackbonegame2d.Jukebox;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.StateInfo;

public class S_Win extends Scene {

    static StateInfo stateInfo;

    public static StateInfo getStateInfo() {
        if (stateInfo == null) {
            stateInfo = new StateInfo_Win();
        }
        return stateInfo;
    }

    /* Enum for tracking/defining state info for this scene. */
    enum StateMap {
        RESERVED(0); // Win doesn't need any actual state for now

        int idx;

        StateMap(int i) {
            idx = i;
        }
    }

    public static class StateInfo_Win extends StateInfo {

        public StateInfo_Win() {
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];

            /* Initialize state values. */
            vals[StateMap.RESERVED.idx] = 0;
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

    /* Enum of avilable images for this scene */
    public enum imagePathMap {
        WIN("resources/images/Win.jpg");

        public String str;

        imagePathMap(String s) {
            str = s;
        }
    }

    public S_Win() {
        /* Basic initialization params */
        sceneName = "S_Win";
        isSubscene = false;
        animationType = Scene.AnimationType.NO_ANIMATION;
        width = GameFrame.NOMINAL_WIDTH;
        height = GameFrame.NOMINAL_HEIGHT;

        /* Initialize this scene's image */
        imagePath = imagePathMap.WIN.str;

        /* Create any subscenes and add to array */
        // No subscenes for this scene

        /* Add any starting transitions */
        // No transitions

        /* Victory music. */
        g.jukebox.stopAll();
        g.jukebox.play(Jukebox.Sounds.VICTORY, false);

        /* Standard scene drawing routines for top level scenes */
        refresh();
    }

}
