package com.code.aon.calendar;

public class CalendarEntry {

	protected static final String DEFAULT_ID = "default";
	
	private String id;
	private int year;
	private String path;
	
	/**
	 * @param id
	 * @param path
	 * @param year
	 */
	public CalendarEntry(String id, int year, String path) {
		this.id = id;
		this.path = path;
		this.year = year;
	}

	public String getId() {
		return this.id;
	}

	public String getPath() {
		return this.path;
	}

	public int getYear() {
		return this.year;
	}
	
	
}
