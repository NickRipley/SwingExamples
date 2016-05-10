package asteroids;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 * 
 * @author Nick Lloyd and Andrew Katsanevas
 * 
 * Listens for the music to be finished.
 *
 */

public class MusicListener extends PlaybackListener
{
    // Add member variables
    private MusicListen musicL;
    private AdvancedPlayer playmp3;
    
    public MusicListener(MusicListen y, AdvancedPlayer x) {
        // Set the member variables
        musicL = y;
        playmp3 = x;
    }
    public void playbackFinished(PlaybackEvent evt)
    {
        // When it's done, call the MusicListen method
         musicL.itsDone(playmp3);
    }
}
