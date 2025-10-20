import {AonElement} from '../components/AonElement.js';

import { MSG, CONSTANT, AON_ICONS, CSS, EVENT, MATERIAL_ICONS, TAG } from '../environments/environments.js';
import { AonIcon } from '../components/aon-icon.js';
import { AonApplication } from '../components/aon-application.js';
import * as LS from '../services/localStorageService.js';
import { getContratado, getDomainUserRoles, sendFormData } from '../services/companyService.js';
import { getAuth } from '../services/authService.js';
import { DomainUserRoles } from '../models/DomainUserRoles.js';
import * as GWT from '../gwt/gwt.js';

export class AonNewDesktop extends AonElement {
	
	AON_DESKTOP;
	
	portalApps;
	portalNoApps;
	suiteApps;
	suiteNoApps;
	dur;
	
	constructor(portalApps, portalNoApps, suiteApps, suiteNoApps) {
		super();
		this.portalApps = portalApps;
		this.portalNoApps = portalNoApps;
		this.suiteApps = suiteApps;
		this.suiteNoApps = suiteNoApps;
	}
	
	connectedCallback () {
		this.init();
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
	}
	
	init() {
		this.AON_DESKTOP = 'aonDesktop';
	}

	async build() {
      	let app = this.createApplication(this.AON_DESKTOP, MSG.APPLICATIONS, new AonApplication());
		app.main = "true";
		
		app.removeToolbar();
		app.removeSidenav();
	
		let div = this.createDiv();	
		div.style.padding = "2rem";
		
		if(this.getDur().isTrial() || this.getDur().hasBeenTrial())
			div.appendChild(this.buildPlanApps());
			
		div.appendChild(this.buildPortalApps());
		
		if(this.suiteApps.length != 0)
			div.appendChild(this.buildSuiteApps());
		
		app.setContent(div);
	}

	getDur() {
		return this.dur;
	}
	
	buildPortalApps(){
		let div = this.createDiv();

		let bannerAppsDiv = this.createElement(TAG.DIV);
		let titleH1 = this.createElement(TAG.H1);
		titleH1.innerHTML = MSG.APPLICATIONS +  " Portal";
		titleH1.classList.add("aonNewDesktopTitleH1");
		bannerAppsDiv.appendChild(titleH1);
		div.appendChild(bannerAppsDiv);

		let desktopAppsDiv =  this.createElement(TAG.DIV);
		desktopAppsDiv.style.display = "flex";
		desktopAppsDiv.style.flexWrap = "wrap";
		
		for ( const app in this.portalApps ) {
			desktopAppsDiv.appendChild(this.buildApp(this.portalApps[app]));
		}
		
		//for ( const app in this.portalNoApps ) {
		//	desktopAppsDiv.appendChild(this.buildApp(this.portalNoApps[app], false));
		//}
		
		div.appendChild(desktopAppsDiv);
		return div;
	}

	buildSuiteApps(){
		let div = this.createDiv();	

		let bannerAppsDiv = this.createElement(TAG.DIV);
		let titleActivas = this.createElement(TAG.H1);
		titleActivas.innerHTML = MSG.APPLICATIONS + " Suite";
		titleActivas.classList.add("aonNewDesktopTitleH1");
		bannerAppsDiv.appendChild(titleActivas);
		div.appendChild(bannerAppsDiv);

		let appsActivas =  this.createElement(TAG.DIV);
		appsActivas.style.display = "flex";
		appsActivas.style.flexWrap = "wrap";
		
		for ( const app in this.suiteApps ) {
			appsActivas.appendChild(this.buildApp(this.suiteApps[app]));
		}
		
		//for ( const app in this.suiteNoApps ) {
		//	appsActivas.appendChild(this.buildApp(this.suiteNoApps[app], false));
		//}	

		div.appendChild(appsActivas);
		return div;
	}
	
