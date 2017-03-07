package com.code.aon.conexflow;

public class ConexFlowConnection {

	private Boolean active;
	private String server;
	private String serverAck;
	private String cfUser;
	private Integer empresa;
	private Integer centro;
	private	Integer tpv;
	private	String keyA;
	private String keyB;
	
	public String getServer() {
		return server;
	}
	
	public ConexFlowConnection setServer(String server) {
		this.server = server;
		return this;
	}
	
	public String getServerAck() {
		return serverAck;
	}
	
	public ConexFlowConnection setServerAck(String serverAck) {
		this.serverAck = serverAck;
		return this;
	}
	
	public String getCfUser() {
		return cfUser;
	}
	
	public ConexFlowConnection setCfUser(String cfUser) {
		setInfo(cfUser);
		this.cfUser = cfUser;
		return this;
	}
	
	public Integer getEmpresa() {
		return empresa;
	}
	
	public ConexFlowConnection setEmpresa(Integer empresa) {
		this.empresa = empresa;
		return this;
	}
	
	public Integer getCentro() {
		return centro;
	}
	
	public ConexFlowConnection setCentro(Integer centro) {
		this.centro = centro;
		return this;
	}
	
	public Integer getTpv() {
		return tpv;
	}
	
	public ConexFlowConnection setTpv(Integer tpv) {
		this.tpv = tpv;
		return this;
	}

	public Boolean isActive() {
		return active;
	}

	public ConexFlowConnection setActive(Boolean active) {
		this.active = active;
		return this;
	}

	public String getKeyA() {
		return keyA;
	}

	public ConexFlowConnection setKeyA(String keyA) {
		this.keyA = keyA;
		return this;
	}

	public String getKeyB() {
		return keyB;
	}

	public ConexFlowConnection setKeyB(String keyB) {
		this.keyB = keyB;
		return this;
	}

	private ConexFlowConnection setInfo(String cfUser) {
		setEmpresa(Integer.parseInt(cfUser.substring(0, 8)));
		setCentro(Integer.parseInt(cfUser.substring(8, 12)));
		setTpv(Integer.parseInt(cfUser.substring(12)));
		return this;
	}
}
