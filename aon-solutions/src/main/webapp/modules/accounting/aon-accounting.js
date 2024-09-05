// COMPONENTS
import {AonElement} from '../../components/AonElement.js';

// SERVICES
import {getDomainUserRoles} from '../../services/service.js';
import { getPeriods } from "../../services/accountingService.js";

// MODELS
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

// CONSTANTS

import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js";
import { AonGraphicsTrial } from './aon-graphics-trial.js';
import {ACCOUNTING } from '../../services/app.js';

import * as GWT from '../../gwt/gwt.js';
import { waitEl } from '../../services/utils.js';
import { AonApplication } from '../../components/aon-application.js';

export class AonAccounting extends AonElement {

	dur;
	AON_ACCOUNTING;
	filter;

	params = {
		domain: localStorage.getItem("aon_domain_id"),
		domainName: localStorage.getItem("aon_domain_name"),
		user: "",
		level: 5,
		byMonth: true,
	};
	
	constructor(filter) {
		super();
		this.filter = filter;
	}

	connectedCallback () {
		this.initialize();
		this.createApplication(this.AON_ACCOUNTING, MSG.ACCOUNTING, new AonApplication());

		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
 	}

	initialize() {
		this.AON_ACCOUNTING = CONSTANT.AON_ACCOUNTING;
	}

