import { CONSTANT } from '../environments/environments.js';
import {AonElement} from './AonElement.js';
//import '../css/aon-loader.css';

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
			<div id="${this.PROGRESS}" class="aonProgress aonProgressHide"></div>
			<div id="${this.LOADING}" class="aonLoading">
				<div class="bounce1"></div>
				<div class="bounce2"></div>
				<div class="bounce3"></div>
			</div>
		`;
	}

	start() {
		const progressElements = this.querySelectorAll(`.aonProgress`);
    progressElements.forEach(element => {
      element.style.setProperty('display', 'flex', 'important');
    });
	}

	stop() {
		const progressElements = this.querySelectorAll(`.aonProgress`);
    progressElements.forEach(element => {
      element.style.display = 'none';
    });
	}

	startLoading() {
    const loadingElements = this.querySelectorAll(`.aonLoading`);
    loadingElements.forEach(element => {
      element.style.display = 'block';
    });
	}

	stopLoading() {
    const loadingElements = this.querySelectorAll(`.aonLoading`);
    loadingElements.forEach(element => {
      element.style.display = 'none';
    });
	}

}
if(!window.customElements.get('aon-loader')){
  window.customElements.define('aon-loader', AonLoader);
}
