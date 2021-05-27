package com.esferalia.aon.occam.api.model.project;

import java.util.Date;

public class ProjectReservationService {

	Date creationDate;
	String creationUser;
	String description;
	Integer domain;
	Integer extra;
	Integer id;
	Integer item;
	String mealPlan;
	Date modificationDate;
	String modificationUser;
	Integer projectReservation;
	Integer projectReservationRoom;
	String serviceCode;
	Integer serviceIndex;
	
	public Date getCreationDate() {
		return creationDate;
	}
	public ProjectReservationService setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public ProjectReservationService setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public ProjectReservationService setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public ProjectReservationService setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getExtra() {
		return extra;
	}
	public ProjectReservationService setExtra(Integer extra) {
		this.extra = extra;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public ProjectReservationService setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getItem() {
		return item;
	}
	public ProjectReservationService setItem(Integer item) {
		this.item = item;
		return this;
	}
	public String getMealPlan() {
		return mealPlan;
	}
	public ProjectReservationService setMealPlan(String mealPlan) {
		this.mealPlan = mealPlan;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public ProjectReservationService setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public ProjectReservationService setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
	
	return this;}
	public Integer getProjectReservation() {
		return projectReservation;
	}
	public ProjectReservationService setProjectReservation(Integer projectReservation) {
		this.projectReservation = projectReservation;
		return this;
	}
	public Integer getProjectReservationRoom() {
		return projectReservationRoom;
	}
	public ProjectReservationService setProjectReservationRoom(Integer projectReservationRoom) {
		this.projectReservationRoom = projectReservationRoom;
	return this;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public ProjectReservationService setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	return this;
	}
	public Integer getServiceIndex() {
		return serviceIndex;
	}
	public ProjectReservationService setServiceIndex(Integer serviceIndex) {
		this.serviceIndex = serviceIndex;
		return this;
	}
	
	
}
