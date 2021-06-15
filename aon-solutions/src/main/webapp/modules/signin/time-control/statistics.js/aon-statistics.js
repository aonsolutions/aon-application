import { SIGNIN_VIEWS } from "../../signinEnums.js";
import { charts } from "./charts";
import {  CONSTANT } from "../../../../environments/environments.js";
import { AonElement } from "../../../../components/AonElement.js";
import { timeHour } from "../utils.js";
import { getTaskHoldersUser } from "../../../../services/taskHolderService.js";
import { getTaskHolderTimeControl,
} from "../../../../services/timeControlService.js";
import { formatDateOrigin } from "../../../../services/utils.js";
import { DAYS } from "../../../../models/enums.js";

export class AonStatistics extends AonElement {
  TABLE_ID;
  dur;
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
    this.paintChart();
  }

  async paintChart() {
    try {
      const resp = await this.getData();
      let sum = 0;
      let count = 0;
      let datos = [];
      for (const key in resp) {
        const { time, start_date } = resp[key];
        const newTime = this.timeToDecimal(time);
        const day    =  new Date(start_date);
        let color = "#bdbdbd";
        if(day.setHours(0,0,0,0) === new Date().setHours(0,0,0,0))
          color = "#86D364";
        datos.push({
          time:newTime, 
          dayLetter: this.getFirstLettersDay(day),
          color
        });
        sum = sum + newTime;
        if (newTime > 0) count++;
      }

      const average = sum / count;
      let newData = [];
      for (const dt of datos) 
        newData.push([dt.dayLetter, dt.time, `color:${dt.color};stroke-width:0;` , average]);

      await charts(this, newData);
    } catch (error) {
      console.log(error);
    }
  }

  

  async getData() {
    let dt = this.data;
    try {
      if (!dt.length) {
        const startDate = formatDateOrigin( new Date().addDay(-7));
        const endDate = formatDateOrigin(new Date());
        const [taskHolder] = await getTaskHoldersUser();
        dt = await getTaskHolderTimeControl({
          taskHolderId: taskHolder.id,
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
