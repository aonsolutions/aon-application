import { INPUTS_ALL } from '../environments/constants.js';

const days = ['Dom', 'Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'];

const months = ["ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"];

export const getReader = (file) => {
  return new Promise((resolve) => {
    const READER = new FileReader();
    READER.readAsDataURL(file);
    READER.onload = () => {
      const { result } = READER;
      const { name, size, type: contentType } = file;
      const base64File = result.split(',')[1];
      const datos = {
        content: base64File,
        contentType,
        contentEncoding: 'base64',
        name,
        size
      };
      resolve(datos);
    };
  });
}

export const isEmptyObject = (obj) => !obj || obj.constructor === Object &&  Object.keys(obj).length === 0;

//order by obj, campo, order asc or desc
export const sortBy = (obj, value, orderBy='asc') =>  obj.sort((a, b) => 'asc' === orderBy.toLocaleLowerCase() ? a[value] - b[value] : b[value] - a[value]);

export const isNumber = (n) => !isNaN(parseFloat(n)) && isFinite(n);

export const round = (value) => decimalAdjust('round', value, -2);

export const decimalAdjust = (type, value, exp) => {
  // Si el exp no está definido o es cero...
  if (typeof exp === 'undefined' || +exp === 0) {
    return Math[type](value);
  }
  value = +value;
  exp = +exp;
  // Si el valor no es un número o el exp no es un entero...
  if (isNaN(value) || !(typeof exp === 'number' && exp % 1 === 0)) {
    return NaN;
  }
  // Shift
  value = value.toString().split('e');
  value = Math[type](+(value[0] + 'e' + (value[1] ? (+value[1] - exp) : -exp)));
  // Shift back
  value = value.toString().split('e');
  return +(value[0] + 'e' + (value[1] ? (+value[1] + exp) : exp));
}

export const serializeForm = (form) => {
  let inputs = [
    ...form.querySelectorAll(INPUTS_ALL)
  ];
  let obj = {}
  inputs.filter(({name, value})=> value && value!= "undefined" && name!=null).map(({ name, value }) => obj[name] = value);
  return obj;
}

export const setValueName = (name, value) => {
  let el = document.querySelector(`[name="${name}"]`);
  if (el && value) el.value = value;
  return el;
}

export const timePaser = (time) =>{
  let msecPerMinute = 1000 * 60;
  let msecPerHour = msecPerMinute * 60;

  // Calcular las horas , minutos y segundos
  let hours = Math.floor(time / msecPerHour );
  time = time - (hours * msecPerHour );

  let minutes = Math.floor(time / msecPerMinute );
  time = time - (minutes * msecPerMinute );

  let seconds = Math.floor(time / 1000 );

  return (hours < 10 ? '0' : '') + hours + ':'
    + (minutes < 10 ? '0' : '') + minutes + ':'
    + (seconds < 10 ? '0' : '') + seconds;
}

export const timeHour = (time) =>{
 return timePaser(time).substr(0,5);
}

const formatDate = (d) => {
  let date = new Date(d);
  let day = date.getDate();
  if (day <= 9) day = '0' + day;
  let month = date.getMonth() + 1;
  if (month <= 9) month = '0' + month;
  let year = date.getFullYear();
  return day + '/' + month + '/' + year;
}

export const setDate = (date) => formatDate(date);

export const addDays = (date, day) => {
  let result = new Date(date);
  result.setDate(result.getDate() + day);
  return result;
}
export const addMonth = (date, month) => {
  let result = new Date(date);
  result.setMonth( result.getMonth()  + month);
  return result;
}
export const addYear = (date, year) => {
  let result = new Date(date);
  result.setFullYear( result.getFullYear() + year);
  return result;
}

export const firstDayWeek = (d) => {
  let result = new Date(d);
  return result.getDate() - result.getDay() + 1; 
}

export const lastDayWeek = (d) => firstDayWeek(new Date(d)) + 6;

export const formatDateOrigin = (d) => {
  let date = new Date(d);
  let day = date.getDate();
  if (day <= 9) day = '0' + day;
  let month = date.getMonth() + 1;
  if (month <= 9) month = '0' + month;
  let year = date.getFullYear();
  return year + '-' + month + '-' + day;
}

export const setDateTimestamp = (d) => {
  const date = new Date(d);
  return formatDate(date) + " " + setTime(date);
}

export const setDateTimestampDay = (d)=>{
  const date = new Date(d);
  const now = new Date();
  let day = days[date.getDay()];

  if(date.getDay() === now.getDay()){
    day = "Hoy";
  } else if(date.getDay() === addDays(now, -1).getDay()){
    day = "Ayer";
  }
  return day+", "+formatDate(date) + " " + setTime(date);
}

export const setTime = (date)=> {
  let hour = date.getHours();
  if (hour <= 9) hour = '0' + hour;
  let min  = date.getMinutes();
  if (min <= 9) min = '0' + min;
  return hour+":"+min;
}


export const getDayMonth = (date) => {
  const d = new Date(date);
  let day = d.getDate();
  if (day <= 9) day = '0' + day;
  const month = months[d.getMonth()];
  return day + '-' + month;
}