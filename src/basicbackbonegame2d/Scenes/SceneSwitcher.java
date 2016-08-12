
package basicbackbonegame2d.Scenes;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scenes.Scene1.Scene1;
import basicbackbonegame2d.Scenes.Scene2.Scene2;  

public class SceneSwitcher{    
    
    public SceneSwitcher(){
        
    }
    
    /* Currently only need to list top-level scenes. */
    public enum SceneList{
        INVALID,
        SCENE1,
        SCENE2
    }    
    
    public static void switchScene(BasicBackboneGame2D g, SceneList sceneId){
        
        switch(sceneId){
            case SCENE1: //Scene1
                g.topLvlScene = new Scene1();
                break;
            case SCENE2: //Scene2
                g.topLvlScene = new Scene2();
                break;
            default:
                System.out.println("Invalid sceneId " + sceneId);
                break;
            }
    }
}