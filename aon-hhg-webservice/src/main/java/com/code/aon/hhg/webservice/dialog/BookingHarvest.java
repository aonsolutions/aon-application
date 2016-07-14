package com.code.aon.hhg.webservice.dialog;

import java.util.LinkedList;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookingHarvest {

	public static final String URL = "http://intranet.hhg-hotels.net/services/booking-harvest/";

	private String username;
	private String nonce;
	private String hash;
	private LinkedList<Payload> payload;
	
	//***** Gets & Sets *****/

	public String getUsername() {
		return username;
	}
	public BookingHarvest setUsername(String username) {
		this.username = username;
		return this;
	}
	public String getNonce() {
		return nonce;
	}
	public BookingHarvest setNonce(String nonce) {
		this.nonce = nonce;
		return this;
	}
	public String getHash() {
		return hash;
	}
	public BookingHarvest setHash(String hash) {
		this.hash = hash;
		return this;
	}
	public LinkedList<Payload> getPayload() {
		return payload;
	}
	public BookingHarvest setPayload(LinkedList<Payload> payload) {
		this.payload = payload;
		return this;
	}

	//***** Utils *****/
	
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		try {
			json.put("username", getUsername());
			json.put("nonce", getNonce());
			json.put("hash", getHash());
			System.out.println(getPayload().get(0).getReservation());
			json.put("payload",new JSONArray(
					getPayload().stream().map(res -> res.toJSON())
					.collect(Collectors.toCollection(LinkedList::new))));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
		
	public BookingHarvest calculateHash(String password){
		String hash = "";
		hash = hash.concat(getUsername()).concat(getNonce()).concat(password);
		setHash(DigestUtils.md5Hex(hash));
		return this;
	}
	
	//***** Classes *****/
	
	public class Payload{
		private Reservation reservation;

		public Reservation getReservation() {
			return reservation;
		}
		public void setReservation(Reservation reservation) {
			this.reservation = reservation;
		}
		
		public JSONObject toJSON(){
			JSONObject json = new JSONObject();
			try {
				json.put("reservation", getReservation().toJSON());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
		}
	}
	
	public class Reservation{
		private String method;
		private String project;
		private String agency;
		private String tariff;
		private String crscode;
		private String startDate;
		private String endDate;	
		private LinkedList<Room> rooms;
		private LinkedList<Service> services;
		
		private Integer projectAttachId;
		
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public String getProject() {
			return project;
		}
		public void setProject(String project) {
			this.project = project;
		}
		public String getAgency() {
			return agency;
		}
		public void setAgency(String agency) {
			this.agency = agency;
		}
		public String getTariff() {
			return tariff;
		}
		public void setTariff(String tariff) {
			this.tariff = tariff;
		}
		public String getCrscode() {
			return crscode;
		}
		public void setCrscode(String crscode) {
			this.crscode = crscode;
		}
		public LinkedList<Room> getRooms() {
			return rooms;
		}
		public void setRooms(LinkedList<Room> rooms) {
			this.rooms = rooms;
		}
		public LinkedList<Service> getServices() {
			return services;
		}
		public void setServices(LinkedList<Service> services) {
			this.services = services;
		}
		public String getStartDate() {
			return startDate;
		}
		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		
		public Integer getProjectAttachId(){
			return projectAttachId;
		}
		public Reservation setProjectAttachId(Integer projectAttachId){
			this.projectAttachId = projectAttachId;
			return this;
		}
		
		public JSONObject toJSON(){
			JSONObject json = new JSONObject();
			try {
				json.put("method", getMethod());
				json.put("project", getProject());
				json.put("agency", getAgency());
				json.put("tariff", getTariff());
				json.put("crscode", getCrscode());
				json.put("start_date", getStartDate());
				json.put("end_date", getEndDate());
				json.put("rooms", new JSONArray(
						getRooms().stream().map(room -> room.toJSON())
						.collect(Collectors.toCollection(LinkedList::new))));
				json.put("services", new JSONArray(
						getServices().stream().map(service -> service.toJSON())
						.collect(Collectors.toCollection(LinkedList::new))));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
		}
	}
	
	public class Room{
		private String roomIndex;
		private String roomCode;
		private String ratePlan;
		private String adults;
		private String children;
		private String hotel;
		
		public Room() {}
		
		public String getRoomIndex() {
			return roomIndex;
		}
		public void setRoomIndex(String roomIndex) {
			this.roomIndex = roomIndex;
		}
		public String getRoomCode() {
			return roomCode;
		}
		public void setRoomCode(String roomCode) {
			this.roomCode = roomCode;
		}
		public String getRatePlan() {
			return ratePlan;
		}
		public void setRatePlan(String ratePlan) {
			this.ratePlan = ratePlan;
		}
		public String getAdults() {
			return adults;
		}
		public void setAdults(String adults) {
			this.adults = adults;
		}
		public String getChildren() {
			return children;
		}
		public void setChildren(String children) {
			this.children = children;
		}
		public String getHotel() {
			return hotel;
		}
		public void setHotel(String hotel) {
			this.hotel = hotel;
		}

		public JSONObject toJSON(){
			JSONObject jsonRoom = new JSONObject();
			JSONObject json = new JSONObject();
			try {
				json.put("room_index", getRoomIndex());
				json.put("room_code", getRoomCode());
				json.put("rate_plan", getRatePlan());
				json.put("adults", getAdults());
				json.put("children", getChildren());
				json.put("hotel", getHotel());
				jsonRoom.put("room", json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonRoom;
		}
	}
	
	public class Service{
		private String serviceCode;
		private String mealPlan;
		private LinkedList<ServiceDetail> servicesDetail;
		
		public String getServiceCode() {
			return serviceCode;
		}
		public void setServiceCode(String serviceCode) {
			this.serviceCode = serviceCode;
		}
		public String getMealPlan() {
			return mealPlan;
		}
		public void setMealPlan(String mealPlan) {
			this.mealPlan = mealPlan;
		}
		public LinkedList<ServiceDetail> getServicesDetail() {
			return servicesDetail;
		}
		public void setServicesDetail(LinkedList<ServiceDetail> servicesDetail) {
			this.servicesDetail = servicesDetail;
		}

		public JSONObject toJSON(){
			JSONObject jsonService = new JSONObject();
			JSONObject json = new JSONObject();
			try {
				json.put("service_code", getServiceCode());
				json.put("meal_plan", getMealPlan());
				json.put("servicesDetail", new JSONArray(
						getServicesDetail().stream().map(service -> service.toJSON())
						.collect(Collectors.toCollection(LinkedList::new))));
				jsonService.put("service", json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonService;
		}
	}
	
	public class ServiceDetail{
		private String effectiveDate;
		private String quantity;
		private String price;
		private String taxableBase;

		public String getEffectiveDate() {
			return effectiveDate;
		}
		public void setEffectiveDate(String effectiveDate) {
			this.effectiveDate = effectiveDate;
		}
		public String getQuantity() {
			return quantity;
		}
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getTaxableBase() {
			return taxableBase;
		}
		public void setTaxableBase(String taxableBase) {
			this.taxableBase = taxableBase;
		}

		public JSONObject toJSON(){
			JSONObject json = new JSONObject();
			try {
				json.put("effective_date", getEffectiveDate());
				json.put("quantity", getQuantity());
				json.put("price", getPrice());
				json.put("taxable_base", getTaxableBase());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
		}
	}
}
