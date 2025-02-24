
import {getTaskHolderNoCache, getTastHoldersList} from '../../../services/service.js';
import { AonMobileTaskHolder } from './aon-mobile-taskholder.js';
import { AonMobileRegistryList } from '../aon-mobile-registry-list.js';

export class AonMobileTaskHolderList extends AonMobileRegistryList {

	getRegistries() {
		return getTastHoldersList(this.filter2);
	}

	async buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA']
		};
		const r = await getTaskHolderNoCache(data);
		let aonTaskHolder = new AonMobileTaskHolder();
		aonTaskHolder.id = this.getApplication().id + 'TaskHolder';
		aonTaskHolder.setTaskHolder(r);
		this.getApplication().setContent(aonTaskHolder);
	}

	setFilter(filter) {
		this.filter2 = filter;	
		this.init();
	}
}

if(!window.customElements.get("aon-mobile-taskholder-list")) {
	window.customElements.define("aon-mobile-taskholder-list", AonMobileTaskHolderList);
}
