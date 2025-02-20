import { TAG} from '../../../environments/environments.js';
import { getCarrier, getCarriers } from '../../../services/registryService.js';
import { AonMobileRegistryList } from '../aon-mobile-registry-list.js';
import { AonMobileCarrier } from './aon-mobile-carrier.js';

export class AonMobileCarrierList extends AonMobileRegistryList {

	getRegistries() {
		return getCarriers(this.filter2);
	}

	buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD']
		};
		getCarrier(data).then(r => {
			let aonRegistry = new AonMobileCarrier();
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

if(!window.customElements.get(TAG.AON_MOBILE_CARRIER_LIST)) {
	window.customElements.define(TAG.AON_MOBILE_CARRIER_LIST, AonMobileCarrierList);
}
