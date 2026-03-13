import { TAG } from "../../environments/environments.js";
import { getModelsFiscal } from "../../services/fiscalService.js";
import { formatNumber, sortBy } from "../../services/utils.js";
import { TAX_ENUMS } from "./FiscalEnums.js";

const createImgAdmin = (administration) => {
  const img = document.createElement(TAG.IMG);
  img.id = "imgAeat";
  img.src = getPathImg(administration);
  return img;
}

const getPathImg = (administration)=>{
  const path = "assets/img/";
  const {TAX_ADMIN} = TAX_ENUMS;
  let src = "aeat.png";
  switch(administration){
    case TAX_ADMIN.ALAVA:
      src = "aeat_alava.png";
      break;
    case TAX_ADMIN.BIZKAIA:
      src = "aeat_biskaia.png";
      break;
    case TAX_ADMIN.GIPUZKOA:
      src = "aeat_gipuzcoa.png";
      break;
    case TAX_ADMIN.NAVARRA:
      src = "aeat_navarra.png";
      break;
  }
  return path+src;
}

const getModelNumberHtml = (administration, model)=>{
  const newModel = TAX_ENUMS.TAX_MODEL_NUMBER[model];
  let modelText = newModel;
  if(newModel === "111" && administration === "ALAVA") {
    modelText = "110";
  } 

  return /*html*/`<div id = "aonSideNavFiscalCircle" class = "aonSideNavFiscalCircle">${modelText}</div>`;
}

const getModelNumber = (administration, model)=>{
  const newModel = TAX_ENUMS.TAX_MODEL_NUMBER[model];
  let modelText = newModel;
  if(newModel === "111" && administration === "ALAVA") {
    modelText = "110";
  }
  return modelText;
}

const getModelNew = (model)=> {
  const newModel = TAX_ENUMS.TAX_MODEL_NUMBER[model.model];
  const statusText = TAX_ENUMS.TAX_STATUS[model.status];
  let color = "";

  const statusHtml = createStatusDot(model.status);
    
  if(["PENDING", "CUSTOMER_CHECK"].includes(model.status))  {
    color = "pause";
  } else if("FINISHED" === model.status) {
    color = "in";
  }

  let modelText = newModel;
  if(newModel === "111" && model.administration === "ALAVA") {
    modelText = "110";
  } 

  let hacienda = getModelTerritory(model.administration);

  const lettersHtml = /*html*/`<div id="aonFiscalTableDiv" class="profile-letters size ${color}" title="${statusText}">${modelText}</div>`;
  
  return {
    ...model,
    resultFormat: !isNaN(model.result) ? formatNumber(model.result, 2, 2, "EUR") : null,
    periodText: TAX_ENUMS.TAX_PERIOD[model.period],
    modelText: TAX_ENUMS.TAX_MODEL_TEXT[newModel],
    typeText: TAX_ENUMS.TAX_TYPE[model.type],
    statusText,
    statusHtml,
    lettersHtml,
    newModel,
    hacienda
  }

}

const getModelTerritory = (territory) => {
  switch (territory) {
    case "COMMON_TERRITORY":
      return "AEAT";
    default:
      return territory.charAt(0) + territory.slice(1).toLowerCase();
  }
}

const createStatusDot = (status) => {
  let statusPanel = document.createElement(TAG.DIV);
  statusPanel.style.display = "flex";
  statusPanel.style.gap = ".5rem";
  statusPanel.style.alignItems = "center";
  statusPanel.style.fontWeight = "bold";
  statusPanel.style.maxWidth = "6rem";
  statusPanel.title = TAX_ENUMS.TAX_STATUS[status];

  let span = document.createElement(TAG.DIV);
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

  let description = document.createElement(TAG.SPAN);
  description.innerText = TAX_ENUMS.TAX_STATUS[status];

  statusPanel.appendChild(span);
  statusPanel.appendChild(description);

  let elementHTML = document.createElement(TAG.DIV);
  elementHTML.appendChild(statusPanel);

  return elementHTML.innerHTML;
}

const groupBy = (list, keyGetter) =>{
  const map = new Map();
  for(let it in list){
      const item = list[it];
      const key = keyGetter(item);
      const collection = map.get(key);
      if (!collection) {
        map.set(key, [item]);
      } else {
        collection.push(item);
      }
  }
  return map;
}

const getFutureFiscalFilter = async () => {
    const datos = await getModelsFiscalData();

    let orderDatos = [];
    if (datos)
      orderDatos = sortBy(datos, "year", "desc").map((model) => getModelNew(model) );

    const result = orderDatos.filter(function (a) {
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

    let resultReverse = result.reverse();

    if (resultReverse || resultReverse.length !== 0) {
      let period;
      let periodText;
      let year;

      if (resultReverse[0].period == "T1") {
        period = "T2";
        periodText = "2º Trim. " + resultReverse[0].year;
        year = resultReverse[0].year;
      } else if (resultReverse[0].period == "T2") {
        period = "T3";
        periodText = "3º Trim. " + resultReverse[0].year;
        year = resultReverse[0].year;
      } else if (resultReverse[0].period == "T3") {
        period = "T4";
        periodText = "4º Trim. " + resultReverse[0].year;
        year = resultReverse[0].year;
      } else {
        period = "T1";
        periodText = "1º Trim. " + (resultReverse[0].year + 1);
        year = resultReverse[0].year + 1;
      }

      return {
        year: year,
        period: period,
        title: periodText,
        periodText: periodText,
      };
    
    } else return null;
}

const getModelsFiscalData = async () => {
  try {
    const datos = await getModelsFiscal();

    if (datos) {
      let sortData = sortBy(datos, "year", "desc")
        .sort((a, b) => a.period.localeCompare(b.period))
        // .filter(({ status }) => status !== "PENDING")
        .map((model) => FiscalUtils.getModelNew(model));

      return sortData;
    }
  } catch (error) {
    console.error(error);
    this.showError(error);
  }
}

export const FiscalUtils = {
    createImgAdmin,
    getPathImg,
    getModelNumberHtml,
    getModelNumber,
    getModelNew,
    groupBy,
    getFutureFiscalFilter
}