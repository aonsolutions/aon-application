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
import com.esferalia.aon.occam.impl.jooq.PMSImpl;

public class PMS {

	private static IPMS getPMS() {
		return new PMSImpl();
	}

	public static LinkedList<HotelGuestByCountry> getHotelGuestByCountry(String domainName, Integer domainId, String login,
			Integer hotelId, Date date) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPMS().getHotelGuestByCountry(ctx, hotelId, date);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<HotelEmailCatchment> getHotelEmailCatchmentList(String domainName, Integer domainId, String login,
			Integer[] hotelArray, Integer year){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPMS().getHotelEmailCatchmentList(ctx, hotelArray, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static ProjectReservation getHHGReservation(String domainName, Integer domainId, String login, Integer project){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPMS().getHHGReservation(ctx, project);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<ProjectReservation> getHHGReservations(String domainName, Integer domainId, String login, Integer[] array){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPMS().getHHGReservations(ctx, array);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<ProjectReservationRoom> getHHGReservationRooms(String domainName, Integer domainId, String login,
			Integer project){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPMS().getHHGReservationRooms(ctx, project);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<ProjectReservationService> getHHGReservationServices(String domainName, Integer domainId, String login,
			Integer project){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPMS().getHHGReservationServices(ctx, project);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static LinkedList<ProjectReservationServiceDetail> getHHGReservationServicesDetail(String domainName, Integer domainId, String login,
			Integer service){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPMS().getHHGReservationServicesDetail(ctx, service);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static HashMap<Integer, Attach> getHHGProjectAttach(String domainName, Integer domainId, String login, Date date){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPMS().getHHGProjectAttach(ctx, date);
		} finally {
			if (ctx != null)
				ctx.close();
		}		
	}
	
	public static void updateHHGProjectAttachDate(String domainName, Integer domainId, String login, Integer[] ids, Date date){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getPMS().updateHHGProjectAttachDate(ctx, ids, date);
		} finally {
			if(ctx != null)
				ctx.close();
		}
	}
	
}
