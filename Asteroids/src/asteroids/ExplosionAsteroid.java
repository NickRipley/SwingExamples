package asteroids;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Random;

/**
 * Creates a debris particle for asteroid explosions
 * 
 * @author Andrew Katsanevas & Nick Lloyd
 *
 */
public class ExplosionAsteroid extends Participant
{
    // Make the outline
    private Shape outline;

    // Set it's start time
    private long startTime;



    public ExplosionAsteroid (Screen screen)
    {
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(1, 0);
        poly.lineTo(1, 1);
        poly.lineTo(0, 1);
        poly.lineTo(0, 2);
        poly.lineTo(1, 2);
        poly.lineTo(1, 3);
        poly.lineTo(3, 3);
        poly.lineTo(3, 2);
        poly.lineTo(5, 2);
        poly.lineTo(5, 1);
        poly.lineTo(4, 1);
        poly.lineTo(4, 0);
        poly.lineTo(1, 0);
        poly.closePath();
        outline = poly;
        startTime = System.currentTimeMillis();
    }    
    
    // Get the start time 
    public long getStartTime ()
    {
        return startTime;
    }

    // Manually set start time
    public void setStartTime (long x)
    {
        startTime = x;
    }

    @Override
    Shape getOutline ()
    {
        return outline;
    }
}
