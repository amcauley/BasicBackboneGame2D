
package basicbackbonegame2d.Scenes;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.GameFrame;
import basicbackbonegame2d.Scenes.Menu.Menu;
import basicbackbonegame2d.StateInfo;
import basicbackbonegame2d.Scenes.S_Room1.S_Room1;
import basicbackbonegame2d.Scenes.S_Room2.S_Room2;  
import basicbackbonegame2d.Scenes.S_Win.S_Win;
import basicbackbonegame2d.Top;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class SceneManager{    
    
    //public static final String AUTOSAVE_FILENAME = "AutoSave.txt";
    
    public SceneManager(){
        
    }
    
    /* Currently only need to list top-level scenes. */
    public enum SceneList{
        TOP(0, Top.getStateInfo()),         //Pseudo-scene for top level management
        MENU(1, Menu.getStateInfo()),       //Menu "Scene"
        S_ROOM1(2, S_Room1.getStateInfo()), //Room1
        S_ROOM2(3, S_Room2.getStateInfo()), //Room2
        S_WIN(4, S_Win.getStateInfo());     //Win screen
        
        /* Each scene can have state associated with it. It also stores its index. */
        public int idx;
        public StateInfo state;
        
        SceneList(int i, StateInfo si){
            idx = i;
            state = si;
        }
    }        
    
    /* Load saved state from file into each scene's state */
    public void loadState(String fileName) throws IOException{
        SceneList[] sl = SceneList.values();
        String thisLn;
        int thisIdx = 0;
        try (
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
        ) {
            while ((thisLn = br.readLine()) != null) {
                //System.out.println("loading state idx " + thisIdx);
                sl[thisIdx++].state.loadState(thisLn);
            }
        }        
    }
    
    public void loadStateResource(String resourceName) throws IOException{
        SceneList[] sl = SceneList.values();
        String thisLn;
        int thisIdx = 0;
        try (
            BufferedReader br = 
                    new BufferedReader(
                            new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourceName)));
        ) {
            while ((thisLn = br.readLine()) != null) {
                //System.out.println("loading state idx " + thisIdx);
                sl[thisIdx++].state.loadState(thisLn);
            }
        }         
    }
    
    /* If no filename specified, use autosave file. */
    public void loadState() throws IOException{
            //TBD need to work out how to handle this in distributed JAR
            //loadState(AUTOSAVE_FILENAME);     
    }    
    
    /* Save state to file. */
    public void saveState(String fileName){
        /* Add .txt suffix if not already present. */
        String fEnd = fileName.substring(fileName.length()-4);
        if (!fEnd.equals(".txt")){
            fileName += ".txt";
        }
        
        try (PrintWriter out = new PrintWriter(fileName)) {
            System.out.println("Saving to file " + fileName);
            
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
    
    /* If no filename specified, use autosave file. */
    public void saveState(){
        //TBD need to work out how to handle this in distributed JAR
        //saveState(AUTOSAVE_FILENAME);
    }
    
    public static void switchScene(BasicBackboneGame2D g, SceneList sceneId) {
        
        switch(sceneId){
            case MENU:      //Menu "Scene"
                /* Store the scene we're leaving. */
                //System.out.println("Lathing LAST_SCENE, old " + 
                //                   SceneList.MENU.state.vals[Menu.StateMap.LAST_SCENE.idx] +
                //                   ", new " + g.topLvlSceneIdx);
                
                /* If the current scene is already the menu, for example if the program previously
                   exited from the menu screen, don't latch menu into LAST_SCENE, or else we'll not
                   be able to resume from the menu screen. */
                if (g.topLvlSceneIdx != SceneList.MENU.idx){
                    SceneList.MENU.state.vals[Menu.StateMap.LAST_SCENE.idx] = g.topLvlSceneIdx;   
                }
                g.topLvlScene = new Menu();
                break;
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
        
        /* Latch new topLvlSceneIdx */
        g.topLvlSceneIdx = sceneId.idx;
        
        /* Update top level state. */
        SceneList.TOP.state.vals[Top.StateMap.LAST_SCENE_ID.idx] = sceneId.idx;
    }

    public void actionHandler(  BasicBackboneGame2D g, 
                                BasicBackboneGame2D.MouseActions evtType, 
                                int evtX, 
                                int evtY) {
        //System.out.println("SM evt " + evtType + " @ (" + evtX + ", " + evtY + ")");

        // Move player to the location.
        // TODO: Add pathing and transition animations.
        // Also need to add mechanism for interacting with the destination object, and any objects along the way.
        // Interaction could potentially be handled by the scenes themselves through g.player.
        g.player.setLoc(GameFrame.getNativeX(evtX), GameFrame.getNativeY(evtY));

        // Default scene handling.
        g.topLvlScene.actionHandler(g, evtType, evtX, evtY);
    }
}