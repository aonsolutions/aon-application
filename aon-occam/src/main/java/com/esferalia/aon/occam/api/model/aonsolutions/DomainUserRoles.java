
package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.OldAonRole;

public class DomainUserRoles implements Serializable {

	private static final long serialVersionUID = 1L;
	
	Domain domain;
	Domain parentDomain;
	User user;
	
	private List<AonApp> domainApps;
	private List<AonApp> parentDomainApps;
	private List<Module> oldDomainModules;
	private List<Module> oldParentDomainModules;
	
	private List<AonRole> domainUserRoles;
	private List<AonRole> parentDomainUserRoles;
	private boolean domainPayer;
	private boolean isTrial;
	
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
	
	public Domain getParentDomain() {
		if(parentDomain == null) {
			parentDomain = new Domain();
		}
		return parentDomain;
	}
	
	public DomainUserRoles setParentDomain(Domain parentDomain) {
		this.parentDomain = parentDomain;
		return this;
	}
	
	public User getUser() {
		return user;
	}
	
	public DomainUserRoles setUser(User user) {
		this.user = user;
		return this;
	}
	
	public boolean isDomainPayer() {
		return domainPayer;
	}
	
	public DomainUserRoles setDomainPayer(boolean domainPayer) {
		this.domainPayer = domainPayer;
		return this;
	}
	
	public boolean isTrial() {
		return isTrial;
	}
	
	public DomainUserRoles setTrial(boolean isTrial) {
		this.isTrial = isTrial;
		return this;
	}
	
	public List<AonApp> getDomainApps() {
		if(domainApps == null) {
			this.domainApps = new LinkedList<>();
		}
		return domainApps;
	}
	
	public DomainUserRoles setDomainApps(List<AonApp> domainApps) {
		this.domainApps = domainApps;
		return this;
	}
	
	public List<AonApp> getParentDomainApps() {
		if(parentDomainApps == null) {
			this.parentDomainApps = new LinkedList<>();
		}
		return parentDomainApps;
	}
	
	public DomainUserRoles setParentDomainApps(List<AonApp> parentDomainApps) {
		this.parentDomainApps = parentDomainApps;
		return this;
	}
	
	public List<AonRole> getDomainUserRoles() {
		if(domainUserRoles == null) {
			this.domainUserRoles = new LinkedList<>();
		}
		return domainUserRoles;
	}
	
	public DomainUserRoles setDomainUserRoles(List<AonRole> domainUserRoles) {
		this.domainUserRoles = domainUserRoles;
		return this;
	}
	
	public List<AonRole> getParentDomainUserRoles() {
		if(parentDomainUserRoles == null) {
			this.parentDomainUserRoles = new LinkedList<>();
		}
		return parentDomainUserRoles;
	}
	
	public DomainUserRoles setParentDomainUserRoles(List<AonRole> parentDomainUserRoles) {
		this.parentDomainUserRoles = parentDomainUserRoles;
		return this;
	}
	
	public List<Module> getOldDomainModules() {
		if(oldDomainModules == null) {
			oldDomainModules = new LinkedList<>();
		}
		return oldDomainModules;
	}

	public DomainUserRoles setOldDomainModules(List<Module> oldDomainModules) {
		this.oldDomainModules = oldDomainModules;
		return this;
	}

	public List<Module> getOldParentDomainModules() {
		if(oldParentDomainModules == null) {
			oldParentDomainModules = new LinkedList<>();
		}
		return oldParentDomainModules;
	}

	public DomainUserRoles setOldParentDomainModules(List<Module> oldParentDomainModules) {
		this.oldParentDomainModules = oldParentDomainModules;
		return this;
	}

	public boolean isParentUser(){
		return getDomain().getParentId() != null && getDomain().getParentId().equals(getUser().getDomain());
	}
	
	public boolean isEnterpriseChild() {
		return getDomain().isChild() && !getParentDomain().isConsultancy();
	}
	
	private boolean hasOldModule(Module module) {
		return getOldDomainModules().contains(module);
	}
	
	private boolean hasParentOldModule(Module module) {
		return getOldParentDomainModules().contains(module);
	}
	
