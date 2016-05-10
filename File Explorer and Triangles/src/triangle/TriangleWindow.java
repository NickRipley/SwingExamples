package triangle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * 
 * @author Nicholas Lloyd u0949261
 * CS 1410 - Joe Zachary
 * 
 * This class is called by Sierpinski as a panel, to house the Sierpinski triangles.
 * It utilizes an ActionListener to watch the JComboBoxs passed from the caller and
 * repaint whenever the user changes one of them. Overrides the paintComponent() 
 * method of it's parent in order to paint it's space. No tests included as no methods
 * have independently testable return data.
 * 
 * KNOWN ISSUE - Due to mathematical rounding error of integers, the background triangles
 * will stick out 1 pixel off the sides.  The alternative is painting one big back-
 * ground, which also causes pixel length problems, or to not increase the background
 * triangles, causing small gaps throughout the image.
 *
 */

@SuppressWarnings("serial")
public class TriangleWindow extends JPanel implements ActionListener {

	// Create the combo boxes to be passed in
	private JComboBox fg;
	private JComboBox bg;
	private JComboBox level;
	
	// create the fore and back ground colors
	private Color fgC;
	private Color bgC;
	
	// construct the JPanel, make the JComboBoxs accessible from within the class
	@SuppressWarnings("rawtypes")
	public TriangleWindow(JComboBox fgt, JComboBox bgt, JComboBox levelt) {
		
		// Set the instance variable of fg to the one from caller, assign listener for it
		fg = fgt;
		fg.addActionListener(this);
		
		// Set the instance variable of bg to the one from caller, assign listener for it
		bg = bgt;
		bg.addActionListener(this);
		
		// Set the instance variable of level to the one from caller, assign listener for it
		level = levelt;
		level.addActionListener(this);
	}
	
	// Override the paintComponent
	@Override
	public void paintComponent(Graphics g) {
		// Paint the background using the parent method
		super.paintComponent(g);
		
		// Create the points
		Point one = new Point(0, (int) getHeight());
		Point two = new Point((int) getWidth() / 2, 0);
		Point three = new Point((int) getWidth(), (int) getHeight());
		
		// Make sure the colors are right
		changeColors();
		
		// Draw the Sierpinski Triangles
		drawTriangles(g, one, two, three, level.getSelectedIndex());
	}

	// Change the fore and background colors
	private void changeColors() {
		// Change the foreground color
		switch (fg.getSelectedIndex()) {
		case 0: fgC = Color.WHITE;
			break;
		case 1: fgC = Color.BLACK;
			break;
		case 2: fgC = Color.red;
			break;
		case 3: fgC = Color.BLUE;
			break;
		case 4: fgC = Color.GRAY;
			break;
		case 5: fgC = Color.GREEN;
			break;
		case 6: fgC = Color.ORANGE;
			break;
		case 7: fgC = Color.YELLOW;
			break;
		}
		
		// Change the background color
		switch (bg.getSelectedIndex()) {
		case 0: bgC = Color.WHITE;
			break;
		case 1: bgC = Color.BLACK;
			break;
		case 2: bgC = Color.red;
			break;
		case 3: bgC = Color.BLUE;
			break;
		case 4: bgC = Color.GRAY;
			break;
		case 5: bgC = Color.GREEN;
			break;
		case 6: bgC = Color.ORANGE;
			break;
		case 7: bgC = Color.YELLOW;
			break;
		}
	}
	
	// Recursive method to draw Sierpinski Triangles to the screen
	private void drawTriangles(Graphics g, Point one, Point two, Point three, int level) {
		
		// At level 0, draw foreground colored, right side up triangles
		if (level == 0) {
			// Set the color to foreground
			g.setColor(fgC);
			
			// Create points fillPolygon will recognize
			int[] x = { (int) one.getX(), (int) two.getX(), (int) three.getX() };
			int[] y = { (int) one.getY(), (int) two.getY(), (int) three.getY() };
			
			// Fill the triangle with foreground color
			g.fillPolygon(x, y, 3);
		}
		
		// If not at level 0, create center points and call itself three times with the new points
		// and then draw a background colored upside down triangle in the center.
		else {
			// Create center points for each line of the current triangle
			Point four = new Point(((int) three.getX() + (int) one.getX()) / 2, (int) one.getY());
			Point five = new Point(((int) one.getX() + (int) two.getX()) / 2, ((int) one.getY() + (int) two.getY()) / 2);
			Point six = new Point(((int) two.getX() + (int) three.getX()) / 2, ((int) two.getY() + (int) three.getY()) / 2);

			// Create new points fillPolygon will recognize
			int[] x = { (int) four.getX(), (int) five.getX() - 1, (int) six.getX() + 1 };
			int[] y = { (int) four.getY() + 1, (int) five.getY() - 1, (int) six.getY() - 1 };
			
			// Set the color to background
			g.setColor(bgC);
			
			// Fill the triangle with background color
			g.fillPolygon(x,y,3);
			
			// Call itself three times to include new points
			drawTriangles(g, one, five, four, level - 1);
			drawTriangles(g, five, two, six, level - 1);
			drawTriangles(g, four, six, three, level - 1);
		}
	}

	// Any change in the three JComboBoxs requires a repaint
	@Override
	public void actionPerformed(ActionEvent e) {
		// Any change in the JComboBoxs, repaint the panel
		repaint();
	}
}
