import { AonElement } from './AonElement.js';

import './aon-input.js';

export class AonSuggestion extends AonElement {

  INPUT;
  OPTIONS;

  static get observedAttributes() {
    return ['value', 'readonly', 'title', 'disabled'];
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

  get title() {
    return this.getAttribute('title');
  }

  set title(title) {
    this.setAttribute('title', title);
  }

  get readonly() {
    return this.getAttribute('readonly');
  }

  set readonly(readonly) {
    this.setAttribute('readonly', readonly);
  }

  get disabled() {
    return this.getAttribute('disabled');
  }

  set disabled(disabled) {
    this.setAttribute('disabled', disabled);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    //console.log(`attribute ${name} change!! ${newValue}`);
    if ('value' === name && this.getElement(this.INPUT)) {
      this.getElement(this.INPUT).value = newValue;
    }

    if ('readonly' === name && this.getElement(this.INPUT)) {
      this.getElement(this.INPUT).readonly = newValue;
    }

    if ('title' === name && this.getElement(this.INPUT)) {
      this.getElement(this.INPUT).description = newValue;
    }
    if ('disabled' === name) {
      let el = this.getElement(`${this.INPUT}`);
      el.setAttribute('disabled', newValue);
    }
  }

  constructor() {
    super();
    this.INPUT = this.id + 'Input';
    this.OPTIONS = this.id + 'Options'
  }

  connectedCallback() {
    this.innerHTML = `
      <aon-input id="${this.INPUT}" description="${this.title}" name="${this.name}" autocomplete="off"></aon-input>
		`;
    this.build();
  }

  build() {
    let input = this.getElement(this.INPUT);
    input.addEventListener('keyup', () => {
      this.value = input.value;
      this.dispatchEvent(new Event('keyup'));
    });

    input.addEventListener('change', () => {
      this.value = input.value;
      this.dispatchEvent(new Event('change'));
    });

    let div = this.getElement(input.DIV);
    let span = this.createElement('span');
    span.id = input.SPAN;
    div.appendChild(span);

    let options = this.createElement('div');
    options.id = this.OPTIONS;
    options.className = 'aonInputListOptions';
    span.appendChild(options);
  }

  buildOptions(options) {
    this.clearElementById(this.OPTIONS);
    if(options && options.length > 0){
      let div = this.getElement(this.OPTIONS);
      div.classList.add('is-visible');



      let ul = this.createElement('ul');
      ul.className = 'aonInputListOptionsUl';
      ul.setAttribute('for', this.getAttribute('id') + 'Icon');
      for (let i = 0; i < options.length; i++) {
        let li = this.createElement('li');
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
}
if(!window.customElements.get('aon-suggestion')){
  window.customElements.define('aon-suggestion', AonSuggestion);
}
