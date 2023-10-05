import { AonElement } from "./AonElement.js";
import {CONSTANT, CSS, EVENT, TAG, MATERIAL_ICONS, COLORS} from '../environments/environments.js'
import '../css/aon-new-input.css';

export class AonNewInput extends AonElement {
    
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

    connectedCallback() {
        this.initialize();
        this.build();
    }

    initialize() {
        this.value = this.value || CONSTANT.EMPTY;
        this.type = this.type || CONSTANT.TEXT;
    }

    build() {
        let rootDiv = this.createElement(TAG.DIV);
        rootDiv.title = this.getTitle();
        this.appendChild(rootDiv);

        this.buildBox(rootDiv);
        this.buildMsg(rootDiv);

    }

    buildBox(parent) {
        let boxDiv = this.createElement(TAG.DIV);
        boxDiv.className = CSS.AON_INPUT_BOX;
        parent.appendChild(boxDiv);

        let label = this.createElement(TAG.LABEL);
        label.className = CSS.AON_INPUT_BOX_LABEL;  
        boxDiv.appendChild(label);

        let input = this.createElement(TAG.INPUT);
        input.value = this.getValue();
        input.type = this.getType();
        input.addEventListener(EVENT.CHANGE, () => this.setValue(input.value));

        input.placeholder = this.getTitle();
        label.appendChild(input);

        let span = this.createElement(TAG.SPAN);
        span.innerHTML = this.getTitle();
        label.appendChild(span);
    }

    buildMsg(parent) {
        let msgDiv = this.createElement(TAG.DIV)
        msgDiv.className = CSS.AON_INPUT_MSG
        parent.appendChild(msgDiv);
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
}
if(!window.customElements.get(TAG.AON_NEW_INPUT)){
    window.customElements.define(TAG.AON_NEW_INPUT, AonNewInput);
  }
  