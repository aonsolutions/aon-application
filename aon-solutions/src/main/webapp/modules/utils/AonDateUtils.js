import { MSG } from "../../environments/environments.js";
import { DAYS, MONTHS, MONTHS_ABR } from "../../models/enums.js";
import { addZero } from "../../services/utils.js";

export const AonDateUtils = {
  /**
   *
   * @param {Date} date
   * @return {String} dd-MM-yyyy
  */
  formatDate: function (d) {
    const date = new Date(d);
    const day = addZero(date.getDate(), 2);
    const month = addZero(date.getMonth() + 1, 2);
    const year = date.getFullYear();
    return day + "/" + month + "/" + year;
  },
  /**
   *
   * @param {Date} date
   * @return {String} H:m
  */
  setTime: function (date) {
    const newDate = new Date(date);
    const hour = addZero(newDate.getHours(), 2);
    const min = addZero(newDate.getMinutes(), 2);
    return hour + ":" + min;
  },
  /**
   *
   * @param {Date} date
   * @return {String} dd-MM-yyyy
  */
  setDate: function (date) {
    return this.formatDate(date);
  },
  /**
   *
   * @param {Date} date
   * @return {String} yyyy-MM-dd
  */
  formatDateOrigin: function (d) {
    let date = new Date(d);
    const day = addZero(date.getDate(), 2);
    const month = addZero(date.getMonth() + 1, 2);
    const year = date.getFullYear();
    return year + "-" + month + "-" + day;
  },
  /**
   *
   * @param {Date} date
   * @return {String} dd-MM-yyyy H:m
  */
  setDateTimestamp: function (d) {
    const date = new Date(d);
    return this.formatDate(date) + " " + this.setTime(date);
  },
  /**
   *
   * @param {Date} date
   * @return {String} DAY OR NULL
  */
  lastThreeDayStr: function (d) {
    const date = new Date(d);
    const now = new Date();
    let day = null;
    if (date.getFullYear() === now.getFullYear() && date.getMonth() === now.getMonth()) {
      if (date.getDate() === now.addDay(-1).getDate()) day = MSG.YESTERDAY;
      else if (date.getDate() === new Date().getDate()) day = MSG.TODAY;
      else if (date.getDate() === new Date().addDay(1).getDate()) day = MSG.TOMORROW;
    }
    return day;
  },
  /**
   *
   * @param {Date} date
   * @return {Date} date first day year
  */
  getYearFirstDay: function (d) {
    return new Date(d.getFullYear(), 0, 1);
  },
  /**
   *
   * @param {Date} date
   * @return {String} DAY OR NULL
  */
  getDayStr: function (date) {
    return this.lastThreeDayStr(date) || DAYS[date.getDay()];
  },
  /**
   *
   * @param {Date} d
   * @return {String} DAY, dd-MM-yyyy
  */
  setDateTpDay: function (d) {
    const date = new Date(d);
    return this.getDayStr(date) + ", " + this.formatDate(date);
  },
  /**
   *
   * @param {Date} date
   * @return {String} DAY, dd-MM-yyyy H:m
  */
  setDateTimestampDay: function (d) {
    return this.setDateTpDay(new Date(d)) + " " + this.setTime(new Date(d));
  },
  /**
   *
   * @param {Date} date
   * @return {String} DAY, dd de mm de yyyy
  */
  setFullDate: function (d) {
    const date = new Date(d);
    const dayText = this.getDayStr(date);
    const monthText = MONTHS[date.getMonth()];
    return `${dayText}, ${date.getDate()} de ${monthText} de ${date.getFullYear()}`;
  },
  /**
   *
   * @param {Date} date
   * @return {String} MONTH yyyy
  */
  getDayMonth: function (d) {
    const date = new Date(d);
    const day = addZero(date.getDate(), 2);
    const month = MONTHS[date.getMonth()];
    return day + " " + month;
  },
  /**
   *
   * @param {Date} date
   * @return {String} MONTH yyyy
  */
  getDayMonthAbr: function (d) {
    const date = new Date(d);
    const day = addZero(date.getDate(), 2);
    const month = MONTHS_ABR[date.getMonth()];
    return day + " " + month;
  },
  /**
   *
   * @param {Date} date
   * @return {String} dd-MONTH OR OTHER YEAR dd-mm-yyyy
  */
  getDayMonthOrFull: function (d) {
    const date = new Date(d);
    const now = new Date();

    if (date.getFullYear() === now.getFullYear()){
      return this.lastThreeDayStr(date) || this.getDayMonth(date);
    }

    return this.formatDate(date);
  },
  /**
   *
   * @param {Date} date
   * @return {String} dd-MONTH OR OTHER YEAR dd-mm-yyyy
  */
  getDayMonthOrFullShort: function (d) {
    const date = new Date(d);
    const now = new Date();

    if (date.getFullYear() === now.getFullYear()){
      return this.lastThreeDayStr(date) || this.getDayMonthAbr(date);
    }

    return this.formatDate(date);
  },
  /**
   *
   * @param {Date} date
   * @return {String} MONTH. yyyy
  */
  getMonthYear: function (date) {
    const d = new Date(date);
    const month = MONTHS[d.getMonth()];
    return month + ". " + d.getFullYear();
  },
  /**
   *
   * @param {Date} date
   * @return {String} H:M:S
  */
  timeParser: function (time) {
    let msecPerMinute = 1000 * 60;
    let msecPerHour = msecPerMinute * 60;

    // Calcular las horas , minutos y segundos
    let hours = Math.floor(time / msecPerHour);
    time = time - hours * msecPerHour;

    let minutes = Math.floor(time / msecPerMinute);
    time = time - minutes * msecPerMinute;

    let seconds = Math.floor(time / 1000);
    return addZero(hours, 2) + ":" + addZero(minutes, 2) + ":" + addZero(seconds, 2);
  },

  parseStr: function (dateStr) {
      if(dateStr.includes('/')){
        let dateArr = dateStr.split('/');
        let a = dateArr[0].length === 1 
            ? '0' + dateArr[0] : dateArr[0];
        let b = dateArr[1].length === 1 
            ? '0' + dateArr[1] : dateArr[1];
        let c = dateArr[2];
        dateStr = a + b + c;       
      } 
      
      if(dateStr.includes('-')){
        let dateArr = dateStr.split('-');
        let a = dateArr[0].length === 1 
            ? '0' + dateArr[0] : dateArr[0];
        let b = dateArr[1].length === 1 
            ? '0' + dateArr[1] : dateArr[1];
        let c = dateArr[2];
        dateStr = a + b + c;       
      } 
 
      let day = dateStr.substring(0, 2);
      let month = dateStr.substring(2, 4);
      let year = dateStr.substring(4);

      if(Number(month) > 12 || Number(day) > 31 || year.length > 4){
        return this.date;
      } else {
        let d = month + '/' + day + '/' + year;
        return new Date(d);
      }
  },
  /**
   *
   * @param {Date} date
   * @return {String} H:M
  */
  timeParserHHMM: function (time) {
    let date = new Date();
    date.setTime(time);
     // Extraer horas y minutos
    let hours = date.getHours();
    let minutes = date.getMinutes();
    
    // Agregar un 0 al inicio si es necesario para formato de dos dígitos
    hours = hours < 10 ? '0' + hours : hours;
    minutes = minutes < 10 ? '0' + minutes : minutes;

    // Retornar en formato hh:mm
    return `${hours}:${minutes}`;
  }
}