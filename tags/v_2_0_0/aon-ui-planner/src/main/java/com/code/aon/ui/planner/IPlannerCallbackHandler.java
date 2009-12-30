package com.code.aon.ui.planner;

import java.util.Date;
import java.util.List;

import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.planner.EventException;
import com.code.aon.planner.IEvent;

/**
 * This interface should be implemented by those classes that needs a feedback with the 
 * <code>EventManager</code> class. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 27/02/2007
 *
 */
public interface IPlannerCallbackHandler {

	/**
	 * Tell if the planner generated events are spreadable through calendars hierarchy.
	 * 
	 * @return
	 */
	boolean isSpreadable();

	/**
	 * @return the outcome navigation
	 */
	String getOutcome();

    /**
     * @return a category list
     */
	List getCategories();

	/**
	 * Refresh schedule for showing the event.
	 * 
	 * @param event
	 */
	void refresh( IEvent event );

	/**
	 * Tell if the scheduler has events between dates.
	 * 
	 * @param from 
	 * @param to 
	 * @param ec
	 * @param id
	 * @return
	 */
	boolean hasEvents( Date from, Date to, EventCategory[] ec, String id );

	/**
	 * Add the event.
	 * 
	 * @param event
	 * @throws EventException
	 */
	void add( IEvent event ) throws EventException;

	/**
	 * Update the event.
	 * 
	 * @param event
	 * @throws EventException
	 */
	void update( IEvent event ) throws EventException;

	/**
	 * Remove the event.
	 * 
	 * @param event
	 * @throws EventException
	 */
	boolean remove( IEvent event ) throws EventException; 
}
