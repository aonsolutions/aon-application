import { AonElement } from 'aonsolutions/components/AonElement.js';
import { Apps, HomeApps, MenuApps, AuxApps, DESKTOP_APPS, MENU_APPS, TOP_MENU_APPS, AON_APPS, NEW, HOME, AON_CLASSIC, APPS, APPLICATIONS, NEW_APPS } from '../services/app.js';
import {COMMERCE, OFFICE, GARAGE, ACADEMY} from  "aonsolutions/services/app.js";
import {ACCOUNTING_MENU, COMMERCIAL_MENU, GROUPWARE_MENU, MANAGEMENT_MENU, TREASURY_MENU, WAREHOUSE_MENU, FISCAL_MENU, PAYROLL_MENU, MARKETING_MENU, CONFIGURATION_MENU} from "../services/app.js"
import { MSG, CONSTANT, AON_ICONS, CSS, EVENT, MATERIAL_ICONS, TAG } from 'aonsolutions/environments/environments.js';
import { AonDocumental } from 'aonsolutions/modules/documental/aon-documental.js';
import 'aonsolutions/modules/project/aon-project-panel.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';
import * as LS from 'aonsolutions/services/localStorageService.js';
import { AonMessenger } from 'aonsolutions/modules/messenger/aon-messenger.js';
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import { AonUploadToast } from "aonsolutions/components/aon-upload-toast.js";
import { AonDialogMenu } from 'aonsolutions/components/aon-dialog-menu.js';
import { AonFiscal } from 'aonsolutions/modules/fiscal/aon-fiscal.js';
import { AonTimecontrol } from 'aonsolutions/modules/timecontrol/aon-timecontrol.js';
import { AonLaboral } from 'aonsolutions/modules/laboral/aon-laboral.js';
import { AonComunica } from 'aonsolutions/modules/laboral/aon-comunica.js';
import { AonAccounting } from 'aonsolutions/modules/accounting/aon-accounting.js';
import { AonIcon } from 'aonsolutions/components/aon-icon.js';
import { AonNote } from 'aonsolutions/modules/note/aon-note.js';
import { AonInvoicePanel } from 'aonsolutions/modules/invoice/aon-invoice-panel.js';
import { AonOfficePanel } from 'aonsolutions/modules/office/aon-office-panel.js';
import { AonConsole } from 'aonsolutions/modules/console/aon-console.js';
import { AonAppMenu } from 'aonsolutions/modules/aon-app-menu.js';
import { AonNotes } from 'aonsolutions/modules/note/aon-notes.js';
import { AonDesktop } from 'aonsolutions/modules/company/aon-desktop.js';
import { AonWarehouse } from 'aonsolutions/modules/warehouse/aon-warehouse.js';
//import { AonMarketing } from 'aonsolutions/modules/marketing/aon-marketing.js';
import * as OPTION from 'aonsolutions/modules/invoice/InvoiceOptions.js';
import { TASK_SOURCE } from 'aonsolutions/modules/messenger/MessengerEnums.js';
import { uploadDocuments } from "aonsolutions/modules/documental/DocumentalUtils.js";

import { AonNewDesktop } from './aon-new-desktop.js';
import { AonAccountingMenu } from './accounting/aon-accounting-menu.js';
import { AonCommercialMenu } from './commercial/aon-commercial-menu.js';
import { AonManagementMenu } from './management/aon-management-menu.js';
import { AonTreasuryMenu } from './treasury/aon-treasury-menu.js';
import { AonGroupwareMenu } from './groupware/aon-groupware-menu.js';
import { AonWarehouseMenu } from './warehouse/aon-warehouse-menu.js';
import { AonFiscalMenu } from './fiscal/aon-fiscal-menu.js';
import { AonPayrollMenu } from './payroll/aon-payroll-menu.js';
import { AonMarketingMenu } from './marketing/aon-marketing-menu.js';
import { AonAcademyMenu } from './academy/aon-academy-menu.js';
import { AonCommerceMenu } from './commerce/aon-commerce-menu.js';
import { AonGarageMenu } from './garage/aon-garage-menu.js';
import { AonConfigurationMenu } from './configuration/aon-configuration-menu.js';

