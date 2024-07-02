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
import * as LS from "../../services/localStorageService.js";

export class AonFiscal extends AonElement {
  AON_FISCAL;
  MODELS = [];
  BANKS = [];
  dur;
  _filter;
  constructor() {
    super();
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
    this._filter = {
      year: undefined,
      period: undefined,
      model: undefined,
    };
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
      if (!LS.isNewTheme()) {
        console.log("buildToolbaroldtheme");
        application.addToolbarOption2(VAT_PANEL, () => {
          application.closeSidenav();
          this.showView(FISCAL_VIEWS.VAT_PANEL);
        });

        application.addToolbarOption2(RETENTION_PANEL, () => {
          application.closeSidenav();
          this.showView(FISCAL_VIEWS.IRPF_REPORT);
        });
      }
      application.addTitleToolSection("Estimaciones");
    }

    this.getModelsFiscal().then(async (mdls) => {
      let firstYear = this.getFirstYear(mdls);
      if (firstYear) {
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
        id: model.model,
        icon: undefined,
        img: FiscalUtils.getPathImg(model.administration),
        name: model.modelText,
        fn: () => {
          this._filter.model =
            this._filter.model === model.model ? undefined : model.model;
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

      RETENTION_PANEL.fn = () => {
        application.closeSidenav();
        this.showView(FISCAL_VIEWS.IRPF_REPORT);
      };

      VAT_PANEL.fn = () => {
        application.closeSidenav();
        this.showView(FISCAL_VIEWS.VAT_PANEL);
      };
      let data4 = {
        id: "panels",
        title: "Paneles",
        name: "Paneles",
        app: FISCAL,
        options: [VAT_PANEL, RETENTION_PANEL],
      };

      application.addSidenavOptions3(data4);

      this.showView(FISCAL_VIEWS.AON_TAX);

    });
  }

  async getModelsFiscal() {
    if (!this.MODELS.length) {
      try {
        const datos = await getModelsFiscal();
        if (datos) {
          this.MODELS = sortBy(datos, "year", "desc")
            .sort((a, b) => a.period.localeCompare(b.period))
            .filter(({ status }) => status !== "PENDING")
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

  orderBy(array, order = "asc") {}

  getDataForKey(models, key) {
    let datas = models.map((m) => m[key]);
    return datas.filter((item, pos) => datas.indexOf(item) === pos);
  }

  getModelsNoRepeat(models) {
    return models.filter(
      (v, i) => models.findIndex((v2) => v2.model === v.model) === i
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
          clickable: true,
          fn: () => {
            this._filter.period = period;
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
          this.addBackgroundSidenav();
          this.showView(FISCAL_VIEWS.AON_TAX);
        },
      };
      periods.unshift(allPeriod);

      if (this._filter.year == new Date().getFullYear()) {
        let futurePeriod = await this.filterFutureFiscal();
        periods.push(futurePeriod);
      }

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

    if (!result || result.length === 0) {
      return;
    }

    let lastPeriod;
    let period;
    let periodText;
    let year;

    if (result[0].period == "T1") {
      period = "T2";
      periodText = "2º Trim.";
      year = result[0].year;
      lastPeriod = new Date(result[0].year + "-" + "03-31");
    } else if (result[0].period == "T2") {
      period = "T3";
      periodText = "3º Trim.";
      year = result[0].year;
      lastPeriod = new Date(result[0].year + "-" + "06-30");
    } else if (result[0].period == "T3") {
      period = "T4";
      periodText = "4º Trim.";
      year = result[0].year;
      lastPeriod = new Date(result[0].year + "-" + "09-30");
    } else {
      period = "T1";
      periodText = "1º Trim.";
      year = result[0].year + 1;
      lastPeriod = new Date(result[0].year + "-" + "12-31");
    }

    let futurePeriod = {
      name: "Borrador " + periodText,
      icon: MATERIAL_ICONS.EVENT,
      clickable: true,
      id: "Borrador" + periodText,
      fn: () => {
        this._filter.period = "future";
        this._filter.estimationFilter = {year: year, period: period, title: "Borrador " + periodText, periodText: periodText};
        this.showView(FISCAL_VIEWS.AON_TAX);
      },
    };

    return futurePeriod;

    // const dayDiff = Math.floor((new Date() - lastPeriod) / (1000 * 60 * 60 * 24));

    // if(dayDiff > 30){
    // 	setTimeout(() => {
    // 		aonFiscalCard.filterEstimationTable({year: year, period: period, title: "Borrador " + periodText})
    // 	}, 100);
    // }
  }

  async getFilterModels() {
    const datos = this.MODELS;
    let orderDatos = [];
    if (datos) {
      orderDatos = sortBy(datos, "year", "desc").map((model) =>
        FiscalUtils.getModelNew(model)
      );
    }

    const result = orderDatos.filter(function (a) {
      var key = a.year + "|" + a.period;
      if (!this[key]) {
        this[key] = true;
        return true;
      }
    }, Object.create(null));

    result.sort(function (a, b) {
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
        case FISCAL_VIEWS.VAT_PANEL:
          GWT.load(GWT.VAT_REPORT, this.applicationEl.CONTENT);
          break;
        case FISCAL_VIEWS.IRPF_REPORT:
          GWT.load(GWT.IRPF_REPORT, this.applicationEl.CONTENT);
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
