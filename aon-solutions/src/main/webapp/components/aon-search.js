import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { AonIconButton } from './aon-icon-button.js';
import {AonElement} from './AonElement.js';

export class AonSearch extends AonElement {

	constructor () {
		super();
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

	connectedCallback () {
		this.initialize();
		this.build();


		// this.innerHTML = `
		// <div id="aon-search-div" style="height: 40px;">
		// 	<aon-icon-button id="aon-search-button" icon="search"></aon-icon-button>
		// 	<input title="Búsqueda" id="search-input"
		// 		autocomplete="off" placeholder="Búsqueda" class="aonSearchBox">
		// </div>
		// `;
		// this.setAttribute('opened', true);
		// let div = document.getElementById('aon-search-div');
		// let input = document.getElementById('search-input');
		// input.addEventListener('keyup', () => {
		// 	this.value = input.value;
	    // 	this.dispatchEvent(new Event('keyup'));
		// });


		// let search = document.getElementById('aon-search-button');
		// search.addEventListener('click', () => {

		// });
	}

	initialize() {
		this.id = this.id || CONSTANT.AON_SEARCH;
		this.SEARCH_BUTTON = this.id + CONSTANT.SEARCH_BUTTON.initCap();
		this.SEARCH_INPUT = this.id + CONSTANT.SEARCH_INPUT.initCap();
		this.ADVANCED_BUTTON = this.id + CONSTANT.ADVANCED_BUTTON.initCap();
		this.OPTIONS = this.id + CONSTANT.OPTIONS.initCap();
		this.SPAN = this.id + CONSTANT.SPAN.initCap();
	}

	build() {
		
		let span = this.createElement(TAG.SPAN);
		span.id = this.SPAN;
		span.style.display = 'inline-flex';

		this.appendChild(span);
		
		let searchButton = new AonIconButton();
		searchButton.id = this.SEARCH_BUTTON;
		searchButton.icon = MATERIAL_ICONS.SEARCH;
		searchButton.title = MSG.SEARCH;
		span.appendChild(searchButton);
;

		// let form = this.createElement(TAG.FORM);
		// span.appendChild(form);

		let input = this.createElement(TAG.INPUT);
		input.id = this.SEARCH_INPUT;
		input.title = MSG.SEARCH;
		input.placeholder = MSG.SEARCH;
		input.style.display = 'none';
		input.style.outline = 'none';
		input.style.border = 'none';
		input.style.marginTop = '1px';
		input.style.height = '37px';
		span.appendChild(input);

		let advancedButton = new AonIconButton();
		advancedButton.style.display = 'none';
		advancedButton.id = this.ADVANCED_BUTTON;
		advancedButton.icon = MATERIAL_ICONS.ARROW_DROP_DOWN;
		span.appendChild(advancedButton);
	
		// let advanced = this.createElement('div');
		// advanced.style.display = 'none';
		// advanced.className = 'aonDialog';
		
		// this.appendChild(advanced);

		advancedButton.addEventListener(EVENT.CLICK, () => {
			this.buildOptions();
		})
		
		input.addEventListener(EVENT.KEYUP, () => {
			this.dispatchEvent(new CustomEvent(EVENT.SEARCH,{detail: input.value}));
		});

		searchButton.addEventListener(EVENT.CLICK, () => {
			if(input.style.display === 'none'){
				if(this.isMobile()) {
					this.style.position = 'absolute';
					this.style.width = '100%';
					this.style.background = 'white';
					span.style.width = '100%';
					advancedButton.style.position = 'absolute';
					advancedButton.style.right = '0px';		
				}
				input.style.display = 'block';
				advancedButton.style.display = 'block';
				span.style.borderBottom = '2px solid #002469';
				input.focus();
			} else {
				if(this.isMobile()) {
					this.style.position = null;
					this.style.width = null;
					this.style.background = 'transparent';
					span.style.width = '100%';
					advancedButton.style.position = 'absolute';
					advancedButton.style.right = '0px';		
				}
				input.value = '';
				this.dispatchEvent(new CustomEvent(EVENT.SEARCH,{detail: input.value}));
				input.style.display = 'none';
				advancedButton.style.display = 'none';
				span.style.borderBottom = '0px';
				this.closeOptions();
			}
		});

		let options = this.createElement('div');
		options.id = this.OPTIONS;
		options.className = CSS.AON_INPUT_LIST_OPTIONS;
	    options.style.width = span.clientWidth;
		this.appendChild(options);
	}

	buildOptions(options) {
		this.clearElementById(this.OPTIONS);
	  	let div = this.getElement(this.OPTIONS);
		div.style.width = this.getElement(this.SPAN).clientWidth;
		// div.classList.add('is-visible');
		// div.innerHTML = 'HOLAA';
		//   div.appendChild(ul)
	
		document.addEventListener('click', function (event) {
			let isClickInside = this.contains(event.target);
			if (!isClickInside) {
				if (div.classList.contains('is-visible')) {
					div.classList.remove('is-visible');
				}
			}
		});
	  }

	  closeOptions() {
		let div = this.getElement(this.OPTIONS);
		if (div && div.classList.contains('is-visible')) {
		  div.classList.remove('is-visible');
		}
	  }
}
if(!window.customElements.get(TAG.AON_SEARCH)){
	window.customElements.define(TAG.AON_SEARCH, AonSearch);
}
