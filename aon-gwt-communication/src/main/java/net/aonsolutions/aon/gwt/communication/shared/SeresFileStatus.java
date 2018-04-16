package net.aonsolutions.aon.gwt.communication.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum SeresFileStatus implements IsSerializable{

	PENDING("Pendiente"),
	SEND("Enviado"),
	CANCEL("Cancelado"),
	ACCEPT("Aceptado"),
	FAIL("Fallido");

	private String description;
	
	private SeresFileStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
}
