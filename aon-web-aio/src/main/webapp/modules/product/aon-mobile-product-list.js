import {AonMobileList} from '../../components/aon-mobile-list.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, TAG } from '../../environments/environments.js';
import {getItem, getProducts} from '../../services/productService.js';
import {AonMobileProduct} from './aon-mobile-product.js';

export class AonMobileProductList extends AonMobileList {

    PER_PAGE = 30;

    more;

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
            this.loadMore();
    };

    disconnectedCallback() {
        this.removeEventListener(EVENT.MORE, this.moreFn);
    }

    initialize() {
        super.initialize();
        this.more = true;
    }

    loadMore() {
        let filter = this.getFilter();
        if(!filter.page) return;
        this.more = false;
        filter.page = filter.page + 1;
        super.setFilter(filter);
        getProducts(filter).then(products => {
            if(products.length > 0)
                this.more = true;
            products.forEach((product, i) => this.addRow(product, i));
        });
    }

    init() {
        let filter = this.getFilter();
        filter.page = 1;
        filter.perPage = filter.perPage || this.PER_PAGE;
        super.setFilter(filter);
        this.more = true;

        this.build();
        getProducts(filter).then(products => {
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
        this.addLi(liValue, i, () => this.aonProduct(product));
    }
    
    aonProduct(product) {
        if(product && product.id) {
            getItem({product: product.id}).then(item => {
                let aonProduct = new AonMobileProduct();
                aonProduct.setProduct(product);
                aonProduct.setItem(item);
                this.getApplication().setContent(aonProduct);
            });
        } else {
            this.getApplication().setContent(new AonMobileProduct());
        }
    }

    get filter() {
        return this.getFilter();
    }

    set filter(filter) {
        super.setFilter(filter);
    }

    getFilter() {
        if(!this.hasAttribute(CONSTANT.FILTER)) return {};
        try {
            return JSON.parse(this.getAttribute(CONSTANT.FILTER));
        } catch(e) {
            return {};
        }
    }

    setFilter(filter) {
        super.setFilter(filter);
        this.init();
    }
}
window.customElements.define('aon-mobile-product-list', AonMobileProductList);
