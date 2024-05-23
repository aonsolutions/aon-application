import { AonElement } from 'aonsolutions/components/AonElement.js';
import { AonHeader } from 'aonsolutions/modules/aon-header.js';
import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import * as LS from 'aonsolutions/services/localStorageService.js';
import { AonNewMenu } from './aon-new-menu.js';
import { AonConfig } from './aon-config.js';
import { AonHelp } from './aon-help.js';
import { AonRightPanel } from './aon-right-panel.js';
import { AonLoginPanel } from './aon-login-panel.js';
import { AonNotificationPanel } from './aon-notification-panel.js';
import { clearAuth } from 'aonsolutions/services/service.js';

export class AonAccountingMenu extends AonElement {

	AON_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;

	constructor () {
		super();
	}

	connectedCallback () {
		this.clear();
		this.initialize();
		this.build();
	}

	initialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
	}

	build() {
        
	}
	
}
if(!window.customElements.get(TAG.AON_ACCOUNTING_MENU)){
	window.customElements.define(TAG.AON_ACCOUNTING_MENU, AonAccountingMenu);
}
