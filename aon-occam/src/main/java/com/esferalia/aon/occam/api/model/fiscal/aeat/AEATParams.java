package com.esferalia.aon.occam.api.model.fiscal.aeat;

import java.io.Serializable;
import java.util.ArrayList;

public class AEATParams implements Serializable {
	
	private static final long serialVersionUID = 64199381803164777L;
	
	private int domainId;
	private String domainName;
	private String user;
	private Integer mod;
	private Integer certificateId;
	private byte[] certificate;
	private String pass;
	private String name;
	private String document;
	private String nrc;
	private boolean test;
	private ArrayList<String> selected; // Seleccionados para la presentación múltiple desde la matriz
	private ArrayList<String> errores;  // FALTA - Mensajes de error de la presentación de cada modelo, en la presentación múltiple desde la matriz
	
	public int getDomainId() {
		return domainId;
	}
	public AEATParams setDomainId(int domainId) {
		this.domainId = domainId;
		return this;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public AEATParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public AEATParams setUser(String user) {
		this.user = user;
		return this;
	}
	
	public Integer getMod() {
		return mod;
	}
	public AEATParams setMod(Integer mod) {
		this.mod = mod;
		return this;
	}
	
	public Integer getCertificateId() {
		return certificateId;
	}
	public AEATParams setCertificateId(Integer certificateId) {
		this.certificateId = certificateId;
		return this;
	}
	
	public byte[] getCertificate() {
		return certificate;
	}
	public AEATParams setCertificate(byte[] certificate) {
		this.certificate = certificate;
		return this;
	}
	
	public String getPass() {
		return pass;
	}
	public AEATParams setPass(String pass) {
		this.pass = pass;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public AEATParams setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getDocument() {
		return document;
	}
	public AEATParams setDocument(String document) {
		this.document = document;
		return this;
	}
	
	public String getNrc() {
		return nrc;
	}
	public AEATParams setNrc(String nrc) {
		this.nrc = nrc;
		return this;
	}
	public boolean isTest() {
		return test;
	}
	public AEATParams setTest(boolean test) {
		this.test = test;
		return this;
	}
	public ArrayList<String> getSelected() {
		return selected;
	}
	public AEATParams setSelected(ArrayList<String> selected) {
		this.selected = selected;
		return this;
	}
	public ArrayList<String> getErrores() {
		return errores;
	}
	public AEATParams setErrores(ArrayList<String> errores) {
		this.errores = errores;
		return this;
	}
	
}
