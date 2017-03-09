package com.esferalia.aon.occam.api.model.type;

public enum ElaborationStatus {

    PENDING("Pendiente"),
    CLOSED("Cerrado"),
    IN_PROGRESS("En progreso"),
    FAIL("Fallido"),
    ;
	
	private String name;
	
	private ElaborationStatus(String name) {
		this.name = name;
	}
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return name;
    }

}
