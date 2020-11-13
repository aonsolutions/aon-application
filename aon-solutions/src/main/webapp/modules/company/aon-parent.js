import {AonElement} from '../../components/AonElement.js';
import {closeSession, getUserAppRole, getCompanies} from  '../../services/service.js';
import {rootPanel} from '../../services/gwtLoader.js';
import './aon-desktop.js';
import '../signin/aon-sign.js';

export class AonParent extends AonElement {

	companies;
	selected;

	constructor () {
		super();
		this.id = 'aonParent';
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonParentMain" title="Parent" main="true"></aon-application>
		`;

		let aonParent = document.getElementById('aonParentMain');

		let taskOptions = [{
				name: 'Documentos sin leer',
				icon: 'snippet_folder',
				fn: () => {}
			},{
				name: 'Notificaciones',
				icon: 'notifications',
				fn: () => {}
			},{
				name: 'Facturas Pendientes',
				icon: 'inbox',
				fn: () => {}
			}, {
				name: 'Facturas Rechazadas',
				icon: 'report',
				fn: () => {}
			}, {
				name: 'Solicitudes Abiertas',
				icon: 'assignment',
				fn: () => {}
			}, {
				name: 'Solicitudes para ti',
				icon: 'assignment_ind',
				fn: () => {}
			}
		];
		aonParent.addSidenavOptions('TAREAS PENDIENTES', taskOptions);

		aonParent.addSidenavWidgetHTML('CONTROL DE HORARIO',
		 		'<aon-sign></aon-sign>');

		let filterOptions = [{
				name: 'Activas',
				icon: 'domain',
				fn: () => {}
			}, {
				name: 'Inactivas',
				icon: 'domain_disabled',
				fn: () => {}
			}, {
				name: 'Compartidas',
				icon: 'share',
				fn: () => {}
			}, {
				name: 'Entorno',
				icon: 'apartment',
				fn: () => {}
			},{
				name: 'Despacho',
				icon: 'work',
				fn: () => {}
			}
		];
		aonParent.addSidenavOptions('FILTROS', filterOptions);


		this.init();
	}

	init(filter) {
		let aonParent = document.getElementById('aonParentMain');
		aonParent.startLoader();
		getCompanies()
		.then( companies => {
        this.build(companies.filter(f => this.companyFilter(f, filter)));
				aonParent.stopLoader();
      }, () => closeSession()
    );
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

 	build(companies) {
		let aonParent = document.getElementById('aonParentMain');
		let content = document.createElement('div');
		let div = document.createElement('div');
		div.style.borderBottom = '1px solid #5f6368';
		div.style.marginTop = '15px';
		div.style.marginLeft = '20px';
		div.style.marginRight = '20px';
		div.style.paddingBottom = '10px';
		div.style.paddingLeft = '16px';
		div.innerHTML = 'EMPRESAS';
		content.appendChild(div);

		let ul = document.createElement('ul');
		ul.className = 'list-group';
		ul.style.marginLeft= '20px';
		ul.style.marginRight= '20px';

		for(let i = 0; i < companies.length; i++){
			ul.appendChild(this.buildLi(companies[i], 'transparent'));
		}
		content.appendChild(ul);
		aonParent.setContent(content);
	}

	buildLi(company, color) {
		let li = document.createElement('li');
		li.className = 'mdl-list__item mdl-list__item--two-line aonLi';
		li.style.backgroundColor = company.parent ? '#E1ECFF' : color;
		li.addEventListener('click', () => {
			this.companySelection(company);
		});

		li.addEventListener('mouseover', () => {
			li.style.backgroundColor = '#ddd';
		});

		li.addEventListener('mouseleave', () => {
			li.style.backgroundColor = company.parent ? '#E1ECFF' : color;
		});

		li.addEventListener('contextmenu', () => {

		});

		let span = document.createElement('span');
		span.className = 'mdl-list__item-primary-content';

		let i = document.createElement('i');
		i.className = 'material-icons aonAvatar';

		if(company.parent) i.innerHTML = 'apartment';
		else if(company.shared) i.innerHTML = 'share';
		else if(!company.active) i.innerHTML = 'domain_disabled';
		else i.innerHTML = 'business';
		let span2 = document.createElement('span');
		span2.innerHTML = company.name;

		let span3 = document.createElement('span');
		span3.className = 'mdl-list__item-sub-title';
		span3.innerHTML = company.document;

		span.appendChild(i);
		span.appendChild(span2);
		span.appendChild(span3);
		li.appendChild(span);

		let sp = document.createElement('span');
		let i2 = document.createElement('i');
		i2.className = 'material-icons aonAvatar';
		i2.innerHTML = 'keyboard_arrow_right';
		sp.appendChild(i2);
		li.appendChild(sp);
		return li;
	}

	companySelection(company) {
		const BASE_ID = 'aonHeader';
		localStorage.setItem('company', JSON.stringify(company));
		if(!this.isMobile()){
			let aonHeaderCompanyList = document.getElementById(BASE_ID + 'CompanyList');
			aonHeaderCompanyList.style.display = 'block';

			let aonShowMenu = document.getElementById('aonShowMenu');
			aonShowMenu.style.display = 'block';

			let aonHeaderHelp = document.getElementById(BASE_ID + 'Help');
			aonHeaderHelp.style.display = 'block';

			let aonHeaderSearch = document.getElementById(BASE_ID + 'Search');
			aonHeaderSearch.style.display = 'none';

			let aonHeaderHome = document.getElementById(BASE_ID + 'Home');
			aonHeaderHome.style.display = 'block';

			let aonHeaderCompany = document.getElementById(BASE_ID + 'Company');
			aonHeaderCompany.style.display = 'block';

			let aonHeaderCompanyName = document.getElementById(BASE_ID + 'CompanyName');
			aonHeaderCompanyName.innerHTML = company.name;

			let aonLogo = document.getElementById('aonLogo');
		} else {
			document.getElementById('aonHeaderSearchBox').setAttribute('selected', company.name);
		}
		localStorage.setItem("aon_domain_id", company.id);
		localStorage.setItem("aon_domain_name", company.domain);

		getUserAppRole().then(user => {
			if(!this.isMobile()) {
				let aonMenu = document.getElementById('aonMenu');
				aonMenu.innerHTML = '';
				aonMenu.setAttribute('company', JSON.stringify(company));
				aonMenu.setAttribute('user', JSON.stringify(user));
				aonMenu.build()
			}

			let aonHeader = document.getElementById('aonHeader');
			aonHeader.setAttribute('company', JSON.stringify(company));
			aonHeader.setAttribute('user', JSON.stringify(user));

			rootPanel(this.isMobile()
				? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
				: '<aon-desktop id="aonDesktop"></aon-desktop>');
			let aonDesktop = document.getElementById('aonDesktop');
			aonDesktop.setAttribute('company', JSON.stringify(company));
			aonDesktop.setAttribute('user', JSON.stringify(user));
		});
	}

}
window.customElements.define('aon-parent', AonParent);
