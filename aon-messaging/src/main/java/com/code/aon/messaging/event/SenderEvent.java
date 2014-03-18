package com.code.aon.messaging.event;

import java.util.EventObject;

import com.code.aon.AonVersion;

public class SenderEvent extends EventObject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private int errorCode;

	public SenderEvent(Object source) {
		super(source);
	}

	public SenderEvent(Object source, int errorCode) {
		super(source);
		this.errorCode = errorCode;
	}

	public SenderEvent(Object source, String[] messageId) {
		super(source);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
