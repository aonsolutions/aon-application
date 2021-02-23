import {App, Role} from './enums.js';


export class DomainUserRoles {

  domain;
	user;
  parentUser;

	domainApps;
	parentDomainApps;
	domainUserRoles;
	parentDomainUserRoles;

  oldDomainModules;
  oldParentDomainModules;
  oldUserRoles;

  constructor(data) {
    this.domain = data.domain;
    this.user = data.user;
    this.parentUser = data.parentUser;
    this.domainApps = data.domainApps;
    this.parentDomainApps = data.parentDomainApps;
    this.domainUserRoles = data.domainUserRoles;
    this.parentDomainUserRoles = data.parentDomainUserRoles;

    this.oldDomainModules = data.oldDomainModules;
    this.oldParentDomainModules = data.oldParentDomainModules;
    this.oldUserRoles = data.oldUserRoles;
  }

  getDomain() {
		return this.domain;
	}

	setDomain(domain) {
		this.domain = domain;
	}

	getUser() {
		return this.user;
	}

	setUser(user) {
		this.user = user;
	}

  getDomainApps() {
		return this.domainApps;
	}

	setDomainApps(domainApps) {
		this.domainApps = domainApps;
	}

  getParentDomainApps() {
		return this.parentDomainApps;
	}

	setParentDomainApps(parentDomainApps) {
		this.parentDomainApps = parentDomainApps;
	}

  getDomainUserRoles() {
		return this.domainUserRoles;
	}

	setDomainUserRoles(domainUserRoles) {
		this.domainUserRoles = domainUserRoles;
	}

  getParentDomainUserRoles() {
		return this.parentDomainUserRoles;
	}

  setParentDomainUserRoles(parentDomainUserRoles) {
		this.parentDomainUserRoles = parentDomainUserRoles;
	}

  getOldDomainModules() {
		return this.oldDomainModules;
	}

  setOldDomainModules(oldDomainModules) {
		this.oldDomainModules = oldDomainModules;
	}

  getOldParentDomainModules() {
		return this.oldParentDomainModules;
	}

  setOldParentDomainModules(oldParentDomainModules) {
		this.oldParentDomainModules = oldParentDomainModules;
	}

  getOldUserRoles() {
    return this.oldUserRoles;
  }

  setOldUserRoles(oldUserRoles) {
    this.oldUserRoles = oldUserRoles;
  }

	isParentUser(){
		return this.parentUser;
	}

  hasOldModule(mod) {
  	return getOldDomainModules().includes(mod) || getOldParentDomainModules().includes(mod);
  }

  hasOldRole(oldRole) {
		return this.getOldUserRoles().includes(oldRole);
	}

  hasApp(aonApp) {
  	return this.getDomainApps().includes(aonApp) || this.getParentDomainApps().includes(aonApp);
	}

  hasParentApp(aonApp) {
    return this.getParentDomainApps().includes(aonApp);
  }

  hasRole(aonRole) {
  	return this.getDomainUserRoles().includes(aonRole)
  	  || (this.isParentUser() && this.getParentDomainUserRoles().includes(aonRole));
	}

	isAdmin() {
		return this.hasRole(Role.ADMIN);
	}

	isAccounting() {
		return this.hasApp(App.ACCOUNTING) && (this.isAdmin() || this.hasRole(Role.ACCOUNTING));
	}

	isAccountingManager() {
    return this.hasApp(App.ACCOUNTING) && (this.isAdmin() || this.hasRole(Role.ACCOUNTING_MANAGER));
	}

	isFiscal() {
    return this.hasApp(App.FISCAL) && (this.isAdmin() || this.hasRole(Role.FISCAL));
	}

	isFiscalManager() {
		return this.hasApp(App.FISCAL) && (this.isAdmin() || this.hasRole(Role.FISCAL_MANAGER));
	}

  isPayroll() {
		return this.hasApp(App.PAYROLL) && (this.isAdmin() || this.hasRole(Role.PAYROLL));
	}

