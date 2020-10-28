import {isMobile} from '../../services/utils.js';
import './aon-icon-button.js';

(function() {

	class AonToolbar extends HTMLElement {

		static get observedAttributes() {
			return ['title', 'option'];
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

		get option() {
			return this.getAttribute('option');
		}

		set option(option) {
			this.setAttribute('option', option);
		}

		attributeChangedCallback(name, oldValue, newValue) {
			if('title' === name) {
				let title = document.getElementById('aon-toolbar-title');
				if(title) title.innerHTML = newValue;
			}
			if('option' === name) {
				let option = document.getElementById('aonToolbarTitleOption');
				if(option) option.innerHTML = ' / ' + newValue;
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
			section.appendChild(title);

			let option = document.createElement('span');
			option.style.color = 'gray';
			option.style.fontSize = '14px';
			option.setAttribute('id', 'aonToolbarTitleOption');
			option.innerHTML = this.option ? '/ ' + this.option : '';
			section.appendChild(option);

			header.appendChild(section);

			let toolSection = document.createElement('section');
			toolSection.setAttribute('id', this.getAttribute('id') + 'aonToolbarToolSection');
			toolSection.className = "aonToolbarSection aonToolbarSectionEnd";

			header.appendChild(toolSection);

		 	return header;
		}

		addButton(name, icon, fn) {
			const id = this.getId() + name + 'Button';
			let button = `<aon-icon-button id="${id}" icon="${icon}"> </aon-icon-button>`;
			let span = document.createElement('span');
			span.innerHTML= button;

			let toolSection = document.getElementById(this.getId() + 'aonToolbarToolSection');
			toolSection.style.paddingRight = this.getAttribute('opened') || isMobile() ? '0px' : '40px';
			if(toolSection.children.length > 0) {
				toolSection.insertBefore(span, toolSection.children[0]);
			} else toolSection.appendChild(span);

			let b = document.getElementById(id);
			b.addEventListener('click', fn);
		}

		removeButton(name) {
			const id = this.getId() + name + 'Button';
			document.getElementById(id).remove();
		}

		removeButtons() {
			let toolSection = document.getElementById(this.getId() + 'aonToolbarToolSection');
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
