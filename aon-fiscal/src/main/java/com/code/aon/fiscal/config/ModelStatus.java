package com.code.aon.fiscal.config;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.fiscal.enumeration.Period;

public class ModelStatus implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static enum Status {
		MISSING("aon-icon-minus","No realizado. Click para crear."),
		PENDING("aon-icon-edit-add-simple","Creado - Pendiente. Click para editar."),
		FINISHED("aon-icon-accept","Finalizado. Click para ver.");
		//aon-icon-predetermine
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

	public ModelStatus(boolean finished, Period period) {
		this.status = (finished)?Status.FINISHED:Status.PENDING;
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
	
	public String getStyleClass() {
		return status.getStyleClass();
	}
	public String getMessage() {
		return status.getMessage();
	}
	
}