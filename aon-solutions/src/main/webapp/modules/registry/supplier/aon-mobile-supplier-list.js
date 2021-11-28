import {getSupplier} from '../../../services/service.js';
import { TAG} from '../../../environments/environments.js';
import { AonMobileRegistryList } from '../aon-mobile-registry-list.js';
import { AonMobileSupplier } from './aon-mobile-supplier.js';

export class AonMobileSupplierList extends AonMobileRegistryList {

	getRegistries() {
		return getSupplier(this.filter2);
	}

	buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RECORD_DATA']
		};
		getSupplier(data).then(r => {
			let aonRegistry = new AonMobileSupplier();
			aonRegistry.id = this.getApplication().id + 'Registry';
			aonRegistry.setRegistry(r);
			this.getApplication().setContent(aonRegistry);
		});
	}

	setFilter(filter) {
		this.filter2 = filter;	
		this.init();
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_SUPPLIER_LIST)) {
	window.customElements.define(TAG.AON_MOBILE_SUPPLIER_LIST, AonMobileSupplierList);
}
