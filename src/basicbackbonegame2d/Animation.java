package basicbackbonegame2d;

import java.awt.image.BufferedImage;

/*
 * Class to contain an animation. Animations are stored as large images - if
 * each frame is w x h pixels (width by height), then the animation image is
 * size (m x w) x (n x h), where m x n = <number of frames>. Upon creation,
 * we'll read the image into memory, and for every frame we'll draw a small
 * region of the image.
 */
public class Animation extends ImageContainer {

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

        Log.trace("Created new animation: " + str());
    }

    /* Draw the current sceen and update info for the next frame to be drawn. */
    @Override
    void update(boolean fromTick) {
        if (!fromTick) {
            return; // only update state if due to periodic tick
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

        Log.trace("Animation frame " + (curFrame + 1) + "/" + numFrames +
                ", x " + xIdx + ", y " + yIdx);

        return GameScreen.scaleBufferedImage(
                img.getSubimage(xIdx * xFrameSize, yIdx * yFrameSize, xFrameSize, yFrameSize),
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
