import { AonElement } from '../components/AonElement.js';
import { Apps, AuxApps, MenuApps, AccountingMenu, PayrollMenu, AeatFiscalMenu, ToolsMenu, AccountingPortalMenu} from  '../services/app.js';
import { getDomainUserRoles } from  '../services/service.js';
import { DomainUserRoles } from '../models/DomainUserRoles.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS,  TAG } from '../environments/environments.js';
import { AonDocumental } from './documental/aon-documental.js';
import { AonDocumentalAyudat } from './documental/ayudat/aon-documental-ayudat.js';
import './project/aon-project-panel.js';
import * as GWT from "../gwt/gwt.js";
import * as LS from "../services/localStorageService.js";
import { AonMessenger } from './messenger/aon-messenger.js';
import { AonIconButton } from '../components/aon-icon-button.js';
import { AonFiscal } from './fiscal/aon-fiscal.js';
import { AonTimecontrol } from './timecontrol/aon-timecontrol.js';
import { AonLaboral } from './laboral/aon-laboral.js';
import { AonComunica } from './laboral/aon-comunica.js';
import { AonSaltra } from './laboral/aon-saltra.js';
import { AonIcon } from '../components/aon-icon.js';
import { AonNote } from './note/aon-note.js';
import { AonInvoicePanel } from './invoice/aon-invoice-panel.js';
import { AonBooking } from './marketplace/aon-booking.js';
import { AonOfficePanel } from './office/aon-office-panel.js';
import { AonConsole } from './console/aon-console.js';

const ID = 'id';
const OPENED = 'opened';
const APP = 'app';


export class AonNewMenu extends AonElement {

	dur;
	AON_MENU_SIDENAV;
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

	connectedCallback () {
		this.initialize();
		this.init();
    }

	initialize() {
		this.dur = [];
		this.AON_MENU_SIDENAV = 'aonMenuSidenav';
		this.CLOSE = true;
	}

	init() {
		this.setAttribute('opened', true);
		if(localStorage.getItem('aon_domain_id') && localStorage.getItem('company')){
			getDomainUserRoles({}).then(r => {
				this.dur = new DomainUserRoles(r);
				this.build();
			});
		}
	}

	getDur() {
		return this.dur;
	}

	toogle() {
		const aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		
		let open = this.getAttribute('opened');

		if(open) {
			this.getElement('aonShowMenuButton').style.display = 'block';
			this.close();
		} else if(this.getAttribute('app')){
			aonMenuSidenav.style.width = '250px';
			this.setAttribute('opened', true);
			this.getElement('aonShowMenuButton').style.display = 'none';
		} else {
			aonMenuSidenav.style.width = '60px';
			this.getRootPanel().style.marginLeft = '60px';
			this.setAttribute('opened', true);
			this.getElement('aonShowMenuButton').style.display = 'none';
		}
		this.toolbarClose();
	}

	appSelection(app) {
		switch(app) {
			case Apps.CONSOLE.app:
				this.rootPanel(new AonConsole());
				break;
			case Apps.DOCUMENTAL.app:
				this.rootPanel(this.getDur().isBidoq() ? new AonDocumentalAyudat() : new AonDocumental());
				break;
			case Apps.ACCOUNTING.app:
				this.buildAppMenu(Apps.ACCOUNTING);
				break;
			case Apps.FISCAL.app:
				if(this.getDur().isFiscalManager()) {
					this.buildAppMenu(Apps.FISCAL);
				} else this.rootPanel(new AonFiscal());
				break;
			case Apps.PAYROLL.app:
				if(this.getDur().isPayrollManager()) {
					this.buildAppMenu(Apps.PAYROLL);
				} else this.rootPanel(new AonLaboral());
				break;
			case Apps.COMUNICA.app:
				this.rootPanel(new AonComunica());
				break;
			case MenuApps.AON_SALTRA.app:
				this.rootPanel(new AonSaltra());
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
				this.buildNoteMenu(Apps.NOTES);
			break;
			case Apps.OFFICE.app:
				this.rootPanel(new AonOfficePanel());
			break;
		}
	}

