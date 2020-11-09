import {AonElement} from '../../components/AonElement.js';
import {AllApps, Apps, Services, AccountingMenu, PayrollMenu, AeatFiscalMenu, ArabaFiscalMenu,
	 GipuzkoaFiscalMenu, BizkaiaFiscalMenu, NavarraFiscalMenu, ToolsMenu} from  '../../services/app.js';
import {getDomainApps, setDomainApp, getCompanies} from  '../../services/service.js';
import {bidoq} from  '../../services/bidoq.js';
import {startModule, rootPanel} from '../../services/gwtLoader.js';

import '../../components/aon-icon.js';
import '../../components/aon-application.js';
import '../../components/aon-suggestion.js';
import '../marketplace/aon-marketplace.js';

export class AonMobileDesktop extends AonElement {

	SUGGESTION;

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
		this.SUGGESTION = this.id + 'Suggestion';
	}

	connectedCallback () {
		this.buildNotifications();
  }

	buildNotifications() {
		let searchDiv = document.createElement('div');
		searchDiv.id = 'aonHeaderCompany';
		this.appendChild(searchDiv)
		searchDiv.innerHTML = `<aon-suggestion id="${this.SUGGESTION}" title="Búsqueda Empresas"></aon-suggestion>`;

		let searchSuggestion = this.getElement(this.SUGGESTION);
		searchSuggestion.addIcon('search');
		searchSuggestion.addEventListener('keyup', () => {
			if(searchSuggestion.value.length > 2) {
				getCompanies().then( companies => searchSuggestion
					.buildOptions(companies.filter(f => this.companyFilter(f, {value: searchSuggestion.value})))
				);
			} else {
				searchSuggestion.closeOptions();
			}
		});
		searchSuggestion.addEventListener('select', (event) => {
			let company = event.detail;
			localStorage.setItem("aon_domain_id", company.id);
			localStorage.setItem("aon_domain_name", company.domain);
		});


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
		li.style.height = '40px';
		li.style.lineHeight = '40px';
		li.style.borderBottom = '1px solid #ddd';

		let i = document.createElement('i');
		i.className = 'material-icons aonVerticalMiddle';
		i.innerHTML = icon;
		li.appendChild(i);

		let span = document.createElement('span');
		span.className = 'aonMenuItemSpan';
		span.innerHTML = name;
		li.appendChild(span);

		let sp = document.createElement('span');
		sp.style.position = 'absolute';
		sp.style.right = '0px';

		let i2 = document.createElement('i');
		i2.className = 'material-icons aonAvatar';
		i2.innerHTML = 'keyboard_arrow_right';
		sp.appendChild(i2);
		li.appendChild(sp);
		return li;
	}

	companyFilter(f, q) {
		if(!q) {
			q = {
				inactive: false,
				active: true,
				shared: true
			};
		}
		let value = true;
		if(q && q.value) {
			const document = f.document && f.document.toUpperCase().includes(q.value.toUpperCase());
			const name = f.name && f.name.toUpperCase().includes(q.value.toUpperCase());
			value = document || name;
		}

		if(q && q.active && !q.inactive) {
			value = f.active && value;
		}

		if(q && q.inactive && !q.active) {
			value = !f.active && value;
		}

		if(q && q.shared) {
			// TODO
		}

		return value;
	}

}

window.customElements.define('aon-mobile-desktop', AonMobileDesktop);
