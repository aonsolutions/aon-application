package com.code.aon.record;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.resources.Employee;

@Entity
@Table(name="lh_position")
public class Position implements ITransferObject {

	private static final long serialVersionUID = 8326077287347834756L;

	/** Identifier */
	private Integer id;
	
	/** Employee */
	private Employee employee;
	
	/** Staring date */
	private Date startingDate;

	/** Ending date */
	private Date endingDate;
	
	/** Description */
	private String description;

	/** Working place */
	private WorkPlace workPlace;

	/** Working activity */
	private WorkActivity workActivity;

    /** Indicates the working activity calendar identifier. */
    private Integer calendar;

    @Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
    @JoinColumn(name="employee", nullable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Column(name="startingdate")
	public Date getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	@Column(name="endingdate")
	public Date getEndingDate() {
		return endingDate;
	}

	public void setEndingDate(Date endingDate) {
		this.endingDate = endingDate;
	}

	@Column(length=64)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToOne
    @JoinColumn(name="workplace")
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	@OneToOne
    @JoinColumn(name="workactivity")
	public WorkActivity getWorkActivity() {
		return workActivity;
	}

	public void setWorkActivity(WorkActivity workActivity) {
		this.workActivity = workActivity;
	}

	/**
	 * Return calendar identifier.
	 * 
	 * @return calendar
	 */
	public Integer getCalendar() {
		return calendar;
	}

	/**
	 * Set calendar identifier.
	 * 
	 * @param calendar
	 */
	public void setCalendar(Integer calendar) {
		this.calendar = calendar;
	}

}
