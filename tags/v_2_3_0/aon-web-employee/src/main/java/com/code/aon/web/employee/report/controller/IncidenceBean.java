/**
 * 
 */
package com.code.aon.web.employee.report.controller;

import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.resources.Employee;
import com.code.aon.planner.IEvent;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 13/09/2007
 *
 */
public class IncidenceBean {

	private WorkPlace workPlace;
	private WorkActivity workActivity;
	private Employee employee;
	private IEvent event;
	/**
	 * @return the employee
	 */
	public Employee getEmployee() {
		return employee;
	}
	/**
	 * @param employee the employee to set
	 */
	public void setEmployee(Employee employee) {
		this.employee = employee;
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
	 * @return the workActivity
	 */
	public WorkActivity getWorkActivity() {
		return workActivity;
	}
	/**
	 * @param workActivity the workActivity to set
	 */
	public void setWorkActivity(WorkActivity workActivity) {
		this.workActivity = workActivity;
	}
	/**
	 * @return the workPlace
	 */
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	/**
	 * @param workPlace the workPlace to set
	 */
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

}
