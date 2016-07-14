package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.pms.HotelEmailCatchment;
import com.esferalia.aon.occam.api.model.pms.HotelGuestByCountry;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.project.ProjectReservationRoom;
import com.esferalia.aon.occam.api.model.project.ProjectReservationService;
import com.esferalia.aon.occam.api.model.project.ProjectReservationServiceDetail;

public interface IPMS {
	public void deleteReservationCreditCard(AONContext ctx, Integer reservationId);

	public LinkedList<HotelGuestByCountry> getHotelGuestByCountry(AONContext ctx, Integer hotelId, Date date);
	
	public LinkedList<HotelEmailCatchment> getHotelEmailCatchmentList(AONContext ctx, Integer[] hotelArray, Integer year);
	
	
	//***** HHG HOTELS *****//
	public ProjectReservation getHHGReservation(AONContext ctx, Integer project);
	public LinkedList<ProjectReservation> getHHGReservations(AONContext ctx, Integer[] array);
	public LinkedList<ProjectReservationRoom> getHHGReservationRooms(AONContext ctx, Integer project);	
	public LinkedList<ProjectReservationService> getHHGReservationServices(AONContext ctx, Integer project);
	public LinkedList<ProjectReservationServiceDetail> getHHGReservationServicesDetail(AONContext ctx, Integer service);
	public HashMap<Integer, Attach> getHHGProjectAttach(AONContext ctx, Date date);
	public void updateHHGProjectAttachDate(AONContext ctx, Integer[] ids, Date date);
}
