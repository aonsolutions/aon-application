package com.esferalia.aon.pms;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.pms.enumeration.BookingStayType;

public class Booking implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer id;
	private int domain;
	private int projectReservationRoom;
	private int hotel;
	private Integer agency;
	private int item;
	private int tariff;
	private Date stayDate;
	private BookingStayType stayType;
	private int guests;

	public Integer getId() { 
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public int getProjectReservationRoom() {
		return projectReservationRoom;
	}

	public void setProjectReservationRoom(int projectReservationRoom) {
		this.projectReservationRoom = projectReservationRoom;
	}

	public int getHotel() {
		return hotel;
	}

	public void setHotel(int hotel) {
		this.hotel = hotel;
	}

	public Integer getAgency() {
		return agency;
	}

	public void setAgency(Integer agency) {
		this.agency = agency;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

	public int getTariff() {
		return tariff;
	}

	public void setTariff(int tariff) {
		this.tariff = tariff;
	}

	public Date getStayDate() {
		return stayDate;
	}

	public void setStayDate(Date stayDate) {
		this.stayDate = stayDate;
	}

	public BookingStayType getStayType() {
		return stayType;
	}

	public void setStayType(BookingStayType stayType) {
		this.stayType = stayType;
	}

	public int getGuests() {
		return guests;
	}

	public void setGuests(int guests) {
		this.guests = guests;
	}

}
