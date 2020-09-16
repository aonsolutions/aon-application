import {isMobile} from  '../services/utils.js';
import {closeSession, getUserAppRole, getCompanies} from  '../services/service.js';
import {rootPanel} from '../services/gwtLoader.js';

import './aon-desktop.js';

class AonParent extends HTMLElement {

	companies;

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- AON COMPANY (PARENT) TOOLBAR -->
			<aon-toolbar id="aonParent" title="EMPRESAS"></aon-toolbar>
			<!-- AON CONFIGURATION MENU (SIDENAV) -->
			<div id="aonParentSidenav" class="sidenav">
				<ul class="aonClip">
					<div id="aonParenSidenavFilter" class="aonSidenavDiv">
						<div class="aonSidenavTitle"> FILTROS </div>
						<div> Activas </div>
						<div> Inactivas </div>
						<div> Compartidas </div>
						<div> Entorno </div>
					</div>
				</ul>
			</div>

			<!-- AON CONFIGURATION CONTENT -->
			<div id="aonParentContent" class="aonContent">
				<ul id="aon-company-list" class="demo-list-two mdl-list aonCompanyList"></ul>
			</div>

		`;
		this.init();
 	}

	init(filter) {
		getCompanies()
		.then( r => {
			  if(!r.end){
          this.init(filter);
        }
        this.build(r.companies.filter(f => this.companyFilter(f, filter)))
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
		let list = document.getElementById("aon-company-list");
		list.innerHTML = '';

		for(let i = 0; i < companies.length; i++){
			list.appendChild(this.buildLi(companies[i], (i === 0 || i%2 === 0) ? '#f1f1f1' : 'transparent'));
		}
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
		i.innerHTML = 'business';

		let span2 = document.createElement('span');
		span2.innerHTML = company.name;

		let span3 = document.createElement('span');
		span3.className = 'mdl-list__item-sub-title';
		span3.innerHTML = company.document;

		span.appendChild(i);
		span.appendChild(span2);
		span.appendChild(span3);
		li.appendChild(span);
		return li;
	}

	companySelection(company) {
		const BASE_ID = 'aonHeader';

		let aonShowMenu = document.getElementById('aonShowMenu');
		aonShowMenu.style.display = 'block';
		let aonHeaderCompanyList = document.getElementById(BASE_ID + 'CompanyList');
		aonHeaderCompanyList.style.display = 'block';

		if(!isMobile()){
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
		}
		localStorage.setItem("aon_domain_id", company.id);
		localStorage.setItem("aon_domain_name", company.domain);

		getUserAppRole().then(user => {
			if(!isMobile()) {
				let aonMenu = document.getElementById('aonMenu');
				aonMenu.innerHTML = '';
				aonMenu.setAttribute('company', JSON.stringify(company));
				aonMenu.setAttribute('user', JSON.stringify(user));
				aonMenu.build()
			}

			let aonHeader = document.getElementById('aonHeader');
			aonHeader.setAttribute('company', JSON.stringify(company));
			aonHeader.setAttribute('user', JSON.stringify(user));

			rootPanel('<aon-desktop id="aonDesktop"></aon-desktop>');
			let aonDesktop = document.getElementById('aonDesktop');
			aonDesktop.setAttribute('company', JSON.stringify(company));
			aonDesktop.setAttribute('user', JSON.stringify(user));
		});
	}

}
window.customElements.define('aon-parent', AonParent);
