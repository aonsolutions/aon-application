import {AonElement} from './AonElement.js';

import './aon-icon.js';

export class AonIconButton extends AonElement {

	BUTTON;
	ICON;
	AON_ICON;
	IMAGE;

	static get observedAttributes() {
		return ['disabled', 'visible', 'icon', 'color'];
	}

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get icon() {
		return this.getAttribute('icon');
	}

	set icon(icon) {
		this.setAttribute('icon', icon);
	}

	get aonIcon() {
		return this.getAttribute('aonIcon');
	}

	set aonIcon(aonIcon) {
		this.setAttribute('aonIcon', aonIcon);
	}

	get image() {
		return this.getAttribute('image');
	}

	set image(image) {
		this.setAttribute('image', image);
	}

	get color() {
		return this.getAttribute('color');
	}

	set color(color) {
		this.setAttribute('color', color);
	}

	get outlined() {
		return this.getAttribute('outlined');
	}

	set outlined(outlined) {
		this.setAttribute('outlined', outlined);
	}

	get noHover() {
		return this.getAttribute('noHover');
	}

	set noHover(noHover) {
		this.setAttribute('noHover', noHover);
	}

	get visible() {
		return this.getAttribute('visible');
	}

	set visible(visible) {
		this.setAttribute('visible', visible);
	}

	get disabled() {
		return this.getAttribute('disabled');
	}

	set disabled(disabled) {
		this.setAttribute('disabled', disabled);
	}

	get title() {
		return this.getAttribute('title');
	}

	set title(title) {
		this.setAttribute('title', title);
	}

	get background() {
		return this.getAttribute('background');
	}

	set background(background) {
		this.setAttribute('background', background);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('disabled' === name){
			document.getElementById(this.getAttribute('id') + 'IconButton').setAttribute('disabled', newValue);
		}

		if('visible' === name){
			if(this.getAttribute('visible') != undefined && 'false' == this.getAttribute('visible')){
				this.style.width = '0px';
				this.style.display = 'none';
			} else {
				this.style.width = null;
				this.style.display = 'block';
			}
		}

		if('icon' === name){
			let icon = this.getElement(this.ICON);
			if(icon) icon.innerHTML = this.getAttribute('icon');
		}

		if('image' === name){
			let image = this.getElement(this.IMAGE);
			if(image) image.src = this.getAttribute('image');
		}

		if('color' === name){
			let button = this.getElement(this.BUTTON);
			if(button)
				button.style.color = this.getAttribute('color') ? this.getAttribute('color') : '#5f6368';
		}
	}

	constructor () {
		super();
		this.BUTTON = this.id + 'IconButton';
		this.ICON = this.id + 'Icon';
		this.AON_ICON = this.id + 'AonIcon';
		this.IMAGE = this.id + 'Image';
	}


	connectedCallback () {
		this.appendChild(this.build());
	}

	build() {
		let background = this.hasAttribute('background') ? this.getAttribute('background') : 'transparent';
		let button = document.createElement('button');
		button.setAttribute('id', this.BUTTON);
		button.className = "aonIconButton";
		button.style.color = this.getAttribute('color') ? this.getAttribute('color') : '#5f6368';
		button.style.backgroundColor = background;
		if(this.hasAttribute('title')){
				button.title = this.getAttribute('title');
		}

		if(this.getAttribute('disabled')){
			button.setAttribute('disabled', true);
		}

		if(this.getAttribute('visible') != undefined && 'false' == this.getAttribute('visible')){
			this.style.width = '0px';
			this.style.display = 'none';
		}

		if(!this.getAttribute('noHover')) {
			button.addEventListener('mouseover', () => {
				button.style.backgroundColor = '#f1f1f1';
				button.style.color = 'black';
			});

			button.addEventListener('mouseleave', () => {
				button.style.backgroundColor = background;
				button.style.color = this.getAttribute('color') ? this.getAttribute('color') : '#5f6368';
			});

			this.addEventListener('click', () => {
				button.style.backgroundColor = '#ddd';
			});
		}

		if(this.hasAttribute('icon')){
			let icon = document.createElement('i');
			icon.id = this.ICON;
			icon.className = this.getAttribute('outlined') ? 'material-icons-outlined' : 'material-icons';
			icon.innerHTML = this.getAttribute('icon');
			button.appendChild(icon);
		} else if(this.hasAttribute('image')) {
			let image = document.createElement('img');
			image.id = this.IMAGE;
			image.style.width = '24px';
			image.style.height = '24px';
			image.src = this.getAttribute('image');
		 	button.appendChild(image);
		} if(this.hasAttribute('aonIcon')){
			button.innerHTML = `<aon-icon id="${this.AON_ICON}" icon="${this.aonIcon}"></aon-icon>`;
		}
		return button;
	}
}

window.customElements.define('aon-icon-button',  AonIconButton);
