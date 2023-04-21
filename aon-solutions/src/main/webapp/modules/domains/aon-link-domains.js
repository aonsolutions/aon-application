import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { getDomains, getCustomers } from '../../services/domainsService.js';
import { get } from '../../services/request.js';
import { AonDomainCustomer } from '../../components/aon-domain-customer-link.js';



export class AonLinkDomains extends AonElement {

	AON_DOMAINS;

	domains;
	customers;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
    	this.build();
 	}

	initialize(){
		this.AON_DOMAINS = 'aonDomains';
	}

 	async build() {

		this.domains = await getDomains();
		this.customers = await getCustomers({perPage: 1000});
		this.paintView();
		console.log(this.domains);

		this.applicationEl = this.getApplication();
		this.applicationParentEl = this.getApplicationParent();
	}

	paintView(){
		// this.createApplication(this.AON_DOMAINS, "Domains", new AonApplication());
		let adc = new AonDomainCustomer();
		this.appendChild(adc);
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
