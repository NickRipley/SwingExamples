package asteroids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * 
 * @author Nick Lloyd & Andrew Katsanevas
 * 
 * Represents a TieFighter
 *
 */

public class TieFighter extends Participant
{
    // Create the outline
    private Shape outline;
    
    // Create a startTime
    long startTime;
    
    // Is it and Advanced Tie Fighter?
    boolean advanced;
    
    public TieFighter(boolean advanced) {
        
        // Set if it's advanced
        this.advanced = advanced;
        
        // If it's not advanced, this is the outline:
        if (!advanced) {
            Path2D.Double poly = new Path2D.Double();
            poly.moveTo(8,10);
            poly.lineTo(10, 10);
            poly.lineTo(10, -10);
            poly.lineTo(8, -10);
            poly.lineTo(8, -2);
            poly.lineTo(4, -2);
            poly.curveTo(4, -6, -4, -6, -4, -2);
            poly.lineTo(-8, -2);
            poly.lineTo(-8, -10);
            poly.lineTo(-10, -10);
            poly.lineTo(-10, 10);
            poly.lineTo(-8, 10);
            poly.lineTo(-8, 2);
            poly.lineTo(-4, 2);
            poly.curveTo(-4, 6, 4, 6, 4, 2);
            poly.lineTo(8, 2);
            poly.lineTo(8, 10);
            poly.closePath();
            outline = poly;
        }
        
        // If it's advanced, this is the outline:
        else if(advanced) {
            Path2D.Double poly = new Path2D.Double();
            poly.moveTo(5, 9);
            poly.lineTo(7, 9);
            poly.curveTo(14, 9, 14, -9, 7, -9);
            poly.lineTo(5, -9);
            poly.curveTo(8, -9, 11, -2, 8,-2);
            poly.lineTo(4, -2);
            poly.curveTo(4, -6, -4, -6, -4, -2);
            poly.lineTo(-8, -2);
            poly.curveTo(-11, -2, -8, -9, -5, -7);
            poly.lineTo(-7, -9);
            poly.curveTo(-14, -9, -14, 9, -7, 9);
            poly.lineTo(-5, 9);
            poly.curveTo(-8, 9, -11, 2,-8, 2);
            poly.lineTo(-4, 2);
            poly.curveTo(-4, 6, 4, 6, 4, 2);
            poly.lineTo(8, 2);
            poly.curveTo(11, 2, 8, 9, 5, 9);
            poly.closePath();
            outline = poly;
        }
        
        // Set the startTime to now
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Return if it's advanced
     */
    public boolean isAdvanced() {
        return advanced;
    }
    
    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose
     * is located.
     */
    public double getXNose ()
    {
        Point2D.Double point = new Point2D.Double(5, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the y-coordinate of the point on the screen where the ship's nose
     * is located.
     */
    public double getYNose ()
    {
        Point2D.Double point = new Point2D.Double(5, 0);
        transformPoint(point);
        return point.getY();
    }
    
    /**
     * Draws this participant
     */
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
