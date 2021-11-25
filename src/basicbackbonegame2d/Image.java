package basicbackbonegame2d;

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
}
