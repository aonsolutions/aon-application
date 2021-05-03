import { AON_TAGS } from "../environments/aonTag.js";
import { DAYS, MONTHS } from "../environments/msg.js";

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

/**
 * 
 * @param {string} selector selector html
 * @returns element
 */
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

/**
 * 
 * @param {element HTML} form 
 * @returns {Obj} values form 
 */
export const serializeForm = (form) => {
  let inputs = [
    ...form.querySelectorAll(AON_TAGS)
  ];
  let obj = {};
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
  const date = new Date(d);
  const day = addZero(date.getDate(), 2);
  const month = addZero(date.getMonth() + 1, 2);
  const year = date.getFullYear();
  return day + '/' + month + '/' + year;
}

export const setDate = (date) => formatDate(date);

export const formatDateOrigin = (d) => {
  let date = new Date(d);
  const day = addZero(date.getDate(), 2);
  const month = addZero(date.getMonth() + 1, 2);
  const year = date.getFullYear();
  return year + '-' + month + '-' + day;
}

export const setDateTimestamp = (d) => {
  const date = new Date(d);
  return formatDate(date) + " " + setTime(date);
}

export const dayStr = (date) => {
  const now = new Date();
  let day = DAYS[date.getDay()];
  if( (date.getFullYear() === now.getFullYear()) && (date.getMonth() === now.getMonth()) ){
    if(date.getDay() === now.getDay()){
      day = "hoy";
    } else if(date.getDay() === now.addDay(-1).getDay()){
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
  const monthText = MONTHS[date.getMonth()];
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
  const month = MONTHS[d.getMonth()];
  return day + '-' + month;
}

export const geMonthYear = (date) => {
  const d = new Date(date)
  const month = MONTHS[d.getMonth()];
  return month+". "+ d.getFullYear();
}

/**
 * 
 * @param {number} value valor
 * @param {decimals} decimals cantidad de decimales  (opcional)
 * @param {string} simbolo moneda (EUR) (opcional)
 * @param {*} locale pais (opcional)
 * @returns 
 */
export const formatNumber = (value = 0, decimals = 0, simbolo = undefined, locale = "de-DE") => {
  let options = { minimumFractionDigits: decimals, maximumFractionDigits: decimals};
  if(simbolo) { options.style = 'currency'; options.currency = simbolo;  }
  return  new Intl.NumberFormat(locale, options).format(value.toString().replace(",", "."));
}

/**
 * 
 * @param {string} formId form para deshabilitar
 * @param {string} elems elementos adicionales para deshabilitar (opcional)
 */
export const disabledForm = (formId, elems) => {
  let elems_disabled = AON_TAGS;
  if (elems) elems_disabled = elems + ', ' + AON_TAGS;
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

/** 
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
  properties.element  =  properties.element || document.createElement('div');

  //Internal functions
  properties.appendTo =  (e) => e.appendChild(properties.element);
  properties.appendChild =  (e) => properties.element.appendChild(e);
  properties.clean = () => properties.element.innerHTML = '';

  //Check information
  if(properties.type)   
      properties.element = document.createElement(properties.type);

  if(properties.id)    
      properties.element.id  = properties.id;

  if(properties.text)   
      properties.element.innerHTML = properties.text;

  //Set data to element
  setAttributes(properties.element,properties.attributes);
  setDataset(properties.element,properties.dataset);
  setEvents(properties.element,properties.events);
  setStyles(properties.element,properties.styles);
  setClasses(properties.element,properties.classes);

  return properties;
} 

/**
 * Set attributes to element
 * @param {*} element 
 * @param {*} attributes 
 */
export const setAttributes = (element, attributes) =>{
  if(element && attributes) 
      for (const key in attributes)  
          element.setAttribute(key,attributes[key]);
  return element;
}

/**
 * Set dataset to an element
 * @param {*} element 
 * @param {*} dataset 
 */
export const setDataset = (element,dataset) => {
  if(element && dataset) 
      for (const key in dataset) 
          element.dataset[key] = dataset[key];
  return element;
}

/**
 * Set events to an element
 * @param {*} element 
 * @param {*} events 
 */
export const setEvents =(element,events) => {
  if(element && events) 
      for (const key in events)  
          element.addEventListener(key,events[key]);
  return element;
}

/**
 * Set styles to an element
 * @param {*} element 
 * @param {*} styles 
 */
export const setStyles = (element,styles) => {
  if(element && styles) 
    for (const key in styles)  
      element.style[key] = styles[key];  
  return element;
}

/**
 * Set classes 
 * @param {*} element 
 * @param {*} classes 
 */
export const setClasses = (element,classes) => {
  if(element && classes) 
    classes.forEach(cl => element.classList.add(cl));
  return element;
}