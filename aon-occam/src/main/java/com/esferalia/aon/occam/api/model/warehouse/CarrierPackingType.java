package com.esferalia.aon.occam.api.model.warehouse;

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
}
