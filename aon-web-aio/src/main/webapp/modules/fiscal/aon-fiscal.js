import { AonElement } from "../../components/AonElement.js";
import {
	getCompanyBanks,
	getDomainUserRoles,
} from "../../services/companyService.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { MSG, MATERIAL_ICONS } from "../../environments/environments.js";
import { FiscalOptions, FISCAL_VIEWS, TAX_ENUMS } from "./FiscalEnums.js";
import { AonTax } from "./tax/aon-tax.js";
import { AonApplication } from "../../components/aon-application.js";
import { FISCAL } from "../../services/app.js";
import { getModelsFiscal } from "../../services/fiscalService.js";
import { sortBy } from "../../services/utils.js";
import { FiscalUtils } from "./FiscalUtils.js";
import { SigninSidenav } from "../timecontrol/signinEnums.js";
import * as GWT from "../../gwt/gwt.js";
import { RETENTION_PANEL, VAT_PANEL } from "./FiscalOptions.js";
import { AonFutureTax } from "./tax/aon-future-tax.js";

export class AonFiscal extends AonElement {
	AON_FISCAL;
	MODELS = [];
	BANKS = [];
	dur;
	_filter;

	firstTime = false;

	constructor(filter) {
		super();
		if (filter) {
			this.firstTime = true;
			this._filter = filter;
		}
	}

