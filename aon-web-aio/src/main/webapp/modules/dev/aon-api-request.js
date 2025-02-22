import { AonElement } from "../../components/AonElement.js";

import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { REQUEST } from "../../environments/msg-en.js";
 
export class AonApiRequest extends AonElement {

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
	let div = this.createElement(TAG.DIV);
	div.style.margin = '20px';
	div.style.width = '50%';
	this.appendChild(div);

	let title = this.createElement(TAG.H2);
	title.innerHTML = this.title;
	div.appendChild(title);
	
	let divAtr = this.createElement(TAG.DIV);
	divAtr.style.fontSize = '15px';
	divAtr.style.fontWeight = 'bold';
	divAtr.style.paddingBottom = '10px';
	divAtr.innerHTML = MSG.HTTP_REQUEST;
	div.appendChild(divAtr);

	let div1 = this.createElement(TAG.DIV);
	div1.style.margin = '20px';
	div1.style.padding = '20px';
	div1.style.backgroundColor = '#1E2224';
	div1.style.color = 'white';
	div1.innerHTML = this.httpRequest;
	div.appendChild(div1);

	let divAtr2 = this.createElement(TAG.DIV);
	divAtr2.style.fontSize = '15px';
	divAtr2.style.fontWeight = 'bold';
	divAtr2.style.paddingBottom = '10px';
	divAtr2.innerHTML = MSG.HTTP_REQUEST_HEADER;
	div.appendChild(divAtr2);
	
	this.buildList(div, this.getHeaderParams());
}

  getHeaderParams() {
	  return [{
		name: 'session_id',
		type: 'string',
		description: 'Token de autenticación de la aplicación.'
	  },{
		name: 'domain_id',
		type: 'number',
		description: 'Identificador del dominio.'
	  },{
		name: 'domain_name',
		type: 'string',
		description: 'Nombre del dominio.'
	  }, {
		name: 'domain_login',
	  	type: 'string',
	  	description: 'Identificador de usuario.'
	  }]
  }

	buildList(parent, params) {
		let ul = this.createElement(TAG.UL);
		ul.style.listStyle = 'none';
		ul.style.padding = '0';
		ul.style.margin = '0';
		ul.style.borderTop = '1px solid #ddd';
		for(let i= 0; i < params.length; i++) {
			this.buildValue(ul, params[i]);
		}
		parent.appendChild(ul);
	}

	buildValue(parent, attribute) {
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

  setTitle(title) {
	this.title = title;
  }

  setHttpRequest(httpRequest) {
	this.httpRequest = httpRequest;
  }
}
if(!window.customElements.get('aon-api-request')){
  window.customElements.define('aon-api-request', AonApiRequest);
}
