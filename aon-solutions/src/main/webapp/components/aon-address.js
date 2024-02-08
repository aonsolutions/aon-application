import { AonElement } from './AonElement.js';
import { Countries } from "../services/country.js";

import { CONSTANT, EVENT, TAG, MATERIAL_ICONS, MSG, CSS } from '../environments/environments.js';
import { Address } from '../models/registry/Address.js';
import { AonInput } from './aon-input.js';
import { AonSelect } from './aon-select.js';
import { TABLE } from '../environments/aonTag.js';
import { AonBasicTable } from './aon-basic-table.js';
import { getStreetTypes, streetType } from '../services/StreetType.js';
import * as LS from '../services/localStorageService.js';
import { AonNewInput } from './aon-new-input.js';
import { AonNewSelect } from './aon-new-select.js';

export class AonAddress extends AonElement {

  STREET_TYPE;
  ADDRESS;
  NUMBER;
  ADDRESS2;
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
    this.STREET_TYPE = this.id + 'StreetType';
    this.ADDRESS = this.id + 'Address';
    this.ADDRESS2 = this.id + 'Address2';
    this.NUMBER = this.id + 'Number';
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
    let aonInput = LS.isNewTheme() ? new AonNewInput() : new AonInput();
    aonInput.id = this.INPUT;
    aonInput.description = this.title;
    aonInput.title = this.title;
    aonInput.value = this.address.getFullAddress();
    aonInput.readonly = CONSTANT.READONLY;
    this.appendChild(aonInput);

    this.buildAddress();
    let color = this.address.isMain() ? '#002469' : undefined;
    aonInput.addIconWithRemove(MATERIAL_ICONS.ROOM, color, () => this.dispatchEvent(new Event(EVENT.DELETE)));

    this.getElement(aonInput.INPUT).style.cursor = 'pointer';
    aonInput.addEventListener(EVENT.CLICK, () => {
      alert(this.isReadonly());
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
    table.style.backgroundColor = "#f1f1f1";
    this.appendChild(table);

    table.addRow();

    let streetTypeSelect = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
    streetTypeSelect.id = this.STREET_TYPE;
    streetTypeSelect.title = 'Tipo vía'; //MSG.STREET_TYPE;
    streetTypeSelect.options = JSON.stringify(
      getStreetTypes().map((c) => {
        return { value: c.ineCode, name: c.description.toLowerCase().initCap()};
      })
    );
    streetTypeSelect.readonly = this.isReadonly();
    streetTypeSelect.value = this.address.getStreetType();
    streetTypeSelect.addEventListener(EVENT.SELECT, () => {
      this.address.setStreetType(streetTypeSelect.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addCell(streetTypeSelect, 2);
    
    let addressInput = LS.isNewTheme() ? new AonNewInput() : new AonInput();
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
    table.addCell(addressInput, 6);

    table.addRow();

    let numberInput = LS.isNewTheme() ? new AonNewInput() : new AonInput();
    numberInput.id = this.NUMBER;
    numberInput.description = MSG.NUMBER;
    numberInput.className = CSS.AON_WIDTH_ALL;
    numberInput.value = this.address.getNumber();
    numberInput.readonly = this.isReadonly();
    numberInput.addEventListener(EVENT.CHANGE, () => {
      this.address.setNumber(numberInput.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addCell(numberInput, 2);

    let address2Input = LS.isNewTheme() ? new AonNewInput() : new AonInput();
    address2Input.id = this.ADDRESS2;
    address2Input.description = 'Resto Dirección';//MSG.ADDRESS;
    address2Input.className = CSS.AON_WIDTH_ALL;
    address2Input.value = this.address.getAddress2();
    address2Input.readonly = this.isReadonly();
    address2Input.addEventListener(EVENT.CHANGE, () => {
      this.address.setAddress2(address2Input.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addCell(address2Input, 6);

    table.addRow();


    let countryInput = LS.isNewTheme() ? new AonNewSelect() : new AonSelect();
    countryInput.id = this.COUNTRY;
    countryInput.title = MSG.COUNTRY;
    countryInput.options = JSON.stringify(
      Countries.map((c) => {
        return { value: c.iso2, name: c.iso2 };
      })
    );
    countryInput.readonly = this.isReadonly();
    countryInput.value = this.address.getCountry();
    countryInput.addEventListener(EVENT.SELECT, () => {
      this.address.setCountry(countryInput.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    let countryTd = table.addCell(countryInput, 1);
    countryTd.style.width = '20%';

    let zipInput = LS.isNewTheme() ? new AonNewInput() : new AonInput();
    zipInput.id = this.ZIP;
    zipInput.description = MSG.POSTAL_CODE_MIN;
    zipInput.value = this.address.getZip();
    zipInput.readonly = this.isReadonly();
    zipInput.addEventListener(EVENT.CHANGE, () => {
      this.address.setZip(zipInput.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.getElement(this.PROVINCE).value = this.address.getProvince();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    let zipTd = table.addCell(zipInput, 1);
    zipTd.style.width = '15%';
    let cityInput = LS.isNewTheme() ? new AonNewInput() : new AonInput();
    cityInput.id = this.CITY;
    cityInput.description = MSG.CITY;
    cityInput.value = this.address.getCity();
    cityInput.readonly = this.isReadonly();
    cityInput.addEventListener(EVENT.CHANGE, () => {
      this.address.setCity(cityInput.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addCell(cityInput, 3);


    let provinceInput = LS.isNewTheme() ? new AonNewInput() : new AonInput();
    provinceInput.id = this.PROVINCE;
    provinceInput.description = MSG.PROVINCE;
    provinceInput.value = this.address.getProvince();
    provinceInput.readonly = this.isReadonly();
    provinceInput.addEventListener(EVENT.CHANGE, () => {
      this.address.setProvince(provinceInput.value);
      this.getElement(this.INPUT).value = this.address.getFullAddress();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addCell(provinceInput, 3);
  }

  isReadonly() {
    return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
      && CONSTANT.FALSE !== this.getAttribute(CONSTANT.READONLY);
  }

  getAddress() {
    return this.address;
  }

  setAddress(address, build) {
    this.address = new Address(address);
    if(build) 
      this.build()
  }
}

if(!window.customElements.get(TAG.AON_ADDRESS)){
	window.customElements.define(TAG.AON_ADDRESS, AonAddress);
}
