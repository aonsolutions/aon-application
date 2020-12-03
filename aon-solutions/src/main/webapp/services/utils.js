import {INPUTS_ALL} from '../environments/constants.js';

export const getReader = (file) => {
  return new Promise((resolve, reject) => {
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
  inputs.filter(el => el.value).map(({ name, value }) => obj[name] = value);
  return obj;
}

export const setValueName = (name, value) => {
  let el = document.querySelector(`[name="${name}"]`);
  if(el && value) el.value = value;
  return el;
}

const formatDate = (d) => {
  let day = d.getDate();
  if (day <= 9) day = '0' + day;
  let month = d.getMonth() + 1;
  if (month <= 9) month = '0' + month;
  let year = d.getFullYear();
  return day + '-' + month + '-' + year;
}

export const setDate = (date) => formatDate(new Date(date));

export const addDays = (date, days) => {
  let result = new Date(date);
  result.setDate(result.getDate() + days);
  return result;
}


export const getDayMonth = (date) => {
  let months = ["ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"];
  let d = new Date(date);
  let day = d.getDate();
  if (day <= 9) day = '0' + day;
  let month = months[d.getMonth()];
  return day + '-' + month;
}

