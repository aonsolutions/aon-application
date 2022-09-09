import { CONSTANT, CSS, EVENT, TAG } from "../environments/environments.js";
import { AonElement } from "./AonElement.js";

import '../css/aon-autosize-textarea.css';

export class AonAutosizeTextarea extends AonElement {

    mainColor;
    detailColor;

    TITLE_BAR;
    TEXTAREA;

    _disabled;
    _title;
    _scrollLimit;

    OPTIONS;
    OPTIONS_UL;
    OPTIONS_LI;
    options;
    selected;
    

    /**
     * @param {string} limit
     */
    set scrollLimit(limit) {
        this._scrollLimit = limit;
        this.setAttribute("scroll-limit", limit);
    }

    get scrollLimit() {
        this.getAttribute("scroll-limit");
    }

    static get observedAttributes() {
        return ["disabled", "title", "scroll-limit", CONSTANT.NAME, CONSTANT.VALUE];
    }    

    get title() {
        return this.getAttribute(CONSTANT.TITLE);
    }
    
    set title(title) {
        this.setAttribute(CONSTANT.TITLE, title);
    }
    
    set value(value) {
        this.setAttribute(CONSTANT.VALUE, value);
    }

    get value() {
        return this.getAttribute(CONSTANT.VALUE);
    }

    set name(name) {
        this.setAttribute(CONSTANT.NAME, name)
    }

    get name() {
        return this.getAttribute(CONSTANT.NAME);
    }

    getValue() {
        return this.value;
    }

    set disabled(disabled) {
        this._disabled = disabled;
        this.setAttribute("disabled", disabled);
    }

    get disabled() {
        return this.getAttribute("disabled");
    }

    get DETAIL_COLOR_ACTIVE() {
        return "var(--aon-color-interaction)";
    }

    get DETAIL_COLOR_NOT_ACTIVE() {
        return "var(--aon-color-ink-medium-contrast)";
    }

    constructor() {
        super();

    }

    connectedCallback() {
        this.initialize();
        this.build();
    }
    
    initialize() {
        this._scrollLimit = this._scrollLimit ? this._scrollLimit  : "100px";
        this.detailColor = "darkgray";
        this.mainColor = "transparent";
        
        this.id = this.id || Math.random().toString(36).substring(7);
        this.TEXTAREA_ID = this.id + "Textarea";
        this.TITLE_BAR_ID = this.id + "TitleBar";

        this.OPTIONS_ID = this.id + 'Options';
        this.OPTIONS_UL = this.OPTIONS_ID + 'Ul';
        this.OPTIONS_LI = this.OPTIONS_ID + 'Li';
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (name === "disabled") {
            if (["true", ""].includes(newValue)) {
                this._disabled = true;
                this.setDisabled();
            } else {
                this._disabled = false;
                this.setEnabled();
            }
        } else if (CONSTANT.TITLE === name && this.TITLE_BAR) {
            this.TITLE_BAR.innerHTML = newValue || '';
        } else if (name === 'scroll-limit') {
            if (!newValue) {
                this._scrollLimit = '100px';
            } else {
                this._scrollLimit = newValue || '';
            }
            this.updateScrollLimit();
        } else if (CONSTANT.NAME === name) {
            if (this.TEXTAREA) {
                this.name = newValue;
                this.TEXTAREA.name = newValue;
            }
        } else if (CONSTANT.VALUE === name) {
            if (this.TEXTAREA) {
                this.TEXTAREA.value = newValue;
                this.autoAdjustTextarea();
            }
        }
    }

    build() {
        this.className = CSS.AON_AUTOSIZE_TEXTAREA;
        this.tabIndex = 0;

        this.createTitleBar();
        this.createTextArea();
        this.createOptions();
        this.appendChild(this.TITLE_BAR);
        this.appendChild(this.TEXTAREA);
        this.appendChild(this.OPTIONS);

        this.autoAdjustTextarea();
 
        this.addEventListeners();
    }

    autoAdjustTextarea() {
        this.TEXTAREA.style.height = "auto";
        this.TEXTAREA.style.height = this.TEXTAREA.scrollHeight + "px";
    }

