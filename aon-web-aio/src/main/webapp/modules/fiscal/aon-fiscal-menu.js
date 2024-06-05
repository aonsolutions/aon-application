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
            title: 'IVA',
            options: [ {
                description: "Modelo 303 ",
                description2: " |AEAT |Bizk",
                title: "IVA Autoliquidación",
                action: () => alert("description")
            },{
                description: "Modelo 347 ",
                description2: " |AEAT |Álava |Bizk |Gipu |Navarra",
                title: "Declaración anual operaciones con terceras personas",
                action: () => alert("description")
            },{
                description: "Modelo 349 ",
                description2: " |AEAT |Álava |Bizk |Gipu |Navarra",
                title: "Declaración recapitulativas de operaciones intracomunitarias",
                action: () => alert("description")
            },{
                description: "Modelo 390 ",
                description2: " |Álava |Bizk |Gipu",
                title: "Declaración resumen anual IVA",
                action: () => alert("description")
            }]
        },{
            title: 'IRPF',
            options: [{
                description: "Modelo 110/111 ",
                description2: " |AEAT |Álava |Bizk |Gipu",
                title: "Retenciones e ingresos a cuenat sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => alert("description")
            },{
                description: "Modelo 115 ",
                description2: " |AEAT |Álava |Bizk |Gipu",
                title: "Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos",
                action: () => alert("description")
            },{
                description: "Modelo 123 ",
                description2: " |AEAT |Álava |Bizk |Gipu",
                title: "Retención e ingreso a cuenta sobre determinadas rendimientos del capital mobiliario o determinadas rentas",
                action: () => alert("description")
            },{
                description: "Modelo 130 ",
                description2: " |AEAT |Bizk",
                title: "Pago fraccionado. Empresarios y profesionales en estimación directa",
                action: () => alert("description")
            }]
        },{
            title: 'IRPF Anual',
            options: [{
                description: "Modelo 131 ",
                description2: " |AEAT",
                title: "Pago fraccionado. Empresarios y profesionales en estimación objetiva",
                action: () => alert("description")
            },{
                description: "Modelo 180 ",
                description2: " |AEAT |Álava |Bizk |Gipu |Navarra",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos",
                action: () => alert("description")
            },{
                description: "Modelo 184 ",
                description2: " |AEAT |Álava |Bizk |Gipu |Navarra",
                title: "Declaración anual. Entidades en régimen de atribución rentas",
                action: () => alert("description")
            },{
                description: "Modelo 190 ",
                description2: " |AEAT |Álava |Bizk |Gipu |Navarra",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta",
                action: () => alert("description")
            },{
                description: "Modelo 193 ",
                description2: " |AEAT |Álava |Bizk |Gipu |Navarra",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas",
                action: () => alert("description")
            }]
        },{
            title: 'Otros',
            options: [{
                description: "Modelo 202 ",
                description2: " |AEAT",
                title: "Impuesto Sociedades. Pago fraccionado",
                action: () => alert("description")
            },{
                description: "Modelo 200 ",
                description2: " |AEAT",
                tite: "Impuesto sobre Sociedades",
                action: () => alert("description")
            },{
                description: "Modelo SII ",
                description2: " |AEAT |Álava |Bizk |Gipu |Navarra",
                title: "Suministro Inmediato de Información",
                action: () => alert("description")
            },{
                description: "Modelo 140 ",
                description2: " |Bizk",
                title: "Libro-registro de operaciones económicas de personas físicas",
                action: () => alert("description")
            },{
                description: "Modelo 390 ",
                description2: " |AEAT",
                title: "Declaración resumen anual IVA",
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