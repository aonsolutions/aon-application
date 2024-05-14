import { AonElement } from 'aonsolutions/components/AonElement.js';
import { Apps, HomeApps, MenuApps, AuxApps, MENU_APPS, TOP_MENU_APPS, AON_APPS, HOME } from '../services/app.js';
import {COMMERCE, OFFICE, GARAGE, ACADEMY} from  "aonsolutions/services/app.js";
import { getDomainUserRoles } from 'aonsolutions/services/service.js';
import { DomainUserRoles } from 'aonsolutions/models/DomainUserRoles.js';
import { MSG, CONSTANT, AON_ICONS, CSS, EVENT, MATERIAL_ICONS, TAG } from 'aonsolutions/environments/environments.js';
import { AonDocumental } from 'aonsolutions/modules/documental/aon-documental.js';
import { AonDocumentalAyudat } from 'aonsolutions/modules/documental/ayudat/aon-documental-ayudat.js';
import 'aonsolutions/modules/project/aon-project-panel.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';
import * as LS from 'aonsolutions/services/localStorageService.js';
import { AonMessenger } from 'aonsolutions/modules/messenger/aon-messenger.js';
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import { AonFiscal } from 'aonsolutions/modules/fiscal/aon-fiscal.js';
import { AonTimecontrol } from 'aonsolutions/modules/timecontrol/aon-timecontrol.js';
import { AonLaboral } from 'aonsolutions/modules/laboral/aon-laboral.js';
import { AonComunica } from 'aonsolutions/modules/laboral/aon-comunica.js';
import { AonAccounting } from 'aonsolutions/modules/accounting/aon-accounting.js';
import { AonSaltra } from 'aonsolutions/modules/laboral/aon-saltra.js';
import { AonIcon } from 'aonsolutions/components/aon-icon.js';
import { AonNote } from 'aonsolutions/modules/note/aon-note.js';
import { AonInvoicePanel } from 'aonsolutions/modules/invoice/aon-invoice-panel.js';
import { AonBooking } from 'aonsolutions/modules/marketplace/aon-booking.js';
import { AonOfficePanel } from 'aonsolutions/modules/office/aon-office-panel.js';
import { AonConsole } from 'aonsolutions/modules/console/aon-console.js';
import { AonAppMenu } from 'aonsolutions/modules/aon-app-menu.js';
import { AonNotes } from 'aonsolutions/modules/note/aon-notes.js';
import { AonWarehouse } from 'aonsolutions/modules/warehouse/aon-warehouse.js';
//import { AonMarketing } from 'aonsolutions/modules/marketing/aon-marketing.js';

