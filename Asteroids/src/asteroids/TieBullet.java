package asteroids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * 
 * @author Nick Lloyd & Andrew Katsanevas
 * 
 * Represents a TieBullet
 *
 */

public class TieBullet extends Participant
{
    // Outline of the tie bullet
    private Shape outline;
    
 // Set it's start time
    private long startTime;

    public TieBullet() {
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(1, 0);
        poly.lineTo(1, 2);
        poly.lineTo(-1, 2);
        poly.lineTo(-1, 0);
        poly.closePath();
        outline = poly;
        startTime = System.currentTimeMillis();
        
    }
    
    // Get the start time of the bullet
    public long getStartTime() {
        return startTime;
    }
    
    // manually setting start time
    public void setStartTime(long x) {
        startTime = x;
    }
    
    /**
     * Draws this participant
     */
    @Override
    public void draw (Graphics2D g)
    {
            g.setColor(Color.GREEN);
            super.draw(g);
            g.setColor(Color.WHITE);
    }

    @Override
    Shape getOutline ()
    {
        return outline;
    }
    
}
