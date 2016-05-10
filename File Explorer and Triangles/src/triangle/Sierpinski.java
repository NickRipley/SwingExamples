package triangle;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author Nicholas Lloyd u0949261
 * CS 1410 - Joe Zachary
 * 
 * This class is a JFrame, which encapsulates three JPanels, two content holding panes
 * and a main one to house them. The top panel will hold three JComboBoxs which will
 * be used to determine user choices of foreground color, background color, and level.
 * The second panel is an instance of the TriangleWindow class, which is an extension
 * of JPanel. This panel will house Sierpinski triangles.  No test cases are included
 * as no methods contain independently testable data returns.
 *
 */

@SuppressWarnings("serial")
public class Sierpinski extends JFrame {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Sierpinski () {
		// Name the Frame
		setTitle("Sierpinski Triangles");
		
		// Give the initial size
		setPreferredSize(new Dimension(600,600));
		
		// Exit on close
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Make a panel to encase the others
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);
        
        // Create the options pane
        JPanel options = new JPanel();
        options.setLayout(new GridLayout(2,3));
        
        // Create the labels, align them in the center of their cells
        JLabel fgCol = new JLabel("Choose Foreground Color:");
        fgCol.setHorizontalAlignment(JLabel.CENTER);
        JLabel bgCol = new JLabel("Choose Background Color:");
        bgCol.setHorizontalAlignment(JLabel.CENTER);
        JLabel levell = new JLabel("Choose a level:");
        levell.setHorizontalAlignment(JLabel.CENTER);
        
        // Create the drop down color and level options
        String[] colorOptions = { "White", "Black", "Red", "Blue", "Grey", "Green", "Orange", "Yellow" };
        JComboBox fgColor = new JComboBox(colorOptions);
        JComboBox bgColor = new JComboBox(colorOptions);
        JComboBox level = new JComboBox( new String[] { "0","1","2","3","4","5","6","7","8","9" } );
        fgColor.setSelectedIndex(1);
        
        // Add the menus to the panel
        options.add(fgCol);
        options.add(bgCol);
        options.add(levell);
        options.add(fgColor);
        options.add(bgColor);
        options.add(level);
        
        // Add the two panels to the enclosure
        mainPanel.add(options, "North");
        mainPanel.add(new TriangleWindow(fgColor, bgColor, level), "Center");
        
        // Magic... (I still don't understand why pack() works, I'm just glad it does)
        pack();
	}
	
	// Get the ball rolling...
	public static void main (String[] args) {
		Sierpinski main = new Sierpinski();
		main.setVisible(true);
	}

}
