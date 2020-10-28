
class AonLoader extends HTMLElement {

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<div id="${this.getAttribute('id') + 'Progress'}" class="aonProgress"></div>
		`;
	}

	start() {
		document.getElementById(this.getAttribute('id') + 'Progress')
			.style.display = 'flex';
	}

	stop() {
		document.getElementById(this.getAttribute('id') + 'Progress')
			.style.display = 'none';
	}
}

window.customElements.define('aon-loader',  AonLoader);
