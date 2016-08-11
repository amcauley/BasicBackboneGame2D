
package basicbackbonegame2d;

import java.util.List;
import java.util.ArrayList;

public abstract class Scene {

    /* Screen on which all scenes will draw */
    static public GameScreen screen = new GameScreen();
    
    public String sceneId = "default";
    
    public int xLoc;       //x location (ex top left corner for rectangular scenes)
    public int yLoc;       //y location
    public int width;      //width
    public int height;     //height
    public String imgPath; //file location of this scene's image
    
    /* Dynamic array of any subscenes */
    public List<Scene> subScenes = new ArrayList<Scene>();
    
    /* Default scene constructor */
    public Scene(){
        
    }
    
    /* Constructor with an input image path. Don't add the image to the screen,
       otherwise every scene we initialize will automatically add itself to the
       (static) screen. Instead, use the updateScreen() method below for updating
       screen. */
    public Scene(String sceneName, String imgStr, int xx, int yy, int w, int h){
        sceneId = sceneName;
        imgPath = imgStr;
        xLoc = xx;
        yLoc = yy;
        width = w;
        height = h;
    }
    
    /* Actually update screen with the image in this scene, as well as any images
       from any subscenes. */
    final public void updateScreen(){
        screen.addImg(imgPath, xLoc, yLoc);
        
        for (Scene scn : subScenes) {
            scn.updateScreen();
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
    
    /* Is the (x,y) location within this scene? */
    public boolean isHit(int x, int y){
        x -= xLoc;
        y -= yLoc;
        return (x >= 0) && (x < width) &&
               (y >= 0) && (y < height);
    }    
    
    /* Default action handler - subclasses should probably override this. */
    public void actionHandler(BasicBackboneGame2D g, int evtType, int evtX, int evtY) {
        //TODO: convert evtType into enum
        
        /* Handle the event. */ 
        System.out.println(sceneId + ": evt " + evtType + ", (" + evtX + "," + evtY + ")");   
        
        /* Check if any subscenes are hit and handle it if they are. */
        for (Scene ss : subScenes) {
            if (ss.isHit(evtX, evtY)) {
                ss.actionHandler(g, evtType, evtX, evtY);
            }
        }
    }
}
