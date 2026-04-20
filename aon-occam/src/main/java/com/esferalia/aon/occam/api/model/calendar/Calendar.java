package com.esferalia.aon.occam.api.model.calendar;

import java.io.Serializable;

public class Calendar implements Serializable {

	private Integer id;
	private Integer domain;
	private Holiday holiday;
	private Double anualHours;
	private Double anualPersonalDays;
	private Double anualHolidays;
	private Byte holidaysType;
	private String description;
	private String comment;
	private boolean isMonday;
	private Double mondayHours;
	private boolean isTuesday;
	private Double tuesdayHours;
	private boolean isWednesday;
	private Double wednesdayHours;
	private boolean isThursday;
	private Double thursdayHours;
	private boolean isFriday;
	private Double fridayHours;
	private boolean isSaturday;
	private Double saturdayHours;
	private boolean isSunday;
	private Double sundayHours;
	private boolean isGeneric;
	private Integer calendarParent;
	
	public Calendar() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public Calendar setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Calendar setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Holiday getHoliday() {
		return holiday;
	}

	public Calendar setHoliday(Holiday holiday) {
		this.holiday = holiday;
		return this;
	}

	public Double getAnualHours() {
		return null == anualHours ? 0.00 : anualHours;
	}

	public Calendar setAnualHours(Double anualHours) {
		this.anualHours = anualHours;
		return this;
	}

	public Double getAnualPersonalDays() {
		return null == anualPersonalDays ? 0.00 : anualPersonalDays;
	}

	public Calendar setAnualPersonalDays(Double anualPersonalDays) {
		this.anualPersonalDays = anualPersonalDays;
		return this;
	}

	public Double getAnnualHolidays() {
		return null == anualHolidays ? 0.00 : anualHolidays;
	}

	public Calendar setAnnualHolidays(Double anualHolidays) {
		this.anualHolidays = anualHolidays;
		return this;
	}

	public Byte getHolidaysType() {
		return holidaysType;
	}

	public Calendar setHolidaysType(Byte holidaysType) {
		this.holidaysType = holidaysType;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Calendar setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public Calendar setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public boolean isMonday() {
		return isMonday;
	}

	public Calendar setMonday(boolean isMonday) {
		this.isMonday = isMonday;
		return this;
	}

	public Double getMondayHours() {
		return null == mondayHours ? 0.00 : mondayHours;
	}

	public Calendar setMondayHours(Double mondayHours) {
		this.mondayHours = mondayHours;
		return this;
	}

	public boolean isTuesday() {
		return isTuesday;
	}

	public Calendar setTuesday(boolean isTuesday) {
		this.isTuesday = isTuesday;
		return this;
	}

	public Double getTuesdayHours() {
		return null == tuesdayHours ? 0.00 : tuesdayHours;
	}

	public Calendar setTuesdayHours(Double tuesdayHours) {
		this.tuesdayHours = tuesdayHours;
		return this;
	}

	public boolean isWednesday() {
		return isWednesday;
	}

	public Calendar setWednesday(boolean isWednesday) {
		this.isWednesday = isWednesday;
		return this;
	}

	public Double getWednesdayHours() {
		return null == wednesdayHours ? 0.00 : wednesdayHours;
	}

	public Calendar setWednesdayHours(Double wednesdayHours) {
		this.wednesdayHours = wednesdayHours;
		return this;
	}

	public boolean isThursday() {
		return isThursday;
	}

	public Calendar setThursday(boolean isThursday) {
		this.isThursday = isThursday;
		return this;
	}

	public Double getThursdayHours() {
		return null == thursdayHours ? 0.00 : thursdayHours;
	}

	public Calendar setThursdayHours(Double thursdayHours) {
		this.thursdayHours = thursdayHours;
		return this;
	}

	public boolean isFriday() {
		return isFriday;
	}

	public Calendar setFriday(boolean isFriday) {
		this.isFriday = isFriday;
		return this;
	}

	public Double getFridayHours() {
		return null == fridayHours ? 0.00 : fridayHours;
	}

	public Calendar setFridayHours(Double fridayHours) {
		this.fridayHours = fridayHours;
		return this;
	}

	public boolean isSaturday() {
		return isSaturday;
	}

	public Calendar setSaturday(boolean isSaturday) {
		this.isSaturday = isSaturday;
		return this;
	}

	public Double getSaturdayHours() {
		return null == saturdayHours ? 0.00 : saturdayHours;
	}

	public Calendar setSaturdayHours(Double saturdayHours) {
		this.saturdayHours = saturdayHours;
		return this;
	}

	public boolean isSunday() {
		return isSunday;
	}

	public Calendar setSunday(boolean isSunday) {
		this.isSunday = isSunday;
		return this;
	}

	public Double getSundayHours() {
		return null == sundayHours ? 0.00 : sundayHours;
	}

	public Calendar setSundayHours(Double sundayHours) {
		this.sundayHours = sundayHours;
		return this;
	}

	public boolean isGeneric() {
		return isGeneric;
	}

	public Calendar setGeneric(boolean isGeneric) {
		this.isGeneric = isGeneric;
		return this;
	}

	public Integer getCalendarParent() {
		return calendarParent;
	}

	public Calendar setCalendarParent(Integer calendarParent) {
		this.calendarParent = calendarParent;
		return this;
	}

	
}
