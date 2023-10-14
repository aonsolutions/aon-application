import { AonElement } from "../../../components/AonElement.js";
import { getDomainUserRoles } from "../../../services/companyService.js";
import { DomainUserRoles } from "../../../models/DomainUserRoles.js";
import { CSS } from "../../../environments/environments.js";
import { TAG } from "../../../environments/environments.js";
import { formatNumber, isEmptyObject, sortBy, waitEl, setValueName } from "../../../services/utils.js";
import { getEmployeeSalaries, getEnterpriseSalaries, getPeriodLaboral, getWorkplaceCCCs, getAllEmployeesWorkplace } from "../../../services/service.js";

export class AonPayrollCard extends AonElement {
  AON_FISCAL;
  MODELS = [];
  BANKS=[];
  dur;
  TABLE_ID;
  content;
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
    this.TBODY = "tbody";
  }

  getDur() {
    return this.dur;
  }

  build() {
    this.paintView();
    this.buildToolbar();
  }

  paintView() {
    let cardContent = this.createElement(TAG.DIV);
    cardContent.className = CSS.AON_FLEX_COLUMN;
    cardContent.id = "payrollCardTable";
    this.appendChild(cardContent);
  }

  buildToolbar() {
    this.getPayrolls().then(payrolls => {
      console.log("Payrolls");
      console.log(payrolls);
      this.getTable(payrolls)
    });
  }

  async getPayrolls() {
    let data = [];
    try {
      let filter = this.getApplicationParent()._filter;
      let datos = []; 
      if(this.dur.isEmployee())  {
        datos = await getEmployeeSalaries(filter);
        datos = sortBy(datos, 'endDate', 'desc').filter(({endDate})=> new Date(endDate) <= new Date());
      } else {
        datos = await getEnterpriseSalaries(filter);
        datos = sortBy(datos, 'employeeName', 'asc');
      }

      if (!isEmptyObject(datos)) {
        data = datos.map(res => ({
          ...res,
          name: res.employeeName,
          lettersHtml: this.getDivIconStyle(res.type),
          totalDeduction: formatNumber(res.totalDeduction, 2, "EUR"),
          totalLiquid: formatNumber(res.totalLiquid, 2, "EUR"),
          totalPayment: formatNumber(res.totalPayment, 2, "EUR"),
        }));
        this._list = data;
        if(this.searchFilter) data = this.filterSearch(["name", "workplaceName"], data);
      }
      
    } catch (e) {
      console.log(e);
    }
    return data;
  }

  getDivIconStyle(type){
    const {color, typeReduce} = this.getTypeSalaryText(type);
    let div = this.createElement("div");
    div.innerText = typeReduce;
    div.classList.add("profile-letters");
    if(color) div.classList.add(color);
    return div;
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

  getTable(payrolls) {
    const meses = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Deciembre"];

    let content = this.getElement("payrollCardTable");

    for (let index = 0; index < 5; index++) {
      const payroll = payrolls[index];
      
      let row = this.createElement(TAG.DIV);
      row.className = CSS.AON_FLEX;
      row.style.justifyContent = "space-between";
      row.style.width = "100%";
      row.style.borderBottom = "1px solid #ddd";
      row.style.padding = "1rem 0";

      let leftContent = this.createElement(TAG.DIV);
      leftContent.className = CSS.AON_FLEX;
      leftContent.style.gap = ".5rem";
      leftContent.style.alignContent = "center";
      leftContent.style.alignItems = "center";

      let icon = payroll.lettersHtml;
      leftContent.appendChild(icon);

      let month = this.createElement(TAG.SPAN);
      month.style.fontSize = "1.1rem";
      month.style.color = "rgb(51, 169, 169)";
      month.style.fontWeight = "500";
      month.style.minWidth = "6rem";
      month.style.textAlign = "left";
      
      month.innerHTML = meses[new Date(Date.parse(payroll.endDate)).getMonth()];
      leftContent.appendChild(month);

      let year = this.createElement(TAG.SPAN);
      year.style.fontSize = "1rem";
      year.style.color = "rgb(51, 169, 169)";
      year.style.fontWeight = "500";
      year.innerHTML = new Date(Date.parse(payroll.endDate)).getFullYear();
      leftContent.appendChild(year);

      let rightContent = this.createElement(TAG.DIV);
      rightContent.className = CSS.AON_FLEX;
      rightContent.style.alignItems = "center";
      rightContent.style.gap = "1rem";

      let amount = this.createElement(TAG.SPAN);
      amount.style.fontWeight = "bold";
      amount.style.minWidth = "5rem";
      amount.style.textAlign = "right";
      amount.innerHTML = payroll.totalLiquid;
      rightContent.appendChild(amount);

      row.appendChild(leftContent);
      row.appendChild(rightContent);

      content.appendChild(row);
    }
  }

  createStatus(status){
    let span = this.createElement(TAG.DIV);
    span.style.width = "10px";
    span.style.height = "10px";
    span.style.borderRadius = "50%";

    switch (status) {
      case "PENDING":
        span.title= "Pendiente";
        span.style.backgroundColor = "lightgray";
        break;
      case "FINISHED":
        span.title= "Finalizado";
        span.style.backgroundColor = "rgb(227, 255, 171)";
        break;
      case "BATCHED":
        span.title= "En Lote";        
        span.style.backgroundColor = "black";
        break;
      case "BLOCKED":
        span.title= "Bloqueado";
        span.style.backgroundColor = "black";
        break;
      case "SENT":
        span.title= "Presentado";
        span.style.backgroundColor = "rgb(62, 201, 70)";
        break;
      case "MISSING":
        span.title= "Desconocido";
        span.style.backgroundColor = "black";
        break;
      case "CUSTOMER_CHECK":
        span.title= "Envio a cliente";
        span.style.backgroundColor = "lightyellow";
        break;
      case "CUSTOMER_ACCEPTED":
        span.title= "Aceptado por cliente";
        span.style.backgroundColor = "rgb(233, 255, 219)";
        break;
      case "CUSTOMER_REJECTED":
        span.title= "Rechazado por cliente";
        span.style.backgroundColor = "darkred";
        break;
      default:
        span.title= "";
        span.style.backgroundColor = "black";
        break;
    }

    return span;
  }

}
window.customElements.define("aon-payroll-card", AonPayrollCard);
