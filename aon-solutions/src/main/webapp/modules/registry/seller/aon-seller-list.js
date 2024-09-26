import {AonElement} from '../../../components/AonElement.js';
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG} from '../../../environments/environments.js';
import { AonTable } from '../../../components/aon-table.js';
import { getSellers, getRSellers } from '../../../services/commercialService.js';
import { AonSellerAdd } from './aon-seller-add.js';
import { AonItemAdd } from '../target/item/aon-item-add.js';
import * as ACTION from '../../actions.js';

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
					item.option = this.getOptions(item);
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
					item.option = this.getOptions(item);
					this.TABLE.addRow(item, () => {
						// this.openDialogItems(item);
					});
				});
			});	
		}
	}

	getOptions(res) {
		let option = [
			{
				...ACTION.EDIT,
				fn: () => this.openDialogItems(res)
			},
			{
				...ACTION.DELETE,
				fn: () => this.onRemoveItem(res)
			}
		];

		return option;
	}

	async getData(filter){
		let rsellerTypePriority = {
			SOPORTE: 1,
			COMERCIAL: 2
		};

		try {
			const data = await getRSellers(filter);
			return data
			.sort((a, b) => {
				// Primero compara por prioridad de statusText
				if (rsellerTypePriority[a.type] < rsellerTypePriority[b.type]) {
					return -1;
				}
				if (rsellerTypePriority[a.type] > rsellerTypePriority[b.type]) {
					return 1;
				}
				
				// Si el statusText es el mismo, compara por endDate
				if (a.end_date == null && b.end_date != null) {
					return -1;
				}
				if (a.end_date != null && b.end_date == null) {
					return 1;
				}
				if (a.end_date != null && b.end_date != null) {
					if (new Date(a.end_date) > new Date(b.end_date)) {
					return -1;
					}
					if (new Date(a.end_date) < new Date(b.end_date)) {
					return 1;
					}
				}

				// Si el statusText y el endDate son los mismos, compara alfabéticamente por sellerName
				return a.seller.name.localeCompare(b.seller.name);
			})
			.map(rseller =>{
				let s = rseller.seller;
				s.sellerName = s.name;
				s.startDate = rseller.start_date;
				s.endDate = rseller.end_date;
				s.statusText = rseller.status;
				s.rsellerType = rseller.type;
				s.rsellerId = rseller.id;
				s.rseller = JSON.parse(JSON.stringify(rseller)); // clone object
				s.color = this.getColor(rseller);
				return s;
			});
		} catch (error) {
			this.showError(error);
		}
		return [];
	}

	getColor(rseller){
		if(rseller.status == "ACTIVE" && (rseller.end_date && new Date(rseller.end_date) <= new Date()))
			return "orange";
		else
			return "black";
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
		dialog.setTitle(MSG.ASSIGN + " Agente");
	
		const aonTargetItemAdd = new AonSellerAdd();
		if (selectedItem) {
			aonTargetItemAdd.setSelectedRSeller(selectedItem);
		} else {
			aonTargetItemAdd.setSelectedRSeller({
				rseller : {
					registry : this.registry.id
				},
				statusText : "ACTIVE"
			});
		}
	
		dialog.setContent(aonTargetItemAdd);
		
		dialog.addSendAction(async()=>{
			aonTargetItemAdd.addCustomer(this.registry);
	
			application.startLoading();
	
			await aonTargetItemAdd.save().then(success => {
				if(success){
					this.init();
					dialog.close();
				}
			}).catch(err=> this.showError(err));

			application.stopLoading();
	
		}, MSG.SAVE);
	
		dialog.open();
	}

	onRemoveItem(item) {
		this.getApplication().confirmDialog(MSG.DELETE, MSG.DELETE_CONFIRM + " el agente " + item.rseller.seller.name, async ()=>{
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
