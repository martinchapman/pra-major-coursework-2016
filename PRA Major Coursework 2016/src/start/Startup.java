package start;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import api.jaws.Jaws;
import favourites.Favourites;
import search.Search;
import utils.GUIUtils;

/**
 * A frame to show the initial window, containing the buttons required to launch
 * either the search frame or the favourites frame.
 * 
 * THIS CLASS COVERS REQUIREMENT 7.1
 * 
 * @author Martin
 *
 */
public class Startup extends JFrame {
	
	/**
	 * A reference to the search frame
	 */
	private Search search;
	
	/**
	 * A reference to the favourites frame
	 */
	private Favourites favourites;
	
	/**
	 * Constructor takes existing references to the search and favourites frames
	 * 
	 * @param search
	 * @param favourites
	 */
	public Startup(Search search, Favourites favourites) {
		
		// Title given to menu frame
		super("Amnity Police");
		
		this.search = search;
		
		this.favourites = favourites;
		
		setupFrame();
		
		setupButtons();
		
		pack();
		
	}
	
	/**
	 * Sets up the layout of the menu frame
	 */
	private void setupFrame() {
		
		/* Terminates the program when the window this frame is closed, 
		 * as opposed to leaving the program executing (invisibly)
		 */
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel contentPanel = new JPanel();
		
		GUIUtils.setBorder(contentPanel);
		
		setContentPane(contentPanel);
		
		// Border layout allows for placement in either North, East, South, West or Centre
		setLayout(new BorderLayout());
		
		setPreferredSize(new Dimension(300, 350));
		
		// Adds an icon to the frame. Students will typically use their own icon here
		BufferedImage sharkIcon = null;
		
		try {
			
			// Nice to keep the icon in some logical place, such as a library folder
			sharkIcon = ImageIO.read(new File("lib/shark.png"));
			
		} catch (IOException e) {}
		
		// Lots of different ways to add images: my choice is via a JLabel
		JLabel picLabel = new JLabel( new ImageIcon(sharkIcon) );
		
		add(picLabel, BorderLayout.NORTH);
		
	}
	
	/**
	 * Show favourites is manipulated by multiple methods
	 * so it is made into a field.
	 */
	private JButton showFavourites;
	
	/**
	 * Adds actions to the buttons on the menu frame
	 */
	private void setupButtons() {
		
		JPanel buttons = new JPanel();
		
		/* Grid layout specifies the number of rows and columns 
		 * within which the components will flow.
		 */
		buttons.setLayout(new GridLayout(0, 1));
		
		add(buttons, BorderLayout.CENTER);
		
		//
		
		JButton showSearch = new JButton("Search");
		
		/* I favour anonymous action listeners for events that only occur once
		 * (e.g. showing the search frame) -- the students should take a similar
		 * approach to events, as opposed to, for example, creating entirely 
		 * separate classes for single, non-repeated actions.
		 */
		showSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				search.setVisible(true);
				
			}
			
		});
		
		buttons.add(showSearch);
		
		// 
		
		showFavourites = new JButton("Favourites");
		
		// Similar approach for showing the favourites frame
		showFavourites.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				favourites.setVisible(true);
				
			}
			
		});
		
		buttons.add(showFavourites);
		
		// Get a path to the favourite sharks of the currently `logged in' user
		Path path = Paths.get(favourites.getUserProfile());
		
		/* Uses Java 8 file reading syntax to quickly determine if a user has any
		 * favourite sharks (if there are any lines in their profile file). If they do not, 
		 * then the button to show favourite sharks is naturally disabled.
		 */
		try ( BufferedReader reader = Files.newBufferedReader(path) ) {
		   
			if (reader.lines().collect( Collectors.toList() ).size() == 0) {
				
				showFavourites.setEnabled(false);
			
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
		
	}
	
	/**
	 * Allows the favourites button to be enabled from outside
	 * this class
	 */
	public void enableFavouriteButton() {
		
		showFavourites.setEnabled(true);
		
	}

	/**
	 * Allows the favourites buttons to the disable from outside
	 * this class
	 */
	public void disableFavouriteButton() {
		
		showFavourites.setEnabled(false);

	}

	/**
	 * Main method: typically the students will place this in a separate file
	 * called `Main'.
	 * 
	 * @param args
	 */
	public static void main( String[] args ) {
		
		// Connecting to the remote Jaws API using an assigned private and public key
		Jaws jaws = new Jaws( "", "", false );
		
		/* Create all the frame objects at the beginning, and pass them the object 
		 * references relevant to their operation:
		 */
		
		/* The favourites frame and the search frame require access to the jaws API
		 * through the Jaws object reference.
		 */
		Favourites favouritesFrame = new Favourites(jaws);
		
		Search searchFrame = new Search(jaws);
		
		/* The startup (menu) frame needs references to the search and 
		 * favourites frame, in order to make them visible
		 */
		Startup startupFrame = new Startup(searchFrame, favouritesFrame);
		
		/* Additional public methods avoid null pointer references, that 
		 * would occur if objects were passed via constructors alone:
		 */
		
		/* The favourites frame needs to reference the menu frame (to disable
		 * access to itself if all favourites are removed) and to the search frame
		 * (to show information about a favourite shark when clicked).
		 */
		favouritesFrame.addPartnerFrames(startupFrame, searchFrame);
		
		/* The search frame needs to reference the menu frame (to enable access
		 * to favourites if new sharks are followed) and access to the favourites frame
		 * to add new sharks to the favourites list when they are followed.
		 */
		searchFrame.addPartnerFrames(startupFrame, favouritesFrame);
		
		startupFrame.setVisible(true);
		
	}

}
