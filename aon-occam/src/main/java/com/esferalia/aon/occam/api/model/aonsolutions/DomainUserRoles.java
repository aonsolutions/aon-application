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
	
	public Boolean isDev() {
		return  hasRole(AonRole.DEV);
	}
	
	// ACCOUNTING - CONTABILIDAD
	
	public boolean hasAccounting() {
		return hasApp(AonApp.PACK_SUITE) || hasApp(AonApp.PACK_FISCAL_ACCOUNTING) || hasApp(AonApp.ACCOUNTING);
	}
	
	public Boolean isAccounting() {	
		return hasAccounting() && (isAdmin() || hasRole(AonRole.ACCOUNTING));
	}
	
	public Boolean isAccountingManager() {
		return hasAccounting() && (isAdmin() || hasRole(AonRole.ACCOUNTING_MANAGER));
	}
	
	// FISCAL - FISCAL
	
	public boolean hasFiscal() {
		return hasApp(AonApp.PACK_SUITE) || hasApp(AonApp.PACK_FISCAL_ACCOUNTING) || hasApp(AonApp.FISCAL);
	}
	
	public Boolean isFiscal() {
		return hasFiscal() && (isAdmin() || hasRole(AonRole.FISCAL));
	}
	
	public Boolean isFiscalManager() {
		return hasFiscal() && (isAdmin() || hasRole(AonRole.FISCAL_MANAGER));
	}
	
	// PAYROLL - LABORAL
	
	public boolean hasPayroll() {
		return hasApp(AonApp.PACK_SUITE) || hasApp(AonApp.PACK_PAYROLL) || hasApp(AonApp.PAYROLL);
	}
	
	public Boolean isPayroll() {
		return hasPayroll() && (isAdmin() || hasRole(AonRole.PAYROLL));
	}
	
	public Boolean isPayrollPortal() {
		return hasPayroll() && (isAdmin() || hasRole(AonRole.PAYROLL_PORTAL));
	}
	
	public Boolean isPayrollManager() {
		return hasPayroll() && (isAdmin() || hasRole(AonRole.PAYROLL_MANAGER));
	}
	
	// DOCUMENTAL - DOCUMENTAL
	
	public boolean hasDocumental() {
		return hasApp(AonApp.PACK_SUITE) || hasApp(AonApp.PACK_PORTAL) || hasApp(AonApp.DOCUMENTAL);
	}
	
	public Boolean isDocumental() {
		return hasDocumental() && (isAdmin() || hasRole(AonRole.DOCUMENTAL));
	}
	
	public Boolean isDocumentalPortal() {
		return (hasDocumental() && (isAdmin() || hasRole(AonRole.DOCUMENTAL_PORTAL))) 
				|| hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole.ADMIN)
				|| hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole.DOCUMENT);
	}
	
	public Boolean isDocumentalManager() {
		return ((hasOldModule(Module.DOCUMENT) || hasDocumental())
			&& ( isAdmin() || hasRole(AonRole.DOCUMENTAL_MANAGER)))
				|| hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole.ADMIN) 
				||hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole.DOCUMENT_MANAGER);
	}
	
	// COMUNIC@ - COMUNIC@
	
	public boolean hasComunica() {
		return hasApp(AonApp.PACK_SUITE) || hasApp(AonApp.PACK_PORTAL) || hasApp(AonApp.COMUNICA);
	}
	
	public Boolean isComunica() {
		return hasComunica() && (isAdmin() || hasRole(AonRole.COMUNICA));
	}
	
	public Boolean isComunicaPortal() {
		return hasComunica() && (isAdmin() || hasRole(AonRole.COMUNICA_PORTAL));
	}
	
	public Boolean isComunicaManager() {
		return hasComunica() && (isAdmin() || hasRole(AonRole.COMUNICA_MANAGER));
	}
	
	// TIMECONTROL - CONTROL DE HORARIO
	
	public boolean hasTimecontrol() {
		return hasApp(AonApp.PACK_SUITE) || hasApp(AonApp.PACK_PORTAL) || hasApp(AonApp.TIMECONTROL) || hasComunica();
	}
	
	public Boolean isTimecontrol() {
		return hasTimecontrol() && (isAdmin() || hasRole(AonRole.TIMECONTROL));
	}
	
	public Boolean isTimecontrolPortal() {
		return hasTimecontrol() && (isAdmin() || hasRole(AonRole.TIMECONTROL_PORTAL));
	}
	
	public Boolean isTimecontrolManager() {
		return hasTimecontrol() && (isAdmin() || hasRole(AonRole.TIMECONTROL_MANAGER));
	}
	
	// MESSENGER - MENSAJERÍA
	
	public boolean hasMessenger() {
		return hasApp(AonApp.PACK_SUITE) || hasApp(AonApp.PACK_PORTAL) || hasApp(AonApp.MESSENGER);
	}
	
	public Boolean isMessenger() {
		return hasMessenger() && (isAdmin() || hasRole(AonRole.MESSENGER));
	}
	
	public Boolean isMessengerManager() {
		return hasMessenger() && (isAdmin() || hasRole(AonRole.MESSENGER_MANAGER));
	}
	
	// INVOICE - FACTURAS
	
	public boolean hasInvoice() {
		return hasApp(AonApp.PACK_SUITE) || hasApp(AonApp.PACK_PORTAL) || hasApp(AonApp.INVOICE);
	}
	
	public Boolean isInvoice() {
		return hasInvoice() && (isAdmin() || hasRole(AonRole.INVOICE));
	}
	
	public Boolean isInvoicePortal() {
		return hasInvoice() && (isAdmin() || hasRole(AonRole.INVOICE_PORTAL));
	}
	
	public Boolean isInvoiceManager() {
		return hasInvoice() && (isAdmin() || hasRole(AonRole.INVOICE_MANAGER));
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
	
	// OCR
	
	public Boolean hasOcr() {
		return hasApp(AonApp.OCR);
	}
	
	public Boolean isOcr() {
		return hasOcr() && (isAdmin() || hasRole(AonRole.OCR));
	}
	
	public Boolean isBank() {
		return hasApp(AonApp.BANK) && (isAdmin() || hasRole(AonRole.BANK));
	}
	
	public Boolean isConvenios() {
		return hasApp(AonApp.CONVENIOS); // TODO añadir -> && (isAdmin() || hasRole(AonRole.CONVENIOS));
	}
	
	public Boolean isAon() {
		return hasApp(AonApp.AIO) && (isAdmin() || hasRole(AonRole.AIO));
	}
	
	public Boolean isBidoq() {
		return hasApp(AonApp.BIDOQ) && (isAdmin() || hasRole(AonRole.BIDOQ));
	}
	
	public Boolean isSelfconta() {
		return hasApp(AonApp.SELFCONTA) && (isAdmin() || hasRole(AonRole.SELFCONTA));
	}
	
	public Boolean isConfidential() {
		return hasRole(AonRole.CONFIDENTIALITY) || hasOldRole(com.esferalia.aon.occam.api.model.type.AonRole.CONFIDENTIALITY);
	}
	
	public boolean isAlpha() {
		return hasRole(AonRole.ALPHA);
	}

	public boolean isBeta() {
		return this.hasRole(AonRole.BETA);
	}

	public boolean isEmployee() {
		return this.hasRole(AonRole.EMPLOYEE);
	}

	public boolean isEnterprise() {
		return this.hasRole(AonRole.ENTERPRISE);
	}
}
