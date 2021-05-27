package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Address implements Serializable {

	private static final long serialVersionUID = -1371668275083259023L;
	
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

	public Address setRdocument(String rdocument) {
		this.rdocument = rdocument;
		return this;
	}

	public String getRname() {
		return rname;
	}

	public Address setRname(String rname) {
		this.rname = rname;
		return this;
	}

	public String getRstreetType() {
		return rstreetType;
	}

	public Address setRstreetType(String rstreetType) {
		this.rstreetType = rstreetType;
		return this;
	}

	public String getRstreetName() {
		return rstreetName;
	}

	public Address setRstreetName(String rstreetName) {
		this.rstreetName = rstreetName;
		return this;
	}

	public String getRstreetNumber() {
		return rstreetNumber;
	}

	public Address setRstreetNumber(String rstreetNumber) {
		this.rstreetNumber = rstreetNumber;
		return this;
	}

	public String getRstreetStair() {
		return rstreetStair;
	}

	public Address setRstreetStair(String rstreetStair) {
		this.rstreetStair = rstreetStair;
		return this;
	}

	public String getRstreetFloor() {
		return rstreetFloor;
	}

	public Address setRstreetFloor(String rstreetFloor) {
		this.rstreetFloor = rstreetFloor;
		return this;
	}

	public String getRstreetDoor() {
		return rstreetDoor;
	}

	public Address setRstreetDoor(String rstreetDoor) {
		this.rstreetDoor = rstreetDoor;
		return this;
	}

	public String getRphone() {
		return rphone;
	}

	public Address setRphone(String rphone) {
		this.rphone = rphone;
		return this;
	}

	public String getRtown() {
		return rtown;
	}

	public Address setRtown(String rtown) {
		this.rtown = rtown;
		return this;
	}

	public int getRprovince() {
		return rprovince;
	}

	public Address setRprovince(int rprovince) {
		this.rprovince = rprovince;
		return this;
	}

	public String getRzip() {
		return rzip;
	}

	public Address setRzip(String rzip) {
		this.rzip = rzip;
		return this;
	}
}
