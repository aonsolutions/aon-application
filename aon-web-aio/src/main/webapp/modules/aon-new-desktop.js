import {AonElement} from 'aonsolutions/components/AonElement.js';
import { Apps, HomeApps, MenuApps, AuxApps, MENU_APPS, TOP_MENU_APPS, AON_APPS, HOME } from '../services/app.js';
import {COMMERCE, OFFICE, GARAGE, ACADEMY} from  "aonsolutions/services/app.js";
import { getDomainUserRoles } from 'aonsolutions/services/service.js';
import { DomainUserRoles } from 'aonsolutions/models/DomainUserRoles.js';
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
import { AonNote } from 'aonsolutions/modules/note/aon-note.js';
import { AonInvoicePanel } from 'aonsolutions/modules/invoice/aon-invoice-panel.js';
import { AonBooking } from 'aonsolutions/modules/marketplace/aon-booking.js';
import { AonOfficePanel } from 'aonsolutions/modules/office/aon-office-panel.js';
import { AonConsole } from 'aonsolutions/modules/console/aon-console.js';
import { AonAppMenu } from 'aonsolutions/modules/aon-app-menu.js';
import { AonNotes } from 'aonsolutions/modules/note/aon-notes.js';
import { AonWarehouse } from 'aonsolutions/modules/warehouse/aon-warehouse.js';
import { AonMarketing } from 'aonsolutions/modules/marketing/aon-marketing.js';
import { AonCalendar } from 'aonsolutions/modules/calendar/aon-calendar.js';

