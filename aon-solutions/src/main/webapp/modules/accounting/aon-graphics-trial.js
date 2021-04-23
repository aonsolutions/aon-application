import { AonElement } from "../../components/AonElement.js";
import { TAG } from "../../environments/environments.js";

import {
  formatDate,
  isEmptyObject,
  setValueName,
} from "../../services/utils.js";
import { colChart } from "./charts.js";
import { getAccounting, getPeriods, PERIOD_FILTER } from "../../services/accountingService.js";
import "../../components/aon-filter.js";
import { SigninSidenav } from "../signin/signinEnums.js";
import { getPeriodAccounting } from "../../services/service.js";
import { ToolbarType } from "../../models/enums.js";
import * as ACTION from "../actions.js";

export class AonGraphicsTrial extends AonElement {
  PERIODS;
  ACCOUNTS;
  params;
  filter;
  selectedPeriod;
  params = {
    domain: localStorage.getItem("aon_domain_id"),
    domainName: localStorage.getItem("aon_domain_name"),
    user: "",
    level: 5,
    byMonth : true
  };

  set id(id) {
    this.setAttribute("id", id);
  }

  get id() {
    return this.getAttribute("id");
  }

  constructor() {
    super();
    this.id = this.id || "aonGraphicsTrial";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.TOOLBAR = this.id + 'Toolbar';
  }

  async build() {
    this.innerHTML = `
    <aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="Pérdidas y Ganancias"> </aon-toolbar>
    <aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    this.PERIODS = await getPeriods(this.params).catch((error) => null);

    let lastDateTime = Math.max.apply(null, this.PERIODS.map(p => new Date(p.initiationDate).getTime()));
    let lastPeriod = this.PERIODS.filter(p => new Date(p.initiationDate).getTime() == lastDateTime)[0];
    this.selectedPeriod = lastPeriod;

    this.buildToolbar();
    this.buildPyGToolbar();
    await this.buildFilter();

    this.draw();
    if (this.selectedPeriod)
      this.getElement("year").value = this.selectedPeriod.id;

    this.getElement("show").value = this.filter != null ? this.filter.show : "yearly";
    this.getElement("detail").value = this.params.level;
    

  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, (e) =>
      filterEl.openFilter()
    );
  }

  buildPyGToolbar() {
    let toolbar = this.getElement(this.TOOLBAR);
    toolbar.removeButtons();
    toolbar.addButton2(ACTION.BACK, null);
  }

  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs([...PERIOD_FILTER]);
    aonFilter.addEventListener("applyFilter", ({ detail }) => {
      if (detail) {
        this.filter = detail;
        this.build();
        // this.applicationParentEl.setDataFilter(detail);
      }
    });

    //------------------YEAR-----------
    let yearEl = this.getElement("year");
    let years = [];

    for (let i=0; i<this.PERIODS.length; i++) {
      years.push({
        name: this.PERIODS[i].name,
        value: this.PERIODS[i].id,
      });
    }
    yearEl.options = JSON.stringify(years);
    //------------------SHOW-----------
    let showEl = this.getElement("show");
    showEl.options = JSON.stringify(getPeriodAccounting());

    //----------------DETAIL-----------
    let detailEl = this.getElement("detail");
    let detailsJson = [
      {
        name: "Resumido",
        value: 3
      },
      {
        name: "Estándar",
        value: 5
      },
      {
        name: "Detallado",
        value: 9
      }
    ];
    detailEl.options = JSON.stringify(detailsJson);
  }

  async draw() {
    this.getApplication().startLoader();


    let id = "chart_div";

    const result = await this.getData();
    if (result) {
      let div = this.getElement(id) || this.createElement(TAG.DIV);
      div.innerHTML = "";
      div.id = id;
      div.style.textAlign = "center";
      div.style.display = "flex";
      div.style.justifyContent = "center";
      div.style.flexWrap = "wrap";
      this.appendChild(div);
      div.style.margin = "auto";
      colChart(div, result, this.selectedPeriod, this.isMobile(), this.filter);
    }
    this.getApplication().stopLoader();
  }

  getData = async () => {
    if (this.filter) {
      this.selectedPeriod = this.PERIODS.filter(p => p.id == this.filter.year)[0];
      this.params.level = this.filter.detail;
    }

    if (!isEmptyObject(this.PERIODS)) {


      if (this.PERIODS && this.PERIODS.length > 0) {
        this.params.period = this.selectedPeriod.id;
        
        this.params.fromDate = this.selectedPeriod.initiationDate;
        this.params.toDate = this.selectedPeriod.deadline;
          

      }
      this.ACCOUNTS = await getAccounting(this.params).catch((error) => null);
      console.log(this.PERIODS);
      console.log(this.ACCOUNTS);
    }

    return this.ACCOUNTS;
  };
}
window.customElements.define("aon-graphics-trial", AonGraphicsTrial);
