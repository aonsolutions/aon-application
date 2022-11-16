import {AonElement} from '../components/AonElement.js';
import {mobileAction, getTimeControl} from  '../services/service.js';
import '../components/aon-icon-button.js';
import '../components/aon-dialog-menu.js';
import './configuration/aon-configuration.js';
import './company/aon-mobile-desktop.js';
import './notification/aon-notification-icon.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from '../environments/environments.js';
import { AonIconButton } from '../components/aon-icon-button.js';
import { AonDialogMenu } from '../components/aon-dialog-menu.js';
import * as LS from '../services/localStorageService.js';
import { AonMobileProfile } from './user/aon-mobile-profile.js';

export class AonNewMobileHeader extends AonElement {

	COMPANY_LIST;
	activeTimecontrol;
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
		this.activeTimecontrol = false;
		this.parent = true;
		this.id = this.id || 'aonHeader';
		this.WEB = this.id + 'Web';
		this.LOGO = this.id + 'Logo';
		this.USER = this.id + 'User';
		this.USER_BUTTON = this.USER + 'Button';
		this.NOTIFICATION = this.id + 'Notification';
		this.NOTIFICATION_ICON = this.NOTIFICATION + 'Icon';
		this.NOTIFICATION_BUTTON = this.NOTIFICATION + 'Button';
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
			div.style.backgroundColor = '#002469';
		}
			
		this.appendChild(div);

		let spanLogo = this.createElement(TAG.SPAN);
		let logo = this.createElement(TAG.IMG);
		logo.id = this.LOGO;
		spanLogo.style.display = this.isParent() ? 'block' : 'none';
		spanLogo.style.paddingTop = '20px';
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

		let spanUser = this.createElement(TAG.SPAN);
		spanUser.id = this.USER;
		spanUser.classList.add(CSS.AON_RIGHT_20);
		spanUser.classList.add(CSS.AON_MOBILE_HEADER_BUTTON);
		let userButton = new AonIconButton();
		userButton.id = this.USER_BUTTON;
		userButton.icon = MATERIAL_ICONS.ACCOUNT_CIRCLE;
		userButton.noHover = true;
		if(!this.isParent()) userButton.color = 'white';
		spanUser.appendChild(userButton);
		div.appendChild(spanUser);

		let spanCompanyList = this.createElement(TAG.SPAN);
		spanCompanyList.id = this.COMPANY_LIST;
		spanCompanyList.classList.add(CSS.AON_RIGHT_60);
		spanCompanyList.classList.add(CSS.AON_MOBILE_HEADER_BUTTON);
		let companyListButton = new AonIconButton();
		companyListButton.id = this.COMPANY_LIST_BUTTON;
		companyListButton.icon = MATERIAL_ICONS.BUSINESS;
		companyListButton.noHover = true;
		companyListButton.addEventListener(EVENT.CLICK, () => {
			if(!companyListButton.isDisabled()) {
				this.companyOut();
				this.rootPanelHtml('<aon-mobile-parent id="aonParent"></aon-mobile-parent>');
			}
		});
		if(!this.isParent()) companyListButton.color = 'white';
		spanCompanyList.appendChild(companyListButton);
		div.appendChild(spanCompanyList);

		let  dialogMenu = new AonDialogMenu();
		dialogMenu.id = this.DIALOG_MENU;
		this.appendChild(dialogMenu);

		this.buildLogo();
		if(this.activeTimecontrol) {
			getTimeControl().then(r => {
				this.timeControlStatus(r);
			});
		}
	}

	timeControlStatus(signin) {
		this.activeTimecontrol = true;
		let aonUserConnected = this.getElement('aonHeaderUserConnected');
		if(!aonUserConnected) {
			aonUserConnected = document.createElement('div');
			aonUserConnected.id = this.id + 'UserConnected';
			aonUserConnected.className = 'aonConnected';

			let aonHeaderUserButtonIconButton = this.getElement('aonHeaderUserButtonIconButton');
			aonHeaderUserButtonIconButton.appendChild(aonUserConnected);
		}

		if(signin.status === 'in'){
			aonUserConnected.style.backgroundColor = '#86D364';
		} else if(signin.status === 'pause') {
			aonUserConnected.style.backgroundColor = '#F39F1D';
		} else {
			aonUserConnected.style.backgroundColor = '#DC4D30';
		}
	}

	buildLogo() {
		let aonLogo = this.getElement(this.LOGO);
		const href = window.location.href;       
		if(href.includes('ayudat')){
			aonLogo.src = 'assets/ayudat-logo2.png';
		} else if(href.includes('translogia') || href.includes('tedi')){
			aonLogo.src = 'assets/ayudat-logo3.png';
		} else {
			aonLogo.src = 'assets/aon-logo3.svg';
			aonLogo.style.top = "21px";
			aonLogo.style.width = "123px";
			aonLogo.style.marginLeft = "21px";
		}
		aonLogo.addEventListener('click', () => {
			this.home();
		});
	}

	showCompanyOption(company) {

	}

	companyIn(onlyOne) {
		onlyOne = onlyOne || LS.isOnlyOne();
		mobileAction({ action: "statusBar", statusBar: true});

		this.parent = false;
		let div = this.getElement(this.WEB);
		div.style.backgroundColor = '#002469';

		this.getElement(this.LOGO).style.display = 'none';

		let company = this.getElement(this.COMPANY);
		company.innerHTML = LS.getCompany().name;
		company.style.display = 'block';

		let spanCompany = this.createElement(TAG.SPAN);
		spanCompany.id = this.COMPANY;
		spanCompany.className = CSS.AON_MOBILE_HEADER_COMPANY;
	
		let userButton = this.getElement(this.USER_BUTTON);
		userButton.color = 'white';
		userButton.addEventListener(EVENT.CLICK, ()=>{
			if(!this.getElement(CONSTANT.AON_MOBILE_PROFILE)){
				if(LS.getCompany()) {
					let aonHeader = this.getElement("aonHeader");
					aonHeader.companyIn();
				}
				const aonMobileProfile = new AonMobileProfile();
				aonMobileProfile.id = CONSTANT.AON_MOBILE_PROFILE;
				this.rootPanel(aonMobileProfile);
			}
		})

		// let notificationButton = this.getElement(this.NOTIFICATION_BUTTON); //'aonHeaderNotificationButton');
		// notificationButton.color = 'white';
		
		let companyListButton = this.getElement(this.COMPANY_LIST_BUTTON);
		companyListButton.color = 'white';
		if(onlyOne) {
			this.getElement(this.COMPANY_LIST_BUTTON).style.display = 'none';
		}
	}

	companyOut() {
		mobileAction({ action: "statusBar", statusBar: false});

		this.parent = true;
		let div = this.getElement(this.WEB);
		div.style.backgroundColor = 'white';

		this.getElement(this.LOGO).style.display = 'block';
		this.getElement(this.COMPANY).style.display = 'none';

		let userButton = this.getElement(this.USER_BUTTON);
		userButton.color = '#5f6368';

		// let notificationButton = this.getElement(this.NOTIFICATION_BUTTON);
		// notificationButton.color = '#5f6368';
		
		let companyListButton = this.getElement(this.COMPANY_LIST_BUTTON);
		companyListButton.color = '#5f6368';
	}

	home() {
		if(LS.getCompany()) {
			this.companyIn();
			this.rootPanelHtml('<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>');
		} else {
			this.companyOut();
			this.rootPanelHtml('<aon-mobile-parent id="aonParent"></aon-mobile-parent>');
		}
	}

}

window.customElements.define('aon-new-mobile-header', AonNewMobileHeader);
