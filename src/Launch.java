import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Log;

import java.io.IOException;

/* TODO:
    - Replace direct member accesses with getters/setters.
    - Refactor player; player probably shouldn't be derived from scene.
    - Player should start at a fixed position, even after reloading a scene or starting a new game.
    - Text boxes. Maybe handle in GameFrame/Scene since this'll be a type of overlay, always some position in viewport.
    - Move simple scenes to a default template/loader.
    --- Ex) Clock's basic params could be in JSON and a parser could initialize a Scene dynamically.
    --- Default behavior is to interpret file structure as scene / subscene
    - Move top level config to template/loader. Ex) starting scene and window size.
    - Animation update - don't reload image every time the player moves or is scaled.
    - Unit tests.
*/

public class Launch {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Log.setLevel(Log.DEBUG);
            BasicBackboneGame2D game = new BasicBackboneGame2D();
            game.run();
        } catch (IOException ex) {
            Log.error(ex.toString());
        }
    }
}
