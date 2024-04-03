import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { AonMobileElaborationList } from './elaboration/aon-mobile-elaboration-list.js';
import Apps from '../../services/app.js';
import {WarehouseSidenav, ELABORATION, PACKAGING,  DELIVERY, TAGS, CARRIER } from './WarehouseOptions.js';
import { AonMobilePackaging } from './packaging/aon-mobile-packaging.js';
import * as ACTION from '../actions.js';
import { deleteWarehouse, getDelivery, getWarehouses, saveWarehouse } from '../../services/warehouseService.js';
import { AonDeliveryTag } from './deliveryTag/aon-delivery-tag.js';
import { AonMobileDeliveryList } from '../delivery/aon-mobile-delivery-list.js';
import { AonMobileDelivery } from '../delivery/aon-mobile-delivery.js';
import { AonDeliveryList } from '../delivery/aon-delivery-list.js';
import { AonCarrierList } from '../registry/carrier/aon-carrier-list.js';
import { AonMobileCarrierList } from '../registry/carrier/aon-mobile-carrier-list.js';
import { AonNewInput } from '../../components/aon-new-input.js';
import { AonNewSelect } from '../../components/aon-new-select.js';
import { AonSwitch } from '../../components/aon-switch.js';
import { getWorkplaces } from '../../services/workplaceService.js';
import * as LS from '../../services/localStorageService.js';

export class AonWarehouse extends AonElement {

	WAREHOUSE;
	WAREHOUSE_EDIT_DIV;
	WAREHOUSE_EDIT_NAME;
	WAREHOUSE_EDIT_WORKPLACE;
	WAREHOUSE_EDIT_ACTIVE;
	option;

	constructor () {
		super();
	}

	connectedCallback() {
		this.initialize();
    	this.build();
 	}

	initialize() {
		this.WAREHOUSE = 'aonWarehouse';
		this.WAREHOUSE_EDIT_DIV = this.WAREHOUSE + 'EditDiv';
		this.WAREHOUSE_EDIT_NAME = this.WAREHOUSE_EDIT_DIV + CONSTANT.NAME.initCap();
		this.WAREHOUSE_EDIT_WORKPLACE = this.WAREHOUSE_EDIT_DIV + CONSTANT.WORKPLACE.initCap();
		this.WAREHOUSE_EDIT_ACTIVE = this.WAREHOUSE_EDIT_DIV + CONSTANT.ACTIVE.initCap();
		this.option = this.option || ELABORATION;
	}

 	build() {
		this.createApplication(this.WAREHOUSE, MSG.WAREHOUSE, new AonApplication());
		this.buildSidenav();
		this.selectOption(this.option);
	}

	buildSidenav() {
		if(this.isMobile()){
			this.getApplication().addMobileSidenavHeader(Apps.WAREHOUSE);
		}
		this.getApplication().addEventListener(EVENT.SELECT_OPTION, 
			(e) => this.selectOption(e.detail));

		if(!this.isMobile()) this.buildWarehouseOptions();
		this.buildElaborationOptions();
	}

	buildWarehouseOptions() {
		this.getApplication().addSidenavOptions3(WarehouseSidenav.WAREHOUSES, () => this.buildWarehouse());
		this.loadWarehouses();
	}

	loadWarehouses() {
		getWarehouses().then(warehouses => {
			this.clearElementById(this.getApplication().SIDENAV + WarehouseSidenav.WAREHOUSES.id + 'List');
			warehouses.forEach((warehouse, i) => {
				let option = {
				  name: warehouse.name,
				  icon: MATERIAL_ICONS.WAREHOUSE,
				  actions: [
					  	{	
							id: 'Delete',
							icon: 'delete',
							action: () => this.removeWarehouse(warehouse)
						},{
							id: 'Edit',
							icon: 'edit',
							action: () => this.buildWarehouse(warehouse)
				  		}
					]
				};
				this.getApplication().addSidenavOptionsListValue(WarehouseSidenav.WAREHOUSES, option);
			});
		});
	}

