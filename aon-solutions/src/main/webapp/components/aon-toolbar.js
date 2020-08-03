import './aon-icon-button.js';

(function() {

	class AonToolbar extends HTMLElement {

		static get observedAttributes() {
			return [];
		}

		get id() {
			return this.getAttribute('id');
		}

		set id(id) {
			this.setAttribute('id', id);
		}

		get title() {
			return this.getAttribute('title');
		}

		set title(title) {
			this.setAttribute('title', title);
		}

		attributeChangedCallback(name, oldValue, newValue) {

		}

		constructor () {
			super();
			this.appendChild(this.build());
		}

		connectedCallback () {

		}

		build() {
			let header = document.createElement('header');
			header.setAttribute('id', this.getAttribute('id') + 'aon-toolbar');
			header.className = "aonToolbar";

			let section = document.createElement('section');
			section.setAttribute('id', this.getAttribute('id') + 'aon-toolbar-section');
			section.className = "aonToolbarSection";


			let button = '<aon-icon-button id="' + this.getAttribute('id')
				+ 'aon-toolbar-menu' + '" icon="menu" onclick="toogleNav()"> </aon-icon-button>';
			section.innerHTML = button;

			let title = document.createElement('span');
			title.setAttribute('id', 'aon-toolbar-title');
			title.innerHTML = this.getAttribute('title');
			section.appendChild(title)

			header.appendChild(section);

			let toolSection = document.createElement('section');
			toolSection.setAttribute('id', this.getAttribute('id') + 'aon-toolbar-tool-section');
			toolSection.className = "aonToolbarSection aonToolbarSectionEnd";

			let addbutton = '<aon-icon-button id="' + this.getAttribute('id')
				+ 'AddButton" icon="add"> </aon-icon-button>';
			toolSection.innerHTML = addbutton;

			header.appendChild(toolSection);

		 	return header;
		}
	}

	window.customElements.define('aon-toolbar',  AonToolbar);

})();
