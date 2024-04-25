import { AonElement } from 'aonsolutions/components/AonElement.js';

import { AonHeader } from 'aonsolutions/modules/aon-header.js';

import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from 'aonsolutions/environments/environments.js'; 

import * as LS from 'aonsolutions/services/localStorageService.js';
import { AonNewMenu } from './aon-new-menu.js';
import { AonConfig } from './aon-config.js';
import { AonHelp } from './aon-help.js';

export class AonHome extends AonElement {

	AON_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;
	RIGHT_PANEL_HELP;

	constructor () {
		super();
	}

	connectedCallback () {
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
		this.RIGHT_PANEL_HELP = 'rightPanelHelp'
	}

	build() {
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

		let aonMenu = new AonNewMenu();
		aonMenu.id = this.AON_MENU;
		aonMenu.className = CSS.AON_MENU;

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
			let app = e.detail; 
			console.log(JSON.stringify(e.detail));
			
			if ( !app.home ){
				
				let appEl = aonMenu.buildApp(app, 
				{
					height: '32px',
					color: '#ffffff',
					flexDirection: 'row'
				}
				);
				aonHeader.setApp(appEl);
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

		let headerConfig = this.getElement('aonHeaderConfig');
		let panelConfig = new AonConfig();
		this.appendChild(panelConfig);
		panelConfig.addEventListener(EVENT.CLOSE, () => {
			rootPanel.style.marginRight = '0px';		
		});
		if(headerConfig)
			headerConfig.addEventListener(EVENT.CLICK, () => {
				rootPanel.style.marginRight = '321px';
				panelConfig.open();
		});

		let headerHelp = this.getElement('aonHeaderHelp');
		if(headerHelp)
			headerHelp.addEventListener(EVENT.CLICK, () => {
				let panelHelp = new AonHelp();
				this.appendChild(panelHelp);	
				panelHelp.addEventListener(EVENT.CLOSE, () =>{
					rootPanel.style.marginRight = '0px';	
					this.removeChild(panelHelp);
				});
				rootPanel.style.marginRight = '321px';
				panelHelp.open();
		})

		this.getElement('aonMenuSidenav').style.with = '0px';
		this.getElement('aonMenuList').style.visibility = "hidden";
		this.getElement('aonMenuSidenav').style.display = "none";
		rootPanel.style.marginLeft = '0px';
	}
	

	customize(){
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
