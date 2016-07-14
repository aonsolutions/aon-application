package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProjectReservationRoomDetail implements Serializable{
	
	Integer assetActivity;
	Integer domain;
	Integer id;
	Integer projectReservationRoom;
	
	public Integer getAssetActivity() {
		return assetActivity;
	}
	public ProjectReservationRoomDetail setAssetActivity(Integer assetActivity) {
		this.assetActivity = assetActivity;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public ProjectReservationRoomDetail setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public ProjectReservationRoomDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getProjectReservationRoom() {
		return projectReservationRoom;
	}
	public ProjectReservationRoomDetail setProjectReservationRoom(Integer projectReservationRoom) {
		this.projectReservationRoom = projectReservationRoom;
		return this;
	}
}
