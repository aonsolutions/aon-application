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
		this.last = "Últimas acciones";
		this.new = "Nueva Acción";
		this.cardData = {
			title: "Actividad",
			info: ["Pedidos ptes.", "Cuotas abiertas", "Fras. sin contabilizar"]
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

			title: 'Tablas Principales',
			options: [{
				description: "Clientes",
				title: "Clientes",
				action: () => this.rootPanel(new JSF.AonJsfCustomer()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: "Grupos de Facturación",
				title: "Grupos de Facturación",
				action: () => this.rootPanel(new JSF.AonJsfInvoicingGroup()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: "Proveedores",
				title: "Proveedores",
				action: () => this.rootPanel(new JSF.AonJsfSupplier()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: "Acreedores",
				title: "Acreedores",
				action: () => this.rootPanel(new JSF.AonJsfCreditor()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: "Productos",
				title: "Productos",
				action: () => this.getApplication().setContent(new JSF.AonJsfProduct())
				//action: () => this.rootPanel(new JSF.AonJsfProduct())
			}, {
				description: "Gastos",
				title: "Gastos",
				action: () => this.getApplication().setContent(new JSF.AonJsfExpense())
			}, {
				description: "Clientes Potenciales",
				title: "Clientes Potenciales",
				action: () => this.rootPanel(new JSF.AonJsfTarget()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: "Agentes Comerciales",
				title: "Agentes Comerciales",
				action: () => this.rootPanel(new JSF.AonJsfSeller()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: "Consola de facturación",
				title: "Consola de facturación",
				action: () => GWT.iLoad(GWT.INVOICE_CONSOLE),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: "Carga de datos desde ficheros Excel",
				title: "Carga de datos desde ficheros Excel",
				action: () => GWT.iLoad(GWT.IMPORT),
				filter: () => this.isNotDomainManagementAvailable()
			}
			]
		}, {
			title: 'Tablas Auxiliares de Gestión',
			options: [
				{
					description: MSG.PAYMETHODS,
					title: MSG.PAYMETHODS,
					action: () => GWT.iLoad(GWT.PAY_METHOD, this.getApplication().CONTENT)
				}, {
					description: "Asignación contable por tipo de forma de pago",
					title: "Asignación contable por tipo de forma de pago",
					action: () => this.getApplication().setContent(new JSF.AonJsfPayMethodTypeDetail()),
					nofilter: () => this.isDomainManagementAvailable()
				}, {
					description: "Conceptos Bancarios",
					title: "Conceptos Bancarios",
					action: () => this.getApplication().setContent(new JSF.AonJsfBankConcept())
				}, {
					description: "Series",
					title: "Series",
					action: () => this.getApplication().setContent(new JSF.AonJsfSeries())
				}, {
					description: "Impuestos",
					title: "Impuestos",
					action: () => this.getApplication().setContent(new JSF.AonJsfTax())
				}, {
					description: "País/Provincia",
					title: "País/Provincia",
					action: () => this.getApplication().setContent(new JSF.AonJsfGeotree())
				}, {
					description: "Segmentación",
					title: "Segmentación",
					action: () => this.getApplication().setContent(new JSF.AonJsfSegment())
				}, {
					description: "Tipos de relaciones entre entidades",
					title: "Tipos de relaciones entre entidades",
					action: () => alert("Tipos de relaciones entre entidades"),
					filter: () => false
					//filter: () => this.isDomainManagementAvailable()
				}]
		}, {
			title: 'Tablas Auxiliares de Productos',
			options: [{
				description: "Etiquetas de Productos",
				title: "Etiquetas de Productos",
				action: () => this.rootPanel(new JSF.AonJsfProductTag())
			}, {
				description: "Etiquetas de Formatos/Medidas",
				title: "Etiquetas de Formatos/Medidas",
				action: () => alert("Etiquetas de Formatos/Medidas"),
				filter: () => this.isDomainManagementAvailable()
			}, {
				description: "Categorías",
				title: "Categorías",
				action: () => this.rootPanel(new JSF.AonJsfProductCategory())
			}, {
				description: "Marcas",
				title: "Marcas",
				action: () => this.rootPanel(new JSF.AonJsfBrand())
			}, {
				description: "Tarifas",
				title: "Tarifas",
				action: () => GWT.iLoad(GWT.TARIFF_MODULE)
				// action: () => this.rootPanel(new JSF.AonJsfTariff())
			}, {
				description: "Catálogos",
				title: "Catálogos",
				action: () => this.rootPanel(new JSF.AonJsfCatalogue())
			}],
			filter: () => this.isNotDomainManagementAvailable()
		}, {
			title: 'Utilidades Carga de Datos',
			options: [{
				description: "Carga de datos desde ficheros Excel",
				title: "Carga de datos desde ficheros Excel",
				action: () => GWT.iLoad(GWT.IMPORT),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: "Gestión Plantillas para carga de datos",
				title: "Gestión Plantillas para carga de datos",
				action: () => GWT.iLoad(GWT.TEMPLATE)
			}, {
				description: "Carga de datos desde ficheros CSV",
				title: "Carga de datos desde ficheros CSV",
				action: () => this.rootPanel(new JSF.AonJsfLoader()),
				filter: () => this.isNotDomainManagementAvailable()
			}, {
				description: "Descarga de datos en formato Excel",
				title: "Descarga de datos en formato Excel",
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