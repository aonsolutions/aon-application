import { CONSTANT, CSS, TAG } from '../environments/environments.js';
import { AonElement } from './AonElement.js';

export class AonAvatar extends AonElement {
    IMAGE;

    static get observedAttributes() {
        return ["src"];
    }
    
    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    get src() {
        return this.getAttribute('src');
    }

    set src(src) {
        this.setAttribute('src', src);
    }

    attributeChangedCallback(name, oldValue, newValue) {
		if("src" === name) {
			let img = this.getElement(this.IMAGE);
			if(img) img.src = newValue;
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
        this.id = this.id || Math.random().toString(36).substring(7);
        this.IMAGE = this.id+"Img";
    }
 
    build() {
        let img = this.createElement(TAG.IMG);
        img.id  = this.IMAGE;
        img.src = this.src && this.src != 'undefined' && this.src != 'null' 
            ? this.src  : 'assets/img/profile.png';
        img.className = CSS.AON_IMG_AVATAR;
        img.style.objectFit = "cover";
        this.appendChild(img);
    }
}

if(!window.customElements.get('aon-avatar')){
  window.customElements.define('aon-avatar', AonAvatar);
}