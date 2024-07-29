import {AonElement} from '../../../components/AonElement.js';
import { CSS, EVENT, MSG, TAG} from '../../../environments/environments.js';
import { AonTable } from '../../../components/aon-table.js';
import { getRSellers } from '../../../services/commercialService.js';

export class AonSellerSmallList extends AonElement {
	more;
	filter;
	parent;	
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

		this.buildTable();
		
		this.TABLE.addEventListener(EVENT.MORE, () => {
			if(this.more) {
				this.loadMore()
			}
		});

		this.init();
	}

	buildTable(){
		let div = this.createElement(TAG.DIV);
        div.className = CSS.AON_FLEX;
		div.style.position = "relative";
        this.appendChild(div);

		div.appendChild(this.TABLE);

		this.TABLE.addColumn(MSG.TYPE, 'string', 'rsellerType', '30%');
		this.TABLE.addColumn(MSG.NAME, 'string', 'sellerName', '70%');
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
					this.TABLE.addRow(item);
				});
			});
		}
	}

	init() {
		if(this.TABLE) {
			this.getData(this.getFilter())
			.then(items => {
				this.TABLE.removeRows();
				if((!items || items.length == 0) && this.parent){
					this.parent.style.display = "none";
				} else{
					if(this.parent) this.parent.style.display = "block";
					items.forEach((item) => {
						this.TABLE.addRow(item);
					});
				}
			});	
		}
	}

	async getData(filter){
		let rsellerTypePriority = {
			SOPORTE: 1,
			COMERCIAL: 2
		};

		try {
			const data = await getRSellers(filter);
			return data
			.filter(rseller => rseller.status === "ACTIVE" && (!rseller.end_date || (rseller.end_date && new Date(rseller.end_date) >= new Date())))
			.sort((a, b) => {
				// Primero compara por prioridad de statusText
				if (rsellerTypePriority[a.status] < rsellerTypePriority[b.status]) {
					return -1;
				}
				if (rsellerTypePriority[a.status] > rsellerTypePriority[b.status]) {
					return 1;
				}
				// Si el statusText es el mismo, compara alfabéticamente por sellerName
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

	setPatent(parent){
		this.parent = parent;
	}
	
}

if(!window.customElements.get("aon-seller-small-list")) {
	window.customElements.define("aon-seller-samll-list", AonSellerSmallList);
}
