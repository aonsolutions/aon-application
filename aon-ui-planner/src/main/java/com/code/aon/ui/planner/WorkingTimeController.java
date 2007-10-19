/*
 * Created on 21-nov-2005
 *
 */
package com.code.aon.ui.planner;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarUtil;
import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.common.ManagerBeanException;

import com.code.aon.planner.EventException;
import com.code.aon.planner.IEvent;
import com.code.aon.planner.IPlannerListener;
import com.code.aon.planner.core.Event;
import com.code.aon.planner.enumeration.EventStatus;
import com.code.aon.planner.model.CalendarScheduleModel;
import com.code.aon.planner.util.PlannerUtil;
import com.code.aon.ui.planner.EventManager;
import com.code.aon.ui.planner.IPlannerCallbackHandler;
import com.code.aon.ui.planner.PlannerController;

public class WorkingTimeController implements IPlannerCallbackHandler, IPlannerListener {

	public static final String MANAGER_BEAN_NAME = "workingTime";

	/** Working Time DataModel. A list of working time periods. */
    private DataModel workingTimeModel;
    /** Working hours during the current year. */
    private double amount;
    /** Agreement Working hours during the current year. */
    private int agreementAmount;
    /** Selecteed event index */
    private int selectedEventIndex;
    /** Old selected event */
    private IEvent oldEvent;

	/**
	 * @return Returns the model.
	 */
	public DataModel getModel() {
		return workingTimeModel;
	}

