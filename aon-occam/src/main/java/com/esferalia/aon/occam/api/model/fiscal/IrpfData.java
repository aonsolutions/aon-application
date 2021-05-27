package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class IrpfData implements Serializable {
	
	private static final long serialVersionUID = 3767479902944582762L;
	
	private Integer id;
	private Integer domain;
	
	private boolean ceutaMelilla;
	private int birthYear;
	private byte familySituation;
	private String spouseDocument;
	private Byte disability;
	private byte contract;
	private boolean workActivityExtension;
	private boolean geographicMobility;
	
	public Integer getId() {
		return id;
	}
	public IrpfData setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public IrpfData setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public boolean isCeutaMelilla() {
		return ceutaMelilla;
	}
	public IrpfData setCeutaMelilla(boolean ceutaMelilla) {
		this.ceutaMelilla = ceutaMelilla;
		return this;
	}

	public int getBirthYear() {
		return birthYear;
	}
	public IrpfData setBirthYear(int birthYear) {
		this.birthYear = birthYear;
		return this;
	}
	public byte getFamilySituation() {
		return familySituation;
	}
	public IrpfData setFamilySituation(byte familySituation) {
		this.familySituation = familySituation;
		return this;
	}
	public String getSpouseDocument() {
		return spouseDocument;
	}
	public IrpfData setSpouseDocument(String spouseDocument) {
		this.spouseDocument = spouseDocument;
		return this;
	}
	public Byte getDisability() {
		return disability;
	}
	public IrpfData setDisability(Byte disability) {
		this.disability = disability;
		return this;
	}
	public byte getContract() {
		return contract;
	}
	public IrpfData setContract(byte contract) {
		this.contract = contract;
		return this;
	}
	public boolean isWorkActivityExtension() {
		return workActivityExtension;
	}
	public IrpfData setWorkActivityExtension(boolean workActivityExtension) {
		this.workActivityExtension = workActivityExtension;
		return this;
	}
	public boolean isGeographicMobility() {
		return geographicMobility;
	}
	public IrpfData setGeographicMobility(boolean geographicMobility) {
		this.geographicMobility = geographicMobility;
		return this;
	}
	public byte ensureDisability() {
		return getDisability() == null ? 0 : getDisability();
	}
}
