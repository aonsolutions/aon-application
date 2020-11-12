import {AonElement} from './AonElement.js';

import './aon-input.js';
export class AonSuggestion extends AonElement {

  INPUT;
  OPTIONS;

  static get observedAttributes() {
    return ['value', 'readonly', 'title'];
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

  attributeChangedCallback(name, oldValue, newValue) {
    //console.log(`attribute ${name} change!! ${newValue}`);
    if('value' === name  && this.getElement(this.INPUT)) {
      this.getElement(this.INPUT).value = newValue;
    }

    if('readonly' === name && this.getElement(this.INPUT)){
      this.getElement(this.INPUT).readonly = newValue;
    }

    if('title' === name && this.getElement(this.INPUT)){
      this.getElement(this.INPUT).description = newValue;
    }
  }

	constructor () {
		super();
    this.INPUT = this.id + 'Input';
    this.OPTIONS = this.id + 'Options'
  }

	connectedCallback () {
		this.innerHTML = `
      <aon-input id="${this.INPUT}"  description="${this.title}"></aon-input>
		`;
    this.build();
	}

  build() {
    let input = this.getElement(this.INPUT);
    input.addEventListener('keyup', () => {
			this.value = input.value;
	    this.dispatchEvent(new Event('keyup'));
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
    this.clearElement(this.OPTIONS);
    let div = this.getElement(this.OPTIONS);
    div.classList.add('is-visible');

    if(options.length === 0) return div;

    let ul = this.createElement('ul');
    ul.className = 'aonInputListOptionsUl';
    ul.setAttribute('for', this.getAttribute('id') + 'Icon');
    for(let i = 0; i < options.length; i++) {
      let li = this.createElement('li');
      li.className = 'aonInputListOptionsItem'
      li.innerHTML = options[i].name;
      li.addEventListener('click', (e) => {
        div.classList.remove('is-visible');
        this.value = options[i].value;
        let input = this.getElement(this.INPUT);
        input.value = options[i].name;
        this.dispatchEvent(new CustomEvent('select', {detail: options[i]}));
      });
      ul.appendChild(li);
    }
    div.appendChild(ul)

    document.addEventListener('click', function(event) {
      let isClickInside = div.contains(event.target);
      if(!isClickInside){
        if(div.classList.contains('is-visible')){
          div.classList.remove('is-visible');
        }
      }
    });
  }

  addIcon(icon) {
    this.getElement(this.INPUT).addIcon(icon);
  }

  addIconButton(icon, fn) {
    this.getElement(this.INPUT).addIconButton(icon, fn);
  }

  closeOptions() {
    let div = this.getElement(this.OPTIONS);
    if(div.classList.contains('is-visible')){
      div.classList.remove('is-visible');
    }
  }
}

window.customElements.define('aon-suggestion',  AonSuggestion);
