// import {AonElement} from './AonElement.js';
import { CONSTANT } from '../environments/environments.js';
import './aon-icon-button.js';


export class AonSearchBox extends HTMLElement {

	constructor () {
		super();
	}

	static get observedAttributes() {
		return [CONSTANT.SELECTED];
	}

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get opened() {
		return this.getAttribute(CONSTANT.OPENED);
	}

	set opened(opened) {
		this.setAttribute(CONSTANT.OPENED, opened);
	}

	get value() {
		return this.getAttribute(CONSTANT.VALUE);
	}

	set value(value) {
		this.setAttribute(CONSTANT.VALUE, value);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if(CONSTANT.VALUE === name){
			document.getElementById('search-input').value = newValue;
		}
	}

	connectedCallback () {
		this.innerHTML = `
		<div id="aon-search-div" style="height: 40px;">
			<aon-icon-button id="aon-search-button" icon="search"></aon-icon-button>
			<input title="Búsqueda" id="search-input"
				autocomplete="off" placeholder="Búsqueda" class="aonSearchBox">
		</div>
		`;
		this.setAttribute('opened', true);
		let div = document.getElementById('aon-search-div');
		let input = document.getElementById('search-input');
		input.addEventListener('keyup', () => {
			this.value = input.value;
	    	this.dispatchEvent(new Event('keyup'));
		});


		let search = document.getElementById('aon-search-button');
		search.addEventListener('click', () => {

		});
	}
}
if(!window.customElements.get('aon-search-box')){
	window.customElements.define('aon-search-box', AonSearchBox);
}
