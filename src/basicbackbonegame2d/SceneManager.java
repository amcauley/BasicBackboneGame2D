
package basicbackbonegame2d;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import example.Scenes.Menu.Menu;
import example.Scenes.S_Room1.S_Room1;
import example.Scenes.S_Room2.S_Room2;
import example.Scenes.S_Win.S_Win;

public class SceneManager {

    static final String AUTOSAVE_FILENAME = "AutoSave.txt";

    public static final int TOP = 0;
    public static final int MENU = 1;
    public static final int S_ROOM1 = 2;
    public static final int S_ROOM2 = 3;
    public static final int S_WIN = 4;
    public static final int SCENE_COUNT = 5;

    public static Hashtable<Integer, StateInfo> sceneTable;

    public GameScreen gs;

    // Last processed event X, Y location, in window coordinates.
    int lastEvtX;
    int lastEvtY;

    public SceneManager() {
        Log.info("Initializing SM");

        sceneTable = new Hashtable<Integer, StateInfo>();
        sceneTable.put(TOP, Top.getStateInfo());
        sceneTable.put(MENU, Menu.getStateInfo());
        sceneTable.put(S_ROOM1, S_Room1.getStateInfo());
        sceneTable.put(S_ROOM2, S_Room2.getStateInfo());
        sceneTable.put(S_WIN, S_Win.getStateInfo());

    }

    public void setGameScreen(GameScreen gameScreen) {
        gs = gameScreen;
    }

    public Path getAutoSaveFilePath() {
        return Paths.get(System.getProperty("user.dir"), AUTOSAVE_FILENAME);
    }

    /* Load saved state from file into each scene's state */
    public void loadState(String fileName) throws IOException {
        Log.info("Loading from file " + fileName);
        String thisLn;
        int thisIdx = 0;
        try (FileReader fr = new FileReader(fileName); BufferedReader br = new BufferedReader(fr);) {
            while ((thisLn = br.readLine()) != null) {
                Log.debug("Loading state idx " + thisIdx);
                sceneTable.get(thisIdx++).loadState(thisLn);
            }
        }
    }

