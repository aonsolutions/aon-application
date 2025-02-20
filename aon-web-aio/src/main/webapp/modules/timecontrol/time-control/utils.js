import { MSG, TAG } from "../../../environments/environments.js";

import { AonSelect } from "../../../components/aon-select.js";
import { APRIL, AUGUST, DECEMBER, FEBRUARY, JANUARY, JULY, JUNE, MARCH, MAY, NOVEMBER, OCTOBER, SEPTEMBER } from "../../../environments/msg.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";

export const StringTwoLetters = (str) => {
  let newStr = "";
  if (str) {
    let newArray = str.trim().split(" ");
    if (newArray.length > 1) {
      newArray = newArray.filter(Boolean);
      newStr =
        newArray[0].substr(0, 1).toUpperCase() +
        newArray[1].substr(0, 1).toUpperCase();
    } else {
      newStr =
        newArray[0].substr(0, 1).toUpperCase() +
        newArray[0].substr(1, 1).toUpperCase();
    }
  }
  return newStr;
};

export const firstLetters = (l) => l.replace(/^.{1}/g, l[0].toUpperCase());

export const dateCustomDayHour = (d) => {
  let day = AonDateUtils.lastThreeDayStr(d);
  if(day) day = firstLetters(day)+", "+ AonDateUtils.setTime(d);
  return day;
}

export const timeHour = (time) => {
  let arr = AonDateUtils.timeParser(time).split(":");
  return `${arr[0]}:${arr[1]}`;
}

/**
 * 
 * @param {HTMLElement} application component
 * @param {HTMLElement} aonPresenceList component
 * @param {String} type excel, pdf
 */
export const modalReport = (application, aonPresenceList, type) => {
  let d = application.getDialog();
    if(d){
      d.clear();
      if (!application.isMobile()) d.width = '400px';
      d.setTitle(MSG.FILTERS);
      d.setContent(createContent(type));
      d.open();
      
      d.addSendAction(() =>{
        const aonSelectYear = document.getElementById("year").value;
        if(type.indexOf("pdf") >= 0){ 
          const aonSelectMonth = document.getElementById("month").value;
          if(aonSelectMonth){
            aonPresenceList.getTimeControlPdf(`${aonSelectYear}-${aonSelectMonth}-01`);
          }
        } else {
          aonPresenceList.getTimeControlExcel(aonSelectYear);
        }
        d.close();
      }, "Aceptar");
    }
}

/**
 * 
 * @param {String} type excel, pdf
 */
const createContent = (type="")=> {

  const div = document.createElement(TAG.DIV);
  div.style.display = "flex";
  const aonSelectYear = createSelect("year", MSG.YEAR);
  aonSelectYear.options= JSON.stringify(getYears());
  aonSelectYear.value = new Date().getFullYear();
  div.appendChild(aonSelectYear);

  if(type.indexOf("pdf") >= 0){
    const aonSelectMonth = createSelect("month", MSG.MONTH);
    aonSelectMonth.options= JSON.stringify(getMonths());
    div.appendChild(aonSelectMonth);
  }  else {
    aonSelectYear.style.width = "100%";
  }
  return div;
}

const createSelect = (id, title) => {
  const aonSelect = new AonSelect();
  aonSelect.id = id;
  aonSelect.title = title;
  aonSelect.style.margin = "5px";
  return aonSelect;
}

const getMonths  = () => [
  {
    name:JANUARY,
    value:"01"
  },
  {
    name:FEBRUARY,
    value:"02"
  },
  {
    name:MARCH,
    value:"03"
  },
  {
    name:APRIL,
    value:"04"
  },  
  {
    name:MAY,
    value:"05"
  },  
  {
    name:JUNE,
    value:"06"
  },  
  {
    name:JULY,
    value:"07"
  },  
  {
    name:AUGUST,
    value:"08"
  },  
  {
    name:SEPTEMBER,
    value:"09"
  },  {
    name:OCTOBER,
    value:"10"
  },  
  {
    name:NOVEMBER,
    value:"11"
  },  
  {
    name:DECEMBER,
    value:"12"
  },
];

const getYears = () => [
  {
    name: (new Date().addYear(-2)).getFullYear(),
    value:(new Date().addYear(-2)).getFullYear()
  },
  {
    name: (new Date().addYear(-1)).getFullYear(),
    value:(new Date().addYear(-1)).getFullYear()
  },
  {
    name:new Date().getFullYear(),
    value:new Date().getFullYear()
  },
  
];
