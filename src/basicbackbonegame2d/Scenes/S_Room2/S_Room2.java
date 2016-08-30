
package basicbackbonegame2d.Scenes.S_Room2;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.GameFrame;
import basicbackbonegame2d.Jukebox;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.S_Room2.S_Bauble.S_Bauble;
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
        KEY_IN_DOOR(0), //Key in door
        BAUBLE(1);
        
        int idx;
        
        SubSceneMap(int i){
            idx = i;
        }
    }   
    
    /* Enum of avilable images for this scene */
    public enum imagePathMap{
        DARK("resources/images/Room2_dark.jpg"),
        LIGHT("resources/images/Room2_light.jpg");
        
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
        width = GameFrame.NOMINAL_WIDTH;
        height = GameFrame.NOMINAL_HEIGHT;
        
        /* Reset screen */        
        screen.clearImgs();
                
        /* Create any subscenes and add to array */
        numSubScenes = SubSceneMap.values().length;
        subScenes = new Scene[numSubScenes];
        
        subScenes[SubSceneMap.KEY_IN_DOOR.idx] = new S_Key_In_Door();       
        subScenes[SubSceneMap.BAUBLE.idx] = new S_Bauble();        
        
        /* Initialize this scene's image */
        if (SceneManager.SceneList.TOP.state.vals[Top.StateMap.ROOM2_HAS_PWR.idx] == 0){
            imagePath = imagePathMap.DARK.str;
            subScenes[SubSceneMap.KEY_IN_DOOR.idx].setActiveState(false);
            subScenes[SubSceneMap.BAUBLE.idx].setActiveState(false);
        } else {
            imagePath = imagePathMap.LIGHT.str;
            /* If key is in door, set key in door subscene to active and add transition
               to win screen. */
            if (stateInfo.vals[StateMap.KEY_IN_DOOR.idx] == 1){
                //subScenes[SubSceneMap.KEY_IN_DOOR.idx].swapImage(S_Key_In_Door.imagePathMap.KEY_IN_DOOR.str);
                subScenes[SubSceneMap.KEY_IN_DOOR.idx].setActiveState(false);
                addTransition(new Transition(SceneList.S_WIN, 193, 164, 76, 122, Jukebox.Sounds.NONE));
            }            

            /* Set bauble invisible if it's been obtained already. */
            if(SceneManager.SceneList.TOP.state.vals[Top.StateMap.HAS_BAUBLE.idx] == 1){
                subScenes[SubSceneMap.BAUBLE.idx].setActiveState(false);
            }             
            
        }               
        
        /* Add any starting transitions */        
        addTransition(new Transition(SceneList.S_ROOM1, 20, 168, 53, 180, Jukebox.Sounds.DOOR0));
        
        /* Start BG music. */
        g.jukebox.play(Jukebox.Sounds.BG_MUSIC0, true);        
        
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
            
            addTransition(new Transition(SceneList.S_WIN, 193, 164, 76, 122, Jukebox.Sounds.NONE)); 
            
            screen.clearImgs();
            updateScreen(); 
            draw();             
            
            /* Cease further processing to prevent cursor type from changing again in original
               calling routine. */
            return 1;
        }
        
        if ( (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) && 
              subScenes[SubSceneMap.BAUBLE.idx].isHit(evtX, evtY) ){
         
            /* Set bauble in scene to non-active so it won't be drawn. */
            subScenes[SubSceneMap.BAUBLE.idx].setActiveState(false);
            
            /* Update state to reflect the fact that bauble is taken. */
            SceneManager.SceneList.TOP.state.vals[Top.StateMap.HAS_BAUBLE.idx] = 1;
            
            /* Refresh the screen. */
            screen.clearImgs();
            updateScreen();
            draw();
        }        

        return 0;
    }
    
}
