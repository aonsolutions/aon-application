import './aon-icon-button.js';

class AonSearchBox extends HTMLElement {
	constructor () {
		super();
	}

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get opened() {
		return this.getAttribute('opened');
	}

	set opened(opened) {
		this.setAttribute('opened', opened);
	}

	connectedCallback () {
		this.innerHTML = `
		<div id="aon-search-div" style="border-bottom: 1px solid #ddd; height: 40px;">
			<aon-icon-button id="aon-search-button" icon="search"></aon-icon-button>
			<input role="search" title="Búsqueda" id="search-input"
				autocomplete="off" placeholder="Búsqueda" class="tedi-search-box tedi-search-noborder">
			<aon-icon-button id="aon-search-advanced-button" icon="arrow_drop_down"></aon-icon-button>
		</div>
		`;
		this.setAttribute('opened', true);
		let div = document.getElementById('aon-search-div');
		let input = document.getElementById('search-input');
		input.addEventListener('keyup', () => {
			alert(input.value);
		});

		let advanced = document.getElementById('aon-search-advanced-button');
		advanced.addEventListener('click', () => {

		});

		let search = document.getElementById('aon-search-button');
		search.addEventListener('click', () => {
			// if(this.getAttribute('opened')){
			// 	this.removeAttribute('opened');
			// 	input.style.display = 'none';
			// 	advanced.style.display = 'none';
			// 	div.borderBottom = '0px';
			// } else {
			// 	this.setAttribute('opened', true);
			// 	input.style.display = 'block';
			// 	advanced.style.display = 'block';
			// 	div.borderBottom = '1px solid #ddd';
			// }
		});
	}
}

window.customElements.define('aon-search-box', AonSearchBox);
