import {AonElement} from 'aonsolutions/components/AonElement.js';

import { MSG, CONSTANT, AON_ICONS, CSS, EVENT, MATERIAL_ICONS, TAG } from 'aonsolutions/environments/environments.js';
import { AonIcon } from 'aonsolutions/components/aon-icon.js';
import * as LS from 'aonsolutions/services/localStorageService.js';

export class AonNewDesktop extends AonElement {
	
	AON_DESKTOP;
	
	apps;
	aonApps;
	
	constructor(apps, aonApps) {
		super();
		this.apps = apps;
		this.aonApps = aonApps;
	}
	
	connectedCallback () {
		this.init();
		this.build();
	}
	
	init() {
		this.AON_DESKTOP = 'aonDesktop';
	}

	build() {
		
		let headerDiv = this.createElement(TAG.DIV);
		headerDiv.classList.add("aonNewDesktopHeaderDiv");
		this.appendChild(headerDiv);		


		let desktopDiv = this.createElement(TAG.DIV);
		desktopDiv.classList.add("aonNewDesktopDesktopDiv");
		
		let bannerAppsDiv = this.createElement(TAG.DIV);
		let titleH1 = this.createElement(TAG.H1);
		titleH1.innerHTML = MSG.APPLICATIONS;
		titleH1.classList.add("aonNewDesktopTitleH1");
		bannerAppsDiv.appendChild(titleH1);
		desktopDiv.appendChild(bannerAppsDiv);
		
		let desktopAppsDiv =  this.createElement(TAG.DIV);
		desktopAppsDiv.classList.add('aonDesktopAppsContainer');		
		
		for ( const app in this.apps ) {
			let titulo = this.apps[app].title;
			if (titulo!=null && titulo!="Inicio" && titulo!="Aplicaciones") {
				desktopAppsDiv.appendChild(this.buildApp(this.apps[app]));
			}
		}
		
		desktopDiv.appendChild(desktopAppsDiv);
		
		let bannerAonAppsDiv = this.createElement(TAG.DIV);
		let titleAonH1 = this.createElement(TAG.H1);
		titleAonH1.innerHTML = 'Más de AON Solutions';
		titleAonH1.classList.add("aonNewDesktopTitleAonH1");
		bannerAonAppsDiv.appendChild(titleAonH1);
		desktopDiv.appendChild(bannerAonAppsDiv);

		let desktopAonAppsDiv =  this.createElement(TAG.DIV);
		desktopAonAppsDiv.classList.add('aonDesktopAonAppsContainer');

		for ( const app in this.aonApps ) {
			desktopAonAppsDiv.appendChild(this.buildMasApp(this.aonApps[app]));
		}
		desktopDiv.appendChild(desktopAonAppsDiv);

		this.appendChild(desktopDiv);		
	}
	
