import {AonElement} from '../components/AonElement.js';
import {getDomainUserRoles} from  '../services/service.js';

import {DomainUserRoles} from '../models/DomainUserRoles.js';
import { TAG } from '../environments/environments.js';

import '../components/aon-icon.js';
import '../components/aon-application.js';
import './marketplace/aon-marketplace.js';
import './invoice/aon-invoice-panel.js';
import './laboral/aon-laboral.js';
import './messenger/aon-messenger.js';
import './fiscal/aon-fiscal.js';
import './accounting/aon-accounting.js';


export class AonNew extends AonElement {

	dur;

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
  	}

	initialize(){

	}

	getDur() {
		return this.dur;
	}

	build() {
		this.appendChild(this.buildTitle('NUEVO'));

		let ul = this.createElement(TAG.UL);
		ul.className = 'list-group';

		ul.appendChild(this.buildLi('Nueva Factura', 'receipt', () => {alert('En desarrollo')}));
		ul.appendChild(this.buildLi('Nuevo Documento', 'description', () => {alert('En desarrollo')}));
		ul.appendChild(this.buildLi('Nueva Solicitud', 'message', () => {alert('En desarrollo')}));
  	
		this.appendChild(ul);
	}

	buildLi(title, icon, fn) {
		let li = this.createElement(TAG.LI);
				li.className = 'list-group-item aonAppLi';
				li.style.borderRight = '0px';
				li.style.borderLeft = '0px';
				li.style.cursor = 'pointer';
				li.addEventListener('click', fn);
				let span = this.createElement(TAG.SPAN);
				span.style.margin = '20px';
				
				let i = this.createElement(TAG.I);
				i.className = 'material-icons';
				i.innerHTML = icon;
				span.appendChild(i);
				
				let span2 = this.createElement(TAG.SPAN);
				span2.className = 'aonAppTitle';
				span2.style.paddingTop = '5px';
				span2.innerHTML = title;
				span.appendChild(span2);

				let buttons = this.createElement(TAG.SPAN);
				buttons.style.position = 'absolute';
				buttons.style.right = '10px';

				let i2 = this.createElement('i');
				i2.className = 'material-icons';
				i2.innerHTML = 'keyboard_arrow_right';
				buttons.appendChild(i2);

				span.appendChild(buttons);
				li.appendChild(span);
		return li;

	}

	buildTitle(title) {
		let div = this.createElement(TAG.DIV);
		div.style.color = 'gray';
		div.style.padding = '20px';
		div.innerHTML = title;
		return div;
	}
}

if(!window.customElements.get('aon-new')){
	window.customElements.define('aon-new', AonNew);
}
