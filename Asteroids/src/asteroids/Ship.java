package asteroids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.*;

/**
 * Represents ship objects
 * 
 * @author Joe Zachary & Nick Lloyd & Andrew Katsanevas
 */
public class Ship extends Participant
{
    
    // The outline of the ship
    private Shape outline;
    
    // For invulnerability
    private boolean invulnerable;

    // Constructs a ship
    public Ship ()
    {
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(0, 15);
        poly.curveTo(-20, 15, -20, -15, 0, -15);
        poly.lineTo(20, -8);
        poly.lineTo(20, -3);
        poly.lineTo(8, -3);
        poly.lineTo(8, 3);
        poly.lineTo(20, 3);
        poly.lineTo(20, 8);
        poly.lineTo(11, 11);
        poly.lineTo(15, 11);
        poly.lineTo(15, 15);
        poly.lineTo(0, 15);
        poly.closePath();
        outline = poly;
        invulnerable = false;
    }
    
    public void setInvulnerable() {
        invulnerable = true;
    }
    
    public void setVulnerable() {
        invulnerable = false;
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose
     * is located.
     */
    public double getXNose ()
    {
        Point2D.Double point = new Point2D.Double(10, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the y-coordinate of the point on the screen where the ship's nose
     * is located.
     */
    public double getYNose ()
    {
        Point2D.Double point = new Point2D.Double(15, 0);
        transformPoint(point);
        return point.getY();
    }

    /**
     * Returns the outline of the ship.
     */
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
        super.move();
        friction();
    }
    
    /**
     * Draws this participant
     */
    @Override
    public void draw (Graphics2D g)
    {
        if (invulnerable) {
            g.setColor(Color.ORANGE);
            g.draw(border);
            g.setColor(Color.WHITE);
        }
        else super.draw(g);
        
    }
}