import {AonElement} from './AonElement.js';
import { CONSTANT, CSS, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import './aon-icon-button.js';
import { AonIconButton } from './aon-icon-button.js';
import * as LS from '../services/localStorageService.js';

export class AonSearchBox extends AonElement {

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
		let div = this.createElement(TAG.DIV);
		div.id = 'aon-search-div';
		div.style.height = '40px';
		div.style.minWidth = '400px';
		if(!LS.isNewTheme()) {
			div.style.backgroundColor = '#eaf1fb';
			div.style.borderRadius = '10px';
		} else {
			div.style.borderRadius = '20px';
			div.style.border = '1px solid #d2d2d6';
		}
		
		this.appendChild(div);

		let iconButton = new AonIconButton();
		iconButton.id = 'aon-search-button';
		iconButton.icon = MATERIAL_ICONS.SEARCH;
		div.appendChild(iconButton);

		let input = this.createElement(TAG.INPUT);
		input.id = 'search-input';
		input.autocomplete = 'off';
		input.placeholder = MSG.SEARCH;
		input.title = MSG.SEARCH;
		input.className = LS.isNewTheme() ? CSS.AON_SEARCH_BOX_BETA : CSS.AON_SEARCH_BOX;
		if(!LS.isNewTheme()) input.style.backgroundColor = '#eaf1fb';
		input.style.width = '340px';
		div.appendChild(input);

		this.setAttribute('opened', true);

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
