package asteroids;

import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * Creates a piece of the ship's explosion
 * 
 * @author Andrew Katsanevas & Nick Lloyd
 *
 */
public class ShipExpPiece extends Participant
{
    // Make the outline
    private Shape outline;

    // Set it's start time
    private long startTime;
    
    public ShipExpPiece()
    {
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(0, 15);

        poly.curveTo(-9, 9 * Math.sqrt(3), -9 * Math.sqrt(3), 9, -15, 0);

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
