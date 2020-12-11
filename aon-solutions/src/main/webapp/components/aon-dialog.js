import { AonElement } from './AonElement.js';

import * as MSG from "../environments/msg.js";

export class AonDialog extends AonElement {

	DIALOG;
	MAIN;
	TITLE;
	CONTENT;
	ACTION;
	CANCEL;
	ACCEPT;

	static get observedAttributes() {
		return ['width'];
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

	get type() {
		return this.getAttribute('type');
	}

	set type(type) {
		this.setAttribute('type', type);
	}

	get width() {
		return this.getAttribute('width');
	}

	set width(width) {
		this.setAttribute('width', width);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if ('width' === name) {
			let main = this.getElement(this.MAIN);
			if (main) main.style.width = newValue;
		}
	}

	constructor() {
		super();
		this.DIALOG = this.id + 'Dialog';
		this.MAIN = this.DIALOG + 'Main';
		this.TITLE = this.DIALOG + 'Title';
		this.CONTENT = this.DIALOG + 'Content';
		this.ACTION = this.DIALOG + 'Action';
		this.CANCEL = this.ACTION + 'Cancel';
		this.ACCEPT = this.ACTION + 'Accept';
	}

	connectedCallback() {

		this.innerHTML = `
		<div id="${this.DIALOG}" class="aonDialog">
			<div id="${this.MAIN}" class="aonDialogContent">
				<h4 id="${this.TITLE}"></h4>
				<div id="${this.CONTENT}"></div>
				<div id="${this.ACTION}"></div>
			</div>

		</div>
		`;
		this.build();
	}

	clear() {
		this.getElement(this.TITLE).innerHTML = '';
		this.getElement(this.CONTENT).innerHTML = '';
		this.getElement(this.ACTION).innerHTML = '';
	}

	build() {
		let dialog = this.getElement(this.DIALOG);
		let main = this.getElement(this.MAIN);
		let content = this.getElement(this.CONTENT);
		content.style.marginBottom = '20px';

		let divAction = this.getElement(this.ACTION);
		divAction.style.textAlign = 'right';

		if (this.hasAttribute('width')) {
			main.style.width = this.getAttribute('width');
		}

		if (this.isTypeMenu()) {
			dialog.style.backgroundColor = 'transparent';
			dialog.style.paddingTop = '0px';

			main.style.position = 'absolute';
			main.style.width = '200px';
			main.style.padding = '0px';
		}

		dialog.onclick = (event) => {
			if (event.target === dialog) {
				this.close();
			}
		}

	}

	isTypeMenu() {
		return this.hasAttribute('type') && 'menu' === this.getAttribute('type');
	}

	open() {
		let dialog = document.getElementById(this.getAttribute('id') + 'Dialog');
		dialog.style.display = 'block';
	}

	close() {
		let dialog = document.getElementById(this.getAttribute('id') + 'Dialog');
		dialog.style.display = 'none';
	}

	getContent() {
		return document.getElementById(this.getAttribute('id') + 'DialogContent');
	}

	setContent(widget, title) {
		let content = document.getElementById(this.getAttribute('id') + 'DialogContent');
		content.innerHTML = '';
		content.appendChild(widget);
	}

	setContentHTML(html) {
		let content = document.getElementById(this.getAttribute('id') + 'DialogContent');
		content.innerHTML = html;
	}

	setMenuOptions(options, top, left) {
		let dialog = document.getElementById(this.getAttribute('id') + 'Dialog')

		let content = document.getElementById(this.getAttribute('id') + 'DialogContent');
		content.style.top = top || '90px';
		content.style.left = left > (dialog.offsetWidth / 2) ? left - 180 : left;
		content.innerHTML = '';
		let ul = document.createElement('ul');
		content.appendChild(ul);
		options.forEach((item, i) => {
			console.log(item);
			let li = document.createElement('li');
			li.style.padding = '10px';
			li.style.cursor = 'pointer';
			ul.appendChild(li);
			
			if(item.aonIcon) {
				let ai = document.createElement('span');
				ai.style.verticalAlign = 'middle';
				ai.innerHTML = `<aon-icon icon="${item.aonIcon}"></aon-icon>`;
				li.appendChild(ai);
			} else {
				let ic = document.createElement('i');
				ic.className = 'material-icons';
				ic.style.verticalAlign = 'middle';
				ic.innerHTML = item.icon;
				li.appendChild(ic);
			}

			let span = document.createElement('span');
			span.style.marginLeft = '5px';
			span.innerHTML = item.name;
			li.appendChild(span);
			li.addEventListener('click', () => {
				this.close();
				item.fn();
			});
		});
	}

	setTitle(title) {
		this.getElement(this.TITLE).innerHTML = title;
	}

	addCancelAction(fn) {
		let cancel = this.createElement('button');
		cancel.id = this.CANCEL;
		cancel.className = 'aonButton';
		cancel.innerHTML = MSG.AON_MSG_CANCEL;
		cancel.addEventListener('click', () => {
			fn();
			this.close();
		});
		this.getElement(this.ACTION).appendChild(cancel);
	}

	addAcceptAction(fn) {
		let accept = this.createElement('button');
		accept.id = this.ACCEPT;
		accept.className = 'aonButton';
		accept.innerHTML = MSG.AON_MSG_ACCEPT;
		accept.addEventListener('click', () => {
			fn();
			this.close();
		});
		this.getElement(this.ACTION).appendChild(accept);
	}
}

window.customElements.define('aon-dialog', AonDialog);
