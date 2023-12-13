import { AonMobileList } from '../../components/aon-mobile-list.js';
import { MATERIAL_ICONS, TAG } from '../../environments/environments.js';
import { getDeliveries, getDelivery } from '../../services/warehouseService.js';
import { AonMobileDelivery } from './aon-mobile-delivery.js';

export class AonMobileDeliveryList extends AonMobileList {
	more;
    filter;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.init();
        this.addEventListener('more', this.moreFn);
    }

    moreFn = () => {
        if(this.more)
            this.loadMore()
    };

    disconnectedCallback() {
        this.removeEventListener('more', this.moreFn);
    }

    initialize() {
        this.filter = this.filter || {
            page: 1,
            perPage: 30,
            status: 'IN_PREPARATION'
        };
        this.more = true;
    }

    loadMore() {
        let filter = this.filter;
        if(filter.page) {
            filter.page = filter.page + 1;
            this.setFilter(filter);
            getDeliveries(filter).then(deliveries => {
                if(deliveries.length == 0)
                    this.more = false;
                deliveries.forEach((delivery, i) => this.addRow(delivery, i));
            });
        }
    }

    init() {
        this.build();
        getDeliveries(this.filter).then(deliveries => {
            if(deliveries.length == 0){   
                this.empty();
            }
            deliveries.forEach((delivery, i) => this.addRow(delivery, i));
        });        
    }

    addRow(delivery, i) {
        let liValue = {
            icon: MATERIAL_ICONS.LOCAL_SHIPPING,
            title: delivery.reference,
            subtitle: delivery.customer.name
        }
        this.addLi(liValue, i, () => this.aonDelivery(delivery, i));
    }

    aonDelivery(delivery, i) {
        let data = {
            id: delivery.id,
            full: true
        }
        getDelivery(data).then(r => {
            let aonDelivery = new AonMobileDelivery();
            aonDelivery.setDelivery(r);
            this.getApplication().setContent(aonDelivery);
        });
    }
    

    setFilter(filter) {
        this.filter = filter;
    }

}

if(!window.customElements.get(TAG.AON_MOBILE_DELIVERY_LIST)) {
    window.customElements.define(TAG.AON_MOBILE_DELIVERY_LIST, AonMobileDeliveryList);
}