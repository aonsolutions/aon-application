import { CONSTANT, CSS, EVENT, TAG } from "../environments/environments.js";
import { AonElement } from "./AonElement.js";

import '../css/aon-time.css';

export class AonTime extends AonElement {

    #max;
    #min;

    #value;
    #disabled;

    hours;
    minutes;

    LABEL;
    TIME_CONTAINER;
    TIME_PANEL;
    HOUR_INPUT;
    MINUTE_INPUT;

    HIDDEN_INPUT;

    MINUTE_INPUT_ID;
    HOUR_INPUT_ID; 


    get TIME_REGEX() {
        return /^(\d+):(\d{1,2})$/;
    }


    get COLORS() {
        return {
            backgroundColor: "transparent",
            backgroundColorHover: "var(--aon-color-interaction-minus-two)",
            detailColor: "var(--aon-color-ink-medium-contrast)",
            detailColorFocus: "var(--aon-color-interaction)",
            disabledColor: "var(--aon-color-bg-low-contrast)"
        };
    }

    get DEFAULT_MIN() {
        return "00:00";
    }

    get DEFAULT_MAX() {
        return "23:59";
    }

    get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

    get max() {
        return this.#max;
    }

    set max(max) {
        this.setAttribute("max", max);
    }

    get min() {
        return this.#min;
    }

    set min(min) {
        this.setAttribute("min", min);
    }

