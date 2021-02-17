import {AonElement} from './AonElement.js';
import { AonIconButton } from "./aon-icon-button.js";

export class AonCard extends AonElement {

 	CARD;
	TITLE;
  TITLE_SECTION1;
  TITLE_SECTION2;
	CONTENT;

	static get observedAttributes() {
		return ['visible'];
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

	get visible() {
		return this.getAttribute('visible');
	}

	set visible(visible) {
		this.setAttribute('visible', visible);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('visible' === name){
			if(this.getAttribute('visible') != undefined && 'false' == this.getAttribute('visible')){
				this.style.display = 'none';
			} else {
				this.style.display = 'block';
			}
		}
	}

	constructor () {
		super();
		this.CARD = this.id + 'Card';
	  this.TITLE = this.id + 'Title';
    this.TITLE_SECTION1 = this.TITLE + 'Section1';
    this.TITLE_SECTION2 = this.TITLE + 'Section2';
		this.CONTENT = this.id + 'Content';
	}

	connectedCallback () {
		this.build();
	}

	build() {
		let div = this.createElement('div');
		div.id = this.CARD;
    div.className = 'aonCard';
		this.appendChild(div);

		let title = this.createElement('div');
		title.id = this.TITLE;
		title.className = 'aonCardTitle';

    let section1 = this.createElement('section');
    section1.id = this.TITLE_SECTION1;
    section1.className = 'aonCardTitleSection';
    section1.innerHTML = this.title;
    title.appendChild(section1);

    let section2 = this.createElement('section');
    section2.id = this.TITLE_SECTION2;
    section2.className = 'aonCardTitleSection aonCardTitleSectionEnd';
    title.appendChild(section2);

		div.appendChild(title);

		let content = this.createElement('div');
		content.id = this.CONTENT;
		div.appendChild(content);
	}

  cleanSection2() {
    let section2 = this.getElement(this.TITLE_SECTION2);
    section2.innerHTML = '';
  }

  addTitleButton(name, icon, selected, fn) {
    let button = new AonIconButton();
    button.id = this.TITLE_SECTION2 + name + 'Button';
    button.icon = icon;
    button.style.position = 'relative';
    button.title = name;

    if(selected) {
      button.background = 'lightgray';
    }

    button.addEventListener('click', fn);
    let title = this.getElement(this.TITLE_SECTION2);
    title.appendChild(button);
  }

	setContent(el) {
		this.getElement(this.CONTENT).appendChild(el);
	}

	setContentHTML(html) {
		this.getElement(this.CONTENT).innerHTML = html;
	}

	setVisible(visible) {
		this.setAttribute('visible', visible);
	}

	setBackground(color) {
		this.getElement(this.CARD).style.backgroundColor = color;
	}
}
window.customElements.define('aon-card',  AonCard);
