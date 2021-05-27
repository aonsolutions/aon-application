/**
 * 
 */
package com.code.aon.file.format.model;

/**
 * Disk filler exception
 * 
 * @author Consulting & Development. Iñigo GAyarre - 30/01/2007
 * @since 1.0
 * 
 */
@SuppressWarnings("serial")
public class Fd0Exception extends RuntimeException {

	/**
	 * Error detailed string
	 */
	private String detail;

	/**
	 * Default constructor 
	 * 
	 * @param s exception cause
	 * @param detail the datail
	 */
	public Fd0Exception(String s, String detail) {
		super(s);
		this.detail = detail;
	}

	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * @param detail the detail to set
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

	
	
}
