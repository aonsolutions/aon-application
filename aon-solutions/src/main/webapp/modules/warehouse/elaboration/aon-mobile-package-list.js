import { AonMobileList } from '../../../components/aon-mobile-list.js';
import { MATERIAL_ICONS, TAG } from '../../../environments/environments.js';
import { getElaborationPackages } from '../../../services/warehouseService.js';
import { AonMobilePackage } from './aon-mobile-package.js';

export class AonMobilePackageList extends AonMobileList {

    more;

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
        this.filter = {
            page:1,
            perPage:30
        }
    }

    loadMore() {
        let filter = this.getFilter();
        if(filter.page) {
            filter.page = filter.page + 1;
            this.setFilter(filter);
            getElaborationPackages(filter).then(packages => {
                if(packages.length == 0)
                    this.more = false;
                packages.forEach((packaging, i) => this.addRow(packaging, i));
            });
        }
    }

    init() {
        this.build();
        getElaborationPackages({
            page:1,
            perPage:30
        }).then(packages => {
            if(packages.length == 0){   
                this.empty();
            }
            packages.forEach((packaging, i) => this.addRow(packaging, i));
        });        
    }

    addRow(packaging, i) {
        let liValue = {
            icon: MATERIAL_ICONS.PALLET,
            title: packaging.reference,
            subtitle: packaging.description
        }
        this.addLi(liValue, i, () => this.aonPackage(packaging, i));
    }

    aonPackage(packaging, i) {
        let aonPackage = new AonMobilePackage();
        aonPackage.setPackaging(packaging);
        let div = this.getElement('aonPackageDiv');
        this.clearElement(div);
        div.appendChild(aonPackage);
    }
    
    getFilter() {
		return this.filter || {};
	}

    setFilter(filter) {		
		this.filter = JSON.stringify(filter);
        this.init();
	}
}
if(!window.customElements.get(TAG.AON_MOBILE_PACKAGE_LIST)){
    window.customElements.define(TAG.AON_MOBILE_PACKAGE_LIST, AonMobilePackageList);
}