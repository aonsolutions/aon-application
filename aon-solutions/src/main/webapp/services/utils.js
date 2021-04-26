import { CONSTANT } from "../environments/environments.js";

const days = ['domingo', 'lunes', 'martes', 'miercoles', 'jueves', 'viernes', 'sabado'];

const months = ["enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"];

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

export const isEmptyObject = (obj) => !obj || (obj.constructor === Object &&  Object.keys(obj).length === 0);

export const removeEmpty = (obj) => {
  Object.keys(obj).forEach((key) =>  isEmptyObject(obj[key]) ?  delete obj[key] : null);
  return obj;
};

//order by obj, campo, order asc or desc
export const sortBy = (obj, value, orderBy='asc') =>  obj.sort((a, b) => 'asc' === orderBy.toLocaleLowerCase() ? (typeof a[value] === 'string') - (typeof b[value] === 'string') || a[value] > b[value] || -(a[value] < b[value]) : (typeof b[value] === 'string') - (typeof a[value] === 'string') || b[value] > a[value] || -(b[value] < a[value]));

export const waitEl = (selector)=> new Promise((resolve,reject)=>{
  let i = 0;
  let element = null;
  let interval = setInterval(()=> {
    i++;
    element = document.querySelector(selector);
    if (element) {
      clearInterval(interval);
      resolve(element);
    } else if(i >= 100){ // 10 seg
      clearInterval(interval);
      reject("Element empty");
    }
  }, 100); // check every 100ms
});

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
    ...form.querySelectorAll(CONSTANT.INPUTS_ALL)
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

export const addZero = (value, length) => value.toString().length < length ? addZero("0" + value, length) : value;

export const timePaser = (time) =>{
  let msecPerMinute = 1000 * 60;
  let msecPerHour = msecPerMinute * 60;

  // Calcular las horas , minutos y segundos
  let hours = Math.floor(time / msecPerHour );
  time = time - (hours * msecPerHour );

  let minutes = Math.floor(time / msecPerMinute );
  time = time - (minutes * msecPerMinute );

  let seconds = Math.floor(time / 1000 );
  return  addZero(hours, 2) + ':'+ addZero(minutes, 2) + ':'+ addZero(seconds, 2);
}


export const formatDate = (d) => {
  let date = new Date(d);
  let day = addZero(date.getDate(), 2);
  let month = addZero(date.getMonth() + 1, 2);
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

export const formatDateOrigin = (d) => {
  let date = new Date(d);
  let day = addZero(date.getDate(), 2);
  let month = addZero(date.getMonth() + 1, 2);
  let year = date.getFullYear();
  return year + '-' + month + '-' + day;
}

export const setDateTimestamp = (d) => {
  const date = new Date(d);
  return formatDate(date) + " " + setTime(date);
}

export const dayStr = (date) => {
  const now = new Date();
  let day = days[date.getDay()];
  if( (date.getFullYear() === now.getFullYear()) && (date.getMonth() === now.getMonth()) ){
    if(date.getDay() === now.getDay()){
      day = "hoy";
    } else if(date.getDay() === addDays(now, -1).getDay()){
      day = "ayer";
    }
  }
  return day;
}

export const setDateTpDay = (d)=>{
  const date = new Date(d);
  const day = dayStr(date);
  return day+", "+formatDate(date);
}

export const setDateTimestampDay = (d)=> setDateTpDay(new Date(d)) +" " + setTime(new Date(d));

export const setFullDate = (d) => {
  const date = new Date(d);
  const dayText = dayStr(date);
  const monthText = months[date.getMonth()];
  return `${dayText}, ${date.getDate()} de ${monthText} de ${date.getFullYear()}`;
}

export const setTime = (date)=> {
  const newDate = new Date(date);
  const hour =  addZero(newDate.getHours(), 2);
  const min  =  addZero(newDate.getMinutes(), 2);
  return hour+":"+min;
}

export const getDayMonth = (date) => {
  const d = new Date(date);
  const day = addZero(d.getDate(), 2);
  const month = months[d.getMonth()];
  return day + '-' + month;
}
export const geMonthYear = (date) => {
  const d = new Date(date)
  const month = months[d.getMonth()];
  return month+". "+ d.getFullYear();
}

export const formatNumber = (value = 0, decimals = 0, simbolo = undefined, locale = "de-DE") => {
  let options = { minimumFractionDigits: decimals, maximumFractionDigits: decimals};
  if(simbolo) { options.style = 'currency'; options.currency = simbolo;  }
  return  new Intl.NumberFormat(locale, options).format(value.toString().replace(",", "."));
}


export const handleError = (error)=>{
  if(typeof error === "string") error = JSON.parse(error);
  let {message, type} = error;
  type = type || CONSTANT.PRIMARY;
  return {message, type};
}

export const disabledForm = (formId, elems) => {
  let elems_disabled = CONSTANT.INPUTS_ALL;
  if (elems) elems_disabled = elems + ', ' + CONSTANT.INPUTS_ALL;
  [...document.getElementById(formId).querySelectorAll(elems_disabled)].map(el => {
      el.disabled = true;
  })
}


/**
 * 
 * @param {element html or undefined} element 
 * @param {*} fn return end elment
 */
export const scrollInfinite = (element, fn) => {
    if(element){
      element.addEventListener("scroll", async ({target:{scrollTop, scrollHeight, offsetHeight}}) => {
        if (scrollTop >= (scrollHeight - offsetHeight)) fn();
      });
    } else {
      element = document.body;
      window.addEventListener('scroll', ()=>{
        if ( (element.scrollTop + element.clientHeight) >= element.scrollHeight) fn();
     }) 
    }
}

/*
 * Creates a new component
 * @param {*} properties - json with properties
 *  
 *  EXAMPLE
 * {
 *    type : tag-type,
 *    id :  id,
 *    classes : [class1,class2],
 *    styles : {
 *        style : value 
 *    },
 *    data : {
 *       name : value
 *    }
 *    events : {
 *        event : function 
 *    },
 * }
 * 
 * @returns Object of the component
 */
 export const newComponent = (properties) =>{
  if(properties == undefined) properties = {};
  properties.element  =  document.createElement('div');
  properties.appendTo =  (e) => e.appendChild(properties.element);

  if(properties.type != undefined)    properties.element           = document.createElement(properties.type);
  if(properties.id   != undefined)    properties.element.id        = properties.id;
  if(properties.text != undefined)    properties.element.innerHTML = properties.text;

  if(properties.attributes    != undefined) 
      for (const key in properties.attributes)  
          properties.element.setAttribute(key,properties.attributes[key]);

  if(properties.dataset    != undefined) 
      for (const key in properties.dataset) 
          properties.element.dataset[key] = properties.dataset[key];

  if(properties.events     != undefined) 
      for (const key in properties.events)  
          properties.element.addEventListener(key,properties.events[key]);

  if(properties.styles != undefined) 
      for (const key in properties.styles)  
          properties.element.style.setProperty(key,properties.styles[key]);  

  if(properties.classes != undefined) 
      properties.classes.forEach(cl => properties.element.classList.add(cl));
 
  if(properties.data != undefined) 
  for (const key in properties.data)  
      properties.element.dataset[key] = properties.data[key];

  properties.clean = function(){properties.element.innerHTML = '';};

  return properties;
} 
