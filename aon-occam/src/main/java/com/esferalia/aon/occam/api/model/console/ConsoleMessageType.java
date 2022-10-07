package com.esferalia.aon.occam.api.model.console;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum ConsoleMessageType {

	TITLE 			{ @Override public void visit(Visitor visitor) {visitor.visitTitle(); }},
	SUBTITLE		{ @Override public void visit(Visitor visitor) {visitor.visitSubtitle(); }},
	MESSAGE			{ @Override public void visit(Visitor visitor) {visitor.visitMessage(); }},
	MAIN_PROGRESS	{ @Override public void visit(Visitor visitor) {visitor.visitMainProgress(); }},
	PROGRESS		{ @Override public void visit(Visitor visitor) {visitor.visitProgress(); }},
	ERROR			{ @Override public void visit(Visitor visitor) {visitor.visitError(); }},
	WARNING			{ @Override public void visit(Visitor visitor) {visitor.visitWarning(); }},
	OK				{ @Override public void visit(Visitor visitor) {visitor.visitOk(); }},
	CONSOLE_MESSAGE	{ @Override public void visit(Visitor visitor) {visitor.visitConsoleMessage(); }},
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
		void visitMainProgress();
		void visitProgress();
		void visitWarning();
		void visitError();
		void visitOk();
		void visitConsoleMessage();
	}
	
}
