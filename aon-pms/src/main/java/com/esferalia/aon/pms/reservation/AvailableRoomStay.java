package com.esferalia.aon.pms.reservation;

import java.io.Serializable;

import com.code.aon.common.AonVersion;

public class AvailableRoomStay implements IReservationConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private int index;
	private String reservationId;
	private String tariffCode;
	private String tariffDescription;
	private boolean tariffStopSales;
	private String inventoryCode;
	private String roomCode;
	private String roomDescription;
	private String mealPlan;
	private double dailyPrice;
	private double totalPrice;
	private String availability;
	private String cancelPenalty;
	private boolean error;
	private String errorMessage;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getReservationId() {
		return reservationId;
	}

	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}

	public String getTariffCode() {
		return tariffCode;
	}

	public void setTariffCode(String tariffCode) {
		this.tariffCode = tariffCode;
	}

	public String getTariffDescription() {
		return tariffDescription;
	}

	public void setTariffDescription(String tariffDescription) {
		this.tariffDescription = tariffDescription;
	}

	public boolean getTariffStopSales() {
		return tariffStopSales;
	}

	public void setTariffStopSales(boolean tariffStopSales) {
		this.tariffStopSales = tariffStopSales;
	}

	public String getInventoryCode() {
		return inventoryCode;
	}

	public void setInventoryCode(String inventoryCode) {
		this.inventoryCode = inventoryCode;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public String getRoomDescription() {
		return roomDescription;
	}

	public void setRoomDescription(String roomDescription) {
		this.roomDescription = roomDescription;
	}

	public String getMealPlan() {
		return mealPlan;
	}

	public void setMealPlan(String regime) {
		this.mealPlan = regime;
	}

	public double getDailyPrice() {
		return dailyPrice;
	}

	public void setDailyPrice(double dailyPrice) {
		this.dailyPrice = dailyPrice;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public String getCancelPenalty() {
		return cancelPenalty;
	}

	public void setCancelPenalty(String cancelPenalty) {
		this.cancelPenalty = cancelPenalty;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
