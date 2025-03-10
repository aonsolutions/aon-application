import {getCreditor} from '../../../services/service.js';
import { TAG} from '../../../environments/environments.js';
import { AonRegistryList } from '../aon-registry-list.js';
import { AonCreditor } from './aon-creditor.js';

export class AonCreditorList extends AonRegistryList {

	build(){
		this.filter = {
			page: 1,
			perPage: 50		
		}
		super.build();
	}

	getRegistries() {
		return getCreditor(this.filter);
	}

	buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD']
		};
		getCreditor(data).then(r => {
			let aonCreditor = new AonCreditor();
			aonCreditor.id = this.getApplication().id + 'Creditor';
			aonCreditor.setCreditor(r);
			this.getApplication().setContent(aonCreditor);
		});
	}
}

if(!window.customElements.get(TAG.AON_CREDITOR_LIST)) {
	window.customElements.define(TAG.AON_CREDITOR_LIST, AonCreditorList);
}
