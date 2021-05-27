package com.esferalia.aon.occam.api.model.pms;

import com.esferalia.aon.occam.api.model.type.Country;

public class HotelGuestByCountry {
	String hotelName;
	Country country;
	Integer guestQuantity;
	
	public String getHotelName() {
		return hotelName;
	}
	public HotelGuestByCountry setHotelName(String hotelName) {
		this.hotelName = hotelName;
		return this;
	}
	public Country getCountry() {
		return country;
	}
	public HotelGuestByCountry setCountry(Country country) {
		this.country = country;
		return this;
	}
	public Integer getGuestQuantity() {
		return guestQuantity;
	}
	public HotelGuestByCountry setGuestQuantity(Integer guestQuantity) {
		this.guestQuantity = guestQuantity;
		return this;
	}
	
	
}
