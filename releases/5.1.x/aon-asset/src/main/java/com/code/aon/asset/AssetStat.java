package com.code.aon.asset;

import java.util.Calendar;
import java.util.Date;

/**
 * Class for representing an asset statistic.
 * 
 * @author Consulting & Development. eagirrezabal - 04/02/2009
 * 
 */
public class AssetStat {

	/**
	 * Tipo de estadistica
	 */
	private String type;
	/**
	 * Clave del referido
	 */
	private String key;
	/**
	 * Nombre del referido
	 */
	private String name;
	/**
	 * Usuario referido
	 */
	private String user;
	/**
	 * Fecha de la estadistica
	 */
	private Date date;
	/**
	 * Horas totales
	 */
	private double hours;
	/**
	 * Numero de reservas
	 */
	private int request;
	/**
	 * Media de horas por reserva
	 */
	private double average;
	
	/**
	 * Constructor. Set date to current date
	 * @param date
	 */
	public AssetStat() {
		date = Calendar.getInstance().getTime();
		type = "DATE";
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getHours() {
		return hours;
	}
	public void setHours(double hours) {
		this.hours = hours;
	}
	public int getRequest() {
		return request;
	}
	public void setRequest(int request) {
		this.request = request;
	}
	public double getAverage() {
		return average;
	}
	public void setAverage(double average) {
		this.average = average;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}

enum StatType{
	DATE, USER;
	
}