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
import { AonConfigurationMenu } from './aon-configuration-menu.js';
import { AonMessengerConfig } from "../messenger/aon-messenger-config.js";
import { AonBooking } from '../marketplace/aon-booking.js';
import { AonComunicaConfig } from "../laboral/aon-comunica-config.js";
import { AonServiceAccountList } from "../user/aon-service-account-list.js";
import { AonNewsList } from "../news/news/aon-news-list.js";
import { AonCustomerList } from "../registry/customer/aon-customer-list.js";
import { createInput } from "../../components/CreateComponent.js";

export class AonConfigurationEnviroment extends AonElement {

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

		
		if (localStorage.getItem("aon_domain_id")) {
			let appOptions = [];
			if (this.getDur().isInvoice()) {
				appOptions.push({
					name: INVOICE.title,
					icon: MATERIAL_ICONS.MONITORING,
					fn: () => this.buildInvoiceConfiguration(),
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

	buildInvoiceConfiguration() {
		this.getApplication().setContent(new AonInvoiceConfiguration());
	}

	buildMessengerConfiguration() {
		this.getApplication().setContent(new AonMessengerConfig());
	}

	buildConfigurationMenu() {
		this.getApplication().setContent(new AonConfigurationMenu());
	}

	buildNews() {
		this.getApplication().setContent(new AonNewsList());
	}

	getApplication() {
		return this.getElement(this.AON_CONFIGURATION);
	}
}

window.customElements.define("aon-configuration-enviroment", AonConfigurationEnviroment);
