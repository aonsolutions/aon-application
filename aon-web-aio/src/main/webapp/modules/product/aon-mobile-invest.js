import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { createCard } from '../../components/CreateComponent.js';
import { EVENT, MSG, TAG } from '../../environments/environments.js';
import { InvestAssetRegimeOptions, InvestAssetTypeOptions, ToolbarType } from '../../models/enums.js';
import { getCompanyActivities } from '../../services/companyService.js';
import * as ACTION from '../actions.js';
import { AonInvest } from './aon-invest.js';
import { AonMobileInvestList } from './aon-mobile-invest-list.js';

export class AonMobileInvest extends AonInvest {

	constructor () {
		super();
	}

	initialize() {
		this.id = this.id || 'aonMobileInvest';
		super.initialize();
	}

	back() {
		let aonInvestList = new AonMobileInvestList();
		this.getApplication().setContent(aonInvestList);
	}

	build() {
		this.buildToolbar();

		let div = this.createElement(TAG.DIV);
		div.id = this.id + 'Div';
		this.appendChild(div);

		this.buildInvest(div);
	}

	buildToolbar() {
		let toolbar = new AonToolbar();
		toolbar.id = this.INVEST_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.investAsset.id
			? this.investAsset.description : MSG.NEW_INVEST_ASSET.toUpperCase();
		this.appendChild(toolbar);
		if(this.investAsset.id)
			toolbar.addButton2(ACTION.DELETE, () => this.delete());
		toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.BACK, () => this.back());
	}

	buildInvest(parent) {
		let card = createCard(this.INVEST_CARD, MSG.GENERAL_INFORMATION, parent);

		let table = new AonBasicTable();
		table.id = this.INVEST_TABLE;
		card.setContent(table);

		table.addRow();

		let descriptionInput = this.createInput(this.INVEST_DESCRIPTION, MSG.DESCRIPTION);
		descriptionInput.value = this.investAsset.description ? this.investAsset.description : '';
		descriptionInput.addEventListener(EVENT.CHANGE, () => this.investAsset.description = descriptionInput.value);
		table.addCell(descriptionInput);

		table.addRow();

		let activitySelect = this.createSelect(this.INVEST_ACTIVITY, MSG.ACTIVITY);
		activitySelect.setAlias('id', 'description');
		activitySelect.addEventListener(EVENT.SELECT, () => {
			this.investAsset.activity = {
				id: activitySelect.value
			};
			if(this.autosave) this.save();
		});
		table.addCell(activitySelect);
		getCompanyActivities({}).then(activities => {
			activitySelect.setOptions(activities);
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
		table.addCell(typeSelect);

		table.addRow();

		let regimeSelect = this.createSelect(this.INVEST_REGIME, MSG.REGIME);
		regimeSelect.setOptions(InvestAssetRegimeOptions);
		regimeSelect.value = this.investAsset.regime;
		regimeSelect.addEventListener(EVENT.SELECT, () => {
			this.investAsset.regime = regimeSelect.value;
			if(this.autosave) this.save();
		});
		table.addCell(regimeSelect);

		table.addRow();

		let startDate = this.createDate(this.INVEST_START_DATE, MSG.START_DATE);
		startDate.addEventListener(EVENT.CHANGE, () =>
			this.investAsset.startDate = startDate.value);
		table.addCell(startDate);
		if(this.investAsset.startDate)
			startDate.value = this.investAsset.startDate;

		table.addRow();

		let endDate = this.createDate(this.INVEST_END_DATE, MSG.END_DATE);
		endDate.addEventListener(EVENT.CHANGE, () =>
			this.investAsset.endDate = endDate.value);
		table.addCell(endDate);
		if(this.investAsset.endDate)
			endDate.value = this.investAsset.endDate;

		table.addRow();

		let vatPercent = this.createNumber(this.INVEST_VAT_PERCENT, '% Afectación IVA');
		vatPercent.value = this.investAsset.vatPercent ? this.investAsset.vatPercent : 0.0;
		vatPercent.addEventListener(EVENT.CHANGE, () =>
			this.investAsset.vatPercent = vatPercent.value);
		table.addCell(vatPercent);

		table.addRow();

		let retentionPercent = this.createNumber(this.INVEST_RETENTION_PERCENT, '% Afectación Imp.Directa');
		retentionPercent.value = this.investAsset.retentionPercent ? this.investAsset.retentionPercent : 0.0;
		retentionPercent.addEventListener(EVENT.CHANGE, () =>
			this.investAsset.retentionPercent = retentionPercent.value);
		table.addCell(retentionPercent);
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_INVEST)){
	window.customElements.define(TAG.AON_MOBILE_INVEST, AonMobileInvest);
}
