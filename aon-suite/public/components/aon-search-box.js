import './aon-icon-button.js';

export class AonSearchBox extends HTMLElement {

	constructor () {
		super();
	}

	static get observedAttributes() {
		return ['selected'];
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

	get value() {
		return this.getAttribute('value');
	}

	set value(value) {
		this.setAttribute('value', value);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('value' === name){
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

window.customElements.define('aon-search-box', AonSearchBox);