	buildApp(app) {
		
		let cardDiv  = this.createElement(TAG.DIV);
		cardDiv.id = `aonDesktop-${app.app}`;
		cardDiv.classList.add(CSS.AON_CARD);
		cardDiv.classList.add("aonNewDesktopCardDiv");

		let cardButton = this.createElement(TAG.SPAN);
		cardButton.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		cardButton.classList.add('aonAppMoreBtn');
		cardButton.classList.add("aonNewDesktopCardButton");
		

		let optionsDiv = this.createElement(TAG.DIV);
		optionsDiv.classList.add("aonNewDesktopOptionsDiv");

		let optionsList = this.createElement(TAG.UL);
		optionsList.classList.add("aonNewDesktopOptionsList");
		
		let optionsItem1 = this.createElement(TAG.LI);
		optionsItem1.classList.add('aonAppMoreListItem');

		let item1Anchor = this.createElement(TAG.A);
		
		let item1Icon = this.createElement(TAG.SPAN);
		item1Icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		item1Icon.innerHTML = 'launch';
		item1Icon.classList.add('aonAppMoreIcon');

		let item1Label = this.createElement(TAG.SPAN)
		item1Label.innerHTML = "Open in new tab";

		item1Anchor.appendChild(item1Icon);
		item1Anchor.appendChild(item1Label);

		optionsItem1.appendChild(item1Anchor);

		let optionsItem2 = this.createElement(TAG.LI);
		optionsItem2.classList.add('aonAppMoreListItem');

		let item2Anchor = this.createElement(TAG.A);

		let item2Icon = this.createElement(TAG.SPAN);
		item2Icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		item2Icon.innerHTML = 'info';
		item2Icon.classList.add('aonAppMoreIcon');

		let item2Label = this.createElement(TAG.SPAN);
		item2Label.innerHTML = "About";

		item2Anchor.appendChild(item2Icon);
		item2Anchor.appendChild(item2Label);

		optionsItem2.appendChild(item2Anchor);

		optionsList.append(optionsItem1);
		optionsList.append(optionsItem2);

		optionsDiv.append(optionsList);

		window.addEventListener(EVENT.CLICK, function(e) {
			if (cardButton.contains(e.target)) {
				if (optionsDiv.style.display == 'block') {
					optionsDiv.style.display = 'none';
				} else {
					optionsDiv.style.position = 'fixed';
					optionsDiv.style.zIndex = 8;
					let position = cardButton.getBoundingClientRect();
					if (position.left + 200 >= window.screen.width) {
						optionsDiv.style.left = position.left - 150;
					} else {
						optionsDiv.style.left = position.left + 15;
					}
					if (position.top + 150 >= window.screen.height) {
						optionsDiv.style.top = position.top - 100;
					} else {
						optionsDiv.style.top = position.top + 15;
					}
					optionsDiv.style.display = 'block';
					optionsDiv.animate([
						{transform: 'translateY(-10px)'},
						{transform: 'translateY(0px)'}
					], {
						duration: 100,
						fill: 'forwards'
					});
				}
			} else {
				optionsDiv.style.display = 'none';
			}
		});

		cardDiv.addEventListener(EVENT.MOUSEOVER, () => {
			cardButton.style.visibility = 'visible';
		});

		cardDiv.addEventListener(EVENT.MOUSELEAVE, () => {
			cardButton.style.visibility = 'hidden';
		});

		cardDiv.append(optionsDiv);

		cardDiv.append(cardButton);

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
		} else if (app.icon) {
			let aonIcon = new AonIcon();
			aonIcon.id = `aonDesktopAppImg-${app.app}`;
			aonIcon.icon = app.newIcon || app.icon;
			aonIcon.color = app.newColor || app.color;
			aonIcon.size = "32px";
			appDiv.appendChild(aonIcon);
		} else if (app.logo) {
			let img = this.createElement(TAG.IMG);
			img.id = `aonDesktopAppImg-${app.app}`;
			img.style.width = '24px';
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

		if(app.app == "accounting"|| app.app == "fiscal" || app.app == "payroll")
			appDiv.className = "appDiv";

		cardDiv.appendChild(appA);

		return cardDiv;		
	}

