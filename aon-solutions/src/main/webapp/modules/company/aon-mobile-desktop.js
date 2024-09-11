import {getCompanies, getDomainNotice, getUserNotice, getUser, getTimeControl, getCompanyHeaderInfo, getDomainUserRoles} from  '../../services/service.js';
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import {AonElement} from '../../components/AonElement';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { AonSign } from '../timecontrol/aon-sign.js';
import { AonMessenger } from '../messenger/aon-messenger.js';
import '../invoice/aon-invoice-panel.js';
import { AonStatistics } from '../timecontrol/time-control/statistics/aon-statistics.js';
import { Apps, getAppsByDur } from '../../services/app.js';
import { AonSaltra } from '../laboral/aon-saltra.js';
import { getPosition } from '../../services/maps.js';
import * as LS from '../../services/localStorageService.js';

export class AonMobileDesktop extends AonElement {

	SUGGESTION;
	DIV_PARENT;

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

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			if(!this.openFirstApp(this.dur)){
				this.build();
				this.observerListener();
			}
		});
	}

	initialize() {
		this.id = 'aonDesktop';
		this.DIV_PARENT = this.id + "DivParent";
		this.SUGGESTION = this.id + 'Suggestion';
		this.COMPANY_DIV = this.id + 'CompanyDiv';
		this.PENDING_TASK = this.id + 'PendingTask';
		this.TIMECONTROL_TITLE = this.id + 'TimecontrolTitle';
		this.TIMECONTROL_SIGN = this.id + 'TimecontrolSign';
	}

	getDur() {
		return this.dur;
	}

	async build() {
		try {
			this.innerHTML = '';
			let divParent = this.createElement(TAG.DIV);
			divParent.id = this.DIV_PARENT;
			divParent.style.height = '100%';
			divParent.style.maxWidth = '100%';
			divParent.style.overflowX = 'hidden';
			divParent.style.display = "flex";
			divParent.style.flexDirection = "column";
			divParent.style.paddingBottom = "1rem";
			this.appendChild(divParent);
			let notice = undefined;
			if(localStorage.getItem('company')) {
				let company = JSON.parse(localStorage.getItem('company'));
				localStorage.setItem('aon_domain_id', company.id);
				localStorage.setItem('aon_domain_name', company.domain);
				localStorage.setItem('aon_domain_document', company.document);
				const user = await getUser();
				localStorage.setItem('aon_domain_login', user.login);
				await this.buildCompany();
				if(!this.getDur().isEmployee()){
					notice = await getDomainNotice();
				}
			} else {
				await this.buildCompany();
				if(!this.getDur().isEmployee()) {
					notice = await getUserNotice();
				}
			}

			this.buildNotifications(notice);

			await this.buildTimeControl();
		} catch (error) {
			console.log(error);
		}
	}

	observerListener(){
		window.addEventListener(EVENT.RESUME_APP, ()=>{
			this.buildTimeControl();
		});
	}

	openFirstApp(dur){
		const appsOpen = getAppsByDur(dur).filter(app=>  ![Apps.NOTES.app, Apps.TIMECONTROL.app,  Apps.MESSENGER.app].includes(app.app));

		if(appsOpen && appsOpen.length===1){
			let app = appsOpen[0];
			if( app.app === Apps.AON_SALTRA.app ){
				this.rootPanel(new AonSaltra())
				return true;
			}
		}
		return false;
	}

	async buildCompany() {
		try {
			let companyDiv = this.getElement(this.COMPANY_DIV) || this.createElement(TAG.DIV);
			companyDiv.id = this.COMPANY_DIV;
			companyDiv.style.margin = '10px';
			companyDiv.style.marginLeft = '50px';
			companyDiv.style.marginRight = '50px';
			companyDiv.style.textAlign = 'center';
			this.getElement(this.DIV_PARENT).appendChild(companyDiv);
	
			const pi = await getCompanyHeaderInfo();
			let url = pi.logo || 'https://sig.aonsolutions.org/aonDocuments/company.logo';
			let img = this.createElement(TAG.IMG);
			img.style.maxWidth = '200px';
			img.style.maxHeight = '100px';
			img.style.position = 'relative';
			img.src = url;
			img.onerror = () => {
				img.src = '../../assets/aon-logo.svg';
				//companyDiv.style.display = 'none';
			}
			this.clearElement(companyDiv);
			companyDiv.appendChild(img);
			//companyDiv.innerHTML = `<img style="position: relative;width: 100%;" src="${url}">`;
	
			if(localStorage.getItem('company')) {
				// let menu = document.querySelector('aon-mobile-menu');
				// menu.reload();
			} else {
				const companies = await getCompanies();
				if(companies.length > 0) {
					let company = companies[0];
					localStorage.setItem('company', JSON.stringify(company));
					localStorage.setItem("aon_domain_id", company.id);
					localStorage.setItem("aon_domain_name", company.domain);
					localStorage.setItem("aon_domain_document", company.document);
					const user = await getUser();
					localStorage.setItem('aon_domain_login', user.login);
					// let menu = document.querySelector('aon-mobile-menu');
					// menu.reload();
				}
			}
		} catch (error) {
			console.log(error);
		}
	}

	buildNotifications(notice) {
		let div = this.getElement(this.PENDING_TASK) || this.createElement(TAG.DIV);
		div.id = this.PENDING_TASK;
		div.style.borderTop = '1px solid #ddd';
		this.getElement(this.DIV_PARENT).appendChild(div);
		this.clearElement(div);

		if(notice){		
			let isRemoved = true;

			let titleA = this.createElement(TAG.DIV);
			titleA.className = LS.isNewTheme() ? "aonSidenavTitleBeta" : "aonSidenavTitle";
			titleA.innerHTML = 'TAREAS PENDIENTES';
			div.appendChild(titleA);

			let ul = this.createElement(TAG.UL);
			ul.id = this.PENDING_TASK + "Ul";
			ul.classList.add(CSS.AON_UL);
			ul.classList.add(CSS.AON_CLIP);
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
			
				ul.appendChild(this.buildNotificationsLi(MSG.PENDING_INVOICES, MATERIAL_ICONS.INBOX, inboxCount, () => {
					if(inboxCount > 0) {
						this.rootPanelHtml('<aon-invoice-panel></aon-invoice-panel>');
					}
				}));
				
				ul.appendChild(this.buildNotificationsLi(MSG.REJECTED_INVOICES, MATERIAL_ICONS.REPORT, rejectedCount, () => {
					if(rejectedCount > 0) {
						this.rootPanelHtml('<aon-invoice-panel status="rejected"></aon-invoice-panel>');
					}
				}));
				isRemoved = false;
			}	

			if( this.getDur().isMessenger()){
				
				let requestCount = 0;

				if(notice.solicitudes && notice.solicitudes.task_holder) {
					requestCount = notice.solicitudes.task_holder;
				}
				if(requestCount>0){
					ul.appendChild(
						this.buildNotificationsLi(MSG.REQUESTS_RECEIVED, MATERIAL_ICONS.ASSIGNMENT, requestCount, () =>  this.rootPanel(new AonMessenger()) )
					);
					isRemoved = false;
				}
			}

			if(isRemoved){
				try {
					div.remove();
				} catch(e){}
			}
		}
	}

	async buildTimeControl() {
		if(this.getDur().isTimecontrol()) {

			getPosition().then(console.log).catch(console.error); // GET POSITION

			const r = await getTimeControl();
			let div2 = this.getElement(this.TIMECONTROL_TITLE) || this.createElement(TAG.DIV);
			div2.id = this.TIMECONTROL_TITLE;
			if(!this.isMobile()){
				this.clearElement(div2);
				div2.appendChild(this.createTitleTime());
			}
			this.getElement(this.DIV_PARENT).appendChild(div2);

			let div3 = this.getElement(this.TIMECONTROL_SIGN) || this.createElement(TAG.DIV);
			div3.id = this.TIMECONTROL_SIGN;
			if(this.isMobile()){
				div3.style.borderTop = '1px solid #ddd';
				this.clearElement(div3);
				div3.appendChild(this.createTitleTime());
			}

			let staticsDiv = this.createElement(TAG.DIV);
			staticsDiv.style.height = "15rem";
			staticsDiv.style.minWidth = "10rem";
			staticsDiv.style.maxWidth = "20rem";
			staticsDiv.style.margin = "0 auto";

			// Make static smaller if pendingTask exists
			if(this.isMobile()){
				let pendingTask = this.getElement(this.PENDING_TASK);
				if(pendingTask){
					let pendingTaskUl = this.getElement(this.PENDING_TASK + "Ul");
					if(pendingTaskUl){
						if(pendingTaskUl.childElementCount == 3)
							staticsDiv.style.height = "10rem";
						else if(pendingTaskUl.childElementCount == 2) {
							staticsDiv.style.height = "12rem";
						}
					}
				}
			}

			staticsDiv.appendChild(new AonStatistics());

			div3.appendChild(staticsDiv);
			let aonSign = new AonSign();
			aonSign.setTimeControl(r);
			div3.appendChild(aonSign);
			this.getElement(this.DIV_PARENT).appendChild(div3);
			
			let aonHeader = this.getElement('aonHeader');
			aonHeader.timeControlStatus(r);
		}
	}

	createTitleTime(){
		let div = this.createElement(TAG.DIV);
		div.className = LS.isNewTheme() ? "aonSidenavTitleBeta" : "aonSidenavTitle";
		div.innerHTML = 'CONTROL HORARIO';
		div.style.textAlign = "left";
		div.style.marginLeft = "0";
		div.style.paddingLeft = "10";
		return div;
	}

	buildNotificationsLi(name, icon, count, fn) {
		let li = this.createElement(TAG.LI);
		li.className = 'aonAppMenuSidenavList aonOpacity';
		li.style.height = '40px';
		li.style.lineHeight = '40px';
		// li.style.borderBottom = '1px solid #ddd';

		let i = this.createElement(TAG.I);
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
		sp.style.float = 'right';

		let i2 = this.createElement(TAG.I);
		i2.className = 'material-icons aonAvatar';
		i2.innerHTML = MATERIAL_ICONS.KEYBOARD_ARROW_RIGHT;
		sp.appendChild(i2);
		li.appendChild(sp);

		li.addEventListener(EVENT.CLICK, () => fn());
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
