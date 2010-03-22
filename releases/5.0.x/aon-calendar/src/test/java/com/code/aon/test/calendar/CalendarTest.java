package com.code.aon.test.calendar;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.CategoryList;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.FreeBusy;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.calendar.CalendarManager;
import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.common.IResourceLoader;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 08-jun-2005
 * @since 1.0
 *  
 */
public class CalendarTest extends TestCase {

	private static final String CONFIG_FILE = "com/code/aon/test/calendar/calendar-config.xml";
	
	private CalendarManager calendarManager;
	
	public CalendarTest(String name) {
		super(name);
	}

	protected void setUp() {
		if ( this.calendarManager == null ) {
	    	IResourceLoader resourceLoad = new IResourceLoader() {
				public InputStream getResourceAsStream(String resource) {
//					if (resource.indexOf(".xml") > -1) {
//						try {
//							return new File("C:\\PROYECTOS\\aon-calendar\\src\\com\\code\\aon\\test\\calendar\\calendar-config.xml").toURL().openStream();
//						} catch (Exception e) {
//						}
//					} 
//					if (resource.indexOf(".ics") > -1) {
//						try {
//							return new File("C:\\PROYECTOS\\aon-calendar\\src\\com\\code\\aon\\test\\calendar\\2006.ics").toURL().openStream();
//						} catch (Exception e) {
//						}
//					} 
//					return null;
					return CalendarTest.class.getClassLoader().getResourceAsStream(resource);
			    }
	    	};
	    	try {
				this.calendarManager = CalendarManager.getInstance(resourceLoad,CONFIG_FILE);
			} catch (CalendarException e) {
				fail( "Error loading calendar config file: " + CONFIG_FILE );
			}
		}
	}

	public void testLoadCalendar() {
		AonCalendar calendar = null;
		try {
			calendar = this.calendarManager.getCalendar( 2006 );
		} catch (CalendarException e) {
			fail( "Error loading default calendar of year 2006" );			
		}
		assertNotNull( calendar );
		assertNotNull( calendar.getCalendar() );
		assertTrue(! calendar.getCalendar().getComponents().isEmpty() );
	}

	private void checkEventsCategory( AonCalendar calendar, Date date, EventCategory eventCategory ) {
		ComponentList cl = calendar.getEventsInDay( date );
		assertNotNull( cl );
		assertTrue( cl.size() == 1 );
		EventCategory[] ec = AonCalendar.getEventCategory( cl );
		assertNotNull( ec );
		assertTrue( ec.length == 1 );
		assertTrue( ec[0] == eventCategory );
	}
	
	public void testEventCategory() throws CalendarException {
		AonCalendar calendar = this.calendarManager.getCalendar( 2006 );

		Calendar cal = Calendar.getInstance();
		cal.clear();

//		cal.set( 2006, 11, 31 );
//		checkEventsCategory( calendar, cal.getTime(), EventCategory.DAY_OFF );	
		
		cal.set( 2006, 0, 1 );
		checkEventsCategory( calendar, cal.getTime(), EventCategory.PUBLIC_HOLIDAY );
		
		cal.set( 2006, 0, 7 );
		checkEventsCategory( calendar, cal.getTime(), EventCategory.DAY_OFF );		

		cal.set( 2006, 3, 28 );
		checkEventsCategory( calendar, cal.getTime(), EventCategory.DAY_OFF );		

		cal.set( 2006, 3, 30 );
		checkEventsCategory( calendar, cal.getTime(), EventCategory.DAY_OFF );		
		
		cal.set( 2006, 6, 25 );
		checkEventsCategory( calendar, cal.getTime(), EventCategory.PUBLIC_HOLIDAY );		

		cal.set( 2006, 7, 15 );
		checkEventsCategory( calendar, cal.getTime(), EventCategory.PUBLIC_HOLIDAY );		
		
		cal.set( 2006, 11, 6 );
		checkEventsCategory( calendar, cal.getTime(), EventCategory.PUBLIC_HOLIDAY );		
	}

	public void testCalcYearlyHours() throws CalendarException, ParseException {
		AonCalendar calendar = this.calendarManager.getCalendar( 2008 );

		if ( calendar != null ) {
			float hours = 0, minutes = 0;
			DateTime start = new DateTime( "20080101T000000" );
			DateTime end = new DateTime( "20081231T000000" );
	
			VFreeBusy request = new VFreeBusy( start, end );
			VFreeBusy busyTime = new VFreeBusy( request, calendar.getCalendar().getComponents() );
	        FreeBusy fg = (FreeBusy) busyTime.getProperties().getProperty( Property.FREEBUSY );
	        if ( fg != null ) {
		        Iterator iter = fg.getPeriods().iterator();
		        while (iter.hasNext()) {
		            Period p = (Period) iter.next();
		            hours += p.getDuration().getHours();
		            minutes += p.getDuration().getMinutes();
		        }
	        }
	        assertEquals( 308, hours, 0 );
	        assertEquals( 0, minutes, 0 );
		}
	}

