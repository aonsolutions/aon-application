import { AonElement } from "./AonElement.js";

export class AonAutosizeTextarea extends AonElement {

    inputTitle;

    mainColor;
    detailColor;

    TITLE_BAR;
    TEXTAREA;

    _disabled;
    _title;
    _scrollLimit;

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
        return ["disabled", "title", "scroll-limit"];
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

    getValue() {
        return this.TEXTAREA.innerHTML;
    }
    constructor() {
        if(!window.customElements.get('aon-autosize-textarea')) {
            window.customElements.define("aon-editor", AonAutosizeTextarea);
        }
        super();

        this._scrollLimit = "100px";
        this.inputTitle = "" ;
        this.detailColor = "darkgray";
        this.mainColor = "transparent";
        this.build();
    }

    initialize() {
        this.id = this.id || Math.random().toString(36).substring(7);
        this.TEXTAREA.id = this.id + "Textarea";
        this.TITLE_BAR.id = this.id + "TitleBar";
    }
    connectedCallback() {
        this.initialize();
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
        } else if (name === "title") {
            if (oldValue != newValue) {
                this.inputTitle = newValue ? newValue : "";
                this.TITLE_BAR.innerHTML = this.inputTitle;
            }
        } else if (name === "scroll-limit") {
            if (!newValue) {
                this._scrollLimit = "100px";
            } else {
                this._scrollLimit = newValue ? newValue : "";
            }
            this.updateScrollLimit();
        }
    }

    build() {
        this.style.position = "relative";
        this.style.display = "flex";
        this.style.minHeight = "48px";
        this.style.flexWrap = "wrap";
        this.style.alignItems = "flex-end";
        this.style.flexDirection = "row";
        this.style.justifyContent = "center";
        this.style.backgroundColor = this.mainColor;
        this.style.borderBottom = "1px solid darkgray";
        this.style.width = "100px";
        this.style.cursor = "text";
        this.tabIndex = 0;

        this.createTitleBar();
        this.createTextArea();
        this.appendChild(this.TITLE_BAR);
        this.appendChild(this.TEXTAREA);
        
        this.addEventListeners();

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
        this.TEXTAREA.addEventListener("focusout", (ev) => {
            if (!this._disabled) {
                this.focusOutAction();
            }
        });

        this.addEventListener("mouseover", (ev) => {
            if (!this._disabled) {
                this.style.backgroundColor = "var(--aon-color-interaction-minus-two)"
            }
        });

        this.addEventListener("mouseout", (ev) => {
            if (!this._disabled) {
                this.style.backgroundColor = "transparent"
            }
        });
    }

    createTitleBar() {
        this.TITLE_BAR = document.createElement("div");
        this.TITLE_BAR.style.display = "flex";
        this.TITLE_BAR.style.width = "95%";
        this.TITLE_BAR.style.color = this.detailColor;
        this.TITLE_BAR.style.overflow = "hidden";
        this.TITLE_BAR.style.textOverflow = "ellipsis";
        this.TITLE_BAR.innerHTML = this.inputTitle;
        this.TITLE_BAR.tabIndex = 0;

        this.TITLE_BAR.style.position = "absolute";
        this.TITLE_BAR.style.top = "0px";
        
        this.TITLE_BAR.style.fontSize = "1em";
        this.TITLE_BAR.style.alignItems = "flex-start";
    }
    
    createTextArea() {
        this.TEXTAREA = document.createElement("div");
        this.TEXTAREA.contentEditable = true;
        this.TEXTAREA.style.padding = "0 0 .2em 0";
        this.TEXTAREA.style.bottom = "0";
        this.TEXTAREA.style.maxHeight = this._scrollLimit;
        this.TEXTAREA.style.overflowY = "scroll";
        this.TEXTAREA.style.width = "95%";
        this.TEXTAREA.style.outline = "0px solid transparent";
        this.TEXTAREA.style.textOverflow = "clip";
        this.TEXTAREA.style.wordBreak = "break-word";
        this.TEXTAREA.style.marginTop = "1.3em";
        this.TEXTAREA.style.overflowY = "hidden";
        this.TEXTAREA.tabIndex = 0;
        this.TEXTAREA.classList.add("aon-autosize-textarea-div");
        
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
        this.TEXTAREA.contentEditable = false;
        this.style.cursor = "not-allowed";
    }

    setEnabled() {
        this.style.backgroundColor = "transparent";
        this.TEXTAREA.contentEditable = true;
        this.style.cursor = "text";
    }

    updateScrollLimit() {
        let editableDiv = this.querySelector("div.aon-autosize-textarea-div");
        if (editableDiv) {
            editableDiv.style.maxHeight = this._scrollLimit;
        }
    }
    
}