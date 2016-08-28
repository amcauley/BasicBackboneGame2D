
package basicbackbonegame2d;

import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JFrame;

public class GameFrame extends JFrame{
    
    /* Nominal size of window and native image size. */
    public static final int NOMINAL_WIDTH = 400;
    public static final int NOMINAL_HEIGHT = 400;
    
    /* Store the actual, current width and height. */
    //TODO: maybe use getters and setters for these
    public static int width;
    public static int height;
    public static float scale;
    
    /* Horizontal padding. Amount of padding on the left of drawable image. */
    public static int xPad;
    /* Vertical padding - padding from the top of the frame to drawable image area. */
    public static int yPad;
    
    /* Padding due to window border/header. TODO: Need to study this more. */
    int framePadX;    
    int framePadY;
    
    public void scaleComp(){
        /* Compute scaling from native size to current target size. */     
        float scaleX = (float)width / NOMINAL_WIDTH;
        float scaleY = (float)height / NOMINAL_HEIGHT;
        
        scale = Math.min(scaleX, scaleY);     
        
        xPad = (int)((scaleX - scale)*NOMINAL_WIDTH*0.5);
        yPad = (int)((scaleY - scale)*NOMINAL_HEIGHT*0.5);  
        
        //System.out.println("Frame " + width + "x" + height + ", scale " + scale + 
        //                   ", xPad " + xPad + ", yPad " + yPad);        
    }
        
    public GameFrame(){
        
        width = 1000;
        height = 800;
  
        scaleComp();
    }
    
    public void init() {
        setTitle("Default Title"); 
               
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setResizable(true);       
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 1, 0, 0));
        
        /* Add the static screen to this JFrame-based object. */
        add(Scene.screen);
        
        setVisible(true); 
        
        /* Now that frame exists, we can check what the extra border padding values are. Possibly
           there's a better way to do this. Register resize listener after this, since this is required
           info for proper resizing. */
        framePadX = getContentPane().getWidth() - width;
        framePadY = getContentPane().getHeight() - height;
        
        System.out.println("framePadX " + framePadX + ", framePadY " + framePadY);
        
        /* Listener for resizing */
        addComponentListener(new ComponentListener(){
            public void componentHidden(ComponentEvent e) {
                //System.out.println("Frame hidden");
            }
            
            public void componentMoved(ComponentEvent e) {   
                //System.out.println("Frame moved");
            }
            
            public void componentResized(ComponentEvent e) {
                width = getContentPane().getWidth();
                height = getContentPane().getHeight();
                scaleComp();
                
                //System.out.println("Frame resized: " + getContentPane().getWidth() + "x" + 
                //                   getContentPane().getHeight());                
                
                Scene.screen.repaint();
            }

            public void componentShown(ComponentEvent e) {
                //System.out.println("Frame shown");
            }               
        });        
    }    
    
}
