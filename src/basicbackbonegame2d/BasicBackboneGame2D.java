
package basicbackbonegame2d;

import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;


public class BasicBackboneGame2D {

    GameFrame gameFrame = new GameFrame();
    
    /* Starting scene for the game. */
    Scene topLvlScene = new Scene( "testScene1", 
                                   "Scenes\\testScene1.jpg", 
                                   0, 0, 400, 400 );

    /* Handle mouse events. Needs access to topLvlScene for passing on events
       to the scene. */
    public class gameMouseListener extends MouseInputAdapter {
    
        public gameMouseListener() {
            System.out.println("Instantiating gameMouseListener");
        }
    
        @Override
        public void mousePressed(MouseEvent me) { 
            topLvlScene.actionHandler(0, me.getX(), me.getY());
        }
            
        @Override
        public void mouseMoved(MouseEvent me) {
            topLvlScene.actionHandler(1, me.getX(), me.getY());
        }
    }    
    
    public BasicBackboneGame2D(){
        gameFrame.init();
        
        gameMouseListener mouseListener = new gameMouseListener();
        topLvlScene.screen.registerMouseListener(mouseListener);
        
        /* Sub-scene. TODO: This is just for testing - eventually each scene can get its own
           file as a derived class of the parent class Scene. Scene connections can be specified
           for each derived class at compile time. Or maybe some other method will be used; needs
           more thought.
        */
        Scene secondaryScene = new Scene( "testScene2",
                                          "Scenes\\testScene2.jpg", 
                                          75, 250, 100, 50 );
        topLvlScene.addSubScene(secondaryScene);        
        
        topLvlScene.updateScreen();
        
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
