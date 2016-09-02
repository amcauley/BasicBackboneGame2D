
package basicbackbonegame2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/* Jukebox encapsulates all of the sounds for the game, including background music and
   any sound effects. Scenes will select what to play via an enum of available sound
   files known to the Jukebox. */
public class Jukebox {
    
    /* Enum of available sound files */
    public enum Sounds{
        NONE(""),   //Can use this as a flag that no sound is expected
        BG_MUSIC0("resources/sounds/BG_Music0.wav"),
        DOOR0("resources/sounds/StringDoor.wav"),
        VICTORY("resources/sounds/Victory.wav");
        
        String fileName;
        
        Sounds(String fn){
            fileName = fn;
        }
    }
    
    class ClipInfo{
        
        /* Name of sound file playing */
        String fileName;
        /* The currently active clip */
        Clip clip;

        ClipInfo(String fn, Clip c){
            fileName = fn;
            clip = c;
        }
    }
    
    /* List of all active clips. */
    List<ClipInfo> clips = new ArrayList<>();
    
    public void JukeBox(){
    
    }
    
    /* Stop playing all clips */
    public void stopAll(){
        /* Stop all clips individually before clearing list */
        for (ClipInfo c : clips){
            c.clip.stop();
        }
        clips.clear();
    }
    
    /* Play a given sound/music, with a flag indicating if
       it should loop. */
    public void play(Sounds sound, boolean loop){
        
        /* Whenever we get a new play request, clean up any clips that are no longer active,
           ex. completed single-use effects. Need to use iterators, otherwise can run into
           concurrent modification issues. */
        Iterator<ClipInfo> it = clips.iterator();
        while(it.hasNext()){
            ClipInfo ci = it.next();
            if (!ci.clip.isActive()){
                //System.out.println("Removing inactive clip " + ci.fileName);
                it.remove();
            }
        }
        
        /* If we received the NONE sound, exit here. */
        if (sound == Sounds.NONE){
            return;
        }
        
        /* If we're already playing this sound, exit. May want to add extra abilities later,
           such as a flag to restart if already playing vs just ignoring new request. */
        for (ClipInfo c : clips){
            if (c.fileName.equals(sound.fileName)){
                //System.out.println(sound.fileName + " already active");
                return;
            }
        }
        
        try {
            Clip newClip = AudioSystem.getClip();
            AudioInputStream inputStream = 
                    AudioSystem.getAudioInputStream(getClass().getClassLoader().getResource(sound.fileName));
            newClip.open(inputStream);
            
            if (loop){
                newClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            else{
                newClip.start();
            }
            
            //System.out.println("Playing " + sound.fileName);
            clips.add(new ClipInfo(sound.fileName, newClip));
            
        } catch (Exception ex){
            System.out.println("Error playing " + sound.fileName + ':');
            System.out.println(ex.getMessage());
        }
    }
}
