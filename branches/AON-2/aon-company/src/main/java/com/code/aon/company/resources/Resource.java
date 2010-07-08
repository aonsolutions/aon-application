/**
 * 
 */
package com.code.aon.company.resources;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 07/03/2007
 *
 */
@Entity
@Table(name="resource")
public class Resource implements ITransferObject {

	/** Transfer Object Identifier. */
	private Integer id;
	
	/** Employee reference. */
	private Employee employee;

	/** Work Place reference. */
	private WorkPlace workPlace;

	/** Work Activity reference. */
	private WorkActivity workActivity;

	/** Starting date. */
	private Date startingDate;

	/** Ending date. */
	private Date endingDate;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return this.id;
	}

	/**
	 * Set identifier.
	 * 
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Return the employee 
	 */
	@OneToOne
	@JoinColumn(name="employee")
	public Employee getEmployee() {
		return this.employee;
	}

	/**
	 * Set employee.
	 * 
	 * @param employee
	 */
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	/**
	 * Return the working place 
	 */
	@OneToOne
	@JoinColumn(name="workplace")
	public WorkPlace getWorkPlace() {
		return this.workPlace;
	}

	/**
	 * Set working place.
	 * 
	 * @param workPlace
	 */
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	/**
	 * Return the working activity.  
	 */
	@OneToOne
	@JoinColumn(name="workactivity")
	public WorkActivity getWorkActivity() {
		return this.workActivity;
	}

	/**
	 * Set working activity.
	 * 
	 * @param workActivity
	 */
	public void setWorkActivity(WorkActivity workActivity) {
		this.workActivity = workActivity;
	}

	/**
	 * Return the ending date while the employee is working in this place and activity.  
	 */
	@Column(name="endingdate")
	public Date getEndingDate() {
		return this.endingDate;
	}

	/**
	 * Set ending date.
	 * 
	 * @param endingDate
	 */
	public void setEndingDate(Date endingDate) {
		this.endingDate = endingDate;
	}

	/**
	 * Return the starting date from the employee is working in this place and activity.  
	 */
	@Column(name="startingdate", nullable=false)
	public Date getStartingDate() {
		return this.startingDate;
	}

	/**
	 * Set starting date.
	 * 
	 * @param startingDate
	 */
	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	/**
	 * Returns the calendar identifier.
	 * 
	 * @return
	 */
	@Transient
	public Integer getCalendar() {
		return this.employee.getCalendar();
	}

	/**
	 * Returns the event identifier.
	 * 
	 * @return
	 */
	@Transient
	public String getOwner() {
		return this.employee.getRegistry().getName() + " " + this.employee.getRegistry().getSurname();
	}

}
