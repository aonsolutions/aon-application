package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.shared.AonData;

public class FiscalModelModuleOptions extends  ModuleOptions<FiscalModelModuleOptions> {

	private static final long serialVersionUID = -8275746757276688771L;
	private Integer fiscalModelId;
	private ModuleCallback externalCallback;
	private AonData aonData;
	private boolean embedded = false;
	private boolean backButtonVisible = true;

	public Integer getFiscalModelId() {
		return fiscalModelId;
	}

	public FiscalModelModuleOptions setFiscalModelId(Integer fiscalModelId) {
		this.fiscalModelId = fiscalModelId;
		return this;
	}

	public ModuleCallback getExternalCallback() {
		return externalCallback;
	}

	public boolean hasExternalCallback() {
		return getExternalCallback() != null;
	}

	public FiscalModelModuleOptions setExternalCallback(ModuleCallback externalCallback) {
		this.externalCallback = externalCallback;
		return this;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	public FiscalModelModuleOptions setAonData(AonData aonData) {
		this.aonData = aonData;
		return this;
	}

	public boolean isEmbedded() {
		return embedded;
	}
	public FiscalModelModuleOptions setEmbedded(boolean embedded) {
		this.embedded = embedded;
		return this;
	}
	
	public boolean isBackButtonVisible() {
		return backButtonVisible;
	}
	public FiscalModelModuleOptions setBackButtonVisible(boolean backButtonVisible) {
		this.backButtonVisible = backButtonVisible;
		return this;
	}

}
