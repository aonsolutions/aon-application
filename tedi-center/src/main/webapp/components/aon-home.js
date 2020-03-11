import './aon-menu.js';
import './aon-desktop.js';
import './aon-parent.js';

class AonHome extends HTMLElement {
	constructor () {
		super();
	}
	
	connectedCallback () {
		this.innerHTML = `
			<!-- Wide card with share menu button -->
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
			<div id="rootPanel" class="root-panel">
			
			</div>
			`;
  }
}

window.customElements.define('aon-home', AonHome);
