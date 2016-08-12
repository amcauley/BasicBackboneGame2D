
package basicbackbonegame2d.Scenes.Scene2;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.SceneSwitcher.SceneList;

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
        
        addTransition(new Transition(SceneList.SCENE1,   0, 0, 50, 400));
        addTransition(new Transition(SceneList.SCENE1, 350, 0, 50, 400));
        
        updateScreen();
        draw();
    }
    
    @Override
    public void uniqueActionHandler( BasicBackboneGame2D g, 
                                     BasicBackboneGame2D.MouseActions evtType, 
                                     int evtX, 
                                     int evtY) {
      
    }
    
}
