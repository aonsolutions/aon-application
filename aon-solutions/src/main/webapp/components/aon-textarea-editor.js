import { AonElement } from "./AonElement.js";
import { getReader } from '../services/utils.js';
import { openFileUrl } from '../services/fileService.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG} from '../environments/environments.js';
import { KEYDOWN } from "../environments/aonEvent.js";
import '../css/aon-textarea-editor.css';

export class AonTextareaEditor extends AonElement {

    #windowListeners;

    textBox;
    bar;
    writtenText;

    textBoxEnvelope;

    textBoxEnvelopeId;
    textBoxId;
    barId;

    #disabledBarLayer;

    ELEMENTS;

    #elementFilter;

    FILES;

    elementMarginX;
    elementMarginY;
    elementHeight;

    #textAreaBackground;
    #textAreaHoverBackground;
    #defaultValue;
    #disabled;
    #barPosition;
    #barIntegrated;

    #textBoxHeight;
    #textBoxMaxHeight;
    #textBoxMinHeight;

    #htmlMode;

    #extraElements
    #timeoutResize;

    #selectionRange;

    static get observedAttributes() {
        return ['text-area-background', 'text-area-hover-background', 'bar-background', 'placeholder', 'disabled', 'bar-position', 'bar-integrated', 'text-box-height', 'text-box-min-height', 'text-box-max-height','id'];
    }

    static get BAR_POSITIONS() {
        return {
            top: "top",
            bottom: "bottom"
        };
    }

    get DEFAULT_ELEMENTS_FILTER() {
        return {
            undo: true,
            redo: true,
            font: true,
            fontSize: true,
            bold: true,
            italic: true,
            underline: true,
            color: true,
            backgroundColor: true,
            alignment: true,
            orderedList: true,
            unorderedList: true,
            indent: true,
            outdent: true,
            removeFormat: true,
            strikethrough: true,
            quote: true,
            hyperlink: true,
            attachment: true,
            editorMode: true
        };
    }

