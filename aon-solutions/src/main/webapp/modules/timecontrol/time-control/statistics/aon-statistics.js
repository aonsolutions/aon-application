import { SIGNIN_VIEWS } from "../../signinEnums.js";
import {  CONSTANT, TAG } from "../../../../environments/environments.js";
import { AonElement } from "../../../../components/AonElement.js";
import { timeHour } from "../utils.js";
import { getTaskHolder} from "../../../../services/taskHolderService.js";
import { getTaskHolderTimeControl} from "../../../../services/timeControlService.js";
import { DAYS } from "../../../../models/enums.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";
import * as LS from "../../../../services/localStorageService.js";

export class AonStatistics extends AonElement {
  
  TABLE_ID;
  dur;
  taskHolder;
  comboBarChart;

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

      let canvasDiv = this.createElement(TAG.DIV);
      canvasDiv.id = "timeControlCanvasDiv";
      canvasDiv.style.width = "100%";
      canvasDiv.style.height = "100%";

      this.appendChild(canvasDiv);

      let canvas = this.createElement(TAG.CANVAS);
      canvas.id = "timeControlCanvas";
      canvas.cle
      canvasDiv.appendChild(canvas);

      this.paintChart(canvas);
    });

  }

  async paintChart(canvas) {
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
        let color = "rgba(189, 189, 189, 1)";
        const newDayTime = day.setHours(0,0,0,0);
        if(newDayTime === new Date().setHours(0,0,0,0)){
            color = "rgba(134, 211, 100, 1)";
        } else if(newDayTime >= firstDayOfWeek){
          color = "rgba(200, 230, 201, 1)";
        } 
        
        if(
          newTime>0 && start_date && 
          new Date(start_date).setHours(0,0,0,0) < new Date().setHours(0,0,0,0) 
        ){
          sum = sum + newTime;
          count ++;
        }
          
        datos.push({
          time: newTime, 
          dayLetter: this.getFirstLettersDay(day),
          color
        });
      }
      
      let average = sum / count;
      let labels = datos.map(dt => dt.dayLetter);
      let colors = datos.map(dt => dt.color);
      let datas = datos.map(dt => dt.time);
      let colorGrid = LS.isDarkTheme() ? "#ffffff" : "#bdbdbd"
      
      const dataChart = {
        labels,
        datasets: [
          {
            label: 'Horas',
            data: datas,
            backgroundColor: colors,
            borderRadius: Number.MAX_VALUE,
            borderSkipped: false,
            order: 1
          },
          // Lines
          {
            label: 'Media',
            // borderColor: '#4c4c4c',
            data: [average, average, average, average, average, average, average, average],
            type: 'line',
            borderColor: "rgb(143, 143, 143)",
            borderDash: [2, 4],
            pointStyle: 'circle',
            pointRadius: 0,
            fill: false,
            order: 0
          }
        ]
      };

      const config = {
        type: "bar",
        data: dataChart,
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            x: {
              grid: {
                display : false
              },
              border : {
                color : colorGrid
              },
              ticks : {
                color : colorGrid
              }
            },
            y: {
              grid: {
                display : false
              },
              border : {
                color :  colorGrid
              },
              ticks : {
                color : colorGrid
              }
            },
          },
          plugins: {
            legend: {
              display: false, // This hides all text in the legend and also the labels.
            },
          },
        },
      };

      if (this.comboBarChart != undefined) {
        this.comboBarChart.destroy();
      }
      // clear canvas for android mobiles
      canvas.innerHTML = '';
  
      this.comboBarChart = new Chart(canvas, config);
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
