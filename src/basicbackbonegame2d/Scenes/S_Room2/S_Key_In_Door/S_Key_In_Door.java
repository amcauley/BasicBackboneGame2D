
package basicbackbonegame2d.Scenes.S_Room2.S_Key_In_Door;

import basicbackbonegame2d.Scene;

public class S_Key_In_Door extends Scene{

    /* Enum of avilable images for this scene */
    public enum imagePathMap{
        NO_KEY("resources/images/No_Key.jpg");
        
        public String str;
        
        imagePathMap(String s){
            str = s;
        }
    }       
    
    public S_Key_In_Door(){
        /* Basic initialization params */         
        sceneName = "S_Key_In_Door";
        isSubscene = true;
        xLoc = 198;
        yLoc = 227;
        width = 20;
        height = 18;
         
        /* Initialize this scene's image */
        imagePath = imagePathMap.NO_KEY.str;
        
        /* Reset screen - if top level scene */        
                
        /* Create any subscenes and add to array */

        /* Add any starting transitions */        
        
        /* Standard scene drawing routines for top level scenes */           
    }    
    
}