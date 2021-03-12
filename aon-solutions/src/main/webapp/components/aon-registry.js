import { AonElement } from './AonElement.js';

import { getCustomers } from '../services/service.js';

import * as EVENT from "../../environments/aonEvent.js";
import * as AON_TAG from "../../environments/aonTag.js";
import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";
import * as CSS from "../../environments/css.js";

export class AonRegistry extends AonElement {

  OPTIONS;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

  get type() {
		return this.getAttribute(CONSTANT.TYPE);
	}

	set type(type) {
		this.setAttribute(CONSTANT.TYPE, type);
	}

	get value() {
		return this.getAttribute(CONSTANT.VALUE);
	}

	set value(value) {
		this.setAttribute(CONSTANT.VALUE, value);
	}

	constructor() {
		super();
	}

	connectedCallback() {
    this.initialize();
    this.build();
	}

  initialize() {
    this.id = this.id || 'aonRegistry';
    this.OPTIONS = this.id + 'Options'
    this.NIF = this.id + 'Nif';
    this.NAME = this.id + 'Name';
  }

  clear() {
    this.innerHTML = '';
  }

  build() {
    this.clear();
    let div = document.createElement('div');
    div.style.marginBottom = '1.5rem';
    this.appendChild(div);

    let table = document.createElement('table');
    table.style.width = '100%';
    div.appendChild(table);

    let tdNif = document.createElement('td');
		tdNif.setAttribute('colspan', '1');
		tdNif.innerHTML = `<aon-input id="${this.NIF}" description="NIF"></aon-input>`;
    table.appendChild(tdNif);
    let nif = this.getElement(this.NIF);
    this.getElement(nif.DIV).style.margin = '0px';
    nif.addEventListener('keyup', () => {
      console.log(nif.value);
      if(nif.value.length > 2) {
				getCustomers().then( r => {
					this.buildOptions(r.filter(f => f.document.toUpperCase().includes(nif.value.toUpperCase()))
						.map(r => {return {name: r.document + ' - ' + r.name, value: r.document, registry: r};}));
				});
			} else {
				this.closeOptions();
			}
    });

    let tdName = document.createElement('td');
    tdName.setAttribute('colspan', '3');
    tdName.innerHTML = `<aon-input id="${this.NAME}" description="${MSG.AON_MSG_BUSINESS_NAME}"></aon-input>`;
    table.appendChild(tdName);
    let name = this.getElement(this.NAME);
    this.getElement(name.DIV).style.margin = '0px';
    name.addEventListener('keyup', () => {
      // get options!!
      if(name.value.length > 2) {
        getCustomers().then( r => {
          this.buildOptions(r.filter(f => f.name.toUpperCase().includes(name.value.toUpperCase()))
            .map(r => {return {name: r.document + ' - ' + r.name, value: r.document, registry: r};}));
        });
      } else {
        this.closeOptions();
      }
    });


		let options = this.createElement('div');
		options.id = this.OPTIONS;
		options.className = CSS.AON_INPUT_LIST_OPTIONS;
    options.style.width = table.clientWidth;
		div.appendChild(options);

  }

	buildOptions(options) {
    this.clearElementById(this.OPTIONS);
    if(options && options.length > 0){
      let div = this.getElement(this.OPTIONS);
      div.classList.add('is-visible');
      let ul = this.createElement('ul');
      ul.className = CSS.AON_INPUT_LIST_OPTIONS_UL;
      ul.setAttribute('for', this.getAttribute('id') + 'Icon');
      for (let i = 0; i < options.length; i++) {
        let li = this.createElement('li');
        li.className = CSS.AON_INPUT_LIST_OPTIONS_ITEM;
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

  closeOptions() {
    let div = this.getElement(this.OPTIONS);
    if (div.classList.contains('is-visible')) {
      div.classList.remove('is-visible');
    }
  }
}

if(!window.customElements.get(AON_TAG.AON_REGISTRY)){
	window.customElements.define(AON_TAG.AON_REGISTRY, AonRegistry);
}
