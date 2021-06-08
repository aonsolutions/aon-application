import {getCompanies, getDomainNotice, getUserNotice, getUser, getTimeControl, getCompanyHeaderInfo, getDomainUserRoles} from  '../../services/service.js';
import { CSS, EVENT, MSG, TAG } from '../../environments/environments.js';
import {AonElement} from '../../components/components.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { AonSign } from '../signin/aon-sign.js';
import { AonMessenger } from '../messenger/aon-messenger.js';
import '../invoice/aon-invoice-panel.js';
// import '../../components/aon-suggestion.js';
// import './aon-mobile-parent.js';
// import '../../components/aon-application.js';
// import '../../components/aon-icon.js';

export class AonMobileDesktop extends AonElement {

	SUGGESTION;
	DIV_PARENT;
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
	}

	connectedCallback () {
		this.initialize();
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
	}

	initialize() {
		this.id = 'aonDesktop';
		this.DIV_PARENT = this.id + "DivParent";
		this.SUGGESTION = this.id + 'Suggestion';
	}

	getDur() {
		return this.dur;
	}

	build() {
		this.innerHTML = '';
		let divParent = this.createElement(TAG.DIV);
		divParent.id = this.DIV_PARENT;
		divParent.style.height = "100%";
		divParent.style.display = "flex";
		divParent.style.flexDirection = "column";
		this.appendChild(divParent);
		if(localStorage.getItem('company')) {
			let company = JSON.parse(localStorage.getItem('company'));
			localStorage.setItem('aon_domain_id', company.id);
			localStorage.setItem('aon_domain_name', company.domain);
			getUser().then(user => {
				localStorage.setItem('aon_domain_login', user.login);
			});
			getDomainNotice().then(notice => {
				this.buildNotifications(notice);
			});
		} else {
			getUserNotice().then(notice => {
				this.buildNotifications(notice);
			});
		}
	}

	buildNotifications(notice) {
		//let searchDiv = document.createElement(TAG.DIV);
		//searchDiv.id = 'aonHeaderCompany';
		//this.getElement(this.DIV_PARENT).appendChild(searchDiv)
		//searchDiv.innerHTML = `<aon-suggestion id="${this.SUGGESTION}" title="Búsqueda Empresas"></aon-suggestion>`;
		let company = JSON.parse(localStorage.getItem('company'));

		let cSpan = this.createElement(TAG.SPAN);
		cSpan.innerHTML = company.name;
		cSpan.className = 'aonMobileDesktopCompanyName';
		this.getElement(this.DIV_PARENT).appendChild(cSpan);

		let companyDiv = this.createElement(TAG.DIV);
		companyDiv.style.margin = '10px';
		companyDiv.style.marginLeft = '50px';
		companyDiv.style.marginRight = '50px';
		companyDiv.style.textAlign = 'center';
		this.getElement(this.DIV_PARENT).appendChild(companyDiv);

		getCompanyHeaderInfo().then((pi) =>{
			let url = pi.logo || 'https://sig.aonsolutions.org/aonDocuments/company.logo';
			let img = this.createElement('img');
			img.style.maxWidth = '200px';
			img.style.maxHeight = '100px';
			img.style.position = 'relative';
			img.src = url;
			img.onerror = () =>companyDiv.style.display = 'none';
			companyDiv.appendChild(img);
			//companyDiv.innerHTML = `<img style="position: relative;width: 100%;" src="${url}">`;
		});

		// let searchSuggestion = this.getElement(this.SUGGESTION);

		if(localStorage.getItem('company')) {
			let company = JSON.parse(localStorage.getItem('company'));
			// searchSuggestion.title = 'Empresa Seleccionada';
			// searchSuggestion.value = company.name;
			// searchSuggestion.readonly = true;
			let menu = document.querySelector('aon-mobile-menu');
			menu.reload();
		} else {
			getCompanies()
			.then( companies => {
				if(companies.length > 0) {
					let company = companies[0];
					localStorage.setItem('company', JSON.stringify(company));
					localStorage.setItem("aon_domain_id", company.id);
					localStorage.setItem("aon_domain_name", company.domain);
					getUser().then(user => {
						localStorage.setItem('aon_domain_login', user.login);
					});
					// searchSuggestion.title = 'Empresa Seleccionada';
					// searchSuggestion.value = company.name;
					// searchSuggestion.readonly = true;
					let menu = document.querySelector('aon-mobile-menu');
					menu.reload();
				}
			});
		}

		document.addEventListener(EVENT.CLICK, ({target})=> {
			let sg = this.getElement(this.SUGGESTION);
			if(sg) {
				let isClickInside = sg.contains(target);
				if(!isClickInside){
					if(localStorage.getItem('company')) {
						sg.title = 'Empresa Seleccionada';
						sg.value = JSON.parse(localStorage.getItem('company')).name;
						sg.readonly = true;
					}
				}
			}
		});

		// searchSuggestion.addIconButton('search', () => {
		// 	searchSuggestion.title = 'Búsqueda Empresas';
		// 	searchSuggestion.readonly = false;
		// 	searchSuggestion.value = '';
		// 	this.getElement(searchSuggestion.INPUT).focus();
		// });
		// searchSuggestion.addEventListener('keyup', () => {
		// 	if(searchSuggestion.value.length > 2) {
		// 		getCompanies().then( companies => searchSuggestion
		// 			.buildOptions(companies.filter(f => this.companyFilter(f, {value: searchSuggestion.value})))
		// 		);
		// 	} else {
		// 		searchSuggestion.closeOptions();
		// 	}
		// });
		//
		// searchSuggestion.addEventListener('select', (event) => {
		// 		searchSuggestion.title = 'Empresa Seleccionada';
		// 	let company = event.detail;
		// 	searchSuggestion.setAttribute('readonly', true);
		// 	localStorage.setItem('company', JSON.stringify(company));
		// 	localStorage.setItem("aon_domain_id", company.id);
		// 	localStorage.setItem("aon_domain_name", company.domain);
		// 	getUser().then(user => {
		// 		localStorage.setItem('aon_domain_login', user.login);
		// 	});
		// 	this.build();
		// 	let menu = document.querySelector('aon-mobile-menu');
		// 	menu.reload();
		// });

		let div = this.createElement(TAG.DIV);
		div.style.paddingBottom = '25px';
		this.getElement(this.DIV_PARENT).appendChild(div);

		let titleA = this.createElement(TAG.DIV);
		titleA.className = 'aonSidenavTitle';
		titleA.innerHTML = 'TAREAS PENDIENTES';
		div.appendChild(titleA);

		let ul = this.createElement(TAG.UL);
		ul.className = 'aonClip';
		div.appendChild(ul);

		if(this.getDur().isInvoice()){			
			let inboxCount = 0;
			if(notice.invoice && notice.invoice.inbox && notice.invoice.inbox.count && notice.invoice.inbox.count > 0) {
				inboxCount = notice.invoice.inbox.count;
			}

			let rejectedCount = 0;
			if(notice.invoice && notice.invoice.rejected && notice.invoice.rejected.count && notice.invoice.rejected.count > 0) {
				rejectedCount = notice.invoice.rejected.count;
			}
		
			ul.appendChild(this.buildNotificationsLi(MSG.PENDING_INVOICES, 'inbox', inboxCount, () => {
				if(inboxCount > 0) {
					this.rootPanelHtml('<aon-invoice-panel></aon-invoice-panel>');
				}
			}));
			ul.appendChild(this.buildNotificationsLi(MSG.REJECTED_INVOICES, 'report', rejectedCount, () => {
				if(rejectedCount > 0) {
					this.rootPanelHtml('<aon-invoice-panel status="refused"></aon-invoice-panel>');
				}
			}));
		}
		ul.appendChild(this.buildNotificationsLi('Solicitudes', 'assignment', 0, () => this.isBeta() 
			? this.rootPanel(new AonMessenger()) : this.development('Solicitud')));

		getTimeControl().then(r => {
			let div2 = this.createElement(TAG.DIV);
			if(!this.isMobile()){
				div2.appendChild(this.createTitleTime());
			}
			this.getElement(this.DIV_PARENT).appendChild(div2);
			let div3 = this.createElement(TAG.DIV);
			div3.style.marginLeft = '25px';
			if(this.isMobile()){
				div3.style.marginTop = "auto";
				div3.style.marginBottom = "10px";
				div3.appendChild(this.createTitleTime());
			}
			div3.appendChild(new AonSign());
			this.getElement(this.DIV_PARENT).appendChild(div3);
			let aonHeader = this.getElement('aonHeader');
			aonHeader.timeControlStatus(r);
		});

	}

	createTitleTime(){
		let div = this.createElement(TAG.DIV);
		div.className = 'aonSidenavTitle';
		div.innerHTML = 'CONTROL HORARIO';
		div.style.borderTop = '1px solid #ebebeb';
		div.style.textAlign = "left";
		div.style.marginLeft = "0";
		div.style.paddingTop = "10";
		div.style.paddingLeft = "10";
		return div;
	}

	buildNotificationsLi(name, icon, count, fn) {
		let li = this.createElement(TAG.LI);
		li.className = 'aonAppMenuSidenavList aonOpacity';
		li.style.height = '40px';
		li.style.lineHeight = '40px';
		li.style.borderBottom = '1px solid #ddd';

		let i = this.createElement('i');
		i.className = 'material-icons aonVerticalMiddle';
		i.innerHTML = icon;
		li.appendChild(i);

		let span = this.createElement(TAG.SPAN);
		span.className = 'aonMenuItemSpan';
		if(count > 0) {
			span.innerHTML = name + ' (' + count + ')';
			span.style.fontWeight = 'bold';
		} else span.innerHTML = name;

		li.appendChild(span);

		let sp = this.createElement(TAG.SPAN);
		sp.style.position = 'absolute';
		sp.style.right = '0px';

		let i2 = this.createElement('i');
		i2.className = 'material-icons aonAvatar';
		i2.innerHTML = 'keyboard_arrow_right';
		sp.appendChild(i2);
		li.appendChild(sp);

		li.addEventListener('click', () => fn());
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
if(!window.customElements.get('aon-mobile-desktop')){
	window.customElements.define('aon-mobile-desktop', AonMobileDesktop);
}
