package basicbackbonegame2d;

import java.lang.Math;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Image {
    String imgPath;
    BufferedImage img;
    int width, height;

    Image(String pathName) {

        imgPath = pathName;

        try {
            System.out.println("Loading image:" + imgPath);
            img = ImageIO.read(getClass().getClassLoader().getResource(imgPath));
            width = img.getWidth();
            height = img.getHeight();
        } catch (IOException e) {
            System.out.println("Error loading " + imgPath + ":");
            System.out.println(e.getMessage());
        }
    }

    // Get the value at normalized coordinates in the range [0, 1].
    int getARGB(double x, double y) {
        int argb = img.getRGB(
                Math.max(0, Math.min((int) (x * (width - 1)), width - 1)),
                Math.max(0, Math.min((int) (y * (height - 1)), height - 1)));

        // System.out.println("Check " + imgPath + " @ (" + x + ", " + y + "): " +
        // String.format("0x%08X", argb));

        return argb;
    }
}
