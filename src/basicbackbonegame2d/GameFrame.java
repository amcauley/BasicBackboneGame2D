
package basicbackbonegame2d;

import java.awt.GridLayout;
import javax.swing.JFrame;

public class GameFrame extends JFrame{
    
    public GameFrame(){
        
    }
    
    public void init() {
        setTitle("Default Title"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setResizable(false);       
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 1, 0, 0));
        
        /* Add the static screen to this JFrame-based object. */
        add(Scene.screen);
        
        setVisible(true); 
    }    
    
}
