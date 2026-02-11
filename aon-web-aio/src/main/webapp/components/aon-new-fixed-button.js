import { AonElement } from './AonElement.js';
import { CONSTANT } from '../environments/environments.js';

export class AonNewFixedButton extends AonElement {

    CONTENT;

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    constructor() {
        super();
    }

    connectedCallback() {
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || "newFixedButton";
        this.CONTENT = this.id + 'Content';
    }

    build() {
        const wrapper = document.createElement("button");
        wrapper.classList.add("newFixedButton");

        wrapper.innerHTML = `
            <span class="plus">+</span>
        `;

        this.appendChild(wrapper);

        // Reenvía el click hacia el exterior
        wrapper.addEventListener("click", (e) => {
            this.dispatchEvent(new Event("click"));
        });
    }
}

if (!window.customElements.get('aon-new-fixed-button')) {
    window.customElements.define('aon-new-fixed-button', AonNewFixedButton);
}