	buildMasApp(app) {
		
		let cardDiv = this.createElement(TAG.DIV);
		cardDiv.id = `aonDesktop-${app.app}`;
		cardDiv.classList.add(CSS.AON_CARD);
		cardDiv.classList.add("aonDesktopCardDiv");

		let cardButton = this.createElement(TAG.SPAN);
		cardButton.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		cardButton.innerHTML = 'more_horiz';
		cardButton.classList.add('aonAppMoreBtn');
		cardButton.classList.add("aonNewDesktopCardButton2");
		

		let optionsDiv = this.createElement(TAG.DIV);
		optionsDiv.classList.add("aonNewDesktopOptionsDiv2")

		let optionsList = this.createElement(TAG.UL);
		optionsList.classList.add("aonNewDesktopOptionsList");
		
		let optionsItem1 = this.createElement(TAG.LI);
		optionsItem1.classList.add('aonAppMoreListItem');

		let item1Anchor = this.createElement(TAG.A);
		
		let item1Icon = this.createElement(TAG.SPAN);
		item1Icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		item1Icon.innerHTML = 'launch';
		item1Icon.classList.add('aonAppMoreIcon');

		let item1Label = this.createElement(TAG.SPAN)
		item1Label.innerHTML = "Open in new tab";

		item1Anchor.appendChild(item1Icon);
		item1Anchor.appendChild(item1Label);

		optionsItem1.appendChild(item1Anchor);

		let optionsItem2 = this.createElement(TAG.LI);
		optionsItem2.classList.add('aonAppMoreListItem');

		let item2Anchor = this.createElement(TAG.A);

		let item2Icon = this.createElement(TAG.SPAN);
		item2Icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		item2Icon.innerHTML = 'info';
		item2Icon.classList.add('aonAppMoreIcon');

		let item2Label = this.createElement(TAG.SPAN);
		item2Label.innerHTML = "About";

		item2Anchor.appendChild(item2Icon);
		item2Anchor.appendChild(item2Label);

		optionsItem2.appendChild(item2Anchor);

		optionsList.append(optionsItem1);
		optionsList.append(optionsItem2);

		optionsDiv.append(optionsList);

		window.addEventListener(EVENT.CLICK, function(e) {
			if (cardButton.contains(e.target)) {
				if (optionsDiv.style.display == 'block') {
					optionsDiv.style.display = 'none';
				} else {
					optionsDiv.style.position = 'fixed';
					optionsDiv.style.zIndex = 8;
					let position = cardButton.getBoundingClientRect();
					if (position.left + 200 >= window.screen.width) {
						optionsDiv.style.left = position.left - 150;
					} else {
						optionsDiv.style.left = position.left + 15;
					}
					if (position.top + 150 >= window.screen.height) {
						optionsDiv.style.top = position.top - 100;
					} else {
						optionsDiv.style.top = position.top + 15;
					}
					optionsDiv.style.display = 'block';
					optionsDiv.animate([
						{transform: 'translateY(-10px)'},
						{transform: 'translateY(0px)'}
					], {
						duration: 100,
						fill: 'forwards'
					});
				}
			} else {
				optionsDiv.style.display = 'none';
			}
		});

		cardDiv.addEventListener(EVENT.MOUSEOVER, () => {
			cardButton.style.visibility = 'visible';
		});

		cardDiv.addEventListener(EVENT.MOUSELEAVE, () => {
			cardButton.style.visibility = 'hidden';
		});

		cardDiv.append(optionsDiv);

		cardDiv.append(cardButton);

		let appA = this.createElement(TAG.A);
		appA.classList.add("aonNewDesktopAppA");

		appA.addEventListener(EVENT.CLICK, () => {
			this.appSelection(app);
		});

		let appDiv = this.createElement(TAG.DIV);
		appDiv.classList.add("aonNewDesktopAppDiv");

		let mainDiv = this.createElement(TAG.DIV);
		mainDiv.classList.add("aonNewDesktopMainDiv");

		appDiv.append(mainDiv);

		if (app.symbol) {
			let icon = this.createElement(TAG.SPAN);
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.id = `aonDesktopAppImg-${app.app}`;
			icon.innerHTML = app.symbol;
			icon.style.fontSize = "24px";
			mainDiv.appendChild(icon);
		} else if (app.icon) {
			let aonIcon = new AonIcon();
			aonIcon.id = `aonDesktopAppImg-${app.app}`;
			aonIcon.icon = app.newIcon || app.icon;
			aonIcon.color = app.newColor || app.color;
			aonIcon.size = "32px";
			mainDiv.appendChild(aonIcon);
		} else if (app.logo) {
			let img = this.createElement(TAG.IMG);
			img.id = `aonDesktopAppImg-${app.app}`;
			img.style.width = '24px';
			img.src = app.logo;
			img.title = app.title;
			mainDiv.appendChild(img);
		}

		let titleSpan = this.createElement(TAG.SPAN);
		titleSpan.id = `aonDesktopAppTitle-${app.app}`;
		titleSpan.innerHTML = app.title;
		titleSpan.classList.add("aonNewDekstopTitleSpan");
		mainDiv.appendChild(titleSpan);

		let descriptionDiv = this.createElement(TAG.DIV);
		descriptionDiv.classList.add("aonNewDesktopDescriptionDiv");

		let descriptionSpan = this.createElement(TAG.SPAN);
		descriptionSpan.innerHTML = app.description;
		descriptionSpan.classList.add("aonNewDesktopDescriptionSpan");

		descriptionDiv.appendChild(descriptionSpan);

		appDiv.appendChild(descriptionDiv);

		appA.appendChild(appDiv);

		cardDiv.appendChild(appA);

		if (app.price != " ") {
			cardDiv.style.paddingRight = '32px';

			let priceDiv = this.createElement(TAG.DIV);
			priceDiv.style.position = 'absolute';
			priceDiv.style.padding = '10px';
			priceDiv.style.right = '0px';
			priceDiv.style.bottom = '0px';
			priceDiv.style.width = '28px';
			priceDiv.style.height = '28px';
			priceDiv.style.background = 'linear-gradient(to bottom right, #ffffff 50%, #e6e6e6 50%)';
			
			let priceSpan = this.createElement(TAG.SPAN);
			priceSpan.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			priceSpan.innerHTML = 'euro_symbol';
			priceSpan.style.color = '#242424';
			priceSpan.style.fontSize = '15px';

			priceDiv.appendChild(priceSpan);

			cardDiv.appendChild(priceDiv);
		}

		return cardDiv;		
	}
	
}


if(!window.customElements.get('aon-new-desktop')){
	window.customElements.define('aon-new-desktop', AonNewDesktop);
}
