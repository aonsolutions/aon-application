/**
 * 
 */
package com.code.aon.ui.planner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Status;

import org.apache.myfaces.custom.calendar.HtmlInputCalendar;
import org.apache.myfaces.custom.schedule.model.ScheduleEntry;
import org.apache.myfaces.shared_tomahawk.util.MessageUtils;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.calendar.CalendarUtil;
import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.resources.Resource;
import com.code.aon.planner.IEvent;
import com.code.aon.planner.core.IncidenceType;
import com.code.aon.planner.core.dao.IPlannerAlias;
import com.code.aon.planner.enumeration.EventStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.planner.CalendarManagerBean;
import com.code.aon.ui.planner.ControllerUtil;
import com.code.aon.ui.planner.core.Event;
import com.code.aon.ui.planner.util.PlannerUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 03/01/2007
 *
 */
public class IncidencesController extends BasicController {

	public static final String MANAGER_BEAN_NAME = "incidences";

	/** Selected Date */
	private Date date;
	/** Selected incidence. */
	IEvent event;
	/** Incidence Type */
	private Integer incidenceType;
	/** Incidence List of Types */
	private List<SelectItem> incidenceTypes;

	/** Incidence starting hour */
	private Integer hour = 24;
	/** Incidence starting minute */
	private Integer minute = 0;
	/** Length (hundredth hours). */
	private double length;

	/** All day Incidence */
	private boolean isAllday;
	/** Frecuency Incidence. This tells the incidence has an ending date */
	private boolean isFrecuency;
	/** Allows a user to add a new Incidence */
	private boolean isEnabled;
	/** Enables resource drop component. */
	private boolean isResourceChangingEnabled;

	/** Resources list */
	private List<Resource> resources = new ArrayList<Resource>();
	/** Resource reference */
	private Resource resource;
	/** SelectItem Resources */
	private List<SelectItem> itemResources = new ArrayList<SelectItem>(); 

    /**
     * @param date The date to set.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return Returns the date.
     */
    public Date getDate() {
        return date;
    }

	/**
	 * @return the event
	 */
	public IEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(IEvent event) {
		this.event = event;
	}

	/**
	 * @return the incidenceType
	 */
	public Integer getIncidenceType() {
		return incidenceType;
	}

	/**
	 * @param incidenceType the incidenceType to set
	 */
	public void setIncidenceType(Integer incidenceType) {
		this.incidenceType = incidenceType;
	}

	/**
	 * @return the hour
	 */
	public Integer getHour() {
		return hour;
	}

	/**
	 * @param hour the hour to set
	 */
	public void setHour(Integer hour) {
		this.hour = hour;
	}

	/**
	 * @return the minute
	 */
	public Integer getMinute() {
		return minute;
	}

	/**
	 * @param minute the minute to set
	 */
	public void setMinute(Integer minute) {
		this.minute = minute;
	}

	/**
	 * @return the length
	 */
	public double getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(double length) {
		this.length = length;
	}

	/**
	 * @return the isAllday
	 */
	public boolean isAllday() {
		return isAllday;
	}

	/**
	 * @param isAllday the isAllday to set
	 */
	public void setAllday(boolean isAllday) {
		this.isAllday = isAllday;
	}

	/**
	 * @return the isFrecuency
	 */
	public boolean isFrecuency() {
		return isFrecuency;
	}

	/**
	 * @param isFrecuency the isFrecuency to set
	 */
	public void setFrecuency(boolean isFrecuency) {
		this.isFrecuency = isFrecuency;
	}

	/**
	 * @return the isEnabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled the isEnabled to set
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * @return the isResourceChangingEnabled
	 */
	public boolean isResourceChangingEnabled() {
		return isResourceChangingEnabled;
	}

	/**
	 * @param isResourceChangingEnabled the isResourceChangingEnabled to set
	 */
	public void setResourceChangingEnabled(boolean isResourceChangingEnabled) {
		this.isResourceChangingEnabled = isResourceChangingEnabled;
	}

