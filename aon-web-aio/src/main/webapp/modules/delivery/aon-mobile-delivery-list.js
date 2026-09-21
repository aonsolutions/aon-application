import { AonMobileList } from '../../components/aon-mobile-list.js';
import { EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { getDeliveries, getDelivery } from '../../services/warehouseService.js';
import { AonMobileDelivery } from './aon-mobile-delivery.js';

import * as OPTION from './DeliveryOptions.js';

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
        this.buildSearch();
    }

    moreFn = () => {
        if(this.more)
            this.loadMore()
    };

    disconnectedCallback() {
        this.removeEventListener('more', this.moreFn);
    }

    initialize() {
        this.id = this.id || 'aonDeliveryList';
        super.initialize();
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

    buildSearch() {
        const btnSearch = this.getSearchButton();
        let searchFn = (event) => this.search(event.detail);
        btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);
        btnSearch.buildOptionsFilter(OPTION.DELIVERY_SEARCH_OPTIONS);

        this.getElement('status').setOptions([
             { name: "-", value: undefined },
             { name: MSG.PENDING, value: "PENDING" },
             { name: MSG.INVOICED, value: "INVOICED" },
             { name: MSG.IN_PREPARATION, value: "IN_PREPARATION" }
        ]);
    }

    getSearchButton() {
        return this.getApplication().addSearchOption();
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
            this.setContent(aonDelivery);
        });
    }

    setContent = (content) => {
        this.getApplication().setContent(content);
    }
    
    search(detail) {
        let value = detail.search;
		if(detail.search) this.filter.value = value;
        if(detail.status) this.filter.status = detail.status;
        if(detail.startDate) this.filter.from = detail.startDate;
        if(detail.to) this.filter.to = detail.to;
		this.init();
	}

    setFilter(filter) {
        this.filter = filter;
    }

}

if(!window.customElements.get(TAG.AON_MOBILE_DELIVERY_LIST)) {
    window.customElements.define(TAG.AON_MOBILE_DELIVERY_LIST, AonMobileDeliveryList);
}