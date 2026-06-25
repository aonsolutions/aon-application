import { MSG, TAG } from '../../environments/environments.js';
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';
import { AonConfiguration } from '../configuration/aon-configuration.js';
import { PAYMETHODS } from '../MenuOptions.js';


export class AonConfigurationMenu extends AonSuiteMenu {

	AON_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;

	constructor() {
		super();
		this.configurationInitialize()
	}

	connectedCallback() {
		this.buildDur().then(() => {
			this.clear();
			this.initialize();
			this.build();
		})
	}

	configurationInitialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
		this.last = MSG.LAST_ACTIONS;
		this.new = MSG.NEW_ACTION;
		this.cardData = {
			title: MSG.ACTIVITY,
			info: [MSG.ORDERS_PENDING, MSG.OPEN_FEES, MSG.INVOICES_UNACCOUNTED_ABBR]
		};
		this.selectOptions = [];
		this.initOptions();
	}

	initOptions() {
		this.options = [{
			/*title: 'Datos de Empresa',
				options: [{
					description: "Configuración Global",
					title: "Configuración Global",
					action: () => this.rootPanel(new JSF.AonJsfGlobalConfig())
				}, {
					description: "Descarga de Empresas",
					title: "Descarga de Empresas",
					action: () => window.open("https://www.aonsolutions.es/solicitud-copia-de-seguridad-datos-empresa/", "_blank")
				}]
		}, {*/

			title: MSG.MAIN_TABLES,
			options: [{
				description: MSG.CUSTOMERS,
				title: MSG.CUSTOMERS,
				action: () => this.rootPanel(new JSF.AonJsfCustomer()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: MSG.BILLING_GROUPS,
				title: MSG.BILLING_GROUPS,
				action: () => this.rootPanel(new JSF.AonJsfInvoicingGroup()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: MSG.SUPPLIERS,
				title: MSG.SUPPLIERS,
				action: () => this.rootPanel(new JSF.AonJsfSupplier()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: MSG.CREDITORS,
				title: MSG.CREDITORS,
				action: () => this.rootPanel(new JSF.AonJsfCreditor()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: MSG.PRODUCTS,
				title: MSG.PRODUCTS,
				action: () => this.getApplication().setContent(new JSF.AonJsfProduct())
				//action: () => this.rootPanel(new JSF.AonJsfProduct())
			}, {
				description: MSG.EXPENSE,
				title: MSG.EXPENSE,
				action: () => this.getApplication().setContent(new JSF.AonJsfExpense())
			}, {
				description: MSG.TARGETS,
				title: MSG.TARGETS,
				action: () => this.rootPanel(new JSF.AonJsfTarget()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: MSG.AGENTS_COMMERCIAL,
				title: MSG.AGENTS_COMMERCIAL,
				action: () => this.rootPanel(new JSF.AonJsfSeller()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: MSG.INVOICE_CONSOLE,
				title: MSG.INVOICE_CONSOLE,
				action: () => GWT.iLoad(GWT.INVOICE_CONSOLE),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: MSG.EXCEL_DATA_LOAD,
				title: MSG.EXCEL_DATA_LOAD,
				action: () => GWT.iLoad(GWT.IMPORT),
				filter: () => this.isNotDomainManagementAvailable()
			}
			]
		}, {
			title: MSG.AUX_MANAGEMENT_TABLES,
			options: [
				{
					description: MSG.PAYMETHODS,
					title: MSG.PAYMETHODS,
					action: () => GWT.iLoad(GWT.PAY_METHOD, this.getApplication().CONTENT)
				}, {
					description: MSG.ACCOUNTING_PAYMENT_TYPE,
					title: MSG.ACCOUNTING_PAYMENT_TYPE,
					action: () => this.getApplication().setContent(new JSF.AonJsfPayMethodTypeDetail()),
					nofilter: () => this.isDomainManagementAvailable()
				}, {
					description: MSG.BANK_CONCEPTS,
					title: MSG.BANK_CONCEPTS,
					action: () => this.getApplication().setContent(new JSF.AonJsfBankConcept())
				}, {
					description: MSG.SERIES,
					title: MSG.SERIES,
					action: () => this.getApplication().setContent(new JSF.AonJsfSeries())
				}, {
					description: MSG.TAXES,
					title: MSG.TAXES,
					action: () => this.getApplication().setContent(new JSF.AonJsfTax())
				}, {
					description: MSG.COUNTRY_PROVINCE,
					title: MSG.COUNTRY_PROVINCE,
					action: () => this.getApplication().setContent(new JSF.AonJsfGeotree())
				}, {
					description: MSG.SEGMENTATION,
					title: MSG.SEGMENTATION,
					action: () => this.getApplication().setContent(new JSF.AonJsfSegment())
				}, {
					description: MSG.ENTITY_RELATIONSHIP_TYPES,
					title: MSG.ENTITY_RELATIONSHIP_TYPES,
					action: () => alert("Tipos de relaciones entre entidades"),
					filter: () => false
					//filter: () => this.isDomainManagementAvailable()
				}]
		}, {
			title: MSG.AUX_PRODUCT_TABLES,
			options: [{
				description: MSG.PRODUCT_TAGS,
				title: MSG.PRODUCT_TAGS,
				action: () => this.rootPanel(new JSF.AonJsfProductTag())
			}, {
				description: MSG.FORMAT_SIZE_TAGS,
				title: MSG.FORMAT_SIZE_TAGS,
				action: () => alert("Etiquetas de Formatos/Medidas"),
				filter: () => this.isDomainManagementAvailable()
			}, {
				description: MSG.CATEGORIES,
				title: MSG.CATEGORIES,
				action: () => this.rootPanel(new JSF.AonJsfProductCategory())
			}, {
				description: MSG.BRANDS,
				title: MSG.BRANDS,
				action: () => this.rootPanel(new JSF.AonJsfBrand())
			}, {
				description: MSG.TARIFFS,
				title: MSG.TARIFFS,
				action: () => GWT.iLoad(GWT.TARIFF_MODULE)
				// action: () => this.rootPanel(new JSF.AonJsfTariff())
			}, {
				description: MSG.CATALOGUES,
				title: MSG.CATALOGUES,
				action: () => this.rootPanel(new JSF.AonJsfCatalogue())
			}],
			filter: () => this.isNotDomainManagementAvailable()
		}, {
			title: MSG.DATA_LOAD_UTILITIES,
			options: [{
				description: MSG.EXCEL_DATA_LOAD,
				title: MSG.EXCEL_DATA_LOAD,
				action: () => GWT.iLoad(GWT.IMPORT),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: MSG.DATA_LOAD_TEMPLATE_MGT,
				title: MSG.DATA_LOAD_TEMPLATE_MGT,
				action: () => GWT.iLoad(GWT.TEMPLATE)
			}, {
				description: MSG.CSV_DATA_LOAD,
				title: MSG.CSV_DATA_LOAD,
				action: () => this.rootPanel(new JSF.AonJsfLoader()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: MSG.EXCEL_DATA_DOWNLOAD,
				title: MSG.EXCEL_DATA_DOWNLOAD,
				action: () => GWT.iLoad(GWT.INVOICE_REPORT),
				filter: () => this.isNotDomainManagementAvailable()
			}],
			filter: () => this.isNotDomainManagementAvailable()
		},
		/*
		{
			title: 'Configuración',
			options: [
			{
				description: "Configuración Global",
				title: "Configuración Global",
				action: () => this.rootPanel(new JSF.AonJsfGlobalConfig())
			},
			{
				description: "Cuentas de Correo",
				title: "Cuentas de Correo",
				action: () => this.rootPanel(new JSF.AonJsfMailAccount())
			}, {
				description: "Firmas de Correo",
				title: "Firmas de Correo",
				action: () => this.rootPanel(new JSF.AonJsfMailSignature())
			}, {
				description: "Contactos",
				title: "Contactos",
				action: () => this.rootPanel(new JSF.AonJsfMailContact())
			}],
			filter: () => this.isNotDomainManagementAvailable()
		}
		*/
		];
	}

}
if (!window.customElements.get(TAG.AON_CONFIGURATION_MENU)) {
	window.customElements.define(TAG.AON_CONFIGURATION_MENU, AonConfigurationMenu);
}