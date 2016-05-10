package asteroids;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * 
 * @author Nick Lloyd and Andrew Katsanevas
 *
 */

public class TieFighterTimer implements ActionListener
{
    // For the TieListener
    private TieListener x;
    
    // For the TieFighter
    private TieFighter t;
    
    // Create the timer
    private Timer timer;
    
    // Is the game paused?
    private boolean paused;
    
    // Is the tie advanced?
    private boolean advanced;
    
    // Construct a new TieFighterTimer
    public TieFighterTimer(TieListener x, TieFighter t, boolean advanced) {
        this.x = x;
        this.t = t;
        paused = false;
        this.advanced = advanced;
        
        timer = new Timer(1500, this);
        timer.start();
    }
    
    // Set to advanced
    public void setAdvanced() {
        advanced = true;
    }
    
    // Set to not advanced
    public void setNotAdvanced() {
        advanced = false;
    }
    
    // Start the timer
    public void start() {
        timer.start();
    }
    
    // Stop the timer
    public void stop() {
        timer.stop();
    }

    // When the timer runs out, FIRE!
    @Override
    public void actionPerformed (ActionEvent e)
    {
        if (!paused) x.tieFighterFire(t, advanced);
    }
}