	build() {
		let aonMenuSidenav = this.createElement(TAG.DIV);
		aonMenuSidenav.id = "aonMenuSidenav";
		aonMenuSidenav.className = CSS.AON_MENU_SIDENAV;
		this.appendChild(aonMenuSidenav);

		aonMenuSidenav.addEventListener(EVENT.MOUSELEAVE, () => {
			if(this.CLOSE){
				aonMenuSidenav.style.transitionDuration = '500ms';
				if(this.getAttribute('opened')) {
					aonMenuSidenav.style.width = '60px';
					this.getRootPanel().style.marginLeft = '60px';
				} else {
					aonMenuSidenav.style.width = '0px';
					this.getRootPanel().style.marginLeft = '0px';
				}
				document.querySelectorAll("[id^='aonMenuListApp-']").forEach((item, i) => {
					item.style.display = 'none';
				});
				this.buildMenu();
			}
		});

		if(this.getAttribute('opened')) {
			aonMenuSidenav.style.width = '60px';
			this.getRootPanel().style.marginLeft = '60px';
		} else {
			aonMenuSidenav.style.width = '0px';
			this.getRootPanel().style.marginLeft = '0px';
		}
		this.buildMenu();
	}
	
	buildMenu() {
		let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		let ul = this.createElement(TAG.UL);
		ul.classList.add(CSS.AON_UL);
		ul.id = 'aonMenuList';
		ul.style.margin = '0px';
		ul.style.padding = '0px';
		ul.style.listStyle = 'none';

		let li = this.createElement(TAG.LI);
		li.style.textAlign =  'right';
		li.style.paddingRight = '12px';
		li.style.backgroundColor = 'transparent';
		li.style.cursor = 'pointer';
		ul.appendChild(li);
		const icon = MATERIAL_ICONS.KEYBOARD_ARROW_RIGHT;
		let aib   = new AonIconButton();
		aib.id    = "aonMenuShowButton";
		aib.title = `Ocultar Menu`;
		aib.icon = icon;
		li.addEventListener(EVENT.MOUSEMOVE, (ev)=> {
			ev.preventDefault();
			aonMenuSidenav.style.transitionDuration = '300ms';
			aonMenuSidenav.style.width = '60px';
			this.getRootPanel().style.marginLeft = '60px';
		});
		li.appendChild(aib);

		if(localStorage.getItem('aon_domain_id') && localStorage.getItem('company')){
			// let company = JSON.parse(localStorage.getItem('company'));
			for (let item in MenuApps){
				if(this.isApp(MenuApps[item])){
					ul.appendChild(this.buildApp(MenuApps[item]));
				}
			}

			if(this.getDur().isAdmin()) {
				let liAdd = this.createElement(TAG.LI);
				liAdd.style.textAlign =  'right';
				liAdd.style.paddingRight = '10px';
				liAdd.style.backgroundColor = 'transparent';
				ul.appendChild(liAdd);
				let aibm = new AonIconButton();
				aibm.id   = "aonMenuAddButton";
				aibm.icon = "add";
				liAdd.appendChild(aibm);
			}
			aonMenuSidenav.innerHTML = '';
			aonMenuSidenav.appendChild(ul);

			aib.addEventListener(EVENT.CLICK, () => this.close());

			if(this.getDur().isAdmin()) {
				let aonMenuAddButton = this.getElement('aonMenuAddButton');
				aonMenuAddButton.addEventListener(EVENT.CLICK, () => {
					let aonMarketPlace = new AonBooking()
					aonMarketPlace.id = "aonMarketplace";
					this.rootPanel(aonMarketPlace);
				});
			}
		}
	}

	showMenuButton(){
		let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		if(this.getAttribute('opened')) {
			aonMenuSidenav.style.width = '60px';
			this.getRootPanel().style.marginLeft = '60px';
		} 
	}

