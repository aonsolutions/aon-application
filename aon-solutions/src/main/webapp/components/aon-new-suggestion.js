import { AonElement } from './AonElement.js';

import './aon-input.js';
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonNewInput } from './aon-new-input.js';

export class AonNewSuggestion extends AonNewInput {

  OPTIONS;
  OPTIONS_UL;
  OPTIONS_LI;

  options;
  selected;

  static get observedAttributes() {
    return [CONSTANT.VALUE, CONSTANT.READONLY, CONSTANT.TITLE, CONSTANT.DISABLED];
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

  get readonly() {
    return this.getAttribute(CONSTANT.READONLY);
  }

  set readonly(readonly) {
    this.setAttribute(CONSTANT.READONLY, readonly);
  }

  get disabled() {
    return this.getAttribute(CONSTANT.DISABLED);
  }

  set disabled(disabled) {
    this.setAttribute(CONSTANT.DISABLED, disabled);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    //console.log(`attribute ${name} change!! ${newValue}`);
    if (CONSTANT.VALUE === name && this.getElement(this.INPUT)) {
      this.getElement(this.INPUT).value = newValue;
    }

    if (CONSTANT.READONLY === name && this.getElement(this.INPUT)) {
      this.getElement(this.INPUT).readonly = newValue;
    }

    if (CONSTANT.TITLE === name && this.getElement(this.INPUT)) {
      this.getElement(this.INPUT).description = newValue;
    }

    if (CONSTANT.DISABLED === name) {
      let el = this.getElement(this.INPUT);
      el.setAttribute(CONSTANT.DISABLED, newValue);
      if(newValue == CONSTANT.FALSE)
        el.removeAttribute(CONSTANT.DISABLED); 
    }
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
    this.buildSuggestion();
  }

  initialize() {
    super.initialize();
    this.id = this.id || 'aonSuggestion';
    this.OPTIONS = this.id + 'Options';
    this.OPTIONS_UL = this.OPTIONS + 'Ul';
    this.OPTIONS_LI = this.OPTIONS + 'Li';

  }

  buildSuggestion() {
    let input = this.getElement(this.INPUT);
    input.readonly = this.isReadonly();
    input.value = this.value;
    input.addEventListener(EVENT.KEYUP, (e) => {
      this.value = input.value;
      if(e.key || e.keyCode) {
        if (e.keyCode == '38' || e.key == 'ArrowUp') {
          // up arrow
          let li = this.getElement(this.OPTIONS_LI + this.selected);
          if(li) li.style.backgroundColor = 'transparent';
          if(this.selected > -1){
            this.selected = this.selected - 1;
            let li2 = this.getElement(this.OPTIONS_LI + this.selected);
            if(li2) li2.style.backgroundColor = '#f1f1f1';
         }
       }
       else if (e.keyCode == '40' || e.key == 'ArrowDown') {
         // down arrow
         let li = this.getElement(this.OPTIONS_LI + this.selected);
         if(li) li.style.backgroundColor = 'transparent';
         this.selected = this.selected + 1;
         let li2 = this.getElement(this.OPTIONS_LI + this.selected);
         if(li2) li2.style.backgroundColor = '#f1f1f1';
       } else if (e.keyCode == '13' || e.key == 'Enter') {
         // enter
         this.closeOptions();
        if(this.selected) {
            this.value = this.options[this.selected].value;
            let input = this.getElement(this.INPUT);
            input.value = this.options[this.selected].name;
            this.dispatchEvent(new CustomEvent(EVENT.SELECT, { detail: this.options[this.selected] }));
        }  
       } else {
         this.dispatchEvent(new Event(EVENT.AON_KEYUP));
       }
     }
    });

    input.addEventListener('change', () => {
      this.value = input.value;
      this.dispatchEvent(new Event('change'));
    });

    let rootDiv = this.getElement(this.ROOT);
    let span = this.createSpan();
    span.id = this.id + "OptionsSpan";
    rootDiv.appendChild(span);

    let optionsDiv = this.createDiv();
    optionsDiv.id = this.OPTIONS;
    optionsDiv.className = 'aonInputListOptions';
    span.appendChild(optionsDiv);
  }

  buildOptions(options) {
    this.clearElementById(this.OPTIONS);
    this.options = options;
    this.selected = -1;
    if(options && options.length > 0){
      let div = this.getElement(this.OPTIONS);
      div.style.width = this.getBoundingClientRect().width;
      div.classList.add('is-visible');
      let ul = this.createElement(TAG.UL);
      ul.id = this.OPTIONS_UL;
      ul.classList.add(CSS.AON_UL);
      ul.classList.add(CSS.AON_INPUT_LIST_OPTIONS_UL);
      ul.setAttribute('for', this.getAttribute('id') + 'Icon');
      for (let i = 0; i < options.length; i++) {
        let li = this.createElement('li');
        li.id = this.OPTIONS_LI + i;
        li.className = 'aonInputListOptionsItem'
        li.innerHTML = options[i].name;
        li.addEventListener('click', (e) => {
          div.classList.remove('is-visible');
          this.value = options[i].value;
          let input = this.getElement(this.INPUT);
          input.value = options[i].name;
          this.dispatchEvent(new CustomEvent('select', { detail: options[i] }));
        });
        ul.appendChild(li);
      }
      div.appendChild(ul)

      document.addEventListener('click', function (event) {
        let isClickInside = div.contains(event.target);
        if (!isClickInside) {
          if (div.classList.contains('is-visible')) {
            div.classList.remove('is-visible');
          }
        }
      });
    } else this.closeOptions();
  }

  addIcon(icon, color) {
    this.getElement(this.INPUT).addIcon(icon, color);
  }

  removeIcon() {
    this.getElement(this.INPUT).removeIcon();
  }

  addIconButton(icon, fn) {
    this.getElement(this.INPUT).addIconButton(icon, fn);
  }

  loading(start) {
    this.getElement(this.INPUT).loading(start);
  }
  
  closeOptions() {
    let div = this.getElement(this.OPTIONS);
    if (div.classList.contains('is-visible')) {
      div.classList.remove('is-visible');
    }
  }

  focus() {
    this.getElement(this.INPUT).focus();
  }

  isReadonly() {
    return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
      && CONSTANT.FALSE !== this.getAttribute(CONSTANT.READONLY);
  }


  setMaxlength(maxlength) {
    this.getElement(this.INPUT).maxlength = maxlength;
  }
}
if(!window.customElements.get(TAG.AON_NEW_SUGGESTION)){
  window.customElements.define(TAG.AON_NEW_SUGGESTION, AonNewSuggestion);
}
