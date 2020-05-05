import './aon-menu.js';
import './aon-inputText.js';

class AonHome extends HTMLElement {
	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `

			<style>

				.root-panel {
					width: 100%;
					min-height: 100%;
					height: auto !important;
					height: 100%;
					margin: 0;
				}

			</style>
						
			<aon-menu></aon-menu>
			<div id="rootPanel" class="root-panel" ></div>

		`;
  }
}

window.customElements.define('aon-home', AonHome);
