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
        if(this.auth){
            let divImagen = this.createDiv();
            divImagen.appendChild(this.buildImage(this.getName(this.auth)));
            this.appendChild(divImagen);
        }else{
            let img = this.createElement(TAG.IMG);
            img.id  = this.IMAGE;
            img.src = this.src && this.src != 'undefined' && this.src != 'null' 
                ? this.src  : 'assets/img/profile.png';
            img.className = CSS.AON_IMG_AVATAR;
            img.style.objectFit = "cover";
        }
        
        if(this.scale){
            img.style.scale = this.scale;
            img.style.marginLeft = this.marginLeft;
        }
        
        this.appendChild(img);
    }

    setScale(scale,marginLeft) {
        this.scale = scale;
        this.marginLeft = marginLeft;
    }

    setAuth(auth){
        this.auth = auth;
    }

    getName(auth){
        let name = auth.name.substring(0,1).toUpperCase()+auth.surname.substring(0,1).toUpperCase();
        return name;
    }

    buildImage(letters){
		let div = this.createDiv();
		div.className = "profile-letters";
		div.style.scale = "3.2";
		div.style.marginLeft = "40px";
		div.style.marginTop = "19px";
		div.style.border = "none";
		div.style.backgroundColor = "var(--aonBlue)";
		div.style.color = "white";
		div.innerHTML = letters;
	
		return div;
	}
}

if(!window.customElements.get('aon-avatar')){
  window.customElements.define('aon-avatar', AonAvatar);
}