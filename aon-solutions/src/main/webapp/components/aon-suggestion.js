import {AonElement} from './AonElement.js';

import './aon-input.js';
export class AonSuggestion extends AonElement {

  INPUT;
  OPTIONS;

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
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

	constructor () {
		super();
    this.INPUT = this.id + 'Input';
    this.OPTIONS = this.id + 'Options'
  }

	connectedCallback () {
		this.innerHTML = `
      <aon-input id="${this.INPUT}"  description="${this.title}"></aon-input>
      <div id="${this.OPTIONS}" class="aonInputListOptions"><span>
		`;
    this.build();
	}

  build() {
    let input = this.getElement(this.INPUT);
    input.addEventListener('keyup', () => {
			this.value = input.value;
	    this.dispatchEvent(new Event('keyup'));
    });
    this.getElement(input.DIV).style.marginBottom = '0px';
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
        this.dispatchEvent(new Event('select'));
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

  closeOptions() {
    let div = this.getElement(this.OPTIONS);
    if(div.classList.contains('is-visible')){
      div.classList.remove('is-visible');
    }
  }
}

window.customElements.define('aon-suggestion',  AonSuggestion);