	public void testWorkingTime() throws CalendarException, ParseException {
		AonCalendar calendar = this.calendarManager.getCalendar( 2006 );

		DateTime start = new DateTime( "20060102T000000" );
		Period period = new Period( start, new Dur(1,0,0,0) );
		PeriodList list = calendar.getWorkingTime( period );
		assertNotNull( list );
		assertFalse( list.isEmpty() );
		Period period1 = (Period) list.iterator().next();
		assertEquals( period1.getStart(), new DateTime("20060102T080000") );
		assertEquals( period1.getDuration().toString(), "PT5H" );

		start = new DateTime( "20060107T000000" );		
		period = new Period( start, new Dur(1,0,0,0) );
		
		list = calendar.getWorkingTime( period );
		assertNotNull( list );
		assertTrue( list.isEmpty() );

		start = new DateTime( "20060108T000000" );
		period = new Period( start, new Dur(1,0,0,0) );
		
		list = calendar.getWorkingTime( period );
		assertNotNull( list );
		assertTrue( list.isEmpty() );

		start = new DateTime( "20060501T000000" );
		period = new Period( start, new Dur(1,0,0,0) );
		
		list = calendar.getWorkingTime( period );
		assertNotNull( list );
		assertTrue( list.isEmpty() );

		start = new DateTime( "20061205T000000" );
		period = new Period( start, new Dur(7,0,0,0) );
		
		list = calendar.getWorkingTime( period );
		assertNotNull( list );
		assertEquals( 3, list.size() );
		Iterator i = list.iterator();
		Period p1 = (Period) i.next();
		assertEquals( p1.getStart(), new DateTime("20061205T080000") );
		assertEquals( p1.getDuration().toString(), "PT5H" );
		Period p2 = (Period) i.next();
		assertEquals( p2.getStart(), new DateTime("20061207T080000") );
		assertEquals( p2.getDuration().toString(), "PT5H" );
		Period p3 = (Period) i.next();
		assertEquals( p3.getStart(), new DateTime("20061211T080000") );
		assertEquals( p3.getDuration().toString(), "PT5H" );

//		start = new DateTime( "20061011T1000000" );
//		period = new Period( start, new Dur(0,0,30,0) );
//		
//		list = calendar.getWorkingTime( period );
//		assertNotNull( list );
//		assertFalse( list.isEmpty() );
//		period1 = (Period) list.iterator().next();
//		assertEquals( period1.getStart(), new DateTime("20061011T1000000") );
//		assertEquals( period1.getDuration().toString(), "PT3H" );
	}

	public void testFreeTime1() throws CalendarException, ParseException {
		AonCalendar calendar = this.calendarManager.getCalendar( 2006 );

		DateTime start = new DateTime( "20060103T000000" );
		Period period = new Period( start, new Dur(1,0,0,0) );
		PeriodList list = calendar.getWorkingTime( period );
		Period period1 = (Period) list.iterator().next();
		PeriodList freeList = calendar.getFreeTime( period1, new Dur(0,0,5,0) );
		assertNotNull( freeList );
		assertEquals( 1, freeList.size() );
		Period free1 = (Period) freeList.iterator().next();		
		assertEquals( free1.getStart(), new DateTime("20060103T080000") );
		assertEquals( free1.getDuration().toString(), "PT5H" );
	}

	@SuppressWarnings("unchecked")
	public void testFreeTime2() throws CalendarException, ParseException {
		AonCalendar calendar = this.calendarManager.getCalendar( 2006 );

		CategoryList categoryList = new CategoryList( EventCategory.APPOINTMENT.name() );
		Categories categories = new Categories(categoryList);
		
		VEvent event1 = new VEvent( new DateTime("20061201T080000"), new Dur(0,0,15,0), "Consultation 1" );
		event1.getProperties().add( categories );
		calendar.getCalendar().getComponents().add( 0, event1 );

		VEvent event2 = new VEvent( new DateTime("20061201T110000"), new Dur(0,0,15,0), "Consultation 2" );
		event2.getProperties().add( categories );
		calendar.getCalendar().getComponents().add( 0, event2 );

		VEvent event3 = new VEvent( new DateTime("20061201T124500"), new Dur(0,0,15,0), "Consultation 2" );
		event3.getProperties().add( categories );
		calendar.getCalendar().getComponents().add( 0, event3 );
		
		DateTime start = new DateTime( "20061201T000000" );
		Period period = new Period( start, new Dur(1,0,0,0) );
		PeriodList list = calendar.getWorkingTime( period );
		Period period1 = (Period) list.iterator().next();
		PeriodList freeList = calendar.getFreeTime( period1, new Dur(0,0,5,0) );
		assertNotNull( freeList );
		assertEquals( 2, freeList.size() );
		Iterator i = freeList.iterator();		
		Period free1 = (Period) i.next();		
		assertEquals( free1.getStart(), new DateTime("20061201T081500Z") );
		assertEquals( free1.getDuration().toString(), "PT2H45M" );
		Period free2 = (Period) i.next();		
		assertEquals( free2.getStart(), new DateTime("20061201T111500Z") );
		assertEquals( free2.getDuration().toString(), "PT1H30M" );
	}
}