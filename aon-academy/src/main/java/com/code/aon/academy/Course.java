package com.code.aon.academy;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.company.WorkPlace;

/**
 * The Class Course.
 */
@Entity
@Table(name = "course")
public class Course implements ITransferObject {

	/** The id. */
	private Integer id;

	/** The code. */
	private String code;

	/** The description. */
	private String description;

	/** The start date. */
	private Date startDate;

	/** The end date. */
	private Date endDate;

	/** The academic year. */
	private AcademicYear academicYear;

	/** The subject. */
	private CourseSubject courseSubject;

	/** The level. */
	private CourseLevel courseLevel;

	/** The workplace. */
	private WorkPlace workPlace;

	/** The limit. */
	private int alumnLimit;

	/** The status. */
	private CourseStatus status;

	/** The comments. */
	private String comments;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the code.
	 * 
	 * @return the code
	 */
	@Column(length = 5)
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 * 
	 * @param code
	 *            the code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(length = 64)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the start date.
	 * 
	 * @return the start date
	 */
	@Column(name = "start_date", nullable = false)
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date.
	 * 
	 * @param startDate
	 *            the start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return the end date
	 */
	@Column(name = "end_date", nullable = false)
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate
	 *            the end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the academic year.
	 * 
	 * @return the academic year
	 */
	@ManyToOne
	@JoinColumn(name = "academic_year", nullable = false)
	public AcademicYear getAcademicYear() {
		return academicYear;
	}

	/**
	 * Sets the academic year.
	 * 
	 * @param academicYear
	 *            the academic year
	 */
	public void setAcademicYear(AcademicYear academicYear) {
		this.academicYear = academicYear;
	}

	/**
	 * Gets the subject.
	 * 
	 * @return the subject
	 */
	@ManyToOne
	@JoinColumn(name = "subject", nullable = false)
	public CourseSubject getCourseSubject() {
		return courseSubject;
	}

	/**
	 * Sets the subject.
	 * 
	 * @param courseSubject
	 *            the subject
	 */
	public void setCourseSubject(CourseSubject courseSubject) {
		this.courseSubject = courseSubject;
	}

	/**
	 * Gets the level.
	 * 
	 * @return the level
	 */
	@ManyToOne
	@JoinColumn(name = "level", nullable = false)
	public CourseLevel getCourseLevel() {
		return courseLevel;
	}

	/**
	 * Sets the level.
	 * 
	 * @param courseLevel
	 *            the level
	 */
	public void setCourseLevel(CourseLevel courseLevel) {
		this.courseLevel = courseLevel;
	}

	/**
	 * Gets the workplace.
	 * 
	 * @return the workplace
	 */
	@ManyToOne
	@JoinColumn(name = "workplace", nullable = false)
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	/**
	 * Sets the workplace.
	 * 
	 * @param workPlace
	 *            the workplace
	 */
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	/**
	 * Gets the limit.
	 * 
	 * @return the limit
	 */
	@Column(name = "alumn_limit")
	public int getAlumnLimit() {
		return alumnLimit;
	}

	/**
	 * Sets the limit.
	 * 
	 * @param alumnLimit
	 *            the limit
	 */
	public void setAlumnLimit(int alumnLimit) {
		this.alumnLimit = alumnLimit;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public CourseStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the status
	 */
	public void setStatus(CourseStatus status) {
		this.status = status;
	}

	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	@Column(length = 128)
	public String getComments() {
		return comments;
	}

	/**
	 * Sets the comments.
	 * 
	 * @param comments
	 *            the comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public boolean equals(Object obj) {
		if (id == null) {
			return super.equals(obj);
		}
		if (obj instanceof Course) {
			return (this.id.equals(((Course) obj).getId()));
		}
		return false;
	}
}