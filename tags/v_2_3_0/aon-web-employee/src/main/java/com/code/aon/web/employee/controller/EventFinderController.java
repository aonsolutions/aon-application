/**
 * 
 */
package com.code.aon.web.employee.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.ui.planner.ControllerUtil;
import com.code.aon.ui.planner.EventManager;
import com.code.aon.ui.planner.IPlannerCallbackHandler;
import com.code.aon.ui.planner.core.CalendarScheduleModel;
import com.code.aon.ui.planner.core.Event;
import com.code.aon.ui.planner.util.PlannerUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 23/08/2007
 *
 */
public class EventFinderController {

	/** Título del Evento a buscar. */
	private String title;
	/** Categoría del Evento a buscar. */
	private Integer category;
	/** Fecha del Evento a buscar. */
	private Date date;
	/** Found events. */
	private DataModel events;

	/**
	 * @return the category
	 */
	public Integer getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(Integer category) {
		this.category = category;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the events
	 */
	public DataModel getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(DataModel events) {
		this.events = events;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns a list of all categories.
	 * 
	 * @return
	 */
	public List<SelectItem> getCategories() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		for (EventCategory ec : EventCategory.values()) {
			types.add( new SelectItem( ec.ordinal(), ec.getName(locale) ) );
		}
		types.add( new SelectItem( -1, "Todas" ) );
		return types;
	}

	/**
	 * Initializes attributes.
	 *   
	 * @param event
	 */
	public void init(ActionEvent event) {
		setCategory( null );
		setTitle( null );
		setDate( null );
		setEvents( null );
	}

	/**
	 * Accepts search parameters and ask <code>EventSearchManager</code> to find them.
	 *   
	 * @param event
	 */
	public void onAccept(ActionEvent event) {
		AonCalendar c = 
			( (CalendarScheduleModel) ControllerUtil.getPlannerController().getScheduleModel() ).getCalendar();
		EventCategory ec = null;
		if ( this.category != -1 )
			ec = EventCategory.values()[ this.category ];
		setEvents( new ListDataModel( PlannerUtil.getEvents( c, this.title, this.date, ec ) ) );
	}

	/**
	 * Cancels search operation, initializing search attributes.
	 * 
	 * @param event
	 */
	public void onCancel(ActionEvent event) {
		init( event );
	}

	/**
	 * Assigns to <code>EventManger</code> which is the selected event.
	 * 
	 * @param event
	 */
	public void onSelect(ActionEvent event) {
    	EventManager em = ControllerUtil.getEventManager();
    	em.setNew(false);
    	Event evt = (Event) this.events.getRowData();
    	String msg = 
    		ControllerUtil.getPlannerBundle().getString( "aon_planner_edit_event" ) + ": " + 
    		evt.getSubtitle() + ", " + evt.getTitle();
    	IPlannerCallbackHandler cb = ControllerUtil.getPlannerController();
    	if ( evt.getCategory() == EventCategory.WORK ) {
    		cb = ControllerUtil.getWorkingTime();
    	}
    	em.initialize( evt, cb, msg );
	}

}