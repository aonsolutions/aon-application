// Validar que en los input de type = number, tenemos un formato valido
export function validateFormatNumber(event: any): string | boolean {
  let key;
  if (event.type === 'paste') {
    key = event.clipboardData.getData('text/plain');
  } else {
    key = event.keyCode;
    key = String.fromCharCode(key);
  }

  // 1 - Permitir del 0 al 9
  // 2 - Permitir .
  // 3 - Permitir ,
  // 4 - Permitir -
  const regex = /[0-9]|\.|\,|\-/;

  if (!regex.test(key)) {
    if (event.returnValue) event.returnValue = false;
    if (event.preventDefault) {
      event.preventDefault();
    }
    return false;
  }

  return key;
}
// Agregamos decimales al numero pasado, si no se le pasan decimales, usaremos 2 decimales
export function formatNumber(number: number, decimales: number = 2): string {
  if (typeof number === 'number') {
    // El idioma que tenemos seleccionado
    const language = localStorage.getItem('selectedLanguage') || 'es';
    // Formatear el dependiendo del idioma
    const numberFormatter = new Intl.NumberFormat(language, {
      minimumFractionDigits: decimales,
      maximumFractionDigits: decimales,
      useGrouping: false,
    });
    // Retornamos el valor formateado
    return numberFormatter.format(number);
  } else {
    return 'N/D';
  }
}
