package search;

import java.awt.GridLayout;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Shows a frame containing three pie charts, each one demonstrating
 * the portion of sharks with a given attribute value that have appeared
 * within a specified time frame.
 * 
 * Advanced Requirement 3 - Part of REQUIREMENT 7.3
 * 
 * @author Martin
 *
 */
public class Statistics extends JFrame {
	
	/**
	 * Accepts three tables containing the relevant data for a given attribute as value:occurence pairs.
	 * 
	 * @param genders
	 * @param stagesOfLife
	 * @param tagLocations
	 */
	public Statistics( Hashtable<String, Integer> genders, Hashtable<String, Integer> stagesOfLife, Hashtable<String, Integer> tagLocations) {
	
	  super("Statistics");
	  
	  setLayout(new GridLayout(1, 0));
	  
	  /* If, for any of the attribute tables, there is no supplied information
	   * (perhaps there was not enough information in the dataset) print 'insufficient data'
	   * instead of a chart.
	   * 
	   * If there is enough supplied information, create a chart of this information and
	   * add it to the frame.
	   */
	  if ( genders.size() == 0 ) {
		  
		  add(new JLabel("Insufficient data."));
		  
	  } else {
		  
		  add(createChart(genders, "Genders"));
		  
	  }
	  
	  if ( stagesOfLife.size() == 0 ) {
		  
		  add(new JLabel("Insufficient data."));
		  
	  } else {
		  
		  add(createChart(stagesOfLife, "Stage of Life"));
		  
	  }
	  
	  if ( tagLocations.size() == 0 ) {
		  
		  add(new JLabel("Insufficient data."));
		  
	  } else {
		  
		  add(createChart(tagLocations, "Tag Locations"));
		  
	  }
	  
	  pack();
		
	}
	
	/**
	 * Simple method that relies on the JFreeChart library to both construct a
	 * dataset, and then uses this dataset to create a panel that can be added to the frame.
	 * 
	 * @param data
	 * @param title
	 * @return
	 */
	private ChartPanel createChart( Hashtable<String, Integer> data, String title ) {
		
		// Create a dataset as prescribed by JFreeChart
		DefaultPieDataset chartData = new DefaultPieDataset();
		
		/* For each entry in the supplied table, add both the value and the 
		 * occurences to the pie chart (e.g. gender = 5).
		 */
		for ( Entry<String, Integer> entry : data.entrySet() ) {
			
			chartData.setValue(entry.getKey(), entry.getValue());
		}
		
		// Construct a panel with this information, again usig JFreeChart, and return it.
        return new ChartPanel(ChartFactory.createPieChart(title, chartData));
		
	}

}
