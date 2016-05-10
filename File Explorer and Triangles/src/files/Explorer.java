package files;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 
 * @author Nicholas Lloyd u0949261
 * This program displays a resizable window with a single button, which when pressed
 * will give the user a window to choose a directory in their filesystem.  When
 * chosen, the program will display the information for the directory, including
 * which file contained in it (or below it) is the largest, what the number of files
 * contained (or below it) is, which file below it was most recently modified,
 * and the number of files contained (or below it) which exceed 100,000 bytes in 
 * size.  Test cases are included in the ExplorerTests class.
 *
 */

@SuppressWarnings("serial")
public class Explorer extends JFrame implements ActionListener {

	// Create the container for the largest file information
	private File largestFile;
	
	// Create the container for the number of files
	private long numberOfFiles;
	
	// Create the container for the number of files larger than 100k bytes
	private int numberOfLargeFiles;
	
	// Create the container for the most recent file or folder
	private File mostRecent;
	
	// Create the main JPanel
	private JPanel mainPanel = new JPanel();
	
	// Create a sub panel for the info
	private JPanel sub = new JPanel();
	
	// Create the info JLabels
	private JLabel working = new JLabel(" ");
	private JLabel chosenDir = new JLabel("Chosen Directory:");
	private JLabel chosenDira = new JLabel(" ");
	private JLabel largeFile = new JLabel("The largest file (or folder) in the selected Directory is:");
	private JLabel largeFilea = new JLabel(" ");
	private JLabel largeFile1 = new JLabel("and it's size is:");
	private JLabel largeFile1a = new JLabel(" ");
	private JLabel total = new JLabel("Total number of files and folders contained in this directory:");
	private JLabel totala = new JLabel(" ");
	private JLabel large = new JLabel("Total number of files over 100,000 bytes:");
	private JLabel largea = new JLabel(" ");
	private JLabel newest = new JLabel("The file (or folder) most recently modified is:");
	private JLabel newesta = new JLabel(" ");
	private JLabel newest1 = new JLabel("and it was modified on:");
	private JLabel newest1a = new JLabel(" ");
	
	// Create the File Chooser Button
	private JButton fileChooser = new JButton();
	
	// Grab the jpg for the button
	private ImageIcon button = new ImageIcon("Assets/button.jpg");
	
	// Create a JFileChooser
	private JFileChooser chooser;
	
	// Make a file object to house the chosen directory
	private File chosen;
	
	// What happens when the button is pressed? This.
	public void buttonPress() {
		
		// Instantiate the JFileChooser
		chooser = new JFileChooser(); 
		
		// Set the directory to wherever you are
	    chooser.setCurrentDirectory(new java.io.File("."));
	    
	    // Set the title for the box
	    chooser.setDialogTitle("Please Choose a Folder");
	    
	    // Make sure the user can only choose a directory
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    
	    // Only do something if given a valid folder
	    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
	        
	    	// Put the file into chosen
	    	chosen = chooser.getSelectedFile();
	        
	        // Attempt to get the number of files
	        try {
	        	numberOfFiles = findNumberFiles(chosen);
	        }
	        catch (Exception e) {
	        	JOptionPane.showMessageDialog(null, "Something was wrong with the given directory");
	        }

	        // Attempt to get the largest file
	        try {
	        	largestFile = findLargest(chosen); 
	        }
	        catch (Exception e) {
	        	JOptionPane.showMessageDialog(null, "Something was wrong with the given directory");
	        }
	        
	        // Attempt to get the number of large files
	        try {
	        	numberOfLargeFiles = findNumberofLarge(chosen);
	        }
	        catch (Exception e) {
	        	JOptionPane.showMessageDialog(null, "Something was wrong with the given directory");
	        }
	        
	     // Attempt to get the number of large files
	        try {
	        	mostRecent = getNewest(chosen);
	        }
	        catch (Exception e) {
	        	JOptionPane.showMessageDialog(null, "Something was wrong with the given directory");
	        }
	        
	        // Update the JLabels
	        changeLabels();
	        working.setText(" ");
	    }
	    
