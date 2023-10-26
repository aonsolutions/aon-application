import { DatePipe } from '@angular/common';

  export const DateFormat = (dateFormat: any, format: string) => {
    // El idioma que tenemos seleccionado
    const language = localStorage.getItem('selectedLanguage');
    // Formateamos el dato como string
    dateFormat = dateFormat.toString().replace(/CET/gi, '');
    // Comprovamos el formato a devolver
      // Datos necesarios para comprobar
        const chars: {[index: string]: string} = {
          'dd'  : 'yyyy',
          'yyyy': 'dd'
        };
        const formatNotAllowed = [
          'yyyy/MM/dd',
          'yyyy/MM/dd, HH:mm',
          'yyyy/MM/dd HH:mm',
          'yyyy-MM-dd, HH:m m',
          'yyyy-MM-dd HH:mm'
        ];
      // Modificamos el valor, por el formato correcto si es necesario
        if(formatNotAllowed.includes(format)){
          format = format.replace(/yyyy|dd/gi, m => chars[m]);
        }
    // Retornamos el valor formateado
    return new DatePipe(language || 'es').transform(dateFormat, format);
  }