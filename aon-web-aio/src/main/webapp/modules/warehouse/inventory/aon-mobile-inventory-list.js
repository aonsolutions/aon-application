import { AonMobileList } from '../../../components/aon-mobile-list.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../../environments/environments.js';
import { getInventories, getInventory, getWarehouses } from '../../../services/warehouseService.js';

import '../../../css/paturpat.css';
import { inventoryStatuses } from './InventoryStatus.js';
import { AonMobileInventory } from './aon-mobile-inventory.js';

export class AonMobileInventoryList extends AonMobileList {

    more;
    filtro;

    constructor () {
        super();
    }

    connectedCallback() {
        this.initialize();
        this.init();
        this.addEventListener(EVENT.MORE, this.moreFn);
        this.buildSearch();
    }

    moreFn = () => {
        if(this.more)
            this.loadMore()
    };


    disconnectedCallback() {
        this.removeEventListener(EVENT.MORE, this.moreFn);
    }

    initialize() {
        this.id = this.id || 'aonMobileInventoryList';
        super.initialize();
        this.more = true;
        this.filtro = this.filtro || {
            page:1,
            perPage:30,
            status: CONSTANT.OPENED,
        };
    }

	buildSearch(){
		const btnSearch = this.getSearchButton();
		let searchFn = (event) => this.search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);

		getWarehouses().then(warehouses => {
			let options = [{
				type: CONSTANT.DATE,
				name: "startDate",
				id: "startDate",
				title: MSG.FROM,
			  }, {
				type: CONSTANT.DATE,
				name: "endDate",
				id: "endDate",
				title: MSG.TO,
			  }, {
				type: CONSTANT.SELECT,
				name: "status",
				id: "status",
				title: MSG.STATUS,
				options: JSON.stringify(inventoryStatuses)
			  }];
			if(warehouses.length > 1) {
				options.push({
					type: CONSTANT.SELECT,
					name: "warehouse",
					id: "warehouse",
					title: MSG.WAREHOUSE,
					options: JSON.stringify(warehouses.map(r => {
						let w = {
							value: r.id,			
							name: r.name
						};
						return w;
					}))
				})
			}
            btnSearch.buildOptionsFilter(options);
		});
    }

    getSearchButton() {
        return this.getApplication().addSearchOption();
    }

	search(detail) {
		this.filtro.value = detail.search;
		this.filtro.warehouse = detail.warehouse;
		this.filtro.from = detail.startDate;
		this.filtro.to = detail.endDate; 
		this.filtro.status = detail.status;
		this.filtro.page = 1;
		this.filtro.perPage = 30;
		this.init();
	}

    loadMore() {
        if(this.filtro.page) {
            this.filtro.page = this.filtro.page + 1;
            getInventories(this.filtro).then(inventories => {
                if(inventories.length == 0)
                    this.more = false;
                inventories.forEach((inventory, i) => this.addRow(inventory, i));
            });
        }
    }

    init() {
        this.build();
        getInventories(this.filtro).then(inventories => {
            if(inventories.length == 0){   
                this.empty();
            }
            inventories.forEach((inventory, i) => this.addRow(inventory, i));
        });        
    }

    addRow(inventory, i) {
        let liValue = {
            icon: MATERIAL_ICONS.INVENTORY,
            title: inventory.description,
            subtitle: ""
        }
        this.addLi(liValue, i, () => this.aonInventory(inventory, i));
    }

    aonInventory(inventory, i) {
        getInventory(inventory.id).then(el => {
            let aonInventory = new AonMobileInventory();
            aonInventory.setInventory(el);
            this.setContent(aonInventory);
        });
    }

    setContent = (content) => {
        this.getApplication().setContent(content);
    }
}
if(!window.customElements.get(TAG.AON_MOBILE_INVENTORY_LIST)){
    window.customElements.define(TAG.AON_MOBILE_INVENTORY_LIST, AonMobileInventoryList);
}