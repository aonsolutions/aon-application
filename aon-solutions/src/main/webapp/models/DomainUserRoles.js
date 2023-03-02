import {App, OldModule, Role} from './enums.js';
import * as LS from '../services/localStorageService.js';
import { Domain } from './Domain.js';

export class DomainUserRoles {

  domain;
  parentDomain;

  maxDefinedUsers;
  definedUsers;

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
    this.domain = new Domain(data.domain);
    this.parentDomain = new Domain(data.parentDomain);
    this.user = data.user;
    this.parentUser = data.parentUser;
    this.domainApps = data.domainApps;
    this.parentDomainApps = data.parentDomainApps;
    this.domainUserRoles = data.domainUserRoles;
    this.parentDomainUserRoles = data.parentDomainUserRoles;

    this.oldDomainModules = data.oldDomainModules;
    this.oldParentDomainModules = data.oldParentDomainModules;
    this.oldUserRoles = data.oldUserRoles;

    this.maxDefinedUsers = data.maxDefinedUsers;
    this.definedUsers = data.definedUsers;
  }

  checkUsers() {
    return this.definedUsers < this.maxDefinedUsers;
  }
  
  getDomain() {
		return this.domain;
	}

	setDomain(domain) {
		this.domain = domain;
	}

  getParentDomain() {
		return this.parentDomain;
	}

	setParentDomain(parentDomain) {
		this.parentDomain = parentDomain;
	}

  isEnterpriseChild() {
    return this.getDomain().isChild() && !this.getParentDomain().isConsultancy()
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

  hasTaskHolder() {	
		return this.user && this.user.taskHolders && this.user.taskHolders.filter(th => th.domain.id === this.domain.id).length > 0;
	}

  hasOldModule(mod) {
  	return (this.getOldDomainModules() && this.getOldDomainModules().includes(mod));
  }

  hasParentOldModule(mod) {
  	return this.getOldParentDomainModules() && this.getOldParentDomainModules().includes(mod);
  }

  hasOldRole(oldRole) {
		return this.getOldUserRoles() && this.getOldUserRoles().includes(oldRole);
	}

  hasApp(aonApp) {
  	return (this.getDomainApps() && this.getDomainApps().includes(aonApp));
	}

  hasParentApp(aonApp) {
    return this.getParentDomainApps() && this.getParentDomainApps().includes(aonApp);
  }

  hasRole(aonRole) {
  	return (this.getDomainUserRoles() && this.getDomainUserRoles().includes(aonRole))
  	  || (this.isParentUser() && this.getParentDomainUserRoles() && this.getParentDomainUserRoles().includes(aonRole));
	}

	isAdmin() {
		return this.hasRole(Role.ADMIN) || this.hasOldRole(Role.ADMIN);
	}

  isDev() {
		return this.hasRole(Role.DEV);
	}

  // PACK SUITE

  hasPackSuite() {
    return this.hasApp(App.PACK_SUITE);
  }

  hasParentPackSuite() {
    return this.hasParentApp(App.PACK_SUITE);
  }

  // PACK PORTAL

  hasPackPortal() {
    return this.hasPackSuite() || this.hasApp(App.PACK_PORTAL);
  }

  hasParentPackPortal() {
    return this.hasParentPackSuite() || this.hasParentApp(App.PACK_PORTAL);
  }

  // PACK PAYROLL

  hasPackPayroll() {
    return this.hasPackSuite() || this.hasApp(App.PACK_PAYROLL);
  }

  hasParentPackPayroll() {
    return this.hasParentPackSuite() || this.hasParentApp(App.PACK_PAYROLL);
  }

  // PACK FISCONTA

  hasPackFiscalAccounting() {
    return this.hasApp(App.PACK_FISCAL_ACCOUNTING);
  }

  hasParentPackFiscalAccounting() {
    return this.hasParentPackSuite() || this.hasParentApp(App.PACK_FISCAL_ACCOUNTING);
  }
  
  // BASIC MANAGEMENT
  
  hasBasicManagement() {
    return this.hasApp(App.BASIC_MANAGEMENT);
  }

  hasParentBasicManagement() {
    return this.hasParentApp(App.BASIC_MANAGEMENT);
  }

  // STANDARD MANAGEMENT

  hasStandarManagement() {
    return this.hasApp(App.STANDAR_MANAGEMENT) || this.hasOldModule(OldModule.AON_ONE);
  }

  hasParentStandarManagement() {
    return this.hasParentApp(App.STANDAR_MANAGEMENT) || this.hasParentOldModule(OldModule.AON_ONE);
  }

  // PROFESSIONAL MANAGEMENT

  hasProfessionalManagement() {
    return this.hasApp(App.PROFESSIONAL_MANAGEMENT) || this.hasOldProfessionalManagement();
  }

  hasOldProfessionalManagement() {
    return !this.hasOldModule(OldModule.AON_FINANCE) 
      && !this.hasOldModule(OldModule.AON_ONE) && !this.hasApp(App.BASIC_MANAGEMENT);
  }

  hasParentProfessionalManagement() {
    return this.hasParentApp(App.PROFESSIONAL_MANAGEMENT) || (!this.getParentDomain().isConsultancy() && this.hasParentOldProfesionalManagement());
  }

  hasParentOldProfesionalManagement() {
    return !this.hasParentOldModule(OldModule.AON_FINANCE) 
      && !this.hasParentOldModule(OldModule.AON_ONE) && !this.hasParentApp(App.BASIC_MANAGEMENT);
  }

  // ACCOUNTING

  hasAccounting() {
    return this.hasApp(App.ACCOUNTING) || this.hasOldModule(OldModule.ACCOUNTING);
  }

  hasParentAccounting() {
    return this.hasParentApp(App.ACCOUNTING) || this.hasParentOldModule(OldModule.ACCOUNTING);
  }

	isAccounting() {
    
		return (this.hasAccounting() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentAccounting()))
      && (this.isAdmin() || this.hasRole(Role.ACCOUNTING));
	}

	isAccountingManager() {
    return (this.hasAccounting() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentAccounting()))
    && (this.isAdmin() || this.hasRole(Role.ACCOUNTING_MANAGER));
	}

  // FISCAL

  hasFiscal() {
    return  this.hasApp(App.FISCAL) || this.hasOldModule(OldModule.FISCAL);
  }

  hasParentFiscal() {
    return this.hasParentApp(App.FISCAL) || this.hasParentOldModule(OldModule.FISCAL);
  }

	isFiscal() {
    return (this.hasFiscal() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentFiscal()))
      && (this.isAdmin() || this.hasRole(Role.FISCAL));
	}

	isFiscalManager() {
		return (this.hasFiscal() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentFiscal()))
      && (this.isAdmin() || this.hasRole(Role.FISCAL_MANAGER));
	}

  // PAYROLL - LABORAL

  hasPayroll() {
    return this.hasApp(App.PAYROLL) || this.hasOldModule(OldModule.PAYROLL);
  }

  hasParentPayroll() {
    return this.hasParentApp(App.PAYROLL) || this.hasParentOldModule(OldModule.PAYROLL);
  }

  isPayroll() {
    
		return (this.hasPayroll() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentPayroll()))
      && (this.isAdmin() || this.hasRole(Role.PAYROLL));
	}

	isPayrollPortal() {
		return (this.hasPayroll() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentPayroll()))
      && (this.isAdmin() || this.hasRole(Role.PAYROLL_PORTAL));
	}

  isPayrollManager() {
		return (this.hasPayroll() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentPayroll()))
      && (this.isAdmin() || this.hasRole(Role.PAYROLL_MANAGER));
	}

  // DOCUMENTAL

  hasDocumental() {
    return this.hasApp(App.DOCUMENTAL) || this.hasOldModule(OldModule.DOCUMENT)
      || this.hasOldModule(OldModule.DOCUMENT_PORTAL);
  }

  hasParentDocumental() {
    return this.hasParentApp(App.DOCUMENTAL) || this.hasParentOldModule(OldModule.DOCUMENT)
      || this.hasParentOldModule(OldModule.DOCUMENT_PORTAL);
  }

	isDocumental() {
		return ((this.hasDocumental() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentDocumental())) 
      && (this.isAdmin() || this.hasRole(Role.DOCUMENTAL)))
      || this.isDocumentalPortal() || this.isDocumentalManager();
	}

  isDocumentalPortal() {
		return ((this.hasDocumental() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentDocumental()))
        && (this.isAdmin() || this.hasRole(Role.DOCUMENTAL_PORTAL)))
        || (!LS.isAonSolutions() && (this.hasOldRole('ADMIN') || this.hasOldRole('DOCUMENT')));
	}

	isDocumentalManager() {
		return ((this.hasDocumental() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentDocumental()))
        && (this.isAdmin() || this.hasRole(Role.DOCUMENTAL_MANAGER)))
        || (!LS.isAonSolutions() && (this.hasOldRole('ADMIN') || this.hasOldRole('DOCUMENT_MANAGER')));
	}

  // COMUNICA

  hasComunica() {
    return this.hasApp(App.COMUNICA) || this.hasOldModule(OldModule.COMUNICA);
  }

  hasParentComunica() {
    return this.hasParentApp(App.COMUNICA);
  }

	isComunica() {
    
		return (this.hasComunica() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentComunica()))
      && (this.isAdmin() || this.hasRole(Role.COMUNICA)) 
      || this.isComunicaPortal() || this.isComunicaManager();
	}

	isComunicaPortal() {
		return  (this.hasComunica() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentComunica())) 
      && (this.isAdmin() || this.hasRole(Role.COMUNICA_PORTAL));
	}

	isComunicaManager() {
		return  (this.hasComunica() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentComunica())) 
      && (this.isAdmin() || this.hasRole(Role.COMUNICA_MANAGER));
	}

  // API SERVICE

  hasApiService() {
    return this.hasApp(App.API_SERVICE);
  }

  hasParentApiService() {
    return this.hasParentApp(App.API_SERVICE);
  }

	isApiService() {
		return this.hasApiService() && (this.isAdmin() || this.isDev());
	}

  // TIME CONTROL

  hasTimeControl() {
    return this.hasApp(App.TIMECONTROL);
  }

  hasParentTimeControl() {
    return this.hasParentApp(App.TIMECONTROL);
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
    return this.hasApp(App.MESSENGER);
  }

  hasParentMessenger() {
    return this.hasParentApp(App.MESSENGER);
  }

	isMessenger() {
		return this.hasTaskHolder() && this.hasMessenger() && (this.isAdmin() || this.hasRole(Role.MESSENGER));
	}

	isMessengerManager() {
		return this.hasTaskHolder() && this.hasMessenger() && (this.isAdmin() || this.hasRole(Role.MESSENGER_MANAGER));
	}

  // CALL CENTER

  hasCallCenter() {
    return this.hasOldModule(OldModule.CALL_CENTER);
  }

  // INVOICES - FACTURAS

  hasInvoice() {
    return this.hasApp(App.INVOICE) || this.hasOldModule(OldModule.FINANCE_PORTAL)
      || this.hasOldModule(OldModule.AON_FINANCE) || this.hasOldModule(OldModule.MANAGEMENT)
      || this.hasOldModule(OldModule.AON_ONE);
  }

  hasParentInvoice() {
    return this.hasParentApp(App.INVOICE) || this.hasParentOldModule(OldModule.FINANCE_PORTAL)
      || this.hasParentOldModule(OldModule.AON_FINANCE) || this.hasParentOldModule(OldModule.MANAGEMENT)
      || this.hasOldModule(OldModule.AON_ONE);
  }

	isInvoice() {
		return (this.hasInvoice() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentInvoice()))
      && (this.isAdmin() || this.hasRole(Role.INVOICE));
	}

	isInvoicePortal() {
		return (this.hasInvoice() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentInvoice()))
      && (this.isAdmin() || this.hasRole(Role.INVOICE_PORTAL));
	}

	isInvoiceManager() {
		return (this.hasInvoice() || ((this.parentUser || this.isEnterpriseChild()) && this.hasParentInvoice()))
      && (this.isAdmin() || this.hasRole(Role.INVOICE_MANAGER));
	}

  // COMMERCIAL - COMERCIAL

  hasCommercial() {
    return this.hasOldModule(OldModule.CRM) || this.hasApp(App.COMMERCIAL);
  }

  hasParentCommercial() {
    return this.hasParentOldModule(OldModule.CRM) || this.hasParentApp(App.COMMERCIAL);
  }

  isCommercial() {
    return this.hasCommercial() && (this.isAdmin() || this.hasRole(Role.COMMERCIAL));
  }

  // TREASURY - TESORERIA

  hasTreasury() {
    return this.hasOldModule(OldModule.TREASURY) || this.hasApp(App.TREASURY);
  }

  hasParentTreasury() {
    return this.hasParentOldModule(OldModule.TREASURY) || this.hasParentApp(App.TREASURY);
  }

  isTreasury() {
    return this.hasTreasury() && (this.isAdmin() || this.hasRole(Role.TREASURY));
  }

  // MARKETING

  hasMarketing() {
    return this.hasOldModule(OldModule.MARKETING) || this.hasApp(App.MARKETING);
  }
  
  hasParentMarketing() {
    return this.hasParentOldModule(OldModule.MARKETING) || this.hasParentApp(App.MARKETING);
  }

  isMarketing() {
    return this.hasMarketing() && (this.isAdmin() || this.hasRole(Role.MARKETING));
  }

  // GROUPWARE - EXPEDIENTES

  hasGroupware() {
    return this.hasOldModule(OldModule.GROUPWARE) || this.hasApp(App.GROUPWARE);
  }

  hasParentGroupware() {
    return this.hasParentOldModule(OldModule.GROUPWARE) || this.hasParentApp(App.GROUPWARE);
  }

  isGroupware() {
    return this.hasGroupware() && (this.isAdmin() || this.hasRole(Role.GROUPWARE));
  }

  // WAREHOUSE - ALMACEN

  hasWarehouse() {
    return this.hasApp(App.WAREHOUSE) || this.hasOldModule(OldModule.WAREHOUSE);
  }

  hasParentWarehouse() {
    return this.hasParentApp(App.WAREHOUSE) || this.hasParentOldModule(OldModule.WAREHOUSE);
  }
  
  isWarehouse() {
    return this.hasWarehouse() && (this.isAdmin() || this.hasRole(Role.WAREHOUSE));
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

  hasAon() {
    return this.hasApp(App.AIO);
  }

  hasParentAon() {
    return this.hasParentApp(App.AIO);
  }

  isAon() {
		return this.hasAon() && (this.isAdmin() || this.hasRole(Role.AIO));
	}

  isBidoq() {
		return this.hasApp(App.BIDOQ) && (this.isAdmin() || this.hasRole(Role.BIDOQ));
	}

  isSelfconta() {
		return this.hasApp(App.SELFCONTA) && (this.isAdmin() || this.hasRole(Role.SELFCONTA));
	}

  hasSaltra() {
		return this.hasApp(App.SALTRA);
	}
  
  hasParentSaltra() {
    return this.hasParentApp(App.SALTRA);
  }

  isSaltra() {
		return this.hasApp(App.SALTRA);
    //  && (this.isAdmin() || this.hasRole(Role.SALTRA))
    // || this.isSaltraPortal() || this.isSaltraManager();
	}

	isSaltraPortal() {
		return this.hasSaltra() && (this.isAdmin() || this.hasRole(Role.SALTRA_PORTAL));
	}

	isSaltraManager() {
		return this.hasSaltra() && (this.isAdmin() || this.hasRole(Role.SALTRA_MANAGER));
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
  
  hasCustomView() {
    return this.hasApp(App.CUSTOM_VIEW);
  }

  isEmptyDomain() {
    return !(this.domain && this.domain.id);
  }
}
