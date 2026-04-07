import { AonElement } from "../../components/AonElement.js";
import { getAuth, getCompany, getDomainUserRoles, getRegistry, saveServiceAccount, getCompanyOne, getRelationShipCompany, getSiblingsOffice } from "../../services/service.js";
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { AonCompanyList } from "../company/aon-company-list.js";
import { AonCompany } from "../company/aon-company.js";
import { AonApplication } from '../../components/aon-application.js';
import { AON_ICONS, CONSTANT, MATERIAL_ICONS, MSG, TAG, EVENT } from '../../environments/environments.js';
import { AonUserList } from "../user/aon-user-list.js";
import { AonMobileUserList } from "../user/aon-mobile-user-list.js";
import * as ACTION from '../actions.js';
import { CONFIGURATION, INVOICE, MESSENGER, AON_SALTRA, COMUNICA } from "../../services/app.js";
import { AonUser } from "../user/aon-user.js";
import { AonWorkgroup } from "./groups/aon-workgroup.js";
import { AonReg } from "../registry/aon-reg.js";
import * as GWT from '../../gwt/gwt.js';
import { Registry } from "../../models/registry/Registry.js";
import { AonInvoiceConfiguration } from "../invoice/aon-invoice-configuration.js";
import { AonInvoiceCommunicationConfig } from "../invoice/aon-invoice-communication-config.js";
import { AonConfigurationMenu } from './aon-configuration-menu.js';
import { AonMessengerConfig } from "../messenger/aon-messenger-config.js";
import { AonBooking } from '../marketplace/aon-booking.js';
import { AonComunicaConfig } from "../laboral/aon-comunica-config.js";
import { AonServiceAccountList } from "../user/aon-service-account-list.js";
import { AonNewsList } from "../news/news/aon-news-list.js";
import { AonCustomerList } from "../registry/customer/aon-customer-list.js";
import { createInput } from "../../components/CreateComponent.js";

export class AonConfiguration extends AonElement {

	AON_CONFIGURATION;

	COMPANY;
	COMPANY_LIST;
	CUSTOMER_LIST;

	selected;
	company;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get user() {
		return this.getAttribute("user");
	}

	set user(user) {
		this.setAttribute("user", user);
	}

	get option() {
		return this.getAttribute(CONSTANT.OPTION);
	}

