import {AonElement} from '../../components/AonElement.js';
import {AllApps, Apps, Services, AccountingMenu, PayrollMenu, AeatFiscalMenu, ArabaFiscalMenu,
	 GipuzkoaFiscalMenu, BizkaiaFiscalMenu, NavarraFiscalMenu, ToolsMenu} from  '../../services/app.js';
import {getDomainApps, setDomainApp} from  '../../services/service.js';
import {bidoq} from  '../../services/bidoq.js';
import {startModule, rootPanel} from '../../services/gwtLoader.js';

import '../../components/aon-icon.js';
import '../../components/aon-application.js';
import '../../components/aon-search-box.js';

import '../marketplace/aon-marketplace.js';

export class AonMobileDesktop extends AonElement {

	static get observedAttributes() {
		return ['company'];
	}

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	get user() {
		return this.getAttribute('user');
	}

	set user(user) {
		this.setAttribute('user', user);
	}

	attributeChangedCallback(name, oldValue, newValue) {

	}

	constructor () {
		super();
		this.id = 'aonDesktop';
	}

	connectedCallback () {
		this.buildNotifications();
  }

	buildNotifications() {
		let searchDiv = document.createElement('div');
		searchDiv.id = 'aonHeaderCompany';
		this.appendChild(searchDiv)
		searchDiv.innerHTML = `<aon-search-box id="aonHeaderSearchBox"></aon-search-box>`;

		// search = document.getElementById("aonHeaderSearchBox");

		let div = document.createElement('div');
		div.style.paddingBottom = '25px';
		div.style.borderBottom = '1px solid #ebebeb';
		this.appendChild(div);

		let titleA = document.createElement('div');
		titleA.className = 'aonSidenavTitle';
		titleA.innerHTML = 'TAREAS PENDIENTES';
		div.appendChild(titleA);

		let ul = document.createElement('ul');
		ul.className = 'aonClip';
		div.appendChild(ul);

		ul.appendChild(this.buildNotificationsLi('99 Documentos sin leer', 'snippet_folder'));
		ul.appendChild(this.buildNotificationsLi('99 Notificaciones', 'notifications'));
		ul.appendChild(this.buildNotificationsLi('99 Facturas Pendientes', 'inbox'));
		ul.appendChild(this.buildNotificationsLi('99 Facturas Rechazadas', 'report'));
		ul.appendChild(this.buildNotificationsLi('99 Solicitudes Abiertas', 'assignment'));
		ul.appendChild(this.buildNotificationsLi('99 Solicitudes para ti', 'assignment_ind'));

		let div2 = document.createElement('div');

		let titleB = document.createElement('div');
		titleB.className = 'aonSidenavTitle';
		titleB.innerHTML = 'CONTROL DE HORARIO';
		div2.appendChild(titleB);

		this.appendChild(div2);

		let div3 = document.createElement('div');
		div3.id = "aonControl";
		div3.style.margin = '25px';
		this.appendChild(div3);

		this.entrada();
	}

	entrada() {
		let div2 = document.getElementById('aonControl');
		div2.innerHTML = '';

		let button = document.createElement('button');
		button.style.backgroundColor = '#86D364';
		button.style.marginRight = '10px';
		button.innerHTML = 'ENTRADA';
		button.addEventListener('click', () => {
			this.salida();
		});
		div2.appendChild(button);
	}

	salida() {
		let div2 = document.getElementById('aonControl');
		div2.innerHTML = '';

		let button = document.createElement('button');
		button.style.backgroundColor = '#DC4D30';
		button.style.marginRight = '10px';
		button.innerHTML = 'SALIDA';
		button.addEventListener('click', () => {
			this.entrada();
		});
		div2.appendChild(button);

		let button2 = document.createElement('button');
		button2.style.backgroundColor = '#F39F1D';
		button2.style.marginRight = '10px';
		button2.innerHTML = 'PAUSA';
		button2.addEventListener('click', () => {
			this.vuelta();
		});
		div2.appendChild(button2);
	}

	vuelta() {
		let div2 = document.getElementById('aonControl');
		div2.innerHTML = '';

		let button = document.createElement('button');
		button.style.backgroundColor = '#86D364';
		button.style.marginRight = '10px';
		button.innerHTML = 'VUELTA';
		button.addEventListener('click', () => {
			this.salida();
		});
		div2.appendChild(button);
	}

	buildNotificationsLi(name, icon) {
		let li = document.createElement('li');
		li.className = 'aonAppMenuSidenavList aonOpacity';

		let i = document.createElement('i');
		i.className = 'material-icons aonVerticalMiddle';
		i.innerHTML = icon;
		li.appendChild(i);

		let span = document.createElement('span');
		span.className = 'aonMenuItemSpan';
		span.innerHTML = name;
		li.appendChild(span);
		return li;
	}
}

window.customElements.define('aon-mobile-desktop', AonMobileDesktop);
