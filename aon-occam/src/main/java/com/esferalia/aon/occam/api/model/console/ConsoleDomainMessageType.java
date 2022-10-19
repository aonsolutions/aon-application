package com.esferalia.aon.occam.api.model.console;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum ConsoleDomainMessageType {
	
	INTEGRITY 	{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitIntegrity(); }},
	PRODUCT 	{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitProduct(); }},
	AGREEMENT 	{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitAgreement(); }};

	public static ConsoleDomainMessageType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (ConsoleDomainMessageType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}

	public abstract <T> T visit(Visitor<T> visitor);
	
	public static interface Visitor<T> {
		T visitIntegrity();
		T visitProduct();
		T visitAgreement();
	}
}
