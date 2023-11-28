package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class FiscalMatrixParams implements Serializable{

	private static final long serialVersionUID = 7741345914522007076L;
	
	private int year;
	private FiscalModelType model;
	private Administration administration;
	private Integer scope;
	private String declared;
	
	private boolean configuredVisible;
	private boolean madeModelsVisible;
	
	private FiscalStatus status;
	private Period period;
	
	private boolean multiplePresentation;
	
	public int getYear() {
		return year;
	}
	public FiscalMatrixParams setYear(int year) {
		this.year = year;
		return this;
	}
	
	public FiscalModelType getModel() {
		return model;
	}
	public FiscalMatrixParams setModel(FiscalModelType model) {
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
	public Integer getScope() {
		return scope;
	}
	public FiscalMatrixParams setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
	public String getDeclared() {
		return declared;
	}
	public FiscalMatrixParams setDeclared(String declared) {
		this.declared = declared;
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
	
	public FiscalStatus getStatus() {
		return status;
	}
	public FiscalMatrixParams setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}
	
	public Period getPeriod() {
		return period;
	}
	public FiscalMatrixParams setPeriod(Period period) {
		this.period = period;
		return this;
	}
	
	public boolean isMultiplePresentation() {
		return multiplePresentation;
	}
	public FiscalMatrixParams setMultiplePresentation(boolean multiplePresentation) {
		this.multiplePresentation = multiplePresentation;
		return this;
	}
	
	public boolean isFiscalModelTypePresent() {
		return model != null; 
	}
	
	public boolean isMod390HFVisible() {
		return !isFiscalModelTypePresent() || model == FiscalModelType.M390_HF; 
	}
	public boolean accept(FiscalModelType modelType) {
		return (getModel() == null  || getModel() == modelType);
	}	
	
}
