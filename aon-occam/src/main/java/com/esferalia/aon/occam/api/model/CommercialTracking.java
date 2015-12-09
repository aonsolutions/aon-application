package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class CommercialTracking implements Serializable {
	
	Integer activity;
	Boolean allday;
	String comments;
	Date date;
	Integer domain;
	Date endDate;
	String eventId;
	Integer id;
	String location;
	Integer nextCommercialTracking;
	Integer offer;
	Integer projectCommercial;
	Integer seller;
	Byte status;
	
	public Integer getActivity() {
		return activity;
	}
	public CommercialTracking setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public Boolean getAllday() {
		return allday;
	}
	public CommercialTracking setAllday(Boolean allday) {
		this.allday = allday;
		return this;
	}
	public String getComments() {
		return comments;
	}
	public CommercialTracking setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public CommercialTracking setDate(Date date) {
		this.date = date;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public CommercialTracking setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Date getEndDate() {
		return endDate;
	}
	public CommercialTracking setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	public String getEventId() {
		return eventId;
	}
	public CommercialTracking setEventId(String eventId) {
		this.eventId = eventId;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public CommercialTracking setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getLocation() {
		return location;
	}
	public CommercialTracking setLocation(String location) {
		this.location = location;
		return this;
	}
	public Integer getNextCommercialTracking() {
		return nextCommercialTracking;
	}
	public CommercialTracking setNextCommercialTracking(Integer nextCommercialTracking) {
		this.nextCommercialTracking = nextCommercialTracking;
		return this;
	}
	public Integer getOffer() {
		return offer;
	}
	public CommercialTracking setOffer(Integer offer) {
		this.offer = offer;
		return this;
	}
	public Integer getProjectCommercial() {
		return projectCommercial;
	}
	public CommercialTracking setProjectCommercial(Integer projectCommercial) {
		this.projectCommercial = projectCommercial;
		return this;
	}
	public Integer getSeller() {
		return seller;
	}
	public CommercialTracking setSeller(Integer seller) {
		this.seller = seller;
		return this;
	}
	public Byte getStatus() {
		return status;
	}
	public CommercialTracking setStatus(Byte status) {
		this.status = status;
		return this;
	}
	
	
}
