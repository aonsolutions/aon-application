(function() {
	
	const template = document.createElement('template');
	  template.innerHTML = `
	  	<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
		<link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css">
		<script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
	    
	    <style>
		  	
		</style>

		<table class="mdl-data-table mdl-js-data-table mdl-data-table--selectable mdl-shadow--2dp">
			<thead>
				<tr id="aon-table-header" >
		  			
				</tr>
			</thead>
			<tbody id="aon-table-body">
	
			</tbody>
		</table>
	  `;
	  
	class AonTable extends HTMLElement {
		
		get header() {
			return this._user;
		}
		
		set header(val) {
			this._user = val;
		}
		
		get values() {
			return this._user;
		}
		
		set values(val) {
			this._user = val;
		}
		
		constructor () {
			super();
			this._user = {};
			this.attachShadow({mode: 'open'});
		    this.shadowRoot.appendChild(template.content.cloneNode(true));
		}
		
		connectedCallback () {
			
		}
	}

	window.customElements.define('aon-table', AonTable);
	window.createTable = createTable;
	
})();	
