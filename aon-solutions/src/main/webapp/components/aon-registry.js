import { AonElement } from './AonElement.js';
import { RegistryType } from '../models/enums.js';
import { getRegistries } from '../services/service.js';

import { AonBasicTable, AonSuggestion, AonAddress } from './components.js';
import { CONSTANT, CSS, EVENT, MSG, TAG } from '../environments/environments.js';

export class AonRegistry extends AonElement {

  OPTIONS;
  types;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get value() {
		return this.getAttribute(CONSTANT.VALUE);
	}

	set value(value) {
		this.setAttribute(CONSTANT.VALUE, value);
	}

  get type() {
		return this.getAttribute(CONSTANT.TYPE);
	}

	set type(type) {
		this.setAttribute(CONSTANT.TYPE, type);
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
    this.TABlE = this.id + CONSTANT.TABLE.initCap();
    this.OPTIONS = this.id + 'Options'
    this.DOCUMENT = this.id + 'Document';
    this.NAME = this.id + 'Name';
  }

  build() {
    this.clear();
    let div = this.createElement(TAG.DIV);
    div.style.display = "flex";
    this.appendChild(div);
    // let table = new AonBasicTable();
		// table.id = this.TABLE;
		// div.appendChild(table);
    // table.addRow();
    let span1 = this.createElement(TAG.SPAN);
    span1.style.width="25%";
    span1.style.marginRight = "2px";
    div.appendChild(span1);
    let doc = new AonSuggestion();
    doc.id = this.DOCUMENT;
    doc.title = MSG.NIF;
    doc.value = this.registry.document;
    // doc.readonly = this.invoice.isReadonly();
    doc.addEventListener(EVENT.CHANGE, () => {
      this.registry.setDocument(doc.value);
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    doc.addEventListener(EVENT.KEYUP, () => {
      console.log(doc.value);
      if(doc.value.length > 2) {
        let data = { types: this.types, document: doc.value};
				getRegistries(data).then(r => {
					this.buildOptions(r.map(r => {return {name: r.document + ' - ' + r.name, value: r.document, registry: r};}));
				});
			} else {
				this.closeOptions();
			}
    });
    doc.addEventListener(EVENT.SELECT, (event) => {
      this.setRegistry(event.detail.registry);
      this.dispatchEvent(new Event(EVENT.SELECT));
    });
    span1.appendChild(doc);

    let span2 = this.createElement(TAG.SPAN);
    span2.style.width="75%";
    div.appendChild(span2);
    let name = new AonSuggestion();
    name.id = this.NAME;
    name.name = CONSTANT.NAME;
    name.title = MSG.BUSINESS_NAME;
    name.value = this.registry.name;
    // name.readonly = this.invoice.isReadonly();
    name.addEventListener(EVENT.CHANGE, () => {
      // let registry = this.invoice.getRegistry();
      // registry.setName(total.value);
      // this.invoice.setRegistry(registry);
    });
    name.addEventListener(EVENT.KEYUP, () => {
      if(name.value.length > 2) {
        console.log(this.types);
        let data = { types: this.types, name: name.value};
				getRegistries(data).then(r => {
					this.buildOptions(r.map(r => {return {name: r.document + ' - ' + r.name, value: r.document, registry: r};}));
				});
      } else {
        this.closeOptions();
      }
    });
    name.addEventListener(EVENT.SELECT, (event) => {
      let registry = event.detail.registry;
      this.invoice.setRegistry(registry);
    });
    span2.appendChild(name);

    let div2 = this.createElement(TAG.DIV);
    let address = new AonAddress();
    div2.appendChild(address);
    this.appendChild(div2);

		// let options = this.createElement('div');
		// options.id = this.OPTIONS;
		// options.className = CSS.AON_INPUT_LIST_OPTIONS;
    // options.style.width = table.clientWidth;
		// div.appendChild(options);
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
          // let input = this.getElement(this.INPUT);
          // input.value = options[i].name;

          let nif = this.getElement(this.NIF);
          nif.value = options[i].registry.document;

          let name = this.getElement(this.NAME);
          name.value = options[i].registry.name;

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


  setRegistry(registry) { 
    this.registry = registry;
  }

  setType(type) { 
    this.type = type;
  }

  setTypes(types){
    this.types = types;
  }

}

if(!window.customElements.get(TAG.AON_REGISTRY)){
	window.customElements.define(TAG.AON_REGISTRY, AonRegistry);
}
