
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

            if (sm == null) {
                Log.warning("Skipping mouse handling - SM not initialized");
                return;
            }

            // TODO: Queue up events and process it from the main tick-handling thread.

            if (SwingUtilities.isLeftMouseButton(me)) {
                // Let the scene manager handle movement / clicks.
                // It'll have to coordinate information between the player and scene, ex. for
                // pathing.
                sm.actionHandler(BasicBackboneGame2D.this, MouseActions.LEFT_BUTTON, me.getX(), me.getY());
            } else if (SwingUtilities.isRightMouseButton(me)) {
                // TODO: Rework the logic so we don't need 2 calls to switchScene().
                //
                // - 1st switch() is so coordinate translation will load menu
                // and use its coordinates.
                //
                // - actionHandler() will then use menu-translated coordinates for
                // its mouse position update.
                //
                // - 2nd switch() will update the cursor now that location is updated.
                //
                // Testing note:
                // 1) Enter menu while cursor is where the Resume button will be.
                // 2) Resume.
                // 3) Right click between where Resume and Save were.
                // 4) Move mouse to where top of Save was.
                // 5) Enter menu.
                //
                // Without the double switch(), the cursor wasn't blue for inspection.
                // With the fix, it's properly blue, since it now knows it's over a button.
                // Without double switch(), the character movement messes with coord checks.
                // Repros with PLAYER_CONSTRAINED camera, not GLOBAL camera.
                sm.switchScene(BasicBackboneGame2D.this, SceneManager.MENU);
                sm.actionHandler(BasicBackboneGame2D.this, MouseActions.RIGHT_BUTTON, me.getX(), me.getY());
                sm.switchScene(BasicBackboneGame2D.this, SceneManager.MENU);
            }

        }

        @Override
        public void mouseMoved(MouseEvent me) {
            sm.actionHandler(BasicBackboneGame2D.this, MouseActions.MOVEMENT, me.getX(), me.getY());
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

        gameFrame.setVisible(true);

        /*
         * Add this after screen is added and setVisible, since scene creation calls
         * updateScreen(), which calls getLocationOnScreen() for screen, and it must
         * already be drawn on the screen or we hit a runtime error.
         */

        /* Load game state from file, and set topLvlScene to the stored scene. */
        sm.loadState();
        topLvlSceneIdx = stateInfo.vals[Top.StateMap.LAST_SCENE_ID.idx];

        sm.switchScene(this, topLvlSceneIdx);

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

        gs.setFromTick(true);
        player.onTick();
        topLvlScene.updateScreen(true);
        topLvlScene.draw();
    }
}
