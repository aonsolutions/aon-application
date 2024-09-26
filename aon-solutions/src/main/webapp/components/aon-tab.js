import { CONSTANT, EVENT, TAG } from "../environments/environments.js";
import { setDataset } from "../services/utilsComponents.js";
import { AonElement } from "./AonElement.js";

export class AonTab extends AonElement {

    DIV;
    SPAN;
    selected;
    options;

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }
    
    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }
    
    connectedCallback() {
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || 'aonTab';
        this.DIV = this.id + "Div";
        this.SPAN = this.id + "Span";
        this.selected = 0;
    }

    build() {
        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.style.height = '40px';
        div.style.marginLeft = '20px';
        div.style.marginRight = '20px'
        div.style.paddingTop = '15px';
        this.appendChild(div);
        if(!this.options) 
            this.options = [];

        this.options.forEach((option, i) => {
            this.printOption(option, i);
        })
    }

    printOption(option, i) {
        let span = this.createElement(TAG.SPAN);
        span.id = option.id || this.SPAN + i;
        span.innerHTML = option.title;
        span.style.padding = '10px';
        span.style.paddingBottom = '5px';
        span.style.cursor = 'pointer';
        if(option.dataset){
            setDataset(span, option.dataset);
        }
        span.addEventListener(EVENT.CLICK, () => {
            this.querySelectorAll(TAG.SPAN).forEach(sp => {
                sp.style.borderBottom = 'none';
            });
            this.selected = i;
            span.style.borderBottom = '2px solid #002469';
        });
        span.addEventListener(EVENT.CLICK, option.fn);
        if(this.selected === i) {
            span.style.borderBottom = '2px solid #002469';
        }
        this.getElement(this.DIV).appendChild(span);
        return span;
    }

    setOptions(options) {
        this.options = options;
    }

    addOption(option) {
        this.options.push(option);
        return this.printOption(option, this.options.length - 1);
    }

    setOptionsPrint(options){
        this.options = options;
        this.options.forEach((option, i) => {
            this.printOption(option, i);
        })
    }

    getTabByDatasetId(id){
        return document.querySelector(`[id*='${this.SPAN}'][data-id='${id}']`);
    }

}
if(!window.customElements.get('aon-tab')){
  window.customElements.define("aon-tab", AonTab);
}