    addEventListeners() {
        [this, this.TITLE_BAR].forEach((element) => {
            element.addEventListener("focus", (ev) => {
                if (!this._disabled) {
                    this.TEXTAREA.style.display = "block";
                    this.TEXTAREA.focus();
                }
            }); 
        });

        this.TEXTAREA.addEventListener("focus", (ev) => {
            if (!this._disabled) {
                this.focusAction();
            }
        });

        this.TEXTAREA.addEventListener(EVENT.BLUR, () => {
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });

        this.TEXTAREA.addEventListener("focusout", () => {
            if (!this._disabled) {
                this.focusOutAction();
            }
        });

        this.addEventListener("mouseover", () => {
            if (!this._disabled) {
                this.style.backgroundColor = "var(--aon-color-interaction-minus-two)"
            }
        });

        this.addEventListener("mouseout", () => {
            if (!this._disabled) {
                this.style.backgroundColor = "transparent"
            }
        });

        this.TEXTAREA.addEventListener(EVENT.KEYUP, (e) => this.onkeyupTextarea(e));
    }

    createTitleBar() {
        this.TITLE_BAR = this.createElement(TAG.DIV);
        this.TITLE_BAR.id = this.TITLE_BAR_ID;
        this.TITLE_BAR.className = CSS.AON_AUTOSIZE_TEXTAREA_TITLE;
        this.TITLE_BAR.innerHTML = this.title;
        this.TITLE_BAR.tabIndex = 0;
    }
    
    createTextArea() {
        let lineHeight = "1.3em";
        this.TEXTAREA = this.createElement("textarea");
        if (this.name) {
            this.TEXTAREA.name = this.name;
        }
        this.TEXTAREA.id = this.TEXTAREA_ID;
        this.TEXTAREA.rows = 1;
        this.TEXTAREA.style.display = "block";
        this.TEXTAREA.style.backgroundColor = "transparent";
        this.TEXTAREA.style.border = "none";
        this.TEXTAREA.style.overflowY = "scroll";
        this.TEXTAREA.style.maxHeight = this._scrollLimit;
        // this.TEXTAREA.style.height = lineHeight;
        this.TEXTAREA.style.resize = "none";
        // this.TEXTAREA.contentEditable = true;
        this.TEXTAREA.value = this.value;
        this.TEXTAREA.className = CSS.AON_AUTOSIZE_TEXTAREA_CONTENT;
        this.TEXTAREA.tabIndex = 0;

        this.TEXTAREA.addEventListener("input", ({target}) => {
            console.log("target.scrollHeight", target.scrollHeight);
            target.style.height = "auto";
            target.style.height = target.scrollHeight + "px";

            // ev.preventDefault();
            // let rowNum = this.TEXTAREA.rows;
            // // console.log(this.TEXTAREA.value.split(/\n/g));
            // if (rowNum > 0) {
            //     this.TEXTAREA.style.height = lineHeight * rowNum;
            // }
        });
    }

    createOptions() {
        this.OPTIONS = this.createElement(TAG.DIV);
        this.OPTIONS.id = this.OPTIONS_ID;
        this.OPTIONS.className = CSS.AON_INPUT_LIST_OPTIONS;
        this.OPTIONS.style.top = "48px";
    }

    changeDetailColor(color) {
        this.detailColor = color;
        this.TITLE_BAR.style.color = color;
        this.style.borderBottomColor = color;
    }

    focusAction() {
        this.TITLE_BAR.style.transition = "font-size 0.1s ease-in";
        this.style.transition = "border-bottom 0.1s ease-out";
        this.changeDetailColor(this.DETAIL_COLOR_ACTIVE);
        this.style.borderBottomWidth = "2.5px";
    }

    focusOutAction() {
        this.TITLE_BAR.style.transition = "font-size 0.1s ease-out";
        this.style.transition = "border-bottom 0.1s ease-in";
        this.changeDetailColor(this.DETAIL_COLOR_NOT_ACTIVE);
        this.style.borderBottomWidth = "1px";
    }