import { AonParent } from './aon-parent.js';
import { AonNewDesktop } from './aon-new-desktop.js';

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
		this.buildDur().then(()=> {
			this.build();
		})

		//if(localStorage.getItem('aon_domain_id') && localStorage.getItem('company')){
		//	getDomainUserRoles({}).then(r => {
		//		this.dur = new DomainUserRoles(r);
		//		this.build();
		//	});
		//}
	}

	appSelection(app) {
		switch (app.app) {
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
				this.rootPanel(new AonParent());
				break;
			default/*Apps.HOME*/ :
				this.rootPanel(new AonNewDesktop(MENU_APPS, AON_APPS));
				break;
		}
		
		this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_SELECT, { detail: app } ));
	}

	build() {
		let aonMenuLefttop = this.createElement(TAG.DIV);
		aonMenuLefttop.id = this.AON_MENU_LEFTOP;
		aonMenuLefttop.className = CSS.AON_MENU_LEFTOP;
		this.appendChild(aonMenuLefttop);
		aonMenuLefttop.style.width = '68px';
		aonMenuLefttop.style.visibility = "hidden";
		this.buildMenuLeftop();

		let aonMenuSidenav = this.createElement(TAG.DIV);
		aonMenuSidenav.id = this.AON_MENU_SIDENAV;
		aonMenuSidenav.className = CSS.AON_MENU_SIDENAV;
		this.appendChild(aonMenuSidenav);
		aonMenuSidenav.style.width = '0px';
		this.getRootPanel().style.marginLeft = '0px';
		this.buildMenuSidenav();

		let aonMenuTopnav = this.createElement(TAG.DIV);
		aonMenuTopnav.id = this.AON_MENU_TOPNAV;
		aonMenuTopnav.className = CSS.AON_MENU_TOPNAV;
		this.appendChild(aonMenuTopnav);
		aonMenuTopnav.style.height = '0px';
		this.getRootPanel().style.marginTop = '1px'; //'69px';
		this.buildMenuTopnav();

		if(LS.isTopMenu()){
			this.showTopNav();
		}

		if(LS.isLeftMenu()){
			this.showSideNav();
		} 
	}

	buildMenuLeftop() {
		let aonMenuLeftop = this.getElement(this.AON_MENU_LEFTOP);

		let div = this.createElement(TAG.DIV);
		div.style.display = 'flex';
		div.style.flexDirection = 'row';
		div.style.alignItems = 'center';
		div.style.justifyContent = 'center';
		
		let app = HomeApps.APPLICATIONS;
		let appDiv = this.createElement(TAG.DIV);
		appDiv.id = `aonMenuLeftop-${app.app}`;
		appDiv.style.width = '68px';
		appDiv.style.backgroundColor = 'transparent';
		appDiv.appendChild(this.buildApp(app, {height:'48px'}));
		div.appendChild(appDiv);

		aonMenuLeftop.appendChild(div);
	}

	buildMenuSidenav() {
		let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		let ul = this.createElement(TAG.UL);
		ul.classList.add(CSS.AON_UL);
		ul.id = 'aonMenuList';
		ul.style.margin = '0px';
		ul.style.padding = '0px';
		ul.style.marginTop = '8px';
		ul.style.listStyle = 'none';
		
		for (let item in MENU_APPS) {
			if (this.isApp(MENU_APPS[item])) {
				let app = MENU_APPS[item];
				let li = this.createElement(TAG.LI);
				li.id = `aonMenuList-${app.app}`;
				li.style.backgroundColor = 'transparent';
				li.style.cursor = 'pointer';
				li.appendChild(this.buildApp(app,undefined,true));

				ul.appendChild(li);
			}
		}

		let li = this.createElement(TAG.LI);
		li.style.height = '10px'
		ul.appendChild(li);

		aonMenuSidenav.innerHTML = '';
		aonMenuSidenav.appendChild(ul);

	}

	buildMenuTopnav() {
		let aonMenuTopnav = this.getElement(this.AON_MENU_TOPNAV);
		let div = this.createElement(TAG.DIV);
		div.style.display = 'flex';
		div.style.flexDirection = 'row';
		div.style.alignItems = 'center';
		div.style.justifyContent = 'center';
		div.style.height = '100%';
		
		if(!LS.isLeftMenu()) {
			div.appendChild(this.buildTopApp(HOME));
		}

		for (let item in TOP_MENU_APPS) {
			if (this.isApp(TOP_MENU_APPS[item])) {
					let app = TOP_MENU_APPS[item];
					div.appendChild(this.buildTopApp(app));
			}
			
		}

		aonMenuTopnav.appendChild(div);
	}

	reloadTopNav(){
		let aonMenuTopnav = this.getElement(this.AON_MENU_TOPNAV);
		this.clearElement(aonMenuTopnav);
		this.buildMenuTopnav();
	}

	buildTopApp(app) {
		let appDiv = this.createElement(TAG.DIV);
		appDiv.id = `aonMenuBar-${app.app}`;
		appDiv.style.width = '68px';
		appDiv.style.cursor = 'pointer';
		appDiv.style.backgroundColor = 'transparent';
		appDiv.appendChild(this.buildApp(app,undefined,false));
	
		appDiv.style.transition = 'background-color 0.2s';
		appDiv.style.backgroundColor = 'transparent';
		appDiv.addEventListener('mouseover', () => {
			appDiv.style.backgroundColor = 'white';
		});
		appDiv.addEventListener('mouseout', () => {
			appDiv.style.backgroundColor = 'transparent';
		});
	
		return appDiv;
	}
