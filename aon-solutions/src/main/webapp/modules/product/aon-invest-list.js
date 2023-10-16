import {AonElement} from '../../components/AonElement.js';
import {getInvestAssets, getUserRoles} from  '../../services/service.js';
import {setList, setIndex, addList, getFilter, setFilter, getList} from './cache.js';

import { AonInvest } from './aon-invest.js';
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { AonTable } from '../../components/aon-table.js';
import { InvestAssetRegime, InvestAssetType } from '../../models/enums.js';
import { INVOICE } from '../../services/app.js';

export class AonInvestList extends AonElement {

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	TABLE;
	more;
	filter;
	back;

	constructor () {
		super();
	}

	connectedCallback() {
		this.initialize()
		this.build();
 	}

	initialize() {
		this.back = this.back || false;
		this.more = true;
		this.id = this.id || CONSTANT.AON_INVEST_LIST;
		this.TABLE = this.id + CONSTANT.TABLE.initCap();
		this.filter = this.filter || {
			page: 1,
			perPage: 30		
		};
		if(this.back) 
			this.filter = getFilter();
	}

	build() {
		let table = this.createAonElement(new AonTable(), this.TABLE);
		table.setApp(INVOICE);

		this.appendChild(table);

		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH, searchFn);
		
		table.addColumn(MSG.DESCRIPTION, CONSTANT.STRING, CONSTANT.DESCRIPTION, '25%');
		table.addColumn(MSG.ACTIVITY, CONSTANT.STRING, CONSTANT.ACTIVITY_NAME, '15%');
		table.addColumn(MSG.TYPE, CONSTANT.STRING, CONSTANT.TYPE_NAME, '15%');
		table.addColumn(MSG.REGIME, CONSTANT.STRING, CONSTANT.REGIME_NAME, '15%');
		table.addColumn('% Afectación IVA', CONSTANT.NUMBER, CONSTANT.VAT_PERCENT, '15%');
		table.addColumn('% Afectación Imp.Directa', CONSTANT.NUMBER, CONSTANT.RETENTION_PERCENT, '15%');
		table.addEventListener(EVENT.MORE, () => {
			if(this.more) this.loadMore()
		});
		this.init();
	}

	search(value) {
		this.filter.value = value;
		this.init();		
	}

	init() {
		let table = document.getElementById(this.TABLE);
		if(table) {
			if(this.back) {
				this.back = false;
				getList().forEach((object, i) => {
					object.activityName = object.activity.description;
					object.typeName = InvestAssetType[object.type].name;
					object.regimeName = InvestAssetRegime[object.regime].name;
					table.addRow(object, () => this.aonInvest(object, i));
				});
			} else {
				this.filter.page = 1;
				getInvestAssets(this.filter).then(objects => {
					setList(objects);
					table.removeRows();
					objects.forEach((object, i) => {
						object.activityName = object.activity.description;
						object.typeName = InvestAssetType[object.type].name;
						object.regimeName = InvestAssetRegime[object.regime].name;
						table.addRow(object, () => this.aonInvest(object, i));
					});
				});
			}
		}
	}

	loadMore() {
		this.more = false;
		let table = this.getElement(this.TABLE);
		if(table && this.filter.page) {
			this.filter.page = this.filter.page + 1;
			getInvestAssets(this.filter).then(objects => {
				addList(objects);
				if(objects.length > 0)
					this.more = true;
				objects.forEach((object, i) => {
					table.addRow(object, () => this.aonUser(object, i));
				});
			});
		}
	}

	aonInvest(object, index) {
		setIndex(index);
		setFilter(this.filter);
		let aonInvest = new AonInvest();
		aonInvest.setInvestAsset(object);
		this.getApplication().setContent(aonInvest);
	}

	setFilter(filter) {
		this.filter = filter;
	}

	setBack(back) {
		this.back = back || false;
	}
}
if(!window.customElements.get(TAG.AON_INVEST_LIST)){
	window.customElements.define(TAG.AON_INVEST_LIST, AonInvestList);
}

