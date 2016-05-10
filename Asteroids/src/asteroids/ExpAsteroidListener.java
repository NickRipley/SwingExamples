package asteroids;

/**
 * An interface for creating asteroid explosions in the controller
 * 
 * @author Andrew Katsanevas & Nick Lloyd
 *
 */
public interface ExpAsteroidListener
{
    public void AsteroidExplode(ExplosionAsteroid t, double x, double y);
}
