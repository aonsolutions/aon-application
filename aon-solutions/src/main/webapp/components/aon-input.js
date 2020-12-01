import { AonElement } from './AonElement.js';
import { Countries } from '../services/country.js';

export class AonInput extends AonElement {

  SPAN;
  DIV;
  ICON;
  ICON_LABEL;
  INPUT;
  DESCRIPTION;

  static get observedAttributes() {
    return ['value', 'disabled', 'readonly', 'visible', 'options', 'description'];
  }

  get id() {
    return this.getAttribute('id');
  }

  set id(id) {
    this.setAttribute('id', id);
  }

  get required() {
    return this.getAttribute('required');
  }

  set required(required) {
    this.setAttribute('required', required);
  }

  get name() {
    return this.getAttribute('name');
  }

  set name(name) {
    this.setAttribute('name', name);
  }

  get type() {
    return this.getAttribute('type');
  }

  set type(type) {
    this.setAttribute('type', type);
  }

  get value() {
    return this.getAttribute('value');
  }

  set value(value) {
    this.setAttribute('value', value);
  }

  get description() {
    return this.getAttribute('description');
  }

  set description(description) {
    this.setAttribute('description', description);
  }

  get visible() {
    return this.getAttribute('visible');
  }

  set visible(visible) {
    this.setAttribute('visible', visible);
  }

  get readonly() {
    return this.getAttribute('readonly');
  }

  set readonly(readonly) {
    this.setAttribute('readonly', readonly);
  }

  get disabled() {
    return this.getAttribute('disabled');
  }

  set disabled(disabled) {
    this.setAttribute('disabled', disabled);
  }

  get filled() {
    return this.getAttribute('filled');
  }

  set filled(filled) {
    this.setAttribute('filled', filled);
  }

  get options() {
    return this.getAttribute('options');
  }

  set options(options) {
    this.setAttribute('options', options);
  }

  get pattern() {
    return this.getAttribute('pattern');
  }

  set pattern(pattern) {
    this.setAttribute('pattern', pattern);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    //console.log(`attribute ${name} change!! ${newValue}`);
    if ('value' === name) {
      let input = this.getElement(this.INPUT);
      if (this.isTypeList()) {
        let options = this.hasAttribute('options') ? JSON.parse(this.getAttribute('options')) : [];
        options.forEach((item, i) => {
          if (item.value == newValue) {
            input.value = item.name;
          }
        });
      } else if (this.isTypeAddress()) {
        let value = (this.value && this.value != 'undefined') ? JSON.parse(this.value) : {
          country: 'ES',
          address: '',
          zip: '',
          city: '',

          province: ''
        };
        let val = `${value.address}, ${value.zip} ${value.city}, ${value.province}, ${value.country}`;
        input.value = val;
      } else {

        if (newValue && 'undefined' !== newValue && input) input.value = newValue;
        if (input && newValue === '') {
          input.value = '';
        }
      }
    }

    if ('disabled' === name) {
      let el = this.getElement(this.getAttribute('id') + 'Input');
      if (el) {
        if (newValue == "false") {
          el.removeAttribute('disabled');
        } else
          el.setAttribute('disabled', this.isDisabled());
      }
    }

    if ('readonly' === name) {
      if (this.isReadonly())
        this.getElement(this.INPUT).setAttribute('readonly', this.isReadonly());
      else this.getElement(this.INPUT).removeAttribute('readonly');
    }

    if ('visible' === name) {
      let label = document.getElementById(this.getAttribute('id') + 'Label');
      if (label) {
        label.style.display = this.isVisible() ? 'block' : 'none';
      }
    }

    if ('filled' === name) {
      let label = document.getElementById(this.getAttribute('id') + 'Label');
      label.className = this.isFilled() ? 'omrs-input-filled' : 'omrs-input-underlined';
    }

    if ('options' === name) {
      this.buildOptions();
    }

    if ('description' === name && this.getElement(this.DESCRIPTION)) {
      this.getElement(this.DESCRIPTION).innerHTML = newValue;
    }
  }

  constructor() {
    super();
    this.SPAN = this.id + 'Span';
    this.DIV = this.id + 'Div';
    this.ICON = this.id + 'Icon';
    this.ICON_LABEL = this.id + 'IconLabel';
    this.INPUT = this.id + 'Input';
    this.DESCRIPTION = this.id + 'Description';
  }

