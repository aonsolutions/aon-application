package com.code.aon.aio.controller;

import java.io.Serializable;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.User;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;

public class InvoiceCommunicationConfigurationController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private InvoiceCommunicationConfiguration icc;
	
	public InvoiceCommunicationConfiguration getIcc() {
		if(icc == null) {
			loadInvoiceCommunicationConfiguration();
		}
		return icc;
	}
	
	public void setIcc(InvoiceCommunicationConfiguration icc) {
		this.icc = icc;
	}
	
	private void loadInvoiceCommunicationConfiguration() {		
		this.icc = AON.getInvoiceCommunicationConfiguration(getOccam());
	}
	
	public boolean isNoSif() {
		return getIcc().isNoSif();
	}
	
	public boolean hasCommunication() {
		return getIcc().hasCommunication();
	}
	
	private Occam getOccam() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		User user = UserUtils.getInstance().getLoggedUser();
		return new Occam()
			.setDomain(domainId)
			.setDomainName(domainName)
			.setUser(user.getLogin());
	}
	
	
	
}
