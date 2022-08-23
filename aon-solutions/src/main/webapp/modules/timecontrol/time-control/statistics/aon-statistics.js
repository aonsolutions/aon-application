import { SIGNIN_VIEWS } from "../../signinEnums.js";
import { charts } from "./charts";
import {  CONSTANT } from "../../../../environments/environments.js";
import { AonElement } from "../../../../components/AonElement.js";
import { timeHour } from "../utils.js";
import { getTaskHoldersUser, getTaskHolder} from "../../../../services/taskHolderService.js";
import { getTaskHolderTimeControl,
} from "../../../../services/timeControlService.js";
import {  setStyles } from "../../../../services/utilsComponents.js";
import { DAYS } from "../../../../models/enums.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";

export class AonStatistics extends AonElement {
  
  TABLE_ID;
  dur;
  taskHolder;

  static get observedAttributes() {
    return [CONSTANT.FILTER, CONSTANT.DATA];
  }

  get filter() {
    return JSON.parse(this.getAttribute(CONSTANT.FILTER));
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get data() {
    let value = this.getAttribute(CONSTANT.DATA)
      ? this.getAttribute(CONSTANT.DATA)
      : "[]";
    return JSON.parse(value);
  }

  set data(value) {
    if (value) this.setAttribute(CONSTANT.DATA, JSON.stringify(value));
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || SIGNIN_VIEWS.AON_STATISTICS;
  }

  build() {
    getTaskHolder({reload:true}).then(th => {
      this.taskHolder = th;
      this.paintChart();
    });

  }

  async paintChart() {
    try {
      const resp = await this.getData();
      let sum = 0;
      let count = 0;
      let datos = [];
      const firstDayOfWeek = new Date().getFirstDayOfWeek().setHours(0,0,0,0);
      for (const key in resp) {
        let { time, start_date, status, in_date } = resp[key];
        if(in_date && status && status.indexOf("in")>=0){
          time =  Number((new Date().getTime() - in_date)  + time);
        }
        const newTime = this.timeToDecimal(time);
        const day =  new Date(start_date);
        let color = "#bdbdbd";
        const newDayTime = day.setHours(0,0,0,0);
        if(newDayTime === new Date().setHours(0,0,0,0)){
            color = "#86D364";
        } else if(newDayTime >= firstDayOfWeek){
          color = "#c8e6c9";
        } 
        
        if(
          newTime>0 && start_date && 
          new Date(start_date).setHours(0,0,0,0) < new Date().setHours(0,0,0,0) 
        ){
          sum = sum + newTime;
          count ++;
        }
          
        datos.push({
          time:newTime, 
          dayLetter: this.getFirstLettersDay(day),
          color
        });
      }
      
      const average = sum / count;
      let newData = [];
      for (const dt of datos) 
        newData.push([dt.dayLetter, dt.time, `color:${dt.color};stroke-width:0;` , average]);
 
      setStyles(this,{
        display:"flex",
        flexWrap:"wrap",
        justifyContent:"center",
        alignItems:"center",
        width:"100%"
      });

      await charts(this, newData);
    } catch (error) {
      console.log(error);
    }
  }

  async getData() {
    let dt = this.data;
    try {
      if (!dt.length) {
        const startDate = AonDateUtils.formatDateOrigin( new Date().addDay(-7));
        const endDate = AonDateUtils.formatDateOrigin(new Date());
        dt = await getTaskHolderTimeControl({
          taskHolderId: this.taskHolder.id,
          group: "DAY",
          startDate,
          endDate,
        });
      }
    } catch (error) {
      console.log(error);
    }
    return dt;
  }

  timeToDecimal(tm) {
    let t = timeHour(Number(tm));
    let arr = t.split(":");
    let dec = parseInt((arr[1] / 6) * 10, 10);
    return parseFloat(parseInt(arr[0], 10) + "." + (dec < 10 ? "0" : "") + dec);
  }

  getFirstLettersDay(date){
    let dayInt = date.getDay();
    let day = DAYS[dayInt];
    let newValue = null;
    if(dayInt ===3) day = "X";
    if(day) newValue = day.toString().substr(0,1).toUpperCase();
    return newValue;
  }
}

window.customElements.define("aon-statistics", AonStatistics);
