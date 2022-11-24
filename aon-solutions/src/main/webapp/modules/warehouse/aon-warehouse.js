import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { AonMobileElaborationList } from './elaboration/aon-mobile-elaboration-list.js';
import Apps from '../../services/app.js';
import {WarehouseSidenav, ELABORATION, PACKAGING, TAGS } from './WarehouseOptions.js';
import { AonMobilePackaging } from './packaging/aon-mobile-packaging.js';
import * as ACTION from '../actions.js';
import { getWarehouses } from '../../services/warehouseService.js';
import { AonDeliveryTag } from './deliveryTag/aon-delivery-tag.js';

export class AonWarehouse extends AonElement {

	WAREHOUSE;
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
		this.getApplication().addSidenavOptions3(WarehouseSidenav.WAREHOUSES, () => this.getApplication().development(MSG.NEW_WAREHOUSE));
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
							action: () => this.getApplication().development(MSG.DELETE_WAREHOUSE)
						},{
							id: 'Edit',
							icon: 'edit',
							action: () => this.getApplication().development(MSG.NEW_WAREHOUSE)
				  		}
					]
				};
				this.getApplication().addSidenavOptionsListValue(DocumentalSidenav.CATEGORIES, option);
			});
		});
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
		case TAGS.id:
			this.aonDeliveryTag();
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
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
	}

	aonPackaging() {
		this.getApplication().removeFloatOption();
		this.getApplication().setContent(new AonMobilePackaging());
	}

	aonDeliveryTag() {
		this.getApplication().removeFloatOption();
		this.getApplication().setContent(new AonDeliveryTag());
	}

}
if(!window.customElements.get(TAG.AON_WAREHOUSE)){
	window.customElements.define(TAG.AON_WAREHOUSE, AonWarehouse);
}
