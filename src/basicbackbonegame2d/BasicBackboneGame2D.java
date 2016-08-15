
package basicbackbonegame2d;

import basicbackbonegame2d.Scenes.Scene1.Scene1;
import basicbackbonegame2d.Scenes.SceneManager;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;


public class BasicBackboneGame2D {

    GameFrame gameFrame = new GameFrame();
    
    public SceneManager sm = new SceneManager();
    
    /* Top level scene */
    public Scene topLvlScene;

    public enum MouseActions{
        LEFT_BUTTON, RIGHT_BUTTON, MOVEMENT
    }    
    
    /* Handle mouse events. Needs access to topLvlScene for passing on events
       to the scene. */
    public class gameMouseListener extends MouseInputAdapter {
        
        public gameMouseListener() {
            System.out.println("Instantiating gameMouseListener");
        }
    
        @Override
        public void mousePressed(MouseEvent me) { 
            if (SwingUtilities.isLeftMouseButton(me)) {
                topLvlScene.actionHandler(BasicBackboneGame2D.this, 
                                            MouseActions.LEFT_BUTTON, 
                                            me.getX(), me.getY());                
            }
            else if (SwingUtilities.isRightMouseButton(me)) {
                topLvlScene.actionHandler(BasicBackboneGame2D.this, 
                                            MouseActions.RIGHT_BUTTON, 
                                            me.getX(), me.getY());                
            }
            
        }
            
        @Override
        public void mouseMoved(MouseEvent me) {
            topLvlScene.actionHandler(  BasicBackboneGame2D.this, 
                                        MouseActions.MOVEMENT, 
                                        me.getX(), me.getY());
        }
    }    
    
    public BasicBackboneGame2D(){
    
    }
    
    public void run(){
        gameFrame.init();
        
        /* Register game object to scene. */
        Scene.g = this;
        
        gameMouseListener mouseListener = new gameMouseListener();
        topLvlScene.screen.registerMouseListener(mouseListener);
        
        /* Add the static screen to this JFrame-based object. */
        gameFrame.add(Scene.screen);
        gameFrame.setVisible(true);    
        
        /* Add this after screen is added and setVisible, since scene creation calls
           updateScreen(), which calls getLocationOnScreen() for screen, and it must
           already be drawn on the screen or we hit a runtime error.
        */
        topLvlScene = new Scene1();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BasicBackboneGame2D game = new BasicBackboneGame2D();
        game.run();
    }
    
}