	/**
	 * @param model The model to set.
	 */
	public void setModel(DataModel workingTimeModel) {
		this.workingTimeModel = workingTimeModel;
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @return the agreementAmount
	 */
	public int getAgreementAmount() {
		return agreementAmount;
	}

	/**
	 * @param agreementAmount the agreementAmount to set
	 */
	public void setAgreementAmount(int agreementAmount) {
		this.agreementAmount = agreementAmount;
	}

	/**
	 * Returns true if adding a new spread event is allowed, otherwise false.
	 * 
	 * @return
	 */
	public boolean isAddSpreadEventAllowed() {
    	CalendarScheduleModel csm = 
    		(CalendarScheduleModel) ControllerUtil.getPlannerController().getScheduleModel();
    	return csm.getCalendar().isAddSpreadEventAllowed();
	}

	/**
	 * @param bol
	 */
	public void setAddSpreadEventAllowed(boolean bol) {
    	CalendarScheduleModel csm = 
    		(CalendarScheduleModel) ControllerUtil.getPlannerController().getScheduleModel();
    	csm.getCalendar().setAddSpreadEventAllowed( bol );
	}

	/**
     * Inicializa el Evento horario de trabajo asociado al Calendario pasado por parámetro.
     * 
     * @param calendar
     * @param agreementAmount
     * @throws ManagerBeanException 
     */
    public void initialize(AonCalendar calendar, int agreementAmount) throws ManagerBeanException {
    	this.agreementAmount = agreementAmount;
		init(calendar);
	}

	/**
	 * This method gets called when the adding a new spread event is checked.
	 * 
	 * @param event
	 */
	public void addSpreadEventAllowed(ValueChangeEvent event) {
    	CalendarScheduleModel csm = 
    		(CalendarScheduleModel) ControllerUtil.getPlannerController().getScheduleModel();
    	csm.getCalendar().setAddSpreadEventAllowed( ( (Boolean) event.getNewValue() ).booleanValue() );
		ControllerUtil.getCalendarManagerBean().updateCalendar( csm.getCalendar() );
	}

	/**
	 * Acción destinada a crear un evento nuevo de tipo: WORK.
     * 
     * @return
	 */
	@SuppressWarnings("unused")
	public void onReset(ActionEvent event) {
		PlannerController planner = ControllerUtil.getPlannerController(); 
    	EventManager em = ControllerUtil.getEventManager();
    	em.setNew(true);
    	CalendarScheduleModel csm = (CalendarScheduleModel) planner.getScheduleModel();
    	int id = csm.getCalendar().getPrimaryKey().intValue();
    	ResourceBundle bundle = ControllerUtil.getPlannerBundle();
    	String msg = bundle.getString( "aon_planner_new_event" ) + ": " + 
    				bundle.getString( "aon_planner_working_label" );
    	em.initialize( PlannerUtil.createWorkingTimeEvent( id, new Date() ), this, msg );
	}

	/**
	 * Acción destinada a visualizar un evento de tipo: WORK, para su posterior modificación.
     * 
     * @return
	 * @throws CloneNotSupportedException 
	 */
	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) throws CloneNotSupportedException {
    	EventManager em = ControllerUtil.getEventManager();
    	em.setNew(false);
    	Event evt = (Event) this.workingTimeModel.getRowData();
    	evt.setState( EventStatus.LOCKED );
    	this.oldEvent = (Event) evt.clone(); 
    	this.selectedEventIndex = this.workingTimeModel.getRowIndex();
    	String msg = 
    		ControllerUtil.getPlannerBundle().getString( "aon_planner_edit_event" ) + ": " + 
    		evt.getSubtitle() + ", " + evt.getTitle(); 
    	em.initialize( evt, this, msg );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.planner.IPlannerCallbackHandler#outcome()
	 */
	public String getOutcome() {
		return ControllerUtil.getPlannerController().getOutcome();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.planner.IPlannerCallbackHandler#isSpreadable()
	 */
	public boolean isSpreadable() {
		return ControllerUtil.getPlannerController().isSpreadable();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.planner.IPlannerCallbackHandler#getCategories()
	 */
	public List getCategories() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> categories = new LinkedList<SelectItem>();
        categories.add( new SelectItem( EventCategory.WORK, EventCategory.WORK.getName(locale)) );
        return categories;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.planner.IPlannerCallbackHandler#refresh(com.code.aon.planner.IEvent)
	 */
	public void refresh(IEvent event) {
		ControllerUtil.getPlannerController().refresh(event);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.planner.IPlannerCallbackHandler#hasEvents(com.code.aon.planner.IEvent)
	 */
	public boolean hasEvents(Date from, Date to, EventCategory[] ec, String id) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.planner.IPlannerCallbackHandler#add(com.code.aon.planner.IEvent)
	 */
	public void add(IEvent event) throws EventException {
		PlannerController planner = ControllerUtil.getPlannerController();
		AonCalendar aonCalendar = 
			( (CalendarScheduleModel) planner.getScheduleModel() ).getCalendar();
		if ( PlannerUtil.validate(aonCalendar, event) ) {
			PlannerUtil.addExDates(aonCalendar, event);
			planner.add(event);
		} else {
			EventException e = new EventException("aon_planner_workevent_overlap_exception");
			e.setParameters( new Object[] {event.getTitle(), event.getStartTime(), event.getEndTime()} );
			throw e;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.planner.IPlannerCallbackHandler#remove(com.code.aon.planner.IEvent)
	 */
	public boolean remove(IEvent event) throws EventException {
		return ControllerUtil.getPlannerController().remove(event);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.planner.IPlannerCallbackHandler#update(com.code.aon.planner.IEvent)
	 */
	@SuppressWarnings("unchecked")
	public void update(IEvent event) throws EventException {
		PlannerController planner = ControllerUtil.getPlannerController();
		AonCalendar aonCalendar = 
			( (CalendarScheduleModel) planner.getScheduleModel() ).getCalendar();
		if ( PlannerUtil.validate(aonCalendar, event) ) {
			PlannerUtil.addExDates(aonCalendar, event);
			planner.update(event);
		} else {
			List l = (List) this.workingTimeModel.getWrappedData();
			l.set( selectedEventIndex, oldEvent );
			EventException e = new EventException("aon_planner_workevent_overlap_exception");
			e.setParameters( new Object[] {event.getTitle(), event.getStartTime(), event.getEndTime()} );
			throw e;
		}
	}

    /* (non-Javadoc)
	 * @see com.code.aon.planner.IPlannerListener#eventAdded(com.code.aon.planner.IEvent)
	 */
	@SuppressWarnings("unchecked")
	public void eventAdded(IEvent event) {
		if (event.getCategory().equals(EventCategory.WORK) ) {
			( (List)this.workingTimeModel.getWrappedData() ).add(event);
			calculateStartAndEndHours(event);
		}
		PlannerController planner = ControllerUtil.getPlannerController();
		CalendarScheduleModel csm = (CalendarScheduleModel) planner.getScheduleModel();
		this.amount = 
			PlannerUtil.calcYearlyHours( PlannerUtil.getYearlyComponentList( csm.getCalendar() ), null );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.planner.IPlannerListener#eventRemoved(com.code.aon.planner.IEvent)
	 */
	@SuppressWarnings("unchecked")
	public void eventRemoved(IEvent event) {
		PlannerController planner = ControllerUtil.getPlannerController();
		CalendarScheduleModel csm = (CalendarScheduleModel) planner.getScheduleModel();
		if (event.getCategory().equals(EventCategory.WORK) ) {
			init( csm.getCalendar() );
		} else {
			this.amount = 
				PlannerUtil.calcYearlyHours( PlannerUtil.getYearlyComponentList( csm.getCalendar() ), null );
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.planner.IPlannerListener#eventUpdated(com.code.aon.planner.IEvent)
	 */
	@SuppressWarnings("unchecked")
	public void eventUpdated(IEvent event) {
		if ( event.getCategory().equals(EventCategory.WORK) ) {
			calculateStartAndEndHours(event);
		}
		PlannerController planner = ControllerUtil.getPlannerController();
		CalendarScheduleModel csm = (CalendarScheduleModel) planner.getScheduleModel();
		this.amount = 
			PlannerUtil.calcYearlyHours( PlannerUtil.getYearlyComponentList( csm.getCalendar() ), null );
	}

    /**
     * Load Working Time and FreeWork Time shifts.
     * 
     * @return
     */
	@SuppressWarnings("unchecked")
    private final void init(AonCalendar calendar) {
    	int startHour = 24, endHour = 1;
		this.workingTimeModel = new ListDataModel(new ArrayList());
		ComponentList components = calendar.getVEvents(EventCategory.WORK); 
		Iterator iter = components.iterator();
		while (iter.hasNext()) {
			VEvent vevent = (VEvent) iter.next();
		    Date start = CalendarUtil.getDate( (DateTime)vevent.getStartDate().getDate() );
		    Date end = CalendarUtil.getDate( (DateTime)vevent.getEndDate().getDate() );
		    IEvent event = (IEvent) PlannerUtil.getScheduleEntry(vevent, start, end);
			( (List)this.workingTimeModel.getWrappedData() ).add(event);
			startHour = getStartHour(startHour, event);
			endHour = getEndHour(endHour, event); 
		}
		if (24 == startHour)
			startHour = 1;
		if (1 == endHour)
			endHour = 24;
		ControllerUtil.getPlannerController().setStartHour(startHour);
		ControllerUtil.getPlannerController().setEndHour(endHour);
		this.amount = 
			PlannerUtil.calcYearlyHours( PlannerUtil.getYearlyComponentList( calendar ), null);
    }

	/**
     * Calculate the calendar start hour using the current hour and compares it with 
     * the event start hour and sets it to the planner.
     * 
     * @param startHour
     * @param event
     * @return
     */
	private int getStartHour(int startHour, IEvent event) {
		return (startHour > event.getStartHour())? event.getStartHour(): startHour;
	}

    /**
     * Calculate the calendar end hour using the current hour and compares it with 
     * the event end hour and sets it to the planner.
     * 
     * @param endHour
     * @param event
     * @return
     */
	private int getEndHour(int endHour, IEvent event) {
		return (endHour < event.getEndHour())? event.getEndHour(): endHour; 
	}

	/**
     * Re-calculate planner start and end hour comparing with the event passed by parameter.
     * 
     * @param event
     */
    private void calculateStartAndEndHours(IEvent event) {
		PlannerController planner = ControllerUtil.getPlannerController();
		int startHour = planner.getStartHour();
		int endHour = planner.getEndHour();
		startHour = getStartHour(startHour, event);
		endHour = getEndHour(endHour, event); 
		planner.setStartHour(startHour);
		planner.setEndHour(endHour);
    }

}
