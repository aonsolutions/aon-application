package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Messages implements Serializable {

	public class Message implements Serializable {
		private String description;
		private String message;
		
		public Message() {
			super();
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
	}
	
	private List<Message> infoMessages;
	private List<Message> successMessages; 
	private List<Message> errorMessages;
	private List<Message> warningMessages; 
	
	public Messages() {
		super();
		this.infoMessages = new ArrayList<Messages.Message>();
		this.successMessages = new ArrayList<Messages.Message>();
		this.errorMessages = new ArrayList<Messages.Message>();
		this.warningMessages = new ArrayList<Messages.Message>();
	}

	public List<Message> getInfoMessages() {
		return infoMessages;
	}

	public void setInfoMessages(List<Message> infoMessages) {
		this.infoMessages = infoMessages;
	}

	public List<Message> getSuccessMessages() {
		return successMessages;
	}

	public void setSuccessMessages(List<Message> successMessages) {
		this.successMessages = successMessages;
	}

	public List<Message> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<Message> errorMessages) {
		this.errorMessages = errorMessages;
	}
	
	public List<Message> getWarningMessages() {
		return warningMessages;
	}

	public void setWarningMessages(List<Message> warningMessages) {
		this.warningMessages = warningMessages;
	}
	
	public void addInfoMessage(Message infoMessage) {
		this.infoMessages.add(infoMessage);
	}
	
	public void addErrorMessage(Message errorMessage) {
		this.errorMessages.add(errorMessage);
	}
	
	public void addSuccessMessage(Message successMessage) {
		this.successMessages.add(successMessage);
	}
	
	public void addWarningMessage(Message warningMessage) {
		this.warningMessages.add(warningMessage);
	}
	
}