  connectedCallback() {
    this.build();
  }

  build() {
    let div = document.createElement('div');
    div.id = this.DIV;
    div.className = 'omrs-input-group';
    div.style.width = '100%';
    this.appendChild(div);

    let label = document.createElement('label');
    label.id = this.getAttribute('id') + 'Label';
    label.className = this.isFilled() ? 'omrs-input-filled' : 'omrs-input-underlined';
    label.style.marginBottom = '0px';
    label.style.width = '100%';

    let input = document.createElement('input');
    input.required = this.getAttribute('required');
    input.id = this.getAttribute('id') + 'Input';
    input.name = this.getAttribute('name');
    if (this.getAttribute('pattern')) {
      input.pattern = this.getAttribute('pattern');
    }
    input.value = this.getAttribute('value') ? this.getAttribute('value') : '';
    input.type = this.getAttribute('type') && !this.isTypeList() ? this.getAttribute('type') : 'text';
    if ('date' === this.getAttribute('type')) {
      this.style.minWidth = '150px';
    }
    if (this.isDisabled())
      input.disabled = true;
    if (this.isReadonly() || this.isTypeList())
      input.readonly = true;

    input.addEventListener('change', () => {
      this.setAttribute('value', document.getElementById(input.getAttribute('id')).value);
    });

    input.addEventListener('keyup', (e) => {
      this.value = input.value;
      this.dispatchEvent(new Event('keyup'));
    });


    input.addEventListener('blur', (e) => {
      this.dispatchEvent(new Event('blur'));
    });

    label.appendChild(input);

    let span = document.createElement('span');
    span.id = this.DESCRIPTION;
    span.className = 'omrs-input-label';
    span.innerHTML = this.getAttribute('description');

    label.appendChild(span);

    // if('password' === this.getAttribute('type')){
    //   let icon = document.createElement('i');
    // 	icon.setAttribute('id', this.getAttribute('id') + 'Icon');
    // 	icon.className =  'material-icons';
    // 	icon.innerHTML = 'visibility'; //'visibility_off'
    // 	label.appendChild(icon);
    // }

    label.style.display = this.isVisible() ? 'block' : 'none';

    div.appendChild(label);

    if (this.isTypeList()) {
      let iconLabel = document.createElement('label');
      iconLabel.style.position = 'absolute';
      iconLabel.style.top = '5px';
      iconLabel.style.right = '0px';
      iconLabel.style.marginBottom = '0px';
      iconLabel.setAttribute('id', this.getAttribute('id') + 'Icon');
      iconLabel.setAttribute('for', input.getAttribute('id'));
      iconLabel.innerHTML = `<aon-icon-button id="${this.getAttribute('id') + 'IconLabel'}" icon="arrow_drop_down" noHover="true"></aon-icon-button>`;
      div.appendChild(iconLabel);

      let span = document.createElement('span');
      span.style.width = '100%';
      span.setAttribute('id', this.getAttribute('id') + 'Span');
      div.appendChild(span);
    }

    if (this.isTypeAddress()) {
      let value = this.hasAttribute('value') ? JSON.parse(this.getAttribute('value')) : {
        country: 'ES',
        address: '',
        zip: '',
        city: '',
        province: ''
      };
      input.setAttribute('readonly', 'readonly');
      input.setAttribute('value', `${value.address}, ${value.zip} ${value.city}, ${value.province}, ${value.country}`);

      let iconLabel = document.createElement('label');
      iconLabel.style.position = 'absolute';
      iconLabel.style.top = '5px';
      iconLabel.style.right = '0px';
      iconLabel.style.marginBottom = '0px';
      iconLabel.setAttribute('id', this.getAttribute('id') + 'Icon');
      iconLabel.setAttribute('for', input.getAttribute('id'));
      iconLabel.innerHTML = `<aon-icon-button id="${this.getAttribute('id') + 'IconLabel'}" icon="room" noHover="true"></aon-icon-button>`;
      div.appendChild(iconLabel);
      this.buildAddress();
      iconLabel.addEventListener('click', () => {
        let divEdit = document.getElementById(this.getAttribute('id') + 'Edit');
        if (divEdit.style.display === 'block') {
          divEdit.style.display = 'none';
        } else {
          divEdit.style.display = 'block';
        }
      });
    }
  }

