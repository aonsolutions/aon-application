package com.code.aon.ui.registry.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.event.DomainLoookupListener;

public class CorporateIdentityController extends RegistryAttachController {

	private boolean showDomainLookup;
	
	private boolean massiveUpload;
	
	private RegistryAttachment lastAttachment;
	
	private IControllerListener domainLookupListener;

	public CorporateIdentityController() {
		this.domainLookupListener = new DomainLoookupListener();
		Integer parentDomainId = AdminUtil.getParentDomain(DomainManager.getCurrentDomain());
		this.showDomainLookup = (parentDomainId == null);
	}
	
	public boolean isShowDomainLookup() {
		return showDomainLookup;
	}

	public IControllerListener getDomainLookupListener() {
		return domainLookupListener;
	}

	public boolean isMassiveUpload() {
		return massiveUpload;
	}

	public void setMassiveUpload(boolean massiveUpload) {
		this.massiveUpload = massiveUpload;
	}

	public RegistryAttachment getLastAttachment() {
		return lastAttachment;
	}

	public void setLastAttachment(RegistryAttachment lastAttachment) {
		this.lastAttachment = lastAttachment;
	}

	public void masiveUpload( ActionEvent event ) {
		accept(event);
		setLastAttachment( (RegistryAttachment) getTo() );
		onReset(event);
	}
	
}