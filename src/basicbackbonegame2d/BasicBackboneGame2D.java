
package basicbackbonegame2d;

import static basicbackbonegame2d.Top.stateInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

public class BasicBackboneGame2D implements ActionListener {

    public GameFrame gameFrame;

    /* Register jukebox for music control */
    public Jukebox jukebox;

    public Player player;

    public SceneManager sm;

    /*
     * Swing timer (not just regular util timer) for all our timing needs. Swing
     * timer operates from event dispatch thread, so same context as other event
     * handling, therefore no race conditions.
     */
    public Timer timer;

    /* Top level scene */
    public Scene topLvlScene;

    /* Index of top lvl scene. Update when we update topLvlScene itself. */
    public int topLvlSceneIdx;

    public enum MouseActions {
        LEFT_BUTTON, RIGHT_BUTTON, MOVEMENT
    }

    /*
     * Handle mouse events. Needs access to topLvlScene for passing on events to the
     * scene.
     */
    public class gameMouseListener extends MouseInputAdapter {

        public gameMouseListener() {
            Log.info("Instantiating gameMouseListener");
        }

        @Override
        public void mousePressed(MouseEvent me) {

            Log.trace("Mouse press: " + me.toString());

            if (SwingUtilities.isLeftMouseButton(me)) {
                // Let the scene manager handle movement / clicks.
                // It'll have to coordinate information between the player and scene, ex. for
                // pathing.
                sm.actionHandler(BasicBackboneGame2D.this, MouseActions.LEFT_BUTTON, me.getX(), me.getY());
            } else if (SwingUtilities.isRightMouseButton(me)) {

                /* Enter menu */
                SceneManager.switchScene(BasicBackboneGame2D.this, SceneManager.SceneList.MENU);
            }

        }

        @Override
        public void mouseMoved(MouseEvent me) {
            topLvlScene.actionHandler(BasicBackboneGame2D.this, MouseActions.MOVEMENT, me.getX(), me.getY());
        }
    }

    public BasicBackboneGame2D() {
    }

    public void run() throws IOException {
        Log.info("Starting run()");

        gameFrame = new GameFrame();
        jukebox = new Jukebox();
        player = new Player();
        sm = new SceneManager();

        gameFrame.init();

        /* Register game object to scene. */
        Scene.g = this;

        timer = new Timer(1000 / GameScreen.FRAMES_PER_SEC, this);
        Scene.screen.registerTimer(timer);

        /* Add the static screen to this JFrame-based object. */
        gameFrame.add(Scene.screen);
        gameFrame.setVisible(true);

        /*
         * Add this after screen is added and setVisible, since scene creation calls
         * updateScreen(), which calls getLocationOnScreen() for screen, and it must
         * already be drawn on the screen or we hit a runtime error.
         */

        /* Load game state from file, and set topLvlScene to the stored scene. */
        // sm.loadState();
        topLvlSceneIdx = stateInfo.vals[Top.StateMap.LAST_SCENE_ID.idx];

        /*
         * Overwrite top level scene for now until AutoSave file handling for JAR
         * distribution is figured out. Just load to room 0.
         */
        SceneManager.switchScene(this, SceneManager.SceneList.S_ROOM1);

        SceneManager.switchScene(this, SceneManager.SceneList.values()[topLvlSceneIdx]);

        /*
         * Mouse listener references topLvlScene, so this should come after topLvlScene
         * is initialized.
         */
        gameMouseListener mouseListener = new gameMouseListener();
        Scene.screen.registerMouseListener(mouseListener);
    }

    /*
     * Swing timer, runs in same context (event dispatch thread) as rest of code, so
     * no worries about race conditions.
     */
    public void actionPerformed(ActionEvent e) {
        Log.trace("Timer triggered");

        Scene.screen.fromTick = true;
        player.onTick();
        topLvlScene.updateScreen(true);
        topLvlScene.draw();
    }
}
