
package basicbackbonegame2d.Scenes.S_Room1.S_Key;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;

public class S_Key extends Scene{

    /* Enum of avilable images for this scene */
    public enum imagePathMap{
        KEY("src\\basicbackbonegame2d\\Scenes\\S_Room1\\S_Key\\Key.jpg");
        
        public String str;
        
        imagePathMap(String s){
            str = s;
        }
    }       
    
    public S_Key(){
        /* Basic initialization params */         
        sceneName = "S_Key";
        isSubscene = true;
        xLoc = 73;
        yLoc = 235;
        width = 51;
        height = 30;
         
        /* Initialize this scene's image */
        imagePath = imagePathMap.KEY.str;         
        
        /* Reset screen - if top level scene */        
                
        /* Create any subscenes and add to array */

        /* Add any starting transitions */        
        
        /* Standard scene drawing routines for top level scenes */           
    }    
  
}