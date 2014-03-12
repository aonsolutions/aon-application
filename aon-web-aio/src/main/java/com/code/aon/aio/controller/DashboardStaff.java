package com.code.aon.aio.controller;

import java.io.Serializable;

import com.code.aon.common.AonVersion;

public class DashboardStaff implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	String month;
	int altas;
	int bajas;
	
	public DashboardStaff(){
		//Constructor
	}
	public DashboardStaff(String pMonth, int pAltas, int pBajas) {
		this.month = pMonth;
		this.altas = pAltas;
		this.bajas = pBajas;
	}
	
	public void setMonth(String pMonth) {
		this.month = pMonth;
	}
	public void setAltas(int pAltas) {
		this.altas = pAltas;
	}
	public void setBajas(int pBajas) {
		this.bajas = pBajas;
	}
	public String getMonth() {
		return month;		
	}
	public int getAltas() {
		return altas;
	}
	public int getBajas() {
		return bajas;
	}
	

}
