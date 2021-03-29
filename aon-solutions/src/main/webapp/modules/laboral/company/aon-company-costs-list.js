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
import { pieChar, addLegend, addTrTableLegend } from "./pieChar.js";

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
    this.applicationEl.addToolbarTitle("Costes de empresa");
  }

  async build() {
    this.paintView();
    this.buildToolbar();
    await this.buildFilter();
    await this.getTable();
  }

  paintView() {
    let innerHTML = `<aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + `<aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = innerHTML;
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
    let filter = this.applicationParentEl._filter;
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
    // startDateEl.value = filter.startDate;
    startDateEl.addEventListener(
      "change",
      (ev) => (periodEl.value = "personalized")
    );
    let endDateEl =this.getElement("endDate");
    // endDateEl.value = filter.endDate;
    endDateEl.addEventListener(
      "change",
      (ev) => (periodEl.value = "personalized")
    );
  }

  async getTable() {
    this.applicationEl = await waitEl("#aonLaboral");
    this.applicationEl.startLoader();
    if(this.isMobile())await this.paintPieChar();
    else await this.getTableDesk();
    this.applicationEl.stopLoader();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("Nombre", "string", "name", "30%");
      aonTable.addColumn("C. Trabajo", "string", "workplaceName", "20%");
      aonTable.addColumn("Tipo", "string", "salaryType", "10%");
      aonTable.addColumn("Bruto", "number", "raw", "10%");
      aonTable.addColumn("Seg. Social", "number", "totalSS", "10%");
      aonTable.addColumn("Coste total", "number", "totalCost", "10%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        let sumTotal = {
          raw: 0,
          totalCost: 0,
          totalSS: 0,
        };
        resp.map((res) => {
          sumTotal.raw += parseFloat(res.raw);
          sumTotal.totalCost += parseFloat(res.totalCost);
          sumTotal.totalSS += parseFloat(res.totalSS);
          aonTable.addRow(
            {
              ...res,
              raw: formatNumber(res.raw, 2, "EUR"),
              totalSS: formatNumber(res.totalSS, 2, "EUR"),
              totalCost: formatNumber(res.totalCost, 2, "EUR"),
            },
            (el) => console.log(el)
          );
        });

        if (resp.length > 0) {
          aonTable.addRow(
            {
              salaryType: `<span style="font-weight:800;">TOTALES</span`,
              raw: formatNumber(sumTotal.raw, 2, "EUR"),
              totalSS: formatNumber(sumTotal.totalSS, 2, "EUR"),
              totalCost: formatNumber(sumTotal.totalCost, 2, "EUR"),
            },
            (el) => console.log(el)
          );
        }
      } catch (e) {
        console.log(e);
      }
    }
  }

  async paintPieChar() {
    const resp = await this.getData();
    if(resp && resp.length > 0 ){
      let filter = this.applicationParentEl._filter;
      let title = "Resumen de costes";
      let id = this.id+ "pieChar";
      let idTitle = id + "Title";
      let total = 0;
      let div =   this.getElement(id) || this.createElement('div');
      let divTitle = this.getElement(idTitle) || this.createElement('div');
      div.innerHTML = "";
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
      const startDateText = geMonthYear(new Date(filter.startDate)) ;
      const endDateText = geMonthYear(new Date(filter.endDate)) ;
      if(startDateText === endDateText){
        title = title + " "+ startDateText;
      } else {
        title = `${title} ${startDateText} - ${endDateText}`;
      }
      let sumEnterpriseSs = resp.reduce((sum,key)=> sum + (parseFloat(key.enterpriseSS) - parseFloat(key.bonuses)),0); 
      let sumEmployeeSs = resp.reduce((sum,key)=>sum + (parseFloat(key.employeeSS) + parseFloat(key.otherDeductions)), 0); 
      let totalSS = sumEnterpriseSs + sumEmployeeSs;
      let importIrpf = resp.reduce((sum,key)=>sum + parseFloat(key.irpf), 0); 
      let totalLiquid = resp.reduce((sum,key)=>sum + parseFloat(key.liquid), 0); 
      total = sumEnterpriseSs + sumEmployeeSs + importIrpf + totalLiquid;
      let data = [
        ['SS Empresa', sumEnterpriseSs],
        ['SS Empleado', sumEmployeeSs],
        ['Importe IRPF', importIrpf],
        ['Importe Nominas', totalLiquid]
      ];
      let colors = {
        0: { color: '#0051C6' },
        1: { color: '#db4437' },
        2: { color: '#B3B3B3' },
        3: { color: '#5e97f6' }
      };
      let options = { 
        slices: colors,
      };
      await pieChar(div, data, options, (evClick)=>{
        console.log(evClick);
      });
      let newData = data.map(el=> [el[0], formatNumber(el[1], 2, "EUR")]);
      let tableLegend = await addLegend(div, newData, colors, (evClick)=>{
        console.log(evClick);
      });
      // let tbody = tableLegend.querySelector('tbody');
      // let trButton = this.createElement('tr');
      // tbody.appendChild(trButton);
      // let tdButton =this.createElement('td');
      // trButton.appendChild(tdButton);

      let button = this.createElement('button');
      button.className = "aonButton";
      button.id = `${this.id}Nomina`;
      button.innerHTML = "Ver nóminas";
      button.style.marginTop = "10px";
      div.appendChild(button);
      button.addEventListener('click',()=>{
        this.applicationParentEl.showView(PAYROLL_VIEWS.AON_PAYROLL_LIST);
      });
      // addTrTableLegend
      // tbody.appendChild();
      let workplaceText = "";
      let workplaceEl = this.getElement('workplace').querySelector('LI');
      if(workplaceEl && workplaceEl.textContent) workplaceText = workplaceEl.textContent+": ";
     
      title = `${title}<br> ${workplaceText} <span style="color:black;font-weight:600;">${formatNumber(total, 2, "EUR")}<span>`;
      divTitle.innerHTML = title;
    }

 
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
          this.applicationParentEl._filter = {startDate: datos[0].startDate,endDate: datos[0].endDate, period:"personalized"};
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
        this.getFilterLastMonth();
      }
    } catch (e) {
      console.log(e);
    }
    this.applicationParentEl.changeFilter();
    this.applicationEl.stopLoader();
    return data;
  }

  getFilterLastMonth(){
    const valueDefault = getPeriodLaboral("last_month");
    this.applicationParentEl._filter = {period:valueDefault.value, startDate:valueDefault.startDate,endDate: valueDefault.endDate};
  }

  async getCompanyCostsExcel() {
    let filter = this.applicationParentEl._filter;
    this.applicationEl.startLoading();
    try {
      await getCompanyCostsExcel({ ...filter });
    } catch ({ message, type }) {
      if (message && type)
        this.applicationEl.getToast().start({ message, type });
    }
    this.applicationEl.stopLoading();
  }
}

window.customElements.define("aon-company-costs-list", AonCompanyCostsList);
