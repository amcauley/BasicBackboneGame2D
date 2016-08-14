
package basicbackbonegame2d.Scenes.Scene1.Scene1_1;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;

public class Scene1_1 extends Scene{

    public Scene1_1(){
        /* Basic initialization params */         
        sceneName = "Scene1_1";
        imgPath = "src\\basicbackbonegame2d\\Scenes\\Scene1\\Scene1_1\\Scene1_1.jpg";
        isSubscene = true;
        xLoc = 75;
        yLoc = 250;
        width = 100;
        height = 50;
        
        /* Reset screen - if top level scene */        
                
        /* Create any subscenes and add to array */

        /* Add any starting transitions */        
        
        /* Standard scene drawing routines for top level scenes */           
    }    
    
    @Override
    public void uniqueActionHandler( BasicBackboneGame2D g, 
                                     BasicBackboneGame2D.MouseActions evtType, 
                                     int evtX, 
                                     int evtY) {
      
    }   
}