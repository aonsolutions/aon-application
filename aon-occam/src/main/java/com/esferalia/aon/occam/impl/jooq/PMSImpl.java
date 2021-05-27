package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IPMS;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.pms.HotelEmailCatchment;
import com.esferalia.aon.occam.api.model.pms.HotelGuestByCountry;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.project.ProjectReservationRoom;
import com.esferalia.aon.occam.api.model.project.ProjectReservationService;
import com.esferalia.aon.occam.api.model.project.ProjectReservationServiceDetail;
import com.esferalia.aon.occam.impl.jooq.dao.PMSDAO;

public class PMSImpl implements IPMS {
	
	@Override
	public LinkedList<HotelGuestByCountry> getHotelGuestByCountry(AONContext ctx, Integer hotelId, Date date) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PMSDAO.getHotelGuestByCountry(ctx, hotelId, date));
	}

	@Override
	public LinkedList<HotelEmailCatchment> getHotelEmailCatchmentList(AONContext ctx, Integer[] hotelArray,
			Integer year) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PMSDAO.getHotelEmailCatchmentList(ctx, hotelArray, year));
	}

	@Override
	public LinkedList<ProjectReservation> getHHGReservations(AONContext ctx, Integer[] array) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PMSDAO.getHHGReservations(ctx, array));
	}
	
	@Override
	public ProjectReservation getHHGReservation(AONContext ctx, Integer project) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PMSDAO.getHHGReservation(ctx, project));
	}

	@Override
	public LinkedList<ProjectReservationRoom> getHHGReservationRooms(AONContext ctx, Integer project) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PMSDAO.getHHGReservationRooms(ctx, project));
	}

	@Override
	public LinkedList<ProjectReservationService> getHHGReservationServices(AONContext ctx, Integer project) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PMSDAO.getHHGReservationServices(ctx, project));
	}

	@Override
	public LinkedList<ProjectReservationServiceDetail> getHHGReservationServicesDetail(AONContext ctx, Integer service) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PMSDAO.getHHGReservationServicesDetail(ctx, service));
	}

	@Override
	public HashMap<Integer, Attach> getHHGProjectAttach(AONContext ctx) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PMSDAO.getHHGProjectAttach(ctx));
	}

	@Override
	public void updateHHGProjectAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> PMSDAO.updateHHGProjectAttach(ctx, attach));
	}
	
	@Override
	public Stream<Integer> getFailPreauthorizationProjectIdStream(AONContext ctx) {
		return ctx.getDslContext().transactionResult(configuration -> 
			PMSDAO.getFailPreauthorizationProjectIdStream(ctx));
	}

	
}
