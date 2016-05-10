package asteroids;

import java.awt.Shape;

/**
 * Creates an array of asteroid explosions
 * 
 * @author Andrew Katsanevas & Nick Lloyd
 *
 */
public class Explosion extends Participant
{
    // Create the member variables
    private ExplosionAsteroid[] parts;
    private long startTime;
    
    // Construct the explosion
    public Explosion() 
    {
        // Initialize parts
        parts = new ExplosionAsteroid[] { null, null, null, null, null, null,
                null, null };
        
        // Set the start time to now
        startTime = System.currentTimeMillis();
    }

    // Set the part
    public void setPart(ExplosionAsteroid piece, int i)
    {
        if (i >= 0 && i < 28)
        {
            parts[i] = piece;
        }
    }

    // Return the part
    public ExplosionAsteroid getPart(int i)
    {
        return parts[i];
    }
    
    // Get the start time of the bullet
    public long getStartTime ()
    {
        return startTime;
    }

    // manually setting start time
    public void setStartTime (long x)
    {
        startTime = x;
    }

    // No outline to return
    @Override
    Shape getOutline ()
    {
        return null;
    }
}