	public boolean hasApp(AonApp aonApp) {
		return getDomainApps().contains(aonApp);
	}
	
	public boolean hasParentApp(AonApp aonApp) {
		return getParentDomainApps().contains(aonApp);
	}
	
	private boolean hasRole(AonRole aonRole) {
		return getDomainUserRoles().contains(aonRole) 
			|| (isParentUser() && getParentDomainUserRoles().contains(aonRole));
	}
	
	private boolean hasOldRole(OldAonRole role) {
		if ( getUser().getUserRoles() == null )
			return false;

		Boolean bool = false;
		for (OldAonRole ar : getUser().getUserRoles()) {
			if(ar.equals(role)) {
				bool = true;
			}
		}
		return bool;
	}
	
	public boolean isAdmin() {
		return  hasRole(AonRole.ADMIN) || hasOldRole(OldAonRole.ADMIN);
	}
	
	public boolean isDev() {
		return  hasRole(AonRole.DEV);
	}
	
	public boolean isConsole() {
		return  hasRole(AonRole.CONSOLE);
	}
	
	// ACCOUNTING - CONTABILIDAD
	
	public boolean hasAccounting() {
		return hasApp(AonApp.ACCOUNTING) || hasOldModule(Module.ACCOUNTING);
	}
	
	public boolean hasParentAccounting() {
		return hasParentApp(AonApp.ACCOUNTING) || hasParentOldModule(Module.ACCOUNTING);
	}
	
	public boolean isAccounting() {	
		return (hasAccounting() || ((isParentUser() || isEnterpriseChild()) && hasParentAccounting()))
			&& (isAdmin() || hasRole(AonRole.ACCOUNTING) 
				|| hasOldRole(OldAonRole.ACCOUNTING)
				|| hasOldRole(OldAonRole.ACCOUNTING_MANAGER));
	}
	
	public boolean isAccountingManager() {
		return (hasAccounting() || ((isParentUser() || isEnterpriseChild()) && hasParentAccounting()))
			&& (isAdmin() || hasRole(AonRole.ACCOUNTING_MANAGER)
				|| hasOldRole(OldAonRole.ACCOUNTING_MANAGER));
	}
	
	// FISCAL - FISCAL
	
	public boolean hasFiscal() {
		return hasApp(AonApp.FISCAL) || hasOldModule(Module.FISCAL);
	}
	
	public boolean hasParentFiscal() {
		return hasParentApp(AonApp.FISCAL) || hasParentOldModule(Module.FISCAL);
	}
	
	public boolean isFiscal() {
		return (hasFiscal() || ((isParentUser() || isEnterpriseChild()) && hasParentFiscal()))
			&& (isAdmin() || hasRole(AonRole.FISCAL)
				|| hasOldRole(OldAonRole.FISCAL));
	}
	
	public boolean isFiscalManager() {
		return (hasFiscal() || ((isParentUser() || isEnterpriseChild()) && hasParentFiscal()) )
			&& (isAdmin() || hasRole(AonRole.FISCAL_MANAGER)
				|| hasOldRole(OldAonRole.FISCAL));
	}
	
	// PAYROLL - LABORAL
	
	public boolean hasPayroll() {
		return hasApp(AonApp.PAYROLL) 
			|| hasOldModule(Module.PAYROLL) || hasOldModule(Module.PAYROLL_PORTAL);
	}
	
	public boolean hasParentPayroll() {
		return hasParentApp(AonApp.PAYROLL) || hasParentOldModule(Module.PAYROLL);
	}
	
	public boolean isPayroll() {
		return (hasPayroll() || ((isParentUser() || isEnterpriseChild()) && hasParentPayroll()))
			&& (isAdmin() || hasRole(AonRole.PAYROLL)
				|| hasOldRole(OldAonRole.PAYROLL));
	}
	
	public boolean isPayrollPortal() {
		return (hasPayroll() || ((isParentUser() || isEnterpriseChild()) && hasParentPayroll()))
			&& (isAdmin() || hasRole(AonRole.PAYROLL_PORTAL)
				|| hasOldRole(OldAonRole.PAYROLL));
	}
	
