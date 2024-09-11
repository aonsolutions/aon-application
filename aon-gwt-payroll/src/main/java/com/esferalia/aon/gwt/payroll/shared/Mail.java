package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class Mail implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String from;
	private String to;
	private String cc;
	private String cco;
	private String bodyHTML;
	private boolean password = false;
	
	public Mail() {
		super();
	}

	public String getFrom() {
		return from;
	}

	public Mail setFrom(String from) {
		this.from = from;
		return this;
	}

	public String getTo() {
		return to;
	}

	public Mail setTo(String to) {
		this.to = to;
		return this;
	}

	public String getCc() {
		return cc;
	}

	public Mail setCc(String cc) {
		this.cc = cc;
		return this;
	}

	public String getCco() {
		return cco;
	}

	public Mail setCco(String cco) {
		this.cco = cco;
		return this;
	}

	public String getBodyHTML() {
		return bodyHTML;
	}

	public Mail setBodyHTML(String bodyHTML) {
		this.bodyHTML = bodyHTML;
		return this;
	}

	public boolean isPassword() {
		return password;
	}

	public Mail setPassword(boolean password) {
		this.password = password;
		return this;
	}
	
	
}
