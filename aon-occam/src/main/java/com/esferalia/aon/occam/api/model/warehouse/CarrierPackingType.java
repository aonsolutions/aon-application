package com.esferalia.aon.occam.api.model.warehouse;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum CarrierPackingType {

	SHIPMENT_REQUEST("Solicitud de Carga"),
	WAYBILL("Hoja de ruta");
	
	private String name;
	
	private CarrierPackingType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static CarrierPackingType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static CarrierPackingType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= CarrierPackingType.values().length) return null;
		return CarrierPackingType.values()[i];
	}
	public static CarrierPackingType safeValueOf( String i ) {
		if (AonStringUtils.isBlank(i)) return null;
		for (CarrierPackingType value : values()) {
			if (i.equalsIgnoreCase(value.name()))
				return value;
		}
		return null;
	}
	
	public boolean isShipmentRequest() {
		return SHIPMENT_REQUEST.equals(this);
	}
	
	public boolean isWaybill() {
		return WAYBILL.equals(this);
	}
}
