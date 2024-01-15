import { AonElement } from "./AonElement.js";
import {CONSTANT, CSS, EVENT, TAG, MATERIAL_ICONS, COLORS, MSG} from '../environments/environments.js'
import '../css/aon-new-input.css';
import { AonIconButton } from "./aon-icon-button.js";

export class AonNewInput extends AonElement {

    ROOT;
    BOX;
    LABEL;
    INPUT;
    ICON;
    ICON_BUTTON;
    TITLE;
    MSG;
    MSG_SPAN;
    
    
    get id() {
        return this.getAttribute(CONSTANT.ID);
    }
    
    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    get type() {
        return this.getAttribute(CONSTANT.TYPE);
    }
    
    set type(type) {
        this.setAttribute(CONSTANT.TYPE, type);
    }

    get name() {
        return this.getAttribute(CONSTANT.NAME);
    }

    set name(name) {
        this.setAttribute(CONSTANT.NAME, name);
    }

    get title() {
        return this.getAttribute(CONSTANT.TITLE);
    }
    
    set title(title) {
        this.setAttribute(CONSTANT.TITLE, title);
    }

    get value() {
        return this.getAttribute(CONSTANT.VALUE);
    }
    
    set value(value) {
        this.setAttribute(CONSTANT.VALUE, value);
    }

    get required() {
        return this.getAttribute(CONSTANT.REQUIRED);
    }
    
    set required(value) {
        this.setAttribute(CONSTANT.REQUIRED, value);
    }

    get readonly(){
        return this.getAttribute(CONSTANT.READONLY);
    } 

    set readonly(value) {
        this.setAttribute(CONSTANT.READONLY, value);
    }

    get disabled() {
        return this.getAttribute(CONSTANT.DISABLED);
    }

    set disabled(value) {
        this.setAttribute(CONSTANT.DISABLED, value);
    }

    get maxlength() {
        return this.getAttribute(CONSTANT.MAXLENGTH);
    }
    
    set maxlength(maxlength) {
       this.setAttribute(CONSTANT.MAXLENGTH, maxlength);
    }

    connectedCallback() {
        this.initialize();
        this.build();
    }

    initialize() {
        this.ROOT = this.id + CONSTANT.ROOT.initCap();
        this.BOX = this.id + CONSTANT.BOX.initCap();
        this.LABEL = this.id + CONSTANT.LABEL.initCap();
        this.INPUT = this.id + CONSTANT.INPUT.initCap();
        this.TITLE = this.id + CONSTANT.TITLE.initCap();
        this.MSG = this.id + CONSTANT.MSG.initCap();
        this.MSG_SPAN = this.id + CONSTANT.MSG.initCap() + CONSTANT.SPAN.initCap();
        this.ICON = this.id + CONSTANT.ICON.initCap();
        this.ICON_BUTTON = this.id + CONSTANT.ICON_BUTTON.initCap();
        this.value = this.value || CONSTANT.EMPTY;
        this.type = this.type || CONSTANT.TEXT;

    }

    build() {
        let rootDiv = this.createElement(TAG.DIV);
        rootDiv.id = this.ROOT;
        rootDiv.title = this.getTitle();
        this.appendChild(rootDiv);
    
        this.buildBox(rootDiv);
        this.buildMsg(rootDiv);
    }

    buildBox(parent) {
        let boxDiv = this.createElement(TAG.DIV);
        boxDiv.id = this.BOX;
        boxDiv.className = CSS.AON_INPUT_BOX;
        parent.appendChild(boxDiv);

        let label = this.createElement(TAG.LABEL);
        label.id = this.LABEL;
        label.className = CSS.AON_INPUT_BOX_LABEL;  
        boxDiv.appendChild(label);

        let input = this.createElement(TAG.INPUT);
        input.id = this.INPUT;
        input.value = this.getValue();
        input.type = this.getType();
        input.className = CSS.AON_NEW_INPUT;

        if (this.isDisabled()) input.setAttribute("disabled", "true");
        if (this.isReadonly())  input.setAttribute("readonly", "true");
    
        
        input.addEventListener(EVENT.CHANGE, () => this.setValue(input.value));
        input.addEventListener(EVENT.BLUR, this.onBlur);
        input.addEventListener(EVENT.INPUT, this.onInput);

        input.placeholder = this.getTitle();
        label.appendChild(input);
        if(this.maxlength) {
            input.setAttribute("maxlength", this.maxlength);
        }

        let span = this.createElement(TAG.SPAN);
        span.id = this.TITLE;
        let requiredText = this.isRequired() ? " *" : "";
        span.innerHTML = this.getTitle() + requiredText;
        label.appendChild(span);

        if(this.isPassword()) {
            this.addIcon(MATERIAL_ICONS.VISIBILITY, undefined,() => {
                const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
                this.getElement(this.ICON_BUTTON).icon =  type === 'password' ? 'visibility' : 'visibility_off';
                input.type = type;
            })
        }
    }

