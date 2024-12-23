import { embedDashboard } from "@superset-ui/embedded-sdk";
import { AonElement } from 'aonsolutions/components/AonElement.js';
import { CONSTANT, EVENT, MSG, TAG } from 'aonsolutions/environments/environments.js';
import * as LS from 'aonsolutions/services/localStorageService.js';

import { API } from "aonsolutions/environments/environments.js";
import { request, put, get, getDefaultSessionData} from "aonsolutions/services/request.js";
//

export class Superset extends AonElement {

	SUPERSET;
	
	dashboard;

	constructor(dashboard) {
		super();
		this.dashboard = dashboard;
	}

	connectedCallback() {
		this.initialize();
		this.build();
	}
	
	disconnectedCallback(){
		this.replaceChildren();
	}

	initialize() {
		this.SUPERSET = CONSTANT.SUPERSET;
		this.id = this.id || this.SUPERSET;
	}

	build() {
		embedDashboard({
			id: this.dashboard , // given by the Superset embedding UI
			supersetDomain: this.getSupersetDomain(), //"https://superset.aonsolutions.org",
			mountPoint: document.getElementById(this.SUPERSET), // any html element that can contain an iframe
			fetchGuestToken: () => this.fetchGuestTokenFromBackend(),
			dashboardUiConfig: { // dashboard UI config: hideTitle, hideTab, hideChartControls, filters.visible, filters.expanded (optional), urlParams (optional)
				hideTitle: true,
				filters: {
					expanded: false,
				},
				urlParams: {
					// foo: 'value1',
					// bar: 'value2',
					// ...
				}
			},
			// optional additional iframe sandbox attributes
			iframeSandboxExtras: ['allow-top-navigation', 'allow-popups-to-escape-sandbox']
		});
	}
	
	async fetchGuestTokenFromBackend(){
		let data = 		{
//		  resources: [{
		      id: this.dashboard,//"39aa7e93-a3a2-4bf2-b6c6-1297d00bd0e0",
		      type: "dashboard"
//		    }]
			,
//		  rls: [{
		      clause: `domain = ${LS.getDomainId()}`
//		    }]
			,
//		  user: {
		    first_name: "Superset",
		    last_name: "Admin",
		    username: "admin"
//		  }
		};
		return new Promise((resolve, reject) => {
			request("GET", `${API.API}/superset/guest_token`, getDefaultSessionData(), data, (result, error) => {
				if (error) {
					reject(error);
				} else {
					let token = JSON.parse(result);
					resolve(token?.token || []);
				}
			}
			);
		});
	}
	
	getSupersetDomain(){
		let domainName = LS.getDomainName();
		let parentDomainName = domainName.split('.').slice(-2).join('.');
		return `https://superset.${parentDomainName}`;	
	}
	
}



if (!window.customElements.get(TAG.SUPERSET)) {
	window.customElements.define(TAG.SUPERSET, Superset);
}
