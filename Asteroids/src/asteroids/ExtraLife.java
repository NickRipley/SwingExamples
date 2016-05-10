package asteroids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * 
 * @author Nick Lloyd and Andrew Katsanevas
 * 
 * Creates an extra life to be captured!
 *
 */

public class ExtraLife extends Participant
{   
    // The Outline
    private Shape outline;
    
    // Set the start time
    private long startTime;
    
    // construct a new Extra Life
    public ExtraLife() {
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(15, 0);
        poly.curveTo(9, 9, 10, 7, 0, 15);
        poly.curveTo(-9, 9, -10, 7, -15, 0);
        poly.curveTo(-25, -20, 0, -15, 0, -5);
        poly.curveTo(0, -15, 25, -20, 15, 0);
        poly.closePath();
        outline = poly;
        
        startTime = System.currentTimeMillis();
    }
    
    // Get the start time
    public long getStart() {
        return startTime;
    }
    
    // Manual Start Time set
    public void setStart(long x) {
        startTime = x;
    }
    
    @Override
    public void draw (Graphics2D g)
    {
        g.setColor(Color.PINK);
        super.draw(g);
        g.setColor(Color.WHITE);
    }
    
    @Override
    Shape getOutline ()
    {
        return outline;
    }

}
