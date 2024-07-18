import {getCustomers, getCustomer, getScopes, getTarget} from '../../../services/service.js';
import { EVENT, TAG} from '../../../environments/environments.js';
import { AonRegistryList } from '../aon-registry-list.js';
import { AonCustomer } from './aon-customer.js';
import * as ACTION from '../../actions.js';
import { OfficeUtils } from '../../office/OfficeUtils.js';
import { getProjectTypes } from '../../../services/projectService.js';
import { OfficeEnums } from '../../office/OfficeEnums.js';

export class AonCustomerList extends AonRegistryList {

	parent;

	constructor(parent) {
		super();
		this.parent = parent;
	}

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
			return this.filter.potential ? getTarget(data) : getCustomer(data);
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

		let btnSearch = this.getApplication().addSearchOption(true);
		
		btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail}) => {
			clearTimeout(timeOut);
			timeOut = setTimeout(() => {
				this.filter = {
					...this.filter,
					value:detail.search,
					scope:detail.scope,
					projectType: detail.projectType,
					rrelationship: detail.rrelationship,
					status: OfficeUtils.getCustomerStatus(detail),
					potential : OfficeUtils.getCustomerPotential(detail),
					page:1
				}
				this.setFilter(this.filter);
				this.parent.setFilterCustomers(this.filter);
				let filterCount = this.getElement("aonOfficePanelToolbarHeaderToolSectionSearchCountFilter");
				filterCount.style.display = 'none';
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

        const rrelationshipEl = this.getElement("rrelationship");
        rrelationshipEl.setOptions([
			{name:'Con empresa', value:true},
			{name:'Sin empresa', value:false}
		]);

		const rrelationship = this.filter.rrelationship;
		if(rrelationship!=null){
			rrelationshipEl.value = rrelationship;
		}

        let active = this.getElement("active");
        active.value = (this.filter.status ||  []).includes("ACTIVE");
        
        let inactive = this.getElement("inactive");
        inactive.value = (this.filter.status ||  []).includes("INACTIVE");

        let blocked = this.getElement("blocked");
        blocked.value = (this.filter.status ||  []).includes("BLOCKED");

		let potential = this.getElement("potential");
        potential.value = this.filter.potential == "true" ? "true" : "false";
	}
}

if(!window.customElements.get(TAG.AON_CUSTOMER_LIST)) {
	window.customElements.define(TAG.AON_CUSTOMER_LIST, AonCustomerList);
}
