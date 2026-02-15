import { AonElement } from './AonElement.js';
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonIcon } from './aon-icon.js';
import { AonIconButton } from './aon-icon-button.js';


export class AonNewDialogMenu extends AonElement {

	CONTENT;
	
	_options;

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

	open(el = undefined, isFixedButton = false) {
	    const dialog = this.getElement(this.id);
	    
	    this.isFixedButton = isFixedButton; 
	    this.lastEl = el;
	    
	    if (el) {
	        const rect = el.getBoundingClientRect();
	        const content = this.getElement(this.CONTENT);
	
	        if (isFixedButton) {
	            // Aseguramos que el content ya tenga tamaño (por si está hidden)
	            content.style.visibility = "hidden";
	            content.style.display = "block";
	
	            const contentHeight = content.offsetHeight;
	
	            // Volvemos a ocultarlo correctamente
	            content.style.visibility = "";
	            content.style.display = "";
	
	            // Posición: ENCIMA del botón
	            const top = rect.top - contentHeight - (this._options.length * 60); // 60px de separación
	
	            content.style.top = `${top}px`;
	            content.style.right = `30px`;
	            content.style.left = `auto`;
	
	        } else {
	            // Tu comportamiento original
	            const top = rect.top;
	            const left = rect.right;
	
	            content.style.top = top + 'px' || '90px';
	            content.style.left =
	                (left > (window.innerWidth / 2) ? left - 180 : (left + 10)) + 'px';
	            content.style.right = `auto`;
	        }
	    }
	
	    dialog.classList.remove("hidden");
	
		let newFixedButton = document.querySelector('aon-new-fixed-button .newFixedButton');
		
		if (!isFixedButton) {
		    const iconEl = this.getElement('aonMenuListAppImgTop-new');
		    iconEl.classList.add('open');
		    if(newFixedButton)
		    	 newFixedButton.classList.add('open');
	    } else
			newFixedButton.classList.add('open');
	    
	}
	
	repositionFixedMenu(el) {
	    const content = this.getElement(this.CONTENT);
	    const rect = el.getBoundingClientRect();
	
	    // Aseguramos que el content tenga tamaño real
	    content.style.visibility = "hidden";
	    content.style.display = "block";
	
	    const contentHeight = content.offsetHeight;
	
	    content.style.visibility = "";
	    content.style.display = "";
	
	    // Posición encima del botón flotante
	    const top = rect.top - contentHeight - 60;
	
	    content.style.top = `${top}px`;
	    content.style.right = `30px`;
	    content.style.left = `auto`;
	}


	close() {
		let dialog = this.getElement(this.id);
		dialog.classList.add("hidden");
		
		let iconEl = this.getElement('aonMenuListAppImgTop-new');
		if(iconEl) iconEl.classList.remove('open');
		
		let newFixedButton = document.querySelector('aon-new-fixed-button .newFixedButton');
		if(newFixedButton) newFixedButton.classList.remove('open');
	}

	setOptions(options = [], isFixedButton = false) {
		const content = this.getElement(this.CONTENT);
		content.innerHTML = "";

		this._options = options;

		options.forEach(option => {
			content.appendChild(this.createOption(option, isFixedButton));
		});
	}

	createOption(option, isFixedButton = false) {
		const wrapper = this.createElement(TAG.DIV);

		const item = this.createElement(TAG.DIV);
		item.classList.add("menu-option");

		const text = this.createElement(TAG.SPAN);
		text.textContent = option.name;
		item.appendChild(text);

		if (option.icon && !isFixedButton) {
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
				children.appendChild(this.createOption(child, isFixedButton));
			});

			item.addEventListener(EVENT.CLICK, (e) => {
				e.stopPropagation();
				children.classList.toggle("open");
				item.classList.toggle("open");
				
				if (this.isFixedButton && this.lastEl) { this.repositionFixedMenu(this.lastEl); }
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
