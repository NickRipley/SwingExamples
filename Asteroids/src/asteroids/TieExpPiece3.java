package asteroids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * Creates a piece of the tie fighter's explosion
 * 
 * @author Andrew Katsanevas & Nick Lloyd
 *
 */
public class TieExpPiece3 extends Participant
{
    // Make the outline
    private Shape outline;

    // Set it's start time
    private long startTime;
    
    boolean advanced;
    
    public TieExpPiece3(boolean advanced)
    {
        this.advanced = advanced;
        Path2D.Double poly = new Path2D.Double();
        if(!advanced)
        {       
            poly.moveTo(-4,-2);
            poly.lineTo(-8, -2);
            poly.lineTo(-8, -10);
            poly.lineTo(-10, -10);
            poly.lineTo(-10, 10);
            poly.lineTo(-8, 10);
            poly.lineTo(-8, 2);
            poly.lineTo(-4, 2);
        }
        else if(advanced)
        {
            poly.moveTo(-5,-7);
            poly.lineTo(-7, -9);
            poly.curveTo(-14, -9, -14, 9, -7, 9);
            poly.lineTo(-5, 9);
            poly.curveTo(-8, 9, -11, 2,-8, 2);
        }
        
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
    public void draw (Graphics2D g)
    {
        g.setColor(Color.RED);
        super.draw(g);
        g.setColor(Color.WHITE);
    }

    @Override
    Shape getOutline ()
    {
        return outline;
    }
}
