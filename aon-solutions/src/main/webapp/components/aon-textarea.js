import { CONSTANT, CSS, TAG} from '../environments/environments.js';
import { newComponent, waitEl } from '../services/utils.js';
import { AonElement } from './AonElement.js';

export class AonTextArea extends AonElement {

	TEXTAREA;
	TOOLBAR;
	LEFT;
	RIGHT;
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
		return textarea ? textarea.innerHTML.trim() : null;
		// return this.getAttribute(CONSTANT.VALUE);
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

	clear(){
		const area = this.getDivTextArea();
		area.innerHTML = "";
		area.value = "";
	}

	getDivTextArea(){
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

	attributeChangedCallback(name, oldValue, newValue) {
		if(CONSTANT.VALUE === name){
			let input = this.getElement(this.getAttribute(CONSTANT.ID) + 'Input');
			if(this.hasAttribute(CONSTANT.VALUE) && "true" === this.getAttribute(CONSTANT.VALUE)){
				input.setAttribute(CONSTANT.CHECKED, CONSTANT.CHECKED);
			} else input.removeAttribute(CONSTANT.CHECKED);
		}
	}

	constructor () {
		super();
		this.left = "left";
		this.right = "right";
	}

	connectedCallback () {
		this.TEXTAREA = "textarea" + this.id;
		this.toolbar = "toolbar" + this.id;
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
	  
		const left = newComponent({
			id:this.LEFT,
			classes: [
				'left',
				CSS.FLEX_ROW,
				CSS.FLEX_JUSTIFY_START,
				CSS.FLEX_ALIGN_CENTER
			]
		});

		const right = newComponent({
			id:this.RIGHT, 
			classes: [
				'right',
				CSS.FLEX_ROW,
				CSS.FLEX_ALIGN_CENTER,
				CSS.FLEX_JUSTIFY_END
			]
		});

		// const eye = newComponent({
		// 	text: MATERIAL_ICONS.VISIBILITY,
		// 	id: "preview",
		// 	classes: 
		// 	[
		// 		'icon',
		// 		"material-icons",
		// 		CSS.CENTER_FLEX
		// 	],
		// 	dataset :{
		// 		selected : false
		// 	}, 
		// });

		const textarea = this.generateTextArea();
		// setEvents(eye.element,
		// 	{click : 
		// 		() => {
		// 			const backup = this.generateTextArea().element;
		// 			if(eye.element.dataset.selected == "false"){
		// 				eye.element.innerHTML = MATERIAL_ICONS.VISIBILITY_OFF;
		// 				eye.element.dataset.selected = true;
		// 				this.dataset.value = backup.value;
						
		// 				const preview = newComponent({
		// 					type : 'pre',
		// 					classes : [CSS.NO_COPY],
		// 					text :  this.COMPILE(),
		// 					styles : {
		// 						height : '100%',
		// 						margin : 0
		// 					}
		// 				});

		// 				this.removeChild(backup);
		// 				this.appendChild(preview.element);
		// 			} 
		// 			else{
		// 				eye.element.innerHTML = MATERIAL_ICONS.VISIBILITY;
		// 				eye.element.dataset.selected = false;

		// 				const preview = this.querySelector("pre");
		// 				this.removeChild(preview);
		// 				this.appendChild(backup);
		// 			} 	
		// 		}
		// 	}
		// );
		// eye.appendTo(left.element);

		left.appendTo(bar.element);
		right.appendTo(bar.element);

		bar.appendTo(this);
		textarea.appendTo(this);

	}

	getSelection() {
		let userSelection;
		if (window.getSelection) {
			userSelection = window.getSelection();
		}
		else if (document.selection) { // Opera
			userSelection = document.selection.createRange();
		}  
		return userSelection;
		
	  } 

	generateTextArea(){
		return newComponent({
			type: TAG.DIV,
			classes: [CSS.COPY, CSS.NO_FOCUS, CSS.MATERIAL_SCROLL],
			id: this.TEXTAREA,
			text: this.dataset.value,
			attributes:{
				contentEditable: true,
				draggable: true
			},
			styles : {
				userSelect : 'text',
				height: '100%',
				padding: "10px"
			}
		});
		// let area = newComponent({
		// 	type: "textarea",
		// 	classes : [CSS.COPY, CSS.NO_FOCUS, CSS.MATERIAL_SCROLL],
		// 	id: this.TEXTAREA,
		// 	text : this.dataset.value,
		// 	styles : {
		// 		userSelect : 'text',
		// 		height: '100%'
		// 	}
		// });



		// area.element.addEventListener('keydown', function(e) {
		// 	if (e.key == 'Tab') {
		// 	  e.preventDefault();
		// 	  const start = this.selectionStart;
		// 	  const end = this.selectionEnd;
		  
		// 	  // set textarea value to: text before caret + tab + text after caret
		// 	  this.innerHTML = this.innerHTML.substring(0, start) +
		// 		"\t" + this.innerHTML.substring(end);
		  
		// 	  // put caret at right position again
		// 	  this.selectionStart =
		// 		this.selectionEnd = start + 1;
		// 	}
		// });
		
		// area.element.addEventListener('focusout', ({target}) => {

		// 	let start = area.element.selectionStart;
		// 	let end = area.element.selectionEnd;
		// 	let text = target.value;
		// 	if(start === end)
		// 		end = parseInt(start) +(text ? parseInt(text.substr(start).split(" ")[0].length) : -1);

		// 	setDataset(area.element,{
		// 		lastFocusedText : getSelection(),
		// 		start,
		// 		end
		// 	});
		// });

		// area.element.addEventListener('focusin', () => {
		// 	setDataset(area.element,{
		// 		lastFocusedText : "",
		// 		start: -1,
		// 		end: -1
		// 	});
		// });

		// return area;
	}

	getValue() {return this.value && this.value === 'true';}
}
if(!window.customElements.get('aon-textarea')){
	window.customElements.define('aon-textarea',  AonTextArea);
}

