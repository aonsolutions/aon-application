package com.esferalia.aon.occam.api.model.console;

import java.io.Serializable;

public enum ConsoleTableFieldType implements Serializable {
	STRING 		{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitString(); }},
	BYTE		{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitByte(); }},
	SHORT		{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitShort(); }},
	INTEGER		{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitInteger(); }},
	DOUBLE		{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitDouble(); }},
	DECIMAL		{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitDecimal(); }},
	DATE		{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitDate(); }},
	TIMESTAMP	{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitTimestamp(); }},
	BINARY		{ @Override public <T> T visit(Visitor<T> visitor) { return visitor.visitBinary(); }}
	;
	
	public abstract <T> T visit(Visitor<T> visitor);

	public static interface Visitor<T> {
		T visitString();
		T visitByte();
		T visitShort();
		T visitInteger();
		T visitDouble();
		T visitDecimal();
		T visitDate();
		T visitTimestamp();
		T visitBinary();
	}

}
