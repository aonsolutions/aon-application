package com.code.aon.test.calendar;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.RRule;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 08-jun-2005
 * @since 1.0
 *  
 */
public class iCal4jTest extends TestCase {

	public iCal4jTest(String name) {
		super(name);
	}

	public void testVFreeBusyCreateFreeTime() throws ParseException {

		ComponentList components = new ComponentList();
		
		VEvent event1 = new VEvent( new DateTime("20060101T080000"), new Dur(0,0,15,0), "Consultation 1" );
		components.add( event1 );
		
		DateTime start = new DateTime( "20060103T000000" );		
		DateTime end = new DateTime( "20060104T000000" );
				
        VFreeBusy requestFree = new VFreeBusy( start, end, new Dur(0,0,5,0) );

        VFreeBusy freeBusy = new VFreeBusy(requestFree, components );
        
        FreeBusy fg = (FreeBusy) freeBusy.getProperties().getProperty( Property.FREEBUSY );
        assertNotNull( fg );
		
	}

	public void testRecurGetDates() throws ParseException {

		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone("Etc/UTC") );
		
		Recur recur = new Recur( "FREQ=WEEKLY;INTERVAL=1;BYDAY=SA" );
		
		Date start = new Date( "20060101Z" );		
		Date end = new Date( "20060101Z" );
		
		DateList list = recur.getDates( start, end, null );
		for( int i = 0; i < list.size(); i++ ) {
			Date date = (Date) list.get( i );
			calendar.setTime( date );
			assertEquals( Calendar.SATURDAY, calendar.get( Calendar.DAY_OF_WEEK ) );
		}
				
	}

	public void testExDate() throws ParseException {

		VEvent event1 = new VEvent( new DateTime("20060103T080000"), new Dur(0,0,15,0), "Event 1" );
		
		Recur rRuleRecur = new Recur( "FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR" );
		RRule rRule = new RRule( rRuleRecur );
		event1.getProperties().add( rRule );
		
		ParameterList parameterList = new ParameterList();
		parameterList.add( Value.DATE );
		ExDate exDate = new ExDate( parameterList, "20060106" );
		event1.getProperties().add( exDate );
		
		Date start = new Date( "20060106" );
		Date end = new Date( "20060107" );
		PeriodList list = event1.getConsumedTime( start, end );
		assertTrue( list.isEmpty() );
	}

	public void testDate() throws ParseException {
		Date date1 = new Date( "20060101" );
		
		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone("Etc/UTC") );
		calendar.clear();
		calendar.set( 2006, 0, 1 );
		calendar.set( Calendar.MILLISECOND, 1 );
		Date date2 = new Date( calendar.getTime() );
		
		assertEquals( date1.toString(), date2.toString() );
		assertEquals( date1, date2 );
	}
	
	public void testDateTime() throws ParseException {
		DateTime dateTime1 = new DateTime( "20060101T000001Z" );
		Date date1 = new Date( dateTime1 );
		Date date2 = new Date( "20060101" );
		
		DateList list = new DateList(Value.DATE);
		list.setUtc( true );
		
		list.add( date1 );
		
		assertTrue( list.contains(date1) );
		assertTrue( !list.contains(dateTime1) );
		assertTrue( list.contains(date2) );
	}

	public void testBusyTime() throws ParseException {

		VEvent event1 = new VEvent( new DateTime("20060102T080000Z"), new Dur(0,5,0,0), "Event 1" );
		
		Recur rRuleRecur = new Recur( "FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR" );
		RRule rRule = new RRule( rRuleRecur );
		event1.getProperties().add( rRule );
		
		ComponentList list = new ComponentList();
		list.add( event1 );
		
		DateTime start = new DateTime( "20060102T1100000Z" );
		Period period = new Period( start, new Dur(0,0,30,0) );
		
		VFreeBusy request = new VFreeBusy( period.getStart(), period.getEnd() );
		VFreeBusy busyTime = new VFreeBusy( request, list );
        FreeBusy fg = (FreeBusy) busyTime.getProperties().getProperty( Property.FREEBUSY );
        assertNotNull( fg );
        PeriodList periods = fg.getPeriods();
        assertNotNull( periods );
        assertTrue( periods.size() == 1 );
		Period busy1 = (Period) periods.iterator().next();
		assertNotSame( busy1.getStart(), new DateTime("20060102T1100000Z") );
		assertNotSame( busy1.getDuration().toString(), "PT30M" );
	}

}