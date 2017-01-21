package favourites;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Displays a map of the world. Points can be added to the map
 * to indicate the location of different sharks.
 * 
 * Part of Advanced Requirement 4 - REQUIREMENT 7.3
 * 
 * @author Martin
 */
public class SharkMap extends JFrame {
	
	/**
	 * The width of the image used for this map
	 */
	private static final int mapWidth = 823;
	
	/**
	 * The height of the image used for this map
	 */
	private static final int mapHeight = 698;
	
	/**
	 * The set of location points currently being displayed on this map
	 */
	private ArrayList<Point> locationPoints;
	
	/**
	 * Simple utility class to store a location point.
	 * 
	 * @author Martin
	 *
	 */
	private class Point {
		
		/**
		 * The x position of this point
		 */
		private double x;
		
		/**
		 * The y position of this point
		 */
		private double y;
		
		/**
		 * Set the x and y position of this point
		 * @param x
		 * @param y
		 */
		public Point(double x, double y) {
			
			this.x = x;
			
			this.y = y;
			
		}
		
		/**
		 * @return the x position
		 */
		public double getX() {
			
			return x;
			
		}
		
		/**
		 * @return the y position
		 */
		public double getY() {
			
			return y;
			
		}
		
	}
	
	/**
	 * Construct the frame and add the map.
	 */
	public SharkMap() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		locationPoints = new ArrayList<Point>();
		
		setMinimumSize( new Dimension(mapWidth, mapHeight) );
		
		// Add a new JPanel to the frame, with a custom paint method
		add(new JPanel() {
			
			public void paintComponent(Graphics panelGraphics) {
				
				BufferedImage mapImage = null;
				
				try {
				
					// Read the image from a sensible location, like a library folder
					mapImage = ImageIO.read( new File("lib/worldmap_mercator.jpg") );
				
				} catch (IOException e) {

					e.printStackTrace();
				
				}
				
				// Draw the loaded image onto the panel
				panelGraphics.drawImage(mapImage, 0, 0, null);
			
				/* For each stored location point, draw a rectangle, also onto the panel (on top of the map).
				 * 
				 * We offset the x and the y of the point by half the width and the height (10 / 2), 
				 * to ensure that the centre of the square illustrating the location is in the correct place.
				 */
				for ( Point point : locationPoints ) {
				
					( (Graphics2D) panelGraphics ).draw( new Rectangle2D.Double( point.getX() - (10 / 2), point.getY() - (10 / 2), 10, 10 ) );

				}
				
			}
			
		});
		
	}
	

	/**
	 * Converts a longitude and latitude point to an (x,y) point on a Mercator
	 * projected map image.
	 * 
	 * @param longitude
	 * @param latitude
	 */
	public void addToMap(double longitude, double latitude) {
		
		/* So long as the formula used looks similar to this (or, if another formula is given,
		 * a link is supplied showing from where the formula was derived), then I'm not too concerned
		 * about the accuracy of the map (so long as it isn't too sporadic!); these conversions
		 * have a margin of error, anyway.
		 */
		double latRad = latitude * Math.PI / 180;
	
		double mercN = Math.log ( Math.tan ( ( Math.PI / 4.0 ) + ( latRad / 2.0 ) ) );
				
		Point point = new Point(
				
			( longitude + 180.0 ) * ( mapWidth / 360.0 ),

			( mapHeight / 2.0 ) - ( mapWidth * mercN / ( 2 * Math.PI ) )
		
		);
		
		// Store the new point
		locationPoints.add(point);
		
		/* Invalidate the graphics to force a refresh, and redraw the list of points
		 * which now contains the new point.
		 */
		revalidate();
		
	}
	
}