	public boolean isPayrollManager() {
		return (hasPayroll() || ((isParentUser() || isEnterpriseChild()) && hasParentPayroll()))
			&& (isAdmin() || hasRole(AonRole.PAYROLL_MANAGER)
				|| hasOldRole(OldAonRole.PAYROLL));
	}
	
	// DOCUMENTAL - DOCUMENTAL
	
	public boolean hasDocumental() {
		return hasApp(AonApp.DOCUMENTAL)
				|| hasOldModule(Module.DOCUMENT) || hasOldModule(Module.DOCUMENT_PORTAL);
	}
	
	public boolean hasParentDocumental() {
		return hasParentApp(AonApp.DOCUMENTAL)
				|| hasParentOldModule(Module.DOCUMENT) 
				|| hasParentOldModule(Module.DOCUMENT_PORTAL);
	}
	
	public boolean isDocumental() {
		return (hasDocumental() || ((isParentUser() || isEnterpriseChild()) && hasParentDocumental()))
			&& (isAdmin() || hasRole(AonRole.DOCUMENTAL)
				|| hasOldRole(OldAonRole.DOCUMENT)
				|| hasOldRole(OldAonRole.DOCUMENT_MANAGER));
	}
	
	public boolean isDocumentalPortal() {
		return (hasDocumental() || ((isParentUser() || isEnterpriseChild()) && hasParentDocumental()))
			&& (isAdmin() || hasRole(AonRole.DOCUMENTAL_PORTAL)
				|| hasOldRole(OldAonRole.DOCUMENT));
	}
	
	public boolean isDocumentalManager() {
		return (hasDocumental() || ((isParentUser() || isEnterpriseChild()) && hasParentDocumental()))
			&& ( isAdmin() || hasRole(AonRole.DOCUMENTAL_MANAGER) 
				|| hasOldRole(OldAonRole.DOCUMENT_MANAGER));
	}
	
	// COMMERCIAL
	
	public boolean hasCommercial() {
		return hasApp(AonApp.COMMERCIAL) || hasOldModule(Module.CRM);
	}
	
	public boolean hasParentCommercial() {
		return hasParentApp(AonApp.COMMERCIAL) || hasParentOldModule(Module.CRM);
	}
	
	public boolean isCommercial() {
		return (hasCommercial() || ((isParentUser() || isEnterpriseChild()) && hasParentCommercial()))
			&& (isAdmin() || hasRole(AonRole.COMMERCIAL) 
				|| hasOldRole(OldAonRole.COMMERCIAL));
	}
	
	// WAREHOUSE
	
	public boolean hasWarehouse() {
		return hasApp(AonApp.WAREHOUSE) || hasOldModule(Module.WAREHOUSE);
	}
	
	public boolean hasParentWarehouse() {
		return hasParentApp(AonApp.WAREHOUSE) || hasParentOldModule(Module.WAREHOUSE);
	}
	
	public boolean isWarehouse() {
		return (hasWarehouse() || ((isParentUser() || isEnterpriseChild()) && hasParentWarehouse()))
			&& (isAdmin() || hasRole(AonRole.WAREHOUSE) 
				|| hasOldRole(OldAonRole.WAREHOUSE));
	}
	
	// COMUNIC@ - COMUNIC@
	
	public boolean hasComunica() {
		return hasApp(AonApp.COMUNICA) || hasOldModule(Module.COMUNICA);
	}
	
	public boolean hasParentComunica() {
		return hasParentApp(AonApp.COMUNICA) || hasParentOldModule(Module.COMUNICA);
	}
	
	public boolean isComunica() {
		return (hasComunica() || ((isParentUser() || isEnterpriseChild()) && hasParentComunica()))
			&& (isAdmin() || hasRole(AonRole.COMUNICA));
	}
	
	public boolean isComunicaPortal() {
		return (hasComunica() || ((isParentUser() || isEnterpriseChild()) && hasParentComunica()))
			&& (isAdmin() || hasRole(AonRole.COMUNICA_PORTAL));
	}
	
