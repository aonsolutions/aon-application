import { AonElement } from "./AonElement.js";
import {CONSTANT, CSS, EVENT, TAG, MATERIAL_ICONS, COLORS, MSG} from '../environments/environments.js'
import '../css/aon-new-textarea.css';
import { AonIconButton } from "./aon-icon-button.js";

export class AonNewTextarea extends AonElement {

    ROOT;
    BOX;
    LABEL;
    TEXTAREA;
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
        this.TEXTAREA = this.id + CONSTANT.TEXTAREA.initCap();
        this.TITLE = this.id + CONSTANT.TITLE.initCap();
        this.MSG = this.id + CONSTANT.MSG.initCap();
        this.MSG_SPAN = this.id + CONSTANT.MSG.initCap() + CONSTANT.SPAN.initCap();
        this.ICON = this.id + CONSTANT.ICON.initCap();
        this.ICON_BUTTON = this.id + CONSTANT.ICON_BUTTON.initCap();
        this.value = this.value || CONSTANT.EMPTY;
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
        boxDiv.className = CSS.AON_TEXTAREA_BOX;
        parent.appendChild(boxDiv);

        let label = this.createElement(TAG.LABEL);
        label.id = this.LABEL;
        label.className = CSS.AON_TEXTAREA_BOX_LABEL;  
        boxDiv.appendChild(label);

        let textarea = this.createElement(TAG.TEXTAREA)
        textarea.id = this.TEXTAREA;
        textarea.className = CSS.AON_NEW_TEXTAREA;
        textarea.value = this.getValue();

        if (this.isDisabled()) textarea.setAttribute("disabled", "true");
        if (this.isReadonly())  textarea.setAttribute("readonly", "true");
    
        
        textarea.addEventListener(EVENT.CHANGE, () => this.setValue(textarea.value));
        textarea.addEventListener(EVENT.BLUR, this.onBlur);
        textarea.addEventListener(EVENT.INPUT, this.onInput);

        // textarea.placeholder = this.getTitle();
        label.appendChild(textarea);
        if(this.maxlength) {
            textarea.setAttribute("maxlength", this.maxlength);
        }

        let span = this.createElement(TAG.SPAN);
        span.id = this.TITLE;
        let requiredText = this.isRequired() ? " *" : "";
        span.innerHTML = this.getTitle() + requiredText;
        label.appendChild(span);
    }

    buildMsg(parent) {
        let msgDiv = this.createElement(TAG.DIV)
        msgDiv.id = this.MSG;
        msgDiv.className = CSS.AON_TEXTAREA_MSG
        parent.appendChild(msgDiv);
        msgDiv.style.display = 'none';
    }

    buildErrorMessage(message) {
        let div = this.getElement(this.MSG);
        this.clearElement(div);
        div.style.display = '';
        
        let span = this.createElement(TAG.SPAN);
        span.id = this.MSG_SPAN;
        span.className = CSS.AON_TEXTAREA_MSG_ERROR;
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
        iconLabel.setAttribute("for", this.TEXTAREA);
        let aonIconButton = new AonIconButton();
        aonIconButton.id = this.ICON_BUTTON;
        aonIconButton.icon = icon;
        aonIconButton.noHover = "true";
        if(fn) aonIconButton.addEventListener(EVENT.CLICK, fn);
        iconLabel.appendChild(aonIconButton);
        
        if(color) iconLabel.color = color;
        this.getElement(this.TEXTAREA).style.paddingRight = '40px';
    }

    addError(message) {
        let span = this.getElement(this.TITLE);
        span.classList.add(CSS.AON_TEXTAREA_BOX_LABEL_SPAN_ERROR);
        let textarea = this.getElement(this.TEXTAREA);
        textarea.classList.add(CSS.AON_TEXTAREA_BOX_LABEL_TEXTAREA_ERROR);
        
        if(message) {
            this.buildErrorMessage(message);
        }

    }

    removeError() {
        let span = this.getElement(this.TITLE);
        span.classList.remove(CSS.AON_TEXTAREA_BOX_LABEL_SPAN_ERROR);
        let textarea = this.getElement(this.TEXTAREA);
        textarea.classList.remove(CSS.AON_TEXTAREA_BOX_LABEL_TEXTAREA_ERROR);

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
if(!window.customElements.get(TAG.AON_NEW_TEXTAREA)){
    window.customElements.define(TAG.AON_NEW_TEXTAREA, AonNewTextarea);
}
  