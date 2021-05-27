package net.aonsolutions.aon.gwt.communication.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum IngenetAttachStatus implements IsSerializable{

	REQUEST("Pendiente"),
	OK("Procesado"),
	FAIL("Fallido");

	private String description;
	
	private IngenetAttachStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
}
