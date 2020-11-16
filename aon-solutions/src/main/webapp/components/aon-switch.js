import {AonElement} from './AonElement.js';

export class AonSwitch extends AonElement {

	INPUT;

  static get observedAttributes() {
    return ['value', 'checked'];
  }

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

  get value() {
    return this.getAttribute('value')
  }

  set value(value) {
    this.setAttribute('value', value);
  }

  get checked() {
    return this.getAttribute('checked')
  }

  set checked(checked) {
    this.setAttribute('checked', checked);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if('value' === name) {
      this.getElement(this.INPUT).value = newValue;
  	}
    if('checked' === name) {
      this.getElement(this.INPUT).checked = this.isChecked();
  	}
  }

	constructor () {
		super();
    this.id = this.id || 'aonSwitch';
		this.INPUT = this.id + 'Input';
	}

	connectedCallback () {
		this.innerHTML = `
      <label class="aonSwitch">
        <input id="${this.INPUT}" type="checkbox">
        <span></span>
      </label>
		`;

    let input = this.getElement(this.INPUT);
    input.addEventListener('change', () => {
      this.value = input.value;
      this.checked = input.checked;
    });
  }

  isChecked(){
    return this.hasAttribute('checked') && this.getAttribute('checked')
      && 'false' !== this.getAttribute('checked')
  }
}

window.customElements.define('aon-switch',  AonSwitch);
