import {getCreditor} from '../../../services/service.js';
import { TAG} from '../../../environments/environments.js';
import { AonMobileRegistryList } from '../aon-mobile-registry-list.js';
import { AonMobileCreditor } from './aon-mobile-creditor.js';

export class AonMobileCreditorList extends AonMobileRegistryList {

	getRegistries() {
		return getCreditor(this.filter2);
	}

	buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RECORD_DATA']
		};
		getCreditor(data).then(r => {
			let aonRegistry = new AonMobileCreditor();
			aonRegistry.id = this.getApplication().id + 'Registry';
			aonRegistry.setCreditor(r);
			this.getApplication().setContent(aonRegistry);
		});
	}

	setFilter(filter) {
		this.filter2 = filter;	
		this.init();
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_CREDITOR_LIST)) {
	window.customElements.define(TAG.AON_MOBILE_CREDITOR_LIST, AonMobileCreditorList);
}
