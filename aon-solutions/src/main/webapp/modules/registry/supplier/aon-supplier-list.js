import {getSupplier} from '../../../services/service.js';
import { TAG} from '../../../environments/environments.js';
import { AonRegistryList } from '../aon-registry-list.js';
import { AonSupplier } from './aon-supplier.js';

export class AonSupplierList extends AonRegistryList {

	getRegistries() {
		return getSupplier(this.filter);
	}

	buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD']
		};
		getSupplier(data).then(r => {
			let aonSupplier = new AonSupplier();
			aonSupplier.id = this.getApplication().id + 'Supplier';
			aonSupplier.setSupplier(r);
			this.getApplication().setContent(aonSupplier);
		});
	}
}

if(!window.customElements.get(TAG.AON_SUPPLIER_LIST)) {
	window.customElements.define(TAG.AON_SUPPLIER_LIST, AonSupplierList);
}
