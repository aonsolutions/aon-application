import { CONSTANT, EVENT, TAG } from "../environments/environments.js";
import { setDataset } from "../services/utilsComponents.js";
import { AonElement } from "./AonElement.js";

export class AonTab extends AonElement {
    tabsComponent;  // Donde metemos todo los tab
    TAB;            // Cada tab  
    tabSelected;
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
        const id      = this.id || `aonTab-${generateSimpleUUID()}`;
        this.id       = id;
        this.TAB      = this.id + "Div";
        this.selected = 0;
    }

    build() {
//        if (!this.options || this.options.length === 0) {
//            console.log("No hay opciones disponibles, en Tab.");
//            return;
//        }

        const tabs = this.createElement(TAG.DIV);
        tabs.classList.add('tabs');
        this.tabsComponent = tabs;
        this.appendChild(tabs);

		if(this.options)
	        this.options.forEach((option, i) => {
	            this.printOption(option, i);
	        })
    }

    printOption(option, i) {
        let tab       = this.createElement(TAG.DIV);
        tab.id        = option.id || this.TAB + i;
        tab.classList.add('tab');
        tab.innerHTML = option.title;
        if(option.dataset){
            setDataset(tab, option.dataset);
        }
        tab.addEventListener(EVENT.CLICK, () => {
            this.querySelectorAll(TAG.DIV).forEach(sp => {
                sp.classList.remove('tab-selected');
            });
            this.selected = i;
            tab.classList.add('tab-selected');
            this.tabSelected = tab;
        });
        tab.addEventListener(EVENT.CLICK, option.fn);
        if(this.selected === i) {
            tab.classList.add('tab-selected');
            this.tabSelected = tab;
        }

        // Animación de hover
            tab.addEventListener("mouseenter", () => {
                if (this.selected !== i) {
                    // Quitar del seleccionado
                    this.tabSelected.classList.remove("tab-selected");
                }
            });

            if(this.tabsComponent){
                this.tabsComponent.addEventListener("mouseleave", () => {
                // Poner en el seleccionado
                this.tabSelected.classList.add("tab-selected");
                });
            // FIN Animación de hover
            }

        if(this.tabsComponent){
            this.tabsComponent.appendChild(tab);
        }
        // return tab;
    }

    setOptions(options) {
        this.options = options;
    }

    addOption(option) {
        if(!this.options){
            this.options = [];
        } 
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
        return document.querySelector(`[id*='${this.TAB}'][data-id='${id}']`);
    }

}
if(!window.customElements.get('aon-tab')){
  window.customElements.define("aon-tab", AonTab);
}