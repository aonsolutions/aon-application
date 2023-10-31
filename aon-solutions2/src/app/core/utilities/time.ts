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

  // Filtros fechas principio semana
export const getStartDateOfWeek = (): string => {
  const startDate = new Date();
  const currentDay = startDate.getDay();
  const startDay = currentDay === 0 ? 6 : currentDay - 1;
  startDate.setDate(startDate.getDate() - startDay);
  return DateFormat(startDate, 'yyyy-MM-dd')|| '';
};

// Filtros fechas final semana
export const getEndDateOfWeek = (): string => {
  const endDate = new Date();
  const currentDay = endDate.getDay();
  const remainingDays = 7 - currentDay - 1;
  endDate.setDate(endDate.getDate() + remainingDays);
  return DateFormat(endDate, 'yyyy-MM-dd')|| '';
};

// Filtros fechas principio mes
export const getStartDateOfMonth = (): string => {
  const startDate = new Date();
  startDate.setDate(1);
  return DateFormat(startDate, 'yyyy-MM-dd')|| '';
};

// Filtros fechas final mes
export const getEndDateOfMonth = (): string => {
  const endDate = new Date();
  endDate.setMonth(endDate.getMonth() + 1);
  endDate.setDate(0);
  return DateFormat(endDate, 'yyyy-MM-dd')|| '';
};
