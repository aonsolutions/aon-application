import {AonElement} from './AonElement.js';
import * as CONSTANT from "../environments/constants.js";

export class AonSwitch extends AonElement {

	INPUT;
	TITLE;

  static get observedAttributes() {
    return [CONSTANT.VALUE, CONSTANT.CHECKED, CONSTANT.TITLE, 'disabled'];
  }

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

  get value() {
    return this.getAttribute(CONSTANT.VALUE)
  }

  set value(value) {
    this.setAttribute(CONSTANT.VALUE, value);
  }

	get title() {
    return this.getAttribute(CONSTANT.TITLE)
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
  }

  get checked() {
    return this.getAttribute(CONSTANT.CHECKED)
  }

  set checked(checked) {
    this.setAttribute(CONSTANT.CHECKED, checked);
  }

  get disabled() {
    return this.getAttribute(CONSTANT.disabled)
  }

  set disabled(disabled) {
    this.setAttribute("disabled", disabled);
  }

	get readonly() {
		return this.getAttribute('readonly');
	}

	set readonly(readonly) {
		this.setAttribute('readonly', readonly);
	}


  attributeChangedCallback(name, oldValue, newValue) {
    if(CONSTANT.VALUE === name) {
      this.getElement(this.INPUT).value = newValue;
  	}
    if(CONSTANT.CHECKED === name) {
      this.getElement(this.INPUT).checked = this.isChecked();
  	}
		if(CONSTANT.TITLE === name) {
			let title = this.getElement(this.TITLE);
			if(title)
				title.innerHTML = this.hasAttribute(CONSTANT.TITLE)
					? this.getAttribute(CONSTANT.TITLE) : CONSTANT.EMPTY;
    }

    if ('disabled' === name) {
      let el = this.getElement(this.INPUT);
      if (el) {
        if (newValue == "false")
          el.removeAttribute('disabled');
        else
          el.setAttribute('disabled', newValue);
      }
    }

		if ('readonly' === name) {
      let el = this.getElement(this.INPUT);
      if (el) {
        el.setAttribute('readonly', newValue);
      }
    }
  }

	constructor () {
		super();
    this.id = this.id || 'aonSwitch';
		this.INPUT = this.id + CONSTANT.INPUT.initCap();
		this.TITLE = this.id + CONSTANT.TITLE.initCap();
	}

	connectedCallback () {
		this.innerHTML = `
      <label class="aonSwitch">
        <input id="${this.INPUT}" type="checkbox">
        <span id="${this.TITLE}"></span>
      </label>
		`;

    let input = this.getElement(this.INPUT);
    input.addEventListener('change', () => {
      this.value = input.value;
      this.checked = input.checked;
    });
		if(this.hasAttribute('readonly')) {
			input.setAttribute('readonly', 'readonly');
		}

    let title = this.getElement(this.TITLE);
		title.innerHTML = this.hasAttribute(CONSTANT.TITLE)
			? this.getAttribute(CONSTANT.TITLE) : CONSTANT.EMPTY;
	}

  isChecked(){
    return this.hasAttribute(CONSTANT.CHECKED) && this.getAttribute(CONSTANT.CHECKED)
      && CONSTANT.FALSE !== this.getAttribute(CONSTANT.CHECKED)
  }
}
if(!window.customElements.get('aon-switch')){
	window.customElements.define('aon-switch',  AonSwitch);
}
