import { AonElement } from '../../components/AonElement.js';
import { CSS, TAG } from '../../environments/environments.js'; 
import { AonMobileMenu } from '../../modules/aon-mobile-menu.js';
import { AonPaturpatHome } from './aon-paturpat-home.js';
import { AonPaturpatMenu } from './aon-paturpat-menu.js';

import '../../css/aon.css';

export class AonPaturpat extends AonElement {

	MOBILE_ROOT_PANEL = 'mobileRootPanel';

	constructor () {
		super();
	}

	connectedCallback () {
		this.clear();
		this.initialize();
		this.build();
	}

	initialize() {
	
	}

	build() {
		document.body.className = 'aonBody';
		document.body.style.backgroundColor = '#effbf4';
		document.body.style.margin = '0px';
		document.documentElement.style.setProperty('--aonBlue', '#005f2c');
		document.documentElement.style.setProperty('--accent-color', '#005f2c');

		let rootPanel = this.createDiv(this.MOBILE_ROOT_PANEL, CSS.AON_MOBILE_ROOT_PANEL);
		rootPanel.style.top = '0px';
		this.appendChild(rootPanel);
		rootPanel.appendChild(new AonPaturpatHome());

		let aonMobileMenu = new AonPaturpatMenu();
		this.appendChild(aonMobileMenu);
	}
}

if (!window.customElements.get(TAG.AON_PATURPAT)) {
	window.customElements.define(TAG.AON_PATURPAT, AonPaturpat);
}
