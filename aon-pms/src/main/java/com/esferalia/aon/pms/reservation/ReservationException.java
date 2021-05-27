package com.esferalia.aon.pms.reservation;

import com.code.aon.common.AonException;
import com.code.aon.AonVersion;

public class ReservationException extends AonException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String record;
	private int type;

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}

	public ReservationException() {
		this("Unknown error", 1);
	}

	public ReservationException(String message) {
		this(message, 1);
	}

	public ReservationException(Throwable cause) {
		this(cause.getMessage(), 1);
	}

	public ReservationException(String message, Throwable cause) {
		this(message, 1);
	}

	public ReservationException(String message, int type) {
		this(message, null, type);
	}

	public ReservationException(String message, String record, int type) {
		super(message);
		this.record = record;
		this.type = type;
	}

}
