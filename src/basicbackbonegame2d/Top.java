
package basicbackbonegame2d;

/* Pseudo-scene for top level management. */
public class Top extends Scene {

    static StateInfo stateInfo = new StateInfo_Top();

    public static StateInfo getStateInfo() {
        if (stateInfo == null) {
            stateInfo = new StateInfo_Top();
        }
        return stateInfo;
    }

    /* Enum for tracking/defining state info for this scene. */
    public enum StateMap {
        LAST_SCENE_ID(0), // Stores the last active scene
        HAS_KEY(1), // Player has the key
        ROOM2_HAS_PWR(2), // Room2 scene is powered
        HAS_BAUBLE(3); // Useless item just for testing

        public int idx;

        StateMap(int i) {
            idx = i;
        }
    }

    public static class StateInfo_Top extends StateInfo {

        public StateInfo_Top() {
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];

            /* Initialize state values. */
            vals[StateMap.LAST_SCENE_ID.idx] = SceneManager.MENU; // Default scene is the Menu.
            vals[StateMap.HAS_KEY.idx] = 0; // No key yet
            vals[StateMap.ROOM2_HAS_PWR.idx] = 0; // Not yet powered
            vals[StateMap.HAS_BAUBLE.idx] = 0; // Not collected
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

    public Top() {
        /* Basic initialization params */
        sceneName = "Top";
        isSubscene = false;
        locX = 0;
        locY = 0;
        width = 0;
        height = 0;

        /* Initialize this scene's image */
        // NA

        /* Reset screen */
        // NA

        /* Create any subscenes and add to array */
        // NA

        /* Add any starting transitions */
        // NA

        /* Standard scene drawing routines for top level scenes */
        // NA
    }

}
