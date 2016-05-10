package lightsOut;
import java.util.Random;

/**
 * @author Nicholas Lloyd u0949261
 * 
 *  Represents a boolean[] board such that:
 *  
 *  0  1  2  3  4
 *  5  6  7  8  9
 *  10 11 12 13 14
 *  15 16 17 18 19
 *  20 21 22 23 24
 *  
 */

public class GameState {
	
	// Create the representation of the board using boolean onOff[], and the number of moves
	// false stands for off, true stands for on
	private boolean[] onOff = new boolean[25];
	private int moves = 0;
	
	// Returns a random integer between 0 and 25
	public int randInt() {
		Random x = new Random();
		return x.nextInt(25);
	}
	
	// Sets up a new game and generates random moves to ensure it's winable
	public void newGame() {
		// set all states to off
		for (int x = 0; x < 25; x++) {
			onOff[x] = false;
		}	
		// generate a series of random moves
		for (int x = 25; x > 0; x--){
			move(randInt());
		}
		// reset moves
		moves = 0;
	}
	
	// Constructor: starts by setting up a new game
	public GameState() {
		newGame();
	}
	
	// changes the state of onOff[] for the clicked button to it's opposite
	public void moveCustom(int clicked) {
		onOff[clicked] = !onOff[clicked];	
	}
	
	// makes all booleans in onOff[] false
	public void blankBoard() {
		for (int x = 0; x < 25; x++) {
			onOff[x] = false;
		}
		moves = 0;
	}
	
	// takes the int of the clicked button and changes onOff[] appropriately, also adds 1 to moves
	public void move(int clicked) {
		onOff[clicked] = !onOff[clicked];
		if (clicked - 5 >= 0)
			onOff[clicked - 5] = !onOff[clicked - 5];
		if (clicked + 5 <= 24)
			onOff[clicked + 5] = !onOff[clicked + 5];
		if (clicked != 4 && clicked != 9 && clicked != 14 && clicked != 19 && clicked != 24)
			onOff[clicked + 1] = !onOff[clicked + 1];
		if (clicked != 0 && clicked != 5 && clicked != 10 && clicked != 15 && clicked != 20)
			onOff[clicked - 1] = !onOff[clicked - 1];
		
		moves ++;
	}
	
	// returns the state of the board
	public boolean[] state(){
		return onOff;
	}
	
	// returns the number of moves made
	public int getMoves() {
		return moves;
	}
	
	// returns true if game is won
	public boolean isWon() {
		boolean isWon = true;
		for (boolean x : onOff) {
			if (x) isWon = false;
		}
		return isWon;
	}
	
	// Copy one GameState to another
	public void copy(GameState x){
		for (int y = 0; y < 25; y++) {
			onOff[y] = x.state()[y];
		}
	}
}
