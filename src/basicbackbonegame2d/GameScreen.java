
package basicbackbonegame2d;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameScreen extends JPanel {
    
    Timer timer;
    
    public boolean fromTick = false;
    
    public static final int FRAMES_PER_SEC = 2;
    
    public enum CursorType{
        INVALID, DEFAULT, INSPECTION, TRANSITION
    }    
    
    /* currently used cursor type */
    private CursorType activeCursorType;
    
    /* Inner class for storing image info, including location and the actual buffered
       image data. */
    private class ImageContainer {
        String imgPath;
        BufferedImage img;
        int x, y;
        int width, height;
        
        ImageContainer(){
            
        }
        
        ImageContainer(String pathName, int xx, int yy){
            imgPath = pathName;
            x = xx;
            y = yy;
            
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
        
        /* Not needed for basic image, but Animation will want this. */
        void update(){
            
        }
        
        BufferedImage getImg(){
            return img;
        }
     
        /* isAvtive will be used to determine if we need to schedule the timer for processing
           any Animations. Regular images are never "active" since they're static pictures. */
        boolean isActive(){
            return false;
        }        
        
        boolean isAnimated(){
            return false;
        }
        
        int getXOffset(){
            return 0;
        }
        
        int getYOffset(){
            return 0;
        }      
    }
    
    /* Array of all images that contribute to the current screen. */
    private List<ImageContainer> images = new ArrayList<>();
    
    /* Class to contain an animation. Animations are stored as large images - if each
       frame is w x h pixels (width by height), then the animation image is size
       (m x w) x (n x h), where m x n = <number of frames>. Upon creation, we'll read the
       image into memory, and for every frame we'll draw a small region of the image. */
    private class Animation extends ImageContainer{
        
        private int numXFrames;
        private int numYFrames;
        private int xFrameSize;
        private int yFrameSize;
        
        private int numFrames;
        private int curFrame;
        private Scene.AnimationType animationType;
        
        /* Current state of animation: 0 = inactive/finished, 1 = running. May want to further
           distinguish been not started / paused / finished in the future. */
        private int active;
        
        
        /* Constructor needs to know how the large each frame is. We'll compute the width of the
           number of frames by dividing the total image by the size of a frame and with the help 
           of getWidth() or getHeight(). */
        Animation(String pathName, int xx, int yy, int frameSizeX, int frameSizeY, Scene.AnimationType aType){
            
            super(pathName, xx, yy);
            
            xFrameSize = frameSizeX;
            yFrameSize = frameSizeY;
            
            width = xFrameSize;
            height = yFrameSize;
            
            numXFrames = img.getWidth()/xFrameSize;
            numYFrames = img.getHeight()/yFrameSize;
            numFrames = numXFrames * numYFrames;            
            
            curFrame = 0;
            animationType = aType;
            
            /* Start running by default. */
            active = 1;
            
            System.out.println("Animation created");
        }
        
        /* Draw the current sceen and update info for the next frame to be drawn. */
        @Override
        void update(){
            if(!fromTick){
                return; //only update state if due to tick
            }
            if (active != 0){
                curFrame = (curFrame+1)%numFrames;
                if ((curFrame == 0) && (animationType == Scene.AnimationType.ANIMATED_NO_LOOP)){
                    active = 0;
                }
            }
        }
        
        /* Get the current frame and return it (it's a subimage of the overall image). All of these
           calculations are handled using the nominal screen size - the paint method that called this
           routine will scale the returned (sub)image as needed. */
        @Override
        BufferedImage getImg(){
            int yIdx = curFrame/numXFrames;
            int xIdx = curFrame - yIdx*numXFrames;
            
            System.out.println("Animation frame " + curFrame + "/ " + numFrames + ", x " + xIdx + ", y " + yIdx);
            
            return img.getSubimage( xIdx*xFrameSize, 
                                    yIdx*yFrameSize,
                                    xFrameSize,
                                    yFrameSize);
        }
        
        /* If animation is in progress, keep reporting active so that timer keeps firing. */
        @Override
        boolean isActive(){
            return active != 0;
        }
        
        @Override
        boolean isAnimated(){
            return true;
        }

        @Override
        int getXOffset(){
            int yIdx = curFrame/numXFrames;
            int xIdx = curFrame - yIdx*numXFrames;
            
            return xIdx * xFrameSize;
        }
        
        @Override
        int getYOffset(){
            int yIdx = curFrame/numXFrames;

            return yIdx * yFrameSize;
        }        
        
    }
    
    public GameScreen() {
        activeCursorType = CursorType.INVALID; //init to invalid so we'll update at first chance
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));        
    }
    
    public void addImg(String imgPath, int x, int y){
        images.add(new ImageContainer(imgPath, x, y));
        //System.out.println(images.size() + " images");
    }
    
    public void addAnimation(String imgPath, int x, int y, int frameSizeX, int frameSizeY, Scene.AnimationType aType){
        images.add(new Animation(imgPath, x, y, frameSizeX, frameSizeY, aType));
    }
    
    /* Clear all images, including active animations. */
    public void clearImgs(){
        images.clear();
    }
    
    /* Only clear non-animated images, so animations won't lose state. */
    public void clearStillImgs(){
        for (Iterator<ImageContainer> it = images.iterator(); it.hasNext();) {
            ImageContainer ic = it.next();
            if (!ic.isAnimated()) {
                it.remove();
            }
        }        
    }
    
    @Override
    public void paint(Graphics g) { 
        
        System.out.println("painting GameScreen, fromTick " + fromTick);
        
        /* Draw background across entire drawable area. This will form the border between the
           window frame and the scene itself. */
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameFrame.width, GameFrame.height); 
        
        float scale = GameFrame.scale;
                
        /* Draw all images (and animations) in the current screen. */
        boolean needNextTimerTick = false;
        for (int icIdx = 0; icIdx < images.size(); icIdx++) {
            ImageContainer ic = images.get(icIdx);
            
            if (ic.isAnimated()){
                continue;
            }
            
            int startX = (int)(ic.x*scale) + GameFrame.xPad;
            int startY = (int)(ic.y*scale) + GameFrame.yPad;
            int startWidth = ic.width;
            int startHeight = ic.height;
            
            System.out.println("1." + icIdx + " " + ic.imgPath);
            System.out.println("x " + startX + ", y " + startY + ", w " + startWidth + ", h " + startHeight);
            
            g.drawImage(ic.getImg(),
                        startX, startY,
                        startX + (int)(startWidth*scale), startY + (int)(startHeight*scale),
                        0, 0,
                        startWidth, startHeight,
                        null);
        }
        
        /* Process animations after regular images as a workaround for background image during update, thus
           covering over the animation. */
        for (int icIdx = 0; icIdx < images.size(); icIdx++) {
            ImageContainer ic = images.get(icIdx);
            
            if (!ic.isAnimated()){
                continue;
            }
            
            int startX = (int)(ic.x*scale) + GameFrame.xPad;
            int startY = (int)(ic.y*scale) + GameFrame.yPad;
            int startWidth = ic.width;
            int startHeight = ic.height;
            
            System.out.println("2." + icIdx + " " + ic.imgPath);
            System.out.println("x " + startX + ", y " + startY + ", w " + startWidth + ", h " + startHeight);
            
            g.drawImage(ic.getImg(),
                        startX, startY,
                        startX + (int)(startWidth*scale), startY + (int)(startHeight*scale),
                        0, 0,
                        startWidth, startHeight,
                        null);
            
            /* Update animations for next frame (no-op for static images). */
            ic.update();
            if (ic.isActive()){
                needNextTimerTick = true;
            }
        }
        
        /* Update timer as needed for animations */
        if (timer.isRunning()){
            if (needNextTimerTick){
                System.out.println("Timer running");   
            }
            else {
                System.out.println("Timer stopping");
                timer.stop();
            }
        } else {
            if (needNextTimerTick){
                System.out.println("Timer restart");
                timer.restart();
            }
            else {
                System.out.println("Timer stopped");
            }
        }
        
        fromTick = false;
    }
    
    public void registerTimer(Timer t){
        timer = t;
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