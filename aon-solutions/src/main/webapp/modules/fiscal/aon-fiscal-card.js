import { AonElement } from "../../components/AonElement.js";
import { getDomainUserRoles } from "../../services/companyService.js";
import { formatNumber } from "../../services/utils.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { CSS } from "../../environments/environments.js";
import { FISCAL_VIEWS } from "./FiscalEnums.js";
import { getModelsFiscal, getModels390Fiscal, getEstimationModelsFiscal } from "../../services/fiscalService.js";
import { sortBy } from "../../services/utils.js";
import { FiscalUtils } from "./FiscalUtils.js";
import { TAG } from "../../environments/environments.js";
import { MATERIAL_ICONS } from "../../environments/environments.js";

export class AonFiscalCard extends AonElement {
  AON_FISCAL;
  MODELS = [];
  MODELS390 = [];
  BANKS = [];
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
    this.style.display = "flex";
    this.style.flexDirection = "column";
    this.style.justifyContent = "space-between";
    this.style.height = "100%";

    let cardContent = this.createElement(TAG.DIV);
    cardContent.className = CSS.AON_FLEX_COLUMN;
    cardContent.id = "fiscalCardTable";
    this.appendChild(cardContent);

    let totalDiv = this.createElement(TAG.DIV);
    totalDiv.id = "fiscalTotalDiv";
    this.appendChild(totalDiv);
  }

  buildToolbar() {
    this.getModelsFiscal().then((modelDatas) => {
      this.getModels390Fiscal().then((model390Datas) => {
        this.getTable(modelDatas, model390Datas);
      });
    });
  }

  async getModelsFiscal() {
    if (!this.MODELS.length) {
      try {
        const datos = await getModelsFiscal();
        if (datos) {
          this.MODELS = sortBy(datos, "year", "desc").map((model) =>
            FiscalUtils.getModelNew(model)
          );
        } else {
          return [];
        }

        const filter = await this.defaultFilter;

        if(!filter) { return []; }

        let filterDatos = this.MODELS.filter((dato) => {
          return dato.year == filter.year && dato.period === filter.period;
        });
        return filterDatos;
      } catch (error) {
        console.error(error);
        this.showError(error);
      }
    } else {
      return this.MODELS;
    }
  }

  async getModels390Fiscal() {
    if (!this.MODELS390.length) {
      try {
        const datos = await getModels390Fiscal();
        if (datos) {
          this.MODELS390 = sortBy(datos, "year", "desc").map((model) =>
            FiscalUtils.getModelNew(model)
          );
        } else {
          return [];
        }

        const filter = await this.defaultFilter;

        if(!filter) { return []; }

        let filterDatos = this.MODELS390.filter((dato) => {
          return dato.year == filter.year;
        });
        return filterDatos;
      } catch (error) {
        console.error(error);
        this.showError(error);
      }
    } else {
      return this.MODELS390;
    }
  }

  getDataForKey(models, key) {
    let datas = models.map((m) => m[key]);
    return datas.filter((item, pos) => datas.indexOf(item) === pos);
  }

  getTable(modelDatas, model390Datas) {
    let content = this.getElement("fiscalCardTable");
    this.removeAllChildNodes(content);

    if(modelDatas && modelDatas.length === 0){
      let emptyMessage = this.createElement(TAG.DIV);
      emptyMessage.innerHTML = "No existen modelos fiscales";
      emptyMessage.style.fontWeight = "bold";

      content.style.height = "100%";
      content.appendChild(emptyMessage);

      let fiscalCard = this.getElement("fiscalCard");
      fiscalCard.style.display = "none";
    } else {
      let maxModels = modelDatas && modelDatas.length < 5 ? modelDatas.length : 5;
      let accumulatedModels = 0;
      let total390 = 0;

      // Model 390
      if(modelDatas[0].period === "T4" && model390Datas && model390Datas.length !== 0){
        const modelData = model390Datas[0];

        let row = this.createElement(TAG.DIV);
        row.className = CSS.AON_FLEX;
        row.style.justifyContent = "space-between";
        row.style.width = "100%";
        row.style.borderBottom = "1px solid #ddd";
        row.style.padding = ".4rem 0";

        let leftContent = this.createElement(TAG.DIV);
        leftContent.className = CSS.AON_FLEX;
        leftContent.style.alignItems = "center";
        leftContent.style.gap = "1rem";

        let leftContent2 = this.createElement(TAG.DIV);
        leftContent2.className = CSS.AON_FLEX;
        leftContent2.style.alignContent = "center";
        leftContent2.style.flexDirection = "column";
        leftContent.appendChild(leftContent2);

        let description = this.createElement(TAG.SPAN);
        description.style.fontSize = modelDatas.length > 5 ? "1rem" : "1.2rem";
        description.style.color = "#fb982e";
        description.style.fontWeight = "500";
        description.innerHTML = "Modelo " + this.getParsedModel(modelData);
        leftContent2.appendChild(description);

        let info = this.createElement(TAG.SPAN);
        info.style.color = "rgb(120, 120, 133)";
        info.style.fontSize = ".7rem";
        info.innerHTML = this.getModelDescription(modelData.newModel) + " Anual";
        leftContent2.appendChild(info);

        let territory = this.createElement(TAG.SPAN);
        territory.style.color = "rgb(120, 120, 133)";
        territory.style.fontSize = modelDatas.length > 5 ? ".8rem" : "1rem";
        territory.innerHTML = this.getModelTerritory(modelData.administration);
        leftContent.appendChild(territory);

        let rightContent = this.createElement(TAG.DIV);
        rightContent.className = CSS.AON_FLEX;
        rightContent.style.alignItems = "center";
        rightContent.style.gap = "1rem";

        let status = this.createElement(TAG.SPAN);
        status.style.textAlign = "center";
        status.style.cursor = "pointer";
        status.appendChild(this.createStatus(modelData.status));
        rightContent.appendChild(status);

        let amount = this.createElement(TAG.SPAN);
        amount.style.fontWeight = "bold";
        amount.style.minWidth = "5rem";
        amount.style.textAlign = "right";
        amount.innerHTML = formatNumber(modelData.result, 2, "EUR");
        rightContent.appendChild(amount);

        accumulatedModels += modelData.amount;
        total390 = modelData.result;

        row.appendChild(leftContent);
        row.appendChild(rightContent);

        content.appendChild(row);
      }

      for (let index = 0; index < maxModels; index++) {
        const modelData = modelDatas[index];

        let row = this.createElement(TAG.DIV);
        row.className = CSS.AON_FLEX;
        row.style.justifyContent = "space-between";
        row.style.width = "100%";
        row.style.borderBottom = "1px solid #ddd";
        row.style.padding = ".4rem 0";

        let leftContent = this.createElement(TAG.DIV);
        leftContent.className = CSS.AON_FLEX;
        leftContent.style.alignItems = "center";
        leftContent.style.gap = "1rem";

        let leftContent2 = this.createElement(TAG.DIV);
        leftContent2.className = CSS.AON_FLEX;
        leftContent2.style.alignContent = "center";
        leftContent2.style.flexDirection = "column";
        leftContent.appendChild(leftContent2);

        let description = this.createElement(TAG.SPAN);
        description.style.fontSize = modelDatas.length > 5 ? "1rem" : "1.2rem";
        description.style.color = "#fb982e";
        description.style.fontWeight = "500";
        description.innerHTML = "Modelo " + this.getParsedModel(modelData);
        leftContent2.appendChild(description);

        let info = this.createElement(TAG.SPAN);
        info.style.color = "rgb(120, 120, 133)";
        info.style.fontSize = ".7rem";
        info.innerHTML = this.getModelDescription(modelData.newModel);
        leftContent2.appendChild(info);

        let territory = this.createElement(TAG.SPAN);
        territory.style.color = "rgb(120, 120, 133)";
        territory.style.fontSize = modelDatas.length > 5 ? ".8rem" : "1rem";
        territory.innerHTML = this.getModelTerritory(modelData.administration);
        leftContent.appendChild(territory);

        let rightContent = this.createElement(TAG.DIV);
        rightContent.className = CSS.AON_FLEX;
        rightContent.style.alignItems = "center";
        rightContent.style.gap = "1rem";

        let status = this.createElement(TAG.SPAN);
        status.style.textAlign = "center";
        status.style.cursor = "pointer";
        status.appendChild(this.createStatus(modelData.status));
        rightContent.appendChild(status);

        let amount = this.createElement(TAG.SPAN);
        amount.style.fontWeight = "bold";
        amount.style.minWidth = "5rem";
        amount.style.textAlign = "right";
        amount.innerHTML = formatNumber(modelData.result, 2, "EUR");
        rightContent.appendChild(amount);

        accumulatedModels += modelData.result;

        row.appendChild(leftContent);
        row.appendChild(rightContent);

        content.appendChild(row);
      }

      // Create others row
      let total = modelDatas.reduce((t, model) => t + model.result, 0);
      total = total + total390;
     
      if (modelDatas.length > 5) {
        let row = this.createElement(TAG.DIV);
        row.className = CSS.AON_FLEX;
        row.style.justifyContent = "space-between";
        row.style.width = "100%";
        row.style.borderBottom = "1px solid #ddd";
        row.style.padding = ".4rem 0";

        let leftContent = this.createElement(TAG.DIV);
        leftContent.className = CSS.AON_FLEX;
        leftContent.style.alignItems = "center";
        leftContent.style.gap = "1rem";

        let description = this.createElement(TAG.SPAN);
        description.style.fontSize = "1rem";
        description.style.color = "#fb982e";
        description.style.fontWeight = "500";
        description.innerHTML = "Otros";
        leftContent.appendChild(description);

        let rightContent = this.createElement(TAG.DIV);
        rightContent.className = CSS.AON_FLEX;
        rightContent.style.alignItems = "center";
        rightContent.style.gap = "1rem";

        let amount = this.createElement(TAG.SPAN);
        amount.style.fontWeight = "bold";
        amount.style.minWidth = "5rem";
        amount.style.textAlign = "right";
        amount.innerHTML = formatNumber(total - accumulatedModels, 2, "EUR");
        rightContent.appendChild(amount);

        row.appendChild(leftContent);
        row.appendChild(rightContent);

        content.appendChild(row);
      }

      const fiscalTotalDiv = this.getElement("fiscalTotalDiv");
      fiscalTotalDiv.className = CSS.AON_CARD_TOTAL;
      fiscalTotalDiv.classList.add(CSS.AON_FISCAL_CARD_TOTAL);
      fiscalTotalDiv.innerHTML = this.getTotal(modelDatas, total390);
    }
  }

  getEstimationTable(estimationModels) {
    let content = this.getElement("fiscalCardTable");
    this.removeAllChildNodes(content);

    estimationModels = estimationModels.filter(estimationModel => estimationModel.amount && estimationModel.amount > 0);

    let maxModels = estimationModels && estimationModels.length < 5 ? estimationModels.length : 5;
    let accumulatedModels = 0;

    for (let index = 0; index < maxModels; index++) {
      const modelData = estimationModels[index];

      let row = this.createElement(TAG.DIV);
      row.className = CSS.AON_FLEX;
      row.style.justifyContent = "space-between";
      row.style.width = "100%";
      row.style.borderBottom = "1px solid #ddd";
      row.style.padding = ".8rem 0";

      let leftContent = this.createElement(TAG.DIV);
      leftContent.className = CSS.AON_FLEX;
      leftContent.style.alignItems = "center";
      leftContent.style.gap = "1rem";

      let description = this.createElement(TAG.SPAN);
      description.style.fontSize = estimationModels.length > 5 ? "1rem" : "1.2rem";
      description.style.color = "#fb982e";
      description.style.fontWeight = "500";
      description.innerHTML = modelData.description;
      leftContent.appendChild(description);

      let rightContent = this.createElement(TAG.DIV);
      rightContent.className = CSS.AON_FLEX;
      rightContent.style.alignItems = "center";
      rightContent.style.gap = "1rem";

      let amount = this.createElement(TAG.SPAN);
      amount.style.fontWeight = "bold";
      amount.style.minWidth = "5rem";
      amount.style.textAlign = "right";
      amount.innerHTML = formatNumber(modelData.amount, 2, "EUR");
      rightContent.appendChild(amount);

      accumulatedModels += modelData.amount;

      row.appendChild(leftContent);
      row.appendChild(rightContent);

      content.appendChild(row);
    }

    // Create others row
    let total = estimationModels.reduce((t, model) => t + model.amount, 0);
    if (estimationModels.length > 5) {
      let row = this.createElement(TAG.DIV);
      row.className = CSS.AON_FLEX;
      row.style.justifyContent = "space-between";
      row.style.width = "100%";
      row.style.borderBottom = "1px solid #ddd";
      row.style.padding = ".8rem 0";

      let leftContent = this.createElement(TAG.DIV);
      leftContent.className = CSS.AON_FLEX;
      leftContent.style.alignItems = "center";
      leftContent.style.gap = "1rem";

      let description = this.createElement(TAG.SPAN);
      description.style.fontSize = "1rem";
      description.style.color = "#fb982e";
      description.style.fontWeight = "500";
      description.innerHTML = "Otros";
      leftContent.appendChild(description);

      let rightContent = this.createElement(TAG.DIV);
      rightContent.className = CSS.AON_FLEX;
      rightContent.style.alignItems = "center";
      rightContent.style.gap = "1rem";

      let amount = this.createElement(TAG.SPAN);
      amount.style.fontWeight = "bold";
      amount.style.minWidth = "5rem";
      amount.style.textAlign = "right";
      amount.innerHTML = formatNumber(total - accumulatedModels, 2, "EUR");
      rightContent.appendChild(amount);

      row.appendChild(leftContent);
      row.appendChild(rightContent);

      content.appendChild(row);
    }

    const fiscalTotalDiv = this.getElement("fiscalTotalDiv");
    fiscalTotalDiv.className = CSS.AON_CARD_TOTAL;
    fiscalTotalDiv.classList.add(CSS.AON_FISCAL_CARD_TOTAL);
    fiscalTotalDiv.innerHTML = this.getEstimationTotal(estimationModels);
  }

  removeAllChildNodes(parent) {
    while (parent.firstChild) {
      parent.removeChild(parent.firstChild);
    }
  }

  getParsedModel(modelData) {
    let model = modelData.newModel;
    let territory = modelData.administration;
    if(model === "111" && territory === "ALAVA"){
      return "110";
    } 
    return model;
  }

  getModelTerritory(territory) {
    switch (territory) {
      case "COMMON_TERRITORY":
        return "AEAT";
      default:
        return territory.charAt(0) + territory.slice(1).toLowerCase();
    }
  }

  getTotal(models, total390) {
    let total = models.reduce((t, model) => t + model.result, 0);
    return formatNumber(total + total390, 2, "EUR");
  }

  getEstimationTotal(models) {
    let total = models.reduce((t, model) => t + model.amount, 0);
    return formatNumber(total, 2, "EUR");
  }

  createStatus(status) {
    let span = this.createElement(TAG.DIV);
    span.style.width = "10px";
    span.style.height = "10px";
    span.style.borderRadius = "50%";

    switch (status) {
      case "PENDING":
        span.title = "Pendiente";
        span.style.backgroundColor = "lightgray";
        break;
      case "FINISHED":
        span.title = "Finalizado";
        span.style.backgroundColor = "rgb(227, 255, 171)";
        break;
      case "BATCHED":
        span.title = "En Lote";
        span.style.backgroundColor = "black";
        break;
      case "BLOCKED":
        span.title = "Bloqueado";
        span.style.backgroundColor = "black";
        break;
      case "SENT":
        span.title = "Presentado";
        span.style.backgroundColor = "rgb(62, 201, 70)";
        break;
      case "MISSING":
        span.title = "Desconocido";
        span.style.backgroundColor = "black";
        break;
      case "CUSTOMER_CHECK":
        span.title = "Envio a cliente";
        span.style.backgroundColor = "lightyellow";
        break;
      case "CUSTOMER_ACCEPTED":
        span.title = "Aceptado por cliente";
        span.style.backgroundColor = "rgb(233, 255, 219)";
        break;
      case "CUSTOMER_REJECTED":
        span.title = "Rechazado por cliente";
        span.style.backgroundColor = "darkred";
        break;
      default:
        span.title = "";
        span.style.backgroundColor = "black";
        break;
    }

    return span;
  }

  getModelDescription(model) {
    switch (model) {
      case "303":
        return "IVA";
      case "390":
        return "IVA";
      case "110":
        return "IRPF Nóminas";
      case "111":
        return "IRPF Nóminas";
      case "115":
          return "IRPF Arrendamiento";
      case "123":
          return "IRPF Arrendamiento";
      default:
        return "";
    }

    return span;
  }

  get getFilterModels() {
    const result = this.MODELS.filter(function (a) {
      var key = a.year + "|" + a.period;
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

      if (aSize == bSize) {
        return aLow < bLow ? -1 : aLow > bLow ? 1 : 0;
      } else {
        return aSize < bSize ? -1 : 1;
      }
    });

    return result.reverse();
  }

  filterTable(filter) {
    let section2 = this.getElement("fiscalCardTitleSection2");
    let titleSection2 = section2.firstChild;

    titleSection2.innerHTML = filter.title;
    let filterDatos = this.MODELS.filter((dato) => {
      return dato.year == filter.year && dato.period === filter.period;
    });
    let filter390Datos = this.MODELS390.filter((dato) => {
      return dato.year == filter.year && filter.period === "T4";
    });
    this.getTable(filterDatos, filter390Datos);
  }

  async filterEstimationTable(filter) {
    let section2 = this.getElement("fiscalCardTitleSection2");
    let titleSection2 = section2.firstChild;
    titleSection2.innerHTML = filter.title;

    const estimationModels = await getEstimationModelsFiscal(filter);
    // console.log(estimationModels);
    this.getEstimationTable(estimationModels);
  }
}
window.customElements.define("aon-fiscal-card", AonFiscalCard);
