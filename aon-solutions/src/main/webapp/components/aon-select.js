import {AonElement} from './AonElement.js';
import { CONSTANT, EVENT, TAG } from '../environments/environments.js';
import { AonInput } from './aon-input.js';

export class AonSelect extends AonElement {

  INPUT;
  OPTIONS;
  detail;
  _selected;

  static get observedAttributes() {
    return [CONSTANT.VALUE, CONSTANT.OPTIONS, CONSTANT.DISABLED];
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
    return this.getAttribute(CONSTANT.VALUE);
  }

  set value(value) {
    this.setAttribute(CONSTANT.VALUE, value);
  }

  get title() {
    return this.getAttribute(CONSTANT.TITLE);
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
  }

  get options() {
  	return this.getAttribute(CONSTANT.OPTIONS);
  }

	set options(options) {
		this.setAttribute(CONSTANT.OPTIONS, options);
	}

  get autocomplete() {
    return this.getAttribute(CONSTANT.AUTOCOMPLETE);
  }

  set autocomplete(autocomplete) {
    this.setAttribute(CONSTANT.AUTOCOMPLETE, autocomplete);
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


  attributeChangedCallback(name, oldValue, newValue) {
    if(CONSTANT.VALUE === name) {
      let options = this.hasAttribute(CONSTANT.OPTIONS) ? JSON.parse(this.getAttribute(CONSTANT.OPTIONS)) : [];
      this.detail = {};
      options.forEach((item, i) => {
        if(item.value == newValue) {
          let input = this.getElement(this.INPUT);
          if(input) input.value = item.name;
        }
      });

      if(options.length > 0)
        this.detail =  options.find(v=>  v.value == newValue);
      this.dispatchEvent(new CustomEvent(EVENT.CHANGE,{detail: this.detail || {} }));
    } else if(CONSTANT.DISABLED === name){
      if(CONSTANT.TRUE == this.disabled){
        let input = this.getElement(this.INPUT);
        if(input) input.removeIcon();
      }
    }
  }

	constructor () {
		super();
  }

	connectedCallback () {
    this.INPUT = this.id + 'Input';
    this.OPTIONS = this.id + CONSTANT.OPTIONS;
    let aonInput = new AonInput();
    aonInput.id = this.INPUT;
    aonInput.description = this.title;
    aonInput.autocomplete = "off";
    this.appendChild(aonInput);
    this.build();
	}

  build() {
    let input = this.getElement(this.INPUT);
    if(input){
      input.readonly = this.isReadonly();
      if(!this.hasAttribute(CONSTANT.AUTOCOMPLETE)) {     
        input.setAttribute(CONSTANT.READONLY, true);
      }
      input.addEventListener(EVENT.KEYUP, () => {
        const optios = this.hasAttribute(CONSTANT.OPTIONS) ? JSON.parse(this.getAttribute(CONSTANT.OPTIONS)) : [];
        this.buildOptions(optios.filter(opt => opt.name.toUpperCase().includes(input.value.toUpperCase())));
      });
      input.addIconButton('arrow_drop_down', () => {
        if(!this.isReadonly()) {
          const optios = this.hasAttribute(CONSTANT.OPTIONS) ? JSON.parse(this.getAttribute(CONSTANT.OPTIONS)) : [];
          this.buildOptions(optios);
        }
      });
  
      input.addEventListener(EVENT.CLICK, () => {
        if(!this.isReadonly()) {
          const optios = this.hasAttribute(CONSTANT.OPTIONS) && !this.getDisabled() ? JSON.parse(this.getAttribute(CONSTANT.OPTIONS)) : [];
          this.buildOptions(optios);
        }
      });

      input.addEventListener(EVENT.BLUR, ()=>{
        const exists = this.getOptions().some(({name})=> name == input.value);
        if(!exists){
          const option = this.getOptions().find(f => f.value == this.value);
          if(option)
            input.value = option.name;
        } 
      })

      if(this.disabled)
        input.disabled = true;
      
      if(this.readonly && this.readonly == "true")
       input.readonly = true;

      let div = this.getElement(input.DIV);
      let span = this.createElement(TAG.SPAN);
      span.id = input.SPAN;
      div.appendChild(span);
  
      const options = this.createElement(TAG.DIV);
      options.id = this.OPTIONS;
      options.className = 'aonInputListOptions';
      span.appendChild(options);
  
      let opts = this.hasAttribute(CONSTANT.OPTIONS) ? JSON.parse(this.getAttribute(CONSTANT.OPTIONS)) : [];
      opts.forEach((item, i) => {
        if(item.value == this.value) {
          this.getElement(this.INPUT).value = item.name;
        }
      });
    }
  }

  buildOptions(options) {
    if(options.length === 0) return null;
    this.clearElementById(this.OPTIONS);
    let input = this.getElement(this.INPUT);
    let div = this.getElement(this.OPTIONS);
    div.classList.add('is-visible');
  
    if(this.default ||  this.hasAttribute(CONSTANT.DEFAULT)) 
      options.unshift({ name:"-", value:"" }); //EMPTY

    let ul = this.createElement(TAG.UL);
    ul.className = 'aonInputListOptionsUl';
    ul.setAttribute('for', this.getAttribute(CONSTANT.ID) + 'Icon');
    for (const option of options) {
      let li = this.createElement(TAG.LI);
      li.className = 'aonInputListOptionsItem'
      li.innerHTML = option.name;
      li.addEventListener(EVENT.CLICK, () => {
        div.classList.remove('is-visible');
        this.value = option.value;
        input.value = option.name;
        this._selected = option;
        this.dispatchEvent(new CustomEvent(EVENT.SELECT, {detail: option}));
      });
      ul.appendChild(li);
    }
    div.appendChild(ul);
    
    document.addEventListener(EVENT.CLICK, function(event) {
      this.value = this._selected ? this._selected.name : '';
      let isClickInside = input.contains(event.target);
      if(!isClickInside){
        if(div.classList.contains('is-visible')){
          div.classList.remove('is-visible');
        }
      }
    });
  }

  closeOptions() {
    let div = this.getElement(this.OPTIONS);
    if(div.classList.contains('is-visible')){
      div.classList.remove('is-visible');
    }
  }

  setOptions(options) {
    this.setAttribute(CONSTANT.OPTIONS, JSON.stringify(options));
  }

  getOptions() {
    const options = this.options ? this.options : "[]";
    return JSON.parse(options);
  }

  setEnumOptions(options) {
    let opts = [];
    for(let key in options) {
      opts.push({
        value: key,
        name: options[key]
      });
    }
    this.setAttribute(CONSTANT.OPTIONS, JSON.stringify(opts));
  }

  getDisabled(){
    return CONSTANT.TRUE == this.disabled;
  }

  setDisabled(disabled) {
    this.disabled = disabled;
    let input = this.getElement(this.INPUT);
    input.disabled = disabled; 
  } 
  
  isReadonly() {
    return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
      && CONSTANT.UNDEFINED !== this.getAttribute(CONSTANT.READONLY) && CONSTANT.FALSE !== this.getAttribute(CONSTANT.READONLY);
  }

  focus() {
    this.getElement(this.INPUT).focus();
  }

  clear(){
    this.value = "";
    let input = this.getElement(this.INPUT);
    if(input) input.value = "";
  }

  getText() {
    const input = this.getElement(this.INPUT);
    if(!input) return null;
  	return input.value;
  }

  getDetail(){
    return this.detail || {};
  }
}
if(!window.customElements.get('aon-select')){
  window.customElements.define('aon-select', AonSelect);
}
