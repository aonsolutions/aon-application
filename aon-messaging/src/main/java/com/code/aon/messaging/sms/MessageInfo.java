package com.code.aon.messaging.sms;

import java.util.Calendar;

public class MessageInfo {

	private String originator, organization, name, message;
    private Calendar sentat, receivedat;
    private int validityperiod;

    /**
     * Message Originator.
     * 
     * @return
     */
	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	/**
	 * Organization name.
	 * 
	 * @return
	 */
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * Sender name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Message.
	 * 
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Sending date.
	 * 
	 * @return
	 */
	public Calendar getSentat() {
		return sentat;
	}

	public void setSentat(Calendar sentat) {
		this.sentat = sentat;
	}

	/**
	 * Receiving date.
	 * 
	 * @return
	 */
	public Calendar getReceivedat() {
		return receivedat;
	}

	public void setReceivedat(Calendar receivedat) {
		this.receivedat = receivedat;
	}

	/**
	 * Validity period in hours.
	 * 
	 * @return
	 */
	public int getValidityperiod() {
		return validityperiod;
	}

	public void setValidityperiod(int validityperiod) {
		this.validityperiod = validityperiod;
	}

}
