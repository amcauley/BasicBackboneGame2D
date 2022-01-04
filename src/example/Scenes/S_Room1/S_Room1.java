
package example.Scenes.S_Room1;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Jukebox;
import basicbackbonegame2d.Obstacle;
import basicbackbonegame2d.ScaleMap;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.SceneManager;
import basicbackbonegame2d.StateInfo;
import basicbackbonegame2d.Top;
import example.Scenes.S_Room1.S_Clock.S_Clock;
import example.Scenes.S_Room1.S_Key.S_Key;
import example.Scenes.S_Room1.S_Switch.S_Switch;

public class S_Room1 extends Scene {

    static StateInfo stateInfo;

    public static StateInfo getStateInfo() {
        if (stateInfo == null) {
            stateInfo = new StateInfo_Room1();
        }
        return stateInfo;
    }

    /* Enum for tracking/defining state info for this scene. */
    enum StateMap {
        RESERVED(0); // Reserved for future use

        int idx;

        StateMap(int i) {
            idx = i;
        }
    }

    public static class StateInfo_Room1 extends StateInfo {

        public StateInfo_Room1() {
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];

            /* Initialize state values. */
            vals[StateMap.RESERVED.idx] = 0; // Reserved for future use
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
        KEY(0), SWITCH(1), CLOCK(2);

        int idx;

        SubSceneMap(int i) {
            idx = i;
        }
    }

    /* Enum of avilable images for this scene */
    public enum imagePathMap {
        ROOM1("resources/images/Room1.jpg");

        public String str;

        imagePathMap(String s) {
            str = s;
        }
    }

    public S_Room1() {
        /* Basic initialization params */
        sceneName = "S_Room1";
        isSubscene = false;
        showPlayer = true;
        animationType = Scene.AnimationType.NO_ANIMATION;
        width = 400;
        height = 400;

        cameraType = CameraType.PLAYER_CONSTRAINED;
        cameraViewportWidth = 350;
        cameraViewportHeight = 350;

        /* Initialize this scene's image */
        imagePath = imagePathMap.ROOM1.str;

        obstacle = new Obstacle("resources/images/Room1_Obstacle.bmp", width, height);
        scaleMap = new ScaleMap("resources/images/Room1_ScaleMap.bmp");

        /* Create any subscenes and add to array */
        numSubScenes = SubSceneMap.values().length;
        subScenes = new Scene[numSubScenes];

        subScenes[SubSceneMap.KEY.idx] = subSceneRel(new S_Key(), 100, 280, 10);

        /* Set key invisible if it's been obtained already. */
        if (SceneManager.sceneTable.get(SceneManager.TOP).vals[Top.StateMap.HAS_KEY.idx] == 1) {
            subScenes[SubSceneMap.KEY.idx].setActiveState(false);
        }

        subScenes[SubSceneMap.SWITCH.idx] = subSceneRel(new S_Switch(), 223, 186);

        /* Set switch up (down by default) if it's been toggled. */
        if (SceneManager.sceneTable.get(SceneManager.TOP).vals[Top.StateMap.ROOM2_HAS_PWR.idx] == 1) {
            subScenes[SubSceneMap.SWITCH.idx].swapImage(S_Switch.imagePathMap.UP.str);
        }

        /* Clock animation. */
        subScenes[SubSceneMap.CLOCK.idx] = subSceneRel(new S_Clock(), 128, 149, 100);

        /* Add any starting transitions */
        addTransitionRel(SceneManager.S_ROOM2, 325, 165, 53, 180, Jukebox.Sounds.DOOR0);

        /* Start BG music. */
        // g.jukebox.play(Jukebox.Sounds.BG_MUSIC0, true);
    }

    @Override
    public int uniqueActionHandler(BasicBackboneGame2D g, BasicBackboneGame2D.MouseActions evtType, int evtX,
            int evtY) {

        if ((evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON)
                && subScenes[SubSceneMap.KEY.idx].isHit(evtX, evtY)) {

            /* Set key in scene to non-active so it won't be drawn. */
            subScenes[SubSceneMap.KEY.idx].setActiveState(false);

            /* Update state to reflect the fact that key is taken. */
            SceneManager.sceneTable.get(SceneManager.TOP).vals[Top.StateMap.HAS_KEY.idx] = 1;

            /* Refresh the screen. */
            refresh();
        }

        /* Handle light switch */
        if ((evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON)
                && subScenes[SubSceneMap.SWITCH.idx].isHit(evtX, evtY)) {

            /* Flip switch */
            SceneManager.sceneTable.get(SceneManager.TOP).vals[Top.StateMap.ROOM2_HAS_PWR.idx] ^= 0x1;

            subScenes[SubSceneMap.SWITCH.idx]
                    .swapImage((SceneManager.sceneTable.get(SceneManager.TOP).vals[Top.StateMap.ROOM2_HAS_PWR.idx] == 1)
                            ? S_Switch.imagePathMap.UP.str
                            : S_Switch.imagePathMap.DOWN.str);

            refresh();
        }

        return 0;

    }
}
