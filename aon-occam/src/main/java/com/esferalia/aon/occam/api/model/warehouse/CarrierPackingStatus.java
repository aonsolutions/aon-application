package com.esferalia.aon.occam.api.model.warehouse;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum CarrierPackingStatus {

	PENDING("Pendiente", "orangered"),
	ON_ROUTE("En ruta", "orange"),
	ON_BASCULA("En bascula", "green"),
	FINISHED("Finalizada", "darkslategray");
	
	private String name;
	private String color;
	
	private CarrierPackingStatus(String name, String color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public String getColor() {
		return color;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static CarrierPackingStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static CarrierPackingStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= CarrierPackingStatus.values().length) return null;
		return CarrierPackingStatus.values()[i];
	}
	public static CarrierPackingStatus safeValueOf( String i ) {
		if (AonStringUtils.isBlank(i)) return null;
		for (CarrierPackingStatus value : values()) {
			if (i.equalsIgnoreCase(value.name()))
				return value;
		}
		return null;
	}
}
