package lightsOut;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.*;

/**
 * 
 * @author Nicholas Lloyd u0949261
 * 
 * Presents a GUI which displays and allows the play of the game "Lights Out".
 * Uses 4 JPanels, mainPanel, buttonPanel, bottom, & upper to display the 
 * buttons and elements of the game including a new game button, a custom game
 * button, a time and a moves counter, as well as the game board and two buttons
 * which display the rules and a fast way to win.
 *
 */

@SuppressWarnings("serial")
public class LightsOut extends JFrame implements ActionListener {

	// Create all needed elements and variables
	
	// Create ImageIcons from gifs in the Assets folder for use on the board and in JOptionPanes
	private ImageIcon buttonIconOn = new ImageIcon("Assets/on.gif");
	private ImageIcon buttonIconOff = new ImageIcon("Assets/off.gif");
	
	// Create a container for the board buttons
	private JButton[] button = new JButton[25];
	
	// Create a GameState for computing the state of the board
	private GameState current = new GameState();
	
	// Create the four panels to be displayed
	private JPanel mainPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private JPanel bottom = new JPanel();
	private JPanel upper = new JPanel();
	
	// Create the buttons (aside from the board buttons)
	private JButton newGame = new JButton("New Game");
	private JButton manualSetup = new JButton("Enter Manual Setup");
	private JButton rules = new JButton("Show the Rules");
	private JButton showHints = new JButton("How to Win");
	
	// Create the JLabels to show move counter and the time counter
	private JLabel moves = new JLabel("| Moves: " + current.getMoves());
	private JLabel time = new JLabel("Time: ");
	
	// Creates a boolean to represent when the user is creating a custom board
	private boolean custom = false;
	
	// Creates a long which will be used to hold the start time for the time counter
	private long start;
	
	// Creates a String to represent the time when the win JOptionPane is open so 
	// the time counter does not keep counting while it's open
	private String winTime;
	
	// Pause represents a state when the counter should not be going
	private boolean pause = false;
	
	// Start 'er up!
	public static void main (String[] args) {
		LightsOut game = new LightsOut();
		game.setVisible(true);
	}
	
	// Adds the bottom panel elements and assigns them listeners and ActionCommands
	public void addBottom(JPanel bottom) {
		bottom.add(rules);
		rules.addActionListener(this);
		rules.setActionCommand("rules");
		bottom.add(showHints);
		showHints.addActionListener(this);
		showHints.setActionCommand("hints");
	}
	
	// Adds the upper panel elements and assigns them listeners and ActionCommands
	public void addUpper(JPanel upper) {
		upper.add(moves);
		upper.add(newGame);
		upper.add(manualSetup);
		upper.add(time);
		upper.add(moves);
		newGame.addActionListener(this);
		newGame.setActionCommand("new");
		manualSetup.addActionListener(this);
		manualSetup.setActionCommand("custom");
	}
	
	// Updates the JPanel moves to reflect current move
	public void changeMoves() {
		moves.setText("| Moves: " + current.getMoves());
	}
	
	// Changes the text in JButton manualSetup
	public void changeCustom() {
		if (!custom)
		manualSetup.setText("Enter Manual Setup");
		else manualSetup.setText("Exit Manual Setup");
	}
	
	/** 
	 * Adds the game board buttons to the buttonPanel and sets them to the proper .gif,
	 * as well as making sure the button border does not display.  Then gives each
	 * board button an ActionListener and ActionCommand.
	 */
	public void addButtons(JPanel buttons) {
		
		for (int x = 0; x < current.state().length; x++) {
			if (current.state()[x]) {
				button[x] = new JButton(buttonIconOn);
				button[x].setBorder(BorderFactory.createEmptyBorder());
				button[x].setContentAreaFilled(false);
				button[x].addActionListener(this);
				button[x].setActionCommand("" + x);
				buttons.add(button[x]);
			}
			else {
				button[x] = new JButton(buttonIconOff);
				button[x].setBorder(BorderFactory.createEmptyBorder());
				button[x].setContentAreaFilled(false);
				button[x].addActionListener(this);
				button[x].setActionCommand("" + x);
				buttons.add(button[x]);
			}
		}
			
	}
	
