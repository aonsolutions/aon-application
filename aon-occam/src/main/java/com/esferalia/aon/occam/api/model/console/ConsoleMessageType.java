package com.esferalia.aon.occam.api.model.console;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum ConsoleMessageType {

	TITLE 		{ @Override public void visit(Visitor visitor) {visitor.visitTitle(); }},
	SUBTITLE	{ @Override public void visit(Visitor visitor) {visitor.visitSubtitle(); }},
	MESSAGE		{ @Override public void visit(Visitor visitor) {visitor.visitMessage(); }},
	PROGRESS	{ @Override public void visit(Visitor visitor) {visitor.visitProgress(); }},
	ERROR		{ @Override public void visit(Visitor visitor) {visitor.visitError(); }},
	OK			{ @Override public void visit(Visitor visitor) {visitor.visitOk(); }},
	;

	private ConsoleMessageType() {
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	public String getValue() {
		return toString();
	}
	
	public static ConsoleMessageType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ConsoleMessageType.values().length) return null;
		return ConsoleMessageType.values()[i];
	}
	
	public static ConsoleMessageType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (ConsoleMessageType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
	public abstract void visit(Visitor visitor);
	
	public static interface Visitor {
		void visitTitle();
		void visitSubtitle();
		void visitMessage();
		void visitProgress();
		void visitError();
		void visitOk();
	}
	
}
