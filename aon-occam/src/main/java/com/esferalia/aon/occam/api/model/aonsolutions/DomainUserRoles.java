package com.esferalia.aon.occam.api.model.aonsolutions;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;

public class DomainUserRoles {
	Domain domain;
	User user;
	
	LinkedList<DomainApp> domainApps;
	LinkedList<DomainApp> parentDomainApps;
	LinkedList<UserAppRole> domainUserRoles;
	LinkedList<UserAppRole> parentDomainUserRoles;
	
	public DomainUserRoles() {
	
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
	
	public LinkedList<DomainApp> getDomainApps() {
		return domainApps;
	}
	
	public DomainUserRoles setDomainApps(LinkedList<DomainApp> domainApps) {
		this.domainApps = domainApps;
		return this;
	}
	
	public LinkedList<DomainApp> getParentDomainApps() {
		return parentDomainApps;
	}
	
	public DomainUserRoles setParentDomainApps(LinkedList<DomainApp> parentDomainApps) {
		this.parentDomainApps = parentDomainApps;
		return this;
	}
	
	public LinkedList<UserAppRole> getDomainUserRoles() {
		return domainUserRoles;
	}
	
	public DomainUserRoles setDomainUserRoles(LinkedList<UserAppRole> domainUserRoles) {
		this.domainUserRoles = domainUserRoles;
		return this;
	}
	
	public LinkedList<UserAppRole> getParentDomainUserRoles() {
		return parentDomainUserRoles;
	}
	
	public DomainUserRoles setParentDomainUserRoles(LinkedList<UserAppRole> parentDomainUserRoles) {
		this.parentDomainUserRoles = parentDomainUserRoles;
		return this;
	}
	
	public Boolean isParentUser(){
		return getDomain().getParentId().equals(getUser().getDomain());
	}
	
	public Boolean isAdmin() {
		return getDomainUserRoles().stream().filter(f -> AonRole.ADMIN.equals(f.getRole())).count() > 0
			|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.ADMIN.equals(f.getRole())).count() > 0);
	}
	
	private Boolean hasApp(AonApp aonApp) {
		DomainApp dapp;
		if(getDomainApps().size() > 0) {
			dapp = getDomainApps().stream().filter(f -> aonApp.equals(f.getApp())).findFirst().orElse(null);
		} else dapp = getParentDomainApps().stream().filter(f -> aonApp.equals(f.getApp())).findFirst().orElse(null);
		return dapp != null && dapp.getActive();
	}
	
	public Boolean isAccounting() {	
		return hasApp(AonApp.ACCOUNTING)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.ACCOUNTING.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.ACCOUNTING.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isAccountingManager() {
		return hasApp(AonApp.ACCOUNTING)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.ACCOUNTING_MANAGER.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.ACCOUNTING_MANAGER.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isFiscal() {
		return hasApp(AonApp.FISCAL)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.FISCAL.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.FISCAL.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isFiscalManager() {
		return hasApp(AonApp.FISCAL)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.FISCAL_MANAGER.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.FISCAL_MANAGER.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isPayroll() {
		return hasApp(AonApp.PAYROLL)
			&& ( getDomainUserRoles().stream().filter(f -> AonRole.PAYROLL.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.PAYROLL_MANAGER.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isPayrollPortal() {
		return hasApp(AonApp.PAYROLL)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.PAYROLL_PORTAL.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.PAYROLL_PORTAL.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isPayrollManager() {
		return hasApp(AonApp.PAYROLL)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.PAYROLL_MANAGER.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.PAYROLL_MANAGER.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isDocumental() {
		return hasApp(AonApp.DOCUMENTAL)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.DOCUMENTAL.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.DOCUMENTAL.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isDocumentalManager() {
		return hasApp(AonApp.DOCUMENTAL)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.DOCUMENTAL_MANAGER.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.DOCUMENTAL_MANAGER.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isComunica() {
		return hasApp(AonApp.COMUNICA)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.COMUNICA.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.COMUNICA.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isComunicaPortal() {
		return hasApp(AonApp.COMUNICA)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.COMUNICA_PORTAL.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.COMUNICA_PORTAL.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isComunicaManager() {
		return hasApp(AonApp.COMUNICA)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.COMUNICA_MANAGER.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.COMUNICA_MANAGER.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isTimecontrol() {
		return hasApp(AonApp.TIMECONTROL)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.TIMECONTROL.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.TIMECONTROL.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isTimecontrolPortal() {
		return hasApp(AonApp.TIMECONTROL)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.TIMECONTROL_PORTAL.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.TIMECONTROL_PORTAL.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isTimecontrolManager() {
		return hasApp(AonApp.TIMECONTROL)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.TIMECONTROL_MANAGER.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.TIMECONTROL_MANAGER.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isMessenger() {
		return hasApp(AonApp.MESSENGER)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.MESSENGER.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.MESSENGER.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isMessengerManager() {
		return hasApp(AonApp.MESSENGER)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.MESSENGER_MANAGER.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.MESSENGER_MANAGER.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isInvoice() {
		return hasApp(AonApp.INVOICE)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.INVOICE.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.INVOICE.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isInvoicePortal() {
		return hasApp(AonApp.INVOICE)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.INVOICE_PORTAL.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.INVOICE_PORTAL.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isInvoiceManager() {
		return hasApp(AonApp.INVOICE)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.INVOICE_MANAGER.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.INVOICE_MANAGER.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isManagement() {
		return hasApp(AonApp.MANAGEMENT)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.MANAGEMENT.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.MANAGEMENT.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isManagementManager() {
		return hasApp(AonApp.MANAGEMENT)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.MANAGEMENT_MANAGER.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.MANAGEMENT_MANAGER.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isAlma() {
		return hasApp(AonApp.ALMA)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.ALMA.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.ALMA.equals(f.getRole())).count() > 0));
	}
	public Boolean isOcr() {
		return hasApp(AonApp.OCR)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.OCR.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.OCR.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isBank() {
		return hasApp(AonApp.BANK)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.BANK.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.BANK.equals(f.getRole())).count() > 0));
	}
	
	public Boolean isConvenios() {
		return hasApp(AonApp.CONVENIOS)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.CONVENIOS.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.CONVENIOS.equals(f.getRole())).count() > 0));
	}
	public Boolean isAon() {
		return hasApp(AonApp.AIO)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.AON.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.AON.equals(f.getRole())).count() > 0));
	}
	public Boolean isBidoq() {
		return hasApp(AonApp.BIDOQ)
			&& (getDomainUserRoles().stream().filter(f -> AonRole.BIDOQ.equals(f.getRole())).count() > 0
				|| (isParentUser() && getParentDomainUserRoles().stream().filter(f -> AonRole.BIDOQ.equals(f.getRole())).count() > 0));
	}
}
