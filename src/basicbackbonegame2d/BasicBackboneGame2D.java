
package basicbackbonegame2d;

import static basicbackbonegame2d.Top.stateInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import basicbackbonegame2d.GameScreen.CameraType;

public class BasicBackboneGame2D implements ActionListener {

    public GameFrame gameFrame;

    /* Register jukebox for music control */
    public Jukebox jukebox;

    public Player player;

    public SceneManager sm;

    public GameScreen gs;

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

            // TODO: Queue up events and process it from the main tick-handling thread.

            if (SwingUtilities.isLeftMouseButton(me)) {
                // Let the scene manager handle movement / clicks.
                // It'll have to coordinate information between the player and scene, ex. for
                // pathing.
                sm.actionHandler(BasicBackboneGame2D.this, MouseActions.LEFT_BUTTON, me.getX(), me.getY());
            } else if (SwingUtilities.isRightMouseButton(me)) {

                /* Enter menu */
                SceneManager.switchScene(BasicBackboneGame2D.this, SceneManager.MENU);
            }

        }

        @Override
        public void mouseMoved(MouseEvent me) {
            // Convert to scene units. For mouse clicks, this is handled within
            // SceneManager.
            int mvtX = gs.windowToSceneX(me.getX());
            int mvtY = gs.windowToSceneY(me.getY());
            topLvlScene.actionHandler(BasicBackboneGame2D.this, MouseActions.MOVEMENT, mvtX, mvtY);
        }
    }

    public BasicBackboneGame2D() {
    }

    public void run() throws IOException {
        Log.info("Starting run()");
        Log.info("Launch directory: " + System.getProperty("user.dir"));

        gameFrame = new GameFrame();
        jukebox = new Jukebox();
        gs = new GameScreen();

        player = new Player();
        player.setGameScreen(gs);

        sm = new SceneManager();
        sm.setGameScreen(gs);

        gameFrame.init(gs);

        /* Register game object to scene. */
        // TODO: Get rid of this static access, it makes ownership too difficult to
        // manage.
        Scene.g = this;

        timer = new Timer(1000 / GameScreen.FRAMES_PER_SEC, this);
        gs.registerTimer(timer);

        // TODO: Fix access. Need to revisit Scene, Frame, etc. initialization.
        gs.cameraType = CameraType.GLOBAL;

        gameFrame.setVisible(true);

        /*
         * Add this after screen is added and setVisible, since scene creation calls
         * updateScreen(), which calls getLocationOnScreen() for screen, and it must
         * already be drawn on the screen or we hit a runtime error.
         */

        /* Load game state from file, and set topLvlScene to the stored scene. */
        sm.loadState();
        topLvlSceneIdx = stateInfo.vals[Top.StateMap.LAST_SCENE_ID.idx];

        SceneManager.switchScene(this, topLvlSceneIdx);

        /*
         * Mouse listener references topLvlScene, so this should come after topLvlScene
         * is initialized.
         */
        gameMouseListener mouseListener = new gameMouseListener();
        gs.registerMouseListener(mouseListener);
    }

    /*
     * Swing timer, runs in same context (event dispatch thread) as rest of code, so
     * no worries about race conditions.
     */
    public void actionPerformed(ActionEvent e) {
        Log.trace("Timer triggered");

        gs.fromTick = true;
        player.onTick();
        topLvlScene.updateScreen(true);
        topLvlScene.draw();
    }
}
