package com.code.aon.hhg.webservice.jooq;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.code.aon.hhg.webservice.dialog.BookingHarvest;
import com.code.aon.hhg.webservice.dialog.BookingHarvest.Payload;
import com.code.aon.hhg.webservice.dialog.BookingHarvest.Reservation;
import com.code.aon.hhg.webservice.dialog.BookingHarvest.Room;
import com.code.aon.hhg.webservice.dialog.BookingHarvest.Service;
import com.code.aon.hhg.webservice.dialog.BookingHarvest.ServiceDetail;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PMS;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.project.ProjectReservationRoom;
import com.esferalia.aon.occam.api.model.project.ProjectReservationService;
import com.esferalia.aon.occam.api.model.project.ProjectReservationServiceDetail;
import com.esferalia.aon.occam.api.model.type.AppParam;

public class DBConsults {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public static BookingHarvest getHHG(String domainName, Integer domainId, String login) throws ParseException {
		HashMap<Integer, Attach> map = PMS.getHHGProjectAttach(domainName, domainId, login);
		Integer[] array = map.keySet().toArray(new Integer[map.size()]);
		BookingHarvest bh = new BookingHarvest();
		bh.setPayload(getHHGReservations(domainName, domainId, login, array).stream().map(new ReservationFiller(bh, map, domainName, domainId, login))
				.findFirst().orElse(bh.new Payload()));
		return bh;
	}
	
	public static Integer getHHGCount(String domainName, Integer domainId, String login){
		HashMap<Integer, Attach> map = PMS.getHHGProjectAttach(domainName, domainId, login);
		Integer[] array = map.keySet().toArray(new Integer[map.size()]);	
		return getHHGReservations(domainName, domainId, login, array).size();		
	}
	
	public static ProjectReservation getHHGReservation(String domainName, Integer domainId, String login, Integer project){
		return PMS.getHHGReservation(domainName, domainId, login, project);
	}
	
	public static LinkedList<ProjectReservation> getHHGReservations(String domainName, Integer domainId, String login){
		HashMap<Integer, Attach> map = PMS.getHHGProjectAttach(domainName, domainId, login);
		Integer[] array = map.keySet().toArray(new Integer[map.size()]);	
		return PMS.getHHGReservations(domainName, domainId, login, array);
	}
	
	public static LinkedList<ProjectReservation> getHHGReservations(String domainName, Integer domainId, String login, Integer[] array){
		return PMS.getHHGReservations(domainName, domainId, login, array);
	}
	
	public static LinkedList<ProjectReservationRoom> getHHGReservationRooms(String domainName, Integer domainId, String login, Integer project){
		return PMS.getHHGReservationRooms(domainName, domainId, login, project);
	}
	
	public static LinkedList<ProjectReservationService> getHHGReservationServices(String domainName, Integer domainId, String login, Integer project){
		return PMS.getHHGReservationServices(domainName, domainId, login, project);
	}
	
	public static LinkedList<ProjectReservationServiceDetail> getHHGReservationServicesDetail(String domainName, Integer domainId, String login, Integer service){
		return PMS.getHHGReservationServicesDetail(domainName, domainId, login, service);
	}
	
	public static void updateHHGProjectAttach(String domainName, Integer domainId, String login, Attach attach){
		PMS.updateHHGProjectAttach(domainName, domainId, login, attach);
	}
	
	public static String getHHGUrl(String domainName, Integer domainId, String login){
		ApplicationParameter ap = AON.fetchApplicationParameter(domainName,domainId, login, AppParam.PMS_HHG_SERVER);
		return ap != null ? ap.getValue() : null;
	}

	private static class ReservationFiller implements Function<ProjectReservation, Payload> {
		BookingHarvest bh;
		HashMap<Integer, Attach> map;
		String domainName;
		Integer domainId;
		String login;
		public ReservationFiller(BookingHarvest bh, HashMap< Integer, Attach> map,
				String domainName, Integer domainId, String login) {
			this.bh = bh;
			this.map = map;
			this.domainName = domainName;
			this.domainId = domainId;
			this.login = login;
		}
		
