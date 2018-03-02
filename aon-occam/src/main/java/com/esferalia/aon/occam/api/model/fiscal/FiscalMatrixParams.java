package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Administration;

public class FiscalMatrixParams implements Serializable{

	private static final long serialVersionUID = 7741345914522007076L;
	
	private int year;
	private String model;
	private Administration administration;
	
	private boolean configuredVisible;
	private boolean madeModelsVisible;
	
	public int getYear() {
		return year;
	}
	public FiscalMatrixParams setYear(int year) {
		this.year = year;
		return this;
	}
	
	public String getModel() {
		return model;
	}
	public FiscalMatrixParams setModel(String model) {
		this.model = model;
		return this;
	}
	
	public Administration getAdministration() {
		return administration;
	}
	public FiscalMatrixParams setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	public boolean isConfiguredVisible() {
		return configuredVisible;
	}
	public FiscalMatrixParams setConfiguredVisible(boolean configuredVisible) {
		this.configuredVisible = configuredVisible;
		return this;
	}
	public boolean isMadeModelsVisible() {
		return madeModelsVisible;
	}
	public FiscalMatrixParams setMadeModelsVisible(boolean madeModelsVisible) {
		this.madeModelsVisible = madeModelsVisible;
		return this;
	}
	
	
}
