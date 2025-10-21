import { AonElement } from './AonElement.js';
import { TAG } from '../environments/environments.js';
import { AonIcon } from './aon-icon.js';
import { EVENT } from '../environments/environments.js';

export class AonInfo extends AonElement {
	
	ICON;
	TITLE;
	TEXT;
	
	constructor () {
    	super();
	}
	
	connectedCallback () {
      	this.initialize();
    	this.build();
	}
	
  	initialize() {
		this.ICON = this.ICON || "";
		this.TEXT = this.TEXT || "";
    }

    build() {
		this.classList.add("tooltip-container");
		let icon = new AonIcon();
		icon.icon = this.ICON;
		this.appendChild(icon);
		this.buildHover();
		this.buildClick();
    }
	
	/**
     * @param {string} icon
     */
	set icon(icon) {
		this.ICON = icon;
	}
	
	/**
     * @param {string} text
     */
	set text(text) {
		this.TEXT = text;
	}
	
	/**
	 * @param {string} title
	 */
	set title(title) {
		this.TITLE = title;
	}
	
	buildClick() {
		this.addEventListener(EVENT.CLICK, (event) => {
			event.stopPropagation();
			if(this.isMobile() || this.isMobileResolution()){				
				this.info(this.TITLE, this.TEXT);
			}
		});
	}
	
	buildHover() {
		let div = this.createElement(TAG.SPAN);
		div.classList.add("tooltip");
		div.innerHTML = this.TEXT;
		this.appendChild(div);
		this.addEventListener(EVENT.POINTEROVER, (event) => {
			event.stopPropagation();
			if(!(this.isMobile() || this.isMobileResolution())){				
				div.style.opacity = 1;
				div.style.visibility = "visible";
			}
		});
		this.addEventListener(EVENT.POINTERLEAVE, (event) => {
			event.stopPropagation();
			if(!(this.isMobile() || this.isMobileResolution())){				
				div.style.opacity = 0;
				div.style.visibility = "hidden";
			}
		});
	}
	
	info(title, description) {
	    let application = document.querySelector(TAG.AON_APPLICATION);
	    application.confirmDialog(title, description, () => {});
	};
	
}

if(!window.customElements.get(TAG.AON_INFO)){
	window.customElements.define(TAG.AON_INFO, AonInfo);
}