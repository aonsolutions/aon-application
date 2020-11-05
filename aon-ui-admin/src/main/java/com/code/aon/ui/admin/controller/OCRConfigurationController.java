package com.code.aon.ui.admin.controller;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;

public class OCRConfigurationController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private boolean active;
	
	public void init() {
		setActive( AON_SOLUTIONS.isOCRActive(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), AonUtil.getRemoteUser()) );
	}
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	public void contract() {
		AON_SOLUTIONS.saveDomainApp(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), AonUtil.getRemoteUser(),
				new DomainApp()
					.setDomain(DomainManager.getCurrentDomain())
					.setApp(AonApp.OCR)
					.setActive(isActive())
				);
	}

	
}