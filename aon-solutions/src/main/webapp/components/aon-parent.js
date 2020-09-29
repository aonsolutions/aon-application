import {isMobile} from  '../services/utils.js';
import {closeSession, getUserAppRole, getCompanies} from  '../services/service.js';
import {rootPanel} from '../services/gwtLoader.js';

import './aon-desktop.js';

class AonParent extends HTMLElement {

	companies;
	selected;

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- AON CONFIGURATION MENU (SIDENAV) -->
			<div id="aonParentSidenav" class="sidenav" style="top:60px !important;height: calc(100vh - 61px) !important;">
				<div class="aonSidenavTitle"> FILTROS </div>
				<ul class="aonClip">
					<li id="aonParentSidenavActive" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">domain</i>
						<span class="aonMenuItemSpan"> Activas </span>
					</li>

					<li id="aonParentSidenavInactive" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">domain_disabled</i>
						<span class="aonMenuItemSpan"> Inactivas </span>
					</li>

					<li id="aonParentSidenavShared" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">share</i>
						<span class="aonMenuItemSpan"> Compartidas </span>
					</li>

					<li id="aonParentSidenavEntorno" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">apartment</i>
						<span class="aonMenuItemSpan"> Entorno </span>
					</li>
				</ul>

				<div class="aonSidenavTitle"> TAREAS PENDIENTOS </div>
				<ul class="aonClip">
					<li id="aonParentSidenavInvoiceInbox" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">inbox</i>
						<span class="aonMenuItemSpan" style="font-weight:bold;"> Facturas Pendientes (22) </span>
					</li>

					<li id="aonParentSidenavInvoiceReport" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">report</i>
						<span class="aonMenuItemSpan"> Incidencias </span>
					</li>

					<li id="aonParentSidenavNotification" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">notifications</i>
						<span class="aonMenuItemSpan"> Notificaciones </span>
					</li>

					<li id="aonParentSidenavTicket" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">article</i>
						<span class="aonMenuItemSpan" style="font-weight:bold;"> Tickets (7)</span>
					</li>
				</ul>
			</div>

			<!-- AON CONFIGURATION CONTENT -->
			<div id="aonParentContent" class="aonContent" style="height: calc(100vh - 61px) !important;">

			</div>
		`;
		this.init();
		this.toogleNav();

		let listIds = ['aonParentSidenavActive', 'aonParentSidenavInactive', 'aonParentSidenavShared', 'aonParentSidenavEntorno',
		 	'aonParentSidenavInvoiceInbox', 'aonParentSidenavInvoiceReport', 'aonParentSidenavNotification', 'aonParentSidenavTicket'];

		listIds.forEach((id, i) => {
				let el = document.getElementById(id);

				el.addEventListener('mouseover', () => {
					if(!this.selected || this.selected !== id)
						el.style.backgroundColor = '#f1f1f1';
				});
				el.addEventListener('mouseleave', () => {
					if(!this.selected || this.selected !== id)
						el.style.backgroundColor = 'transparent';
				});
				el.addEventListener('click', () => {
					listIds.forEach((id, i) => {
						let el1 = document.getElementById(id);
						el1.style.backgroundColor = 'transparent';
					});
					this.selected = id;
					el.style.backgroundColor = '#ddd';
				});
		});
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

	toogleNav() {
		let sidenav = 'aonParentSidenav';
		let content = 'aonParentContent';
		if(document.getElementById(sidenav).style.width === "250px"){
			document.getElementById(sidenav).style.width = "0px";
			document.getElementById(content).style.marginLeft = "0px";
		} else {
			document.getElementById(sidenav).style.width = "250px";
			document.getElementById(content).style['margin-left'] = "250px";
		}
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
		let content = document.getElementById('aonParentContent');
		content.innerHTML = '';
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
