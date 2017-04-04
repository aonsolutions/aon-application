package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public enum VatSummaryType implements Serializable {

	 NATIONAL 		("Nacional"		,vat -> vat.isNational() && !vat.isFarmerRegime())
	,SURCHARGE 		("Rec. Equiv."	,vat -> false)	
	,FARMER 		("R\u00E9g. Agr\u00EDc."	,vat -> vat.isNational() && vat.isFarmerRegime())
	,INTRACOMMUNITY	("Intracomun."	,vat -> vat.isIntracommunity())
	,EXTRACOMMUNITY	("Extracomun."	,vat -> vat.isExtracommunity())
	,CAN_CEU_MEL	("Can/Ceu/Mel"	,vat -> vat.isCanCeuMel())
	,OTHER_ISP		("I.S.P."		,vat -> vat.isOtherISP())
	;
	

	@FunctionalInterface
	public static interface IModelAccepter {
		boolean accept(VatContext vat);
	}
	
	private String description;
	private IModelAccepter accepter;
	
	private VatSummaryType(String description,IModelAccepter accepter) {
		this.description = description;
		this.accepter = accepter;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}

	public static VatSummaryType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static VatSummaryType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= VatSummaryType.values().length) return null;
		return VatSummaryType.values()[i];
	}

	public static VatSummaryType accept( VatContext vat) {
		for (VatSummaryType type : VatSummaryType.values() ) {
			if ( type.accepter.accept(vat) ) {
				return type;
			}
		}
		return null;
	}
}