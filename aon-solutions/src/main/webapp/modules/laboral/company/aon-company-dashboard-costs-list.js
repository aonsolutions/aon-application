import { AonElement } from "../../../components/AonElement.js";
import { AonIframe } from "../../../components/aon-iframe.js";
import { formatNumber, isEmptyObject, sortBy } from "../../../services/utils.js";
import { getCompanyCosts } from "../../../services/service.js";
import { PAYROLL_VIEWS } from "../PayrollEnums.js";
import { CompanyPieChart } from "./CompanyPieChart.js";
import { CONSTANT, CSS, MSG, TAG } from "../../../environments/environments.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";

export class AonCompanyDashboardCostsList extends AonElement {
  TABLE_ID;
  static get observedAttributes() {
    return [CONSTANT.FILTER];
  }

  get filter() {
    return JSON.parse(this.getAttribute(CONSTANT.FILTER));
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (CONSTANT.FILTER === name) this.getTable();
  }

  filterPeriod;

  constructor(period) {
    super();
    this.filterPeriod = period;
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || PAYROLL_VIEWS.AON_COMPANY_COSTS_LIST;
    this.TABLE_ID = this.id + "Table";
  }

  async build() {
    await this.getTable();
  }

  async getTable() {
    await this.paintPieChar();
  }

  async paintPieChar() {
    const iframeId = this.id+ "Iframe";
    let startDate = new Date();
    let endDate = new Date();
    let title = MSG.RESUME_COSTS;
    let workplaceText = "";
    let total = 0;

    let aonIframe = this.getElement(iframeId);
    if(!aonIframe){
      aonIframe =  new AonIframe();
      aonIframe.id = iframeId;
      this.appendChild(aonIframe);
      await aonIframe.load();
    } else {
      aonIframe.clearContent();
    }

    const main = document.createElement(TAG.DIV);
    main.style.textAlign = "center";
    main.style.height    = "100%";
    main.style.overflowY = "auto";
    main.className = CSS.MATERIAL_SCROLL;
    aonIframe.setContent(main);

    //-----TITLE--------------
    let divTitle = this.createElement(TAG.DIV);
    divTitle.style.color  = "grey";
    divTitle.style.fontWeight ="500";
    main.appendChild(divTitle);

    try {
      const data = await this.getData();
      if(data && data.length > 0 ){
      
        await aonIframe.loadChart();

        startDate = new Date(data[0].startDate);
        endDate   = new Date(data[0].endDate);

        //-----DIV CHART--------------
        let div = this.createElement(TAG.DIV);
        div.id = this.id+ "pieChart";
        div.style.textAlign = "center";
        main.appendChild(div);
      
        const resp = await CompanyPieChart.paintPieChart(data, div, aonIframe);
        
        total = resp.total;

        workplaceText = resp.workplaceText;
      }

      let startDateText = AonDateUtils.getMonthYear(startDate),
      endDateText = AonDateUtils.getMonthYear(endDate);
      
      if(startDateText === endDateText){
        title = title + " "+ startDateText;
      } else {
        title = `${title} ${startDateText} - ${endDateText}`;
      }

      title = `${title}<br> ${workplaceText} <span style="color:black;font-weight:600;">${formatNumber(total, 2, "EUR")}<span>`;

      divTitle.innerHTML = title;
    } catch (error) {
      console.log(error);
    }
  }

  async getData() {
    let data = [];
    try {
      let datos = await getCompanyCosts(this.filterPeriod);
      if (!isEmptyObject(datos)) {
        sortBy(datos, "employee", "asc").map((resp) => {
         const lettersType = this.getTypeSalaryText(
            resp.salaryType
          );
          const lettersHtml = `<div class="profile-letters ${lettersType.color}">${lettersType.typeReduce}</div>`;
          const obj = {
            ...resp,
            lettersHtml,
            name: resp.employee,
            salaryType: lettersType.type,
            workplaceName: resp.workplace,
          };
          data.push(obj);
        });
      }
    } catch (e) {
      console.log(e);
    }
    return data;
  }

  getTypeSalaryText(type) {
    let obj = { color: "", type: "", typeReduce: "" };
    switch (type) {
      case "SALARY":
        obj.type = "NOMINA";
        break;
      case "EXTRA":
        obj.type = "EXTRA";
        obj.color = "in";
        break;
      case "SETTLE":
        obj.type = "FINIQUITO";
        obj.color = "fin";
        break;
      case "DELAY":
        obj.type = "ATRASOS";
        obj.color = "pause";
        break;
    }
    if (obj.type) obj.typeReduce = obj.type.toString().substr(0, 1);
    return obj;
  }

}



window.customElements.define("aon-company-dashboard-costs-list", AonCompanyDashboardCostsList);
