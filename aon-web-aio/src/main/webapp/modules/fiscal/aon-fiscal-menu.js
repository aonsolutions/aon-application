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
        this.options = [{
            title: 'IVA',
            options: [ {
                description: "Modelo 303",
                action: () => alert("description")
            },{
                description: "Modelo 347",
                action: () => alert("description")
            },{
                description: "Modelo 349",
                action: () => alert("description")
            },{
                description: "Modelo 390",
                action: () => alert("description")
            }]
        },{
            title: 'IRPF',
            options: [{
                description: "Modelo 111",
                action: () => alert("description")
            },{
                description: "Modelo 115",
                action: () => alert("description")
            },{
                description: "Modelo 123",
                action: () => alert("description")
            },{
                description: "Modelo 130",
                action: () => alert("description")
            }]
        },{
            title: 'IRPF Anual',
            options: [{
                description: "Modelo 131",
                action: () => alert("description")
            },{
                description: "Modelo 180",
                action: () => alert("description")
            },{
                description: "Modelo 184",
                action: () => alert("description")
            },{
                description: "Modelo 190",
                action: () => alert("description")
            },{
                description: "Modelo 193",
                action: () => alert("description")
            }]
        },{
            title: 'Otros',
            options: [{
                description: "Modelo 202",
                action: () => alert("description")
            },{
                description: "Modelo 200",
                action: () => alert("description")
            },{
                description: "Modelo SII",
                action: () => alert("description")
            },{
                description: "Modelo 140",
                action: () => alert("description")
            },{
                description: "Modelo 390",
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