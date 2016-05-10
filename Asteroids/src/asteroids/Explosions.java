package asteroids;

/**
 * Creates an array of explosion particle arrays
 * 
 * @author Andrew Katsanevas & Nick Lloyd
 *
 */
public class Explosions
{
    // Create the member variable
    private Explosion[] exps;

    // Construct an explosion
    public Explosions ()
    {
        // Initialize exps
        exps = new Explosion[] { null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null };
    }

    // Set the explosion to the position specified
    public void setExp(Explosion exp, int i)
    {
        if (i >= 0 && i < 28)
        {
            exps[i] = exp;
        }
    }

    // The the explosion from the right position
    public Explosion getExp (int i)
    {
        return exps[i];
    }
}
