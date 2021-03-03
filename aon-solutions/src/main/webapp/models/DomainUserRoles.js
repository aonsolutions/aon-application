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

  hasPackSuite() {
    return this.hasApp(App.PACK_SUITE);
  }

  hasParentPackSuite() {
    return this.hasParentApp(App.PACK_SUITE);
  }

  hasPackPortal() {
    return this.hasPackSuite() || this.hasApp(App.PACK_PORTAL);
  }

  hasParentPackPortal() {
    return this.hasParentPackSuite() || this.hasParentApp(App.PACK_PORTAL);
  }

  hasPackPayroll() {
    return this.hasPackSuite() || this.hasApp(App.PACK_PAYROLL);
  }

  hasParentPackPayroll() {
    return this.hasParentPackSuite() || this.hasParentApp(App.PACK_PAYROLL);
  }

  hasPackFiscalAccounting() {
    return this.hasPackSuite() || this.hasApp(App.PACK_FISCAL_ACCOUNTING);
  }

  hasParentPackFiscalAccounting() {
    return this.hasParentPackSuite() || this.hasParentApp(App.PACK_FISCAL_ACCOUNTING);
  }

  hasAccounting() {
    return this.hasApp(App.PACK_SUITE) || this.hasApp(App.PACK_FISCAL_ACCOUNTING)
      || this.hasApp(App.ACCOUNTING);
  }

  hasParentAccounting() {
    return this.hasParentPackSuite() || this.hasParentPackFiscalAccounting()
      || this.hasParentApp(App.ACCOUNTING);
  }

	isAccounting() {
		return this.hasAccounting() && (this.isAdmin() || this.hasRole(Role.ACCOUNTING));
	}

	isAccountingManager() {
    return this.hasAccounting() && (this.isAdmin() || this.hasRole(Role.ACCOUNTING_MANAGER));
	}

  hasFiscal() {
    return this.hasApp(App.PACK_SUITE) || this.hasApp(App.PACK_FISCAL_ACCOUNTING)
      || this.hasApp(App.FISCAL);
  }

  hasParentFiscal() {
    return this.hasParentPackSuite() || this.hasParentPackFiscalAccounting()
      || this.hasParentApp(App.FISCAL);
  }

	isFiscal() {
    return this.hasFiscal() && (this.isAdmin() || this.hasRole(Role.FISCAL));
	}

	isFiscalManager() {
		return this.hasApp(App.FISCAL) && (this.isAdmin() || this.hasRole(Role.FISCAL_MANAGER));
	}

  hasPayroll() {
    return this.hasApp(App.PACK_SUITE) || this.hasApp(App.PACK_PAYROLL)
      || this.hasApp(App.PAYROLL);
  }

  hasParentPayroll() {
    return this.hasParentPackSuite() || this.hasParentPackPayroll()
      || this.hasParentApp(App.PAYROLL);
  }

  isPayroll() {
		return this.hasPayroll() && (this.isAdmin() || this.hasRole(Role.PAYROLL));
	}

	isPayrollPortal() {
		return this.asApp(App.PAYROLL) && (this.isAdmin() || this.hasRole(Role.PAYROLL_PORTAL));
	}

  isPayrollManager() {
		return this.hasApp(App.PAYROLL) && (this.isAdmin() || this.hasRole(Role.PAYROLL_MANAGER));
	}

  hasDocumental() {
    return this.hasPackSuite() || this.hasPackPortal()
      || this.hasApp(App.DOCUMENTAL);
  }

  hasParentDocumental() {
    return this.hasParentPackSuite() || this.hasParentPackPortal()
      || this.hasParentApp(App.DOCUMENTAL);
  }

	isDocumental() {
		return (this.hasDocumental() && (this.isAdmin() || this.hasRole(Role.DOCUMENTAL)))
      || this.isDocumentalPortal() || this.isDocumentalManager();
	}

  isDocumentalPortal() {
		return (this.hasDocumental() && (this.isAdmin() || this.hasRole(Role.DOCUMENTAL_PORTAL)))
        || this.hasOldRole('ADMIN') || this.hasOldRole('DOCUMENT');
	}

	isDocumentalManager() {
		return (this.hasDocumental() && (this.isAdmin() || this.hasRole(Role.DOCUMENTAL_MANAGER)))
        || this.hasOldRole('ADMIN') || this.hasOldRole('DOCUMENT_MANAGER');
	}

  hasComunica() {
    return this.hasApp(App.PACK_SUITE) || this.hasApp(App.PACK_PAYROLL)
      || this.hasApp(App.COMUNICA);
  }

  hasParentComunica() {
    return this.hasParentPackSuite() || this.hasParentPackPayroll()
      || this.hasParentApp(App.COMUNICA);
  }

	isComunica() {
		return this.hasComunica() && (this.isAdmin() || this.hasRole(Role.COMUNICA));
	}

	isComunicaPortal() {
		return this.hasComunica() && (this.isAdmin() || this.hasRole(Role.COMUNICA_PORTAL));
	}

	isComunicaManager() {
		return this.hasComunica() && (this.isAdmin() || this.hasRole(Role.COMUNICA_MANAGER));
	}

  hasTimeControl() {
    return this.hasApp(App.PACK_SUITE) || this.hasApp(App.PACK_PORTAL)
      || this.hasApp(App.TIMECONTROL) || this.hasComunica();
  }

  hasParentTimeControl() {
    return this.hasParentPackSuite() || this.hasParentPackPortal()
      || this.hasParentApp(App.TIMECONTROL) || this.hasParentComunica();
  }

	isTimecontrol() {
		return this.hasTimeControl() && (this.isAdmin() || this.hasRole(Role.TIMECONTROL));
	}

	isTimecontrolPortal() {
		return this.hasTimeControl() && (this.isAdmin() || this.hasRole(Role.TIMECONTROL_PORTAL));
	}

	isTimecontrolManager() {
		return this.hasTimeControl() && (this.isAdmin() || this.hasRole(Role.TIMECONTROL_MANAGER));
	}

  // MESSENGER - MENSAJERÍA

  hasMessenger() {
    return this.hasApp(App.PACK_SUITE) || this.hasApp(App.PACK_PORTAL)
      || this.hasApp(App.MESSENGER);
  }

  hasParentMessenger() {
    return this.hasParentPackSuite() || this.hasParentPackPortal()
      || this.hasParentApp(App.MESSENGER);
  }

	isMessenger() {
		return this.hasMessenger() && (this.isAdmin() || this.hasRole(Role.MESSENGER));
	}

	isMessengerManager() {
		return this.hasMessenger() && (this.isAdmin() || this.hasRole(Role.MESSENGER_MANAGER));
	}

  // INVOICES - FACTURAS

  hasInvoice() {
    return this.hasApp(App.PACK_SUITE) || this.hasApp(App.PACK_PORTAL)
      || this.hasApp(App.INVOICE);
  }

  hasParentInvoice() {
    return this.hasParentPackSuite() || this.hasParentPackPortal()
      || this.hasParentApp(App.INVOICE);
  }

	isInvoice() {
		return this.hasInvoice() && (this.isAdmin() || this.hasRole(Role.INVOICE));
	}

	isInvoicePortal() {
		return this.hasInvoice() && (this.isAdmin() || this.hasRole(Role.INVOICE_PORTAL));
	}

	isInvoiceManager() {
		return this.hasInvoice() && (this.isAdmin() || this.hasRole(Role.INVOICE_MANAGER));
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

  isConfidential() {
    return this.hasRole(Role.CONFIDENTIALITY);
  }

  isAlpha() {
    return this.hasRole(Role.ALPHA);
  }

  isBeta() {
    return this.hasRole(Role.BETA);
  }

  isEmployee() {
    return this.hasRole(Role.EMPLOYEE);
  }

  isEnterprise() {
    return this.hasRole(Role.ENTERPRISE);
  }
}
