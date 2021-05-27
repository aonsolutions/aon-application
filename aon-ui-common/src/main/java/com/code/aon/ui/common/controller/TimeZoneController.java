package com.code.aon.ui.common.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

import com.code.aon.AonVersion;

/**
 * TimeZoneController handles the timeZone where the program is running.
 */
public class TimeZoneController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
