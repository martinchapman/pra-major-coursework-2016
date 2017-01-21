package search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import api.jaws.Jaws;
import api.jaws.Ping;
import api.jaws.Shark;
import data.SharkTime;
import favourites.Favourites;
import start.Startup;
import utils.GUIUtils;

/**
 * A frame to show the search window, allow the user to interact with the main
 * features of the application, including searching for a following individual
 * sharks.
 *
 * REQUIREMENTS 7.1.1 and 7.1.2
 * 
 * @author Martin
 *
 */
public class Search extends JFrame {

	/**
	 * A reference to the Jaws API, from where shark data is obtained.
	 */
	private Jaws jaws;
	
	/**
	 * @param jaws
	 * @param startup
	 */
	public Search( Jaws jaws ) {
		
		super("Search");
	
		this.jaws = jaws;
		
		setupFrame();
		
		centre();
		
		left();
		
		south();
		
		pack();
		
	}
	
	/**
	 * A reference to the menu or startup frame
	 */
	private Startup startup;
	
	/**
	 * A reference to the favourites frame
	 */
	private Favourites favourites;
	
	/**
	 * Allows for startup and favourites frames to be specified from outside
	 * the class.
	 *  
	 * @param startup
	 * @param favourites
	 */
	public void addPartnerFrames( Startup startup, Favourites favourites ) {
		
		this.startup = startup;
		
		this.favourites = favourites;
		
	}
	
	/**
	 * Sets up the general appearance of the frame
	 */
	private void setupFrame() {
		
		JPanel contentPanel = new JPanel();
		
		GUIUtils.setBorder(contentPanel);
		
		setContentPane(contentPanel);
		
		setLayout(new BorderLayout());
		
		setPreferredSize(new Dimension(1200, 800));
		
	}
	
	/**
	 * Sets up the general appearance of the left
	 * hand side of the frame
	 */
	private void left() {
		
		JPanel leftPane = new JPanel();
		
		add( leftPane, BorderLayout.WEST );
		
		leftPane.setLayout( new BorderLayout() );
		
		GUIUtils.setBorder( leftPane,  BorderFactory.createLineBorder( Color.BLACK ) );
		
		tracker( leftPane );
		
	}
	
	/**
	 * Sets ip the general appearance of the tracker, part
	 * of the left hand side of the frame.
	 * 
	 * @param leftPane
	 */
	private void tracker( JPanel leftPane ) {
		
		//
		
		JPanel topLeft = new JPanel();
	
		topLeft.setLayout(new GridLayout(20, 1));
		
		leftPane.add( topLeft, BorderLayout.CENTER );
		
		topTracker( topLeft );
		
		//
		
		JPanel bottomLeft = new JPanel();
		
		bottomLeft.setLayout(new BorderLayout());
		
		leftPane.add( bottomLeft, BorderLayout.SOUTH );
		
		bottomLeft( bottomLeft );
		
	}
	
	/**
	 * Adds the option boxes required to refine the search for 
	 * sharks within a given time period.
	 * 
	 * @param topLeft
	 */
	private void topTracker( JPanel topLeft ) {
		
		JLabel header = new JLabel( "Shark Tracker" );
		
		GUIUtils.setBorder( header );
		
		topLeft.add(header);
		
		//
		
		GUIUtils.addSectionHeader( topLeft, "Tracking Range" );
		
		//
		
		JComboBox<String> trackingRange = new JComboBox<String>();
		
		trackingRange.addItem("Last 24 Hours");
		
		trackingRange.addItem("Last Week");
		
		trackingRange.addItem("Last Month");
		
		//
		
		topLeft.add(trackingRange);
		
		//
		
		GUIUtils.addSectionHeader(topLeft, "Gender");
		
		//
		
		JComboBox<String> gender = new JComboBox<String>();
		
		gender.addItem("Any");
		
		gender.addItem("Male");
		
		gender.addItem("Female");
		
		//
		
		topLeft.add(gender);
		
		//
		
		GUIUtils.addSectionHeader(topLeft, "Stage of Life");
		
		//
	
		JComboBox<String> stageOfLife = new JComboBox<String>();
		
		stageOfLife.addItem("Any");
		
		stageOfLife.addItem("Mature");
		
		stageOfLife.addItem("Immature");
		
		stageOfLife.addItem("Undetermined");
		
		//
		
		topLeft.add(stageOfLife);
		
		//
		
		GUIUtils.addSectionHeader(topLeft, "Tag Location");
		
		//
		
		JComboBox<String> tagLocation = new JComboBox<String>();
		
		tagLocation.addItem("Any");
		
		/* Use the API to list all the possible tag locations currently in the
		 * data set - students should definitely do this to ensure the correct
		 * functionality */
		for ( String location : jaws.getTagLocations() ) {
			
			tagLocation.addItem(location);
			
		}
		
		//
		
		topLeft.add(tagLocation);
		
		//
		
		topLeft.add(new JSeparator());
		
		//
		
		JButton search = new JButton("Search");
		
		topLeft.add( search );
		
		addSearchListener( search, trackingRange, gender, stageOfLife, tagLocation );
		
		//
		
		JButton statistics = new JButton("Statistics");
		
		topLeft.add( statistics );
		
		addStatisticsListener( statistics, trackingRange );	
	
	}
	
