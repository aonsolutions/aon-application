import {AonElement} from './AonElement.js';
import { CONSTANT, TAG, EVENT } from '../environments/environments.js';
import '../css/aon-switch.css';

export class AonSwitch extends AonElement {

	INPUT;
  LABEL;
	TITLE;

  static get observedAttributes() {
    return [CONSTANT.VALUE, CONSTANT.CHECKED, CONSTANT.TITLE, CONSTANT.DISABLED];
  }

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

  get value() {
    return this.getAttribute(CONSTANT.VALUE)
  }

  set value(value) {
    this.setAttribute(CONSTANT.VALUE, value);
  }

	get title() {
    return this.getAttribute(CONSTANT.TITLE)
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
  }

  get checked() {
    return this.getAttribute(CONSTANT.CHECKED)
  }

  set checked(checked) {
    this.setAttribute(CONSTANT.CHECKED, checked);
  }

  get disabled() {
    return this.getAttribute(CONSTANT.DISABLED)
  }

  set disabled(disabled) {
    this.setAttribute(CONSTANT.DISABLED, disabled);
  }

	get readonly() {
		return this.getAttribute(CONSTANT.READONLY);
	}

	set readonly(readonly) {
		this.setAttribute(CONSTANT.READONLY, readonly);
	}

  get name() {
		return this.getAttribute(CONSTANT.NAME);
	}

	set name(name) {
		this.setAttribute(CONSTANT.NAME, name);
	}

  attributeChangedCallback(name, oldValue, newValue) {
    const el = this.getElement(this.INPUT);
    if(CONSTANT.VALUE === name) {
      if(el) el.value = newValue;
  	} else if(CONSTANT.CHECKED === name) {
      let boolean = newValue == CONSTANT.TRUE;
      if(el) el.checked = boolean;
      this.value = boolean;
  	} else if(CONSTANT.TITLE === name) {
			let title = this.getElement(this.TITLE);
			if(title)
				title.innerHTML = this.hasAttribute(CONSTANT.TITLE)
					? this.getAttribute(CONSTANT.TITLE) : CONSTANT.EMPTY;
    } else if (CONSTANT.DISABLED === name) {
      if (el) {
        if (newValue ==CONSTANT.FALSE)
          el.removeAttribute(CONSTANT.DISABLED);
        else
          el.setAttribute(CONSTANT.DISABLED, newValue);
      }
    } else if (CONSTANT.READONLY === name) {
      if (el) 
        el.setAttribute(CONSTANT.READONLY, newValue);
    }
  }

	constructor () {
		super();
	}

	connectedCallback () {
    this.initialize();
    let label = this.createElement(TAG.LABEL);
    label.id = this.LABEL;
    label.className = "aonSwitch";
    this.appendChild(label);

    let input = this.createElement(TAG.INPUT);
    input.id   = this.INPUT;
    input.name = this.name || this.INPUT ;
    input.type = "checkbox";
    if(this.disabled =="true") input.disabled = this.disabled;
    label.appendChild(input);

    let span = this.createElement(TAG.SPAN);
    span.id = this.TITLE;
    label.appendChild(span);

    input.addEventListener(EVENT.CHANGE, () => {
      this.value = input.value;
      this.checked = input.checked;
    });
		if(this.hasAttribute(CONSTANT.READONLY)) {
			input.setAttribute(CONSTANT.READONLY, CONSTANT.READONLY);
		}
   
		span.innerHTML = this.hasAttribute(CONSTANT.TITLE)
			? this.getAttribute(CONSTANT.TITLE) : CONSTANT.EMPTY;
    let boolean = false
    if(this.checked && this.checked==CONSTANT.TRUE) {
      input.checked = true;
      boolean= true;
    }
    this.value = boolean;
	}

  initialize() {
    this.id = this.id || 'aonSwitch';
		this.INPUT = this.id + CONSTANT.INPUT.initCap();
    this.LABEL = this.id + "Label";
		this.TITLE = this.id + CONSTANT.TITLE.initCap();
  }

  isChecked(){
    return this.hasAttribute(CONSTANT.CHECKED) && this.getAttribute(CONSTANT.CHECKED)
      && CONSTANT.FALSE !== this.getAttribute(CONSTANT.CHECKED)
  }

  focus() {
    this.getElement(this.INPUT).focus();
  }

  setDisabled(disabled) {
    this.disabled = disabled;
    let input = this.getElement(this.INPUT);
    if (disabled){
      input.setAttribute(CONSTANT.DISABLED, disabled);
    } else input.removeAttribute(CONSTANT.DISABLED); 
  } 

  setWidth(width) {
    let span = this.getElement(this.TITLE);
    span.style.width = width;
  }

  setMarginBottom(mb) {
    let span = this.getElement(this.TITLE);
    span.style.marginBottom = mb;
  }

  setLabelWidth(mb) {
    let label = this.getElement(this.LABEL);
    if(label) label.style.width = mb;
  }

  clear(){
  
  }
}
if(!window.customElements.get('aon-switch')){
	window.customElements.define('aon-switch',  AonSwitch);
}
