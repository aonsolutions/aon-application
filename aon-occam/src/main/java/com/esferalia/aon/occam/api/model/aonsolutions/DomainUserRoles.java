package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.security.User;

public class DomainUserRoles implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Domain domain;
	User user;
	
	LinkedList<AonApp> domainApps;
	LinkedList<AonApp> parentDomainApps;
	LinkedList<Module> oldDomainModules;
	LinkedList<Module> oldParentDomainModules;
	
	LinkedList<AonRole> domainUserRoles;
	LinkedList<AonRole> parentDomainUserRoles;
	
	public DomainUserRoles() {
		super();
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public DomainUserRoles setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public User getUser() {
		return user;
	}
	
	public DomainUserRoles setUser(User user) {
		this.user = user;
		return this;
	}
	
	public LinkedList<AonApp> getDomainApps() {
		return domainApps;
	}
	
	public DomainUserRoles setDomainApps(LinkedList<AonApp> domainApps) {
		this.domainApps = domainApps;
		return this;
	}
	
	public LinkedList<AonApp> getParentDomainApps() {
		return parentDomainApps;
	}
	
	public DomainUserRoles setParentDomainApps(LinkedList<AonApp> parentDomainApps) {
		this.parentDomainApps = parentDomainApps;
		return this;
	}
	
	public LinkedList<AonRole> getDomainUserRoles() {
		return domainUserRoles;
	}
	
	public DomainUserRoles setDomainUserRoles(LinkedList<AonRole> domainUserRoles) {
		this.domainUserRoles = domainUserRoles;
		return this;
	}
	
	public LinkedList<AonRole> getParentDomainUserRoles() {
		return parentDomainUserRoles;
	}
	
	public DomainUserRoles setParentDomainUserRoles(LinkedList<AonRole> parentDomainUserRoles) {
		this.parentDomainUserRoles = parentDomainUserRoles;
		return this;
	}
	
	public LinkedList<Module> getOldDomainModules() {
		if(oldDomainModules == null) {
			oldDomainModules = new LinkedList<>();
		}
		return oldDomainModules;
	}

	public void setOldDomainModules(LinkedList<Module> oldDomainModules) {
		this.oldDomainModules = oldDomainModules;
	}

	public LinkedList<Module> getOldParentDomainModules() {
		if(oldParentDomainModules == null) {
			oldParentDomainModules = new LinkedList<>();
		}
		return oldParentDomainModules;
	}

	public void setOldParentDomainModules(LinkedList<Module> oldParentDomainModules) {
		this.oldParentDomainModules = oldParentDomainModules;
	}

	public Boolean isParentUser(){
		return getDomain().getParentId() != null && getDomain().getParentId().equals(getUser().getDomain());
	}
	
	private Boolean hasOldModule(Module module) {
		return getOldDomainModules().contains(module) || getOldParentDomainModules().contains(module);
	}
	
	private Boolean hasApp(AonApp aonApp) {
		return getDomainApps().contains(aonApp) || getParentDomainApps().contains(aonApp);
	}
	
	private Boolean hasRole(AonRole aonRole) {
		return getDomainUserRoles().contains(aonRole) 
			|| (isParentUser() && getParentDomainUserRoles().contains(aonRole));
	}
	
	private Boolean hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole role) {
		Boolean bool = false;
		for (com.esferalia.aon.occam.api.model.type.AonRole ar : getUser().getUserRoles()) {
			if(ar.equals(role)) {
				bool = true;
			}
		}
		return bool;
	}
	
	public Boolean isAdmin() {
		return  hasRole(AonRole.ADMIN); // && hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole.ADMIN);
	}
	
	public Boolean isAccounting() {	
		return hasApp(AonApp.ACCOUNTING) && (isAdmin() || hasRole(AonRole.ACCOUNTING));
	}
	
	public Boolean isAccountingManager() {
		return hasApp(AonApp.ACCOUNTING) && (isAdmin() || hasRole(AonRole.ACCOUNTING_MANAGER));
	}
	
	public Boolean isFiscal() {
		return hasApp(AonApp.FISCAL) && (isAdmin() || hasRole(AonRole.FISCAL));
	}
	
	public Boolean isFiscalManager() {
		return hasApp(AonApp.FISCAL) && (isAdmin() || hasRole(AonRole.FISCAL_MANAGER));
	}
	
	public Boolean isPayroll() {
		return hasApp(AonApp.PAYROLL) && (isAdmin() || hasRole(AonRole.PAYROLL));
	}
	
	public Boolean isPayrollPortal() {
		return hasApp(AonApp.PAYROLL) && (isAdmin() || hasRole(AonRole.PAYROLL_PORTAL));
	}
	
	public Boolean isPayrollManager() {
		return hasApp(AonApp.PAYROLL) && (isAdmin() || hasRole(AonRole.PAYROLL_MANAGER));
	}
	
	public Boolean isDocumental() {
		return hasApp(AonApp.DOCUMENTAL) && (isAdmin() || hasRole(AonRole.DOCUMENTAL));
	}
	
	public Boolean isDocumentalPortal() {
		return (hasApp(AonApp.DOCUMENTAL) && (isAdmin() || hasRole(AonRole.DOCUMENTAL_PORTAL))) 
				|| hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole.ADMIN)
				|| hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole.DOCUMENT);
	}
	
	public Boolean isDocumentalManager() {
		return ((hasOldModule(Module.DOCUMENT) || hasApp(AonApp.DOCUMENTAL))
			&& ( isAdmin() || hasRole(AonRole.DOCUMENTAL_MANAGER)))
				|| hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole.ADMIN) 
				||hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole.DOCUMENT_MANAGER);
	}
	
	public Boolean isComunica() {
		return hasApp(AonApp.COMUNICA) && (isAdmin() || hasRole(AonRole.COMUNICA));
	}
	
	public Boolean isComunicaPortal() {
		return hasApp(AonApp.COMUNICA) && (isAdmin() || hasRole(AonRole.COMUNICA_PORTAL));
	}
	
	public Boolean isComunicaManager() {
		return hasApp(AonApp.COMUNICA) && (isAdmin() || hasRole(AonRole.COMUNICA_MANAGER));
	}
	
	public Boolean isTimecontrol() {
		return hasApp(AonApp.TIMECONTROL) && (isAdmin() || hasRole(AonRole.TIMECONTROL));
	}
	
	public Boolean isTimecontrolPortal() {
		return hasApp(AonApp.TIMECONTROL) && (isAdmin() || hasRole(AonRole.TIMECONTROL_PORTAL));
	}
	
	public Boolean isTimecontrolManager() {
		return hasApp(AonApp.TIMECONTROL) && (isAdmin() || hasRole(AonRole.TIMECONTROL_MANAGER));
	}
	
	public Boolean isMessenger() {
		return hasApp(AonApp.MESSENGER) && (isAdmin() || hasRole(AonRole.MESSENGER));
	}
	
	public Boolean isMessengerManager() {
		return hasApp(AonApp.MESSENGER) && (isAdmin() || hasRole(AonRole.MESSENGER_MANAGER));
	}
	
	public Boolean isInvoice() {
		return hasApp(AonApp.INVOICE) && (isAdmin() || hasRole(AonRole.INVOICE));
	}
	
	public Boolean isInvoicePortal() {
		return hasApp(AonApp.INVOICE) && (isAdmin() || hasRole(AonRole.INVOICE_PORTAL));
	}
	
	public Boolean isInvoiceManager() {
		return hasApp(AonApp.INVOICE) && (isAdmin() || hasRole(AonRole.INVOICE_MANAGER));
	}
	
	public Boolean isManagement() {
		return hasApp(AonApp.MANAGEMENT) && (isAdmin() || hasRole(AonRole.MANAGEMENT));
	}
	
	public Boolean isManagementManager() {
		return hasApp(AonApp.MANAGEMENT) && (isAdmin() || hasRole(AonRole.MANAGEMENT_MANAGER));
	}
	
	public Boolean isAlma() {
		return hasApp(AonApp.ALMA) && (isAdmin() || hasRole(AonRole.ALMA));
	}
	public Boolean isOcr() {
		return hasApp(AonApp.OCR) && (isAdmin() || hasRole(AonRole.OCR));
	}
	
	public Boolean isBank() {
		return hasApp(AonApp.BANK) && (isAdmin() || hasRole(AonRole.BANK));
	}
	
	public Boolean isConvenios() {
		return hasApp(AonApp.CONVENIOS) && (isAdmin() || hasRole(AonRole.CONVENIOS));
	}
	public Boolean isAon() {
		return hasApp(AonApp.AIO) && (isAdmin() || hasRole(AonRole.AON));
	}
	public Boolean isBidoq() {
		return hasApp(AonApp.BIDOQ) && (isAdmin() || hasRole(AonRole.BIDOQ));
	}
	
	public Boolean isConfidential() {
		return hasRole(AonRole.CONFIDENTIALITY) || hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole.CONFIDENTIALITY);
	}
}
