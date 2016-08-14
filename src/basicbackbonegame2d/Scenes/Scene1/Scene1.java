
package basicbackbonegame2d.Scenes.Scene1;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.SceneManager.SceneList;
import basicbackbonegame2d.StateInfo;
import basicbackbonegame2d.Scenes.Scene1.Scene1_1.Scene1_1;

public class Scene1 extends Scene{
    
    /* Enum for tracking/defining state info for this scene. */
    enum StateMap{
        STATE_INFO0(0);
        
        int val;
        
        StateMap(int v){
            val = v;
        }
    }    
    
    public static class StateInfo_Scene1 extends StateInfo{
    
        public StateInfo_Scene1(){
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];
            
            /* Initialize state values. */
            vals[StateMap.STATE_INFO0.val] = 42;
        }
        
        
        /* Use stateMap enum to map in values from input string */
        @Override
        public void loadState(String str){

        }        

        @Override
        public String saveState(){
            return "";
        }        

    }
    
    /* Use an enum to better track subscenes of this scene */ 
    enum SubSceneMap{
        SCENE1_1(0);
        
        int val;
        
        SubSceneMap(int v){
            val = v;
        }
    }    
    
    public Scene1(){
        /* Basic initialization params */
        sceneName = "Scene1";
        imgPath = "src\\basicbackbonegame2d\\Scenes\\Scene1\\Scene1.jpg";
        isSubscene = false;
        xLoc = 0;
        yLoc = 0;
        width = 400;
        height = 400;
        
        /* Reset screen */
        screen.clearImgs();
        
        /* Create any subscenes and add to array */
        numSubScenes = SubSceneMap.values().length;
        subScenes = new Scene[numSubScenes];
        subScenes[SubSceneMap.SCENE1_1.val] = new Scene1_1();
        
        /* Add any starting transitions */
        addTransition(new Transition(SceneList.SCENE2, 350, 0, 50, 400));
        
        /* Standard scene drawing routines for top level scenes */
        updateScreen();
        draw();
    }
    
    @Override
    public void uniqueActionHandler( BasicBackboneGame2D g, 
                                     BasicBackboneGame2D.MouseActions evtType, 
                                     int evtX, 
                                     int evtY) {
        
        if (subScenes[SubSceneMap.SCENE1_1.val].isHit(evtX, evtY)){
            
        }
      
    }
}
