
package basicbackbonegame2d.Scenes.Scene2.Scene2_1;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;

public class Scene2_1 extends Scene{

    /* Enum of avilable images for this scene */
    public enum imagePathMap{
        IMAGE_0("src\\basicbackbonegame2d\\Scenes\\Scene2\\Scene2_1\\Scene2_1_img0.jpg"),
        IMAGE_1("src\\basicbackbonegame2d\\Scenes\\Scene2\\Scene2_1\\Scene2_1_img1.jpg");
        
        public String str;
        
        imagePathMap(String s){
            str = s;
        }
    }       
    
    public Scene2_1(){
        /* Basic initialization params */         
        sceneName = "Scene2_1";
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