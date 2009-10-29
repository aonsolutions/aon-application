/**
 * 
 */
package com.code.aon.web.employee.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkActivity;
import com.code.aon.planner.calendar.CalendarHelper;
import com.code.aon.ui.planner.CalendarManagerBean;
import com.code.aon.ui.planner.ControllerUtil;
import com.code.aon.ui.planner.IPlanner;
import com.code.aon.ui.planner.PlannerController;
import com.code.aon.ui.planner.core.CalendarScheduleModel;
import com.code.aon.ui.util.AonUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 09/11/2007
 *
 */
public class WorkActivityController 
	extends com.code.aon.ui.company.controller.WorkActivityController implements IPlanner {

	/** The calendar. */
	private AonCalendar calendar;
	
    /**
     * Return Company calendar.
     * @return
     */
    public AonCalendar getAonCalendar() {
    	return this.calendar;
    }

	public CalendarScheduleModel getCalendarScheduleModel(Object widget) throws CalendarException, ManagerBeanException {
		WorkActivity activity = (WorkActivity) widget;
//		Ask for Calendar to CalendarHelper instead of CalendarManagerBean 
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

	public void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException {
		PlannerController planner = ControllerUtil.getPlannerController();
		CalendarScheduleModel csm = getCalendarScheduleModel( (WorkActivity) getTo() );
		planner.initialize( csm, MANAGER_BEAN_NAME, false );
	}

}