 	async build() {
		let application = this.getApplication();

		if(this.isMobile()){
			application.addMobileSidenavHeader(ACCOUNTING);
		}

		if (!this.params.domain || !this.params.domainName) {
			try {
			  let company = JSON.parse(localStorage.getItem("company"));
			  this.params.domain = company.id;
			  this.params.domainName = company.domain;
			} catch (error) {
			  console.log(error);
			}
		}

		this.PERIODS = await getPeriods(this.params)
			.catch((error) => {
				console.log(error);
				return [];
			});

		let periodOptions = this.PERIODS.map((period) => ({
			id: period.name,
			icon: MATERIAL_ICONS.EVENT,
			name: "Ejercicio " + period.name,
			clickable: true,
			fn: () => {
				if(!this.filter) this.filter = {};
				this.filter.year = period.name;
				this.aonGraphicsTrialView ();
			},
		  }));
		
		let viewOptions = [
			{
				id: 'VistaAnual',
				name: 'Vista Anual',
				icon: MATERIAL_ICONS.CALENDAR_TODAY,
				fn: () => {
					if(!this.filter) this.filter = {};
					this.filter.show = "yearly";
					this.aonGraphicsTrialView ();
				}
			},
			{
				id: 'VistaTrimestral',
				name: 'Vista Trimestral',
				icon: MATERIAL_ICONS.CALENDAR_TODAY,
				fn: () => {
					if(!this.filter) this.filter = {};
					this.filter.show = "quarterly";
					this.aonGraphicsTrialView ();
				}
			},
			{
				id: 'VistaMensual',
				name: 'Vista Mensual',
				icon: MATERIAL_ICONS.CALENDAR_TODAY,
				fn: () => {
					if(!this.filter) this.filter = {};
					this.filter.show = "monthly";
					this.aonGraphicsTrialView ();
				}
			}
		];

		let detailOptions = [
			{
				id: 'Estándar',
				name: 'Estándar',
				icon: MATERIAL_ICONS.LIST,
				fn: () => {
					if(!this.filter) this.filter = {};
					this.filter.detail = "5";
					this.aonGraphicsTrialView ();
				}
			},
			{
				id: 'Resumido',
				name: 'Resumido',
				icon: MATERIAL_ICONS.LIST,
				fn: () => {
					if(!this.filter) this.filter = {};
					this.filter.detail = "3";
					this.aonGraphicsTrialView ();
				}
			},
			{
				id: 'Detallado',
				name: 'Detallado',
				icon: MATERIAL_ICONS.LIST,
				fn: () => {
					if(!this.filter) this.filter = {};
					this.filter.detail = "9";
					this.aonGraphicsTrialView ();
				}
			}
		];


		let options = [{
			id: 'PyG',
			name: 'Pérdidas y Ganancias',
			icon: MATERIAL_ICONS.BAR_CHART,
			fn: () => {
				application.removeSidenavById(CONSTANT.OPTIONS);
				
				let periodOpt = {
					id: CONSTANT.OPTIONS,
					name: MSG.OPTIONS + " Gráfico",
					app: ACCOUNTING,
					options: periodOptions
				}
				
				application.addSelectSidenav(periodOpt);

				let viewOpt = {
					parent: CONSTANT.OPTIONS,
					id: CONSTANT.OPTIONS + "View",
					name: MSG.OPTIONS,
					app: ACCOUNTING,
					options: viewOptions
				}

				application.addSelectToPanel(viewOpt);

				let detailOpt = {
					parent: CONSTANT.OPTIONS,
					id: CONSTANT.OPTIONS + "Detail",
					name: MSG.OPTIONS,
					app: ACCOUNTING,
					options: detailOptions
				}

				application.addSelectToPanel(detailOpt);

				this.clearElementById(this.getApplication().getContent().id);
				this.aonGraphicsTrialView ();
			}
		}];
		
		if(this.dur.isBank()) {
			options.push({
				id: 'banksnordigen',
				name: MSG.BANKS,
				icon: MATERIAL_ICONS.ACCOUNT_BALANCE,
				fn: () => {
					this.getApplication().removeSidenavById(CONSTANT.OPTIONS);
					this.clearElementById(this.getApplication().getContent().id);
					GWT.load(GWT.NORDIGEN, this.getApplication().CONTENT);
					this.loader(`#${this.getApplication().getContent().id} .aon_toolbar`);
				}
			});
		}
		options.push({
			id: 'extracto',
			name: "Extracto de cuenta",
			icon: MATERIAL_ICONS.LIST_ALT,
			fn: () => {
				this.getApplication().removeSidenavById(CONSTANT.OPTIONS);
				this.clearElementById(this.getApplication().getContent().id);
				GWT.load(GWT.STATEMENT_REPORT, this.getApplication().CONTENT);
				this.loader(`#${this.getApplication().getContent().id} .aon_toolbar`);
			}
		});
		options.push({
			id: 'cuentapyg1',
			name: "Cuenta Explotación (P y G)",
			icon: MATERIAL_ICONS.LIST_ALT,
			fn: () => {
				this.getApplication().removeSidenavById(CONSTANT.OPTIONS);
				this.clearElementById(this.getApplication().getContent().id);
				GWT.load(GWT.ACCOUNT_OPERATION_REPORT, this.getApplication().CONTENT);
				this.loader(`#${this.getApplication().getContent().id} .aon_toolbar`);
			}
		});
		options.push({
			id: 'balanceSS',
			name: "Balance de Sumas y Saldos",
			icon: MATERIAL_ICONS.LIST_ALT,
			fn: () => {
				this.getApplication().removeSidenavById(CONSTANT.OPTIONS);
				this.clearElementById(this.getApplication().getContent().id);
				GWT.load(GWT.ACCOUNT_TRIAL_BALANCE_REPORT, this.getApplication().CONTENT);
				this.loader(`#${this.getApplication().getContent().id} .aon_toolbar`);
			}
		});
		options.push({
			id: 'listadoD',
			name: "Diario de Movimientos",
			icon: MATERIAL_ICONS.LIST_ALT,
			fn: () => {
				this.getApplication().removeSidenavById(CONSTANT.OPTIONS);
				this.clearElementById(this.getApplication().getContent().id);
				GWT.load(GWT.JOURNAL_REPORT, this.getApplication().CONTENT);
				this.loader(`#${this.getApplication().getContent().id} .aon_toolbar`);
			}
		});
		options.push({
			id: 'listadoM',
			name: "Listado Mayor de Cuentas",
			icon: MATERIAL_ICONS.LIST_ALT,
			fn: () => {
				this.getApplication().removeSidenavById(CONSTANT.OPTIONS);
				this.clearElementById(this.getApplication().getContent().id);
				GWT.load(GWT.LEDGER_REPORT, this.getApplication().CONTENT);
				this.loader(`#${this.getApplication().getContent().id} .aon_toolbar`);
			}
		});
		options.push({
			id: 'balanceO',
			name: "Balances oficiales",
			icon: MATERIAL_ICONS.LIST_ALT,
			fn: () => {
				this.getApplication().removeSidenavById(CONSTANT.OPTIONS);
				this.clearElementById(this.getApplication().getContent().id);
				GWT.load(GWT.ACCOUNT_BALANCE_REPORT, this.getApplication().CONTENT);
				this.loader(`#${this.getApplication().getContent().id} .aon_toolbar`);
			}
		});
		let data1 = {
			id: CONSTANT.ACCOUNTING,
			name: MSG.ACCOUNTING,
			app: ACCOUNTING,
			options
		}

		application.addSidenavOptions3(data1);

		let periodOpt = {
			id: CONSTANT.OPTIONS,
			name: MSG.OPTIONS + " Gráfico",
			app: ACCOUNTING,
			options: periodOptions
		}

		application.addSelectSidenav(periodOpt);

		let viewOpt = {
			parent: CONSTANT.OPTIONS,
			id: CONSTANT.OPTIONS + "View",
			name: MSG.OPTIONS,
			app: ACCOUNTING,
			options: viewOptions
		}

		application.addSelectToPanel(viewOpt);

		let detailOpt = {
			parent: CONSTANT.OPTIONS,
			id: CONSTANT.OPTIONS + "Detail",
			name: MSG.OPTIONS,
			app: ACCOUNTING,
			options: detailOptions
		}

		application.addSelectToPanel(detailOpt);

		application.addBackgroundSidenav("PyG", ACCOUNTING.color);

		if(!this.filter){
			let period = this.PERIODS.filter(period => period.name == new Date().getFullYear());

			this.filter = {};
			this.filter.year = period[0].name;
			this.filter.show = "yearly";
			this.filter.detail = "5";
		}

		this.aonGraphicsTrialView();
	}

	async loader(selector, doc=undefined) {
		this.getApplication().startLoader();
		try {
			await waitEl(selector, doc);
		} catch (e) {}
		this.getApplication().stopLoader();
	}
	
	aonGraphicsTrialView () {
		this.getApplication().setContent(new AonGraphicsTrial(this.filter));
	}
}
if(!window.customElements.get('aon-accounting')){
	window.customElements.define('aon-accounting', AonAccounting);
}
