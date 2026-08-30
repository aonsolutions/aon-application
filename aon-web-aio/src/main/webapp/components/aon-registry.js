import { AonElement } from './AonElement.js';
import { getRegistries, getRegistryAddress } from '../services/service.js';

import { CONSTANT, CSS, EVENT, MSG, TAG } from '../environments/environments.js';
import { AonAddress } from './aon-address.js';

import { AonNewSuggestion } from './aon-new-suggestion.js';

export class AonRegistry extends AonElement {

  OPTIONS;
  OPTIONS_UL;
  OPTIONS_LI;
  DOCUMENT;
  NAME;
  ADDRESS;
  options;
  selected;
  showAddress;
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
    this.OPTIONS = this.id + 'Options';
    this.OPTIONS_UL = this.OPTIONS + 'Ul';
    this.OPTIONS_LI = this.OPTIONS + 'Li';
    this.DOCUMENT = this.id + 'Document';
    this.NAME = this.id + 'Name';
    this.ADDRESS= this.id + 'Address';
    this.selected = -1;

    this.showAddress = this.showAddress === undefined
      ? true : this.showAddress;
  }

  build() {
    this.clear();

    let div = this.createElement(TAG.DIV);
    div.style.display = this.isMobile() ? 'block' : 'flex';
    this.appendChild(div);
    
    let span1 = this.createElement(TAG.SPAN);
    span1.style.width="25%";
    span1.style.marginRight = "2px";
    div.appendChild(span1);
    let doc = new AonNewSuggestion();
    doc.id = this.DOCUMENT;
    doc.title = MSG.NIF;
    doc.readonly = this.isReadonly();
    doc.addEventListener(EVENT.CHANGE, () => {
      this.registry.document = doc.value;
      this.registry.id = undefined;
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    doc.addEventListener(EVENT.KEYUP, (e) => {
      if(this.isMobile()) {
        let options = this.getElement(this.OPTIONS);
        options.style.marginTop = "-80px";
      }
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
        this.setRegistry(this.options[this.selected].registry);
        // this.dispatchEvent(new Event(EVENT.SELECT));
       } else {
          console.log(doc.value);
          if(doc.value.length > 2) {
           let data = { types: this.types, document: doc.value};
			  	  getRegistries(data).then(r => {
			  	  	this.buildOptions(r.map((rs) => {return {name: rs.document + ' - ' + rs.name, value: rs.document, registry: rs};}));
			  	  });
			   } else {
			  	  this.closeOptions();
			   }
        }
      }
    });
    span1.appendChild(doc);
    doc.value = this.registry.document;
    
    let span2 = this.createElement(TAG.SPAN);
    span2.style.width="75%";
    div.appendChild(span2);
    let name = new AonNewSuggestion();
    name.id = this.NAME;
    name.name = CONSTANT.NAME;
    name.title = MSG.BUSINESS_NAME;
    name.readonly = this.isReadonly();
    name.addEventListener(EVENT.CHANGE, () => {
      this.registry.name = name.value;
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    name.addEventListener(EVENT.KEYUP, (e) => {
      if(this.isMobile()) {
        let options = this.getElement(this.OPTIONS);
        options.style.marginTop = "-16px";
      }
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
        this.setRegistry(this.options[this.selected].registry);
        // this.dispatchEvent(new Event(EVENT.SELECT));
       } else {
         if(name.value.length > 2) {
          console.log(this.types);
           let data = { types: this.types, name: name.value};
			  	getRegistries(data).then(r => {
			  		this.buildOptions(r.map(rs => {return {name: rs.document + ' - ' + rs.name, value: rs.document, registry: rs};}));
			  	}).catch(e => alert(e));
        } else {
          this.closeOptions();
         }
        } 
      }
    });
    span2.appendChild(name);
    name.value = this.registry.name;

		let options = this.createElement('div');
		options.id = this.OPTIONS;
		options.className = CSS.AON_INPUT_LIST_OPTIONS;
    options.style.width = div.clientWidth;
    options.style.marginTop = "-16px";
		this.appendChild(options);
    if(this.showAddress) {
      let div2 = this.createElement(TAG.DIV);
      this.appendChild(div2);
      let address = new AonAddress();
      address.id = this.ADDRESS;
      address.title = MSG.ADDRESS;
      address.readonly = this.isReadonly();
      address.setAddress(this.registry.address);
      div2.appendChild(address);

      address.addEventListener(EVENT.CHANGE, () => {
        this.registry.address = address.getAddress();
        this.dispatchEvent(new Event(EVENT.CHANGE));
      });
    }
  }

	buildOptions(options) {
    this.clearElementById(this.OPTIONS);
    this.options = options;
    this.selected = -1;
    if(options && options.length > 0){
      let div = this.getElement(this.OPTIONS);
      div.classList.add('is-visible');
      let ul = this.createElement(TAG.UL);
      ul.id = this.OPTIONS_UL;
      ul.classList.add(CSS.AON_UL);
      ul.classList.add(CSS.AON_INPUT_LIST_OPTIONS_UL);
      ul.setAttribute('for', this.getAttribute('id') + 'Icon');
      for (let i = 0; i < options.length; i++) {
        let li = this.createElement('li');
        li.id = this.OPTIONS_LI + i;
        li.className = CSS.AON_INPUT_LIST_OPTIONS_ITEM;
        li.innerHTML = options[i].name;
        li.addEventListener('click', (e) => {
          div.classList.remove('is-visible');
          this.setRegistry(options[i].registry);
          // this.dispatchEvent(new Event(EVENT.SELECT));
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

  getRegistry() {
    return this.registry;
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
        let data = {registry: registry.id, global: registry.global};
        getRegistryAddress(data).then(ra => {
            ra.registry = registry.id;
            ra.global = registry.global;
            address.setAddress(ra, true);
            this.registry.address = ra;
            this.dispatchEvent(new Event(EVENT.SELECT_REGISTRY));
        });
      } else this.dispatchEvent(new Event(EVENT.SELECT_REGISTRY));
    }
  }

  onChange(fn) {
    this.addEventListener(EVENT.CHANGE, fn);
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
