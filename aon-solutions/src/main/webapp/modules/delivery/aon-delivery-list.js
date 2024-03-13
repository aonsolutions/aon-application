import { AonElement } from '../../components/AonElement.js';
import { AonTable } from '../../components/aon-table.js';

import { CONSTANT, EVENT, MSG, MATERIAL_ICONS, TAG } from '../../environments/environments.js';
import { getDeliveries, getDelivery } from '../../services/warehouseService.js';
import { AonMobileDelivery } from './aon-mobile-delivery.js';
import { WAREHOUSE } from '../../services/app.js';
import { AonDelivery } from './aon-delivery.js';

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
            let aonDelivery = new AonDelivery();
            aonDelivery.setDelivery(r);
            this.getApplication().setContent(aonDelivery);
        });
    }
    

    setFilter(filter) {
        this.filter = filter;
    }

	build() {
		let table = this.createAonElement(new AonTable(), this.TABLE);
		table.setApp(WAREHOUSE);
        table.selectable = 'true';
		this.appendChild(table);

		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH, searchFn);

		table.addColumn(MSG.DATE, 'date', 'date', '10%');
		table.addColumn('Número de Albarán', 'string', 'reference', '25%');
		table.addColumn(MSG.HOLDER, 'string', 'customerName', '35%');
		table.addColumn(MSG.STATUS, 'string', 'statusName', '20%');

		table.addEventListener(EVENT.MORE, this.moreFn);
		this.init();
	}

	search(value) {
		this.filter.value = value;
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
        if(tanñe && this.filter.page) {
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