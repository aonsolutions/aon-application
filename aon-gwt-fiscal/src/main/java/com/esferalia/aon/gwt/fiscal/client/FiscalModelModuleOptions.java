package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.client.AonModuleCallback;
import com.esferalia.aon.gwt.common.shared.AonData;

public class FiscalModelModuleOptions<T> extends  ModuleOptions<FiscalModelModuleOptions<T>> {

	private static final long serialVersionUID = -8275746757276688771L;
	private Integer fiscalModelId;
	private AonModuleCallback<T> externalCallback;
	private AonData aonData;
	private boolean embedded = false;
	private boolean backButtonVisible = false;

	public Integer getFiscalModelId() {
		return fiscalModelId;
	}

	public FiscalModelModuleOptions<T> setFiscalModelId(Integer fiscalModelId) {
		this.fiscalModelId = fiscalModelId;
		return this;
	}

	public AonModuleCallback<T> getExternalCallback() {
		return externalCallback;
	}

	public boolean hasExternalCallback() {
		return getExternalCallback() != null;
	}

	public FiscalModelModuleOptions<T> setExternalCallback(AonModuleCallback<T> externalCallback) {
		this.externalCallback = externalCallback;
		return this;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	public FiscalModelModuleOptions<T> setAonData(AonData aonData) {
		this.aonData = aonData;
		return this;
	}

	public boolean isEmbedded() {
		return embedded;
	}
	public FiscalModelModuleOptions<T> setEmbedded(boolean embedded) {
		this.embedded = embedded;
		return this;
	}
	
	public boolean isBackButtonVisible() {
		return backButtonVisible;
	}
	public FiscalModelModuleOptions<T> setBackButtonVisible(boolean backButtonVisible) {
		this.backButtonVisible = backButtonVisible;
		return this;
	}

}
