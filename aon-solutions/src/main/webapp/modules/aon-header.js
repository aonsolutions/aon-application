import {AonElement} from '../components/AonElement.js';
import {closeSession, getTimeControl, saveTimeControl, clearDurum, getDomainUserRoles} from  '../services/service.js';
import {getPosition} from '../services/maps.js';

import '../components/aon-dialog-menu.js';
import '../components/aon-icon-button.js';
import '../components/aon-search-box.js';
import './configuration/aon-configuration.js';
import './company/aon-desktop.js';
import './company/aon-mobile-desktop.js';
import './notification/aon-notification-icon.js';
import { CONSTANT, CSS, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { DomainUserRoles } from '../models/DomainUserRoles.js';
import { AonComponentsDoc } from './dev/aon-components-doc.js';
import * as LS from '../services/localStorageService.js';
import { Language } from '../models/Language.js';
import { AonDialogMenu } from '../components/aon-dialog-menu.js';
import { AonDialog } from '../components/aon-dialog.js';
import { AonSearchBox } from '../components/aon-search-box.js';
import { AonIcon } from '../components/aon-icon.js';
import { AonIconButton } from '../components/aon-icon-button.js';
import { AonNotificationIcon } from './notification/aon-notification-icon.js';
import { AonParent } from 'aonparent';

export class AonHeader extends AonElement {

	BASE_ID;
	activeTimecontrol;
	newTheme;

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
		this.AON_HEADER_CONFIG = this.BASE_ID + 'Config';
		this.AON_HEADER_CONFIG_BUTTON = this.BASE_ID + 'ConfigButton';
	}

	build() {
		let div = this.createElement(TAG.DIV);
		div.id = 'aonHeaderWeb';
		div.className = (LS.isNewTheme() || this.newTheme) ? CSS.AON_HEADER : CSS.AON_HEADER_BETA;
		this.appendChild(div);

		let helpOption = new AonDialogMenu();
		helpOption.id = 'aonHeaderDialogHelpOption';
		this.appendChild(helpOption);

		let userOption = new AonDialogMenu();
		userOption.id = 'aonHeaderDialogUserOption';
		this.appendChild(userOption);

		let aonLogo = this.createElement(TAG.IMG);
		aonLogo.id = "aonLogo";
		aonLogo.style.paddingLeft = '0px';
		aonLogo.style.width = '123px';
		aonLogo.className = (LS.isNewTheme() || this.newTheme) ? "aonNewLogo" : "aonLogo";
		div.appendChild(aonLogo);

        let aonHeaderApp = this.createElement(TAG.DIV);
		aonHeaderApp.id = "aonHeaderApp";
		aonHeaderApp.className = "aonHeaderApp";
		aonHeaderApp.style.minWidth = '120px';
		aonHeaderApp.style.display = "none";
		aonHeaderApp.appendChild(this.createElement(TAG.SPAN));
		div.appendChild(aonHeaderApp);

		let aonHeaderSearch = this.createElement(TAG.SPAN);
		aonHeaderSearch.id = this.AON_HEADER_SEARCH;
		aonHeaderSearch.style.display = "flex";
		aonHeaderSearch.style.alignItems = "center";
		aonHeaderSearch.style.width = "100%";
		aonHeaderSearch.style.minWidth = '150px';
		aonHeaderSearch.style.maxWidth = '500px';
		
		let aonHeaderSearchBox = new AonSearchBox();
		aonHeaderSearchBox.id = this.AON_HEADER_SEARCH_BOX;
		aonHeaderSearchBox.newTheme = this.newTheme;
		aonHeaderSearch.appendChild(aonHeaderSearchBox);

		div.appendChild(aonHeaderSearch);

		let aonHeaderButtons = this.createElement(TAG.DIV);
		aonHeaderButtons.id = 'aonHeaderButtons';
		aonHeaderButtons.className = "aonHeaderButtons";
		if (!LS.isNewTheme() && !this.newTheme){
			aonHeaderButtons.style.display = "flex";
			aonHeaderButtons.style.alignItems = "center";		
		}
		
		let aonHeaderCompany = this.createElement(TAG.SPAN);
		aonHeaderCompany.id = this.AON_HEADER_COMPANY;
		aonHeaderCompany.style.display = "none";
		aonHeaderCompany.style.height = "14px";
		aonHeaderCompany.style.color = "var(--aonHeaderButtonColor)"

		let aonHeaderCompanyName = this.createElement(TAG.SPAN);
		aonHeaderCompanyName.id = this.AON_HEADER_COMPANY_NAME;
		aonHeaderCompany.appendChild(aonHeaderCompanyName);

		aonHeaderButtons.appendChild(aonHeaderCompany);

		let aonHeaderHome = this.createElement(TAG.SPAN);
		aonHeaderHome.id = this.AON_HEADER_HOME;
		aonHeaderHome.style.display = "none";

		let aonHeaderHomeButton = new AonIconButton();
		aonHeaderHomeButton.id = this.AON_HEADER_HOME_BUTTON;
		aonHeaderHomeButton.icon = "home";
		aonHeaderHomeButton.outlined = true;
		aonHeaderHome.appendChild(aonHeaderHomeButton);

		aonHeaderButtons.appendChild(aonHeaderHome);

		let aonHeaderCompanyList = this.createElement(TAG.SPAN);
		aonHeaderCompanyList.id = this.AON_HEADER_COMPANY_LIST;
		aonHeaderCompanyList.style.display = "none";
		aonHeaderCompanyList.title = "Listado de empresas";

		let aonHeaderHomeCompanyListButton = new AonIconButton();
		aonHeaderHomeCompanyListButton.id = this.AON_HEADER_COMPANY_LIST_BUTTON;
		aonHeaderHomeCompanyListButton.icon = "business";
		aonHeaderCompanyList.appendChild(aonHeaderHomeCompanyListButton);

		aonHeaderButtons.appendChild(aonHeaderCompanyList);

		let aonHeaderHelp = this.createElement(TAG.SPAN);
		aonHeaderHelp.id = this.AON_HEADER_HELP;
		aonHeaderHelp.title = MSG.HELP;

		let aonHeaderHelpButton = new AonIconButton();
		aonHeaderHelpButton.id = this.AON_HEADER_HELP_BUTTON;
		aonHeaderHelpButton.icon = "help_outline";
		aonHeaderHelp.appendChild(aonHeaderHelpButton);

		aonHeaderButtons.appendChild(aonHeaderHelp);

		if(this.newTheme){
			let aonHeaderConfig = this.createElement(TAG.SPAN);
			aonHeaderConfig.id = this.AON_HEADER_CONFIG;
			aonHeaderConfig.title = MSG.CONFIGURATION;

			let aonHeaderConfigButton = new AonIconButton();
			aonHeaderConfigButton.id = this.AON_HEADER_CONFIG_BUTTON;
			aonHeaderConfigButton.icon = "settings"
			aonHeaderConfig.appendChild(aonHeaderConfigButton);

			aonHeaderButtons.appendChild(aonHeaderConfig);
		}


		let aonHeaderNotiication = this.createElement(TAG.SPAN);
		aonHeaderNotiication.id = this.AON_HEADER_NOTIFICATION;
		aonHeaderNotiication.title = MSG.NOTIFICATIONS;
		if(this.newTheme){
			let aonHeaderNotificationButton = new AonIconButton();
			aonHeaderNotificationButton.id = 'aonHeaderNotificationButton';
			aonHeaderNotificationButton.icon = "notifications";
			aonHeaderNotiication.appendChild(aonHeaderNotificationButton);
		}else
			aonHeaderNotiication.appendChild(new AonNotificationIcon());

		aonHeaderButtons.appendChild(aonHeaderNotiication);

		let aonHeaderUser = this.createElement(TAG.SPAN)
		aonHeaderUser.id = this.AON_HEADER_USER;
		aonHeaderUser.title = MSG.USER;
		
		let aonHeaderUserButton = new AonIconButton();
		aonHeaderUserButton.id = this.AON_HEADER_USER_BUTTON;
		aonHeaderUserButton.icon = "account_circle";
		aonHeaderUser.appendChild(aonHeaderUserButton);
		
		aonHeaderButtons.appendChild(aonHeaderUser);

		div.appendChild(aonHeaderButtons);
		
		this.buildLogo();

		if(!this.isMobile()) {
			if(!LS.isNewTheme() && !this.newTheme) {
				let aonHeaderButtons = this.getElement('aonHeaderButtons');
				aonHeaderButtons.style.position = 'absolute';
				aonHeaderButtons.style.right = '20px';
 		 		aonHeaderButtons.style.top = '10px';

				let aonHeaderSearch2 = this.getElement('aonHeaderSearch');
				aonHeaderSearch2.style.marginLeft = '108px';
			}

			let aonHeaderHomeButton = this.getElement(this.BASE_ID + 'HomeButton');
			aonHeaderHomeButton.addEventListener('click', () => {
				this.rootPanelHtml(this.isMobile()
					? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
					: '<aon-desktop id="aonDesktop"></aon-desktop>');
				let aonDesktop = this.getElement('aonDesktop');
				aonDesktop.setAttribute('company', this.getAttribute('company'));
			});
			if(!this.newTheme){
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
				if(!LS.isNewTheme() && !this.newTheme){
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
			this.rootPanel(new AonParent());

			let header = this.getElement("aonHeaderWeb");
			header.style.removeProperty("background-color");

			let apps = this.getElement("aonMenuListAppImg-applications");
			apps.style.color = "rgb(95, 99, 104)";

			let headerapp = this.getElement("aonHeaderApp");
			headerapp.style.display = "none";

			let logo = this.getElement("aonLogo");
			logo.style.display = "block";
			logo.style.filter = "none";

			let rootPanel = this.getElement("rootPanel");
			rootPanel.style.backgroundColor = "transparent";
			let enterprise = this.getElement("aonHeaderCompanyName");
			enterprise.style.color = "rgb(95, 99, 104)";
			let appss = this.getElement("applications");
			let welcome = this.getElement("aonCompanyTabFilter");
			if(welcome){
				appss.addEventListener("click", (event) => {
					event.preventDefault();
					event.stopPropagation();
				});
			}
			appss.style.color = "rgb(95, 99, 104)";

		});
		if(this.activeTimecontrol) {
			getTimeControl().then(r => this.timeControlStatus(r) );
		}
		if(!this.newTheme){
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
		if(LS.isDarkTheme())
			aonLogo.src = '../assets/aon-white-logo.svg';
		else if(!LS.isDarkTheme())
			aonLogo.src = '../assets/aon-black-logo.svg';

		aonLogo.addEventListener('click', () => {
			if(LS.getDomainId()){
				this.rootPanelHtml(this.isMobile()
					? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
					: '<aon-desktop id="aonDesktop"></aon-desktop>');
				let aonDesktop = this.getElement('aonDesktop');
				aonDesktop.setAttribute('company', this.getAttribute('company'));
			} else {
				this.rootPanel(new AonParent());
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
		if(!LS.isNewTheme() && !this.newTheme)
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
	
	
	setColor(color, backgroundColor) {
		let buttons = [
			this.getElement('aonHeaderHelpButton'),
			this.getElement('aonHeaderHomeButton'),
			this.getElement('aonHeaderUserButton'),
			this.getElement('aonHeaderConfigButton'),
			this.getElement('aonHeaderNotificationButton'),
			this.getElement('aonHeaderCompanyListButton')
		];
		let texts = [ 
			this.getElement('aonHeaderApp'),
			this.getElement('aonHeaderCompanyName'),
			this.getElement('aonMenuListAppImg-applications')
		];
		let imgs = [ 
			this.getElement('aonLogo')
		];
		
		//
		if ( color ) {
			texts.forEach( (text) => text.style.color = color );
			buttons.forEach( (button) => {
				button.setColor(color);
				button.setBackgroundColor(backgroundColor);
			});
			imgs.forEach( (img) => img.style.filter = 'invert(100%) sepia(0%) saturate(7470%) hue-rotate(111deg) brightness(106%) contrast(94%)' );
		} else {
			imgs.forEach( (img) => img.style.removeProperty ('filter') );
			texts.forEach( (text) => text.style.removeProperty('color') );
			buttons.forEach( (button) => button.getButton().style.removeProperty('color'));
		}
	}

	setBackgroundColor(backgroundColor) {
		if ( backgroundColor ) {
			this.getElement(this.AON_HEADER_WEB).style.backgroundColor = backgroundColor;
		} else {
			this.getElement(this.AON_HEADER_WEB).style.removeProperty('background-color');
		}
	}

	buildApp(app, sidenav){	
		let a = this.createElement(TAG.A);
		a.classList.add('aonMenuApp');

		let div = this.createElement(TAG.DIV);
		div.style.padding = '1px';
		div.style.display = 'flex';
		div.style.alignItems = 'center';
		// div.style.justifyContent = 'center';
		div.style.height =  '32px';
		div.style.flexDirection =  'row';
		div.style.backgroundColor = 'transparent';

		if ((!sidenav && app.symbol) || (sidenav && !app.icon && app.symbol)) {
			let icon = this.createElement(TAG.SPAN);
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.id = `aonMenuListAppImg-${app.app}`;
			icon.innerHTML = app.symbol;
			if(app.color) icon.style.color = "white";
			icon.style.padding = "4px";
			icon.style.fontSize = "24px";
			div.appendChild(icon);
		} else if (app.icon) {
			let aonIcon = new AonIcon();
			aonIcon.id = `aonMenuListAppImg-${app.app}`;
			aonIcon.icon = app.icon;
			aonIcon.color = "white";
			aonIcon.size = "32px";
			div.appendChild(aonIcon);
		} else if (app.logo) {
			let img = this.createElement(TAG.IMG);
			img.id = `aonMenuListAppImg-${app.app}`;
			img.style.width = '24px';
			img.src = app.logo;
			img.title = app.title;
			div.appendChild(img);
		}

		if (app.title) {
			// let titles = app.title.match(/\b\w+\b/g);
			// for (let i = 0; i < 2; i++) {
				let span = this.createElement(TAG.SPAN);
				span.id = `aonMenuListAppTitle-${app.app}`;//-${i}`;
				span.style.textAlign = 'center';
				span.style.minHeight = '21px';
				span.style.color = "white";
				span.innerHTML = app.title; // titles.length > i ? titles[i] : '&nbsp;';
				div.appendChild(span);
			// }
		}

		a.appendChild(div);
		let headerApp = this.getElement('aonHeaderApp');
		headerApp.replaceChild(a, headerApp.firstChild);
	}

	
	setApp(el) {
		let headerApp = this.getElement('aonHeaderApp');
		headerApp.replaceChild(el, headerApp.firstChild);
	}
	
	setVisibleApp(visible) {
		this.setVisibleElement('aonHeaderApp', visible)
	}

	setVisibleLogo(visible) {
		this.setVisibleElement('aonLogo', visible)
	}

	setVisibleHomeButton(visible) {
		this.setVisibleElement('aonHeaderHomeButton', visible)
	}

	setVisibleCompanyListButton(visible) {
		this.setVisibleElement('aonHeaderCompanyListButton', visible)
	}
	
	setVisibleElement(elementId, visible) {
		if ( visible )
			this.getElement(elementId).style.removeProperty('display');
		else 
			this.getElement(elementId).style.display = 'none';
	}
	

}

window.customElements.define('aon-header', AonHeader);
