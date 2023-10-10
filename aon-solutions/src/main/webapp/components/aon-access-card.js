import {AonElement} from './AonElement.js';
import { MATERIAL_ICONS, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonIconButton } from './aon-icon-button.js';

export class AonAccessCard extends AonElement {
 	CARD;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.build();
	}

	initialize(){
		this.CARD = this.id + 'Card';
	}

	build() {
		this.id = this.CARD;
    	this.className = CSS.AON_ACCESS_CARD;
	}

	/*
		option {
			title,
			icon,
			logo,
			color,
			colorRGBA
		}
	*/
	addContent(option, fn) {
		let card = this.getElement(this.CARD);
		card.addEventListener(EVENT.CLICK, fn);

		let content = this.createElement(TAG.DIV);
		content.id = this.CARD + "Content";
		content.className = CSS.AON_ACCESS_CARD_CONTENT;
		card.appendChild(content);

		let line = this.createElement(TAG.DIV);
		line.id = this.CARD + "Line";
		line.className = CSS.AON_ACCESS_CARD_LINE;
		line.style.borderColor = `${option.color}`;
		content.appendChild(line);

		let textDiv = this.createElement(TAG.DIV);
		textDiv.id = this.CARD + "TextDiv";
		textDiv.className = CSS.AON_ACCESS_CARD_TEXT_DIV;

		let spanIcon = this.createElement(TAG.SPAN);
		if(option.icon) {
			spanIcon.innerHTML = `<aon-icon icon="${option.icon}" color="${option.color}" size="30px"></aon-icon>`;
		} else {
			let img = this.createElement(TAG.IMG);
			img.style.width = '30px';
			img.src = option.logo;
			spanIcon.appendChild(img);
		}
		textDiv.appendChild(spanIcon);

		let text = this.createElement(TAG.SPAN);
		text.id = this.CARD + "Text";
		text.className = CSS.AON_ACCESS_CARD_TEXT;
		text.innerHTML = option.title;
		textDiv.appendChild(text);

		content.appendChild(textDiv);

		let arrow = this.createElement(TAG.I);
        arrow.className = CSS.MATERIAL_ICONS;
        arrow.innerHTML = MATERIAL_ICONS.CHEVRON_RIGHT;
		arrow.style.color = option.color;
		content.appendChild(arrow);
	}
}

if(!window.customElements.get('aon-access-card')){
  window.customElements.define('aon-access-card',  AonAccessCard);
}
