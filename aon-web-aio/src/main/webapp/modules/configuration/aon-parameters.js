import { AonElement } from "../../components/AonElement.js";
import { getAuth, getCompany, saveServiceAccount, getCompanyOne, getRelationShipCompany } from "../../services/service.js";
import { AonCompanyList } from "../company/aon-company-list.js";
import { AonCompany } from "../company/aon-company.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MATERIAL_ICONS, MSG, TAG, EVENT } from '../../environments/environments.js';
import { AonUserList } from "../user/aon-user-list.js";
import { AonMobileUserList } from "../user/aon-mobile-user-list.js";
import * as ACTION from '../actions.js';
import { CONFIGURATION, INVOICE, MESSENGER } from "../../services/app.js";
import { AonUser } from "../user/aon-user.js";
import { AonWorkgroup } from "./groups/aon-workgroup.js";
import { AonReg } from "../registry/aon-reg.js";
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';
import { Registry } from "../../models/registry/Registry.js";
import { AonInvoiceConfiguration } from "../invoice/aon-invoice-configuration.js";
import { AonConfigurationMenu } from './aon-configuration-menu.js';
import { AonMessengerConfig } from "../messenger/aon-messenger-config.js";
import { AonBooking } from '../marketplace/aon-booking.js';
import { AonServiceAccountList } from "../user/aon-service-account-list.js";
import { AonNewsList } from "../news/news/aon-news-list.js";
import { AonCustomerList } from "../registry/customer/aon-customer-list.js";
import { createInput } from "../../components/CreateComponent.js";

export class AonParameters extends AonElement {

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
			let principalTableOptions = [];
			
			principalTableOptions.push({
				name: "Clientes",
				fn: () => this.getApplication().setContent(new JSF.AonJsfCustomer()),
			});
			
			principalTableOptions.push({
				name: "Grupos de Facturación",
				fn: () => this.getApplication().setContent(new JSF.AonJsfInvoicingGroup()),
			});
			
			principalTableOptions.push({
				name: "Proveedores",
				fn: () => this.getApplication().setContent(new JSF.AonJsfSupplier()),
			});
			
			principalTableOptions.push({
				name: "Acreedores",
				fn: () => this.getApplication().setContent(new JSF.AonJsfCreditor()),
			});
			
			principalTableOptions.push({
				name: "Productos",
				fn: () => this.getApplication().setContent(new JSF.AonJsfProduct()),
			});
			
			principalTableOptions.push({
				name: "Gastos",
				fn: () => this.getApplication().setContent(new JSF.AonJsfExpense()),
			});
			
			principalTableOptions.push({
				name: "Clientes Potenciales",
				fn: () => this.getApplication().setContent(new JSF.AonJsfTarget()),
			});
			
			principalTableOptions.push({
				name: "Agentes Comerciales",
				fn: () => this.getApplication().setContent(new JSF.AonJsfSeller()),
			});
			
			principalTableOptions.push({
				name: "Consola de facturación",
				fn: () => GWT.iLoad(GWT.INVOICE_CONSOLE, this.getApplication().CONTENT),
			});
			
			principalTableOptions.push({
				name: "Carga de datos Excel",
				fn: () => GWT.iLoad(GWT.IMPORT, this.getApplication().CONTENT),
			});
			
			aonConfiguration.addSidenavOptions("T. PRINCIPALES", principalTableOptions);
			
			let auxiliaryManagementTableOptions = [];
			
			auxiliaryManagementTableOptions.push({
				name: MSG.PAYMETHODS,
				fn: () => GWT.iLoad(GWT.PAY_METHOD, this.getApplication().CONTENT),
			});
			
			auxiliaryManagementTableOptions.push({
				name: "Asig. contable forma de pago",
				fn: () => this.getApplication().setContent(new JSF.AonJsfPayMethodTypeDetail()),
			});
			
			auxiliaryManagementTableOptions.push({
				name: "Conceptos Bancarios",
				fn: () => this.getApplication().setContent(new JSF.AonJsfBankConcept()),
			});
			
			auxiliaryManagementTableOptions.push({
				name: "Series",
				fn: () => this.getApplication().setContent(new JSF.AonJsfSeries()),
			});
			
