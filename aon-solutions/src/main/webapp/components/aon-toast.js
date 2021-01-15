import { AonElement } from './AonElement.js';

export class AonToast extends AonElement {

	DIV;

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor() {
		super();
		this.id = this.id || 'aonToast';
		this.DIV = this.id + 'Div';
	}

	connectedCallback() {
		this.innerHTML = `
			<div id="${this.DIV}" class="aonToast"></div>
		`;
	}

	start(options) {
		let toast = this.getElement(this.DIV);
		let { message, delay, type } = options;
		let color = '#333';

		if (!delay) delay = 3000;
		if (type === 'error') color = '#f44336';
		else if (type === 'success') color = '#4CAF6E';
		else if (type === 'primary') color = '#2196f3';

		toast.classList.add("aonToastShow");
		toast.innerHTML = message;
		toast.style.background = color;

		setTimeout(() => toast.classList.remove('aonToastShow'), delay);
	}

}

window.customElements.define('aon-toast', AonToast);
