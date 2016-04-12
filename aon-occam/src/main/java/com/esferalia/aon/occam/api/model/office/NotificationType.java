package com.esferalia.aon.occam.api.model.office;

public enum NotificationType {
	
	OPEN("NUEVA INCIDENCIA ","creada con la referencia "),
	NEW_INFO("INFORMACION ADICIONAL ","incluida en la referencia "),
	CLOSE("INCIDENCIA CERRADA ","con referencia "),
	REOPEN("INCIDENCIA REABIERTA ","con la referencia ");
	
	private String title;
	private String description;
	 
	private NotificationType(String title, String description) {
		this.title = title;
		this.description = description;
	}
	
	public String getTitle() {
		return title;
	}
	public NotificationType setTitle(String title) {
		this.title = title;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public NotificationType setDescription(String description) {
		this.description = description;
		return this;
	}
	
	
}
