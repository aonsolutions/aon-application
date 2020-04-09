package com.esferalia.aon.altai.utils;

import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.occam.api.model.type.DomainType;

public class Utils {
	
	
	public static final byte CIF = 1;
	public static final byte NIF = 0;
	public static final byte DNI = 0;
	public static final byte NIE = 2;
	public static final byte OTHER = 6;
	
//	NIF,
//	CIF,
//	NIE,
//	PASSPORT,
//	WORK_PERMIT,
//	COMMUNITY_CARD,
//	OTHER,
//	NOT_CENSUSED
    
	public static byte getType(String document ) {
		if ( isCif(document) ) 
			return 1; 
		if ( isNif(document) ) 
			return 0; 
		if ( isDni(document) ) 
			return 0; 
		if ( isNie(document) ) 
			return 2; 
		return 6; 
	}
	
    public static boolean isDni(String dni) {
    	return dni != null ? dni.trim().toUpperCase().matches("^[0-9]+[A-Z]$") : false;
    }

    public static boolean isNie(String nie) {
    	return nie != null ? nie.trim().toUpperCase().matches("^[XYZ][0-9]+[A-Z]$") : false;
    }

    public static boolean isNif(String nif) {
    	return nif != null ? nif.trim().toUpperCase().matches("^[KLM][0-9]+[A-Z]$") : false;
    }    
	
    public static boolean isCif(String cif) {
    	return cif != null ? cif.trim().toUpperCase().matches("^[A-Z][0-9]+$") : false;
    }    

}
