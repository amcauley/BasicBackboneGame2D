
package basicbackbonegame2d.Scenes.S_Room2;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.S_Room2.S_Key_In_Door.S_Key_In_Door;
import basicbackbonegame2d.Scenes.SceneManager;
import basicbackbonegame2d.Scenes.SceneManager.SceneList;
import basicbackbonegame2d.StateInfo;
import basicbackbonegame2d.Top;

public class S_Room2 extends Scene{
    
    static StateInfo stateInfo = new S_Room2.StateInfo_Room2();
    
    public static StateInfo getStateInfo(){
        return stateInfo;
    }    
    
    /* Enum for tracking/defining state info for this scene. */
    enum StateMap{
        KEY_IN_DOOR(0); //Key is in the door
        
        int idx;
        
        StateMap(int i){
            idx = i;
        }
    }    
    
    public static class StateInfo_Room2 extends StateInfo{
    
        public StateInfo_Room2(){
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];
            
            /* Initialize state values. */
            vals[StateMap.KEY_IN_DOOR.idx] = 0; //Default to no key in door
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
        KEY_IN_DOOR(0); //Key in door
        
        int idx;
        
        SubSceneMap(int i){
            idx = i;
        }
    }   
    
    /* Enum of avilable images for this scene */
    public enum imagePathMap{
        DARK("src\\basicbackbonegame2d\\Scenes\\S_Room2\\Room2_dark.jpg"),
        LIGHT("src\\basicbackbonegame2d\\Scenes\\S_Room2\\Room2_light.jpg");
        
        public String str;
        
        imagePathMap(String s){
            str = s;
        }
    }    
    
    public S_Room2(){
        /* Basic initialization params */     
        sceneName = "S_Room2";
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
        
        subScenes[SubSceneMap.KEY_IN_DOOR.idx] = new S_Key_In_Door();       
        
        /* Initialize this scene's image */
        if (SceneManager.SceneList.TOP.state.vals[Top.StateMap.ROOM2_HAS_PWR.idx] == 0){
            imagePath = imagePathMap.DARK.str;
            subScenes[SubSceneMap.KEY_IN_DOOR.idx].setActiveState(false);
        } else {
            imagePath = imagePathMap.LIGHT.str;
            /* If key is in door, set key in door subscene to active and add transition
               to win screen. */
            if (stateInfo.vals[StateMap.KEY_IN_DOOR.idx] == 1){
                //subScenes[SubSceneMap.KEY_IN_DOOR.idx].swapImage(S_Key_In_Door.imagePathMap.KEY_IN_DOOR.str);
                subScenes[SubSceneMap.KEY_IN_DOOR.idx].setActiveState(false);
                addTransition(new Transition(SceneList.S_WIN, 193, 164, 76, 122));
            }            
            
        }        
        
        /* Add any starting transitions */        
        addTransition(new Transition(SceneList.S_ROOM1, 20, 168, 53, 180));
        
        /* Standard scene drawing routines for top level scenes */        
        updateScreen();
        draw();
    }
    
    @Override
    public int uniqueActionHandler(  BasicBackboneGame2D g, 
                                     BasicBackboneGame2D.MouseActions evtType, 
                                     int evtX, 
                                     int evtY) {  

        /* Handle key */
        if ( (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) && 
              subScenes[SubSceneMap.KEY_IN_DOOR.idx].isHit(evtX, evtY) &&
              (SceneManager.SceneList.TOP.state.vals[Top.StateMap.HAS_KEY.idx] == 1) ){
            
            stateInfo.vals[StateMap.KEY_IN_DOOR.idx] = 1;
            /* Set subscene inactive, revealing the background that has the key drawn on
               it already. If we just swapped images, then we would get the wrong cursor
               when hovering over the key - we want the transition cursor, since the key
               can't be interacted with any more. */
            subScenes[SubSceneMap.KEY_IN_DOOR.idx].setActiveState(false);
            
            addTransition(new Transition(SceneList.S_WIN, 193, 164, 76, 122)); 
            
            screen.clearImgs();
            updateScreen(); 
            draw();             
            
            /* Cease further processing to prevent cursor type from changing again in original
               calling routine. */
            return 1;
        }

        return 0;
    }
    
}
