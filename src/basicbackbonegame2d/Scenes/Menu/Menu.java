
package basicbackbonegame2d.Scenes.Menu;

import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.GameFrame;
import basicbackbonegame2d.Scene;
import basicbackbonegame2d.Scenes.Menu.ItemSlot0.ItemSlot0;
import basicbackbonegame2d.Scenes.Menu.ItemSlot1.ItemSlot1;
import basicbackbonegame2d.Scenes.Menu.Load.Load;
import basicbackbonegame2d.Scenes.Menu.New_Game.New_Game;
import basicbackbonegame2d.Scenes.Menu.Resume.Resume;
import basicbackbonegame2d.Scenes.Menu.Save.Save;
import basicbackbonegame2d.Scenes.SceneManager;
import basicbackbonegame2d.StateInfo;
import basicbackbonegame2d.Top;
import java.io.IOException;
import javax.swing.JFileChooser;

public class Menu extends Scene{
    
    public static final String SAVE_DIRECTORY = "src\\basicbackbonegame2d\\Saves\\UserSaves";
    public static final String NEW_GAME_FILENAME = "src\\basicbackbonegame2d\\Saves\\NewGameTemplate.txt";
    
    static final int NUM_ITEM_SLOTS = 2;
    
    static StateInfo stateInfo = new Menu.StateInfo_Menu();
    
    public static StateInfo getStateInfo(){
        return stateInfo;
    }    
    
    /* Enum for tracking/defining state info for this scene. */
    public enum StateMap{
        LAST_SCENE(0);    //What scene we were in prior to entering menu
        
        public int idx;
        
        StateMap(int i){
            idx = i;
        }
    }    
    
    public static class StateInfo_Menu extends StateInfo{
    
        public StateInfo_Menu(){
            /* Allocate memory for state */
            vals = new int[StateMap.values().length];
            
            /* Initialize state values. */
            vals[StateMap.LAST_SCENE.idx] = 0;
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
        RESUME(0),
        SAVE(1),
        LOAD(2),
        NEW_GAME(3),
        ITEM_SLOT0(4),  //Put item slots in order at the end of scene map
        ITEM_SLOT1(5);        
        
        int idx;
        
        SubSceneMap(int i){
            idx = i;
        }
    }   
    
    /* Enum of avilable images for this scene (or subscenes) */
    public enum ImagePathMap{
        MENU("src\\basicbackbonegame2d\\Scenes\\Menu\\Menu.jpg"),
        KEY_ICON("src\\basicbackbonegame2d\\Items\\KeyIcon.png"),
        BAUBLE_ICON("src\\basicbackbonegame2d\\Items\\BaubleIcon.png");
        
        public String str;
        
        ImagePathMap(String s){
            str = s;
        }
    }    
    
