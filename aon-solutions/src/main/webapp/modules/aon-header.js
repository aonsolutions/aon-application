import {AonElement} from '../components/AonElement.js';
import {closeSession, getTimeControl, saveTimeControl, clearDurum, getDomainUserRoles} from  '../services/service.js';
import {getPosition} from '../services/maps.js';

import '../components/aon-dialog-menu.js';
import '../components/aon-icon-button.js';
import '../components/aon-search-box.js';
import './configuration/aon-configuration.js';
import './company/aon-desktop.js';
import './company/aon-mobile-desktop.js';
import './company/aon-parent.js';
import './notification/aon-notification-icon.js';
import { CONSTANT, CSS, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { AonApiDoc } from './dev/aon-api-doc.js';
import { DomainUserRoles } from '../models/DomainUserRoles.js';
import { AonComponentsDoc } from './dev/aon-components-doc.js';
import { AonMessenger } from './messenger/aon-messenger.js';
import { TASK_SOURCE } from './messenger/MessengerEnums.js';
import * as LS from '../services/localStorageService.js';
import { Language } from '../models/Language.js';
import { AonDialogMenu } from '../components/aon-dialog-menu.js';
import { AonDialog } from '../components/aon-dialog.js';

export class AonHeader extends AonElement {

	BASE_ID;
	activeTimecontrol;

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
		this.activeTimecontrol = false;
		this.build();
  }

	initialize() {
		this.BASE_ID = 'aonHeader';
		this.AON_HEADER_WEB = this.BASE_ID + 'Web';
		this.AON_LOGO = 'aonLogo';
		this.AON_HEADER_SEARCH = this.BASE_ID + 'Search';
		this.AON_HEADER_SEARCH_BOX = this.AON_HEADER_SEARCH + 'Box';
		this.AON_HEADER_USER = this.BASE_ID + 'User';
		this.AON_HEADER_USER_BUTTON = this.AON_HEADER_USER + 'Button';
		this.AON_HEADER_NOTIFICATION = this.BASE_ID + 'Notification';
		this.AON_HEADER_HELP = this.BASE_ID + 'Help';
		this.AON_HEADER_HELP_BUTTON = this.AON_HEADER_HELP + 'Button';
		this.AON_HEADER_COMPANY_LIST = this.BASE_ID + 'CompanyList';
		this.AON_HEADER_COMPANY_LIST_BUTTON = this.AON_HEADER_COMPANY_LIST + 'Button';
		this.AON_HEADER_HOME = this.BASE_ID + 'Home';
		this.AON_HEADER_HOME_BUTTON = this.AON_HEADER_HOME + 'Button';
		this.AON_HEADER_COMPANY = this.BASE_ID + 'Company';
		this.AON_HEADER_COMPANY_NAME = this.AON_HEADER_COMPANY + 'Name';
		this.AON_HEADER_DIALOG_HELP_OPTION = this.BASE_ID + 'DialogHelpOption';
		this.AON_HEADER_DIALOG_USER_OPTION = this.BASE_ID + 'DialogUserOption';
	}

	build() {
		let div = this.createElement(TAG.DIV);
		div.id = 'aonHeaderWeb';
		div.className = LS.isNewTheme() ? CSS.AON_HEADER : CSS.AON_HEADER_BETA;
		this.appendChild(div);

		let helpOption = new AonDialogMenu();
		helpOption.id = 'aonHeaderDialogHelpOption';
		this.appendChild(helpOption);

		let userOption = new AonDialogMenu();
		userOption.id = 'aonHeaderDialogUserOption';
		this.appendChild(userOption);

		if(LS.isNewTheme()){ // Change logo size
			div.innerHTML = /*html*/`
				<img id="aonLogo" class="aonNewLogo"/>

				<span id="aonHeaderSearch" style="display: flex; align-items: center; width: 100%; min-width: 150px; max-width: 500px;" >
					<aon-search-box id="aonHeaderSearchBox"></aon-search-box>
				</span>

				<div class="aonHeaderButtons">

					<span id="aonHeaderCompany" style="display:none;">
						<span id="aonHeaderCompanyName"> </span>
					</span>	

					<span id="aonHeaderHome" style="display:none;">
						<aon-icon-button id="aonHeaderHomeButton" icon="home" outlined="true"></aon-icon-button>
					</span>
 
					<span id="aonHeaderCompanyList" style="display:none;">
						<aon-icon-button id="aonHeaderCompanyListButton" icon="business"></aon-icon-button>
					</span>

					<span id="aonHeaderHelp">
						<aon-icon-button id="aonHeaderHelpButton" icon="help_outline"></aon-icon-button>
					</span>

					<span id="aonHeaderNotiication" >
						<aon-notification-icon></aon-notification-icon>
					</span>

					<span id="aonHeaderUser" >
						<aon-icon-button id="aonHeaderUserButton" icon="account_circle"></aon-icon-button>
					</span>
				
				</div>
			`;
		} else {
			div.innerHTML = /*html*/`
				<img id="aonLogo" class="aonLogo"  />

				<span id="aonHeaderSearch" style="display: flex; align-items: center; width: 100%; min-width: 150px; max-width: 500px;" >
					<aon-search-box id="aonHeaderSearchBox"></aon-search-box>
				</span>

				<div id="aonHeaderButtons" style="display: flex; align-items: center;">

					<span id="aonHeaderCompany" style="display:none;">
						<span id="aonHeaderCompanyName"> </span>
					</span>	

					<span id="aonHeaderHome" style="display:none;">
						<aon-icon-button id="aonHeaderHomeButton" icon="home" outlined="true"></aon-icon-button>
					</span>
 
					<span id="aonHeaderCompanyList" style="display:none;">
						<aon-icon-button id="aonHeaderCompanyListButton" icon="business"></aon-icon-button>
					</span>

					<span id="aonHeaderHelp">
						<aon-icon-button id="aonHeaderHelpButton" icon="help_outline"></aon-icon-button>
					</span>

					<span id="aonHeaderNotiication" >
						<aon-notification-icon></aon-notification-icon>
					</span>

					<span id="aonHeaderUser" >
						<aon-icon-button id="aonHeaderUserButton" icon="account_circle"></aon-icon-button>
					</span>
				
				</div>
			`;
		}

		this.buildLogo();

		if(!this.isMobile()) {
			if(!LS.isNewTheme()) {
				let aonHeaderButtons = this.getElement('aonHeaderButtons');
				aonHeaderButtons.style.position = 'absolute';
				aonHeaderButtons.style.right = '20px';
 		 		aonHeaderButtons.style.top = '10px';

				let aonHeaderSearch2 = this.getElement('aonHeaderSearch');
				aonHeaderSearch2.style.marginLeft = '33px';
			}

			let aonHeaderHomeButton = this.getElement(this.BASE_ID + 'HomeButton');
			aonHeaderHomeButton.addEventListener('click', () => {
				this.rootPanelHtml(this.isMobile()
					? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
					: '<aon-desktop id="aonDesktop"></aon-desktop>');
				let aonDesktop = this.getElement('aonDesktop');
				aonDesktop.setAttribute('company', this.getAttribute('company'));
			});

			let aonHeaderHelpButton = this.getElement(this.BASE_ID + 'HelpButton');
			aonHeaderHelpButton.addEventListener('click', () => {
				const top  = aonHeaderHelpButton.getBoundingClientRect().top;
				const left = aonHeaderHelpButton.getBoundingClientRect().left;
				getDomainUserRoles({}).then(r => {
					this.dur = new DomainUserRoles(r);
					let d = this.getElement('aonHeaderDialogHelpOption');
					let options = []
					// let support = {
					// 	name: MSG.SUPPORT + ' / CAU',
					// 	icon: MATERIAL_ICONS.SUPPORT_AGENT,
					// 	fn: () =>{
					// 		let aonMessenger = new AonMessenger();
					// 		aonMessenger.cau = 1;
					// 		aonMessenger._filter.source = TASK_SOURCE.CAU;
					// 		this.rootPanel(aonMessenger);
					// 	}
					// };
					
					let language = {
						id: CONSTANT.LANGUAGE,
						name: MSG.LANGUAGE,
						icon: 'language',
						options: [{
							name: MSG.SPANISH,
							image: '../assets/img/aonIconCastellano.png',
							fn: () => LS.setLanguage(Language.SPANISH)
						}, {
							name: MSG.ENGLISH,
							image: '../assets/img/aonIconEnglish.png',
							fn: () => LS.setLanguage(Language.ENGLISH)
						}, {
							name: MSG.DEUTSCH,
							image: '../assets/img/aonIconDeutsch.png',
							fn: () => LS.setLanguage(Language.DEUTSCH)
						}, {
							name: MSG.BASQUE,
							image: '../assets/img/aonIconEuskera.png',
							fn: () => LS.setLanguage(Language.BASQUE)
						}, {
							name: MSG.CATALAN,
							image: '../assets/img/aonIconCatala.png',
							fn: () => LS.setLanguage(Language.CATALAN)
						}, {
							name: MSG.GALICIAN,
							image: '../assets/img/aonIconGalego.png',
							fn: () => LS.setLanguage(Language.GALICIAN)
						} ]
					};

					// let help = {
					// 	name: MSG.HELP,
					// 	icon: 'help_outline',
					// 	fn: () => {
					// 		let iframe = document.createElement("iframe");
					// 		iframe.height = "100%";
					// 		iframe.width = "100%";
					// 		iframe.src = "https://faqs.aonsolutions.es";
					// 		this.rootPanel(iframe);
					// 	} 
					// };

					// if(LS.getDomainId())
					// 	options.push(support)
					// if(this.isBeta())
					options.push(language);
					// options.push(help);

					if(this.dur.isDev()) {
						options.push({
							name: MSG.COMPONENTS,
							icon: MATERIAL_ICONS.EXTENSION,
							fn: () => this.rootPanel(new AonComponentsDoc())
						});
						

					}

					if(this.dur.hasApiService()) {
						let iframe = this.createElement(TAG.IFRAME);
						iframe.src = 'https://aonsolutions.github.io/apidoc';
						iframe.style.height = '100%';
						iframe.style.width = '100%';
						iframe.style.border = '0';
						options.push({
							name: MSG.API_DOCUMENTATION,
							icon: MATERIAL_ICONS.API,
							fn: () => this.rootPanel(iframe)
						});
					}

					let about = {
						name: MSG.ABOUT,
						icon: MATERIAL_ICONS.INFO,
						fn: () => this.about()
					}
					options.push(about)

					d.setMenuOptions(options, top, left);
					d.open();
				});
			});
		}

		let aonHeaderCompanyListButton = this.getElement(this.BASE_ID + 'CompanyListButton');
		aonHeaderCompanyListButton.addEventListener('click', () => {
			if(!this.isMobile()) {
				let aonHeaderSearch = this.getElement(this.BASE_ID + 'Search');
				aonHeaderSearch.style.display = 'flex';

				let aonHeaderHome = this.getElement(this.BASE_ID + 'Home');
				aonHeaderHome.style.display = 'none';

				let aonHeaderCompany = this.getElement(this.BASE_ID + 'Company');
				aonHeaderCompany.style.display = 'none';
				if(!LS.isNewTheme()){
					let aonShowMenu = this.getElement('aonShowMenu');
					aonShowMenu.style.display = 'none';
				}
				let aonMenu = this.getElement('aonMenu');
				aonMenu.removeAttribute('company');
				aonMenu.removeAttribute('user');
				aonMenu.close();
			}

			let aonHeaderCompanyList = this.getElement(this.BASE_ID + 'CompanyList');
			aonHeaderCompanyList.style.display = 'none';

			this.removeAttribute('company');
			this.removeAttribute('user');

			LS.removeDomain();

			clearDurum();
			this.rootPanelHtml('<aon-parent id="aonParent"></aon-parent>');
		});
		if(this.activeTimecontrol) {
			getTimeControl().then(r => this.timeControlStatus(r) );
		}
		let aonHeaderUserButton = this.getElement('aonHeaderUserButton');
		aonHeaderUserButton.addEventListener('click', () => {
			const top  = aonHeaderUserButton.getBoundingClientRect().top;
			const left = aonHeaderUserButton.getBoundingClientRect().left;
			if(this.activeTimecontrol) {
				getTimeControl().then(r => {
					this.timeControlStatus(r);

					let d = this.getElement('aonHeaderDialogUserOption');
					
					let fichajeText = r.status === 'in' ? MSG.MARK_EXIT : MSG.MARK_ENTRY;
					let signin = r.status === 'in' ? {status: 'out'} : {status: 'in'};
					let options = [{
							name: fichajeText,
							icon: 'alarm',
							id: 'dialogAlarm',
							fn: () => this.aonFichar(signin)
						}, {
							name: MSG.CONFIGURATION,
							icon: 'settings',
							id: 'dialogLanguage',
							fn: () => this.aonConfiguration()
						}, {
							name: MSG.CLOSE_SESSION,
							icon: MATERIAL_ICONS.LOGOUT,
							id: 'dialogLogout',
							fn: () => {
								this.activeTimecontrol= false;
								closeSession();
							}
						}];
						d.setMenuOptions(options, top, left);
						d.open();
					}).catch(e => {
						let d = this.getElement('aonHeaderDialogUserOption');
						let options = [{
							name: MSG.CONFIGURATION,
							icon: 'settings',
							id: 'dialogSettings',
							fn: () => this.aonConfiguration()
						}, {
							name: MSG.CLOSE_SESSION,
							icon: MATERIAL_ICONS.LOGOUT,
							id: 'dialogLogout',
							fn: () => closeSession()
						}];
						d.setMenuOptions(options, top, left);
						d.open();
					});
				} else {
					let d = this.getElement('aonHeaderDialogUserOption');
					let options = [{
							name: MSG.CONFIGURATION,
							icon: 'settings',
							id: 'dialogSettings',
							fn: () => this.aonConfiguration()
						}, {
							name: MSG.CLOSE_SESSION,
							icon: MATERIAL_ICONS.LOGOUT,
							id: 'dialogLogout',
							fn: () => closeSession()
						}];
						d.setMenuOptions(options, top, left);
						d.open();
				}
		});
	}

	timeControlStatus(signin) {
		this.activeTimecontrol = true;
		let aonUserConnected = this.getElement('aonHeaderUserConnected');
		if(!aonUserConnected) {
			aonUserConnected = this.createElement('div');
			aonUserConnected.id = this.BASE_ID + 'UserConnected';
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

	aonFichar(signin) {
		getPosition().then(position => {
			if(position) {
				signin.coordinates = position.latitude + ',' + position.longitude;
			}
			saveTimeControl(signin).then(r => {
				let aonSign = this.getElement('aonSign');
				if(aonSign) {
					aonSign.buildSignin(r);
				}

				this.timeControlStatus(r);
			});
		});
	}

	buildLogo() {
		let aonLogo = this.getElement('aonLogo');

		aonLogo.src = '../assets/aon-logo.svg';

		aonLogo.addEventListener('click', () => {
			if(LS.getDomainId()){
				this.rootPanelHtml(this.isMobile()
					? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
					: '<aon-desktop id="aonDesktop"></aon-desktop>');
				let aonDesktop = this.getElement('aonDesktop');
				aonDesktop.setAttribute('company', this.getAttribute('company'));
			} else {
				this.rootPanelHtml('<aon-parent id="aonParent"></aon-parent>');
			}
		})
	}

	aonConfiguration() {
		this.rootPanelHtml('<aon-configuration id="aon-configuration"></aon-configuration>');
		let aonConfiguration = this.getElement('aon-configuration');
		if(this.getAttribute('company')){
			aonConfiguration.setAttribute('company', this.getAttribute('company'));
		}
		if(this.getAttribute('user')){
			aonConfiguration.setAttribute('user', this.getAttribute('user'));
		}
	}

	showCompanyOption(company, onlyOne) {
		let aonHeaderCompanyList = this.getElement(this.AON_HEADER_COMPANY_LIST);
		aonHeaderCompanyList.style.display = company && !onlyOne ? 'block' : 'none';

		let aonHeaderHelp = this.getElement(this.AON_HEADER_HELP);
		aonHeaderHelp.style.display = company ? 'block' : 'none';

		let aonHeaderSearch = this.getElement(this.AON_HEADER_SEARCH);
		aonHeaderSearch.style.display = company ? 'none' : 'flex';
		if(!LS.isNewTheme())
			aonHeaderSearch.style.marginLeft = '33px';

		let aonHeaderHome = this.getElement(this.AON_HEADER_HOME);
		aonHeaderHome.style.display = company ? 'block' : 'none';

		let aonHeaderCompany = this.getElement(this.AON_HEADER_COMPANY);
		aonHeaderCompany.style.display = company ? 'block' : 'none';

		let aonHeaderCompanyName = this.getElement(this.AON_HEADER_COMPANY_NAME);
		aonHeaderCompanyName.innerHTML = company ? company.name : '';

		if(onlyOne) {
			// aonHeaderHome.style.right = '140px';
			// aonHeaderCompany.style.right = '180px';
			aonHeaderCompanyList.style.display = 'none';
		}
	}

	about() {
		const aboutDialogId = "aboutDialog";
		let d = this.getElement(aboutDialogId);
		if(!d){
		  d = new AonDialog();
		  d.id = aboutDialogId;
		  this.appendChild(d);
		}
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ABOUT);
		d.setContent(this.aboutContent());
		d.open();
	}

	aboutContent() {
		let aboutContent = this.createElement(TAG.DIV);

		let img = this.createElement(TAG.IMG);
		img.src = '../assets/aon-logo.svg';
		img.style.maxWidth = '360px';
		img.style.maxHeight = '60px';
		aboutContent.appendChild(img);

		let contactDiv = this.createElement(TAG.DIV);
		contactDiv.style.marginTop = '10px';
		contactDiv.innerHTML = `<div id="aonContent:j_id36:j_id49">
				<span class="aon-outputText" style="font-weight: bold; font-size: 11px; color: #666;">Datos de contacto:</span>
				</div>
				<div id="aonContent:j_id36:j_id51" style="margin-left: 5%">
				<div id="aonContent:j_id36:j_id52">
				<span class="aon-outputText" style="font-size: 11px; color: #666;">Teléfono: (+34) 900 831 205</span>
				</div>
				<div id="aonContent:j_id36:j_id54"><span class="aon-outputText" style="font-size: 11px; color: #666;">Correos:</span>
				<span class="aon-outputText" style="display:block;font-size: 11px; color: #666; padding-left: .8em;">· soporte@aonSolutions.es / Atención a usuarios</span>
				<span class="aon-outputText" style="display:block;font-size: 11px; color: #666; padding-left: .8em;">· comercial@aonSolutions.es / Ventas y contratación</span>
				<span class="aon-outputText" style="display:block; font-size: 11px; color: #666; padding-left: .8em;">· administración@aonSolutions.es / Facturación, cobros y pagos</span>
				</div>
				<div id="aonContent:j_id36:j_id59">
	
				<span class="aon-outputText" style="font-size: 11px; color: #666;">Horario:</span>
				<span class="aon-outputText" style="display:block; font-size: 11px; color: #666; padding-left: .8em;">· Lunes a jueves de 8:00 a 15:00</span>
				<span class="aon-outputText" style="display:block; font-size: 11px; color: #666; padding-left: .8em;">· Viernes de 8:00 a 14:00</span>
				</div>
				</div>`;
		aboutContent.appendChild(contactDiv);

		let divInfo = this.createElement(TAG.DIV);
		divInfo.style.color = '#666';
		divInfo.style.fontSize = '9px';
		divInfo.style.borderTop = '1px solid #ddd';
		divInfo.style.marginTop = '10px';
		divInfo.style.padding = '15px';
		divInfo.innerHTML = `
		  <span>
			<a target="_blank" class="aonLink" href="http://www.aonsolutions.es">
			  aonSolutions
			</a> ${MSG.REGISTERED_TRADEMARK_AON}
		  </span>
		  <div id="aonManifest"></div>`;
		aboutContent.appendChild(divInfo);
		return aboutContent;
	}
}

window.customElements.define('aon-header', AonHeader);
