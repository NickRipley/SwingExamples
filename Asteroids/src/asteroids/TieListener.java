package asteroids;

/**
 * 
 * @author Nick Lloyd and Andrew Katsanevas
 * Listens to tiefighters for return times
 *
 */

public interface TieListener
{
    public void tieFighterFire(TieFighter t, boolean advanced);
}