	set option(option) {
		this.setAttribute(CONSTANT.OPTION, option);
	}

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
		this.createApplication(this.AON_CONFIGURATION, MSG.SETTING, new AonApplication());

		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.AON_CONFIGURATION = "aonConfiguration";
		this.COMPANY = this.AON_CONFIGURATION + "Company";
		this.COMPANY_LIST = this.AON_CONFIGURATION + "CompanyList";
		this.CUSTOMER_LIST = this.AON_CONFIGURATION + "CustomerList";
	}

	build() {
		let aonConfiguration = this.getElement(this.AON_CONFIGURATION);

		if (this.isMobile()) {
			aonConfiguration.addMobileSidenavHeader(CONFIGURATION);
		}

		let officeOptions = [];

		// Ficha Cliente
		if ( this.dur.isParentUser() && 
			 this.company && 
			 (this.company.registry || this.company.id) && 
			 (this.company.type !== "OFFICE" || this.company.domain.domainType !== "OFFICE") ) 
		{
			
			getRelationShipCompany({
				url: this.company.domain?.name || this.company.domain,
				relatedRegistry: this.company.registry || this.company.id
			}).then(relationshipCompany => {
				
				if (relationshipCompany.rrelationship) {
					officeOptions.push({
						name: MSG.CLIENT_FILE,
						icon: MATERIAL_ICONS.CONTACTS,
						fn: () => this.buildCustomerList(),
					});
					
					aonConfiguration.addSidenavOptionsFirst(
						MSG.OFFICE.toUpperCase(),
						officeOptions
					);
				}
				
			});
			
		}

		let companyOptions = [];

		if (this.getDur().isAdmin() || (!this.getDur().isEmployee() && !this.isMobile())) {
			companyOptions.push({
				name: MSG.GENERAL_INFORMATION,
				icon: MATERIAL_ICONS.BUSINESS,
				fn: () => this.buildGeneral(),
			});
		}

		if (this.getDur().isAdmin()) {
			companyOptions.push({
				name: MSG.USER_MANAGEMENT,
				icon: MATERIAL_ICONS.PEOPLE,
				fn: () => this.buildUser(),
			});
			if (this.company && this.company.domain && !this.company.domain.parentId) {
				companyOptions.push({
					name: MSG.COMPANY_MANAGEMENT,
					icon: MATERIAL_ICONS.BUSINESS,
					fn: () => this.buildCompanyList(),
				});
			}

			companyOptions.push({
				name: MSG.GROUP_MANAGEMENT,
				icon: MATERIAL_ICONS.GROUPS,
				fn: () => this.buildGroups(),
			});

			if (this.getDur().isApiService()) {
				companyOptions.push({
					name: MSG.SERVICE_ACCOUNTS,
					icon: MATERIAL_ICONS.API,
					fn: () => this.buildServiceAccount(),
				});
			}

			if (!this.isMobile() && ( (!this.getDur().isTrial() && !this.getDur().hasBeenTrial()) || this.getDur().isParentUser()) ) {
				companyOptions.push({
					name: MSG.HIRING,
					icon: MATERIAL_ICONS.STORE_MALL_DIRECTORY,
					fn: () => this.buildStore(),
				});
			}
		}
		
		if(this.company && this.company.registry && this.company.domain){
			getRelationShipCompany({
				url: this.company.domain,
				relatedRegistry: this.company.registry
			}).then(relationshipCompany => {
			
				if (relationshipCompany.rrelationship) {
					companyOptions.push({
						name: MSG.HIRING + ' (Planes)',
						icon: MATERIAL_ICONS.STORE_MALL_DIRECTORY,
						fn: () => this.buildPlans(),
					});
				}
				aonConfiguration.addSidenavOptions(this.getDur().isConsultancy() ? MSG.ENVIRONMENT.toUpperCase() : MSG.COMPANY.toUpperCase(), companyOptions);
			}).catch(e => {	
				aonConfiguration.addSidenavOptions(this.getDur().isConsultancy() ? MSG.ENVIRONMENT.toUpperCase() : MSG.COMPANY.toUpperCase(), companyOptions);
			});
		} else aonConfiguration.addSidenavOptions(this.getDur().isConsultancy() ? MSG.ENVIRONMENT.toUpperCase() : MSG.COMPANY.toUpperCase(), companyOptions);
		

		if (localStorage.getItem("aon_domain_id")) {
			let appOptions = [];
			if (this.getDur().isInvoice()) {
				appOptions.push({
					name: INVOICE.title,
					icon: MATERIAL_ICONS.MONITORING,
					fn: () => this.buildInvoiceConfiguration(),
				});
				appOptions.push({
					name: MSG.COMMUNICATIONS,
					icon: MATERIAL_ICONS.COMMUNICATION,
					fn: () => this.buildInvoiceCommunication(),
				});
			}

			if (!this.getDur().isEmployee()) {
				appOptions.push({
					id: MESSENGER.title,
					name: MESSENGER.title,
					icon: MATERIAL_ICONS.SPEAKER_NOTES,
					fn: () => this.buildMessengerConfiguration(),
				});
			}

			/*
			if (this.getDur().isSaltraManager()) {
				appOptions.push({
					id: AON_SALTRA.title,
					name: AON_SALTRA.title,
					icon: MATERIAL_ICONS.ALTERNATE_EMAIL,
					fn: () => this.buildComunicaConfiguration(),
				});
			} else if (this.getDur().isComunicaManager()) {
				appOptions.push({
					id: COMUNICA.title,
					name: COMUNICA.title,
					icon: MATERIAL_ICONS.ALTERNATE_EMAIL,
					fn: () => this.buildComunicaConfiguration(),
				});
			}
			*/

			if (!this.getDur().isEmployee() && this.isBeta()) {
				appOptions.push({
					id: "notice",
					icon: "rss_feed",
					name: "Comunicaciones",
					fn: () => this.buildNews(),
				});
			}

			aonConfiguration.addSidenavOptions(MSG.APPLICATIONS.toUpperCase(), appOptions);
		}

		if (localStorage.getItem("aon_domain_id") && this.isBeta()) {
			let menuOptions = [];

			menuOptions.push({
				id: "options panel",
				icon: "dashboard",
				name: "Panel Configuración",
				fn: () => this.buildConfigurationMenu(),
			});

			aonConfiguration.addSidenavOptions(MSG.MENU.toUpperCase(), menuOptions);
		}

		this.buildConfigurationMenu();
	}

	buildPersonal() {
		getAuth().then((user) => {
			let aonConfiguration = this.getElement(this.AON_CONFIGURATION);

			let toolbar = this.getElement(aonConfiguration.TOOLBAR);
			toolbar.setAttribute('option', MSG.MY_DATA);

			aonConfiguration.removeToolbarOptions();

			let aonUser = new AonUser();
			aonUser.id = 'aonUserPersonal';
			aonUser.setShowPassword(true);
			aonUser.setShowToolbar(true);
			aonUser.setOnlyAuth(true);
			aonUser.setUser(user);
			aonUser.style.width = "100%";

			aonConfiguration.setContent(aonUser);
		});
	}

	buildGeneral() {
		let data = {
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RECORD_DATA']
		};

		getCompanyOne(data).then(cp => {
			let aonRegistry = new AonReg();
			aonRegistry.id = this.getApplication().id + 'Registry';
			aonRegistry.setShowLogo(true);
			aonRegistry.setRegistry(cp);
			this.getApplication().setContent(aonRegistry);
		});
	}

	buildUser() {
		let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
		aonConfiguration.removeToolbarOptions();

		if (this.isMobile()) {
			aonConfiguration.addFloatOption(ACTION.ADD, () => this.buildCreateUser(false));
		} else {
			aonConfiguration.addToolbarOption("UserShare", "share", () =>
				this.buildCreateUser(true)
			);
			aonConfiguration.addToolbarOption("UserAdd", "add", () =>
				this.buildCreateUser(false)
			);
		}

		let userList = this.isMobile()
			? new AonMobileUserList()
			: new AonUserList();
		aonConfiguration.setContent(userList);
	}

	buildServiceAccount() {
		let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
		aonConfiguration.removeToolbarOptions();

		if (this.isMobile()) {
			aonConfiguration.addFloatOption(ACTION.ADD, () => this.createServiceAccount());
		} else {
			aonConfiguration.addToolbarOption("ServiceAccountAdd", "add", () =>
				this.createServiceAccount()
			);
		}

		let serviceAccountList = this.isMobile()
			? new AonMobileUserList()
			: new AonServiceAccountList();
		aonConfiguration.setContent(serviceAccountList);
	}


	createServiceAccount() {
		let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
		let d = document.getElementById(aonConfiguration.DIALOG);

		let div = this.createElement(TAG.DIV);

		let input = createInput(CONSTANT.NAME, MSG.NAME);
		div.appendChild(input);

		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.CREATE_SERVICE_ACCOUNT);
		d.setContent(div);
		d.addAcceptAction(() => {
			saveServiceAccount({ name: input.value }).then(() => {
				this.getElement(CONSTANT.AON_SERVICE_ACCOUNT_LIST).reload();
			});
		});
		d.open();
	}

	buildInvoiceConfiguration() {
		this.getApplication().setContent(new AonInvoiceConfiguration());
	}

	buildInvoiceCommunication() {
		this.getApplication().setContent(new AonInvoiceCommunicationConfig());
	}

	buildMessengerConfiguration() {
		this.getApplication().setContent(new AonMessengerConfig());
	}

	/*
	buildComunicaConfiguration() {
		this.getApplication().setContent(new AonComunicaConfig());
	}
	*/

	buildConfigurationMenu() {
		this.getApplication().setContent(new AonConfigurationMenu());
	}

	buildNews() {
		this.getApplication().setContent(new AonNewsList());
	}

	buildCustomerList() {
		let aonConfiguration = this.getApplication();
		
		let aonCustomerList = new AonCustomerList(this);
		aonCustomerList.id = this.CUSTOMER_LIST;
		aonCustomerList.office = true;
		aonCustomerList.clientFile = true;
		aonCustomerList.filter = {
			page: 1,
			perPage: 50,
			target: false,
			status: ["ACTIVE", "BLOCKED", "INACTIVE"],
			relatedRegistry: true
		};

		let buildListener = (event) => {
			if (event.detail.registries.length === 1) {
				aonCustomerList.buildRegistry(event.detail.registries[0])
			}
			aonCustomerList.removeEventListener(EVENT.BUILD, buildListener);
		};
		aonCustomerList.addEventListener(EVENT.BUILD, buildListener);

		aonConfiguration.setContent(aonCustomerList);
	}

	buildCompanyList() {
		let aonConfiguration = this.getApplication();
		aonConfiguration.removeToolbarOptions();

		aonConfiguration.addToolbarOption("UserAdd", "add", () => this.buildCompany());

		let aonCompanyList = new AonCompanyList();
		aonCompanyList.id = this.COMPANY_LIST;
		aonCompanyList.filter = { parent: true };
		aonConfiguration.setContent(aonCompanyList);
	}

	buildCompany() {
		let aonCompany = new AonCompany();
		aonCompany.id = this.getApplication().id + 'Company';
		let reg = new Registry();
		reg.domain = undefined;
		aonCompany.options = [
			{ title: MSG.GENERAL_DATA, fn: () => aonCompany.buildGeneralData() },
			{ title: MSG.BANK_DATA, fn: () => aonCompany.buildBankData() },
			{ title: MSG.REGISTRATION_DATA, fn: () => aonCompany.buildRegistralData() }
		];
		aonCompany.setRegistry(reg);
		this.getApplication().setContent(aonCompany);
	}

	buildCreateUser(share) {
		let aonUser = new AonUser();
		aonUser.id = 'aonUserCreate';
		aonUser.setShowApps(true);
		aonUser.setShowToolbar(true);
		aonUser.style.width = "100%";
		if (share) aonUser.setAttribute('share', share);

		this.getApplication().setContent(aonUser);
	}

	buildStore() {
		let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
		aonConfiguration.removeToolbarOptions();
		let marketplace = new AonBooking();
		marketplace.id = 'aonMarketplace';
		aonConfiguration.setContent(marketplace);
	}
	
	buildPlans() {
		GWT.iLoad(GWT.PRODUCT_CATALOGUE_MODULE);
	}

	buildGroups() {
		this.getApplication().setContent(new AonWorkgroup());
	}

	hiddenGeneral() {
		return false;
	}

	hiddenUser() {
		return false;
	}

	hiddenCompany() {
		return false;
	}

	hiddenStore() {
		return false;
	}

	getApplication() {
		return this.getElement(this.AON_CONFIGURATION);
	}
}

window.customElements.define("aon-configuration", AonConfiguration);
