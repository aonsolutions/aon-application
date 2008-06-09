package com.code.aon.ui.cms.velocity.attribute;

import java.util.Date;

import com.code.aon.cms.HiruCourseDetail;
import com.code.aon.cms.enumeration.HiruCourseSubject;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.velocity.ArticleGenerator;
import com.code.aon.ui.cms.velocity.HiruGenerator;

public class HiruCourseHandler {

	private String alias;
	
	private Date initDate;

	private Date endDate;

	private String hiruPlace;
	
	private HiruCourseSubject subject;

	private String name;

	private String info;

	private String url;

	private String local_url;

	private String genericInfo;

	private String objetives;

	private String contents;

	private String length;
	
	private String employeeRegistration;

	private String notEmployeeRegistration;

	private String giverEntity;

	private String numberParticipant;	
	
	public HiruCourseHandler (HiruCourseDetail hcd) {
		this.alias = hcd.getHiruCourse().getAlias();
		this.initDate = hcd.getHiruCourse().getInitDate();
		this.endDate = hcd.getHiruCourse().getEndDate();
		this.hiruPlace = hcd.getHiruCourse().getHiruPlace();
		this.subject = hcd.getHiruCourse().getSubject();
		this.name = hcd.getName();
		this.info = hcd.getInfo();
		this.url = hcd.getUrl();
		this.genericInfo = hcd.getGenericInfo();
		this.objetives = hcd.getObjetives();
		this.contents = hcd.getContents();
		this.length = hcd.getLength();
		this.employeeRegistration = hcd.getEmployeeRegistration();
		this.notEmployeeRegistration = hcd.getNotEmployeeRegistration();
		this.giverEntity = hcd.getGiverEntity();
		this.numberParticipant = hcd.getNumberParticipant();	
		
		this.local_url = Templates.HIRU_COURSE.getHtmlName();
		this.local_url = this.local_url.replaceAll("%NAME%", this.alias);
	}

	public String getAlias() {
		return alias;
	}

	public Date getInitDate() {
		return initDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getHiruPlace() {
		return hiruPlace;
	}

	public HiruCourseSubject getSubject() {
		return subject;
	}

	public String getName() {
		return name;
	}

	public String getInfo() {
		return info;
	}

	public String getUrl() {
		return url;
	}

	public String getGenericInfo() {
		return genericInfo;
	}

	public String getObjetives() {
		return objetives;
	}

	public String getContents() {
		return contents;
	}

	public String getLength() {
		return length;
	}

	public String getEmployeeRegistration() {
		return employeeRegistration;
	}

	public String getNotEmployeeRegistration() {
		return notEmployeeRegistration;
	}

	public String getGiverEntity() {
		return giverEntity;
	}

	public String getNumberParticipant() {
		return numberParticipant;
	}

	public String getLocal_url() {
		return local_url;
	}
	
}
