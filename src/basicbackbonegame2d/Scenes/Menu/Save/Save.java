
package basicbackbonegame2d.Scenes.Menu.Save;

import basicbackbonegame2d.Scene;

public class Save extends Scene{

    /* Enum of avilable images for this scene */
    public enum imagePathMap{
        SAVE("resources/images/Save.jpg");
        
        public String str;
        
        imagePathMap(String s){
            str = s;
        }
    }       
    
    public Save(){
        /* Basic initialization params */         
        sceneName = "Save";
        isSubscene = true;
        xLoc = 51;
        yLoc = 126;
        width = 98;
        height = 48;
         
        /* Initialize this scene's image */
        imagePath = imagePathMap.SAVE.str;
        
        /* Reset screen - if top level scene */        
                
        /* Create any subscenes and add to array */

        /* Add any starting transitions */        
        
        /* Standard scene drawing routines for top level scenes */           
    }    
    
}