import {AonElement} from '../../components/AonElement.js';
import { getRegistry} from '../../services/service.js';
import { MSG, TAG, EVENT} from '../../environments/environments.js';
import { AonReg } from './aon-reg.js';
import { AonTable } from '../../components/aon-table.js';

export class AonRegistryList extends AonElement {

	TABLE;
	more;
	filter;	

	connectedCallback () {
		this.initialize();

		this.TABLE = new AonTable();
		this.TABLE.id = 'aonRegistryTable';
		this.appendChild(this.TABLE);
		this.build();
	}

	initialize() {
		this.more = true;
		this.filter = this.filter || {
			page: 1,
			perPage: 50		
		};
	}

	build() {
	    this.selectabledTable();
	    this.TABLE.addColumn(MSG.DOCUMENT, 'string', 'document', '10%');
	    this.TABLE.addColumn(MSG.NAME,  'string', 'name',  '30%', undefined, true);
		this.TABLE.addColumn(MSG.ALIAS, 'string', 'alias', '15%', undefined, true);
	    this.TABLE.addColumn(MSG.STATUS, 'string', 'statusText', '10%');
	
	    if(this.selectable){
	        this.TABLE.addColumn('Vinc.', 'string', 'link', '8%');
	    }
	
	    this.addCustomColumns(); 
	
	    this.TABLE.addEventListener(EVENT.MORE, () => {
	        if(this.more) this.loadMore();
	    });
	    this.init();
	}
	
	addCustomColumns() {}   // por defecto no hace nada

	selectabledTable(){
		if(this.selectable){
			this.TABLE.selectable = true;
			this.TABLE.addEventListener(EVENT.SELECT, () => {
				this.dispatchEvent(new CustomEvent(EVENT.SELECT, {detail: {table:this.TABLE}}));
			});
		}
	}

	loadMore() {
		this.more = false;
		if(this.TABLE && this.filter.page) {
			this.filter.page = this.filter.page + 1;
			this.getRegistries(this.filter)
			.then(registries => {
				if(registries.length > 0)
					this.more = true;
				
				registries.forEach((registry) => {

					if(registry.status){
						registry.statusText = MSG[registry.status];
					} 

					let tr = this.TABLE.addRow(registry, () => this.buildRegistry(registry));
					tr.id = "aonInvoiceRow";
				});
			});
		}
	}

	init() {
		if(this.TABLE) {
			this.TABLE.removeRows();
			this.getRegistries(this.getFilter())
			.then(registries => {
				registries.forEach((registry) => {

					if(registry.status){
						registry.statusText = MSG[registry.status];
					}

					if(registry.isRelationship!=null){
						registry.link = registry.isRelationship ? "Si" : "No";
					}

					let tr = this.TABLE.addRow(registry, () => this.buildRegistry(registry));
					tr.id = "aonInvoiceRow";
				});
				this.dispatchEvent(new CustomEvent(EVENT.BUILD, {detail: {registries: registries}}));
			});	
		}
	}

	async getRegistries() {

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
