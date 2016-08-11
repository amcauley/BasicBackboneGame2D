
package basicbackbonegame2d;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class GameScreen extends JPanel {
    
    /* Inner class for storing image info, including location and the actual buffered
       image data. */
    private class imageContainer {
        private String imgPath;
        private BufferedImage img;
        private int x, y;
        
        imageContainer(){
            
        }
        
        imageContainer(String pathName, int xx, int yy){
            imgPath = pathName;
            x = xx;
            y = yy;
            
            try {
                File imgFile = new File(imgPath);
                img = ImageIO.read(imgFile);
            } catch (IOException e) {
                System.out.println("Error loading " + imgPath);
            }
        }
    }
    
    /* Array of all images that contribute to the current screen. */
    private List<imageContainer> images = new ArrayList<>();
    
    public GameScreen() {
        
    }
    
    public void addImg(String imgPath, int x, int y){
        images.add(new imageContainer(imgPath, x, y));
        System.out.println(images.size() + " images");
    }
    
    public void clearImgs(){
        images.clear();
    }
    
    @Override
    public void paint(Graphics g) {
        /* Draw all images in the current screen. */
        for (imageContainer ic : images) {
            g.drawImage(ic.img, ic.x, ic.y, null);
        }
    }
    
    /* Mouse listeners are registered to the screen so that all mouse events
       coordinates are relative the the drawable area (i.e. the GameScreen). */
    public void registerMouseListener(BasicBackboneGame2D.gameMouseListener ml){
        addMouseListener(ml);
        addMouseMotionListener(ml);
    }
}