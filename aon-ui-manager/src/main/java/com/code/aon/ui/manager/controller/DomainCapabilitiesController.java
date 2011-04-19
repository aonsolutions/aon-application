package com.code.aon.ui.manager.controller;

import static com.code.aon.ui.manager.controller.DomainController.DEFAULT_MAX_DOCUMENT_SIZE;
import static com.code.aon.ui.manager.controller.DomainController.DEFAULT_MAX_TOTAL_DOCUMENT_SIZE;
import static com.code.aon.ui.manager.controller.IManagerConstants.DEFAULT_SUBDOMAIN_SUFFIX;
import static com.code.aon.ui.manager.controller.IManagerConstants.DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.manager.controller.IManagerConstants.DOMAIN_USER_CONTROLLER_NAME;
import static com.code.aon.ui.manager.controller.IManagerConstants.MANAGER_CONTROLLER_NAME;
import static com.code.aon.ui.manager.controller.IManagerConstants.SHOW_DOMAIN_MANAGEMENT;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.manager.Domain;
import com.code.aon.manager.enumeration.DomainCapability;
import com.code.aon.ui.util.AonUtil;

public class DomainCapabilitiesController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainCapabilitiesController.class);
	
	private String beanName;
	
	private boolean isNew;
	
	private DataModel model;
	
	private DomainCapability to;
	
	private List<SelectItem> capabiilities;
	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}	

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public DomainCapability getTo() {
		return this.to;
	}
	
	public void setTo( DomainCapability capability ) {
		this.to = capability;
	}
	
	public DataModel getModel() {
		return this.model;
	}	
	
	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	private DomainController getDomainController() {
		return (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
	}	
	
	public void onInit(ActionEvent event) {
		this.model = new ListDataModel( getDomainController().getDomain().getCapabilities() );
		resetCapabilities();
		resetTo();
	}
	
	private boolean isShowDomainManagement() {
		String value = getManager().getConfig().getProperty(SHOW_DOMAIN_MANAGEMENT);
		return BooleanUtils.toBoolean(value);
	}
	
	public boolean isDeletable() {
		if ( getManager().isAdministrator() ) {
			return true;
		}
		return getTo() != DomainCapability.MULTI_DOMAIN; 
	}
	
	private void resetCapabilities() {
		Locale locale = AonUtil.getCurrentLocale();
		this.capabiilities = new LinkedList<SelectItem>();
		Domain domain = getDomainController().getDomain();
		List<DomainCapability> list = domain.getCapabilities();
		if (! isShowDomainManagement() ) {
			list.add( DomainCapability.MULTI_DOMAIN );
		}
		for (DomainCapability capability : DomainCapability.values()) {
			if (! list.contains(capability) ) {
				String name = capability.getName(locale);
				SelectItem item = new SelectItem(capability, name );
				this.capabiilities.add(item);						
			}
		}
	}		

	public List<SelectItem> getCapabilities() throws ManagerBeanException {
		return capabiilities;
	}		
	
	public void onSelect(ActionEvent event) {
		this.to = (DomainCapability) model.getRowData();
		setNew(false);
	}	

	public void onReset(ActionEvent event) {
		this.to = null;
		setNew(true);
		resetCapabilities();
		getManager().setTermsOfServiceAccepted(getManager().isAdministrator());
	}	

	private void resetTo() {
		this.to = null;
		setNew(false);
	}
		
	public void onCancel(ActionEvent event) {
		resetTo();
	}

	public void onAccept(ActionEvent event) {
		DomainController dc = getDomainController();
		Domain domain = dc.getDomain();
		List<DomainCapability> list = domain.getCapabilities();
		list.add( getTo() );
		domain.setCapabilities( list );
		try {
			capabilityChanged(domain, getTo(), false);			
			dc.getManagerBean().update( domain );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onAccept exception ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		this.model.setWrappedData( list );
		resetTo();
		resetCapabilities();
	}
	
	public void onRemove(ActionEvent event) {
		DomainController dc = getDomainController();
		Domain domain = dc.getDomain();
		List<DomainCapability> list = domain.getCapabilities();
		DomainCapability capability = (DomainCapability) model.getRowData(); 
		list.remove( capability );
		domain.setCapabilities( list );
		try {
			capabilityChanged(domain, capability, true);
			dc.getManagerBean().update( domain );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onRemove exception ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		this.model.setWrappedData( list );
		resetTo();
		resetCapabilities();
	}	


	private void capabilityChanged( Domain domain, DomainCapability capability, boolean removed ) throws ManagerBeanException {
		if ( DomainCapability.DOCUMENTAL == capability ) {
			getManager().getLogger().documental(domain);
			domain.setMaxDocumentSize(removed?null:DEFAULT_MAX_DOCUMENT_SIZE);
			domain.setMaxTotalDocumentSize(removed?null:DEFAULT_MAX_TOTAL_DOCUMENT_SIZE);
		} else if ( DomainCapability.MULTI_DOMAIN == capability ) {
			getManager().getLogger().multiDomain(domain);
			if ( StringUtils.isEmpty(domain.getSubDomainSuffix()) ) {
				domain.setSubDomainSuffix(DEFAULT_SUBDOMAIN_SUFFIX);
			}
		} else if ( DomainCapability.MULTI_USER == capability ) {
			if ( removed ) {
				DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);
				duc.deactiveUsers();				
			}
			getManager().getLogger().multiUser(domain);			
		}
	}
	
}
