/**
 * 
 */
package com.code.aon.ui.planner;

import javax.faces.event.ActionEvent;

import com.code.aon.calendar.CalendarException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.planner.core.CalendarScheduleModel;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 09/11/2007
 *
 */
public interface IPlanner {

	/**
     * Return selected widget schedule model, if not exist loads it from 
     * parent widget schedule model.
     * 
	 * @param widget
     * @return
     * @throws CalendarException 
     * @throws ManagerBeanException 
     */
	CalendarScheduleModel getCalendarScheduleModel(Object widget) throws CalendarException, ManagerBeanException;

	/**
	 * This method is called whenever a widget calendar is requested.
	 * 
	 * @param event
	 * @throws CalendarException
	 * @throws ManagerBeanException
	 */
	void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException;

}
