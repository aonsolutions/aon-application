package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvestAssetType implements Serializable{

	PREMISES("Local"),
	OTHER_BUILDING("Otros Inmuebles"),
	MEANS_OF_TRANSPORT("Medios de Transporte"),
	FIXED_PHONE("Telefono Fijo"),
	CELLULAR_PHONE("Telefono Movil"),
	FAX("Fax"),
	FURNITURE("Mobiliario"),
	MACHINERY("Maquinaria"),
	COMPUTER_EQUIPMENT("Equipos Informaticos"),
	INSTALLATION("Instalacion"),
	ACCOUNT_GROUP_20_ASSET("Bienes Grupo 20 PGC"),
	ACCOUNT_GROUP_21_ASSET("Bienes Grupo 21 PGC"),
	ACCOUNT_GROUP_23_ASSET("Bienes Grupo 23 PGC"),
	BUILDING_PLOT("Solar");

	private String description;
	
	private InvestAssetType(String description) {
		this.description = description;
	}
	
	public String description(){
		return description;
	}
	
	public byte value(){
		return (byte) ordinal();
	}
	
	public static Optional<InvestAssetType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<InvestAssetType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvestAssetType.values().length) return Optional.empty();
		return Optional.of(InvestAssetType.values()[i]);
	}
	
	public static Optional<InvestAssetType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
}
