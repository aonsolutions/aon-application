import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonCard } from '../../components/aon-card.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import {AonElement} from '../../components/AonElement.js';
import { TAG, CONSTANT, MSG, EVENT } from '../../environments/environments.js';
import { InvestAssetRegimeOptions, InvestAssetTypeOptions, ToolbarType } from '../../models/enums.js';
import { getCompanyActivities } from '../../services/companyService.js';
import { deleteInvestAsset, saveInvestAsset } from '../../services/productService.js';
import * as ACTION from '../actions.js';
import * as LS from '../../services/localStorageService.js';
import { AonInvestList } from './aon-invest-list.js';
import { createNumber, createSelect, createInput, createDate } from '../../components/CreateComponent.js';

export class AonInvest extends AonElement {

	INVEST_TOOLBAR;
	INVEST_CARD;
	INVEST_TABLE;
	INVEST_DESCRIPTION;
	INVEST_ACTIVITY;
	INVEST_TYPE;
	INVEST_REGIME;
	INVEST_START_DATE;
	INVEST_END_DATE;
	INVEST_VAT_PERCENT;
	INVEST_RETENTION_PERCENT;
	investAsset;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.build();
    }

	initialize() {	
		this.id = this.id || 'aonInvest';
		this.INVEST_TOOLBAR = this.id + 'Toolbar';
		this.INVEST_CARD = this.id + 'Card';
		this.INVEST_TABLE = this.id + 'Table';
		this.INVEST_DESCRIPTION = this.id + 'Description';
		this.INVEST_ACTIVITY = this.id + 'Activity';
		this.INVEST_TYPE = this.id + 'Type';
		this.INVEST_REGIME = this.id + 'Regime';
		this.INVEST_START_DATE = this.id + 'StartDate';
		this.INVEST_END_DATE = this.id + 'EndDate';
		this.INVEST_VAT_PERCENT = this.id + 'VatPercent';
		this.INVEST_RETENTION_PERCENT = this.id + 'RetentionPercent';
		this.investAsset = this.investAsset || {
			domain: LS.getDomainId()
		};
	}

	build() {
		let newTitle = MSG.NEW_INVEST_ASSET;
		let toolbar = new AonToolbar();
		toolbar.id = this.INVEST_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.investAsset.id
            ? this.investAsset.description : newTitle.toUpperCase(); 
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.DELETE, () => this.delete());
		toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.BACK, () => this.back());

		let div = this.createElement(TAG.DIV);
		div.style.display = "flex";
		div.style.width = "100%";
		this.appendChild(div);

		this.buildInvest(div);	
	}

	buildInvest(parent) {
		let card = this.createCard(this.INVEST_CARD, MSG.GENERAL_INFORMATION);
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.INVEST_TABLE;
		div.appendChild(table);

		table.addRow();

		let descriptionInput = this.createInput(this.INVEST_DESCRIPTION, MSG.DESCRIPTION);
		descriptionInput.value = this.investAsset.description ? this.investAsset.description : '';
		descriptionInput.addEventListener(EVENT.CHANGE, () => this.investAsset.description = descriptionInput.value);
    	table.addCell(descriptionInput, 2);

		table.addRow();

		let activitySelect = createSelect(this.INVEST_ACTIVITY, MSG.ACTIVITY);
		activitySelect.setAlias('id', 'description');		
		activitySelect.addEventListener(EVENT.SELECT, () => {
			this.investAsset.activity = {
				id: activitySelect.value
			};
			if(this.autosave) this.save();
		});
		table.addCell(activitySelect, 2);
		getCompanyActivities({}).then(activities => {
			activitySelect.setOptions(activities) ;
			if(this.investAsset.activity && this.investAsset.activity.id)
				activitySelect.value = this.investAsset.activity.id;
		});

		table.addRow();

		let typeSelect = this.createSelect(this.INVEST_TYPE, MSG.TYPE);
    	typeSelect.setOptions(InvestAssetTypeOptions);
		typeSelect.value = this.investAsset.type;
		typeSelect.addEventListener(EVENT.SELECT, () => {
			this.investAsset.type = typeSelect.value;
			if(this.autosave) this.save();
		});
		table.addCell(typeSelect, 1);


		let regimeSelect = this.createSelect(this.INVEST_REGIME, MSG.REGIME);
    	regimeSelect.setOptions(InvestAssetRegimeOptions);
		regimeSelect.value = this.investAsset.regime;
		regimeSelect.addEventListener(EVENT.SELECT, () => {
			this.investAsset.regime = regimeSelect.value;
			if(this.autosave) this.save();
		});
		table.addCell(regimeSelect, 1);

		table.addRow();

		let startDate = this.createDate(this.INVEST_START_DATE, MSG.START_DATE);
		startDate.addEventListener(EVENT.CHANGE, () => 
			this.investAsset.startDate = startDate.value);
		table.addCell(startDate, 1);
		if(this.investAsset.startDate)
			startDate.value = this.investAsset.startDate;

		let endDate = this.createDate(this.INVEST_END_DATE, MSG.END_DATE);
		endDate.addEventListener(EVENT.CHANGE, () => 
			this.investAsset.endDate = endDate.value);
		table.addCell(endDate, 1);
		if(this.investAsset.endDate)
			endDate.value = this.investAsset.endDate;

		table.addRow();

		let vatPercent = this.createNumber(this.INVEST_VAT_PERCENT, '% Afectación IVA'); //MSG.VAT_PERCENT);
		vatPercent.value = this.investAsset.vatPercent ? this.investAsset.vatPercent : 0.0;
		vatPercent.addEventListener(EVENT.CHANGE, () => 
			this.investAsset.vatPercent = vatPercent.value);
		table.addCell(vatPercent, 1);

		let retentionPercent = this.createNumber(this.INVEST_RETENTION_PERCENT, '% Afectación Imp.Directa'); //MSG.RETENTION_PERCENT);
		retentionPercent.value = this.investAsset.retentionPercent ? this.investAsset.retentionPercent : 0.0;
		retentionPercent.addEventListener(EVENT.CHANGE, () => 
			this.investAsset.retentionPercent = retentionPercent.value);
		table.addCell(retentionPercent, 1);
	}

	save() {
		saveInvestAsset(this.investAsset).then(ia => {
			this.investAsset = ia;
			this.getApplication().getToast().start({
				type: CONSTANT.SUCCESS,
				message: MSG.SAVED_DATA
			});
		}).catch(e => this.showError(e));
	}

	delete() {
    	let d = this.getApplication().getDialog();
   	 	d.clear();
    	if(!this.isMobile()) d.width = '400px';
    	d.setTitle(MSG.DELETE);
   	 	d.setContentHTML(`Estás seguro de eliminar el bien afecto`);
    	d.addAcceptAction(() => {
			let data = { id: this.investAsset.id};
			deleteInvestAsset(data).then(() => this.back(true));
    	});
    	d.open();
	}

	back(bool) {
		let aonInvestList = new AonInvestList();
		aonInvestList.setBack(!bool);
		this.getApplication().setContent(aonInvestList);
	}

	setInvestAsset(investAsset) {
		this.investAsset = investAsset;
	}

	// Create Components

	createCard(id, title) {
		let card = new AonCard();
		card.id = id;
		card.title = title;
		card.style.width = '50%';
		return card;
	}

	createSelect(id, title) {
		return createSelect(id, title);
	}

	createInput(id, title) {
		return createInput(id, title);
	}

	createNumber(id, title) {
		let number = createNumber(id, title);
		number.format = CONSTANT.TRUE;
		number.decimals = "2";
		return number;
	}

	createDate(id, title) {
		return createDate(id, title);
	}
}
if(!window.customElements.get(TAG.AON_INVEST)){
	window.customElements.define(TAG.AON_INVEST, AonInvest);
}