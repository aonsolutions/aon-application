package com.esferalia.aon.occam.api.model.console;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum ConsoleDomainMessageFixType {
	
	DELETE		{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitDelete(); }},
	SET_NULL	{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitSetNull(); }},
	NEW_VALUE	{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitNewValue(); }}
	;

	public static ConsoleDomainMessageFixType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (ConsoleDomainMessageFixType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}

	public abstract <T> T visit(Visitor<T> visitor);
	
	public static interface Visitor<T> {
		T visitDelete();
		T visitSetNull();
		T visitNewValue();
	}
}
