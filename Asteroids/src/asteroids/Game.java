package asteroids;

import javax.swing.*;
import java.awt.*;
import static asteroids.Constants.*;

/**
 * Implements an asteroid game.
 * 
 * @author Joe Zachary & Nick Lloyd & Andrew Katsanevas
 *
 */
public class Game extends JFrame
{
    /**
     * Launches the game
     */
    public static void main (String[] args)
    {
        Game a = new Game();
        a.setVisible(true);
    }

    /**
     * Lays out the game and creates the controller
     */
    public Game ()
    {
        // Title at the top
        setTitle(TITLE);

        // Default behavior on closing
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create the JLabel to display lives
        JLabel lives = new JLabel("Lives: ");
        
        // Create the JLabel to display the score
        JLabel score = new JLabel("| Score: ");
        
        // Create the JLabel for the best score
        JLabel bestScore = new JLabel("Best Score: ");

        // The main playing area and the controller
        Screen screen = new Screen();
        Controller controller = new Controller(this, screen, lives, score, bestScore);

        // This panel contains the screen to prevent the screen from being
        // resized
        JPanel screenPanel = new JPanel();
        screenPanel.setLayout(new GridBagLayout());
        screenPanel.add(screen);

        // This panel contains buttons and labels
        JPanel controls = new JPanel();

        // The button that starts the game
        controls.add(bestScore);
        JButton startGame = new JButton(START_LABEL);
        controls.add(startGame);
        controls.add(lives);
        controls.add(score);

        // Organize everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(screenPanel, "Center");
        mainPanel.add(controls, "North");
        setContentPane(mainPanel);
        pack();

        // Connect the controller to the start button
        startGame.addActionListener(controller);
    }
}
