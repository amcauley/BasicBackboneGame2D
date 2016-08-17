
package basicbackbonegame2d.Scenes.S_Room1.S_Switch;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;

public class S_Switch extends Scene{

    /* Enum of avilable images for this scene */
    public enum imagePathMap{
        DOWN("src\\basicbackbonegame2d\\Scenes\\S_Room1\\S_Switch\\Switch_down.jpg"),
        UP("src\\basicbackbonegame2d\\Scenes\\S_Room1\\S_Switch\\Switch_up.jpg");
        public String str;
        
        imagePathMap(String s){
            str = s;
        }
    }       
    
    public S_Switch(){
        /* Basic initialization params */         
        sceneName = "S_Switch";
        isSubscene = true;
        xLoc = 223;
        yLoc = 186;
        width = 37;
        height = 41;
         
        /* Initialize this scene's image - switch is down */
        imagePath = imagePathMap.DOWN.str;         
        
        /* Reset screen - if top level scene */        
                
        /* Create any subscenes and add to array */

        /* Add any starting transitions */        
        
        /* Standard scene drawing routines for top level scenes */           
    }    
 
}