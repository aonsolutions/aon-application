package com.esferalia.aon.occam.api.model.console;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum ConsoleDomainMessageType {
	
	INTEGRITY 		{ @Override public <T> T visit(ConsoleDomainMessageTypeVisitor<T> visitor) { return visitor.visitIntegrity(); }},
	SCOPE_INTEGRITY { @Override public <T> T visit(ConsoleDomainMessageTypeVisitor<T> visitor) { return visitor.visitScopeIntegrity(); }},
	PRODUCT 		{ @Override public <T> T visit(ConsoleDomainMessageTypeVisitor<T> visitor) { return visitor.visitProduct(); }},
	AGREEMENT 		{ @Override public <T> T visit(ConsoleDomainMessageTypeVisitor<T> visitor) { return visitor.visitAgreement(); }};

	public static ConsoleDomainMessageType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (ConsoleDomainMessageType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}

	public abstract <T> T visit(ConsoleDomainMessageTypeVisitor<T> visitor);
	
	public static interface ConsoleDomainMessageTypeVisitor<T> {
		T visitIntegrity();
		T visitScopeIntegrity();
		T visitProduct();
		T visitAgreement();
	}
}
