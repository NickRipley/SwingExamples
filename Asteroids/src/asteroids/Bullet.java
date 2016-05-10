package asteroids;

import java.awt.Shape;
import java.awt.geom.Path2D;
import static asteroids.Constants.*;

/**
 * 
 * @author Nick Lloyd and Andrew Katsanevas
 * 
 * For creating bullet participants
 *
 */

public class Bullet extends Participant
{
    // Make the outline
    private Shape outline;
    
    // Set it's start time
    private long startTime;

    // Construct the bullet
    public Bullet() {
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
    
    
    @Override
    public Shape getOutline ()
    {
        return outline;
    }
}
