import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

export class AonFiscalMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
    }

    connectedCallback () {
        this.clear();
        this.comercialInitialize()
        this.initialize();
        this.build();
        this.setTitle("Opciones fiscales");
    }

    comercialInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = "Matriz fiscal";
        this.new = "Nueva Acción"
        this.cardData={
            title: "Facturas",
            info:["con IRPF profesional", "con IRPF alquiler", "Intracomunitarias", "Extracomunitarias"]
        };
        this.selectOptions= [{
            title: "Modelo 303",
            action : () => alert("description")
        },{
            title: "Modelo 111",
            action : () => alert("description")
        },{
            title: "Modelo 115",
            action : () => alert("description")
        }];
        this.options = [{
            title: 'IVA AEAT',
            options: [ {
                description: "Modelo 303 ",
                title: "IVA Autoliquidación",
                action: () => alert("description")
            },{
                description: "Modelo 349 ",
                title: "Declaración recapitulativas de operaciones intracomunitarias",
                action: () => alert("description")
            },{
                description: "Modelo 390 ",
                title: "Declaración resumen anual IVA",
                action: () => alert("description")
            },{
                description: "Modelo 347 ",
                title: "Declaración anual operaciones con terceras personas",
                action: () => alert("description")
            },{
                description: "Declaración SII ",
                title: "Suministro Inmediato de Información",
                action: () => alert("description")
            }]
        },{
            title: 'IRPF AEAT',
            options: [{
                description: "Modelo 111 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, permios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => alert("description")
            },{
                description: "Modelo 115 ",
                title: "Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos",
                action: () => alert("description")
            },{
                description: "Modelo 123 ",
                title: "Retención e ingreso a cuenta sobre determinadas rendimientos del capital mobiliario o determinadas rentas",
                action: () => alert("description")
            },{
                description: "Modelo 180 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos",
                action: () => alert("description")
            },{
                description: "Modelo 184 ",
                title: "Declaración anual. Entidades en régimen de atribución de rentas",
                action: () => alert("description")
            },{
                description: "Modelo 190 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta",
                action: () => alert("description")
            },{
                description: "Modelo 193 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientosdel capital mobiliario, IS e IRNR sobre determinadas rentas",
                action: () => alert("description")
            }]
        },{
            title: 'AEAT',
            options: [{
                description: "Modelo 130 ",
                description2: " |Profes./Empresar.",
                title: "IRPF. Pago fraccionado. Empresarios y profesionales en estimación directa",
                action: () => alert("description")
            },{
                description: "Modelo 131 ",
                description2: " |Profes./Empresar.",
                title: "Pago fraccionado. Empresarios y profesionales en estimación objetiva",
                action: () => alert("description")
            },{
                description: "Modelo 200 ",
                description2: " |Sociedades",
                title: "Impuesto sobre Sociedades",
                action: () => alert("description")
            },{
                description: "Modelo 202 ",
                description2: " |Sociedades",
                title: "Impuesto Sociedades. Pago fraccionado",
                action: () => alert("description")
            }]
        },{
            title: 'IVA Forales',
            options: [{
                description: "Modelo 303 ",
                description2: " |320 Gipuzkoa",
                title: "IVA. Autoliquidación",
                action: () => alert("description")
            },{
                description: "Modelo 349 ",
                description2: " |AEAT",
                tite: "Declaración recapitulativa de operaciones intracomunitarias",
                action: () => alert("description")
            },{
                description: "Modelo 390 ",
                title: "Declaración resume anual IVA",
                action: () => alert("description")
            },{
                description: "Modelo 347 ",
                title: "Declaración anual operaciones con terceras personas",
                action: () => alert("description")
            },{
                description: "Declaración SII ",
                title: "Suministro Inmediato de Información",
                action: () => alert("description")
            }]
        },{
            title: 'IRPF Forales',
            options: [{
                description: "Modelo 110/111 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => alert("description")
            },{
                description: "Modelo 115 ",
                tite: "Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanes",
                action: () => alert("description")
            },{
                description: "Modelo 123 ",
                title: "Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas",
                action: () => alert("description")
            },{
                description: "Modelo 180 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos",
                action: () => alert("description")
            },{
                description: "Modelo 184 ",
                title: "Declaración anual. Entidades en régimen de atribución de rentas",
                action: () => alert("description")
            },{
                description: "Modelo 190 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta",
                action: () => alert("description")
            },{
                description: "Modelo 193 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas",
                action: () => alert("description")
            }]
        },{
            title: 'LROE Bizkaia',
            options: [{
                description: "Modelo 140 ",
                title: "Libro-registro de operaciones económicas de personas físicas",
                action: () => alert("description")
            },{
                description: "Modelo 240 ",
                tite: "Libro-registro de operaciones económicas de sociedades",
                action: () => alert("description")
            }]
        },{
            title: 'IVA Navarra',
            options: [{
                description: "Modelo F69 ",
                title: "Autoliquidación",
                action: () => alert("description")
            },{
                description: "Modelo 349 ",
                tite: "Declaración recapitulativa de operaciones intracomunitarias",
                action: () => alert("description")
            },{
                description: "Modelo 111 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => alert("description")
            },{
                description: "Modelo 357 ",
                title: "Declaración anual operaciones con terceras personas",
                action: () => alert("description")
            },{
                description: "Declaración SII ",
                title: "Suministro Inmediato de Información",
                action: () => alert("description")
            }]
        },{
            title: 'IRPF Navarra',
            options: [{
                description: "Modelo 745/715 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => alert("description")
            },{
                description: "Modelo 759/760 ",
                tite: "Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos",
                action: () => alert("description")
            },{
                description: "Modelo 716 ",
                title: "Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas",
                action: () => alert("description")
            },{
                description: "Modelo 180 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimiento procedentes de arrendamiento de inmuebles Urbanos",
                action: () => alert("description")
            },{
                description: "Modelo 184 ",
                title: "Declaración anual. Entidades en régmien de atribución de rentas",
                action: () => alert("description")
            },{
                description: "Modelo 184 ",
                title: "Declaración anual. Entidades en régmien de atribución de rentas",
                action: () => alert("description")
            },{
                description: "Modelo 190 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta",
                action: () => alert("description")
            },{
                description: "Modelo 193 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas",
                action: () => alert("description")
            }]
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_FISCAL_MENU)){
    window.customElements.define(TAG.AON_FISCAL_MENU, AonFiscalMenu);
}