	buildWarehouse(warehouse) {
		let d = document.getElementById(this.getApplication().DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(warehouse ? MSG.NEW_WAREHOUSE : MSG.EDIT_WAREHOUSE);
		if(!warehouse) warehouse = {domain: LS.getDomainId(), active: true};
		let div = this.createDiv(this.WAREHOUSE_EDIT_DIV);

		let warehouseName = new AonNewInput();
		warehouseName.id = this.WAREHOUSE_NAME;
		warehouseName.title = MSG.WAREHOUSE;
		warehouseName.value = warehouse && warehouse.name 
			? warehouse.name : CONSTANT.EMPTY;
		warehouseName.addEventListener(EVENT.CHANGE, () => {
			warehouse.name = warehouseName.value;
		});
		
		div.appendChild(warehouseName);

		let warehouseWorkplace = new AonNewSelect();
		warehouseWorkplace.id = this.WAREHOUSE_EDIT_WORKPLACE;
		warehouseWorkplace.title = MSG.WORKPLACE;
		warehouseWorkplace.autocomplete = true;
		warehouseWorkplace.default = true;
		warehouseWorkplace.addEventListener(EVENT.SELECT, () => {
			warehouse.workplace = warehouseWorkplace.value;
		});

		getWorkplaces().then(r => {
			let workplaces = r.map(w => {return {name: w.description, value: w.id};});
			warehouseWorkplace.options = JSON.stringify(workplaces);
			if(warehouse.workplace) warehouseWorkplace.value = warehouse.workplace;
		});

		div.appendChild(warehouseWorkplace);

		d.setContent(div);
		d.addAcceptAction(() => {
			saveWarehouse(warehouse).then(r => {
				this.loadWarehouses();
			}).catch(e => {
				this.showError(e)
			});
		});
		d.open();
	}

	removeWarehouse(warehouse) {
		let aonDocumental = this.getApplication();
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE);
		d.setContentHTML(`Estás seguro de eliminar el Almacén ${warehouse.name}`);
		d.addAcceptAction(() => {
			deleteWarehouse(warehouse.id).then(() => {
				this.loadWarehouses();
			}).catch(e => {
				this.showError(e)
			});
		});
		d.open();

	}

	buildElaborationOptions() {
		this.getApplication().addSidenavOptions3(WarehouseSidenav.ELABORATION);
	}

	selectOption(option) {
		switch(option.id){
		case ELABORATION.id:
			this.aonElaboration();
			break;
		case PACKAGING.id:
			this.aonPackaging();
			break;
		case DELIVERY.id:
			this.aonDelivery(option);
			break;
		case TAGS.id:
			this.aonDeliveryTag();
			break;
		case CARRIER.id:
			this.aonCarriers();
			break;
		default:
			this.aonElaboration();
			break;
		}
	}

	setOption(option) {
		this.option = option;
	}

	aonElaboration() {
		this.getApplication().getToolbar().option = MSG.ELABORATION;
		this.getApplication().removeFloatOption();
		this.getApplication().addFloatOption(ACTION.ADD, () => this.addElaboration());
		this.getApplication().setContent(new AonMobileElaborationList());
	}
	
	addElaboration(){
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile())d.width = '400px';
		d.setTitle(MSG.NEW_ELABORATION);
		d.setContentHTML(MSG.IN_DEVELOPMENT);
		d.addAcceptAction(() => {});
		d.open();
	}

	aonPackaging() {
		this.getApplication().removeFloatOption();
		this.getApplication().setContent(new AonMobilePackaging());
	}

	aonDelivery(option) {
		this.getApplication().getToolbar().option = MSG.DELIVERY;
		this.getApplication().removeFloatOption();
		if(option && option.delivery) {
			let data = {
				id: option.delivery,
				full: true
			}
			getDelivery(data).then(r => {
				let aonDelivery = new AonMobileDelivery();
				aonDelivery.setDelivery(r);
				this.getApplication().setContent(aonDelivery);
			});
		} else this.getApplication().setContent(new AonMobileDeliveryList());
			// this.isMobile()
			// ? new AonMobileDeliveryList() : new AonDeliveryList());
	}

	aonDeliveryTag() {
		this.getApplication().removeFloatOption();
		this.getApplication().setContent(new AonDeliveryTag());
	}

	aonCarriers() {
		this.getApplication().removeFloatOption();
		this.getApplication().setContent(this.isMobile()
			? new AonMobileCarrierList() : new AonCarrierList());
	}

}
if(!window.customElements.get(TAG.AON_WAREHOUSE)){
	window.customElements.define(TAG.AON_WAREHOUSE, AonWarehouse);
}
