import {AonElement} from '../../../../components/AonElement.js';
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG} from '../../../../environments/environments.js';
import { AonTable } from '../../../../components/aon-table.js';
import { getItems, getRItems } from '../../../../services/productService.js';
import { BookingItemStatus, RegistryItemStatus } from '../../../../models/product/RegistryItemStatus.js';
import { AonBookingItemAdd } from './aon-booking-item-add.js';
import { Item } from '../../../../models/product/Item.js';
import { AonItemAdd } from './aon-item-add.js';

export class AonItemList extends AonElement {
	more;
	filter;	
	TABLE;

	connectedCallback () {
		this.initialize();
		this.build();
	}

	initialize() {
		this.more = true;
		this.holders = [];
		this.filter = this.filter || {
			page: 1,
			perPage: 50		
		}

	}

	build() {
		this.TABLE = new AonTable();
		this.TABLE.id = 'aonItemListTable';
		this.TABLE.selectedColor = true;

		this.TABLE.style.width = "100%";

		if(this.registry){
			this.buildTableIsRegistry();
		} else {
			this.buildTable();
		}
		
		this.TABLE.addEventListener(EVENT.MORE, () => {
			if(this.more) {
				this.loadMore()
			}
		});

		this.init();
	}

	buildTable(){
		this.appendChild(this.TABLE);
		this.TABLE.addColumn(MSG.CODE, 'string', 'productCode', '30%');
		this.TABLE.addColumn(MSG.NAME, 'string', 'productName', '30%');
		this.TABLE.addColumn(MSG.CATEGORY, 'string', 'productCategory', '20%');
		this.TABLE.addColumn(MSG.STATUS, 'string', 'statusText', '10%');
	}

	buildTableIsRegistry(){
		let div = this.createElement(TAG.DIV);
        div.className = CSS.AON_FLEX;
		div.style.position = "relative";
        this.appendChild(div);

		div.appendChild(this.TABLE);
		this.TABLE.addColumn(MSG.CODE, 'string', 'productCode', '22.5%');
		this.TABLE.addColumn(MSG.NAME, 'string', 'productName', '22.5%');
		this.TABLE.addColumn(MSG.START_DATE, 'date', 'startDate', '22.5%');
		this.TABLE.addColumn(MSG.END_DATE, 'date', 'endDate', '22.5%');
		this.TABLE.addColumn(MSG.STATUS, 'string', 'statusText', '10%');
		this.TABLE.addColumn("", 'icon', 'icon', '5%');
		this.TABLE.addColumnIcon({title:MSG.ADD+" "+MSG.PRODUCTS, name:MATERIAL_ICONS.ADD, type:"string", width:"5%", id:"option"}, 
		()=>{
			this.openDialogItems();
		});
	}

	loadMore() {
		this.more = false;
		if(this.TABLE && this.filter.page) {
			this.filter.page = this.filter.page + 1;
			this.getData(this.filter)
			.then(items => {
				if(items.length > 0)
					this.more = true;
				items.forEach((item) => {
					this.buildIconRemove(item);
					this.TABLE.addRow(item, () => {
						this.openDialogItems(item);
					});
				});
			});
		}
	}

	init() {
		if(this.TABLE) {
			this.getData(this.getFilter())
			.then(items => {
				this.TABLE.removeRows();
				items.forEach((item) => {
					this.buildIconRemove(item);
					this.TABLE.addRow(item, () => {
						this.openDialogItems(item);
					});
				});
			});	
		}
	}

	buildIconRemove(res){
		res.icon = MATERIAL_ICONS.DELETE;
		res.icon_color = "grey";
		res.fn = () => this.onRemoveItem(res);
	}

	async getData(filter){
		try {
			const data = await getRItems(filter);
			return data
			.map(ritem =>{
				let p = ritem.item;
				p.productName = p.product.name;
				p.productCode = p.product.code;
				p.startDate = ritem.start_date;
				p.endDate = ritem.end_date;
				p.statusText = RegistryItemStatus.getText(ritem.status);
				p.ritem = JSON.parse(JSON.stringify(ritem)); // clone object
				return p;
			});
		} catch (error) {
			this.showError(error);
		}
		return [];
	}

	getFilter() {
		return this.filter || {};
	}

	setFilter(filter) {
		this.filter = filter;
		this.init();
	}
	

	openDialogItems(selectedItem){
		const application = this.getApplication();
		const dialog = application.getDialog();
		dialog.autoclose = false;
		dialog.width = '40%';
		dialog.clear();
		dialog.setTitle(MSG.ASSIGN + " " + MSG.CONTRACTED_PRODUCTS);
	
		const aonTargetItemAdd = new AonItemAdd();
		if (selectedItem) {
			aonTargetItemAdd.setSelectedRItem(selectedItem.ritem);
		}
	
		dialog.setContent(aonTargetItemAdd);
		
		dialog.addSendAction(async()=>{
			aonTargetItemAdd.addCustomer(this.registry);
	
			application.startLoading();
	
			await aonTargetItemAdd.save().catch(err=> this.showError(err));

			this.init();

			dialog.close();
			application.stopLoading();
	
		}, MSG.SAVE);
	
		dialog.open();
	}

	onRemoveItem(item) {
		this.getApplication().confirmDialog(MSG.DELETE, MSG.DELETE_CONFIRM, async ()=>{
			this.getApplication().startLoading();

			try {
				const aonTargetItemAdd = new AonItemAdd();

				aonTargetItemAdd.addCustomer(this.registry);
				aonTargetItemAdd.addItem(item);

				await aonTargetItemAdd.remove(item.id);

				this.showToast({ message: MSG.DELETED_DATA });

				this.init();

			} catch (error) {
				console.log(error);
				this.showError(error);
			}

			this.getApplication().stopLoading();	
		});
	}

}

if(!window.customElements.get("aon-item-list")) {
	window.customElements.define("aon--item-list", AonItemList);
}
