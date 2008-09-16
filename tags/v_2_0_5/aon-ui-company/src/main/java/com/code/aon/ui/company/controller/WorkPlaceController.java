package com.code.aon.ui.company.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.planner.calendar.CalendarHelper;
import com.code.aon.planner.model.CalendarScheduleModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.planner.CalendarManagerBean;
import com.code.aon.ui.planner.ControllerUtil;
import com.code.aon.ui.planner.PlannerController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the workPlace maintenance.
 */
public class WorkPlaceController extends BasicController {
	
	public static final String MANAGER_BEAN_NAME = "workplace";

	/** The calendar. */
	private AonCalendar calendar;
	
    /**
     * The empty constructor.
     * 
     * @throws ManagerBeanException the manager bean exception
     */
    public WorkPlaceController() throws ManagerBeanException {
        super();
    }

    /**
     * Gets the calendar.
     * 
     * @return the calendar
     */
    public AonCalendar getAonCalendar() {
    	return this.calendar;
    }

    /**
     * Gets the current working place.
     * 
     * @return
     */
    public WorkPlace getWorkPlace() {
    	return (WorkPlace) getTo();
    }

	/**
	 * Gets a list of company working Places.
	 * 
	 * @return a list with all workPlaces.
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getCompanyWorkPlaces() throws ManagerBeanException {
		List<SelectItem> workplaces = new ArrayList<SelectItem>();
		workplaces.add( new SelectItem( -1, "" ) );
		workplaces.addAll( getAll() );
		return workplaces;
	}

	/**
	 * Gets a list with all workPlaces.
	 * 
	 * @return a list with all workPlaces.
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public List<SelectItem> getAll() throws ManagerBeanException {
		List<SelectItem> workplaces = new ArrayList<SelectItem>();
		Iterator iter = ( (List) super.getModel().getWrappedData() ).iterator();
		while (iter.hasNext()) {
			WorkPlace wp = (WorkPlace) iter.next();
			SelectItem item = new SelectItem( wp.getId(), wp.getDescription() );
			workplaces.add(item);
		}
		return workplaces;
	}

	/**
	 * Execute a search each time the WorkPlace menu option is pressed.
	 * 
	 * @param event
	 */
	public void onSearch(MenuEvent event) {
        this.onSearch((ActionEvent)event);
    }

	/**
	 * This method gets called when the working place calendar is requested.
	 * 
	 * @param event
	 * @throws CalendarException
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unused")
	public void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException {
		PlannerController planner = ControllerUtil.getPlannerController();
		CalendarScheduleModel csm = getCalendarScheduleModel( (WorkPlace) getTo() );
		planner.initialize( csm, MANAGER_BEAN_NAME, false );
	}

    /**
     * Return selected working place schedule model, if not exist loads it from 
     * company schedule model.
     * 
	 * @param workPlace
     * @return
     * @throws CalendarException 
     * @throws ManagerBeanException 
     */
	public CalendarScheduleModel getCalendarScheduleModel(WorkPlace workPlace) 
				throws CalendarException, ManagerBeanException {
//	Ask for Calendar to CalendarHelper instead of CalendarManagerBean 
		if ( workPlace.getCalendar() == null ) {
			CompanyController cc = 
				(CompanyController) AonUtil.getController( CompanyController.COMPANY_NAME );
			AonCalendar cCalendar = cc.getCalendarScheduleModel().getCalendar();
			CalendarManagerBean cmb = ControllerUtil.getCalendarManagerBean();
			this.calendar = cmb.cloneCalendar( cCalendar );
			this.calendar.setDescription( workPlace.getAddress().getAddress() );
			this.calendar.setAddSpreadEventAllowed( true );
			cmb.updateCalendar( this.calendar );
			workPlace.setCalendar( this.calendar.getPrimaryKey() );
			getManagerBean().update( workPlace );
		} else {
			this.calendar = CalendarHelper.getCalendar( workPlace.getCalendar() );
		}
		return new CalendarScheduleModel(this.calendar);    
	}

}
