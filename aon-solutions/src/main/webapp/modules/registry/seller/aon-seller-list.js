import {AonElement} from '../../../components/AonElement.js';
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG} from '../../../environments/environments.js';
import { AonTable } from '../../../components/aon-table.js';
import { getSellers, getRSellers } from '../../../services/commercialService.js';
import { AonSellerAdd } from './aon-seller-add.js';
import { AonItemAdd } from '../target/item/aon-item-add.js';

export class AonSellerList extends AonElement {
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
		this.TABLE.id = 'aonSellerListTable';
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
		// this.TABLE.addColumn(MSG.CODE, 'string', 'productCode', '30%');
		this.TABLE.addColumn(MSG.NAME, 'string', 'sellerName', '30%');
		this.TABLE.addColumn(MSG.CATEGORY, 'string', 'rsellerType', '20%');
		this.TABLE.addColumn(MSG.STATUS, 'string', 'statusText', '10%');
	}

	buildTableIsRegistry(){
		let div = this.createElement(TAG.DIV);
        div.className = CSS.AON_FLEX;
		div.style.position = "relative";
        this.appendChild(div);

		div.appendChild(this.TABLE);

		this.TABLE.addColumn(MSG.TYPE, 'string', 'rsellerType', '22.5%');
		this.TABLE.addColumn(MSG.NAME, 'string', 'sellerName', '22.5%');
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
						// this.openDialogItems(item);
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
						// this.openDialogItems(item);
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
			const data = await getRSellers(filter);
			return data
			.map(rseller =>{
				let s = rseller.seller;
				s.sellerName = s.name;
				s.startDate = rseller.start_date;
				s.endDate = rseller.end_date;
				s.statusText = rseller.status;
				s.rsellerType = rseller.type;
				s.rsellerId = rseller.id;
				s.rseller = JSON.parse(JSON.stringify(rseller)); // clone object
				return s;
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
	
		const aonTargetItemAdd = new AonSellerAdd();
		if (selectedItem) {
			aonTargetItemAdd.setSelectedRSeller(selectedItem.ritem);
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
				const aonSellerAdd = new AonSellerAdd();

				aonSellerAdd.addCustomer(this.registry);
				aonSellerAdd.addSeller(item);

				await aonSellerAdd.remove(item.rsellerId);

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

if(!window.customElements.get("aon-seller-list")) {
	window.customElements.define("aon--seller-list", AonSellerList);
}
