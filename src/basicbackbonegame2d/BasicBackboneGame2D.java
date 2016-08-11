
package basicbackbonegame2d;

import basicbackbonegame2d.Scenes.Scene1.Scene1;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;


public class BasicBackboneGame2D {

    GameFrame gameFrame = new GameFrame();
    
    /* Starting scene for the game. */
    public Scene topLvlScene = new Scene1();

    /* Handle mouse events. Needs access to topLvlScene for passing on events
       to the scene. */
    public class gameMouseListener extends MouseInputAdapter {
    
        public gameMouseListener() {
            System.out.println("Instantiating gameMouseListener");
        }
    
        @Override
        public void mousePressed(MouseEvent me) { 
            topLvlScene.actionHandler(BasicBackboneGame2D.this, 0, me.getX(), me.getY());
        }
            
        @Override
        public void mouseMoved(MouseEvent me) {
            topLvlScene.actionHandler(BasicBackboneGame2D.this, 1, me.getX(), me.getY());
        }
    }    
    
    public BasicBackboneGame2D(){
        gameFrame.init();
        
        gameMouseListener mouseListener = new gameMouseListener();
        topLvlScene.screen.registerMouseListener(mouseListener);
        
        /* Add the static screen to this JFrame-based object. */
        gameFrame.add(Scene.screen);
        gameFrame.setVisible(true);        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new BasicBackboneGame2D();
    }
    
}
