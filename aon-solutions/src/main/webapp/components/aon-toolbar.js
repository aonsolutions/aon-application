import {AonElement} from './AonElement.js';
import {ToolbarType} from '../models/enums.js';
import { AonSearch } from './aon-search.js';
import { CONSTANT, EVENT } from '../environments/environments.js';
import './aon-icon-button.js';

export class AonToolbar extends AonElement {
	HEADER;
	TITLE_SECTION;
	TITLE_SECTION_SPAN;
	TITLE_SECTION_OPTION;
	TOOL_SECTION;

	static get observedAttributes() {
		return [CONSTANT.TITLE, CONSTANT.OPTION];
	}

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get title() {
		return this.getAttribute(CONSTANT.TITLE);
	}

	set title(title) {
		this.setAttribute(CONSTANT.TITLE, title);
	}

	get option() {
		return this.getAttribute(CONSTANT.OPTION);
	}

	set option(option) {
		this.setAttribute(CONSTANT.OPTION, option);
	}

	get type() {
		return this.getAttribute('type');
	}

	set type(type) {
		this.setAttribute('type', type);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if(CONSTANT.TITLE === name) {
			let title = this.getElement(this.TITLE_SECTION_SPAN);
			if(title) title.innerHTML = newValue;
		}
		if(CONSTANT.OPTION === name) {
			let option = this.getElement(this.TITLE_SECTION_OPTION);
			if(option) option.innerHTML = ' / ' + newValue;
		}
	}

	constructor () {
		super();
		this.HEADER = this.id + 'Header';
		this.TITLE_SECTION = this.HEADER + 'TitleSection';
		this.TITLE_SECTION_MENU = this.TITLE_SECTION + 'Menu';
		this.TITLE_SECTION_SPAN = this.TITLE_SECTION + 'Span';
		this.TITLE_SECTION_OPTION = this.TITLE_SECTION + 'Option';
		this.TOOL_SECTION = this.HEADER + 'ToolSection';

	}

	toogleSidenav(fn) {
		let menu = this.getElement(this.TITLE_SECTION_MENU);
		if(menu) menu.addEventListener('click', fn);
	}

	connectedCallback () {
		this.appendChild(this.build());
	}

	build() {
		let type = this.hasAttribute('type') ? this.getAttribute('type') : ToolbarType.APPLICATION;

		let header = document.createElement('header');
		header.id = this.HEADER;
		if(ToolbarType.SECONDARY === type) {
			header.className = "aonSecondaryToolbar";

			let toolSection = document.createElement('section');
			toolSection.id = this.TOOL_SECTION;
			toolSection.className = "aonToolbarSection";
			header.appendChild(toolSection);

			let titleSection = document.createElement('section');
			titleSection.id = this.TITLE_SECTION;
			titleSection.className = "aonToolbarSection aonToolbarSectionEnd";

			let title = document.createElement('span');
			title.id = this.TITLE_SECTION_SPAN;
			title.className = 'aonSecondaryToolbarTitle';
			title.innerHTML = this.getTitle().toUpperCase();
			titleSection.appendChild(title);
			header.appendChild(titleSection);
		} else {
			header.className = "aonToolbar";

			let titleSection = document.createElement('section');
			titleSection.id = this.TITLE_SECTION;
			titleSection.className = "aonToolbarSection";
			titleSection.innerHTML = `<aon-icon-button id="${this.TITLE_SECTION_MENU}" icon="menu"></aon-icon-button>`;

			let title = document.createElement('span');
			title.id = this.TITLE_SECTION_SPAN;
			title.className = 'aonToolbarTitle';
			title.innerHTML = this.getTitle();
			titleSection.appendChild(title);

			let option = document.createElement('span');
			option.id = this.TITLE_SECTION_OPTION;
			option.style.color = 'gray';
			option.style.fontSize = '14px';
			option.innerHTML = this.option ? '/ ' + this.option : '';
			titleSection.appendChild(option);

			header.appendChild(titleSection);

			let toolSection = document.createElement('section');
			toolSection.id = this.TOOL_SECTION;
			toolSection.className = "aonToolbarSection aonToolbarSectionEnd";

			header.appendChild(toolSection);
		}
		return header;
	}


	removeSeparators() {
		this.querySelectorAll('hr').forEach((item, i) => {
				item.remove();
		});
	}

