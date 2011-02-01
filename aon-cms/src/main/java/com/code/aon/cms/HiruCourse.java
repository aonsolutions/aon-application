package com.code.aon.cms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.cms.enumeration.HiruCourseSubject;
import com.code.aon.cms.util.IActivableObject;

@Entity
@Table(name="hiru_course")
public class HiruCourse implements IActivableObject {

	private Integer id;
	
	private String alias;
	
	private boolean active = true;
	
	private Date initDate;

	private Date endDate;

	private HiruOrganizerCentre hiruOrganizerCentre; 
	
	private String hiruPlace;
	
	private HiruCourseSubject subject;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false,length=32)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name = "init_date")
	public Date getInitDate() {
		return initDate;
	}

	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	@Column(name = "end_date")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "hiru_organizer_centre", nullable = false)
	public HiruOrganizerCentre getHiruOrganizerCentre() {
		return hiruOrganizerCentre;
	}

	public void setHiruOrganizerCentre(HiruOrganizerCentre hiruOrganizerCentre) {
		this.hiruOrganizerCentre = hiruOrganizerCentre;
	}

	@Column(name = "hiru_place",nullable=false,length=128)
	public String getHiruPlace() {
		return hiruPlace;
	}

	public void setHiruPlace(String hiruPlace) {
		this.hiruPlace = hiruPlace;
	}

	@Column(name = "subject",nullable=false)
	public HiruCourseSubject getSubject() {
		return subject;
	}

	public void setSubject(HiruCourseSubject subject) {
		this.subject = subject;
	}

	
}