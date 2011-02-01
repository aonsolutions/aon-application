package com.code.gbp.report;

import java.util.List;

import com.code.aon.common.ITransferObject;
import com.code.gbp.Supplier;

public class SupplierReport implements ITransferObject {

	private Supplier supplier;
	
	private List<ITransferObject> addInfos;
	
	private List<ITransferObject> contacts;
	
	private List<ITransferObject> campaigns;

	private List<ITransferObject> economicDatas;

	private List<ITransferObject> observations;
	
	private List<ITransferObject> contactPersons;

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<ITransferObject> getAddInfos() {
		return addInfos;
	}

	public void setAddInfos(List<ITransferObject> addInfos) {
		this.addInfos = addInfos;
	}

	public List<ITransferObject> getContacts() {
		return contacts;
	}

	public void setContacts(List<ITransferObject> contacts) {
		this.contacts = contacts;
	}

	public List<ITransferObject> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<ITransferObject> campaigns) {
		this.campaigns = campaigns;
	}

	public List<ITransferObject> getEconomicDatas() {
		return economicDatas;
	}

	public void setEconomicDatas(List<ITransferObject> economicDatas) {
		this.economicDatas = economicDatas;
	}

	public List<ITransferObject> getObservations() {
		return observations;
	}

	public void setObservations(List<ITransferObject> observations) {
		this.observations = observations;
	}

	public List<ITransferObject> getContactPersons() {
		return contactPersons;
	}

	public void setContactPersons(List<ITransferObject> contactPersons) {
		this.contactPersons = contactPersons;
	}
	
	
}
