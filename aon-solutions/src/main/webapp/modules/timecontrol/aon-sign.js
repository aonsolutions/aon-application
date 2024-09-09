import {AonElement} from '../../components/AonElement.js';
import {getPeriod, getTaskHolder, getTaskHoldersUser, getTaskHolderTimeControl, getTimeControl, saveTimeControl, saveTimeControlDetail} from '../../services/service.js';
import {getPosition} from '../../services/maps.js';
import { AonSelect } from '../../components/aon-select.js';
import { SIGNIN_VIEWS } from "./signinEnums.js";
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { timeHour } from './time-control/utils.js';
import { AonDateUtils } from '../utils/AonDateUtils.js';

export class AonSign extends AonElement {
  _taskHolders;
  _taskHolder;
  parent;
  tc;
  AON_SIGN;
  CONTENT;
  TIME;
  TIME_ID;
  // TOTAL_HOUR;
  get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
  }

  initialize() {
    this.AON_SIGN = SIGNIN_VIEWS.AON_SIGN;
    this.id = this.id || this.AON_SIGN;
    this.CONTENT = this.id + 'Content';
    this.TIME = this.id + 'Time';
    this.TOTAL_HOUR = "totalHour";
    this.TIME_ID = "TIME_ID";
    this.applicationEl = this.getApplication();
    this.parent = this.parent || false;
    getTaskHolder({reload:true});
    if(this.isMobile()){
      getPosition().catch(console.error);  // GET POSITION
    }
    if(this.parent) {
      getTaskHoldersUser().then(r => {
        if(r.length > 0) {
         this._taskHolders = r;
         this._taskHolder = r[0].id;
         this.build();
       }
      });
    } else this.build();
  }

  disconnectedCallback(){
    localStorage.removeItem(this.TIME_ID);
  }

  setParent(parent) {
    this.parent = parent;
  }

  setTimeControl(tc) {
    this.tc = tc;
  }

  build(){
    let divGeneral = this.createElement(TAG.DIV);
    divGeneral.style.textAlign = "center";   
    this.appendChild(divGeneral);

    if(this.isMobile()){
      this.parentNode.style.marginLeft = 0;
    } else {
      this.parentNode.style.paddingLeft = 0;
    }

    if(this.parent && this._taskHolders.length > 1){
      let company = this.createElement(TAG.DIV);
      company.style.marginLeft = '20px';
      company.style.width = '200px';
      divGeneral.appendChild(company);

      let select = new AonSelect();
      select.id = this.AON_SIGN +'Select2';
      select.title = MSG.COMPANY;
      select.setOptions(this._taskHolders.map(c => ({value: c.id, name: c.company}) ) );
      company.appendChild(select);
      
      select.addEventListener(EVENT.CHANGE, () => {
        this._taskHolder = select.value;
        getTimeControl({parent:true, task_holder: this._taskHolder}).then(r => this.buildSignin(r));
      });
      
      select.value = this._taskHolders[0].id
    }

    if(!this.getElement(this.TIME)){
      let time = this.createElement(TAG.DIV);
      time.style.fontSize = '30px';
      time.id = this.TIME;
      time.innerHTML = "00:00:00";
      divGeneral.appendChild(time);
    }

    let div = this.createElement(TAG.DIV);
    div.id = this.CONTENT;
    if(this.isMobile()) {
      div.style.marginTop = "5px";
    }

    divGeneral.appendChild(div);

    if(this.tc){
      this.buildSignin(this.tc);
    }
  }

  entrada() {
    let content = this.getElement(this.CONTENT);
		if(content){
      this.clearElement(content);
      let button = this.createElement(TAG.BUTTON);
      button.id = this.id+"Entrada";
      button.className = 'aonButton';
      button.style.backgroundColor = '#86D364';
      button.style.padding = '1rem 1rem';
      button.style.width = '120px';
      button.innerHTML = MSG.ENTRY.toUpperCase();
      if(this.isMobile()){
        button.style.width = "60%";
        button.style.borderRadius = "12px";
      }
      button.addEventListener(EVENT.CLICK, () => this.saveTimeCtrl('in'));
      content.appendChild(button);
    }
	}

  vuelta() {
    let content = this.getElement(this.CONTENT);
    if(content){
      this.clearElement(content);
      let button = this.createElement(TAG.BUTTON);
      button.id = this.id+"Vuelta";
      button.className = 'aonButton';
      button.style.backgroundColor = '#86D364';
      button.style.width = '120px';
      button.style.padding = '1rem 1rem';
      button.innerHTML = 'VUELTA';
      if(this.isMobile()){
        button.style.width = "60%";
        button.style.borderRadius = "12px";
      }
      button.addEventListener(EVENT.CLICK, () => this.saveTimeCtrl('in'));
      content.appendChild(button);
    }
	}

	salida() {
    let content = this.getElement(this.CONTENT);
    if(content){
      this.clearElement(content);
      let button = this.createElement(TAG.BUTTON);
      button.id = this.id+"Salida";
      button.className = 'aonButton';
      button.style.backgroundColor = '#DC4D30';
      button.style.marginRight = '10px';
      button.style.width = '100px';
      button.style.padding = '1rem 1rem';
      button.innerHTML = MSG.EXIT.toUpperCase();
      button.addEventListener(EVENT.CLICK, () => this.saveTimeCtrl('out'));
      content.appendChild(button);
  
      let button2 = this.createElement(TAG.BUTTON);
      button2.className = 'aonButton';
      button2.style.backgroundColor = '#F39F1D';
      button2.style.marginRight = '10px';
      button2.style.width = '100px';
      button2.style.padding = '1rem 1rem';
      button2.innerHTML = 'PAUSA';
      button2.addEventListener(EVENT.CLICK, () => this.saveTimeCtrl('pause'));
      content.appendChild(button2);
    }
	}

  async saveTimeCtrl(status){
    let signin = {status, task_holder: this._taskHolder, parent: this.parent}
    this.disabledButton(true);

    let timeOutPosition = false;

    await getPosition()
    .then(position=>{
      if(position){
        signin.coordinates = position.latitude + ',' + position.longitude;
      }
    })
    .catch(error=>{
      timeOutPosition = error && error.timeout;
      this.showToast(error);
    }); 

    const resp = await saveTimeControl(signin);

    if(timeOutPosition && this.isMobile() && resp && resp.id){
      getPosition()
      .then(position=>{
        if(position){
          r.coordinates = position.latitude + ',' + position.longitude;
          saveTimeControl({...resp, ...signin})
          .then(console.log)
          .catch(console.error);
        }
      })
      .catch(console.error);
    }

    this.buildSignin(resp);

    this.disabledButton(false);
  }

  disabledButton(disabled){
    const content = this.getElement(this.CONTENT);
    if(content){
      content.querySelectorAll('.aonButton')
      .forEach(element => {
        if(disabled){
          element.setAttribute(CONSTANT.DISABLED, true);
        } else {
          element.removeAttribute(CONSTANT.DISABLED);
        }
      });
    }
  }

  buildSignin(signin) {
    this._taskHolder = signin.task_holder.id;
		const aonUserConnected = this.getElement('aonHeaderUserConnected');
    const timeEl = this.getElement(this.TIME);
    timeEl.style.cursor = "default";

    let time = signin.time;
    localStorage.removeItem(this.TIME_ID);

    let color = '#DC4D30';

    if(signin.status === 'in') {
      color =  '#86D364';
      time = signin.time + (new Date().getTime() - signin.in_date);
      this.salida();
      let timeId =  Math.random();
      localStorage.setItem(this.TIME_ID, timeId);
      this.timeAction(time, timeId);
    } else if(signin.status === 'pause') {
      color = '#F39F1D';
      this.vuelta();
    } else {
      this.entrada();
    }

    if(aonUserConnected) {
      aonUserConnected.style.backgroundColor = color;
    }

    this.changeTime(time);
    this.divLastTime(signin);
  }

  async timeAction(time, id) {
    
    this.changeTime(time);

    this.updateHour();

    await new Promise((resolve) => setTimeout(resolve, 1000));

    const aonSign = document.querySelector(`#`+this.id);

    const timeIdStorage = parseFloat(localStorage.getItem(this.TIME_ID));

    if(aonSign && (id ===  timeIdStorage)) {
      this.timeAction(time+ 1000, id);
    }
  }

  changeTime(time){
    let timeDiv = this.getElement(this.TIME);
    if( timeDiv && time >= 0 ) timeDiv.innerHTML = AonDateUtils.timeParser(time);
  }

  divLastTime(signin){
    let content = this.getElement(this.CONTENT);
    if(content && signin && signin.last_date){
      let textStatus = null;
      switch(signin.status){
        case "pause":
          textStatus = MSG.PAUSE.toLowerCase();
          break;
        case "out":
          textStatus = MSG.EXIT.toLowerCase();
          break;
        default:
          textStatus = MSG.ENTRY.toLowerCase();
          break;
      }
      const id = 'lastTimeUser';
      const div = this.getElement(id) || this.createElement(TAG.DIV);
      div.id = id;
      div.classList.add("aonSignDivLastTime");
      div.innerHTML = `${MSG.LAST} ${textStatus} ${AonDateUtils.setDateTimestampDay(signin.last_date)}`;
      content.appendChild(div);
      this.totalHourWeek();
    }
  }

  async totalHourWeek(){
    try {
      if(this._taskHolder){
        const period = getPeriod("this_week");
        let filter = {
          taskHolderId:this._taskHolder, 
          group:"DAY",
          startDate: period.startDate,
          endDate: period.endDate
        };
        let datos = await getTaskHolderTimeControl(filter);
        if(datos){
          let sumHour = datos.reduce((total, {time, status, in_date})=> status && status.indexOf("in")>=0 && in_date ? ((total + (new Date().getTime() - in_date))  + time) : total + time, 0);
          if(sumHour>0){
            let content = this.getElement(this.CONTENT);
            const div = this.getElement(this.TOTAL_HOUR) || this.createElement(TAG.DIV);
            div.id = this.TOTAL_HOUR;
            div.style.marginTop = "10px";
            div.style.color = "grey";
            div.style.fontSize = "12px";
            div.style.cursor = "default";
            div.dataset.sumHour = sumHour;
            div.innerHTML = "Horas semana actual: ";
            const span = this.createElement(TAG.SPAN);
            span.style.fontWeight = 800;
            span.id = this.TOTAL_HOUR+"Span";
            div.appendChild(span);
            content.append(div);
            this.updateHour();
          }
        }
      }
    } catch (error) {console.log(error);}
  }

  updateHour(){
    try {
      const div = this.getElement(this.TOTAL_HOUR);
      const dataset = div.dataset;
      if(dataset){
        const hour = Number(dataset.sumHour);
        const span = this.getElement(div.id+"Span");
        span.innerHTML = timeHour(hour);
        span.style.color = "black";
        dataset.sumHour  = hour + 1000;
      }
    } catch (error) {}
  }
}
window.customElements.define('aon-sign', AonSign);
