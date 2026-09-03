import {AonElement} from './AonElement.js';
import {ToolbarType} from '../models/enums.js';
import { AonSearch } from './aon-search.js';
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonIconButton } from './aon-icon-button.js';

export class AonToolbar extends AonElement {
	HEADER;
	TITLE_SECTION;
	TITLE_SECTION_SPAN;
	TITLE_SECTION_OPTION;
	TOOL_SECTION;
	TOOL_SECTION_TITLE;

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
		return this.getAttribute(CONSTANT.TYPE);
	}

	set type(type) {
		this.setAttribute(CONSTANT.TYPE, type);
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
	}

	toogleSidenav(fn) {
		let menu = this.getElement(this.TITLE_SECTION_MENU);
		if(menu) menu.addEventListener(EVENT.CLICK, fn);
	}
	
	toogleClose(){
		let titleSectionMenu = this.getElement(this.TITLE_SECTION_MENU);
		let titleSectionMenuIcon = titleSectionMenu.querySelector('i');
		titleSectionMenuIcon.classList.toggle("closeIcon");
	}

	connectedCallback () {
		this.initialize();
		this.appendChild(this.build());
	}

	initialize() {
		this.id = this.id || Math.random().toString(36).substring(7);
		this.HEADER = this.id + 'Header';
		this.TITLE_SECTION = this.HEADER + 'TitleSection';
		this.TITLE_SECTION_MENU = this.TITLE_SECTION + 'Menu';
		this.TITLE_SECTION_SPAN = this.TITLE_SECTION + 'Span';
		this.TITLE_SECTION_OPTION = this.TITLE_SECTION + 'Option';
		this.TOOL_SECTION = this.HEADER + 'ToolSection';
		this.TOOL_SECTION_TITLE = this.TOOL_SECTION + 'Title';
	}

	build() {
		let type = this.hasAttribute(CONSTANT.TYPE) ? this.getAttribute(CONSTANT.TYPE) : ToolbarType.APPLICATION;

		let header = this.createElement('header');
		header.id = this.HEADER;
		if(ToolbarType.SECONDARY === type) {
			header.className = "aonSecondaryToolbar";

			let toolSection = this.createElement(TAG.SECTION);
			toolSection.id = this.TOOL_SECTION;
			toolSection.className = "aonToolbarSection";
			header.appendChild(toolSection);

			let titleSection = this.createElement(TAG.SECTION);
			titleSection.id = this.TITLE_SECTION;
			titleSection.className = "aonToolbarSection aonToolbarSectionEnd";

			let title = this.createElement(TAG.SPAN);
			title.id = this.TITLE_SECTION_SPAN;
			title.className = 'aonSecondaryToolbarTitle';
			title.innerHTML = this.getTitle().toUpperCase();
			titleSection.appendChild(title);
			header.appendChild(titleSection);
		} else {
			header.className = CSS.AON_TOOLBAR;

			let titleSection = this.createElement(TAG.SECTION);
			titleSection.id = this.TITLE_SECTION;
			titleSection.className = "aonToolbarSection";
			let aib = new AonIconButton();
			aib.id = this.TITLE_SECTION_MENU;
			aib.icon  = "menu";
			titleSection.appendChild(aib);

			let title = this.createElement(TAG.SPAN);
			title.id = this.TITLE_SECTION_SPAN;
			title.className = 'aonToolbarTitle';
			title.innerHTML = this.getTitle();
			titleSection.appendChild(title);

			let option = this.createElement(TAG.SPAN);
			option.id = this.TITLE_SECTION_OPTION;
			option.className = 'aonToolbarSubtitle';

			option.innerHTML = this.option ? ' / ' + this.option : '';
			titleSection.appendChild(option);

			header.appendChild(titleSection);

			let toolSection = this.createElement(TAG.SECTION);
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
		let span = this.createElement(TAG.SPAN);
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
			let aib = new AonIconButton();
			aib.id = id;
			aib.icon  = icon;
			aib.addEventListener(EVENT.CLICK, fn);
			let span = this.createElement(TAG.SPAN);
			span.appendChild(aib);

			let aonMenu = this.getElement('aonMenu');
			let toolSection = this.getElement(this.TOOL_SECTION);
			toolSection.style.paddingRight = (aonMenu && aonMenu.getAttribute('opened')) || this.isMobile() ? '0px' : '40px';
			toolSection.style.marginBottom = '2px';
			if(toolSection.children.length > 0) {
				toolSection.insertBefore(span, toolSection.children[0]);
			} else toolSection.appendChild(span);
		}
	}

	addSearchButton(opened=false, filterDocumental=false) {
		let search = this.getSearchButton();
		if(!search) {
			search = new AonSearch(filterDocumental);
			search.id = this.TOOL_SECTION + 'Search';
			const searchFn = (event) => this.dispatchEvent(new CustomEvent(EVENT.SEARCH,{detail: event.detail}));
			search.addEventListener(EVENT.SEARCH, searchFn);

			let aonMenu = this.getElement('aonMenu');
			let toolSection = this.getElement(this.TOOL_SECTION);
			toolSection.style.paddingRight = (aonMenu && aonMenu.getAttribute('opened')) || this.isMobile() ? '0px' : '40px';
			toolSection.style.marginBottom = '2px';
			if(toolSection.children.length > 0) {
				toolSection.insertBefore(search, toolSection.children[0]);
			} else{
				toolSection.appendChild(search);
			}

			if(opened){
				search.openSearch();
			}
		}

		return search;
	}

	cleanSearchValue() {
		let search = this.getSearchButton();
		if(search) {
			let input = this.getElement(search.SEARCH_INPUT);
			input.value = '';
		}
	}

	getSearchButton(){
		return this.getElement(this.TOOL_SECTION + 'Search');
	}

	addButton2(action, fn) {
		let aib = new AonIconButton();
		aib.id = this.TOOL_SECTION + action.id + 'Button';;
		if(!this.getElement(aib.id)){
			let span = this.createElement(TAG.SPAN);

			aib.title = action.name;
			aib.addEventListener(EVENT.CLICK, fn);
			if(action.aonIcon){
				aib.aonIcon = action.aonIcon;
			} else if (action.image)  {
				aib.image = action.image;
			} else{
				aib.icon = action.icon;
			}
			
			span.appendChild(aib);

			let aonMenu = this.getElement('aonMenu');
			let toolSection = this.getElement(this.TOOL_SECTION);
			toolSection.style.paddingRight = (aonMenu && aonMenu.getAttribute('opened')) || this.isMobile() ? '0px' : '40px';
			toolSection.style.marginBottom = '2px';
			if(toolSection.children.length > 0) {
				toolSection.insertBefore(span, toolSection.children[0]);
			} else toolSection.appendChild(span);
		}
		return aib;
	}

	addButton2End(action, fn) {
		const id = this.TOOL_SECTION + action.id + 'Button';
		let aib = new AonIconButton();
		aib.id = id;
		if(!this.getElement(id)){

			let span = this.createElement(TAG.SPAN);

			aib.title = action.name;
			aib.addEventListener(EVENT.CLICK, fn);
			if(action.aonIcon){
				aib.aonIcon = action.aonIcon;
			} else 
				aib.icon = action.icon;
			
			span.appendChild(aib);

			let aonMenu = this.getElement('aonMenu');
			let toolSection = this.getElement(this.TOOL_SECTION);
			toolSection.style.paddingRight = (aonMenu && aonMenu.getAttribute('opened')) || this.isMobile() ? '0px' : '40px';
			toolSection.style.marginBottom = '2px';
			toolSection.appendChild(span);
		}
		return aib;
	}

	addButtonAfter(action, fn , after) {
		const id = this.TOOL_SECTION + action.id + 'Button';
		let aib = new AonIconButton();
		aib.id = id;
		if(!this.getElement(id)){
			let span = this.createElement(TAG.SPAN);
			aib.title = action.name;
			aib.addEventListener(EVENT.CLICK, fn);
			if(action.aonIcon){
				aib.aonIcon = action.aonIcon;
			} else 
				aib.icon = action.icon;
			
			span.appendChild(aib);
	
			let aonMenu = this.getElement('aonMenu');
			let toolSection = this.getElement(this.TOOL_SECTION);
			toolSection.style.paddingRight = (aonMenu && aonMenu.getAttribute('opened')) || this.isMobile() ? '0px' : '40px';
			toolSection.style.marginBottom = '2px';
			if(after && this.getElement(this.TOOL_SECTION + after + 'Button')) {
				let btn = this.getElement(this.TOOL_SECTION + after + 'Button');
				toolSection.insertBefore(span, btn.parentNode);
			} else 
				toolSection.appendChild(span);
		}
		return aib;
	}

	addButtonTitle(action, fn) {
		const id = this.TITLE_SECTION + action.id + 'Button';
		if(this.getElement(id) == null) {
			let span = this.createElement(TAG.SPAN);
			let aib = new AonIconButton();
			aib.id = id;
			aib.icon  = action.icon;
			aib.title = action.name;
			if (fn) aib.addEventListener(EVENT.CLICK, fn);
			span.appendChild(aib);
			let titleSection = this.getElement(this.TITLE_SECTION);
			titleSection.insertBefore(span, titleSection.children[0]);
			return aib;
		}
		return null;
	}

	showButton(id, show = false) {
		const aib = this.getElement(this.TOOL_SECTION + id + 'Button');
		if(aib!=null && aib.parentNode!=null) {
			aib.parentNode.style.display = show ? 'block' : 'none';
		}
	}

	addTitleToolSection(title, uppercase = true) {
		if(this.getElement(this.TOOL_SECTION_TITLE) == null) {
			this.addSeparator();
			let span = this.createElement(TAG.SPAN);
			span.id = this.TOOL_SECTION_TITLE;
			span.style.marginRight = "10px";
			if(title) span.innerHTML = uppercase ? title.toString().toUpperCase() : title;
			let titleSection = this.getElement(this.TOOL_SECTION);
			titleSection.insertBefore(span, titleSection.children[0]);
		}
	}

	getTitleSection(){
		return this.getElement(this.TITLE_SECTION);
	}
	getToolSection(){
		return this.getElement(this.TOOL_SECTION);
	}

	removeButton(name) {
		let button = this.getElement(this.TOOL_SECTION + name + 'Button');
		if(button) button.remove();
	}

	removeButtons() {
		let section = this.getElement(this.TOOL_SECTION);
		if(section) section.innerHTML = '';
	}

	toogleNav() {
		this.dispatchEvent(new CustomEvent('toogle'));
		const sidenav = this.id + 'Sidenav';
		const content = this.id + 'Content';
		const sidenavEl = this.getElement(sidenav);
		const contentEl = this.getElement(content);
		if(sidenavEl.style.width === "250px"){
			sidenavEl.style.width = "0px";
			contentEl.style.marginLeft = "0px";
		} else {
			sidenavEl.style.width = "250px";
			contentEl.style.marginLeft = "250px";
		}
	}

	getTitle() {
		return this.hasAttribute(CONSTANT.TITLE) ? this.getAttribute(CONSTANT.TITLE) : '';
	}
}
if(!window.customElements.get('aon-toolbar')){
	window.customElements.define('aon-toolbar',  AonToolbar);
}
