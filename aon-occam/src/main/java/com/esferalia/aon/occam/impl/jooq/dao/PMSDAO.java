package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Hotel.HOTEL;
import static com.esferalia.aon.jooq.tables.ProjectReservation.PROJECT_RESERVATION;
import static com.esferalia.aon.jooq.tables.ProjectReservationGuest.PROJECT_RESERVATION_GUEST;
import static com.esferalia.aon.jooq.tables.SurveyResponse.SURVEY_RESPONSE;
import static com.esferalia.aon.jooq.tables.SurveyResponseDetail.SURVEY_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.mvel2.ast.And;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.pms.HotelEmailCatchment;
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
	
	public static LinkedList<HotelEmailCatchment> getHotelEmailCatchmentList(AONContext ctx, Integer[] hotelArray, Integer year){
		
		 Field<String> captacion = DSL.decode()
			.when(DSL.length(SURVEY_RESPONSE_DETAIL.VALUE_TEXT).greaterThan(0),
				DSL.decode()
					.when(DSL.length(PROJECT_RESERVATION_GUEST.EMAIL).greaterThan(0),
						DSL.decode()
							.when(PROJECT_RESERVATION_GUEST.EMAIL.eq(SURVEY_RESPONSE_DETAIL.VALUE_TEXT), "WIFI")
							.when(PROJECT_RESERVATION_GUEST.EMAIL.ne(SURVEY_RESPONSE_DETAIL.VALUE_TEXT), "RECEPCION"))
					.when(DSL.length(PROJECT_RESERVATION_GUEST.EMAIL).lessOrEqual(0), "WIFI"))
			.when(DSL.length(SURVEY_RESPONSE_DETAIL.VALUE_TEXT).lessOrEqual(0),
				DSL.decode()
					.when(DSL.length(PROJECT_RESERVATION_GUEST.EMAIL).greaterThan(0), "RECEPCION")
					.when(DSL.length(PROJECT_RESERVATION_GUEST.EMAIL).lessOrEqual(0), "SINEMAIL"));
		
		 Field<Integer> correos = DSL.decode()
			.when(PROJECT_RESERVATION_GUEST.EMAIL.isNull().or(DSL.length(PROJECT_RESERVATION_GUEST.EMAIL).eq(0)),
				DSL.decode()
					.when(SURVEY_RESPONSE_DETAIL.VALUE_TEXT.isNull().or(DSL.length(SURVEY_RESPONSE_DETAIL.VALUE_TEXT).eq(0)), 0)
					.when(SURVEY_RESPONSE_DETAIL.VALUE_TEXT.isNotNull().and(DSL.length(SURVEY_RESPONSE_DETAIL.VALUE_TEXT).ne(0)), 1))
			.when(PROJECT_RESERVATION_GUEST.EMAIL.isNotNull().and(DSL.length(PROJECT_RESERVATION_GUEST.EMAIL).ne(0)),
				DSL.decode()
					.when(SURVEY_RESPONSE_DETAIL.VALUE_TEXT.isNull().or(DSL.length(SURVEY_RESPONSE_DETAIL.VALUE_TEXT).eq(0)),1)
					.when(SURVEY_RESPONSE_DETAIL.VALUE_TEXT.isNotNull().and(DSL.length(SURVEY_RESPONSE_DETAIL.VALUE_TEXT).ne(0)),
						DSL.decode()
							.when(PROJECT_RESERVATION_GUEST.EMAIL.eq(SURVEY_RESPONSE_DETAIL.VALUE_TEXT), 1)
							.when(PROJECT_RESERVATION_GUEST.EMAIL.ne(SURVEY_RESPONSE_DETAIL.VALUE_TEXT), 2)));
		 
		 Field<Integer> hotel = PROJECT_RESERVATION.HOTEL.as("hotel");
		
		/* Result<Record6<Integer, String, Integer, Integer, String, BigDecimal>> result = ctx.getDslContext()
		.select(hotel, WORKPLACE.DESCRIPTION, DSL.year(PROJECT_RESERVATION.START_DATE), DSL.month(PROJECT_RESERVATION.START_DATE)
			,captacion, DSL.sum(correos))
		.from(*/
		 
		 Result<Record6<Integer, String, Integer, Integer, String, Integer>> result = ctx.getDslContext()
	    	.select(hotel, WORKPLACE.DESCRIPTION, DSL.year(PROJECT_RESERVATION.START_DATE), DSL.month(PROJECT_RESERVATION.START_DATE)
	    			,captacion, correos)
	    	.from(PROJECT_RESERVATION_GUEST).join(PROJECT_RESERVATION).on(PROJECT_RESERVATION_GUEST.PROJECT_RESERVATION.eq(PROJECT_RESERVATION.PROJECT))
				.join(HOTEL).on(HOTEL.ID.eq(PROJECT_RESERVATION.HOTEL))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(HOTEL.WORKPLACE))
				.leftOuterJoin(SURVEY_RESPONSE).on(SURVEY_RESPONSE.REGISTRY.eq(PROJECT_RESERVATION_GUEST.PERSON)
					.and(SURVEY_RESPONSE.RESPONSE_DATE.between(DSL.timestamp(PROJECT_RESERVATION.START_DATE), DSL.timestamp(PROJECT_RESERVATION.END_DATE).sub(1))))
				.join(SURVEY_RESPONSE_DETAIL).on(SURVEY_RESPONSE_DETAIL.SURVEYRESPONSE.eq(SURVEY_RESPONSE.ID).and(SURVEY_RESPONSE_DETAIL.QUESTION.eq(6)))
			.where(PROJECT_RESERVATION.STATUS.eq((byte)3))
				.and(PROJECT_RESERVATION.CHECK_STATUS.lessThan((byte)3))
				.and(PROJECT_RESERVATION.HOTEL.in(hotelArray))
				.and(DSL.year(PROJECT_RESERVATION.START_DATE).eq(year))
				.and((PROJECT_RESERVATION_GUEST.EMAIL.isNull().or(DSL.length(PROJECT_RESERVATION_GUEST.EMAIL).eq(0)))
					.or(PROJECT_RESERVATION_GUEST.EMAIL.likeRegex("^[a-zA-Z0-9][a-zA-Z0-9._-]*[a-zA-Z0-9._-]@[a-zA-Z0-9][a-zA-Z0-9._-]*[a-zA-Z0-9]\\.[a-zA-Z]{2,4}$")
						.and(PROJECT_RESERVATION_GUEST.EMAIL.notLike("%@guest.booking.com"))
						.and(PROJECT_RESERVATION_GUEST.EMAIL.notLike("%@travelrepublic.co.uk"))
						.and(PROJECT_RESERVATION_GUEST.EMAIL.notLike("%@lowcostbeds.com"))
						.and(PROJECT_RESERVATION_GUEST.EMAIL.notLike("%@alwaystravelling.es"))
						.and(PROJECT_RESERVATION_GUEST.EMAIL.notLike("davidmursl@msn.com"))))
				.and((SURVEY_RESPONSE_DETAIL.VALUE_TEXT.isNull().or(DSL.length(SURVEY_RESPONSE_DETAIL.VALUE_TEXT).eq(0)))
					.or(SURVEY_RESPONSE_DETAIL.VALUE_TEXT.likeRegex("^[a-zA-Z0-9][a-zA-Z0-9._-]*[a-zA-Z0-9._-]@[a-zA-Z0-9][a-zA-Z0-9._-]*[a-zA-Z0-9]\\.[a-zA-Z]{2,4}$")
						.and(SURVEY_RESPONSE_DETAIL.VALUE_TEXT.notLike("%@guest.booking.com"))
						.and(SURVEY_RESPONSE_DETAIL.VALUE_TEXT.notLike("%@travelrepublic.co.uk"))
						.and(SURVEY_RESPONSE_DETAIL.VALUE_TEXT.notLike("%@lowcostbeds.com"))
						.and(SURVEY_RESPONSE_DETAIL.VALUE_TEXT.notLike("%@alwaystravelling.es"))
						.and(SURVEY_RESPONSE_DETAIL.VALUE_TEXT.notLike("davidmursl@msn.com"))))
				.and(PROJECT_RESERVATION.START_DATE.lessThan(DSL.currentDate()))
			.groupBy(WORKPLACE.DESCRIPTION,PROJECT_RESERVATION.PROJECT, DSL.year(PROJECT_RESERVATION.START_DATE),DSL.month(PROJECT_RESERVATION.START_DATE)
				,PROJECT_RESERVATION_GUEST.ID)//.having(correos.greaterThan(0))
		
		/*)
		.groupBy(WORKPLACE.DESCRIPTION, DSL.year(PROJECT_RESERVATION.START_DATE),DSL.month(PROJECT_RESERVATION.START_DATE), captacion)
	*/
		.orderBy(WORKPLACE.DESCRIPTION, DSL.year(PROJECT_RESERVATION.START_DATE),DSL.month(PROJECT_RESERVATION.START_DATE), captacion)
		.fetch();
		
		HashMap<String, HotelEmailCatchment> map = new HashMap<String, HotelEmailCatchment>();
		
		result.stream().forEach(r -> {
			Integer hotelId = r.value1();
			String hotelName = r.value2();
			String month = Month.values()[r.value4()-1].name();
			Integer yy = r.value3();
			String capt = r.value5();
			Integer emails= r.value6().intValue();
			String key = hotelName + r.value4();

			if(map.containsKey(key)){
				map.get(key).setTotal(map.get(key).getTotal()+emails);
				if(capt.equals("WIFI"))	map.get(key).setWifi(map.get(key).getWifi()+emails);
				if(capt.equals("RECEPCION")) map.get(key).setReception(map.get(key).getReception()+emails);
				Double receptionPercentage = (double) (map.get(key).getReception()*100) /map.get(key).getTotal();
				map.get(key).setReceptionPercentage((double) Math.round(receptionPercentage));
				Double totalPercentage = (double) (map.get(key).getTotal()*100) /map.get(key).getGuest();	
				map.get(key).setTotalPercentage((double) Math.round(totalPercentage));
			} else {
				HotelEmailCatchment hec = new HotelEmailCatchment()
				.setHotelName(hotelName)
				.setMonth(month)
				.setTotal(emails);
				if(capt.equals("WIFI")) hec.setWifi(emails); else hec.setWifi(0);
				if(capt.equals("RECEPCION")) hec.setReception(emails); else hec.setReception(0);
				hec.setGuest(getGuest(ctx, yy,r.value4(), hotelId));
				map.put(key, hec);
			}
		});
		LinkedList<HotelEmailCatchment> l = new LinkedList<HotelEmailCatchment>();
		l.addAll(map.values());
		return l;
	}

	private static Integer getGuest(AONContext ctx, Integer year, Integer month, Integer hotelId) {
		Record1<Integer> a = ctx.getDslContext().select(DSL.count(PROJECT_RESERVATION_GUEST.ID))
		.from(PROJECT_RESERVATION_GUEST).join(PROJECT_RESERVATION).on(PROJECT_RESERVATION.PROJECT.eq(PROJECT_RESERVATION_GUEST.PROJECT_RESERVATION))
		.join(HOTEL).on(HOTEL.ID.eq(PROJECT_RESERVATION.HOTEL))
		.join(WORKPLACE).on(WORKPLACE.ID.eq(HOTEL.WORKPLACE))
		.where(DSL.year(PROJECT_RESERVATION.START_DATE).eq(year))
		.and(PROJECT_RESERVATION.HOTEL.in(hotelId))
		.and(PROJECT_RESERVATION.STATUS.eq((byte)3))
		.and(PROJECT_RESERVATION.CHECK_STATUS.lessThan((byte) 3 ))
		.and(DSL.month(PROJECT_RESERVATION.START_DATE).eq(month))
		.fetchOne();
		
		return a.value1();
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
