
package basicbackbonegame2d.Scenes;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.StateInfo;
import basicbackbonegame2d.Scenes.S_Room1.S_Room1;
import basicbackbonegame2d.Scenes.S_Room2.S_Room2;  
import basicbackbonegame2d.Scenes.S_Win.S_Win;
import basicbackbonegame2d.Top;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SceneManager{    
    
    public SceneManager(){
        
    }
    
    /* Currently only need to list top-level scenes. */
    public enum SceneList{
        TOP(0, Top.getStateInfo()),         //Pseudo-scene for top level management
        S_ROOM1(1, S_Room1.getStateInfo()), //Room1
        S_ROOM2(2, S_Room2.getStateInfo()), //Room2
        S_WIN(3, S_Win.getStateInfo());     //Win screen
        
        /* Each scene can have state associated with it. It also stores its index. */
        public int idx;
        public StateInfo state;
        
        SceneList(int i, StateInfo si){
            idx = i;
            state = si;
        }
    }    
    
    /* Load saved state from file into each scene's state */
    public void loadState() throws IOException{
        SceneList[] sl = SceneList.values();
        String thisLn;
        int thisIdx = 0;
        try (
            FileReader fr = new FileReader("Z:\\Documents\\NetBeansProjects\\BasicBackboneGame2D\\src\\basicbackbonegame2d\\Saves\\Save0.txt");
            BufferedReader br = new BufferedReader(fr);
        ) {
            while ((thisLn = br.readLine()) != null) {
                System.out.println("loading state idx " + thisIdx);
                sl[thisIdx++].state.loadState(thisLn);
            }
        }        
    }
    
    /* Save state to file. */
    public void saveState(){
        String fileName = "Z:\\Documents\\NetBeansProjects\\BasicBackboneGame2D\\src\\basicbackbonegame2d\\Saves\\Save0.txt";
        try (PrintWriter out = new PrintWriter(fileName)) {
            //System.out.println("Saving to file " + fileName);
            
            /* Iterate over scenes and write their save state info out to file, one line per scene in the
               same order the scenes were declared (important to have fixed order for when we want to load
               state from this file later. */
            for (SceneList s : SceneList.values()){
                //For debugging: print out save info
                //System.out.println(s.state.saveState());
                /* Write to file */
                out.println(s.state.saveState());  
            }    
           
        } catch ( IOException e) {
            //Should probably handle this exception
        }

    }
    
    public static void switchScene(BasicBackboneGame2D g, SceneList sceneId){
        
        switch(sceneId){
            case S_ROOM1:   //Room1
                g.topLvlScene = new S_Room1();
                break;
            case S_ROOM2:   //Room2
                g.topLvlScene = new S_Room2();
                break;
            case S_WIN:     //End of game
                g.topLvlScene = new S_Win();
                break;
            default:
                System.out.println("Invalid sceneId " + sceneId);
                break;
            }
        
        /* Update top level state. */
        SceneList.TOP.state.vals[Top.StateMap.LAST_SCENE_ID.idx] = sceneId.idx;
    }
}