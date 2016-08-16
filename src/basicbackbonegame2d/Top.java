
package basicbackbonegame2d;

import static basicbackbonegame2d.Scene.g;
import basicbackbonegame2d.Scenes.SceneManager;

/* Pseudo-scene for top level management. */
public class Top extends Scene{
    
    static StateInfo stateInfo = new StateInfo_Top();
    
    public static StateInfo getStateInfo(){
        return stateInfo;
    }
    
    /* Enum for tracking/defining state info for this scene. */
    public enum StateMap{
        LAST_SCENE_ID(0);
        
        public int idx;
        
        StateMap(int i){
            idx = i;
        }
    }    
    
    public static class StateInfo_Top extends StateInfo{
    
        public StateInfo_Top(){
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];
            
            /* Initialize state values. */
            vals[StateMap.LAST_SCENE_ID.idx] = 1;
        }
        
        @Override
        public String saveState(){
            String strOut = "";
            for (StateMap st : StateMap.values()){
                strOut += String.valueOf(stateInfo.vals[st.idx]) + " ";
            }
            return strOut;
        }           
        
        
        /* Use stateMap enum to map in values from input string */
        @Override
        public void loadState(String str){
            String[] strVals = str.split(" ");
            int idx = 0;
            for (StateMap st : StateMap.values()){
                stateInfo.vals[st.idx] = Integer.valueOf(strVals[idx++]);
            }
        }             

    }    

    public Top(){
        /* Basic initialization params */
        sceneName = "Top";
        isSubscene = false;
        xLoc = 0;
        yLoc = 0;
        width = 0;
        height = 0;
        
        /* Initialize this scene's image */
        //NA
        
        /* Reset screen */
        //NA
        
        /* Create any subscenes and add to array */
        //NA
        
        /* Add any starting transitions */
        //NA
        
        /* Standard scene drawing routines for top level scenes */
        //NA
    }
    
}
