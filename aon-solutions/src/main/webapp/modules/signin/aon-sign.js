import {AonElement} from '../../components/AonElement.js';
import {getTaskHoldersUser, getTimeControl, saveTimeControl} from '../../services/service.js';
import {getPosition} from '../../services/maps.js';
import { timePaser, setDateTimestampDay } from '../../services/utils.js';
import { AonSelect } from '../../components/aon-select.js';
import { SIGNIN_VIEWS } from "./signinEnums.js";
import { EVENT } from '../../environments/environments.js';

export class AonSign extends AonElement {

  _taskHolders;
  _taskHolder;

  AON_SIGN;
  CONTENT;
  TIME;

  get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

  constructor () {
    super();
  }

  connectedCallback () {
    this.AON_SIGN = SIGNIN_VIEWS.AON_SIGN;
    this.id = this.id || this.AON_SIGN;
    this.CONTENT = this.id + 'Content';
    this.TIME = this.id + 'Time';
    this.applicationEl = this.getApplication();
    getTaskHoldersUser().then(r => {
      if(r.length > 0) {
        this._taskHolders = r;
        this._taskHolder = r[0].id;
        this.build();
      }
    });
  }

  disconnectedCallback(){
    this.clearTimeAction();
  }

  build(){
    this.style.textAlign= "center";
    if(this.isMobile()){
      this.parentNode.style.marginLeft = 0;
    } else {
      this.parentNode.style.paddingLeft = 0;
    }

    if(this._taskHolders.length > 1){
      let company = this.createElement('div');
      company.style.marginLeft = '20px';
      company.style.width = '200px';
      this.appendChild(company);
      let select = new AonSelect();
      select.id = this.AON_SIGN +'Select2';
      select.title = 'Empresa';
      select.options = JSON.stringify(this._taskHolders.map(c => {
        return {
          value: c.id,
          name: c.company
        }
      }));
      select.addEventListener(EVENT.CHANGE, () => {
        this._taskHolder = select.value;
        getTimeControl({task_holder: this._taskHolder}).then(r => this.buildSignin(r));
      });
      company.appendChild(select);
      select.value = this._taskHolders[0].id

    }

    let time = this.createElement('div');
    time.style.fontSize = '30px';
    time.id = this.TIME;
    time.innerHTML = "00:00:00";
    this.appendChild(time);
    let div = this.createElement('div');
    if(this.isMobile()) div.style.marginTop = "5px";
    div.id = this.CONTENT;
    this.appendChild(div);
    getTimeControl({task_holder: this._taskHolder}).then(r => this.buildSignin(r));
  }

  entrada() {
    let content = this.getElement(this.CONTENT);
		if(content){
      this.clearElement(content);
      let button = this.createElement('button');
      button.id = this.id+"Entrada";
      button.className = 'aonButton';
      button.style.backgroundColor = '#86D364';
      button.style.padding = '1rem 1rem';
      button.style.width = '120px';
      button.innerHTML = 'ENTRADA';
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
      let button = this.createElement('button');
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
      let button = this.createElement('button');
      button.id = this.id+"Salida";
      button.className = 'aonButton';
      button.style.backgroundColor = '#DC4D30';
      button.style.marginRight = '10px';
      button.style.width = '100px';
      button.style.padding = '1rem 1rem';
      button.innerHTML = 'SALIDA';
      button.addEventListener(EVENT.CLICK, () => this.saveTimeCtrl('out'));
      content.appendChild(button);
  
      let button2 = this.createElement('button');
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
    let signin = {status, task_holder: this._taskHolder}
    this.disabledButton(true);
    try{
      await getPosition().then(async(position) => {
        if(position && position.latitude && position.longitude){signin.coordinates = position.latitude + ',' + position.longitude;}
        await saveTimeControl(signin).then(r => this.buildSignin(r));
      });
    } catch (error) {
      this.showToast(error);
    }
    this.disabledButton(false);
  }

  disabledButton(disabled){
    let content = this.getElement(this.CONTENT);
    if(content) [...content.querySelectorAll('button')].map(el => el.disabled = disabled);
  }

  buildSignin(signin) {
		const aonUserConnected = this.getElement('aonHeaderUserConnected');
    const timeEl = this.getElement(this.TIME);
    if(signin.status === 'in') {
      const time = signin.time + (new Date().getTime() - signin.in_date);
      if(aonUserConnected) aonUserConnected.style.backgroundColor = '#86D364';
      this.salida();
      this.clearTimeAction();
      this.timeAction(time);
    } else if(signin.status === 'pause') {
      if(aonUserConnected) aonUserConnected.style.backgroundColor = '#F39F1D';
      this.vuelta();
      this.clearTimeAction();
      if(timeEl)timeEl.innerHTML = timePaser(signin.time);
    } else {
      if(aonUserConnected) aonUserConnected.style.backgroundColor = '#DC4D30';
      this.entrada();
      this.clearTimeAction();
      if(timeEl)timeEl.innerHTML = timePaser(signin.time);
    }
    this.divLastTime(signin);
  }

  timeAction(time) {
    let timeDiv = this.getElement(this.TIME);
    if(timeDiv && time>0){
      timeDiv.innerHTML = timePaser(time);
      timeDiv.style.cursor = "default";
    } else this.clearTimeAction();
    this.setTimeAction(setTimeout( () => this.timeAction(time + 1000), 1000));
  }

  divLastTime(signin){
    let content = this.getElement(this.CONTENT);
    if(content && signin && signin.last_date){
      let textStatus = "entrada";
      switch(signin.status){
        case "pause":
          textStatus = 'pausa';
        break;
        case "out":
          textStatus = 'salida';
        break;
      }
      const id = 'lastTimeUser';
      const div = this.getElement(id) || this.createElement('div');
      div.id = id;
      div.style.marginTop = "10px";
      div.style.color = "grey";
      div.style.fontSize = "12px";
      div.style.cursor = "default";
      div.innerHTML = `Ult. ${textStatus} ${setDateTimestampDay(signin.last_date)}`;
      content.append(div);
    }
  }
}
window.customElements.define('aon-sign', AonSign);
