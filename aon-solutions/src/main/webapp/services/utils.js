import { AON_TAGS } from "../environments/aonTag.js";
import { Attach } from "../models/Attach.js";

export const getReader = (file) =>  new Promise((resolve) => {
  const READER = new FileReader();
  READER.readAsDataURL(file);
  READER.onload = () => {
    const { result } = READER;
    const { name, size, type: contentType } = file;
    const base64File = result.split(',')[1];
    let attach = new Attach()
      .setContent(base64File)
      .setContentType(contentType)
      .setContentEncoding('base64')
      .setContentSize(size)
      .setSize(size)
      .setContentName(name)
      .setName(name);
  
    resolve(attach);
  };
});

/**
 * 
 * @param {String} url 
 * @param {String} contentType mimeType
 * @returns Promise<String>
 */
export const getBase64FromUrl = async (url, contentType)=> {
  const data = await fetch(url,{
    headers: {'Content-Type': contentType}
  });
  const blob = await data.blob();
  return new Promise((resolve) => {
      const reader = new FileReader();
      reader.readAsDataURL(blob); 
      reader.onloadend = () => {
      resolve(reader.result);
    }
  });
}


/**
 * 
 * @param {HTMLElement} element 
 */
export const addHorizontalScroll = (element) => {
  element.addEventListener("wheel", function (evt) {
      let maxScroll = element.scrollWidth - element.offsetWidth;
      let currentScroll = element.scrollLeft + evt.deltaY;

      if (currentScroll > 0 && currentScroll < maxScroll) {
        evt.preventDefault();
        element.scrollLeft = currentScroll;
      }
      else if (currentScroll <= 0) {
        element.scrollLeft = 0;
      }
      else {
        element.scrollLeft = maxScroll;
      }
  });
}


export const formatBytes = (a,b=2)=>{if(0===a)return"0 Bytes";const c=0>b?0:b,d=Math.floor(Math.log(a)/Math.log(1024));return parseFloat((a/Math.pow(1024,d)).toFixed(c))+" "+["Bytes","KB","MB","GB","TB","PB","EB","ZB","YB"][d]}

export const isEmptyObject = (obj) => !obj || (obj.constructor === Object &&  Object.keys(obj).length === 0);

/**
 * 
 * @param {Object} obj obj
 * @returns obj sin datos vacios
 */
export const removeEmpty = (obj) => {
  Object.keys(obj).forEach((key) =>  isEmptyObject(obj[key]) ? delete obj[key] : null);
  return obj;
};

/**
 * 
 * @param {array} obj array a ordenar
 * @param {string} value // key por ordenar
 * @param {string} orderBy  asc o desc
 * @returns 
 */
export const sortBy = (obj, value, orderBy='asc') =>  obj.sort((a, b) => {
  let num = 0;
  if (!a[value]) num = 1;
  else if (!b[value]) num = -1;
  else num = 'asc' === orderBy.toLocaleLowerCase() ? (typeof a[value] === 'string') - (typeof b[value] === 'string') || a[value] > b[value] || -(a[value] < b[value]) : (typeof b[value] === 'string') - (typeof a[value] === 'string') || b[value] > a[value] || -(b[value] < a[value])
  return num;
});

/**
 * 
 * @param {string} selector selector html
 * @returns element
 */
export const waitEl = (selector, doc = undefined)=> new Promise((resolve,reject)=>{
  let i = 0;
  let element = null;
  let interval = setInterval(()=> {
    i++;
    element = (doc || document).querySelector(selector);
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

export const round = (value) => {
    const type = 'round';
    let exp = -2;

    if (typeof exp === 'undefined' || +exp === 0)  return Math[type](value);
    value = +value;
    exp = +exp;
    // Si el valor no es un número o el exp no es un entero...
    if (isNaN(value) || !(typeof exp === 'number' && exp % 1 === 0)) return NaN;
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
 * @returns {Object} values form 
 */
export const serializeForm = (form) => {
  let inputs = [...form.querySelectorAll(AON_TAGS)];
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

/**
 * 
 * @param {number} value valor
 * @param {decimals} decimals cantidad de decimales  (opcional)
 * @param {string} simbolo moneda (EUR) (opcional)
 * @param {*} locale pais (opcional)
 * @returns 
 */
export const formatNumber = (value = 0, decimals = 0, simbolo = undefined, locale = "de-DE") => {
  value = value || 0;
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
  [...document.getElementById(formId).querySelectorAll(elems_disabled)].map(el => el.disabled = true);
}

export const now = () => {
  const today = new Date();
  const yyyy = today.getFullYear();
  let mm = today.getMonth() + 1; // Months start at 0!
  let dd = today.getDate();
  
  if (dd < 10) dd = '0' + dd;
  if (mm < 10) mm = '0' + mm;
  
  return yyyy + '-' + mm + '-' + dd;
}