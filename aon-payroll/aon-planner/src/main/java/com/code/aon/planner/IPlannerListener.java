package com.code.aon.planner;

/**
 * A "IEvent" event gets fired whenever it changes a "bound" attribute. 
 * You can register an IPlannerListener with a planner controller so as to be notified 
 * of any bound attribute updates. 
 *
 * @author Consulting & Development. Iñaki Ayerbe - 17/01/2007
 *
 */

public interface IPlannerListener {

	/**
	 * This method gets called when an event is added.
	 * 
	 * @param A IEvent object describing the event source that has changed.
	 */
	void eventAdded(IEvent event);
	
	/**
	 * This method gets called when an event is removed.
	 * 
	 * @param A IEvent object describing the event source that has changed.
	 */
	void eventRemoved(IEvent event);

	/**
	 * This method gets called when an event is updated.
	 * 
	 * @param A IEvent object describing the event source that has changed.
	 */
	void eventUpdated(IEvent event);
}
