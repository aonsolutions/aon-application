package com.code.aon.hhg.webservice.dialog;

import java.util.LinkedList;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookingHarvest {

//	public static final String URL = "http://intranet.hhg-hotels.net/services/booking-harvest/";
	public static final String URL = "https://213.236.3.11/services/booking-harvest/";

	private String username;
	private String nonce;
	private String hash;
	private Payload payload;
	
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
	public Payload getPayload() {
		return payload;
	}
	public BookingHarvest setPayload(Payload payload) {
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
			json.put("payload", getPayload().toJSON());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
		
	public BookingHarvest calculateHash(String password){
		String hash = "";
		if(getNonce() == null) return this; 
		hash = hash.concat(getNonce()).concat(password);
		setHash(DigestUtils.md5Hex(hash));
		return this;
	}
	
	public BookingHarvest calculateNonce(String password){
		String nonce = "" ;
		if(getPayload().getReservation() == null || getPayload().getReservation().getCrscode() == null)
			return this; 
		nonce = nonce.concat(getUsername()).concat(getPayload().getReservation().getCrscode()).concat(password);
		setNonce(DigestUtils.md5Hex(nonce));
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
		String strServices;
		String strRooms;
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
				
				JSONObject jsonRooms = new JSONObject();
				for (Integer i = 0; i < getRooms().size(); i++) 
					jsonRooms.put(i.toString(), getRooms().get(i).toJSON());
				json.put("rooms",jsonRooms);

				JSONObject jsonServices = new JSONObject();
				for (Integer i = 0; i < getServices().size(); i++) 
					jsonServices.put(i.toString(), getServices().get(i).toJSON());
				json.put("services",jsonServices);	
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
			JSONObject json = new JSONObject();
			try {
				json.put("room_index", getRoomIndex());
				json.put("room_code", getRoomCode());
				json.put("rate_plan", getRatePlan());
				json.put("adults", getAdults());
				json.put("children", getChildren());
				json.put("hotel", getHotel());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
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
		String strServicesDetail;
		public JSONObject toJSON(){
			JSONObject json = new JSONObject();
			try {
				json.put("service_code", getServiceCode());
				json.put("meal_plan", getMealPlan());
								
				JSONArray arrayServicesDetail = new JSONArray();
				getServicesDetail().stream().map(service -> service.toJSON()).forEach(s -> arrayServicesDetail.put(s));
				json.put("servicesdetail",arrayServicesDetail);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
		}
	}
	
	public class ServiceDetail{
		private String effectiveDate;
		private Double quantity;
		private Double price;
		private Double taxableBase;
		private Double total;

		public String getEffectiveDate() {
			return effectiveDate;
		}
		public void setEffectiveDate(String effectiveDate) {
			this.effectiveDate = effectiveDate;
		}
		public Double getQuantity() {
			return quantity;
		}
		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}
		public Double getPrice() {
			return price;
		}
		public void setPrice(Double price) {
			this.price = price;
		}
		public Double getTaxableBase() {
			return taxableBase;
		}
		public void setTaxableBase(Double taxableBase) {
			this.taxableBase = taxableBase;
		}
		public Double getTotal() {
			return total;
		}
		public void setTotal(Double total) {
			this.total = total;
		}

		public JSONObject toJSON(){
			JSONObject json = new JSONObject();
			try {
				json.put("effective_date", getEffectiveDate());
				json.put("quantity", getQuantity());
				json.put("price", getPrice());
				json.put("taxable_base", getTaxableBase());
				json.put("total", getTotal());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
		}
	}
}
