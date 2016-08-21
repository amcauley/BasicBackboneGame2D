
package basicbackbonegame2d.Scenes.S_Win;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Jukebox;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.SceneManager.SceneList;
import basicbackbonegame2d.StateInfo;

public class S_Win extends Scene{
    
    static StateInfo stateInfo = new S_Win.StateInfo_Win();
    
    public static StateInfo getStateInfo(){
        return stateInfo;
    }    
    
    /* Enum for tracking/defining state info for this scene. */
    enum StateMap{
        RESERVED(0);    //Win doesn't need any actual state for now
        
        int idx;
        
        StateMap(int i){
            idx = i;
        }
    }    
    
    public static class StateInfo_Win extends StateInfo{
    
        public StateInfo_Win(){
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];
            
            /* Initialize state values. */
            vals[StateMap.RESERVED.idx] = 0;
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
    //enum SubSceneMap{
    //    RESERVED(0);    //Currently no subscenes
    //    
    //    int idx;
    //    
    //    SubSceneMap(int i){
    //        idx = i;
    //    }
    //}   
    
    /* Enum of avilable images for this scene */
    public enum imagePathMap{
        WIN("src\\basicbackbonegame2d\\Scenes\\S_Win\\Win.jpg");
        
        public String str;
        
        imagePathMap(String s){
            str = s;
        }
    }    
    
    public S_Win(){
        /* Basic initialization params */     
        sceneName = "S_Win";
        isSubscene = false;
        xLoc = 0;
        yLoc = 0;
        width = 400;
        height = 400;
        
        /* Initialize this scene's image */
        imagePath = imagePathMap.WIN.str;         
        
        /* Reset screen */        
        screen.clearImgs();
                
        /* Create any subscenes and add to array */
        //No subscenes for this scene        
        
        /* Add any starting transitions */
        //No transitions
        
        /* Victory music. */
        g.jukebox.stopAll();
        g.jukebox.play(Jukebox.Sounds.VICTORY, false);
        
        /* Standard scene drawing routines for top level scenes */        
        updateScreen();
        draw();
    }
    
}