    set elementFilter(ef) {
        this.#elementFilter = ef;
        if (this.bar) {
            this.clearElement(this.bar);
            this.appendElements(this.bar);
            this.bar.appendChild(this.#disabledBarLayer);
        }
    }

    get elementFilter() {
        return this.#elementFilter;
    }

    set textBoxHeight(textBoxHeight) {
        this.setAttribute('text-box-height', textBoxHeight);
    }

    get textBoxHeight() {
        return this.#textBoxHeight;
    }

    set textBoxMaxHeight(textBoxMaxHeight) {
        this.setAttribute('text-box-max-height', textBoxMaxHeight);
    }

    get textBoxMaxHeight() {
        return this.#textBoxMaxHeight;
    }

    set textBoxMinHeight(textBoxMinHeight) {
        this.setAttribute('text-box-min-height', textBoxMinHeight);
    }

    get textBoxMaxHeight() {
        return this.#textBoxMinHeight;
    }

    set textAreaBackground(textAreaBackground) {
        if (this.textBox) {
            this.#textAreaBackground = textAreaBackground;
            this.textBox.style.backgroundColor = this.#textAreaBackground;
        }
    }

    get textAreaBackground() {
            return this.#textAreaBackground;
    }

    /**
     * @param {string} textAreaHoverBackground
     */
    set textAreaHoverBackground(textAreaHoverBackground) {
        this.#textAreaHoverBackground = textAreaHoverBackground;
    }

    get textAreaHoverBackground() {
        return this.#textAreaHoverBackground;
    }

    set barBackground(barBackground) {
        if (this.bar) {
            this.bar.style.backgroundColor = barBackground;
        }
    }

    get barBackground() {
        if (this.bar) {
            return this.bar.style.backgroundColor;
        }
        return "";
    }



    /**
     * @param {string} barPosition
     */
    set barPosition(barPosition) {
        this.setAttribute("bar-position", barPosition);
    }

    get barPosition() {
        return this.#barPosition;
    }

    /**
     * @param {boolean} barIntegrated
     */
    set barIntegrated(barIntegrated) {
        this.setAttribute("bar-integrated", barIntegrated);
    }

    get barIntegrated() {
        return this.#barIntegrated;
    }

    /**
     * @param {boolean} disabled
     */
    set disabled(disabled) {
        this.setAttribute("disabled", disabled);
    }

    set id(id) {
        this.setAttribute("id", id);
    }

    get id() {
        return this.getAttribute("id");
    }

    set placeholder(placeholder) {
        this.setAttribute("placeholder", placeholder);
    }

    get placeholder() {
        return this.getAttribute("placeholder");
    }

    set value(value) {
        this.#defaultValue = value;
        if (this.textBox) {
            this.textBox.innerHTML = value;
        }
    }

    get value() {
        if (this.textBox) {
            return this.#htmlMode ? this.textBox.innerText : this.textBox.innerHTML;
        }
        return "";
    }

    constructor() {
        super();
        this.#windowListeners = [];
        this.FILES = [];
        this.elementMarginX = "3px";
        this.elementMarginY= "4px";
        this.style.display = "block";

        this.#textAreaBackground = "var(--aon-color-bg-low-contrast)";
        this.#textAreaHoverBackground = "var(--aon-color-interaction-minus-two)";
        this.#barPosition = this.constructor.BAR_POSITIONS.bottom;
        this.#barIntegrated = false;
        this.#disabled = false;

    }


    setEnabled(enabled) {
        if (!enabled) {
            if (this.textBox) {
                this.textBox.contentEditable = false;
            }
            if (this.bar) {
                this.bar.style.overflow = "hidden";
            }
            if (this.bar && this.#disabledBarLayer) {
                this.#disabledBarLayer.style.display = "block"
            }
        } else {
            if (this.textBox) {
                this.textBox.contentEditable = true;
            }
            if (this.bar) {
                this.bar.style.overflow = "";
            }
            if (this.#disabledBarLayer) {
                this.#disabledBarLayer.style.display = "none";
            }
        }
    }
    

    attributeChangedCallback(name, oldValue, newValue) {
        switch (name) {
            case 'text-area-background':
                this.textAreaBackground = newValue;
                break;
            case 'text-area-hover-background':
                this.textAreaHoverBackground = newValue;
                break;
            case 'bar-background':
                this.barBackground = newValue;
            case 'bar-position':
                this.#barPosition = newValue;
                if (this.bar) {
                    this.moveBar();
                }
                break;
            case 'bar-integrated':
                this.#barIntegrated = (newValue === "true");
                if (this.bar) {
                    if (this.#barIntegrated === true) {
                        this.integrateBar();
                    } else {
                        this.disintegrateBar();
                    }
                }
                break;
            case 'disabled':
                let dsbl = !(newValue === "false");
                this.#disabled = dsbl;
                this.setEnabled(!this.#disabled);
                break;
            case 'id':
                this.textBoxId = `${newValue}TextBox`;
                this.textBoxEnvelopeId = `${newValue}TextBoxEnvelope`;
                this.barId = `${newValue}Bar`;
                if (this.textBox) {
                    this.textBox.id = this.textBoxId;
                }
                if (this.textBoxEnvelope) {
                    this.textBoxEnvelope.id = this.textBoxEnvelopeId;
                }
                if (this.bar) {
                    this.bar.id = this.barId;
                }
                break;
            case 'placeholder':
                if (this.textBox) {
                    this.textBox.setAttribute("placeholder", this.placeholder);
                }
                break;
            case 'text-box-height':
                this.#textBoxHeight = newValue;
                if (this.textBoxEnvelope) {
                    this.textBoxEnvelope.style.height = this.#textBoxHeight;
                }
                break;
            case 'text-box-max-height':
                this.#textBoxMaxHeight = newValue;
                if (this.textBoxEnvelope) {
                    this.textBoxEnvelope.style.maxHeight = this.#textBoxMaxHeight;
                }
                break;
            case 'text-box-min-height':
                this.#textBoxMinHeight = newValue;
                if (this.textBoxEnvelope) {
                    this.textBoxEnvelope.style.minHeight = this.#textBoxMinHeight;
                }
                break;

            default:
                break;
        }
    }

    connectedCallback() {
        if (!this.elementFilter) {
            this.elementFilter = this.DEFAULT_ELEMENTS_FILTER;
        }
        this.drawElement();
        if (!this.id) {
            this.id = Math.random().toString(36).substring(7);
        }
        setTimeout(() => {
            this.resizeBar();
        }, 50);
    }

    resizeBar() {

        let remaining = this.bar.querySelector(".additionalElements");
        if (remaining) {
            if (this.#extraElements) {
                let last = this.bar.lastChild;
                Array.from(this.#extraElements).forEach(elem => this.bar.appendChild(elem));
                this.bar.appendChild(last);
            }
            remaining.remove();
        }

        let children = Array.from(this.bar.children);
        
        if (children.length > 1) {
            let firstChild = children[0];
            
            let rect = firstChild.getBoundingClientRect();
            let firstY = rect.bottom;
            for (let i=1; i<children.length - 1; i++) {
                let element = children[i];
                rect = element.getBoundingClientRect();
                let currentY = rect.bottom;
                if (Math.abs(currentY - firstY) > 10) {
                    let firstAdditionalElementIndex = i - 1;
                    this.createAdditionalDropdown(firstAdditionalElementIndex);
                    break;
                }
            }
        }
    }

    disconnectedCallback() {
        if (this.#windowListeners) {
            for (const lsn of this.#windowListeners) {
                window.removeEventListener(lsn.event, lsn.listener);
            }
        }
    }

    hideBar() {
        if (this.bar) {
            this.bar.style.display = "none";
        }
    }
    
    showBar() {
        if (this.bar) {
            this.bar.style.display = "flex";
        }
    }

    integrateBar() {
        if (this.bar) {
            this.bar.style.maxWidth = "100%";
            this.bar.style.width = "100%";
            this.bar.style.margin = "0 auto 0 auto";
            this.bar.style.boxShadow = "none";
            this.bar.style.borderRadius = "none";
        }
        this.barDetails();
    }

    disintegrateBar() {
        if (this.bar) {
            this.bar.style.maxWidth = "90%";
            this.bar.style.width = "fit-content";
            this.bar.style.margin = 0;
            this.bar.style.boxShadow = "0 0 10px 0 rgba(0,0,0,0.2)";
            this.bar.style.borderRadius = "10px";
        }
        this.barDetails();
    }

    barDetails() {
        if (this.bar) {
            if (!this.#barIntegrated) {
                switch (this.#barPosition) {
                    case this.constructor.BAR_POSITIONS.top:
                        this.bar.style.margin = "0 auto .5em auto";
                        break;
                    case this.constructor.BAR_POSITIONS.bottom:
                        this.bar.style.margin = ".5em auto 0 auto";
                        break;
                    default:
                        this.bar.style.margin = ".5em auto 0 auto";
                        break;
                }
            } else {
                switch (this.#barPosition) {
                    case this.constructor.BAR_POSITIONS.top:
                        this.bar.style.borderRadius = "10px 10px 0 0";
                        break;
                        case this.constructor.BAR_POSITIONS.bottom:
                        this.bar.style.borderRadius = "0 0 10px 10px";
                        break;
                        default:
                        this.bar.style.borderRadius = "0 0 10px 10px";
                        break;
                }
            }
        }
    }

    moveBar() {
        if (this && this.bar && this.textBoxEnvelope) {
            switch (this.#barPosition) {
                case this.constructor.BAR_POSITIONS.top:
                    this.appendChild(this.textBoxEnvelope);
                    // this.bar.style.margin = "0 auto .5em auto";
                    break;
                case this.constructor.BAR_POSITIONS.bottom:
                    this.appendChild(this.bar);
                    // this.bar.style.margin = ".5em auto 0 auto";
                    break;
                default://Moved down by default
                    this.appendChild(this.bar);
                    // this.bar.style.margin = ".5em auto 0 auto";
                    break;
            }
            this.barDetails();
        }
    }

    formatDoc(sCmd, sValue) {
        console.log(this.#selectionRange);
        if (this.isApple) {
            document.designMode = "on";
        }
        this.textBox.focus();
        document.execCommand(sCmd, false, sValue);
        if (this.isApple) {
            document.designMode = "off";
        }
    }
    
    textBoxElement() {
        let box = document.createElement("div");
        box.classList.add("contentEditable");
        box.contentEditable = true;
        box.style.margin = 0;
        box.style.width = "100%";
        box.style.height = "100%";
        box.style.overflowY = "scroll";
        box.style.wordBreak = "break-word";
        // box.style.backgroundColor = this.#textAreaBackground;
        box.style.outline = "0px solid transparent";
        box.addEventListener(EVENT.KEYDOWN, (ev) => {
            let charCode = ev.keyCode || ev.which;
            if (charCode === 8 && box.innerHTML === "<br>") {
                ev.preventDefault();
                this.clearElement(box);
            }
        });
        box.addEventListener(EVENT.INPUT, ev => {
            ev.preventDefault();
            ev.stopPropagation();
            this.dispatchEvent(new CustomEvent(EVENT.INPUT));
        });

        ["focus", "mouseover"].forEach((eventType) => {
            box.addEventListener(eventType, (ev) => {
                if (!this.#disabled && this.#textAreaHoverBackground) {
                    this.textBoxEnvelope.style.backgroundColor = this.#textAreaHoverBackground;
                }
            });
        });

        ["focusout", "mouseout"].forEach((eventType) => {
            box.addEventListener(eventType, (ev) => {
                if (!this.#disabled && this.textAreaBackground && document.activeElement !== this.textBox) {
                    this.textBoxEnvelope.style.backgroundColor = this.#textAreaBackground;
                }
            });
        });

        box.setAttribute("placeholder", this.placeholder);
        if (this.#defaultValue) {
            box.innerHTML = this.#defaultValue;
        }

        return box;
    }

    hoverEfect(element) {
        if (element) {
            element.style.transition = "background-color 0.3s ease-in-out";
            element.addEventListener("mouseover", () => {
                if (!this.#disabled) {
                    element.style.backgroundColor = "lightgray";
                }
            });
            element.addEventListener("mouseout", () => {
                if (!this.#disabled) {
                    element.style.backgroundColor = "transparent";
                }
            });
        }
    }

    unselectable(element) {
        element.style.setProperty("-webkit-user-select", "none");
        element.style.setProperty("-moz-user-select", "none");
        element.style.setProperty("-ms-user-select", "none");
        element.style.userSelect = "none";
    }

    createCommandButton(materialIcon, options) {
        options = options || {};
        let defaultIconColor = "#444";
        let container = document.createElement("div");

        container.style.display = "flex";
        container.style.justifyContent = "center";
        container.style.alignItems = "center";
        container.style.width = "1.5em";
        container.style.height = "1.5em";
        container.style.color = options.color || defaultIconColor;
        // container.style.margin = `${this.elementMarginY} ${this.elementMarginX}`;
        container.style.borderRadius = "5px";
        container.style.cursor = "pointer";
        container.style.position = "relative";


        let containerI = document.createElement("i");
        containerI.classList.add("material-icons");
        containerI.innerHTML = materialIcon;
        containerI.style.width = "100%";
        // containerI.style.height = "100%";
        containerI.style.textAlign = "center";
        containerI.style.margin = "auto";
        containerI.style.fontSize = options.fontSize || "22px";
        container.appendChild(containerI);

        this.unselectable(container);

        this.hoverEfect(container);

        container.addEventListener("mousedown", (ev) => {
            ev.preventDefault();
            ev.stopPropagation();
        });

        return container;
    }

    undoElement() {
        let btn = this.createCommandButton("undo", {fontSize: "19px"});
        btn.title = "Deshacer";
        btn.addEventListener("click", () => {
            this.formatDoc('undo');
        });
        return btn;
    }

    redoElement() {
        let btn = this.createCommandButton("redo", {fontSize: "19px"});
        btn.title = "Restaurar";
        btn.addEventListener("click", () => {
            this.formatDoc('redo');
        });
        return btn;
    }

    setDocMode(bToSource) {
        let oContent;
        if (bToSource) {
          oContent = document.createTextNode(this.textBox.innerHTML);
          console.log(this.textBox);
          this.clearElement(this.textBox);
          this.textBox.innerHTML = "";
          this.textBox.appendChild(oContent);
        //   let oPre = document.createElement("pre");
        //   this.textBox.contentEditable = false;
        //   oPre.id = "sourceText";
        //   oPre.contentEditable = true;
        //   oPre.appendChild(oContent);
        //   this.textBox.appendChild(oPre);
        } else {
          if (document.getElementsByTagName('*')) {
            this.textBox.innerHTML = this.textBox.innerText;
          } else {
            oContent = document.createRange();
            oContent.selectNodeContents(this.textBox.firstChild);
            this.textBox.innerHTML = oContent.toString();
          }
          this.textBox.contentEditable = true;
        }
        setTimeout(() => {
            this.textBox.focus();
        }, 0);
      }

    fontSelector() {
        let selector = document.createElement("select");
        this.hoverEfect(selector);
        selector.style.border = "none";
        selector.style.cursor = "pointer";
        selector.style.borderRadius = "5px";
        selector.style.background = "none";
        selector.style.textOverflow = "ellipsis";
        selector.style.maxWidth = "8em";
        //FUENTES POSIBLES:    https://blog.hubspot.com/website/web-safe-html-css-fonts#what-web-safe-fonts
        [/*"Tipo fuente", */"Arial", "Arial Black", "Courier New", "Helvetica", "Times New Roman"].forEach(font => {
            let fontOption = document.createElement("option");
            fontOption.innerText = font;
            fontOption.style.fontFamily = font;
            selector.appendChild(fontOption);
        });
        selector.addEventListener("change", () => {
            if (selector[selector.selectedIndex].value !== "Tipo fuente") {
                this.formatDoc('fontname', selector[selector.selectedIndex].value);
            }
        });
        return selector;
    }

    dropdown(options, listener, iconChange) {
        let dropdown = document.createElement("div");
        dropdown.style.boxShadow = "0 0 10px 0 rgba(0,0,0,0.2)";
        dropdown.style.zIndex = "999";
        // dropdown.style.fontFamily = "Arial";
        dropdown.style.position = "absolute";

        dropdown.style.backgroundColor = "white";
        dropdown.style.borderRadius = "5px";
        Object.entries(options).forEach(([key, value]) => {
            let option = document.createElement("div");
            option.style.display = "flex";
            option.style.alignItems = "center";
            option.style.height = "32.5px";
            if (value.fontSize)
                option.style.fontSize = value.fontSize;
            option.style.padding = "0 10px";
            if (value.icon) {
                let icon = document.createElement("i");
                icon.classList.add("material-icons");
                icon.innerText = value.icon;
                option.appendChild(icon);
            }
            if (value.name) {
                let name = document.createElement("span");
                name.innerText = value.name;
                option.appendChild(name);
            }
            if (value.title) {
                option.title = value.title;
            }
            if (value.fontFamily) {
                option.style.fontFamily = value.fontFamily;
            }
            option.value = key;
            option.addEventListener("click", (ev) => {
                ev.stopPropagation();
                listener(ev);
                if (iconChange)
                    iconChange(dropdown.parentElement.firstChild, value.icon);
                dropdown.parentElement.removeChild(dropdown);
                dropdown.remove();
            });
            this.hoverEfect(option);
            dropdown.appendChild(option);
        });
        dropdown.style.bottom = "1.75em";
        return dropdown;
    }

    createAdditionalDropdown(firstAdditionalElementIndex) {


        let children = Array.from(this.bar.children);
        let lastChild = children[children.length - 1];

        this.#extraElements = children.slice(firstAdditionalElementIndex, children.length - 1);

        let dropdown = document.createElement("div");
        dropdown.style.boxShadow = "0 0 10px 0 rgba(0,0,0,0.2)";
        dropdown.style.zIndex = "999";
        // dropdown.style.fontFamily = "Arial";
        dropdown.style.position = "absolute";

// <<<<<<< Updated upstream
        dropdown.style.minWidth = "95px";
// =======
//         dropdown.style.minWidth = "70px";
//         dropdown.style.maxWidth = "80px";
// >>>>>>> Stashed changes
        dropdown.style.display = "flex";
        dropdown.style.flexWrap = "wrap";
        dropdown.style.justifyContent = "center";
        dropdown.style.rowGap = "2px";
        dropdown.style.padding = "2px 0";
        dropdown.style.maxHeight = "200px";
        dropdown.style.overflowY = "scroll";
        dropdown.style.overflowX = "hidden";
        dropdown.style.right = "0px";


        dropdown.style.backgroundColor = "white";
        dropdown.style.borderRadius = "5px";
        dropdown.classList.add("additionalElementsDropdown");



        dropdown.style.bottom = "1.75em";

        this.#extraElements.forEach(elem => dropdown.appendChild(elem));

        let additional = this.commonSelector("", dropdown);
        additional.classList.add("additionalElements");

        this.bar.appendChild(additional);
        this.bar.appendChild(lastChild);
        
    }

    fontFamilyDrop() {
        return this.dropdown({
            "Arial": {name: "Arial", fontFamily: "Arial"},
            "Arial Black": {name: "Arial Black", fontFamily: "Arial Black"},
            "Courier New": {name: "Courier New", fontFamily: "Courier New"},
            "Times New Roman": {name: "Times New Roman", fontFamily: "Times New Roman"}
        }, (ev) => {
            this.formatDoc('fontname',ev.currentTarget.value);
        });
    }

    fontSizeDrop() {
        return this.dropdown({
            2: {name: "Pequeño", fontSize: ".75em"},
            3: {name: "Normal", fontSize: "1em"},
            5: {name: "Grande", fontSize: "1.25em"},
            7: {name: "Enorme", fontSize: "1.5em"}
        }, (ev) => {
            this.formatDoc('fontsize',ev.currentTarget.value);
        });
    }

    commonSelector(materialIcon, dropdownElement, color) {
            let defaultIconColor = "#444";

            let selector = document.createElement("div");
            this.hoverEfect(selector);
            selector.style.display = "flex";
            selector.style.flexDirection = "row";
            selector.style.position = "relative";
            selector.style.color = color || defaultIconColor;
    
            selector.style.border = "none";
            selector.style.cursor = "pointer";
            selector.style.borderRadius = "5px";
            selector.style.background = "none";
            selector.style.textOverflow = "ellipsis";
            selector.style.height = "1.5em";
            // selector.style.margin = `${this.elementMarginY} ${this.elementMarginX}`;
            selector.classList.add("commonSelector");
    
            this.unselectable(selector);
            
            let icon = document.createElement("div");
            icon.classList.add("material-icons");
            icon.innerText = materialIcon;
            selector.appendChild(icon);
            let drop = document.createElement("div");
            drop.classList.add("material-icons");
            drop.innerText = "expand_more";
            selector.appendChild(drop);
            let options = dropdownElement;
            
            [icon, drop].forEach(el => el.style.fontSize = "20px");

            selector.addEventListener("mousedown", (ev) => {
                console.log("propagación evitada");
                ev.preventDefault();
                ev.stopPropagation();
            });

            selector.addEventListener("click", (ev) => {
                if (options.parentElement !== selector) {
                    selector.appendChild(options);
                    let selParent = selector.parentElement;
                    if (selParent.classList.contains("additionalElementsDropdown")) {

                    }
                } else {
                    let clickedEl = ev.target;

                    if (!(clickedEl.classList.contains("commonSelector") || (clickedEl.parentElement.classList.contains("commonSelector") && clickedEl.parentElement !== selector))) {
                        selector.removeChild(options);
                    }
                    
                }
            });

            let windowListener = (ev) => {
                if (selector && selector.contains(options)) {
                    if (ev.target != selector && !selector.contains(ev.target)) {
                        selector.removeChild(options);
                    }
                }
            };
    
            window.addEventListener("click", windowListener);
            this.#windowListeners.push({
                event: "click",
                listener: windowListener
            });
            let resizeListener = () => {
                clearTimeout(this.#timeoutResize);
                this.#timeoutResize = setTimeout(() => {
                    if (this.bar) {
                        this.resizeBar();
                    }
                }, 50);

            };
            window.addEventListener("resize", resizeListener);

            // new ResizeObserver(() => {
            //     clearTimeout(this.#timeoutResize);
            //     this.#timeoutResize = setTimeout(() => {
            //         if (this.bar) {
            //             console.log();
            //             this.bar.style.width = `${this.textBoxEnvelope.offsetWidth * (this.barIntegrated ? 1 : 0.9) - 10}px`;
            //         }
            //     }, 50);
            // }).observe(this.textBoxEnvelope)

            return selector;
    }


    fontSizeSelector() {
        return this.commonSelector("format_size", this.fontSizeDrop());
    }

    fontFamilySelector() {
        return this.commonSelector("font_download", this.fontFamilyDrop());
    }

    boldElement() {
        let btn = this.createCommandButton("format_bold");
        btn.title = "Negrita";
        btn.addEventListener("click", (ev) => {;
            this.formatDoc('bold');
        });
        return btn;
    }

    italicElement() {
        let btn = this.createCommandButton("format_italic");
        btn.title = "Cursiva";
        btn.addEventListener("click", () => {
            this.formatDoc('italic');
        });
        return btn;
    }
    
    underlineElement() {
        let btn = this.createCommandButton("format_underlined");
        btn.title = "Subrayado";
        btn.addEventListener("click", () => {
            this.formatDoc('underline');
        });
        return btn;
    }

    colorElement(material, title, colorListener) {
        let btn = this.createCommandButton(material, {fontSize: "19px"});
        btn.title = title;

        let inputColor = document.createElement("input");
        inputColor.type = "color";
        inputColor.style.position = "absolute";
        inputColor.style.visibility = "hidden";
        inputColor.addEventListener("change", (ev) => {
            colorListener(ev);
        });
        btn.appendChild(inputColor);

        btn.addEventListener("click", () => {
            inputColor.click();
        });

        return btn;
    }

    textColorElement() {
        return this.colorElement("format_color_text", "Color de texto", (ev) => {
            let inputColor = ev.currentTarget;
            this.formatDoc('forecolor', inputColor.value);
        });
    }

    backgroundColorElement() {
        return this.colorElement("format_color_fill", "Color de fondo", (ev) => {
            let inputColor = ev.currentTarget;
            this.formatDoc('backcolor', inputColor.value);
        });
    }

    alignmentDrop() {
        return this.dropdown({
            "justifyleft": {icon: "format_align_left", title: "Izquierda"},
            "justifycenter": {icon: "format_align_center", title: "Centrado"},
            "justifyright": {icon: "format_align_right", title: "Derecha"},
        }, (ev) => {
            this.formatDoc(ev.currentTarget.value);
        },
        (elem, icon) => {
            elem.innerHTML = icon;
        });
    }

    alignmentElement() {
        let elem = this.commonSelector("format_align_left", this.alignmentDrop());
        elem.title = "Alineación";
        return elem;
    }

    orderedListElement() {
        let btn = this.createCommandButton("format_list_numbered");
        btn.title = "Lista ordenada";
        btn.addEventListener("click", () => {
            this.formatDoc('insertorderedlist');
        });
        return btn;
    }

    unorderedListElement() {
        let btn = this.createCommandButton("format_list_bulleted");
        btn.title = "Lista no ordenada";
        btn.addEventListener("click", () => {
            this.formatDoc('insertunorderedlist');
        });
        return btn;
    }

    indentElement() {
        let btn = this.createCommandButton("format_indent_increase");
        btn.title = "Tabular";
        btn.addEventListener("click", () => {
            this.formatDoc('indent');
        });
        return btn;
    }
    
    outdentElement() {
        let btn = this.createCommandButton("format_indent_decrease");
        btn.title = "Recoger";
        btn.addEventListener("click", () => {
            this.formatDoc('outdent');
        });
        return btn;
    }
    
    removeFormatElement() {
        let btn = this.createCommandButton("format_strikethrough");
        btn.title = "Eliminar formato";
        btn.addEventListener("click", () => {
            this.formatDoc('removeFormat');
        });
        return btn;
    }

    strikethroughElement() {
        let btn = this.createCommandButton("strikethrough_s");
        btn.title = "Tachado";
        btn.addEventListener("click", () => {
            this.formatDoc('strikeThrough');
        });
        return btn;
    }

    editorModeElement() {
        let btn = this.createCommandButton("html");
        btn.title = "Modo del editor";
        btn.addEventListener("click", () => {
            this.setDocMode(this.#htmlMode = !this.#htmlMode);
            btn.firstChild.innerHTML = !this.#htmlMode ? "html" : "abc";
        });
        return btn;
    }

    
    blockquote = () =>{
        const selection = document.getSelection();
        const blockquoteEl = document.createElement("blockquote");
        blockquoteEl.style.margin = "0px 0px 0px 0.8ex";
        blockquoteEl.style.borderLeft = "1px solid #cccccc";
        blockquoteEl.style.paddingLeft = "1ex";
        
        blockquoteEl.textContent = selection;
        if (this.isApple) {
            document.designMode = "on"
        }
        document.execCommand('insertHTML', false, blockquoteEl.outerHTML);
        if (this.isApple) {
            document.designMode = "off"
        }
    }

    quoteElement() {
        let btn = this.createCommandButton("format_quote");
        btn.title = "Cita";
        btn.addEventListener("click", () => {
            this.blockquote();
            this.textBox.innerHTML += "<div><br/></div>"
            // this.formatDoc('formatblock','blockquote')
        });
        return btn;
    }

    hyperlinkElement() {
        let btn = this.createCommandButton("insert_link");
        btn.title = "Enlace";
        btn.addEventListener("click", () => {
            let sLnk=prompt('Escriba la URL','https:\/\/');
            if(sLnk&&sLnk!=''&&sLnk!='http://') {
                if (getSelection() && getSelection().toString()) {
                    this.formatDoc('createlink',sLnk)
                } else {
                    this.addLink(sLnk);
                }
            }
        });
        return btn;
    }

    addLink(url) {
        let element = document.createElement(TAG.A);
        element.target = "_blank";
        element.className = CSS.AON_LINK;
        element.href = url;
        element.textContent = url;
        element.title = url;
        this.textBox.appendChild(element);
    }

    attachmentElement() {
        let btn = this.createCommandButton("attach_file");
        btn.title = "Adjuntar";
        let inputFile = document.createElement("input");
        inputFile.type = "file";
        inputFile.style.display = "none";
        btn.appendChild(inputFile);
        btn.addEventListener("click", () => {
            inputFile.click();
        });
        inputFile.addEventListener("change", (ev) => {
            let files = inputFile.files;
            this.addFiles(files).then(() => {
                console.log(this.FILES);
            })
        });
        return btn;
    }


    //--------------------------- FILE UPLOAD STUFF (powered by Ray) --------------------------------
    
    	/**
	 * 
	 * @param {String} base64Str base64 file
	 * @param {String} contentType mimeType
	 * @returns {String} url
	 */
	convertBase64Url(base64Str, contentType) {
		let byteCharacters = atob(base64Str);
		let byteNumbers = new Array(byteCharacters.length);
		for (let i = 0; i < byteCharacters.length; i++) byteNumbers[i] = byteCharacters.charCodeAt(i);
		let file = new Blob([new Uint8Array(byteNumbers)], { type: `${contentType};base64` });
		return URL.createObjectURL(file);
	}

    getSelection() {
		let userSelection;
		if (window.getSelection) {
			userSelection = window.getSelection();
		} else if (document.selection) { // Opera
			userSelection = document.selection.createRange();
		}  
		return userSelection;
	} 

	getSelectionForAdd(){
		const textArea = this.textBox;
		const range = this.getSelection().getRangeAt(0);
		const selectedText = range.extractContents();

		if(range && range.toString()!=""){
			let div = document.createElement(TAG.DIV); 
			div.appendChild(selectedText);
			range.insertNode(div);
	
			const baseSelection = this.getSelection().baseNode;
	
			const inside = textArea.contains(baseSelection);
			if(inside) {//  inside
				return div; 
			}
		}

		return textArea;
	}

    async addFiles(files, parent=undefined) {
		for await (const file of files) {
			await this.addFile(file, parent);
		}
	}

	async addFile(file, parent=undefined) {
		let div = parent || this.getSelectionForAdd();

		const reader = await getReader(file).catch(()=>null);
		if(reader) {
			
			const fileId = Math.random().toString(36).substring(7);

			this.FILES.push({
				contentType: reader.contentType,
				content: reader.content,
				id:fileId
			});

			const url = this.convertBase64Url(reader.content, reader.contentType);
			let element = null;
			if(reader.contentType && reader.contentType.indexOf("image")>-1){
				element = document.createElement(TAG.IMG);
				element.src = url;
				element.className = CSS.AON_IMG_COMMENT;
			} else if(reader.contentType && reader.contentType.indexOf("mp4")>-1){
				element = document.createElement("video");
				element.controls = true;
				element.style.width = "100%";
				element.style.minHeight = element.style.maxHeight = "184px";
				const source = document.createElement("source");
				source.src = url;
				source.type = reader.contentType;
				element.appendChild(source);
			} else {
				element = document.createElement(TAG.A);
				element.target = "_blank";
				element.className = CSS.AON_LINK;
				element.href = url;
				element.textContent = reader.name;
			}
			element.dataset.id = fileId;
			element.setAttribute(CONSTANT.TYPE, CONSTANT.AON_FILE);
			element.addEventListener(EVENT.CLICK, ()=> openFileUrl(url));
			div.appendChild(element);
			div.appendChild(document.createElement(TAG.BR));

			this.dispatchEvent(new CustomEvent(EVENT.INPUT));
		}
	}

    checkFiles() {
        let filesIds = [...this.textBox.querySelectorAll(`[type='${CONSTANT.AON_FILE}']`)].map(el => el.dataset.id);
        this.FILES = this.FILES.filter(f => (filesIds || []).includes(f.id));
    }

    draggableEnable(){
		const divTextArea = this.textBox;
		divTextArea.classList.add("divDragOver");
		
		const highlight = ()   => {
			divTextArea.classList.add('highlight');
			divTextArea.setAttribute("placeholder", "");
		} 
		
		const unhighlight = () =>{
			divTextArea.classList.remove('highlight');
			divTextArea.setAttribute("placeholder", this.placeholder);
		}

		[EVENT.DRAGENTER, EVENT.DRAGOVER].forEach(eventName => divTextArea.addEventListener(eventName, highlight, false));
		[EVENT.DRAGLEAVE, EVENT.DROP].forEach(eventName => divTextArea.addEventListener(eventName, unhighlight, false));

	    divTextArea.addEventListener(EVENT.DROP, (ev) => {
			if(ev && ev.dataTransfer && ev.dataTransfer.files){
                ev.preventDefault();
				this.addFiles(ev.dataTransfer.files);
			}
		});
	}

    //-----------------------------------------------------------------------------------------------

    createElements() {
        this.ELEMENTS = {
            undoEl: this.undoElement(),
            redoEl: this.redoElement(),
            fontSel: this.fontSelector(),
            fontSizeSel: this.fontSizeSelector(),
            boldEl: this.boldElement(),
            italicEl: this.italicElement(),
            underlineEl: this.underlineElement(),
            textColorEl: this.textColorElement(),
            backgroundColorEl: this.backgroundColorElement(),
            alignmentEl: this.alignmentElement(),
            orderedListEl: this.orderedListElement(),
            unorderedListEl: this.unorderedListElement(),
            outdentEl: this.outdentElement(),
            indentEl: this.indentElement(),
            removeFormatEl: this.removeFormatElement(),
            strikethroughEl: this.strikethroughElement(),
            quoteEl: this.quoteElement(),
            hyperlinkEl: this.hyperlinkElement(),
            attachmentEl: this.attachmentElement(),
            editorModeEl: this.editorModeElement()
        };
    }

    appendElements(bar) {
        if (this.elementFilter.font) {
            bar.appendChild(this.ELEMENTS.fontSel);
        }
        if (this.elementFilter.fontSize) {
            bar.appendChild(this.ELEMENTS.fontSizeSel);
        }
        if (this.elementFilter.alignment) {
            bar.appendChild(this.ELEMENTS.alignmentEl);
        }
        if (this.elementFilter.undo) {
            bar.appendChild(this.ELEMENTS.undoEl);
        }

        if (this.elementFilter.redo) {
            bar.appendChild(this.ELEMENTS.redoEl);
        }
        if (this.elementFilter.bold) {
            bar.appendChild(this.ELEMENTS.boldEl);
        }
        if (this.elementFilter.italic) {
            bar.appendChild(this.ELEMENTS.italicEl);
        }
        if (this.elementFilter.underline) {
            bar.appendChild(this.ELEMENTS.underlineEl);
        }
        if (this.elementFilter.color) {
            bar.appendChild(this.ELEMENTS.textColorEl);
        }
        if (this.elementFilter.backgroundColor) {
            bar.appendChild(this.ELEMENTS.backgroundColorEl);
        }
        if (this.elementFilter.orderedList) {
            bar.appendChild(this.ELEMENTS.orderedListEl);
        }
        if (this.elementFilter.unorderedList) {
            bar.appendChild(this.ELEMENTS.unorderedListEl);
        }
        if (this.elementFilter.outdent) {
            bar.appendChild(this.ELEMENTS.outdentEl);
        }
        if (this.elementFilter.indent) {
            bar.appendChild(this.ELEMENTS.indentEl);
        }
        if (this.elementFilter.removeFormat) {
            bar.appendChild(this.ELEMENTS.removeFormatEl);
        }
        if (this.elementFilter.strikethrough) {
            bar.appendChild(this.ELEMENTS.strikethroughEl);
        }
        if (this.elementFilter.quote) {
            bar.appendChild(this.ELEMENTS.quoteEl);
        }
        if (this.elementFilter.hyperlink) {
            bar.appendChild(this.ELEMENTS.hyperlinkEl);
        }
        if (this.elementFilter.attachment) {
            bar.appendChild(this.ELEMENTS.attachmentEl);
        }
        if (this.elementFilter.editorMode) {
            bar.appendChild(this.ELEMENTS.editorModeEl);
        }
    }

    commandBar(elements) {
        
        let bar = document.createElement("div");
        bar.style.display = "flex";
        bar.style.flexDirection = "row";
        bar.style.flexWrap = "wrap";
        
        bar.style.justifyContent = "center";
        bar.style.minWidth = "30%";
        // bar.style.margin = "0 auto";
        
        bar.style.margin = ".5em auto 0 auto";
        bar.style.backgroundColor = "#f5f5f5";
        bar.style.boxShadow = "0 0 10px 0 rgba(0,0,0,0.2)";
        bar.style.borderRadius = "10px";
        bar.style.padding = "5px";
        bar.style.maxWidth = "90%";
        bar.style.width = "fit-content";
        bar.style.position = "relative";
        bar.style.overflow = this.#disabled ? "hidden" : "";
        bar.style.columnGap = "3px";
        
        this.unselectable(bar);
        
        this.createElements();
        this.appendElements(bar);
        
        this.#disabledBarLayer = document.createElement("div");
        this.#disabledBarLayer.style.display = !this.#disabled ? "none" : "block";
        this.#disabledBarLayer.style.position = "absolute";
        this.#disabledBarLayer.style.backgroundColor = "lightgray";
        this.#disabledBarLayer.style.width = "100%";
        this.#disabledBarLayer.style.height = "100%";
        this.#disabledBarLayer.style.bottom = "0";
        this.#disabledBarLayer.style.opacity = "0.4";
        
        bar.appendChild(this.#disabledBarLayer);

        return bar;
    }


    drawElement() {
        
        this.textBoxEnvelope = document.createElement("div");
        this.textBoxEnvelope.style.overflow = "hidden";
        this.textBoxEnvelope.style.resize = this.isMobile() ? "vertical" : "both";
        this.textBoxEnvelope.style.minHeight = this.#textBoxMinHeight || this.#textBoxHeight || "3em";
        this.textBoxEnvelope.style.width = "100%";
        this.textBoxEnvelope.style.height = this.#textBoxHeight || "";
        this.textBoxEnvelope.style.maxHeight = this.#textBoxMaxHeight /*|| this.#textBoxHeight*/ || this.isMobile() ? "300px" : "500px";
        this.textBoxEnvelope.style.backgroundColor = this.#textAreaBackground;
        this.textBoxEnvelope.style.padding = "5px";
        this.textBoxEnvelope.style.cursor = "text";
        this.textBoxEnvelope.classList.add("materialScroll");
        
        this.textBox = this.textBoxElement();
        this.textBoxEnvelope.addEventListener("click", (ev) => {
            this.textBox.focus();
            // let range = document.createRange
        });
        this.bar = this.commandBar(this.elementFilter);
        this.textBoxEnvelope.appendChild(this.textBox);
        this.appendChild(this.textBoxEnvelope);
        this.appendChild(this.bar);
        this.setEnabled(!this.#disabled);
        this.draggableEnable();
        this.textBoxEnvelopeId = `${this.id}TextBoxEnvelope`;
        this.textBoxEnvelope.id = this.textBoxEnvelopeId;
        this.textBoxId = `${this.id}TextBox`;
        this.textBox.id = this.textBoxId;
        this.barId = `${this.id}Bar`;
        this.bar.id = this.barId;

        if (this.#barIntegrated) {
            this.integrateBar();
        } else if (this.isMobile()) {
            this.barIntegrated = true;
            this.barPosition = "top";
        }

        this.formatDoc('fontname',"Arial");

    }
}

if(!window.customElements.get('aon-textarea-editor')) {
    window.customElements.define("aon-textarea-editor", AonTextareaEditor);
}