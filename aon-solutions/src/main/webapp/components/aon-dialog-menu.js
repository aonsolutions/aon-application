import {AonElement} from './AonElement.js';
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonIcon } from './aon-icon.js';


export class AonDialogMenu extends AonElement {

	DIALOG;
	CONTENT;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		let divOne = this.createElement(TAG.DIV);
		divOne.className = `aonDialog`;
		divOne.id = this.DIALOG;
		this.appendChild(divOne);

		let divTwo = this.createElement(TAG.DIV);
		divTwo.className = `aonDialogMenuContent`;
		divTwo.id = this.CONTENT;
		divOne.appendChild(divTwo);

		this.build();
  	}

	initialize() {
		this.DIALOG = this.id + 'DialogMenu';
		this.CONTENT = this.DIALOG + 'Content';
	}
  

	build() {
		let dialog = this.getElement(this.DIALOG);
		let content = this.getElement(this.CONTENT);

		dialog.style.backgroundColor = 'transparent';
		dialog.style.paddingTop = '0px';

		content.style.position = 'absolute';
	  	content.style.width = '200px';
		content.style.padding = '0px';

		dialog.onclick = (event) => {
			if (event.target === dialog) this.close();
		}

		dialog.oncontextmenu = (event) => {
			event.preventDefault();
			if (event.target === dialog) this.close();
		}
	}

	open(){
    	let dialog = this.getElement(this.DIALOG);
		dialog.style.display = 'block';
	}

	openPosition({left,top}){
		let dialog = this.getElement(this.DIALOG);
		let content = this.getElement(this.CONTENT);
  		content.style.top = top + 'px' || '90px';
		content.style.left = (left > (dialog.offsetWidth/2) ? left - 180 : left)+'px';
		dialog.style.display = 'block';
	}

	clear() {
		this.getElement(this.CONTENT).innerHTML = '';
	}

	close() {
		let dialog = this.getElement(this.DIALOG);
		dialog.style.display = 'none';
	}

	setContentHTML(html) {
		let content = this.getElement(this.CONTENT);
		content.innerHTML = html;
	}


	setContent(element) {
		let content = this.getElement(this.CONTENT);
		content.appendChild(element);
	}

	getContent(){
		return  this.getElement(this.CONTENT);
	}

	setContentTitle(title){
		let content = this.getElement(this.CONTENT);
		let p = this.createElement('p');
		p.innerHTML = title;
		p.style.fontWeight = "600";
		p.style.margin = "auto";
		p.style.marginTop = "3px";
		p.style.textAlign = "center";

		if(content.children.length > 0) {
			content.insertBefore(p, content.firstElementChild);
		} else content.appendChild(p);
	}

	setMenuOptions(options, top, left) {
		let dialog = this.getElement(this.DIALOG);
		let content = this.getElement(this.CONTENT);

  		content.style.top = top + 'px' || '90px';
		content.style.left = (left > (dialog.offsetWidth/2) ? left - 180 : left)+'px' ;

		content.innerHTML = '';
		let ul = document.createElement(TAG.UL);
		ul.className = CSS.AON_UL;
		content.appendChild(ul);
		options.forEach((item, i) => {
			let li = document.createElement('li');
			if(item.id) li.id = item.id;
			li.className = 'aonAppLi';
			li.style.padding = '10px';
			li.style.cursor = 'pointer';
			ul.appendChild(li);

			if(item.options) {
				let d = new AonDialogMenu();
				d.id = 'newDialog';
				this.getElement('rootPanel').appendChild(d);
				li.addEventListener(EVENT.MOUSEOVER, () => {
					const rect = li.getBoundingClientRect();
					d.setMenuOptions(item.options, rect.top, rect.left - 12);
					d.open();
				});

				li.addEventListener(EVENT.MOUSELEAVE, (e) => {
					let isClickInside = li.contains(e.target) || li === e.target || d.contains(e.target) || d === e.target;
				    if (!isClickInside) d.close();
				});

			}
			if(item.image) {
				let img = document.createElement('img');
				img.src = item.image;
				li.appendChild(img);
			} else if(item.aonIcon) {
				let ai = document.createElement(TAG.SPAN);
				ai.style.verticalAlign = 'middle';
				let aonIcon = new AonIcon();
				aonIcon.icon = item.aonIcon;
				aonIcon.size = 15;
				ai.appendChild(aonIcon);
				li.appendChild(ai);
			} else if(item.icon){
				let ic = document.createElement('i');
				ic.className = item.icon_class || 'material-icons';
				ic.style.verticalAlign = 'middle';
				ic.style.fontSize = '16px';
				ic.innerHTML = item.icon;
				li.appendChild(ic);
			}

			let span = document.createElement(TAG.SPAN);
			span.style.marginLeft = '5px';
			span.style.fontSize = '13px';
			span.innerHTML = item.name;
			span.title     = item.name;
			li.appendChild(span);
			li.addEventListener(EVENT.CLICK, (ev) => {
				this.close();
				item.fn(ev);
			});
		});
	}

	setContent(element, top, left) {
		let dialog = this.getElement(this.DIALOG);
		let content = this.getElement(this.CONTENT);
		content.innerHTML = "";

  		content.style.top = top + 'px' || '90px';
		content.style.left = (left > (dialog.offsetWidth/2) ? left - 180 : left)+'px' ;

		if(element){
			content.appendChild(element);
		}
	}
}
if(!window.customElements.get('aon-dialog-menu')){
	window.customElements.define('aon-dialog-menu', AonDialogMenu);
}
