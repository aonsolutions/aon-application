package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class IrpfData implements Serializable, IsSerializable {
	
	private boolean ceutaMelilla;
	private int birthYear;
	private int familySituation;
	private String spouseDocument;
	private int disability;
	private int contract;
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
	public int getFamilySituation() {
		return familySituation;
	}
	public void setFamilySituation(int familySituation) {
		this.familySituation = familySituation;
	}
	public String getSpouseDocument() {
		return spouseDocument;
	}
	public void setSpouseDocument(String spouseDocument) {
		this.spouseDocument = spouseDocument;
	}
	public int getDisability() {
		return disability;
	}
	public void setDisability(int disability) {
		this.disability = disability;
	}
	public int getContract() {
		return contract;
	}
	public void setContract(int contract) {
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
