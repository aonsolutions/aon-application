package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Hotel.HOTEL;
import static com.esferalia.aon.jooq.tables.ProjectReservation.PROJECT_RESERVATION;
import static com.esferalia.aon.jooq.tables.ProjectReservationGuest.PROJECT_RESERVATION_GUEST;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.pms.HotelGuestByCountry;
import com.esferalia.aon.occam.api.model.type.Country;

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
	
	
	public static LinkedList<HotelGuestByCountry> getHotelGuestByCountry(AONContext ctx, Integer hotelId, Date date) {
		Result<Record3<String, String, Integer>> r = ctx.getDslContext()
			.select(WORKPLACE.DESCRIPTION, PROJECT_RESERVATION_GUEST.DOCUMENT_COUNTRY, DSL.count(PROJECT_RESERVATION_GUEST.ID))
			.from(PROJECT_RESERVATION_GUEST).join(PROJECT_RESERVATION).on(PROJECT_RESERVATION.PROJECT.eq(PROJECT_RESERVATION_GUEST.PROJECT_RESERVATION))
				.join(HOTEL).on(HOTEL.ID.eq(PROJECT_RESERVATION.HOTEL))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(HOTEL.WORKPLACE))
			.where(PROJECT_RESERVATION.STATUS.eq((byte) 3))
				.and(PROJECT_RESERVATION.CHECK_STATUS.lessThan((byte) 3)
				.and(PROJECT_RESERVATION.HOTEL.eq(hotelId))
				.and(PROJECT_RESERVATION.START_DATE.lessOrEqual(new java.sql.Date(date.getTime())))
				.and(PROJECT_RESERVATION.END_DATE.greaterThan(new java.sql.Date(date.getTime()))))
			.groupBy(WORKPLACE.DESCRIPTION, PROJECT_RESERVATION_GUEST.DOCUMENT_COUNTRY)	
			.orderBy(WORKPLACE.DESCRIPTION.asc(), DSL.count(PROJECT_RESERVATION_GUEST.ID).desc())
			.fetch();
		return r.stream().map(new HotelGuestByCountryFiller())
			.collect(Collectors.toCollection(LinkedList::new));
	}


	private static class HotelGuestByCountryFiller implements Function<Record3<String, String, Integer>, HotelGuestByCountry> {
	
		@Override
		public HotelGuestByCountry apply(Record3<String, String, Integer> r) {
			return new HotelGuestByCountry()
					.setHotelName(r.value1())
					.setCountry(Country.safeValueOf(r.value2()))
					.setGuestQuantity(r.value3());
		}
	}
}
