import { DatePipe } from '@angular/common';

  export const DateFormat = (dateFormat: any, format: string) => {
       // El idioma que tenemos seleccionado
    const language = localStorage.getItem('selectedLanguage');
    // Formateamos el dato como string
    dateFormat = dateFormat.toString().replace(/CET/gi, '');
    // Comporvamos el formato a devolver
      // Datos necesarios para comprobar
        const chars: {[index: string]: string} = {
          'dd'  : 'yyyy',
          'yyyy': 'dd'
        };
        const formatEnglish = [
          'yyyy/MM/dd',
          'yyyy/MM/dd, HH:mm',
          'yyyy/MM/dd HH:mm',
          'yyyy-MM-dd, HH:m m',
          'yyyy-MM-dd HH:mm'
        ];
        const formatSpanish = [
          'dd/MM/yyyy',
          'dd/MM/yyyy, HH:mm',
          'dd/MM/yyyy HH:mm',
          'dd-MM-yyyy, HH:mm',
          'dd-MM-yyyy HH:mm'
        ];
      // Modificamos el valor, por el formato correcto si es necesario
        switch (language) {
          // Spanish format
          case 'es':
            // Comprobar si el formato metido esta en English para modificarlo
            if(formatEnglish.includes(format)){
              format = format.replace(/yyyy|dd/gi, m => chars[m]);
            }
          break;
          // English format
          case 'en':
            if(formatSpanish.includes(format)){
              format = format.replace(/yyyy|dd/gi, m => chars[m]);
            }
          break;
        }      
    // Retornamos el valor formateado
    return new DatePipe(language || 'es').transform(dateFormat, format);
  }
/*
export class DateFormat {
  
  constructor(
  ){
  }
  
  public static format(dateFormat: any, format: string){
    // El idioma que tenemos seleccionado
    const language = localStorage.getItem('selectedLanguage');
    // Formateamos el dato como string
    dateFormat = dateFormat.toString().replace(/CET/gi, '');
    // Comporvamos el formato a devolver
      // Datos necesarios para comprobar
        const chars: {[index: string]: string} = {
          'dd'  : 'yyyy',
          'yyyy': 'dd'
        };
        const formatEnglish = [
          'yyyy/MM/dd',
          'yyyy/MM/dd, HH:mm',
          'yyyy/MM/dd HH:mm',
          'yyyy-MM-dd, HH:m m',
          'yyyy-MM-dd HH:mm'
        ];
        const formatSpanish = [
          'dd/MM/yyyy',
          'dd/MM/yyyy, HH:mm',
          'dd/MM/yyyy HH:mm',
          'dd-MM-yyyy, HH:mm',
          'dd-MM-yyyy HH:mm'
        ];
      // Modificamos el valor, por el formato correcto si es necesario
        switch (language) {
          // Spanish format
          case 'es':
            // Comprobar si el formato metido esta en English para modificarlo
            if(formatEnglish.includes(format)){
              format = format.replace(/yyyy|dd/gi, m => chars[m]);
            }
          break;
          // English format
          case 'en':
            if(formatSpanish.includes(format)){
              format = format.replace(/yyyy|dd/gi, m => chars[m]);
            }
          break;
        }      
    // Retornamos el valor formateado
    return new DatePipe(language || 'es').transform(dateFormat, format);
  };

}
*/