	/**
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * @return the itemResources
	 */
	public List<SelectItem> getItemResources() {
		return itemResources;
	}

	/**
	 * @param itemResources the itemResources to set
	 */
	public void setItemResources(List<SelectItem> itemResources) {
		this.itemResources = itemResources;
	}

	/**
	 * @return the resources
	 */
	public List<Resource> getResources() {
		return resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	/**
	 * @return the incidenceTypes
	 */
	public List<SelectItem> getIncidenceTypes() {
		if ( this.incidenceTypes == null ) {
			this.incidenceTypes = Utils.getIncidenceTypes();
			this.incidenceType = (Integer) ( (SelectItem) this.incidenceTypes.get(0) ).getValue();
		}
		return incidenceTypes;
	}

	/**
	 * @return the day hours
	 */
	public List<SelectItem> getHours() {
		List<SelectItem> hours = new ArrayList<SelectItem>();
		for (int i = 0; i < 24; i++) {
			hours.add( new SelectItem( new Integer(i), "" + i) );
		}
		return hours;
	}

	/**
	 * @return the minutes
	 */
	public List<SelectItem> getMinutes() {
		List<SelectItem> minutes = new ArrayList<SelectItem>();
		minutes.add( new SelectItem( 0, "00" ) );
		minutes.add( new SelectItem( 15, "15" ) );
		minutes.add( new SelectItem( 30, "30" ) );
		minutes.add( new SelectItem( 45, "45" ) );
		return minutes;
	}

	/**
	 * Sets incidence selected index.
	 * 
	 * @param id
	 */
	public void setSelected(String id) {
		List l = (List) super.model.getWrappedData();
		for (int i = 0; i < l.size(); i++) {
			IEvent ievent = (IEvent) l.get( i );
			if ( ievent.getId().equals( id ) ) {
				super.model.setRowIndex( i );
				break;
			}
		}
	}

	/**
	 * Initializes Incidences controller with a resource passed by parameter.
	 * 
	 * @param r
	 */
	public void initialize(Resource r, boolean enableResourceChanging) {
		setResourceChangingEnabled( enableResourceChanging );
		setResource( r );
		try {
			findStartTime();
			super.onSearch( null );
		} catch (CalendarException e) {
			setEnabled(false);
			MessageUtils.addMessage( FacesMessage.SEVERITY_FATAL, e.getMessage(), null);
		}
	}

	@SuppressWarnings("unused")
	public void onLoad(MenuEvent event) {
        try {
            ControllerEvent evt = new ControllerEvent(this);
            controllerListenerSupport.fireBeforeBeanCreated(evt);
            init();
            controllerListenerSupport.fireAfterBeanCreated(evt);
            setNew(true);
        } catch (ControllerListenerException e) {
            addMessage(e.getMessage());
            throw new AbortProcessingException(e.getMessage(), e);
        }
	}

    /**
     * <p>When a date is selected in the calendar component, navigate to that date.</p>
     *
     * @param event the action event
     */
    public void calendarValueChanged(ValueChangeEvent event) {
    	Date date = (Date) event.getNewValue();
    	if ( date != null ) {
    		this.date = date;
    		( (Event) this.event ).setStartTime( date );
    		( (Event) this.event ).setEndTime( date );
        	( (HtmlInputCalendar) event.getComponent() ).setValue( date );
    	}
    }

    /**
     * <p>
     * When a Resource is selected, set it to Incidence<code>ITransferObject</code>.
     * </p>
     * 
     * @param event
     */
    public void resourceChanged(ValueChangeEvent event) {
    	Integer id = (Integer) event.getNewValue();
    	if ( id != null ) {
			Iterator<Resource> iter = this.resources.iterator();
			while (iter.hasNext()) {
				Resource r = iter.next();
				if ( id.intValue() == r.getId().intValue() ) {
					this.resource = r;
					try {
						initializeModel();
						findStartTime();
					} catch (CalendarException e) {
						setEnabled(false);
						MessageUtils.addMessage( FacesMessage.SEVERITY_FATAL, e.getMessage(), null);
					}
					return;
				}
			}
    	}
    }

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onReset(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onReset(ActionEvent event) {
        try {
            ControllerEvent evt = new ControllerEvent(this);
            init();
            controllerListenerSupport.fireBeforeBeanReset(evt);
            setNew( true );
            controllerListenerSupport.fireAfterBeanReset(evt);
        } catch (ControllerListenerException e) {
            addMessage(e.getMessage());
            throw new AbortProcessingException(e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onAccept(javax.faces.event.ActionEvent)
	 */
	public void onAccept(ActionEvent event) {
        try {
	    	if ( this.incidenceType == 9 )
	    		findEndTime();
			if ( this.isAllday ) {
		    	CalendarManagerBean cmb = ControllerUtil.getCalendarManagerBean();
		    	Integer id = this.resource.getCalendar();
				AonCalendar aonCalendar = cmb.getCalendar( id );
				this.length = PlannerUtil.calcWorkingHours( aonCalendar, this.event.getStartTime() );
			}
			VEvent vevent = createVEvent();
	    	CalendarManagerBean cmb = ControllerUtil.getCalendarManagerBean();
	    	AonCalendar aonCalendar = cmb.getCalendar( this.resource.getCalendar() );
	        ControllerEvent evt = new ControllerEvent(this);
	        if ( isNew() ) {
	            controllerListenerSupport.fireBeforeBeanAdded(evt);
	    		aonCalendar.getCalendar().getComponents().add( vevent );
	            controllerListenerSupport.fireAfterBeanAdded(evt);
	        } else {
	            controllerListenerSupport.fireBeforeBeanUpdated(evt);
	    		aonCalendar.updateEvent( vevent );
	            controllerListenerSupport.fireAfterBeanUpdated(evt);
	        }
			cmb.updateCalendar( aonCalendar );
        	initializeModel();
        	setNew( false );
        } catch (ControllerListenerException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_FATAL, e.getMessage(), null);
            throw new AbortProcessingException(e.getMessage(), e);
        } catch (ManagerBeanException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_FATAL, e.getMessage(), null);
            throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			if ( isResourceChangingEnabled() )
				init();
		}
	}

    /* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onSelect(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onSelect(ActionEvent event) {
        try {
            ControllerEvent evt = new ControllerEvent(this);
            controllerListenerSupport.fireBeforeBeanSelected(evt);
			setEvent( (IEvent) super.model.getRowData() );
			setDate( this.event.getStartTime() );
			setAllday( this.event.isAllDay() );
			setFrecuency( this.event.getPeriod().getDuration().getDays() > 0 );
			setEnabled( true );
			Calendar calendar = Calendar.getInstance();
			calendar.setTime( this.event.getStartTime() );
			this.hour = calendar.get( Calendar.HOUR_OF_DAY );
			this.minute = calendar.get( Calendar.MINUTE );
			int minutes = this.event.getPeriod().getDuration().getMinutes();
			minutes = (minutes == 0 )? minutes: ( 60 / minutes );
			this.length = this.event.getPeriod().getDuration().getHours() + minutes;
			int startIndexType = this.event.getDescription().indexOf( '(' ) + 1; 
			int endIndexType = this.event.getDescription().indexOf( ')' ); 
			this.incidenceType = Integer.valueOf( this.event.getDescription().substring( startIndexType, endIndexType ) );
            setNew( false );
            controllerListenerSupport.fireAfterBeanSelected(evt);
        } catch (ControllerListenerException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_FATAL, e.getMessage(), null);
            throw new AbortProcessingException(e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onRemove(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onRemove(ActionEvent event) {
        try {
            ControllerEvent evt = new ControllerEvent(this);
            controllerListenerSupport.fireBeforeBeanRemoved(evt);
	    	CalendarManagerBean cmb = ControllerUtil.getCalendarManagerBean();
	    	AonCalendar aonCalendar = cmb.getCalendar( this.resource.getCalendar() );
            aonCalendar.removeEvent( this.event.getId() );
            controllerListenerSupport.fireAfterBeanRemoved(evt);
            cmb.updateCalendar( aonCalendar );
            initializeModel();
            setNew( true );
			init();
        } catch (ControllerListenerException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_FATAL, e.getMessage(), null);
            throw new AbortProcessingException(e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#initializeModel()
	 */
	@Override
	public void initializeModel() {
		try {
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeModelInitialized(evt);
			List<ScheduleEntry> list = new ArrayList<ScheduleEntry>();
			CalendarManagerBean cmb = ControllerUtil.getCalendarManagerBean();
			Integer id = this.resource.getCalendar();
			AonCalendar aonCalendar = cmb.getCalendar( id );
			ComponentList components = aonCalendar.getVEvents( EventCategory.INCIDENCE );
			for (Iterator i = components.iterator(); i.hasNext();) {
				VEvent vevent = (VEvent) i.next();
				Date start = CalendarUtil.getDate( (DateTime) vevent.getStartDate().getDate() );
				Date end = null;
				if ( vevent.getProperties().getProperty( Property.DTEND ) != null )
					end = CalendarUtil.getDate( (DateTime) vevent.getEndDate().getDate() );
				else if ( vevent.getProperties().getProperty( Property.DURATION ) != null ) {
					Duration duration = 
						(Duration) vevent.getProperties().getProperty(Property.DURATION);
					Period p =  
						new Period( (DateTime) vevent.getStartDate().getDate(), duration.getDuration() );
					end = CalendarUtil.getDate( p.getEnd() );
				}
				list.add( PlannerUtil.getScheduleEntry(vevent, start, end) );
			}
			super.model = new ListDataModel( list );
			controllerListenerSupport.fireAfterModelInitialized(evt);
		} catch (ControllerListenerException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	/**
	 * Finds current resource working start time. 
	 * 
	 * @throws CalendarException
	 */
	public void findStartTime() throws CalendarException {
    	CalendarManagerBean cmb = ControllerUtil.getCalendarManagerBean();
		AonCalendar aonCalendar = cmb.getCalendar( this.resource.getCalendar() );
		ComponentList cl = aonCalendar.getVEvents(EventCategory.WORK);
		if ( cl.size() > 0 ) {
			Iterator it = cl.iterator();
			while (it.hasNext()) {
				VEvent vevent = (VEvent) it.next();
			    Date start = CalendarUtil.getDate( (DateTime)vevent.getStartDate().getDate() );
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(start);
				if ( calendar.get(Calendar.HOUR_OF_DAY) < this.hour ) {
					setHour( calendar.get(Calendar.HOUR_OF_DAY) );
					setMinute( calendar.get(Calendar.MINUTE) );
				}
			}
		} else {
			String msg = Utils.getMessage( "aon_planner_workingtime_not_found", new String[] { this.resource.getOwner() } );
			throw new CalendarException( msg ); 
		}
    }

	/**
	 * Finds current resource working end time. 
	 */
    private void findEndTime() {
    	CalendarManagerBean cmb = ControllerUtil.getCalendarManagerBean();
		AonCalendar aonCalendar = cmb.getCalendar( this.resource.getCalendar() );
		ComponentList cl = aonCalendar.getVEvents(EventCategory.WORK);
		if ( cl.size() > 0 ) {
			Iterator it = cl.iterator();
			while (it.hasNext()) {
				VEvent vevent = (VEvent) it.next();
			    Date end = CalendarUtil.getDate( (DateTime)vevent.getEndDate().getDate() );
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(end);
				if ( calendar.get(Calendar.HOUR_OF_DAY) > this.hour ) {
					setHour( calendar.get(Calendar.HOUR_OF_DAY) );
					setMinute( calendar.get(Calendar.MINUTE) );
				}
			}
		}
    }

    /**
     * Initializes attributes.
     */
    private void init() {
		setEvent( new Event() );
		setDate( new Date() );
		( (Event) this.event ).setStartTime( this.date );
		( (Event) this.event ).setEndTime( this.date );
		setAllday( false );
		setFrecuency( false );
		setEnabled( true );
		this.length = 0.0;
		if ( this.incidenceTypes != null )
			this.incidenceType = (Integer) ( (SelectItem) this.incidenceTypes.get(0) ).getValue();
    }

    /**
	 * Create a calendar component, as an Incidence event.
     * 
     * @return
     * @throws ManagerBeanException 
     */
	private VEvent createVEvent() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( IncidenceType.class );
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( bean.getFieldName( IPlannerAlias.INCIDENCE_TYPE_ID), this.incidenceType );
		List l = bean.getList(criteria);
		if ( l.size() <= 0 ) {
			String msg = Utils.getMessage( "aon_planner_incidence_type_not_found", new Integer[] { this.incidenceType } );
			MessageUtils.addMessage( FacesMessage.SEVERITY_FATAL, msg, null);
			throw new AbortProcessingException();
		}
		IncidenceType type = (IncidenceType) l.get(0);
		Calendar c = Calendar.getInstance();
		c.setTime( event.getStartTime() );
		VEvent vEvent = null;
		if ( this.isAllday ) {
			c.set( Calendar.HOUR_OF_DAY, 0 );
			c.set( Calendar.MINUTE, 0 );
			net.fortuna.ical4j.model.Date start = 
				CalendarUtil.getICalDateTime( c.getTime(), false );
			Dur dur = new Dur(1, 0, 0, 0);
			vEvent = new VEvent( start, dur, type.getDescription() );
		} else {
			c.set( Calendar.HOUR_OF_DAY, this.hour );
			c.set( Calendar.MINUTE, this.minute );
			net.fortuna.ical4j.model.Date start = 
				CalendarUtil.getICalDateTime( c.getTime(), false );
			if ( event.getEndTime().after( event.getStartTime() ) ) { 
				net.fortuna.ical4j.model.Date end = 
					CalendarUtil.getICalDateTime( event.getEndTime(), false);
				vEvent = new VEvent( start, end, type.getDescription() );
			} else {
				int hour = (int)Math.floor( this.length );
				int min  = (int)Math.floor( (this.length - hour) * 60.0 + 0.5 );
				Dur dur = new Dur(0, hour, min, 0);
				vEvent = new VEvent( start, dur, type.getDescription() );
			}
		}
		vEvent.getProperties().add( new ProdId( getEventId( this.resource ) ) );
		vEvent.getProperties().add( new Location(PlannerUtil.EMPTY_STRING) );
		String description = "(" + type.getId() + ")" + type.getDescription() + "-" + this.length; 
		vEvent.getProperties().add( new Description(description) );
		vEvent.getProperties().add( new Status( EventStatus.CONFIRMED.name() ) );
		vEvent.getProperties().add( new Categories( EventCategory.INCIDENCE.name() ) );
		return vEvent;
	}

	/**
	 * Returns the calendar owner name and surname.
	 * 
	 * @return
	 */
	private String getEventId(Resource r) {
		String id = PlannerUtil.generateEventId( r.getEmployee().getCalendar(), new Date() );
		id += "." + r.getId().toString();
		if ( r.getWorkActivity() == null || r.getWorkActivity().getId() == -1 )
			id += "_" + r.getWorkPlace().getId() + "_null";
		else 
			id += "_" + r.getWorkPlace().getId() + "_" + r.getWorkActivity().getId();
		return id;
	}


}
