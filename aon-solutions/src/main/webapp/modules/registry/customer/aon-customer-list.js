import {getCustomers, getCustomer} from '../../../services/service.js';
import { TAG} from '../../../environments/environments.js';
import { AonRegistryList } from '../aon-registry-list.js';
import { AonCustomer } from './aon-customer.js';

export class AonCustomerList extends AonRegistryList {

	build(){
		this.filter = {
			page: 1,
			perPage: 50		
		}
		super.build();
	}

	getRegistries() {
		return getCustomers(this.filter);
	}

	buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD']
		};
		getCustomer(data).then(r => {
			let aonCustomer = new AonCustomer();
			aonCustomer.id = this.getApplication().id + 'Customer';
			aonCustomer.setCustomer(r);
			this.getApplication().setContent(aonCustomer);
		});
	}
}

if(!window.customElements.get(TAG.AON_CUSTOMER_LIST)) {
	window.customElements.define(TAG.AON_CUSTOMER_LIST, AonCustomerList);
}