  addIcon(icon, color) {
    let div = this.getElement(this.DIV);
    let iconLabel = this.getElement(this.ICON);
    if (!iconLabel) {
      iconLabel = this.createElement('label');
      div.appendChild(iconLabel)
    }
    iconLabel.style.position = 'absolute';
    iconLabel.style.top = '5px';
    iconLabel.style.right = '0px';
    iconLabel.style.marginBottom = '0px';
    iconLabel.setAttribute('id', this.ICON);
    iconLabel.setAttribute('for', this.INPUT);
    iconLabel.innerHTML = `<aon-icon-button id="${this.ICON_LABEL}" icon="${icon}" noHover="true"></aon-icon-button>`;

    if (color)
      this.getElement(this.ICON_LABEL).color = color;
  }

  removeIcon() {
    let iconLabel = this.getElement(this.ICON);
    if (iconLabel) iconLabel.remove();
  }

  addIconButton(icon, fn) {
    this.addIcon(icon);
    this.getElement(this.ICON_LABEL)
      .addEventListener('click', (event) => {
        event.preventDefault();
        fn();
      });
  }

  buildOptions() {
    let span = document.getElementById(this.getAttribute('id') + 'Span');
    span.innerHTML = "";

    let options = this.hasAttribute('options') ? JSON.parse(this.getAttribute('options')) : [];
    let div = document.createElement('div')
    div.id = this.getAttribute('id') + 'Options';
    div.className = 'aonInputListOptions';
    span.appendChild(div);

    if (options.length === 0) return div;

    let ul = document.createElement('ul');
    ul.className = 'aonInputListOptionsUl';
    ul.setAttribute('for', this.getAttribute('id') + 'Icon');
    for (let i = 0; i < options.length; i++) {
      let li = document.createElement('li');
      li.className = 'aonInputListOptionsItem'
      li.innerHTML = options[i].name;
      li.addEventListener('click', (e) => {
        div.classList.remove('is-visible');
        this.value = options[i].value;
        let input = document.getElementById(this.getAttribute('id') + 'Input');
        input.value = options[i].name;
        this.dispatchEvent(new Event('select'));
      });
      ul.appendChild(li);
    }
    div.appendChild(ul)

    let button = document.getElementById(this.getAttribute('id') + 'IconLabel');
    button.addEventListener('click', (event) => {
      event.stopPropagation();
      let el = document.getElementById(this.getAttribute('id') + 'Options');
      if (el.classList.contains('is-visible')) {
        el.classList.remove('is-visible');
      } else el.classList.add('is-visible');
    });

    document.addEventListener('click', function (event) {
      let isClickInside = div.contains(event.target);
      if (!isClickInside) {
        if (div.classList.contains('is-visible')) {
          div.classList.remove('is-visible');
        }
      }
    });
  }

  buildAddress(value) {
    let div = document.createElement('div');
    div.setAttribute('id', this.getAttribute('id') + 'Edit');
    div.style.width = '100%';
    div.style.display = 'none';
    let html = `
      <aon-input class='aon-width-100' id='${this.getAttribute('id') + 'Address'}' description='Dirección'></aon-input>
      <aon-input class='aonWidth25' id='${this.getAttribute('id') + 'Zip'}' description='C.P.'></aon-input>
      <aon-input class='aonWidth25' id='${this.getAttribute('id') + 'City'}' description='Ciudad'></aon-input>
      <aon-input class='aonWidth25' id='${this.getAttribute('id') + 'Province'}' description='Provincia'></aon-input>
      <aon-input type="list" class='aonWidth25' id='${this.getAttribute('id') + 'Country'}' description='País' ></aon-input>
    `;

    div.innerHTML = html;
    this.appendChild(div);
  }

