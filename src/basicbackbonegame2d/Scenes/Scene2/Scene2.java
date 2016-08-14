
package basicbackbonegame2d.Scenes.Scene2;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.SceneManager.SceneList;
import basicbackbonegame2d.StateInfo;

public class Scene2 extends Scene{
    
    /* Enum for tracking/defining state info for this scene. */
    enum StateMap{
        STATE_INFO0(0);
        
        int val;
        
        StateMap(int v){
            val = v;
        }
    }    
    
    public static class StateInfo_Scene2 extends StateInfo{
    
        public StateInfo_Scene2(){
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];
            
            /* Initialize state values. */
            vals[StateMap.STATE_INFO0.val] = 99;
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
    
    public Scene2(){
        /* Basic initialization params */        
        sceneName = "Scene2";
        imgPath = "src\\basicbackbonegame2d\\Scenes\\Scene2\\Scene2.jpg";
        isSubscene = false;
        xLoc = 0;
        yLoc = 0;
        width = 400;
        height = 400;

        /* Reset screen */        
        screen.clearImgs();
                
        /* Create any subscenes and add to array */

        /* Add any starting transitions */        
        addTransition(new Transition(SceneList.SCENE1,   0, 0, 50, 400));
        addTransition(new Transition(SceneList.SCENE1, 350, 0, 50, 400));
        
        /* Standard scene drawing routines for top level scenes */        
        updateScreen();
        draw();
    }
    
    @Override
    public void uniqueActionHandler( BasicBackboneGame2D g, 
                                     BasicBackboneGame2D.MouseActions evtType, 
                                     int evtX, 
                                     int evtY) {
      
    }
    
}
