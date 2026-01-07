import { AonElement } from './AonElement.js';
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonIcon } from './aon-icon.js';
import { AonIconButton } from './aon-icon-button.js';


export class AonNewDialogMenu extends AonElement {

	CONTENT;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
		this.build();
	}

	initialize() {
		this.id = this.id || "newDialogMenu";
		this.CONTENT = this.id + 'Content';
	}

	build() {
		this.classList.add('newMenuOverlay');
		this.classList.add('hidden');

		let content = this.createElement(TAG.DIV);
		content.id = this.CONTENT;
		content.classList.add('newMenuContent');

		this.appendChild(content);

		this.addEventListener(EVENT.CLICK, (e) => {
			e.stopPropagation();
			if (e.target !== content) this.close();
		});
	}

	open(el = undefined) {
		let dialog = this.getElement(this.id);
		
		if(el){
			let top = el.getBoundingClientRect().top;
			let left = el.getBoundingClientRect().right;
			
			let content = this.getElement(this.CONTENT);
			content.style.top = top + 'px' || '90px';
			content.style.left = (left > (window.innerWidth/2) ? left - 180 : left)+'px' ;
		}
		
		dialog.classList.remove("hidden");
	}

	close() {
		let dialog = this.getElement(this.id);
		dialog.classList.add("hidden");
	}

	setOptions(options = []) {
		const content = this.getElement(this.CONTENT);
		content.innerHTML = "";

		options.forEach(option => {
			content.appendChild(this.createOption(option));
		});
	}

	createOption(option) {
		const wrapper = this.createElement(TAG.DIV);

		const item = this.createElement(TAG.DIV);
		item.classList.add("menu-option");

		const text = this.createElement(TAG.SPAN);
		text.textContent = option.name;
		item.appendChild(text);

		if (option.icon) {
			let ic = document.createElement('i');
			ic.className = 'material-icons';
			ic.style.verticalAlign = 'middle';
			ic.style.fontSize = `24px`;
			ic.innerHTML = option.icon;
			ic.setAttribute('icon', option.icon);
			item.appendChild(ic);
		}

		wrapper.appendChild(item);

		// Tiene subopciones
		if (option.options && option.options.length) {
			const children = this.createElement(TAG.DIV);
			children.classList.add("menu-children");

			option.options.forEach(child => {
				children.appendChild(this.createOption(child));
			});

			item.addEventListener(EVENT.CLICK, (e) => {
				e.stopPropagation();
				children.classList.toggle("open");
				item.classList.toggle("open");
			});

			wrapper.appendChild(children);

		} else if (option.fn) {
			// Opción final
			item.addEventListener(EVENT.CLICK, (e) => {
				e.stopPropagation();
				option.fn();
				this.close();
			});
		}

		return wrapper;
	}

}

if (!window.customElements.get('aon-new-dialog-menu')) {
	window.customElements.define('aon-new-dialog-menu', AonNewDialogMenu);
}
