package com.esferalia.aon.dsi.util;

import org.jooq.tools.StringUtils;

import com.code.aon.registry.enumeration.DocumentType;

public class EnumUtils {

	
	public static DocumentType getDocumentType(String doc){
		if ( StringUtils.isBlank(doc))
			return null;
		
		
		if ( doc.matches("(?i)[\\dKLM]\\d{7}\\w"))
			return DocumentType.NIF;

		if ( doc.matches("(?i)[XYZ]\\d{7}\\w"))
			return DocumentType.NIE;

		if ( doc.matches("(?i)[ABCDEFGHJNPQRSUVW]\\d{7}\\w"))
			return DocumentType.CIF;
		
		
		return DocumentType.OTHER;
	}
	
	public static  <T extends Enum<?>> Byte enum2Byte(T t){
		return t == null ? null: (byte)t.ordinal();
	}
	
	
	
	
	
}
