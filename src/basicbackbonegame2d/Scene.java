
package basicbackbonegame2d;

import basicbackbonegame2d.Scenes.Scene1.Scene1;
import basicbackbonegame2d.Scenes.Scene2.Scene2;
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
    public String imgPath;      //file location of this scene's image
    
    /* Dynamic array of any subscenes */
    public List<Scene> subScenes = new ArrayList<>();
    
    /* Dynamic array of Transitions */
    public List<Transition> transitions = new ArrayList<>();
    
    /* Default scene constructor */
    public Scene(){
        
    }
    
    /* Actually update screen with the image in this scene, as well as any images
       from any subscenes. */
    final public void updateScreen(){
        screen.addImg(imgPath, xLoc, yLoc);
        
        for (Scene scn : subScenes) {
            scn.updateScreen();
        }
        
        /* Update cursor as well by issuing a dummy movement event at the current location. */
        if (!isSubscene){
            Point mouseLoc = getPointerInfo().getLocation();
            Point screenLoc = screen.getLocationOnScreen();
            actionHandler(g, 1, mouseLoc.x-screenLoc.x, mouseLoc.y-screenLoc.y);
        }
    }
    
    public void draw(){
        screen.repaint();
    }
    
    /* In case a scene needs to be added during runtime instead of allocated at
       compile time, use this method. */
    final public void addSubScene(Scene subScene){
        subScenes.add(subScene);
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
        
        int sceneId;
        int xLoc;
        int yLoc;
        int width;
        int height;
        
        /* Currently sceneId is only defined here and referenced as a magic number from
           any scenes that call for the transition. Possible area of cleanup/simplification
           later on.
        */
        public Transition(int sId, int x, int y, int w, int h){
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
            switch(sceneId){
                case 0: //Scene1
                    g.topLvlScene = new Scene1();
                    break;
                case 1: //Scene2
                    g.topLvlScene = new Scene2();
                    break;
                default:
                    System.out.println("Invalid sceneId " + sceneId);
                    break;
            }
        }
    }
    
    /* This will be overridden by each individual scene to provide custom action handling. */
    public void uniqueActionHandler(BasicBackboneGame2D g, int evtType, int evtX, int evtY){
        
    }
    
    /* Base action handler, common to all scenens */
    public void actionHandler(BasicBackboneGame2D g, int evtType, int evtX, int evtY) {
        //TODO: convert evtType into enum
        
        /* Handle the event. */ 
        //System.out.println(sceneName + ": evt " + evtType + ", (" + evtX + "," + evtY + ")");   
        
        boolean hit = false; //Flag indicating if any subscene or transition returned true on their
                             //isHit() methods. If not, we'll use the default cursor.
        
        for (Transition t : transitions){
            if(t.isHit(evtX, evtY)){
                if (evtType == 0){
                    t.activate();
                    /* scene has switched, don't process any more in this scene. */
                    return; 
                }
                else {
                    if (!isSubscene){
                        screen.updateCursor(2);
                    }
                }
                hit = true;
            }
        }
        
        /* Check if any subscenes are hit and handle it if they are. */
        for (Scene ss : subScenes) {
            if (ss.isHit(evtX, evtY)) {
                ss.actionHandler(g, evtType, evtX, evtY);
                if (!isSubscene){
                    screen.updateCursor(1);
                }
                hit = true;
            }
        }
        
        if ((!hit) && (!isSubscene)){
            screen.updateCursor(0);
        }
        
        /* Custom handling for this scene. */
        uniqueActionHandler(g, evtType, evtX, evtY);
    }
}
