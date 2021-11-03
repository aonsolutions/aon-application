import { MSG } from "../../environments/environments.js";
import { DAYS, MONTHS } from "../../models/enums.js";
import { addZero } from "../../services/utils.js";

/**
 * @param {Date} AonDateUtils.formatDate date
 * @return {String} AonDateUtils.formatDate dd-MM-yyyy
 * @param {Date} AonDateUtils.setTime date
 * @return {String} AonDateUtils.setTime H:m
 * @param {Date} AonDateUtils.setDate date
 * @return {String} AonDateUtils.setDate dd-MM-yyyy
 * @param {Date} AonDateUtils.formatDateOrigin date
 * @return {String} AonDateUtils.formatDateOrigin yyyy-MM-dd
 * @param {Date} AonDateUtils.setDateTimestamp date
 * @return {String} AonDateUtils.setDateTimestamp dd-MM-yyyy H:m
 * @param {Date} AonDateUtils.lastThreeDayStr date
 * @return {String} AonDateUtils.lastThreeDayStr DAY OR NULL
 * @param {Date} AonDateUtils.dayStr date
 * @return {String} AonDateUtils.dayStr DAY OR NULL
 * @param {Date} AonDateUtils.setDateTpDay date
 * @return {String} AonDateUtils.setDateTpDay DAY, dd-MM-yyyy
 * @param {Date} AonDateUtils.setDateTimestampDay date
 * @return {String} AonDateUtils.setDateTimestampDay DAY, dd-MM-yyyy H:m
 * @param {Date} AonDateUtils.setFullDate date
 * @return {String} AonDateUtils.setFullDate DAY, dd de mm de yyyy
 * @param {Date} AonDateUtils.getDayMonth date
 * @return {String} AonDateUtils.getDayMonth dd-MONTH
 * @param {Date} AonDateUtils.getMonthYear date
 * @return {String} AonDateUtils.getMonthYear MONTH. yyyy
 * @param {Number} AonDateUtils.timeParser number
 * @return {String} AonDateUtils.timeParser H:M:S
 */

export const AonDateUtils = {
    formatDate: function(d) { // dd-MM-yyyy
        const date = new Date(d);
        const day = addZero(date.getDate(), 2);
        const month = addZero(date.getMonth() + 1, 2);
        const year = date.getFullYear();
        return day + '/' + month + '/' + year;
    },
    setTime: function(date) { // H:m
        const newDate = new Date(date);
        const hour =  addZero(newDate.getHours(), 2);
        const min  =  addZero(newDate.getMinutes(), 2);
        return hour+":"+min;
    },
    setDate: function(date) { // dd-MM-yyyy
        return this.formatDate(date);
    },
    formatDateOrigin: function(d) { // yyyy-MM-dd
        let date = new Date(d);
        const day = addZero(date.getDate(), 2);
        const month = addZero(date.getMonth() + 1, 2);
        const year = date.getFullYear();
        return year + '-' + month + '-' + day;
    },
    setDateTimestamp: function(d) { // dd-MM-yyyy H:m
        const date = new Date(d);
        return this.formatDate(date) + " " + this.setTime(date);
    },
    lastThreeDayStr: function(d) { //DAY OR NULL
        const date = new Date(d);
        const now = new Date();
        let day = null;
        if( (date.getFullYear() === now.getFullYear()) && (date.getMonth() === now.getMonth()) ){
          if(date.getDate() === now.addDay(-1).getDate())
            day = MSG.YESTERDAY;
          else if(date.getDate() === new Date().getDate())
            day = MSG.TODAY;
          else if(date.getDate() === new Date().addDay(1).getDate())
            day = MSG.TOMORROW;
        }
        return day;
    },
    dayStr: function(date) { //DAY STR
        return this.lastThreeDayStr(date) || DAYS[date.getDay()]
    },
    setDateTpDay: function(d) { // DAY, dd-MM-yyyy
        const date = new Date(d);
        const day = this.dayStr(date);
        return day+", "+this.formatDate(date);
    },
    setDateTimestampDay: function(d) { // DAY, dd-MM-yyyy H:m
        return this.setDateTpDay(new Date(d)) +" " + this.setTime(new Date(d));
    },
    setFullDate: function(d) { // DAY, dd de mm de yyyy
        const date = new Date(d);
        const dayText = this.dayStr(date);
        const monthText = MONTHS[date.getMonth()];
        return `${dayText}, ${date.getDate()} de ${monthText} de ${date.getFullYear()}`;
    },
    getDayMonth: function(date) { // dd-MONTH
        const d = new Date(date);
        const day = addZero(d.getDate(), 2);
        const month = MONTHS[d.getMonth()];
        return day + '-' + month;
    },
    getMonthYear: function(date) { // MONTH. yyyy
        const d = new Date(date)
        const month = MONTHS[d.getMonth()];
        return month+". "+ d.getFullYear();
    },
    timeParser: function(time) {
        let msecPerMinute = 1000 * 60;
        let msecPerHour = msecPerMinute * 60;
      
        // Calcular las horas , minutos y segundos
        let hours = Math.floor(time / msecPerHour );
        time = time - (hours * msecPerHour );
      
        let minutes = Math.floor(time / msecPerMinute );
        time = time - (minutes * msecPerMinute );
      
        let seconds = Math.floor(time / 1000 );
        return addZero(hours, 2) + ':'+ addZero(minutes, 2) + ':'+ addZero(seconds, 2);
    }
}