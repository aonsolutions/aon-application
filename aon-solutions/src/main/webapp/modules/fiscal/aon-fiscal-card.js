import { AonElement } from "../../components/AonElement.js";
import { getDomainUserRoles } from "../../services/companyService.js";
import { formatNumber } from "../../services/utils.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { CSS } from "../../environments/environments.js";
import { FISCAL_VIEWS } from "./FiscalEnums.js";
import { getModelsFiscal } from "../../services/fiscalService.js";
import { sortBy } from "../../services/utils.js";
import { FiscalUtils } from "./FiscalUtils.js";
import { TAG } from "../../environments/environments.js";
import { MATERIAL_ICONS } from "../../environments/environments.js";

export class AonFiscalCard extends AonElement {
  AON_FISCAL;
  MODELS = [];
  BANKS=[];
  dur;
  TABLE_ID;
  content;
  _filter;

  defaultFilter;
  
  constructor(filter) {
    super();
    this.defaultFilter = filter;
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
    this.TBODY = "tbody";
    this.id = "aonFiscalCard";
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
    cardContent.id = "fiscalCardTable";
    this.appendChild(cardContent);

    let totalDiv = this.createElement(TAG.DIV);
    totalDiv.id = "fiscalTotalDiv";
    this.appendChild(totalDiv);
  }

  buildToolbar() {
    this.getModelsFiscal().then(filteredData => {
      this.getTable(filteredData);
    });
  }

  async getModelsFiscal() {
    if(!this.MODELS.length){
      try {
        const datos = await getModelsFiscal();
        if (datos) {
          this.MODELS = sortBy(datos,'year','desc')
          .map((model) => FiscalUtils.getModelNew(model));
        }

        const filter = await this.defaultFilter;
        let filterDatos = this.MODELS.filter(dato => { return dato.year == filter.year && dato.period === filter.period});
        return filterDatos;

      } catch (error) {
        console.error(error);
        this.showError(error);
      }
    } else {
      return this.MODELS;
    }
    
  }

  getDataForKey(models, key){
    let datas = models.map(m => m[key]);
    return datas.filter((item, pos) => datas.indexOf(item) === pos);
  }

  getTable(modelDatas) {
    let content = this.getElement("fiscalCardTable");
    this.removeAllChildNodes(content);

    modelDatas.forEach(modelData => {
      let row = this.createElement(TAG.DIV);
      row.className = CSS.AON_FLEX;
      row.style.justifyContent = "space-between";
      row.style.width = "100%";
      row.style.borderBottom = "1px solid #ddd";
      row.style.padding = "1rem 0";

      let leftContent = this.createElement(TAG.DIV);
      leftContent.className = CSS.AON_FLEX;
      leftContent.style.gap = "1rem";
      leftContent.style.alignContent = "center";
      leftContent.style.alignItems = "center";

      let description = this.createElement(TAG.SPAN);
      description.style.fontSize = "1.2rem";
      description.style.color = "#fb982e";
      description.style.fontWeight = "500";
      description.innerHTML = "Modelo " + modelData.newModel;
      leftContent.appendChild(description);

      let iva = this.createElement(TAG.SPAN);
      iva.style.color = "rgb(120, 120, 133)";
      iva.innerHTML = this.getModelType(modelData.newModel);
      leftContent.appendChild(iva);

      let rightContent = this.createElement(TAG.DIV);
      rightContent.className = CSS.AON_FLEX;
      rightContent.style.alignItems = "center";
      rightContent.style.gap = "1rem";

      let status = this.createElement(TAG.SPAN);
      status.style.textAlign = "center";
      status.appendChild(this.createStatus(modelData.status));
      rightContent.appendChild(status);

      let amount = this.createElement(TAG.SPAN);
      amount.style.fontWeight = "bold";
      amount.style.minWidth = "5rem";
      amount.style.textAlign = "right";
      amount.innerHTML = formatNumber(modelData.result, 2, "EUR");
      rightContent.appendChild(amount);

      row.appendChild(leftContent);
      row.appendChild(rightContent);

      content.appendChild(row);
    });

    const fiscalTotalDiv = this.getElement("fiscalTotalDiv");
    fiscalTotalDiv.className = CSS.AON_CARD_TOTAL;
    fiscalTotalDiv.innerHTML = this.getTotal(modelDatas);
  }

  removeAllChildNodes(parent) {
    while (parent.firstChild) {
        parent.removeChild(parent.firstChild);
    }
}

  getModelType(model){
    switch (model.charAt(0)) {
      case "1":
        return "IRPF"
      default:
        return "IVA";
    }
  }

  getTotal(models){
    let total = models.reduce((t, model) => t + model.result, 0);
    return formatNumber(total, 2, "EUR");
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

  get getFilterModels(){
    const result = this.MODELS.filter(function (a) {
      var key = a.year + '|' + a.period;
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
  
      if(aSize == bSize)
      {
          return (aLow < bLow) ? -1 : (aLow > bLow) ? 1 : 0;
      }
      else
      {
          return (aSize < bSize) ? -1 : 1;
      }
    });

    return result.reverse();
  }

  filterTable(filter){
    let section2 = this.getElement("fiscalTitleSection2");
    let titleSection2 = section2.firstChild;

    titleSection2.innerHTML = filter.title;
    let filterDatos = this.MODELS.filter(dato => { return dato.year == filter.year && dato.period === filter.period});
    this.getTable(filterDatos);
  }

}
window.customElements.define("aon-fiscal-card", AonFiscalCard);
