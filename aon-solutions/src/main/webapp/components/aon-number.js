import { AonElement } from './AonElement.js';
import { formatNumber } from '../services/utils.js';
import { CONSTANT, CSS } from '../environments/environments.js';
import './aon-icon-button.js';


export class AonNumber extends AonElement {

    SPAN;
    DIV;
    ICON;
    ICON_LABEL;
    INPUT;
    DESCRIPTION;

    static get observedAttributes() {
        return [CONSTANT.VALUE, CONSTANT.DISABLED, CONSTANT.READONLY, CONSTANT.VISIBLE, CONSTANT.OPTIONS, CONSTANT.DESCRIPTION];
    }

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    get required() {
        return this.getAttribute(CONSTANT.REQUIRED);
    }

    set required(required) {
        this.setAttribute(CONSTANT.REQUIRED, required);
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

    get format() {
        return "true" == this.getAttribute('format');
    }

    set format(format) {
        this.setAttribute('format', format);
    }

    get decimals() {
        return this.getAttribute('decimals');
    }

    set decimals(decimals) {
        this.setAttribute('decimals', decimals);
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if ('value' === name) {
            let input = this.getElement(this.INPUT);
            if (newValue && 'undefined' !== newValue && input && !isNaN(newValue)) input.value = this.onBlur(newValue);
            if (input && newValue === '') input.value = '';
            let desc = this.getElement(this.DESCRIPTION);
            if(desc && input.value.length > 0) {
              desc.classList.add(CSS.AON_INPUT_NOT_EMPTY);
            } else if(desc) desc.classList.remove(CSS.AON_INPUT_NOT_EMPTY);
        }
        if (CONSTANT.DISABLED === name) {
            this.getElement(this.getAttribute('id') + 'Input').setAttribute(CONSTANT.DISABLED, this.isDisabled());
        }

        if (CONSTANT.READONLY === name) {
            if (this.isReadonly())
                this.getElement(this.INPUT).setAttribute(CONSTANT.READONLY, this.isReadonly());
            else this.getElement(this.INPUT).removeAttribute(CONSTANT.READONLY);
        }

        if (CONSTANT.VISIBLE === name) {
            let label = this.getElement(this.getAttribute('id') + 'Label');
            if (label) {
                label.style.display = this.isVisible() ? 'block' : 'none';
            }
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
        this.ICON_LABEL = this.id + 'IconLabel';
        this.INPUT = this.id + 'Input';
        this.DESCRIPTION = this.id + CONSTANT.DESCRIPTION;
    }

    build() {
        let div = document.createElement('div');
        div.id = this.DIV;
        div.className = CSS.AON_INPUT_GROUP;
        div.style.width = '100%';
        this.appendChild(div);

        let label = document.createElement('label');
        label.id = this.getAttribute('id') + 'Label';
        label.className = CSS.AON_INPUT_UNDERLINED;
        label.style.marginBottom = '0px';
        label.style.width = '100%';

        let input = document.createElement('input');
        input.required = true;//this.getAttribute(CONSTANT.REQUIRED);
        input.id = this.getAttribute('id') + 'Input';
        input.name = this.getAttribute(CONSTANT.NAME);
        input.value = this.getAttribute('value') ? this.getAttribute('value') : '';
        input.type = 'text';
        input.autocomplete = "off"
        input.style.textAlign = 'right'
        if ('date' === this.getAttribute(CONSTANT.TYPE)) {
            this.style.minWidth = '150px';
        }
        if (this.isDisabled())
            input.disabled = true;

        input.addEventListener('keypress', (ev) => {
            let keyChar = String.fromCharCode(ev.which || ev.keyCode);
            let reg = new RegExp(/[^0-9]/g);
            if (this.format) reg = new RegExp(/[^0-9\.,]/g);
            if (reg.test(keyChar)) ev.preventDefault();
            this.dispatchEvent(new Event('keypress'));
        });

        input.addEventListener('focus', ({ target }) => {
            let value = target.value;
            if (value) input.value = this.onFocus(value);
            input.select();
            this.dispatchEvent(new Event('focus'));
        });

        input.addEventListener('blur', ({ target }) => {
            let value = target.value;
            if (value) {
                let newValue = this.onBlur(value);
                this.value = this.onFocus(newValue);
            }
            this.dispatchEvent(new Event('blur'));
        });

        input.addEventListener('change', ({ target }) => {
            let value = target.value;
            if (value) {
                let newValue = this.onBlur(value);
                this.value = this.onFocus(newValue);
            }
        });

        label.appendChild(input);

        let span = document.createElement('span');
        span.id = this.DESCRIPTION;
        span.className = CSS.AON_INPUT_LABEL;
        span.innerHTML = this.getAttribute(CONSTANT.DESCRIPTION);
        if(input.value && CONSTANT.EMPTY !== input.value) {
          span.classList.add(CSS.AON_INPUT_NOT_EMPTY);
        }
        label.appendChild(span);
        label.style.display = this.isVisible() ? 'block' : 'none';

        div.appendChild(label);
    }

    addIcon(icon) {
        let div = this.getElement(this.DIV);
        let iconLabel = this.createElement('label');
        iconLabel.style.position = 'absolute';
        iconLabel.style.top = '5px';
        iconLabel.style.right = '0px';
        iconLabel.style.marginBottom = '0px';
        iconLabel.setAttribute('id', this.ICON);
        iconLabel.setAttribute('for', this.INPUT);
        iconLabel.innerHTML = `<aon-icon-button id="${this.ICON_LABEL}" icon="${icon}" noHover="true"></aon-icon-button>`;
        div.appendChild(iconLabel);
    }

    addIconButton(icon, fn) {
        this.addIcon(icon);
        this.getElement(this.ICON_LABEL).addEventListener('click', (event) => {
            event.preventDefault();
            fn();
        });
    }


    onChange(fn) {
        let input = this.getElement(this.getAttribute('id') + 'Input');
        input.addEventListener('change', fn);
    }

    onBlur(value) {
        let newValue = value;
        let decimals = this.decimals || 0;
        if (this.format) {
            newValue = formatNumber(value, decimals);
        }
        return newValue;
    }

    onFocus(value) {
        return value.replace(/\./g, "").replace(/\,/g, ".");
    }

    isVisible() {
        return !this.hasAttribute(CONSTANT.VISIBLE) || (this.hasAttribute(CONSTANT.VISIBLE) && 'false' !== this.getAttribute(CONSTANT.VISIBLE));
    }

    setVisible(visible) {
        this.setAttribute(CONSTANT.VISIBLE, visible);
    }

    isReadonly() {
        return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
            && 'false' !== this.getAttribute(CONSTANT.READONLY)
    }

    setReadonly(readonly) {
        this.setAttribute(CONSTANT.READONLY, readonly);
    }

    isDisabled() {
        return this.hasAttribute(CONSTANT.DISABLED) && 'false' !== this.getAttribute(CONSTANT.DISABLED)
    }

    setDisabled(disabled) {
        this.setAttribute(CONSTANT.DISABLED, disabled);
    }
}
if(!window.customElements.get('aon-number')){
  window.customElements.define('aon-number', AonNumber);
}