	public boolean isComunicaManager() {
		return (hasComunica() || ((isParentUser() || isEnterpriseChild()) && hasParentComunica()))
			&& (isAdmin() || hasRole(AonRole.COMUNICA_MANAGER));
	}
	
	// TIMECONTROL - CONTROL DE HORARIO
	
	public boolean hasTimecontrol() {
		return hasApp(AonApp.TIMECONTROL);
	}
	
	public boolean hasParentTimecontrol() {
		return hasParentApp(AonApp.TIMECONTROL);
	}
	
	public boolean isTimecontrol() {
		return (hasTimecontrol() || ((isParentUser() || isEnterpriseChild()) && hasParentTimecontrol()))
			&& (isAdmin() || hasRole(AonRole.TIMECONTROL));
	}
	
	public boolean isTimecontrolPortal() {
		return (hasTimecontrol() || ((isParentUser() || isEnterpriseChild()) && hasParentTimecontrol()))
			&& (isAdmin() || hasRole(AonRole.TIMECONTROL_PORTAL));
	}
	
	public boolean isTimecontrolManager() {
		return (hasTimecontrol() || ((isParentUser() || isEnterpriseChild()) && hasParentTimecontrol()))
			&& (isAdmin() || hasRole(AonRole.TIMECONTROL_MANAGER));
	}
	
	// MESSENGER - MENSAJERÍA
	
	public boolean hasMessenger() {
		return hasApp(AonApp.MESSENGER) || hasOldModule(Module.CALL_CENTER);
	}
	
	public boolean hasParentMessenger() {
		return hasParentApp(AonApp.MESSENGER) || hasParentOldModule(Module.CALL_CENTER);
	}
	
	public boolean isMessenger() {
		return (hasMessenger() || ((isParentUser() || isEnterpriseChild()) && hasParentMessenger()))
			&& (isAdmin() || hasRole(AonRole.MESSENGER));
	}
	
	public boolean isMessengerManager() {
		return (hasMessenger() || ((isParentUser() || isEnterpriseChild()) && hasParentMessenger()))
			&& (isAdmin() || hasRole(AonRole.MESSENGER_MANAGER));
	}

	// NOTES - NOTAS
	
	public boolean hasNotes() {
		return true;
	}

	public boolean isNotes() {
		return true;
	}
	
	// BASIC MANAGEMENT
	
	public boolean hasBasicManagement() {
		return hasApp(AonApp.BASIC_MANAGEMENT) || hasOldModule(Module.AON_FINANCE);
	}
	
	public boolean hasStandarManagement() {
		return hasApp(AonApp.STANDAR_MANAGEMENT) || hasOldModule(Module.AON_ONE);
	}
	
	public boolean hasProfessionalManagement() {
		return hasApp(AonApp.PROFESSIONAL_MANAGEMENT) || hasOldProfessionalManagement();
	}
	
	public boolean hasOldProfessionalManagement() {
		return !hasOldModule(Module.AON_FINANCE) 
			&& !hasOldModule(Module.AON_ONE) 
		    && !hasApp(AonApp.BASIC_MANAGEMENT);
	}
	
	// INVOICE - FACTURAS
	
	public boolean hasInvoice() {
		return hasApp(AonApp.INVOICE) || hasOldModule(Module.AON_FINANCE)
			|| hasOldModule(Module.FINANCE_PORTAL) || hasOldModule(Module.MANAGEMENT)
			|| hasOldModule(Module.AON_ONE);
	}
	
	public boolean hasParentInvoice() {
		return hasParentApp(AonApp.INVOICE) || hasParentOldModule(Module.AON_FINANCE)
			|| hasParentOldModule(Module.FINANCE_PORTAL) || hasParentOldModule(Module.MANAGEMENT)
			|| hasOldModule(Module.AON_ONE);
	}
	
	public boolean isInvoice() {
		return (hasInvoice() || ((isParentUser() || isEnterpriseChild()) && hasParentInvoice()))
			&& ((isAdmin() || hasRole(AonRole.INVOICE))
				|| isOldManagementRoles());
	}
	
