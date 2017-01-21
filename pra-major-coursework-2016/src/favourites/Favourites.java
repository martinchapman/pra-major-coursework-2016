package favourites;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import api.jaws.Jaws;
import api.jaws.Location;
import data.SharkTime;
import search.Search;
import start.Startup;
import utils.GUIUtils;

/**
 * Provides access to a list of favourite sharks, and associated functionality
 * such as display the location of those sharks on a map, or triggering the search
 * frame to display the most recent information about a favourite shark.
 *
 * REQUIREMENT 7.2
 *
 * @author Martin
 *
 */
public class Favourites extends JFrame {
	
	/**
	 * A copy of the object used to access the Jaws API (shark data)
	 */
	private Jaws jaws;
	
	/**
	 * Google API access string used for Shark location queries
	 */
	private static String GOOGLE_API = "AIzaSyA8QjnDvzKc89i99NZsuBE7byJxymFB_AE";
	
	/**
	 * The path to the user's profile, containing their favourite sharks
	 */
	private String userProfile;
	
	/**
	 * Returns the profile (a path to a simple text file
	 * in which favourite shark information is stored) of
	 * the currently 'logged in' user.
	 * 
	 * @return path to profile
	 */
	public String getUserProfile() {
		
		return userProfile;
		
	}
	
	/**
	 * Setting up the favourites frame requires the passing of an
	 * object from which shark data can be derived.
	 * 
	 * @param jaws
	 */
	public Favourites( Jaws jaws ) {
		
		super("Favourites");
		
		setMinimumSize(new Dimension(100, 300));
		
		this.jaws = jaws;
		
		userProfile = "lib/default.txt";
		
		setupFrame();
		
		sharkList();
		
		sharkMap();
		
		pack();
		
	}
	
	/**
	 * A reference to the menu or startup frame
	 */
	private Startup startup;
	
	/**
	 * A reference to the search frame
	 */
	private Search search;
	
	/**
	 * Allows for the menu and search frame to be set after an object
	 * of this class has been constructed.
	 * 
	 * @param startup
	 * @param favourites
	 */
	public void addPartnerFrames( Startup startup, Search search ) {
		
		this.startup = startup;
		
		this.search = search;
		
	}
	