    public void loadStateResource(String resourceName) throws IOException {
        String thisLn;
        int thisIdx = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourceName)));) {
            while ((thisLn = br.readLine()) != null) {
                Log.debug("Loading state resource idx " + thisIdx);
                sceneTable.get(thisIdx++).loadState(thisLn);
            }
        }
    }

    /* If no filename specified, use autosave file. */
    public void loadState() throws IOException {
        Path filePath = getAutoSaveFilePath();
        if (Files.exists(filePath)) {
            loadState(filePath.toString());
        } else {
            Log.info("No AutoSave found");
        }
    }

    /* Save state to file. */
    public void saveState(String fileName) {
        /* Add .txt suffix if not already present. */
        String fEnd = fileName.substring(fileName.length() - 4);
        if (!fEnd.equals(".txt")) {
            fileName += ".txt";
        }

        try (PrintWriter out = new PrintWriter(fileName)) {
            Log.info("Saving to file " + fileName);

            /*
             * Iterate over scenes and write their save state info out to file, one line per
             * scene in the same order the scenes were declared (important to have fixed
             * order for when we want to load state from this file later.
             */
            for (int sIdx = 0; sIdx < SCENE_COUNT; sIdx++) {
                String state = sceneTable.get(sIdx).saveState();
                Log.debug("Scene " + sIdx + " save state: " + state);
                /* Write to file */
                out.println(state);
            }

        } catch (IOException e) {
            // Should probably handle this exception
        }

    }

    /* If no filename specified, use autosave file. */
    public void saveState() {
        saveState(getAutoSaveFilePath().toString());
    }

    public void switchScene(BasicBackboneGame2D g, int sceneId) {

        switch (sceneId) {
            case MENU: // Menu "Scene"
                /* Store the scene we're leaving. */
                Log.debug("Latching LAST_SCENE, old " + sceneTable.get(MENU).vals[Menu.StateMap.LAST_SCENE.idx]
                        + ", new " + g.topLvlSceneIdx);

                /*
                 * If the current scene is already the menu, for example if the program
                 * previously exited from the menu screen, don't latch menu into LAST_SCENE, or
                 * else we'll not be able to resume from the menu screen.
                 */
                if (g.topLvlSceneIdx != MENU) {
                    sceneTable.get(MENU).vals[Menu.StateMap.LAST_SCENE.idx] = g.topLvlSceneIdx;
                }
                g.topLvlScene = new Menu();
                break;
            case S_ROOM1: // Room1
                g.topLvlScene = new S_Room1();
                break;
            case S_ROOM2: // Room2
                g.topLvlScene = new S_Room2();
                break;
            case S_WIN: // End of game
                g.topLvlScene = new S_Win();
                break;
            default:
                Log.error("Invalid sceneId " + sceneId);
                break;
        }

        /* Latch new topLvlSceneIdx */
        g.topLvlSceneIdx = sceneId;

        // Issue a dummy movement command to trigger mouse cursor update.
        actionHandler(g, BasicBackboneGame2D.MouseActions.MOVEMENT, lastEvtX, lastEvtY);

        // Draw the new screen.
        g.topLvlScene.setGameScreen(g.gs);
        g.topLvlScene.refresh();

        /* Update top level state. */
        sceneTable.get(TOP).vals[Top.StateMap.LAST_SCENE_ID.idx] = sceneId;

        g.player.setObstacle(g.topLvlScene.getObstacle());
        g.player.setScaleMap(g.topLvlScene.getScaleMap());
    }

    public void actionHandler(BasicBackboneGame2D g, BasicBackboneGame2D.MouseActions evtType, int evtX, int evtY) {
        int evtSceneX = gs.windowToSceneX(evtX);
        int evtSceneY = gs.windowToSceneY(evtY);

        Log.debug("SM evt " + evtType + " @ (" + evtX + "," + evtY + "), scene (" + evtSceneX + "," + evtSceneY + ")");

        lastEvtX = evtX;
        lastEvtY = evtY;

        // Move player to the location.
        // TODO: Add pathing and transition animations.
        // Also need to add mechanism for interacting with the destination object.
        g.player.actionHandler(g, evtType, evtSceneX, evtSceneY);

        GameScreen.CursorType cursorType = GameScreen.CursorType.DEFAULT;

        ArrayList<Scene> scenes = g.topLvlScene.getHitScenesR(evtSceneX, evtSceneY);
        for (int scnIdx = 0; scnIdx < scenes.size(); scnIdx++) {
            Scene s = scenes.get(scnIdx);

            ArrayList<Transition> transitions = s.getHitTransitions(evtSceneX, evtSceneY);
            for (int tIdx = 0; tIdx < transitions.size(); tIdx++) {
                Transition t = transitions.get(tIdx);

                if (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) {
                    t.activate();

                    /* Scene has switched, don't process any more in this scene. Do save, though. */
                    saveState();
                    return;

                } else if (evtType == BasicBackboneGame2D.MouseActions.MOVEMENT) {
                    // TODO: Move isSubscene to a method, i.e. isSubscene().
                    if (!s.isSubscene) {
                        cursorType = GameScreen.CursorType.TRANSITION;
                    }
                }
            }

            /*
             * Custom handling for this scene. This gives each scene a chance to custom
             * handle events before falling back on the default handling (other than
             * transitions, which are handled first).
             */
            if (s.uniqueActionHandler(g, evtType, evtSceneX, evtSceneY) != 0) {
                /* If return int is non-zero, further recursion/processing should be skipped. */
                break;
            }

            // If we enter this section, we're processing a subscene hit.
            // Don't override a transition.
            if ((cursorType != GameScreen.CursorType.TRANSITION) && s.isSubscene) {
                cursorType = GameScreen.CursorType.INSPECTION;
            }
        }

        // Note: Using SM's copy of GameScreen isn't just clean design, it's
        // functionally necessary. Scene's may not be fully set up up yet,
        // ex. if switchScene() hasn't initialized their GameScreen instance yet.
        gs.updateCursor(cursorType);

        /*
         * Save after handling any left clicks that hit something (a subscene), which
         * could have updated state. Transition saves are handled separately earlier.
         */
        if ((evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) && (scenes.size() > 1)) {
            g.sm.saveState();
        }

    }
}