		@Override
		public Payload apply(ProjectReservation pr) {
			Payload payload = bh.new Payload();
			Reservation reservation = bh.new Reservation();
			String agency = AON.getRegistry(domainName, domainId, login, pr.getAgency()).getName();
			reservation.setAgency(agency); // TODO ESTA PASANDO EL ID DE AGENCY ¿PASAR EL NOMBRE? 
			reservation.setCrscode(pr.getCrsCode());
			reservation.setEndDate(dateFormat.format(pr.getEndDate()));
			reservation.setMethod(map.get(pr.getProject()).getDescription());
			reservation.setProjectAttachId(map.get(pr.getProject()).getId());
			reservation.setProject(pr.getProject().toString());
			reservation.setStartDate(dateFormat.format(pr.getStartDate()));
			reservation.setRooms(getHHGReservationRooms(domainName, domainId, login, pr.getProject()).stream()
				.map(new RoomFiller(bh)).collect(Collectors.toCollection(LinkedList::new)));
			reservation.setServices(getHHGReservationServices(domainName, domainId, login, pr.getProject()).stream()
				.map(new ServiceFiller(bh, domainName, domainId, login)).collect(Collectors.toCollection(LinkedList::new)));
			
			payload.setReservation(reservation);
			return payload;
		}
	}
	
	private static class RoomFiller implements Function<ProjectReservationRoom, Room> {
		BookingHarvest bh;
		public RoomFiller(BookingHarvest bh) {
			this.bh = bh;
		}
		
		@Override
		public Room apply(ProjectReservationRoom pr) {
			Room room = bh.new Room();
			room.setAdults(pr.getAdults().toString());
			room.setChildren(pr.getChildren().toString());
			room.setHotel(pr.getHotel().toString()); 
			room.setRatePlan(pr.getRatePlan());
			room.setRoomCode(pr.getRoomCode());
			room.setRoomIndex(pr.getRoomIndex().toString());
			
			return room;
		}
	}
	
	private static class ServiceFiller implements Function<ProjectReservationService, Service> {
		BookingHarvest bh;
		String domainName;
		Integer domainId;
		String login;
		public ServiceFiller(BookingHarvest bh, String domainName, Integer domainId, String login) {
			this.bh = bh;
			this.domainName = domainName;
			this.domainId = domainId;
			this.login = login;
		}
		
		@Override
		public Service apply(ProjectReservationService pr) {
			Service service = bh.new Service();
			service.setMealPlan(pr.getMealPlan());
			service.setServiceCode(pr.getServiceCode());
			Item item = AON.getItem(domainName, domainId, login, pr.getItem());
			
			Tax tax = AON.getTax(domainName, domainId, login,item.getProduct().getVat());
			service.setServicesDetail(getHHGReservationServicesDetail(domainName, domainId, login, pr.getId()).stream()
				.map(new ServiceDetailFiller(bh, tax.getPercentage())).collect(Collectors.toCollection(LinkedList::new)));
			return service;
		}
	}
	
	private static class ServiceDetailFiller implements Function<ProjectReservationServiceDetail, ServiceDetail> {
		BookingHarvest bh;
		Double vat;
		public ServiceDetailFiller(BookingHarvest bh, Double vat) {
			this.bh = bh;
			this.vat = vat;
		}
		
		@Override
		public ServiceDetail apply(ProjectReservationServiceDetail pr) {
			
			ServiceDetail serviceDetail = bh.new ServiceDetail();
			serviceDetail.setEffectiveDate(dateFormat.format(pr.getEffectiveDate()));
			serviceDetail.setPrice(pr.getPrice());
			serviceDetail.setQuantity(pr.getQuantity());
			serviceDetail.setTaxableBase(pr.getTaxableBase());
			serviceDetail.setTotal(pr.getTaxableBase() + (pr.getTaxableBase() * (vat/100)));
			
			return serviceDetail;
		}
	}
}
