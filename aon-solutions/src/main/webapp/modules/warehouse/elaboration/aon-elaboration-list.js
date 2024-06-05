import { AonElement } from '../../../components/AonElement.js';
import { AonTable } from '../../../components/aon-table.js';
import { CONSTANT, MATERIAL_ICONS, MSG, TAG, EVENT } from '../../../environments/environments.js';
import { WAREHOUSE } from '../../../services/app.js';
import { getElaboration, getElaborations, getWarehouses } from '../../../services/warehouseService.js';
import { AonMobileElaboration } from './aon-mobile-elaboration.js';
import { addElements, setElements } from './ElementCache.js';
import { elaborationStatuses } from '../../../models/elaboration/elaborationStatus.js';

export class AonElaborationList extends AonElement {

	more;
	filter;
	TABLE;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor () {
		super();
		this.more = true;
	}

	connectedCallback () {
		this.initialize();
		this.buildDur().then(() => this.build());
 	}

	initialize() {
		this.id = this.id || 'aonElaborationList';
		this.TABLE = this.id + 'Table';
		this.filter = this.filter || {
			page: 1,
			perPage: 50
		};
	}

 	build() {
		let aonTable = new AonTable();
		aonTable.id = this.TABLE;
		aonTable.selectable = 'true';
		aonTable.setApp(WAREHOUSE);
		this.appendChild(aonTable);
		aonTable.addColumn(MSG.DATE, 'date', 'dateTable', '10%');
		aonTable.addColumn(MSG.REFERENCE, 'string', 'reference', '15%');
		aonTable.addColumn(MSG.DESCRIPTION, 'string', 'description', '35%');
		aonTable.addColumn(MSG.QUANTITY, 'number', 'quantity', '10%');
		aonTable.addColumn(MSG.WAREHOUSE, 'string', 'warehouseName', '20%');
		aonTable.addColumn('', 'icons', 'icons', '10%');

		this.init();
		aonTable.addEventListener('more', () => {
			if(this.more)
				this.loadMore();
		});

		aonTable.addEventListener('select', () => {
			if(aonTable.selected.length === 1) {
				this.addElaborationActions();
			} else if(aonTable.selected.length === 0){
				this.removeElaborationActions();
			}
		});
		this.buildSearch();
	}

	buildSearch(){
		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);

		getWarehouses().then(warehouses => {
			let options = [{
				type: CONSTANT.DATE,
				name: "startDate",
				id: "startDate",
				title: MSG.FROM,
			  },
			  {
				type: CONSTANT.DATE,
				name: "endDate",
				id: "endDate",
				title: MSG.TO,
			  },{
				type: CONSTANT.SELECT,
				name: "status",
				id: "status",
				title: MSG.STATUS,
				options: JSON.stringify(elaborationStatuses)
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

	search(detail) {
		this.filter.value = detail.search;
		this.filter.warehouse = detail.warehouse;
		this.filter.from = detail.startDate;
		this.filter.to = detail.endDate; 
		this.filter.status = detail.status;
		this.filter.page = 1;
		this.filter.perPage = 50;
		this.init();
	}

	loadMore() {
		let aonTable = document.getElementById(this.TABLE);
		if(aonTable && this.filter.page) {
			this.filter.page = this.filter.page + 1;
			getElaborations(this.filter).then(elaborations => {
				if(elaborations.length == 0)
					this.more = false;
				addElements(elaborations);
				elaborations.forEach((elaboration, i) => {
					let date = new Date(elaboration.date);
					let day = date.getDate();
					let month = date.getMonth() + 1;
					let year = date.getFullYear();
					elaboration.dateTable = day + '/' + month + '/' + year;
					elaboration.warehouseName = elaboration.warehouse
						? elaboration.warehouse.name : '';
					elaboration.icons = this.buildRowIcons(elaboration); 
					aonTable.addRow(elaboration, () => this.aonElaboration(elaboration, i), (e) => {});
				});
			});
		}
	}

	init() {
		this.more = true;
		let aonTable = this.getElement(this.TABLE);
		if(aonTable) {
			getElaborations(this.getFilter()).then(elaborations => {
				setElements(elaborations);
			
				aonTable.removeRows();
				aonTable.selected = [];
				this.removeElaborationActions();
				elaborations.forEach((elaboration, i) => {
					let date = new Date(elaboration.date);
					let day = date.getDate();
					let month = date.getMonth() + 1;
					let year = date.getFullYear();
					elaboration.dateTable = day + '/' + month + '/' + year;
					elaboration.warehouseName = elaboration.warehouse
						? elaboration.warehouse.name : '';
					elaboration.icons = this.buildRowIcons(elaboration); 
					aonTable.addRow(elaboration, () => this.aonElaboration(elaboration, i), (e) => {});
				});
			});
		}
	}

	buildRowIcons(elaboration) {
		let icons = [];

		let icon = {
			icon: MATERIAL_ICONS.CIRCLE,
			title: "ESTADO",
			color: "gray"
		};
		icons.push(icon);
		
		return icons;
	}
	
	addElaborationActions() {

	}

	removeElaborationActions() {

	}

    aonElaboration(elaboration, i) {
        getElaboration(elaboration.id).then(elaboration => {
			setIndex(i);
			let aonElaboration = new AonMobileElaboration();
            aonElaboration.setElaboration(elaboration);
            this.getApplication().setContent(aonElaboration);
        });
    }

	getFilter() {
		return this.filter || {		
			page: 1,
			perPage: 50
		};
	}

	setFilter(filter) {
		this.filter = filter;
	}
}
if(!window.customElements.get(TAG.AON_ELABORATION_LIST)){
	window.customElements.define(TAG.AON_ELABORATION_LIST, AonElaborationList);
}