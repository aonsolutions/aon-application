package com.code.aon.conexflow;

public class ConexFlowConnection {

	Boolean active;
	String server;
	String serverAck;
	String cfUser;
	Integer empresa;
	Integer centro;
	Integer tpv;
	
	public String getServer() {
		return server;
	}
	
	public void setServer(String server) {
		this.server = server;
	}
	
	public String getServerAck() {
		return serverAck;
	}
	
	public void setServerAck(String serverAck) {
		this.serverAck = serverAck;
	}
	
	public String getCfUser() {
		return cfUser;
	}
	
	public void setCfUser(String cfUser) {
		setInfo(cfUser);
		this.cfUser = cfUser;
	}
	
	public Integer getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(Integer empresa) {
		this.empresa = empresa;
	}
	
	public Integer getCentro() {
		return centro;
	}
	
	public void setCentro(Integer centro) {
		this.centro = centro;
	}
	
	public Integer getTpv() {
		return tpv;
	}
	
	public void setTpv(Integer tpv) {
		this.tpv = tpv;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	private void setInfo(String cfUser) {
		setEmpresa(Integer.parseInt(cfUser.substring(0, 8)));
		setCentro(Integer.parseInt(cfUser.substring(8, 12)));
		setTpv(Integer.parseInt(cfUser.substring(12)));
	}
}
