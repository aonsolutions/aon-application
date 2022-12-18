import { AonMobileList } from '../../components/aon-mobile-list.js';
import { MATERIAL_ICONS, TAG } from '../../environments/environments.js';
import { getSales } from '../../services/salesService.js';

export class AonMobileSalesList extends AonMobileList {
	more;
    filter;

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
        this.filter =  {
            page: 1,
            perPage: 30,
            full: true,
            to: new Date(),
            status: 'PENDING'
        };
        this.more = false;
    }

    loadMore() {
        let filter = this.filter;
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
        getSales(this.filter).then(sales => {
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

}

if(!window.customElements.get(TAG.AON_MOBILE_SALES_LIST)) {
    window.customElements.define(TAG.AON_MOBILE_SALES_LIST, AonMobileSalesList);
}