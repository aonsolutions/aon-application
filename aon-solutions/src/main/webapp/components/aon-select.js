import {AonElement} from './AonElement.js';

import './aon-input.js';

export class AonSelect extends AonElement {

  INPUT;
  OPTIONS;

  _selected;

  static get observedAttributes() {
    return ['value', 'options', 'disabled'];
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

  get options() {
  	return this.getAttribute('options');
  }

	set options(options) {
		this.setAttribute('options', options);
	}

  get autocomplete() {
    return this.getAttribute('autocomplete');
  }

  set autocomplete(autocomplete) {
    this.setAttribute('autocomplete', autocomplete);
  }

  get disabled() {
    return this.getAttribute('disabled')
  }

  set disabled(disabled) {
    this.setAttribute("disabled", disabled);
  }

  get readonly() {
    return this.getAttribute('readonly');
  }

  set readonly(readonly) {
    this.setAttribute('readonly', readonly);
  }


  attributeChangedCallback(name, oldValue, newValue) {
    if('value' === name) {
      let options = this.hasAttribute('options') ? JSON.parse(this.getAttribute('options')) : [];
      let detail = {};
      options.forEach((item, i) => {
        if(item.value == newValue) {
          this.getElement(this.INPUT).value = item.name;
        }
      });

      if(options.length > 0)
        detail =  options.find(v=>  v.value == newValue);

      this.dispatchEvent(new CustomEvent('change',{detail}));
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
    if(!this.hasAttribute('autocomplete')) {
      input.setAttribute('readonly', true);
    }
    input.addEventListener('keyup', () => {
      let options = this.hasAttribute('options') ? JSON.parse(this.getAttribute('options')) : [];
      this.buildOptions(options.filter(opt => opt.name.toUpperCase().includes(input.value.toUpperCase())));
    });
    input.addIconButton('arrow_drop_down', () => {
      if(!this.hasAttribute('readonly')) {
        let options = this.hasAttribute('options') ? JSON.parse(this.getAttribute('options')) : [];
        this.buildOptions(options);
      }
    });

    input.addEventListener('click', () => {
      if(!this.hasAttribute('readonly')) {
        let options = this.hasAttribute('options') && !this.getDisabled() ? JSON.parse(this.getAttribute('options')) : [];
        this.buildOptions(options);
      }
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
        this._selected = options[i];
        this.dispatchEvent(new CustomEvent('select', {detail: options[i]}));
      });
      ul.appendChild(li);
    }
    div.appendChild(ul)

    let input = this.getElement(this.INPUT);
    document.addEventListener('click', function(event) {
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
    this.setAttribute('options', JSON.stringify(options));
  }

  setEnumOptions(options) {
    let opts = [];
    for(let key in options) {
      opts.push({
        value: key,
        name: options[key]
      });
    }
    this.setAttribute('options', JSON.stringify(opts));
  }

  getDisabled(){
    return this.disabled == "true";
  }
}

window.customElements.define('aon-select', AonSelect);
