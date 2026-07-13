import { AonElement } from '../components/AonElement.js';
import { closeSession, getTimeControl, saveTimeControl, clearDurum, getDomainUserRoles, getNotification, getCompanies, getUser, getAllContracts, getAuth, getHelpDatas } from '../services/service.js';
import { getPosition } from '../services/maps.js';

import '../components/aon-dialog-menu.js';
import '../components/aon-icon-button.js';
import '../components/aon-search-box.js';
import './configuration/aon-configuration.js';
import './company/aon-desktop.js';
import './notification/aon-notification-icon.js';
import { AON_SYMBOLS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { DomainUserRoles } from '../models/DomainUserRoles.js';
import { AonComponentsDoc } from './dev/aon-components-doc.js';
import * as LS from '../services/localStorageService.js';
import { Language } from '../models/Language.js';
import { AonDialogMenu } from '../components/aon-dialog-menu.js';
import { AonDialog } from '../components/aon-dialog.js';
import { AonSearchBox } from '../components/aon-search-box.js';
import { AonIcon } from '../components/aon-icon.js';
import { AonIconButton } from '../components/aon-icon-button.js';
import { AonParent } from './aon-parent.js';
import { AonDesktop } from './company/aon-desktop.js';
import { AonInvoiceRecord } from './invoice/aon-invoice-record.js';

import * as GWT from '../gwt/gwt.js';
import { favicon, title, loadCustomView } from '../css/aon-customView.js';

import { AonStringUtils } from './utils/AonStringUtils.js'

const AON_DIALOG_SEARCH = 'aon-dialog-search';

class AonDialogSearch extends AonDialogMenu {

	constructor() {
		super();
	}

	close() {
		this.hide();
	}

	hasContent() {
		return this.getList();
	}
}

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

	constructor() {
		super();
	}

	connectedCallback() {
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
		this.AON_HEADER_SEARCH_DIALOG_MENU = this.AON_HEADER_SEARCH + 'DialogMenu';
		this.AON_HEADER_USER = this.BASE_ID + 'User';
		this.AON_HEADER_USER_BUTTON = this.AON_HEADER_USER + 'Button';
		this.AON_HEADER_NOTIFICATION = this.BASE_ID + 'Notification';
		this.AON_HEADER_HELP = this.BASE_ID + 'Help';
		this.AON_HEADER_HELP_BUTTON = this.AON_HEADER_HELP + 'Button';
		this.AON_HEADER_COMPANY_LIST = this.BASE_ID + 'CompanyList';
		this.AON_HEADER_COMPANY_LIST_BUTTON = this.AON_HEADER_COMPANY_LIST + 'Button';
		this.AON_HEADER_HOME = this.BASE_ID + 'Home';
		this.AON_HEADER_HOME_BUTTON = this.AON_HEADER_HOME + 'Button';
		this.AON_HEADER_SEARCH_DIV = this.BASE_ID + 'SearchDiv';
		this.AON_HEADER_SEARCH_BUTTON = this.AON_HEADER_SEARCH_DIV + 'Button';
		this.AON_HEADER_COMPANY = this.BASE_ID + 'Company';
		this.AON_HEADER_COMPANY_NAME = this.AON_HEADER_COMPANY + 'Name';
		this.AON_HEADER_DIALOG_HELP_OPTION = this.BASE_ID + 'DialogHelpOption';
		this.AON_HEADER_DIALOG_USER_OPTION = this.BASE_ID + 'DialogUserOption';
		this.AON_HEADER_CONFIG = this.BASE_ID + 'Config';
		this.AON_HEADER_CONFIG_BUTTON = this.BASE_ID + 'ConfigButton';
		this.AON_MENU_LEFTOP = 'aonMenuLeftop';
	}

	build() {
		let div = this.createElement(TAG.DIV);
		div.id = 'aonHeaderWeb';
		div.className = (LS.isNewTheme() || this.newTheme) ? `${CSS.AON_HEADER} ${CSS.AON_HEADER_START} ` : CSS.AON_HEADER_BETA;
		this.appendChild(div);

		let helpOption = new AonDialogMenu();
		helpOption.id = 'aonHeaderDialogHelpOption';
		this.appendChild(helpOption);

		let userOption = new AonDialogMenu();
		userOption.id = 'aonHeaderDialogUserOption';
		this.appendChild(userOption);

		let aonLeftTopMenu = this.createElement(TAG.DIV);
		aonLeftTopMenu.id = this.AON_MENU_LEFTOP + "Div";
		aonLeftTopMenu.style.height = "100%";
		aonLeftTopMenu.style.display = "flex";
		aonLeftTopMenu.style.alignItems = "center";
		div.appendChild(aonLeftTopMenu);

		if (!this.getElement(this.AON_MENU_LEFTOP)) {
			let aonMenuLefttop = this.createElement(TAG.DIV);
			aonMenuLefttop.id = this.AON_MENU_LEFTOP;
			aonMenuLefttop.classList.add("aonNewMenuLeftTop");
			aonMenuLefttop.style.visibility = "visible";
			aonMenuLefttop.style.height = "100%";
			aonLeftTopMenu.appendChild(aonMenuLefttop);

			this.buildMenuLeftop();
		}

		let aonLogo = this.createElement(TAG.DIV);
		aonLogo.id = "aonLogo";
		aonLogo.className = (LS.isNewTheme() || this.newTheme) ? "aonNewLogo" : "aonLogo";
		aonLeftTopMenu.appendChild(aonLogo);

		let aonHeaderApp = this.createElement(TAG.DIV);
		aonHeaderApp.id = "aonHeaderApp";
		aonHeaderApp.style.display = "none";
		aonHeaderApp.appendChild(this.createElement(TAG.SPAN));
		aonLeftTopMenu.appendChild(aonHeaderApp);

		let aonHeaderSearch = this.createElement(TAG.SPAN);
		aonHeaderSearch.id = this.AON_HEADER_SEARCH;
		aonHeaderSearch.classList.add("aonHeaderSearch");

		let aonHeaderSearchBox = new AonSearchBox();
		aonHeaderSearchBox.id = this.AON_HEADER_SEARCH_BOX;
		aonHeaderSearchBox.newTheme = this.newTheme;
		aonHeaderSearchBox.addEventListener(EVENT.KEYUP, () => {
			clearTimeout(this.searchTimeoutId);
			this.searchTimeoutId = setTimeout(() => this.search(this), 1000);
		});
		aonHeaderSearchBox.addEventListener(EVENT.FOCUS, () => {
			clearTimeout(this.searchTimeoutId);
			this.searchTimeoutId = setTimeout(() => this.showSearch(this), 300);
		});
		aonHeaderSearch.appendChild(aonHeaderSearchBox);
		div.appendChild(aonHeaderSearch);

		let aonHeaderButtons = this.createElement(TAG.DIV);
		aonHeaderButtons.id = 'aonHeaderButtons';
		aonHeaderButtons.className = "aonHeaderButtons";
		if (!LS.isNewTheme() && !this.newTheme) {
			aonHeaderButtons.style.display = "flex";
			aonHeaderButtons.style.alignItems = "center";
		}

		let aonHeaderCompany = this.createElement(TAG.SPAN);
		aonHeaderCompany.id = this.AON_HEADER_COMPANY;
		aonHeaderCompany.classList.add("aonHeaderCompany");

		let aonHeaderCompanyName = this.createElement(TAG.SPAN);
		aonHeaderCompanyName.id = this.AON_HEADER_COMPANY_NAME;
		aonHeaderCompany.appendChild(aonHeaderCompanyName);

		aonHeaderButtons.appendChild(aonHeaderCompany);

		let aonHeaderHome = this.createElement(TAG.SPAN);
		aonHeaderHome.id = this.AON_HEADER_HOME;

		let aonHeaderHomeButton = new AonIconButton();
		aonHeaderHomeButton.id = this.AON_HEADER_HOME_BUTTON;
		aonHeaderHomeButton.icon = AON_SYMBOLS.HOME;
		aonHeaderHomeButton.aonSymbol = true;
		aonHeaderHomeButton.color = "var(--aonHeaderButtonColor)";
		aonHeaderHome.appendChild(aonHeaderHomeButton);

		aonHeaderButtons.appendChild(aonHeaderHome);

		let aonHeaderCompanyList = this.createElement(TAG.SPAN);
		aonHeaderCompanyList.id = this.AON_HEADER_COMPANY_LIST;
		aonHeaderCompanyList.classList.add("aonHeaderCompanyList");
		aonHeaderCompanyList.title = "Listado de empresas";

		let aonHeaderHomeCompanyListButton = new AonIconButton();
		aonHeaderHomeCompanyListButton.id = this.AON_HEADER_COMPANY_LIST_BUTTON;
		aonHeaderHomeCompanyListButton.icon = AON_SYMBOLS.BUILDING;
		aonHeaderHomeCompanyListButton.aonSymbol = true;
		aonHeaderHomeCompanyListButton.color = "var(--aonHeaderButtonColor)";
		aonHeaderCompanyList.appendChild(aonHeaderHomeCompanyListButton);

		aonHeaderButtons.appendChild(aonHeaderCompanyList);
		
		let aonHeaderSearchDiv = this.createElement(TAG.SPAN);
		aonHeaderSearchDiv.id = this.AON_HEADER_SEARCH_DIV;

		let aonHeaderSearchButton = new AonIconButton();
		aonHeaderSearchButton.id = this.AON_HEADER_SEARCH_BUTTON;
		aonHeaderSearchButton.icon = AON_SYMBOLS.SEARCH;
		aonHeaderSearchButton.aonSymbol = true;
		aonHeaderSearchButton.color = "var(--aonHeaderButtonColor)";
		aonHeaderSearchDiv.appendChild(aonHeaderSearchButton);

		aonHeaderButtons.appendChild(aonHeaderSearchDiv);

		let aonHeaderHelp = this.createElement(TAG.SPAN);
		aonHeaderHelp.id = this.AON_HEADER_HELP;
		aonHeaderHelp.title = MSG.HELP;

		let aonHeaderHelpButton = new AonIconButton();
		aonHeaderHelpButton.id = this.AON_HEADER_HELP_BUTTON;
		aonHeaderHelpButton.icon = AON_SYMBOLS.HELP_CIRCLE;
		aonHeaderHelpButton.aonSymbol = true;
		aonHeaderHelpButton.color = "var(--aonHeaderButtonColor)";
		aonHeaderHelp.appendChild(aonHeaderHelpButton);

		aonHeaderButtons.appendChild(aonHeaderHelp);

		if (this.newTheme) {
			let aonHeaderConfig = this.createElement(TAG.SPAN);
			aonHeaderConfig.id = this.AON_HEADER_CONFIG;
			aonHeaderConfig.title = MSG.CONFIGURATION;

			let aonHeaderConfigButton = new AonIconButton();
			aonHeaderConfigButton.id = this.AON_HEADER_CONFIG_BUTTON;
			aonHeaderConfigButton.icon = AON_SYMBOLS.SETTINGS;
			aonHeaderConfigButton.aonSymbol = true;
			aonHeaderConfigButton.color = "var(--aonHeaderButtonColor)";
			aonHeaderConfig.appendChild(aonHeaderConfigButton);

			aonHeaderButtons.appendChild(aonHeaderConfig);
		}


		let aonHeaderNotiication = this.createElement(TAG.SPAN);
		aonHeaderNotiication.id = this.AON_HEADER_NOTIFICATION;
		aonHeaderNotiication.title = MSG.NOTIFICATIONS;

		let aonHeaderNotificationButton = new AonIconButton();
		aonHeaderNotificationButton.id = 'aonHeaderNotificationButton';
		aonHeaderNotificationButton.icon = AON_SYMBOLS.BELL;
		aonHeaderNotificationButton.aonSymbol = true;
		aonHeaderNotificationButton.color = "var(--aonHeaderButtonColor)";
		aonHeaderNotiication.appendChild(aonHeaderNotificationButton);
		this.getData().then(n => {
			if (n.length > 0) {
				let aonUnread = this.createElement('div');
				aonUnread.id = this.BASE_ID + 'Unread';
				aonUnread.className = 'aonConnected';
				aonUnread.style.backgroundColor = "#DC4D30";

				let aonHeaderNotificationButtonIconButton = this.getElement('aonHeaderNotificationButtonIconButton');
				aonHeaderNotificationButtonIconButton.appendChild(aonUnread);
			}
		});
		aonHeaderButtons.appendChild(aonHeaderNotiication);

		let aonHeaderUser = this.createElement(TAG.SPAN)
		aonHeaderUser.id = this.AON_HEADER_USER;
		getAuth().then(auth => {
			aonHeaderUser.title = auth.name + " " + auth.surname;
		});


		let aonHeaderUserButton = new AonIconButton();
		aonHeaderUserButton.id = this.AON_HEADER_USER_BUTTON;
		aonHeaderUserButton.icon = AON_SYMBOLS.USER;
		aonHeaderUserButton.aonSymbol = true;
		aonHeaderUserButton.color = "var(--aonHeaderButtonColor)";
		aonHeaderUser.appendChild(aonHeaderUserButton);

		aonHeaderButtons.appendChild(aonHeaderUser);

		div.appendChild(aonHeaderButtons);

		this.buildLogo();

		if (!this.isMobile()) {
			if (!LS.isNewTheme() && !this.newTheme) {
				let aonHeaderButtons = this.getElement('aonHeaderButtons');
				aonHeaderButtons.style.position = 'absolute';
				aonHeaderButtons.style.right = '20px';
				aonHeaderButtons.style.top = '10px';

				let aonHeaderSearch2 = this.getElement('aonHeaderSearch');
				aonHeaderSearch2.style.marginLeft = '108px';
			}

			let aonHeaderHomeButton = this.getElement(this.BASE_ID + 'HomeButton');
			aonHeaderHomeButton.addEventListener('click', () => {
				this.rootPanel(LS.isCompanySelected() ? new AonDesktop() : new AonParent());
				// this.rootPanelHtml('<aon-desktop id="aonDesktop"></aon-desktop>');
				// let aonDesktop = this.getElement('aonDesktop');
				// aonDesktop.setAttribute('company', this.getAttribute('company'));
				let aonLogo = this.getElement('aonLogo');
				aonLogo.style.display = "block";
				let aonHeaderApp = this.getElement('aonHeaderApp');
				aonHeaderApp.style.display = 'none';
				document.querySelectorAll('.aonNewMenuSideNavLi').forEach(li => li.classList.remove('aonMenuSidenavLiSeleted'));
			});
			
			let aonHeaderSearchButton = this.getElement(this.AON_HEADER_SEARCH_BUTTON);
			let searchWidget = this.getElement(this.AON_HEADER_SEARCH);
			aonHeaderSearchButton.addEventListener('click', () => {
				
				searchWidget.classList.add('open');
				
				let searchBoxWidgetInput = document.querySelector('#aonHeaderSearchBox input');
				setTimeout(() => { searchBoxWidgetInput.focus(); }, 500);
			});
			
			document.addEventListener('click', (event) => {
			  if (!searchWidget.contains(event.target) && !aonHeaderSearchButton.contains(event.target)) {
			    searchWidget.classList.remove('open');
			  }
			});

			
			if (!this.newTheme) {
				let aonHeaderHelpButton = this.getElement(this.BASE_ID + 'HelpButton');
				aonHeaderHelpButton.addEventListener('click', () => {
					const top = aonHeaderHelpButton.getBoundingClientRect().top;
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
							}]
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

						if (this.dur.isDev()) {
							options.push({
								name: MSG.COMPONENTS,
								icon: MATERIAL_ICONS.EXTENSION,
								fn: () => this.rootPanel(new AonComponentsDoc())
							});


						}

						if (this.dur.hasApiService()) {
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
			this.goToParent();
		});

		if (this.activeTimecontrol) {
			getTimeControl().then(r => this.timeControlStatus(r));
		}
		if (!this.newTheme) {
			aonHeaderUserButton.addEventListener('click', () => {
				const top = aonHeaderUserButton.getBoundingClientRect().top;
				const left = aonHeaderUserButton.getBoundingClientRect().left;
				if (this.activeTimecontrol) {
					getTimeControl().then(r => {
						this.timeControlStatus(r);

						let d = this.getElement('aonHeaderDialogUserOption');

						let fichajeText = r.status === 'in' ? MSG.MARK_EXIT : MSG.MARK_ENTRY;
						let signin = r.status === 'in' ? { status: 'out' } : { status: 'in' };
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
								this.activeTimecontrol = false;
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

		let aonHeaderSearchDialogMenu = new AonDialogSearch();
		aonHeaderSearchDialogMenu.id = this.AON_HEADER_SEARCH_DIALOG_MENU;
		this.appendChild(aonHeaderSearchDialogMenu);


	}

	goToParent() {
		if (!this.isMobile()) {
			let aonHeaderSearch = this.getElement(this.BASE_ID + 'Search');
			aonHeaderSearch.style.display = 'flex';


			//let aonHeaderCompanyName = this.getElement(this.AON_HEADER_COMPANY_NAME);
			//this.getCompanyName().then( name =>  {
			//	aonHeaderCompanyName.innerHTML = name; 
			//} ) ;

			if (!LS.isNewTheme() && !this.newTheme) {
				let aonShowMenu = this.getElement('aonShowMenu');
				aonShowMenu.style.display = 'none';
			}

			let aonMenu = this.getElement('aonMenu');
			aonMenu.removeAttribute('company');
			aonMenu.removeAttribute('user');
		}

		LS.setCompanySelected(false);

		let aonHeaderCompanyList = this.getElement(this.BASE_ID + 'CompanyList');
		aonHeaderCompanyList.style.display = 'none';

		this.removeAttribute('company');
		this.removeAttribute('user');

		LS.removeDomain();
		LS.removeCompany();

		clearDurum();
		this.rootPanel(new AonParent());

		let header = this.getElement("aonHeaderWeb");
		let apps = this.getElement("aonMenuLeftop-applications");
		let headerapp = this.getElement("aonHeaderApp");
		headerapp.style.display = "none";

		let logo = this.getElement("aonLogo");
		logo.style.display = "block";
		logo.style.filter = "none";

		let rootPanel = this.getElement("rootPanel");
		rootPanel.style.backgroundColor = "transparent";
		let enterprise = this.getElement("aonHeaderCompanyName");
		let appss = this.getElement("applications");
		let welcome = this.getElement("aonCompanyTabFilter");

		// if (welcome) {
		// 	const welcomeClickListener = (event) => {
		// 		event.preventDefault();
		// 		event.stopPropagation();
		// 		appss.removeEventListener("click", welcomeClickListener);
		// 	};

		// 	appss.addEventListener("click", welcomeClickListener);
		// }

		let header2 = this.getElement('aonHeaderWeb');
		header2.className = 'aonHeader aonHeaderStart';
		let applications = this.getElement('applications');
		applications.className = 'aonMenuLeftopStart';
		
		let aonHeaderCompanyName = this.getElement(this.AON_HEADER_COMPANY_NAME);
		this.getCompanyName().then( name =>  {
			aonHeaderCompanyName.innerHTML = name;
		}) ;
	}

	timeControlStatus(signin) {
		this.activeTimecontrol = true;
		let aonUserConnected = this.getElement('aonHeaderUserConnected');
		if (!aonUserConnected) {
			aonUserConnected = this.createElement('div');
			aonUserConnected.id = this.BASE_ID + 'UserConnected';
			aonUserConnected.className = 'aonConnected';

			let aonHeaderUserButtonIconButton = this.getElement('aonHeaderUserButtonIconButton');
			aonHeaderUserButtonIconButton.appendChild(aonUserConnected);
		}

		if (signin.status === 'in') {
			aonUserConnected.style.backgroundColor = '#86D364';
		} else if (signin.status === 'pause') {
			aonUserConnected.style.backgroundColor = '#F39F1D';
		} else {
			aonUserConnected.style.backgroundColor = '#DC4D30';
		}
	}

	aonFichar(signin) {
		getPosition().then(position => {
			if (position) {
				signin.coordinates = position.latitude + ',' + position.longitude;
			}
			saveTimeControl(signin).then(r => {
				let aonSign = this.getElement('aonSign');
				if (aonSign) {
					aonSign.buildSignin(r);
				}
				this.timeControlStatus(r);
			});
		});
	}

	buildLogo() {

		aonLogo.addEventListener('click', () => {
			if (LS.getDomainId()) {
				this.rootPanelHtml('<aon-desktop id="aonDesktop"></aon-desktop>');
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
		if (this.getAttribute('company')) {
			aonConfiguration.setAttribute('company', this.getAttribute('company'));
		}
		if (this.getAttribute('user')) {
			aonConfiguration.setAttribute('user', this.getAttribute('user'));
		}
	}

	showCompanyOption(company, onlyOne) {
		let aonHeaderCompanyList = this.getElement(this.AON_HEADER_COMPANY_LIST);
		aonHeaderCompanyList.style.display = company && !onlyOne ? 'block' : 'none';

		let aonHeaderCompanyListButton = this.getElement(this.AON_HEADER_COMPANY_LIST_BUTTON);
		aonHeaderCompanyListButton.style.display = company && !onlyOne ? 'block' : 'none';

		let aonHeaderHelp = this.getElement(this.AON_HEADER_HELP);
		aonHeaderHelp.style.display = company ? 'block' : 'none';

		let aonHeaderSearch = this.getElement(this.AON_HEADER_SEARCH);
		// aonHeaderSearch.style.display = company ? 'none' : 'flex';
		aonHeaderSearch.style.display = 'flex';
		if (!LS.isNewTheme() && !this.newTheme)
			aonHeaderSearch.style.marginLeft = '33px';

		let aonHeaderHome = this.getElement(this.AON_HEADER_HOME);
		aonHeaderHome.style.display = company ? 'block' : 'none';


		let aonHeaderCompanyName = this.getElement(this.AON_HEADER_COMPANY_NAME);
		this.getCompanyName(company).then(name => {
			aonHeaderCompanyName.innerHTML = name;
		}); 

		if (onlyOne) {
			// aonHeaderHome.style.right = '140px';
			// aonHeaderCompany.style.right = '180px';
			aonHeaderCompanyList.style.display = 'none';
		}
	}

	about() {
		const aboutDialogId = "aboutDialog";
		let d = this.getElement(aboutDialogId);
		if (!d) {
			d = new AonDialog();
			d.id = aboutDialogId;
			this.appendChild(d);
		}
		d.clear();
		if (!this.isMobile()) d.width = '400px';
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
			// this.getElement('aonHeaderHomeButton'),
			this.getElement('aonHeaderUserButton'),
			this.getElement('aonHeaderConfigButton'),
			this.getElement('aonHeaderNotificationButton'),
			this.getElement('aonHeaderCompanyListButton')
		];
		let texts = [
			this.getElement('aonHeaderApp'),
			this.getElement('aonHeaderCompanyName'),
			this.getElement('aonMenuListAppImgTop-applications')
		];
		let imgs = [
			this.getElement('aonLogo')
		];

		//
		if (color) {
			texts.forEach((text) => text.style.color = color);
			buttons.forEach((button) => {
				button.setColor(color);
				button.setBackgroundColor(backgroundColor);
			});
			//imgs.forEach( (img) => img.style.filter = 'invert(100%) sepia(0%) saturate(7470%) hue-rotate(111deg) brightness(106%) contrast(94%)' );
		} else {
			imgs.forEach((img) => img.style.removeProperty('filter'));
			texts.forEach((text) => text.style.removeProperty('color'));
			buttons.forEach((button) => button.setColor('var(--aonHeaderButtonColor)'));
		}
	}

	setClassName(className) {
		if (className) {
			this.getElement(this.AON_HEADER_WEB).className = className;
		} else {
			this.getElement(this.AON_HEADER_WEB).removeAttribute('className');
		}
	}

	setBackgroundColor(backgroundColor) {
		if (backgroundColor) {
			this.getElement(this.AON_HEADER_WEB).style.backgroundColor = backgroundColor;
		} else {
			this.getElement(this.AON_HEADER_WEB).style.removeProperty('background-color');
		}
	}

	buildApp(app, sidenav) {
		let a = this.createElement(TAG.A);
		a.classList.add('aonMenuApp');

		let div = this.createElement(TAG.DIV);
		div.classList.add("aonHeaderAppDiv");

		if (app.symbol) {
			let icon = this.createElement(TAG.SPAN);
			icon.id = `aonMenuListAppImgTop-${app.app}`;
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.innerHTML = app.symbol;
			icon.color = "var(--aonIcon)";
			icon.classList.add("aonHeaderAppIcon");
			div.appendChild(icon);
		}
		else {
			if (app.cssIcon) {
				const appColor = app.newColor || app.color;
				let aonIcon = new AonIcon();
				aonIcon.id = `aonMenuListAppImgTop-${app.app}`;
				aonIcon.icon = app.cssIcon;
				aonIcon.color = "var(--aonIcon)";
				aonIcon.size = "32px";
				div.appendChild(aonIcon);
			} else if (app.cssSymbol) {
				let icon = this.createElement(TAG.SPAN);
				icon.id = `aonMenuListAppImgTop-${app.app}`;
				icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
				icon.innerHTML = app.cssSymbol;
				icon.color = "var(--aonIcon)";
				icon.classList.add("aonHeaderAppIcon");
				div.appendChild(icon);
			} else if (app.cssLogo) {
				let img = this.createElement(TAG.IMG);
				img.id = `aonMenuListAppImgTop-${app.app}`;
				img.classList.add("aonHeaderAppIcon");
				img.src = app.cssLogo;
				img.title = app.title;
				div.appendChild(img);
			} else if (app.headerIcon) {
				let aonIcon = new AonIcon();
				aonIcon.id = `aonMenuListAppImg-${app.app}`;
				aonIcon.icon = app.headerIcon;
				aonIcon.color = "var(--aonIcon)";
				aonIcon.size = app.iconSize || "32px";
				div.appendChild(aonIcon);
			} else if (app.icon) {
				const appColor = app.newColor || app.color;
				let aonIcon = new AonIcon();
				aonIcon.id = `aonMenuListAppImgTop-${app.app}`;
				aonIcon.icon = app.newIcon || app.icon;
				aonIcon.color = "var(--aonIcon)";
				aonIcon.size = "32px";
				div.appendChild(aonIcon);
			} else if (app.symbol) {
				let icon = this.createElement(TAG.SPAN);
				icon.id = `aonMenuListAppImgTop-${app.app}`;
				icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
				icon.innerHTML = app.symbol;
				icon.color = "var(--aonIcon)";
				icon.classList.add("aonHeaderAppIcon");
				div.appendChild(icon);
			} else if (app.logo) {
				let img = this.createElement(TAG.IMG);
				img.id = `aonMenuListAppImgTop-${app.app}`;
				img.classList.add("aonHeaderAppIcon");
				img.src = app.logo;
				img.title = app.title;
				div.appendChild(img);
			}
		}

		if (app.title) {
			// let titles = app.title.match(/\b\w+\b/g);
			// for (let i = 0; i < 2; i++) {
			let span = this.createElement(TAG.SPAN);
			span.id = `aonMenuListAppTitle-${app.app}`;//-${i}`;
			span.classList.add("aonHeaderAppSpan");
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
		// this.setVisibleElement('aonHeaderHomeButton', visible)
	}

	setVisibleCompanyListButton(visible) {
		this.setVisibleElement('aonHeaderCompanyListButton', visible)
	}

	setVisibleElement(elementId, visible) {
		if (this.getElement(elementId)) {
			if (visible)
				this.getElement(elementId).style.removeProperty('display');
			else
				this.getElement(elementId).style.display = 'none';
		}
	}
	
	setCompanyName(name) {
		this.getElement(this.AON_HEADER_COMPANY_NAME).innerHTML = name;
	}

	getData() {
		return getNotification({ page: 1, perPage: 1, status: "unread" });
	}

	search(aonHeader) {

		let aonHeaderSearchBox = aonHeader.getElement(this.AON_HEADER_SEARCH_BOX);
		let aonHeaderSearchDialogMenu = aonHeader.getElement(this.AON_HEADER_SEARCH_DIALOG_MENU);

		let aonHeaderSearchBoxValue = aonHeaderSearchBox.value;
		
		if(!aonHeaderSearchBoxValue || aonHeaderSearchBoxValue.length === 0){
			aonHeaderSearchDialogMenu.close();
		} else {

			getCompanies().then(companies => {
				let searchCompanies = companies.filter(company => {
					const name = AonStringUtils.containsMatching(company?.name, aonHeaderSearchBoxValue);
					if (name) return true;
					const document = AonStringUtils.containsMatching(company?.document, aonHeaderSearchBoxValue);
					if (document) return true;
					const email = company?.emails?.some(email => AonStringUtils.containsMatching(email, aonHeaderSearchBoxValue));
					if (email) return true;
					const phone = company?.phones?.some(phone => AonStringUtils.containsMatching(phone, aonHeaderSearchBoxValue));
					if (phone) return true;
	
					return false;
				});
	
				let searchOptions = [];
	
				let title = searchCompanies.length == 1 ? `${MSG.ONE} ${MSG.ENTERPRISE}` : `${searchCompanies.length} ${MSG.ENTERPRISES}`;
				searchOptions.push({
					icon: MATERIAL_ICONS.BUSINESS,
					name: `<span style="font-weight: bold; cursor: default" header >${title}</span>`,
				});
	
				searchCompanies.slice(0, 10).forEach(company => {
	
					const companyName = this.decorateMatching(company.name, aonHeaderSearchBoxValue);
					const companyDocument = this.decorateMatching(company.document, aonHeaderSearchBoxValue);
					const companyEmail = this.decorateMatching(company.emails.find(email => AonStringUtils.containsMatching(email, aonHeaderSearchBoxValue)), aonHeaderSearchBoxValue);
					const companyPhone = this.decorateMatching(company.phones.find(phone => AonStringUtils.containsMatching(phone, aonHeaderSearchBoxValue)), aonHeaderSearchBoxValue);
	
					searchOptions.push({
						id: `Company${company.id}`,
						icon: aonHeader.getIcon(company),
						name: `<span>${companyName}&nbsp;${companyEmail}&nbsp;${companyPhone}</span><span style="float:right;">${companyDocument}<i id="Company${company.id}Copy" style="display: none; vertical-align: middle; font-size: 16px;" class="${CSS.MATERIAL_SYMBOLS_OUTLINED}">${MATERIAL_ICONS.CONTENT_COPY}</i></span>`,
						title: `${company.domain}`,
						fn: () => { aonHeader.companySelection(company); },
					});
				});
	
				searchOptions.push({
					icon: MATERIAL_ICONS.GROUP,
					name: `<span id="${this.AON_HEADER_SEARCH_DIALOG_MENU}Employees" style="font-weight: bold; cursor: default" class="${CSS.AON_COMPANY_FILTER_LOADING}" header >${MSG.EMPLOYEES}</span>`,
				});
	
				aonHeaderSearchDialogMenu.getContent().style.minWidth = `${aonHeaderSearchBox.offsetWidth * 1.5}px`;
	
				const top = aonHeaderSearchBox.getBoundingClientRect().bottom;
				const left = aonHeaderSearchBox.getBoundingClientRect().left;
				aonHeaderSearchDialogMenu.setMenuOptions(searchOptions, top, left);
				aonHeaderSearchDialogMenu.open();
	
				// Copy & Paste of company domain URL.
				this.setupDomainUrlCopy(searchOptions);
	
				let firstDayOfMonth = new Date();
				firstDayOfMonth.setUTCHours(0, 0, 0, 0);
				firstDayOfMonth.setUTCMonth(firstDayOfMonth.getUTCMonth() - 1, 1); // Set to first day of the previous month
	
				getAllContracts({ to: firstDayOfMonth.toISOString(), status: true, pattern: aonHeaderSearchBoxValue, limit: 26 })
					.then(contracts => {
	
						let searchOptions = [];
						contracts
							.map(contract => {
								contract.company = companies.find(company => company.domain == contract.domain);
								return contract;
							})
							.filter(contract => contract.company)
							.slice(0, 10)
							.forEach(contract => {
								const contractName = this.decorateMatching(contract.name, aonHeaderSearchBoxValue);
								const contractDocument = this.decorateMatching(contract.document, aonHeaderSearchBoxValue);
								const contractSSNumber = this.decorateMatching(contract.ssNumber, aonHeaderSearchBoxValue);
								const contractEndDate = contract.end_date ? `(${new Date(contract.end_date).toLocaleDateString()})` : '';
	
								const contractIdentifier = (this.isDecorated(contractSSNumber) && !this.isDecorated(contractDocument)) ? contractSSNumber : contractDocument;
	
								searchOptions.push({
									icon: MATERIAL_ICONS.PERSON,
									name: `<span>${contractName}</span><span style="margin-left: 16px" >${contractIdentifier}</span><span style="margin-left: 16px" >${contractEndDate}</span><span style="float:right;">${contract.company.name}</span>`,
									fn: () => {
										this.companySelection(contract.company, false, () => { GWT.iLoad(GWT.EMPLOYEES, undefined, { employeeSearch: contract.document || contract.name }) });
									},
								});
							});
						aonHeaderSearchDialogMenu.addMenuOptions(searchOptions);
	
						let employeesSpan = aonHeaderSearchDialogMenu.getElement(`${this.AON_HEADER_SEARCH_DIALOG_MENU}Employees`);
						employeesSpan.innerText = `${contracts.length > 25 ? '>' : ''} ${contracts.length} ${MSG.EMPLOYEES}`;
						employeesSpan.classList.remove(CSS.AON_COMPANY_FILTER_LOADING);
	
						let applicationsOptions = [];
						let aonMenu = this.getElement('aonMenu');
						let searchApplicationsOptions = aonMenu.getApplicationsOptions()?.filter(application => AonStringUtils.containsMatching(application.name, aonHeaderSearchBoxValue));
	
						let applicationsTitle = searchApplicationsOptions.length == 1 ? `${MSG.ONE} ${MSG.APPLICATION}` : `${searchApplicationsOptions.length} ${MSG.APPLICATIONS}`;
						applicationsOptions.push({
							icon: MATERIAL_ICONS.APPLICATIONS,
							name: `<span style="font-weight: bold; cursor: default" header >${applicationsTitle}</span>`,
						});
						searchApplicationsOptions.slice(0, 10).forEach(applicationOption => {
							const appicationName = this.decorateMatching(applicationOption.name, aonHeaderSearchBoxValue);
							applicationsOptions.push({ ...applicationOption, name: `<span>${appicationName}</span>` });
						});
						aonHeaderSearchDialogMenu.addMenuOptions(applicationsOptions);
	
						aonHeaderSearchDialogMenu.addMenuOptions([{
							icon: MATERIAL_ICONS.HELP,
							name: `<span id="${this.AON_HEADER_SEARCH_DIALOG_MENU}Help" style="font-weight: bold; cursor: default" class="${CSS.AON_COMPANY_FILTER_LOADING}" header >${MSG.HELP}</span>`,
						}]);
	
						getHelpDatas({ pattern: aonHeaderSearchBoxValue, limit: 11 })
							.then(helpDatas => {
	
								let helpOptions = [];
								helpDatas.forEach(helpData => {
									const helpFile = helpData.file;
									const helpTitle = this.decorateMatching(helpData.title, aonHeaderSearchBoxValue);
									const helpFileName = helpFile.replace(/\.[^/.]+$/, '');
	
									let tooltipUrl;
									if (helpData.uri.lastIndexOf('#') == -1) {
										tooltipUrl = AonStringUtils.b64EncodeUnicode(`../../Tooltip/?page=0&filename=${helpFile}`);
									} else {
										const helpName = helpData.uri.substr(helpData.uri.lastIndexOf('#') + 1);
										tooltipUrl = AonStringUtils.b64EncodeUnicode(`../../Tooltip/?name=${helpName}&filename=${helpFileName}`);
									}
	
									helpOptions.push({
										icon: MATERIAL_ICONS.OPEN_IN_NEW,
										title: `${helpTitle} ${helpFile}`,
										name: `<span style="text-transform : uppercase;" >${helpTitle}</span><span style="float:right;">${helpFileName}</span>`,
										fn: () => {
											window.open(helpData.uri, '_blank');
										},
										options: [
											{
												name: `<div style='width: 50rem;' ><iframe style='height: 16rem; width: 100%; border: none;' src='html/pdfjs/viewer.html?encoded=true&amp;file=${tooltipUrl}#zoom=page-width'><iframe></div>`
											}
										],
									});
								});
								aonHeaderSearchDialogMenu.addMenuOptions(helpOptions);
	
								let helpSpan = aonHeaderSearchDialogMenu.getElement(`${this.AON_HEADER_SEARCH_DIALOG_MENU}Help`);
								helpSpan.innerText = `${helpDatas.length > 10 ? '>' : ''} ${helpDatas.length} ${MSG.HELP_RESULTS}`;
								helpSpan.classList.remove(CSS.AON_COMPANY_FILTER_LOADING);
	
							});
					}).catch(error => console.log(error));
	
	
			});
		}

	}

	isDecorated(text) {
		return text?.includes('<b>');
	}

	decorateMatching(text, searcher) {
		if (!text)
			return '';
		if (!searcher)
			return text;

		let decoratedText = text;
		let matchingWords = AonStringUtils.getMatching(decoratedText, searcher);
		for (let matchingWord of matchingWords) {
			decoratedText = decoratedText.replaceAll(matchingWord, `<b decorate >${matchingWord}</b>`);
		}
		return decoratedText;
	}

	showSearch(aonHeader) {

		let aonHeaderSearchBox = aonHeader.getElement(this.AON_HEADER_SEARCH_BOX);
		let aonHeaderSearchDialogMenu = aonHeader.getElement(this.AON_HEADER_SEARCH_DIALOG_MENU);
		
		let aonHeaderSearchBoxValue = aonHeaderSearchBox ? aonHeaderSearchBox.value : '';

		// Only show if searchBok has value replace old
		// if (aonHeaderSearchDialogMenu.hasContent())
		if (aonHeaderSearchBoxValue && aonHeaderSearchBoxValue.length !== 0) {
			aonHeaderSearchDialogMenu.getContent().style.minWidth = `${aonHeaderSearchBox.offsetWidth * 1.5}px`;
			aonHeaderSearchDialogMenu.open();
		}

	}

	setupDomainUrlCopy(searchOptions) {
		searchOptions.filter(option => option.id).forEach(option => {
			this.getElement(option.id).addEventListener(EVENT.MOUSEOVER, () => {
				this.getElement(`${option.id}Copy`).style.removeProperty('display');
			});
			this.getElement(option.id).addEventListener(EVENT.MOUSEOUT, () => {
				this.getElement(`${option.id}Copy`).style.display = 'none';
			});
			this.getElement(`${option.id}Copy`).addEventListener(EVENT.CLICK, (event) => {
				event.stopPropagation();
				this.copyToClipboard(option.title).then(() => {
					this.showToast({ message: MSG.COPIED_TO_CLIPBOARD });
				}, err => {
					this.showError(err);
				})
			});
		});
	}

	copyToClipboard(textToCopy) {
		// Navigator clipboard api needs a secure context (https)
		if (navigator.clipboard && window.isSecureContext) {
			return navigator.clipboard.writeText(textToCopy);
		} else {
			return new Promise((resolve, reject) => {
				// Use the 'out of viewport hidden text area' trick
				const textArea = document.createElement(TAG.TEXTAREA);
				textArea.value = textToCopy;

				// Move textarea out of the viewport so it's not visible
				textArea.style.position = "absolute";
				textArea.style.left = "-999999px";

				document.body.prepend(textArea);
				textArea.select();

				try {
					document.execCommand('copy');
					resolve();
				} catch (error) {
					reject(error);
				} finally {
					textArea.remove();
				}
			});
		}
	}
	getIcon(company) {
		let icon = "business";
		if (company.type === 'OFFICE') icon = 'work';
		else if (company.parent) icon = MATERIAL_ICONS.APARTMENT;
		else if (company.shared) icon = MATERIAL_ICONS.SHARE;
		else if (!company.active) icon = 'domain_disabled';

		return icon;

	}

	showParent() {
		let aonParent = new AonParent();
		aonParent.id = "aonParent";
		this.rootPanel(aonParent);
	}

	showDesktop() {
		let aonDesktop = new AonDesktop();
		aonDesktop.id = "aonDesktop";
		this.rootPanel(aonDesktop);
	}

	showCompany(company) {
		if (company.domainManagement) {
			this.showParent();
		} else {
			this.showDesktop();
		}
	}

	companySelectionToPendingAccounting(company) {
	   this.companySelection(company, false, () => this.rootPanel(new AonInvoiceRecord()));
	 }
	 
	 companySelection(company, onlyOne, callback = (company) => this.showCompany(company)) {
		localStorage.setItem('company', JSON.stringify(company));
		LS.setDomainId(company.id);
		LS.setDomainName(company.domain);
		LS.setDomainLogin(company.login);
		localStorage.setItem("aon_domain_document", company.document);
		localStorage.setItem("onlyOne", onlyOne);

		if (!LS.isNewTheme() && (company.parentId || company.type !== 'CONSULTANCY')) {
			let aonShowMenu = this.getElement('aonShowMenu');
			aonShowMenu.style.display = 'block';
		}

		let aonHeaderHome = this.getElement(this.AON_HEADER_HOME);
		if (!LS.isNewTheme()) {
			aonHeaderHome.style.display = 'block';
		}

		let aonHeaderCompanyName = this.getElement(this.AON_HEADER_COMPANY_NAME);
		aonHeaderCompanyName.innerHTML = company.name;

		let aonHeaderCompany = this.getElement(this.AON_HEADER_COMPANY);
		aonHeaderCompany.style.display = 'block';

		if (!onlyOne) {
			LS.setCompanySelected(true);
			let aonHeaderCompanyList = this.getElement(this.AON_HEADER_COMPANY_LIST);
			aonHeaderCompanyList.style.display = 'block';
			let aonHeaderCompanyListButton = this.getElement(this.AON_HEADER_COMPANY_LIST_BUTTON);
			aonHeaderCompanyListButton.style.display = 'block';
		} else {
			aonHeaderHome.style.right = '140px';
			aonHeaderCompany.style.right = '180px';
		}

		let aonMenu = this.getElement('aonMenu');
		aonMenu.init().then(() => {
			aonMenu.open();
			let customUrl = LS.getDomainName() + '/customview?domain=' + company.domain;
			loadCustomView(customUrl).then(() => {
				favicon();
				title();
			}).catch(() => { });
			
			aonMenu.closeEmptyApps();
			
			// Check if fixed new button is needed
		    let aonMenuAppHover = this.getElement('aonMenuList-new');
		    let newFixedButton = this.getElement('newFixedButton');
		    
		    if(LS.getFixedButton() === 'on' && LS.isFutureTheme()){
				if(newFixedButton) newFixedButton.classList.remove('hidden');
				if(aonMenuAppHover) aonMenuAppHover.classList.add('hidden');
			} else {
				if(newFixedButton) newFixedButton.classList.add('hidden');
				if(aonMenuAppHover) aonMenuAppHover.classList.remove('hidden');
			}
			
		}
		);

		getUser().then(user => {
			localStorage.setItem('aon_domain_login', user.login);
			callback(company);
		});
	}



	buildMenuLeftop() {
		let aonMenuLeftop = this.getElement(this.AON_MENU_LEFTOP);

		let app = {
			home: true,
			title: MSG.APPLICATIONS,
			app: CONSTANT.APPLICATIONS,
			symbol: MATERIAL_ICONS.APPLICATIONS,
		}

		let appDiv = this.createElement(TAG.DIV);
		appDiv.id = `aonMenuLeftop-${app.app}`;
		appDiv.appendChild(this.buildApplicationsApp(app));
		aonMenuLeftop.appendChild(appDiv);
	}

	buildApplicationsApp(app) {
		let a = this.createElement(TAG.A);
		a.classList.add('aonMenuApp');

		let hoverDiv = this.createElement(TAG.DIV);
		hoverDiv.innerHTML = app.title;
		hoverDiv.style.display = 'none';
		hoverDiv.classList.add('aonMenuAppHover');
		a.appendChild(hoverDiv);

		let div = this.createElement(TAG.DIV);
		div.id = app.app;
		div.style.display = 'flex';
		div.style.alignItems = 'center';
		div.style.justifyContent = 'center';
		div.style.height = '100%';
		div.style.transition = 'background-color 0.2s';
		div.style.cursor = "pointer";
		div.title = app.title;

		if (app.symbol) {
			let icon = this.createElement(TAG.SPAN);
			icon.id = `aonMenuListAppImgTop-${app.app}`;
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.style.fontVariationSettings = "'FILL' 0, 'wght' 230, 'GRAD' 0, 'opsz' 24";
			icon.innerHTML = app.symbol;
			if (app.newColor || app.color) {
				icon.style.color = app.newColor || app.color;
			}
			icon.classList.add("aonNewMenuAppIcon");
			div.appendChild(icon);
		}

		a.appendChild(div);

		return a;
	}
	
	getCompanyName( company ) {
		return new Promise((resolve) => {
			if ( company )
				resolve(company.name);
			else
				this.buildDur().then((dur) => resolve(dur.domain?.description) );
		});
	}
}

window.customElements.define(TAG.AON_HEADER, AonHeader);
window.customElements.define(AON_DIALOG_SEARCH, AonDialogSearch);