	connectedCallback() {
		this.initialize();
		getDomainUserRoles({ reload: true }).then((r) => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
	}

	initialize() {
		this.AON_FISCAL = FISCAL_VIEWS.AON_FISCAL;
		if (!this._filter) {
			this._filter = {
				year: undefined,
				period: undefined,
				model: undefined,
			};
		}
	}

	getDur() {
		return this.dur;
	}

	build() {
		this.paintView();
		this.buildToolbar();
	}

	paintView() {
		this.createApplication(this.AON_FISCAL, MSG.FISCAL, new AonApplication());
		this.applicationEl = this.getApplication();
	}

	async buildToolbar() {
		let application = this.getApplication();

		application.addToolbarOption2(
			{ ...SigninSidenav.SYNCHRONIZE, name: MSG.UPDATE },
			() => this.rootPanel(new AonFiscal())
		);

		if (this.isMobile()) {
			application.addMobileSidenavHeader(FISCAL);
		} else {
			application.addTitleToolSection("Estimaciones");
		}

		this.getModelsFiscal().then(async (mdls) => {

			if (mdls && mdls.length !== 0) {
				let firstYear = this.getFirstYear(mdls);
				if (firstYear && !this._filter.year) {
					this._filter.year = firstYear;
				}

				let ejercicios = this.getDataForKey(mdls, "year").map((year) => ({
					...FiscalOptions.AON_TAX,
					id: year,
					icon: MATERIAL_ICONS.EVENT,
					name: year,
					clickable: true,
					fn: () => {
						this._filter.year = year;
						this._filter.estimationFilter = undefined;
						this.addBackgroundSidenav();

						this.checkAviablePeriodsForYear();
						this._filter.period = undefined;

						this.showView(FISCAL_VIEWS.AON_TAX);
					},
				}));

				let data = {
					id: "Periodo",
					title: "Periodo",
					name: "Periodo",
					app: FISCAL,
					options: ejercicios,
				};

				application.addSelectSidenav(data);

				// Show aviable periods for default year
				await this.checkAviablePeriodsForYear();

				let models = this.getModelsNoRepeat(mdls).map((model) => ({
					...FiscalOptions.AON_TAX,
					id: FiscalUtils.getModelNumber(model.administration, model.model),
					icon: undefined,
					// img: FiscalUtils.getPathImg(model.administration),
					html: FiscalUtils.getModelNumberHtml(model.administration, model.model),
					name: model.modelText === "IRPF Trabajo y Profesionales" ? "IRPF Trabajo/Profesionales" : model.modelText,
					fn: () => {
						this._filter.model = this._filter.model === FiscalUtils.getModelNumber(model.administration, model.model) ? undefined : FiscalUtils.getModelNumber(model.administration, model.model);
						this._filter.estimationFilter = undefined;
						this.addBackgroundSidenav();
						this.showView(FISCAL_VIEWS.AON_TAX);
					},
				}));

				let data3 = {
					id: "Modelo",
					title: "Modelo",
					name: "Modelo",
					app: FISCAL,
					options: models,
				};
				application.addSidenavOptions3(data3);
			}

			let futurePeriod = await this.filterFutureFiscal();

			let data4 = {
				id: "panels",
				title: "Precálculo",
				name: "Precálculo",
				app: FISCAL,
				options: futurePeriod
			};

			application.addSidenavOptions3(data4);

			this.showView(FISCAL_VIEWS.AON_TAX);
			
			if (this.firstTime) {
				this.firstTime = false;

				if (this._filter.future) {
					this._filter.period = undefined;
					let aonFiscalSidenavEjercicioSelect = this.getElement("aonFiscalSidenavEjercicioSelect");
					if(aonFiscalSidenavEjercicioSelect) aonFiscalSidenavEjercicioSelect.value = '"Todos"';

					let futureButton = this.getElement("aonFiscalSidenavFuture");
					futureButton.click();
				} else {

					let aonFiscalSidenavModeloSelect = this.getElement("aonFiscalSidenavPeriodoSelect");
					aonFiscalSidenavModeloSelect.value = this._filter.year;

					let aonFiscalSidenavEjercicioSelect = this.getElement("aonFiscalSidenavEjercicioSelect");
					aonFiscalSidenavEjercicioSelect.value = this._filter.period ? '"' + this._filter.period + '"' : '"Todos"';
				}
			}

		});
	}

	async getModelsFiscal() {
		if (!this.MODELS.length) {
			try {
				const datos = await getModelsFiscal();

				if (datos) {
					this.MODELS = sortBy(datos, "year", "desc")
						.sort((a, b) => a.period.localeCompare(b.period))
						// .filter(({ status }) => status !== "PENDING")
						.map((model) => FiscalUtils.getModelNew(model));
				}
			} catch (error) {
				console.error(error);
				this.showError(error);
			}
		}
		return this.MODELS;
	}

	async getBanks() {
		if (!this.BANKS.length) {
			let result = await getCompanyBanks().catch(() => null);
			if (result) {
				this.BANKS = result;
			}
		}
		return this.BANKS;
	}

	getFirstYear(models) {
		let model = models.find((o) => o.year);
		return model ? model.year : null;
	}

	orderBy(array, order = "asc") { }

	getDataForKey(models, key) {
		let datas = models.map((m) => m[key]);
		return datas.filter((item, pos) => datas.indexOf(item) === pos);
	}

	getModelsNoRepeat(models) {
		return models.filter(
			(v, i) => models.findIndex((v2) => v2.model === v.model && v2.administration === v.administration) === i
		);
	}

	addBackgroundSidenav() {
		const application = this.getApplication();
		const filter = this._filter;
		if (filter) {
			application.removeBackgroundSidenavAll(FISCAL.color);
			if (filter.year) {
				application.addBackgroundSidenav(filter.year, FISCAL.color);
			}
			if (filter.period) {
				application.addBackgroundSidenav(filter.period, FISCAL.color);
			}
			if (filter.model) {
				application.addBackgroundSidenav(filter.model, FISCAL.color);
			}
		}
	}

	async checkAviablePeriodsForYear() {
		if (this.MODELS) {
			const application = this.getApplication();

			let year = this._filter.year;

			// Filtrar la lista de objetos
			const listaFiltrada = this.MODELS.filter((objeto) => objeto.year == year);

			let periods = this.getDataForKey(listaFiltrada, "period")
				.sort()
				.map((period) => ({
					...FiscalOptions.AON_TAX,
					id: period,
					icon: MATERIAL_ICONS.EVENT,
					name: TAX_ENUMS.TAX_PERIOD[period],
					value: period,
					clickable: true,
					fn: () => {
						this._filter.period = period;
						this._filter.estimationFilter = undefined;
						this.addBackgroundSidenav();
						this.showView(FISCAL_VIEWS.AON_TAX);
					},
				}));

			// Add all periods
			let allPeriod = {
				name: "Todos",
				icon: MATERIAL_ICONS.EVENT,
				clickable: true,
				id: "Todos",
				fn: () => {
					this._filter.period = undefined;
					this._filter.estimationFilter = undefined;
					this.addBackgroundSidenav();
					this.showView(FISCAL_VIEWS.AON_TAX);
				},
			};
			periods.unshift(allPeriod);

			// if (this._filter.year == new Date().getFullYear()) {
			//   let futurePeriod = await this.filterFutureFiscal();
			//   periods.push(futurePeriod);
			// }

			let data2 = {
				parent: "Periodo",
				id: "Ejercicio",
				title: "Ejercicio",
				name: "Ejercicio",
				app: FISCAL,
				options: periods,
			};

			application.addSelectToPanel(data2);
		}
	}

	async filterFutureFiscal() {
		let result = await this.getFilterModels();
		let futureFiscals = [];

		if (!result || result.length === 0) {
			let period = this.getCurrentFiscalPeriod();
			let year = new Date().getFullYear();
			
			// Borrador actual (para empresas nuevas o sin modelos existentes)
			let currentPeriod;
			let currentPeriodText;
			let currentYear;
			if (period == "T1") {
				currentPeriod = "T1";
				currentPeriodText = "1º Trim. " + (year);
				currentYear = year;
			} else if (period == "T2") {
				currentPeriod = "T2";
				currentPeriodText = "2º Trim. " + year;
				currentYear = year;
			} else if (period == "T3") {
				currentPeriod = "T3";
				currentPeriodText = "3º Trim. " + year;
				currentYear = year;
			} else {
				currentPeriod = "T4";
				currentPeriodText = "4º Trim. " + year;
				currentYear = year;
			}
	
			let currentPeriodObj = {
				name: currentPeriodText,
				icon: MATERIAL_ICONS.EVENT,
				clickable: true,
				id: "Future",
				fn: () => {
					if (this._filter.estimationFilter && this._filter.estimationFilter.year == currentYear) {
						this._filter.estimationFilter = undefined;
						this.addBackgroundSidenav();
						this.showView(FISCAL_VIEWS.AON_TAX);
					} else {
						this._filter.estimationFilter = { year: currentYear, period: currentPeriod, title: currentPeriodText, periodText: currentPeriodText };
						this._filter.model = undefined;
						this.showView(FISCAL_VIEWS.AON_FUTURE_TAX);
					}
				}
			};
	
			futureFiscals.push(currentPeriodObj);
		} else {
			let currentLastPeriod;
			let currentPeriod;
			let currentPeriodText;
			let currentYear;
	
			if (result[0].period == "T1") {
				currentPeriod = "T1";
				currentPeriodText = "1º Trim. " + (result[0].year);
				currentYear = result[0].year;
				currentLastPeriod = new Date(result[0].year + "-" + "12-31");
			} else if (result[0].period == "T2") {
				currentPeriod = "T2";
				currentPeriodText = "2º Trim. " + result[0].year;
				currentYear = result[0].year;
				currentLastPeriod = new Date(result[0].year + "-" + "03-31");
			} else if (result[0].period == "T3") {
				currentPeriod = "T3";
				currentPeriodText = "3º Trim. " + result[0].year;
				currentYear = result[0].year;
				currentLastPeriod = new Date(result[0].year + "-" + "06-30");
			} else {
				currentPeriod = "T4";
				currentPeriodText = "4º Trim. " + result[0].year;
				currentYear = result[0].year;
				currentLastPeriod = new Date(result[0].year + "-" + "09-30");
			}
	
			let currentPeriodObj = {
				name: currentPeriodText,
				icon: MATERIAL_ICONS.EVENT,
				clickable: true,
				id: "Current",
				fn: () => {
					if (this._filter.estimationFilter && this._filter.estimationFilter.year == currentYear) {
						this._filter.estimationFilter = undefined;
						this.addBackgroundSidenav();
						this.showView(FISCAL_VIEWS.AON_TAX);
					} else {
						this._filter.estimationFilter = { year: currentYear, period: currentPeriod, title: currentPeriodText, periodText: currentPeriodText };
						this._filter.model = undefined;
						this.showView(FISCAL_VIEWS.AON_FUTURE_TAX);
					}
					// this._filter.estimationFilter = {year: year, period: period, title: periodText, periodText: periodText};
	
				},
			};
	
			futureFiscals.push(currentPeriodObj);
	
			let futureLastPeriod;
			let futurePeriod;
			let futurePeriodText;
			let futureYear;
	
			if (result[0].period == "T1") {
				futurePeriod = "T2";
				futurePeriodText = "2º Trim. " + result[0].year;
				futureYear = result[0].year;
				futureLastPeriod = new Date(result[0].year + "-" + "03-31");
			} else if (result[0].period == "T2") {
				futurePeriod = "T3";
				futurePeriodText = "3º Trim. " + result[0].year;
				futureYear = result[0].year;
				futureLastPeriod = new Date(result[0].year + "-" + "06-30");
			} else if (result[0].period == "T3") {
				futurePeriod = "T4";
				futurePeriodText = "4º Trim. " + result[0].year;
				futureYear = result[0].year;
				futureLastPeriod = new Date(result[0].year + "-" + "09-30");
			} else {
				futurePeriod = "T1";
				futurePeriodText = "1º Trim. " + (result[0].year + 1);
				futureYear = result[0].year + 1;
				futureLastPeriod = new Date(result[0].year + "-" + "12-31");
			}
	
			let futurePeriodObj = {
				name: futurePeriodText,
				icon: MATERIAL_ICONS.EVENT,
				clickable: true,
				id: "Future",
				fn: () => {
					if (this._filter.estimationFilter && this._filter.estimationFilter.year == futureYear) {
						this._filter.estimationFilter = undefined;
						this.addBackgroundSidenav();
						this.showView(FISCAL_VIEWS.AON_TAX);
					} else {
						this._filter.estimationFilter = { year: futureYear, period: futurePeriod, title: futurePeriodText, periodText: futurePeriodText };
						this._filter.model = undefined;
						this.showView(FISCAL_VIEWS.AON_FUTURE_TAX);
					}
					// this._filter.estimationFilter = {year: year, period: period, title: periodText, periodText: periodText};
	
				},
			};
	
			futureFiscals.push(futurePeriodObj);
		}

		return futureFiscals;
	}
	
	getCurrentFiscalPeriod(){
		const fecha = new Date();
		const mes = fecha.getMonth(); // getMonth() devuelve un número entre 0 (enero) y 11 (diciembre)
    
	    if (mes >= 0 && mes <= 2) return "T1";  // Enero - Marzo
	    if (mes >= 3 && mes <= 5) return "T2";  // Abril - Junio
	    if (mes >= 6 && mes <= 8) return "T3";  // Julio - Septiembre
	    return "T4"; // Octubre - Diciembre
	}

	async getFilterModels() {
		const datos = this.MODELS;
		let orderDatos = [];
		if (datos) {
			orderDatos = sortBy(datos, "year", "desc").map((model) =>
				FiscalUtils.getModelNew(model)
			);
		}

		const result = orderDatos.filter(function(a) {
			var key = a.year + "|" + a.period;
			if (!this[key]) {
				this[key] = true;
				return true;
			}
		}, Object.create(null));

		result.sort(function(a, b) {
			var aSize = a.year;
			var bSize = b.year;
			var aLow = a.period;
			var bLow = b.period;

			if (aSize == bSize) {
				return aLow < bLow ? -1 : aLow > bLow ? 1 : 0;
			} else {
				return aSize < bSize ? -1 : 1;
			}
		});

		return result.reverse();
	}

	showView(view, data, filter = undefined) {
		this.showAviablePeriods;
		return new Promise(async (resolve) => {
			let aonView = undefined;
			switch (view) {
				case FISCAL_VIEWS.AON_TAX:
					aonView = new AonTax();
					break;
				case FISCAL_VIEWS.AON_FUTURE_TAX:
					aonView = new AonFutureTax(FISCAL, this._filter.estimationFilter);
					break;
				case FISCAL_VIEWS.VAT_PANEL:
					GWT.iLoad(GWT.VAT_REPORT, this.applicationEl.CONTENT);
					break;
				case FISCAL_VIEWS.IRPF_REPORT:
					GWT.iLoad(GWT.IRPF_REPORT, this.applicationEl.CONTENT);
					break;
			}
			if (aonView) {
				aonView.id = view;
				if (filter) aonView.filter = filter;
				if (data) aonView.data = data;
				this.applicationEl.setContent(aonView);
			}
			resolve(aonView);
		});
	}
}
window.customElements.define("aon-fiscal", AonFiscal);
