import './aon-header.js';
import './aon-menu.js';
import './aon-inputText.js';

class AonHome extends HTMLElement {
	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-header></aon-header>
			<aon-menu id="aonMenu" class="aonMenu"></aon-menu>
			<div id="rootPanel" class="rootPanel"></div>
		`;
  }
}

window.customElements.define('aon-home', AonHome);
