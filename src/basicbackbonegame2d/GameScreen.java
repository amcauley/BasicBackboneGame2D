
package basicbackbonegame2d;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GameScreen extends JPanel {
    
    public enum CursorType{
        INVALID, DEFAULT, INSPECTION, TRANSITION
    }    
    
    /* currently used cursor type */
    private CursorType activeCursorType;
    
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
                System.out.println("Loading " + imgPath);
                img = ImageIO.read(getClass().getClassLoader().getResource(imgPath));
            } catch (IOException e) {
                System.out.println("Error loading " + imgPath + ":");
                System.out.println(e.getMessage());
            }
        }
    }
    
    /* Array of all images that contribute to the current screen. */
    private List<imageContainer> images = new ArrayList<>();
    
    public GameScreen() {
        activeCursorType = CursorType.INVALID; //init to invalid so we'll update at first chance
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));        
    }
    
    public void addImg(String imgPath, int x, int y){
        images.add(new imageContainer(imgPath, x, y));
        //System.out.println(images.size() + " images");
    }
    
    public void clearImgs(){
        images.clear();
    }
    
    @Override
    public void paint(Graphics g) { 
        
        //System.out.println("painting GameScreen");
        
        /* Draw background across entire drawable area. This will form the border between the
           window frame and the scene itself. */
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameFrame.width, GameFrame.height); 
        
        float scale = GameFrame.scale;
                
        /* Draw all images in the current screen. */
        for (int icIdx = 0; icIdx < images.size(); icIdx++) {
            imageContainer ic = images.get(icIdx);
            //g.drawImage(ic.img, ic.x, ic.y, null);
            
            int startX = (int)(ic.x*scale) + GameFrame.xPad;
            int startY = (int)(ic.y*scale) + GameFrame.yPad;
            int startWidth = ic.img.getWidth();
            int startHeight = ic.img.getHeight();
            
            g.drawImage(ic.img,
                        startX, startY,
                        startX + (int)(startWidth*scale), startY + (int)(startHeight*scale),
                        0, 0,
                        startWidth, startHeight,
                        null);
        }
    }
    
    /* Update mouse cursor if it doen't match the currently active cursor type. */
    public void updateCursor(CursorType cursorType){
        if (activeCursorType != cursorType){
            switch(cursorType){
                case DEFAULT: //Default cursor
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    break;
                case INSPECTION: //Inspection
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    break;
                case TRANSITION: //Transition
                    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    break;
                default:
                    System.out.println("Undefined cursorType " + cursorType);
                    break;
            }

            activeCursorType = cursorType;            
        }
    }
    
    /* Mouse listeners are registered to the screen so that all mouse events
       coordinates are relative the the drawable area (i.e. the GameScreen). */
    public void registerMouseListener(BasicBackboneGame2D.gameMouseListener ml){
        addMouseListener(ml);
        addMouseMotionListener(ml);
    }
}