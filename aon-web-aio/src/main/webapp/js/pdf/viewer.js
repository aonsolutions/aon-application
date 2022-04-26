import {
	CONSTANT,
	TAG
} from './environments.js';
import {
	AonIcon
} from './aon-icon.js';

import {
	AonElement
} from './AonElement.js';

export class AonViewer extends AonElement {

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get type() {
		return this.getAttribute(CONSTANT.TYPE);
	}

	set type(type) {
		this.setAttribute(CONSTANT.TYPE, type);
	}

	get file() {
		return this.getAttribute(CONSTANT.FILE);
	}

	set file(file) {
		this.setAttribute(CONSTANT.FILE, file);
	}

	get viewer(){
		return this.getAttribute("viewer");
	}

	set viewer(url){
		this.setAttribute("viewer", url);
	}

	get toolbar() {
		return this.getAttribute("toolbar");
	}

	set toolbar(toolbar) {
		this.setAttribute("toolbar", toolbar);
	}

	get base64(){
		return this.getAttribute("base64");
	}

	set base64(base64){
		this.setAttribute("base64", base64);
	}

	constructor() {
		super();
		this.initialize();
	}

	b64EncodeUnicode(str) {
        // first we use encodeURIComponent to get percent-encoded UTF-8,
        // then we convert the percent encodings into raw bytes which
        // can be fed into btoa.
        return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
            function toSolidBytes(match, p1) {
                return String.fromCharCode('0x' + p1);
        }));
    }
    


	connectedCallback() {
		
		const iframe = document.createElement("iframe");
		iframe.style = `width: 100%;height: 100%;border: none;`
		
		let url = this.viewer;
		const params = [];


		if(this.file){ 
			if(this.base64){
				params.push("file=" + this.file);
			} else {
				params.push("file=" + this.b64EncodeUnicode(this.file));
			}
			
			params.push("encoded=true");
		}

		if(this.toolbar){
			params.push("newtoolbar=true");
		}

		url += "?" + params.join("&");
		iframe.src = url;	
		this.appendChild(iframe);

	}

	initialize() {

	}

	/**
	 * Prints an image 
	 */
	printImage() {
		let img = this.createElement(TAG.IMG);
		img.style.width = '100%';
		img.src = this.file;
		img.onerror = () => {
			img.remove();
			this.notSupport(this.type);
		}
		this.appendChild(img);
	}

	/**
	 * Show a message when the file is not supported
	 * @param {*} type 
	 * @param {*} reason 
	 */
	notSupport(type, reason) {
		let div = this.createElement(TAG.DIV);
		div.style.backgroundColor = 'rgba(128, 128, 128, 0.1)';
		div.style.height = '100%';
		div.style.display = 'flex';
		this.appendChild(div);

		let div2 = this.createElement(TAG.DIV);
		div2.style.margin = 'auto';
		div2.appendChild(this.getTypeIcon(type));
		let div3 = this.createElement(TAG.DIV);
		div3.style.textAlign = "center";
		div3.innerHTML = reason || 'Vista previa no disponible';
		div2.appendChild(div3);
		div.appendChild(div2);
	}

	getTypeIcon(type) {
		let ai = new AonIcon();
		ai.size = "160";
		if (type.includes('pdf')) {
			ai.icon = "aon_pdf";
		} else if (type.includes('powerpoint') || type.includes('presentation')) {
			ai.icon = "aon_powerpoint";
			ai.color = "orange";
		} else if (type.includes('excel') || type.includes('spreadsheet')) {
			ai.icon = "aon_excel";
			ai.color = "green";
		} else if (type.includes('word') || type.includes('text')) {
			ai.icon = "aon_word";
			ai.color = "cornflowerblue";
		} else {
			ai.icon = "aon_file";
		}
		return ai;
	}


}

if (!window.customElements.get(TAG.AON_VIEWER)) {
	window.customElements.define(TAG.AON_VIEWER, AonViewer);
}