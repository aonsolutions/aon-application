package com.code.aon.ui.admin.controller;

import java.io.Serializable;
import java.util.HashMap;

import com.code.aon.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;

public class MarketplaceController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Domain domain;
	private String login;
	private HashMap<AonApp, DomainApp> domainApps = new HashMap<AonApp, DomainApp>();

	private boolean suitePortal;
	
	public void init() {
		setLogin(AonUtil.getRemoteUser());
		setDomain(AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), getLogin()));
		
		AON_SOLUTIONS.getDomainApp(getDomain().getName(), getDomain().getId(), getLogin(), f -> f.getDomainProperty().eq(getDomain().getId()))
		.forEach(r -> {
			domainApps.put(r.getApp(), r);
		});
		
		this.suitePortal = AON.getDomainModules(getDomain().getName(), getDomain().getId(), getLogin()).filter(f -> Module.SUITE_PORTAL.equals(f)).count() > 0;
	}

	public boolean isActive(AonApp app) {
		return domainApps.containsKey(app)&& domainApps.get(app).isActive();
	}
	
	public void setActive(AonApp app, boolean active) {
		if(domainApps.containsKey(app)) 
			domainApps.get(app).setActive(active);
		else domainApps.put(app, new DomainApp()
				.setDomain(DomainManager.getCurrentDomain())
				.setApp(app)
				.setActive(active));
	}
	
	public boolean isSuitePortal() {
		return suitePortal;
	}
	
	public void setSuitePortal(boolean suitePortal) {
		this.suitePortal = suitePortal;
	}

	public void contract() {
		domainApps.entrySet().stream().forEach(r -> {
			AON_SOLUTIONS.saveDomainApp(getDomain().getName(), getDomain().getId(), getLogin(), r.getValue());
		});
	}

	public Domain getDomain() {
		return domain;
	}
	
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}

	public boolean isInvoiceActive() {
		return isActive(AonApp.INVOICE);
	}
	
	public boolean isDocumentalActive() {
		return isActive(AonApp.DOCUMENTAL);
	}
	
	public boolean isMessengerActive() {
		return isActive(AonApp.MESSENGER);
	}
	
	public boolean isAccountngActive() {
		return isActive(AonApp.ACCOUNTING);
	}
	
	public boolean isFiscalActive() {
		return isActive(AonApp.FISCAL);
	}
	
	public boolean isPayrollActive() {
		return isActive(AonApp.PAYROLL);
	}
	
	public boolean isOcrActive() {
		return isActive(AonApp.OCR);
	}
	
	public boolean isAioActive() {
		return isActive(AonApp.AIO);
	}
	
	public boolean isAlmaActive() {
		return isActive(AonApp.ALMA);
	}
	
	public boolean isComunicaActive() {
		return isActive(AonApp.COMUNICA);
	}
	
	public boolean isBidoqActive() {
		return isActive(AonApp.BIDOQ);
	}
	
	public boolean isConveniosActive() {
		return isActive(AonApp.CONVENIOS);
	}
	
	public boolean isBankActive() {
		return isActive(AonApp.BANK);
	}
	
	public boolean isTimecontrolActive() {
		return isActive(AonApp.TIMECONTROL);
	}
	
	public boolean isManagementActive() {
		return isActive(AonApp.MANAGEMENT);
	}
	
	public void setInvoiceActive(boolean active) {
		setActive(AonApp.INVOICE, active);
	}
	
	public void setDocumentalActive(boolean active) {
		setActive(AonApp.DOCUMENTAL, active);
	}
	
	public void setMessengerActive(boolean active) {
		setActive(AonApp.MESSENGER, active);
	}
	
	public void setAccountngActive(boolean active) {
		setActive(AonApp.ACCOUNTING, active);
	}
	
	public void setFiscalActive(boolean active) {
		setActive(AonApp.FISCAL, active);
	}
	
	public void setPayrollActive(boolean active) {
		setActive(AonApp.PAYROLL, active);
	}
	
	public void setOcrActive(boolean active) {
		setActive(AonApp.OCR, active);
	}
	
	public void setAioActive(boolean active) {
		setActive(AonApp.AIO, active);
	}
	
	public void setAlmaActive(boolean active) {
		setActive(AonApp.ALMA, active);
	}
	
	public void setComunicaActive(boolean active) {
		setActive(AonApp.COMUNICA, active);
	}
	
	public void setBidoqActive(boolean active) {
		setActive(AonApp.BIDOQ, active);
	}
	
	public void setConveniosActive(boolean active) {
		setActive(AonApp.CONVENIOS, active);
	}
	
	public void setBankActive(boolean active) {
		setActive(AonApp.BANK, active);
	}
	
	public void setTimecontrolActive(boolean active) {
		setActive(AonApp.TIMECONTROL, active);
	}
	
	public void isManagementActive(boolean active) {
		setActive(AonApp.MANAGEMENT, active);
	}
	
}