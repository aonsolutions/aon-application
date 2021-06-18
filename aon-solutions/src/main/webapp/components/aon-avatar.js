import { CSS, TAG } from '../environments/environments.js';
import { AonElement } from './AonElement.js';

export class AonAvatar extends AonElement {

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

    constructor () {
		super();
	}

	connectedCallback () {
        this.build();
    }
 
    build() {
        let img = this.createElement(TAG.IMG);
        img.src = this.src && this.src != 'undefined' && this.src != 'null' 
            ? this.src  : 'assets/img/profile.png';
        img.className = CSS.AON_IMG_AVATAR;
        this.appendChild(img);
    }
}

if(!window.customElements.get('aon-avatar')){
  window.customElements.define('aon-avatar', AonAvatar);
}