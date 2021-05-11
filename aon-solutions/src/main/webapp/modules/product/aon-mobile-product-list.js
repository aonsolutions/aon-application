import {AonMobileList} from '../../components/aon-mobile-list.js';
import { MATERIAL_ICONS, TAG } from '../../environments/environments.js';
import {getProducts} from '../../services/service.js';

export class AonMobileProductList extends AonMobileList {

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
            getProducts(filter).then(products => {
                if(products.length == 0)
                    this.more = false;
                products.forEach((product, i) => this.addRow(product, i));
            });
        }
    }

    init() {
        this.build();
        getProducts(this.getFilter()).then(products => {
            if(products.length == 0){   
                this.empty();
            }
            products.forEach((product, i) => this.addRow(product, i));
        });        
    }

    addRow(product, i) {
        let liValue = {
            icon: MATERIAL_ICONS.LOCAL_MALL,
            title: product.name,
            subtitle: product.code
        }
        this.addLi(liValue, i, () => {});
    }
}
window.customElements.define('aon-mobile-product-list', AonMobileProductList);
