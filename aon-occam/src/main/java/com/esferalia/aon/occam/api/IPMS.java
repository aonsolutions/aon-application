package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.pms.HotelEmailCatchment;
import com.esferalia.aon.occam.api.model.pms.HotelGuestByCountry;

public interface IPMS {
	public void deleteReservationCreditCard(AONContext ctx, Integer reservationId);

	public LinkedList<HotelGuestByCountry> getHotelGuestByCountry(AONContext ctx, Integer hotelId, Date date);
	
	public LinkedList<HotelEmailCatchment> getHotelEmailCatchmentList(AONContext ctx, Integer[] hotelArray, Integer year);
}
