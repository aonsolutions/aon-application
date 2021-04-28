import { AonElement } from './AonElement.js';
import { getRegistries, getRegistryAddress } from '../services/service.js';

import { AonSuggestion, AonAddress } from './components.js';
import { CONSTANT, CSS, EVENT, MSG, TAG } from '../environments/environments.js';

export class AonRegistry extends AonElement {

  OPTIONS;
  DOCUMENT;
  NAME;
  ADDRESS;

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

  get readonly() {
		return this.getAttribute(CONSTANT.READONLY);
	}

	set readonly(readonly) {
		this.setAttribute(CONSTANT.READONLY, readonly);
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
    this.DOCUMENT = this.id + 'Document';
    this.NAME = this.id + 'Name';
    this.ADDRESS= this.id + 'Address';
  }

  build() {
    this.clear();

    let div = this.createElement(TAG.DIV);
    div.style.display = "flex";
    this.appendChild(div);
    
    let span1 = this.createElement(TAG.SPAN);
    span1.style.width="25%";
    span1.style.marginRight = "2px";
    div.appendChild(span1);
    let doc = new AonSuggestion();
    doc.id = this.DOCUMENT;
    doc.title = MSG.NIF;
    doc.value = this.registry.document;
    doc.readonly = this.isReadonly();
    doc.addEventListener(EVENT.CHANGE, () => {
      this.registry.document = doc.value;
      // getGlobalRegistries().then(r => {
      //   if(r.length > 0) {
      //     this.setRegistry(r[0]);
      //   }
      //   this.dispatchEvent(new Event(EVENT.CHANGE));
      // });
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
    span1.appendChild(doc);

    let span2 = this.createElement(TAG.SPAN);
    span2.style.width="75%";
    div.appendChild(span2);
    let name = new AonSuggestion();
    name.id = this.NAME;
    name.name = CONSTANT.NAME;
    name.title = MSG.BUSINESS_NAME;
    name.value = this.registry.name;
    name.readonly = this.isReadonly();
    name.addEventListener(EVENT.CHANGE, () => {
      this.registry.name = name.value;
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    name.addEventListener(EVENT.KEYUP, () => {
      if(name.value.length > 2) {
        console.log(this.types);
        let data = { types: this.types, name: name.value};
				getRegistries(data).then(r => {
					this.buildOptions(r.map(r => {return {name: r.document + ' - ' + r.name, value: r.document, registry: r};}));
				}).catch(e => alert(e));
      } else {
        this.closeOptions();
      }
    });
    span2.appendChild(name);

		let options = this.createElement('div');
		options.id = this.OPTIONS;
		options.className = CSS.AON_INPUT_LIST_OPTIONS;
    options.style.width = div.clientWidth;
    options.style.marginTop = "-16px";
		this.appendChild(options);

    let div2 = this.createElement(TAG.DIV);
    this.appendChild(div2);
    let address = new AonAddress();
    address.id = this.ADDRESS;
    address.title = MSG.ADDRESS;
    div2.appendChild(address);
    address.buildAddressValue(this.registry.address)
    address.addEventListener(EVENT.CHANGE, () => {
      this.registry.address = JSON.parse(address.value);
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
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
          this.setRegistry(options[i].registry);
          this.dispatchEvent(new Event(EVENT.CHANGE));
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
    if(this.DOCUMENT && this.NAME && this.ADDRESS) {
      let doc = this.getElement(this.DOCUMENT);
      if(doc) doc.value = registry.document;
      let name = this.getElement(this.NAME);
      if(name) name.value = registry.name;
      let address = this.getElement(this.ADDRESS);
      if(address) {
        let data = {registry: registry.id};
        getRegistryAddress(data).then(ra => {
           address.buildAddressValue(ra);
        });
      }
    }
  }

  setTypes(types){
    this.types = types;
  }

  isReadonly() {
    return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
      && CONSTANT.FALSE !== this.getAttribute(CONSTANT.READONLY);
  }
}

if(!window.customElements.get(TAG.AON_REGISTRY)){
	window.customElements.define(TAG.AON_REGISTRY, AonRegistry);
}
