package com.esferalia.aon.occam.api.model.task;

public enum NotificationMode {

	NOT_SEND("No enviar ninguna notificación"),
	TEST("Estado de pruebas"),
	REAK("Entorno de producción REAL");
	
	private String name;
	
	private NotificationMode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
    public byte value() {
    	return (byte) this.ordinal();
	}
}

