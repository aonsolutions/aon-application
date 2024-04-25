import { AonElement } from '../../components/AonElement.js';
import { getRegistries, getRegistryAddress, getRegistry } from '../../services/service.js';

import { AonSuggestion} from '../../components/aon-suggestion.js';

import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { AonAddress } from '../../components/aon-address.js';
import { RegistryType } from '../../models/enums.js';
import { AonSelect } from '../../components/aon-select.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { Address } from '../../models/registry/Address.js';

import * as LS from '../../services/localStorageService.js';
import { AonNewSuggestion } from '../../components/aon-new-suggestion.js';
import { AonNewSelect } from '../../components/aon-new-select.js';
import { Countries } from '../../services/country.js';

export class AonRegistrySuggestion extends AonElement {

  OPTIONS;
  OPTIONS_UL;
  OPTIONS_LI;
  DOCUMENT;
  NAME;
  ADDRESS;
  options;
  selected;
  showAddress;
  registry;
  showAddressList;
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
    this.init();
	}

  setRegistry(registry){
    this.registry = registry;
  }
  
  setTypes(types){
    this.types = types;
  }

  initialize() {
    this.id = this.id || 'aonRegistrySuggestion';
    this.OPTIONS = this.id + 'Options';
    this.OPTIONS_UL = this.OPTIONS + 'Ul';
    this.OPTIONS_LI = this.OPTIONS + 'Li';
    this.DOCUMENT_COUNTRY = this.id + 'DocumentCountry';
    this.DOCUMENT = this.id + 'Document';
    this.NAME = this.id + 'Name';
    this.REMOVE_REGISTRY = this.id + 'RemoveRegistry';
    this.TABLE = this.id + 'Table';
    this.ADDRESS= this.id + 'Address';
    this.ADDRESS_DIV = this.ADDRESS + 'Div';
    this.ADDRESS_TABLE = this.ADDRESS + 'Table';
    this.ADDRESS_LIST = this.ADDRESS + 'List';
    this.ADDRESS_ADD = this.ADDRESS + 'Add';
    this.ADDRESS_LIST_BUTTON = this.ADDRESS + 'ListButon';
    this.selected = -1;
    this.showAddress = this.showAddress || false;
    this.showAddressList = true;
    this.registry = this.registry || {name: '', document: ''};
  }

  build(){
     this.buildGeneral();
    this.buildAddress();
  }

  onChangeDocument(document) {
    let data = {
      document,
      additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD']
    };
    getRegistry(data).then(r => {
      if(r.id) {
        this.updateRegistry(registry);
      } else {
        if(!this.registry) this.registry = {};
        this.registry.document = document;
      }
      this.buildAddress();
      this.dispatchEvent(new Event(EVENT.SELECT_REGISTRY));
    });
  }

  onChangeName(name) {
    if(!this.registry) this.registry = {};
    this.registry.name = name;
    this.buildAddress();
    this.dispatchEvent(new Event(EVENT.SELECT_REGISTRY));
  }

  onKeyupDocument(event, value) {
    if(this.isMobile()) {
      let options = this.getElement(this.OPTIONS);
      options.style.marginTop = "-80px";
    }
    let data = { types: this.types, document: value};
    this.onKeyup(event, data, value);
  }

  onKeyupName(event, value) {
    if(this.isMobile()) {
      let options = this.getElement(this.OPTIONS);
      options.style.marginTop = "-16px";
    }
    let data = { types: this.types, name: value};
    this.onKeyup(event, data, value);
  }

  onKeyup(e, data, value) {
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
        this.updateRegistry(this.options[this.selected].registry);
        this.closeOptions();
     } else {
       if(value.length > 2) {
        getRegistries(data).then(r => {
          this.buildOptions(r.map(rs => {return {name: rs.document + ' - ' + rs.name, value: rs.document, registry: rs};}));
        }).catch(e => {
          // alert(e);
        });
      } else {
        this.closeOptions();
       }
      } 
    }
  }

  buildGeneral() {
    let div = this.createElement(TAG.DIV);
    div.className = this.isMobile() ? CSS.AON_BLOCK : CSS.AON_FLEX;
    this.appendChild(div);

    let span0 = this.createElement(TAG.SPAN);
    span0.style.width="20%";
    span0.style.marginRight = "2px";
    div.appendChild(span0);

    let country = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
    country.id = this.DOCUMENT_COUNTRY;
    country.title = MSG.COUNTRY;
    country.autocomplete = true;
    country.options = JSON.stringify(
      Countries.map((c) => {
        return { value: c.iso2, name: c.nombre };
      })
    );
    country.readonly = this.isReadonly();
    country.value = this.registry.documentCountry || 'ES';
    country.addEventListener(EVENT.SELECT, () => {
      this.registry.documentCountry = country.value;
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    span0.appendChild(country);
    if(this.registry.id) country.disabled = true;

    let span1 = this.createElement(TAG.SPAN);
    span1.style.width="25%";
    span1.style.marginRight = "2px";
    div.appendChild(span1);

    let document = this.createAonElement(LS.isNewTheme() ? new AonNewSuggestion() : new AonSuggestion(), this.DOCUMENT, MSG.NIF);
    document.readonly = this.isReadonly();
    document.value = this.registry.document;
    document.addEventListener(EVENT.KEYUP, (e) => this.onKeyupDocument(e, document.value));
    document.addEventListener(EVENT.CHANGE, () => this.onChangeDocument(document.value));
    span1.appendChild(document);
    if(this.registry.id) document.disabled = true;

    let span2 = this.createElement(TAG.SPAN);
    span2.style.width="55%";
    div.appendChild(span2);

    let name = this.createAonElement(LS.isNewTheme() ? new AonNewSuggestion() : new AonSuggestion(), this.NAME, MSG.BUSINESS_NAME);
    name.name = CONSTANT.NAME;
    name.value = this.registry.name;
    name.readonly = this.isReadonly();
    name.addEventListener(EVENT.KEYUP, (e) => this.onKeyupName(e, name.value));
    name.addEventListener(EVENT.CHANGE, () => this.onChangeName(name.value));
    span2.appendChild(name);
    if(this.registry.id) name.disabled = true;

    let removeRegistry = new AonIconButton();
    removeRegistry.id = this.REMOVE_REGISTRY;
    removeRegistry.title = MSG.DELETE;
    removeRegistry.icon = MATERIAL_ICONS.HIGHLIGHT_OFF;
    removeRegistry.style.paddingTop = '13px';
    removeRegistry.style.marginRight = '3px';
    removeRegistry.style.marginLeft = '4px';
    removeRegistry.style.display = !this.isReadonly() && this.registry.id ? 'block' : 'none';
    removeRegistry.addEventListener(EVENT.CLICK, () => {
      this.registry = {};
      country.value = 'ES';
      country.disabled = false;
      name.value = '';
      name.disabled = false;
      document.value = '';
      document.disabled = false;
      removeRegistry.style.display = 'none';
      this.clearAddress();
      this.dispatchEvent(new Event(EVENT.SELECT_REGISTRY));
    });
    div.appendChild(removeRegistry);

    let options = this.createElement(TAG.DIV);
		options.id = this.OPTIONS;
		options.className = CSS.AON_INPUT_LIST_OPTIONS;
    options.style.width = div.clientWidth;
    options.style.marginTop = "-16px";
		this.appendChild(options);
  }

  clearAddress() {
    let div = this.getElement(this.ADDRESS_DIV);
      if(!div) {
        div = this.createElement(TAG.DIV);
        div.id = this.ADDRESS_DIV;
        this.appendChild(div);
      }
      this.clearElement(div);
  }

  buildAddress() {
    if(this.isShowAddress()) {
      let div = this.getElement(this.ADDRESS_DIV);
      if(!div) {
        div = this.createElement(TAG.DIV);
        div.id = this.ADDRESS_DIV;
        this.appendChild(div);
      }
      this.clearElement(div);

      if(this.registry.address.id && this.registry.addresses && this.registry.addresses.length > 0 && this.showAddressList) { 
        let table = new AonBasicTable();
		    table.id = this.ADDRESS_TABLE;
		    div.appendChild(table);

        table.addRow();
        
        let addressList = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
        addressList.id = this.ADDRESS_LIST;
        addressList.title = MSG.ADDRESS;
        addressList.setAlias('id', 'fullAddress');
        addressList.setOptions(this.registry.addresses);
        addressList.value = this.registry.address.id;
        addressList.addEventListener(EVENT.CHANGE, () => {
          this.registry.address = this.registry.addresses.filter(f => f.id == addressList.value)[0];
          this.dispatchEvent(new Event(EVENT.CUSTOMER_CHANGE));
        });

        let addAddress = new AonIconButton();
        addAddress.id = this.ADDRESS_ADD;
        addAddress.title = MSG.ADD;
        addAddress.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
        addAddress.addEventListener(EVENT.CLICK, () => {
          this.showAddressList = false;
          this.registry.address = new Address();
          this.buildAddress();
        });
        let td = table.addCell(addressList);
	  		td.style.width = '100%';
  			table.addCell(addAddress);
      } else {
        let table = new AonBasicTable();
		    table.id = this.ADDRESS_TABLE;
		    div.appendChild(table);

        table.addRow();

        let address = new AonAddress();
        address.id = this.ADDRESS;
        address.title = MSG.ADDRESS;
        address.readonly = this.isReadonly();
        address.setAddress(this.registry.address);
        address.addEventListener(EVENT.CHANGE, () => {
          this.registry.address = address.getAddress();
          this.dispatchEvent(new Event(EVENT.CUSTOMER_CHANGE));
        });

        let td = table.addCell(address);
	  		td.style.width = '100%';
        if(!this.showAddressList || (this.registry.addresses && this.registry.addresses.length > 0)) {
          let listAddress = new AonIconButton();
          listAddress.id = this.ADDRESS_LIST_BUTTON;
          listAddress.title = MSG.ADDRESS;
          listAddress.outlined = CONSTANT.TRUE;
          listAddress.icon = MATERIAL_ICONS.PLAYLIST_ADD_CIRCLE;
          listAddress.addEventListener(EVENT.CLICK, () => {
            this.showAddressList = true;
            this.registry.address = this.registry.addresses.filter(f => f.main)[0]
            this.buildAddress();
          });
          table.addCell(listAddress);
        }
      }
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
          this.updateRegistry(options[i].registry);
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

  isShowAddress() {
    return this.showAddress && this.registry && this.registry.name && this.registry.document;
  }

  getRegistry() {
    return this.registry;
  }

  updateRegistry(registry) {
    if(registry) {
      this.registry = registry;
      let docCountry = this.getElement(this.DOCUMENT_COUNTRY);
      if(docCountry) docCountry.value = registry.documentCountry;
      let doc = this.getElement(this.DOCUMENT);
      if(doc) doc.value = registry.document;
      let name = this.getElement(this.NAME);
      if(name) name.value = registry.name;
      if(registry.global){
        let data = {registry: registry.id, global: registry.global};
        docCountry.disabled = true;
        doc.disabled = true;
        name.disabled = true;
        let rr = this.getElement(this.REMOVE_REGISTRY);
        if(rr) rr.style.display = 'block';
        getRegistryAddress(data).then(ra => {
            this.registry.address = ra;          
            this.buildAddress();
            this.dispatchEvent(new Event(EVENT.SELECT_REGISTRY));
        });
      } else if(registry.id) {
        docCountry.disabled = true;
        doc.disabled = true;
        name.disabled = true;
        let rr = this.getElement(this.REMOVE_REGISTRY);
        if(rr) rr.style.display = 'block';
        let data = {
          id: registry.id,
          additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD']
        };

        getRegistry(data).then(r => {
          this.registry = r;
          if(this.registry && this.registry.addresses) {
            this.registry.address = this.registry.addresses.filter(f => f.main)[0]; 
          }
          this.buildAddress();
          this.dispatchEvent(new Event(EVENT.SELECT_REGISTRY));
        });
      } else {
        this.buildAddress();
      }

    }
  }

  init() {
    if(this.registry) {
      if(this.registry.global){
        let data = {registry: this.registry.id, global: this.registry.global};
        getRegistryAddress(data).then(ra => {
            this.registry.address = ra;          
            this.build();
        });
      } else if(this.registry.id) {
        let data = {
          id: this.registry.id,
          additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD']
        };
        getRegistry(data).then(r => {
          let address = this.registry.address;
          this.registry = r;
          this.registry.address = address;

          if(this.registry && this.registry.addresses && this.registry.addresses.length > 0) {
            this.registry.address = this.registry.address || this.registry.addresses.filter(f => f.main)[0]; 
          }
          this.build();
        });
      } else this.build();
    } else this.build();
  }


  setTypes(types){
    this.types = types;
  }

  isReadonly() {
    return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
      && CONSTANT.FALSE !== this.getAttribute(CONSTANT.READONLY);
  }
}

if(!window.customElements.get(TAG.AON_REGISTRY_SUGGESTION)){
	window.customElements.define(TAG.AON_REGISTRY_SUGGESTION, AonRegistrySuggestion);
}
