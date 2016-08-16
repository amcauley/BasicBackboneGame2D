
package basicbackbonegame2d.Scenes;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.StateInfo;
import basicbackbonegame2d.Scenes.Scene1.Scene1;
import basicbackbonegame2d.Scenes.Scene2.Scene2;  
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SceneManager{    
    
    public SceneManager(){
        
    }
    
    /* Currently only need to list top-level scenes. */
    public enum SceneList{
        SCENE1(Scene1.getStateInfo()),
        SCENE2(Scene2.getStateInfo());
        
        /* Each scene can have state associated with it. */
        public StateInfo state;
        
        SceneList(StateInfo si){
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
                sl[thisIdx++].state.loadState(thisLn);
            }
        }        
    }
    
    /* Save state to file. */
    public void saveState(){
        String fileName = "Z:\\Documents\\NetBeansProjects\\BasicBackboneGame2D\\src\\basicbackbonegame2d\\Saves\\Save0.txt";
        try (PrintWriter out = new PrintWriter(fileName)) {
            System.out.println("Saving to file " + fileName);
            
            /* Iterate over scenes and write their save state info out to file, one line per scene in the
               same order the scenes were declared (important to have fixed order for when we want to load
               state from this file later. */
            for (SceneList s : SceneList.values()){
                //For debugging: print out save info
                System.out.println(s.state.saveState());
                /* Write to file */
                out.println(s.state.saveState());  
            }    
           
        } catch ( IOException e) {
            //Should probably handle this exception
        }

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