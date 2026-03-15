import { AonElement } from '../components/AonElement.js';
import { Apps, HomeApps, MenuApps, AuxApps, DESKTOP_APPS, MENU_APPS, TOP_MENU_APPS, AON_APPS, NEW, HOME, AON_CLASSIC, APPS, APPLICATIONS, NEW_APPS, SUPERSET, TOP_MENU_APPS_HOME, getConstNewApps, EXPAND_HIRIND, CONTENT_INDEX, MESSENGER, } from '../services/app.js';
import { COMMERCE, OFFICE, GARAGE, ACADEMY } from "../services/app.js";

import { ACCOUNTING_MENU, COMMERCIAL_MENU, GROUPWARE_MENU, MANAGEMENT_MENU, TREASURY_MENU, WAREHOUSE_MENU, FISCAL_MENU, PAYROLL_MENU, MARKETING_MENU, CONFIGURATION_MENU, ENTERPRISE_MENU, CONSOLE_MENU } from "../services/app.js"
import { MSG, CONSTANT, AON_ICONS, CSS, EVENT, MATERIAL_ICONS, TAG } from '../environments/environments.js';
import { AonDocumental } from '../modules/documental/aon-documental.js';
import '../modules/project/aon-project-panel.js';
import * as GWT from '../gwt/gwt.js';
import * as LS from '../services/localStorageService.js';
import * as JSF from './aon-jsf-app.js';
import { AonMessenger } from '../modules/messenger/aon-messenger.js';
import { AonIconButton } from '../components/aon-icon-button.js';
import { AonUploadToast } from "../components/aon-upload-toast.js";
import { AonDialogMenu } from '../components/aon-dialog-menu.js';
import { AonFiscal } from '../modules/fiscal/aon-fiscal.js';
import { AonTimecontrol } from '../modules/timecontrol/aon-timecontrol.js';
import { AonLaboral } from '../modules/laboral/aon-laboral.js';
import { AonComunica } from '../modules/laboral/aon-comunica.js';
import { AonAccounting } from '../modules/accounting/aon-accounting.js';
import { AonIcon } from '../components/aon-icon.js';
import { AonNote } from '../modules/note/aon-note.js';
import { AonInvoicePanel } from '../modules/invoice/aon-invoice-panel.js';
import { AonOfficePanel } from '../modules/office/aon-office-panel.js';
import { AonConsole } from '../modules/console/aon-console.js';
import { AonAppMenu } from '../modules/aon-app-menu.js';
import { AonNotes } from '../modules/note/aon-notes.js';
import { AonDesktop } from '../modules/company/aon-desktop.js';
import { AonWarehouse } from '../modules/warehouse/aon-warehouse.js';
//import { AonMarketing } from '../modules/marketing/aon-marketing.js';
import * as OPTION from '../modules/invoice/InvoiceOptions.js';
import { TASK_SOURCE } from '../modules/messenger/MessengerEnums.js';
import { uploadDocuments } from "../modules/documental/DocumentalUtils.js";

import { AonNewDesktop } from './aon-new-desktop.js';
import { AonAccountingMenu } from './accounting/aon-accounting-menu.js';
import { AonAccountingBeta } from './accounting/aon-accounting-beta.js';
import { AonCommercialMenu } from './commercial/aon-commercial-menu.js';
import { AonManagementMenu } from './management/aon-management-menu.js';
import { AonTreasuryMenu } from './treasury/aon-treasury-menu.js';
import { AonGroupwareMenu } from './groupware/aon-groupware-menu.js';
import { AonWarehouseMenu } from './warehouse/aon-warehouse-menu.js';
import { AonFiscalMenu } from './fiscal/aon-fiscal-menu.js';
import { AonFiscalBeta } from './fiscal/aon-fiscal-beta.js';
import { AonPayrollMenu } from './payroll/aon-payroll-menu.js';
import { AonMarketingMenu } from './marketing/aon-marketing-menu.js';
import { AonAcademyMenu } from './academy/aon-academy-menu.js';
import { AonCommerceMenu } from './commerce/aon-commerce-menu.js';
import { AonGarageMenu } from './garage/aon-garage-menu.js';
import { AonConfigurationMenu } from './configuration/aon-configuration-menu.js';
import { AonConfiguration } from '../modules/configuration/aon-configuration.js';
import { AonEnterpriseMenu } from './enterprise/aon-enterprise-menu.js';
import { AonConsoleMenu } from './console/aon-console-menu.js';
import { Superset } from './superset/superset.js';
import { AonSearchBox } from '../components/aon-search-box.js';

import { AonParent } from "./aon-parent.js";

import { generateJobId } from '../modules/invoice/InvoiceUtils.js';

import { getApplicationParameters } from '../services/applicationParameterService.js';
import { AonSuiteMenu } from './aon-suite-menu.js';
import { AonIncome } from './invoice/aon-income.js';
import { AonExpense } from './invoice/aon-expense.js';
import { Income } from './invoice/Income.js';
import { Expense } from './invoice/Expense.js';
import { createSelect } from '../components/CreateComponent.js';
import { getCompanyActivities } from '../services/companyService.js';
import { AonDialog } from '../components/aon-dialog.js';
import { AonPayrollBeta } from './payroll/aon-payroll-beta.js';
import { getInvoiceCount } from '../services/invoiceService.js';
import { getRelationShipCompany } from '../services/registryService.js';
import { AonTooltip } from '../components/aon-tooltip.js';
import { AonCommercialBeta } from './commercial/aon-commercial-beta.js';
import { AonManagementBeta } from './management/aon-management-beta.js';
import { AonTreasuryBeta } from './treasury/aon-treasury-beta.js';
import { AonWarehouseBeta } from './warehouse/aon-warehouse-beta.js';
import { AonGroupwareBeta } from './groupware/aon-groupware-beta.js';
import { AonMarketingBeta } from './marketing/aon-marketing-beta.js';

//	Falla la compilación por esta línea que no se usa. REVISAR!!
// import { FISCAL } from '../../../../target/aon-aio/environments/msg-es.js';
//

const ID = 'id';
const OPENED = 'opened';
const APP = 'app';

export class AonNewMenu extends AonElement {
	AON_MENU_TOPNAV;
	AON_MENU_LEFTOP;
	AON_MENU_SIDENAV;
	AON_MENU_APP_OPTIONS;
	AON_MENU_SEARCH;
	AON_MENU_SEARCH_BOX;
	AON_MENU_SEARCH_DIALOG;
	CLOSE;

	OPTION_DIALOG;

	supersetDashboard;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get opened() {
		return this.getAttribute('opened');
	}

	set opened(opened) {
		this.setAttribute('opened', opened);
	}

	get app() {
		return this.getAttribute('app');
	}

	set app(app) {
		this.setAttribute('app', app);
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	get user() {
		return this.getAttribute('user');
	}

	set user(user) {
		this.setAttribute('user', user);
	}

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
		this.init();
	}

	initialize() {
		this.AON_MENU_TOPNAV = 'aonMenuTopnav';
		this.AON_MENU_LEFTOP = 'aonMenuLeftop';
		this.AON_MENU_SIDENAV = 'aonMenuSidenav';
		this.AON_MENU_APP_OPTIONS = 'aonMenuAppOptions';
		this.AON_MENU_SEARCH = 'aonMenuSearch';
		this.AON_MENU_SEARCH_BOX = 'aonMenuSearchBox';
		this.AON_MENU_SEARCH_DIALOG = 'aonMenuSearchDialog';
		this.CLOSE = true;

		this.OPTION_DIALOG = 'newOptionDialog';
	}