	public boolean isInvoicePortal() {
		return (hasInvoice() || ((isParentUser() || isEnterpriseChild()) && hasParentInvoice()))
			&& ((isAdmin() || hasRole(AonRole.INVOICE_PORTAL))
				|| isOldManagementRoles());
	}
	
	public boolean isInvoiceManager() {
		return (hasInvoice() || ((isParentUser() || isEnterpriseChild()) && hasParentInvoice()))
			&& ((isAdmin() || hasRole(AonRole.INVOICE_MANAGER))
				|| isOldManagementRoles());
	}
	
	private boolean isOldManagementRoles() {
		return hasOldRole(OldAonRole.SALE)
		|| hasOldRole(OldAonRole.PURCHASE)
		|| hasOldRole(OldAonRole.FINANCE);
	}
	
	public boolean hasManagement() {
		return hasOldModule(Module.AON_FINANCE) || hasOldModule(Module.AON_ONE)
				|| hasOldModule(Module.MANAGEMENT) || hasApp(AonApp.MANAGEMENT);
	}
	
	public boolean isManagement() {
		return hasManagement() && (isAdmin() || hasRole(AonRole.MANAGEMENT));
	}
	
	public boolean isManagementManager() {
		return hasManagement() && (isAdmin() || hasRole(AonRole.MANAGEMENT_MANAGER));
	}
	
	public boolean isAlma() {
		return hasApp(AonApp.ALMA) && (isAdmin() || hasRole(AonRole.ALMA));
	}
	
	// OCR
	
	public boolean hasOcr() {
		return hasApp(AonApp.OCR);
	}
	
	public boolean hasParentOcr() {
		return hasParentApp(AonApp.OCR);
	}
	
	public boolean isOcr() {
		return (hasOcr() || ((isParentUser() || isEnterpriseChild()) && hasParentOcr()))
			&& (isAdmin() || hasRole(AonRole.OCR));
	}
	
	// OCR INVOFOX
	
	public boolean hasInvofox() {
		return hasApp(AonApp.INVOFOX);
	}
	
	public boolean hasParentInvofox() {
		return hasParentApp(AonApp.INVOFOX);
	}
	
	public boolean isInvofox() {
		return (hasInvofox() || ((isParentUser() || isEnterpriseChild()) && hasParentInvofox()))
			&& (isAdmin() || hasRole(AonRole.INVOFOX));
	}
	
	// FACTURAE
	
	public boolean hasFacturae() {
		return hasApp(AonApp.FACTURAE);
	}
	
	public boolean hasParentFacturae() {
		return hasParentApp(AonApp.FACTURAE);
	}
	
	public boolean isFacturae() {
		return (hasFacturae() || ((isParentUser() || isEnterpriseChild()) && hasParentFacturae()))
			&& (isAdmin() || hasRole(AonRole.FACTURAE));
	}
	
	// SERES
	
	public boolean hasSeres() {
		return hasApp(AonApp.SERES);
	}
	
	public boolean hasParentSeres() {
		return hasParentApp(AonApp.SERES);
	}
	
	public boolean isSeres() {
		return (hasInvofox() || ((isParentUser() || isEnterpriseChild()) && hasParentSeres()))
			&& (isAdmin() || hasRole(AonRole.SERES));
	}
	
	// BANK
	
	public boolean hasBank() {
		return hasApp(AonApp.BANK);
	}
	
	public boolean hasParentBank() {
		return hasParentApp(AonApp.BANK);
	}
	
	public boolean isBank() {
		return (hasBank() || ((isParentUser() || isEnterpriseChild()) && hasParentBank()))
				&& (isAdmin() || hasRole(AonRole.BANK));
	}
	
	// CONVENIOS
	
	private boolean hasConvenios() {
		return hasApp(AonApp.CONVENIOS);
	}
	
	private boolean hasParentConvenios() {
		return hasParentApp(AonApp.CONVENIOS);
	}
	
	public boolean isConvenios() {
		return hasConvenios() || ((isParentUser() || isEnterpriseChild()) && hasParentConvenios());
			// TODO añadir -> && (isAdmin() || hasRole(AonRole.CONVENIOS));
	}
	