	/**
	 * Lays out the bottom left section of the frame, predominantly
	 * the shark of the day (advanced Requirement 2 - part of REQUIREMENT 7.3)
	 * 
	 * @param bottomLeft
	 */
	private void bottomLeft( JPanel bottomLeft ) {
		
		// Load shark icon in the same manner as the meny frame
		BufferedImage sharkIcon = null;
		
		try {
			
			sharkIcon = ImageIO.read(new File("lib/shark.png"));
			
		} catch (IOException e) {}
		
		JLabel picLabel = new JLabel( new ImageIcon(sharkIcon) );
		
		bottomLeft.add(picLabel, BorderLayout.CENTER);
		
		//
		
		/* Advanced Requirement 2 (part of REQUIREMENT 7.3): you *must* 
		 * ensure that the students implement the management of time here properly.
		 * 
		 * They must make some kind of record of when a shark of the day is chosen
		 * to ensure that they can monitor when 24 hours have passed, and a new shark
		 * must be chosen. 
		 * 
		 * For example, I have a file sharkOfTheDay.txt, where the expected content of 
		 * the file is the name of the shark of the day, and a timestamp of when that
		 * shark of the day was chosen, in the form <Timestamp>|<Shark name> (there's 
		 * probably an example in the lib folder).
		 */
		
		// Holds information read from the sharkOfTheDay file.
		String timeAndShark = null;
		
		// Holds either a new, randomly selected shark or the shark from the sharkOfTheDay file
		String shark = "";
		
		// Read information from the sharkOfTheDay file (may be empty)
		try {
		
			timeAndShark = Files.newBufferedReader(Paths.get("lib/sharkOfTheDay.txt")).readLine();
		
		} catch (IOException e) {
		
			e.printStackTrace();
		
		}
		
		try {
			
			// Find the timestamp of exactly 24 hours prior to the current time
			Calendar yesterday = Calendar.getInstance();
	        
			yesterday.add(Calendar.DATE, -1);  
			
			/* We need a new shark of the day IF 
			 * 1. No shark of the day currently exists (perhaps the program has never been run before)
			 * 2. The time 24 hours ago is AFTER the time the last shark of the day was selected 
			 *    (i.e. the last shark of the day was selected over 24 hours ago)
			 *    
			 * Note that the split here is due to the <Timestamp>|<Shark name> format of the sharkOfTheDay file
			 */
			if ( timeAndShark == null || yesterday.getTime().after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeAndShark.split("\\|")[0])) )  {
				
				// Get the list of all shark names from the API
				ArrayList<String> sharkNames = jaws.getSharkNames();
				 
				// Select a new shark of the day at random
				shark = sharkNames.get( (int)(Math.random() * sharkNames.size()) );
 
				try {
					
					// Write this new shark information to the sharkOfTheDay file, and include timestamp of current time (using Date object)
					Files.write(Paths.get("lib/sharkOfTheDay.txt"), new ArrayList<String>(Arrays.asList(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "|" + shark)));

				} catch (IOException e) {
					
					e.printStackTrace();
				
				}
				
			} else {
				
				// The shark of the day is the existing shark grabbed from the sharkOfTheDay file.
				shark = timeAndShark.split("\\|")[1];
				
			}
			
		} catch (ParseException e) {

			e.printStackTrace();
		
		}
		
		/* Display the current shark of the day on the frame, be this an existing one from the sharkOfTheDay
		 * file, or a new one just selected.
		 */
		bottomLeft.add(new JLabel("Shark of the day: " + shark, SwingConstants.CENTER), BorderLayout.SOUTH);
		
	}

	/**
	 * Given a list of pings (a name/time piece of tracking data for a shark),
	 * order those pings by time (most recent first) using the {@see data.SharkTime} utility
	 * class.
	 * 
	 * @param pings
	 * @return
	 */
	public ArrayList<SharkTime> orderedPings( ArrayList<Ping> pings ) {
		
		ArrayList<SharkTime> sharkTimes = new ArrayList<SharkTime>();
		
		for ( Ping ping : pings ) {
			
			sharkTimes.add(new SharkTime(ping.getName(), ping.getTime()));
				
		}
		
		Collections.sort(sharkTimes);
		
		return sharkTimes;
		
	}
	
	/**
	 * Calls the Jaws API for a specified (temporal) tracking range, passes the returned
	 * pings to be ordered, and then removes duplicates by passing the ordered pings into
	 * a linked hash set.
	 * 
	 * @param trackingRange
	 * @return unique shark times
	 */
	private LinkedHashSet<SharkTime> uniqueSharkTimes( JComboBox<String> trackingRange ) {
		
		ArrayList<Ping> pings = new ArrayList<Ping>();
		
		/* Take the appropriate list of pings from the API depending on the specified
		 * tracking range.
		 */
		if ( trackingRange.getSelectedItem().equals("Last 24 Hours") ) {
			
			pings = jaws.past24Hours();
			
		} else if ( trackingRange.getSelectedItem().equals("Last Week") ) {
			
			pings = jaws.pastWeek();
			
		} else {
			
			pings = jaws.pastMonth();
			
		}
		
		// Call pre-defined orderPings method
		ArrayList<SharkTime> sharkTimes = orderedPings( pings );
		
		// LinkedHashSet retains order and does not allow for the insertion of duplicates
		LinkedHashSet<SharkTime> uniqueSharkTimes = new LinkedHashSet<SharkTime>();
		
		/* Pass each shark time into a linked hash set. As a hash set only allows the 
		 * insertion of the same object one, and we specified that two SharkTime objects
		 * are the same when they have the same name, only the most recent (due to being 
		 * ordered) appearance of any shark will be retained. 
		 */
		for ( SharkTime sharkTime : sharkTimes ) {
			
			uniqueSharkTimes.add(sharkTime);
				
		}
		
		return uniqueSharkTimes;
		
	}
	
	/**
	 * Adds the main search functionality to the `search' button, which enables a user to
	 * find those sharks matching a set of search requirements.
	 * 
	 * @param search
	 * @param trackingRange
	 * @param gender
	 * @param stageOfLife
	 * @param tagLocation
	 */
	private void addSearchListener( JButton search, JComboBox<String> trackingRange, JComboBox<String> gender, JComboBox<String> stageOfLife, JComboBox<String> tagLocation ) {
		
		search.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				// Clear any existing search results on the frame, as a new search is being performed.
				clearSearchResults();
				
				/* For each possible shark ping that we might display (i.e. the pings fall within the tracking
				 * range, without duplicates), determine if the shark associated with that ping matches
				 * the search preferences expressed by the user on the frame:
				 */
				for ( SharkTime sharkTime : uniqueSharkTimes( trackingRange ) ) {
					
					// Get all the shark details attached to this name
					Shark shark = jaws.getShark( sharkTime.getName() );
					
					/* Three boolean tests determine if a shark is included in the results. The tests relate to
					 * the attribute dropdowns (Gender, Stage of Life and Tag Location) on the left of the frame.
					 * 
					 * If `Any' is selected in a search specification dropdown, then the boolean
					 * test associated to that dropdown is set to true, otherwise a test is performed
					 * to see if the attribute value of this shark matches the requested attribute value in the dropdown
					 * (e.g. gender dropdown says 'Male', and this shark is male)
					 */
					boolean genderSearch = gender.getSelectedItem().equals("Any") ? true : shark.getGender().equals(gender.getSelectedItem());
					
					boolean lifeSearch = stageOfLife.getSelectedItem().equals("Any") ? true : shark.getStageOfLife().equals(stageOfLife.getSelectedItem());
					
					boolean tagSearch = tagLocation.getSelectedItem().equals("Any") ? true : shark.getTagLocation().equals(tagLocation.getSelectedItem());
					
					// If all three boolean tests pass, then this shark is added to the results.
					if ( genderSearch && lifeSearch && tagSearch ) {
						
						addToSearchResults( sharkResult(shark, sharkTime.getTime()) );
					
					}
					
				}
				
				/* If nothing has been added to the search results, then there are no results and a 
				 * label is added stating this.
				 */
				if ( bottomRight.getComponentCount() == 0 ) {
					
					bottomRight.add(new JLabel("No results."));
					
				}
				
				/* Scales the panel in the bottom right of the screen (search results area) according to
				 * how many results there are.
				 */
				bottomRight.setPreferredSize(new Dimension(200, bottomRight.getComponentCount() * 300));
				
				/* Revalidation shows new elements that have been dynamically added to the frame (in this 
				 * case search result panels).
				 */
				bottomRight.revalidate();
				
				// Set the position of the results vertical scroll bar to the top
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					
				   public void run() { 
				
					   resultsScroll.getVerticalScrollBar().setValue(0);
				   
				   }
				
				});
				
			}
			
		});
		
	}
	
	/**
	 * Adds the action to perform when the `statistics' button is pressed (Advanced Requirement 3 - 
	 * PART OF REQUIREMENT 7.3).
	 * 
	 * @param statistics
	 * @param trackingRange
	 */
	private void addStatisticsListener( JButton statistics, JComboBox<String> trackingRange ) {
		
		Hashtable<String, Integer> genders = new Hashtable<String, Integer>();
		
		Hashtable<String, Integer> stagesOfLife = new Hashtable<String, Integer>();
		
		Hashtable<String, Integer> tagLocations = new Hashtable<String, Integer>();
		
		statistics.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				/* Use the same method as search (uniqueSharkTimes) to grab all the relevant
				 * ping data from the specified tracking range *without* duplicates.
				 */
				for ( SharkTime sharkTime : uniqueSharkTimes( trackingRange ) ) {
					
					// Get the data associated with each shark attributed to each ping time
					Shark shark = jaws.getShark( sharkTime.getName() );
					
					// Store this data in the pre-defined tables
					GUIUtils.insertOrIncrement( genders, shark.getGender() );
					
					GUIUtils.insertOrIncrement( stagesOfLife, shark.getStageOfLife() );
					
					GUIUtils.insertOrIncrement( tagLocations, shark.getTagLocation() );
					
				}
				
				// Pass these tables to the statistics frame for processing
				new Statistics( genders, stagesOfLife, tagLocations ).setVisible(true);
				
			}
			
		});
			
	}
	
	
	/**
	 * Clears all results from the right hand side of the frame
	 */
	public void clearSearchResults() {

		bottomRight.removeAll();
		
	}
	
	/**
	 * Adds a component (search result) to the search result portion of the frame
	 * 
	 * @param component
	 */
	public void addToSearchResults(JComponent component) {
		
		bottomRight.add(component);
		
		bottomRight.revalidate();
		
	}

	/**
	 * Constructs a JPanel containing all information pertinent to a shark
	 * ping, including information about the shark, and the time of the ping.
	 * 
	 * @param shark
	 * @param lastPing
	 * @return A JPanel containing components (which is nice for reusability)
	 */
	public JPanel sharkResult( Shark shark, String lastPing ) {
		
		JPanel sharkResult = new JPanel(new BorderLayout());
		
		/* Adds all the attributes and values of a shark (some correspond to those
		 * selected in the attribute dropdowns, some are extra).
		 */
		
		JPanel sharkResultDetails = new JPanel();
		
		sharkResultDetails.setLayout(new GridLayout(0, 2, 3, 3));
		
		sharkResult.add(sharkResultDetails, BorderLayout.NORTH);
		
		details( shark, sharkResultDetails );
		
		// Adds a description about the shark
		
		JPanel sharkResultDescription = new JPanel();
		
		sharkResultDescription.setLayout(new GridLayout(0, 1, 2, 2));
		
		sharkResult.add(sharkResultDescription, BorderLayout.CENTER);
		
		description( shark, sharkResultDescription );
		
		// Adds the option to follow (or unfollow) a shark
		
		JPanel followPane = new JPanel();
		
		followPane.setLayout(new BorderLayout());
		
		sharkResult.add(followPane, BorderLayout.SOUTH);
		
		follow( followPane, lastPing, shark.getName() );
		
		//
		
		return sharkResult;
		
	}
	
	/**
	 * Adds the appropriate text details to a JPanel, based upon
	 * a Shark object containing data.
	 * 
	 * @param shark
	 * @param sharkResultDetails
	 */
	private void details( Shark shark, JPanel sharkResultDetails ) {
		
		sharkResultDetails.add(new JLabel("Name:"));
		
		sharkResultDetails.add(new JLabel(shark.getName()));
		
		sharkResultDetails.add(new JLabel("Gender:"));
		
		sharkResultDetails.add(new JLabel(shark.getGender()));
		
		sharkResultDetails.add(new JLabel("Stage of Life:"));
		
		sharkResultDetails.add(new JLabel(shark.getStageOfLife()));
		
		sharkResultDetails.add(new JLabel("Species:"));
		
		sharkResultDetails.add(new JLabel(shark.getSpecies()));
		
		sharkResultDetails.add(new JLabel("Length:"));
		
		sharkResultDetails.add(new JLabel(shark.getLength()));
		
		sharkResultDetails.add(new JLabel("Weight:"));
		
		sharkResultDetails.add(new JLabel(shark.getWeight()));
		
	}
	
	/**
	 * Adds a description about a shark to the provided results panel, as
	 * a block of text.
	 * 
	 * @param shark
	 * @param sharkResultDescription
	 */
	private void description( Shark shark, JPanel sharkResultDescription ) {
		
		sharkResultDescription.add(new JLabel("Description:"));
		
		JTextArea description = new JTextArea(shark.getDescription(), 10, 1);
		
		description.setPreferredSize(new Dimension(0, 500));
		
		description.setOpaque(false);
		
		description.setLineWrap(true);
		
		sharkResultDescription.add(description);
		
	}
	
	/**
	 * A temporary, local record of the number of sharks being 
	 * followed.
	 * 
	 */
	private int following = 0;
	
	/**
	 * Sets up the ability to either follow or unfollow a shark, from
	 * within a shark result panel. When a shark is followed, they are
	 * added to a user's profile, and displayed in the favourite sharks
	 * window.
	 * 
	 * Part of REQUIREMENT 7.2.3 -- Don't forget my favourites
	 * 
	 * @param followPane
	 * @param lastPing
	 * @param sharkName
	 */
	private void follow( JPanel followPane, String lastPing, String sharkName ) {
		
		// Add last ping to follow pane for aesthetics.
		JLabel ping = new JLabel( "Last ping: " + lastPing );
		
		followPane.add(ping, BorderLayout.WEST);
		
		// 
		
		JButton follow = new JButton("Follow");
		
		/* Determine if this shark exists in the user's profile (a text file storing
		 * the sharks they are following. If it is, then set the text on the follow
		 * button to following, to demonstrate that this shark is already being followed.
		 */
		Path path = Paths.get(favourites.getUserProfile());
		
		try (BufferedReader reader = Files.newBufferedReader(path)) {
		   
			List<String> favourites = reader.lines().collect( Collectors.toList() );
			
			/* Also grab how many sharks in total are being followed, based on the
			 * number of lines in the user's profile. This is used to understand when
			 * to hide and show access to the favourites frame.
			 */
			following = favourites.size();
			
			if (favourites.contains(sharkName)) {
		    
				follow.setText("Following");
				
			}
			
		} catch (IOException e2) {

			e2.printStackTrace();
		
		}
		
		/* When the follow button is clicked, perform an action depending upon whether
		 * the button currently reads 'follow' (the shark is not currently being followed)
		 * or 'following' (the shark is currently being followed).
		 */
		follow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				// If the shark is being followed currently when the button is pressed
				if ( follow.getText().equals("Following")) {
					
					// Change the text to follow, so that the shark can be followed again 
					follow.setText("Follow");
					
					// Decrement the local record of how many sharks are being followed
					following--;
					
					/* If a user's last favourite shark has now been `unfollowed', disable
					 * access to the favourites frame, as it will now contain no favourites.
					 */
					if ( following == 0 ) {
						
						startup.disableFavouriteButton();
						
					}
					
					try (BufferedReader reader = Files.newBufferedReader(path)) {
						
						/* Java 8 syntax to filter the list to remove the name of the shark to which this follow button pertains.
						 * (This syntax is not expected, so do not penalise for less efficient implementations)
						 */
						List<String> updatedLines = reader.lines().filter(s -> !s.contains(sharkName)).collect(Collectors.toList());
						 
						Files.write(path, updatedLines);
							
					} catch (IOException e1) {
			
						e1.printStackTrace();
					
					}
				
				// If the shark is not yet being followed:
				} else {
					
					// Set the text to following
					follow.setText("Following");
					
					// Increase the number of sharks being followed
					following++;
					
					// If this is the first shark being followed, enable access to the favourites frame
					if ( following == 1) {
						
						startup.enableFavouriteButton();
						
					}
					
					try {
						
						// Add the new shark being followed to the user's profile
						Files.write(path, new ArrayList<String>(Arrays.asList(new String[] {sharkName})), StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
					
					} catch (IOException e1) {
						
						e1.printStackTrace();
					
					}
					
				}
				
				/* Force a refresh of the list in the favourites frame to either show a
				 * new shark being followed, or remove a shark that has just been unfollowed.
				 */
				favourites.updateFavouritesList();
				
			}
			
		});
		
		follow.setMaximumSize(new Dimension(50, 5));
		
		followPane.add(follow, BorderLayout.EAST);
		
		followPane.add(new JSeparator(), BorderLayout.SOUTH);
		
	}
	
	/**
	 * Scroll pane for results is specified as a field in order
	 * to allow for manipulation when the number of results is known
	 */
	private JScrollPane resultsScroll;
	
	/**
	 * The bottom right panel is contained within the resultsScroll,
	 * and also needs to be manipulated in a number of places
	 */
	private JPanel bottomRight;
	
	/**
	 * Sets up the general layout of the centre of the frame
	 */
	private void centre() {
		
		JPanel centre = new JPanel();
		
		GUIUtils.setBorder( centre,  BorderFactory.createLineBorder( Color.BLACK ) );
		
		centre.setLayout( new BorderLayout() );
		
		add( centre, BorderLayout.CENTER );
		
		//
		
		bottomRight = new JPanel();
		
		resultsScroll = new JScrollPane(bottomRight);
		
		centre.add( resultsScroll, BorderLayout.CENTER );
		
		bottomRight( bottomRight );
		
	}

	/**
	 * Sets up the general layout of the bottom right of the frame,
	 * to which the shark result panels will be added.
	 * 
	 * @param bottomRight
	 */
	private void bottomRight( JPanel bottomRight ) {
		
		bottomRight.setLayout(new GridLayout(0, 1));
		
	}
	
	/**
	 * Sets up the general layout of the bottom of the frame.
	 */
	private void south() {
		
		JPanel south = new JPanel();
		
		south.setLayout(new BorderLayout());
		
		add(south, BorderLayout.SOUTH);
		
		// This acknowledgement must be shown!
		JTextArea acknowledgement = new JTextArea( jaws.getAcknowledgement() );
		
		acknowledgement.setOpaque(false);
		
		acknowledgement.setLineWrap(true);
		
		GUIUtils.setBorder(acknowledgement);
		
		acknowledgement.setEditable(false);
		
		south.add( acknowledgement, BorderLayout.CENTER );
		
	}

}
