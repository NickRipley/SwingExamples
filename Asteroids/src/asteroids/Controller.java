package asteroids;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.*;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TimerTask;

import javax.swing.*;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import static asteroids.Constants.*;

/**
 * Controls a game of asteroids
 * 
 * @author Joe Zachary & Nicholas Lloyd (u0949261) & Andrew Katsanevas (u0901239)
 * 
 */
public class Controller implements CollisionListener, ActionListener,
        KeyListener, CountdownTimerListener, MusicListen, TieListener,
        ExpAsteroidListener
{
    // For the extra life
    private ExtraLife extra1;

    // Best score so far
    private int bestScore;

    // Number of collisions
    private int collisions;

    // To pause the game
    private boolean pause;
    private long pauseStart;

    // Create the score
    private int score = 0;

    // Is a key pressed?
    private boolean upPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean qPressed;
    private boolean wPressed;
    private boolean ePressed;

    // bullets
    private int bullets;
    private Bullet[] bullet;
    private int tieBullets;
    private TieBullet[] tieBullet;

    // For ship explosions
    private Participant[] shipExp = new Participant[] { null, null, null, null };
    private Participant[] tieExp = new Participant[] { null, null, null, null };

    // Shared random number generator
    private Random random;

    // The ship (if one is active) or null (otherwise)
    private Ship ship;

    // When this timer goes off, it is time to refresh the animation
    private Timer refreshTimer;

    // Count of how many transitions have been made. This is used to keep two
    // conflicting transitions from being made at almost the same time.
    private int transitionCount;

    // Number of lives left
    private int lives;

    // Create the JLabels for lives and score
    private JLabel life;
    private JLabel scoreLabel;
    private JLabel bestScoreLabel;

    // The Game and Screen objects being controlled
    private Game game;
    private Screen screen;

    // Level
    private int level;

    // To make the ship invulnerable for a couple seconds after creation
    private boolean invulnerable;
    private boolean invulnerableCheat;
    private long invulnerableTimer;

    // For the music and sound effects
    private FileInputStream play;
    private AdvancedPlayer playMP3;
    private int playCount;

    // One TieFighter at a time!
    private TieFighter tie;
    private TieFighterTimer forTie;
    private boolean unleashed;

    // To hold explosions
    private Explosions explosions = new Explosions();

    // Has the player cheated?
    private boolean cheat;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller (Game game, Screen screen, JLabel lives, JLabel score,
            JLabel bestScoreLab)
    {
        // Set the keys
        upPressed = false;
        leftPressed = false;
        rightPressed = false;
        qPressed = false;
        wPressed = false;
        ePressed = false;

        // Set the initial state of the ship
        invulnerable = false;
        invulnerableCheat = false;

        // Set the bullet counts
        bullets = 0;
        tieBullets = 0;
        bullet = new Bullet[] { null, null, null, null, null, null, null, null };
        tieBullet = new TieBullet[] { null, null, null, null, null, null, null,
                null };

        // Set the member variables
        life = lives;
        scoreLabel = score;
        bestScoreLabel = bestScoreLab;

        // Record the game and screen objects
        this.game = game;
        this.screen = screen;

        // Set the bestScore
        bestScore = 0;

        // Initialize the random number generator
        random = new Random();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);
        transitionCount = 0;

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        refreshTimer.start();

        // Set to unpaused
        pause = false;

        // Set extra1
        extra1 = null;

        // No tie fighter has been unleashed yet
        unleashed = false;

        // Start the audio
        try
        {
            play = new FileInputStream(
                    "Assets/Star Wars V - The Asteroid Field.mp3");
            playMP3 = new AdvancedPlayer(play);
        }
        catch (Exception e)
        {
            System.out.print("whoops...");
        }
        MusicListener forthis = new MusicListener(this, playMP3);
        playMP3.setPlayBackListener(forthis);
        playCount = 1;
        audio();
        audioEffects("Assets/Never tell me the odds.mp3");
    }

    // Sound Effects for the game
    public void audioEffects (String clipLocation)
    {
        try
        {
            FileInputStream toPlay = new FileInputStream(clipLocation);
            final AdvancedPlayer player = new AdvancedPlayer(toPlay);
            new Thread(new Runnable()
            {
                // Play in a new thread
                public void run ()
                {
                    try
                    {
                        player.play();
                    }
                    catch (JavaLayerException e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        catch (Exception e)
        {
            System.out.print("whoops...");
        }
    }

    // Background music
    public void audio ()
    {
        // Do it in a new thread
        new Thread(new Runnable()
        {
            public void run ()
            {
                try
                {
                    playMP3.play();
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        }).start();
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Make sure screen displays the proper legend
        screen.splashTrue();
        
        // Clear the screen and display the legend
        screen.clear();
        screen.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();

        // Make sure there's no ship
        ship = null;

        // Play the intro
        audioEffects("Assets/Never tell me the odds.mp3");

    }

    /**
     * Get the number of transitions that have occurred.
     */
    public int getTransitionCount ()
    {
        return transitionCount;
    }

    /**
     * The game is over. Displays a message to that effect and enables the start
     * button to permit playing another game.
     */
    private void finalScreen ()
    {
        // Chewbacca says: Game Over
        audioEffects("Assets/Chewbacca.mp3");

        // Remove any high scores left on the screen
        if (extra1 != null)
        {
            screen.removeParticipant(extra1);
            extra1 = null;
        }

        // Make sure screen shows the high scores
        screen.setEnd();

        // If player cheated, they can't get a high score
        if (!cheat)
            screen.checkScore(score);
        cheat = false;

        // Show the game over status
        screen.setLegend(GAME_OVER);

        // Turn off the listeners
        screen.removeCollisionListener(this);
        screen.removeKeyListener(this);
    }

    /**
     * Places four asteroids near the corners of the screen. Gives them random
     * velocities and rotations.
     */
    private void placeAsteroids ()
    {
        Participant a = new Asteroid(0, 2, EDGE_OFFSET, EDGE_OFFSET);
        a.setVelocity(2 + level, random.nextDouble() * 2 * Math.PI);
        a.setRotation(2 * Math.PI * random.nextDouble());
        screen.addParticipant(a);

        a = new Asteroid(1, 2, SIZE - EDGE_OFFSET, EDGE_OFFSET);
        a.setVelocity(2 + level, random.nextDouble() * 2 * Math.PI);
        a.setRotation(2 * Math.PI * random.nextDouble());
        screen.addParticipant(a);

        a = new Asteroid(2, 2, EDGE_OFFSET, SIZE - EDGE_OFFSET);
        a.setVelocity(2 + level, random.nextDouble() * 2 * Math.PI);
        a.setRotation(2 * Math.PI * random.nextDouble());
        screen.addParticipant(a);

        a = new Asteroid(3, 2, SIZE - EDGE_OFFSET, SIZE - EDGE_OFFSET);
        a.setVelocity(2 + level, random.nextDouble() * 2 * Math.PI);
        a.setRotation(2 * Math.PI * random.nextDouble());
        screen.addParticipant(a);
    }

    /**
     * For placing a TieFighter on the screen
     */
    private void releaseTieFighter ()
    {

        // Generate some random numbers to determine what corner tie will start
        // from
        Random generator = new Random();
        int one = generator.nextInt();
        int two = generator.nextInt();

        // Possible start position
        int x = 1;
        int y = 1;

        // For even numbered levels, release a regular tie fighter
        if (level % 2 == 0)
        {
            tie = new TieFighter(false);
            if (forTie != null)
                forTie.start();
            else
                forTie = new TieFighterTimer(this, tie, false);
            forTie.setNotAdvanced();

            // Use the random numbers to determine what corner
            if (one % 2 == 0 && two % 2 == 0)
            {
                x = SIZE - 1;
                y = 1;
            }
            if (one % 2 != 0 && two % 2 == 0)
            {
                x = SIZE - 1;
                y = SIZE - 1;
            }
            if (one % 2 == 0 && two % 2 != 0)
            {
                x = 1;
                y = SIZE - 1;
            }
            tie.setPosition(x, y);
            
            // Randomly determine the direction of the tie fighter
            if (one % 2 == 0)
                tie.setVelocity(5, generator.nextInt());
            else
                tie.setVelocity(5, -generator.nextInt());
            screen.addParticipant(tie);
            unleashed = true;
        }

        // For uneven levels, release a tie fighter advanced
        else
        {
            tie = new TieFighter(true);
            if (forTie != null)
                forTie.start();
            else
                forTie = new TieFighterTimer(this, tie, false);
            forTie.setAdvanced();

            // Use the random numbers to determine what corner
            if (one % 2 == 0 && two % 2 == 0)
            {
                x = SIZE - 1;
                y = 1;
            }
            if (one % 2 != 0 && two % 2 == 0)
            {
                x = SIZE - 1;
                y = SIZE - 1;
            }
            if (one % 2 == 0 && two % 2 != 0)
            {
                x = 1;
                y = SIZE - 1;
            }
            tie.setPosition(x, y);
            
            // Randomly determine the direction of the tie fighter advanced
            if (one % 2 == 0)
                tie.setVelocity(5, generator.nextInt());
            else
                tie.setVelocity(5, -generator.nextInt());
            screen.addParticipant(tie);
            unleashed = true;
        }
    }

    /**
     * Set things up and begin a new game.
     */
    private void initialScreen ()
    {
        // It's not the splash screen
        screen.splashFalse();

        // Clear the screen
        screen.clear();

        // Set level to 1
        level = 1;

        // Place four asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // Set collisions to 0
        collisions = 0;

        // Reset statistics
        lives = 3;
        life.setText("Lives: " + lives);

        // Start listening to events. In case we're already listening, take
        // care to avoid listening twice.
        screen.removeCollisionListener(this);
        screen.removeKeyListener(this);
        screen.addCollisionListener(this);
        screen.addKeyListener(this);

        // Remove any Tie Fighter bullets still on screen
        for (int x = 0; x < 8; x++)
        {
            if (tieBullet[x] != null
                    && System.currentTimeMillis() - tieBullet[x].getStartTime() > BULLET_DURATION)
            {
                screen.removeParticipant(tieBullet[x]);
                tieBullet[x] = null;
                tieBullets--;
            }
        }

        // Make sure tie is null
        tie = null;

        // Give focus to the game screen
        screen.requestFocusInWindow();
    }

    /**
     * Place a ship in the center of the screen.
     */
    private void placeShip ()
    {
        // Make sure there isn't already a ship
        if (ship == null) {
            ship = new Ship();
        }
        
        // Position the ship in the middle of the screen
        ship.setPosition(SIZE / 2, SIZE / 2);
        
        // Set the ship rotation to forward
        ship.setRotation(-Math.PI / 2);
        
        // Set the ship to invulnerable for the first three seconds
        invulnerable = true;
        ship.setInvulnerable();
        
        // Set a time on the invulnerability
        invulnerableTimer = System.currentTimeMillis();
        
        // Add the ship to the screen
        screen.addParticipant(ship);
    }

    /**
     * Fires a ship bullet.
     */
    private void fireBullet ()
    {
        
        // Only fire a bullet if there are less than eight onscreen
        if (ship != null && bullets < 8) {
            
            // Cycle through available slots and determine if one is open
            for (int x = 0; x < 8; x++)
            {
                if (bullet[x] == null)
                {
                    // If it is, place a bullet in it and place it onscreen
                    Bullet b = new Bullet();
                    b.setPosition(ship.getXNose(), ship.getYNose());
                    b.setVelocity(BULLET_SPEED, ship.getRotation());
                    bullet[x] = b;
                    screen.addParticipant(bullet[x]);
                    
                    // Get out of the "for" loop
                    break;
                }
            }
            
            // increase the bullet count
            bullets++;
            
            // Play the blaster sound
            audioEffects("Assets/blaster.mp3");
        }
    }

    /**
     * Fires a bullet from the tie fighter.
     */
    private void tieFireBullet (boolean advanced)
    {
        // Make sure the tie is onscreen and fire a bullet if there are less than 8 onscreen
        if (tie != null && tieBullets < 8)
        {
            // Create a random generator for directions
            Random generator = new Random();
            
            // Is the direction to be positive or negative?
            boolean plus = generator.nextInt() % 2 == 0;
            
            // Housing for the direction
            int direction;
            
            // Make the direction positive or negative, randomly
            if (plus)
                direction = generator.nextInt();
            else
                direction = -generator.nextInt();
            
            // Cycle through bullet spaces to find an open one
            for (int x = 0; x < 8; x++)
            {
                // If open, place a bullet in it and put it onscreen
                if (tieBullet[x] == null)
                {
                    // If the tie isn't advanced, fire randomly
                    if (!advanced && ship != null)
                    {
                        // Create it
                        TieBullet b = new TieBullet();
                        b.setPosition(tie.getX(), tie.getY());
                        b.setVelocity(BULLET_SPEED, direction);
                        tieBullet[x] = b;
                        screen.addParticipant(tieBullet[x]);
                        
                        // Get out of the "for" loop
                        break;
                    }
                    
                    // If the tie is advanced, fire at the ship
                    else if (advanced && ship != null && tie != null)
                    {
                        TieBullet b = new TieBullet();
                        b.setPosition(tie.getX(), tie.getY());
                        // The arctangent of the differences in Y and X positions is the angle
                        b.setVelocity(
                                BULLET_SPEED,
                                Math.atan2((ship.getY() - tie.getY()),
                                        (ship.getX() - tie.getX())));
                        tieBullet[x] = b;
                        screen.addParticipant(tieBullet[x]);
                        
                        // Get out of the "for" loop
                        break;
                    }
                }
            }
            // Increase the tie bullet count
            tieBullets++;

            // Make the sound!
            if (ship != null)
                audioEffects("Assets/TIEFire.mp3");
        }
    }

    /**
     * Place an ExtraLife
     */
    private void placeExtra ()
    {
        // Make sure there isn't already an extra life onscreen
        if (extra1 == null) {
            
            // Create the extra life
            ExtraLife extra = new ExtraLife();
            
            // Create a random generator
            Random generator = new Random();
            
            // Set the position of the extra life to be random
            extra.setPosition(generator.nextInt(screen.getWidth()),
                    generator.nextInt(screen.getHeight()));
            
            // Point the member variable at it
            extra1 = extra;
            
            // Add the extra life to the screen
            screen.addParticipant(extra1);
        }
    }

    /**
     * Deal with collisions between participants.
     */
    @Override
    public void collidedWith (Participant p1, Participant p2)
    {
        //If it's a ship & Asteroid
        if (p1 instanceof Asteroid && p2 instanceof Ship && !invulnerable
                && !invulnerableCheat) {
            shipCollision((Ship) p2);
            asteroidCollision((Asteroid) p1);
            collisions++;
        }
        
        // If it's a ship and asteroid
        else if (p1 instanceof Ship && p2 instanceof Asteroid && !invulnerable
                && !invulnerableCheat) {
            shipCollision((Ship) p1);
            asteroidCollision((Asteroid) p2);
            collisions++;
        }
        
        // If it's an asteroid and bullet
        else if (p1 instanceof Asteroid && p2 instanceof Bullet) {
            asteroidCollision((Asteroid) p1);
            bulletCollision((Bullet) p2);
            collisions++;
        }
        
        // If it's a bullet and asteroid
        else if (p1 instanceof Bullet && p2 instanceof Asteroid) {
            asteroidCollision((Asteroid) p2);
            bulletCollision((Bullet) p1);
            collisions++;
        }
        
        // If it's an extralife and ship
        else if (p1 instanceof ExtraLife && p2 instanceof Ship) {
            screen.removeParticipant(p1);
            extra1 = null;
            lives++;
            life.setText("Lives: " + lives);
        }
        
        // If it's a ship and extralife
        else if (p1 instanceof Ship && p2 instanceof ExtraLife) {
            screen.removeParticipant(p2);
            extra1 = null;
            lives++;
            life.setText("Lives: " + lives);
        }
        
        // If it's a ship and tiefighter
        else if (p1 instanceof Ship && p2 instanceof TieFighter
                && !invulnerable && !invulnerableCheat) {
            tieFighterCollision((TieFighter) p2);
            shipCollision((Ship) p1);
        }
        
        // If it's a tiefighter and ship
        else if (p1 instanceof TieFighter && p2 instanceof Ship
                && !invulnerable && !invulnerableCheat) {
            tieFighterCollision((TieFighter) p1);
            shipCollision((Ship) p2);
        }
        
        // If it's a bullet and tiefighter
        else if (p1 instanceof Bullet && p2 instanceof TieFighter) {
            tieFighterCollision((TieFighter) p2);
            bulletCollision((Bullet) p1);
        }
        
        // If it's a tiefighter and bullet
        else if (p1 instanceof TieFighter && p2 instanceof Bullet) {
            tieFighterCollision((TieFighter) p1);
            bulletCollision((Bullet) p2);
        }
        
        // If it's a tiebullet and ship
        else if (p1 instanceof TieBullet && p2 instanceof Ship && !invulnerable
                && !invulnerableCheat) {
            shipCollision((Ship) p2);
            tieBulletCollision((TieBullet) p1);
        }
        
        // If it's a ship and tiebullet
        else if (p1 instanceof Ship && p2 instanceof TieBullet && !invulnerable
                && !invulnerableCheat) {
            shipCollision((Ship) p1);
            tieBulletCollision((TieBullet) p2);
        }
        
        // If all the asteroids are gone, start a new level
        if (collisions == 28) {
            newLevel();
        }
    }

    // Make a new level
    private void newLevel ()
    {
        // Remove any explosions
        removeAllExp();
        
        // Remove the ship
        screen.removeParticipant(ship);
        ship = null;
        
        // Increase the level
        level++;
        
        // Make the collisions count 0
        collisions = 0;
        
        // Display a legend and make it disappear in one second
        screen.setLegend("Level " + level);
        new CountdownTimer(this, null, 1000);
        
        // Place a ship
        placeShip();
        
        // Place the asteroids
        placeAsteroids();
        
        // If there's still a tiefighter onscreen, destroy it
        if (tie != null) tieFighterCollision(tie);
        unleashed = false;

        // Remove TieFighter bullets
        for (int x = 0; x < 8; x++)
        {
            screen.removeParticipant(tieBullet[x]);
            tieBullet[x] = null;
            tieBullets--;
        }

        // Make sure that bullets disappear as necessary
        for (int x = 0; x < bullet.length; x++)
        {
            screen.removeParticipant(bullet[x]);
            bullet[x] = null;
            bullets--;
        }

        // Refresh the explosions
        explosions = new Explosions();
    }

    /**
     * For Tie Fighter collisions
     */
    private void tieFighterCollision (TieFighter t)
    {
        // Play the clip
        if (tie.isAdvanced())
            audioEffects("Assets/Dont get cocky.mp3");
        else
            audioEffects("Assets/Great shot.mp3");

        tieExp(t.getX(), t.getY(), t);

        // Stop the clock
        forTie.stop();

        // Update the score
        if (!tie.advanced)
            score += 150;
        else
            score += 250;
        scoreLabel.setText("| Score: " + score);

        // Remove the Tie Fighter
        screen.removeParticipant(tie);
        tie = null;
        t = null;

        // Remove TieFighter bullets
        for (int x = 0; x < 8; x++)
        {
            screen.removeParticipant(tieBullet[x]);
            tieBullet[x] = null;
            tieBullets--;
        }

    }

    /**
     * The ship has collided with something
     */
    private void shipCollision (Ship s)
    {
        // Play the sound
        audioEffects("Assets/ShipExploding.mp3");

        shipExp(s.getX(), s.getY());

        // Decrement lives
        lives--;
        if (collisions != 27)
        {
            // Remove the ship from the screen and null it out
            screen.removeParticipant(s);
            ship = null;

            // Display a legend and make it disappear in one second
            screen.setLegend("Ouch!");
            new CountdownTimer(this, null, 1000);

            // Start the timer that will cause the next round to begin.
            new TransitionTimer(END_DELAY, transitionCount, this);
        }

        // Remove bullets currently onscreen
        for (int x = 0; x < bullet.length; x++)
        {
            if (bullet[x] != null)
            {
                screen.removeParticipant(bullet[x]);
                bullet[x] = null;
                bullets--;
            }
        }

        // If there's an extra life, remove it
        if (extra1 != null)
        {
            screen.removeParticipant(extra1);
            extra1 = null;
        }

        // Set the JLabel to the new lives
        life.setText("Lives: " + lives);
    }

    /**
     * A tie bullet has collided with something.
     */
    private void tieBulletCollision (TieBullet b)
    {
        // Remove the tiebullet equal to the one that collided
        for (int x = 0; x < 8; x++)
        {
            if (b.equals(tieBullet[x]))
            {
                screen.removeParticipant(tieBullet[x]);
                tieBullet[x] = null;
            }
        }
        
        // Decrement the bullet count
        tieBullets--;
    }

    /**
     * A bullet has collided with something.
     */
    private void bulletCollision (Bullet b)
    {
        // Find a the bullet, get rid of it
        for (int x = 0; x < bullet.length; x++)
        {
            if (b.equals(bullet[x]))
            {
                screen.removeParticipant(bullet[x]);
                bullet[x] = null;
            }
        }
        
        // Decrement the bullet count
        bullets--;
    }

    /**
     * Something has hit an asteroid
     */
    private void asteroidCollision (Asteroid a)
    {
        // Play the clip
        audioEffects("Assets/explosion.mp3");
        // The asteroid disappears
        screen.removeParticipant(a);
        makeExplosion(a.getX(), a.getY());

        // Add to the score base on the size
        if (a.getSize() == 2)
            score += 20;
        else if (a.getSize() == 1)
            score += 50;
        else if (a.getSize() == 0)
            score += 100;
        scoreLabel.setText("| Score: " + score);

        // Create two smaller asteroids. Put them at the same position
        // as the one that was just destroyed and give them a random
        // direction.
        int size = a.getSize();

        // Make some random extra lives after level one, increasing in
        // probability with each level
        if (size == 0 && level > 1)
        {
            int levelHard = 15 - level;
            Random generator = new Random();
            if (generator.nextInt() % levelHard == 0)
                placeExtra();
        }

        // UNLEASH A TIEFIGHTER! (if the time is right)
        if (size == 0 && level > 1 && !unleashed)
            releaseTieFighter();

        // Decrement the size
        size -= 1;
        if (size >= 0)
        {
            // Increase the speed with the level
            int speed = 2 + level;
            
            // Increase speed based on size
            switch (size) {
            case 1:
                speed++;
                break;
            case 0:
                speed += 2;
                break;
            }
            
            // Make new asteroids
            Asteroid a1 = new Asteroid(random.nextInt(4), size, a.getX(),
                    a.getY());
            Asteroid a2 = new Asteroid(random.nextInt(4), size, a.getX(),
                    a.getY());
            
            // Set their velocity in random directions
            a1.setVelocity(speed, random.nextDouble() * 2 * Math.PI);
            a2.setVelocity(speed, random.nextDouble() * 2 * Math.PI);
            
            // Set their rotation randomly
            a1.setRotation(2 * Math.PI * random.nextDouble());
            a2.setRotation(2 * Math.PI * random.nextDouble());
            
            // Add them to the screen
            screen.addParticipant(a1);
            screen.addParticipant(a2);
        }
    }

    /**
     * Pause or unpause the game
     */
    private void pause ()
    {

        // Pause the screen
        screen.pause();

        // Set the pause status so that buttons don't have effect
        pause = !pause;

        // Set the legend to "Paused" and notate the start time of the pause
        if (pause)
        {
            screen.setLegend("PAUSED");
            pauseStart = System.currentTimeMillis();
        }

        // Set the legend back to empty and make sure the bullets and extra life
        // and invulnerability on screen get extended life
        else
        {
            screen.setLegend("");
            long pauseLength = System.currentTimeMillis() - pauseStart;
            for (Bullet x : bullet)
            {
                if (x != null)
                {
                    x.setStartTime(x.getStartTime() + pauseLength);
                }
            }
            if (extra1 != null)
                extra1.setStart(extra1.getStart() + pauseLength);
            if (invulnerable)
                invulnerableTimer = invulnerableTimer + pauseLength;
        }
    }

    /**
     *  Create a ship explosion
     */
    private void shipExp (double x, double y)
    {
        // If it's not paused...
        if (!pause)
        {
            // Create the explosion
            shipExp = new Participant[4];
            
            // Create a random number generator
            Random rand = new Random();
            
            // Create the pieces
            for (int i = 0; i < 2; i++) {
                ShipExpPiece exp = new ShipExpPiece();
                exp.setPosition(x, y);
                // Set random velocity and rotation
                double expX = 0.5 + rand.nextInt(3);
                double expY = 0.5 + rand.nextInt(3);
                exp.setVelocity(expX, expY);
                // exp.rotate(i * Math.PI / 2);
    
                int expR = rand.nextInt(100);
                exp.rotate(0.02 * Math.PI * expR);
    
                shipExp[i] = exp;
                
                // Add the piece of explosion
                screen.addParticipant(exp);
            }
    
            // Create another piece of the explosion
            ShipExpPiece2 exp2 = new ShipExpPiece2();
            exp2.setPosition(x, y);
            
            // Set random velocity and rotation
            double exp2X = 0.5 + rand.nextInt(3);
            double exp2Y = 0.5 + rand.nextInt(3);
            exp2.setVelocity(exp2X, exp2Y);
            
            // Make it random
            int expR2 = rand.nextInt(100);
            exp2.rotate(0.02 * Math.PI * expR2);
            shipExp[2] = exp2;
            
            // Add the piece to the screen
            screen.addParticipant(exp2);
    
            // Create another piece
            ShipExpPiece3 exp3 = new ShipExpPiece3();
            exp3.setPosition(x, y);
            
            // Set random velocity and rotation
            double expX3 = 0.5 + rand.nextInt(3);
            double expY3 = 0.5 + rand.nextInt(3);
            exp3.setVelocity(expX3, expY3);
            
            // randomize it
            int expR3 = rand.nextInt(100);
            exp3.rotate(0.02 * Math.PI * expR3);
            shipExp[3] = exp3;
            
            // Add it to the screen
            screen.addParticipant(exp3);
        }
    }

    /**
     *  Remove ship explosion pieces
     */
    private void removeShipExp ()
    {
        // Remove all pieces one by one
        for (int x = 0; x < shipExp.length; x++)
        {
            // As long as it's not paused
            if (!pause)
            {
                // Create a random number generator
                Random rand = new Random();
                
                // Has to be inside the range [1900, 3600]
                long expDuration = 1900 + rand.nextInt(1700);
                
                // If it's inside the expected duration
                if (shipExp[x] != null
                        && System.currentTimeMillis()
                                - (shipExp[x]).getStartTime() > expDuration) {
                    
                    // Remove the piece
                    screen.removeParticipant(shipExp[x]);
                    shipExp[x] = null;
                }
            }
        }
    }

    /**
     *  Create TieFighter explosion
     */
    private void tieExp (double x, double y, TieFighter t)
    {
        
        // As long as it's not paused
        if (!pause)
        {
            // If the tie fighter isn't advanced
            if (t.isAdvanced() == false)
            {
                // Create the holder for the pieces
                tieExp = new Participant[] { null, null, null, null };
                
                // Create a random number generator
                Random rand = new Random();
    
                // Make a new piece
                TieExpPiece exp = new TieExpPiece(false);
                
                // Set it's position
                exp.setPosition(x, y);
                
                // Set random velocity and rotation
                double expX = 0.5 + rand.nextInt(3);
                double expY = 0.5 + rand.nextInt(3);
                
                // Set velocity
                exp.setVelocity(expX, expY);
                
                // Make the rotation random
                int expR = rand.nextInt(100);
                exp.rotate(0.02 * Math.PI * expR);
                
                // Put it in the array
                tieExp[0] = exp;
                
                // Add it to the screen
                screen.addParticipant(exp);
    
                // Create another piece
                TieExpPiece2 exp2 = new TieExpPiece2(false);
                
                // Set it's position
                exp2.setPosition(x, y);
                
                // Set random velocity and rotation
                double expX2 = 0.5 + rand.nextInt(3);
                double expY2 = 0.5 + rand.nextInt(3);
                exp2.setVelocity(expX2, expY2);
                int expR2 = rand.nextInt(100);
                exp2.rotate(0.02 * Math.PI * expR2);
                tieExp[1] = exp2;
                
                // Add it to the screen
                screen.addParticipant(exp2);
    
                // Create another piece
                TieExpPiece3 exp3 = new TieExpPiece3(false);
                
                // Set it's position
                exp3.setPosition(x, y);
                
                // Set random velocity and rotation
                double expX3 = 0.5 + rand.nextInt(3);
                double expY3 = 0.5 + rand.nextInt(3);
                exp3.setVelocity(expX3, expY3);
                int expR3 = rand.nextInt(100);
                exp.rotate(0.02 * Math.PI * expR3);
                tieExp[2] = exp3;
                
                // Add it to the screen
                screen.addParticipant(exp3);
    
                // Create another piece
                TieExpPiece4 exp4 = new TieExpPiece4(false);
                exp4.setPosition(x, y);
                
                // Set random velocity and rotation
                double expX4 = 0.5 + rand.nextInt(3);
                double expY4 = 0.5 + rand.nextInt(3);
                exp4.setVelocity(expX4, expY4);
                int expR4 = rand.nextInt(100);
                exp4.rotate(0.02 * Math.PI * expR4);
                tieExp[3] = exp4;
                
                // Add it to the screen
                screen.addParticipant(exp4);
            }
            
            // If the tiefighter is advanced
            else if (t.isAdvanced() == true)
            {
                // Make the container for the pieces
                tieExp = new Participant[] { null, null, null, null };
                
                // Create a random number generator
                Random rand = new Random();
    
                // Create another piece
                TieExpPiece exp = new TieExpPiece(true);
                
                // Set it's position
                exp.setPosition(x, y);
                
                // Set random velocity and rotation
                double expX = 0.5 + rand.nextInt(3);
                double expY = 0.5 + rand.nextInt(3);
                exp.setVelocity(expX, expY);
                int expR = rand.nextInt(100);
                exp.rotate(0.02 * Math.PI * expR);
                
                // Add it to the array
                tieExp[0] = exp;
                
                // Add it to the screen
                screen.addParticipant(exp);
    
                
                // Create another piece
                TieExpPiece2 exp2 = new TieExpPiece2(true);
                
                // Set it's position
                exp2.setPosition(x, y);
                
                // Set random velocity and rotation
                double expX2 = 0.5 + rand.nextInt(3);
                double expY2 = 0.5 + rand.nextInt(3);
                exp2.setVelocity(expX2, expY2);
                int expR2 = rand.nextInt(100);
                exp2.rotate(0.02 * Math.PI * expR2);
                
                // Add it to the array
                tieExp[1] = exp2;
                
                // Add it to the screen
                screen.addParticipant(exp2);
    
                // Add another piece
                TieExpPiece3 exp3 = new TieExpPiece3(true);
                
                // Set it's position
                exp3.setPosition(x, y);
                
                // Set random velocity and rotation
                double expX3 = 0.5 + rand.nextInt(3);
                double expY3 = 0.5 + rand.nextInt(3);
                exp3.setVelocity(expX3, expY3);
                int expR3 = rand.nextInt(100);
                exp.rotate(0.02 * Math.PI * expR3);
                
                // Add it to the array
                tieExp[2] = exp3;
                
                // Add it to the screen
                screen.addParticipant(exp3);
    
                // Create another piece
                TieExpPiece4 exp4 = new TieExpPiece4(true);
                
                // Set it's position
                exp4.setPosition(x, y);
                
                // Set random velocity and rotation
                double expX4 = 0.5 + rand.nextInt(3);
                double expY4 = 0.5 + rand.nextInt(3);
                exp4.setVelocity(expX4, expY4);
                int expR4 = rand.nextInt(100);
                exp4.rotate(0.02 * Math.PI * expR4);
                
                // Add it to the array
                tieExp[3] = exp4;
                
                // Add it to the screen
                screen.addParticipant(exp4);
            }
        }
    }

    /**
     * Remove pieces of the tie fighter wreckage
     */
    private void removeTieExp ()
    {
        // Remove each piece
        for (int x = 0; x < tieExp.length; x++) {
            // If it's not paused
            if (!pause)
            {
                // Create a random number generator
                Random rand = new Random();
                
                // Make sure the expected duration is between 1900 and 3600 milliseconds
                long expDuration = 1900 + rand.nextInt(1700);
                
                // Remove it after appropriate time
                if (tieExp[x] != null
                        && System.currentTimeMillis()
                                - (tieExp[x]).getStartTime() > expDuration) {
                    screen.removeParticipant(tieExp[x]);
                    tieExp[x] = null;
                }
            }
        }
    }

    /**
     * Make explosions
     */
    private void makeExplosion (double x, double y)
    {
        // Create the parts of the explosion
        Explosion parts = new Explosion();
        
        // Cycle through them
        for (int n = 0; n < 8; n++)
        {
            ExplosionAsteroid piece = new ExplosionAsteroid(screen);
            parts.setPart(piece, n);
            
            // Set the position
            piece.setPosition(x, y);
            
            // Set random velocity and rotation
            Random rand = new Random();
            double expX = 0.5 + rand.nextInt(3);
            double expY = 0.5 + rand.nextInt(3);
            piece.setVelocity(expX, expY);
            int expR = rand.nextInt(100);
            piece.rotate(0.02 * Math.PI * expR);
            
            // Add it to the screen
            screen.addParticipant(piece);
        }
        
        int i = 0;
        while (explosions.getExp(i) != null) {
            i++;
        }
        
        explosions.setExp(parts, i);
    }

    /**
     * Remove the explosion
     */
    private void removeExp ()
    {
        // Remove each piece if the first one is a piece
        if (explosions.getExp(0) != null)
        {
            // If it's not paused
            if (!pause)
            {
                // Cycle through each possible explosion
                for (int x = 0; x < 35; x++)
                {
                    // If it's onscreen, get rid of it
                    if (explosions.getExp(x) != null)
                    {
                        for (int x1 = 0; x1 < 8; x1++)
                        {
                            // Create a random number generator
                            Random rand = new Random();
                            
                            // Make sure the expected duration is between 900 and 1900 milliseconds
                            long expDuration = 900 + rand.nextInt(1000);
                            
                            // Remove once time requirenments are met
                            if (explosions.getExp(x).getPart(x1) != null
                                    && System.currentTimeMillis()
                                            - explosions.getExp(x).getPart(x1)
                                                    .getStartTime() > expDuration)
                            {
                                screen.removeParticipant(explosions.getExp(x)
                                        .getPart(x1));
                                explosions.getExp(x).setPart(null, x1);
                            }
                        }
                    }
    
                }
            }
        }
    }

    /**
     * Remove all explosions at once
     */
    private void removeAllExp ()
    {
        // If there are any explosions onscreen
        if (explosions.getExp(0) != null)
        {
            // If it's not paused
            if (!pause)
            {
                // Cycle through all pieces
                for (int x = 0; x < 35; x++)
                {
                    // If it's there, get rid of it
                    if (explosions.getExp(x) != null)
                    {
                        for (int x1 = 0; x1 < 8; x1++)
                        {
                            if (explosions.getExp(x).getPart(x1) != null)
                            {
                                screen.removeParticipant(explosions.getExp(x)
                                        .getPart(x1));
                                explosions.getExp(x).setPart(null, x1);
                            }
                        }
    
                    }
                    explosions.setExp(null, x);
                }
            }
        }
    }

    /**
     * Transport the ship to a random location
     */
    public void transport ()
    {
        // Create a random generator
        Random generator = new Random();
        
        // Place the ship randomly
        if (ship != null) ship.setPosition((double) generator.nextInt(screen.getWidth()),
                (double) generator.nextInt(screen.getHeight()));
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            // Make sure it's not paused
            if (pause)
                pause();
            transitionCount++;
            initialScreen();

            // Reset variables
            unleashed = false;
            level = 1;
            score = 0;
            scoreLabel.setText("| Score: " + score);

            // Remove all explosions
            removeAllExp();
            removeShipExp();
            removeTieExp();

            // Turn off endscreen status
            screen.setNotEnd();
        }

        // Time to refresh the screen
        else if (e.getSource() == refreshTimer)
        {
            // Turn off invulnerable after appropriate time (unless cheating)
            if (System.currentTimeMillis() - invulnerableTimer > 3000 && !pause
                    && ship != null && invulnerable && !invulnerableCheat)
            {
                invulnerable = false;
                ship.setVulnerable();
            }

            // Move the ship as necessary
            if (ship != null)
            {
                if (leftPressed)
                    ship.rotate(-Math.PI / 23);
                if (rightPressed)
                    ship.rotate(Math.PI / 23);
                if (upPressed)
                    ship.accelerate(1);

                // Make sure that bullets disappear as necessary
                if (!pause)
                {
                    for (int x = 0; x < bullet.length; x++)
                    {
                        if (bullet[x] != null
                                && System.currentTimeMillis()
                                        - bullet[x].getStartTime() > BULLET_DURATION)
                        {
                            screen.removeParticipant(bullet[x]);
                            bullet[x] = null;
                            bullets--;
                        }
                    }
                }

                // Remove the Extra Life after a 4 seconds
                if (!pause && extra1 != null)
                {
                    if (System.currentTimeMillis() - extra1.getStart() > 4000)
                    {
                        screen.removeParticipant(extra1);
                        extra1 = null;
                    }
                }
            }

            // Remove TieFighter bullets after BULLET_DURATION
            if (!pause && tie != null)
            {
                for (int x = 0; x < 8; x++)
                {
                    if (tieBullet[x] != null
                            && System.currentTimeMillis()
                                    - tieBullet[x].getStartTime() > BULLET_DURATION)
                    {
                        screen.removeParticipant(tieBullet[x]);
                        tieBullet[x] = null;
                        tieBullets--;
                    }
                }
            }

            // Remove all explosions
            removeExp();
            removeShipExp();
            removeTieExp();

        }

        // Refresh screen
        screen.refresh();
    }

    /**
     * Based on the state of the controller, transition to the next state.
     */
    public void performTransition ()
    {
        // Record that a transition was made. That way, any other pending
        // transitions will be ignored.
        transitionCount++;

        // If there are no lives left, the game is over. Show
        // the final screen.
        if (lives == 0) {
            if (score > bestScore)
                bestScore = score;
            bestScoreLabel.setText("Best Score: " + bestScore);
            finalScreen();
        }

        // The ship must have been destroyed. Place a new one and
        // continue on the current level
        else {
            placeShip();
        }
    }

    /**
     * Deals with certain key presses
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        // Only perform moves while not paused
        if (!pause) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                leftPressed = true;
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                rightPressed = true;
            else if (e.getKeyCode() == KeyEvent.VK_UP)
                upPressed = true;
            else if (e.getKeyCode() == KeyEvent.VK_B)
                transport();
            else if (e.getKeyCode() == KeyEvent.VK_Q)
                qPressed = true;
            else if (e.getKeyCode() == KeyEvent.VK_W)
                wPressed = true;
            else if (e.getKeyCode() == KeyEvent.VK_E)
                ePressed = true;
            
            // For turning on and off the invulnerability cheat
            if (!invulnerableCheat && qPressed && wPressed && ePressed)
            {
                invulnerableCheat = true;
                ship.setInvulnerable();
                cheat = true;
            }
            
            else if (invulnerableCheat && qPressed && wPressed && ePressed)
            {
                invulnerableCheat = false;
                ship.setVulnerable();
            }
        }

        // To pause the game on "m" key, or unpause if not
        if (e.getKeyCode() == KeyEvent.VK_M)
        {
            pause();
            upPressed = false;
            leftPressed = false;
            rightPressed = false;
        }
    }

    /**
     * If a key is release, do this:
     */
    @Override
    public void keyReleased (KeyEvent e)
    {
        // Turn off key presses
        if (!pause)
        {
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                leftPressed = false;
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                rightPressed = false;
            else if (e.getKeyCode() == KeyEvent.VK_UP)
                upPressed = false;
            else if (e.getKeyCode() == KeyEvent.VK_SPACE)
                fireBullet();
            else if (e.getKeyCode() == KeyEvent.VK_Q)
                qPressed = false;
            else if (e.getKeyCode() == KeyEvent.VK_W)
                wPressed = false;
            else if (e.getKeyCode() == KeyEvent.VK_E)
                ePressed = false;
        }
    }

    // Not used
    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**
     * Callback for countdown timer. Used to create transient effects.
     */
    @Override
    public void timeExpired (Participant p)
    {
        screen.setLegend("");
    }

    /**
     * This is the listener function which will be called when the mp3 stops
     */
    @Override
    public void itsDone (AdvancedPlayer x)
    {
        // Set the song to something else
        String toPlay = "Assets/Star Wars V - The Battle In The Snow.mp3";
        if (playCount == 2)
            toPlay = "Assets/Star Wars IV - Imperial Attack.mp3";
        if (playCount == 3)
            toPlay = "Assets/Star Wars IV - Cantina Band.mp3";

        // Start the audio
        try {
            play = new FileInputStream(toPlay);
            playMP3 = new AdvancedPlayer(play);
        }
        
        // Catch any audio exceptions and drop them.
        catch (Exception e) {
            System.out.print("whoops...");
        }
        
        MusicListener forthis = new MusicListener(this, playMP3);
        playMP3.setPlayBackListener(forthis);
        audio();

        // Add to the playcount
        playCount++;
    }
 
    // From TieListener, fires a bullet once a timer goes off
    @Override
    public void tieFighterFire (TieFighter t, boolean advanced)
    {
        if (tie != null && !pause)
            tieFireBullet(advanced);
    }

    // Make explosions
    @Override
    public void AsteroidExplode (ExplosionAsteroid t, double x, double y)
    {
        if (!pause)
        {
            makeExplosion(x, y);
        }

    }
}
