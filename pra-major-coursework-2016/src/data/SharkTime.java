package data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import api.jaws.Ping;

/**
 * Utility class that augments Ping in order to make shark appearances
 * to allow them to be sorted by time (most recent first).
 * 
 * Making utility classes like this is good practice, and you should
 * reward students who do so.
 * 
 * @author Martin
 *
 */
public class SharkTime extends Ping implements Comparable<SharkTime> {
	
	/**
	 * Shark time consists of a name and a time, like parent class
	 * 
	 * @param name
	 * @param date
	 */
	public SharkTime(String name, String time) {
		
		super(name, time);
		
	}
	
	/**
	 * Give the option to specify a shark time without a
	 * given time. Typically used when creating temporary shark time
	 * objects to check for the existence of a shark time in a list.
	 * 
	 * @param name
	 */
	public SharkTime(String name) {
		
		super(name, "0000-00-00 00:00:00");
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo( SharkTime otherDate ) {
		
		/* 
		 * A simple comparison of dates using library classes to determine
		 * when one date comes before another
		 * 
		 */
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date firstDate = null;
		
		Date secondDate = null;
		
		/* Parse time strings from Ping class, as actual date objects using
		 * date format library class.
		 */
		try {
			
			firstDate = sdf.parse( getTime() );
			
			secondDate = sdf.parse( otherDate.getTime() );
			
		} catch (ParseException e) {

			e.printStackTrace();
		
		}
		
		// Standard return from comparable: 1 = this before other; -1 other before this; 0 = equal
		if ( firstDate.before(secondDate) ) {
			
			return 1;
			
		} else if ( secondDate.before(firstDate) ) {
			
			return -1;
			
		} else {
			
			return 0;
			
		}
		
	}
	
	/* 
	 * Auto-generated boilerplate to give class identity, based on shark name.
	 * 
	 * Supports identification (and removal) of duplicate shark appearances.
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		
		return result;
	
	}

	/* 
	 * Auto-generated boilerplate to give class identity, based on shark name.
	 * 
	 * Supports identification (and removal) of duplicate shark appearances.
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Ping other = (Ping) obj;
		
		if (getName() == null) {
		
			if (other.getName() != null)
				return false;
		
		} else if (!getName().equals(other.getName()))
			
			return false;
		
		return true;
	
	}
	
}