package asteroids;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * A timer for asteroid explosions
 * 
 * @author Andrew Katsanevas & Nick Lloyd
 *
 */
public class ExpAsteroidTimer implements ActionListener
{
    // For the TieListener
    private ExpAsteroidListener l;
    
    // For the TieFighter
    private ExplosionAsteroid t;
    
    // Create the timer
    private Timer timer;
    
    // Is the game paused?
    private boolean paused;
    
    private double x;
    private double y;
    
    // Construct a new TieFighterTimer
    public ExpAsteroidTimer(ExpAsteroidListener x, ExplosionAsteroid t) 
    {
        this.l = x;
        this.t = t;
        paused = false;
        
        timer = new Timer(1500, this);
        timer.start();
    }
    
    public void start() {
        timer.start();
    }
    
    public void stop() {
        timer.stop();
    }
    
    public void setExpX(int xCoord)
    {
        x = xCoord;
    }
    
    public void setExpY(int yCoord)
    {
        y = yCoord;
    }

    @Override
    public void actionPerformed (ActionEvent e)
    {
        if (!paused) l.AsteroidExplode(t, x, y);
    }
}
