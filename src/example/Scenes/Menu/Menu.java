
package example.Scenes.Menu;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Log;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.SceneManager;
import basicbackbonegame2d.StateInfo;
import basicbackbonegame2d.Top;
import example.Scenes.Menu.ItemSlot0.ItemSlot0;
import example.Scenes.Menu.ItemSlot1.ItemSlot1;
import example.Scenes.Menu.Load.Load;
import example.Scenes.Menu.New_Game.New_Game;
import example.Scenes.Menu.Resume.Resume;
import example.Scenes.Menu.Save.Save;

import java.io.IOException;
import javax.swing.JFileChooser;

public class Menu extends Scene {

    /* Directory constants */
    static final String SAVE_DIRECTORY = "C:";
    static final String NEW_GAME_FILENAME = "resources/txt/NewGameTemplate.txt";

    /* Number of item slots supported */
    static final int NUM_ITEM_SLOTS = 2;

    /* This holds the state info for Menu */
    static StateInfo stateInfo;

    /* Return state info (to SceneManager) */
    public static StateInfo getStateInfo() {
        if (stateInfo == null) {
            stateInfo = new StateInfo_Menu();
        }
        return stateInfo;
    }

    /* Enum for tracking/defining state info for this scene. */
    public enum StateMap {
        LAST_SCENE(0); // What scene we were in prior to entering menu

        public int idx;

        StateMap(int i) {
            idx = i;
        }
    }

    public static class StateInfo_Menu extends StateInfo {

        public StateInfo_Menu() {
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];

            /* Initialize state values. */
            vals[StateMap.LAST_SCENE.idx] = 0;
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
        RESUME(0), SAVE(1), LOAD(2), NEW_GAME(3), ITEM_SLOT0(4), // Put item slots in order at the end of scene map
        ITEM_SLOT1(5);

        int idx;

        SubSceneMap(int i) {
            idx = i;
        }
    }

    /* Enum of avilable images for this scene (or subscenes) */
    enum ImagePathMap {
        MENU("resources/images/Menu.jpg"), KEY_ICON("resources/images/KeyIcon.png"),
        BAUBLE_ICON("resources/images/BaubleIcon.png");

        public String str;

        ImagePathMap(String s) {
            str = s;
        }
    }

    public Menu() {
        /* Basic initialization params */
        sceneName = "Menu";
        isSubscene = false;
        animationType = Scene.AnimationType.NO_ANIMATION;
        locX = 0;
        locY = 0;
        width = 400;
        height = 400;

        /* Initialize this scene's image */
        imagePath = ImagePathMap.MENU.str;

        /* Create any subscenes and add to array */
        numSubScenes = SubSceneMap.values().length;
        subScenes = new Scene[numSubScenes];

        subScenes[SubSceneMap.RESUME.idx] = subSceneRel(new Resume(), 51, 26);
        subScenes[SubSceneMap.SAVE.idx] = subSceneRel(new Save(), 51, 126);
        subScenes[SubSceneMap.LOAD.idx] = subSceneRel(new Load(), 51, 226);
        subScenes[SubSceneMap.NEW_GAME.idx] = subSceneRel(new New_Game(), 51, 326);

        /* Populate item slots */
        assert (SubSceneMap.values().length - SubSceneMap.ITEM_SLOT0.idx == NUM_ITEM_SLOTS);

        int cntCheck = 0; // Just to help check that all item slots are accounted for in our copy/pasting.

        subScenes[SubSceneMap.ITEM_SLOT0.idx] = subSceneRel(new ItemSlot0(), 225, 200);
        subScenes[SubSceneMap.ITEM_SLOT0.idx].setActiveState(false);
        cntCheck++;
        subScenes[SubSceneMap.ITEM_SLOT1.idx] = subSceneRel(new ItemSlot1(), 300, 200);
        subScenes[SubSceneMap.ITEM_SLOT1.idx].setActiveState(false);
        cntCheck++;

        assert (cntCheck == NUM_ITEM_SLOTS);

        /* Now add in all the items that the player has. */

        cntCheck = SubSceneMap.ITEM_SLOT0.idx;

        if (SceneManager.sceneTable.get(SceneManager.TOP).vals[Top.StateMap.HAS_KEY.idx] != 0) {
            subScenes[cntCheck].swapImage(ImagePathMap.KEY_ICON.str);
            subScenes[cntCheck++].setActiveState(true);
        }
        if (SceneManager.sceneTable.get(SceneManager.TOP).vals[Top.StateMap.HAS_BAUBLE.idx] != 0) {
            subScenes[cntCheck].swapImage(ImagePathMap.BAUBLE_ICON.str);
            subScenes[cntCheck++].setActiveState(true);
        }

        assert (cntCheck - SubSceneMap.ITEM_SLOT0.idx <= NUM_ITEM_SLOTS);

        /* Add any starting transitions */
        // No transitions

        /* Music handling (if any) */
        // g.jukebox.stopAll();

        Log.debug("Menu last scene ID: " + stateInfo.vals[StateMap.LAST_SCENE.idx]);
    }

    @Override
    public int uniqueActionHandler(BasicBackboneGame2D g, BasicBackboneGame2D.MouseActions evtType, int evtX,
            int evtY) {

        if ((evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON)
                && subScenes[SubSceneMap.RESUME.idx].isHit(evtX, evtY)) {

            g.sm.switchScene(g, stateInfo.vals[StateMap.LAST_SCENE.idx]);

        } else if ((evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON)
                && subScenes[SubSceneMap.SAVE.idx].isHit(evtX, evtY)) {

            JFileChooser fc = new JFileChooser(SAVE_DIRECTORY);

            int fcRet = fc.showSaveDialog(g.gameFrame);

            if (fcRet == JFileChooser.APPROVE_OPTION) {
                String fileName = fc.getSelectedFile().getAbsolutePath();
                g.sm.saveState(fileName);
            } else {
                Log.info("Save aborted");
            }

        } else if ((evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON)
                && subScenes[SubSceneMap.LOAD.idx].isHit(evtX, evtY)) {

            JFileChooser fc = new JFileChooser(SAVE_DIRECTORY);

            int fcRet = fc.showOpenDialog(g.gameFrame);

            if (fcRet == JFileChooser.APPROVE_OPTION) {
                String fileName = fc.getSelectedFile().getAbsolutePath();
                try {
                    g.sm.loadState(fileName);
                    /* State is loaded, now update topLvlScene based on loaded state. */
                    g.topLvlSceneIdx = stateInfo.vals[Top.StateMap.LAST_SCENE_ID.idx];
                    g.sm.switchScene(g, g.topLvlSceneIdx);
                } catch (IOException ex) {
                    Log.error("File load error: " + ex.getMessage());
                }
            } else {
                Log.info("Load aborted");
            }

        } else if ((evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON)
                && subScenes[SubSceneMap.NEW_GAME.idx].isHit(evtX, evtY)) {

            try {
                g.sm.loadStateResource(NEW_GAME_FILENAME);
                /* State is loaded, now update topLvlScene based on loaded state. */
                g.topLvlSceneIdx = stateInfo.vals[Top.StateMap.LAST_SCENE_ID.idx];
                g.sm.switchScene(g, g.topLvlSceneIdx);
            } catch (IOException ex) {
                Log.error("New game error: " + ex.getMessage());
            }

        }

        if (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) {
            // Stop further action processing, which will also prevent game being saved.
            // This will prevent cursor updates on the menu scene from overwriting
            // the newly transitioned scene's cursor choices.
            return 1;
        }

        return 0;
    }

}
