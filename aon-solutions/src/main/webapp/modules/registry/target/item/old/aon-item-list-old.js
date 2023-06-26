import {AonElement} from '../../../../../components/AonElement.js';
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG} from '../../../../../environments/environments.js';
import { AonTable } from '../../../../../components/aon-table.js';
import { getItems } from '../../../../../services/productService.js';
import { AonTargetItemAdd } from './aon-target-item-add-old.js';
import { RegistryItemStatus } from '../../../../../models/product/RegistryItemStatus.js';

export class AonItemListOld extends AonElement {
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
		this.TABLE.id = 'aoItemListTable';
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
		this.TABLE.addColumn(MSG.CODE, 'string', 'productCode', '30%');
		this.TABLE.addColumn(MSG.NAME, 'string', 'productName', '30%');
		this.TABLE.addColumn(MSG.CATEGORY, 'string', 'productCategory', '30%');
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
					this.TABLE.addRow(item, () => {});
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
					this.TABLE.addRow(item, () => {});
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
			const data = await getItems(filter);
			return data
			.map(p =>{
				p.productName = p.product.name;
				p.productCode = p.product.code;
				p.productCategory = (p.product.category && p.product.category.name)  ? p.product.category.name : "";
				
				p.statusText = RegistryItemStatus.getText(p.status);

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
	

	openDialogItems(){
		const application = this.getApplication();
		const dialog = application.getDialog();
		dialog.autoclose = false;
		dialog.width = '40%';
		dialog.clear();
		dialog.setTitle(MSG.ASSIGN + " " + "Productos comerciales");
	
		const aonTargetItemAdd = new AonTargetItemAdd();
	
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
				const aonTargetItemAdd = new AonTargetItemAdd();

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

if(!window.customElements.get("aon-item-list-old")) {
	window.customElements.define("aon-item-list-old", AonItemListOld);
}
