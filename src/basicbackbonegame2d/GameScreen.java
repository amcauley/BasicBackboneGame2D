
package basicbackbonegame2d;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameScreen extends JPanel {

    public static final int FRAMES_PER_SEC = 5;

    private Timer timer;

    private boolean fromTick = false;

    public enum CursorType {
        INVALID, DEFAULT, INSPECTION, TRANSITION
    }

    /* currently used cursor type */
    private CursorType activeCursorType;

    // The viewport is the actua lwindow into the game scene.
    // Its dimensions are given in scene units.
    private double viewportWidth;
    private double viewportHeight;
    private double viewportCenterX;
    private double viewportCenterY;

    public static BufferedImage scaleBufferedImage(BufferedImage input, double scale) {
        int widthOut = (int) (input.getWidth() * scale);
        int heightOut = (int) (input.getHeight() * scale);

        BufferedImage output = new BufferedImage(widthOut, heightOut, input.getType());

        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        scaleOp.filter(input, output);
        return output;
    }

    /* Array of all images that contribute to the current screen. */
    private List<ImageContainer> images = new ArrayList<>();

    /*
     * For next list of images/animations to be drawn. New entries will get latched
     * into images, i.e. actively displayed images, after calling
     * submitNewDrawList()
     */
    private List<ImageContainer> newImages = new ArrayList<>();

    /*
     * Refresh images, i.e. existing images that have been re-added to the draw
     * list.
     */
    private List<ImageContainer> refreshImages = new ArrayList<>();

    public GameScreen() {
        activeCursorType = CursorType.INVALID; // init to invalid so we'll update at first chance
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        viewportWidth = 0;
        viewportHeight = 0;
        viewportCenterX = 0;
        viewportCenterY = 0;
    }

    public CursorType getActiveCursorType() {
        return activeCursorType;
    }

    public boolean getFromTick() {
        return fromTick;
    }

    public void setFromTick(boolean t) {
        fromTick = t;
    }

    // Center location (in scene units) of the viewport / camera.
    public double getViewportCenterX() {
        return viewportCenterX;
    }

    public double getViewportCenterY() {
        return viewportCenterY;
    }

    // Scene-based dimensions of the viewport / camera, i.e. how much of the scene
    // is visible.
    public void setViewportWidth(double w) {
        viewportWidth = w;
    }

    public void setViewportHeight(double h) {
        viewportHeight = h;
    }

    public double getViewportWidth() {
        return viewportWidth;
    }

    public double getViewportHeight() {
        return viewportHeight;
    }

    public double viewportToWindowScaleX() {
        return (GameFrame.width - GameFrame.xPad * 2.0) / getViewportWidth();
    }

    public double viewportToWindowScaleY() {
        return (GameFrame.height - GameFrame.yPad * 2.0) / getViewportHeight();
    }

    void setViewport(int centerX, int centerY, int width, int height) {
        Log.trace("viewport center (" + centerX + "," + centerY + "), dim (" + width + "," + height + ")");
        viewportCenterX = centerX;
        viewportCenterY = centerY;
        viewportWidth = width;
        viewportHeight = height;
    }

    public int windowToSceneX(int x) {
        double windowCenter = GameFrame.width / 2.0;
        return (int) (getViewportCenterX() + (x - windowCenter) / viewportToWindowScaleX());
    }

    public int windowToSceneY(int y) {
        double windowCenter = GameFrame.height / 2.0;
        return (int) (getViewportCenterY() + (y - windowCenter) / viewportToWindowScaleY());
    }

    public void addImgToDrawList(String imgPath, int x, int y, int depth, String id) {
        // TODO: Unify with addAnimationToDrawList()
        /* Check if the image is already loaded, ex. from the previous frame. */
        for (Iterator<ImageContainer> oldIcIt = images.iterator(); oldIcIt.hasNext();) {
            ImageContainer oldIc = oldIcIt.next();
            if (oldIc.equals(x, y, depth, imgPath, id)) {
                for (Iterator<ImageContainer> refreshIcIt = refreshImages.iterator(); refreshIcIt.hasNext();) {
                    ImageContainer refreshIc = refreshIcIt.next();
                    if (oldIc.equals(refreshIc)) {
                        // Already tracked
                        return;
                    }
                }
                refreshImages.add(oldIc);
                return;
            }
        }

        newImages.add(new ImageContainer(imgPath, x, y, depth, id));
    }

    public void addAnimationToDrawList(String imgPath, int x, int y, int frameSizeX, int frameSizeY,
            Scene.AnimationType aType, int depth, double scale, String id) {

        // TODO: try reusing animation object if it's the same one, only in a different
        // location.
        for (Iterator<ImageContainer> oldIcIt = images.iterator(); oldIcIt.hasNext();) {
            ImageContainer oldIc = oldIcIt.next();
            if (oldIc.equals(x, y, depth, imgPath, id)) { // TODO: Consider full check (animation type, size, etc.)
                for (Iterator<ImageContainer> refreshIcIt = refreshImages.iterator(); refreshIcIt.hasNext();) {
                    ImageContainer refreshIc = refreshIcIt.next();
                    // If already tracked, no need to update anything.
                    if (oldIc.equals(refreshIc)) {
                        return;
                    }
                }
                refreshImages.add(oldIc);
                return;
            }
        }

        newImages.add(new Animation(imgPath, x, y, frameSizeX, frameSizeY, aType, depth, scale, id));
    }

    /* Clear list of new/pending images/animations. */
    public void clearNewDrawList() {
        newImages.clear();
        refreshImages.clear();
    }

    /*
     * Latch any new images/animations into drawing list (images). Remove any old
     * ones. Note that if an animation is in both new and currently active list, it
     * will keep running.
     */
    public void submitNewDrawList() {
        /* Remove any stale items from images. */
        for (Iterator<ImageContainer> oldIcIt = images.iterator(); oldIcIt.hasNext();) {

            ImageContainer oldIc = oldIcIt.next();

            boolean foundMatch = false;
            // If the entry isn't getting refreshed, it's stale.
            for (Iterator<ImageContainer> refreshIcIt = refreshImages.iterator(); refreshIcIt.hasNext();) {
                ImageContainer refreshIc = refreshIcIt.next();

                if (refreshIc.equals(oldIc)) {

                    foundMatch = true;
                    break;
                }
            }

            if (!foundMatch) {
                oldIcIt.remove();
            }
        }

        // Everything in newImages is new and should be added to images.
        images.addAll(newImages);

        /*
         * Finally, sort the new images list in order of ascending depth value (maybe
         * "depth" can be renamed).
         */
        Collections.sort(images, new Comparator<ImageContainer>() {
            public int compare(ImageContainer c1, ImageContainer c2) {
                if (c1.depth == c2.depth) {
                    return 0;
                } else {
                    return c1.depth < c2.depth ? -1 : 1;
                }
            }
        });

    }

    @Override
    public void paint(Graphics g) {

        Log.trace("Painting GameScreen, fromTick " + fromTick);

        /* Draw all images (and animations) in the current screen. */
        boolean needNextTimerTick = false;

        /*
         * Process images/animations in order (from background to foreground, already
         * sorted).
         */
        for (int icIdx = 0; icIdx < images.size(); icIdx++) {
            ImageContainer ic = images.get(icIdx);

            /* Update animations (no-op for static images). */
            ic.update(fromTick);

            int srcX = ic.x;
            int srcY = ic.y;
            int srcWidth = ic.width;
            int srcHeight = ic.height;

            double cameraToScreenScaleX = viewportToWindowScaleX();
            double cameraToScreenScaleY = viewportToWindowScaleY();

            int destX = (int) (GameFrame.width / 2.0
                    + (srcX - getViewportCenterX()) * cameraToScreenScaleX);
            int destWidth = (int) (srcWidth * cameraToScreenScaleX);

            int destY = (int) (GameFrame.height / 2.0
                    + (srcY - getViewportCenterY()) * cameraToScreenScaleY);
            int destHeight = (int) (srcHeight * cameraToScreenScaleY);

            Log.trace("Preparing to paint " + ic.imgPath + ", x " + srcX + ", y " + srcY + ", w " + srcWidth + ", h "
                    + srcHeight + ", destX " + destX + ", destY " + destY + ", destW " + destWidth + ", destH "
                    + destHeight + ", padX " + GameFrame.xPad + ", padY " + GameFrame.yPad + ", icScale "
                    + ic.getScale());

            // If the image is scaled to nothing, there's nothing to draw.
            // Attempting to do so can lead to an exception:
            // "AWT-EventQueue-0" java.lang.IllegalArgumentException: w and h must be > 0
            if ((srcWidth == 0) || (srcHeight == 0) || ic.getScale() == 0) {
                continue;
            }

            // TODO: Need -1 for end points?
            g.drawImage(ic.getImg(), destX, destY, destX + destWidth, destY + destHeight, 0, 0, srcWidth, srcHeight,
                    null);

            if (ic.isActive()) {
                needNextTimerTick = true;
            }
        }

        // Draw horizontal and vertical padding, if any.
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameFrame.xPad, GameFrame.height);
        g.fillRect(GameFrame.width - GameFrame.xPad, 0, GameFrame.xPad, GameFrame.height);
        g.fillRect(0, 0, GameFrame.width, GameFrame.yPad);
        g.fillRect(0, GameFrame.height - GameFrame.yPad, GameFrame.width, GameFrame.yPad);

        /* Update timer as needed for animations */
        if (timer != null) { /* Make sure timer actually exists. */
            if (timer.isRunning()) {
                if (needNextTimerTick) {
                    Log.trace("Timer running");
                } else {
                    Log.debug("Timer stopping");
                    timer.stop();
                }
            } else {
                if (needNextTimerTick) {
                    Log.debug("Timer restart");
                    timer.restart();
                } else {
                    Log.trace("Timer stopped");
                }
            }
        }

        fromTick = false;
    }

    public void registerTimer(Timer t) {
        timer = t;
    }

    /* Update mouse cursor if it doen't match the currently active cursor type. */
    public void updateCursor(CursorType cursorType) {
        // TODO: get these from checking the image dimensions instead of hardcoding
        // them.
        int PREFERRED_CURSOR_WIDTH = 15;
        int PREFERRED_CURSOR_HEIGHT = 15;

        // Windows seems to be scaling the cursor up to its preferred dimensions
        // and the hotspot offset seems to use these new dimensions.
        // TODO: Don't just hardcode these.
        int hotSpotX = 7 * 32 / PREFERRED_CURSOR_WIDTH;
        int hotSpotY = 7 * 32 / PREFERRED_CURSOR_HEIGHT;

        if (activeCursorType != cursorType) {
            switch (cursorType) {
                case DEFAULT: // Default cursor
                    // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                            new ImageIcon(
                                    getClass().getClassLoader().getResource("resources/images/Cursor_Default.png"))
                                            .getImage(),
                            new Point(hotSpotX, hotSpotY), "defaultCursor"));
                    break;
                case INSPECTION: // Inspection
                    // setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                            new ImageIcon(
                                    getClass().getClassLoader().getResource("resources/images/Cursor_Inspect.png"))
                                            .getImage(),
                            new Point(hotSpotX, hotSpotY), "inspectionCursor"));
                    break;
                case TRANSITION: // Transition
                    // setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                            new ImageIcon(
                                    getClass().getClassLoader().getResource("resources/images/Cursor_Transition.png"))
                                            .getImage(),
                            new Point(hotSpotX, hotSpotY), "transitionCursor"));
                    break;
                default:
                    Log.warning("Undefined cursorType " + cursorType);
                    break;
            }

            activeCursorType = cursorType;
        }
    }

    /*
     * Mouse listeners are registered to the screen so that all mouse events
     * coordinates are relative the the drawable area (i.e. the GameScreen).
     */
    public void registerMouseListener(BasicBackboneGame2D.gameMouseListener ml) {
        addMouseListener(ml);
        addMouseMotionListener(ml);
    }
}