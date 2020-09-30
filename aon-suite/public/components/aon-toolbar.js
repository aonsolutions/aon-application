(function() {

	class AonToolbar extends HTMLElement {

		static get observedAttributes() {
			return ['title'];
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
			if('title' === name) {
				let title = document.getElementById('aon-toolbar-title');
				title.innerHTML = newValue;
			}
		}

		constructor () {
			super();
			this.appendChild(this.build());
		}

		toogleSidenav(fn) {
			let button = document.getElementById( this.getAttribute('id') + 'aon-toolbar-menu');
			button.addEventListener('click', fn);
		}

		connectedCallback () {

		}

		build() {
			let header = document.createElement('header');
			header.setAttribute('id', this.getAttribute('id') + '	Header');
			header.className = "aonToolbar";

			let section = document.createElement('section');
			section.setAttribute('id', this.getAttribute('id') + 'aon-toolbar-section');
			section.className = "aonToolbarSection";

			let button = '<aon-icon-button id="' + this.getAttribute('id')
				+ 'aon-toolbar-menu' + '" icon="menu"> </aon-icon-button>';
			section.innerHTML = button;

			let title = document.createElement('span');
			title.setAttribute('id', 'aon-toolbar-title');
			title.innerHTML = this.getTitle().toUpperCase();
			section.appendChild(title)

			header.appendChild(section);

			let toolSection = document.createElement('section');
			toolSection.setAttribute('id', this.getAttribute('id') + 'aon-toolbar-tool-section');
			toolSection.className = "aonToolbarSection aonToolbarSectionEnd";

			header.appendChild(toolSection);

		 	return header;
		}

		addButton(name, icon, fn) {
			const id = this.getId() + name + 'Button';
			let button = `<aon-icon-button id="${id}" icon="${icon}"> </aon-icon-button>`;
			let span = document.createElement('span');
			span.innerHTML= button;

			let aonMenu = document.getElementById('aonMenu');
			let toolSection = document.getElementById(this.getId() + 'aon-toolbar-tool-section');
			toolSection.style.paddingRight = this.getAttribute('opened') ? '0px' : '40px';
			toolSection.appendChild(span);

			let b = document.getElementById(id);
			b.addEventListener('click', fn);
		}

		removeButtons() {
			let toolSection = document.getElementById(this.getId() + 'aon-toolbar-tool-section');
			toolSection.innerHTML = '';
		}

		toogleNav() {
			let sidenav = this.getAttribute('id') + 'Sidenav';
			let content = this.getAttribute('id') + 'Content';
			if(document.getElementById(sidenav).style.width === "250px"){
				document.getElementById(sidenav).style.width = "0px";
				document.getElementById(content).style.marginLeft = "0px";
			} else {
				document.getElementById(sidenav).style.width = "250px";
				document.getElementById(content).style['margin-left'] = "250px";
			}
		}


		getId() {
			return this.getAttribute('id');
		}

		getTitle() {
			return this.getAttribute('title');
		}
	}

	window.customElements.define('aon-toolbar',  AonToolbar);

})();