import { generateJobId } from 'aonsolutions/modules/invoice/InvoiceUtils.js';

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
	CLOSE;

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
		this.CLOSE = true;
	}

	init() {
		
		this.setAttribute('opened', true);
		
		return new Promise((resolve, reject) => {
			this.buildDur().then(()=> {
				this.build();
				resolve();
			});
		});

		
		//if(localStorage.getItem('aon_domain_id') && localStorage.getItem('company')){
		//	getDomainUserRoles({}).then(r => {
		//		this.dur = new DomainUserRoles(r);
		//		this.build();
		//	});
		//}
	}

	appSelection(app, sidenav) {
		let apps = this.getElement("applications");
		apps.className = '';
		const excludedApps = ['commerce', 'garage', 'academy', 'office'];
		const portalApps = DESKTOP_APPS.filter( app => this.isApp(app) ); 
		const portalNoApps = DESKTOP_APPS.filter( app => !this.isApp(app) ); 
		const suiteApps = TOP_MENU_APPS.filter(app => this.isApp(app));
		const suiteNoApps = TOP_MENU_APPS.filter(app => !this.isApp(app));
		if (!this.isApp(app) && !excludedApps.includes(app.app)) {
			this.rootPanel(new AonNewDesktop(portalApps,portalNoApps,suiteApps,suiteNoApps));
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
					this.showNewDialogMenu(this.getElement(app.app));
					break;
				case HOME.app:
					this.rootPanel(new AonDesktop());
					break;
				case AON_CLASSIC.app:
					open('https://' + localStorage.getItem('aon_domain_name') + '/login?token=' + localStorage.getItem('aon_session_id'))
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
				case ACCOUNTING_MENU.app:
					this.rootPanel(new AonAccountingMenu());
					break;
				case COMMERCIAL_MENU.app:
					this.rootPanel(new AonCommercialMenu());
					break;
				case GROUPWARE_MENU.app:
					this.rootPanel(new AonGroupwareMenu());
					break;
				case MANAGEMENT_MENU.app:
					this.rootPanel(new AonManagementMenu());
					break;
				case TREASURY_MENU.app:
					this.rootPanel(new AonTreasuryMenu());
					break;
				case WAREHOUSE_MENU.app:
					this.rootPanel(new AonWarehouseMenu());
					break;
				case FISCAL_MENU.app:
					this.rootPanel(new AonFiscalMenu());
					break;
				case PAYROLL_MENU.app:
					this.rootPanel(new AonPayrollMenu());
					break;
				case MARKETING_MENU.app:
					this.rootPanel(new AonMarketingMenu());
					break;
				case CONFIGURATION_MENU.app:
					this.rootPanel(new AonConfigurationMenu());
					break;
				case ACADEMY.app:
					this.rootPanel(new AonAcademyMenu());
					break;
				case COMMERCE.app:
					this.rootPanel(new AonCommerceMenu());
					break;
				case GARAGE.app:
					this.rootPanel(new AonGarageMenu());
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

        if(this.isApp(app) || excludedApps.includes(app.app)){
			if(app.app != "new"){
				this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_SELECT, { detail }));	
				this.setSelectedMenuSidenav(app);
			}
			
		}
		if(app.app != "new"){
			let appsDiv = this.getElement("aonMenuLeftop-applications");
			appsDiv.style.removeProperty('background-color'); 
			let appName = app.app[0].toUpperCase() + app.app.slice(1);
			appsDiv.className = `${CSS.AON_MENU_LEFTOP} ${CSS.AON_MENU_LEFTOP}${appName}`	
		}
	
	}
	

	build() {
		let aonMenuLefttop = this.createElement(TAG.DIV);
		aonMenuLefttop.id = this.AON_MENU_LEFTOP;
		aonMenuLefttop.className = CSS.AON_MENU_LEFTOP;
		this.appendChild(aonMenuLefttop);
		aonMenuLefttop.classList.add("aonNewMenuLeftTop");
		this.buildMenuLeftop();
		// let icon = this.getElement("aonMenuLeftop");
		aonMenuLefttop.style.visibility = "visible";


		let aonMenuSidenav = this.createElement(TAG.DIV);
		aonMenuSidenav.id = this.AON_MENU_SIDENAV;
		aonMenuSidenav.className = CSS.AON_MENU_SIDENAV;
		aonMenuSidenav.classList.add("hiddenMenuLeft");
		this.appendChild(aonMenuSidenav);
		aonMenuSidenav.classList.add("aonNewMenuSideNav");
		this.getRootPanel().style.marginLeft = '0px';
		this.buildMenuSidenav();

		let aonMenuTopnav = this.createElement(TAG.DIV);
		aonMenuTopnav.id = this.AON_MENU_TOPNAV;
		aonMenuTopnav.className = CSS.AON_MENU_TOPNAV;
		this.appendChild(aonMenuTopnav);
		aonMenuTopnav.classList.add("aonNewMenuTopNav");
		this.getRootPanel().style.marginTop = '1px'; //'69px';
		this.buildMenuTopnav();

		let header = this.getElement('aonHeaderWeb');
		header.className = 'aonHeader aonHeaderStart';
		let applications = this.getElement('applications');
		applications.className = 'aonMenuLeftopStart';
	}

	buildMenuLeftop() {
		let aonMenuLeftop = this.getElement(this.AON_MENU_LEFTOP);

		let div = this.createElement(TAG.DIV);
		div.classList.add("aonNewMenuLeftTopDiv");
		
		let app = HomeApps.APPLICATIONS;
		let appDiv = this.createElement(TAG.DIV);
		appDiv.id = `aonMenuLeftop-${app.app}`;
		appDiv.classList.add("aonNewMenuLeftTopAppDiv");
		appDiv.appendChild(this.buildApp(app, {height:'48px'}));
		div.appendChild(appDiv);

		aonMenuLeftop.appendChild(div);
	}

	overrideDefault( app, suffix ){
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
		
		for (let item in MENU_APPS) {
			if (this.isApp(MENU_APPS[item])) {
				let app = MENU_APPS[item];
				let li = this.createElement(TAG.LI);
				li.id = `aonMenuList-${app.app}`;
				li.classList.add("aonNewMenuSideNavLi");

				app.cssLogo = this.getCssVariable(`${app.app}SideNavLogo`) ;
				app.cssIcon = this.getCssVariable(`${app.app}SideNavIcon`) ;
				app.cssSymbol = this.getCssVariable(`${app.app}SideNavSymbol`);
				
				li.appendChild(this.buildApp(app,{color: `var(--aonSidenavIconColor, ${app.newColor || app.color})`}));
				ul.appendChild(li);
			}
		}

		let li2 = this.createElement(TAG.LI);
		li2.classList.add("aonNewMenuSideNavLi2");
		ul.appendChild(li2);

		aonMenuSidenav.innerHTML = '';
		aonMenuSidenav.appendChild(ul);

	}
	
	buildMenuTopnav() {
		const showAllApps = LS.isAppMenu();
		let aonMenuTopnav = this.getElement(this.AON_MENU_TOPNAV);
		let div = this.createElement(TAG.DIV);
		div.classList.add("aonNewMenuTopNavDiv");
		div.id = "aonTopMenuDiv";

		let sidenav = this.getElement("aonMenuSidenav");
	
		if (!LS.isPortalChecked()) {
			let topMenuHome = div.appendChild(this.buildTopApp(HOME));
			topMenuHome.id = "topMenuHome";
		}
	
		const excludedApps = ['commerce', 'garage', 'academy', 'office'];
	
		for (let item in TOP_MENU_APPS) {
			let app = TOP_MENU_APPS[item];
	
			if (!this.isApp(app)) {
				if (!showAllApps || excludedApps.includes(app.app)) {
					continue;
				} else {
					let appElement = this.buildTopApp(app);

					// app.cssLogo = this.getCssVariable(`${app.app}TopNavLogo`);
					// app.cssIcon = this.getCssVariable(`${app.app}TopNavIcon`);
					// app.cssSymbol = this.getCssVariable(`${app.app}TopNavSymbol`);

					appElement.classList.add("aonNewMenuTopNavAppElement");
					app.color = "var(--aonTopMenuNotAvailable)";
					div.appendChild(appElement);
					continue;
				}
			}
	
			let appElement = this.buildTopApp(app);
			div.appendChild(appElement);
		}
		
		// if(!LS.isLeftMenu() && this.isCSSLoaded("beta.css")){
		// 	for (let item in MENU_APPS) {
		// 		let app = MENU_APPS[item];
		// 		if(app.app!= "home" && app.app!= "new"){
		// 			if (this.isApp(MENU_APPS[item])){
		// 				div.appendChild(this.buildTopApp(MENU_APPS[item]));
		// 			}
		// 		}
						
		// 	}
			
		// }

		this.clearElement(aonMenuTopnav);
		aonMenuTopnav.appendChild(div);
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
		appDiv.appendChild(this.buildApp(app,undefined));
	
		return appDiv;
	}
	
	showTopNav() {	
		let topnav = this.getElement(this.AON_MENU_TOPNAV);
		let rootPanel = this.getElement("rootPanel");
		let rightPanel = this.getElement("aonRightPanel");

		topnav.style.height = '68px';
		rootPanel.style.marginTop = `${rootPanel.style.marginTop + 69}px`;
		if(rightPanel){
			rightPanel.style.marginTop = topnav.offsetHeight;
			rightPanel.style.height = `calc(100vh - 61px)`;
		
		}
		
		rootPanel.style.marginTop = "68px";
		rootPanel.style.height = `calc(100vh - 61px)`;
	}

	hideTopNav() {
		let topnav = this.getElement(this.AON_MENU_TOPNAV);
		let rootPanel = this.getElement("rootPanel");
		let rightPanel = this.getElement("aonRightPanel");
		topnav.style.height = '0px';
		if(rightPanel){
			rightPanel.style.marginTop = "1px";
			rightPanel.style.height = `calc(100vh - 49px)`;
		}
		rootPanel.style.marginTop = "1px";
	
		rootPanel.style.height = `calc(100vh - 50px)`;
	}

	isTopNavVisible() {
		let topnav = this.getElement(this.AON_MENU_TOPNAV);
		return topnav.style.height == '68px';
	}

	showSideNav() {
		if (LS.isTopMenu())
			this.reloadTopNav();
	
		let sidenav = this.getElement(this.AON_MENU_SIDENAV);
		let menulist = this.getElement("aonMenuList");
		let rootPanel = this.getElement("rootPanel");
		let aonlogo = this.getElement("aonLogo");
		let apps = this.getElement("apps");
	
		sidenav.style.transition = 'width 0.3s ease';
		rootPanel.style.transition = 'margin-left 0.3s ease';
		// aonlogo.style.transition = 'left 0.3s ease';
	
		sidenav.style.width = '68px';
		sidenav.style.display = "";  
		sidenav.style.marginTop = "6px";
		apps.style.marginTop = "6px";
	
		menulist.style.visibility = "visible";
	
		// aonlogo.style.left = '60px';
		// aonlogo.style.position = 'relative';
	
		rootPanel.style.marginLeft = '68px';
	}
	

	hideSideNav() {
		if (LS.isTopMenu())
			this.reloadTopNav();
	
		let sidenav = this.getElement(this.AON_MENU_SIDENAV);
		let rootPanel = this.getElement("rootPanel");
		let menulist = this.getElement("aonMenuList");
		let aonlogo = this.getElement("aonLogo");
	
		sidenav.style.transition = 'width 0.3s ease';
		rootPanel.style.transition = 'margin-left 0.3s ease';
		// aonlogo.style.transition = 'left 0.3s ease';
	
		sidenav.style.width = '0px';
	
		// setTimeout(() => {
		// 	sidenav.style.display = "none";
		// }, 300); 
	
		// // Resetear la posición del logo
		// aonlogo.style.left = '0px';
		// aonlogo.style.position = 'relative';
	
		rootPanel.style.marginLeft = '0px';
	}
	

	showMenuButton() {
		let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		if (this.getAttribute('opened')) {
			aonMenuSidenav.style.width = '68px';
			this.getRootPanel().style.marginLeft = '68px';
		}
	}

	buildApp(app, style) {

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
		// div.classList.add("aonNewMenuAppDiv");
		div.style.padding = '1px';
		div.style.display = 'flex';
		div.style.alignItems = 'center';
		div.style.justifyContent = 'center';
		div.title = app.title;
		div.style.height = style?.height || '56px';
		div.style.flexDirection = style?.flexDirection || 'column';
		div.style.transition = 'background-color 0.2s';
		div.style.cursor = "pointer";
		if (!LS.isLeftMenu()) {
			div.style.marginTop = "0px";
		}
		div.title = app.title;
		let header = this.getElement("aonHeaderWeb");
		let welcome = this.getElement("aonCompanyTabFilter");

		
		if (app.cssIcon) {
			const appColor = app.newColor || app.color;
			let aonIcon = new AonIcon();
			aonIcon.id = `aonMenuListAppImgTop-${app.app}`;
			aonIcon.icon =  app.cssIcon;
			aonIcon.color = style?.color || appColor;
			if (app.app == "accounting"){
				aonIcon.size = "26px";
			}else {
				aonIcon.size = "32px";
			}
			div.appendChild(aonIcon);
		} else if (app.cssSymbol) {
			let icon = this.createElement(TAG.SPAN);
			icon.id = `aonMenuListAppImgTop-${app.app}`;
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.innerHTML = app.cssSymbol;
			if(app.newColor || app.color) {
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
			if (app.app == "accounting"){
				aonIcon.size = "26px";
			}else {
				aonIcon.size = "32px";
			}
			div.appendChild(aonIcon);
		} else if (app.symbol) {
			let icon = this.createElement(TAG.SPAN);
			icon.id = `aonMenuListAppImgTop-${app.app}`;
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.innerHTML = app.symbol;
			if(app.newColor || app.color) {
				icon.style.color = app.newColor || app.color;
			}
			icon.classList.add("aonNewMenuAppIcon");
			div.appendChild(icon);
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

		if (app.app == "applications"){
			this.controlSideNav();
		}
		

		return a;
	}

	controlSideNav() {
		const div = this.getElement("aonMenuLeftop");
		

		//if (this.isCSSLoaded("beta.css")) {

			div.addEventListener("mouseenter", () => {
				const side = this.getElement("aonMenuSidenav");

				if (LS.isCompanySelected()){
					this.showSideNav();
					this.getElement("topMenuHome").style.display = "none";
				}
			});

			document.addEventListener("click", (event) => {
				// Verificamos si el clic ocurrió fuera del sidenav
				const buttonNew = this.getElement("new");

				if (!buttonNew.contains(event.target) && !LS.isPortalChecked()) {
					// Si se clicó fuera del sidenav, lo ocultamos
					this.hideSideNav();
				}
			});
	
	}


	isElementAt(ev, el){
		const viewportX = ev.clientX;
		const viewportY = ev.clientY;
		let elements = document.elementsFromPoint(viewportX, viewportY);
		for ( let element of elements ){
			console.log(element.tagName + ": "  + (element === el));
			if ( element === el ){
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
	
	getCssVariable( variable ){
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

	buildNoteMenu(app) {
		this.CLOSE = false;
		let rootPanel = this.getRootPanel();
		let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		aonMenuSidenav.classList.add("aonNewMenuNoteMenuSideNav");
		if (rootPanel) rootPanel.style.marginLeft = '250px';
		aonMenuSidenav.innerHTML = '';

		let div = this.createElement(TAG.DIV);
		div.className = 'aonMenuSidenavAppToolbar';
		div.style.backgroundColor = this.getAppToolbarBackgroundColor(app);
		aonMenuSidenav.appendChild(div);

		let span = this.createElement(TAG.SPAN);
		span.className = 'aonTitle';
		span.innerHTML = app.title;

		let span2 = this.createElement(TAG.SPAN);
		span2.className = 'aonRight40';
		let aibS = new AonIconButton();
		aibS.id = "aon-menu-sidenav-app-launch-button";
		aibS.icon = "add";
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

		aibC.addEventListener(EVENT.CLICK, () => {
			aonMenuSidenav.style.width = '68px';
			if (rootPanel) rootPanel.style.marginLeft = '68px';
			this.buildMenu();
		});

		let div2 = this.createElement(TAG.DIV);
		aonMenuSidenav.appendChild(div2);

		let aonNote = new AonNote();
		div2.appendChild(aonNote);

		aibS.addEventListener(EVENT.CLICK, () => {
			aonNote.addNote();
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

			let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
			if (this.getAttribute('opened')) {
				aonMenuSidenav.style.width = '68px';
				this.getRootPanel().style.marginLeft = '68px';
			} else {
				aonMenuSidenav.style.width = '0px';
				this.getRootPanel().style.marginLeft = '0px';
			}
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

	close() {
		this.hideSideNav();
		this.hideTopNav();
	}

	open() {
		if(LS.isTopMenu()){
			this.showTopNav();
		}

		if(LS.isLeftMenu()){
			this.showSideNav();
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
		if (OFFICE.app === app.app)
			return this.getDur().isOffice();

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
			return this.getDur().isAccounting();
		if (FISCAL_MENU.app === app.app)
			return this.getDur().isFiscal();
		if (PAYROLL_MENU.app === app.app)
			return this.getDur().isPayroll();
		if (MARKETING_MENU.app === app.app)
			return this.getDur().isMarketing();
		if (CONFIGURATION_MENU.app === app.app)
			return this.getDur().isAdmin();
		
		if (MenuApps.ACCOUNTING.app === app.app)
			return this.getDur().isAccounting();
		else if (MenuApps.FISCAL.app === app.app)
			return this.getDur().isFiscal();
		else if (MenuApps.PAYROLL.app === app.app)
			return this.getDur().isPayroll();
		else if (MenuApps.COMUNICA.app === app.app) 
			return !this.getDur().isPayroll() && (this.getDur().isComunica() || this.getDur().isSaltra());			
		else if (MenuApps.DOCUMENTAL.app === app.app)
			return this.getDur().isDocumental();
		else if (MenuApps.TIMECONTROL.app === app.app)
			return this.getDur().isTimecontrol();
		else if (MenuApps.INVOICE.app === app.app)
			return this.getDur().isInvoice();
		else if (MenuApps.MESSENGER.app === app.app)
			return this.getDur().isMessenger();
		else if (AON_CLASSIC.app === app.app)
			return this.getDur().isAon();
		else if (NEW.app === app.app)
			return this.getDur().isAon()
				|| this.getDur().isInvoice()
				|| this.getDur().isMessenger()
				|| this.getDur().isDocumental();
		else if (HOME.app === app.app)
			return true;
		else if (APPS.app === app.app)
			return true;
		else if (APPLICATIONS.app === app.app)
			return true;
		else if (MenuApps.NOTES.app === app.app)
			return true;
		else if (MenuApps.TOOLS.app === app.app)
			return this.isBeta() && !LS.isNewTheme();
		else if (MenuApps.OFFICE.app === app.app) {
			return this.isBeta() && this.getDur().getDomain().isOffice() && !this.getDur().isEmployee();
		} else if (MenuApps.MARKETING.app === app.app) {
			return this.isBeta() && this.getDur().isMarketing();
		} else if (MenuApps.WAREHOUSE.app === app.app) {
			const domain = this.getDur().getDomain();
			return domain.getName() && (domain.getName().includes("udapa") || domain.getName().includes("paturpat") || this.isLocal());
		}
		else return false;
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
	
	showNewDialogMenu(el){
		let newDialogMenu =  this.getApplication().getOptionDialog();

		let newMenuOptions = [];
		if(this.getDur().isInvoice()){
			newMenuOptions.push({
				fn: () => {},
				icon: 'note_add',
				name: MSG.NEW_INVOICE,
				options : [
					{
						name: MSG.ISSUEDS,
						icon: MATERIAL_ICONS.UNARCHIVE,
						fn: () => this.newInvoice('emitida')
					}, {
						name: MSG.RECEIVEDS,
						icon: MATERIAL_ICONS.ARCHIVE,
						fn: () => this.newInvoice('recibida')					
					}, {
						name: MSG.TICKETS+"/"+MSG.SUPPORTING_DOCUMENTS,
						icon: MATERIAL_ICONS.RECEIPT,
						fn: () => this.newInvoice('ticket')
						
					}, {
						name: MSG.UPLOAD_INVOICE,
						icon: MATERIAL_ICONS.CLOUD_UPLOAD,
						fn: () => {
							let input = this.createElement(TAG.INPUT);
							input.type = CONSTANT.FILE;
							input.accept = this.accept;
							input.className = CSS.AON_NONE;
							input.multiple = 'multiple';
							
							input.addEventListener(EVENT.CHANGE, ({target}) => {
								let uploadToast = this.getElement('aonUploadToast');
								if(!uploadToast){ 
									uploadToast = new AonUploadToast();
									this.getApplication().getContent().appendChild(uploadToast);
								}
								let data = { uploaded : 0 };
								uploadToast.setJobId(generateJobId());
								for (let file of target.files) {
									uploadToast.addFile("invoice", file, data);
								}			
							});
							input.click();
							// this.appSelection(Apps.INVOICE);
						}
					}
				]
			});
		}
		if(this.getDur().isDocumental()){
			newMenuOptions.push({
				fn: () => {
					let input = this.createElement(TAG.INPUT);
					input.type = CONSTANT.FILE;
					input.accept = this.accept;
					input.className = CSS.AON_NONE;
					input.addEventListener(EVENT.CHANGE, ({target}) => uploadDocuments(input, target.files) );
					input.click();
					this.appSelection(Apps.DOCUMENTAL);
				},
				icon: MATERIAL_ICONS.CLOUD_UPLOAD,
				name: MSG.UPLOAD_DOCUMENT,
			});
		}
		if(this.getDur().isMessenger()){
			newMenuOptions.push({
				icon: 'add_comment',
				name: MSG.CREATE_QUERY,
				fn: () => {
					let aonMessengerChat = new AonMessenger();	
					aonMessengerChat.data = {source:TASK_SOURCE.QUERY};
					this.rootPanel(aonMessengerChat);
					this.appSelection(Apps.MESSENGER);
				}
			});
		}
		if(this.getDur().isAon()){
			newMenuOptions.push({
				icon: 'person_add',
				name: MSG.NEW_EMPLOYEE,
				fn: () => {
					let aonMessengerChat = new AonMessenger();	
					aonMessengerChat.data = {source:TASK_SOURCE.REQUEST};
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
					this.appSelection(Apps.MESSENGER);

				}
			});
		}

		
		const top  = el.getBoundingClientRect().top ;
		const left = el.getBoundingClientRect().right;
		
		newDialogMenu.setMenuOptions(newMenuOptions, top, left);
		newDialogMenu.open();
		

		
	}
	
	async isElementLoaded(selector){
		while ( document.querySelector(selector) === null) {
		  await new Promise( resolve =>  requestAnimationFrame(resolve) )
		}
		return document.querySelector(selector);
	};
	
	setAppClassName(app){
		let appsDiv = this.getElement("aonMenuLeftop-applications");
		appsDiv.style.removeProperty('background-color'); 
		let appName = app.app[0].toUpperCase() + app.app.slice(1);
		appsDiv.className = `${CSS.AON_MENU_LEFTOP} ${CSS.AON_MENU_LEFTOP}${appName}`		
	}	
	
	newInvoice(invoice) {
		let invoicePanel = new AonInvoicePanel();
		invoicePanel.option = OPTION.CREATE_INVOICE_ISSUED;
		invoicePanel.addEventListener(EVENT.BUILD, () => invoicePanel.aonInvoice(invoice) );
		this.rootPanel(invoicePanel);
		this.setAppClassName(Apps.INVOICE);
		this.setSelectedMenuSidenav(Apps.INVOICE);
		this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_SELECT, { detail : { app: Apps.INVOICE } }));		
	}
	
	setSelectedMenuSidenav(app) {
		const aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		const aonMenuSidenavLis = aonMenuSidenav.getElementsByTagName(TAG.LI);
		for ( const aonMenuSidenavLi of aonMenuSidenavLis  ) {
			if ( aonMenuSidenavLi.id === `aonMenuList-${app?.app}` ) {
				aonMenuSidenavLi.classList.add("aonMenuSidenavLiSeleted");
			} else {
				aonMenuSidenavLi.classList.remove("aonMenuSidenavLiSeleted");
			}
		}
	}
}
if (!window.customElements.get(TAG.AON_NEW_MENU)) {
	window.customElements.define(TAG.AON_NEW_MENU, AonNewMenu);
}