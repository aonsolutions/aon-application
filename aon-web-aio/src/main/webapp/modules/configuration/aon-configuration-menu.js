import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';


export class AonConfigurationMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
    }

    connectedCallback () {
        this.clear();
        this.configurationInitialize()
        this.initialize();
        this.build();
        this.setTitle("Opciones de configuración");
    }

    configurationInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = "Últimas acciones";
        this.new = "Nueva Acción";
        this.cardData={
			title: "Actividad",
			info:["Pedidos ptes.", "Cuotas abiertas", "Fras. sin contabilizar"]
		};
        this.selectOptions= [];
        this.options = [{
            title: 'Configuración correo',
            options: [ {
                description: "Cuentas de Correo",
                title: "Cuentas de Correo",
                action: () => this.rootPanel(new JSF.AonJsfMailAccount())
            },{
                description: "Firmas de Correo",
                title: "Firmas de Correo",
                action: () => this.rootPanel(new JSF.AonJsfMailSignature())
            },{
                description: "Contactos",
                title: "Contactos",
                action: () => this.rootPanel(new JSF.AonJsfMailContact())
            }]
        },{
            title: 'Datos de Empresa',
            options: [{
                description: "Configuración Global",
                title: "Configuración Global",
                action: () => this.rootPanel(new JSF.AonJsfGlobalConfig())
            },{
                description: "Descarga de Empresas",
                title: "Descarga de Empresas",
                action: () => window.open("https://www.aonsolutions.es/solicitud-copia-de-seguridad-datos-empresa/", "_blank")
            }]
        },{
            title: 'Tablas Principales',
            options: [{
                description: "Clientes",
                title: "Clientes",
                action: () => this.rootPanel(new JSF.AonJsfCustomer())
            },{
                description: "Grupos de Facturación",
                title: "Grupos de Facturación",
                action: () => this.rootPanel(new JSF.AonJsfInvoicingGroup())
			},{
			    description: "Proveedores",
			    title: "Proveedores",
			    action: () => this.rootPanel(new JSF.AonJsfSupplier())
			},{
			    description: "Acreedores",
			    title: "Acreedores",
			    action: () => this.rootPanel(new JSF.AonJsfCreditor())
			},{
			    description: "Productos",
			    title: "Productos",
			    action: () => this.rootPanel(new JSF.AonJsfProduct())
			},{
			    description: "Gastos",
			    title: "Gastos",
			    action: () => this.rootPanel(new JSF.AonJsfExpense())
			},{
                description: "Clientes Potenciales",
                title: "Clientes Potenciales",
                action: () => this.rootPanel(new JSF.AonJsfTarget())
            },{
                description: "Agentes Comerciales",
                title: "Agentes Comerciales",
                action: () => this.rootPanel(new JSF.AonJsfSeller())
            }
			]
        },{
            title: 'Tablas Auxiliares de Gestión',
            options: [{
                description: "Formas de Pago",
                title: "Formas de Page",
                action: () => this.rootPanel(new JSF.AonJsfPayMethod())
            }/*,{
                description: "Asignación contable por tipo de forma de pago",
                title: "Asignación contable por tipo de forma de pago",
                action: () => alert("Asignación contable por tipo de forma de pago")
            }*/,{
                description: "Conceptos Bancarios",
                title: "Conceptos Bancarios",
				action: () => this.rootPanel(new JSF.AonJsfBankConcept())
            },{
                description: "Series",
                title: "Series",
				action: () => this.rootPanel(new JSF.AonJsfSeries())
            },{
                description: "Impuestos",
                title: "Impuestos",
				action: () => this.rootPanel(new JSF.AonJsfTax())
            },{
                description: "País/Provincia",
                title: "País/Provincia",
                action: () => this.rootPanel(new JSF.AonJsfGeotree())
            },{
                description: "Segmentación",
                title: "Segmentación",
                action: () => this.rootPanel(new JSF.AonJsfSegment())
            }]
        },{
            title: 'Tablas Auxiliares de Productos',
            options: [{
                description: "Etiquetas de Productos",
                title: "Etiquetas de Productos",
				action: () => this.rootPanel(new JSF.AonJsfProductTag())
            }/*,{
                description: "Etiquetas de Formatos/Medidas",
                title: "Etiquetas de Formatos/Medidas",
                action: () => alert("Etiquetas de Formatos/Medidas")
            }*/,{
                description: "Categorías",
                title: "Categorías",
                action: () => this.rootPanel(new JSF.AonJsfProductCategory())
            },{
                description: "Marcas",
                title: "Marcas",
				action: () => this.rootPanel(new JSF.AonJsfBrand())
            },{
                description: "Tarifas",
                title: "Tarifas",
				action: () => this.rootPanel(new JSF.AonJsfTariff())
            },{
                description: "Catálogos",
                title: "Catálogos",
				action: () => this.rootPanel(new JSF.AonJsfCatalogue())
            }]
        },{
            title: 'Conect@',
            options: [{
                description: "Carga de datos desde ficheros Excel",
                title: "Carga de datos desde ficheros Excel",
				action: () => GWT.iLoad(GWT.IMPORT)
            },{
                description: "Gestión Plantillas para carga de datos",
                title: "Gestión Plantillas para carga de datos",
				action: () => GWT.iLoad(GWT.TEMPLATE)
            },{
                description: "Carga de datos desde ficheros CSV",
                title: "Carga de datos desde ficheros CSV",
				action: () => this.rootPanel(new JSF.AonJsfLoader())
            },{
                description: "Descarga de datos en formato Excel",
                title: "Descarga de datos en formato Excel",
				action: () => GWT.iLoad(GWT.INVOICE_REPORT)
            }]
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_CONFIGURATION_MENU)){
    window.customElements.define(TAG.AON_CONFIGURATION_MENU, AonConfigurationMenu);
}