import { AonElement } from "../../components/AonElement.js";

import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
 
export class AonApiObject extends AonElement {

  object = [];
  example = {};

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
    this.id = this.id || 'aonApiObject';
  }

  build() {
    let main = this.createElement(TAG.DIV);
		main.style.display = 'flex';

		let div = this.createElement(TAG.DIV);
		div.style.margin = '20px';
		div.style.width = '50%';
		main.appendChild(div);

		let exampleDiv = this.createElement(TAG.DIV);
		exampleDiv.style.marginTop = '100px';
		exampleDiv.style.marginLeft = '20px';
		exampleDiv.style.marginRight = '20px';
		exampleDiv.style.padding = '10px';
		exampleDiv.style.fontWeight = '400';
		exampleDiv.style.width = '50%';
		exampleDiv.style.backgroundColor = '#2E3336';
		exampleDiv.style.color = 'white';
		exampleDiv.innerHTML = JSON.stringify(this.example, null, 2);
		alert(JSON.stringify(this.example, null, 2));

    main.appendChild(exampleDiv);

		let h2 = this.createElement(TAG.H2);
		h2.innerHTML = MSG.INVOICE_OBJECT;
		div.appendChild(h2);

		let divAtr = this.createElement(TAG.DIV);
		divAtr.style.fontSize = '15px';
		divAtr.style.fontWeight = 'bold';
		divAtr.style.paddingBottom = '10px';
		divAtr.innerHTML = 'Atributos'; //MSG.ATTRIBUTES;
		div.appendChild(divAtr);
    this.buildAttributes(div);

    this.appendChild(main);
  }

  buildAttributes(parent) {
		let ul = this.createElement(TAG.UL);
		ul.style.listStyle = 'none';
		ul.style.padding = '0';
		ul.style.margin = '0';
		ul.style.borderTop = '1px solid #ddd';
    for(let i= 0; i< this.object.length; i++) {
      this.buildAttribute(ul, this.object[i]);
    }
    parent.appendChild(ul);
  }

  buildAttribute(parent, attribute) {
		let li = this.createElement(TAG.LI);
		let div1 = this.createElement(TAG.DIV);
		div1.style.marginTop = '10px';
		let span1 = this.createElement(TAG.SPAN);
		span1.style.color = '#3c4257';
		span1.style.fontWeight = 'bold';
		span1.style.padding = '10px';
		span1.innerHTML = attribute.name;
		div1.appendChild(span1);

		let span2 = this.createElement(TAG.SPAN);
		span2.style.fontWeight = 'bold';
		span2.style.color = 'gray';
		span2.innerHTML = attribute.type;
	
		div1.appendChild(span2);
		li.appendChild(div1);

		let div2 = this.createElement(TAG.DIV);
		div2.style.padding = '10px';
		div2.style.borderBottom = '1px solid #ddd';
		div2.innerHTML = attribute.description;;

		li.appendChild(div2);

		parent.appendChild(li)
  }

  setObject(object) {
    this.object = object;
  }

  setExample(example) {
    this.example = example;
  }
}
if(!window.customElements.get('aon-api-object')){
  window.customElements.define('aon-api-object', AonApiObject);
}
