import { CONSTANT } from '../environments/environments.js';
import {AonElement} from './AonElement.js';

export class AonLoader extends AonElement {

	PROGRESS;
	LOADING;
	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor () {
		super();
		this.PROGRESS = this.getAttribute(CONSTANT.ID) + 'Progress';
		this.LOADING = this.getAttribute(CONSTANT.ID) + 'Loading';
	}

	connectedCallback () {
		this.innerHTML = `
			<div id="${this.PROGRESS}" class="aonProgress"></div>
			<div id="${this.LOADING}" class="aonLoading">
				<div class="bounce1"></div>
				<div class="bounce2"></div>
				<div class="bounce3"></div>
			</div>
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
if(!window.customElements.get('aon-loader')){
	window.customElements.define('aon-loader',  AonLoader);
}