	    // If they didn't choose a valid option, or exited the window display this
	    else {
	    	JOptionPane.showMessageDialog(null, "Please choose a valid folder", "Incorrect Choice", 1);
	    }
	}
	
	// Find the number of files and folders contained in the specified folder
	public long findNumberFiles(File x) {
		
		// If the file isn't a directory, return one (count symbolic links as files so no loops are created)
		if (x.isFile() || Files.isSymbolicLink(x.toPath())) return 1;
		
		// If the file is a directory, create a count (starting at one since we count the directory)
		// and then add all the number of files inside it
        else if (x.isDirectory()) {
            long count = 1;
            for (File f: x.listFiles()) {
                count += findNumberFiles(f);
            }
            return count;
        }
        else return 0;
	}
	
	// Find the largest file / folder in the specified directory
	public File findLargest(File x) {
		
		// If it's a file, return itself
		if (!x.isDirectory()) return x;
		
		// If it's a directory
		else {
			File isLargest = x;
			for (File t: x.listFiles()) {
				File fileReturned = findLargest(t);
				if (isLargest.length() < fileReturned.length())
					isLargest = fileReturned;
			}
		return isLargest;
		}
	}
	
	// Find the total number of files larger than 100k
	public int findNumberofLarge(File f) {
		
		// If it's a file, return one if it's larger than 100k, 0 if not
		if (!f.isDirectory()) {
			if (f.length() > 100000) return 1;
			else return 0;
		}
		
		// If it's not a file, go through and count up the files under it
		else {
			int count = 0;
			for (File y: f.listFiles()) count += findNumberofLarge(y);
		return count;
		}
	}
	
	// Get the most recently changed file
	public File getNewest(File x) {
		
		// If x is file:
		if (!x.isDirectory()) {
			return x;
		}
		
		// If it's a directory, recurse through it, finding newest
		else {
			File isNewest = x;
			for (File t: x.listFiles()) {
				File fileReturned = getNewest(t);
				if (new Date(isNewest.lastModified()).before(new Date(fileReturned.lastModified())))
					isNewest = fileReturned;
			}
		return isNewest;
		}
	}
	
	// Change the labels
	public void changeLabels() {
		
		// Create the format for the date
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-M-dd hh:mm");
		
		// Change the font to bold to make it stick out, update the labels
		Font forThis = chosenDira.getFont();
		chosenDira.setFont(forThis.deriveFont(forThis.getStyle() | Font.BOLD));
		chosenDira.setText("" + chosen.getName());
		largeFilea.setFont(forThis.deriveFont(forThis.getStyle() | Font.BOLD));
		largeFilea.setText("" + largestFile.getName());
		largeFile1a.setFont(forThis.deriveFont(forThis.getStyle() | Font.BOLD));
		largeFile1a.setText("" + largestFile.length() + " bytes");
		totala.setFont(forThis.deriveFont(forThis.getStyle() | Font.BOLD));
		totala.setText("" + numberOfFiles);
		largea.setFont(forThis.deriveFont(forThis.getStyle() | Font.BOLD));
		largea.setText("" + numberOfLargeFiles);
		newesta.setFont(forThis.deriveFont(forThis.getStyle() | Font.BOLD));
		newesta.setText("" + mostRecent.getName());
		newest1a.setFont(forThis.deriveFont(forThis.getStyle() | Font.BOLD));
		newest1a.setText("" + dt1.format(mostRecent.lastModified()));
	}
	
	// Construct the object
	public Explorer() {
		
		// Make sure it exits properly
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set the title
		setTitle("Explorer");
		
		// Set the size
		setPreferredSize(new Dimension(400,400));
		
		// Set up the panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		// Set mainPanel as the content Pane
		setContentPane(mainPanel);
				
		// Set the button
		fileChooser = new JButton(button);
		fileChooser.setBorder(BorderFactory.createEmptyBorder());
		fileChooser.setContentAreaFilled(false);
		fileChooser.addActionListener(this);
		fileChooser.setActionCommand("chooser");
		
		// Add the button to the panel
		mainPanel.add(fileChooser, BorderLayout.PAGE_START);
		
		// Set the layout and alignment for sub and it's components
		sub.setLayout(new BoxLayout(sub, BoxLayout.Y_AXIS));
		chosenDir.setAlignmentX(CENTER_ALIGNMENT);
		total.setAlignmentX(CENTER_ALIGNMENT);
		large.setAlignmentX(CENTER_ALIGNMENT);
		largeFile.setAlignmentX(CENTER_ALIGNMENT);
		largeFile1.setAlignmentX(CENTER_ALIGNMENT);
		largeFile1a.setAlignmentX(CENTER_ALIGNMENT);
		newest.setAlignmentX(CENTER_ALIGNMENT);
		chosenDira.setAlignmentX(CENTER_ALIGNMENT);
		totala.setAlignmentX(CENTER_ALIGNMENT);
		largea.setAlignmentX(CENTER_ALIGNMENT);
		largeFilea.setAlignmentX(CENTER_ALIGNMENT);
		newesta.setAlignmentX(CENTER_ALIGNMENT);
		newest1.setAlignmentX(CENTER_ALIGNMENT);
		newest1a.setAlignmentX(CENTER_ALIGNMENT);
		working.setAlignmentX(CENTER_ALIGNMENT);
		
		// Add the label for if it's working
		sub.add(working);
		
		// Make some filler
		Filler filler = new Box.Filler(new Dimension(50,50), new Dimension(50,50), new Dimension(50,50));
		filler.setAlignmentX(CENTER_ALIGNMENT);
		sub.add(filler);
		
		// Add subs components
		sub.add(chosenDir);
		sub.add(chosenDira);
		sub.add(total);
		sub.add(totala);
		sub.add(large);
		sub.add(largea);
		sub.add(largeFile);
		sub.add(largeFilea);
		sub.add(largeFile1);
		sub.add(largeFile1a);
		sub.add(newest);
		sub.add(newesta);
		sub.add(newest1);
		sub.add(newest1a);
		mainPanel.add(sub, BorderLayout.CENTER);
		
		// Make the magic happen...
		pack();
		
	}
	
	// Get the ball rolling
	public static void main (String[] args) {
		Explorer main = new Explorer();
		main.setVisible(true);
	}
	
	// If the button is pressed, catch it
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// If it takes awhile because user choose high level directory, display "WORKING..."
		working.setText("WORKING...");
		
		// Call buttonPress() to make all the operations go
		if (e.getActionCommand().equals("chooser")) buttonPress();
	}

}
