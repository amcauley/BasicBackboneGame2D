
package basicbackbonegame2d.Scenes.Scene1;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.Scene1.Scene1_1.Scene1_1;
import basicbackbonegame2d.Scenes.SceneSwitcher.SceneList;

public class Scene1 extends Scene{
    
    public Scene1(){
        sceneName = "Scene1";
        imgPath = "src\\basicbackbonegame2d\\Scenes\\Scene1\\Scene1.jpg";
        isSubscene = false;
        xLoc = 0;
        yLoc = 0;
        width = 400;
        height = 400;
        
        screen.clearImgs();
        
        addSubScene(new Scene1_1());
        
        addTransition(new Transition(SceneList.SCENE2, 350, 0, 50, 400));
        
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
