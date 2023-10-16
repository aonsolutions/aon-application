import {AonElement} from './AonElement.js';
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonIconButton } from './aon-icon-button.js';
import * as LS from "../services/localStorageService.js";

export class AonCard extends AonElement {
 	CARD;
	TITLE;
	TITLE_SECTION1;
	TITLE_SECTION2;
	CONTENT;
	app;

	static get observedAttributes() {
		return [CONSTANT.ID, CONSTANT.VISIBLE, 'flex'];
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

	get visible() {
		return this.getAttribute(CONSTANT.VISIBLE);
	}

	set visible(visible) {
		this.setAttribute(CONSTANT.VISIBLE, visible);
	}

	get flex() {
		return CONSTANT.TRUE == this.getAttribute('flex');
	}

	set flex(v) {
		this.setAttribute('flex', v);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if(CONSTANT.VISIBLE === name){
			if(this.getAttribute(CONSTANT.VISIBLE) != undefined && 'false' == this.getAttribute(CONSTANT.VISIBLE)){
				this.style.display = 'none';
			} else {
				this.style.display = 'block';
			}
		} else if("flex" === name){
			let div = this.getElement(this.CARD);
			if(div) div.classList.add("aonCardFlex");
		}
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.build();
	}

	initialize(){
		this.CARD = this.id + 'Card';
		this.TITLE = this.id + 'Title';
		this.TITLE_SECTION1 = this.TITLE + 'Section1';
		this.TITLE_SECTION2 = this.TITLE + 'Section2';
		this.CONTENT = this.id + 'Content';
	}

	build() {
		let div = this.createElement(TAG.DIV);
		div.id = this.CARD;
    	div.className = 'aonCard';
		if(LS.isNewTheme()) {
			div.style.boxShadow = "none";
			div.classList.add('aonCardHover');
		} 
		if(this.flex) div.classList.add("aonCardFlex");
		this.appendChild(div);

		let title = this.createElement(TAG.DIV);
		title.id = this.TITLE;
		title.className = 'aonCardTitle';

		if(LS.isNewTheme() && this.getApp()) {

			let arrowTitleSpan = this.createElement(TAG.SPAN);
			arrowTitleSpan.className = CSS.AON_SIDENAV_TITLE_ARROW;
			arrowTitleSpan.style.borderColor = this.getApp().color;
			arrowTitleSpan.style.height = '2.35rem';
			title.appendChild(arrowTitleSpan);
		}
		let section1 = this.createElement(TAG.SECTION);
		section1.id = this.TITLE_SECTION1;
		section1.className = 'aonCardTitleSection';
		section1.innerHTML = this.title;
		title.appendChild(section1);

		let section2 = this.createElement(TAG.SECTION);
		section2.id = this.TITLE_SECTION2;
		section2.className = 'aonCardTitleSection aonCardTitleSectionEnd';
		title.appendChild(section2);

		div.appendChild(title);

		let content = this.createElement(TAG.DIV);
		content.id = this.CONTENT;
		div.appendChild(content);
	}

	cleanSection2() {
		let section2 = this.getElement(this.TITLE_SECTION2);
		section2.innerHTML = '';
	}

	addTitleButton(name, icon, selected, fn) {
		let id = this.TITLE_SECTION2 + name + 'Button';
		let background = selected ? 'lightgray' : 'transparent';
		let aib = new AonIconButton();
		aib.id = id;
		aib.icon = icon;
		aib.title = name;
		aib.addEventListener(EVENT.CLICK, fn);
		aib.background = background;
		aib.style.position = "relative";
		let span = this.createElement(TAG.SPAN);
		span.id = id + "Span";
		span.appendChild(aib);
		
		let title = this.getElement(this.TITLE_SECTION2);
		title.appendChild(span);
	}

	addSection2(element) {
		let title = this.getElement(this.TITLE_SECTION2);
		title.appendChild(element);
	}

	getCardTitle1() {
		return this.getElement(this.TITLE_SECTION1);
	}

	setTitleSection1(title){
		this.getCardTitle1().innerHTML = title
	}

	getCardTitle2(){
		return this.getElement(this.TITLE_SECTION2);
	}

	setContent(el) {
		this.addContent(el);
		// this.getContent().appendChild(el);
	}

	clear(){
		this.getContent().innerHTML = "";
	}

	addContent(el) {
		this.getContent().appendChild(el);
	}

	setContentHTML(html) {
		this.getContent().innerHTML = html;
	}
	
	getContent(){
		return this.getElement(this.CONTENT);
	}

	setVisible(visible) {
		this.setAttribute(CONSTANT.VISIBLE, visible);
	}

	setBackground(color) {
		let el = this.getElement(this.CARD);
		if(el){
			el.style.transition ="background-color 0.8s ease";
			el.style.backgroundColor = color;
		}
	}

	getCard(){
		return this.getElement(this.CARD);
	}
	
	getCardTitle(){
		return this.getElement(this.TITLE);
	}

	getApp(){
		return this.app;
	}

	setApp(app) {
		this.app = app;
	}
}

if(!window.customElements.get('aon-card')){
  window.customElements.define('aon-card',  AonCard);
}
