
package basicbackbonegame2d.Scenes.Scene1;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.SceneManager.SceneList;
import basicbackbonegame2d.StateInfo;
import basicbackbonegame2d.Scenes.Scene1.Scene1_1.Scene1_1;

public class Scene1 extends Scene{
    
    static StateInfo stateInfo = new StateInfo_Scene1();
    
    public static StateInfo getStateInfo(){
        return stateInfo;
    }
    
    /* Enum for tracking/defining state info for this scene. */
    enum StateMap{
        STATE_INFO0(0),
        STATE_INFO1(1);
        
        int idx;
        
        StateMap(int i){
            idx = i;
        }
    }    
    
    public static class StateInfo_Scene1 extends StateInfo{
    
        public StateInfo_Scene1(){
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];
            
            /* Initialize state values. */
            vals[StateMap.STATE_INFO0.idx] = 42;
            vals[StateMap.STATE_INFO1.idx] = 7;
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
    
    /* Use an enum to better track subscenes of this scene */ 
    enum SubSceneMap{
        SCENE1_1(0);
        
        int idx;
        
        SubSceneMap(int i){
            idx = i;
        }
    }    
    
    /* Enum of avilable images for this scene */
    public enum imagePathMap{
        IMAGE_0("src\\basicbackbonegame2d\\Scenes\\Scene1\\Scene1.jpg");
        
        public String str;
        
        imagePathMap(String s){
            str = s;
        }
    }
    
    public Scene1(){
        /* Basic initialization params */
        sceneName = "Scene1";
        isSubscene = false;
        xLoc = 0;
        yLoc = 0;
        width = 400;
        height = 400;
        
        /* Initialize this scene's image */
        imagePath = imagePathMap.IMAGE_0.str;        
        
        /* Reset screen */
        screen.clearImgs();
        
        /* Create any subscenes and add to array */
        numSubScenes = SubSceneMap.values().length;
        subScenes = new Scene[numSubScenes];
        subScenes[SubSceneMap.SCENE1_1.idx] = new Scene1_1();
        
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
        
        if ( (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) && 
             subScenes[SubSceneMap.SCENE1_1.idx].isHit(evtX, evtY) ){
            g.sm.saveState();
        }
      
    }
}