import { AonParent } from './aon-parent.js';
import { MSG, CONSTANT, AON_ICONS, CSS, EVENT, MATERIAL_ICONS, TAG } from 'aonsolutions/environments/environments.js';
import { AonIcon } from 'aonsolutions/components/aon-icon.js';


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
			 case Apps.CALENDAR.app:
			 	this.rootPanel(new AonCalendar());
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
		
		let headerDiv = this.createElement(TAG.DIV);
		headerDiv.style.height = '48px';
		this.appendChild(headerDiv);		


		let desktopDiv = this.createElement(TAG.DIV);
		desktopDiv.style.display='grid';
		desktopDiv.style.paddingLeft = '64px';
		desktopDiv.style.paddingRight = '64px';
		
		let bannerAppsDiv = this.createElement(TAG.DIV);
		let titleH1 = this.createElement(TAG.H1);
		titleH1.innerHTML = MSG.APPLICATIONS;
		titleH1.style.fontSize = '20px';
		titleH1.style.fontWeight = '500';
		titleH1.style.paddingBottom = '32px';
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
		titleAonH1.style.marginTop = '32px';
		titleAonH1.style.fontSize = '16px';
		titleAonH1.style.fontWeight = '500';
		titleAonH1.style.paddingBottom = '32px';
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
		cardDiv.style.position = 'relative';

		let cardButton = this.createElement(TAG.SPAN);
		cardButton.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		cardButton.innerHTML = 'more_horiz';
		cardButton.style.position = 'absolute';
		cardButton.style.right = '5px';
		cardButton.style.top = "5px";
		cardButton.classList.add('aonAppMoreBtn');
		cardButton.style.visibility = 'hidden';

		let optionsDiv = this.createElement(TAG.DIV);
		optionsDiv.style.display = 'none';
		optionsDiv.style.backgroundColor = 'white';
		optionsDiv.style.padding = '5px';
		optionsDiv.style.borderRadius = '10px';
		optionsDiv.style.boxShadow = '0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)';

		let optionsList = this.createElement(TAG.UL);
		optionsList.style.listStyleType = 'none';
		optionsList.style.padding = 0;
		optionsList.style.margin = 0;
		
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

		window.addEventListener(EVENT.CLICK, (e) => {
			if (cardButton.contains(e.target)) {
				if (optionsDiv.style.display == 'block') {
					optionsDiv.style.display = 'none';
				} else {
					const rootPanelPosition = this.getElement('rootPanel').getBoundingClientRect();
					optionsDiv.style.position = 'fixed';
					optionsDiv.style.zIndex = 8;
					let position = cardButton.getBoundingClientRect();
					if (position.left + 200 >= rootPanelPosition.width) {
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
		appDiv.style.height = '56px';
		appDiv.style.padding = '1px';
		appDiv.style.display = 'flex';
		appDiv.style.alignItems = 'center';
		appDiv.style.flexDirection = 'column';
		appDiv.style.justifyContent = 'center';

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
			aonIcon.icon = app.icon;
			aonIcon.color = app.color;
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
		titleSpan.style.textAlign = 'center';
		titleSpan.innerHTML = app.title;
		appDiv.appendChild(titleSpan);

		appA.appendChild(appDiv);

		cardDiv.appendChild(appA);

		return cardDiv;		
	}

	buildMasApp(app) {
		
		let cardDiv = this.createElement(TAG.DIV);
		cardDiv.id = `aonDesktop-${app.app}`;
		cardDiv.classList.add(CSS.AON_CARD);
		cardDiv.style.position = 'relative';

		let cardButton = this.createElement(TAG.SPAN);
		cardButton.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		cardButton.innerHTML = 'more_horiz';
		cardButton.style.position = 'absolute';
		cardButton.style.right = '10px';
		cardButton.style.top = "10px";
		cardButton.classList.add('aonAppMoreBtn');
		cardButton.style.visibility = 'hidden';

		let optionsDiv = this.createElement(TAG.DIV);
		optionsDiv.style.display = 'none';
		optionsDiv.style.backgroundColor = 'white';
		optionsDiv.style.padding = '5px';
		optionsDiv.style.borderRadius = '5px';
		optionsDiv.style.boxShadow = '0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)';

		let optionsList = this.createElement(TAG.UL);
		optionsList.style.listStyleType = 'none';
		optionsList.style.padding = 0;
		optionsList.style.margin = 0;
		
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

		window.addEventListener(EVENT.CLICK, (e) => {
			if (cardButton.contains(e.target)) {
				if (optionsDiv.style.display == 'block') {
					optionsDiv.style.display = 'none';
				} else {
					const rootPanelPosition = this.getElement('rootPanel').getBoundingClientRect();
					optionsDiv.style.position = 'fixed';
					optionsDiv.style.zIndex = 8;
					let position = cardButton.getBoundingClientRect();
					if (position.left + 200 >= rootPanelPosition.width) {
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
		appA.style.width = '100%';
		appA.style.height = '100%';

		appA.addEventListener(EVENT.CLICK, () => {
			this.appSelection(app);
		});

		let appDiv = this.createElement(TAG.DIV);
		appDiv.style.height = '56px';
		appDiv.style.padding = '1px';
		appDiv.style.display = 'flex';
		appDiv.style.alignItems = 'flex-start';
		appDiv.style.flexDirection = 'column';
		appDiv.style.justifyContent = 'center';

		let mainDiv = this.createElement(TAG.DIV);
		mainDiv.style.display = 'flex';
		mainDiv.style.alignItems = 'center';
		mainDiv.style.flexDirection = 'row';
		mainDiv.style.width = '100%';

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
			aonIcon.icon = app.icon;
			aonIcon.color = app.color;
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
		titleSpan.style.textAlign = 'center';
		titleSpan.innerHTML = app.title;
		titleSpan.style.marginLeft = '10px';
		mainDiv.appendChild(titleSpan);

		let descriptionDiv = this.createElement(TAG.DIV);
		descriptionDiv.style.textAlign = 'left';
		descriptionDiv.style.marginTop = '10px';

		let descriptionSpan = this.createElement(TAG.SPAN);
		descriptionSpan.innerHTML = app.description;
		descriptionSpan.style.overflow = 'hidden';
		descriptionSpan.style.fontSize = '11px';

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
