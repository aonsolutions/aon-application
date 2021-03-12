import { AonElement } from "../../../components/AonElement.js";
import { formatNumber, isEmptyObject, formatDate, sortBy, waitEl, geMonthYear, setValueName } from "../../../services/utils.js";
import { firstLetters } from "../../signin/time-control/utils.js";
import { getEmployeeSalaries, getEnterpriseSalaries, getPeriodLaboral, getWorkplaceCCCs, getAllEmployeesWorkplace } from "../../../services/service.js";
import {  PresenceFilterInput, SigninSidenav } from "../../signin/signinEnums.js";
import "../../../components/aon-table.js";
import "../../../components/aon-mobile-list.js";
import "../../../components/aon-filter.js";

export class AonPayrollList extends AonElement {
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

  initialize(){
    this.id = this.id || "aonPayrollList";
    this.TABLE_ID = this.id + "Table";
    this.aonLaboralEl = this.getApplication();
    this.aonLaboralParentEl = this.aonLaboralEl.getParent();
    this.aonLaboralEl.addToolbarTitle("Nóminas");
  }
  
  async build(){
    this.paintView();
    if(!this.aonLaboralParentEl.isEmployee()){
      this.buildToolbar();
      await this.buildFilter();
      this.aonLaboralParentEl.changeFilter();
    }
    await this.getTable();
  }

  paintView() {
    let innerHTML =  `<aon-filter id="${this.id}Filter" title="Filtros"></aon-filter>`;
    if (this.isMobile()) {
      innerHTML = innerHTML + ` <aon-mobile-list id='${this.TABLE_ID}' />`;
    } else {
      innerHTML = innerHTML + `<aon-table id='${this.TABLE_ID}' />`;
    }

    this.innerHTML = innerHTML;
  }

  buildToolbar() {
    this.aonLaboralEl.removeToolbarOptions();
    const filterEl = this.getElement(`${this.id}Filter`);
    this.aonLaboralEl.addToolbarOption2(SigninSidenav.FILTER, (e) =>
      filterEl.openFilter()
    );
  }


  async buildFilter() {
    let aonFilter = this.getElement(`${this.id}Filter`);
    aonFilter.setInputs([
      {
        type: "select",
        id: "workplace",
        name: "workplace",
        title: "Centro de trabajo",
      },
      {
        type: "select",
        id: "employee",
        name: "employee",
        title: "Trabajador",
        hidden:true
      },
      ...PresenceFilterInput
    ]);
    aonFilter.addEventListener("applyFilter", ({detail}) => {
      if(detail)this.aonLaboralParentEl.setDataFilter(detail);
    });
  
    // ----------WORKPLACES ------------
    let workplaces = await getWorkplaceCCCs();
    let workplaceEl = this.getElement("workplace");
    workplaceEl.options = JSON.stringify( workplaces.map(({workplace})=> ({ name: workplace.description, value: workplace.id})) );
    workplaceEl.addEventListener('change', ({detail}) => {
      if(detail) this.getEmployees(detail);
    });
    // ----------WORKPLACES END ------------

  
    //------------------PERIOD---------
    let periodEl = this.getElement("period");
    periodEl.options = JSON.stringify(await getPeriodLaboral());
    periodEl.addEventListener('change', ({detail}) => {
      if(detail){
        const {startDate, endDate} = detail;
        setValueName('startDate', startDate);
        setValueName('endDate', endDate);
      }
    });
     // ----------PERIOD END ------------

    this.getElement("startDate").addEventListener("change", (ev)=>periodEl.value = "personalized");
    this.getElement("endDate").addEventListener("change", (ev)=>periodEl.value = "personalized");
  }

