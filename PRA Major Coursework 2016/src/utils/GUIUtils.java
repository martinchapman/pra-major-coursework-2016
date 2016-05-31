package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.border.Border;

/**
 * Utility methods that support the construction of the GUI (mainly), but also
 * some calculations and data handling.
 * 
 * @author Martin
 *
 */
public class GUIUtils {

	/**
	 * Stops the class being initialised
	 */
	private GUIUtils() {}
	
	/**
	 * A utility method for easily adding thin borders to 
	 * components.
	 * 
	 * @param component
	 */
	public static void setBorder( JComponent component ) {
		
		setBorder( component, BorderFactory.createEmptyBorder() );
		
	}
	
	/**
	 * A utility method for easily adding pre-specified borders
	 * to components.
	 * 
	 * @param component
	 * @param border
	 */
	public static void setBorder( JComponent component, Border border ) {
		
		component.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
	}
	
	/**
	 * Adds a JSeparator (a thin line) above a JLabel on a given component, in
	 * order to expedite the laying out of a frame.
	 *  
	 * @param component
	 * @param title
	 */
	public static void addSectionHeader( JComponent component, String title ) {
		
		component.add(new JSeparator());
		
		JLabel sectionHeader = new JLabel(title);
		
		GUIUtils.setBorder(sectionHeader);
		
		component.add(sectionHeader);
		
	}
	
	/**
	 * Finds the distance between two long-lat points in miles.
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public static double getDistanceFromLatLonInMiles( double lat1, double lon1, double lat2, double lon2 ) {
		  
		double R = 6371; // Radius of the earth in km
		
		double dLat = GUIUtils.deg2rad( lat2 - lat1) ;  // deg2rad below
		
		double dLon = GUIUtils.deg2rad( lon2 - lon1 ); 
		
		double a = 
		    Math.sin( dLat / 2) * Math.sin( dLat / 2 ) +
		    Math.cos( deg2rad( lat1 ) ) * Math.cos( deg2rad( lat2 ) ) * 
		    Math.sin( dLon / 2 ) * Math.sin( dLon / 2 )
		    ; 
		
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		
		double d = R * c; // Distance in km

		return d * 0.621371;
	
	}

	/**
	 * Converts degrees to radians
	 * 
	 * @param deg
	 * @return
	 */
	public static double deg2rad(double deg) {
		
		return deg * ( Math.PI / 180);
	
	}
	
	/**
	 * Very simple mechanism for issuing a HTTP request to a URL, and returning 
	 * the resulting page as a list of strings.
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static ArrayList<String> urlResponse(String request) throws UnsupportedEncodingException, IOException {
		
		URL url = new URL(request);

		ArrayList<String> responseLines = new ArrayList<String>();
		
		try (BufferedReader urlReader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
		    
			for (String urlLine; (urlLine = urlReader.readLine()) != null;) {
		    
		    	responseLines.add(urlLine);
		
		    }
		
		}
		
		return responseLines;
		
	}

	/**
	 * Rounds a value to two decimal places.
	 * 
	 * @param value
	 * @return
	 */
	public static String round(double value) {
		
		DecimalFormat df = new DecimalFormat("#.##");
		
		df.setRoundingMode(RoundingMode.CEILING);
		
		return df.format(value);
		
	}
	
	/**
	 * Simple method that either inserts a new record into the supplied hash table
	 * if the supplied key does not exist in that table (with an initial value of 1), or
	 * increments the value associated with that key, if the key is already in the hash table.
	 * 
	 * @param set
	 * @param key
	 */
	public static void insertOrIncrement( Hashtable<String, Integer> set, String key ) {
		
		/* Because some attributes (e.g. gender) may come backs as unknown (null)
		 * important to ensure these are added as a category for the bar charts
		 */
		if ( key == null ) {
			
			return; 
					
		}
		
		if ( set.containsKey(key) ) {
			
			set.put(key, set.get(key) + 1);
			
		} else {
			
			set.put(key, 1);
			
		}
		
	}

}