	public boolean isAon() {
		return hasApp(AonApp.AIO) && (isAdmin() || hasRole(AonRole.AIO));
	}
	
	public boolean isBidoq() {
		return hasApp(AonApp.BIDOQ) && (isAdmin() || hasRole(AonRole.BIDOQ));
	}
	
	public boolean isSelfconta() {
		return hasApp(AonApp.SELFCONTA) && (isAdmin() || hasRole(AonRole.SELFCONTA));
	}
	
	// SALTRA
	
	public boolean hasSaltra() {
		return hasApp(AonApp.SALTRA);
	}
	
	public boolean isSaltra() {
		return hasSaltra() && (isAdmin() || hasRole(AonRole.SALTRA));
	}
	
	public boolean isSaltraPortal() {
		return hasSaltra() && (isAdmin() || hasRole(AonRole.SALTRA_PORTAL));
	}
	
	public boolean isSaltraManager() {
		return hasSaltra() && (isAdmin() || hasRole(AonRole.SALTRA_MANAGER));
	}
	
	public boolean isConfidential() {
		return hasRole(AonRole.CONFIDENTIALITY) || hasOldRole(OldAonRole.CONFIDENTIALITY);
	}
	
	public boolean isAlpha() {
		return hasRole(AonRole.ALPHA);
	}

	public boolean isBeta() {
		return this.hasRole(AonRole.BETA) || getDomain().getName().contains("aonsolutions.org");
	}

	public boolean isEmployee() {
		return this.hasRole(AonRole.EMPLOYEE);
	}

	public boolean isEnterprise() {
		return this.hasRole(AonRole.ENTERPRISE);
	}
	
	// CUSTOM VIEW
	
	public boolean hasCustomView() {
		return hasApp(AonApp.CUSTOM_VIEW);
	}
	
	public boolean hasParentCustomView() {
		return hasParentApp(AonApp.CUSTOM_VIEW);
	}
	
	// TREASURY
	
	public boolean hasTreasury() {
	    return hasOldModule(Module.TREASURY) || this.hasApp(AonApp.TREASURY);
	}
	
	public boolean hasParentTreasury() {
	    return hasParentOldModule(Module.TREASURY) || this.hasParentApp(AonApp.TREASURY);
	}

	public boolean isTreasury() {
		return (hasTreasury() || ((isParentUser() || isEnterpriseChild()) && hasParentTreasury()))
			&& (isAdmin() || hasRole(AonRole.TREASURY) || hasOldRole(OldAonRole.FINANCE));
	}
	
	// MARKETING
	
	public boolean hasMarketing() {
	    return hasOldModule(Module.MARKETING) || this.hasApp(AonApp.MARKETING);
	}
	
	public boolean hasParentMarketing() {
	    return hasParentOldModule(Module.MARKETING) || this.hasParentApp(AonApp.MARKETING);
	}

	public boolean isMarketing() {
		return (hasMarketing() || ((isParentUser() || isEnterpriseChild()) && hasParentMarketing()))
			 && (this.isAdmin() || this.hasRole(AonRole.MARKETING));
	}
	
	// GROUPWARE
	
	public boolean hasGroupware() {
	    return hasOldModule(Module.GROUPWARE) || this.hasApp(AonApp.GROUPWARE);
	}
	
	public boolean hasParentGroupware() {
	    return hasParentOldModule(Module.GROUPWARE) || this.hasParentApp(AonApp.GROUPWARE);
	}

	public boolean isGroupware() {
		return (hasGroupware() || ((isParentUser() || isEnterpriseChild()) && hasParentGroupware()))
			 && (isAdmin() || hasRole(AonRole.GROUPWARE) 
				 || hasOldRole(OldAonRole.TASK_MONITORING));
	}
	
	public boolean hasCallCenter() {
	    return hasOldModule(Module.CALL_CENTER);
	}
	
	public boolean isCallCenter() {
		return hasCallCenter()
			&& (isAdmin() 
				||hasOldRole(OldAonRole.CALL_CENTER)
				|| hasOldRole(OldAonRole.CALL_CENTER_MANAGER));
	}
}
