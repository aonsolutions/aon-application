import { AonElement } from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { ConsoleSidenav, LINK_DOMAINS } from './ConsoleOptions.js';

import Apps from '../../services/app.js';
import { AonLinkDomains } from '../domains/aon-link-domains.js';

export class AonConsole extends AonElement {

	AON_CONSOLE;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
    	this.build();
 	}

	initialize(){
		this.AON_CONSOLE = CONSTANT.AON_CONSOLE;
	}

 	build() {
		this.createApplication(this.AON_CONSOLE, MSG.CONSOLE, new AonApplication());
		this.buildSidenav();
	}

	buildSidenav() {
		if(this.isMobile()){
			this.getApplication().addMobileSidenavHeader(Apps.CONSOLE);
		}
		this.getApplication().addEventListener(EVENT.SELECT_OPTION, 
			(e) => this.selectOption(e.detail));
		
		this.buildUtilitiesOptions();
	}

	buildUtilitiesOptions() {
		this.getApplication().addSidenavOptions3(ConsoleSidenav.UTILITIES);
	}

	selectOption(option) {
		switch(option.id){
		case LINK_DOMAINS.id:
			this.aonLinkDomains();
			break;
		default:
			this.aonLinkDomains();
			break;
		}
	}

	aonLinkDomains() {
		// let d = this.getApplication().getDialog();
		// d.clear();
		// if(!this.isMobile())d.width = '400px';
		// d.setTitle(MSG.LINK_DOMAINS);
		// d.setContentHTML(MSG.IN_DEVELOPMENT);
		// d.addAcceptAction(() => {});
		// d.open();
		this.getApplication().setContent(new AonLinkDomains());
	}

}
if(!window.customElements.get(TAG.AON_CONSOLE)){
	window.customElements.define(TAG.AON_CONSOLE, AonConsole);
}

