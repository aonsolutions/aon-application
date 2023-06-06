import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { getDomains, getCustomers, getBooking, updateDomainBooking, updateDomainBookingLog } from '../../services/domainsService.js';
import { get } from '../../services/request.js';
import { AonDomainCustomer } from '../../components/aon-domain-customer-link.js';
import { App, DomainType, ToolbarType } from '../../models/enums.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { MATERIAL_ICONS, MSG } from '../../environments/environments.js';
import { AonButton } from '../../components/aon-button.js';
import { AonItemUpdate } from '../../components/aon-item-update.js';



export class AonLinkDomains extends AonElement {

	AON_DOMAINS;
	DOMAINS_TOOLBAR;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
    	this.build();
 	}

	initialize(){
		this.AON_DOMAINS = 'aonDomains';
		this.id = this.AON_DOMAINS;
	}

 	async build() {
		this.paintView();
		this.applicationEl = this.getApplication();
		this.applicationParentEl = this.getApplicationParent();
	}

	paintView(){
		// this.createApplication(this.AON_DOMAINS, "Domains", new AonApplication());
		this.DOMAINS_TOOLBAR = new AonToolbar();
		this.DOMAINS_TOOLBAR.id = this.id + "Toolbar";
		this.DOMAINS_TOOLBAR.type = ToolbarType.SECONDARY;
		this.DOMAINS_TOOLBAR.title = "Dominios";
		this.appendChild(this.DOMAINS_TOOLBAR);
		this.DOMAINS_TOOLBAR.removeButtons();
		let toolbarButtonParams = {
			id: "UpdateClientItemsButton",
			name: "Actualizar productos",
			title: "Actualizar productos",
			icon: MATERIAL_ICONS.INVENTORY
		}
		let button = this.DOMAINS_TOOLBAR.addButton2(toolbarButtonParams, () => {
			let dialog = this.getApplication().getDialog();
    		dialog.clear();
    		dialog.setTitle(`Actualizar productos contratados`);
			dialog.width = "400px";
			let itemUpdateElement = new AonItemUpdate();
			itemUpdateElement.onFinish = () => {console.log("Proceso finalizado")};
			dialog.setContent(itemUpdateElement);
			dialog.autoclose = false;
			dialog.open();
		});
		button.querySelector("i").classList.add("material-icons-outlined");
		let adcContainer = document.createElement("div");
		adcContainer.style.width = "100%";
		adcContainer.style.height = "calc(100% - 40px)";
		let adc = new AonDomainCustomer();
		adcContainer.appendChild(adc);
		this.appendChild(adcContainer);
	}

	showView(view, data, filter = undefined){
		return new Promise(async(resolve)=>{
		  let aonView = undefined;
			switch(view){
				case "VIEW_ID":
					// aonView = new AonComponent();
				break;
			}
			aonView.id = view;
			if(filter) aonView.filter = filter;
			if(data) aonView.data = data;
			this.applicationEl.setContent(aonView);
		  resolve(true);
		});
	  }
	  
}

window.customElements.define('aon-link-domains',  AonLinkDomains);
