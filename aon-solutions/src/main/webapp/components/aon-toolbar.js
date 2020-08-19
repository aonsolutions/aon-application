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
			header.setAttribute('id', this.getAttribute('id') + 'Toolbar');
			header.className = "aonToolbar";

			let section = document.createElement('section');
			section.setAttribute('id', this.getAttribute('id') + 'aon-toolbar-section');
			section.className = "aonToolbarSection";

			let sidenav = this.getAttribute('id') + 'Sidenav';
			let content = this.getAttribute('id') + 'Content';
			let button = '<aon-icon-button id="' + this.getAttribute('id')
				+ 'aon-toolbar-menu' + '" icon="menu" onclick="toogleNav(\'' + sidenav + '\', \'' + content + '\')"> </aon-icon-button>';
			section.innerHTML = button;

			let title = document.createElement('span');
			title.setAttribute('id', 'aon-toolbar-title');
			title.innerHTML = this.getAttribute('title');
			section.appendChild(title)

			header.appendChild(section);

			let toolSection = document.createElement('section');
			toolSection.setAttribute('id', this.getAttribute('id') + 'aon-toolbar-tool-section');
			toolSection.className = "aonToolbarSection aonToolbarSectionEnd";

			header.appendChild(toolSection);

		 	return header;
		}

		addButton(id, icon) {
			let button = `<aon-icon-button id="${id}" icon="${icon}"> </aon-icon-button>`;
			let span = document.createElement('span');
			span.innerHTML= button;
			let toolSection = document.getElementById(this.getAttribute('id') + 'aon-toolbar-tool-section');
			toolSection.appendChild(span);
		}

		removeButtons() {
			let toolSection = document.getElementById(this.getAttribute('id') + 'aon-toolbar-tool-section');
			toolSection.innerHTML = '';
		}
	}

	window.customElements.define('aon-toolbar',  AonToolbar);

})();
