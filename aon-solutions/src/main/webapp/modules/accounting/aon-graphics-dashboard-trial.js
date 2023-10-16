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

export class AonDashboardGraphicsTrial extends AonElement {
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
    this.id = this.id || "aonGraphicsDashboardTrial";
    this.applicationEl = document.querySelector("#pygContent");
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    
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

    this.PERIODS = await getPeriods(this.params)
    .catch((error) => {
      console.log(error);
      return [];
    });

    let lastDateTime = Math.max.apply(null,this.PERIODS.map((p) => new Date(p.initiationDate).getTime()) );
    let lastPeriod = this.PERIODS.find((p) => new Date(p.initiationDate).getTime() == lastDateTime );

    this.selectedPeriod = lastPeriod;

    this.draw();

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

    // this.getElement("pygCardContent").loader(`#${id}`, aonIframe.getDocument());

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
      div.className = CSS.MATERIAL_SCROLL;

      aonIframe.addContent(div);

      AccoutingChart.colChart(div, result, this.selectedPeriod, this.isMobile(), this.filter,  aonIframe);
    }

    // aonIframe.firstChild.style.height = '';
    
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
      aonIframe
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
window.customElements.define("aon-dashboard-graphics-trial", AonDashboardGraphicsTrial);
