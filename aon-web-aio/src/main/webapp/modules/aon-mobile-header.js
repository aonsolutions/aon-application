import {AonElement} from '../components/AonElement.js';
import {mobileAction} from  '../services/service.js';
import '../components/aon-icon-button.js';
import '../components/aon-dialog-menu.js';
import './configuration/aon-configuration.js';
import './company/aon-mobile-desktop.js';
import { CSS, EVENT, MATERIAL_ICONS, TAG } from '../environments/environments.js';
import { AonIconButton } from '../components/aon-icon-button.js';
import { AonDialogMenu } from '../components/aon-dialog-menu.js';
import { changeStatusBarColor } from '../services/actionService.js';

import * as LS from '../services/localStorageService.js';
import * as UA from '../services/userAgentService.js';
import { AonMobileParent } from './company/aon-mobile-parent.js';

export class AonMobileHeader extends AonElement {

	COMPANY_LIST;
	parent; // boolean

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		
		this.build();
  	}

	isParent() {
		return this.parent;
	}

	initialize() {
		this.parent = true;
		this.id = this.id || 'aonHeader';
		this.WEB = this.id + 'Web';
		this.LOGO = this.id + 'Logo';
		this.COMPANY_LIST = this.id + 'CompanyList';
		this.COMPANY_LIST_BUTTON = this.COMPANY_LIST + 'Button';
		this.COMPANY = this.id + 'Company';
		this.DIALOG_MENU = this.id  + 'DialogMenu';
	}

	build() {
		let div = this.createElement(TAG.DIV);
		div.id = this.WEB;
		div.className = CSS.AON_MOBILE_HEADER;
		
		if(!this.isParent()) {
			div.style.backgroundColor = '#0f172a';
		}
			
		this.appendChild(div);

		let spanLogo = this.createElement(TAG.SPAN);
		let logo = this.createElement(TAG.IMG);
		logo.id = this.LOGO;
		spanLogo.style.display = this.isParent() ? 'block' : 'none';
		spanLogo.className = CSS.AON_MOBILE_HEADER_LOGO;
		spanLogo.appendChild(logo);	
		div.appendChild(spanLogo);

		let spanCompany = this.createElement(TAG.SPAN);
		spanCompany.id = this.COMPANY;
		spanCompany.className = CSS.AON_MOBILE_HEADER_COMPANY;
		spanCompany.innerHTML = LS.getCompany() ? LS.getCompany().name : '';	
		spanCompany.style.display = this.isParent() ? 'none' : 'block';
		spanCompany.addEventListener('click', () => {
			this.home();
		});
		div.appendChild(spanCompany);

		let spanCompanyList = this.createElement(TAG.SPAN);
		spanCompanyList.id = this.COMPANY_LIST;
		spanCompanyList.classList.add(CSS.AON_RIGHT_20);
		spanCompanyList.classList.add(CSS.AON_MOBILE_HEADER_BUTTON);
		let companyListButton = new AonIconButton();
		companyListButton.id = this.COMPANY_LIST_BUTTON;
		companyListButton.icon = MATERIAL_ICONS.BUSINESS;
		companyListButton.noHover = true;
		companyListButton.addEventListener(EVENT.CLICK, () => {
			if(!companyListButton.isDisabled()) {
				this.companyOut();
				let mobileParent = new AonMobileParent();
				mobileParent.id = 'aonParent';
				this.rootPanel(mobileParent);
			}
		});
		if(!this.isParent()) companyListButton.color = 'white';
		spanCompanyList.appendChild(companyListButton);
		div.appendChild(spanCompanyList);

		let  dialogMenu = new AonDialogMenu();
		dialogMenu.id = this.DIALOG_MENU;
		this.appendChild(dialogMenu);

		this.buildLogo();
	}

	buildLogo() {
		let aonLogo = this.getElement(this.LOGO);
		const href = window.location.href;       

		aonLogo.src = 'assets/aon-logo.svg';
		aonLogo.style.top = "21px";
		aonLogo.style.width = "123px";
		aonLogo.style.marginLeft = "21px";

		aonLogo.addEventListener('click', () => {
			this.home();
		});
	}

	showCompanyOption(company) {

	}

	companyIn(onlyOne) {
		onlyOne = onlyOne || LS.isOnlyOne();
		let ionicData = { action: "statusBar", statusBar: true};
		if(UA.isAndroidApp()) {
			changeStatusBarColor(ionicData, "#0f172a", true);
		} else mobileAction(ionicData);

		this.parent = false;
		let div = this.getElement(this.WEB);
		div.style.backgroundColor = '#0f172a';

		this.getElement(this.LOGO).style.display = 'none';

		let company = this.getElement(this.COMPANY);
		company.innerHTML = LS.getCompany().name;
		company.style.display = 'block';

		let spanCompany = this.createElement(TAG.SPAN);
		spanCompany.id = this.COMPANY;
		spanCompany.className = CSS.AON_MOBILE_HEADER_COMPANY;
		
		let companyListButton = this.getElement(this.COMPANY_LIST_BUTTON);
		companyListButton.color = 'white';
		if(onlyOne) {
			this.getElement(this.COMPANY_LIST_BUTTON).style.display = 'none';
		}
	}

	companyOut() {
		let ionicData = { action: "statusBar", statusBar: false};
		if(UA.isAndroidApp()) {
			changeStatusBarColor(ionicData, "#FFFFFF",false);
		} else mobileAction(ionicData);

		this.parent = true;
		let div = this.getElement(this.WEB);
		div.style.backgroundColor = 'white';

		this.getElement(this.LOGO).style.display = 'block';
		this.getElement(this.COMPANY).style.display = 'none';
		
		let companyListButton = this.getElement(this.COMPANY_LIST_BUTTON);
		companyListButton.color = '#5f6368';
	}

	home() {
		if(LS.getCompany()) {
			this.companyIn();
			this.rootPanelHtml('<aon-mobile-home id="aonMobileHome"></aon-mobile-home>');
		} else {
			this.companyOut();
			this.rootPanelHtml('<aon-mobile-parent id="aonParent"></aon-mobile-parent>');
		}
	}

}
if(!window.customElements.get(TAG.AON_MOBILE_HEADER)){
  window.customElements.define(TAG.AON_MOBILE_HEADER, AonMobileHeader);
}