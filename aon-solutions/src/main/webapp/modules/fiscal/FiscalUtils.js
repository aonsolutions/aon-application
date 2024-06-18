import { TAG } from "../../environments/environments.js";
import { formatNumber } from "../../services/utils.js";
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

const getModelNew = (model)=> {
  const newModel = TAX_ENUMS.TAX_MODEL_NUMBER[model.model];
  const statusText = TAX_ENUMS.TAX_STATUS[model.status];
  let color = "";

  const statusHtml = createStatusDot(model.status);
    
  if(["PENDING", "CUSTOMER_CHECK"].includes(model.status))  {
    color = "fin";
  } else if("FINISHED" === model.status) {
    color = "in";
  }

  let modelText = newModel;
  if(newModel === "111" && model.administration === "ALAVA") {
    modelText = "110";
  } 

  let hacienda = getModelTerritory(model.administration);

  const lettersHtml = /*html*/`<div class="profile-letters size ${color}" title="${statusText}">${modelText}</div>`;
  
  return {
    ...model,
    resultFormat: !isNaN(model.result) ? formatNumber(model.result, 2, "EUR") : null,
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

export const FiscalUtils = {
    createImgAdmin,
    getPathImg,
    getModelNew,
    groupBy
}