	init() {

		this.setAttribute('opened', true);

		return new Promise((resolve, reject) => {
			this.buildDur()
				.then(() => {
					this.clear();
					this.build();
					resolve();
				})
				.catch(reject)
				;
		});

	}

	rootPanelMenu(menu) {
		const dur = this.getDur();
		menu.getDur = function() {
			return dur;
		};
		this.rootPanel(menu);
	}

	appSelection(app, sidenav) {
		let apps = this.getElement("applications");
		apps.className = '';
		const excludedApps = ['commerce', 'garage', 'academy', 'office'];
		const portalApps = DESKTOP_APPS.filter(app => this.isSidenavApp(app));
		const portalNoApps = DESKTOP_APPS.filter(app => !this.isApp(app));
		const suiteApps = TOP_MENU_APPS.filter(app => this.isApp(app));
		const suiteNoApps = TOP_MENU_APPS.filter(app => !this.isApp(app));
		if (!this.isApp(app) && !excludedApps.includes(app.app)) {
			this.rootPanel(new AonNewDesktop(portalApps, portalNoApps, suiteApps, suiteNoApps));
			let headerapp = this.getElement("aonHeaderApp");
			headerapp.style.display = "none";
			let logo = this.getElement("aonLogo");
			logo.classList.add("aonNewMenuAppSelectionLogo");

			let header2 = this.getElement('aonHeaderWeb');
			header2.className = 'aonHeader aonHeaderStart';
			let applications = this.getElement('applications');
			applications.className = 'aonMenuLeftopStart';
		} else {
			switch (app.app) {
				case NEW_APPS:
					this.rootPanel(new AonNewDesktop(portalApps, portalNoApps, suiteApps, suiteNoApps));
					break;
				case NEW.app:
					this.removeOldNewDialogContents();
					this.showNewDialogMenu(LS.isFutureTheme() ? this.getElement('aonMenuListAppImgTop-new') : this.getElement(app.app));
					break;
				case HOME.app:
					this.rootPanel(this.getDur().isDomainManagementAvailable() ? new AonParent() : new AonDesktop());
					break;
				case AON_CLASSIC.app:
					open(location.protocol + '//' + localStorage.getItem('aon_domain_name') + (location.port ? ":" + location.port : "") + '/login?token=' + localStorage.getItem('aon_session_id'))
					return;
				case Apps.CONSOLE.app:
					this.rootPanel(new AonConsole());
					break;
				case Apps.DOCUMENTAL.app:
					this.rootPanel(new AonDocumental());
					break;
				case Apps.ACCOUNTING.app:
					this.rootPanel(new AonAccounting());
					break;
				case Apps.FISCAL.app:
					this.rootPanel(new AonFiscal());
					break;
				case Apps.PAYROLL.app:
					this.rootPanel(new AonLaboral());
					break;
				case Apps.COMUNICA.app:
					this.rootPanel(new AonComunica());
					break;
				case Apps.INVOICE.app:
					this.rootPanel(new AonInvoicePanel());
					break;
				case Apps.TIMECONTROL.app:
					this.rootPanel(new AonTimecontrol());
					break;
				case Apps.MESSENGER.app:
					this.rootPanel(new AonMessenger());
					break;
				case AuxApps.TOOLS.app:
					this.buildAppMenu(AuxApps.TOOLS);
					break;
				case Apps.NOTES.app:
					this.rootPanel(new AonNotes());
					break;
				case Apps.OFFICE.app:
					this.rootPanel(new AonOfficePanel());
					break;
				case Apps.WAREHOUSE.app:
					this.rootPanel(new AonWarehouse());
					break;
				case Apps.MARKETING.app:
					this.rootPanel(new AonMarketing());
					break;
				case HomeApps.HOME.app:
					this.rootPanel(new AonDesktop());
					break;
				case HomeApps.APPLICATIONS.app:
					this.showApplicationsDialog();
					break;
				case ACCOUNTING_MENU.app:
				case FISCAL_MENU.app:
				case PAYROLL_MENU.app:
				case COMMERCIAL_MENU.app:
				case GROUPWARE_MENU.app:
				case MANAGEMENT_MENU.app:
				case TREASURY_MENU.app:
				case WAREHOUSE_MENU.app:
				case MARKETING_MENU.app:
				case CONFIGURATION_MENU.app:
				case ACADEMY.app:
				case COMMERCE.app:
				case GARAGE.app:
				case ENTERPRISE_MENU.app:
				case CONSOLE_MENU.app:
					this.rootPanelMenu(this.getAonSuiteMenu(app));
					break;
				case SUPERSET.app:
					//"39aa7e93-a3a2-4bf2-b6c6-1297d00bd0e0"
					this.rootPanel(new Superset(this.supersetDashboard));
					return;
				case EXPAND_HIRIND.app:
					GWT.iLoad(GWT.PRODUCT_CATALOGUE_MODULE);
					break;
				case CONTENT_INDEX.app:
					this.rootPanel(new JSF.AonJsfHelpContent())
					this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_OPEN, {}));
					break;
				default/*Apps.HOME*/:
					this.rootPanel(new AonNewDesktop(portalApps, portalNoApps, suiteApps, suiteNoApps));
					break;
			}
		}

		let detail = {
			app,
			sidenav
		};

		if (this.isApp(app) || excludedApps.includes(app.app)) {
			if (app.app != "new") {
				this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_SELECT, { detail }));
				this.setSelectedMenuSidenav(app);
			}

		}

		if (app.app != "new") {
			let appsDiv = this.getElement("aonMenuLeftop-applications");
			appsDiv.style.removeProperty('background-color');
			let appName = app.app[0].toUpperCase() + app.app.slice(1);
			appsDiv.className = `${CSS.AON_MENU_LEFTOP}${appName}`;
			//appsDiv.className = `${CSS.AON_MENU_LEFTOP} ${CSS.AON_MENU_LEFTOP}${appName}`;
		}

	}

	removeOldNewDialogContents() {
		const elements = document.querySelectorAll('#newDialogDialogMenuContent');
		elements.forEach(element => element.remove());
	}

	getAonSuiteMenu(app) {
		switch (app.app) {
			case ACCOUNTING_MENU.app:
				return this.isDomainManagementAvailable() ? new AonAccountingMenu() : new AonAccountingBeta();
			case COMMERCIAL_MENU.app:
				return this.isDomainManagementAvailable() ? new AonCommercialMenu() : new AonCommercialBeta();
			case GROUPWARE_MENU.app:
				return this.isDomainManagementAvailable() ? new AonGroupwareMenu() : new AonGroupwareBeta();
			case MANAGEMENT_MENU.app:
				return this.isDomainManagementAvailable() ? new AonManagementMenu() : new AonManagementBeta();
			case TREASURY_MENU.app:
				return this.isDomainManagementAvailable() ? new AonTreasuryMenu() : new AonTreasuryBeta();
			case WAREHOUSE_MENU.app:
				return this.isDomainManagementAvailable() ? new AonWarehouseMenu() : new AonWarehouseBeta();
			case FISCAL_MENU.app:
				return this.isDomainManagementAvailable() ? new AonFiscalMenu() : new AonFiscalBeta();
			case PAYROLL_MENU.app:
				return this.isDomainManagementAvailable() ? new AonPayrollMenu() : new AonPayrollBeta();
			case MARKETING_MENU.app:
				return this.isDomainManagementAvailable() ? new AonMarketingMenu() : new AonMarketingBeta();
			case CONFIGURATION_MENU.app:
				return new AonConfiguration();
			case ACADEMY.app:
				return new AonAcademyMenu();
			case COMMERCE.app:
				return new AonCommerceMenu();
			case GARAGE.app:
				return new AonGarageMenu();
			case ENTERPRISE_MENU.app:
				return new AonEnterpriseMenu();
			case CONSOLE_MENU.app:
				return new AonConsoleMenu();
			default:
				return new AonSuiteMenu();
		}
	}

	build() {
		this.buildMenuLeftop();

		let aonMenuSidenav = this.createElement(TAG.DIV);
		aonMenuSidenav.id = this.AON_MENU_SIDENAV;
		aonMenuSidenav.className = CSS.AON_MENU_SIDENAV;
		aonMenuSidenav.classList.add("hiddenMenuLeft");

		// Set default close/open status
		let aonMenuAnchor = this.getElement("aonMenuAnchor");
		if (aonMenuAnchor && aonMenuAnchor.classList.contains("close"))
			aonMenuSidenav.classList.add("close");
		else
			aonMenuSidenav.classList.remove("close");

		this.appendChild(aonMenuSidenav);
		aonMenuSidenav.classList.add("aonNewMenuSideNav");
		this.buildMenuSidenav();

		let aonMenuTopnav = this.createElement(TAG.DIV);
		aonMenuTopnav.id = this.AON_MENU_TOPNAV;
		aonMenuTopnav.className = CSS.AON_MENU_TOPNAV;
		this.appendChild(aonMenuTopnav);
		aonMenuTopnav.classList.add(CSS.AON_MENU_TOP_NAV_HIDE);
		this.buildMenuTopnav();

		let header = this.getElement('aonHeaderWeb');
		header.className = 'aonHeader aonHeaderStart';
		let applications = this.getElement('applications');
		applications.className = 'aonMenuLeftopStart';

		let aonSearchDialog = new AonDialogMenu();
		aonSearchDialog.id = this.AON_MENU_SEARCH_DIALOG;
		this.appendChild(aonSearchDialog);
		
		if (!this._delegatedHandlerAdded) {

		    document.addEventListener(EVENT.CLICK, (e) => {
		        if (e.target.closest('.newFixedButton')) {
		
		            this.removeOldNewDialogContents();
		
		            let newFixedButton = document.querySelector('aon-new-fixed-button .newFixedButton');
		            
		            if (newFixedButton.classList.contains('open')) {
		                let newDialogMenu = this.getElement('newDialogMenu');
		                newDialogMenu.close();
		            } else {
		                this.showNewDialogMenu(newFixedButton, true);
		                
		                let searchWidget = this.getElement('aonHeaderSearch');
		                if(searchWidget) searchWidget.classList.remove('open');
		                
		                let searchDialogWidget = this.getElement('aonHeaderSearchDialogMenu');
						if (searchDialogWidget) searchDialogWidget.close();
					
		            }
		        }
		    });
		
		    this._delegatedHandlerAdded = true;
		}
	}

	buildMenuLeftop() {
		let aonMenuLeftopAnchor = document.querySelector('#aonMenuLeftop a');
		aonMenuLeftopAnchor.addEventListener(EVENT.CLICK, () => {
			this.appSelection(HomeApps.APPLICATIONS);
		});
	}

	overrideDefault(app, suffix) {
		app.logo = this.getCssVariable(`${app.app}SideNavLogo`) || app.logo;
		app.icon = this.getCssVariable(`${app.app}SideNavIcon`) || app.icon;
		app.symbol = this.getCssVariable(`${app.app}SideNavSymbol`) || app.symbol;
	}

	buildMenuSidenav() {
		let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		let ul = this.createElement(TAG.UL);
		ul.classList.add(CSS.AON_UL);
		ul.id = 'aonMenuList';
		ul.classList.add("aonNewMenuSideNavUl");

		const newApps = getConstNewApps(this.getDur(), this.isAyudaT());
		const index = MENU_APPS.findIndex(app => app.app === CONSTANT.APPS);
		if (index !== -1) {
			MENU_APPS[index] = newApps;  // Reemplazamos el valor segun donde estemos
		}

		for (let item in MENU_APPS) {
			if (this.isSidenavApp(MENU_APPS[item])) {
				let app = MENU_APPS[item];
				this.addMenuSidenavApp(ul, app);
			}
		}

		this.getSupersetDashboard()
			.then((dashboard) => {
				this.supersetDashboard = dashboard;
				this.addMenuSidenavApp(ul, SUPERSET);
			})
			.finally(end => this.createBookPlansButton(ul, EXPAND_HIRIND));

		let li2 = this.createElement(TAG.LI);
		li2.classList.add("aonNewMenuSideNavLi2");
		ul.appendChild(li2);

		aonMenuSidenav.innerHTML = '';
		aonMenuSidenav.appendChild(ul);

		for (let item in MENU_APPS) {
			if (this.isSidenavApp(MENU_APPS[item])) {
				let app = MENU_APPS[item];
				let icon = this.getElement(`aonMenuListAppImgTop-${app.app}`);
				let message = this.getMessage(app.app);
				if (icon && message && message.length > 0)
					new AonTooltip(
						icon,
						this.getMessage(app.app),
						{ position: 'right' }
					);
			}
		}

	}

	createBookPlansButton(ul, app) {

		// Ficha Cliente
		let company = LS.getCompany();
		if (company && company.domain && company.registry) {
			getRelationShipCompany({
				url: company.domain,
				relatedRegistry: company.registry
			}).then(relationshipCompany => {

				let li = this.createElement(TAG.LI);
				li.id = `aonMenuList-${app.app}`;
				li.classList.add("aonNewMenuSideNavLi");

				app.cssSymbol = this.getCssVariable(`${app.app}SideNavSymbol`);

				let a = this.createElement(TAG.A);
				a.addEventListener(EVENT.CLICK, () => {
					this.appSelection(app);
				});
				a.classList.add('aonMenuApp');

				let hoverDiv = this.createElement(TAG.DIV);
				hoverDiv.innerHTML = app.title;
				if (!app.title) {
					hoverDiv.classList.add('aonMenuAppHoverHidden');
				}
				hoverDiv.style.display = 'none';
				hoverDiv.classList.add('aonMenuAppHover');
				a.appendChild(hoverDiv);

				let div = this.createElement(TAG.DIV);
				div.id = app.app;
				div.classList.add("aonNewMenuAppDiv");
				div.title = app.title;

				if (app.symbol) {
					let icon = this.createElement(TAG.SPAN);
					icon.id = `aonMenuListAppImgTop-${app.app}`;
					icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
					icon.style.fontVariationSettings = "'FILL' 0, 'wght' 230, 'GRAD' 0, 'opsz' 24";
					icon.innerHTML = app.symbol;
					icon.classList.add("aonNewMenuAppIcon");
					div.appendChild(icon);
				} else if (app.cssSymbol) {
					let icon = this.createElement(TAG.SPAN);
					icon.id = `aonMenuListAppImgTop-${app.app}`;
					icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
					icon.innerHTML = app.cssSymbol;
					if (app.newColor || app.color) {
						icon.style.color = app.newColor || app.color;
					}
					icon.classList.add("aonNewMenuAppIcon");
					div.appendChild(icon);
				}

				if (app.description) {
					let span = this.createElement(TAG.SPAN);
					span.id = `aonMenuListAppTitle-${app.app}`;//-${i}`;
					span.classList.add("aonNewMenuAppSpan");
					span.innerHTML = app.description;
					div.appendChild(span);
				}

				a.appendChild(div);

				li.appendChild(a);

				ul.appendChild(li);

			});
		}

	}

	addMenuSidenavApp(ul, app) {
		let li = this.createElement(TAG.LI);
		li.id = `aonMenuList-${app.app}`;
		li.classList.add("aonNewMenuSideNavLi");

		app.cssLogo = this.getCssVariable(`${app.app}SideNavLogo`);
		app.cssIcon = this.getCssVariable(`${app.app}SideNavIcon`);
		app.cssSymbol = this.getCssVariable(`${app.app}SideNavSymbol`);
		li.appendChild(this.buildApp(app, { color: `var(--aonSidenavIconColor, ${app.newColor || app.color})` }));
		ul.appendChild(li);
	}

	buildMenuTopnav() {
		const showAllApps = LS.isAppMenu();
		let aonMenuTopnav = this.getElement(this.AON_MENU_TOPNAV);
		let aonTopMenuDiv = this.createElement(TAG.DIV);
		aonTopMenuDiv.classList.add("aonNewMenuTopNavDiv");
		aonTopMenuDiv.id = "aonTopMenuDiv";

		const excludedApps = ['commerce', 'garage', 'academy', 'office'];
		
		for (let item in TOP_MENU_APPS) {

			let app = TOP_MENU_APPS[item];

			if (!this.isApp(app)) {
				if (!showAllApps || excludedApps.includes(app.app)) {
					continue;
				} else {
					let appElement = this.buildTopApp(app);
					
					appElement.classList.add("aonNewMenuTopNavAppElement");
					app.color = "var(--aonTopMenuNotAvailable)";
					aonTopMenuDiv.appendChild(appElement);
					continue;
				}
			}

			let appElement = this.buildTopApp(app);
			aonTopMenuDiv.appendChild(appElement);
		}


		this.clearElement(aonMenuTopnav);
		aonMenuTopnav.appendChild(aonTopMenuDiv);
	}

	reloadTopNav() {
		let aonMenuTopnav = this.getElement(this.AON_MENU_TOPNAV);
		this.clearElement(aonMenuTopnav);
		this.buildMenuTopnav();
	}

	buildTopApp(app) {
		let appDiv = this.createElement(TAG.DIV);
		appDiv.id = `aonMenuBar-${app.app}`;
		appDiv.classList.add("aonNewMenuTopAppDivApp");
		appDiv.appendChild(this.buildApp(app, undefined, appDiv.id));

		return appDiv;
	}

	showTopNav() {
		let topnav = this.getElement(this.AON_MENU_TOPNAV);
		topnav.classList.remove(CSS.AON_MENU_TOP_NAV_HIDE);
		topnav.classList.add(CSS.AON_MENU_TOP_NAV_VISIBLE);
	}

	hideTopNav() {
		let topnav = this.getElement(this.AON_MENU_TOPNAV);
		topnav.classList.remove(CSS.AON_MENU_TOP_NAV_VISIBLE);
		topnav.classList.add(CSS.AON_MENU_TOP_NAV_HIDE);
	}

	isTopNavVisible() {
		let topnav = this.getElement(this.AON_MENU_TOPNAV);
		return topnav.style.height == '68px';
	}

	buildApp(app, style, id) {

		let a = this.createElement(TAG.A);
		a.addEventListener(EVENT.CLICK, () => {
			this.appSelection(app);
		});
		a.classList.add('aonMenuApp');

		let hoverDiv = this.createElement(TAG.DIV);
		hoverDiv.innerHTML = app.title;
		if (!app.title) {
			hoverDiv.classList.add('aonMenuAppHoverHidden');
		}
		hoverDiv.style.display = 'none';
		hoverDiv.classList.add('aonMenuAppHover');
		a.appendChild(hoverDiv);
		let div = this.createElement(TAG.DIV);
		div.id = app.app;
		div.classList.add("aonNewMenuAppDiv");
		div.title = app.title;
		if (style?.height) {
			div.style.height = style.height;
		}
		if (style?.flexDirection) {
			div.style.flexDirection = style?.flexDirection;
		}
		div.title = app.title;
		let header = this.getElement("aonHeaderWeb");
		let welcome = this.getElement("aonCompanyTabFilter");

		if (app.symbol) {
			let icon = this.createElement(TAG.SPAN);
			icon.id = `aonMenuListAppImgTop-${app.app}`;
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.style.fontVariationSettings = "'FILL' 0, 'wght' 230, 'GRAD' 0, 'opsz' 24";
			icon.innerHTML = app.symbol;
			if (app.title == 'Planes' && (!LS.isFutureTheme() || this.isAyudaT()) && (this.getDur().isAdmin() || this.getDur().isEnterprise())) {
				icon.style.color = "green";
			}
			if (app.newColor || app.color) {
				icon.style.color = app.newColor || app.color;
			}
			icon.classList.add("aonNewMenuAppIcon");
			div.appendChild(icon);
		} else if (app.cssIcon) {
			const appColor = app.newColor || app.color;
			let aonIcon = new AonIcon();
			aonIcon.id = `aonMenuListAppImgTop-${app.app}`;
			aonIcon.icon = app.cssIcon;
			aonIcon.color = style?.color || appColor;
			if (app.app == "accounting") {
				aonIcon.size = "26px";
			} else {
				aonIcon.size = "32px";
			}
			div.appendChild(aonIcon);
		} else if (app.cssSymbol) {
			let icon = this.createElement(TAG.SPAN);
			icon.id = `aonMenuListAppImgTop-${app.app}`;
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.innerHTML = app.cssSymbol;
			if (app.newColor || app.color) {
				icon.style.color = app.newColor || app.color;
			}
			icon.classList.add("aonNewMenuAppIcon");
			div.appendChild(icon);
		} else if (app.cssLogo) {
			let img = this.createElement(TAG.IMG);
			img.id = `aonMenuListAppImgTop-${app.app}`;
			img.classList.add("aonNewMenuAppImg");
			img.src = app.cssLogo;
			img.title = app.title;
			div.appendChild(img);
		} else if (app.icon) {
			const appColor = app.newColor || app.color;
			let aonIcon = new AonIcon();
			aonIcon.id = `aonMenuListAppImgTop-${app.app}`;
			aonIcon.icon = app.newIcon || app.icon;
			aonIcon.color = style?.color || appColor;
			if (app.app == "accounting") {
				aonIcon.size = "26px";
			} else {
				aonIcon.size = "32px";
			}
			div.appendChild(aonIcon);
		} else if (app.logo) {
			let img = this.createElement(TAG.IMG);
			img.id = `aonMenuListAppImgTop-${app.app}`;
			img.classList.add("aonNewMenuAppImg");
			img.src = app.logo;
			img.title = app.title;
			div.appendChild(img);
		}

		if (app.description) {
			let span = this.createElement(TAG.SPAN);
			span.id = `aonMenuListAppTitle-${app.app}`;//-${i}`;
			span.classList.add("aonNewMenuAppSpan");
			span.innerHTML = app.description;
			div.appendChild(span);
		}

		a.appendChild(div);

		if (id == "aonMenuBar-home") {
			div.id = "topMenuHome";
		}

		return a;
	}

	getMessage(app) {
		switch (app) {
			case NEW.app:
				return "Crea facturas, sube documentos, crea solicitudes y empleados";
			case AON_CLASSIC.app:
				return "Vuelve a la versión clásica de nuesta app";
			case Apps.DOCUMENTAL.app:
				return "Almacena y comparte documentación adicional, como modelos fiscales de años anteriores o facturas de otro software";
			case Apps.INVOICE.app:
				return "Emita facturas, registra gastos o crea clientes, acreedores y proveedores";
			case Apps.TIMECONTROL.app:
				return "Registra la jornada laboral acorde a la normativa vigente";
			case Apps.NOTES.app:
				return "Crea y organiza anotaciones de todo lo que necesites";
			case CONTENT_INDEX.app:
				return "Consulta nuestros manueles para aprender nuevas funcionalidades";
			case MESSENGER.app:
				return "Crear y administra tus solicitudes";
			default:
				return "";
		}
	}

	isElementAt(ev, el) {
		const viewportX = ev.clientX;
		const viewportY = ev.clientY;
		let elements = document.elementsFromPoint(viewportX, viewportY);
		for (let element of elements) {
			console.log(element.tagName + ": " + (element === el));
			if (element === el) {
				return true;
			}

		}
		return false;
	}

	isCSSLoaded(cssFileName) {
		for (let sheet of document.styleSheets) {
			if (sheet.href && sheet.href.includes(cssFileName)) {
				return true;
			}
		}
		return false;
	}

	getCssVariable(variable) {
		return getComputedStyle(document.body).getPropertyValue(`--${variable}`);
	}

	buildAppMenu(app) {
		let company;
		if (this.getAttribute('company')) {
			company = JSON.parse(this.getAttribute('company'));
		}

		let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		aonMenuSidenav.classList.add("aonNewMenuAppMenuSideNav");

		let div = this.createElement(TAG.DIV);
		div.className = 'aonMenuSidenavAppToolbar';
		div.style.backgroundColor = this.getAppToolbarBackgroundColor(app);

		let span = this.createElement(TAG.SPAN);
		span.className = 'aonTitle';
		span.innerHTML = app.title;

		let span2 = this.createElement(TAG.SPAN);
		span2.className = 'aonRight40';
		let aibS = new AonIconButton();
		aibS.id = "aon-menu-sidenav-app-launch-button";
		aibS.icon = "launch";
		aibS.color = "white";
		aibS.noHover = "true";
		span2.appendChild(aibS);

		let span3 = this.createElement(TAG.SPAN);
		span3.className = 'aonRight0';
		let aibC = new AonIconButton();
		aibC.id = "aon-menu-sidenav-app-close-button";
		aibC.icon = "close";
		aibC.color = "white";
		aibC.noHover = "true";
		span3.appendChild(aibC);
		div.appendChild(span);
		div.appendChild(span2);
		div.appendChild(span3);

		let div2 = this.createElement(TAG.DIV);
		let ul = this.createElement(TAG.UL);
		ul.classList.add(CSS.AON_UL);
		ul.classList.add(CSS.AON_MENU_SIDENAV_SUBAPP_LIST);
		this.getSubApps(app.app).forEach(subapp => {
			if (!subapp.parent || (subapp.parent && company && company.parent)) {
				ul.appendChild(this.buildSubAppMenu(subapp));
			}
		});

		div2.appendChild(ul);

		aonMenuSidenav.innerHTML = '';
		aonMenuSidenav.appendChild(div);
		aonMenuSidenav.appendChild(div2);

		let launchButton = this.getElement('aon-menu-sidenav-app-launch-button');
		launchButton.addEventListener(EVENT.CLICK, () => {
			if (Apps.ACCOUNTING.app === app.app) {
				this.rootPanelHtml('<aon-accounting></aon-accounting>');
			} else if (Apps.FISCAL.app === app.app) {
				this.rootPanel(new AonFiscal());
			} else if (Apps.PAYROLL.app === app.app) {
				this.rootPanel(new AonLaboral());
			}
		});
		let closeButton = this.getElement('aon-menu-sidenav-app-close-button');
		closeButton.addEventListener(EVENT.CLICK, () => {
			aonMenuSidenav.style.width = '175px';
			this.buildMenu();
		});
	}

	development(title) {
		let aonApplication = this.getApplication();
		aonApplication.development(title);
	}

	getSubApps(app) {
		switch (app) {
			case Apps.ACCOUNTING.app:
				return this.getDur().isAccountingManager() ? AccountingMenu : AccountingPortalMenu;
			case Apps.FISCAL.app:
				return AeatFiscalMenu;
			case Apps.PAYROLL.app:
				return PayrollMenu;
			case AuxApps.TOOLS.app:
				return ToolsMenu;
		}
	}

	buildSubAppMenu(subapp) {
		let li = this.createElement(TAG.LI);
		li.className = CSS.AON_MENU_SIDENAV_SUBAPP_LIST_ITEM;;
		li.title = subapp.title;
		li.classList.add("aonNewMenuSubAppMenuLi");

		let a = this.createElement(TAG.A);
		a.className = CSS.AON_MENU_LINK;

		let span = this.createElement(TAG.SPAN);
		span.classList.add("aonNewMenuSubAppMenuSpan");
		span.innerHTML = subapp.title;
		a.appendChild(span);
		li.appendChild(a);
		li.addEventListener(EVENT.CLICK, () => {
			if (subapp.content) {
				this.rootPanelHtml(subapp.content);
			} else if (subapp.initAction) {
				open('https://' + localStorage.getItem('aon_domain_name') + '/login?initAction=' + subapp.initAction + '&token=' + localStorage.getItem('aon_session_id'));
			} else GWT.startModule(subapp.module, subapp.entryPoint);

			this.buildMenu();
		});
		return li
	}


	getAppToolbarBackgroundColor(app) {
		let company;
		if (this.getAttribute('company')) {
			company = JSON.parse(this.getAttribute('company'));
		}
		if (Apps.FISCAL.app === app.app) {
			if (company && company.administration && 'ALAVA' === company.administration) {
				return '#a30c51';
			} else if (company && company.administration && 'BIZKAIA' === company.administration) {
				return '#d70004';
			} else if (company && company.administration && 'GIPUZKOA' === company.administration) {
				return '#a1c031';
			} else if (company && company.administration && 'NAVARRA' === company.administration) {
				return '#da002a';
			} else return '#3a85c3';
		}
		const appColor = app.newColor || app.color;
		return appColor ? appColor : '#f1f1f1';
	}

	addApp(app) {
		let ul = this.getElement('aonMenuList');
		ul.appendChild(this.buildApp(app));
	}

	removeApp(app) {
		let ul = this.getElement('aonMenuList');
		let li = this.getElement('aonMenuList' + app.app);
		ul.removeChild(li);
	}
	
	closeEmptyApps(){
		let aonMenuTopnav = this.getElement(this.AON_MENU_TOPNAV);
		let aonTopMenuDiv = this.getElement('aonTopMenuDiv');
		
		if ((aonMenuTopnav && aonMenuTopnav.childNodes.length == 0) || (aonTopMenuDiv && aonTopMenuDiv.childNodes.length == 0)) {
			this.close();
		}
	}

	close() {
		this.hideTopNav();
	}

	open() {
		if (LS.isTopMenu()) {
			this.showTopNav();
		}
	}

	isExpanded() {
		return this.expanded;
	}

	setExpanded(expanded) {
		this.expanded = expanded;
	}

	getBackgroundHover(backgroundColor) {
		return backgroundColor && !this.isLightColor(this.hexToRgb(backgroundColor)) ? "rgba(255, 255, 255, 0.15)" : "rgba(0, 0, 0, 0.05)";
	}

	isLightColor(colorString) {
		const rgba = colorString.replace(/[^\d,]/g, '').split(',').map(Number);
		const [r, g, b] = rgba;
		const brightness = (r * 299 + g * 587 + b * 114) / 1000;
		return brightness > 128;
	}

	hexToRgb(hex) {
		hex = hex.replace(/^#/, '');
		if (hex.length === 3)
			hex = hex.split('').map(c => c + c).join('');
		const r = parseInt(hex.substring(0, 2), 16);
		const g = parseInt(hex.substring(2, 4), 16);
		const b = parseInt(hex.substring(4, 6), 16);
		return `rgb(${r}, ${g}, ${b})`;
	}

	isApp(app) {
		if (OFFICE.app === app.app)
			return this.getDur().isOffice();
		if (ACADEMY.app === app.app)
			return this.getDur().isAcademy();
		if (COMMERCE.app === app.app)
			return this.getDur().isCommerce();
		if (GARAGE.app === app.app)
			return this.getDur().isGarage();
		if (COMMERCIAL_MENU.app === app.app)
			return this.getDur().isCommercial();
		if (MANAGEMENT_MENU.app === app.app)
			return this.getDur().isManagement();
		if (TREASURY_MENU.app === app.app)
			return this.getDur().isTreasury();
		if (WAREHOUSE_MENU.app === app.app)
			return this.getDur().isWarehouse();
		if (GROUPWARE_MENU.app === app.app)
			return this.getDur().isGroupware();
		if (ACCOUNTING_MENU.app === app.app)
			return this.getDur().isAccountingManager();
		if (FISCAL_MENU.app === app.app)
			return this.getDur().isFiscalManager();
		if (PAYROLL_MENU.app === app.app)
			return this.getDur().isPayrollManager();
		if (MARKETING_MENU.app === app.app)
			return this.getDur().isMarketing();
		if (CONFIGURATION_MENU.app === app.app)
			return this.getDur().isAdmin();
		if (ENTERPRISE_MENU.app === app.app)
			return this.getDur().isDomainManagementAvailable();
		if (CONSOLE_MENU.app === app.app)
			return this.getDur().isConsole();
		if (MenuApps.ACCOUNTING.app === app.app)
			return this.getDur().isAccountingUser();
		if (MenuApps.FISCAL.app === app.app)
			return this.getDur().isFiscalUser();
		if (MenuApps.PAYROLL.app === app.app)
			return this.getDur().isPayrollUser();
		if (MenuApps.COMUNICA.app === app.app)
			return !this.getDur().isPayroll()
				&& (this.getDur().isComunica()
					|| this.getDur().isSaltra());
		if (MenuApps.DOCUMENTAL.app === app.app)
			return this.getDur().isDocumental();
		if (MenuApps.TIMECONTROL.app === app.app)
			return !this.getDur().isDomainManagementAvailable() &&
				(this.getDur().isTimecontrol()
					|| this.getDur().isTimecontrolManager());
		if (MenuApps.INVOICE.app === app.app)
			return this.getDur().isInvoice();
		if (MenuApps.MESSENGER.app === app.app)
			return this.getDur().isMessenger();
		if (AON_CLASSIC.app === app.app)
			return this.getDur().isAon();
		if (NEW.app === app.app)
			return this.getDur().isAon()
				|| this.getDur().isInvoice()
				|| this.getDur().isMessenger()
				|| this.getDur().isDocumental()
				;
		if (HOME.app === app.app)
			return !this.getDur().isConsole();
		if (APPS.app === app.app)
			return !this.getDur().isConsole();
		if (EXPAND_HIRIND.app === app.app)
			return !this.getDur().isConsole();
		if (SUPERSET.app === app.app)
			return !this.getDur().isConsole();
		if (APPLICATIONS.app === app.app)
			return !this.getDur().isConsole();
		if (MenuApps.NOTES.app === app.app)
			return !this.getDur().isConsole();
		if (CONTENT_INDEX.app === app.app)
			return !this.getDur().isConsole();
		if (MenuApps.TOOLS.app === app.app)
			return this.isBeta() && !LS.isNewTheme();
		if (MenuApps.OFFICE.app === app.app)
			return (this.isBeta() || this.getDur().isOffice())
				&& this.getDur().hasOffice()
				&& this.getDur().isManagementManager();
		if (MenuApps.MARKETING.app === app.app)
			return this.isBeta() && this.getDur().isMarketing();
		if (MenuApps.WAREHOUSE.app === app.app) {
			const domain = this.getDur().getDomain();
			return !this.getDur().isConsole()
				&& domain.getName()
				&& (domain.getName().includes("udapa")
					|| domain.getName().includes("paturpat")
					|| this.isLocal());
		}
		if (MenuApps.CONSOLE.app === app.app)
			return this.getDur().isConsole();

		return false;
	}

	isSidenavApp(app) {
		return this.isApp(app)
			&& (
				HOME.app == app.app
				|| APPS.app == app.app
				//|| SUPERSET.app == app.app
				|| AON_CLASSIC.app == app.app
				|| APPLICATIONS.app == app.app
				|| MenuApps.DOCUMENTAL.app == app.app
				|| !this.getDur().isDomainManagementAvailable()
			);
	}


	isAppEnabled(app) {
		return this.isSidenavApp(app);
	}

	buildAppMenuOptions(app) {

		let div = this.getElement(this.AON_MENU_APP_OPTIONS);
		if (div === undefined || div === null) {
			let div = this.createElement(TAG.DIV);
			div.id = this.AON_MENU_APP_OPTIONS;
			this.appendChild(div);
		}
		if (app && div && (!div.style.display || div.style.display == 'none')) {
			this.clearElement(div);
			div.classList.add("aonNewMenuAppMenuOptionsDiv");
			let appMenu = new AonAppMenu();
			appMenu.setApp(app);
			appMenu.setDur(this.dur);

			div.appendChild(appMenu);

		}
	}

	isTopNav(a) {
		let element = a;
		do {
			element = element.parentElement;
		} while (element && element.id != this.AON_MENU_SIDENAV && element.id != this.AON_MENU_TOPNAV);

		return element && element.id == this.AON_MENU_TOPNAV;
	}

	showNewDialogMenu(el, isFixedButton = false) {
		
		let newMenuOptions = [];
		if (this.getDur().isInvoice()) {
			let optionsMenu = [
				{
					name: MSG.ISSUEDS,
					icon: MATERIAL_ICONS.UNARCHIVE,
					fn: () => this.newInvoice('emitida')
				}, {
					name: MSG.RECEIVEDS,
					icon: MATERIAL_ICONS.ARCHIVE,
					fn: () => this.newInvoice('recibida')
				}, {
					name: MSG.TICKETS + "/" + MSG.SUPPORTING_DOCUMENTS,
					icon: MATERIAL_ICONS.RECEIPT,
					fn: () => this.newInvoice('ticket')
				}
			];

			if (this.getDur().isOcr() || this.getDur().isInvofox()) {
				optionsMenu.push({
					name: MSG.UPLOAD_INVOICE,
					icon: MATERIAL_ICONS.CLOUD_UPLOAD,
					fn: async () => {
						// Check trial limit
						let exceedTrail = await this.exceedTrailInvoinces();
						if (exceedTrail) return;

						let input = this.createElement(TAG.INPUT);
						input.type = CONSTANT.FILE;
						input.accept = this.accept;
						input.className = CSS.AON_NONE;
						input.multiple = 'multiple';

						input.addEventListener(EVENT.CHANGE, ({ target }) => {

							getCompanyActivities({}).then(activities => {
								let data = { uploaded: 0 }
								if (activities.length > 1) {
									activities.push({
										id: "all",
										description: "TODAS"
									});
									let activity = createSelect(this.ACTIVITY, MSG.ACTIVITY);
									activity.setAlias("id", "description");
									if (activities.length > 0) {
										activity.setOptions(activities);
										activity.value = activities[0].id;
									}

									let d = new AonDialog();
									let rootPanel = document.getElementById("rootPanel");
									rootPanel.appendChild(d);
									d.clear();

									d.setTitle(MSG.UPLOAD_INVOICE);
									d.setContent(activity);
									d.addAcceptAction(() => {
										data.activity = activity.getValueObject().id;
										let uploadToast = this.getElement('aonUploadToast');
										if (!uploadToast) {
											uploadToast = new AonUploadToast();
											this.getApplication().getContent().appendChild(uploadToast);
										}

										uploadToast.setJobId(generateJobId());
										for (let file of target.files) {
											uploadToast.addFile("invoice", file, data);
										}
									});
									d.open();
								} else {
									if (activities.length > 0) {
										data.activity = activities[0].id;
									}
									let uploadToast = this.getElement('aonUploadToast');
									if (!uploadToast) {
										uploadToast = new AonUploadToast();
										this.getApplication().getContent().appendChild(uploadToast);
									}

									uploadToast.setJobId(generateJobId());
									for (let file of target.files) {
										uploadToast.addFile("invoice", file, data);
									}
								}
							});
						});
						input.click();
					}
				});
			}

			let otherOptions = [
				{
					name: 'Nuevo ingreso',
					icon: 'add_card',
					fn: () => this.getApplication().setContent(new AonIncome(new Income()))
				}, {
					name: "Nuevo gasto",
					icon: MATERIAL_ICONS.ACCOUNT_BALANCE_WALLET,
					fn: () => this.getApplication().setContent(new AonExpense(new Expense()))
				}
			];

			newMenuOptions.push({
				fn: () => { },
				icon: 'note_add',
				name: MSG.NEW_INVOICE,
				options: optionsMenu
			});

			newMenuOptions.push({
				fn: () => { },
				icon: MATERIAL_ICONS.ACCOUNT_BALANCE_WALLET,
				name: 'Otros gastos/ingresos',
				options: otherOptions
			});
		}

		if (this.getDur().isDocumental()) {
			newMenuOptions.push({
				fn: () => {
					let input = this.createElement(TAG.INPUT);
					input.type = CONSTANT.FILE;
					input.accept = this.accept;
					input.className = CSS.AON_NONE;
					input.addEventListener(EVENT.CHANGE, ({ target }) => uploadDocuments(input, target.files));
					input.click();
				},
				icon: MATERIAL_ICONS.CLOUD_UPLOAD,
				name: LS.isFutureTheme() ? 'Subir a mi nube' : MSG.UPLOAD_DOCUMENT,
			});
		}
		if (this.getDur().isMessenger()) {
			newMenuOptions.push({
				icon: 'add_comment',
				name: MSG.CREATE_QUERY,
				fn: () => {
					let aonMessengerChat = new AonMessenger();
					aonMessengerChat.data = { source: TASK_SOURCE.QUERY };
					this.rootPanel(aonMessengerChat);
				}
			});
		}
		if (this.getDur().isPayroll() && this.getDur().isMessenger()) {
			newMenuOptions.push({
				icon: 'person_add',
				name: MSG.NEW_EMPLOYEE,
				fn: () => {
					let aonMessengerChat = new AonMessenger();
					aonMessengerChat.data = { source: TASK_SOURCE.REQUEST };
					this.rootPanel(aonMessengerChat);

					this.isElementLoaded("#sourceTask").then(sourceTaskSelect => {
						console.log(sourceTaskSelect);
						sourceTaskSelect.value = "request";
						this.isElementLoaded("#processType").then(processTypeSelect => {
							console.log(processTypeSelect);
							processTypeSelect.value = "2";
							this.isElementLoaded("#aonMessengerToolbarHeaderTitleSectionMenuIconButton").then(sidenavBtn => {
								console.log(sidenavBtn);
								sidenavBtn.click();
							});
						});
					});
				}
			});
		}
		
		
		const top = el.getBoundingClientRect().top;
		const left = el.getBoundingClientRect().right;

		if (LS.isFutureTheme()) {

			let newDialogMenu = this.getElement('newDialogMenu');
			newDialogMenu.setOptions(newMenuOptions, LS.isFutureTheme());
			newDialogMenu.open(el, isFixedButton);

		} else {

			let newDialogMenu = this.getElement(this.OPTION_DIALOG);
			newDialogMenu.setMenuOptions(newMenuOptions, top, left);
			newDialogMenu.open();

		}

	}

	async isElementLoaded(selector) {
		while (document.querySelector(selector) === null) {
			await new Promise(resolve => requestAnimationFrame(resolve))
		}
		return document.querySelector(selector);
	};

	setAppClassName(app) {
		let appsDiv = this.getElement("aonMenuLeftop-applications");
		appsDiv.style.removeProperty('background-color');
		let appName = app.app[0].toUpperCase() + app.app.slice(1);
		appsDiv.className = `${CSS.AON_MENU_LEFTOP}${appName}`
		//appsDiv.className = `${CSS.AON_MENU_LEFTOP} ${CSS.AON_MENU_LEFTOP}${appName}`		
	}

	async newInvoice(invoice) {
		// Check trial limit
		let exceedTrail = await this.exceedTrailInvoinces();
		if (exceedTrail) return;

		let invoicePanel = new AonInvoicePanel();
		invoicePanel.option = OPTION.CREATE_INVOICE_ISSUED;
		invoicePanel.addEventListener(EVENT.BUILD, () => invoicePanel.aonInvoice(invoice));
		this.rootPanel(invoicePanel);
		this.setAppClassName(Apps.INVOICE);
		this.setSelectedMenuSidenav(Apps.INVOICE);
		this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_SELECT, { detail: { app: Apps.INVOICE } }));
	}

	async exceedTrailInvoinces() {
		// Check trial limit
		const result = await getInvoiceCount();

		if (this.getDur().isTrial() && result.invoiceCount >= this.getDur().getTrialValue()) {
			this.getApplication().confirmDialog(
				"Límite alcanzado",
				"Ha alcanzado el límite de prueba del módulo de facturación. Para poder registrar nuevas facturas debe ampliar su plan actual. ¿Desea navegar a los planes disponibles?",
				async () => {
					GWT.iLoad(GWT.PRODUCT_CATALOGUE_MODULE);
				}
			);
			return true;
		}

		return false;
	}

	setSelectedMenuSidenav(app) {
		const aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		const aonMenuSidenavLis = aonMenuSidenav.getElementsByTagName(TAG.LI);
		for (const aonMenuSidenavLi of aonMenuSidenavLis) {
			if (aonMenuSidenavLi.id === `aonMenuList-${app?.app}`) {
				aonMenuSidenavLi.classList.add("aonMenuSidenavLiSeleted");
			} else {
				aonMenuSidenavLi.classList.remove("aonMenuSidenavLiSeleted");
			}
		}
	}

	getSupersetDashboard() {
		return new Promise((resolve, reject) => {
			reject("Superset dashboard disabled");
		});
/*		return new Promise((resolve, reject) => {
			getApplicationParameters({ params: ['SUPERSET_DASHBOARD'] })
				.then(appParams => {
					if (appParams?.length > 0) {
						appParams.sort((a, b) => b.domain - a.domain);
						resolve(appParams[0].value);
					} else {
						//reject(new Error("Superset dashboard not found"));
						console.error("Superset dashboard not found");
						reject(null);
					}
				})
				.catch(err => {
					console.error("Superset", err);
					reject(null);
				});
			;
		});
*/	}

	getApplicationsOptions() {

		let applicationsOptions = [];

		let topMenuApps = TOP_MENU_APPS.filter(app => this.isApp(app));

		for (let topMenuApp of topMenuApps) {
			let aonSuiteMenu = this.getAonSuiteMenu(topMenuApp);
			aonSuiteMenu.setDur(this.getDur());
			aonSuiteMenu.getOptions?.()
				.filter(subMenu => !subMenu.filter || subMenu.filter())
				.forEach(subMenu => {
					subMenu.options
						.filter(option => !option.filter || option.filter())
						.forEach(option => {
							let name = (option.description || option.title)?.trim();
							if (name?.length === 0)
								return;
							let found = applicationsOptions.find(opt => opt.name.localeCompare(name) == 0);
							if (found)
								return;
							applicationsOptions.push({
								name,
								icon: topMenuApp.symbol,
								fn: () => { option.action(); },
							})
						});
				});
		}

		applicationsOptions.sort((op1, op2) => op1?.name?.localeCompare(op2?.name));

		let desktopApps = DESKTOP_APPS.filter(app => this.isSidenavApp(app));
		for (let desktopApp of desktopApps) {
			let name = (desktopApp.description || desktopApp.title)?.trim();
			if (name?.length === 0)
				break;
			let size = 20;
			let icon = desktopApp.symbol;
			let image = icon ? undefined : desktopApp.logo;
			let aonIcon = icon ? undefined : (desktopApp.newIcon || desktopApp.icon);
			let color = desktopApp.newColor || desktopApp.color;
			let icon_class = icon ? CSS.MATERIAL_SYMBOLS_OUTLINED : undefined;
			applicationsOptions.unshift({
				size,
				name,
				icon,
				image,
				color,
				aonIcon,
				icon_class,
				fn: () => { this.appSelection(desktopApp); }
			});
		}

		return applicationsOptions;
	}

	showApplicationsDialog() {
		let el = this.getElement(HomeApps.APPLICATIONS.app);
		let aonMenuSearchDialog = this.getElement(this.AON_MENU_SEARCH_DIALOG);

		let applicationsOptions = this.getApplicationsOptions();

		let aonMenuSearch = this.createElement(TAG.SPAN);
		aonMenuSearch.id = this.AON_MENU_SEARCH;
		aonMenuSearch.classList.add("aonMenuSearch");


		let aonMenuSearchBox = new AonSearchBox();
		aonMenuSearchBox.id = this.AON_MENU_SEARCH_BOX;
		aonMenuSearchBox.newTheme = this.newTheme;
		aonMenuSearchBox.addEventListener(EVENT.KEYUP, () => {
			clearTimeout(this.searchTimeoutId);
			this.searchTimeoutId = setTimeout(() => {
				let pattern = aonMenuSearchBox.value?.trim()?.toUpperCase()
					.normalize("NFD").replace(/\p{Diacritic}/gu, "");

				let aonMenuSearchDialog = this.getElement(this.AON_MENU_SEARCH_DIALOG);
				let optionsLi = aonMenuSearchDialog.getContent().getElementsByTagName(TAG.LI);
				for (let i = 0; i < optionsLi.length; i++) {
					let li = optionsLi.item(i);

					let text = Array.from(li.getElementsByTagName(TAG.SPAN))
						.map(span => span.innerText?.trim()).join('').toLocaleUpperCase()
						.normalize("NFD").replace(/\p{Diacritic}/gu, "");

					if (pattern.length === 0 || text.includes(pattern)) {
						li.style.removeProperty('display');
					} else {
						li.style.display = 'none';
					}
				}
			}, 1000);

		});
		aonMenuSearchBox.addEventListener(EVENT.BUILD, () => {
			let aonMenuSearchBoxDiv = aonMenuSearchBox.firstChild;
			aonMenuSearchBoxDiv.style.height = '32px';
			aonMenuSearchBoxDiv.style.border = 'none';
			aonMenuSearchBoxDiv.style.borderRadius = '4px';
			aonMenuSearchBoxDiv.style.backgroundColor = "var(--aonSearchBar)";
			aonMenuSearchBoxDiv.style.alignItems = 'center';
			aonMenuSearchBoxDiv.style.boxShadow = '0 2px 4px rgba(0,0,0,.14),0 0 2px rgba(0,0,0,.12)';
		});


		aonMenuSearch.appendChild(aonMenuSearchBox);


		const top = el.getBoundingClientRect().bottom;
		const left = el.getBoundingClientRect().left;
		aonMenuSearchDialog.setMenuOptions(applicationsOptions, top, left);

		let aonMenuSearchDialogContent = aonMenuSearchDialog.getContent();
		aonMenuSearchDialogContent.style.width = '500px';
		aonMenuSearchDialogContent.style.padding = '6px';
		aonMenuSearchDialogContent.classList.add(CSS.AON_DIALOG_MENU_APPS_CONTENT);
		aonMenuSearchDialogContent.insertBefore(aonMenuSearch, aonMenuSearchDialogContent.firstChild);

		aonMenuSearchDialog.open();

	}

	isDomainManagementAvailable() {
		return this.getDur().isDomainManagementAvailable();
	}

}
if (!window.customElements.get(TAG.AON_NEW_MENU)) {
	window.customElements.define(TAG.AON_NEW_MENU, AonNewMenu);
}
