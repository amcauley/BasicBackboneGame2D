package basicbackbonegame2d;

public class ScaleMap {
    Image image;

    ScaleMap() {
    }

    public ScaleMap(String imagePath) {
        image = new Image(imagePath);
    }

    // Get the scaling value at normalized coordinate (x, y).
    // The scaling value is given by the B channel value [0, 255], which is then
    // normalized to [0.0, 1.0].
    double getScalingFactor(double x, double y) {
        int argb = image.getARGB(x, y);
        return (argb & 0x000000FF) / 255.0;
    }
}