  async getTable() {
    this.aonLaboralEl = await waitEl("#aonLaboral");
    this.aonLaboralEl.startLoader();
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
    this.aonLaboralEl.stopLoader();
  }



  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("Nombre", "string", "name", "30%");
      aonTable.addColumn("C. Trabajo", "string", "workplaceName", "20%");
      aonTable.addColumn("F. Inicio", "date", "startDate", "10%");
      aonTable.addColumn("F. Fin", "date", "endDate", "10%");
      aonTable.addColumn("Bruto", "number", "totalPayment", "10%");
      aonTable.addColumn("Deducciones", "number", "totalDeduction", "10%");
      aonTable.addColumn("Líquido", "number", "totalLiquid", "10%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          res.startDate = formatDate(res.startDate);
          res.endDate = formatDate(res.endDate);
          aonTable.addRow(res, (el) => this.aonEvent(el, res));
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getTableMobile() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      try {
        const resp = await this.getData();
        aonTable.removeAllLi();

        let isEmployee = this.aonLaboralParentEl.isEmployee();
        
        resp.map((res, idx) => {
          let options = {};
          let dateParse = firstLetters(geMonthYear(res.endDate));
          if(isEmployee){
            options.paddingTopTitle = "5px";
            options.iconHtmlCustom = `${res.lettersHtml} <span style="padding-top: 5px;float: right;color: rgba(0,0,0,.54);">${res.totalLiquid}</span>`;
            options.title = `${dateParse}`;
          } else {
            options.iconHtmlCustom = `${res.lettersHtml}`;
            options.title = res.name;
            options.subtitle = `${dateParse} <span style="float: right;">${res.totalLiquid}</span> `;
          }
          aonTable.addLi(options, idx, (el) => this.aonEvent(el, res));
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    this.aonLaboralEl.startLoader();
    let data = [];
    try {
      let filter = null;
      try {filter = {...this.aonLaboralParentEl._filter};} catch (error) {}

      let datos = null;
      if(this.aonLaboralParentEl.isEmployee()){
        datos = await getEmployeeSalaries(filter);
      } else {
        datos = await getEnterpriseSalaries(filter);
      }
      if (!isEmptyObject(datos)) {
        sortBy(datos, 'employeeName', 'asc') 
        .filter(({endDate})=> new Date(endDate) <= new Date())
        .map(({
            contract,
            employeeName,
            endDate,
            id,
            startDate,
            totalDeduction,
            totalLiquid,
            totalPayment,
            type,
            workplaceName,
          }) => {
              const lettersType = this.getTypeSalaryText(type);
              const lettersHtml = `<div class="profile-letters ${lettersType.color}">${lettersType.typeReduce}</div>`;
              const obj = {
                id,
                lettersHtml,
                contract,
                name: employeeName,
                startDate,
                endDate,
                totalDeduction: formatNumber(totalDeduction, 2, "EUR"),
                totalLiquid: formatNumber(totalLiquid, 2, "EUR"),
                totalPayment: formatNumber(totalPayment, 2, "EUR"),
                type,
                workplaceName,
              };
              data.push(obj);
          }
        );
      }
    } catch (e) {
      console.log(e);
    }
    this.aonLaboralEl.stopLoader();
    return data;
  }

  aonEvent({ target }, data) {
    const parent = this.aonLaboralParentEl;
    if(parent) parent.getSalary({salaryId:data.id});
  }

  async getEmployees(detail){
    try {
      let employeeEl = this.getElement("employee");
      let employees = await getAllEmployeesWorkplace({workplace: detail.value, allEmployees:true});
      if(employees.length>0) {
        employeeEl.options = JSON.stringify( 
          sortBy(employees, 'surName', 'asc').map(({name, surName, contractId})=> ({name:surName+" "+name, value:contractId}))
        );
        employeeEl.hidden =  false;
      }
      else employeeEl.hidden =  true;

    } catch (error) {
      console.log(error);
    }
  }

  getTypeSalaryText(type){
    let obj = {color:"",  type:"" ,typeReduce:""};
    switch(type){
      case "SALARY":
        obj.type="NOMINA";
      break;
      case "EXTRA":
        obj.type="EXTRA";
        obj.color = "in";
      break;
      case "SETTLE":
        obj.type="FINIQUITO";
        obj.color = "fin";
      break;
      case "DELAY":
        obj.type="ATRASOS";
        obj.color = "pause";
      break;
    }
    if(obj.type) obj.typeReduce = obj.type.toString().substr(0,1);
    return obj;
  }
  
}

window.customElements.define("aon-payroll-list", AonPayrollList);
