import {getElement} from '../services/utils.js'

class AonLoader extends HTMLElement {

	PROGRESS;

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
		this.PROGRESS = this.getAttribute('id') + 'Progress';
	}

	connectedCallback () {
		this.innerHTML = `
			<div id="${this.PROGRESS}" class="aonProgress"></div>
		`;
	}

	start() {
		getElement(this.PROGRESS).style.display = 'flex';
	}

	stop() {
		getElement(this.PROGRESS).style.display = 'none';
	}
}

window.customElements.define('aon-loader',  AonLoader);
