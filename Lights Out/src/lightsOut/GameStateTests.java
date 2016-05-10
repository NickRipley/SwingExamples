package lightsOut;

import static org.junit.Assert.*;

import org.junit.Test;

public class GameStateTests {

	// Test that randInt() returns an integer under 25
	@Test
	public void testRandInt() {
		GameState test = new GameState();
		assertTrue(test.randInt() < 25);
	}
	
	// Test moves
	@Test
	public void testMove() {
		GameState test = new GameState();
		boolean[] test1 = new boolean[25];
		for (int x = 0; x < 25; x++){
			test1[x] = test.state()[x];
		}
		test.move(5);
		assertTrue(test1[6] != test.state()[6]);
		assertTrue(test1[0] != test.state()[0]);
		assertTrue(test1[10] != test.state()[10]);
	}
	
	// Test custom moves
	@Test
	public void testCustom() {
		GameState test = new GameState();
		boolean[] test1 = new boolean[25];
		for (int x = 0; x < 25; x++){
			test1[x] = test.state()[x];
		}
		test.moveCustom(5);
		assertTrue(test1[6] == test.state()[6]);
		assertTrue(test1[0] == test.state()[0]);
		assertTrue(test1[10] == test.state()[10]);
		assertTrue(test1[5] != test.state()[5]);
	}
	
	// Test that blankBoard[] makes all states false
	@Test
	public void testblankBoard() {
		GameState test = new GameState();
		test.blankBoard();
		for (int x = 0; x < 25; x++) {
			assertTrue(!test.state()[x]);
		}
	}
	
	// Test that GameState returns right number of moves
	@Test
	public void testGetMoves() {
		GameState test = new GameState();
		test.move(4);
		test.move(7);
		test.move(15);
		test.move(20);
		assertEquals(4, test.getMoves());
		test.move(24);
		test.move(16);
		assertEquals(6, test.getMoves());
	}
	
	// Test if it will report isWon correctly
	@Test
	public void testIsWon() {
		GameState test = new GameState();
		test.blankBoard();
		assertTrue(test.isWon());
	}
}
