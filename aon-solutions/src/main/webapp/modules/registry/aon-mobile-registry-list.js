import {AonMobileList} from '../../components/aon-mobile-list.js';
import { MATERIAL_ICONS, TAG } from '../../environments/environments.js';
import { getRegistry } from '../../services/registryService.js';
import { AonMobileReg } from './aon-mobile-reg.js';

export class AonMobileRegistryList extends AonMobileList {

    more;
	filter2;	

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.init();
        this.addEventListener('more', () => {
    		if(this.more)
    			this.loadMore()
    	});
    }

    initialize() {
        this.more = false;
        this.filter2 = this.filter2 || {
			page: 1,
			perPage: 50		
		};
    }

    loadMore() {
        let filter = this.getFilter2();
        if(filter.page) {
            filter.page = filter.page + 1;
            this.setFilter(filter);
            this.getRegistries(filter).then(registries => {
                if(registries.length == 0)
                    this.more = false;
                    registries.forEach((registry, i) => this.addRow(registry, i));
            });
        }
    }

    init() {
        this.build();
        this.getRegistries(this.getFilter2()).then(registries => {
            if(registries.length == 0){   
                this.empty();
            }
            registries.forEach((registry, i) => this.addRow(registry, i));
        });        
    }

    addRow(registry, i) {
        let liValue = {
            icon: MATERIAL_ICONS.PERSON,
            title: registry.name,
            subtitle: registry.document
        }
        this.addLi(liValue, i, () => this.buildRegistry(registry));
    }

    getRegistries() {

	}

    buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RECORD_DATA']
		};
		getRegistry(data).then(r => {
			let aonRegistry = new AonMobileReg();
			aonRegistry.id = this.getApplication().id + 'Registry';
			aonRegistry.setRegistry(r);
			this.getApplication().setContent(aonRegistry);
		});
	}


    getFilter2() {
		return this.filter2 || {};
	}
}
window.customElements.define('aon-mobile-registry-list', AonMobileRegistryList);
