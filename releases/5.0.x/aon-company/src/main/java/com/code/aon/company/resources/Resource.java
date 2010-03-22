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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 07/03/2007
 *
 */
@Entity
@Table(name="resource")
@org.hibernate.annotations.Table( appliesTo = "resource", indexes = { @Index(name="IDX_RESOURCE", columnNames={"employee","endingdate"})})
public class Resource implements ITransferObject {

	private static final long serialVersionUID = -4406916296529969516L;

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
	 * 
	 * @return employee
	 */
	@OneToOne
	@JoinColumn(name="employee")
	@ForeignKey(name = "FK_RESOURCE_EMPLOYEE")
	@Index(name = "IDX_RESOURCE_EMPLOYEE")	
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
	 * 
	 * @return workPlace
	 */
	@OneToOne
	@JoinColumn(name="workplace")
	@ForeignKey(name = "FK_RESOURCE_WORKPLACE")
	@Index(name = "IDX_RESOURCE_WORKPLACE")	
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
	 * 
	 * @return workActivity
	 */
	@OneToOne
	@JoinColumn(name="workactivity")
	@ForeignKey(name = "FK_RESOURCE_WORKACTIVITY")
	@Index(name = "IDX_RESOURCE_WORKACTIVITY")		
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
	 * 
	 * @return endingDate
	 */
	@Column(name="endingdate")
	@Temporal(TemporalType.DATE)
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
	 * 
	 * @return startingDate
	 */
	@Column(name="startingdate", nullable=false)
	@Temporal(TemporalType.DATE)
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
	 * @return calendar
	 */
	@Transient
	public Integer getCalendar() {
		return this.employee.getCalendar();
	}

	/**
	 * Returns the event identifier.
	 * 
	 * @return owner
	 */
	@Transient
	public String getOwner() {
		return this.employee.getRegistry().getName() + " " + this.employee.getRegistry().getSurname();
	}

}
