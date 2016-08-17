
package basicbackbonegame2d.Scenes.S_Room2.S_Key_In_Door;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;

public class S_Key_In_Door extends Scene{

    /* Enum of avilable images for this scene */
    public enum imagePathMap{
        NO_KEY("src\\basicbackbonegame2d\\Scenes\\S_Room2\\S_Key_In_Door\\No_Key.jpg");
        
        public String str;
        
        imagePathMap(String s){
            str = s;
        }
    }       
    
    public S_Key_In_Door(){
        /* Basic initialization params */         
        sceneName = "S_Key_In_Door";
        isSubscene = true;
        xLoc = 197;
        yLoc = 225;
        width = 24;
        height = 24;
         
        /* Initialize this scene's image */
        imagePath = imagePathMap.NO_KEY.str;
        
        /* Reset screen - if top level scene */        
                
        /* Create any subscenes and add to array */

        /* Add any starting transitions */        
        
        /* Standard scene drawing routines for top level scenes */           
    }    
    
}