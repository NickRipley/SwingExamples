package asteroids;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.swing.*;

import static asteroids.Constants.*;

/**
 * The area in which the game takes place.
 * 
 * @author Joe Zachary & Nick Lloyd & Andrew Katsanevas
 */
public class Screen extends JPanel
{
    // The participants (asteroids, bullets, ships, etc.) that are
    // involved in the game.
    private LinkedList<Participant> participants;

    // Objects interested in learning about collisions between
    // pairs of participants
    private Set<CollisionListener> listeners;

    // Participants that will be added to/removed from the game at the next
    // refresh
    private Set<Participant> pendingAdds;
    private Set<Participant> pendingRemoves;

    // Legend that is displayed across the screen
    private String legend;
    
    // X & Y coordinates for every participant
    private HashSet<Point> xAndY;
    
    // Is it the end or splash screen?
    private boolean endscreen;
    private boolean splash;
    
    // Create the file to house the all time high list
    File highScores;
    
    // To house the high scores in game
    String[] highScore;
    
    // A string used in the game over screen
    String gameOver = "HIGH SCORES:";

    /**
     * Creates an empty screen
     */
    public Screen ()
    {
        // Initialize the member variables:
        participants = new LinkedList<Participant>();
        listeners = new HashSet<CollisionListener>();
        pendingAdds = new HashSet<Participant>();
        pendingRemoves = new HashSet<Participant>();
        
        // Set the legend to blank
        legend = "";
        
        // Set the sizes
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        
        // Set the colors
        setBackground(Color.black);
        setForeground(Color.white);
        
        // Set the font
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        setFocusable(true);
        xAndY = new HashSet<Point>();
        endscreen = false;
        splash = false;
        
        // Try loading the highscore file
        try {
            loadHighScore();
        }
        catch (FileNotFoundException e)
        {
            JOptionPane.showMessageDialog(null, "Sorry, there was a problem with the high score file...");
        }
    }
    
    // Call to say it's a splash screen
    public void splashTrue() {
        splash = true;
    }
    
    // Call to say it's not a splash screen
    public void splashFalse() {
        splash = false;
    }
    
    /**
     * Load the file for the high scores
     * @throws FileNotFoundException 
     */
    public void loadHighScore() throws FileNotFoundException {
        highScore = new String[]{"null", "null", "null", "null", "null", "null"};
        try {
            highScores = new File("Assets/highScore.txt");
            boolean test = highScores.createNewFile();
            if (test) {
                resetHighs();
            }
        }
        catch (Exception e){ 
        }
      
        try {
            Scanner scan = new Scanner(highScores);
            highScore = scan.nextLine().split("-", 6);
            String temp = highScore[5];
            int x = Integer.parseInt(highScore[1]);
            x = Integer.parseInt(highScore[3]);
            x = Integer.parseInt(highScore[5]);
            scan.close();
        }
        catch (Exception e) {
            resetHighs();
            highScore = new String[]{"Erin", "00000", "Bob", "00000", "Ashley", "00000"};
        }
    }
    
