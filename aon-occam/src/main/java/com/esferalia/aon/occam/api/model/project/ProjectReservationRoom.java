package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ProjectReservationRoom implements Serializable{
	
	Integer adults;
	Integer children;
	Date creationDate;
	String creationUser;
	Integer domain;
	Integer id;
	Integer item;
	Date modificationDate;
	String modificationUser;
	Integer projectReservation;
	String ratePlan;
	String roomCode;
	Integer roomIndex;
	Integer tariff;
	
	Integer hotel;
	
	public Integer getAdults() {
		return adults;
	}
	public ProjectReservationRoom setAdults(Integer adults) {
		this.adults = adults;
		return this;
	}
	public Integer getChildren() {
		return children;
	}
	public ProjectReservationRoom setChildren(Integer children) {
		this.children = children;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public ProjectReservationRoom setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public ProjectReservationRoom setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public ProjectReservationRoom setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public ProjectReservationRoom setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getItem() {
		return item;
	}
	public ProjectReservationRoom setItem(Integer item) {
		this.item = item;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public ProjectReservationRoom setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public ProjectReservationRoom setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public Integer getProjectReservation() {
		return projectReservation;
	}
	public ProjectReservationRoom setProjectReservation(Integer projectReservation) {
		this.projectReservation = projectReservation;
		return this;
	}
	public String getRatePlan() {
		return ratePlan;
	}
	public ProjectReservationRoom setRatePlan(String ratePlan) {
		this.ratePlan = ratePlan;
		return this;
	}
	public String getRoomCode() {
		return roomCode;
	}
	public ProjectReservationRoom setRoomCode(String roomCode) {
		this.roomCode = roomCode;
		return this;
	}
	public Integer getRoomIndex() {
		return roomIndex;
	}
	public ProjectReservationRoom setRoomIndex(Integer roomIndex) {
		this.roomIndex = roomIndex;
		return this;
	}
	public Integer getTariff() {
		return tariff;
	}
	public ProjectReservationRoom setTariff(Integer tariff) {
		this.tariff = tariff;
		return this;
	}
	
	public Integer getHotel() {
		return hotel;
	}
	public ProjectReservationRoom setHotel(Integer hotel) {
		this.hotel = hotel;
		return this;
	}
}
