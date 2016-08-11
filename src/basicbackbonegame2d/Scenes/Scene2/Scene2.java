
package basicbackbonegame2d.Scenes.Scene2;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.Scene1.Scene1;

public class Scene2 extends Scene{
    
    public Scene2(){
        sceneId = "Scene2";
        imgPath = "src\\basicbackbonegame2d\\Scenes\\Scene2\\Scene2.jpg";
        xLoc = 0;
        yLoc = 0;
        width = 400;
        height = 400;
        
        screen.clearImgs();
        
        updateScreen();
        draw();
    }
    
    @Override
    public void actionHandler(BasicBackboneGame2D g, int evtType, int evtX, int evtY) {
        
        /* Handle the event. */ 
        //System.out.println(sceneId + ": evt " + evtType + ", (" + evtX + "," + evtY + ")");           
        
        if ((evtType == 0) && (evtX < 50)){
            //System.out.println(sceneId + " -> " + "Scene1");
            g.topLvlScene = new Scene1();
        }     
    }
    
}
