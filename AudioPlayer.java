import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * AudioPlayer for playing audio.
 *
 * @author Zachary Lorch
 * @author Jimmy Chen
 * @author Steven Weller
 * @version 12.12.2024
 */
public class AudioPlayer 
{
    public static final int THEME = 0;
    public static final int WAKA  = 1;
    public static final int DEATH = 2;
    public static final int SIREN = 3;
    public static final int WIN = 4;
    
    private Clip clip;
    
    //initialize array to store the sounds used in the game
    URL soundURL[] = new URL[5];
    
    /**
     * Initialize AudioPlayer
     */
    public AudioPlayer() 
    {
        soundURL[0] = getClass().getResource("/sound/Pacman Original Theme.wav");
        soundURL[1] = getClass().getResource("/sound/waka.wav");
        soundURL[2] = getClass().getResource("/sound/death.wav");
        soundURL[3] = getClass().getResource("/sound/siren.wav");
        soundURL[4] = getClass().getResource("/sound/win.wav");
    }

    /**
     * Sets the audio file using the provided audio index.
     * 
     * @param audioIndex
     *      The index of the audio to be set.
     */
    public void setFile(int audioIndex) 
    {
        try 
        {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[audioIndex]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Plays the audio.
     */
    public void play() 
    {
        clip.start();
    }
    
    /**
     * Plays the audio continuously.
     */
    public void loop() 
    {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    /**
     * Stops the audio
     */
    public void stop() 
    {
        clip.close();
        clip.stop();
    }
    
    /**
     * Gets the current status of the AudioPlayer.
     * 
     * @return
     *      True if the audio is currently playing. Otherwise, false.
     */
    public boolean isPlaying() 
    {
        return clip != null && clip.isRunning();
    }
}
