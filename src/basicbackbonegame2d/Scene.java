
package basicbackbonegame2d;

import basicbackbonegame2d.Scenes.SceneManager;
import basicbackbonegame2d.Scenes.SceneManager.SceneList;
import static java.awt.MouseInfo.getPointerInfo;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

public abstract class Scene {

    /* Reference to top level game object. Used by Transition class to switch topLvlScene. */
    static public BasicBackboneGame2D g;
    
    /* Screen on which all scenes will draw */
    static public GameScreen screen = new GameScreen();
    
    public String sceneName = "default";
    
    public boolean isSubscene;  //flag indicating that this scene is a subscene.
    public int xLoc;            //x location (ex top left corner for rectangular scenes)
    public int yLoc;            //y location
    public int width;           //width
    public int height;          //height
    
    /* File location of this scene's active image. */
    public String imagePath;
    
    /* Array of subscenes */
    public Scene subScenes[];
    public int numSubScenes;
    
    /* Dynamic array of Transitions */
    public List<Transition> transitions = new ArrayList<>();
    
    /* Default scene constructor */
    public Scene(){
        numSubScenes = 0;
    }
    
    /* Actually update screen with the image in this scene, as well as any images
       from any subscenes. */
    final public void updateScreen(){
        screen.addImg(imagePath, xLoc, yLoc);
        
        for (int scnIdx = 0; scnIdx < numSubScenes ; scnIdx++) {
            subScenes[scnIdx].updateScreen();
        }
        
        /* Update cursor as well by issuing a dummy movement event at the current location. */
        if (!isSubscene){
            Point mouseLoc = getPointerInfo().getLocation();
            Point screenLoc = screen.getLocationOnScreen();
            actionHandler(  g, 
                            BasicBackboneGame2D.MouseActions.MOVEMENT, 
                            mouseLoc.x-screenLoc.x, 
                            mouseLoc.y-screenLoc.y);
        }
    }
    
    public void draw(){
        screen.repaint();
    }
    
    public void swapImage(String newImagePath){
        imagePath = newImagePath;
    }
    
    /* Add a transition to this scene. */
    final public void addTransition(Transition t){
        transitions.add(t);
    }
    
    /* Is the (x,y) location within this scene? */
    public boolean isHit(int x, int y){
        x -= xLoc;
        y -= yLoc;
        return (x >= 0) && (x < width) &&
               (y >= 0) && (y < height);
    }    
      
    
    /* Transition inner class - has isHit() and activate(). isHit() will return true
       if the input (x,y) coords are within the transition object. activate() will
       change the topLvlScene of the BasicBackboneGame2D class to the new scene we're
       transitioning to.
    */
    public class Transition{
        
        SceneList sceneId;
        int xLoc;
        int yLoc;
        int width;
        int height;
        
        /* Currently sceneId is only defined here and referenced as a magic number from
           any scenes that call for the transition. Possible area of cleanup/simplification
           later on.
        */
        public Transition(SceneList sId, int x, int y, int w, int h){
            sceneId = sId;
            xLoc = x;
            yLoc = y;
            width = w;
            height = h;
            
        }

        public boolean isHit(int x, int y){
            x -= xLoc;
            y -= yLoc;
            return (x >= 0) && (x < width) &&
                   (y >= 0) && (y < height);
        }
        
        public void activate(){
            /* Scene switching duty is handled within the SceneManager.java file in order
               to keep all scene enums and handoffs in one location. Makes manual editing
               easier. */
            SceneManager.switchScene(g, sceneId);
        }
    }
    
    /* This will be overridden by each individual scene to provide custom action handling. */
    public void uniqueActionHandler(BasicBackboneGame2D g, 
                                    BasicBackboneGame2D.MouseActions evtType, 
                                    int evtX, 
                                    int evtY){
        
    }
    
    /* Base action handler, common to all scenens */
    public void actionHandler(  BasicBackboneGame2D g, 
                                BasicBackboneGame2D.MouseActions evtType, 
                                int evtX, 
                                int evtY) {

        /* Handle the event. */ 
        //System.out.println(sceneName + ": evt " + evtType + ", (" + evtX + "," + evtY + ")");   
       
        
        /* Custom handling for this scene. This gives each scene a chance to custom handle events
           before falling back on the default behavior. */
        uniqueActionHandler(g, evtType, evtX, evtY);
        
        boolean hit = false; //Flag indicating if any subscene or transition returned true on their
                             //isHit() methods. If not, we'll use the default cursor.
        
        for (int tIdx = 0; tIdx < transitions.size(); tIdx++){
            Transition tt = transitions.get(tIdx);
            if(tt.isHit(evtX, evtY)){
                if (evtType == BasicBackboneGame2D.MouseActions.LEFT_BUTTON){
                    tt.activate();
                    /* scene has switched, don't process any more in this scene. */
                    return; 
                }
                else if (evtType == BasicBackboneGame2D.MouseActions.MOVEMENT) {
                    if (!isSubscene){
                        screen.updateCursor(GameScreen.CursorType.TRANSITION);
                    }
                }
                hit = true;
            }
        }
        
        /* Check if any subscenes are hit and handle it if they are. */
        for (int scnIdx = 0; scnIdx < numSubScenes ; scnIdx++) {
            Scene ss = subScenes[scnIdx];
            if (ss.isHit(evtX, evtY)) {
                ss.actionHandler(g, evtType, evtX, evtY);
                if (!isSubscene){
                    screen.updateCursor(GameScreen.CursorType.INSPECTION);
                }
                hit = true;
            }
        }
        
        if ((!hit) && (!isSubscene)){
            screen.updateCursor(GameScreen.CursorType.DEFAULT);
        }
       
    }
}
