import { AonMobileList } from '../../components/aon-mobile-list.js';
import { MATERIAL_ICONS, TAG } from '../../environments/environments.js';

export class AonMobileSalesList extends AonMobileList {
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
    }

    loadMore() {
        let filter = this.getFilter();
        if(filter.page) {
            filter.page = filter.page + 1;
            this.setFilter(filter);
            getSales(filter).then(sales => {
                if(sales.length == 0)
                    this.more = false;
                sales.forEach((sale, i) => this.addRow(sale, i));
            });
        }
    }

    init() {
        this.build();
        getSales(this.getFilter()).then(sales => {
            if(sales.length == 0){   
                this.empty();
            }
            sales.forEach((sale, i) => this.addRow(sale, i));
        });        
    }

    addRow(sale, i) {
        let liValue = {
            icon: MATERIAL_ICONS.SHOPPING_BAG,
            title: sale.reference,
            subtitle: sale.customer.name
        }
        this.addLi(liValue, i, () => {});
    }
    
    getFilter() {
		return this.filter || {};
	}

    setFilter(filter) {		
		this.filter = JSON.stringify(filter);
        this.init();
	}

}

if(!window.customElements.get(TAG.AON_MOBILE_SALES_LIST)) {
    window.customElements.define(TAG.AON_MOBILE_SALES_LIST, AonMobileSalesList);
}