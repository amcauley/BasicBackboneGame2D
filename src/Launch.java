import basicbackbonegame2d.BasicBackboneGame2D;
import basicbackbonegame2d.Log;

import java.io.IOException;

/* TODO:
    - Replace some direct member accesses with getters/setters
    - Refactor player; player shouldn't be derived from scene.
    - Player should start at a fixed position, even after reloading a scene or starting a new game.
    - Rework positioning. Need to be able to track and convert between:
    --- Window location, i.e. relative to the game window
    --- Viewport location, i.e. position within the viewport (non-padding subsection of the window, i.e. actual game content)
    --- Scene location. Viewport might only show a small part of entire scene (ex. if camera follows player).
        Track location within the overall scene.
    - Text boxes. Maybe handle in GameFrame/Scene since this'll be a type of overlay, always some position in viewport.
    - Move simple scenes to a default template/loader.
    --- Ex) Clock's basic params could be in JSON and a parser could initialize a Scene dynamically.
    --- Default behavior is to interpret file structure as scene / subscene
    - Move top level config to template/loader. Ex) starting scene and window size.
    - Animation update - don't reload image every time the player moves or is scaled.
    - Investigate save/load system. Don't remember which parts are working.
    - Unit tests.
*/

public class Launch {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Log.setLevel(Log.INFO);
            BasicBackboneGame2D game = new BasicBackboneGame2D();
            game.run();
        } catch (IOException ex) {
            Log.error(ex.toString());
        }
    }
}
