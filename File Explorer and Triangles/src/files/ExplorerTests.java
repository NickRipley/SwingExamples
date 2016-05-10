package files;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

/**
 * 
 * @author Nicholas Lloyd u0949261
 * Test cases for the Explorer class, testing the four methods which return actual data.
 *
 */

public class ExplorerTests {

	// Test findNumberFiles(), make sure it returns the right number.
	@Test
	public void testNumber(){
		File test = new File("/Users/Nick/Documents/Aspyr");
		File testa = new File("/Users/Nick/Documents/Klei");
		Explorer test1 = new Explorer();
		assertEquals(73, test1.findNumberFiles(test));
		assertEquals(269, test1.findNumberFiles(testa));
	}
	
	// Test findLargest(), make sure it returns the largest file/folder
	@Test
	public void testLargest(){
		File test = new File("/Users/Nick/Documents/Aspyr");
		File testa = new File("/Users/Nick/Documents/Klei");
		Explorer test1 = new Explorer();
		assertEquals("Localization-BaseGame.db", test1.findLargest(test).getName());
		assertEquals("updater.zip", test1.findLargest(testa).getName());
	}
	
	// Test findNumberofLarge(), make sure it returns the correct number
	@Test
	public void testNumberOfLarge() {
		File test = new File("/Users/Nick/Documents/Aspyr");
		File testa = new File("/Users/Nick/Documents/Klei");
		Explorer test1 = new Explorer();
		assertEquals(17, test1.findNumberofLarge(test));
		assertEquals(26, test1.findNumberofLarge(testa));
	}
	
	// Test getNewest(), make sure it returns most recently modified file
	@Test
	public void testNewest() {
		File test = new File("/Users/Nick/Documents/Aspyr");
		File testa = new File("/Users/Nick/Documents/Klei");
		Explorer test1 = new Explorer();
		assertEquals("Aspyr", test1.getNewest(test).getName());
		assertEquals("log.txt", test1.getNewest(testa).getName());
	}
}
