import {AonElement} from './AonElement.js';

export class AonCard extends AonElement {

	TITLE;
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
		this.TITLE = this.id + 'Title';
		this.CONTENT = this.id + 'Content';
	}

	connectedCallback () {
		this.build();
	}

	build() {
		let div = this.createElement('div');
		div.className = 'aonCard';
		this.appendChild(div);

		let title = this.createElement('div');
		title.id = this.TITLE;
		title.className = 'aonCardTitle';
		title.innerHTML = this.title;
		div.appendChild(title);

		let content = this.createElement('div');
		content.id = this.CONTENT;
		div.appendChild(content);
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
}
window.customElements.define('aon-card',  AonCard);
