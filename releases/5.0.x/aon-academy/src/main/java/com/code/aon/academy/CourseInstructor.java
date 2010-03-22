package com.code.aon.academy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.academy.enumeration.InstructorType;
import com.code.aon.common.ITransferObject;
import com.code.aon.company.resources.Employee;

/**
 * The Class CourseInstructor.
 */
@Entity
@Table(name="course_instructor")
public class CourseInstructor implements ITransferObject {
	
	/** The id. */
	private Integer id;
	
	/** The course. */
	private Course course;
	
	/** The employee. */
	private Employee employee;
	
	/** The type. */
	private InstructorType type;

	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the course.
	 * 
	 * @return the course
	 */
	@ManyToOne
	@JoinColumn( name="course",nullable=false )
	public Course getCourse() {
		return course;
	}

	/**
	 * Sets the course.
	 * 
	 * @param course the course
	 */
	public void setCourse(Course course) {
		this.course = course;
	}

	/**
	 * Gets the employee.
	 * 
	 * @return the employee
	 */
	@ManyToOne
	@JoinColumn( name="employee",nullable=false )
	public Employee getEmployee() {
		return employee;
	}

	/**
	 * Sets the employee.
	 * 
	 * @param employee the employee
	 */
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Column(name="type")
	public InstructorType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setType(InstructorType type) {
		this.type = type;
	}

}
