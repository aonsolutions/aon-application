/*
 * Created on 30-may-2005
 *
 */
package com.code.aon.ui.planner;

import java.io.InputStream;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.calendar.CalendarManager;

import com.code.aon.common.IResourceLoader;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.planner.calendar.CalendarHelper;
import com.code.aon.planner.core.Calendar;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 26-sep-2005
 * @since 1.0
 *
 */

public class CalendarManagerBean {

	public static final String MANAGER_BEAN_NAME = "calendarManager";
	/** Se obtiene el Logger adecuado */
    private static final Logger LOGGER = Logger.getLogger(CalendarManagerBean.class.getName());

    private CalendarManager calendarManager;
    
    /**
     * Constructor por defecto del Controlador de Calendar.
     * @throws CalendarException 
     * 
     * @throws ManagerBeanException
     */
    public CalendarManagerBean() throws CalendarException {
    	IResourceLoader resourceLoad = new IResourceLoader() {
			public InputStream getResourceAsStream(String resource) {
				return FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(resource);
		    }
    	};
    	ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
    	String resource = ectx.getInitParameter(CalendarManager.CONFIG_FILE);
        this.calendarManager = CalendarManager.getInstance(resourceLoad,resource);
    }

    /**
     * Return calendar.
     * 
     * @param id
     * @return
     */
    public AonCalendar getCalendar(Integer id)  {
    	LOGGER.fine( "Obtaining calendar for id:" + id ); 
    	AonCalendar calendar = null;
    	try {
    		calendar = CalendarHelper.getCalendar(id);
    		if ( calendar == null ) {
    	    	LOGGER.fine( "Loading new calendar for id " + id );    			
				calendar = this.calendarManager.getCalendar(2007);
                CalendarHelper.updateCalendar(calendar);
    		}
		} catch (CalendarException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
    	return calendar;
    }

    /**
     * Update calendar.
     * 
     * @param calendar
     * @return
     */
    public Calendar updateCalendar(AonCalendar calendar) {
    	LOGGER.fine( "Updating calendar for id " + calendar.getPrimaryKey() + " of year " + calendar.getYear() );
		try {
			return CalendarHelper.updateCalendar(calendar);
		} catch (CalendarException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
    }

    /**
     * Clone calendar.
     * 
     * @param id
     * @param calendar
     * @return
     */
    public AonCalendar cloneCalendar(AonCalendar calendar) {
    	LOGGER.fine( "Cloning calendar" );
		return new AonCalendar( calendar.getCalendar() , calendar.getYear() );
    }

}
