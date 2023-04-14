import { AonElement } from '../components/AonElement.js';
import { AonIconButton } from '../components/aon-icon-button.js';
import { AonHeader } from './aon-header.js';
import { AonMenu } from './aon-menu.js';
import { AonNewMobileHeader } from './aon-new-mobile-header.js';
import { AonMobileMenu } from './aon-mobile-menu.js';

import { CSS, EVENT, MATERIAL_ICONS, TAG } from '../environments/environments.js'; 

import * as LS from '../services/localStorageService.js';

export class AonHome extends AonElement {

	AON_HEADER;
	ROOT_PANEL;
	AON_MENU;
	AON_MOBILE_MENU;
	AON_SHOW_MENU;
	AON_SHOW_MENU_BUTTON;

	constructor () {
		super();
	}

	connectedCallback () {
		this.clear();
		this.initialize();

		if(this.isMobile())
			this.buildMobile();
		else this.build();
	}

	initialize() {
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.AON_MENU = 'aonMenu';
		this.AON_MOBILE_MENU = 'aonMobileMenu';
		this.AON_SHOW_MENU = 'aonShowMenu';
		this.AON_SHOW_MENU_BUTTON = 'aonShowMenuButton';
	}

	build() {
		let aonMenu = new AonMenu();
		aonMenu.id = this.AON_MENU;
		aonMenu.className = CSS.AON_MENU;

		let aonShowMenu = this.createElement(TAG.SPAN);
		aonShowMenu.id = this.AON_SHOW_MENU;
		aonShowMenu.className = CSS.AON_SHOW_MENU;
		aonShowMenu.addEventListener(EVENT.CLICK, () => {
			if(LS.getDomainId())
				aonMenu.toogle();
		});

		let aonShowMenuButton = new AonIconButton();
		aonShowMenuButton.id = this.AON_SHOW_MENU_BUTTON
		aonShowMenuButton.icon = MATERIAL_ICONS.KEYBOARD_ARROW_LEFT;
		aonShowMenuButton.noHover = true;
		aonShowMenuButton.title = "Mostrar Menu";
		aonShowMenuButton.style.display = 'none';
		aonShowMenu.appendChild(aonShowMenuButton);
		this.appendChild(aonShowMenu);

		let aonHeader = new AonHeader();
		aonHeader.id = this.AON_HEADER;
		this.appendChild(aonHeader);


		this.appendChild(aonMenu);

		let rootPanel = this.createElement(TAG.DIV);
		rootPanel.id = this.ROOT_PANEL;
		rootPanel.className = "rootPanel";
		this.appendChild(rootPanel);
	}

	buildMobile() {
		let aonMobileHeader = new AonNewMobileHeader();
		aonMobileHeader.id = this.AON_HEADER;
		this.appendChild(aonMobileHeader);

		let rootPanel = this.createElement(TAG.DIV);
		rootPanel.id = this.ROOT_PANEL;
		rootPanel.className = CSS.AON_MOBILE_ROOT_PANEL;
		this.appendChild(rootPanel);

		let aonMobileMenu = new AonMobileMenu();
		aonMobileMenu.id = this.AON_MOBILE_MENU;
		this.appendChild(aonMobileMenu);
	}

	showMenu(bool) {
		if(this.isMobile()) {
			let rootPanel = this.getElement(this.ROOT_PANEL);
			rootPanel.style.bottom = '60px';
			let aonMobileMenu = this.getElement(this.AON_MOBILE_MENU);
			if(aonMobileMenu) aonMobileMenu.style.height = '60px';
		} else {
			let aonShowMenu = this.getElement(this.AON_SHOW_MENU);
			aonShowMenu.style.display = bool ? 'block' : 'none';
		}
	}

}
window.customElements.define('aon-home', AonHome);
