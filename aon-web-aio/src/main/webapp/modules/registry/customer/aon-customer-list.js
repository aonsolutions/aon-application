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

			return this.filter.type == "false" ? getTarget(data) : getCustomer(data);
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
					type : detail.type,
					page:1
				}
				this.setFilter(this.filter);
				this.parent.setFilterCustomers(this.filter);
				let filterCount = this.getElement("aonOfficePanelToolbarHeaderToolSectionSearchCountFilter");
				filterCount.style.display = 'none';
			}, 300);
		});

		btnSearch.addEventListener(EVENT.RESET_FILTER, ({detail}) => {
			clearTimeout(timeOut);
			timeOut = setTimeout(() => {
				this.filter = {
					page: 1,
					perPage: 50,
					status: ["ACTIVE", "BLOCKED"]
				}
				this.setFilter(this.filter);
				this.parent.setFilterCustomers(this.filter);
				this.setSearchValues();	
			}, 300);
		});

		let searchInput = this.getElement("aonOfficePanelToolbarHeaderToolSectionSearchSearchInput");
		searchInput.placeholder = "Buscar por nombre, documento o alias";
		searchInput.focus();

		btnSearch.buildOptionsFilter(OfficeEnums.CustomerFilter);//INPUTS
		this.setSearchValues();	
    }

    setSearchValues(){
		let searchInput = this.getElement("aonOfficePanelToolbarHeaderToolSectionSearchSearchInput");
		let searchValue = this.filter.value;
		searchInput.value = searchValue ? searchValue : '';
		
		let scopeEl = this.getElement("scope");
        getScopes().then(scopes=>{
            scopeEl.setOptions(scopes.map(c=> ({...c, value: c.id})) );

            const value = this.filter.scope;
			scopeEl.setValue(value);
        })

        let projectTypeEl = this.getElement("projectType");
        getProjectTypes({})
        .then(t => {
            let types = (t || []).map((r) => ({...r, name: r.description, value: r.id}));

            projectTypeEl.setOptions(types);

            const value = this.filter.projectType;
			projectTypeEl.setValue(value);
        })

        const rrelationshipEl = this.getElement("rrelationship");
        rrelationshipEl.setOptions([
			{name:'Con empresa', value:true},
			{name:'Sin empresa', value:false}
		]);

		const rrelationship = this.filter.rrelationship;
		let input = rrelationshipEl.getInput();
		if(rrelationship){
			input.value = rrelationship == "false" ? "Sin empresa" : "Con empresa";
			rrelationshipEl.setValue("'" + rrelationship + "'");
		} else {
			input.value = '';
			rrelationshipEl.setValue('');
		}

        let active = this.getElement("active");
		if((this.filter.status ||  []).includes("ACTIVE")){
			active.value = true;
		} else {
			active.clear();
		}

		let inactive = this.getElement("inactive");
		if((this.filter.status ||  []).includes("INACTIVE")){
			inactive.value = true;
		} else {
			inactive.clear();
		}

		let blocked = this.getElement("blocked");
        if((this.filter.status ||  []).includes("BLOCKED")){
			blocked.value = true;
		} else {
			blocked.clear();
		}

		const typeEl = this.getElement("type");
        typeEl.setOptions([
			{name:'Cliente', value:true},
			{name:'Cliente Potencial', value:false}
		]);
		const type = this.filter.type;
		let typeInput = typeEl.getInput();
		if(type){
			typeInput.value = type == "false" ? "Cliente Potencial" : "Cliente";
			typeEl.setValue(type);
		}else {
			typeInput.value = '';
			typeEl.setValue('');
		}
	}
}

if(!window.customElements.get(TAG.AON_CUSTOMER_LIST)) {
	window.customElements.define(TAG.AON_CUSTOMER_LIST, AonCustomerList);
}
