import { AonElement } from './AonElement.js';
import { CONSTANT, CSS, EVENT, MSG, TAG } from '../environments/environments.js';
import './aon-icon.js';



export class AonDialog extends AonElement {

	DIALOG;
	MAIN;
	TITLE;
	CONTENT;
	ACTION;
	CANCEL;
	ACCEPT;

	static get observedAttributes() {
		return ['width', 'autoclose'];
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

	get type() {
		return this.getAttribute(CONSTANT.TYPE);
	}

	set type(type) {
		this.setAttribute(CONSTANT.TYPE, type);
	}

	get width() {
		return this.getAttribute('width');
	}

	set width(width) {
		this.setAttribute('width', width);
	}

	get autoclose() {
		return this.getAttribute('autoclose') == "true";
	}

	set autoclose(autoclose) {
		this.setAttribute('autoclose', autoclose);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if ('width' === name) {
			let main = this.getElement(this.MAIN);
			if (main) main.style.width = newValue;
		} 
	}

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
		if(this.isTypeBlank()) this.buildBlank();
		else if(this.isTypeMenu()) this.buildMenu();
		else this.build();
	}

	initialize() {
		this.DIALOG = this.id + 'Dialog';
		this.MAIN = this.DIALOG + 'Main';
		this.TITLE = this.DIALOG + CONSTANT.TITLE;
		this.CONTENT = this.DIALOG + 'Content';
		this.ACTION = this.DIALOG + 'Action';
		this.CANCEL = this.ACTION + 'Cancel';
		this.ACCEPT = this.ACTION + 'Accept';
		this.autoclose = this.autoclose || true;
	}

	clear() {
		const title = this.getElement(this.TITLE);
		const content = this.getElement(this.CONTENT);
		const action = this.getElement(this.ACTION)
		if(title) title.innerHTML = '';
		if(content) content.innerHTML = '';
		if(action) action.innerHTML = '';
	}

	buildBlank() {
		this.innerHTML = /*html*/`
		<div id="${this.DIALOG}" class="aonDialog">
			<div id="${this.CONTENT}" class="aonDialogContent">
				
			</div>
		</div>
		`;
		let dialog = this.getElement(this.DIALOG);
		dialog.style.backgroundColor = 'transparent';
		dialog.style.paddingTop = '0px';

		let content = this.getElement(this.CONTENT);
		content.style.position = 'absolute';
		content.style.width = '200px';
		content.style.padding = '0px';

		this.onClick(dialog);
	}

	buildMenu() {
		this.innerHTML = /*html*/`
		<div id="${this.DIALOG}" class="aonDialog">
			<div id="${this.CONTENT}" class="aonDialogContent">
			</div>
		</div>
		`;
		let dialog = this.getElement(this.DIALOG);
		dialog.style.backgroundColor = 'transparent';
		dialog.style.paddingTop = '0px';

		let content = this.getElement(this.CONTENT);
		content.style.position = 'absolute';
		content.style.width = '200px';
		content.style.padding = '0px';

		this.onClick(dialog);
	}
 
	build() {
		this.innerHTML = /*html*/`
		<div id="${this.DIALOG}" class="aonDialog">
			<div id="${this.MAIN}" class="aonDialogContent">
				<label class="btn-close" id="${this.DIALOG}Click" title="${MSG.CLOSE}">×</label>
				<h2 id="${this.TITLE}"></h2>
				<div id="${this.CONTENT}"></div>
				<div id="${this.ACTION}"></div>
			</div>

		</div>
		`;

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

		this.onClick(dialog);

		this.getElement(`${this.DIALOG}Click`).addEventListener('click', (ev)=>{
			this.close()
		})

	}


	onClick(dialog){
		dialog.onclick = ({target}) => {
			if (target === dialog && this.autoclose) {
				this.close();
			}
		}
	}

	isTypeMenu() {
		return this.hasAttribute(CONSTANT.TYPE) && 'menu' === this.getAttribute(CONSTANT.TYPE);
	}

	isTypeBlank() {
		return this.hasAttribute(CONSTANT.TYPE) && 'blank' === this.getAttribute(CONSTANT.TYPE);
	}

	open() {
		let dialog = document.getElementById(this.getAttribute(CONSTANT.ID) + 'Dialog');
		dialog.style.display = 'block';
	}

