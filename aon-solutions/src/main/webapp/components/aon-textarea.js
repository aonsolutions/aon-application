import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG} from '../environments/environments.js';
import { openFileUrl } from '../services/fileService.js';
import { getReader } from '../services/utils.js';
import { newComponent, setAttributes, setStyles} from '../services/utilsComponents.js';
import { AonElement } from './AonElement.js';
import '../css/aon-textarea.css';
import '../css/aon-css-utils.css';
import { WORKFLOW_TYPES } from '../modules/messenger/MessengerEnums.js';
import { downscaleImage } from '../services/compressImg.js';

export class AonTextArea extends AonElement {

	TEXTAREA;
	TOOLBAR;
	FILES;
	RIGHT;
	LEFT;
	static get observedAttributes() {
		return [CONSTANT.VALUE, CONSTANT.DISABLED, "height"];
	}

	get height() {
		return this.getAttribute("height");
	}

	set height(height) {
		this.setAttribute("height", height);
	}

	get placeholder(){
		return this.getAttribute("placeholder");
	}

	set placeholder(value){
		this.setAttribute("placeholder", value);
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
		const textarea = this.TEXTAREA;
		return textarea && textarea.innerHTML.trim().length>0  ? textarea.innerHTML.trim() : null;
	}

	set value(value) {
		this.setAttribute(CONSTANT.VALUE, value);
	}
	
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
			this.TEXTAREA.innerHTML = newValue;
		} else if(CONSTANT.DISABLED === name){
			const disabled = newValue == "true";
			this.TEXTAREA.contentEditable = !disabled;
		} else if("height" === name){
			this.style.minHeight = this.TEXTAREA.style.minHeight = newValue;
		} 
	}

	constructor () {
		super();
		this.FILES = [];
	}

	connectedCallback () {
		this.build();
	}

	build() {
		this.buildToolbar();

		this.buildGenerateTextArea();
		
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
		this.TOOLBAR = newComponent({
			type: "toolbar",
			id : "toolbar" + this.id,
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
		}).element;
		this.appendChild(this.TOOLBAR);

		this.LEFT = newComponent({
			id:"left"+ this.id,
			classes: [
				'left',
				CSS.FLEX_ROW,
				CSS.FLEX_JUSTIFY_START,
				CSS.FLEX_ALIGN_CENTER
			]
		}).element;
		this.TOOLBAR.appendChild(this.LEFT)

		this.RIGHT = newComponent({
			id:"right"+this.id , 
			classes: [
				'right',
				CSS.FLEX_ROW,
				CSS.FLEX_ALIGN_CENTER,
				CSS.FLEX_JUSTIFY_END
			]
		}).element;
		this.TOOLBAR.appendChild(this.RIGHT)
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
		const textArea = this.TEXTAREA;
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

	buildGenerateTextArea(){
		const preventDefault = (ev) =>{
			ev.preventDefault();
			ev.stopPropagation();
		}  
		
		this.TEXTAREA =  newComponent({
			type: TAG.DIV,
			classes: [CSS.COPY, CSS.NO_FOCUS, CSS.MATERIAL_SCROLL, CSS.CONTENT_EDITABLE],
			id: "textarea" + this.id,
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
		}).element;

		this.TEXTAREA.classList.add(CSS.FOCUS_COLOR_MINUS);
		if(this.NOT_BACKGROUND) this.TEXTAREA.classList.remove(CSS.FOCUS_COLOR_MINUS);

		this.appendChild(this.TEXTAREA);
	}

	clear(){
		const element = this.TEXTAREA;
		element.innerHTML = "";
		element.value = "";
	}

	removeToolbar(){
		let toolbar = this.TOOLBAR;
		if(toolbar) toolbar.remove();
	}

	setValueHtml(html) {
		const textarea = this.TEXTAREA;
		if(textarea) {
			textarea.innerHTML = html;
		}
	}

	addValueHtml(html) {
		const textarea = this.TEXTAREA;
		if(textarea) {
			const element = document.createElement(TAG.DIV);
			element.innerHTML = html;
			textarea.appendChild(element);
		}
	}

	getTextArea(){
		return this.TEXTAREA;
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
				}
			}).element;
			this.addToolbarLeft(icon, fn);
		}
		//ENABLE DRAGGRABLE FILE
		if(properties.icon === MATERIAL_ICONS.ATTACH_FILE){ 
			this.draggableEnable(); 
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
				attributes:{
					title: properties.name ? properties.name : ""
				},
			}).element;
			this.addToolbarRight(icon, fn);
			return icon;
		}
	}
	
	addToolbarLeft(element, fn){
		if(fn) {
			element.addEventListener(EVENT.MOUSEDOWN, (ev)=>{
				ev.preventDefault();
				ev.stopPropagation();
			});
			element.addEventListener(EVENT.CLICK, (ev)=>{
				if(element.id === MATERIAL_ICONS.ATTACH_FILE){
					this.clickFile();
				} else {
					fn(ev);
				}
			});
		}
	
		if(this.LEFT) this.LEFT.appendChild(element);
	}

	addToolbarRight(element, fn){
		element.addEventListener(EVENT.MOUSEDOWN, (ev)=>{
			ev.preventDefault();
			ev.stopPropagation();
		});
		element.addEventListener(EVENT.CLICK, fn);
		if(this.RIGHT) this.RIGHT.appendChild(element);
	}

	clickFile(){
		this.getElement(this.id+"Files").click();
	}

	addColorPicker(beforeId= undefined){
		if(beforeId){
			const defaultColor = "#002469";

			let div = document.createElement(TAG.DIV);
			div.id = MATERIAL_ICONS.FORMAT_COLOR_TEXT;
			div.innerHTML =  MATERIAL_ICONS.FORMAT_COLOR_TEXT;		
			div.setAttribute("title", `Color del texto (Ctrl + Click para cambiar el color)`);
			div.classList.add("icon", "material-icons", CSS.CENTER_FLEX);
			div.style.position = "relative";

			let input = setAttributes(document.createElement(TAG.INPUT),{
				type : "color",
				value: defaultColor,
				id : this.id+ Math.random().toString(36).substring(7),
			});
			setStyles(input,{
				position: "absolute",
				visibility: "hidden",
				height:"0",
				width: "0",
				padding: "0",
				margin: "0",
				left: "0",
				right:"0",
				bottom: "0"
			});
			div.appendChild(input)

			let lastColor = "";
			div.addEventListener(EVENT.CLICK, ({ctrlKey})=>{
				if(ctrlKey){
					input.click();
				} else {
					let value  = input.value;
					let text = this.getSelection().toString();	
	
					if(text){
						lastColor = "";
					}
			
					if(lastColor && lastColor === value ){
						value = "#212529";
					} 
					// console.log("value", value);
					// div.style.color = value;
					document.execCommand("ForeColor", false, value);
	
					lastColor = value;
				}
			});

			let tapedTwice = false;
			div.addEventListener(EVENT.TOUCHSTART, (ev)=>{
				if(!tapedTwice) {
					tapedTwice = true;
					setTimeout( () => { tapedTwice = false; }, 300 );
				} else {
					ev.preventDefault();
					input.click();
				}
			});

			const changeColor = (ev)=>{
				ev.preventDefault();
				ev.stopPropagation();
				const {value} = ev.target;
				div.style.color = value;
				document.execCommand("ForeColor", false, value);
			}

			input.addEventListener(EVENT.INPUT, changeColor);
			input.addEventListener(EVENT.CHANGE, changeColor);
			

			const el = this.getElement(beforeId);
			if(el) el.parentNode.insertBefore(div, el);
		} 
    }

	getValue() {
		return this.value && this.value === CONSTANT.TRUE;
	}

	async addFiles(files, parent=undefined) {
		for await (const file of files) {
			await this.addFile(file, parent);
		}
	}

	async addFile(file, parent=undefined) {
		this.loading(true);

		try {
			let div = parent || this.getSelectionForAdd();

			let reader = await getReader(file).catch(()=>null);
			
			if(reader) {
 				// compress 500kB / file, 500kb, quality default 0.9, maxResolution 1280
				if (reader.contentType && reader.contentType.indexOf("image") >= 0) {
                    reader = await downscaleImage(reader, 1024);
                } 
				
				const fileId = Math.random().toString(36).substring(7);

				this.FILES.push({
					contentType: reader.contentType,
					content: reader.content,
					size: reader.size,
					name: reader.name,
					id:fileId
				});

				const url = this.convertBase64Url(reader.content, reader.contentType);

				if(url){
					let element = null;
					if(reader.contentType && reader.contentType.indexOf("image")>-1){
						element = document.createElement(TAG.IMG);
						element.src = url;
						element.className = CSS.AON_IMG_COMMENT;
					} else if(reader.contentType && reader.contentType.indexOf("mp4")>-1){
						element = setStyles(document.createElement("video"),{
							width: "100%",
							minHeight: "184px",
							maxHeight: "184px",
						});
						element.controls = true;
				
						const source = document.createElement("source");
						source.src = url;
						source.type = reader.contentType;
						element.appendChild(source);
					} else {
						element = document.createElement(TAG.A);
						element.target = "_system";
						element.className = CSS.AON_LINK;
						element.href = url;
						element.textContent = reader.name;
					}
					element.dataset.id = fileId;
					element.setAttribute(CONSTANT.TYPE, CONSTANT.AON_FILE);
					element.addEventListener(EVENT.CLICK, ()=> openFileUrl(url));
					div.appendChild(element);
					div.appendChild(document.createElement(TAG.BR));
	
					this.dispatchEvent(new CustomEvent(EVENT.INPUT));
				}
			}
		} catch (error) {
			console.log(error);
		}

		this.loading(false);
	}

	getFiles(){
        this.checkFiles();
        return this.FILES;
    }

	checkFiles() {
		let filesIds = [...this.TEXTAREA.querySelectorAll(`[${CONSTANT.TYPE}=${WORKFLOW_TYPES.AON_FILE}]`)].map(el => el.dataset.id);
		this.FILES = this.FILES.filter(f => (filesIds || []).includes(f.id));
    }

	getToolbar(){
		return this.TOOLBAR;
	}

	draggableEnable(){
		const textarea = this.TEXTAREA;
		textarea.classList.add("divDragOver");
		
		const highlight = ()   => {
			textarea.classList.add('highlight');
			textarea.setAttribute("placeholder", "");
		} 
		
		const unhighlight = () =>{
			textarea.classList.remove('highlight');
			textarea.setAttribute("placeholder", this.placeholder);
		}

		[EVENT.DRAGENTER, EVENT.DRAGOVER].forEach(eventName => textarea.addEventListener(eventName, highlight, false));
		[EVENT.DRAGLEAVE, EVENT.DROP].forEach(eventName => textarea.addEventListener(eventName, unhighlight, false));

	    textarea.addEventListener(EVENT.DROP, (ev) => {
			if(ev && ev.dataTransfer && ev.dataTransfer.files){
				this.addFiles(ev.dataTransfer.files);
			}
		});
	}

	addLabelTextEnd(){
		let label = setStyles(document.createElement(TAG.LABEL),{
			color: "grey",
			width: "100%",
			cursor: "pointer",
			borderTop: "1px dotted grey",
			fontSize: "10px"
		});

		let span = document.createElement(TAG.SPAN);
		span.style.margin = "0px 5px";
		span.innerHTML = MSG.ATTACH_FILES_DRAGGING_DROPPING+" "+"dentro del recuadro";
		label.appendChild(span);
		
		this.appendChild(label);
		return label;
	}

	removeBackground(){
		this.TEXTAREA.classList.remove(CSS.FOCUS_COLOR_MINUS);
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

	async checkFileBase64() {
		let textArea = this.TEXTAREA;
		if(textArea){
			const elements = textArea.querySelectorAll(`img[src*=";base64"]`);
			if(elements.length){
				for await (const el of elements) {
					let parent = document.createElement(TAG.DIV);
					let blob = this.getBlobBySrc(el.src);
					if(blob){
						await this.addFile(blob, parent);
					}
					if(el.parentNode){
						el.parentNode.replaceChild(parent, el);
					}
				}
			}
		}
	}	
	/**
	 * 
	 * @param {String} base64Str base64 file
	 * @param {String} contentType mimeType
	 * @returns {String} url
	 */
	convertBase64Url(base64Str, contentType) {
		try {
			let blob = this.getBlob(base64Str, contentType);
			return URL.createObjectURL(blob);
		} catch (error) {
			console.error("error getContentFileBase64", error);
		}
		return null;
	}

	/**
	 * 
	 * @param {String} src 
	 * @returns 
	 */
	getBlobBySrc(src){
		try {
			// base64 encoded data doesn't contain commas    
			const base64ContentArray = src.split(",")     
			
			// base64 content cannot contain whitespaces but nevertheless skip if there are!
			const contentType = base64ContentArray[0].match(/[^:\s*]\w+\/[\w-+\d.]+(?=[;| ])/)[0];
			
			// base64 encoded data - pure
			const base64Str = base64ContentArray[1];

			return this.getBlob(base64Str, contentType);
		} catch (error) {
			console.error("error getContentFileBase64", error);
		}
		return null;
	}

	getBlob(base64Str, contentType=undefined) {
		let byteCharacters = atob(base64Str);
		let byteNumbers = new Array(byteCharacters.length);

		for (let i = 0; i < byteCharacters.length; i++) {
			byteNumbers[i] = byteCharacters.charCodeAt(i);
		}

		return  new Blob([new Uint8Array(byteNumbers)], { type: `${contentType}` });
	}

	loading(b){
		let id = this.id+"Loading";
		let div = this.getElement(id);
		if (b && !div) {
		  div = setStyles(this.createElement(TAG.DIV),{
			textAlign: "center",
			position: "absolute",
			left: "0",
			right: "0",
			top: "0",
			bottom: "0",
			margin: "auto",
			background: "grey",
			opacity: "0.1"
		  })
		  div.id = id;
		  this.appendChild(div);

		  let icon = setStyles(this.createElement(TAG.I),{
			position: "absolute",
			top: "0",
			bottom: "0",
			margin: "auto"
		  });
		  icon.classList.add(CSS.AON_LOADER);
		  div.appendChild(icon);
	
		} else if (!b && div) {
		  div.remove();
		}
	}
}
if(!window.customElements.get('aon-textarea')){
	window.customElements.define('aon-textarea',  AonTextArea);
}

