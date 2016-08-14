
package basicbackbonegame2d.Scenes;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.StateInfo;
import basicbackbonegame2d.Scenes.Scene1.Scene1;
import basicbackbonegame2d.Scenes.Scene1.Scene1.StateInfo_Scene1;
import basicbackbonegame2d.Scenes.Scene2.Scene2;  
import basicbackbonegame2d.Scenes.Scene2.Scene2.StateInfo_Scene2;

public class SceneManager{    
    
    public SceneManager(){
        
    }
    
    /* Currently only need to list top-level scenes. */
    public enum SceneList{
        SCENE1(new StateInfo_Scene1()),
        SCENE2(new StateInfo_Scene2());
        
        /* Each scene can have state associated with it. */
        public StateInfo state;
        
        SceneList(StateInfo si){
            state = si;
        }
    }    
    
    /* Load saved state from file into each scene's state */
    public void loadState(){
        
    }
    
    /* Save state to file. */
    public void saveState(){
        
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