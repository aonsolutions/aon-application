package com.esferalia.aon.gwt.template.shared.marketplace;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum CarrierCode implements IsSerializable{

	CORREOS("Correos"),
	MRW("MRW"),
	SEUR("Seur"),
	ENDOPACK("Endopack"),
	CHRONO_EXPRESS("Chrono Express"),
	NACEX("Nacex"),
	UPS("UPS"),
	FEDEX("FedEx"),
	DHL("DHL"),
	FASTWAY("Fastway"),
	GLS("GLS"),
	GO("GO!"),
	HERMES_LOGISTIK_GRUPPE("Hermes Logistik Gruppe"),
	ROYAL_MAIL("Royal Mail"),
	PARCELFORCE("Parcelforce"),
	CITY_LINK("City Link"),
	TNT("TNT"),
	TARGET("Target"),
	DEUTSCHE_POST("Deutsche Post"),
	DPD("DPD"),
	CHRONOPOST("Chronopost"),
	LA_POSTE("La Poste"),
	SMARTMAIL("Smartmail"),
	PARCELNET("Parcelnet"),
	SDA("SDA"),
	POSTE_ITALIANE("Poste Italiane"),
	OTRO("Otro");
	
	String name;
	
	private CarrierCode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static CarrierCode getValue(String name){
		CarrierCode[] array = CarrierCode.values();
		for(Integer i = 0; i < array.length; i++){
			if(array[i].getName().equalsIgnoreCase(name)){
				return array[i];
			}
		}
		return OTRO;
	}
}
