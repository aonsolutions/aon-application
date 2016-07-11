package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IPMS;
import com.esferalia.aon.occam.api.model.pms.HotelEmailCatchment;
import com.esferalia.aon.occam.api.model.pms.HotelGuestByCountry;
import com.esferalia.aon.occam.impl.jooq.dao.PMSDAO;

public class PMSImpl implements IPMS {
	
	@Override
	public void deleteReservationCreditCard(AONContext ctx, Integer reservationId) {
		ctx.getDslContext().transaction(configuration -> 
			PMSDAO.deleteReservationCreditCard(ctx, reservationId));
	}

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
}