			auxiliaryManagementTableOptions.push({
				name: "Impuestos",
				fn: () => this.getApplication().setContent(new JSF.AonJsfTax()),
			});
			
			auxiliaryManagementTableOptions.push({
				name: "País/Provincia",
				fn: () => this.getApplication().setContent(new JSF.AonJsfGeotree()),
			});
			
			auxiliaryManagementTableOptions.push({
				name: "Segmentación",
				fn: () => this.getApplication().setContent(new JSF.AonJsfSegment()),
			});
			
			aonConfiguration.addSidenavOptions("T. AUXILIARES", auxiliaryManagementTableOptions);
			
			let auxiliaryProductTableOptions = [];
			
			auxiliaryProductTableOptions.push({
				name: "Etiquetas de Productos",
				fn: () => this.getApplication().setContent(new JSF.AonJsfProductTag()),
			});
			
			auxiliaryProductTableOptions.push({
				name: "Categorías",
				fn: () => this.getApplication().setContent(new JSF.AonJsfProductCategory()),
			});
			
			auxiliaryProductTableOptions.push({
				name: "Marcas",
				fn: () => this.getApplication().setContent(new JSF.AonJsfBrand()),
			});
			
			auxiliaryProductTableOptions.push({
				name:"Tarifas",
				fn: () => GWT.iLoad(GWT.TARIFF_MODULE, this.getApplication().CONTENT),
			});
			
			auxiliaryProductTableOptions.push({
				name: "Catálogos",
				fn: () => this.getApplication().setContent(new JSF.AonJsfCatalogue()),
			});
			
			aonConfiguration.addSidenavOptions("PRODUCTOS", auxiliaryProductTableOptions);
			
			let utilitiesOptions = [];
			
			utilitiesOptions.push({
				name:'Utilidades Carga de Datos',
				fn: () => GWT.iLoad(GWT.IMPORT, this.getApplication().CONTENT),
			});
			
			utilitiesOptions.push({
				name:"Plantillas Carga de Datos",
				fn: () => GWT.iLoad(GWT.TEMPLATE, this.getApplication().CONTENT),
			});
			
			utilitiesOptions.push({
				name: "Carga de Datos CSV",
				fn: () => this.getApplication().setContent(new JSF.AonJsfLoader()),
			});
			
			utilitiesOptions.push({
				name:"Descarga de datos Excel",
				fn: () => GWT.iLoad(GWT.INVOICE_REPORT, this.getApplication().CONTENT),
			});

			aonConfiguration.addSidenavOptions("UTILIDADES", utilitiesOptions);
		}
		
		let empresa = this.getElement("aonSidenavTitleEMPRESA");
		empresa && empresa.click();
		
		let tPricipal = this.getElement("aonSidenavTitleT. PRINCIPALES");
		tPricipal && tPricipal.click();
		
		let tAuxiliar = this.getElement("aonSidenavTitleT. AUXILIARES");
		tAuxiliar && tAuxiliar.click();
		
		let productos = this.getElement("aonSidenavTitlePRODUCTOS");
		productos && productos.click();
		
		let utilities = this.getElement("aonSidenavTitleUTILIDADES");
		utilities && utilities.click();

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

			if (!this.getDur().isEmployee()) {
				appOptions.push({
					id: "notice",
					icon: "rss_feed",
					name: "Comunicaciones",
					fn: () => this.buildNews(),
				});
			}

			aonConfiguration.addSidenavOptions(MSG.APPLICATIONS.toUpperCase(), appOptions);
		}

		if (localStorage.getItem("aon_domain_id")) {
			let menuOptions = [];

			menuOptions.push({
				id: "OptionsPanel",
				icon: "dashboard",
				name: "Panel Opciones",
				fn: () => this.buildConfigurationMenu(),
			});

			aonConfiguration.addSidenavOptions(MSG.MENU.toUpperCase(), menuOptions);
		}
		
		let optionsPanel = this.getElement('aonConfigurationSidenavOptionsPanel');
		optionsPanel && optionsPanel.click();
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
		this.getApplication().removeToolbarOptions();

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
		this.getApplication().removeToolbarOptions();
		let userList = this.isMobile()
			? new AonMobileUserList()
			: new AonUserList();
		this.getApplication().setContent(userList);
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

window.customElements.define("aon-parameters", AonParameters);