    buildMsg(parent) {
        let msgDiv = this.createElement(TAG.DIV)
        msgDiv.id = this.MSG;
        msgDiv.className = CSS.AON_INPUT_MSG
        parent.appendChild(msgDiv);
        msgDiv.style.display = 'none';
    }

    buildErrorMessage(message) {
        let div = this.getElement(this.MSG);
        this.clearElement(div);
        div.style.display = '';
        
        let span = this.createElement(TAG.SPAN);
        span.id = this.MSG_SPAN;
        span.className = CSS.AON_INPUT_MSG_ERROR;
        span.innerHTML = message;
        div.appendChild(span);
    }

    buildWarningMessage(message) {
        let div = this.getElement(this.MSG);
        this.clearElement(div);
        div.style.display = '';
        
        let span = this.createElement(TAG.SPAN);
        span.id = this.MSG_SPAN;
        span.className = CSS.AON_INPUT_MSG_WARNING;
        span.innerHTML = message;
        div.appendChild(span);
    }

    onBlur = () => {
        this.checkRequired();
        this.dispatchEvent(new Event(EVENT.BLUR));
    };

    onInput = () => {
        this.dispatchEvent(new Event(EVENT.INPUT));
    };

    checkRequired() {
        if(this.isRequired() && this.getValue().isEmpty()) {
            this.addError(this.getTitle() + " " + MSG.IS_REQUIRED);
        } else if(this.isRequired()) {
            this.removeError();
        }
    }


    addIcon(icon, color, fn) {
        let div = this.getElement(this.BOX);
        let iconLabel = this.getElement(this.ICON);
        if (!iconLabel) {
          iconLabel = this.createElement(TAG.LABEL);
          div.appendChild(iconLabel);
        }
        iconLabel.className = CSS.AON_INPUT_ICON_LABEL;
        iconLabel.style.top = '5px';
        iconLabel.id = this.ICON;
        iconLabel.setAttribute("for", this.INPUT);
        let aonIconButton = new AonIconButton();
        aonIconButton.id = this.ICON_BUTTON;
        aonIconButton.icon = icon;
        aonIconButton.noHover = "true";
        if(fn) aonIconButton.addEventListener(EVENT.CLICK, fn);
        iconLabel.appendChild(aonIconButton);
        
        if(color) iconLabel.color = color;
        this.getElement(this.INPUT).style.paddingRight = '40px';
    }

    addError(message) {
        let span = this.getElement(this.TITLE);
        span.classList.add(CSS.AON_INPUT_BOX_LABEL_SPAN_ERROR);
        let input = this.getElement(this.INPUT);
        input.classList.add(CSS.AON_INPUT_BOX_LABEL_INPUT_ERROR);
        
        if(message) {
            this.buildErrorMessage(message);
        }
    }

    addWarning(message) {
        let span = this.getElement(this.TITLE);
        span.classList.add(CSS.AON_INPUT_BOX_LABEL_SPAN_WARNING);
        let input = this.getElement(this.INPUT);
        input.classList.add(CSS.AON_INPUT_BOX_LABEL_INPUT_WARNING);
        
        if(message) {
            this.buildWarningMessage(message);
        }
    }

    removeError() {
        let span = this.getElement(this.TITLE);
        span.classList.remove(CSS.AON_INPUT_BOX_LABEL_SPAN_ERROR);
        let input = this.getElement(this.INPUT);
        input.classList.remove(CSS.AON_INPUT_BOX_LABEL_INPUT_ERROR);

        let div = this.getElement(this.MSG);
        this.clearElement(div);
        div.style.display = 'none';
    }

    removeWarning() {
        let span = this.getElement(this.TITLE);
        span.classList.remove(CSS.AON_INPUT_BOX_LABEL_SPAN_WARNING);
        let input = this.getElement(this.INPUT);
        input.classList.remove(CSS.AON_INPUT_BOX_LABEL_INPUT_WARNING);

        let div = this.getElement(this.MSG);
        this.clearElement(div);
        div.style.display = 'none';
    }

    getId() {
       return this.id; 
    }

    setId(id) {
        this.id = id;
    }

    getTitle() {
        return this.title;
    }

    setTitle(title) {
        this.title = title;
    }

    getType() {
        return this.type;
    }

    setType(type) {
        this.type = type;
    }

    isPassword() {
        return this.type === CONSTANT.PASSWORD;
    }

    getValue() {
        return this.value;
    }

    setValue(value) {
        this.value = value;
    }

    isRequired() {
        return this.required;
    }

    setRequired(required) {
        this.required = required;
    }

    isReadonly() {
        return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
            && 'false' !== this.getAttribute(CONSTANT.READONLY)
    }

    setReadonly(readonly) {
        this.setAttribute(CONSTANT.READONLY, readonly);
    }

    isDisabled() {
        return this.disabled;
    }

    setDisabled(disabled) {
        this.disabled = disabled;
    }
}
if(!window.customElements.get(TAG.AON_NEW_INPUT)){
    window.customElements.define(TAG.AON_NEW_INPUT, AonNewInput);
}
  