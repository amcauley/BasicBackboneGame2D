package basicbackbonegame2d;

import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JFrame;

public class GameFrame extends JFrame {

    // Nominal size of scene.
    // This is really only used for setting the aspect ratio.
    public static final int NOMINAL_WIDTH = 16;
    public static final int NOMINAL_HEIGHT = 9;

    public GameScreen gs;

    // Store the actual, current width and height (including padding).
    // TODO: better naming / definitions. Maybe don't include padding.
    // TODO: maybe use getters and setters for these
    public static int width;
    public static int height;
    public static float scale;

    /* Horizontal padding. Amount of padding on the left of drawable image. */
    public static int xPad;
    /*
     * Vertical padding - padding from the top of the frame to drawable image area.
     */
    public static int yPad;

    /* Padding due to window border/header. */
    int framePadX;
    int framePadY;

    public void scaleComp() {
        /* Compute scaling from native size to current target size. */
        float scaleX = (float) width / NOMINAL_WIDTH;
        float scaleY = (float) height / NOMINAL_HEIGHT;

        scale = Math.min(scaleX, scaleY);

        xPad = (int) ((scaleX - scale) * NOMINAL_WIDTH * 0.5);
        yPad = (int) ((scaleY - scale) * NOMINAL_HEIGHT * 0.5);

        Log.trace("GameFrame scaling: " + width + "x" + height + ", scale " + scale + ", xPad " + xPad + ", yPad "
                + yPad);
    }

    public GameFrame() {

        width = 1000;
        height = 800;

        scaleComp();
    }

    public void init(GameScreen gameScreen) {
        setTitle("Default Title");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setResizable(true);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 1, 0, 0));

        /* Add the static screen to this JFrame-based object. */
        gs = gameScreen;
        add(gs);

        setVisible(true);

        /*
         * Now that frame exists, we can check what the extra border padding values are.
         * Possibly there's a better way to do this. Register resize listener after
         * this, since this is required info for proper resizing.
         */
        framePadX = width - getContentPane().getWidth();
        framePadY = height - getContentPane().getHeight();

        Log.debug("framePadX " + framePadX + ", framePadY " + framePadY);

        /* Listener for resizing */
        addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(ComponentEvent e) {
                Log.info("Frame hidden");
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                Log.debug("Frame moved");
            }

            @Override
            public void componentResized(ComponentEvent e) {
                width = getContentPane().getWidth();
                height = getContentPane().getHeight();
                scaleComp();

                Log.info("Frame resized: " + getContentPane().getWidth() + "x" + getContentPane().getHeight());

                gs.repaint();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                Log.info("Frame shown");
            }
        });
    }

}
