package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ProjectReservation.PROJECT_RESERVATION;

import com.esferalia.aon.occam.api.AONContext;

public class PMSDAO {
	
	public static void deleteReservationCreditCard(AONContext ctx, Integer reservationId){
		String nullString = null;
		ctx.getDslContext().update(PROJECT_RESERVATION)
		.set(PROJECT_RESERVATION.CREDIT_CARD_HOLDER, nullString)
		.set(PROJECT_RESERVATION.CREDIT_CARD_NUMBER, nullString)
		.set(PROJECT_RESERVATION.CREDIT_CARD_EXPIRATION_MONTH, nullString)
		.set(PROJECT_RESERVATION.CREDIT_CARD_EXPIRATION_YEAR, nullString)
		.set(PROJECT_RESERVATION.CREDIT_CARD_CVV, nullString)
		.where(PROJECT_RESERVATION.PROJECT.eq(reservationId)).execute();
	}
}
