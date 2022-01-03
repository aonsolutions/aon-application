package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public enum InvestAssetType implements Serializable{

	PREMISES,
	OTHER_BUILDING,
	MEANS_OF_TRANSPORT,
	FIXED_PHONE,
	CELLULAR_PHONE,
	FAX,
	FURNITURE,
	MACHINERY,
	COMPUTER_EQUIPMENT,
	INSTALLATION,
	ACCOUNT_GROUP_20_ASSET,
	ACCOUNT_GROUP_21_ASSET,
	ACCOUNT_GROUP_23_ASSET,
	BUILDING_PLOT;

	private InvestAssetType() {
	
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static InvestAssetType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvestAssetType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvestAssetType.values().length) return null;
		return InvestAssetType.values()[i];
	}
	
	public static InvestAssetType safeValueOf( String i ) {
		for (InvestAssetType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