/*
	hideTopNav() {
		let topnav = this.getElement(this.AON_MENU_TOPNAV);
		topnav.style.height 
	}

	hideSideNav() {

	}
*/
	showTopNav() {	
		let topnav = this.getElement(this.AON_MENU_TOPNAV);
		let rootPanel = this.getElement("rootPanel");
		let rightPanel = this.getElement("aonRightPanel");

		topnav.style.height = '68px';
		rootPanel.style.marginTop = `${rootPanel.style.marginTop + 69}px`;
		if(rightPanel){
			rightPanel.style.marginTop = topnav.offsetHeight;
			rightPanel.style.height = `calc(100vh - 62px)`;
		
		}
		
		rootPanel.style.marginTop = "69px";
		rootPanel.style.height = `calc(100vh - 62px)`;
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

	showSideNav() {
		if(LS.isTopMenu())
			this.reloadTopNav();

		let sidenav = this.getElement(this.AON_MENU_SIDENAV);
		let menulist = this.getElement("aonMenuList");
		let rootPanel = this.getElement("rootPanel");
		let aonlogo = this.getElement("aonLogo");		
		let icon = this.getElement("aonMenuLeftop");

		sidenav.style.width = '68px';
		sidenav.style.display = "";
		
		menulist.style.visibility = "visible";
		
		aonlogo.style.left = '50px';
		aonlogo.style.position = 'relative';

		icon.style.visibility = "visible";

		rootPanel.style.marginLeft = '69px';
	}

	hideSideNav(){
		if(LS.isTopMenu())
			this.reloadTopNav();

		let sidenav = this.getElement(this.AON_MENU_SIDENAV);
		let rootPanel = this.getElement("rootPanel");
		let menulist = this.getElement("aonMenuList");
		let aonlogo = this.getElement("aonLogo");
		let icon = this.getElement("aonMenuLeftop");
		sidenav.style.width = '0px';
		sidenav.style.display = "none";
		aonlogo.style.position = "relative";
		//icon.style.visibility = "hidden";
		aonlogo.style.left = '50px';
		rootPanel.style.marginLeft = '0px';
		//menulist.style.visibility = "hidden";
	}

	showMenuButton() {
		let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		if (this.getAttribute('opened')) {
			aonMenuSidenav.style.width = '68px';
			this.getRootPanel().style.marginLeft = '68px';
		}
	}

	buildApp(app, style,sidenav) {

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

		a.addEventListener(EVENT.MOUSEOVER, () => {
			if (hoverDiv.style.display == 'none' && !this.isTopNav(a) && !hoverDiv.classList.contains('aonMenuAppHoverHidden')) {
				hoverDiv.style.position = 'fixed';
				let position = a.getBoundingClientRect();
				hoverDiv.style.top = position.top + (position.height / 2);
				hoverDiv.style.left = position.left + position.width + 10;
				hoverDiv.style.display = 'block';
				let hoverDivPosition = hoverDiv.getBoundingClientRect();
				hoverDiv.style.top = position.top + (position.height / 2) - (hoverDivPosition.height / 2);
			}
		});

		a.addEventListener(EVENT.MOUSELEAVE, () => {
			hoverDiv.style.display = 'none';
		});

		a.appendChild(hoverDiv);

		let div = this.createElement(TAG.DIV);
		div.style.padding = '1px';
		div.style.display = 'flex';
		div.style.alignItems = 'center';
		div.style.justifyContent = 'center';
		div.style.height = style?.height || '56px';
		div.style.flexDirection = style?.flexDirection || 'column';
		div.style.transition = 'background-color 0.2s';
		div.style.backgroundColor = 'transparent';
		div.addEventListener('mouseover', () => {
			div.style.backgroundColor = 'white';
		});
		div.addEventListener('mouseout', () => {
			div.style.backgroundColor = 'transparent';
		});

		if ((!sidenav && app.symbol) || (sidenav && !app.icon && app.symbol)) {
			let icon = this.createElement(TAG.SPAN);
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.id = `aonMenuListAppImg-${app.app}`;
			icon.innerHTML = app.symbol;
			if(app.color) icon.style.color = app.color;
			icon.style.padding = "4px";
			icon.style.fontSize = "24px";
			div.appendChild(icon);
		} else if (app.icon) {
			let aonIcon = new AonIcon();
			aonIcon.id = `aonMenuListAppImg-${app.app}`;
			aonIcon.icon = app.icon;
			aonIcon.color = style?.color || app.color;
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
				span.style.minHeight = '26px';
				span.innerHTML = app.title; // titles.length > i ? titles[i] : '&nbsp;';
				div.appendChild(span);
			// }
		}

		a.appendChild(div);

		return a;
	}



	buildAppMenu(app) {
		let company;
		if (this.getAttribute('company')) {
			company = JSON.parse(this.getAttribute('company'));
		}

		let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		aonMenuSidenav.style.width = '250';

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
		aonMenuSidenav.style.width = '250px';
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
		li.addEventListener(EVENT.MOUSEOVER, () => {
			li.style.backgroundColor = '#f1f1f1';
		});
		li.addEventListener(EVENT.MOUSELEAVE, () => {
			li.style.backgroundColor = 'transparent';
		});

		let a = this.createElement(TAG.A);
		a.className = CSS.AON_MENU_LINK;

		let span = this.createElement(TAG.SPAN);
		span.style.fontSize = "12px";
		span.style.fontWeight = "400";
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
		return app.color ? app.color : '#f1f1f1';
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
		let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		aonMenuSidenav.style.transitionDuration = '0ms';
		aonMenuSidenav.style.width = '0px';
		this.getRootPanel().style.marginLeft = '0px';
		this.removeAttribute('opened');
		this.toolbarClose();
	}

	toolbarClose() {
		let application = this.getApplication();
		if (application) {
			let toolbar = application.getToolbar();
			if (toolbar) {
				let toolSection = toolbar.getToolSection();
				if (toolSection)
					toolSection.style.paddingRight = this.getAttribute('opened') ? '0px' : '40px';
			}
		}
	}

	isExpanded() {
		return this.expanded;
	}

	setExpanded(expanded) {
		this.expanded = expanded;
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
		return true;

		if (MenuApps.ACCOUNTING.app === app.app)
			return this.getDur().isAccounting();
		else if (MenuApps.FISCAL.app === app.app)
			return this.getDur().isFiscal();
		else if (MenuApps.PAYROLL.app === app.app)
			return this.getDur().isPayroll();
		else if (MenuApps.COMUNICA.app === app.app)
			return !this.getDur().isPayroll() && this.getDur().isComunica();
		else if (MenuApps.AON_SALTRA.app === app.app)
			return !this.getDur().isPayroll() && !this.getDur().isComunica() && this.getDur().isSaltra();
		else if (MenuApps.DOCUMENTAL.app === app.app)
			return this.getDur().isDocumental();
		else if (MenuApps.TIMECONTROL.app === app.app)
			return this.getDur().isTimecontrol();
		else if (MenuApps.INVOICE.app === app.app)
			return this.getDur().isInvoice();
		else if (MenuApps.MESSENGER.app === app.app)
			return this.getDur().isMessenger();
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
			div.style.position = 'absolute';
			div.style.backgroundColor = app.backgroundColor || '#f1f1f1';
			div.style.height = '500px';
			div.style.width = '250px';
			div.style.zIndex = '999';
			div.style.left = '60px';
			div.style.top = '20px';
			div.style.borderRadius = '5px';
			div.style.display = 'block';

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
		} while (element.id != this.AON_MENU_SIDENAV && element.id != this.AON_MENU_TOPNAV);
		return element.id == this.AON_MENU_TOPNAV;
	}	

}
if (!window.customElements.get(TAG.AON_NEW_MENU)) {
	window.customElements.define(TAG.AON_NEW_MENU, AonNewMenu);
}