	/**
	 * Sets up the general layout of the frame, and also adds a menu bar
	 * to facilitate the creation of new, or the loading of existing, user
	 * profiles -- Advanced Requirement 1 - part of REQUIREMENT 7.3
	 */
	private void setupFrame() {
		
		JPanel contentPanel = new JPanel();
		
		GUIUtils.setBorder(contentPanel);
		
		setContentPane(contentPanel);
		
		setLayout(new BorderLayout());
		
		setPreferredSize(new Dimension(500, 150));
		
		//
		
		JMenuBar userProfileMenu = new JMenuBar();
		
		JMenuItem create = new JMenuItem("Create");
		
		// When a user wishes to create a new profile:
		create.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				File file = null;
				
				/* Store a copy of the existing user profile path
				 * (which is typically the default user profile),
				 * in case the path entered by the user is invalid
				 */
				String existingUser = userProfile;
				
				while ( true ) {
					
					String newProfileName = JOptionPane.showInputDialog(Favourites.this, "Enter name for new user profile:");
					
					if ((newProfileName != null) && (newProfileName.length() > 0)) {
						
						// Construct a path to a new user profile based upon the name supplied by the user
						userProfile = "lib/" + newProfileName + ".txt";
						
						// Construct a new potential file using this path
						file = new File(userProfile);
						
						/* If the file doesn't exist and isn't a directory, break from 
						 * infinite loop.
						 * 
						 * Validation like this is important for the marks.
						 */
						if ( !file.exists() && !file.isDirectory() ) break;
						
						// Otherwise inform the user...
						JOptionPane.showMessageDialog(Favourites.this, "Profile exists.");
						
						// ...and replace user profile with existing (incase the user hits cancel on the next iteration) 
						userProfile = existingUser;
						
						// Then loop again
					
					} else {
						
						break;
						
					}
					
				}
				
				try {
					
					// Physically create the file
					file.createNewFile();
					
				} catch (IOException e1) {
					
					e1.printStackTrace();
				
				}
				
				/* Update the favourites list, which will result in it being cleared if a new profile
				 * has just been created.
				 */
				updateFavouritesList();
				
				// For neatness, clear the search results, as there (usually) is a new user now
				search.clearSearchResults();
				
			}
			
		});
		
		JMenuItem load = new JMenuItem("Load");
		
		// When a user wishes to load an existing profile:
		load.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				File file = null;
				
				String existingUser = userProfile;
				
				while ( true ){
				
					String existingProfileName = JOptionPane.showInputDialog(Favourites.this, "Enter name of user profile:");
							
					if ((existingProfileName != null) && (existingProfileName.length() > 0)) {
						
						userProfile = "lib/" + existingProfileName + ".txt";
						
						file = new File(userProfile);
						
						// If the specified file *does* exist (and isn't a directory), then this user profile is valid
						if ( file.exists() && !file.isDirectory() ) break;
						
						JOptionPane.showMessageDialog(Favourites.this, "No profile with this name exists.");
						
						// Again, overwrite users previously specified profile, incase they cancel on the next iteration
						userProfile = existingUser;
						
					}
					
				}
				
				/* Update the favourites list with the new user's favourites 
				 * (this will be unchanged if the user chooses to cancel the loading
				 * of another profile at any point).
				 */
				updateFavouritesList();
				
				// 
				search.clearSearchResults();
				
			}
			
		});
		
		userProfileMenu.add(create);
		
		userProfileMenu.add(load);
		
		this.setJMenuBar(userProfileMenu);
		
		//
		
		JLabel header = new JLabel("Your favourite sharks are this far away from you right now:");
		
		GUIUtils.setBorder(header);
		
		add(header, BorderLayout.NORTH);
		
	}
	
	/**
	 * A private class to help manage each entry in the favourite sharks list,
	 * and the order of the entries.
	 * 
	 * Part of REQUIREMENT 7.2.2 - Closest to King's
	 * 
	 * @author Martin
	 *
	 */
	private class DistanceFromKings implements Comparable<DistanceFromKings> {
		
		/**
		 * The longitude of King's
		 */
		private static final double kingsLon = 0.0;
		
		/**
		 * The latitude of King's
		 */
		private static final double kingsLat = 0.0;
		
		/**
		 * The location (longitude and latitude) of the favourite shark
		 */
		private Location sharkLocation;
		
		/**
		 * The name of the favourite shark
		 */
		private String sharkName;
		
		/**
		 * The distance this favourite shark is from King's
		 */
		private double distanceFromKings;
		
		/**
		 * @param sharkLocation
		 */
		public DistanceFromKings( String sharkName, Location sharkLocation ) {
			
			// Store shark location and name
			this.sharkLocation = sharkLocation;
			
			this.sharkName = sharkName;
			
			// Calculate distance from King's (in miles), and store it, as soon as favourite shark entry is created.
			distanceFromKings = GUIUtils.getDistanceFromLatLonInMiles(sharkLocation.getLatitude(), sharkLocation.getLongitude(), kingsLat, kingsLon);
			
		}
		
		/**
		 * @return The favourite shark's location
		 */
		public Location getSharkLocation() {
			
			return sharkLocation;
			
		}
		
		/**
		 * @return The favourite shark's name
		 */
		public String getSharkName() {
			
			return sharkName;
			
		}

		/* 
		 * Enable one favourite shark to be compared to another
		 * based upon their distance from King's, thus allowing
		 * them to be sorted by this distance.
		 * 
		 * (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(DistanceFromKings otherShark) {
			
			if ( distanceFromKings < otherShark.distanceFromKings ) {
				
				return -1;
				
			} else if ( distanceFromKings > otherShark.distanceFromKings ) {
				
				return 1;
				
			} else {
				
				return 0;
				
			}
			
		}
		
		/* toString() is important here as it defines what will be shown in
		 * the favourites list.
		 * 
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			
			return sharkName + ": " + GUIUtils.round(distanceFromKings) + " Mi.";
			
		}
		
	}
	
	/**
	 * The elements in the list are of the custom DistanceFromKings type
	 */
	private DefaultListModel<DistanceFromKings> sharkListModel;
	
	/**
	 * Places and sets up the shark list.
	 */
	private void sharkList() {
		
		sharkListModel = new DefaultListModel<DistanceFromKings>();
		
		JList<DistanceFromKings> sharkList = new JList<DistanceFromKings>();
		
		GUIUtils.setBorder(sharkList);
		
		sharkList.setModel(sharkListModel);
		
		// First call to update populates favourites list.
		updateFavouritesList();
		
		add(sharkList, BorderLayout.CENTER);
		
		sharkListListener(sharkList);
		
	}
	
	/**
	 * Updates the favourites list from the user profile, assuming the underlying data has changed.
	 * 
	 * Also handles `Sharknado' functionality REQUIREMENT 7.2.4., and some of 7.2.2 (Closest to King's)
	 */
	public void updateFavouritesList() {
		
		// Clear the list model in preparation for update.
		sharkListModel.clear();
		
		Path path = Paths.get(userProfile);
		
		ArrayList<DistanceFromKings> sharkDistances = new ArrayList<DistanceFromKings>();
		
		// Use path to user profile file to read in each favourite shark
		try ( BufferedReader reader = Files.newBufferedReader(path) ) {
			
			for ( String name : reader.lines().collect( Collectors.toList() )) {
				
				// Get the last location of this favourite shark
				Location location = jaws.getLastLocation(name);
				
				/* Add it to the favourites list model, wrapped in a DistanceFromKing's object,
				 * so that it displays the correct data and can be sorted.
				 */
				sharkDistances.add(new DistanceFromKings(name, location));
				
				/* Sharknado test: there are lots of ways to achieve this, so please take time
				 * to understand the solution the students have chosen.
				 * 
				 * In my case, I simply call Google's reverse geo-coding API, which provides
				 * information (such as country, town, city etc.) for a given long-lat value.
				 * 
				 * If a sharks long-lat value corresponds to some kind of address, then I assume
				 * it is not over the ocean, and show a message that there is a Sharknado event.
				 * 
				 * This is, arguably, fairly unsophisticated, so I welcome more inventive solutions.
				 * Accuracy isn't particularly important, so much as efficiency.
				 */
				ArrayList<String> geocode = GUIUtils.urlResponse("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude() + "," + location.getLongitude() + "&key=" + GOOGLE_API);
				
				if ( geocode.toString().replace("\n", "").replace("\t", "").contains("formatted_address") ) {
					
					Pattern pattern = Pattern.compile( "(?<=formatted_address)(.*)" );
					
					Matcher matcher = pattern.matcher( geocode.toString() );
					
					if ( matcher.find() ) {
						
					   String formatted_address = matcher.group(0);
					
					   JOptionPane.showMessageDialog(this, "Sharknado over " + formatted_address.split("\",")[0].replace("\"", ""));
					
					}
					
				}
				
				//
				
			}
			
			// Sort the list of shark distances
			Collections.sort(sharkDistances);
			
		} catch (IOException e) {

			e.printStackTrace();
			
			// If the update fails, disable the favourites button so the window cannot be show.
			startup.disableFavouriteButton();
		
		}
		
		// Add each distance, in order, to the list model
		for ( DistanceFromKings distance : sharkDistances ) {
			
			sharkListModel.addElement(distance);
			
		}
		
	}
	
	/**
	 * Adds functionality to each favourite shark in the list when clicked.
	 * 
	 * @param sharkList
	 */
	private void sharkListListener( JList<DistanceFromKings> sharkList ) {
		
		/* Do not penalise the students if they have implemented this functionality
		 * slightly differently, perhaps as an additional pop-up window.
		 */
		sharkList.addListSelectionListener(new ListSelectionListener() {

			/* (non-Javadoc)
			 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
			 */
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				// Deals with double event from list selection with getValueIsAdjusting, and ensures something has been selected.
				if (sharkList.getSelectedIndex() >= 0 && !e.getValueIsAdjusting()) {
					
					// Hide the favourites frame...
					Favourites.this.setVisible(false);
					
					// ..show the search frame
					search.setVisible(true);
					
					// Save the name of the favourite shark selected
					String sharkName = sharkList.getSelectedValue().getSharkName();
					
					/* Using existing methods from the search frame, show information about the
					 * selected favourite shark, plus its most recent ping:
					 */
					ArrayList<SharkTime> pings = new ArrayList<SharkTime>();
					
					String lastPing;
					
					/* Find the list of pings, by time, containing the most recent occurrence of this favourite shark's 
					 * appearance (ping), by first looking at the records for the past 24 hours, then the past week and then the past month.
					 */
					if ( search.orderedPings(jaws.past24Hours()).contains(new SharkTime(sharkName)) ) {
						
						pings = search.orderedPings(jaws.past24Hours());
						
					} else if ( search.orderedPings(jaws.pastWeek()).contains(new SharkTime(sharkName)) ) { 
						
						pings = search.orderedPings(jaws.pastWeek());
						
					} else if ( search.orderedPings(jaws.pastMonth()).contains(new SharkTime(sharkName)) ) { 
					
						pings = search.orderedPings(jaws.pastMonth());
						
					} 
					
					/* If this favourite shark did not surface (was not pinged) in the past month, then display its
					 * information as normal, but add that there was no ping information.
					 */
					if ( pings.size() == 0 ) {
						
						search.addToSearchResults(search.sharkResult(jaws.getShark(sharkList.getSelectedValue().getSharkName()), "No ping data available."));
					
					// Otherwise, if the shark did surface:
					} else {
						
						search.clearSearchResults();
						
						// From the list of pings that contains the most recent ping for this shark, select that ping and pass it, along with the shark's information, to the search frame.
						search.addToSearchResults(search.sharkResult(jaws.getShark(sharkList.getSelectedValue().getSharkName()), pings.get(pings.indexOf(new SharkTime(sharkName))).getTime()));
						
					}
				
				}
				
			}
			
		});
	
	}
	
	/**
	 * Adds the ability to generate a map showing favourite shark locations
	 * to the favourites frame
	 */
	private void sharkMap() {
		
		JButton showMap = new JButton("Show on map");
		
		showMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				SharkMap map = new SharkMap();
				
				map.setVisible(true);
				
				// Slightly messy to extract from ListModel
				for ( Object distance : Arrays.asList(sharkListModel.toArray()) ) {
					
					// Pass each favourite shark's (most recent) longitude and latitude to the shark map class
					map.addToMap( ((DistanceFromKings)distance).sharkLocation.getLatitude(), ((DistanceFromKings)distance).sharkLocation.getLatitude() );
					
				}
				
			}
			
		});
		
		add(showMap, BorderLayout.SOUTH);
		
	}
	
	
}
