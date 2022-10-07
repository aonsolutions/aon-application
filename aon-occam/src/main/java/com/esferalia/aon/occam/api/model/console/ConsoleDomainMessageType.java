package com.esferalia.aon.occam.api.model.console;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum ConsoleDomainMessageType {
	
	INTEGRITY,
	PRODUCT,
	AGREEMENT;

	public static ConsoleDomainMessageType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (ConsoleDomainMessageType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}

}
