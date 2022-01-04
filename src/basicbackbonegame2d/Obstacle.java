package basicbackbonegame2d;

public class Obstacle {
    Image image;

    // Scene dimensions this obstacle covers.
    // This can differ from the underlying image, ex. if the obstacle map is low
    // res compared to the actual scene.
    int width;
    int height;

    public Obstacle(String imagePath, int width_, int height_) {
        image = new Image(imagePath);
        width = width_;
        height = height_;
    }

    // Checks if the input coordinates (normalized to the range [0, 1.0])
    // is free of any obstructions.
    boolean isClearN(double x, double y) {
        int argb = image.getARGB(x, y);
        return (argb & 0x00FFFFFF) != 0;
    }

    boolean isClear(int x, int y) {
        return isClearN(x * 1.0 / width, y * 1.0 / height);
    }
}
