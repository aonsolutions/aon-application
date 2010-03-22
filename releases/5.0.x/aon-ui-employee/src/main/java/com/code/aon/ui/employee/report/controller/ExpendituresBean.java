/**
 * 
 */
package com.code.aon.ui.employee.report.controller;

import java.util.Date;

import com.code.aon.company.WorkPlace;
import com.code.aon.company.resources.Employee;
import com.code.aon.employee.ExpendituresItems;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 13/09/2007
 *
 */
public class ExpendituresBean {

	private WorkPlace workPlace;
	private Employee employee;
	private ExpendituresItems item;
	private Date date;
	private Double amount;
	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
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
	 * @return the item
	 */
	public ExpendituresItems getItem() {
		return item;
	}
	/**
	 * @param item the item to set
	 */
	public void setItem(ExpendituresItems item) {
		this.item = item;
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
