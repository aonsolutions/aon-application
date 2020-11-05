
export const isMobile = () => window.innerWidth <= 850 && window.innerHeight <= 850;

export const clearElement = (id) => {
  document.getElementById(id).innerHTML = '';
}

export const getReader = (file)=> {
  return new Promise((resolve, reject)=>{
      const READER = new FileReader();
			READER.readAsDataURL(file);
			READER.onload = () => {
        const {result} = READER;
        const {name, size, type:contentType} = file;
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

export const getElement = (id) => document.getElementById(id);

export const getElementById = (id) => document.getElementById(id);


export const isNumber = (n) => !isNaN(parseFloat(n)) && isFinite(n);

export const round = (value) => decimalAdjust('round', value, -2);

export const decimalAdjust = (type, value, exp)  => {
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
