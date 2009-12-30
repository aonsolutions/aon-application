package com.code.aon.ui.company.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkActivity;
import com.code.aon.planner.calendar.CalendarHelper;
import com.code.aon.planner.model.CalendarScheduleModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.planner.CalendarManagerBean;
import com.code.aon.ui.planner.ControllerUtil;
import com.code.aon.ui.planner.PlannerController;
import com.code.aon.ui.util.AonUtil;

public class WorkActivityController extends BasicController {
	
	public static final String MANAGER_BEAN_NAME = "workactivity";

	private AonCalendar calendar;
	
    public WorkActivityController() throws ManagerBeanException {
        super();
    }

    /**
     * Return Company calendar.
     * @return
     */
    public AonCalendar getAonCalendar() {
    	return this.calendar;
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.ui.form.BasicController#onAccept(javax.faces.event.ActionEvent)
     */
	public void onAccept(ActionEvent event) {
		accept(event);
	}

	/**
	 * This method is called whenever a working activity calendar is requested.
	 * 
	 * @param event
	 * @throws CalendarException
	 * @throws ManagerBeanException
	 */
	public void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException {
		PlannerController planner = ControllerUtil.getPlannerController();
		CalendarScheduleModel csm = getCalendarScheduleModel( (WorkActivity) getTo() );
		planner.initialize( csm, MANAGER_BEAN_NAME, false );
	}

	/**
     * Return selected working activity schedule model, if not exist loads it from 
     * working place schedule model.
     * 
	 * @param activity
     * @return
     * @throws CalendarException 
     * @throws ManagerBeanException 
     */
	public CalendarScheduleModel getCalendarScheduleModel(WorkActivity activity) 
				throws CalendarException, ManagerBeanException {
//	Ask for Calendar to CalendarHelper instead of CalendarManagerBean 
		if ( activity.getCalendar() == null ) {
			CalendarManagerBean cmb = ControllerUtil.getCalendarManagerBean();
//	Ask for working place calendar, if not exist creates it and returns to the activity.
			WorkPlaceController wpc = 
				(WorkPlaceController) AonUtil.getController( WorkPlaceController.MANAGER_BEAN_NAME );
			AonCalendar wpCalendar = 
				wpc.getCalendarScheduleModel( activity.getWorkPlace() ).getCalendar();
			this.calendar = cmb.cloneCalendar(wpCalendar);
			String description = 
				activity.getDescription() + "/" + activity.getWorkPlace().getDescription();
			this.calendar.setDescription( description );
			this.calendar.setAddSpreadEventAllowed( true );
			cmb.updateCalendar( this.calendar );
			activity.setCalendar( this.calendar.getPrimaryKey() );
			getManagerBean().update( activity );
		} else {
			this.calendar = CalendarHelper.getCalendar( activity.getCalendar() );
		}
        return new CalendarScheduleModel(this.calendar);
    }

}