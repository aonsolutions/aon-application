import {getCustomers, getCustomer} from '../../../services/service.js';
import { TAG} from '../../../environments/environments.js';
import { AonRegistryList } from '../aon-registry-list.js';
import { AonCustomer } from './aon-customer.js';

export class AonCustomerList extends AonRegistryList {

	build(){
		this.filter = this.filter || { page: 1, perPage: 50 }
		super.build();
	}

	async getRegistries() {
		let customers = await getCustomers(this.filter);

		if(this.selectable){
			customers = customers.map(customer => {
				customer.option = this.getOptionsLink(customer);
				return customer;
			});
		}

		return customers;
	}

	getCustomerCustom(registry){
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RSEGMENT']
		};
		return getCustomer(data);
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


	getOptionsLink(){
		let option = [];

		option.push({
			name: 'Vincular dominio',
			icon: 'link',
			id: 'domain_link',
			permission:true,
			backgroundColor: "grey",
			fn: () => this.getApplication().development()
		});

		option.push({
			name: 'Crear empresa',
			icon: 'apartment',
			id: 'create_enterprise',
			permission:true,
			backgroundColor: "grey",
			fn: () => this.getApplication().development()
		});
		
		return option;
	}
}

if(!window.customElements.get(TAG.AON_CUSTOMER_LIST)) {
	window.customElements.define(TAG.AON_CUSTOMER_LIST, AonCustomerList);
}