    get value() {
        if (this.checkValue(this.#value)) {
            return this.#value;
        }
    }

    set value(value) {
        if (this.checkValue(value)) {
            this.#value = value;
            this.changeTime();
        }
    }

    set name(name) {
        this.setAttribute(CONSTANT.NAME, name);
    }

    get name() {
        return this.getAttribute(CONSTANT.NAME);
    }

    set title(title) {
        this.setAttribute(CONSTANT.TITLE, title);
    }

    get title() {
        return this.getAttribute(CONSTANT.TITLE);
    }

    set disabled(disabled) {
        if (typeof(disabled) === "boolean") {
            this.#disabled = disabled;
            if (this.#disabled === true) {
                this.setDisabled();
            } else {
                this.setEnabled();
            }
        }
    }
    
    get disabled() {
        return this.#disabled;
    }

    static get observedAttributes() {
        return [CONSTANT.MIN, CONSTANT.MAX, CONSTANT.DISABLED, CONSTANT.NAME, CONSTANT.TITLE];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        switch (name) {
            case CONSTANT.MAX:
                if (newValue === "") {
                    this.setMax(this.DEFAULT_MAX);
                } else {
                    this.setMax(newValue);
                }
                break;
            case CONSTANT.MIN:
                if (newValue === "") {
                    this.setMin(this.DEFAULT_MIN);
                } else {
                    this.setMin(newValue);
                }
                break;
            case CONSTANT.DISABLED:
                if (newValue === "" || newValue === "true") {
                    this.disabled = true;
                } else {
                    this.disabled = false;
                }
                break;
            case CONSTANT.NAME:
                if (this.HIDDEN_INPUT) {
                    this.HIDDEN_INPUT.name = newValue;
                }
                break;
            case CONSTANT.TITLE:
                if (this.LABEL) {
                    this.LABEL.innerText = newValue;
                }
                break;
        }
    }

    setMax(max){
        if (this.validateDate(max) && this.checkMaxAndMin(max, this.#min)) {
            this.#max = max;
        }
    }
    
    setMin(min){
        if (this.validateDate(min) && this.checkMaxAndMin(this.#max, min)) {
            this.#min = min;
        }
    }

    validateDate(date) {
        if (date) {
            let dateMatch = date.match(this.TIME_REGEX);
            if (dateMatch) {
                let minuteStr = dateMatch[2];
                let minute = Number.parseInt(minuteStr);
                return (!isNaN(minute) && minute >= 0 && minute <= 59);
            }
        }
        return false;
    }

    checkMaxAndMin(max, min) {
        if (min && max) {
            let maxMatch = max.match(this.TIME_REGEX);
            let minMatch = min.match(this.TIME_REGEX);
            let maxH = Number.parseInt(maxMatch[1]);
            let maxM = Number.parseInt(maxMatch[2]);
            let minH = Number.parseInt(minMatch[1]);
            let minM = Number.parseInt(minMatch[2]);
    
            if (maxH > minH) {
                return true;
            } else if(maxH === minH) {
                return maxM >= minM;
            }
        }
        return false;
    }

    numberifyDate(date) {
        let match = date.match(this.TIME_REGEX);
        let h = Number.parseInt(match[1]);
        let m = Number.parseInt(match[2]);
        return {hour: h, minute: m};
    }

    getValue() {
        if (this.MINUTE_INPUT && this.HOUR_INPUT) {
            let hourStr = this.HOUR_INPUT.value;
            let minuteStr = this.MINUTE_INPUT.value;
            let hour = Number.parseInt(hourStr);
            let minute = Number.parseInt(minuteStr);
            if (hour >= 0 && minute >= 0) {
                let val = `${`${hour}`.padStart(2, "0")}:${`${minute}`.padStart(2, "0")}`;
                if (this.HIDDEN_INPUT) {
                    this.HIDDEN_INPUT.value = val;
                }
                return val;
            }
        }
        return "";
    }

    checkValue(value) {
        if (value && this.validateDate(value)) {
            let obj = this.numberifyDate(value);
            let hours = obj.hour;
            let mins = obj.minute;
            if (hours >= 0 && mins >= 0) {
                let maxtime = this.numberifyDate(this.#max);
                let mintime = this.numberifyDate(this.#min);
    
                if (hours < maxtime.hour && hours > mintime.hour) {
                    return true;
                } else if (hours === maxtime.hour) {
                    return mins <= maxtime.minute;
                } else if (hours === mintime.hour) {
                    return mins >= mintime.minute;
                }
            }
        }
        return false;
    }

    constructor() {
        super();
        this.#max = this.DEFAULT_MAX;
        this.#min = this.DEFAULT_MIN;
        this.#disabled = false;
    }

    connectedCallback() {
        this.initialize();
        this.build();
    }
    
    initialize() {
        this.id = this.id || Math.random().toString(36).substring(7);
        this.HOUR_INPUT_ID = this.id + "HourInput";
        this.MINUTE_INPUT_ID = this.id + "MinuteInput";
    }

    build() {
        this.className = "aon-time";
        this.tabIndex = 0;
        this.style.display = "block";
        this.style.borderBottom = `1px solid ${this.COLORS.detailColor}`;
        this.buildLabel();
        this.buildTime();
        this.appendChild(this.LABEL);
        this.appendChild(this.TIME_CONTAINER);
        this.addEventListeners();
        if (this.#disabled) {
            this.disabled = true;
        }
    }


    buildLabel() {
        this.LABEL = document.createElement("div");
        this.LABEL.style.width = "calc(100% - .5rem)";
        this.LABEL.style.paddingLeft = ".5rem";
        this.LABEL.style.margin = "3px auto 3px auto";
        this.LABEL.style.overflow = "hidden";
        this.LABEL.style.textOverflow = "ellipsis";
        this.LABEL.style.color = this.COLORS.detailColor;
        this.LABEL.innerText = this.getAttribute(CONSTANT.TITLE);
    }

    buildTime() {
       this.TIME_CONTAINER = document.createElement("div");
       this.TIME_CONTAINER.style.width = "100%";

       this.HOUR_INPUT = document.createElement("input");
       this.HOUR_INPUT.type = "number";
       this.HOUR_INPUT.style.width = "50px";
       this.HOUR_INPUT.style.maxWidth = "calc(40% - .2rem)";
       this.HOUR_INPUT.placeholder = "--";
       this.HOUR_INPUT.style.border = "none";
       this.HOUR_INPUT.style.textAlign = "center";
       this.HOUR_INPUT.style.outline = "none";
       this.HOUR_INPUT.style.backgroundColor = this.COLORS.backgroundColor;
       this.HOUR_INPUT.min = "0";

       this.MINUTE_INPUT = document.createElement("input");
       this.MINUTE_INPUT.type = "number";
       this.MINUTE_INPUT.min = "0";
       this.MINUTE_INPUT.max = "59";
       this.MINUTE_INPUT.placeholder = "--";
       this.MINUTE_INPUT.style.border = "none";
       this.MINUTE_INPUT.style.width = "50px";
       this.MINUTE_INPUT.style.maxWidth = "calc(40% - .2rem)";
       this.MINUTE_INPUT.style.textAlign = "center";
       this.MINUTE_INPUT.style.outline = "none";
       this.MINUTE_INPUT.style.backgroundColor = this.COLORS.backgroundColor;

       this.TIME_PANEL = document.createElement("div");
       this.TIME_PANEL.style.display = "flex";
       this.TIME_PANEL.style.justifyContent = "flex-start";
       this.TIME_PANEL.style.alignItems = "center";
       this.TIME_PANEL.style.flexDirection = "row";
       this.TIME_PANEL.style.width = "125px";
       this.TIME_PANEL.style.maxWidth = "calc(100% - .5rem)";
       this.TIME_PANEL.style.marginTop = "3px";
       this.TIME_PANEL.style.marginBottom = "3px";
       this.TIME_PANEL.style.marginLeft = ".5rem";

       let timeDiv = document.createElement("div");
       timeDiv.innerText = ":";
       timeDiv.style.maxWidth = "calc(10% - .1rem)";

       this.HIDDEN_INPUT = document.createElement("input");
       this.HIDDEN_INPUT.type = "hidden";
       this.HIDDEN_INPUT.name = this.name;

       this.TIME_PANEL.appendChild(this.HOUR_INPUT);
       this.TIME_PANEL.appendChild(timeDiv);
       this.TIME_PANEL.appendChild(this.MINUTE_INPUT);
       this.TIME_PANEL.appendChild(this.HIDDEN_INPUT);

       this.TIME_CONTAINER.appendChild(this.TIME_PANEL);

       this.HOUR_INPUT.id = this.HOUR_INPUT_ID;
       this.MINUTE_INPUT.id = this.MINUTE_INPUT_ID;

       this.changeTime();
       this.setMinMax();
    }

    changeTime() {
        if (this.#value) {
            if (this.HIDDEN_INPUT) {
                this.HIDDEN_INPUT.value = this.#value;
            }
            let numDate = this.numberifyDate(this.#value);
            if (this.MINUTE_INPUT && this.HOUR_INPUT) {
                 this.MINUTE_INPUT.value = `${numDate.minute}`.padStart(2, "0");
                 this.HOUR_INPUT.value = `${numDate.hour}`.padStart(2, "0");
                //  this.setMinMax();
            }
        }
    }


    addEventListeners() {

        this.addEventListener("mouseover", (ev) => {
            if (!this.#disabled) {
                ev.currentTarget.style.backgroundColor = this.COLORS.backgroundColorHover;
            }
        });
        this.addEventListener("mouseout", (ev) => {
            if (!this.#disabled) {
                ev.currentTarget.style.backgroundColor = this.COLORS.backgroundColor;
            }
        });

        [this.HOUR_INPUT, this.MINUTE_INPUT].forEach(inputEl => {

            ["focus"].forEach(event => {
                inputEl.addEventListener(event, (ev) => {
                    ev.target.style.backgroundColor = this.COLORS.backgroundColorHover;
                    this.applyActiveStyle();
                });
            });
    
            ["focusout"].forEach(event => {
                inputEl.addEventListener(event, (ev) => {
                    ev.target.style.backgroundColor = this.COLORS.backgroundColor;
                    this.applyInactiveStyle();
                });
            });

            inputEl.addEventListener("keydown", (ev) => {
                if (inputEl.value === "" && (ev.which === 40 || ev.which === 38)) {
                    if (inputEl.min == 0) {
                        ev.preventDefault();
                        inputEl.value = "00";
                    }
                }
            });

            inputEl.addEventListener("keypress", (ev) => {
                if (ev.which < 48 || ev.which > 57) {
                    ev.preventDefault();
                }
            });
    
        });

        [this, this.LABEL, this.TIME_CONTAINER, this.TIME_PANEL].forEach((elem) => {
            elem.addEventListener("focus", (ev) => {
                this.HOUR_INPUT.focus();
            });
        });

        this.HOUR_INPUT.addEventListener("input", (ev) => {

            let maxtime = this.numberifyDate(this.#max);
            let mintime = this.numberifyDate(this.#min);

            let value = this.HOUR_INPUT.value;
            let mins = this.MINUTE_INPUT.value;
            let mvalue = Number.parseInt(mins);
            let hvalue = Number.parseInt(value);
            if (!isNaN(hvalue) && value !== "0") {
                if (hvalue > maxtime.hour) {
                    this.HOUR_INPUT.value = maxtime.hour;
                    this.MINUTE_INPUT.focus();
                    value = this.HOUR_INPUT.value;
                    hvalue = Number.parseInt(value);
                    if (!isNaN(hvalue)) {
                        if (hvalue === maxtime.hour && mvalue > maxtime.minute) {
                            this.MINUTE_INPUT.value = `${maxtime.minute}`.padStart(2, "0");
                        } else if (hvalue === mintime.hour && mvalue < maxtime.minute) {
                            this.MINUTE_INPUT.value = `${mintime.minute}`.padStart(2, "0");
                        }
                    }
                }
            }

            value = this.HOUR_INPUT.value;
            hvalue = Number.parseInt(value);
            if (!isNaN(hvalue)) {
                this.HOUR_INPUT.value = `${hvalue}`.padStart(2, "0");
            }
            
            this.setMinMax();
            this.#value = this.getValue();
        });

        this.HOUR_INPUT.addEventListener("focusout", (ev) => {
            let maxtime = this.numberifyDate(this.#max);
            let mintime = this.numberifyDate(this.#min);

            let value = this.HOUR_INPUT.value;
            let mins = this.MINUTE_INPUT.value;
            let mvalue = Number.parseInt(mins);
            let hvalue = Number.parseInt(value);
            
            if (!isNaN(hvalue)) {
                if (hvalue > maxtime.hour) {
                    this.HOUR_INPUT.value = maxtime.hour;
                } else if (hvalue < mintime.hour) {
                    this.HOUR_INPUT.value = mintime.hour;
                }

                value = this.HOUR_INPUT.value;
                hvalue = Number.parseInt(value);

                if (!isNaN(hvalue)) {
                    if (hvalue === maxtime.hour && mvalue > maxtime.minute) {
                        this.MINUTE_INPUT.value = `${maxtime.minute}`.padStart(2, "0");
                    } else if (hvalue === mintime.hour && mvalue < maxtime.minute) {
                        this.MINUTE_INPUT.value = `${mintime.minute}`.padStart(2, "0");
                    }
                    if (hvalue < 10) {
                        this.HOUR_INPUT.value = `${hvalue}`.padStart(2, "0");
                    }
                }
            }
            this.setMinMax();
        });

        [this.MINUTE_INPUT, this.HOUR_INPUT].forEach((element) => {
            element.addEventListener("paste", (ev) => {
                let paste = (ev.clipboardData || window.clipboardData).getData('text');
                if (/^\d+$/.test(paste)) {
    
                    let minimum = Number.parseInt(ev.currentTarget.min);
                    let maximum = Number.parseInt(ev.currentTarget.max);
    
                    let pasteNumber = Number.parseInt(paste);
    
                    if (pasteNumber > maximum) {
                        ev.preventDefault();
                        ev.currentTarget.value = `${maximum}`.padStart(2, '0');
                    } else if (pasteNumber < minimum) {
                        ev.preventDefault();
                        ev.currentTarget.value = `${minimum}`.padStart(2, '0');
                    }
                } else {
                    ev.preventDefault();
                }
    
            });
        });

        this.MINUTE_INPUT.addEventListener("input", (ev) => {
            let maxtime = this.numberifyDate(this.#max);
            let mintime = this.numberifyDate(this.#min);

            let hours = this.HOUR_INPUT.value;
            let mins = this.MINUTE_INPUT.value;
            let mvalue = Number.parseInt(mins);
            let hvalue = Number.parseInt(hours);

            if (!isNaN(hvalue) && mins !== "0") {
                if (hvalue === maxtime.hour && mvalue > maxtime.minute) {
                    this.MINUTE_INPUT.value = `${maxtime.minute}`.padStart(2, "0");
                } else if (hvalue === mintime.hour && mvalue < mintime.minute) {
                    this.MINUTE_INPUT.value = `${mintime.minute}`.padStart(2, "0");
                }
            }

            if (!isNaN(mvalue)) {
                if (mvalue > 59) {
                    this.MINUTE_INPUT.value = 59;
                } else {
                    this.MINUTE_INPUT.value = `${mvalue}`.padStart(2, "0");
                }

            }
            this.#value = this.getValue();
        });
    }

    setMinMax() {
        let maxtime = this.numberifyDate(this.#max);
        let mintime = this.numberifyDate(this.#min);

        let hours = this.HOUR_INPUT.value;
        let mins = this.MINUTE_INPUT.value;
        let mvalue = Number.parseInt(mins);
        let hvalue = Number.parseInt(hours);

        if (hvalue === mintime.hour) {
            this.MINUTE_INPUT.min = mintime.minute;
            // this.MINUTE_INPUT.value = mvalue > 9 ? mintime.minute : `0${mintime.minute}`;
        } else if (hvalue === maxtime.hour) {
            this.MINUTE_INPUT.max = maxtime.minute;
            // this.MINUTE_INPUT.value = mvalue > 9 ? maxtime : `0${maxtime}`;
        } else {
            this.MINUTE_INPUT.min = "0";
            this.MINUTE_INPUT.max = "59";
        }

        this.HOUR_INPUT.min = mintime.hour;
        this.HOUR_INPUT.max = maxtime.hour;
    }

    applyActiveStyle() {
        this.LABEL.style.color = this.COLORS.detailColorFocus;
        this.style.borderBottom = `2px solid ${this.COLORS.detailColorFocus}`;
    }

    applyInactiveStyle() {
        this.LABEL.style.color = this.COLORS.detailColor;
        this.style.borderBottom = `1px solid ${this.COLORS.detailColor}`;
    }

    setDisabled() {
        if (this.LABEL && this.HOUR_INPUT && this.MINUTE_INPUT) {
            this.style.backgroundColor = this.COLORS.disabledColor;
            this.style.borderBottom = `1px solid ${this.COLORS.detailColor}`;
            this.LABEL.style.color = this.COLORS.detailColor;
            this.MINUTE_INPUT.disabled = true;
            this.HOUR_INPUT.disabled = true;
            this.style.cursor = "not-allowed";
            this.MINUTE_INPUT.style.cursor = "not-allowed";
            this.HOUR_INPUT.style.cursor = "not-allowed";
        }
    }
    
    setEnabled() {
        if (this.LABEL && this.HOUR_INPUT && this.MINUTE_INPUT) {
            this.style.backgroundColor = this.COLORS.backgroundColor;
            this.LABEL.style.color = this.COLORS.detailColor;
            this.MINUTE_INPUT.disabled = false;
            this.HOUR_INPUT.disabled = false;
            this.style.cursor = "default";
            this.MINUTE_INPUT.style.cursor = "text";
            this.HOUR_INPUT.style.cursor = "text";
        }
    }
}

if(!window.customElements.get("aon-time")) {
    window.customElements.define("aon-time", AonTime);
}