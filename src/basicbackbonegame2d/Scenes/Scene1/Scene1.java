
package basicbackbonegame2d.Scenes.Scene1;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.Scene1.Scene1_1.Scene1_1;
import basicbackbonegame2d.Scenes.Scene2.Scene2;

public class Scene1 extends Scene{
    
    public Scene1(){
        sceneId = "Scene1";
        imgPath = "src\\basicbackbonegame2d\\Scenes\\Scene1\\Scene1.jpg";
        xLoc = 0;
        yLoc = 0;
        width = 400;
        height = 400;
        
        screen.clearImgs();
        
        addSubScene(new Scene1_1());
        
        updateScreen();
        draw();
    }
    
    @Override
    public void actionHandler(BasicBackboneGame2D g, int evtType, int evtX, int evtY) {
        
        /* Handle the event. */ 
        //System.out.println(sceneId + ": evt " + evtType + ", (" + evtX + "," + evtY + ")");   
        
        if ((evtType == 0) && (evtX > 350)){
            //System.out.println(sceneId + " -> " + "Scene2");
            g.topLvlScene = new Scene2();
        }
        
        /* Check if any subscenes are hit and handle it if they are. */
        for (Scene ss : subScenes) {
            if (ss.isHit(evtX, evtY)) {
                ss.actionHandler(g, evtType, evtX, evtY);
            }
        }        
    }
}
