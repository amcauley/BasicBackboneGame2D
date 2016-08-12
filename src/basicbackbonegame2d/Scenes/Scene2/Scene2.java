
package basicbackbonegame2d.Scenes.Scene2;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.Scene1.Scene1;

public class Scene2 extends Scene{
    
    public Scene2(){
        sceneName = "Scene2";
        imgPath = "src\\basicbackbonegame2d\\Scenes\\Scene2\\Scene2.jpg";
        isSubscene = false;
        xLoc = 0;
        yLoc = 0;
        width = 400;
        height = 400;
        
        screen.clearImgs();
        
        addTransition(new Transition(0,   0, 0, 50, 400));
        addTransition(new Transition(0, 350, 0, 50, 400));
        
        updateScreen();
        draw();
    }
    
    @Override
    public void uniqueActionHandler(BasicBackboneGame2D g, int evtType, int evtX, int evtY) {
        
    }
    
}
