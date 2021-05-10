import {AonElement} from './AonElement.js';
import { CONSTANT, EVENT, TAG } from '../environments/environments.js';
import './aon-input.js';

export class AonSelect extends AonElement {

  INPUT;
  OPTIONS;

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
      let detail = {};
      options.forEach((item, i) => {
        if(item.value == newValue) {
          let input = this.getElement(this.INPUT);
          if(input) input.value = item.name;
        }
      });

      if(options.length > 0)
        detail =  options.find(v=>  v.value == newValue);

      this.dispatchEvent(new CustomEvent(EVENT.CHANGE,{detail}));
    }
  }

	constructor () {
		super();
  }

	connectedCallback () {
    this.INPUT = this.id + 'Input';
    this.OPTIONS = this.id + CONSTANT.OPTIONS
    this.innerHTML = `
      <aon-input id="${this.INPUT}"  description="${this.title}"></aon-input>
		`;
    this.build();
	}

  build() {
    let input = this.getElement(this.INPUT);
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
      if(!this.hasAttribute(CONSTANT.READONLY)) {
        const optios = this.hasAttribute(CONSTANT.OPTIONS) && !this.getDisabled() ? JSON.parse(this.getAttribute(CONSTANT.OPTIONS)) : [];
        this.buildOptions(optios);
      }
    });

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

  buildOptions(options) {
    this.clearElementById(this.OPTIONS);
    let input = this.getElement(this.INPUT);
    let div = this.getElement(this.OPTIONS);
    div.classList.add('is-visible');

    if(options.length === 0) return div;

    let ul = this.createElement(TAG.UL);
    ul.className = 'aonInputListOptionsUl';
    ul.setAttribute('for', this.getAttribute(CONSTANT.ID) + 'Icon');
    for(let i = 0; i < options.length; i++) {
      let li = this.createElement(TAG.LI);
      li.className = 'aonInputListOptionsItem'
      li.innerHTML = options[i].name;
      li.addEventListener(EVENT.CLICK, (e) => {
        div.classList.remove('is-visible');
        this.value = options[i].value;
        input.value = options[i].name;
        this._selected = options[i];
        this.dispatchEvent(new CustomEvent('select', {detail: options[i]}));
      });
      ul.appendChild(li);
    }
    div.appendChild(ul)


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
    return this.disabled == "true";
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
  }
}
if(!window.customElements.get('aon-select')){
  window.customElements.define('aon-select', AonSelect);
}
