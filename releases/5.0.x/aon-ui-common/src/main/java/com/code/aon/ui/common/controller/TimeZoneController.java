package com.code.aon.ui.common.controller;

import java.util.Date;
import java.util.TimeZone;

/**
 * TimeZoneController handles the timeZone where the program is running.
 */
public class TimeZoneController {

	/** The time zone where the program is running */
	private TimeZone tm;

	/**
	 * Gets the time zone.
	 * 
	 * @return the time zone
	 */
	public TimeZone getTimeZone() {
		if (tm == null) {
			tm = TimeZone.getDefault();
		}
		return tm;
	}

	/**
	 * Sets the time zone.
	 * 
	 * @param tm the TimeZone
	 */
	public void setTimeZone(TimeZone tm) {
		this.tm = tm;
	}

	/**
	 * Gets the time zone id.
	 * 
	 * @return the time zone id
	 */
	public String getTimeZoneId() {
		return getTimeZone().getID();
	}
	
	/**
	 * Gets the current Date (new Date() ).
	 * @return The current Date.
	 */
	public Date getCurrentDate() {
		return new Date();
	}
}
