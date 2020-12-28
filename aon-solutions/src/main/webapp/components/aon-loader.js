import {AonElement} from './AonElement.js';

export class AonLoader extends AonElement {

	PROGRESS;
	LOADING;
	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
		this.PROGRESS = this.getAttribute('id') + 'Progress';
		this.LOADING = this.getAttribute('id') + 'Loading';
	}

	connectedCallback () {
		this.innerHTML = `
			<div id="${this.PROGRESS}" class="aonProgress"></div>
			<div id="${this.LOADING}" class="aonLoading"></div>
		`;
	}

	start() {
		this.getElement(this.PROGRESS).style.display = 'flex';
	}

	stop() {
		this.getElement(this.PROGRESS).style.display = 'none';
	}

	startLoading() {
		this.getElement(this.LOADING).style.display = 'block';
	}

	stopLoading() {
		this.getElement(this.LOADING).style.display = 'none';
	}

}

window.customElements.define('aon-loader',  AonLoader);
