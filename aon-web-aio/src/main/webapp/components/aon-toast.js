import { CONSTANT, TAG } from '../environments/environments.js';
import { newComponent, setStyles } from '../services/utilsComponents.js';
import { AonElement } from './AonElement.js';

export class AonToast extends AonElement {

	DIV;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor() {
		super();
		this.id = this.id || 'aonToast';
		this.DIV = this.id + 'Div';
	}

	connectedCallback() {
		this.setStyleComponent();
		this.paintView();
	}

	setStyleComponent(){
		setStyles(this, {
			position: "absolute",
			bottom: "10%",
			minWidth: "250px",
			maxWidth: "300px",
			marginLeft: "auto",
			marginRight: "auto",
			left: 0,
			right: 0,
			textAlign: "center",
			zIndex: 99, 
			height: 0,
			overflow: "hidden",
			opacity: 0,
			transition: "opacity 1s ease-out"
		});
	}

	paintView(){
		newComponent({
			type:TAG.DIV,
			id: this.DIV,
			styles:{
				backgroundColor: "#333",
				color:" #fff",
				borderRadius: ".2rem",
				padding: "16px",
				fontSize: "17px",
				wordWrap: "break-word",
			}
		}).appendTo(this);
	}

	start(options) {
		let toast = this.getElement(this.DIV);
		let { message, delay, type} = options;
		let color = '#333';

		if (!delay) delay = 3000;
		if (type === CONSTANT.ERROR) color = '#f44336';
		else if (type === CONSTANT.SUCCESS) color = '#4CAF6E';
		else if (type === CONSTANT.PRIMARY) color = '#2196f3';
		toast.innerHTML = message;
		toast.style.background = color;
		this.displayToast(true);
		setTimeout(() => this.displayToast(false), delay);
	}

	displayToast(boolean = true){
		if(boolean){
			setStyles(this, {
				opacity: 1,
				height: "auto"
			});
		} else {
			setStyles(this, {
				transition: "opacity 1s ease-out",
				opacity: 0,
				height: 0,
				overflow: "hidden",
			});
		}
	}
}
if(!window.customElements.get(TAG.AON_TOAST)){
	window.customElements.define(TAG.AON_TOAST, AonToast);
}
