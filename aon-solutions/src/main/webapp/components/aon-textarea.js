import { CSS, MATERIAL_ICONS } from '../environments/environments.js';
import { newComponent, setDataset, setEvents, waitEl } from '../services/utils.js';
import { AonElement } from './AonElement.js';

export class AonTextArea extends AonElement {

	TEXTAREA;
	TOOLBAR;
	LEFT;
	RIGHT;
	COMPILE;

	static get observedAttributes() {
		return ['value'];
	}

	get compile(){
		return this.COMPILE;
	}

	set compile(fn){
		this.COMPILE = () => fn(this);
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

	get textarea(){
		return this.TEXTAREA;
	}

	set textarea(value){
		this.TEXTAREA = value;
	}

	get toolbar(){
		return this.TOOLBAR;
	}

	set toolbar(value){
		this.TOOLBAR = value;
	}

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get name() {
		return this.getAttribute('name');
	}

	set name(name) {
		this.setAttribute('name', name);
	}

	get value() {
		return this.getAttribute('value');
	}

	set value(value) {
		this.setAttribute('value', value);
	}

	get compiledValue() {
		return this.COMPILE();
	}

	get visible() {
		return this.getAttribute('visible');
	}

	set visible(visible) {
		this.setAttribute('visible', visible);
	}

	get disabled() {
		return this.getAttribute('disabled');
	}

	set disabled(disabled) {
		this.setAttribute('disabled', disabled);
	}

	clear(){
		const area = this.querySelector("#" + this.TEXTAREA);
		area.innerHTML = "";
		area.value = "";
	}

	addToolbarOptionLeft(properties,fn){
		
		if(!properties.id) properties.id = "noId";
		if(!properties.icon && !properties.aonIcon) return;

		if(properties.aonIcon){


		}else
		if(properties.icon)
		{
			const icon = newComponent({
				text: properties.icon,
				id: properties.id,
				classes: ['icon',"material-icons",CSS.CENTER_FLEX],
				events : {click : fn}
			});
			waitEl("#" + this.LEFT).then(el => el.appendChild(icon.element));
		}
	}

	addToolbarOptionRight(properties,fn){
		if(!properties.id) properties.id = "noId";
		if(!properties.icon && !properties.aonIcon) return;
		if(properties.aonIcon){


		}else
		if(properties.icon)
		{
			const icon = newComponent({
				text: properties.icon,
				id: properties.id,
				classes: ['icon',"material-icons",CSS.CENTER_FLEX],
				events : {click : fn}
			});
			waitEl("#" + this.RIGHT).then(el => el.appendChild(icon.element));
		}
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('value' === name){
			let input = this.getElement(this.getAttribute('id') + 'Input');
			if(this.hasAttribute('value') && "true" === this.getAttribute('value')){
				input.setAttribute('checked', 'checked');
			} else input.removeAttribute('checked');
		}
	}

	constructor () {
		super();
		this.left = "left";
		this.right = "right";
		this.textarea = "textarea";
		this.toolbar = "toolbar";
	}

	connectedCallback () {
		this.build();
	}

	build() {
		const bar = newComponent({
			type: this.TOOLBAR,
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

		const eye = newComponent({
			text: MATERIAL_ICONS.VISIBILITY,
			id: "preview",
			classes: 
			[
				'icon',
				"material-icons",
				CSS.CENTER_FLEX
			],
			dataset :{
				selected : false
			}, 
		});

		const textarea = this.generateTextArea();
		setEvents(eye.element,
			{click : 
				() => {

					if(eye.element.dataset.selected == "false"){

						const backup = this.querySelector("textarea");
						eye.element.innerHTML = MATERIAL_ICONS.VISIBILITY_OFF;
						eye.element.dataset.selected = true;
						this.dataset.value = backup.value;
						
						const preview = newComponent({
							type : 'pre',
							classes : [CSS.NO_COPY],
							text :  this.COMPILE(),
							styles : {
								height : '100%',
								margin : 0
							}
						});

						this.removeChild(backup);
						this.appendChild(preview.element);
					} 
					else{
						eye.element.innerHTML = MATERIAL_ICONS.VISIBILITY;
						eye.element.dataset.selected = false;

						const preview = this.querySelector("pre");
						this.removeChild(preview);

						const backup = this.generateTextArea().element;
						this.appendChild(backup);
					} 	
				}
			}
		);
		eye.appendTo(left.element);

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
		let area = newComponent({
			type: "textarea",
			classes : [CSS.NO_FOCUS, CSS.MATERIAL_SCROLL],
			id: this.TEXTAREA,
			text : this.dataset.value,
			styles : {
				height: '100%'
			}
		});

		area.element.addEventListener('keydown', function(e) {
			if (e.key == 'Tab') {
			  e.preventDefault();
			  var start = this.selectionStart;
			  var end = this.selectionEnd;
		  
			  // set textarea value to: text before caret + tab + text after caret
			  this.value = this.value.substring(0, start) +
				"\t" + this.value.substring(end);
		  
			  // put caret at right position again
			  this.selectionStart =
				this.selectionEnd = start + 1;
			}
		});
		
		area.element.addEventListener('focusout', () => {
			setDataset(area.element,{
				lastFocusedText : getSelection(),
				start :area.element.selectionStart,
				end : area.element.selectionEnd,
			});
		});

		area.element.addEventListener('focusin', () => {
			setDataset(area.element,{
				lastFocusedText : "",
				start: -1,
				end: -1
			});
		});

		return area;
	}

	getValue() {return this.value && this.value === 'true';}
}
if(!window.customElements.get('aon-textarea')){
	window.customElements.define('aon-textarea',  AonTextArea);
}

