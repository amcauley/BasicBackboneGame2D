package basicbackbonegame2d;

import java.awt.image.BufferedImage;

/*
 * Inner class for storing image info, including location and the actual
 * buffered image data.
 */
public class ImageContainer extends Image {
    // Coordinates are given in scene units.
    int x, y;

    double scale;
    int depth; // Used for determining what is drawn on top of
    String id; // unique id for this image, cused to check if this is already in images list

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
        boolean eq = (x == other.x) && (y == other.y) && (depth == other.depth) && imgPath.equals(other.imgPath)
                && id.equals(other.id);
        Log.trace("Comparing " + str() + " vs. " + other.str() + ", match: " + eq);
        return eq;
    }

    boolean equals(int xx, int yy, int dpth, String imagePath, String idStr) {
        return (x == xx) && (y == yy) && (depth == dpth) && imgPath.equals(imagePath) && id.equals(idStr);
    }

    /* Not needed for basic image, but Animation will want this. */
    void update(boolean fromTick) {

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
