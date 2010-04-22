package com.code.aon.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.fortuna.ical4j.model.CategoryList;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Categories;

import com.code.aon.calendar.enumeration.EventCategory;

public class CalendarUtil {

    /** Se obtiene el Logger adecuado */
    private static final Logger LOGGER = Logger.getLogger(CalendarUtil.class.getName());

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmmss");
    private static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    
    public static Date getICalDate( java.util.Date date ) {
    	Date result = null;
		try {
			result = new Date( DATE_FORMAT.format(date) );
		} catch (ParseException e) {
			LOGGER.log( Level.SEVERE, "Error parsing date " + date, e );
		}
		return result;
    }

    public static DateTime getICalDateTime( java.util.Date date, boolean onlyDate ) {
    	DateTime result = null;
		try {
			String value;
			if ( onlyDate ) {
				value = DATE_FORMAT.format(date) + "T000000";				
			} else {
				value = DATE_TIME_FORMAT.format(date);				
			}
			result = new DateTime( value );
		} catch (ParseException e) {
			LOGGER.log( Level.SEVERE, "Error parsing date " + date, e );
		}
		return result;
    }

    public static java.util.Date getDate( Date dt ) {
    	java.util.Date result = null;
    	try {
			if ( dt instanceof DateTime ) {
	    		result = DATE_TIME_FORMAT.parse( dt.toString() );	    		
			} else if ( dt instanceof Date ) {
				result = DATE_FORMAT.parse( dt.toString() );
			}
		} catch (ParseException e) {
			LOGGER.log( Level.SEVERE, "Error parsing date " + dt, e );
		}
		return result;
    }
    
    public static DateTime getICalDate( Date date, DateTime time ) {
    	DateTime result = null;
		try {
			String value = DATE_FORMAT.format(date) + 'T' + TIME_FORMAT.format(time); 
			result = new DateTime(value);
		} catch (ParseException e) {
			LOGGER.log( Level.SEVERE, "Error parsing date " + date, e );
		}
		return result;
    }

    public static EventCategory getCategory( VEvent event ) {
    	EventCategory result = null;
    	Categories categories = (Categories) event.getProperties().getProperty(Property.CATEGORIES);
    	CategoryList list = categories.getCategories();
    	if (! list.isEmpty() ) {
    		String value = (String) list.iterator().next();
    		result = EventCategory.get(value);
//    		result = EventCategory.get(Byte.parseByte(value));
    	}
    	return result;
    }
    
    public static void changeProperty( VEvent event, Property property ) {
    	Property p = event.getProperties().getProperty( property.getName() );
    	event.getProperties().remove( p );
    	event.getProperties().add( property );
    }

    public static void changeProperty( ComponentList components, Property property ) {
		Iterator i = components.iterator();
		while ( i.hasNext() ) {
			VEvent event = (VEvent) i.next();
			CalendarUtil.changeProperty( event, property );
		}
    }
}
