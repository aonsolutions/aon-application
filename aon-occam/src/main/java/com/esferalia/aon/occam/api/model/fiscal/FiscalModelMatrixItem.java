package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class FiscalModelMatrixItem implements Serializable {
	
	private static final long serialVersionUID = -3871892696016731326L;
	
	public static enum FiscalStatus {
		 MISSING
		,PENDING
		,FINISHED
	}

	private FiscalModel model;
	private int year;
	private Period period;
	private Administration administration;
	private FiscalStatus status;
	private int domainId;
	private String domainName;
	private String document;
	private String name;
	
	public FiscalModel getModel() {
		return model;
	}
	public FiscalModelMatrixItem setModel(FiscalModel model) {
		this.model = model;
		return this;
	}
	public int getYear() {
		return year;
	}
	public FiscalModelMatrixItem setYear(int year) {
		this.year = year;
		return this;
	}
	public Period getPeriod() {
		return period;
	}
	public FiscalModelMatrixItem setPeriod(Period period) {
		this.period = period;
		return this;
	}
	public Administration getAdministration() {
		return administration;
	}
	public FiscalModelMatrixItem setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	public FiscalStatus getStatus() {
		return status;
	}
	public FiscalModelMatrixItem setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}
	public int getDomainId() {
		return domainId;
	}
	public FiscalModelMatrixItem setDomainId(int domainId) {
		this.domainId = domainId;
		return this;
	}
	public String getDomainName() {
		return domainName;
	}
	public FiscalModelMatrixItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public FiscalModelMatrixItem setDocument(String document) {
		this.document = document;
		return this;
	}
	public String getName() {
		return name;
	}
	public FiscalModelMatrixItem setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String toString() {
		return model
			+ "\t - " +year
			+ " - " +period
			+ "\t - " +administration
			+ " - " +status
			+ " - " +domainId
			+ " - " +domainName
			+ " - " +document
			+ " - " +name;
	}
	
}