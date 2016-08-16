
package basicbackbonegame2d.Scenes.Scene1.Scene1_1;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;

public class Scene1_1 extends Scene{

    /* Enum of avilable images for this scene */
    public enum imagePathMap{
        IMAGE_0("src\\basicbackbonegame2d\\Scenes\\Scene1\\Scene1_1\\Scene1_1.jpg");
        
        public String str;
        
        imagePathMap(String s){
            str = s;
        }
    }       
    
    public Scene1_1(){
        /* Basic initialization params */         
        sceneName = "Scene1_1";
        isSubscene = true;
        xLoc = 75;
        yLoc = 250;
        width = 100;
        height = 50;
         
        /* Initialize this scene's image */
        imagePath = imagePathMap.IMAGE_0.str;         
        
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