import { CONSTANT, TAG, EVENT, CSS, MSG } from "../../environments/environments";
import { AonElement } from "../../components/AonElement.js";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { AonSwitch } from "../../components/aon-switch.js";
import { AonInput } from "../../components/aon-input.js";
import { AonNewSelect } from "../../components/aon-new-select.js";
import { setAttributes } from "../../services/utilsComponents.js";
import { AonNewDate } from "../../components/aon-new-date.js";
import { serializeForm } from "../../services/utils.js";
import { AonDate } from "../../components/aon-date.js";

export class AonPaturpatSearch extends AonElement {

    formComponents = [];

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    set placeholder(placeholder) {
        this.setAttribute(CONSTANT.PLACEHOLDER, placeholder);
    }

    get placeholder() {
        return this.getAttribute(CONSTANT.PLACEHOLDER) || 'Buscar...';
    }

    constructor() {
        super();
    }

    connectedCallback() {
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || 'aonPaturpatSearch';
        this.OPTIONS = this.id + 'Options';
        this.INPUT = this.id + 'Input';
        this.ADVANCED_BUTTON = this.id + 'AdvancedButton';
    }

    build() {
        let div = this.createDiv();
        div.id = this.id + 'Div';
        div.style.margin = '10px';
        this.appendChild(div);

        let input = this.createElement(TAG.INPUT);
        input.type = 'text';
        input.id = this.INPUT;
        input.placeholder = this.placeholder;
        input.style.width = '100%';
        input.style.boxSizing = 'border-box';
        input.style.padding = '10px';
        input.style.fontSize = '16px';
        input.style.border = '1px solid #ccc';
        input.style.borderRadius = '10px';
        div.appendChild(input);
        input.addEventListener(EVENT.KEYUP, (event) => {
            input.dispatchEvent(new CustomEvent(EVENT.SEARCH_NEW, {
                detail: {
                    event,
                    search: input.value,
                    ...this.getValues()
                }
            }));
        });

        // El boton de filtro
        let advancedButton = new AonIconButton();
        advancedButton.id = this.ADVANCED_BUTTON;
        advancedButton.icon = 'page_info';
        advancedButton.style.position = 'absolute';
        advancedButton.style.right = '20px';
        advancedButton.style.marginTop = '5px';
        div.appendChild(advancedButton);

        let divOpts = this.createElement(TAG.DIV);
        divOpts.id = this.OPTIONS;
        divOpts.className = CSS.AON_INPUT_LIST_OPTIONS;
        divOpts.style.maxHeight = "none";
        divOpts.style.padding = "10px";
        divOpts.style.display = "none";
        divOpts.style.width = "auto";
        divOpts.style.backgroundColor = "white";
        divOpts.style.zIndex = "1000";
        divOpts.style.borderRadius = "10px";
        divOpts.style.left = "10px";
        divOpts.style.right = "10px";
        this.appendChild(divOpts);

        advancedButton.addEventListener(EVENT.CLICK, () => {
            if (divOpts.style.display === "none") {
                divOpts.style.display = "block";
                divOpts.style.visibility = "visible";
            } else {
                divOpts.style.display = "none";
                divOpts.style.visibility = "hidden";
            }
        });
    }


    buildOptionsFilter(options) {
        let divOpts = this.getElement(this.OPTIONS);
        divOpts.innerHTML = "";

        let input = this.getElement(this.INPUT);
        options.forEach((attributes) => {
            let el = this.getInput(attributes);
            if (el) divOpts.appendChild(el);
        });

        let buttonsPanel = this.createElement(TAG.DIV);
        buttonsPanel.style.display = "flex";
        buttonsPanel.style.justifyContent = "center";
        buttonsPanel.style.alignItems = "center";

        let reset = this.createElement(TAG.BUTTON);
        reset.textContent = "Limpiar";
        reset.classList.add(CSS.AON_BUTTON, CSS.AON_FLEX);
        reset.style.padding = "0.5rem 1rem";
        reset.style.margin = "9px auto 0 auto";
        reset.style.background = "transparent";
        reset.style.border = "1px solid #005f2c";
        reset.style.color = "black";
        reset.addEventListener(EVENT.CLICK, (event) => {
            this.clearValues();
            this.dispatchEvent(new CustomEvent(EVENT.SEARCH_NEW, {
                detail: {
                    event,
                    search: input.value,
                    ...this.getValues()
                }
            }));
            divOpts.style.display = "none";
            divOpts.style.visibility = "hidden";
        });
        buttonsPanel.appendChild(reset);

        let button = this.createElement(TAG.BUTTON);
        button.textContent = MSG.SEARCH;
        button.classList.add(CSS.AON_BUTTON, CSS.AON_FLEX);
        button.style.padding = "0.5rem 1rem";
        button.style.margin = "9px auto 0 auto";
        button.style.backgroundColor = "#005f2c";
        button.addEventListener(EVENT.CLICK, (event) => {
            this.dispatchEvent(new CustomEvent(EVENT.SEARCH_NEW, {
                detail: {
                    event,
                    search: input.value,
                    ...this.getValues()
                }
            }));
            divOpts.style.display = "none";
            divOpts.style.visibility = "hidden";
        });
        buttonsPanel.appendChild(button);

        divOpts.appendChild(buttonsPanel);
    }

    getInput(attributes) {
        let html = undefined;
        let component = undefined;

        switch (attributes.type) {
            case CONSTANT.CHECKBOX:
                component = new AonSwitch();
                html = setAttributes(component, attributes);
                break;
            case CONSTANT.TEXT:
                component = new AonInput();
                html = setAttributes(component, attributes);
                break;
            case CONSTANT.SELECT:
                component = new AonNewSelect();
                html = setAttributes(component, attributes);
                break;
            case CONSTANT.DATE:
                component = new AonNewDate();
                html = setAttributes(component, attributes);
                break;
            case CONSTANT.NEW_DATE:
                component = new AonNewDate();
                html = setAttributes(component, attributes);
                break;
            case CONSTANT.HTML_ELEMENT:
                component = attributes.element;
                html = setAttributes(component, { ...attributes, element: "" });
                break;
        }

        // Si hemos creado un componente, lo aniadimos al array
        if (component) {
            // Asi tenemos los componetes que se montan para limpiar el filtro o lo que se quiera
            this.formComponents.push(component);
        }

        return html;
    }


    dispatchEventSearch(value, event = undefined) {
        this.dispatchEvent(new CustomEvent(EVENT.SEARCH_NEW, {
            detail: {
                event,
                search: value,
                ...this.getValues()
            }
        }));

        this.buildBadge();
    }

    dispatchCleanEventSearch(value, event = undefined) {

        this.dispatchEvent(new CustomEvent(EVENT.SEARCH_NEW, {
            detail: {
                event,
                search: value,
                ...this.getValues()
            }
        }));
    }

    clearValues() {
        const names = this.getValues();
        for (let name in names) {
            let elem = this.querySelector(`[name=${name}]`);
            if (elem && elem.clear) {
                elem.clear();
            }
        }
    }

    getValues() {
        let divOpts = this.getElement(this.OPTIONS);
        return divOpts ? serializeForm(divOpts) : {};
    }
}
if (!window.customElements.get(TAG.AON_PATURPAT_SEARCH)) {
    window.customElements.define(TAG.AON_PATURPAT_SEARCH, AonPaturpatSearch);
}