	buildApp(app) {
		let li = this.createElement(TAG.LI);
		if (app) {
			li.id = 'aonMenuList' + app.app;
	    li.style.backgroundColor = 'transparent';

	    let a = this.createElement(TAG.A);
	    a.style.cursor = 'pointer';
	    a.style.textAlign = 'right';
	    a.addEventListener(EVENT.CLICK, () => {
	      this.appSelection(app.app);
	    });

	    let div = this.createElement(TAG.DIV);
	    div.style.margin = '8px 0px';
		div.style.marginRight = '15px';
		div.style.borderTopRightRadius = '50px 50px';
		div.style.borderBottomRightRadius = '50px 50px';
		li.addEventListener(EVENT.MOUSEOVER, () => {
			let img = this.getElement('aonMenuListAppImg-' + app.app);
			img.size = '40px';
			div.style.backgroundColor = '#f1f1f1';
			let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
			if(aonMenuSidenav && aonMenuSidenav.style.width !== '250px' && aonMenuSidenav.style.width !== '0px' && localStorage.getItem('aon_domain_id')){
				aonMenuSidenav.style.transitionDuration = '0ms';
				aonMenuSidenav.style.width = '190px';
				document.querySelectorAll("[id^='aonMenuListApp-']").forEach((item, i) => {
					item.style.display = 'inline-block';
					item.style.fontSize = '12px';
					item.style.fontFamily = 'Roboto,sans-serif';
					item.style.color = 'black';
					item.style.right = '50px';
					item.style.margin = '10px';
				});
			}
		});

	    li.addEventListener(EVENT.MOUSELEAVE, () => {
	      let img = this.getElement('aonMenuListAppImg-' + app.app);
	      img.size = '30px';
		  div.style.backgroundColor = 'transparent';
	    });

	    if (app.icon) {
			const span = this.createElement(TAG.SPAN);
			span.id = `aonMenuListApp-${app.app}`;
			span.style.display = "none";
			span.innerHTML = app.title;
			div.appendChild(span);

			const aonIcon = new AonIcon();
			aonIcon.id    = `aonMenuListAppImg-${app.app}`;
			aonIcon.icon  = app.icon;
			aonIcon.color = app.color;
			aonIcon.size  = "30px";
			div.appendChild(aonIcon);
	    } else {
	      let span = this.createElement(TAG.SPAN);
	      span.id = 'aonMenuListApp-' + app.app;
	      span.style.display = 'none';
	      span.style.marginRight = '5px';
	      span.innerHTML = app.title;
	      div.appendChild(span);

	      let img = this.createElement(TAG.IMG);
	      img.id = 'aonMenuListAppImg-' + app.app;
	      img.style.width = '30px';
	      img.src = app.logo;
	      img.title = app.title;
	      div.appendChild(img);
	    }

	    a.appendChild(div);
	    li.appendChild(a);
	  }
		return li;
	}

