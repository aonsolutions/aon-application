import {AonElement} from '../../components/AonElement.js';
import { getRegistry} from '../../services/service.js';

import '../../components/aon-table.js';

import { CONSTANT, MSG, TAG} from '../../environments/environments.js';
import { AonReg } from './aon-reg.js';

export class AonRegistryList extends AonElement {

	AON_REGISTRY_TABLE;
	more;
	filter;	

	connectedCallback () {
		this.initialize();
		this.innerHTML = `
			<aon-table id='${this.AON_REGISTRY_TABLE}'></aon-table>
			`;
		this.build();
 	}

	initialize() {
		this.more = true;
		this.AON_REGISTRY_TABLE = 'aonRegistryTable';
		this.filter = this.filter || {
			page: 1,
			perPage: 50		
		};
	}

	build() {
		let aonTable = this.getElement(this.AON_REGISTRY_TABLE);
		this.selectabledTable(aonTable);

		aonTable.addColumn(MSG.DOCUMENT, 'string', 'document', '20%');
		aonTable.addColumn(MSG.NAME, 'string', 'name', '40%');
		aonTable.addColumn(MSG.ALIAS, 'string', 'alias', '40%');
		aonTable.addEventListener('more', () => {
			if(this.more) this.loadMore()
		});
		this.init();
 	}

	selectabledTable(aonTable){
		if(this.selectable){
			aonTable.selectable = true;
			aonTable.addEventListener('select', () => {
				this.dispatchEvent(new CustomEvent("select", {detail: {parent:this, table:aonTable}}));
			});
		}
	}

	loadMore() {
		this.more = false;
		let table = this.getElement(this.AON_REGISTRY_TABLE);
		if(table && this.filter.page) {
			this.filter.page = this.filter.page + 1;
			this.getRegistries(this.filter).then(registries => {
				if(registries.length > 0)
					this.more = true;
				
				registries.forEach((registry, i) => {
					table.addRow(registry, () => this.buildRegistry(registry));
				});
			});
		}
	}

	init() {
		let table = this.getElement(this.AON_REGISTRY_TABLE);
		if(table) {
			table.removeRows();
			this.getRegistries(this.getFilter()).then(registries => {
				registries.forEach((registry, i) => {
					table.addRow(registry, () => this.buildRegistry(registry));
				});
			});	
		}
	}

	getRegistries() {

	}

	buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RECORD_DATA']
		};
		getRegistry(data).then(r => {
			let aonRegistry = new AonReg();
			aonRegistry.id = this.getApplication().id + 'Registry';
			aonRegistry.setRegistry(r);
			this.getApplication().setContent(aonRegistry);
		});
	}

	getFilter() {
		return this.filter || {};
	}

	setFilter(filter) {
		this.filter = filter;
		this.init();
	}

}

if(!window.customElements.get(TAG.AON_REGISTRY_LIST)) {
	window.customElements.define(TAG.AON_REGISTRY_LIST, AonRegistryList);
}
