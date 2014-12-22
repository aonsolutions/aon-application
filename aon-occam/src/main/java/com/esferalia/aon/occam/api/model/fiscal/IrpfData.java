package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

@SuppressWarnings("serial")
public class IrpfData implements Serializable {
	
	private boolean ceutaMelilla;
	private int birthYear;
	private byte familySituation;
	private String spouseDocument;
	private byte disability;
	private byte contract;
	private boolean workActivityExtension;
	private boolean geographicMobility;

	public boolean isCeutaMelilla() {
		return ceutaMelilla;
	}
	public void setCeutaMelilla(boolean ceutaMelilla) {
		this.ceutaMelilla = ceutaMelilla;
	}

	public int getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}
	public byte getFamilySituation() {
		return familySituation;
	}
	public void setFamilySituation(byte familySituation) {
		this.familySituation = familySituation;
	}
	public String getSpouseDocument() {
		return spouseDocument;
	}
	public void setSpouseDocument(String spouseDocument) {
		this.spouseDocument = spouseDocument;
	}
	public byte getDisability() {
		return disability;
	}
	public void setDisability(byte disability) {
		this.disability = disability;
	}
	public byte getContract() {
		return contract;
	}
	public void setContract(byte contract) {
		this.contract = contract;
	}
	public boolean isWorkActivityExtension() {
		return workActivityExtension;
	}
	public void setWorkActivityExtension(boolean workActivityExtension) {
		this.workActivityExtension = workActivityExtension;
	}
	public boolean isGeographicMobility() {
		return geographicMobility;
	}
	public void setGeographicMobility(boolean geographicMobility) {
		this.geographicMobility = geographicMobility;
	}
}
