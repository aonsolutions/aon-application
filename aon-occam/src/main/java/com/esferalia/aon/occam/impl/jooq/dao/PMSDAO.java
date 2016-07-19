package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Hotel.HOTEL;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;
import static com.esferalia.aon.jooq.tables.ProjectReservation.PROJECT_RESERVATION;
import static com.esferalia.aon.jooq.tables.ProjectReservationGuest.PROJECT_RESERVATION_GUEST;
import static com.esferalia.aon.jooq.tables.ProjectReservationRoom.PROJECT_RESERVATION_ROOM;
import static com.esferalia.aon.jooq.tables.ProjectReservationService.PROJECT_RESERVATION_SERVICE;
import static com.esferalia.aon.jooq.tables.ProjectReservationServiceDetail.PROJECT_RESERVATION_SERVICE_DETAIL;
import static com.esferalia.aon.jooq.tables.SurveyResponse.SURVEY_RESPONSE;
import static com.esferalia.aon.jooq.tables.SurveyResponseDetail.SURVEY_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.time.Month;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;
import com.esferalia.aon.jooq.tables.records.ProjectReservationServiceDetailRecord;
import com.esferalia.aon.jooq.tables.records.ProjectReservationServiceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.pms.HotelEmailCatchment;
import com.esferalia.aon.occam.api.model.pms.HotelGuestByCountry;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.project.ProjectReservationRoom;
import com.esferalia.aon.occam.api.model.project.ProjectReservationService;
import com.esferalia.aon.occam.api.model.project.ProjectReservationServiceDetail;
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
			String month = getMonthName(Month.values()[r.value4()-1]);
			Integer yy = r.value3();
			String capt = r.value5();
			Integer emails= r.value6().intValue();
			String key = hotelName + r.value4();
			if(capt != null){
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
						.setTotal(emails)
						.setMonthNumber(r.value4());
					if(capt.equals("WIFI")) hec.setWifi(emails); else hec.setWifi(0);
					if(capt.equals("RECEPCION")) hec.setReception(emails); else hec.setReception(0);
					hec.setGuest(getGuest(ctx, yy,r.value4(), hotelId));
					map.put(key, hec);
				}
			}
		});
		LinkedList<HotelEmailCatchment> l = new LinkedList<HotelEmailCatchment>();
		l.addAll(map.values());
		Collections.sort(l, (l1,l2) -> l1.getMonthNumber().compareTo(l2.getMonthNumber()));
		return l;
	}
	
	private static String getMonthName(Month month) {
		if(month.equals(Month.JANUARY)) return "Enero";
		if(month.equals(Month.FEBRUARY)) return "Febrero";
		if(month.equals(Month.MARCH)) return "Marzo";		
		if(month.equals(Month.APRIL)) return "Abril";
		if(month.equals(Month.MAY)) return "Mayo";
		if(month.equals(Month.JUNE)) return "Junio";
		if(month.equals(Month.JULY)) return "Julio";
		if(month.equals(Month.AUGUST)) return "Agosto";
		if(month.equals(Month.SEPTEMBER)) return "Septiembre";
		if(month.equals(Month.OCTOBER)) return "Octubre";
		if(month.equals(Month.NOVEMBER)) return "Noviembre";
		if(month.equals(Month.DECEMBER)) return "Diciembre";
		return "";
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
	
	public static ProjectReservation getHHGReservation(AONContext ctx, Integer project){
		return ctx.getDslContext().select()
		.from(PROJECT_RESERVATION)
		.where(PROJECT_RESERVATION.PROJECT.eq(project)).limit(1)
		.fetchInto(PROJECT_RESERVATION).stream().map(new HHGProjectReservationFiller())
			.findFirst().orElse(new ProjectReservation());
	}
	
	public static LinkedList<ProjectReservation> getHHGReservations(AONContext ctx, Integer[] array){
		return ctx.getDslContext().select()
		.from(PROJECT_RESERVATION)
		.where(PROJECT_RESERVATION.PROJECT.in(array))
		.fetchInto(PROJECT_RESERVATION).stream().map(new HHGProjectReservationFiller())
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<ProjectReservationRoom> getHHGReservationRooms(AONContext ctx, Integer project){
		return ctx.getDslContext().select(PROJECT_RESERVATION_ROOM.ROOM_INDEX, PROJECT_RESERVATION_ROOM.ROOM_CODE, 
				PROJECT_RESERVATION_ROOM.ADULTS, PROJECT_RESERVATION_ROOM.CHILDREN, PROJECT_RESERVATION_ROOM.RATE_PLAN,
				PROJECT_RESERVATION.HOTEL)
		.from(PROJECT_RESERVATION).join(PROJECT_RESERVATION_ROOM).on(PROJECT_RESERVATION.PROJECT.eq(PROJECT_RESERVATION_ROOM.PROJECT_RESERVATION))
		.where(PROJECT_RESERVATION.PROJECT.eq(project)).fetch().stream().map(new HHGProjectReservationRoomFiller())
		.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<ProjectReservationService> getHHGReservationServices(AONContext ctx, Integer project){
		return ctx.getDslContext().select(PROJECT_RESERVATION_SERVICE.SERVICE_CODE, PROJECT_RESERVATION_SERVICE.MEAL_PLAN, PROJECT_RESERVATION_SERVICE.ID)
		.from(PROJECT_RESERVATION_SERVICE)
		.where(PROJECT_RESERVATION_SERVICE.PROJECT_RESERVATION.eq(project)).fetchInto(PROJECT_RESERVATION_SERVICE)
		.stream().map(new HHGProjectReservationServiceFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<ProjectReservationServiceDetail> getHHGReservationServicesDetail(AONContext ctx, Integer service){
		return ctx.getDslContext().select(PROJECT_RESERVATION_SERVICE_DETAIL.EFFECTIVE_DATE, PROJECT_RESERVATION_SERVICE_DETAIL.QUANTITY,
				PROJECT_RESERVATION_SERVICE_DETAIL.PRICE, PROJECT_RESERVATION_SERVICE_DETAIL.TAXABLE_BASE)
		.from(PROJECT_RESERVATION_SERVICE_DETAIL)
		.where(PROJECT_RESERVATION_SERVICE_DETAIL.PROJECT_RESERVATION_SERVICE.eq(service)).fetchInto(PROJECT_RESERVATION_SERVICE_DETAIL)
		.stream().map(new HHGProjectReservationServiceDetailFiller()).collect(Collectors.toCollection(LinkedList::new));
	}

	
	public static HashMap<Integer, Attach> getHHGProjectAttach(AONContext ctx){
		Result<Record3<Integer, Integer, String>> result = ctx.getDslContext().select(PROJECT_ATTACH.ID,PROJECT_ATTACH.PROJECT, PROJECT_ATTACH.DESCRIPTION)
		.from(PROJECT_ATTACH)
		.where(PROJECT_ATTACH.DESCRIPTION.like("%CRS%#"))
		.limit(20).fetch();
		
		HashMap<Integer, Attach> map = new HashMap<Integer, Attach>();
		
		result.stream().forEach(r ->{
			Integer id = r.getValue(PROJECT_ATTACH.ID);
			Integer project = r.getValue(PROJECT_ATTACH.PROJECT);
			String description = r.getValue(PROJECT_ATTACH.DESCRIPTION);
			if(!map.containsKey(project)){
				if(description.contains("ALTA"))
					map.put(project, new Attach().setId(id).setDescription("ADD"));
				if(description.contains("MODIFICACION"))
					map.put(project, new Attach().setId(id).setDescription("MODIFY"));
				if(description.contains("CANCELACION"))
					map.put(project, new Attach().setId(id).setDescription("CANCEL"));
			}
		});
		return map;
	}
	
	public static void updateHHGProjectAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().update(PROJECT_ATTACH)
			.set(PROJECT_ATTACH.DESCRIPTION, attach.getDescription())
			.where(PROJECT_ATTACH.ID.eq(attach.getId()))
			.execute();
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
	
	private static class HHGProjectReservationFiller implements Function<ProjectReservationRecord, ProjectReservation> {
		
		@Override
		public ProjectReservation apply(ProjectReservationRecord r) {
			return new ProjectReservation()
					.setProject(r.getProject())
					.setAgency(r.getAgency())
					.setCrsCode(r.getCrsCode())
					.setStartDate(r.getStartDate())
					.setEndDate(r.getEndDate());
		}
	}
	
	private static class HHGProjectReservationRoomFiller implements Function<Record6<Byte, String, Short, Short, String, Integer>, ProjectReservationRoom> {
		
		@Override
		public ProjectReservationRoom apply(Record6<Byte, String, Short, Short, String, Integer> r) {
			return new ProjectReservationRoom()
					.setAdults(r.getValue(PROJECT_RESERVATION_ROOM.ADULTS).intValue())
					.setChildren(r.getValue(PROJECT_RESERVATION_ROOM.CHILDREN).intValue())
					.setRatePlan(r.getValue(PROJECT_RESERVATION_ROOM.RATE_PLAN))
					.setRoomCode(r.getValue(PROJECT_RESERVATION_ROOM.ROOM_CODE))
					.setRoomIndex(r.getValue(PROJECT_RESERVATION_ROOM.ROOM_INDEX).intValue())
					.setHotel(r.getValue(PROJECT_RESERVATION.HOTEL));
		}
	}
	
	private static class HHGProjectReservationServiceFiller implements Function<ProjectReservationServiceRecord, ProjectReservationService> {
		
		@Override
		public ProjectReservationService apply(ProjectReservationServiceRecord r) {
			return new ProjectReservationService()
					.setServiceCode(r.getServiceCode())
					.setMealPlan(r.getMealPlan())
					.setId(r.getId());
		}
	}
	
	private static class HHGProjectReservationServiceDetailFiller implements Function<ProjectReservationServiceDetailRecord, ProjectReservationServiceDetail> {
		
		@Override
		public ProjectReservationServiceDetail apply(ProjectReservationServiceDetailRecord r) {
			return new ProjectReservationServiceDetail()
					.setEffectiveDate(r.getEffectiveDate())
					.setQuantity(r.getQuantity())
					.setPrice(r.getPrice())
					.setTaxableBase(r.getTaxableBase());
		}
	}
}
