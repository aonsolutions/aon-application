import {AonElement} from './AonElement.js';
import { CONSTANT, TAG } from '../environments/environments.js';

export class AonNewColor extends AonElement {

  DEFAULT_COLOR;
  AVIABLE_COLORS;

  INPUT;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

  get value() {
    return this.getAttribute(CONSTANT.VALUE)
  }

  set value(value) {
    this.setAttribute(CONSTANT.VALUE, value);
  }

	constructor (defaltColor, colors) {
		super();
    this.DEFAULT_COLOR = defaltColor;
    this.AVIABLE_COLORS = colors;
	}

	connectedCallback () {
    this.initialize();
    
    this.INPUT = this.createElement(TAG.DIV);

    this.INPUT.style.height = "50px";
    this.INPUT.style.backgroundColor = this.DEFAULT_COLOR;
    this.INPUT.style.position = "relative";
    this.INPUT.style.border = "1px solid #d2d2d6";
    this.INPUT.style.borderRadius = "3px";
    this.INPUT.style.cursor = "pointer";
    this.INPUT.addEventListener("click", (event) => {
      event.preventDefault();
      this.createColorsSelection();
    });

    this.appendChild(this.INPUT);
    
	}

  createColorsSelection(){
    let colorsDiv = this.createElement(TAG.DIV);
    colorsDiv.style.backgroundColor = "white";
    colorsDiv.style.borderRadius = "15px";
    colorsDiv.style.display = "flex";
    colorsDiv.style.gap = ".5rem";
    colorsDiv.style.flexWrap = "wrap";
    colorsDiv.style.width = "10rem";
    colorsDiv.style.position = "absolute";
    colorsDiv.style.bottom = "-35";
    colorsDiv.style.left = "10";
    colorsDiv.style.zIndex = "1";
    colorsDiv.style.padding = ".5rem";
    this.INPUT.appendChild(colorsDiv);

    this.AVIABLE_COLORS.forEach(color => {
      let colorDiv = this.createElement(TAG.DIV);
      colorDiv.style.width = "1rem";
      colorDiv.style.height = "1rem";
      colorDiv.style.borderRadius = "50%";
      colorDiv.style.borderColor = color;
      colorDiv.style.backgroundColor = color;

      colorDiv.addEventListener("click", (event) => {
        event.preventDefault();
        this.value = color;
        this.INPUT.style.backgroundColor = color;
        this.INPUT.innerHTML = ""; // Remove popup
        event.stopPropagation();
      });

      colorsDiv.appendChild(colorDiv);
    });
  }

  initialize() {}

}
if(!window.customElements.get('aon-new-color')){
	window.customElements.define('aon-new-color',  AonNewColor);
}