	/**
	 *  Updates the GameState of the game for each click of the game board 
	 *  and a "New Game" push, then updates the .gifs in each button to reflect 
	 *  the new state. Resets the "start" time when proper.
	 */
	public void changeButtons(int x) {
		// Board is in custom mode and the button pressed wasn't "New Game"
		if (custom && x != 25) {
			// Change the board in custom mode
			current.moveCustom(x);
			// Update the board buttons
			for (int y = 0; y < 25; y++) {
				if (current.state()[y]) button[y].setIcon(buttonIconOn);
				else button[y].setIcon(buttonIconOff);
			}
		}
		// "New Game" button pressed and board isn't in custom mode
		if (x == 25 && !custom) {
			// Update the board buttons
			for (int y = 0; y < 25; y++) {
				if (current.state()[y]) button[y].setIcon(buttonIconOn);
				else button[y].setIcon(buttonIconOff);
			}
			// Restart the time counter
			start = System.currentTimeMillis();
		}
		// "New Game" button pressed and board is in custom mode
		if (x == 25 && custom) {
			// Change all states to false
			current.blankBoard();
			// Update the board buttons
			for (int y = 0; y < 25; y++) {
				if (current.state()[y]) button[y].setIcon(buttonIconOn);
				else button[y].setIcon(buttonIconOff);
			}
			// Restart the time counter
			start = System.currentTimeMillis();
		}
		// Board isn't in custom mode and one of the board buttons has been pressed
		if (x < 25 && !custom) {
			// Update the GameState
			current.move(x);
			// Update the board buttons
			for (int y = 0; y < 25; y++) {
				if (current.state()[y]) button[y].setIcon(buttonIconOn);
				else button[y].setIcon(buttonIconOff);
			}
		}
	}
	
	// Returns the time to be displayed in proper format.  If paused, return a set time.
	public String currentElapsedTime() {
		// Create the format for the response
		DecimalFormat formatter = new DecimalFormat("#00.###");
		// The time counter isn't paused
		if (!pause) {
			// return the formatted time counter time
			return (formatter.format(((System.currentTimeMillis() - start) / 1000) / 60)) + ":" 
			+ formatter.format(((System.currentTimeMillis() - start) / 1000) % 60);
		}
		// If time counter is paused, return time at pause
		return winTime;
	}
	
	// Construct the JFrame, create a timer that reacts ever second (to update the game timer)
	public LightsOut() {
		
		// When this window is closed, the program exits
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Lights Out");
		
		// Create a Timer which updates the time counter
		Timer timer = new Timer(1000, e -> time.setText("Time: " + currentElapsedTime()));
	    timer.setCoalesce(true);
	    timer.setRepeats(true);
	    timer.setInitialDelay(0);
	    timer.start();
		
		// Create the main panel and add the nested ones, reset the start time
		setContentPane(mainPanel);
		setSize(600,650);
		
		// Set the layout managers for the different JPanels
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		upper.setLayout(new FlowLayout());
		bottom.setLayout(new GridLayout(1,2));
		buttonPanel.setLayout(new GridLayout(5, 5));
		
		// Set the MaximumSize for each nested JPanel so that resizing the JFrame won't screw them up
		bottom.setMaximumSize(new Dimension(500,50));
		buttonPanel.setMaximumSize(new Dimension(530,530));
		upper.setMaximumSize(new Dimension(500,37));
		
		// Add the nested JPanels
		mainPanel.add(upper);
		mainPanel.add(buttonPanel);
		mainPanel.add(bottom);
		
		// Call the functions which add elements to the panels
		addUpper(upper);
		addButtons(buttonPanel);
		addBottom(bottom);
		
		// Set the start time to current time
		start = System.currentTimeMillis();
	}

