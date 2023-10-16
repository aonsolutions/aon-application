import { AonElement } from "../../../components/AonElement.js";

import { AonIframe } from "../../../components/aon-iframe.js";

import {
  formatNumber,
  isEmptyObject,
  sortBy,
  waitEl,
  setValueName
} from "../../../services/utils.js";
import {
  getWorkplaceCCCs,
  getCompanyCosts,
  getCompanyCostsExcel,
  getPeriodLaboral,
} from "../../../services/service.js";
import { PRESENCE_FILTER, SigninSidenav } from "../../timecontrol/signinEnums.js";
import {
  PAYROLL_FILTER,
  PAYROLL_VIEWS,
} from "../PayrollEnums.js";
import { CompanyPieChart } from "./CompanyPieChart.js";
import { CONSTANT, CSS, EVENT, MSG, TAG } from "../../../environments/environments.js";
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

  constructor() {
    super();
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
    // await this.searchValueDefault();
    await this.getTable();
  }

  // async searchValueDefault() {
  //   // ----------WORKPLACES ------------
  //   let workplaces = await getWorkplaceCCCs();
  //   let workplaceEl = this.getElement("workplace");
  //   workplaceEl.options = JSON.stringify(
  //     workplaces.map(({ workplace }) => ({
  //       name: workplace.description,
  //       value: workplace.id,
  //     }))
  //   );
  //   // ----------WORKPLACES END ------------

  //   //------------------PERIOD---------
  //   let periodEl = this.getElement("period");
  //   periodEl.options = JSON.stringify(getPeriodLaboral());
  //   periodEl.addEventListener(EVENT.CHANGE, ({ detail }) => {
  //     if (detail) {
  //       const { startDate, endDate } = detail;
  //       setValueName("startDate", startDate);
  //       setValueName("endDate", endDate);
  //     }
  //   });
  //   // ----------PERIOD END ------------
  //   let startDateEl = this.getElement("startDate");
  //   startDateEl.addEventListener(EVENT.CHANGE, () => periodEl.value = "personalized");
  //   let endDateEl =this.getElement("endDate");
  //   endDateEl.addEventListener(EVENT.CHANGE,() => periodEl.value = "personalized" );
  // }

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

        // CompanyPieChart.createButton(main, aonIframe, this);
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

    // aonIframe.firstChild.style.height = '';
  }

  async getData() {
    let data = [];
    try {
      let filter = null;
      try {filter = {...this.getApplicationParent()._filter};} catch (error) {}
      let datos = await getCompanyCosts(filter);
      if (!isEmptyObject(datos)) {
        // if(datos[0] && datos[0].startDate){
        //   this.changeFilterTime({startDate: datos[0].startDate,endDate: datos[0].endDate, value:"personalized"});
        // }
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
      } else {
        // this.changeFilterTime();
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
