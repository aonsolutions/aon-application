package com.code.aon.fiscal.config;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.fiscal.enumeration.Period;

public class ModelStatus implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static enum Status {
		MISSING("aon-icon-point-gray","No realizado."),
		PENDING("aon-icon-point-orange","Creado y pendiente."),
		FINISHED("aon-icon-point-light-green","Creado y finalizado."),
		BLOCKED("aon-icon-point-red","Creado y bloqueado."),
		SENT("aon-icon-point-green","Creado y presentado.");
		
		private String styleClass;
		private String message;
		
		private Status(String styleClass,String message) {
			this.styleClass = styleClass;
			this.message = message;
		}
		
		private String getStyleClass() {
			return styleClass;
		}
		private String getMessage() {
			return message;
		}
	}
	
	private Status status;
	private Period period;
	
	public ModelStatus(Period period) {
		this.status = Status.MISSING;
		this.period = period;
	}

	public ModelStatus(Status status, Period period) {
		this.status = status;
		this.period = period;
	}

	public Period getPeriod() {
		return period;
	}
	
	public boolean isMissing() {
		return status == Status.MISSING;
	}
	public boolean isPending() {
		return status == Status.PENDING;
	}
	public boolean isFinished() {
		return status == Status.FINISHED;
	}
	public boolean isSent() {
		return status == Status.SENT;
	}
	public boolean isBlocked() {
		return status == Status.BLOCKED;
	}
	
	public String getStyleClass() {
		return status.getStyleClass();
	}
	public String getMessage() {
		return status.getMessage();
	}
	
}