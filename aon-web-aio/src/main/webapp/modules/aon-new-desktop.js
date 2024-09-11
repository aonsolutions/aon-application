import {AonElement} from 'aonsolutions/components/AonElement.js';

import { MSG, CONSTANT, AON_ICONS, CSS, EVENT, MATERIAL_ICONS, TAG } from 'aonsolutions/environments/environments.js';
import { AonIcon } from 'aonsolutions/components/aon-icon.js';
import { AonApplication } from 'aonsolutions/components/aon-application.js';
import * as LS from 'aonsolutions/services/localStorageService.js';

export class AonNewDesktop extends AonElement {
	
	AON_DESKTOP;
	
	portalApps;
	portalNoApps;
	suiteApps;
	suiteNoApps;
	
	constructor(portalApps, portalNoApps, suiteApps, suiteNoApps) {
		super();
		this.portalApps = portalApps;
		this.portalNoApps = portalNoApps;
		this.suiteApps = suiteApps;
		this.suiteNoApps = suiteNoApps;
	}
	
	connectedCallback () {
		this.init();
		this.build();
	}
	
	init() {
		this.AON_DESKTOP = 'aonDesktop';
	}

	build() {

		let app = this.createApplication(this.AON_DESKTOP, MSG.APPLICATIONS, new AonApplication());
		app.main = "true";
		this.appendChild(app);
		app.closeSidenav();

		let div = this.createDiv();
		div.appendChild(this.buildNormal());
		div.appendChild(this.buildPrueba());
		app.setContent(div);

	}

	buildPrueba(){
		let div = this.createDiv();
		div.classList.add("aonNewDesktopBeta");

		let headerDiv = this.createElement(TAG.DIV);
		headerDiv.classList.add("aonNewDesktopHeaderDiv");
		div.appendChild(headerDiv);		

		let divActivas = this.createElement(TAG.DIV);
		divActivas.classList.add("aonNewDesktopDesktopDiv");
		
		let bannerAppsDiv = this.createElement(TAG.DIV);
		let titleActivas = this.createElement(TAG.H1);
		titleActivas.innerHTML = MSG.APPLICATIONS + " " + MSG.ACTIVES;
		titleActivas.classList.add("aonNewDesktopTitleH1");
		bannerAppsDiv.appendChild(titleActivas);
		divActivas.appendChild(bannerAppsDiv);

		let appsActivas =  this.createElement(TAG.DIV);
		appsActivas.classList.add('aonDesktopAppsContainer');		
		
		for ( const app in this.portalApps ) {
			if(this.portalApps[app].app!= "aonApplication")
				appsActivas.appendChild(this.buildApp(this.portalApps[app]));
		}
		
		for ( const app in this.suiteApps ) {
			appsActivas.appendChild(this.buildApp(this.suiteApps[app]));
		}

		divActivas.appendChild(appsActivas);
		div.appendChild(divActivas);

		let divMas = this.createElement(TAG.DIV);
		divMas.classList.add("aonNewDesktopDesktopDiv");

		let bannerAppsDiv2 = this.createElement(TAG.DIV);
		bannerAppsDiv2.style.marginTop = "20px";
		bannerAppsDiv2.style.display = "flex";

		let titleMas = this.createElement(TAG.H1);
		titleMas.innerHTML = "Más " + MSG.APPLICATIONS;
		titleMas.classList.add("aonNewDesktopTitleH1");
		bannerAppsDiv2.appendChild(titleMas);

		let titleGratis = this.createElement(TAG.H1);
		titleGratis.innerHTML = "(Prueba GRATIS durante 15 días)";
		titleGratis.style.marginLeft = "5px";
		titleGratis.style.fontSize = "15px";
		titleGratis.style.marginTop = "17px";
		titleGratis.style.fontWeight = "400";
		titleGratis.classList.add("aonNewDesktopTitleH1");
		bannerAppsDiv2.appendChild(titleGratis);

		divMas.appendChild(bannerAppsDiv2);

		let appsMas = this.createElement(TAG.DIV);
		appsMas.classList.add('aonDesktopAppsContainer');

		for ( const app in this.portalNoApps ) {
			if(this.portalNoApps[app].app!= "aonApplication")
				appsMas.appendChild(this.buildMasApp(this.portalNoApps[app]));
		}
		
		for ( const app in this.suiteNoApps ) {
			appsMas.appendChild(this.buildMasApp(this.suiteNoApps[app]));
		}

		divMas.appendChild(appsMas);
		div.appendChild(divMas);
		return div;

	}

