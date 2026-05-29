import { MSG, CSS, EVENT, TAG } from '../../environments/environments.js';
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';


export class AonConsoleMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
		this.configurationInitialize()
    }

    connectedCallback () {
        this.clear();
        this.initialize();
        this.build();
        this.setTitle("Opciones de configuración");
    }

    configurationInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = MSG.LAST_ACTIONS;
        this.new = MSG.NEW_ACTION;
        this.cardData={
			title: MSG.CONSOLE_ADMIN,
			info:[MSG.CONSOLE_ADMIN]
		};
        this.selectOptions= [];
        this.initOptions();
    }

    initOptions() {
        this.options = [, {
            title: MSG.UTILITIES,
            options: [{
                description: "Configuración Global",
                title: "Configuración Global",
                action: () => this.rootPanel(new JSF.AonJsfGlobalConfig())
            }, {
				description: "Consola Administración",
                title: "Consola Administración",
                action: () => GWT.iLoad(GWT.CONSOLE)
            }]
        }, {
            title: MSG.COMPANY_DATA_SECTION,
            options: [{
                description: "Descarga de Empresas",
                title: "Descarga de Empresas",
                action: () => window.open("https://www.aonsolutions.es/solicitud-copia-de-seguridad-datos-empresa/", "_blank")
            }]
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_CONSOLE_MENU)){
    window.customElements.define(TAG.AON_CONSOLE_MENU, AonConsoleMenu);
}