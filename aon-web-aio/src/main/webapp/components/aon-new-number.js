import { formatNumber } from '../services/utils.js';
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import './aon-icon-button.js';
import { AonNewInput } from './aon-new-input.js';


export class AonNewNumber extends AonNewInput {


    static get observedAttributes() {
        return [CONSTANT.VALUE, CONSTANT.DISABLED, CONSTANT.READONLY, CONSTANT.VISIBLE, CONSTANT.OPTIONS, CONSTANT.DESCRIPTION];
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

    get minDecimal() {
        return this.getAttribute('minDecimal');
    }

    set minDecimal(minDecimal) {
        this.setAttribute('minDecimal', minDecimal);
    }

    get maxDecimal() {
        return this.getAttribute('maxDecimal');
    }

    set maxDecimal(maxDecimal) {
        this.setAttribute('maxDecimal', maxDecimal);
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (CONSTANT.VALUE === name) {
            let input = this.getElement(this.INPUT);
            if (newValue && 'undefined' !== newValue && input 
                && !isNaN(newValue) && !newValue.includes(','))
                input.value = this.onBlur2(newValue);
            if (input && newValue === '') input.value = '';
            let desc = this.getElement(this.TITLE);
            // if(desc && input.value.length > 0) {
            //   desc.classList.add(CSS.AON_INPUT_NOT_EMPTY);
            // } else if(desc) desc.classList.remove(CSS.AON_INPUT_NOT_EMPTY);
        }
        else if (CONSTANT.DISABLED === name) {
            let input = this.getElement(this.INPUT);
            if(input) {
                if(this.isDisabled())
                    input.setAttribute(CONSTANT.DISABLED, this.isDisabled());
                else 
                    input.removeAttribute(CONSTANT.DISABLED);
            }
        } else if (CONSTANT.READONLY === name && this.getElement(this.INPUT)) {
            if (this.isReadonly())
                this.getElement(this.INPUT).setAttribute(CONSTANT.READONLY, this.isReadonly());
            else this.getElement(this.INPUT).removeAttribute(CONSTANT.READONLY);
        } else if (CONSTANT.VISIBLE === name) {
            let label = this.getElement(this.getAttribute('id') + 'Label');
            if (label) {
                label.style.display = this.isVisible() ? 'block' : 'none';
            }
        } else if (CONSTANT.DESCRIPTION === name && this.getElement(this.DESCRIPTION)) {
            this.getElement(this.DESCRIPTION).innerHTML = newValue;
        }
    }

    constructor() {
        super();
    }

    connectedCallback() {
        this.initialize();
        this.initializeNumber();
        this.build();
        this.buildNumber();
    }

    initializeNumber() {
        this.value = this.value ? this.onBlur2(this.value) : 0.0;
    }

    buildNumber() {
		let input = this.getElement(this.INPUT);
		input.value = this.value;
		input.style.textAlign = 'right';
		if (this.value) input.value = this.value;

		input.addEventListener(EVENT.KEYPRESS, (ev) => {
			let keyChar = String.fromCharCode(ev.which || ev.keyCode);
			let reg = new RegExp(/[^0-9]/g);
			if (this.format) reg = new RegExp(/[^0-9\.,]/g);
			if ('-' === keyChar) {
				if (input.value.includes('-')) {
					ev.preventDefault();
				}
			} else if (reg.test(keyChar)) ev.preventDefault();
			this.dispatchEvent(new Event(EVENT.KEYPRESS));
		});

		input.addEventListener(EVENT.FOCUS, ({ target }) => {
			let value = target.value;
			if (value) input.value = this.onFocus(value);
			input.select();
			this.dispatchEvent(new Event(EVENT.FOCUS));
		});

		input.addEventListener(EVENT.BLUR, ({ target }) => {
			let value = target.value;
			if (value) {
				let newValue = this.onBlur2(value);
				this.value = this.onFocus(newValue);
			}

			this.dispatchEvent(new Event(EVENT.BLUR));
		});

		input.addEventListener(EVENT.CHANGE, ({ target }) => {
			let value = target.value;
			if (value) {
				let newValue = this.onBlur2(value);
				this.value = this.onFocus(newValue);
			}
			if (input.value.includes('-') && '-' !== input.value.charAt(0)) {
				input.value = input.value.replace('-', '');
				this.value = input.value.replace('-', '');
			}

			this.dispatchEvent(new Event(EVENT.CHANGE_NUMBER));
		});
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
        this.getElement(this.ICON_LABEL).addEventListener(EVENT.CLICK, (event) => {
            event.preventDefault();
            fn();
        });
    }

    onChange(fn) {
        this.addEventListener(EVENT.CHANGE_NUMBER, fn);
    }

    onInput(fn) {
        let input = this.getElement(this.getAttribute('id') + 'Input');
        input.addEventListener(EVENT.INPUT, fn);
    }

    setAlign(align){
        let input = this.getElement(this.getAttribute('id') + 'Input');
        input.style.textAlign = align;
    }
    
    onBlur2(value) {
        let newValue = value;
        if (this.format && !value.includes(',')) {
            let decimals = this.decimals || 0;
            let minDecimal = this.minDecimal || decimals;
            let maxDecimal = this.maxDecimal || decimals;
            newValue = formatNumber(value, minDecimal, maxDecimal);
        }
        return newValue;
    }

    onFocus(value) {
        if(!value) return value;
        return value.replace(/\./g, "").replace(/\,/g, ".");
    }

    focus(){
        this.getElement(this.INPUT).focus();
    }
}
if(!window.customElements.get(TAG.AON_NEW_NUMBER)){
  window.customElements.define(TAG.AON_NEW_NUMBER, AonNewNumber);
}
