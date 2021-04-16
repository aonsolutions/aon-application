import { AonElement } from './AonElement.js';
import { Countries } from "../services/country.js";

import * as EVENT from "../../environments/aonEvent.js";
import * as AON_TAG from "../../environments/aonTag.js";
import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";
import * as CSS from "../../environments/css.js";

export class AonAddress extends AonElement {

  ADDRESS;
  CITY;
  COUNTRY;
  EDIT;
  INPUT;
  PROVINCE;
  ZIP;

  static get observedAttributes() {
    return [CONSTANT.VALUE];
  }

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

	constructor() {
		super();
	}

  attributeChangedCallback(name, oldValue, newValue) {
    this.initialize();
    if(CONSTANT.VALUE === name) {
      let value = this.hasAttribute(CONSTANT.VALUE)
        ? JSON.parse(this.getAttribute(CONSTANT.VALUE))
        : {
            country: "ES",
            address: "",
            zip: "",
            postal_code: "",
            city: "",
            province: "",
          };
      let aonInput = this.getElement(this.INPUT);
      aonInput.value = `${value.address}, ${value.zip} ${value.city}, ${value.province}, ${value.country}`;
    }
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
  }

  build() {
    this.clear();
    this.innerHTML = `
      <aon-input id="${this.INPUT}"  description="${this.title}"></aon-input>
		`;
    let aonInput = this.getElement(this.INPUT);
    let value = this.hasAttribute(CONSTANT.VALUE)
      ? JSON.parse(this.getAttribute(CONSTANT.VALUE))
      : {
          country: "ES",
          address: "",
          zip: "",
          postal_code: "",
          city: "",
          province: "",
        };
    aonInput.readonly = CONSTANT.READONLY;
    aonInput.value = `${value.address}, ${value.zip} ${value.city}, ${value.province}, ${value.country}`;

    aonInput.addIconButton(MATERIAL_ICONS.ROOM, () => {
      if (!this.hasAttribute(CONSTANT.READONLY)) {
        let divEdit = this.getElement(this.EDIT);
        if (divEdit.style.display === "block") {
          divEdit.style.display = "none";
        } else {
          divEdit.style.display = "block";
        }
      }
    });
    this.buildAddress();
  }

  buildAddress() {
    let div = this.createElement(AON_TAG.DIV);
    div.id = this.EDIT;
    div.style.width = "100%";
    div.style.display = "none";
    let html = `
      <aon-input class='aon-width-100' id='${this.ADDRESS}' description='Dirección'></aon-input>
      <aon-input class='aonWidth25' id='${this.ZIP}' description='C.P.'></aon-input>
      <aon-input class='aonWidth25' id='${this.CITY}' description='Ciudad'></aon-input>
      <aon-input class='aonWidth25' id='${this.PROVINCE}' description='Provincia'></aon-input>
      <aon-input type="list" class='aonWidth25' id='${this.COUNTRY}' description='País' ></aon-input>
    `;
    div.innerHTML = html;
    this.appendChild(div);
  }

  buildAddressValue(val) {
    if(!val) val = {};
    let value = {
      country: val.country || "ES",
      address: val.address || "",
      zip: val.zip || "",
      postal_code: val.zip || "",
      city: val.city || "",
      province: val.provice ||  ""
    }
    this.value = JSON.stringify(value);

    let address = this.getElement(this.ADDRESS);
    if (address) {
      address.value = value.address;
      address.addEventListener(EVENT.CHANGE, () => this.updateAddress());
    }

    let zip = this.getElement(this.ZIP);
    if (zip) {
      zip.value = value.zip;
      zip.addEventListener(EVENT.CHANGE, () => this.updateZip());
    }

    let city = this.getElement(this.CITY);
    if (city) {
      city.value = value.city;
      city.addEventListener(EVENT.CHANGE, () => this.updateCity());
    }
    let province = this.getElement(this.PROVINCE);
    if (province) {
      province.value = value.province;
      province.addEventListener(EVENT.CHANGE, () => this.updateProvince());
    }

    let country = this.getElement(this.COUNTRY);
    if (country) {
      country.options = JSON.stringify(
        Countries.map((c) => {
          return { value: c.iso2, name: c.nombre };
        })
      );
      country.value = value.country;
      country.addEventListener(EVENT.SELECT, () => this.updateCountry());
    }
  }

  updateAddress() {
    if (this.hasAttribute(CONSTANT.VALUE)) {
      let address = this.getElement(this.ADDRESS);
      let value = JSON.parse(this.getAttribute(CONSTANT.VALUE));
      value.address = address.value;
      this.value = JSON.stringify(value);
      this.getElement()
    }
  }

  updateZip() {
    if (this.hasAttribute(CONSTANT.VALUE)) {
      let zip = this.getElement(this.ZIP);
      let value = JSON.parse(this.getAttribute(CONSTANT.VALUE));
      value.zip = zip.value;
      value.postal_code = zip.value;
      this.value = JSON.stringify(value);
    }
  }

  updateCity() {
    if (this.hasAttribute(CONSTANT.VALUE)) {
      let city = this.getElement(this.CITY);
      let value = JSON.parse(this.getAttribute(CONSTANT.VALUE));
      value.city = city.value;
      this.value = JSON.stringify(value);
    }
  }

  updateProvince() {
    if (this.hasAttribute(CONSTANT.VALUE)) {
      let province = this.getElement(this.PROVINCE);
      let value = JSON.parse(this.getAttribute(CONSTANT.VALUE));
      value.province = province.value;
      this.value = JSON.stringify(value);
    }
  }

  updateCountry() {
    if (this.hasAttribute(CONSTANT.VALUE)) {
      let country = this.getElement(this.COUNTRY);
      let value = JSON.parse(this.getAttribute(CONSTANT.VALUE));
      value.country = country.value;
      this.value = JSON.stringify(value);
    }
  }

}

if(!window.customElements.get(AON_TAG.AON_ADDRESS)){
	window.customElements.define(AON_TAG.AON_ADDRESS, AonAddress);
}
