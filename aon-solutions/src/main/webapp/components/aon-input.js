import { AonElement } from "./AonElement.js";

import "./aon-icon-button.js";

import {CONSTANT, CSS, EVENT, TAG, MATERIAL_ICONS} from '../environments/environments.js'

export class AonInput extends AonElement {
  SPAN;
  DIV;
  ICON;
  LABEL;
  ICON_LABEL;
  INPUT;
  DESCRIPTION;
  OPTIONS;
  LOADING;

  static get observedAttributes() {
    return [
      CONSTANT.VALUE,
      CONSTANT.DISABLED,
      CONSTANT.READONLY,
      CONSTANT.VISIBLE,
      CONSTANT.OPTIONS,
      CONSTANT.DESCRIPTION,
      CONSTANT.AUTOCOMPLETE
    ];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get name() {
    return this.getAttribute(CONSTANT.NAME);
  }

  set name(name) {
    this.setAttribute(CONSTANT.NAME, name);
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

  get description() {
    return this.getAttribute(CONSTANT.DESCRIPTION);
  }

  set description(description) {
    this.setAttribute(CONSTANT.DESCRIPTION, description);
  }

  get autocomplete() {
    return this.getAttribute(CONSTANT.AUTOCOMPLETE);
  }

  set autocomplete(autocomplete) {
    this.setAttribute(CONSTANT.AUTOCOMPLETE, autocomplete);
  }

  get visible() {
    return this.getAttribute(CONSTANT.VISIBLE);
  }

  set visible(visible) {
    this.setAttribute(CONSTANT.VISIBLE, visible);
  }

  get readonly() {
    return this.getAttribute(CONSTANT.READONLY);
  }

  set readonly(readonly) {
    this.setAttribute(CONSTANT.READONLY, readonly);
  }

  get disabled() {
    return this.getAttribute(CONSTANT.DISABLED);
  }

  set disabled(disabled) {
    this.setAttribute(CONSTANT.DISABLED, disabled);
  }

  get filled() {
    return this.getAttribute(CONSTANT.FILLED);
  }

  set filled(filled) {
    this.setAttribute(CONSTANT.FILLED, filled);
  }

  get options() {
    return this.getAttribute(CONSTANT.OPTIONS);
  }

  set options(options) {
    this.setAttribute(CONSTANT.OPTIONS, options);
  }

  get pattern() {
    return this.getAttribute(CONSTANT.PATTERN);
  }

  set pattern(pattern) {
    this.setAttribute(CONSTANT.PATTERN, pattern);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    this.initialize();
    if (CONSTANT.VALUE === name) {
      let input = this.getElement(this.INPUT);
      if (this.isTypeList()) {
        let options = this.hasAttribute(CONSTANT.OPTIONS)
          ? JSON.parse(this.getAttribute(CONSTANT.OPTIONS))
          : [];
        options.forEach((item, i) => {
          if (item.value == newValue) {
            input.value = item.name;
          }
        });
      } else {
        if (newValue && CONSTANT.UNDEFINED !== newValue && input)
          input.value = newValue;
        if (input && CONSTANT.EMPTY === newValue) {
          input.value = CONSTANT.EMPTY;
        }
      }

      let desc = this.getElement(this.DESCRIPTION);
      if(desc && input.value.length > 0) {
        desc.classList.add(CSS.AON_INPUT_NOT_EMPTY);
      } else if(desc) desc.classList.remove(CSS.AON_INPUT_NOT_EMPTY);
    }

    if (CONSTANT.DISABLED === name) {
      let el = this.getElement(this.INPUT);
      if (el) {
        if (CONSTANT.FALSE == newValue) {
          el.removeAttribute(CONSTANT.DISABLED);
        } else el.setAttribute(CONSTANT.DISABLED, this.isDisabled());
      }
    }

    if (CONSTANT.READONLY === name && this.getElement(this.INPUT)) {
      if (this.isReadonly())
        this.getElement(this.INPUT).setAttribute(CONSTANT.READONLY, this.isReadonly());
      else this.getElement(this.INPUT).removeAttribute(CONSTANT.READONLY);
    }

    if (CONSTANT.VISIBLE === name) {
      let label = document.getElementById(this.LABEL);
      if (label) {
        label.style.display = this.isVisible() ? "block" : "none";
      }
    }

    if (CONSTANT.FILLED === name) {
      let label = document.getElementById(this.LABEL);
      label.className = this.isFilled() ? CSS.AON_INPUT_FILLED : CSS.AON_INPUT_UNDERLINED;
    }

    if (CONSTANT.OPTIONS === name) {
      this.buildOptions();
    }

    if (CONSTANT.DESCRIPTION === name && this.getElement(this.DESCRIPTION)) {
      this.getElement(this.DESCRIPTION).innerHTML = newValue;
    }
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.SPAN = this.id + 'Span';
    this.DIV = this.id + 'Div';
    this.ICON = this.id + 'Icon';
    this.LABEL = this.id + 'Label'
    this.ICON_LABEL = this.id + 'IconLabel';
    this.INPUT = this.id + 'Input';
    this.DESCRIPTION = this.id + 'Description';
    this.OPTIONS = this.id + 'Options';
    this.LOADING = this.id + 'Loading';
  }

  build() {
    let div = this.createElement(TAG.DIV);
    div.id = this.DIV;
    div.className = CSS.AON_INPUT_GROUP;
    div.style.width = "100%";
    this.appendChild(div);

    let label = this.createElement(TAG.LABEL);
    label.id = this.LABEL;
    label.className = this.isFilled() ? CSS.AON_INPUT_FILLED : CSS.AON_INPUT_UNDERLINED;
    label.style.marginBottom = "0px";
    label.style.width = "100%";

    let input = this.createElement(TAG.INPUT);
    if(this.autocomplete) input.autocomplete =this.autocomplete;
    input.required = true;
    input.id = this.INPUT;
    input.name = this.getAttribute(CONSTANT.NAME);
    if (this.getAttribute(CONSTANT.PATTERN)) {
      input.pattern = this.getAttribute(CONSTANT.PATTERN);
    }
    input.value = this.getAttribute(CONSTANT.VALUE) ? this.getAttribute(CONSTANT.VALUE) : CONSTANT.EMPTY;
    input.type = this.getAttribute(CONSTANT.TYPE) && !this.isTypeList()
        ? this.getAttribute(CONSTANT.TYPE) : CONSTANT.TEXT;

    if (this.isDisabled()) input.disabled = true;
    if (this.isReadonly() || this.isTypeList()) input.readonly = true;

    input.addEventListener(EVENT.CHANGE, () => this.value = input.value);

    input.addEventListener(EVENT.KEYUP, (e) => {
      if (input.type !== "time") this.value = input.value;
      this.dispatchEvent(new Event(EVENT.KEYUP));
    });

    input.addEventListener(EVENT.BLUR, (e) => {
      this.dispatchEvent(new Event(EVENT.BLUR));
    });

    label.appendChild(input);

    let span = this.createElement(TAG.SPAN);
    span.id = this.DESCRIPTION;
    span.className = CSS.AON_INPUT_LABEL;;
    span.innerHTML = this.getAttribute(CONSTANT.DESCRIPTION);
    if(input.value && CONSTANT.EMPTY !== input.value) {
      span.classList.add(CSS.AON_INPUT_NOT_EMPTY);
    }

    label.appendChild(span);

    if('password' === this.getAttribute('type') && !this.isDisabled() && !this.isReadonly()){
      let icon = document.createElement('i');
    	icon.setAttribute('id', this.getAttribute('id') + 'Icon');
    	icon.className =  'material-icons';
      icon.style.marginLeft = "-35px";
      icon.style.pointer = "pointer";
    	icon.innerHTML = 'visibility';
      icon.addEventListener(EVENT.CLICK, (e)=> {
        const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
        icon.innerHTML =  type === 'password' ? 'visibility' : 'visibility_off';
        input.type = type;
      });
    	label.appendChild(icon);
    }

    label.style.display = this.isVisible() ? "block" : "none";

    div.appendChild(label);

    if (this.isTypeList()) {
      this.addIcon(MATERIAL_ICONS.ARROW_DROP_DOWN);

      let span = this.createElement(TAG.SPAN);
      span.id = this.SPAN;
      span.style.width = "100%";
      div.appendChild(span);
    }
  }

  addIcon(icon, color) {
    let div = this.getElement(this.DIV);
    let iconLabel = this.getElement(this.ICON);
    if (!iconLabel) {
      iconLabel = this.createElement(TAG.LABEL);
      div.appendChild(iconLabel);
    }
    iconLabel.className = CSS.AON_INPUT_ICON_LABEL;
    iconLabel.id = this.ICON;
    iconLabel.setAttribute("for", this.INPUT);
    iconLabel.innerHTML = `<aon-icon-button id="${this.ICON_LABEL}" icon="${icon}" noHover="true"></aon-icon-button>`;

    if (color) this.getElement(this.ICON_LABEL).color = color;
  }

  removeIcon() {
    let iconLabel = this.getElement(this.ICON);
    if (iconLabel) iconLabel.remove();
  }

  addIconButton(icon, fn) {
    this.addIcon(icon);
    this.getElement(this.ICON_LABEL).addEventListener(EVENT.CLICK, (event) => {
      event.preventDefault();
      fn();
    });
  }

  buildOptions() {
    let span = this.getElement(this.SPAN);
    this.clearElementById(this.SPAN);

    let options = this.hasAttribute(CONSTANT.OPTIONS)
      ? JSON.parse(this.getAttribute(CONSTANT.OPTIONS)) : [];
    let div = document.createElement(TAG.DIV);
    div.id = this.OPTIONS;
    div.className = CSS.AON_INPUT_LIST_OPTIONS;
    span.appendChild(div);

    if (options.length === 0) return div;

    let ul = document.createElement(TAG.UL);
    ul.className = CSS.AON_INPUT_LIST_OPTIONS_UL;
    ul.setAttribute("for", this.ICON);
    for (let i = 0; i < options.length; i++) {
      let li = document.createElement(TAG.LI);
      li.className = CSS.AON_INPUT_LIST_OPTIONS_ITEM;
      li.innerHTML = options[i].name;
      li.addEventListener(EVENT.CLICK, (e) => {
        div.classList.remove(CSS.IS_VISIBLE);
        this.value = options[i].value;
        let input = this.getElement(this.INPUT);
        input.value = options[i].name;
        this.dispatchEvent(new Event(EVENT.SELECT));
      });
      ul.appendChild(li);
    }
    div.appendChild(ul);

    let button = this.getElement(this.ICON_LABEL);
    button.addEventListener(EVENT.CLICK, (event) => {
      event.stopPropagation();
      let el = this.getElement(this.OPTIONS);
      if (el.classList.contains(CSS.IS_VISIBLE)) {
        el.classList.remove(CSS.IS_VISIBLE);
      } else el.classList.add(CSS.IS_VISIBLE);
    });

    document.addEventListener(EVENT.CLICK, function (event) {
      let isClickInside = div.contains(event.target);
      if (!isClickInside && div.classList.contains(CSS.IS_VISIBLE)) {
        div.classList.remove(CSS.IS_VISIBLE);
      }
    });
  }

  onChange(fn) {
    this.getElement(this.INPUT).addEventListener(EVENT.CHANGE, fn);
  }

  isTypeList() {
    return this.hasAttribute(CONSTANT.TYPE) && this.getAttribute(CONSTANT.TYPE) === "list";
  }

  isVisible() {
    return !this.hasAttribute(CONSTANT.VISIBLE) ||
      (this.hasAttribute(CONSTANT.VISIBLE) && CONSTANT.FALSE !== this.getAttribute(CONSTANT.VISIBLE));
  }

  setVisible(visible) {
    this.setAttribute(CONSTANT.VISIBLE, visible);
  }

  isReadonly() {
    return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
      && CONSTANT.FALSE !== this.getAttribute(CONSTANT.READONLY);
  }

  setReadonly(readonly) {
    this.setAttribute(CONSTANT.READONLY, readonly);
  }

  isDisabled() {
    return this.hasAttribute(CONSTANT.DISABLED) && CONSTANT.FALSE !== this.getAttribute(CONSTANT.DISABLED);
  }

  setDisabled(disabled) {
    this.setAttribute(CONSTANT.DISABLED, disabled);
  }

  isFilled() {
    return this.hasAttribute(CONSTANT.FILLED) && CONSTANT.FALSE !== this.getAttribute(CONSTANT.FILLED);
  }

  setFilled(filled) {
    this.setAttribute(CONSTANT.FILLED, filled);
  }

  loading(valor) {
    let label = this.getElement(this.LABEL);
    let id = this.LOADING;
    let div_load = this.getElement(id);
    if (valor && !div_load) {
      div_load = this.createElement(TAG.DIV);
      div_load.id = id;
      div_load.classList.add(CSS.AON_ICON_CONTAINER);
      let load_i = this.createElement(TAG.I);
      load_i.classList.add(CSS.AON_LOADER);
      div_load.appendChild(load_i);
      label.appendChild(div_load);
    } else if (!valor && div_load) div_load.remove();
  }
}
if(!window.customElements.get(TAG.AON_INPUT)){
  window.customElements.define(TAG.AON_INPUT, AonInput);
}