    setDisabled() {
        this.changeDetailColor(this.DETAIL_COLOR_NOT_ACTIVE);
        let disabledColor = "var(--aon-color-bg-low-contrast)";
        this.style.backgroundColor = disabledColor;
        this.style.color = this.DETAIL_COLOR_NOT_ACTIVE;
        this.TEXTAREA.disabled = true;
        this.style.cursor = "not-allowed";
        this.TEXTAREA.style.cursor = "not-allowed";
    }

    setEnabled() {
        this.style.backgroundColor = "transparent";
        this.TEXTAREA.disabled = false;
        this.TEXTAREA.style.cursor = "text";
        this.style.cursor = "text";
    }

    updateScrollLimit() {
        // let textArea = this.getElement(this.TEXTAREA_ID);
        if (this.TEXTAREA) {
            this.TEXTAREA.style.maxHeight = this._scrollLimit;
        }
    }

    onkeyupTextarea(e) {
        this.value = this.TEXTAREA.value;
        if(e.key || e.keyCode) {
          if (e.keyCode == '38' || e.key == 'ArrowUp') {
            // up arrow
            let li = this.getElement(this.OPTIONS_LI + this.selected);
            if(li) li.style.backgroundColor = 'transparent';
            if(this.selected > -1){
              this.selected = this.selected - 1;
              let li2 = this.getElement(this.OPTIONS_LI + this.selected);
              if(li2) {
                  li2.style.backgroundColor = '#f1f1f1';
                  li2.scrollIntoView({block: "center", behavior: "smooth"});
                }
           }
         }
         else if (e.keyCode == '40' || e.key == 'ArrowDown') {
           // down arrow
           let li = this.getElement(this.OPTIONS_LI + this.selected);
           if(li) li.style.backgroundColor = 'transparent';
            if(this.selected < this.options.length) {
                 this.selected = this.selected + 1;
                let li2 = this.getElement(this.OPTIONS_LI + this.selected);
                if(li2) {
                    li2.style.backgroundColor = '#f1f1f1';
                    li2.scrollIntoView({block: "center", behavior: "smooth"});
                }
            }
         } else if (e.keyCode == '13' || e.key == 'Enter') {
           // enter
            if(this.selected > -1 && this.selected < this.options.length) {
                this.value = this.options[this.selected].name;
                this.TEXTAREA.innerHTML = this.options[this.selected].name;
                this.dispatchEvent(new CustomEvent(EVENT.SELECT, { detail: this.options[this.selected] }));
                this.closeOptions();
            }  
         } else {
           this.dispatchEvent(new Event(EVENT.AON_KEYUP));
         }
       }
    }

    closeOptions() {
        if (this.OPTIONS.classList.contains('is-visible')) {
          this.OPTIONS.classList.remove('is-visible');
        }
      }

    buildOptions(options) {
        this.clearElement(this.OPTIONS);
        this.options = options;
        this.selected = -1;
        if(options && options.length > 0) {
            this.OPTIONS.classList.add('is-visible');
            let ul = this.createElement(TAG.UL);
            ul.id = this.OPTIONS_UL;
            ul.classList.add(CSS.AON_UL);
            ul.classList.add(CSS.AON_INPUT_LIST_OPTIONS_UL);
            ul.setAttribute('for', this.getAttribute('id') + 'Icon');
            for (let i = 0; i < options.length; i++) {
                let li = this.createElement('li');
                li.id = this.OPTIONS_LI + i;
                li.className = 'aonInputListOptionsItem'
                li.innerHTML = options[i].name;
                li.addEventListener('click', (e) => {
                    this.OPTIONS.classList.remove('is-visible');
                    this.value = options[i].name;
                    this.TEXTAREA.innerHTML = this.value;
                    this.dispatchEvent(new CustomEvent('select', { detail: options[i] }));
                });
                ul.appendChild(li);
            }
            this.OPTIONS.appendChild(ul);
            document.addEventListener(EVENT.CLICK, (event) => this.clickOutOption(event));
        } else this.closeOptions();
    }

    clickOutOption(event) {
        let isClickInside =  this.OPTIONS.contains(event.target);
        if (!isClickInside) {
            this.closeOptions();
        }
    }
}

if(!window.customElements.get(TAG.AON_AUTOSIZE_TEXTAREA)) {
    window.customElements.define(TAG.AON_AUTOSIZE_TEXTAREA, AonAutosizeTextarea);
}