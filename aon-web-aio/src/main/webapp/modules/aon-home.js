import { AonElement } from 'aonsolutions/components/AonElement.js';
import { AonHeader } from 'aonsolutions/modules/aon-header.js';
import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js';
import * as LS from 'aonsolutions/services/localStorageService.js';
import { closeSession, getCompanies, getUserNotice, getUser, getAuth, getTimeControl, getCompaniesBySchemas } from 'aonsolutions/services/service.js';
import { AonNewMenu } from './aon-new-menu.js';
import { AonConfig } from './aon-config.js';
import { AonHelp } from './aon-help.js';
import { AonCalendar } from 'aonsolutions/modules/calendar/aon-calendar.js';
import { AonRightPanel } from './aon-right-panel.js';
import { AonLoginPanel } from './aon-login-panel.js';

export class AonHome extends AonElement {

	AON_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;

	constructor() {
		super();
	}

	connectedCallback() {
		this.clear();
		this.initialize();
		this.build();
		this.customize();
	}

	initialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
	}

	build() {
		if (LS.isNewTheme()) {

		}
		let gradiantHeader = this.createElement(TAG.DIV);
		gradiantHeader.className = 'aonRootGradiantHeader';
		this.appendChild(gradiantHeader);
		let gradiantHeaderTop = this.createElement(TAG.DIV);
		gradiantHeaderTop.className = 'aonRootGradiantHeaderTop';
		this.appendChild(gradiantHeaderTop);
		let gradiantHeaderBottom = this.createElement(TAG.DIV);
		gradiantHeaderBottom.className = 'aonRootGradiantHeaderBottom';
		this.appendChild(gradiantHeaderBottom);
		let gradiantHeaderBlur = this.createElement(TAG.DIV);
		gradiantHeaderBlur.className = 'aonRootGradiantHeaderBlur';
		this.appendChild(gradiantHeaderBlur);

		let aonHeader = new AonHeader();
		aonHeader.id = this.AON_HEADER;
		aonHeader.newTheme = true;
		this.appendChild(aonHeader);
		aonHeader.setVisibleHomeButton(false);
		aonHeader.setVisibleCompanyListButton(false);

		let aonMenu = new AonNewMenu();
		aonMenu.id = this.AON_MENU;
		aonMenu.className = CSS.AON_MENU;
		aonMenu.addEventListener(EVENT.AON_APPLICATION_SELECT, (e) => {
			let app = e.detail.app;
			let sidenav = e.detail.sidenav;
			console.log(JSON.stringify(e.detail));
			if (!app.home) {
				let appEl = aonMenu.buildApp(app,
					{
						height: '32px',
						color: '#ffffff',
						flexDirection: 'row'
					}, sidenav);
				aonHeader.buildApp(app, sidenav);
				aonHeader.setVisibleLogo(!appEl);
				aonHeader.setVisibleApp(appEl);
			} else {
				aonHeader.setVisibleApp(false);
				aonHeader.setVisibleLogo(true);
			}
			aonHeader.setColor(app.color && '#fff');
			aonHeader.setBackgroundColor(app.color);
		});

		let rootPanel = this.createElement(TAG.DIV);
		rootPanel.id = this.ROOT_PANEL;
		rootPanel.className = "rootPanel";
		this.appendChild(rootPanel);

		this.appendChild(aonMenu);

		// let rightPanel = new AonRightPanel();
		// rightPanel.addEventListener(EVENT.CLOSE, () => {
		// 	rootPanel.style.marginRight = '0px';
		// });

		// this.appendChild(rightPanel);

		// let editButton = this.getElement('aonRightPanelEditButton');
		// let redirectButton = this.getElement('aonRightPanelRedirectButton');
		// let title = this.getElement('aonRightPanelTitle')
		// if (LS.isRightPanel()) {
		// 	LS.removeRightPanel();
		// 	rootPanel.style.marginRight = '321px';
		// 	rightPanel.clear();
		// 	rightPanel.setContent(new AonConfig());
		// 	rightPanel.setTitle(MSG.CONFIGURATION);
		// 	rightPanel.open();
		// }

		let headerConfig = this.getElement('aonHeaderConfig');
		if (headerConfig) {
			headerConfig.addEventListener(EVENT.CLICK, () => {
				let rightPanel = this.getElement('aonRightPanel');
				let config = this.getElement('aonConfig');
				if (config) {
					rootPanel.style.marginRight = '0px';
					rightPanel.remove();
				} else {
					if (rightPanel) {
						rightPanel.remove();
					}
					rightPanel = new AonRightPanel();
					rightPanel.addEventListener(EVENT.CLOSE, () => {
						rootPanel.style.marginRight = '0px';
					});
					this.appendChild(rightPanel);
					let editButton = this.getElement('aonRightPanelEditButton');
					let redirectButton = this.getElement('aonRightPanelRedirectButton');
					let title = this.getElement('aonRightPanelTitle');
					if (LS.isRightPanel()) {
						LS.removeRightPanel();
						rootPanel.style.marginRight = '321px';
						rightPanel.clear();
						rightPanel.setContent(new AonConfig());
						rightPanel.setTitle(MSG.CONFIGURATION);
						rightPanel.open();
					}
					rootPanel.style.marginRight = '321px';
					rightPanel.clear();
					editButton.style.visibility = 'hidden';
					redirectButton.style.visibility = 'hidden';
					title.style.marginLeft = '10px';
					rightPanel.setContent(new AonConfig());
					rightPanel.setTitle(MSG.CONFIGURATION);
					rightPanel.open();
				}
			});
		}

		let headerHelp = this.getElement('aonHeaderHelp');
		if (headerHelp) {
			headerHelp.addEventListener(EVENT.CLICK, () => {
				let rightPanel = this.getElement('aonRightPanel');
				let help = this.getElement('aonHelp');
				if (help) {
					rootPanel.style.marginRight = '0px';
					rightPanel.remove();
				} else {
					if (rightPanel) {
						rightPanel.remove();
					}
					rightPanel = new AonRightPanel();
					rightPanel.addEventListener(EVENT.CLOSE, () => {
						rootPanel.style.marginRight = '0px';
					});
					this.appendChild(rightPanel);
					let editButton = this.getElement('aonRightPanelEditButton');
					let redirectButton = this.getElement('aonRightPanelRedirectButton');
					let title = this.getElement('aonRightPanelTitle');
					if (LS.isRightPanel()) {
						LS.removeRightPanel();
						rootPanel.style.marginRight = '321px';
						rightPanel.clear();
						rightPanel.setContent(new AonConfig());
						rightPanel.setTitle(MSG.CONFIGURATION);
						rightPanel.open();
					}
					rootPanel.style.marginRight = '321px';
					title.style.marginLeft = '10px';
					editButton.style.visibility = 'hidden';
					redirectButton.style.visibility = 'hidden';
					rightPanel.clear();
					rightPanel.setContent(new AonHelp());
					rightPanel.setTitle(MSG.HELP);
					rightPanel.open();
				}
			});
		}

		let headerUser = this.getElement('aonHeaderUser');
		if (headerUser) {
			headerUser.addEventListener(EVENT.CLICK, () => {
				let rightPanel = this.getElement('aonRightPanel');
				let user = this.getElement('aonLoginPanel');
				if (user) {
					rootPanel.style.marginRight = '0px';
					rightPanel.remove();
				} else {
					if (rightPanel) {
						rightPanel.remove();
					}
					rightPanel = new AonRightPanel();
					rightPanel.addEventListener(EVENT.CLOSE, () => {
						rootPanel.style.marginRight = '0px';
					});
					this.appendChild(rightPanel);
					let editButton = this.getElement('aonRightPanelEditButton');
					let redirectButton = this.getElement('aonRightPanelRedirectButton');
					let title = this.getElement('aonRightPanelTitle');
					if (LS.isRightPanel()) {
						LS.removeRightPanel();
						rootPanel.style.marginRight = '321px';
						rightPanel.clear();
						rightPanel.setContent(new AonConfig());
						rightPanel.setTitle(MSG.CONFIGURATION);
						rightPanel.open();
					}
					rightPanel.clear();
					rightPanel.setContent(new AonLoginPanel());
					rightPanel.setTitle(MSG.USER);
					rootPanel.style.marginRight = '0px';
					editButton.style.visibility = 'visible';
					redirectButton.style.visibility = 'hidden';
					title.style.marginLeft = '39px';
					rightPanel.open("200px", "0px", "0 24px 54px rgba(0,0,0,.15),0 4.5px 13.5px rgba(0,0,0,.08)");
				}
			});
		}
	}

	showMenu(bool) {
		// Only for compatibility
	}

	customize() {
		let aonHeader = this.getElement(this.AON_HEADER);
		let aonSearchDiv = aonHeader.getElement("aon-search-div");
		aonSearchDiv.style.height = '32px';
		aonSearchDiv.style.border = 'none';
		aonSearchDiv.style.borderRadius = '4px';
		aonSearchDiv.style.backgroundColor = '#ffffff';
		aonSearchDiv.style.boxShadow = '0 2px 4px rgba(0,0,0,.14),0 0 2px rgba(0,0,0,.12)';
		aonSearchDiv.style.alignItems = 'center';
	}

}
window.customElements.define('aon-home', AonHome);
