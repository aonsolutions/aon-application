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

  const statusHtml = createStatus(model.status);
  
  if(["PENDING", "CUSTOMER_CHECK"].includes(model.status))  {
    color = "fin";
  } else if("FINISHED" === model.status) {
    color = "in";
  }

  const lettersHtml = /*html*/`<div class="profile-letters size ${color}" title="${statusText}">${TAX_ENUMS.TAX_MODEL_NUMBER[model.model]}</div>`;
  
  return {
    ...model,
    resultFormat: !isNaN(model.result) ? formatNumber(model.result, 2, "EUR") : null,
    periodText: TAX_ENUMS.TAX_PERIOD[model.period],
    modelText: TAX_ENUMS.TAX_MODEL_TEXT[newModel],
    typeText: TAX_ENUMS.TAX_TYPE[model.type],
    statusText,
    statusHtml,
    lettersHtml,
    newModel
  }

}

const createStatus = (modeStatus) => {
  let colorStatus;

  switch (modeStatus) {
    case "PENDING":
      colorStatus = "lightgray";
      break;
    case "FINISHED":
      colorStatus = "rgb(227, 255, 171)";
      break;
    case "BATCHED":      
      colorStatus = "black";
      break;
    case "BLOCKED":
      colorStatus = "black";
      break;
    case "SENT":
      colorStatus = "transparent";
      break;
    case "MISSING":
      colorStatus = "black";
      break;
    case "CUSTOMER_CHECK":
      colorStatus = "lightyellow";
      break;
    case "CUSTOMER_ACCEPTED":
      colorStatus = "rgb(233, 255, 219)";
      break;
    case "CUSTOMER_REJECTED":
      //span.style.backgroundColor = "darkred";
      colorStatus = "darkred";
      break;
    default:
      //span.style.backgroundColor = "black";
      colorStatus = "black";
      break;
  }

  return /*html*/`<div style="padding: 0.5rem;border-radius: 0.2rem;background-color: ${colorStatus};font-weight: bold;max-width: 6rem;text-align: center;" title="${TAX_ENUMS.TAX_STATUS[modeStatus]}">${TAX_ENUMS.TAX_STATUS[modeStatus]}</div>`;

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