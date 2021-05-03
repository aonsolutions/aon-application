import { AonElement } from "../../../components/AonElement.js";
import {
  formatNumber,
  isEmptyObject,
  sortBy,
  waitEl,
  geMonthYear,
  setValueName
} from "../../../services/utils.js";
import {
  getWorkplaceCCCs,
  getCompanyCosts,
  getCompanyCostsExcel,
  getPeriodLaboral,
} from "../../../services/service.js";
import { PRESENCE_FILTER, SigninSidenav } from "../../signin/signinEnums.js";
import {
  PAYROLL_FILTER,
  PAYROLL_VIEWS,
} from "../PayrollEnums.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";
import { pieChar, addLegend} from "./pieChar.js";
import { MSG } from "../../../environments/environments.js";

export class AonCompanyCostsList extends AonElement {
  TABLE_ID;
  static get observedAttributes() {
    return ["filter"];
  }

  get filter() {
    return JSON.parse(this.getAttribute("filter"));
  }

  set filter(filter) {
    this.setAttribute("filter", JSON.stringify(filter));
  }

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if ("filter" === name) this.getTable();
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
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.applicationEl.addToolbarTitle(MSG.COMPANY_COSTS);
  }

  async build() {
    this.paintView();
    this.buildToolbar();
    await this.buildFilter();
    await this.getTable();
  }

  paintView() {
    this.innerHTML =  /*html*/`<aon-filter id="${this.id}Filter" title="${MSG.FILTERS}"></aon-filter>`;
  }


  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, (e) =>
      filterEl.openFilter()
    );
    this.applicationEl.addToolbarOption2(SigninSidenav.EXCEL, (e) =>
      this.getCompanyCostsExcel()
    );
  }

  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs([
      PAYROLL_FILTER[0],
      ...PRESENCE_FILTER,
    ]);
    aonFilter.addEventListener("applyFilter", ({ detail }) => {
      if (detail) {
        this.applicationParentEl.setDataFilter(detail);
      }
    });

    // ----------WORKPLACES ------------
    let workplaces = await getWorkplaceCCCs();
    let workplaceEl = this.getElement("workplace");
    workplaceEl.options = JSON.stringify(
      workplaces.map(({ workplace }) => ({
        name: workplace.description,
        value: workplace.id,
      }))
    );
    // ----------WORKPLACES END ------------

    //------------------PERIOD---------
    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(getPeriodLaboral());
    // if(filter && filter.period) periodEl.value = filter.period;
    periodEl.addEventListener("change", ({ detail }) => {
      if (detail) {
        const { startDate, endDate } = detail;
        setValueName("startDate", startDate);
        setValueName("endDate", endDate);
      }
    });
    // ----------PERIOD END ------------
    let startDateEl = this.getElement("startDate");
    startDateEl.addEventListener(
      "change",
      (ev) => (periodEl.value = "personalized")
    );
    let endDateEl =this.getElement("endDate");
    endDateEl.addEventListener("change",(ev) => (periodEl.value = "personalized") );
  }

  async getTable() {
    this.applicationEl = await waitEl("#"+this.applicationEl.id);
    this.applicationEl.startLoader();
    await this.paintPieChar();
    this.applicationEl.stopLoader();
  }

  async paintPieChar() {
    let startDate = new Date();
    let endDate = new Date();
    let title = MSG.RESUME_COSTS;
    let workplaceText = "";
    let id = this.id+ "pieChar";
    let idTitle = id + "Title";
    let total = 0;
    let div =   this.getElement(id) || this.createElement('div');
    let divTitle = this.getElement(idTitle) || this.createElement('div');
    div.innerHTML = "";
    try {
      const resp = await this.getData();
      if(resp && resp.length > 0 ){
        startDate = new Date(resp[0].startDate);
        endDate   = new Date(resp[0].endDate);
        divTitle.style.color  = "grey";
        divTitle.style.fontWeight ="500";
        divTitle.style.margin = "20px";
        divTitle.style.marginBottom = 0;
        divTitle.id = idTitle;
        div.id = id;
        div.style.textAlign = "center";
        divTitle.style.textAlign = "center";
        this.appendChild(divTitle);
        this.appendChild(div);
        let sumEnterpriseSs = resp.reduce((sum,key)=> sum + (parseFloat(key.enterpriseSS) - parseFloat(key.bonuses)),0); 
        let sumEmployeeSs = resp.reduce((sum,key)=>sum + (parseFloat(key.employeeSS) + parseFloat(key.otherDeductions)), 0); 
        let totalSS = sumEnterpriseSs + sumEmployeeSs;
        let importIrpf = resp.reduce((sum,key)=>sum + parseFloat(key.irpf), 0); 
        let totalLiquid = resp.reduce((sum,key)=>sum + parseFloat(key.liquid), 0); 
        total = sumEnterpriseSs + sumEmployeeSs + importIrpf + totalLiquid;
  
        let data = [
          ['SS Empresa', sumEnterpriseSs],
          ['SS Empleado', sumEmployeeSs],
          ['Total IRPF', importIrpf],
          ['Total Nominas', totalLiquid]
        ];

        const colors = ['#0051C6','#db4437', '#B3B3B3', '#5e97f6'];
        
        let options = { 
          slices: colors,
        };
        await pieChar(div, data, options, (evClick)=>{
          console.log(evClick);
        });
        
        data.splice(2, 0, ["Total SS", totalSS]);
        colors.splice(2, 0, "none");
        
        let newColor = colors.map(color=> {
          return {
            divColor: color,
            nameColor: 'grey',
            valueColor: 'grey'
          }
        });
        newColor[2].valueColor = newColor[3].valueColor =  newColor[4].valueColor = "black";
        

        let newData = data.map(el=> [el[0], formatNumber(el[1], 2, "EUR")]);
        await addLegend(div, newData, newColor, (evClick)=>{
          console.log(evClick);
        });

        let button = this.createElement('button');
        button.className = "aonButton";
        button.id = `${this.id}Nomina`;
        button.innerHTML = MSG.VIEW_PAYROLL;
        button.style.marginTop = "10px";
        div.appendChild(button);
        button.addEventListener('click',()=>{
          this.applicationParentEl.showView(PAYROLL_VIEWS.AON_PAYROLL_LIST);
        });

        let workplaceEl = this.getElement('workplace').querySelector('LI');
        if(workplaceEl && workplaceEl.textContent) workplaceText = workplaceEl.textContent+": ";
      }

      let startDateText = geMonthYear(startDate),
      endDateText = geMonthYear(endDate);
      if(startDateText === endDateText){
        title = title + " "+ startDateText;
      } else {
        title = `${title} ${startDateText} - ${endDateText}`;
      }
          
      title = `${title}<br> ${workplaceText} <span style="color:black;font-weight:600;">${formatNumber(total, 2, "EUR")}<span>`;

      divTitle.innerHTML = title;
    } catch (error) {}
  }

  async getData() {
    this.applicationEl.startLoader();
    let data = [];
    try {
      let filter = null;
      try {filter = {...this.applicationParentEl._filter};} catch (error) {}
      let datos = await getCompanyCosts(filter);
      if (!isEmptyObject(datos)) {
        if(datos[0] && datos[0].startDate){
          this.changeFilterTime({startDate: datos[0].startDate,endDate: datos[0].endDate, value:"personalized"});
        }
        sortBy(datos, "employee", "asc").map((resp) => {
          const lettersType = this.applicationParentEl.getTypeSalaryText(
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
        this.changeFilterTime();
      }
    } catch (e) {
      console.log(e);
    }
    this.applicationParentEl.changeFilter();
    this.applicationEl.stopLoader();
    return data;
  }

  changeFilterTime(obj = undefined){
    const value = !obj ? getPeriodLaboral("last_month") : obj;
    this.applicationParentEl._filter = {period:value.value, startDate:value.startDate,endDate: value.endDate};
  }

  async getCompanyCostsExcel() {
    let filter = this.applicationParentEl._filter;
    this.applicationEl.startLoading();
    try {
      await getCompanyCostsExcel({ ...filter });
    } catch (error) {
      this.showToast(error);
    }
    this.applicationEl.stopLoading();
  }
}

window.customElements.define("aon-company-costs-list", AonCompanyCostsList);
