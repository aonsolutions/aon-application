package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "hiru_course_i18n")
public class HiruCourseDetail implements ITransferObject {

	private Integer id;

	private HiruCourse hiruCourse;
	
	private Language language;

	private String name;

	private String info;

	private String url;

	private String genericInfo;

	private String objetives;

	private String contents;

	private String length;
	
	private String employeeRegistration;

	private String notEmployeeRegistration;

	private String giverEntity;

	private String numberParticipant;

	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "hiru_course", nullable = false)
	public HiruCourse getHiruCourse() {
		return hiruCourse;
	}

	public void setHiruCourse(HiruCourse hiruCourse) {
		this.hiruCourse = hiruCourse;
	}

	@ManyToOne
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(name = "name", length=64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "info")
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Column(name = "url", length=255)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "generic_info", length=255)
	public String getGenericInfo() {
		return genericInfo;
	}

	public void setGenericInfo(String genericInfo) {
		this.genericInfo = genericInfo;
	}

	@Column(name = "objetives", length=255)
	public String getObjetives() {
		return objetives;
	}

	public void setObjetives(String objetives) {
		this.objetives = objetives;
	}

	@Column(name = "contents", length=255)
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	@Column(name = "length", length=32)
	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	@Column(name = "employee_registration", length=255)
	public String getEmployeeRegistration() {
		return employeeRegistration;
	}

	public void setEmployeeRegistration(String employeeRegistration) {
		this.employeeRegistration = employeeRegistration;
	}

	@Column(name = "not_employee_registration", length=255)
	public String getNotEmployeeRegistration() {
		return notEmployeeRegistration;
	}

	public void setNotEmployeeRegistration(String notEmployeeRegistration) {
		this.notEmployeeRegistration = notEmployeeRegistration;
	}

	@Column(name = "giver_entity", length=128)
	public String getGiverEntity() {
		return giverEntity;
	}

	public void setGiverEntity(String giverEntity) {
		this.giverEntity = giverEntity;
	}

	@Column(name = "number_participant", length=128)
	public String getNumberParticipant() {
		return numberParticipant;
	}

	public void setNumberParticipant(String numberParticipant) {
		this.numberParticipant = numberParticipant;
	}

	
}
