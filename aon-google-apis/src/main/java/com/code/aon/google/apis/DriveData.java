package com.code.aon.google.apis;

import java.util.Vector;

import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;

public class DriveData {

	private DomainGserviceaccount Gservice;
	// private Vector<Rattach> rattachs;
	private Vector<FileInfo> attachs;

	private String domain;

	public DriveData(DomainGserviceaccount Gservice, Vector<FileInfo> attachs) {
		this.Gservice = Gservice;
		this.attachs = attachs;

	}

	public DriveData() {

	}

	public DomainGserviceaccount getGservice() {
		return Gservice;
	}

	public void setGservice(DomainGserviceaccount Gservice) {
		this.Gservice = Gservice;
	}

	public Vector<FileInfo> getAttachs() {
		return attachs;
	}

	public void setAttachs(Vector<FileInfo> attachs) {
		this.attachs = attachs;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
