import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { getDeliveries, getDelivery } from '../../services/warehouseService.js';
import { AonMobileDelivery } from './aon-mobile-delivery.js';

import { createList } from '../../components/CreateComponent.js';
import * as OPTION from './DeliveryOptions.js';

export class AonDeliveryList extends AonElement {

    get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

    TABLE;
	more;
    filter;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.build();
    }

    moreFn = () => {
        if(this.more)
            this.loadMore()
    };

    disconnectedCallback() {
        this.getElement(this.TABLE).removeEventListener('more', this.moreFn);
    }

    initialize() {
        this.id = this.id || 'DeliveryList';
		this.TABLE = this.id + CONSTANT.TABLE.initCap();
        this.filter = this.filter || {
            page: 1,
            perPage: 30,
            status: 'IN_PREPARATION'
        };
        this.more = true;
    }

    

    aonDelivery(delivery, i) {
        let data = {
            id: delivery.id,
            full: true
        }
        getDelivery(data).then(r => {
            // let aonDelivery = new AonDelivery();
            let aonDelivery = new AonMobileDelivery();
            aonDelivery.setDelivery(r);
            this.getApplication().setContent(aonDelivery);
        });
    }
    

    setFilter(filter) {
        this.filter = filter;
    }

	build() {
		let table = createList(this.TABLE);
        table.selectable = 'true';
		this.appendChild(table);

		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
        btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);
        btnSearch.buildOptionsFilter(OPTION.DELIVERY_SEARCH_OPTIONS);

        this.getElement('status').setOptions([
            { name: "-", value: undefined },
            { name: MSG.PENDING, value: "PENDING" },
            { name: MSG.INVOICED, value: "INVOICED" },
            { name: MSG.IN_PREPARATION, value: "IN_PREPARATION" },
          ]);

		table.addColumn(MSG.DATE, 'date', 'date', '10%');
		table.addColumn('Número de Albarán', 'string', 'reference', '25%');
		table.addColumn(MSG.HOLDER, 'string', 'customerName', '35%');
		table.addColumn(MSG.STATUS, 'string', 'statusName', '20%');

		table.addEventListener(EVENT.MORE, this.moreFn);
		this.init();
	}

	search(detail) {
        let value = detail.search;
		if(detail.search) this.filter.value = value;
        if(detail.status) this.filter.status = detail.status;
        if(detail.startDate) this.filter.from = detail.startDate;
        if(detail.to) this.filter.to = detail.to;
		this.init();
	}

	init() {
		let table = document.getElementById(this.TABLE);
		if(table) {
            getDeliveries(this.filter).then(deliveries => {
                if(deliveries.length == 0){   
                    this.empty();
                }
                table.removeRows();

                deliveries.forEach((delivery, i) => {
                    delivery.customerName = delivery.customer.name;
                    delivery.total = 0.0;
                    delivery.statusName = this.getStatusName(delivery.status);
                    table.addRow(delivery, () => this.aonDelivery(delivery, i));
                });
            });    
		}
	}

	loadMore() {
		this.more = false;
		let table = this.getElement(this.TABLE);
        if(table && this.filter.page) {
            this.filter.page = this.filter.page + 1;
            getDeliveries(this.filter).then(deliveries => {
                if(deliveries.length == 0)
                    this.more = false;
                else this.more = true;
                deliveries.forEach((delivery, i) => {
                    delivery.customerName = delivery.customer.name;
                    delivery.total = 0.0;
                    delivery.statusName = this.getStatusName(delivery.status);
                    table.addRow(delivery, () => this.aonDelivery(delivery, i));
                });
            });
        }
	}

    getStatusName(status) {
        if(status == 'IN_PREPARATION') return 'En Preparación';
        else if(status == 'INVOICED') return "Facturado";
        else return MSG.PENDING;
     }

}

if(!window.customElements.get(TAG.AON_DELIVERY_LIST)) {
    window.customElements.define(TAG.AON_DELIVERY_LIST, AonDeliveryList);
}