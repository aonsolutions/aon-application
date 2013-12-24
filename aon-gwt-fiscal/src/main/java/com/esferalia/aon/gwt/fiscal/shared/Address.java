package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class Address implements Serializable, IsSerializable {

	private String rdocument;
	private String rname;
	private String rstreetType;
	private String rstreetName;
	private String rstreetNumber;
	private String rstreetStair;
	private String rstreetFloor;
	private String rstreetDoor;
	private String rphone;
	private String rtown;
	private int rprovince;
	private String rzip;

	public String getRdocument() {
		return rdocument;
	}

	public void setRdocument(String rdocument) {
		this.rdocument = rdocument;
	}

	public String getRname() {
		return rname;
	}

	public void setRname(String rname) {
		this.rname = rname;
	}

	public String getRstreetType() {
		return rstreetType;
	}

	public void setRstreetType(String rstreetType) {
		this.rstreetType = rstreetType;
	}

	public String getRstreetName() {
		return rstreetName;
	}

	public void setRstreetName(String rstreetName) {
		this.rstreetName = rstreetName;
	}

	public String getRstreetNumber() {
		return rstreetNumber;
	}

	public void setRstreetNumber(String rstreetNumber) {
		this.rstreetNumber = rstreetNumber;
	}

	public String getRstreetStair() {
		return rstreetStair;
	}

	public void setRstreetStair(String rstreetStair) {
		this.rstreetStair = rstreetStair;
	}

	public String getRstreetFloor() {
		return rstreetFloor;
	}

	public void setRstreetFloor(String rstreetFloor) {
		this.rstreetFloor = rstreetFloor;
	}

	public String getRstreetDoor() {
		return rstreetDoor;
	}

	public void setRstreetDoor(String rstreetDoor) {
		this.rstreetDoor = rstreetDoor;
	}

	public String getRphone() {
		return rphone;
	}

	public void setRphone(String rphone) {
		this.rphone = rphone;
	}

	public String getRtown() {
		return rtown;
	}

	public void setRtown(String rtown) {
		this.rtown = rtown;
	}

	public int getRprovince() {
		return rprovince;
	}

	public void setRprovince(int rprovince) {
		this.rprovince = rprovince;
	}

	public String getRzip() {
		return rzip;
	}

	public void setRzip(String rzip) {
		this.rzip = rzip;
	}

}
