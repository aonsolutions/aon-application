import {getTaskHolderNoCache, getTastHoldersList} from '../../../services/service.js';
import { AonRegistryList } from '../aon-registry-list.js';
import { AonTaskHolder } from './aon-taskholder.js';

export class AonTaskHolderList extends AonRegistryList {

	build(){
		this.filter = {
			page: 1,
			perPage: 50		
		}
		super.build();
	}

	getRegistries() {
		return getTastHoldersList(this.filter);
	}

	async buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA']
		};
		const r = await getTaskHolderNoCache(data);
		let aonTaskHolder = new AonTaskHolder();
		aonTaskHolder.id = this.getApplication().id + 'TaskHolder';
		aonTaskHolder.setTaskHolder(r);
		this.getApplication().setContent(aonTaskHolder);
	}
}

if(!window.customElements.get("aon-taskholder-list")) {
	window.customElements.define("aon-taskholder-list", AonTaskHolderList);
}
