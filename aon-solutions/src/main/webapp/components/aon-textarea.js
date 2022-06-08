import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG} from '../environments/environments.js';
import { openFileUrl } from '../services/fileService.js';
import { getReader } from '../services/utils.js';
import { newComponent, setAttributes} from '../services/utilsComponents.js';
import { AonElement } from './AonElement.js';
import '../css/aon-textarea.css';
import '../css/aon-css-utils.css';

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
			let textAreaDiv = this.getTextArea();
			if(textAreaDiv)
				textAreaDiv.innerHTML = newValue;
		} else if("height" === name){
			let textAreaDiv = this.getTextArea();
			if(textAreaDiv){
				this.style.minHeight = textAreaDiv.style.minHeight = newValue;
			}
		}
	}

	constructor () {
		super();
		this.FILES = [];
	}

	connectedCallback () {
		this.TEXTAREA = "textarea" + this.id;
		this.TOOLBAR = "toolbar" + this.id;
		this.left = "left"+ this.id;
		this.right = "right"+this.id;
		this.build();
	}

	build() {
		this.buildToolbar();

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

	buildToolbar(){
		if(this.NOT_TOOLBAR) return;
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
			styles:{
				height: "auto"
			}
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

	getSelectionForAdd(){
		const textArea = this.getTextArea();
		const range = this.getSelection().getRangeAt(0);
		const selectedText = range.extractContents();

		if(range && range.toString()!=""){
			let div = document.createElement(TAG.DIV); 
			div.appendChild(selectedText);
			range.insertNode(div);
	
			const baseSelection = this.getSelection().baseNode;
	
			const inside = textArea.contains(baseSelection);
			if(inside) {//  inside
				return div; 
			}
		}

		return textArea;
	}

	generateTextArea(){
		const preventDefault = (ev) =>{
			ev.preventDefault();
			ev.stopPropagation();
		}  
		const element =  newComponent({
			type: TAG.DIV,
			classes: [CSS.COPY, CSS.NO_FOCUS, CSS.MATERIAL_SCROLL, CSS.CONTENT_EDITABLE],
			id: this.TEXTAREA,
			text: this.dataset.value,
			attributes:{
				contentEditable: true,
				name: this.name,
				placeholder: this.placeholder ? this.placeholder : ""
			},
			events:{
				dragenter: preventDefault,
				dragover:  preventDefault,
				dragleave: preventDefault,
				drop:      preventDefault,
				input: (ev)=>{
					preventDefault(ev);
					this.dispatchEvent(new CustomEvent(EVENT.INPUT, {target:ev.target}))
				},
				
				paste: (ev)=>{
					// this.interceptorPaste(ev);
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

		element.element.classList.add(CSS.FOCUS_COLOR_MINUS);
		if(this.NOT_BACKGROUND) element.element.classList.remove(CSS.FOCUS_COLOR_MINUS);
		return element;
	}

	clear(){
		const area = this.getTextArea();
		area.innerHTML = "";
		area.value = "";
	}

	removeToolbar(){
		let toolbar = this.getElement(this.TOOLBAR);
		if(toolbar) toolbar.remove();
	}

	// getValueHtml(html) {
	// 	const textarea = this.getTextArea();
	// 	if(textarea) 
	// 		return textarea.innerHTML;
	// 		textarea.innerHTML = html;
	// }

	setValueHtml(html) {
		const textarea = this.getTextArea();
		if(textarea) 
			textarea.innerHTML = html;
	}

	addValueHtml(html) {
		const textarea = this.getTextArea();
		if(textarea) {
			const element = document.createElement(TAG.DIV);
			element.innerHTML = html;
			textarea.appendChild(element);
		}
	}

	getTextArea(){
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
			}).element;
			this.addToolbarLeft(icon);
		}
		//ENABLE DRAGGRABLE FILE
		if(properties.icon === MATERIAL_ICONS.ATTACH_FILE){ 
			this.draggableEnable(); 
		}
	}
	
	addToolbarLeft(element, fn){
		if(fn) element.addEventListener(EVENT.CLICK, fn);
		const el = this.getElement(this.LEFT);
		if(el) el.appendChild(element);
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
			}).element;
			this.addToolbarRight(icon, fn);
			return icon;
		}
	}

	addToolbarRight(element, fn){
		element.addEventListener(EVENT.CLICK, fn);
		const el = this.getElement(this.RIGHT);
		if(el) el.appendChild(element);
	}

	getValue() {return this.value && this.value === 'true';}

	async addFiles(files){
		let div = this.getSelectionForAdd();

		for await (const file of files) {
			const reader = await getReader(file).catch(()=>null);
			if(reader) {
				
				const fileId = Math.random().toString(36).substring(7);

				this.FILES.push({
					contentType: reader.contentType,
					content: reader.content,
					id:fileId
				});

				const url = this.convertBase64Url(reader.content, reader.contentType);
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
					element = document.createElement(TAG.A);
					element.target = "_blank";
					element.className = CSS.AON_LINK;
					element.href = url;
					element.textContent = reader.name;
				}
				element.dataset.id = fileId;
				element.setAttribute(CONSTANT.TYPE, CONSTANT.AON_FILE);
				element.addEventListener(EVENT.CLICK, ()=> openFileUrl(url));
				div.appendChild(element);
				div.appendChild(document.createElement("br"));
			}
		}
		this.dispatchEvent(new CustomEvent(EVENT.INPUT));
	}

	getToolbar(){
		return this.getElement(this.TOOLBAR);
	}

	draggableEnable(){
		const divTextArea = this.getTextArea();
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
		span.innerHTML = MSG.ATTACH_FILES_DRAGGING_DROPPING+" "+"dentro del recuadro";
		label.appendChild(span);
		
		this.appendChild(label);
		return label;
	}

	removeBackground(){
		this.getTextArea().classList.remove(CSS.FOCUS_COLOR_MINUS);
	}

	interceptorPaste(ev){
		// const clipboardData = ev.clipboardData || ev.originalEvent.clipboardData;
		// const html  = clipboardData.getData('text/html');
		// console.log(html);
		// setTimeout(()=>{
		// 	this.setValueHtml(html);
		// 	console.log(html);
		// }, 5000);


		// let items = Object.values(clipboardData.items || []).filter(item => item && item.kind);

		// const existFile = items.some(item => item.kind === 'file');

		// if(!existFile){
		// 	preventDefault(ev);
		// 	items =  items.filter(item => item.kind !== 'file');
		// 	if(items && items.length){
		// 		for (let index in items) {
		// 			let item = items[index];
		// 			console.log("HTML");
		// 			item.getAsString( (html)=>{
		// 			});
		// 		}
		// 	}
		// }
		// let newHtml = html || this.value || '';
		// let newValue = newHtml.replace(/src=\"([^\"]*)\"/g, (match, url) =>{ // eslint-disable-line
		// 	let newUrl = url.replaceAll("&amp;", "&");

		// 	// console.log(match);
		// 	console.log(newUrl);
		// 	this.getBase64FromUrl(newUrl).then(base64=>{
		// 		console.log(base64);
		// 	})
			// let codec, extension;
			// if (url.indexOf('data:image/png;base64,') == 0) {
			// 	codec = 'png';
			// 	extension = '.png';
			// } else if (url.indexOf('data:image/jpeg;base64,') == 0) {
			// 	codec = 'jpeg';
			// 	extension = '.jpg';
			// }
			// if (codec) {
			// 	let name = 'image' + images.length + extension,
			// 	base64 = url.replace('data:image/' + codec + ';base64,', ''),
			// 	buffer = new Buffer(base64, 'base64');
			// 	images.push(new mailgun_client.Attachment({
			// 	contentType: 'image/' + codec,
			// 	filename: name,
			// 	data: buffer,
			// 	knownLength: buffer.length,
			// 	}));
			// 	return match.replace(url, 'cid:' + name);
			// }
			// return match.replace(url, `${newUrl}" onerror="this.remove()" referrerpolicy="no-referrer`);
		// });
// 
		// if(newValue && newValue.trim()) {
			// newHtml = newValue;
		// }
// 
		// this.setValueHtml(newValue)
	}

	/**
	 * 
	 * @param {String} base64Str base64 file
	 * @param {String} contentType mimeType
	 * @returns {String} url
	 */
	convertBase64Url(base64Str, contentType) {
		let byteCharacters = atob(base64Str);
		let byteNumbers = new Array(byteCharacters.length);
		for (let i = 0; i < byteCharacters.length; i++) byteNumbers[i] = byteCharacters.charCodeAt(i);
		let file = new Blob([new Uint8Array(byteNumbers)], { type: `${contentType};base64` });
		return URL.createObjectURL(file);
	}

	async getBase64FromUrl(url){
		const data = await fetch(url,{
			headers: { 'Content-Type': 'image/jpeg'}
		});
		const blob = await data.blob();
		return new Promise((resolve) => {
		  const reader = new FileReader();
		  reader.readAsDataURL(blob); 
		  reader.onloadend = () => {
			const base64data = reader.result;   
			resolve(base64data);
		  }
		});
	}
}
if(!window.customElements.get('aon-textarea')){
	window.customElements.define('aon-textarea',  AonTextArea);
}

