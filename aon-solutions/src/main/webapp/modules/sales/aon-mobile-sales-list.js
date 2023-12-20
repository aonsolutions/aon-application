import { AonMobileList } from '../../components/aon-mobile-list.js';
import { EVENT, MATERIAL_ICONS, TAG } from '../../environments/environments.js';
import { getSales } from '../../services/salesService.js';
import { AonMobileSale } from './aon-mobile-sale.js';

export class AonMobileSalesList extends AonMobileList {
	more;
    filter;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.init();
        this.addEventListener(EVENT.MORE, this.moreFn);
    }
    
    moreFn = () => {
        if(this.more)
            this.loadMore()
    };

    disconnectedCallback() {
        this.removeEventListener(EVENT.MORE, this.moreFn);
    }

    initialize() {
        this.filter = this.filter || {
            page: 1,
            perPage: 30,
            full: true,
            to: new Date(),
            status: 'IN_PREPARATION'
        };
        this.more = true;
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
        this.addLi(liValue, i, () => this.aonSale(sale, i));
    }

    aonSale(sale, i) {
        let aonSale = new AonMobileSale();
        aonSale.setSale(sale);
        this.getApplication().setContent(aonSale);
    }

}

if(!window.customElements.get(TAG.AON_MOBILE_SALES_LIST)) {
    window.customElements.define(TAG.AON_MOBILE_SALES_LIST, AonMobileSalesList);
}