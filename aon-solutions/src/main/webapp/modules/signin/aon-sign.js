import {AonElement} from '../../components/AonElement.js';
import {getSigninStatus, updateSigninStatus} from '../../services/service.js';

export class AonSign extends AonElement {

  _timeAction;

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
    this.AON_SIGN = 'aonSign';
    this.id = this.id || this.AON_SIGN;
    this.CONTENT = this.id + 'Content';
    this.TIME = this.id + 'Time';
  }

  connectedCallback () {
    this.build();
  }

  build(){
    let time = this.createElement('div');
    // time.style.marginLeft = '47px';
    time.style.fontSize = '30px';
    time.id = this.TIME;
    time.innerHTML = "00:00:00";
    this.appendChild(time);
    let div = this.createElement('div');
    div.id = this.CONTENT;
    this.appendChild(div);
    this.buildSignin();
  }

  entrada() {
    this.clearElement(this.CONTENT);
		let content = document.getElementById(this.CONTENT);

		let button = document.createElement('button');
		button.style.backgroundColor = '#86D364';
		button.style.marginRight = '10px';
    button.style.width = '100px';
    button.style.padding = '1rem 1rem';
		button.innerHTML = 'ENTRADA';
		button.addEventListener('click', () => {
      this.buildSignin({status: 'in'});
		});
		content.appendChild(button);
	}

	salida() {
    this.clearElement(this.CONTENT);
    let content = document.getElementById(this.CONTENT);

		let button = document.createElement('button');
		button.style.backgroundColor = '#DC4D30';
		button.style.marginRight = '10px';
    button.style.width = '100px';
    button.style.padding = '1rem 1rem';
		button.innerHTML = 'SALIDA';
		button.addEventListener('click', () => {
      this.buildSignin({status: 'out'});
		});
		content.appendChild(button);

		let button2 = document.createElement('button');
		button2.style.backgroundColor = '#F39F1D';
		button2.style.marginRight = '10px';
    button2.style.width = '100px';
    button.style.padding = '1rem 1rem';
		button2.innerHTML = 'PAUSA';
		button2.addEventListener('click', () => {
      this.buildSignin({status: 'pause'});
		});
		content.appendChild(button2);
	}

	vuelta() {
    this.clearElement(this.CONTENT);
    let content = document.getElementById(this.CONTENT);

		let button = document.createElement('button');
		button.style.backgroundColor = '#86D364';
		button.style.marginRight = '10px';
    button.style.width = '100px';
    button.style.padding = '1rem 1rem';
		button.innerHTML = 'VUELTA';
		button.addEventListener('click', () => {
      this.buildSignin({status: 'in'});
		});
		content.appendChild(button);
	}

  buildSignin(signin) {
    if(signin)
      updateSigninStatus(signin);

    getSigninStatus().then(
      r => {
    		let aonUserConnected = document.getElementById('aonHeaderUserConnected');
        if(r.status === 'in') {
          if(aonUserConnected) aonUserConnected.style.backgroundColor = '#86D364';
          this.salida();
          this.timeStop();
          this.timeAction(r.time)
        } else if(r.status === 'pause') {
          if(aonUserConnected) aonUserConnected.style.backgroundColor = '#F39F1D';
          this.vuelta();
          this.timeStop();
          this.getElement(this.TIME).innerHTML = this.timePaser(r.time);
        } else {
          if(aonUserConnected) aonUserConnected.style.backgroundColor = '#DC4D30';
          this.entrada();
          this.timeStop();
          this.getElement(this.TIME).innerHTML = this.timePaser(r.time);
        }
      }
    );
  }

  timeAction(time) {
    let timeDiv = document.getElementById(this.TIME);
    if(timeDiv){
      timeDiv.innerHTML = this.timePaser(time);
    } else this.timeStop();
    this._timeAction = setTimeout( () => this.timeAction(time + 1000), 1000);
  }

  timeStop() {
    clearTimeout(this._timeAction);
  }

  timePaser(time) {
    let msecPerMinute = 1000 * 60;
    let msecPerHour = msecPerMinute * 60;

    // Calcular las horas , minutos y segundos
    let hours = Math.floor(time / msecPerHour );
    time = time - (hours * msecPerHour );

    var minutes = Math.floor(time / msecPerMinute );
    time = time - (minutes * msecPerMinute );

    var seconds = Math.floor(time / 1000 );

    return (hours < 10 ? '0' : '') + hours + ':'
      + (minutes < 10 ? '0' : '') + minutes + ':'
      + (seconds < 10 ? '0' : '') + seconds;
  }


}
window.customElements.define('aon-sign', AonSign);
