import {getCustomers, getCustomer, getScopes} from '../../../services/service.js';
import { EVENT, TAG} from '../../../environments/environments.js';
import { AonRegistryList } from '../aon-registry-list.js';
import { AonCustomer } from './aon-customer.js';
import * as ACTION from '../../actions.js';
import { OfficeUtils } from '../../office/OfficeUtils.js';
import { getProjectTypes } from '../../../services/projectService.js';
import { OfficeEnums } from '../../office/OfficeEnums.js';

export class AonCustomerList extends AonRegistryList {

	build(){
		this.filter = this.filter || { page: 1, perPage: 50 }
		super.build();
	}

	async getRegistries() {
		let customers = await getCustomers(this.filter);

		return customers;
	}

	async getCustomerCustom(registry){
		if(registry && registry.id){
			let data = {
				id: registry.id,
				additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RSEGMENT']
			};
			return getCustomer(data);
		}
		
		return null;
	}

	buildRegistry(registry) {
		this.getCustomerCustom(registry)
		.then(r => {
			let aonCustomer = new AonCustomer();
			aonCustomer.id = this.getApplication().id + 'Customer';
			aonCustomer.setCustomer(r);
			this.getApplication().setContent(aonCustomer);
		});
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addToolbarOption2(ACTION.ADD, () => this.buildRegistry());
		this.buildSearch();
	}

	buildSearch(){
		let timeOut = null;

		let btnSearch = this.getApplication().addSearchOption();
		
		btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail}) => {
			clearTimeout(timeOut);
			timeOut = setTimeout(() => {
				this.filter = {
					...this.filter,
					value:detail.search,
					scope:detail.scope,
					projectType: detail.projectType,
					status: OfficeUtils.getCustomerStatus(detail),
					page:1
				}
				this.setFilter(this.filter);
			}, 300);
		});

		btnSearch.buildOptionsFilter(OfficeEnums.CustomerFilter);//INPUTS
		this.setSearchValues();
    }

    setSearchValues(){
		let scopeEl = this.getElement("scope");
        getScopes().then(scopes=>{
            scopeEl.setOptions(scopes.map(c=> ({...c, value: c.id})) );

            const value = this.filter.scope;
            if(value){
                scopeEl.value = value;
            }
        })

        let projectTypeEl = this.getElement("projectType");
        getProjectTypes({})
        .then(t => {
            let types = (t || []).map((r) => ({...r, name: r.description, value: r.id}));

            projectTypeEl.setOptions(types);

            const value = this.filter.projectType;
            if(value){
                projectTypeEl.value = value;
            }
        })

        let active = this.getElement("active");
        active.value = (this.filter.status ||  []).includes("ACTIVE");
        
        let inactive = this.getElement("inactive");
        inactive.value = (this.filter.status ||  []).includes("INACTIVE");
  
        let blocked = this.getElement("blocked");
        blocked.value = (this.filter.status ||  []).includes("BLOCKED");
	}
}

if(!window.customElements.get(TAG.AON_CUSTOMER_LIST)) {
	window.customElements.define(TAG.AON_CUSTOMER_LIST, AonCustomerList);
}