	isPayrollPortal() {
		return this.asApp(App.PAYROLL) && (this.isAdmin() || this.hasRole(Role.PAYROLL_PORTAL));
	}

  isPayrollManager() {
		return this.hasApp(App.PAYROLL) && (this.isAdmin() || this.hasRole(Role.PAYROLL_MANAGER));
	}

	isDocumental() {
		return (this.hasApp(App.DOCUMENTAL) && (this.isAdmin() || this.hasRole(Role.DOCUMENTAL)))
      || this.isDocumentalPortal() || this.isDocumentalManager();
	}

  isDocumentalPortal() {
		return (this.hasApp(App.DOCUMENTAL) && (this.isAdmin() || this.hasRole(Role.DOCUMENTAL_PORTAL)))
        || this.hasOldRole('ADMIN') || this.hasOldRole('DOCUMENT');
	}

	isDocumentalManager() {
		return (this.hasApp(App.DOCUMENTAL) && (this.isAdmin() || this.hasRole(Role.DOCUMENTAL_MANAGER)))
        || this.hasOldRole('ADMIN') || this.hasOldRole('DOCUMENT_MANAGER');
	}

	isComunica() {
		return this.hasApp(App.COMUNICA) && (this.isAdmin() || this.hasRole(Role.COMUNICA));
	}

	isComunicaPortal() {
		return this.hasApp(App.COMUNICA) && (this.isAdmin() || this.hasRole(Role.COMUNICA_PORTAL));
	}

	isComunicaManager() {
		return this.hasApp(App.COMUNICA) && (this.isAdmin() || this.hasRole(Role.COMUNICA_MANAGER));
	}

	isTimecontrol() {
		return this.hasApp(App.TIMECONTROL) && (this.isAdmin() || this.hasRole(Role.TIMECONTROL));
	}

	isTimecontrolPortal() {
		return this.hasApp(App.TIMECONTROL) && (this.isAdmin() || this.hasRole(Role.TIMECONTROL_PORTAL));
	}

	isTimecontrolManager() {
		return this.hasApp(App.TIMECONTROL) && (this.isAdmin() || this.hasRole(Role.TIMECONTROL_MANAGER));
	}

	isMessenger() {
		return this.hasApp(App.MESSENGER) && (this.isAdmin() || this.hasRole(Role.MESSENGER));
	}

	isMessengerManager() {
		return this.hasApp(App.MESSENGER) && (this.isAdmin() || this.hasRole(Role.MESSENGER_MANAGER));
	}

	isInvoice() {
		return this.hasApp(App.INVOICE) && (this.isAdmin() || this.hasRole(Role.INVOICE));
	}

	isInvoicePortal() {
		return this.hasApp(App.INVOICE) && (this.isAdmin() || this.hasRole(Role.INVOICE_PORTAL));
	}

	isInvoiceManager() {
		return this.hasApp(App.INVOICE) && (this.isAdmin() || this.hasRole(Role.INVOICE_MANAGER));
	}

	isManagement() {
		return this.hasApp(App.MANAGEMENT) && (this.isAdmin() || this.hasRole(Role.MANAGEMENT));
	}

	isManagementManager() {
		return this.hasApp(App.MANAGEMENT) && (this.isAdmin() || this.hasRole(Role.MANAGEMENT_MANAGER));
	}

	isAlma() {
		return this.hasApp(App.ALMA) && (this.isAdmin() || this.hasRole(Role.ALMA));
	}

	isOcr() {
		return this.hasApp(App.OCR) && (this.isAdmin() || this.hasRole(Role.OCR));
	}

	isBank() {
		return this.hasApp(App.BANK) && (this.isAdmin() || this.hasRole(Role.BANK));
	}

	isConvenios() {
		return this.hasApp(App.CONVENIOS) && (this.isAdmin() || this.hasRole(Role.CONVENIOS));
	}

  isAon() {
		return this.hasApp(App.AIO) && (this.isAdmin() || this.hasRole(Role.AON));
	}

  isBidoq() {
		return this.hasApp(App.BIDOQ) && (this.isAdmin() || this.hasRole(Role.BIDOQ));
	}

}