	// Checks if the custom board is playable, displays a JOptionPane to tell user
	public void checkPlayable () {
		// Make a test GameState so the current game won't be affected by the test
		// and copy current.state() into it
		GameState test = new GameState();
		test.copy(current);
		
		// Simulate the "Lights Out" approach to solving the board
		for (int x = 0; x < 5; x++) {
			if (test.state()[x]) test.move(x + 5);
		}
		for (int x = 5; x < 10; x++) {
			if (test.state()[x]) test.move(x + 5);
		}
		for (int x = 10; x < 15; x++) {
			if (test.state()[x]) test.move(x + 5);
		}
		for (int x = 15; x < 20; x++) {
			if (test.state()[x]) test.move(x + 5);
		}
		
		// States of the last row which are winable
		if ((!test.state()[20] && !test.state()[21] && test.state()[22] && test.state()[23] && test.state()[24]) ||
				(!test.state()[20] && test.state()[21] && !test.state()[22] && test.state()[23] && !test.state()[24]) ||
				(!test.state()[20] && test.state()[21] && test.state()[22] && !test.state()[23] && test.state()[24]) ||
				(test.state()[20] && !test.state()[21] && !test.state()[22] && !test.state()[23] && test.state()[24]) ||
				(test.state()[20] && !test.state()[21] && test.state()[22] && test.state()[23] && !test.state()[24]) ||
				(test.state()[20] && test.state()[21] && !test.state()[22] && test.state()[23] && test.state()[24]) ||
				(test.state()[20] && test.state()[21] && test.state()[22] && !test.state()[23] && !test.state()[24]) ||
				(!test.state()[20] && !test.state()[21] && !test.state()[22] && !test.state()[23] && !test.state()[24])) {
			// Let the user know the board is playable
			JOptionPane.showMessageDialog(null, "This game is winable!", "YOU CAN WIN!", 1, buttonIconOn);
		}
		// If it's not winable, the user can't win, better let them know
		else JOptionPane.showMessageDialog(null, "Sorry, it is NOT possible to win this game.", "Lost Cause", 1 , buttonIconOff);
	}
	
	// Handles all events, such as button clicks
	@Override
	public void actionPerformed(ActionEvent e) {
		// For the "won" dialogue
		String moves = " moves!";
		
		// Recognize a move click and call ChangeButtons and changeMoves
		if (e.getActionCommand().matches("-?\\d+")) {
			changeButtons(Integer.parseInt(e.getActionCommand()));
			changeMoves();
			// Handle a win by telling the user and creating a new game
			if (current.isWon() && !custom) {
				// Make sure the time counter doesn't run while the window is open
				winTime = currentElapsedTime();
				pause = true;
				// Change the dialogue if the game is won in one move
				if (current.getMoves() == 1) moves = " move! CHEATER! Just kidding, you're awesome.";
					JOptionPane.showMessageDialog(null, "You won the game in " + current.getMoves() + moves + 
							"\nReady for a new one?", "You Won!", 1, buttonIconOff);
					pause = false;
					current.newGame();
					changeButtons(25);
					changeMoves();
			}
		}
		// Handle a "New Game" button push by creating a new game and resetting the moves counter
		if (e.getActionCommand().equals("new")) {
			current.newGame();
			changeButtons(25);
			changeMoves();
		}
		// Handle a custom game button push by clearing the board and setting the state to custom = true
		if (e.getActionCommand().equals("custom")) {
			if (custom) checkPlayable();
			custom = !custom;
			changeButtons(25);
			changeCustom();
			changeMoves();
		}
		// Handle a "Rules" button push by displaying the rules dialogue box
		if (e.getActionCommand().equals("rules")) {
			JOptionPane.showMessageDialog(null, "Clicking on a cell toggles the light in that cell and it's "
					+ "immediate\nneighbors.  The goal (as the name implies) is to "
					+ "turn out all the lights, "
					+ "\npreferrably in the fewest number of moves.", "The Rules", 1, buttonIconOn);
		}
		// Handle a "How to Win" button push by displaying the How to Win dialogue box
		if (e.getActionCommand().equals("hints")) {
			JOptionPane.showMessageDialog(null, "Although rarely the fastest way to win, there is a simple"
					+ "\nway to do it (if the board is winable) called \"Follow the Lights\".  Starting "
					+ "\nfrom the second row, click each light which is directly below an \"on\" light."
					+ "\nProceed to do this on each successive row until you get to the bottom. Then, find"
					+ "\nthe sequence below which matches your last row and click the corresponding"
					+ "\ntop buttons, then repeat the process, and you've won!\n\n"
					+ "..***       ...+.\n.*.*.       .+..+\n.**.*       +....\n*...*       "
					+ "...++\n*.**.       ....+\n**.**       ..+..\n"
					+ "***..       .+...", "How To Win", 1, buttonIconOff);
		}
	}
}
