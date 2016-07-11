package com.esferalia.aon.occam.api.model.pms;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HotelEmailCatchment implements Serializable {

	private String hotelName;
	private String month;
	private Integer reception;
	private Integer wifi;
	private Integer total;
	private Integer guest;
	private Double receptionPercentage;
	private Double totalPercentage;

	public String getHotelName() {
		return hotelName;
	}
	public HotelEmailCatchment setHotelName(String hotelName) {
		this.hotelName = hotelName;
		return this;
	}
	public String getMonth() {
		return month;
	}
	public HotelEmailCatchment setMonth(String month) {
		this.month = month;
		return this;
	}
	public Integer getReception() {
		return reception;
	}
	public HotelEmailCatchment setReception(Integer reception) {
		this.reception = reception;
		return this;
	}
	public Integer getWifi() {
		return wifi;
	}
	public HotelEmailCatchment setWifi(Integer wifi) {
		this.wifi = wifi;
		return this;
	}
	public Integer getTotal() {
		return total;
	}
	public HotelEmailCatchment setTotal(Integer total) {
		this.total = total;
		return this;
	}
	public Integer getGuest() {
		return guest;
	}
	public HotelEmailCatchment setGuest(Integer guest) {
		this.guest = guest;
		return this;
	}
	public Double getReceptionPercentage() {
		return receptionPercentage;
	}
	public HotelEmailCatchment setReceptionPercentage(Double receptionPercentage) {
		this.receptionPercentage = receptionPercentage;
		return this;
	}
	public Double getTotalPercentage() {
		return totalPercentage;
	}
	public HotelEmailCatchment setTotalPercentage(Double totalPercentage) {
		this.totalPercentage = totalPercentage;
		return this;
	}
}