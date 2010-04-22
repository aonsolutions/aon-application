package com.code.aon.planner.util;

import java.util.Collections;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

public class DateTimeEvent extends EventObject {

	private static final long serialVersionUID = -3895581688455957800L;

	/** Fecha de la cita */
	Date date;
	/** Hora de la cita */
	String time;
	/** Propietario del calendario. */
	private Integer owner;
	/** Indica las horas ya ocupadas. */
	private Map busyHours;
	/** Indica la informacion de cada cupo por hora. */
	private Map<Integer, Object> set4Hours = new HashMap<Integer, Object>();
	/** Indica el número de citas de 1ª vez del médico seleccionado. */
    private int firstTime;

	public DateTimeEvent(Object source) {
		super(source);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return Returns the owner.
	 */
	public Integer getOwner() {
		return owner;
	}

	/**
	 * @param owner The owner to set.
	 */
	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	/**
	 * @return Returns the busyHours.
	 */
	@SuppressWarnings("unchecked")
	public Map getBusyHours() {
		return Collections.unmodifiableMap(busyHours);
	}

	/**
	 * @param busyHours The busyHours to set.
	 */
	public void setBusyHours(Map busyHours) {
		this.busyHours = busyHours;
	}

	/**
	 * @return Returns the set4Hours.
	 */
	public Map<Integer, Object> getSet4Hours() {
		return Collections.unmodifiableMap(set4Hours);
	}

	/**
	 * @param set4Hours The set4Hours to set.
	 */
	public void setSet4Hours(Map<Integer, Object> set4Hours) {
		this.set4Hours = set4Hours;
	}

	/**
	 * Añade un nuevo cupo asociado a la hora.
	 * 
	 * @param hour
	 * @param set4Hour
	 */
	public void putSet4Hour(Integer hour, Object set4Hour) {
		this.set4Hours.put(hour, set4Hour);
	}

	/**
	 * @return Returns the firstTime.
	 */
	public int getFirstTime() {
		return firstTime;
	}

	/**
	 * @param firstTime The firstTime to set.
	 */
	public void setFirstTime(int firstTime) {
		this.firstTime = firstTime;
	}

}
