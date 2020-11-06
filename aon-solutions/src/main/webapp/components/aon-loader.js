import {AonElement} from './AonElement.js';

export class AonLoader extends AonElement {

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
		this.getElement(this.PROGRESS).style.display = 'flex';
	}

	stop() {
		this.getElement(this.PROGRESS).style.display = 'none';
	}
}

window.customElements.define('aon-loader',  AonLoader);
