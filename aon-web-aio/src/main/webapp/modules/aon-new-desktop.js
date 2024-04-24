import {AonElement} from 'aonsolutions/components/AonElement.js';

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
		desktopAppsDiv.style.display = 'grid';
		desktopAppsDiv.style.rowGap = '20px';
		desktopAppsDiv.style.columnGap = '20px';
		desktopAppsDiv.style.gridTemplateColumns = 'repeat(7, minmax(0px, 1fr))';
		
		
		for ( const app in this.apps ) {
			desktopAppsDiv.appendChild(this.buildApp(this.apps[app]));
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
		desktopAonAppsDiv.style.display = 'grid';
		desktopAonAppsDiv.style.rowGap = '20px';
		desktopAonAppsDiv.style.columnGap = '20px';
		desktopAonAppsDiv.style.gridTemplateColumns = 'repeat(7, minmax(0px, 1fr))';

		for ( const app in this.aonApps ) {
			desktopAonAppsDiv.appendChild(this.buildApp(this.aonApps[app]));
		}
		desktopDiv.appendChild(desktopAonAppsDiv);


		this.appendChild(desktopDiv);		
	}
	
	
	buildApp(app) {
		
		let cardDiv  = this.createElement(TAG.DIV);
		cardDiv.id = `aonDesktop-${app.app}`;
		cardDiv.classList.add('aonCardApp');
		cardDiv.classList.add('aonCard');

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
	
}

if(!window.customElements.get('aon-new-desktop')){
	window.customElements.define('aon-new-desktop', AonNewDesktop);
}
