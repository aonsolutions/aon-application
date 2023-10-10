import { AonElement } from "./AonElement.js";
import {CONSTANT, CSS, EVENT, TAG, MATERIAL_ICONS, COLORS, MSG} from '../environments/environments.js'
import '../css/aon-new-input.css';

export class AonNewInput extends AonElement {

    ROOT;
    BOX;
    LABEL;
    INPUT;
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
        input.addEventListener(EVENT.CHANGE, () => this.setValue(input.value));
        input.addEventListener(EVENT.BLUR, this.onBlur);

        input.placeholder = this.getTitle();
        label.appendChild(input);

        let span = this.createElement(TAG.SPAN);
        span.id = this.TITLE;
        let requiredText = this.isRequired() ? " *" : "";
        span.innerHTML = this.getTitle() + requiredText;
        label.appendChild(span);
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

    onBlur = () => {
        this.checkRequired();
        this.dispatchEvent(new Event(EVENT.BLUR));
    };

    checkRequired() {
        if(this.isRequired() && this.getValue().isEmpty()) {
            this.addError(this.getTitle() + " " + MSG.IS_REQUIRED);
        } else if(this.isRequired()) {
            this.removeError();
        }
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

    removeError() {
        let span = this.getElement(this.TITLE);
        span.classList.remove(CSS.AON_INPUT_BOX_LABEL_SPAN_ERROR);
        let input = this.getElement(this.INPUT);
        input.classList.remove(CSS.AON_INPUT_BOX_LABEL_INPUT_ERROR);

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


}
if(!window.customElements.get(TAG.AON_NEW_INPUT)){
    window.customElements.define(TAG.AON_NEW_INPUT, AonNewInput);
}
  