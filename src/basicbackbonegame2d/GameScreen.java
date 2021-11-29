
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

    Timer timer;

    public boolean fromTick = false;

    public static final int FRAMES_PER_SEC = 5;

    public enum CursorType {
        INVALID, DEFAULT, INSPECTION, TRANSITION
    }

    /* currently used cursor type */
    private CursorType activeCursorType;

    public static BufferedImage scaleBufferedImage(BufferedImage input, double scale) {
        int widthOut = (int) (input.getWidth() * scale);
        int heightOut = (int) (input.getHeight() * scale);

        BufferedImage output = new BufferedImage(widthOut, heightOut, input.getType());

        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        scaleOp.filter(input, output);
        return output;
    }

    /*
     * Inner class for storing image info, including location and the actual
     * buffered image data.
     */
    private class ImageContainer extends Image {
        // String imgPath;
        // BufferedImage img;
        int x, y;
        // int width, height;
        double scale;
        int depth; // What the depth of this image is, used for determining what is drawn on top of
                   // what else
        String id; // unique identifier for this image, can be used to check if this is already in
                   // images list

        ImageContainer(String pathName, int xx, int yy, int dpth, String idStr) {
            super(pathName);

            x = xx;
            y = yy;
            depth = dpth;
            id = idStr;
            scale = 1.0;
        }

        String str() {
            return id + ", " + imgPath + ", @ (" + x + ", " + y + ", " + depth + ")";
        }

        boolean equals(ImageContainer other) {
            // System.out.println("Compare: " + str() + " vs. " + other.str());
            return (x == other.x) && (y == other.y) && (depth == other.depth) && imgPath.equals(other.imgPath)
                    && id.equals(other.id);
        }

        boolean equals(int xx, int yy, int dpth, String imagePath, String idStr) {
            return (x == xx) && (y == yy) && (depth == dpth) && imgPath.equals(imagePath) && id.equals(idStr);
        }

        /* Not needed for basic image, but Animation will want this. */
        void update() {

        }

        BufferedImage getImg() {
            return img;
        }

        /*
         * isAvtive will be used to determine if we need to schedule the timer for
         * processing any Animations. Regular images are never "active" since they're
         * static pictures.
         */
        boolean isActive() {
            return false;
        }

        void setScale(double s) {
            scale = s;
        }

        double getScale() {
            return scale;
        }

        boolean isAnimated() {
            return false;
        }

        int getXOffset() {
            return 0;
        }

        int getYOffset() {
            return 0;
        }
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

    /*
     * Class to contain an animation. Animations are stored as large images - if
     * each frame is w x h pixels (width by height), then the animation image is
     * size (m x w) x (n x h), where m x n = <number of frames>. Upon creation,
     * we'll read the image into memory, and for every frame we'll draw a small
     * region of the image.
     */
    private class Animation extends ImageContainer {

        private int numXFrames;
        private int numYFrames;
        private int xFrameSize;
        private int yFrameSize;

        private int numFrames;
        private int curFrame;
        private Scene.AnimationType animationType;

        /*
         * Current state of animation: 0 = inactive/finished, 1 = running. May want to
         * further distinguish been not started / paused / finished in the future.
         */
        private boolean active;

        /*
         * Constructor needs to know how the large each frame is. We'll compute the
         * width of the number of frames by dividing the total image by the size of a
         * frame and with the help of getWidth() or getHeight().
         */
        Animation(String pathName, int xx, int yy, int frameSizeX, int frameSizeY, Scene.AnimationType aType, int depth,
                double s, String id) {

            super(pathName, xx, yy, depth, id);

            xFrameSize = frameSizeX;
            yFrameSize = frameSizeY;

            width = xFrameSize;
            height = yFrameSize;

            numXFrames = img.getWidth() / xFrameSize;
            numYFrames = img.getHeight() / yFrameSize;
            numFrames = numXFrames * numYFrames;

            curFrame = 0;
            animationType = aType;

            scale = s;

            active = true;

            // System.out.println("Animation created");
        }

        /* Draw the current sceen and update info for the next frame to be drawn. */
        @Override
        void update() {
            if (!fromTick) {
                return; // only update state if due to tick
            }
            if (active) {
                curFrame = (curFrame + 1) % numFrames;
                if ((curFrame == 0) && (animationType == Scene.AnimationType.ANIMATED_NO_LOOP)) {
                    active = false;
                }
            }
        }

        /*
         * Get the current frame and return it (it's a subimage of the overall image).
         * All of these calculations are handled using the nominal screen size - the
         * paint method that called this routine will scale the returned (sub)image as
         * needed.
         */
        @Override
        BufferedImage getImg() {
            int yIdx = curFrame / numXFrames;
            int xIdx = curFrame - yIdx * numXFrames;

            // System.out.println("Animation frame " + (curFrame+1) + "/ " + numFrames + ",
            // x " + xIdx + ", y " + yIdx);

            return scaleBufferedImage(img.getSubimage(xIdx * xFrameSize, yIdx * yFrameSize, xFrameSize, yFrameSize),
                    scale);
        }

        /*
         * If animation is in progress, keep reporting active so that timer keeps
         * firing.
         */
        @Override
        boolean isActive() {
            return active;
        }

        @Override
        boolean isAnimated() {
            return true;
        }

        @Override
        int getXOffset() {
            int yIdx = curFrame / numXFrames;
            int xIdx = curFrame - yIdx * numXFrames;

            return xIdx * xFrameSize;
        }

        @Override
        int getYOffset() {
            int yIdx = curFrame / numXFrames;

            return yIdx * yFrameSize;
        }

    }

    public GameScreen() {
        activeCursorType = CursorType.INVALID; // init to invalid so we'll update at first chance
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
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

        // System.out.println(images.size() + " images");
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

        /*
         * System.out.println("submitNewDrawList images:"); for(ImageContainer i :
         * images){ System.out.println("   " + i.id); }
         * System.out.println("submitNewDrawList newImages:"); for(ImageContainer i :
         * newImages){ System.out.println("   " + i.id); } //
         */

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

        // System.out.println("painting GameScreen, fromTick " + fromTick);

        /*
         * Draw background across entire drawable area. This will form the border
         * between the window frame and the scene itself.
         */
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameFrame.width, GameFrame.height);

        float scale = GameFrame.scale;

        /* Draw all images (and animations) in the current screen. */
        boolean needNextTimerTick = false;
        /*
         * Process images/animations in order (from background to foreground, already
         * sorted).
         */
        for (int icIdx = 0; icIdx < images.size(); icIdx++) {
            ImageContainer ic = images.get(icIdx);

            int startX = (int) (ic.x * scale) + GameFrame.xPad;
            int startY = (int) (ic.y * scale) + GameFrame.yPad;
            int startWidth = (int) (ic.width * ic.getScale());
            int startHeight = (int) (ic.height * ic.getScale());

            // System.out.println(ic.imgPath);
            // System.out.println("x " + startX + ", y " + startY + ", w " + startWidth + ",
            // h " + startHeight);

            /* Update animations (no-op for static images). */
            ic.update();

            g.drawImage(ic.getImg(), startX, startY, startX + (int) (startWidth * scale),
                    startY + (int) (startHeight * scale), 0, 0, startWidth, startHeight, null);

            if (ic.isActive()) {
                needNextTimerTick = true;
            }
        }

        /* Update timer as needed for animations */
        if (timer != null) { /* Make sure timer actually exists. */
            if (timer.isRunning()) {
                if (needNextTimerTick) {
                    // System.out.println("Timer running");
                } else {
                    // System.out.println("Timer stopping");
                    timer.stop();
                }
            } else {
                if (needNextTimerTick) {
                    // System.out.println("Timer restart");
                    timer.restart();
                } else {
                    // System.out.println("Timer stopped");
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
                    System.out.println("Undefined cursorType " + cursorType);
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