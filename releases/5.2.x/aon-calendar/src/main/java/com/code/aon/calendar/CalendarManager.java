package com.code.aon.calendar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import com.code.aon.common.IResourceLoader;

public class CalendarManager {

    public static final String CONFIG_FILE = "com.code.aon.calendar.config.file";
    public static final String LATIN_CHARSET = "ISO-8859-1";
	
    private static Logger LOGGER = Logger.getLogger(CalendarManager.class.getName());
	
	private IResourceLoader resourceLoader;
	
	private Map<String, CalendarEntry> calendarEntries;
	
	protected CalendarManager( IResourceLoader resourceLoader ) {
		this.resourceLoader = resourceLoader;
		this.calendarEntries = new HashMap<String, CalendarEntry>();
	}
	
	public static final CalendarManager getInstance( IResourceLoader resourceLoader, String resource ) throws CalendarException {
		return CalendarLoader.getCalendarManager( resourceLoader, resource );
	}
	
	public void addCalendarEntry( CalendarEntry entry ) {
		String key = String.valueOf(entry.getYear());
		if (! CalendarEntry.DEFAULT_ID.equals(entry.getId()) ) {
			key += entry.getId();
		}
		this.calendarEntries.put( key, entry );
	}
	
	public AonCalendar getCalendar( int year ) throws CalendarException {
		return this.getCalendar( year, "" );
	}

	public AonCalendar getCalendar( int year, String id ) throws CalendarException {
		AonCalendar result = null;
		CalendarEntry entry = (CalendarEntry) this.calendarEntries.get( year + id );
		if ( entry != null ) {
			Calendar calendar = loadCalendar( entry.getPath() );
			result = new AonCalendar( calendar, entry.getYear() );		
		}
		return result;
	}
	
	private Calendar loadCalendar( String path ) throws CalendarException {
		InputStream in = this.resourceLoader.getResourceAsStream( path );
		if ( in == null ) {
            throw new CalendarException( "Resource not found: " + path );			
		}
		return loadCalendar( in, path );		
	}
	
	public static AonCalendar getCalendar( byte[] buffer ) throws CalendarException {
		InputStream in = new ByteArrayInputStream( buffer );
		Calendar calendar = loadCalendar( in, "calendar from DB" );
		return new AonCalendar( calendar, getYear(calendar) );		
	}

	public static void write( AonCalendar calendar, OutputStream out ) throws CalendarException {
		CalendarOutputter writer = new CalendarOutputter();
		try {
			writer.output( calendar.getCalendar(), new OutputStreamWriter(out, LATIN_CHARSET) );
		} catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writting calendar", e);
            throw new CalendarException(e.getMessage(), e);
		} catch (ValidationException e) {
            LOGGER.log(Level.SEVERE, "Error validating calendar", e);
            throw new CalendarException(e.getMessage(), e);
		}
	}

	private static Calendar loadCalendar( InputStream in, String description ) throws CalendarException {
		Calendar calendar = null;
        CalendarBuilder builder = new CalendarBuilder();
        try {
            Reader reader = new InputStreamReader( in, Charset.forName(LATIN_CHARSET) );
        	calendar = builder.build(reader);
			in.close();
		} catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading " + description, e);
            throw new CalendarException(e.getMessage(), e);
		} catch (ParserException e) {
            LOGGER.log(Level.SEVERE, "Error parsing " + description, e);
            throw new CalendarException(e.getMessage(), e);
		}
		return calendar;
	}

	private static int getYear( Calendar calendar ) {
		return 2007;
	}
	
}
