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
                description: MSG.GLOBAL_CONFIGURATION,
                title: MSG.GLOBAL_CONFIGURATION,
                action: () => this.rootPanel(new JSF.AonJsfGlobalConfig())
            }, {
				description: MSG.CONSOLE_ADMIN,
                title: MSG.CONSOLE_ADMIN,
                action: () => GWT.iLoad(GWT.CONSOLE)
            }]
        }, {
            title: MSG.COMPANY_DATA_SECTION,
            options: [{
                description: MSG.COMPANY_DOWNLOAD,
                title: MSG.COMPANY_DOWNLOAD,
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