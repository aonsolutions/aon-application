import {AonElement} from '../../components/AonElement.js';
import {getTaskHolders, getTimeControl, saveTimeControl} from '../../services/service.js';
import {getPosition} from '../../services/maps.js';
import { timePaser } from '../../services/utils.js';
import { AonSelect } from '../../components/aon-select.js';

export class AonSign extends AonElement {

  _timeAction;
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
    this.AON_SIGN = 'aonSign';
    this.id = this.id || this.AON_SIGN;
    this.CONTENT = this.id + 'Content';
    this.TIME = this.id + 'Time';
    getTaskHolders().then(r => {
      if(r.length > 0) {
        this._taskHolders = r;
        this._taskHolder = r[0].id;
        this.build();
      }
    });

  }

  build(){
    if(this._taskHolders.length > 1){
      let company = this.createElement('div');
      company.style.width = '200px';
      this.appendChild(company);
      let select = new AonSelect();
      select.id = 'aonSignSelect2';
      select.title = 'Empresa';
      select.options = JSON.stringify(this._taskHolders.map(c => {
        return {
          value: c.id,
          name: c.company
        }
      }));
      select.addEventListener('change', () => {
        this._taskHolder = select.value;
        getTimeControl({task_holder: this._taskHolder}).then(r => this.buildSignin(r));
      });
      company.appendChild(select);
      select.value = this._taskHolders[0].id

    }

    let time = this.createElement('div');
    // time.style.marginLeft = '47px';
    time.style.fontSize = '30px';
    time.id = this.TIME;
    time.innerHTML = "00:00:00";
    this.appendChild(time);
    let div = this.createElement('div');
    div.id = this.CONTENT;
    this.appendChild(div);
    getTimeControl({task_holder: this._taskHolder}).then(r => this.buildSignin(r));
  }

  entrada() {
    this.clearElement(this.CONTENT);
		let content = document.getElementById(this.CONTENT);

		let button = document.createElement('button');
    button.className = 'aonButton';
		button.style.backgroundColor = '#86D364';
		button.style.marginRight = '10px';
    button.style.width = '100px';
    button.style.padding = '1rem 1rem';
		button.innerHTML = 'ENTRADA';
		button.addEventListener('click', () => {
      getPosition().then(position => {
        let signin = {status: 'in', task_holder: this._taskHolder};
        if(position && position.latitude && position.longitude)
          signin.coordinates = position.latitude + ',' + position.longitude;
        saveTimeControl(signin).then(r => this.buildSignin(r));
      });
    });
		content.appendChild(button);
	}

	salida() {
    this.clearElement(this.CONTENT);
    let content = document.getElementById(this.CONTENT);

		let button = document.createElement('button');
    button.className = 'aonButton';
		button.style.backgroundColor = '#DC4D30';
		button.style.marginRight = '10px';
    button.style.width = '100px';
    button.style.padding = '1rem 1rem';
		button.innerHTML = 'SALIDA';
		button.addEventListener('click', () => {
      getPosition().then(position => {
        let signin = {status: 'out', task_holder: this._taskHolder};
        if(position)
          signin.coordinates = position.latitude + ',' + position.longitude;
        saveTimeControl(signin).then(r => this.buildSignin(r));
      });
		});
		content.appendChild(button);

		let button2 = document.createElement('button');
    button2.className = 'aonButton';
		button2.style.backgroundColor = '#F39F1D';
		button2.style.marginRight = '10px';
    button2.style.width = '100px';
    button.style.padding = '1rem 1rem';
		button2.innerHTML = 'PAUSA';
		button2.addEventListener('click', () => {
      getPosition().then(position => {
        let signin = {status: 'pause', task_holder: this._taskHolder};
        if(position)
          signin.coordinates = position.latitude + ',' + position.longitude;
        saveTimeControl(signin).then(r => this.buildSignin(r));
      });
		});
		content.appendChild(button2);
	}

	vuelta() {
    this.clearElement(this.CONTENT);
    let content = document.getElementById(this.CONTENT);

		let button = document.createElement('button');
    button.className = 'aonButton';
		button.style.backgroundColor = '#86D364';
		button.style.marginRight = '10px';
    button.style.width = '100px';
    button.style.padding = '1rem 1rem';
		button.innerHTML = 'VUELTA';
		button.addEventListener('click', () => {
      getPosition().then(position => {
        let signin = {status: 'in', task_holder: this._taskHolder};
        if(position)
          signin.coordinates = position.latitude + ',' + position.longitude;
        saveTimeControl(signin).then(r => this.buildSignin(r));
      });
		});
		content.appendChild(button);
	}

  buildSignin(signin) {
		let aonUserConnected = document.getElementById('aonHeaderUserConnected');
    if(signin.status === 'in') {
      let time = signin.time + (new Date().getTime() - signin.in_date);
      if(aonUserConnected) aonUserConnected.style.backgroundColor = '#86D364';
      this.salida();
      this.timeStop();
      this.timeAction(time);
    } else if(signin.status === 'pause') {
      if(aonUserConnected) aonUserConnected.style.backgroundColor = '#F39F1D';
      this.vuelta();
      this.timeStop();
      this.getElement(this.TIME).innerHTML = timePaser(signin.time);
    } else {
      if(aonUserConnected) aonUserConnected.style.backgroundColor = '#DC4D30';
      this.entrada();
      this.timeStop();
      this.getElement(this.TIME).innerHTML = timePaser(signin.time);
    }
  }

  timeAction(time) {
    let timeDiv = document.getElementById(this.TIME);
    if(timeDiv){
      timeDiv.innerHTML = timePaser(time);
    } else this.timeStop();
    this._timeAction = setTimeout( () => this.timeAction(time + 1000), 1000);
  }

  timeStop() {
    clearTimeout(this._timeAction);
  }

}
window.customElements.define('aon-sign', AonSign);
