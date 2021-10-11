import { AonElement } from './AonElement.js';
import { Countries } from "../services/country.js";

import { CONSTANT, EVENT, TAG, MATERIAL_ICONS, MSG, CSS } from '../environments/environments.js';
import { Address } from '../models/registry/Address.js';
import { AonInput } from './aon-input.js';
import { AonSelect } from './aon-select.js';
import { TABLE } from '../environments/aonTag.js';
import { AonBasicTable } from './aon-basic-table.js';

export class AonAddress extends AonElement {

  ADDRESS;
  CITY;
  COUNTRY;
  EDIT;
  INPUT;
  PROVINCE;
  ZIP;

  address;

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

	constructor() {
		super();
	}

  connectedCallback() {
    this.initialize();
    this.build();
	}

  initialize() {
    this.id = this.id || 'aonAddress';
    this.ADDRESS = this.id + 'Address';
    this.CITY = this.id + 'City';
    this.COUNTRY = this.id + 'Country';
    this.EDIT = this.id + 'Edit';
    this.INPUT = this.id + 'Input';
    this.PROVINCE = this.id + 'Province';
    this.ZIP = this.id + 'Zip';
    this.address = this.address || new Address();
  }

  build() {
    this.clear();
    let aonInput = new AonInput();
    aonInput.id = this.INPUT;
    aonInput.description = this.title;
    aonInput.value = this.address.getFullAddress();
    this.appendChild(aonInput);
    aonInput.readonly = CONSTANT.READONLY;
    this.buildAddress();
    let color = this.address.isMain() ? '#002469' : undefined;
    aonInput.addIconWithRemove(MATERIAL_ICONS.ROOM, color, () => this.dispatchEvent(new Event(EVENT.DELETE)));

    this.getElement(aonInput.INPUT).style.cursor = 'pointer';
    aonInput.addEventListener(EVENT.CLICK, () => {
      if (!this.isReadonly()) {
        let divEdit = this.getElement(this.EDIT);
        if (divEdit.style.display === "block") {
          divEdit.style.display = "none";
        } else {
          divEdit.style.display = "block";
        }
      }
    });
  }

  buildAddress() {
    let table = new AonBasicTable();
    table.id = this.EDIT;
		table.style.display = "none";
    this.appendChild(table);

    table.addRow();
    
    let addressInput = new AonInput();
    addressInput.id = this.ADDRESS;
    addressInput.description = MSG.ADDRESS;
    addressInput.className = CSS.AON_WIDTH_ALL;
    addressInput.value = this.address.getAddress();
    addressInput.readonly = this.isReadonly();
    addressInput.addEventListener(EVENT.CHANGE, () => {
      this.address.setAddress(addressInput.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addCell(addressInput, 4);

    table.addRow();

    let zipInput = new AonInput();
    zipInput.id = this.ZIP;
    zipInput.description = MSG.POSTAL_CODE_MIN;
    zipInput.value = this.address.getZip();
    zipInput.readonly = this.isReadonly();
    zipInput.addEventListener(EVENT.CHANGE, () => {
      this.address.setZip(zipInput.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addCell(zipInput);

    let cityInput = new AonInput();
    cityInput.id = this.CITY;
    cityInput.description = MSG.CITY;
    cityInput.value = this.address.getCity();
    cityInput.readonly = this.isReadonly();
    cityInput.addEventListener(EVENT.CHANGE, () => {
      this.address.setCity(cityInput.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addCell(cityInput);


    let provinceInput = new AonInput();
    provinceInput.id = this.PROVINCE;
    provinceInput.description = MSG.PROVINCE;
    provinceInput.value = this.address.getProvince();
    provinceInput.readonly = this.isReadonly();
    provinceInput.addEventListener(EVENT.CHANGE, () => {
      this.address.setProvince(provinceInput.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addCell(provinceInput);


    let countryInput = new AonSelect();
    countryInput.id = this.COUNTRY;
    countryInput.title = MSG.COUNTRY;
    countryInput.options = JSON.stringify(
      Countries.map((c) => {
        return { value: c.iso2, name: c.nombre };
      })
    );
    countryInput.readonly = this.isReadonly();
    countryInput.value = this.address.getCountry();
    countryInput.addEventListener(EVENT.SELECT, () => {
      this.address.setCountry(countryInput.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addCell(countryInput);
  }

  isReadonly() {
    return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
      && CONSTANT.FALSE !== this.getAttribute(CONSTANT.READONLY);
  }

  getAddress() {
    return this.address;
  }

  setAddress(address) {
    this.address = new Address(address);
  }
}

if(!window.customElements.get(TAG.AON_ADDRESS)){
	window.customElements.define(TAG.AON_ADDRESS, AonAddress);
}
