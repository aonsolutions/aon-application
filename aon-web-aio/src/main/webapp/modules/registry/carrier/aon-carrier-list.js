import {getCarrier, getCarriers} from '../../../services/service.js';
import { TAG} from '../../../environments/environments.js';
import { AonRegistryList } from '../aon-registry-list.js';
import { AonCarrier } from './aon-carrier.js';

export class AonCarrierList extends AonRegistryList {

	getRegistries() {
		return getCarriers(this.filter);
	}

	buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD']
		};
		getCarrier(data).then(r => {
			let aonCarrier = new AonCarrier();
			aonCarrier.id = this.getApplication().id + 'Carrier';
			aonCarrier.setCarrier(r);
			this.getApplication().setContent(aonCarrier);
		});
	}
}

if(!window.customElements.get(TAG.AON_CARRIER_LIST)) {
	window.customElements.define(TAG.AON_CARRIER_LIST, AonCarrierList);
}