  buildAddressValue(val) {
    this.value = val;
    let value = (this.value && this.value != 'undefined') ? JSON.parse(this.getAttribute('value')) : {
      country: 'ES',
      address: '',
      zip: '',
      city: '',
      province: ''
    };

    let address = document.getElementById(this.getAttribute('id') + 'Address');
    if (address) {
      address.value = value.address;
      address.addEventListener('change', () => this.updateAddress());
    }

    let zip = document.getElementById(this.getAttribute('id') + 'Zip')
    if (zip) {
      zip.value = value.zip;
      zip.addEventListener('change', () => this.updateZip());
    }

    let city = document.getElementById(this.getAttribute('id') + 'City');
    if (city) {
      city.value = value.city;
      city.addEventListener('change', () => this.updateCity());
    }
    let province = document.getElementById(this.getAttribute('id') + 'Province');
    if (province) {
      province.value = value.province;
      province.addEventListener('change', () => this.updateProvince());
    }

    let country = document.getElementById(this.getAttribute('id') + 'Country');
    if (country) {
      country.options = JSON.stringify(Countries.map(c => { return { value: c.iso2, name: c.nombre }; }));
      country.value = value.country;
      country.addEventListener('select', () => this.updateCountry());
    }
  }

  updateAddress() {
    if (this.hasAttribute('value')) {
      let address = document.getElementById(this.getAttribute('id') + 'Address');
      let value = JSON.parse(this.getAttribute('value'));
      value.address = address.value;
      this.value = JSON.stringify(value);
    }
  }

  updateZip() {
    if (this.hasAttribute('value')) {
      let zip = document.getElementById(this.getAttribute('id') + 'Zip');
      let value = JSON.parse(this.getAttribute('value'));
      value.zip = zip.value;
      this.value = JSON.stringify(value);
    }
  }

  updateCity() {
    if (this.hasAttribute('value')) {
      let city = document.getElementById(this.getAttribute('id') + 'City');
      let value = JSON.parse(this.getAttribute('value'));
      value.city = city.value;
      this.setAttribute('value', JSON.stringify(value));
    }
  }

  updateProvince() {
    if (this.hasAttribute('value')) {
      let province = document.getElementById(this.getAttribute('id') + 'Province');
      let value = JSON.parse(this.getAttribute('value'));
      value.province = province.value;
      this.setAttribute('value', JSON.stringify(value));
    }
  }

  updateCountry() {
    if (this.hasAttribute('value')) {
      let country = document.getElementById(this.getAttribute('id') + 'Country');
      let value = JSON.parse(this.getAttribute('value'));
      value.country = country.value;
      this.setAttribute('value', JSON.stringify(value));
    }
  }

  onChange(fn) {
    let input = document.getElementById(this.getAttribute('id') + 'Input');
    input.addEventListener('change', fn);
  }

  isTypeList() {
    return this.hasAttribute('type') && this.getAttribute('type') === 'list';
  }

  isTypeAddress() {
    return this.hasAttribute('type') && this.getAttribute('type') === 'address';
  }

  isVisible() {
    return !this.hasAttribute('visible') || (this.hasAttribute('visible') && 'false' !== this.getAttribute('visible'));
  }

  setVisible(visible) {
    this.setAttribute('visible', visible);
  }

  isReadonly() {
    return this.hasAttribute('readonly') && this.getAttribute('readonly')
      && 'false' !== this.getAttribute('readonly')
  }

  setReadonly(readonly) {
    this.setAttribute('readonly', readonly);
  }

  isDisabled() {
    return this.hasAttribute('disabled') && 'false' !== this.getAttribute('disabled')
  }

  setDisabled(disabled) {
    this.setAttribute('disabled', disabled);
  }

  isFilled() {
    return this.hasAttribute('filled') && 'false' !== this.getAttribute('filled')
  }

  setFilled(filled) {
    this.setAttribute('filled', filled);
  }

  loading(valor) {
    let label = this.getElement(this.getAttribute('id') + 'Label');
    let id = this.getAttribute('id') + 'Loading';
    let div_load = this.getElement(id);
    if (valor && !div_load) {
      div_load = this.createElement('div');
      div_load.id = id;
      div_load.classList.add("aonIconContainer");
      let load_i = this.createElement('i');
      load_i.classList.add("aonLoader");
      div_load.appendChild(load_i);
      label.appendChild(div_load);
    } else if (!valor && div_load)
      div_load.remove();
  }

}

window.customElements.define('aon-input', AonInput);
