import {AonElement} from '../components/AonElement.js';
import { Apps} from  '../services/app.js';
import {getDomainUserRoles} from  '../services/service.js';

import {DomainUserRoles} from '../models/DomainUserRoles.js';
import { CONSTANT, CSS, MSG, TAG } from '../environments/environments.js';
import { AonDocumentalAyudat } from './documental/ayudat/aon-documental-ayudat.js';
import { AonDocumental } from './documental/aon-documental.js';
import '../components/aon-icon.js';
import * as UA from '../services/userAgentService.js' ;
import '../components/aon-application.js';
import './invoice/aon-invoice-panel.js';
import './laboral/aon-laboral.js';
import './messenger/aon-messenger.js';
import './fiscal/aon-fiscal.js';
import './accounting/aon-accounting.js';
import { AonTimecontrol } from './timecontrol/aon-timecontrol.js';
import { AonWarehouse } from './warehouse/aon-warehouse.js';
import { AonConsole } from './console/aon-console.js';
import { AonMarketing } from './marketing/aon-marketing.js';
import { AonDragLeftNotification } from './home/aon-dragleft-notification.js';
import { AonComunica } from './laboral/aon-comunica.js';


export class AonApps extends AonElement {

	dur;

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
  	}

	initialize(){

	}

	getDur() {
		return this.dur;
	}

	build() {
		//this.appendChild(this.buildTitle('APLICACIONES DISPONIBLES'));
		let title = this.buildTitle('Aplicaciones');
		title.id = "tituloAplicaciones";
		this.appendChild(title);

		let ul = this.createElement(TAG.UL);
		ul.id = "aonMobileAppSelection";
		ul.classList.add(CSS.AON_UL);
		ul.classList.add(CSS.AON_LIST_GROUP);

		for (let key in Apps){
			let app = Apps[key];
			if(this.isApp(app)) {

				let li = this.createElement(TAG.LI);
				li.id = "aonMobileAppSelectionApp-"+ app.app;
				li.classList.add(CSS.AON_LIST_GROUP_ITEM);
				li.classList.add(CSS.AON_APP_LI);
				li.classList.add("fixLi");
				li.style.borderRight = '0px';
				li.style.borderLeft = '0px';
				li.style.cursor = 'pointer';
				
				li.addEventListener('click', () => {
					this.appSelection(app.app);
				});

				let span = this.createElement(TAG.SPAN);

				span.style.margin = '20px';

				if(app.symbol) {
					let icon = this.createSpan();
					icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
					icon.id = "aonMobileSelectionIcon-"+app.app;
					icon.innerHTML = app.symbol;
					if(app.app == "console")
						icon.style.backgroundColor = "black";
					else
						icon.style.backgroundColor = app.color;
					icon.style.color = "white";
					icon.style.fontVariationSettings = "'FILL' 0, 'wght' 300, 'GRAD' 0, 'opsz' 24";
					icon.style.borderRadius = "5px";
					span.appendChild(icon);
				}  else if(app.icon) {
					span.innerHTML = `<aon-icon id="aonMobileSelectionIcon-${app.app}" icon="${app.icon}" color="${app.color}" size="28px"></aon-icon>`;
				} else {
					let img = this.createElement(TAG.IMG);
					img.id = "aonMobileSelectionImg-"+app.app;
					img.style.width = '30px';
					img.src = app.logo;
					span.appendChild(img);
				}
				let span2 = this.createElement(TAG.SPAN);
				span2.id = "aonAppTitle-"+app.app
				span2.className = 'aonAppTitle';
				span2.innerHTML = app.title;
				span.appendChild(span2);

				// let buttons = this.createElement(TAG.SPAN);
				// buttons.style.position = 'absolute';
				// buttons.style.right = '10px';

				// let i = this.createElement('i');
				// i.className = 'material-icons';
				// i.innerHTML = 'keyboard_arrow_right';
				// buttons.appendChild(i);

				//span.appendChild(buttons);
				li.appendChild(span);
				ul.appendChild(li);
			}
		}
  	
		this.appendChild(ul);
	}

	buildTitle(title) {
		let div = this.createElement(TAG.DIV);
		div.className = 'aonMobileHomeTitle';
		div.innerHTML = title;
		return div;
	}

	appSelection(app) {
		switch(app){
			case Apps.CONSOLE.app:
				this.rootPanel(new AonConsole());
				break;
			case Apps.DOCUMENTAL.app:
				const aonDocumental = this.getDur().isBidoq() ? new AonDocumentalAyudat() : new AonDocumental();
				this.rootPanel(aonDocumental);
				break;
			case Apps.ACCOUNTING.app:
				this.rootPanelHtml('<aon-accounting></aon-accounting>');
				break;
			case Apps.FISCAL.app:
				this.rootPanelHtml('<aon-fiscal></aon-fiscal>');
				break;
			case Apps.COMUNICA.app:
				this.rootPanel(new AonComunica());
				break;
			case Apps.PAYROLL.app:
				this.rootPanelHtml(`<aon-laboral title="${MSG.PAYROLL}"></aon-laboral>`);
				break;
			case Apps.INVOICE.app:
				this.rootPanelHtml('<aon-invoice-panel></aon-invoice-panel>');
				break;
			case Apps.TIMECONTROL.app:
				this.rootPanel(new AonTimecontrol());
				break;
			case Apps.MESSENGER.app:
				this.rootPanelHtml('<aon-messenger></aon-messenger>');
				break;
			case Apps.WAREHOUSE.app:
				this.rootPanel(new AonWarehouse());
				break;
			case Apps.MARKETING.app:
				this.rootPanel(new AonMarketing());
				break;
		}
	}

	development(title) {
		this.getApplication().development(title);
	}

	isApp(app) {
		if(Apps.ACCOUNTING.app === app.app)
			return this.getDur().isAccounting();
		else if(Apps.FISCAL.app === app.app)
			return this.getDur().isFiscal();
		else if(Apps.COMUNICA.app === app.app)
			return (this.getDur().isComunicaManager() || this.getDur().isComunicaPortal() ) && (UA.isMobile() || (!UA.isMobile() && !this.getDur().isPayroll()));
		else if(Apps.PAYROLL.app === app.app)
			return this.getDur().isPayroll();
		else if(Apps.DOCUMENTAL.app === app.app)
			return this.getDur().isDocumental();
		else if(Apps.TIMECONTROL.app === app.app)
			return this.getDur().isTimecontrol();
		else if(Apps.INVOICE.app === app.app)
			return this.getDur().isInvoice();
		else if(Apps.MESSENGER.app === app.app)
			return this.getDur().isMessenger();
		else if(Apps.MARKETING.app === app.app)
			return this.getDur().isMarketing() && this.isBeta();
		else if(Apps.WAREHOUSE.app === app.app){
			const domain = this.getDur().getDomain();
			return domain.getName() && (domain.getName().includes("udapa") || domain.getName().includes("paturpat") || this.isLocal());
		} else if(Apps.CONSOLE.app === app.app){
			return this.isBeta();
		} else 
			return false;
	}
}

if(!window.customElements.get('aon-apps')){
	window.customElements.define('aon-apps', AonApps);
}