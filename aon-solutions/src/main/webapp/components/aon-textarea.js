import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG} from '../environments/environments.js';
import { openFileUrl } from '../services/fileService.js';
import { convertBase64Url, getReader, waitEl } from '../services/utils.js';
import { newComponent, setAttributes} from '../services/utilsComponents.js';
import { AonElement } from './AonElement.js';

export class AonTextArea extends AonElement {

	TEXTAREA;
	TOOLBAR;
	LEFT;
	RIGHT;
	FILES;
	// COMPILE;

	static get observedAttributes() {
		return [CONSTANT.VALUE, "height"];
	}

	// get compile(){
	// 	return this.COMPILE;
	// }

	// set compile(fn){
	// 	this.COMPILE = () => fn(this);
	// }

	get height() {
		return this.getAttribute("height");
	}

	set height(height) {
		this.setAttribute("height", height);
	}

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
			let textAreaDiv = this.getTextAreaDiv();
			if(textAreaDiv)
				textAreaDiv.innerHTML = newValue;
		} else if("height" === name){
			let textAreaDiv = this.getTextAreaDiv();
			if(textAreaDiv){
				this.style.minHeight = textAreaDiv.style.minHeight = newValue;
			}
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

  	 	//ADD INPUT FILE
		let inputFile = setAttributes(document.createElement(TAG.INPUT),{
			id:this.id+"Files",
			type:'file',
			name:'file',
			multiple:true
		});        
		inputFile.style.display = "none";
		inputFile.addEventListener(EVENT.CHANGE, () => this.addFiles(inputFile.files));
		this.appendChild(inputFile);
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
		const preventDefault = (ev) =>{
			ev.preventDefault();
			ev.stopPropagation();
		}  
		return  newComponent({
			type: TAG.DIV,
			classes: [CSS.COPY, CSS.NO_FOCUS, CSS.MATERIAL_SCROLL, CSS.CONTENT_EDITABLE, CSS.FOCUS_COLOR_MINUS],
			id: this.TEXTAREA,
			text: this.dataset.value,
			attributes:{
				contentEditable: true,
				name: this.name,
				placeholder: this.placeholder ? this.placeholder : null
			},
			events:{
				dragenter: preventDefault,
				dragover:  preventDefault,
				dragleave: preventDefault,
				drop:      preventDefault,
				input: (ev)=>{
					preventDefault(ev);
					this.dispatchEvent(new CustomEvent(EVENT.INPUT, {target:ev.target}))
				}
			},
			styles : {
				userSelect : 'text',
				height: '100%',
				padding: '10px',
				// fontSize: "1.3em"
			},
			dataset:{
				dragOver: MSG.DROP_FILE,
			}
		});
	}

	clear(){
		const area = this.getTextAreaDiv();
		area.innerHTML = "";
		area.value = "";
	}

	removeToolbar(){
		let toolbar = this.getElement(this.TOOLBAR);
		if(toolbar) toolbar.remove();
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
				attributes:{
					title: properties.name ? properties.name : "",
				},
				events : {
					click : (ev)=> properties.id === MATERIAL_ICONS.ATTACH_FILE ? this.clickFile() : fn(ev)
				}
			});
			waitEl("#" + this.TOOLBAR + " #" + this.LEFT).then(el => el.appendChild(icon.element));
		}
		//ENABLE DRAGGRABLE FILE
		if(properties.icon === MATERIAL_ICONS.ATTACH_FILE){ 
			this.draggableEnable(); 
		}
	}

	clickFile(){
		this.getElement(this.id+"Files").click();
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
				attributes:{
					title: properties.name ? properties.name : "",
				},
				events : {click : fn}
			});
			waitEl("#" + this.TOOLBAR + " #" + this.RIGHT).then(el => el.appendChild(icon.element));
			return icon.element;
		}
	}

	addToolbarRight(element, fn){
		element.addEventListener(EVENT.CLICK, fn);
		waitEl("#" + this.TOOLBAR + " #" + this.RIGHT).then(el => el.appendChild(element));
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
				} else if(reader.contentType && reader.contentType.indexOf("mp4")>-1){
					element = document.createElement("video");
					element.controls = true;
					element.style.width = "100%";
					element.style.minHeight = element.style.maxHeight = "184px";
					const source = document.createElement("source");
					source.src = url;
					source.type = reader.contentType;
					element.appendChild(source);
				} else {
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
		this.dispatchEvent(new CustomEvent(EVENT.INPUT));
	}

	getToolbar(){
		return this.getElement(this.TOOLBAR);
	}

	draggableEnable(){
		const divTextArea = this.getTextAreaDiv();
		divTextArea.classList.add("divDragOver");
		
		const highlight = ()   => {
			divTextArea.classList.add('highlight');
			divTextArea.setAttribute("placeholder", "");
		} 
		const unhighlight = () =>{
			divTextArea.classList.remove('highlight');
			divTextArea.setAttribute("placeholder", this.placeholder);
		}

		[EVENT.DRAGENTER, EVENT.DRAGOVER].forEach(eventName => divTextArea.addEventListener(eventName, highlight, false));
		[EVENT.DRAGLEAVE, EVENT.DROP].forEach(eventName => divTextArea.addEventListener(eventName, unhighlight, false));

	    divTextArea.addEventListener(EVENT.DROP, (ev) => {
			if(ev && ev.dataTransfer && ev.dataTransfer.files){
				this.addFiles(ev.dataTransfer.files);
			}
		});
	}

	addLabelTextEnd(){
		let label = document.createElement(TAG.LABEL);
		label.style.color = "grey";
		label.style.width = "100%";
		label.style.cursor = "pointer";
		label.style.borderTop = "1px dotted grey";
		label.style.fontSize = "10px";

		let span = document.createElement(TAG.SPAN);
		span.style.margin = "0px 5px";
		span.innerHTML = MSG.ATTACH_FILES_DRAGGING_DROPPING;
		label.appendChild(span);
		
		this.appendChild(label);
		return label;
	}

	removeBackground(){
		this.getTextAreaDiv().classList.remove(CSS.FOCUS_COLOR_MINUS);
	}
}
if(!window.customElements.get('aon-textarea')){
	window.customElements.define('aon-textarea',  AonTextArea);
}