	buildAppMenu(app) {
		let company;
		if(this.getAttribute('company')){
			company = JSON.parse(this.getAttribute('company'));
		}

		let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
		aonMenuSidenav.style.width = '250';

		let div = this.createElement(TAG.DIV);
	 	div.className = 'aonMenuSidenavAppToolbar';
		div.style.backgroundColor = this.getAppToolbarBackgroundColor(app);

		let span = this.createElement(TAG.SPAN);
		span.className= 'aonTitle';
		span.innerHTML = app.title;

		let span2 = this.createElement(TAG.SPAN);
		span2.className = 'aonRight40';
		let aibS = new AonIconButton();
		aibS.id   = "aon-menu-sidenav-app-launch-button";
		aibS.icon ="launch";
		aibS.color = "white";
		aibS.noHover = "true";
		span2.appendChild(aibS);

		let span3 = this.createElement(TAG.SPAN);
		span3.className = 'aonRight0';
		let aibC = new AonIconButton();
		aibC.id   = "aon-menu-sidenav-app-close-button";
		aibC.icon ="close";
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
			if(!subapp.parent || (subapp.parent && company && company.parent)){
				ul.appendChild(this.buildSubAppMenu(subapp));
			}
		});

		div2.appendChild(ul);

		aonMenuSidenav.innerHTML = '';
		aonMenuSidenav.appendChild(div);
		aonMenuSidenav.appendChild(div2);

		let launchButton = this.getElement('aon-menu-sidenav-app-launch-button');
		launchButton.addEventListener(EVENT.CLICK, () => {
			if(Apps.ACCOUNTING.app === app.app) {
				this.rootPanelHtml('<aon-accounting></aon-accounting>');
			} else if(Apps.FISCAL.app === app.app) {
				this.rootPanel(new AonFiscal());
			} else if(Apps.PAYROLL.app === app.app) {
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
		if(rootPanel) rootPanel.style.marginLeft = '250px';
		aonMenuSidenav.innerHTML = '';

		let div = this.createElement(TAG.DIV);
	 	div.className = 'aonMenuSidenavAppToolbar';
		div.style.backgroundColor = this.getAppToolbarBackgroundColor(app);
		aonMenuSidenav.appendChild(div);

		let span = this.createElement(TAG.SPAN);
		span.className= 'aonTitle';
		span.innerHTML = app.title;

		let span2 = this.createElement(TAG.SPAN);
		span2.className = 'aonRight40';
		let aibS = new AonIconButton();
		aibS.id   = "aon-menu-sidenav-app-launch-button";
		aibS.icon ="add";
		aibS.color = "white";
		aibS.noHover = "true";
		span2.appendChild(aibS);

		let span3 = this.createElement(TAG.SPAN);
		span3.className = 'aonRight0';
		let aibC = new AonIconButton();
		aibC.id   = "aon-menu-sidenav-app-close-button";
		aibC.icon ="close";
		aibC.color = "white";
		aibC.noHover = "true";
		span3.appendChild(aibC);
		div.appendChild(span);
		div.appendChild(span2);
		div.appendChild(span3);

		aibC.addEventListener(EVENT.CLICK,()=>{
			this.CLOSE = true;
			aonMenuSidenav.style.width = '175px';
			this.buildMenu();
		});

		let div2 = this.createElement(TAG.DIV);
		aonMenuSidenav.appendChild(div2);
		
		let aonNote = new AonNote();
		div2.appendChild(aonNote);

		aibS.addEventListener(EVENT.CLICK,()=>{
			aonNote.addNote();
		});
	}

	development(title) {
		let aonApplication = this.getApplication();
		aonApplication.development(title);
	}

	getSubApps(app){
		switch(app){
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
			if(subapp.content) {
				this.rootPanelHtml(subapp.content);
			} else if(subapp.initAction) {
				open('https://' + localStorage.getItem('aon_domain_name') + '/login?initAction='+subapp.initAction+'&token=' + localStorage.getItem('aon_session_id'));
			} else GWT.startModule(subapp.module, subapp.entryPoint);

			let aonMenuSidenav = this.getElement(this.AON_MENU_SIDENAV);
			if(this.getAttribute('opened')) {
				aonMenuSidenav.style.width = '60px';
				this.getRootPanel().style.marginLeft = '60px';
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
		if(this.getAttribute('company')){
			company = JSON.parse(this.getAttribute('company'));
		}
		if(Apps.FISCAL.app === app.app) {
			if(company && company.administration && 'ALAVA' === company.administration){
				return '#a30c51';
			} else if(company && company.administration && 'BIZKAIA' === company.administration){
				return '#d70004';
			} else if(company && company.administration && 'GIPUZKOA' === company.administration){
				return '#a1c031';
			} else if(company && company.administration && 'NAVARRA' === company.administration){
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
		this.getElement('aonShowMenuButton').style.display = 'block';
		this.getRootPanel().style.marginLeft = '0px';
		this.removeAttribute('opened');
		this.toolbarClose();
	}

	toolbarClose(){
		let application = this.getApplication();
		if(application){
			let toolbar = application.getToolbar();
			if(toolbar){
				let toolSection = toolbar.getToolSection();
				if(toolSection) 
					toolSection.style.paddingRight = this.getAttribute('opened') ? '0px' : '40px';
			}
		}
	}

	isApp(app) {
		if(MenuApps.ACCOUNTING.app === app.app)
			return this.getDur().isAccounting();
		else if(MenuApps.FISCAL.app === app.app)
			return this.getDur().isFiscal();
		else if(MenuApps.PAYROLL.app === app.app)
			return this.getDur().isPayroll();
		else if(MenuApps.COMUNICA.app === app.app) 
			return !this.getDur().isPayroll() && this.getDur().isComunica();
		else if(MenuApps.AON_SALTRA.app === app.app) 
			return !this.getDur().isPayroll() && !this.getDur().isComunica() && this.getDur().isSaltra();
		else if(MenuApps.DOCUMENTAL.app === app.app)
			return this.getDur().isDocumental();
		else if(MenuApps.TIMECONTROL.app === app.app)
			return this.getDur().isTimecontrol();
		else if(MenuApps.INVOICE.app === app.app)
			return this.getDur().isInvoice();
		else if(MenuApps.MESSENGER.app === app.app)
			return this.getDur().isMessenger();
		else if(MenuApps.NOTES.app === app.app)
			return true;
		else if(MenuApps.TOOLS.app === app.app)
			return this.isBeta();
		else if(MenuApps.OFFICE.app === app.app){
			return this.isBeta() && this.getDur().getDomain().isOffice() && !this.getDur().isEmployee();
		} else if(MenuApps.CONSOLE.app === app.app){
			return this.isBeta();
		}
		else return false;
	}

}
if(!window.customElements.get(TAG.AON_NEW_MENU)){
	window.customElements.define(TAG.AON_NEW_MENU, AonNewMenu);
}
