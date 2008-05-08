/*
 * Created on 16-dic-2005
 *
 */
package com.code.aon.planner.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.Recur;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.calendar.CalendarManager;
import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.common.IResourceLoader;
import com.code.aon.planner.core.Event;
import com.code.aon.planner.enumeration.EventStatus;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-dic-2005
 * @since 1.0
 *  
 */
public class AppointmentTest extends TestCase {

    private static final String CONFIG_FILE = "com/code/aon/planner/test/calendar-config.xml";
    
    private CalendarManager calendarManager;
    
    public AppointmentTest(String name) {
        super(name);
    }

    protected void setUp() {
        if ( this.calendarManager == null ) {
            IResourceLoader resourceLoad = new IResourceLoader() {
                public InputStream getResourceAsStream(String resource) {
                    return AppointmentTest.class.getClassLoader().getResourceAsStream(resource);
                }
            };
            try {
                this.calendarManager = CalendarManager.getInstance(resourceLoad,CONFIG_FILE);
            } catch (CalendarException e) {
                fail( "Error loading calendar config file: " + CONFIG_FILE );
            }
        }
    }

    public static String CPK1 = new String("-9999"); // $codepro.audit.disable numericLiterals

    public static Event CA1 = new Event();
    static {
        CA1.setId(CPK1);
        CA1.setTitle("Titulo Cita de Prueba");
        CA1.setSubtitle("SubTitulo Cita de Prueba");
        CA1.setDescription("Descripcion Cita de Prueba");
        Calendar today = Calendar.getInstance();
        CA1.setStartTime(today.getTime());
        today.add(Calendar.MONTH, 1);
        CA1.setEndTime(today.getTime());
        CA1.setState(EventStatus.TENTATIVE);
        CA1.setCategory(EventCategory.APPOINTMENT);
        try {
            CA1.addRecurrence(new Recur( "FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR" ));
        } catch (ParseException e) {
            fail("Error creating Recur object");
        }
    }

    /**
     * Method testCreate
     */
    public void testCreate() {
        AonCalendar calendar = getCalendar();
        calendar.getCalendar().getComponents().add( CA1.getComponent() );
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("c:/tmp/alavaTest.ics");
        } catch (FileNotFoundException e1) {
            fail( "Error creating calendar file" );
        }
        try {
            CalendarManager.write( calendar, out );
        } catch (CalendarException e) {
            fail( "Error writting appointment to calendar" );
        }
    }

    /**
     * Method getCalendar()
     */
    private AonCalendar getCalendar() {
        AonCalendar calendar = null;
        try {
            calendar = this.calendarManager.getCalendar( 2005 );
        } catch (CalendarException e) {
            fail( "Error loading default calendar of year 2005" );          
        }
        assertNotNull( calendar );
        assertNotNull( calendar.getCalendar() );
        assertTrue(! calendar.getCalendar().getComponents().isEmpty() );
        return calendar; 
    }
}