    /**
     * Reset the High Scores due to bad content in file
     */
    public void resetHighs() {
        FileWriter writer;
        try
        {
            writer = new FileWriter(highScores);
            writer.write("Erin-00000-Bob-00000-Ashley-00000");
            writer.close();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "Sorry, there was a problem with the high score file...");
        }
        
    }
    
    /**
     * Set the endscreen status
     */
    public void setEnd() {
        endscreen = true;
    }
    
    /**
     * Set the endscreen status
     */
    public void setNotEnd() {
        endscreen = false;
    }
    
    /**
     * Add a participant to the game
     */
    public void addParticipant (Participant p)
    {
        pendingAdds.add(p);
    }

    /**
     * Remove a participant from the game
     */
    public void removeParticipant (Participant p)
    {
        pendingRemoves.add(p);
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }
    
    /**
     * Check scores against all time highs and write in new ones
     */
    public void checkScore(int score) {
        if (score > Integer.parseInt(highScore[1])) {
            String highName = getHighName();
            highScore[4] = highScore[2];
            highScore[5] = highScore[3];
            highScore[2] = highScore[0];
            highScore[3] = highScore[1];
            highScore[0] = highName;
            highScore[1] = String.format("%05d", score);
            writeHighs();
        }
        else if (score > Integer.parseInt(highScore[3])) {
            String highName = getHighName();
            highScore[4] = highScore[2];
            highScore[5] = highScore[3];
            highScore[2] = highName;
            highScore[3] = String.format("%05d", score);
            writeHighs();
        }
        else if (score > Integer.parseInt(highScore[5])) {
            String highName = getHighName();
            highScore[4] = highName;
            highScore[5] = String.format("%05d", score);
            writeHighs(); 
        }
    }
    
    /**
     * Prompts user for a name and returns the first four letters
     */
    public String getHighName() {
        String temp = JOptionPane.showInputDialog(null, "You got a HIGH SCORE! Please enter your"
                + "name for the sake of posterity:", "HIGH SCORE", JOptionPane.QUESTION_MESSAGE);
        if (temp == null || (temp != null && ("".equals(temp)))) temp = "Nobody";
        if (temp.length() > 4 ) return temp.substring(0, 4);
        return temp;
    }
    
    /**
     * Write to file highScore with new scores
     */
    public void writeHighs() {
        PrintWriter writer;
        try
        {
            writer = new PrintWriter(highScores);
            writer.print(highScore[0] + "-" + highScore[1] + "-" + highScore[2] + "-" +
            highScore[3] + "-" + highScore[4] + "-" + highScore[5]);
            writer.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics g)
    {
        // Do the default painting
        super.paintComponent(g);

        // Draw each participant in its proper place
        for (Participant e : participants)
        {
            e.draw((Graphics2D) g);
        }

        // Draws the legend across the middle of the panel
        int size = g.getFontMetrics().stringWidth(legend);
        if (!endscreen) g.drawString(legend, (SIZE - size) / 2, SIZE / 2);
        if (endscreen) {
            String[] names = new String[3];
            names[0] = highScore[0] + "      " + highScore[1];
            names[1] = highScore[2] + "      " + highScore[3];
            names[2] = highScore[4] + "      " + highScore[5];
            g.drawString(legend, (SIZE - size) / 2, SIZE / 3);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 40));
            int size2 = g.getFontMetrics().stringWidth(gameOver);
            g.drawString(gameOver, (SIZE - size2) / 2, (SIZE / 3) + 70);
            if (names[0] != null) {
                int size1 = g.getFontMetrics().stringWidth(names[0]);
                g.drawString(names[0], (SIZE - size1) / 2, (SIZE / 3) + 130);
            }
            if (names[1] != null) {
                int size1 = g.getFontMetrics().stringWidth(names[1]);
                g.drawString(names[1], (SIZE - size1) / 2, (SIZE / 3) + 190);
            }
            if (names[2] != null) {
                int size1 = g.getFontMetrics().stringWidth(names[2]);
                g.drawString(names[2], (SIZE - size1) / 2, (SIZE / 3) + 250);
            }
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        }
        if (splash) {
            g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 40));
            g.setColor(Color.RED);
            String splashh = "Hoth Asteroid Field Edition";
            int size1 = g.getFontMetrics().stringWidth(splashh);
            g.drawString(splashh, (SIZE - size1) / 2, (SIZE / 2) + 70);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
            g.setColor(Color.WHITE);
        }
    }

    /**
     * Clear the screen so that nothing is displayed
     */
    public void clear ()
    {
        pendingRemoves.clear();
        pendingAdds.clear();
        participants.clear();
        legend = "";
    }

    /**
     * Records a new listener
     */
    public void addCollisionListener (CollisionListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Removes an existing listener.
     */
    public void removeCollisionListener (CollisionListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Compares each pair of elements to detect collisions, then notifies all
     * listeners of any found.
     */
    private void checkForCollisions ()
    {
        for (Participant p1 : participants)
        {
            Iterator<Participant> iter = participants.descendingIterator();
            while (iter.hasNext())
            {
                Participant p2 = iter.next();
                if (p2 == p1)
                    break;
                if (pendingRemoves.contains(p1))
                    break;
                if (pendingRemoves.contains(p2))
                    break;
                if (p1.overlaps(p2))
                {
                    for (CollisionListener listener : listeners)
                    {
                        listener.collidedWith(p1, p2);
                    }
                }
            }
        }
    }

    /**
     * Completes any adds and removes that have been requested.
     */
    private void completeAddsAndRemoves ()
    {
        // Note: These updates are saved up for later to avoid modifying
        // the participants list while it is being iterated over
        for (Participant p : pendingAdds)
        {
            participants.add(p);
        }
        pendingAdds.clear();
        for (Participant p : pendingRemoves)
        {
            participants.remove(p);
        }
        pendingRemoves.clear();
    }

    /**
     * Called when it is time to update the screen display. This is what drives
     * the animation.
     */
    public void refresh ()
    {
        completeAddsAndRemoves();
        for (Participant p : participants)
        {
            p.move();
        }
        checkForCollisions();
        repaint();
    }
    
    /**
     * Pause Game
     */
    public void pause() {
        for (Participant p: participants) p.pause();
    }
}
