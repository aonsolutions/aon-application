(function() {
	
	const template = document.createElement('template');
	  template.innerHTML = `
	  <head>
		  <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
		  <link href="https://unpkg.com/material-components-web@latest/dist/material-components-web.min.css" rel="stylesheet">
		  <script src="https://unpkg.com/material-components-web@latest/dist/material-components-web.min.js"></script>
	    
		  <style>
		  	.tedi-topbar {
		  		background-color: white !important;
		  		color: black !important;
		  		position: inherit !important;
		  		border-bottom: 1px solid #ddd;
		  	}
		  </style>

		  <header class=" mdc-top-app-bar mdc-top-app-bar--dense tedi-topbar">
		  	<div class="mdc-top-app-bar__row">
		  		<section class="mdc-top-app-bar__section mdc-top-app-bar__section--align-start">
		  			<button class="mdc-icon-button material-icons mdc-top-app-bar__navigation-icon--unbounded" onclick="toogleNav()">menu</button>
		  			<span class="mdc-top-app-bar__title">CONFIGURACIÓN</span> 
		  		</section>
		  		<section class="mdc-top-app-bar__section mdc-top-app-bar__section--align-end">
		  			<button class="mdc-icon-button material-icons mdc-top-app-bar__action-item--unbounded" aria-label="Download">add</button>
		  		</section>
		  	</div>
		</header>
	  `;
	  
	class TediConfigurationToolbar extends HTMLElement {
		
		
		constructor () {
			super();
			this._user = {};
			this.attachShadow({mode: 'open'});
		    this.shadowRoot.appendChild(template.content.cloneNode(true));
		}
		
		connectedCallback () {
			
		}
	}

	window.customElements.define('tedi-configuration-toolbar', TediConfigurationToolbar);
	
})();	
