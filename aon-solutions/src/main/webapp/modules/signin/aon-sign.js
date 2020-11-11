import {AonElement} from '../../components/AonElement.js';
import {getSigninStatus, updateSigninStatus} from '../../services/service.js';

export class AonSign extends AonElement {

  AON_SIGN;
  CONTENT;

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
  }

  connectedCallback () {
    this.build();
  }

  build(){
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
        if(r.status === 'in') {
          this.salida();
        } else if(r.status === 'pause') {
          this.vuelta();
        } else this.entrada();
      }
    );
  }

}
window.customElements.define('aon-sign', AonSign);
