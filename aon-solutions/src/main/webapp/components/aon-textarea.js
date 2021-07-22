import { CONSTANT, CSS, EVENT, TAG} from '../environments/environments.js';
import { openFileUrl } from '../services/fileService.js';
import { convertBase64Url, getReader, newComponent, waitEl } from '../services/utils.js';
import { AonElement } from './AonElement.js';

export class AonTextArea extends AonElement {

	TEXTAREA;
	TOOLBAR;
	LEFT;
	RIGHT;
	FILES;
	// COMPILE;

	static get observedAttributes() {
		return [CONSTANT.VALUE];
	}

	// get compile(){
	// 	return this.COMPILE;
	// }

	// set compile(fn){
	// 	this.COMPILE = () => fn(this);
	// }

	get left(){
		return this.LEFT;
	}

	set left(value){
		this.LEFT = value;
	}

	get right(){
		return this.RIGHT;
	}

	set right(value){
		this.RIGHT = value;
	}

	get placeholder(){
		return this.getAttribute("placeholder");
	}

	set placeholder(value){
		this.setAttribute("placeholder", value);
	}

	// get textarea(){
	// 	return this.TEXTAREA;
	// }

	// set textarea(value){
	// 	this.TEXTAREA = value;
	// }

	get toolbar(){
		return this.TOOLBAR;
	}

	set toolbar(value){
		this.TOOLBAR = value;
	}

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get name() {
		return this.getAttribute(CONSTANT.NAME);
	}

	set name(name) {
		this.setAttribute(CONSTANT.NAME, name);
	}

	get value() {
		const textarea = this.getElement(this.TEXTAREA);
		return textarea && textarea.innerHTML.trim().length>0  ? textarea.innerHTML.trim() : null;
	}

	set value(value) {
		this.setAttribute(CONSTANT.VALUE, value);
	}

	// get compiledValue() {
	// 	return this.COMPILE();
	// }
	
	get visible() {
		return this.getAttribute(CONSTANT.VISIBLE);
	}

	set visible(visible) {
		this.setAttribute(CONSTANT.VISIBLE, visible);
	}

	get disabled() {
		return this.getAttribute(CONSTANT.DISABLED);
	}

	set disabled(disabled) {
		this.setAttribute(CONSTANT.DISABLED, disabled);
	}
	attributeChangedCallback(name, oldValue, newValue) {
		if(CONSTANT.VALUE === name){
			let input = this.getElement(this.getAttribute(CONSTANT.ID) + 'Input');
			if(this.hasAttribute(CONSTANT.VALUE) && "true" === this.getAttribute(CONSTANT.VALUE)){
				input.setAttribute(CONSTANT.CHECKED, CONSTANT.CHECKED);
			} else 
				input.removeAttribute(CONSTANT.CHECKED);
		}
	}

	constructor () {
		super();
		this.left = "left";
		this.right = "right";
		this.FILES = [];
	}

	connectedCallback () {
		this.TEXTAREA = "textarea" + this.id;
		this.TOOLBAR = "toolbar" + this.id;
		this.build();
	}

	build() {
		const bar = newComponent({
			type: "toolbar",
			id : this.TOOLBAR,
			classes: [
				'bar',
				CSS.FLEX_ROW,
				CSS.FLEX_ALIGN_CENTER,
				CSS.FLEX_JUSTIFY_BETWEEN,
				CSS.NO_COPY
			],
		});
		bar.appendTo(this);

		const left = newComponent({
			id:this.LEFT,
			classes: [
				'left',
				CSS.FLEX_ROW,
				CSS.FLEX_JUSTIFY_START,
				CSS.FLEX_ALIGN_CENTER
			]
		});
		left.appendTo(bar.element);

		const right = newComponent({
			id:this.RIGHT, 
			classes: [
				'right',
				CSS.FLEX_ROW,
				CSS.FLEX_ALIGN_CENTER,
				CSS.FLEX_JUSTIFY_END
			]
		});
		right.appendTo(bar.element);

		const textarea = this.generateTextArea();
		textarea.appendTo(this);
	}

	getSelection() {
		let userSelection;
		if (window.getSelection) {
			userSelection = window.getSelection();
		} else if (document.selection) { // Opera
			userSelection = document.selection.createRange();
		}  
		return userSelection;
	} 

	generateTextArea(){
		return  newComponent({
			type: TAG.DIV,
			classes: [CSS.COPY, CSS.NO_FOCUS, CSS.MATERIAL_SCROLL, CSS.CONTENT_EDITABLE],
			id: this.TEXTAREA,
			text: this.dataset.value,
			attributes:{
				contentEditable: true,
				name: this.name,
				placeholder: this.placeholder ? this.placeholder : null
			},
			styles : {
				userSelect : 'text',
				height: '100%',
				padding: '10px',
				background: '#fff'
			}
		});
	}

	clear(){
		const area = this.getTextAreaDiv();
		area.innerHTML = "";
		area.value = "";
	}

	removeToolbar(){
		this.getElement(this.TOOLBAR).remove();
	}

	getTextAreaDiv(){
		return this.getElement(this.TEXTAREA);
	}

	addToolbarOptionLeft(properties,fn){
		if(!properties.id) properties.id = "noId";
		if(!properties.icon && !properties.aonIcon) return;
		if(properties.aonIcon){}
		else if(properties.icon){
			const icon = newComponent({
				text: properties.icon,
				id: properties.id,
				classes: ['icon',"material-icons",CSS.CENTER_FLEX],
				events : {click : fn}
			});
			waitEl("#" + this.TOOLBAR + " #" + this.LEFT).then(el => el.appendChild(icon.element));
		}
	}

	addToolbarOptionRight(properties,fn){
		if(!properties.id) properties.id = "noId";
		if(!properties.icon && !properties.aonIcon) return;
		if(properties.aonIcon){} 
		else if(properties.icon){
			const icon = newComponent({
				text: properties.icon,
				id: properties.id,
				classes: ['icon',"material-icons",CSS.CENTER_FLEX],
				events : {click : fn}
			});
			waitEl("#" + this.TOOLBAR + " #" + this.RIGHT).then(el => el.appendChild(icon.element));
		}
	}

	getValue() {return this.value && this.value === 'true';}

	async addFiles(files){
		const textAreaDiv = this.getTextAreaDiv();
		for await (const file of files) {
			const reader = await getReader(file).catch(e=>null);
			if(reader) {
				const fileId = Math.random().toString(36).substring(7);
				this.FILES.push({
					contentType: reader.contentType,
					content: reader.content,
					id:fileId
				})
				const url = convertBase64Url(reader.content, reader.contentType);
				let element = null;
				if(reader.contentType && reader.contentType.indexOf("image")>-1){
					element = document.createElement(TAG.IMG);
					element.src = url;
					element.className = CSS.AON_IMG_COMMENT;
				} else  {
					element = document.createElement("a");
					element.target = "_blank";
					element.className = CSS.AON_LINK;
					element.href = url;
					element.textContent = reader.name;
				}

				element.dataset.id = fileId;
				element.setAttribute(CONSTANT.TYPE, CONSTANT.AON_FILE);
				element.addEventListener(EVENT.CLICK, ()=> openFileUrl(url));
				textAreaDiv.appendChild(element);
				textAreaDiv.appendChild(document.createElement("br"));
			}
		}
	}
}
if(!window.customElements.get('aon-textarea')){
	window.customElements.define('aon-textarea',  AonTextArea);
}

