package com.esferalia.aon.seres.ftp.seres;

import com.esferalia.aon.occam.api.model.seres.SeresInfo;

public class FtpObject {

	Integer id;
	String reference;
	byte[] data;
	SeresInfo seresInfo;
	
	public Integer getId() {
		return id;
	}
	
	public FtpObject setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getReference() {
		return reference;
	}
	
	public FtpObject setReference(String reference) {
		this.reference = reference;
		return this;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public FtpObject setData(byte[] data) {
		this.data = data;
		return this;
	}
	
	public SeresInfo getSeresInfo() {
		return seresInfo;
	}
	
	public FtpObject setSeresInfo(SeresInfo seresInfo) {
		this.seresInfo = seresInfo;
		return this;
	}
}