	close() {
		this.autoclose = true;
		let dialog = document.getElementById(this.getAttribute(CONSTANT.ID) + 'Dialog');
		if(dialog) dialog.style.display = 'none';
		this.dispatchEvent(new CustomEvent(EVENT.CLOSE));
	}

	getContent() {
		return document.getElementById(this.getAttribute(CONSTANT.ID) + 'DialogContent');
	}

	setContent(widget, top, left, width) {
		let content = document.getElementById(this.getAttribute(CONSTANT.ID) + 'DialogContent');
		content.innerHTML = '';
		content.appendChild(widget);
		if(top && left) {
			let dialog = this.getElement(this.DIALOG);
			content.style.top = top + 'px' || '90px';
			content.style.left = (left > (dialog.offsetWidth/2) ? left - 180 : left)+'px' ;
		}

		if(width) {
			content.style.width = width;
		}
	}

	setContentHTML(html) {
		let content = document.getElementById(this.getAttribute(CONSTANT.ID) + 'DialogContent');
		content.innerHTML = html;
	}

	setMenuOptions(options, top, left) {
		let dialog = document.getElementById(this.getAttribute(CONSTANT.ID) + 'Dialog')

		let content = document.getElementById(this.getAttribute(CONSTANT.ID) + 'DialogContent');
		content.style.top = top || '90px';
		content.style.left = left > (dialog.offsetWidth / 2) ? left - 180 : left;
		content.innerHTML = '';
		let ul = document.createElement(TAG.UL);
		ul.className = CSS.AON_UL;
		content.appendChild(ul);
		options.forEach((item, i) => {
			let li = document.createElement('li');
			li.style.padding = '10px';
			li.style.cursor = 'pointer';
			ul.appendChild(li);

			if(item.aonIcon) {
				let ai = document.createElement('span');
				ai.style.verticalAlign = 'middle';
				ai.innerHTML = `<aon-icon icon="${item.aonIcon}"></aon-icon>`;
				li.appendChild(ai);
			} else if(item.icon) {
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
		if(title) this.getElement(this.TITLE).innerHTML = title;
	}

	addCancelAction(fn, close=true) {
		let cancel = this.createElement('button');
		cancel.id = this.CANCEL;
		cancel.className = 'aonButton';
		cancel.style.backgroundColor="grey";
		cancel.style.marginRight = "10px";
		cancel.innerHTML = MSG.CANCEL;
		cancel.addEventListener('click', (ev) => {
			fn(ev);
			if(close){
				this.close();
			}
		});
		this.getElement(this.ACTION).appendChild(cancel);
		return cancel;
	}

	addAcceptAction(fn) {
		let accept = this.buttonAccept();
		accept.addEventListener('click', (ev) => {
			fn(ev);
			this.close();
		});
	}
	
	buttonAccept(title=undefined){
		let btn = this.getElement(this.ACCEPT) || this.createElement('button');
		btn.id = this.ACCEPT;
		btn.className = 'aonButton';
		btn.innerHTML = title || MSG.ACCEPT;
		btn.title = title || MSG.ACCEPT;
		btn.style.marginLeft= "auto";
		let divAction = this.getElement(this.ACTION);
		divAction.style.display = "flex";
		divAction.style.justifyContent = "space-between";
		divAction.appendChild(btn);
		return btn;
	}

	getButtonAccept(){
		return this.getElement(this.ACCEPT);
	}
	
	getMain(){
		return this.getElement(this.MAIN);
	}

	addSendAction(fn, title) {
		let button = this.buttonAccept(title);
		button.classList.add('buttonload')
		button.addEventListener('click', (ev) => {
			ev.stopPropagation();
			ev.preventDefault();
			fn(ev);
		});
		return button;
	}

	loadingButton(loading){
		let accept = this.getElement(this.ACCEPT);
		// if(loading) accept.classList.add("button--loading"); 
		// else accept.classList.remove("button--loading");
		if(accept){
			let id = "iconDialogSend";
			let icon = this.createElement('i');
			icon.id = id;
			icon.classList.add("fa fa-refresh fa-spin");
			accept.appendChild(icon);
		}
	}
}
if(!window.customElements.get('aon-dialog')){
	window.customElements.define('aon-dialog', AonDialog);
}
