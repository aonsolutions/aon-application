import { SIGNIN_VIEWS } from "../../signinEnums.js";
import { charts } from "./charts";
import { CONSTANT } from "../../../../environments/environments.js";
import { AonElement } from "../../../../components/AonElement.js";
import { timeHour } from "../utils.js";
import { getTaskHoldersUser } from "../../../../services/taskHolderService.js";
import {
  getPeriod,
  getTaskHolderTimeControl,
} from "../../../../services/timeControlService.js";
import { getDomainUserRoles } from "../../../../services/companyService.js";
import { DomainUserRoles } from "../../../../models/DomainUserRoles.js";
import { waitEl } from "../../../../services/utils.js";

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
    // getDomainUserRoles({}).then(r=>{
    //   this.dur = new DomainUserRoles(r);
    this.build();
    // });
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
      let hours = [];
      let sum = 0;
      let count = 0;
      for (const key in resp) {
        const { time } = resp[key];
        hours[key] = this.timeToDecimal(time);
        sum = sum + hours[key];
        if (hours[key] > 0) count++;
      }
      
      const average = sum / count;
      const data = [
        ["L", hours[0] ? hours[0] : 0, average],
        ["M", hours[1] ? hours[1] : 0, average],
        ["X", hours[2] ? hours[2] : 0, average],
        ["J", hours[3] ? hours[3] : 0, average],
        ["V", hours[4] ? hours[4] : 0, average],
        ["S", hours[5] ? hours[5] : 0, average],
        ["D", hours[6] ? hours[6] : 0, average],
      ];
      await charts(this, data);

      waitEl(`#${this.id} path`).then((el) => {
        el.style.opacity = 0.5;
      });
    } catch (error) {
      console.log(error);
    }
  }

  async getData() {
    let dt = this.data;
    try {
      if (!dt.length) {
        const period = getPeriod("this_week");
        const [taskHolder] = await getTaskHoldersUser();
        dt = await getTaskHolderTimeControl({
          taskHolderId: taskHolder.id,
          group: "DAY",
          startDate: period.startDate,
          endDate: period.endDate,
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
}

window.customElements.define("aon-statistics", AonStatistics);
