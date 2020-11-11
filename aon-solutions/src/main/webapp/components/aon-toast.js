import {AonElement} from './AonElement.js';

export class AonToast extends AonElement {


	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<div id="aonToast" class="aonToast"></div>
		`;
	}

	start(options) {
		let toast = this.getElement('aonToast');
		let {message, delay, type} = options;
		let color = '#333';
		
		if(!delay) delay = 3000;
        if(type === 'error') color = '#f44336';
        else if(type === 'success') color = '#4caf50';
		else if(type === 'primary') color = '#2196f3';
		
        toast.className = "show";
        toast.innerHTML = message; 
		toast.style.background = color;
		
		setTimeout(()=> 
			toast.className = toast.className.replace("show", "")
		, delay);
	}

}

window.customElements.define('aon-toast',  AonToast);