	buildPlanApps() {
		let div = this.createDiv();	

		let appsActivas =  this.createElement(TAG.DIV);
		appsActivas.style.display = "flex";
		appsActivas.style.flexWrap = "wrap";
		
		let cardDiv  = this.createElement(TAG.DIV);
		cardDiv.id = `aonDesktop-planApp`;
		cardDiv.classList.add(CSS.AON_CARD);
		cardDiv.classList.add("aonNewDesktopCardDiv");
		cardDiv.title = "Ejecutar";
		cardDiv.style.width = "12rem";
	    cardDiv.style.backgroundColor = "#ff8f00";
	    cardDiv.style.color = "white";
		
		let appA = this.createElement(TAG.A);
		appA.addEventListener(EVENT.CLICK, () => {
			GWT.iLoad(GWT.PRODUCT_CATALOGUE_MODULE);
		});

		let appDiv = this.createElement(TAG.DIV);
		appDiv.classList.add("aonNewDesktopAppDiv");
		
		let icon = this.createElement(TAG.SPAN);
		icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		icon.id = `aonDesktopAppImg-planApp`;
		icon.innerHTML = 'store';
		icon.style.fontSize = "24px";
		appDiv.appendChild(icon);
		
		let titleSpan = this.createElement(TAG.SPAN);
		titleSpan.id = `aonDesktopAppTitle-planApp`;
		titleSpan.classList.add("aonNewDesktopAppDiv");
		titleSpan.innerHTML = "Ampliar Contratación";
		appDiv.appendChild(titleSpan);

		appA.appendChild(appDiv);

		cardDiv.appendChild(appA);
		
		appsActivas.appendChild(cardDiv);

		div.appendChild(appsActivas);
		return div;
	}
	
	buildApp(app, selectable = true) {
		
		let cardDiv  = this.createElement(TAG.DIV);
		cardDiv.id = `aonDesktop-${app.app}`;
		cardDiv.classList.add(CSS.AON_CARD);
		cardDiv.classList.add("aonNewDesktopCardDiv");
		cardDiv.style.width = "10rem";
		cardDiv.title = "Ejecutar";
		if(!selectable) {
			cardDiv.title = "No Contratada";
			cardDiv.style.backgroundColor = "rgb(240, 240, 240)";
			cardDiv.style.cursor = "not-allowed";
		}
		
		let appA = this.createElement(TAG.A);
		if(selectable)
			appA.addEventListener(EVENT.CLICK, () => {
				this.appSelection(app);
			});

		let appDiv = this.createElement(TAG.DIV);
		appDiv.classList.add("aonNewDesktopAppDiv");
		
		if (app.symbol && app.app!="payroll") {
			let icon = this.createElement(TAG.SPAN);
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.id = `aonDesktopAppImg-${app.app}`;
			icon.innerHTML = app.symbol;
			icon.style.fontSize = "24px";
			appDiv.appendChild(icon);
		}
		else if (app.icon) {
			let aonIcon = new AonIcon();
			aonIcon.id = `aonDesktopAppImg-${app.app}`;
			aonIcon.icon = app.newIcon || app.icon;
			aonIcon.color = app.newColor || app.color;
			aonIcon.size = app.iconSize || "32px";
			appDiv.appendChild(aonIcon);
		} 

		else if (app.logo) {
		 	let img = this.createElement(TAG.IMG);
		 	img.id = `aonDesktopAppImg-${app.app}`;
			img.classList.add("aonNewMenuAppImg");
		 	img.src = app.logo;
		 	img.title = app.title;
		 	appDiv.appendChild(img);
		}

		let titleSpan = this.createElement(TAG.SPAN);
		titleSpan.id = `aonDesktopAppTitle-${app.app}`;
		titleSpan.classList.add("aonNewDesktopAppDiv");
		titleSpan.innerHTML = app.title;
		appDiv.appendChild(titleSpan);

		appA.appendChild(appDiv);

		cardDiv.appendChild(appA);

		return cardDiv;		
	}
	
	appSelection(app) {
		document.querySelector(TAG.AON_NEW_MENU).appSelection(app);
	}

	createApplication(id, title, application, main) {
		const app = this.createAonElement(application, id, title, main);
		this.appendChild(app);
		return app;
	}

	createAonElement(el, id, title, main){
		el.id = id || '';
		el.title = title || '';
		el.description = title || '';
		if(main)
		  el.main = true;
		return el;
	}
}

if(!window.customElements.get('aon-new-desktop')){
	window.customElements.define('aon-new-desktop', AonNewDesktop);
}