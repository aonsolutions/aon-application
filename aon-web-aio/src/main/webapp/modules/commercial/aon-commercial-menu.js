import { MSG, TAG } from '../../environments/environments.js';
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';

export class AonCommercialMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
		this.comercialInitialize()
    }

    connectedCallback () {
        this.clear();
        this.initialize();
        this.build();
    }

    comercialInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = MSG.LAST_POTENTIALS;
        this.new = MSG.NEW_BUDGET;
        this.cardData={
            title: MSG.ACTIVITY,
            info:[MSG.BUDGETS_PENDING, MSG.OPEN_OPERATIONS, MSG.EXPIRED_APPOINTMENTS]
        };
        this.selectOptions= [{
            title: MSG.TARGET,
            action: () => this.rootPanel(new JSF.AonJsfTarget())
        },{
            title: "Operación comercial",
            action: () => this.rootPanel(new JSF.AonJsfProjectCommercial())
        },{
            title: MSG.BUDGET,
            action: () => this.rootPanel(new JSF.AonJsfOffer())
        }];
        this.options = [{
            title: MSG.COMMERCIAL_ACTIVITY,
            options: [ {
                description: "Operación Comercial",
                title: "Operación Comercial",
                action: () => this.rootPanel(new JSF.AonJsfProjectCommercial())
            },{
                description: "Agenda Comercial",
                title: "Agenda Comercial",
                action: () => this.rootPanel(new JSF.AonJsfCommercialTracking())
            },{
                description: "Tipos de Actividades Comerciales",
                title: "Agenda Comercial",
                action: () => this.rootPanel(new JSF.AonJsfCommercialActivity())
            }]
        },{
            title: MSG.BUDGETS,
            options: [{
                description: "Presupuesto de Ventas",
                title: "Presupuesto de Ventas",
                action: () => this.rootPanel(new JSF.AonJsfOffer())
            },{
                description: "Pedidos de Venta",
                title: "Pedidos de Venta",
                action: () => this.rootPanel(new JSF.AonJsfSale())
            },{
                description: "Condiciones Comerciales",
                title: "Condiciones Comerciales",
                action: () => this.rootPanel(new JSF.AonJsfCommercialTerm())
            }]
        },{
            title: MSG.MASTERS,
            options: [{
                description: "Agentes Comerciales",
                title: "Agentes Comerciales",
                action: () => GWT.iLoad(GWT.SELLER_MODULE)
                //action: () => this.rootPanel(new JSF.AonJsfSeller())
            },{
                description: "Clientes Potenciales",
                title: "Clientes Potenciales",
                action: () => this.rootPanel(new JSF.AonJsfTarget())
            },{
                description: "Deduplicación de Clientes Potenciales",
                title: "Deduplicación de Clientes Potenciales",
                action: () => this.rootPanel(new JSF.AonJsfTargetDeduplication())
            }]
        },{
            title: MSG.DASHBOARD,
            options: [{
                description: "CM de Agentes Comerciales",
                title: "CM de Agentes Comerciales",
                action: () => this.rootPanel(new JSF.AonJsfCommercialStatSeller())
            },{
                description: "CM de Clientes Potenciales",
                title: "CM de Clientes Potenciales",
                action: () => this.rootPanel(new JSF.AonJsfCommercialStatTarget())
            },{
                description: "CM de Productos",
                title: "CM de Productos",
                action: () => this.rootPanel(new JSF.AonJsfCommercialStatProduct())
            },{
                description: "CM de Categorias",
                title: "CM de Categorias",
                action: () => this.rootPanel(new JSF.AonJsfCommercialStatCategory())
            }]
        },{
            title: MSG.REPORTS,
            options: [{
                description: "Presupuestos por Categoria y Producto",
                title: "Presupuestos por Categoria y Producto",
                action: () => this.rootPanel(new JSF.AonJsfCommercialCategoryStat())
            },{
                description: "Presupuestos por Zona",
                title: "Presupuestos por Zona",
				action: () => this.rootPanel(new JSF.AonJsfCommercialGeozoneStat())
            },{
                description: "Presupuestos por Agente Comercial",
                title: "Presupuestos por Agente Comercial",
				action: () => this.rootPanel(new JSF.AonJsfCommercialSellerStat())
            },{
                description: "Presupuestos por Cliente Potencial",
                title: "Presupuestos por Cliente Potencial",
				action: () => this.rootPanel(new JSF.AonJsfCommercialTargetStat())
            },{
                description: "Presupuestos por Producto",
                title: "Presupuestos por Producto",
                action: () => this.rootPanel(new JSF.AonJsfCommercialProductStat())
            }]
        },{
            title: MSG.COMMISSIONS,
            options: [{
                description: "Comisiones",
                title: "Comisiones",
                action: () => GWT.iLoad(GWT.COMMISSION_CALC)
            },{
                description: "Cálculo de Comisiones",
                title: "Cálculo de Comisiones",
                action: () => this.rootPanel(new JSF.AonJsfCommissionCalc())
            },{
                description: "Control de Comisiones Calculadas",
                title: "Control de Comisiones Calculadas",
                action: () => this.rootPanel(new JSF.AonJsfOfferDetailCommission())
            },{
                description: "Tipos de Comisión",
                title: "Tipos de Comisión",
                action: () => this.rootPanel(new JSF.AonJsfCommissionType())
            },{
                description: "Definición de tramos de comisiones",
                title: "Definición de tramos de comisiones",
                action: () => this.rootPanel(new JSF.AonJsfCommission())
            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_COMMERCIAL_MENU)){
    window.customElements.define(TAG.AON_COMMERCIAL_MENU, AonCommercialMenu);
}