    public Menu(){
        /* Basic initialization params */     
        sceneName = "Menu";
        isSubscene = false;
        xLoc = 0;
        yLoc = 0;
        width = GameFrame.NOMINAL_WIDTH;
        height = GameFrame.NOMINAL_HEIGHT;
        
        /* Initialize this scene's image */
        imagePath = ImagePathMap.MENU.str;         
        
        /* Reset screen */        
        screen.clearImgs();
                
        /* Create any subscenes and add to array */
        numSubScenes = SubSceneMap.values().length;
        subScenes = new Scene[numSubScenes];
        
        subScenes[SubSceneMap.RESUME.idx] = new Resume();
        subScenes[SubSceneMap.SAVE.idx] = new Save();
        subScenes[SubSceneMap.LOAD.idx] = new Load();
        subScenes[SubSceneMap.NEW_GAME.idx] = new New_Game();
        
        /* Populate item slots */
        assert(SubSceneMap.values().length - SubSceneMap.ITEM_SLOT0.idx == NUM_ITEM_SLOTS);
        
        int cntCheck = 0; //Just to help check that all item slots are accounted for in our copy/pasting.
        
        subScenes[SubSceneMap.ITEM_SLOT0.idx] = new ItemSlot0(); 
        subScenes[SubSceneMap.ITEM_SLOT0.idx].setActiveState(false); cntCheck++;
        subScenes[SubSceneMap.ITEM_SLOT1.idx] = new ItemSlot1(); 
        subScenes[SubSceneMap.ITEM_SLOT1.idx].setActiveState(false); cntCheck++;
        
        assert(cntCheck == NUM_ITEM_SLOTS);
        
        /* Now add in all the items that the player has. */
        
        cntCheck = SubSceneMap.ITEM_SLOT0.idx;
        
        if (SceneManager.SceneList.TOP.state.vals[Top.StateMap.HAS_KEY.idx] != 0){
            subScenes[cntCheck].swapImage(ImagePathMap.KEY_ICON.str);
            subScenes[cntCheck++].setActiveState(true);
        }
        if (SceneManager.SceneList.TOP.state.vals[Top.StateMap.HAS_BAUBLE.idx] != 0){
            subScenes[cntCheck].swapImage(ImagePathMap.BAUBLE_ICON.str);
            subScenes[cntCheck++].setActiveState(true);
        }
        
        assert(cntCheck - SubSceneMap.ITEM_SLOT0.idx <= NUM_ITEM_SLOTS);
        
        
        /* Add any starting transitions */
        //No transitions
        
        /* Music handling (if any) */
        //g.jukebox.stopAll();
        
        System.out.println("Menu last scene ID: " + stateInfo.vals[StateMap.LAST_SCENE.idx]);
        
        /* Standard scene drawing routines for top level scenes */        
        updateScreen();
        draw();
    }
   
    @Override
    public int uniqueActionHandler(  BasicBackboneGame2D g, 
                                     BasicBackboneGame2D.MouseActions evtType, 
                                     int evtX, 
                                     int evtY) {
        
        if ( (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) && 
              subScenes[SubSceneMap.RESUME.idx].isHit(evtX, evtY) ){
            
            SceneManager.switchScene(g, SceneManager.SceneList.values()[stateInfo.vals[StateMap.LAST_SCENE.idx]]);
        }
        else if ( (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) && 
              subScenes[SubSceneMap.SAVE.idx].isHit(evtX, evtY) ){
            
            JFileChooser fc = new JFileChooser(SAVE_DIRECTORY);
            
            int fcRet = fc.showSaveDialog(g.gameFrame);
            
            if (fcRet == JFileChooser.APPROVE_OPTION){
                String fileName = fc.getSelectedFile().getAbsolutePath();
                g.sm.saveState(fileName);
            } else {
                System.out.println("Save aborted");
            }      
            
        }        
        else if ( (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) && 
              subScenes[SubSceneMap.LOAD.idx].isHit(evtX, evtY) ){

            JFileChooser fc = new JFileChooser(SAVE_DIRECTORY);
            
            int fcRet = fc.showOpenDialog(g.gameFrame);
            
            if (fcRet == JFileChooser.APPROVE_OPTION){
                String fileName = fc.getSelectedFile().getAbsolutePath();
                try {
                    g.sm.loadState(fileName);
                    /* State is loaded, now update topLvlScene based on loaded state. */
                    g.topLvlSceneIdx = stateInfo.vals[Top.StateMap.LAST_SCENE_ID.idx];
                    SceneManager.switchScene(g, SceneManager.SceneList.values()[g.topLvlSceneIdx]);
                } catch (IOException ex) {
                    System.out.println("File load error:");
                    System.out.println(ex.getMessage());
                }
            } else {
                System.out.println("Load aborted");
            }            
            
        }        
        else if ( (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON) && 
              subScenes[SubSceneMap.NEW_GAME.idx].isHit(evtX, evtY) ){
           
            try {
                g.sm.loadState(NEW_GAME_FILENAME);
                /* State is loaded, now update topLvlScene based on loaded state. */
                g.topLvlSceneIdx = stateInfo.vals[Top.StateMap.LAST_SCENE_ID.idx];
                SceneManager.switchScene(g, SceneManager.SceneList.values()[g.topLvlSceneIdx]);
            } catch (IOException ex) {
                System.out.println("New game error:");
                System.out.println(ex.getMessage());
            }            
   
        }        
        
        /* No further processing. */
        return 0;
      
    }    
    
}
