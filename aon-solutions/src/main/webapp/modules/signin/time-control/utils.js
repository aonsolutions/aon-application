import { addDays, setTime, timePaser } from "../../../services/utils.js";

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
  const date = new Date(d);
  const now = new Date();
  if( (date.getFullYear() === now.getFullYear()) && (date.getMonth() === now.getMonth()) ){
    let day = null;
    if(date.getDay() === now.getDay()){
      day = "hoy";
    } else if(date.getDay() === addDays(now, -1).getDay()){
      day = "ayer";
    }
    if(day) return firstLetters(day)+", "+ setTime(date);
  }
  return null;
}

export const timeHour = (time) => {
  let arr = timePaser(time).split(":");
  return  `${arr[0]}:${arr[1]}`;
}
