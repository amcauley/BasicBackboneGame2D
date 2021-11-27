package basicbackbonegame2d;

public class Obstacle {
    Image image;

    Obstacle() {
    }

    public Obstacle(String imagePath) {
        image = new Image(imagePath);
    }

    // Checks if the input coordinates (normalized to the range [0, 1.0])
    // is free of any obstructions.
    boolean isClear(double x, double y) {
        int argb = image.getARGB(x, y);
        return (argb & 0x00FFFFFF) != 0;
    }
}
