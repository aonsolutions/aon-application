import { AonElement } from "../../components/AonElement.js";
import { CSS, TAG, EVENT } from "../../environments/environments.js";
import { isEmptyObject } from "../../services/utils.js";
import { AccoutingChart } from "./AccoutingChart.js";
import {
  getAccounting,
  getPeriods,
  PERIOD_FILTER,
} from "../../services/accountingService.js";
import { getPeriodAccounting } from "../../services/service.js";
import { ToolbarType } from "../../models/enums.js";
import { AonIframe } from "../../components/aon-iframe.js";
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
    byMonth: true,
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
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.TOOLBAR = this.id + "Toolbar";
  }

  async build() {

    if (!this.params.domain || !this.params.domainName) {
      try {
        let company = JSON.parse(localStorage.getItem("company"));
        this.params.domain = company.id;
        this.params.domainName = company.domain;
      } catch (error) {
        console.log(error);
      }
    }

    this.innerHTML = `
    <aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="Pérdidas y Ganancias"> </aon-toolbar>
    `;

    this.PERIODS = await getPeriods(this.params)
    .catch((error) => {
      console.log(error);
      return [];
    });

    let lastDateTime = Math.max.apply(null,this.PERIODS.map((p) => new Date(p.initiationDate).getTime()) );
    let lastPeriod = this.PERIODS.find((p) => new Date(p.initiationDate).getTime() == lastDateTime );

    this.selectedPeriod = lastPeriod;

    this.buildToolbar();
    this.buildPyGToolbar();
    await this.buildFilter();

    this.draw();
    if (this.selectedPeriod){
      this.getElement("year").value = this.selectedPeriod.id;
    }

    this.getElement("show").value = this.filter != null ? this.filter.show : "yearly";
    this.getElement("detail").value = this.params.level;
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    this.applicationEl.addSearchOption(!this.isMobile());
  }

  buildPyGToolbar() {
    const toolbar = this.getElement(this.TOOLBAR);
    if(toolbar){
      toolbar.removeButtons();
      toolbar.addButton2(ACTION.BACK, null);
    }
  }

  async buildFilter() {
    const application = this.getApplication();
    const btnSearch = application.getSearchButton();
    btnSearch.disabled = true;

    btnSearch.buildOptionsFilter(PERIOD_FILTER);//INPUTS

    btnSearch.addEventListener(EVENT.SEARCH_NEW,({ detail }) => {
      if (detail) {
        this.filter = detail;
        this.build();
      }
    });

    //------------------YEAR-----------
    const yearEl = this.getElement("year");
    let years = [];

    for (const element of this.PERIODS) {
      years.push({
        name: element.name,
        value: element.id,
      });
    }
    yearEl.setOptions(years);
    //------------------SHOW-----------
    const showEl = this.getElement("show");
    showEl.setOptions(getPeriodAccounting());

    //----------------DETAIL-----------
    const detailEl = this.getElement("detail");
    let detailsJson = [
      {
        name: "Resumido",
        value: 3,
      },
      {
        name: "Estándar",
        value: 5,
      },
      {
        name: "Detallado",
        value: 9,
      },
    ];
    detailEl.setOptions(detailsJson);
  }

  async draw() {

    const id = "chart_div";

    const iframeId = this.id+ "Iframe";
    let aonIframe = this.getElement(iframeId);
    if(!aonIframe){
      aonIframe =  new AonIframe();
      aonIframe.id = iframeId;
      this.appendChild(aonIframe);
      await aonIframe.load();
    } else {
      aonIframe.clearContent();
    }

    this.getApplicationParent().loader(`#${id}`, aonIframe.getDocument());

    const result = await this.getData();
    if (result) {
      await aonIframe.loadChart();

      let div = this.createElement(TAG.DIV);
      div.innerHTML = "";
      div.id = id;
      div.style.textAlign = "center";
      div.style.display = "flex";
      div.style.justifyContent = "center";
      div.style.flexWrap = "wrap";
      div.style.overflowY = "auto";
      div.style.height = "100%";
      div.style.alignItems = "center";
      div.className = CSS.MATERIAL_SCROLL;

      aonIframe.addContent(div);

      AccoutingChart.colChart(div, result, this.selectedPeriod, this.isMobile(), this.filter,  aonIframe, true);

      let sidenavBaseId = null;
      try {
        sidenavBaseId = this.isMobile() ? `${this.applicationEl.getMobileSidenav().id}Content` : this.applicationEl.getSidenav().id;
      } catch (e) {
        sidenavBaseId = "";
      }

      const el1 = this.getElement(`${sidenavBaseId}VistaTrimestral`);
      if (el1){
        el1.addEventListener(EVENT.CLICK, () => this.goChart(div, result, aonIframe, "quarterly"));
      }

      const el2 = this.getElement(`${sidenavBaseId}VistaAnual`);
      if (el2){
        el2.addEventListener(EVENT.CLICK, () => this.goChart(div, result, aonIframe, "yearly"));
      }
       
      const el3 = this.getElement(`${sidenavBaseId}VistaMensual`);
      if (el3){
        el3.addEventListener(EVENT.CLICK, () =>  this.goChart(div, result, aonIframe, "monthly"));
      }
    }
  }
  
  goChart(parent, result, aonIframe, showFilter){
    parent.innerHTML = "";

    if (!this.filter) {
      this.filter = new Object();
    }
   
    this.filter.show = showFilter;
    this.getElement("show").value = showFilter;

    AccoutingChart.colChart(
      parent,
      result,
      this.selectedPeriod,
      this.isMobile(),
      this.filter,
      aonIframe,
      true
    );
  }

  async getData() {
    if (this.filter) {
      this.selectedPeriod = this.PERIODS.find((p) => p.id == this.filter.year);
      this.params.level = this.filter.detail;
    }

    if (!isEmptyObject(this.PERIODS)) {
      if (this.PERIODS && this.PERIODS.length > 0) {
        this.params.period = this.selectedPeriod.id;

        this.params.fromDate = this.selectedPeriod.initiationDate;
        this.params.toDate = this.selectedPeriod.deadline;
      }
      this.ACCOUNTS = await getAccounting(this.params)
      .catch((err) => {
        this.showError(err)
        return null;
      });
    }

    return this.ACCOUNTS;
  };
}
window.customElements.define("aon-graphics-trial", AonGraphicsTrial);