	buildNormal(){
		let div = this.createDiv();
		div.classList.add("aonNewDesktopNormal");

		let headerDiv = this.createElement(TAG.DIV);
		headerDiv.classList.add("aonNewDesktopHeaderDiv");
		div.appendChild(headerDiv);		


		let desktopDiv = this.createElement(TAG.DIV);
		desktopDiv.classList.add("aonNewDesktopDesktopDiv");
		
		let bannerAppsDiv = this.createElement(TAG.DIV);
		let titleH1 = this.createElement(TAG.H1);
		titleH1.innerHTML = MSG.APPLICATIONS +  " Portal";
		titleH1.classList.add("aonNewDesktopTitleH1");
		bannerAppsDiv.appendChild(titleH1);
		desktopDiv.appendChild(bannerAppsDiv);

		let desktopAppsDiv =  this.createElement(TAG.DIV);
		desktopAppsDiv.classList.add('aonDesktopAppsContainer');		
		
		for ( const app in this.portalApps ) {
			desktopAppsDiv.appendChild(this.buildApp(this.portalApps[app]));
		}
		
		desktopDiv.appendChild(desktopAppsDiv);
		div.appendChild(desktopDiv);
		return div;
	}
	
	buildApp(app) {
		
		let cardDiv  = this.createElement(TAG.DIV);
		cardDiv.id = `aonDesktop-${app.app}`;
		cardDiv.classList.add(CSS.AON_CARD);
		cardDiv.classList.add("aonNewDesktopCardDiv");
		cardDiv.title = "Ejecutar";
		
		let appA = this.createElement(TAG.A);
		appA.addEventListener(EVENT.CLICK, () => {
			this.appSelection(app);
		});

		let appDiv = this.createElement(TAG.DIV);
		appDiv.classList.add("aonNewDesktopAppDiv");
		
		if (app.symbol) {
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

		// if(app.app == "accounting"|| app.app == "fiscal" || app.app == "payroll")
		// 	appDiv.className = "appDiv";

		cardDiv.appendChild(appA);

		return cardDiv;		
	}

	buildMasApp(app) {
		
		let cardDiv  = this.createElement(TAG.DIV);
		cardDiv.id = `aonDesktop-${app.app}`;
		cardDiv.classList.add(CSS.AON_CARD);
		cardDiv.classList.add("aonNewDesktopCardDiv");
		cardDiv.title = "Contratar";

		let cardButton = this.createElement(TAG.SPAN);
		cardButton.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		cardButton.innerHTML = 'add_shopping_cart';
		cardButton.style.position = 'absolute';
		cardButton.style.fontSize = "20px";
		cardButton.style.right = '10px';
		cardButton.style.top = "10px";
		cardButton.classList.add('aonAppMoreBtn');
		cardButton.style.visibility = 'hidden';

		cardDiv.addEventListener(EVENT.MOUSEOVER, () => {
			cardButton.style.visibility = 'visible';
		});

		cardDiv.addEventListener(EVENT.MOUSELEAVE, () => {
			cardButton.style.visibility = 'hidden';
		});

		cardDiv.append(cardButton);

		let appA = this.createElement(TAG.A);
		// appA.style.width = '100%';
		// appA.style.height = '100%';

		appA.addEventListener(EVENT.CLICK, () => {
			this.appSelection(app);
		});

		let appDiv = this.createElement(TAG.DIV);
		appDiv.classList.add("aonNewDesktopAppDiv");
		
		if (app.symbol) {
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

		cardDiv.style.paddingRight = '32px';
		


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