	addSeparator() {
		let span = document.createElement('span');
		let hr = this.createElement('hr');
		hr.className = 'aonSeparator';
		span.appendChild(hr);

		let toolSection = this.getElement(this.TOOL_SECTION);
		if(toolSection.children.length > 0) {
			toolSection.insertBefore(span, toolSection.children[0]);
		} else toolSection.appendChild(span);
	}

	addButton(name, icon, fn) {
		const id = this.TOOL_SECTION + name + 'Button';
		if(this.getElement(id) == null) {
			let button = `<aon-icon-button id="${id}" icon="${icon}"> </aon-icon-button>`;
			let span = document.createElement('span');
			span.innerHTML= button;

			let toolSection = this.getElement(this.TOOL_SECTION);
			toolSection.style.paddingRight = this.getAttribute('opened') || this.isMobile() ? '0px' : '40px';
			if(toolSection.children.length > 0) {
				toolSection.insertBefore(span, toolSection.children[0]);
			} else toolSection.appendChild(span);

			let b = document.getElementById(id);
			b.addEventListener('click', fn);
		}
	}

	addSearchButton(advanced) {
		let search = new AonSearch();
		search.id = this.TOOL_SECTION + 'Search';
		search.addEventListener(EVENT.SEARCH, (event) => {
			this.dispatchEvent(new CustomEvent('search',{detail: event.detail}));
		});

		let toolSection = this.getElement(this.TOOL_SECTION);
		toolSection.style.paddingRight = this.getAttribute('opened') || this.isMobile() ? '0px' : '40px';
		if(toolSection.children.length > 0) {
			toolSection.insertBefore(search, toolSection.children[0]);
		} else toolSection.appendChild(search);
	}

	addButton2(action, fn) {
		const id = this.TOOL_SECTION + action.id + 'Button';
		let span = document.createElement('span');
		if(action.aonIcon) {
			span.innerHTML= `<aon-icon-button id="${id}" aonIcon="${action.aonIcon}" title="${action.name}"> </aon-icon-button>`;
		} else {
			span.innerHTML= `<aon-icon-button id="${id}" icon="${action.icon}" title="${action.name}"> </aon-icon-button>`;
		}
		let toolSection = this.getElement(this.TOOL_SECTION);
		toolSection.style.paddingRight = this.getAttribute('opened') || this.isMobile() ? '0px' : '40px';
		if(toolSection.children.length > 0) {
			toolSection.insertBefore(span, toolSection.children[0]);
		} else toolSection.appendChild(span);

		let b = document.getElementById(id);
		b.addEventListener('click', fn);
	}

	addButtonTitle(action, fn) {
		const id = this.TITLE_SECTION + action.id + 'Button';
		if(this.getElement(id) == null) {
			let span = document.createElement('span');
			// span.style.marginRight = '20px';
			span.innerHTML = `<aon-icon-button id="${id}" icon="${action.icon}" title="${action.name}"> </aon-icon-button>`;

			let titleSection = this.getElement(this.TITLE_SECTION);
			titleSection.insertBefore(span, titleSection.children[0]);

			let b = document.getElementById(id);
			b.addEventListener('click', fn);
		}
	}

	addTitleToolSection(title) {
		const id = this.TOOL_SECTION + 'Title';
		if(this.getElement(id) == null) {
			this.addSeparator();
			let span = document.createElement('span');
			span.style.marginRight = "10px";
			if(title) span.innerHTML = title.toString().toUpperCase();
			span.id = id;
			let titleSection = this.getElement(this.TOOL_SECTION);
			titleSection.insertBefore(span, titleSection.children[0]);
		}
	}

	removeButton(name) {
		let button = this.getElement(this.TOOL_SECTION + name + 'Button');
		if(button) button.remove();
	}

	removeButtons() {
		this.getElement(this.TOOL_SECTION).innerHTML = '';
	}

	toogleNav() {
		this.dispatchEvent(new CustomEvent('toogle'));
		let sidenav = this.id + 'Sidenav';
		let content = this.id + 'Content';
		if(document.getElementById(sidenav).style.width === "250px"){
			document.getElementById(sidenav).style.width = "0px";
			document.getElementById(content).style.marginLeft = "0px";
		} else {
			document.getElementById(sidenav).style.width = "250px";
			document.getElementById(content).style['margin-left'] = "250px";
		}
	}

	getTitle() {
		return this.hasAttribute('title') ? this.getAttribute('title') : '';
	}
}
if(!window.customElements.get('aon-toolbar')){
	window.customElements.define('aon-toolbar',  AonToolbar);
}
