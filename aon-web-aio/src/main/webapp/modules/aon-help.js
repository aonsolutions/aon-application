import { AonElement } from '../components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "../environments/environments.js";
import { AonCard } from '../components/aon-card.js';
import { getManifest, getDomainUserRoles} from "../services/service.js";
import { AonSwitch } from "../components/aon-switch.js";
import { getSupport, setSupport } from '../services/supportService.js';
import { getParentCompany } from '../services/companyService.js';
import { DomainUserRoles } from '../models/DomainUserRoles.js';
import { AonCertification } from './certification/aon-certification.js';
import * as JSF from './aon-jsf-app.js';


export class AonHelp extends AonElement {

	SUPPORT_SWITCH;
	ABOUT_CONTACT_CARD;
	SCHEDULE_CONTACT_CARD;
	HELP_CONTENT;
	SUPPORT_CONTENT;
	cont;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	connectedCallback() {
		this.initialize();
		this.buildDur()
			.then( dur => this.buildForCompanies() )
			.catch( err => this.buildForAll());
	}

	initialize() {
		this.id = this.id || 'aonHelp';
		this.SUPPORT_SWITCH = this.id + 'SwitchSupport';
		this.ABOUT_CONTACT_CARD = this.id + 'AboutContactCard';
		this.SCHEDULE_CONTACT_CARD = this.id + 'ScheduleContactCard';
		this.HELP_CONTENT = 'helpContent';
		this.SUPPORT_CONTENT = 'supportContent';
		this.cont = 1;
	}
	
	buildForCompanies() {
		this.buildForAll();
		if ((this.dur.isDomainPayer()) || (this.dur.isOffice())){
			let rightPanelAboutContactCard = new AonCard();
			rightPanelAboutContactCard.id = this.ABOUT_CONTACT_CARD;
			rightPanelAboutContactCard.title = MSG.CONTACT_DATA2;
			rightPanelAboutContactCard.className = "rightPanelAboutContactCard";
			helpContent.appendChild(rightPanelAboutContactCard);

			let cardDiv = this.getElement(rightPanelAboutContactCard.CARD);
			cardDiv.className = "aonCard rightPanelcardDiv";
			
			let divGeneral = this.createDiv();
			divGeneral.appendChild(this.buildSupportData("AON SOLUTIONS S.L.", MSG.COMPANY, MATERIAL_ICONS.BUSINESS, CSS.AON_SUPPORT_NAME));
			divGeneral.appendChild(this.buildSupportData("(+34) 900 831 205", MSG.PHONE, MATERIAL_ICONS.PHONE, CSS.AON_SUPPORT_TELEPHONE));
			divGeneral.appendChild(this.buildSupportData("soporte@aonSolutions.es", "Atención a usuarios", MATERIAL_ICONS.MAIL, CSS.AON_SUPPORT_USERS_EMAIL));
			divGeneral.appendChild(this.buildSupportData("comercial@aonSolutions.es", "Ventas y contratación", MATERIAL_ICONS.MAIL, CSS.AON_SUPPORT_SALES_EMAIL));
			divGeneral.appendChild(this.buildSupportData("administración@aonSolutions.es", "Facturación, cobros y pago", MATERIAL_ICONS.MAIL, CSS.AON_SUPPORT_ADMIN_EMAIL));
			rightPanelAboutContactCard.setContent(divGeneral);

			let rightPanelAboutScheduleCard = new AonCard();
			rightPanelAboutScheduleCard.id = this.SCHEDULE_CONTACT_CARD;
			rightPanelAboutScheduleCard.title = MSG.SCHEDULE;
			rightPanelAboutScheduleCard.className = "rightPanelAboutScheduleCard";
			helpContent.appendChild(rightPanelAboutScheduleCard);

			let cardDiv2 = this.getElement(rightPanelAboutScheduleCard.CARD);
			cardDiv2.className = "aonCard rightPanelCardDiv";

			let divGeneral2 = this.createDiv();
			divGeneral2.appendChild(this.buildSupportData(MSG.WEEK_SCHEDULE,MSG.WEEK_SCHEDULE, MATERIAL_ICONS.SCHEDULE, CSS.AON_WEEK_SCHEDULE));
			divGeneral2.appendChild(this.buildSupportData(MSG.WEEK_FRIDAY_SCHEDULE, MSG.WEEK_SCHEDULE,MATERIAL_ICONS.SCHEDULE, CSS.AON_WEEK_FRIDAY_SCHEDULE));
			rightPanelAboutScheduleCard.setContent(divGeneral2);

		}else if(this.dur.getParentDomain() != null){
			let parentDomain = this.dur.getParentDomain();
			let parentId = parentDomain.id;
			let parentName = parentDomain.name;
			getParentCompany({parentId, parentName}).then(r =>{		
				let name = r.name;
				let phoneData = r.media.find(item => item.media === "fixed_phone");
				let phone = phoneData ? phoneData.value : "Teléfono no encontrado";
				let emailData = r.media.find(item => item.media === "email");
				let email = emailData ? emailData.value : "Email no encontrado";
				
				let rightPanelAboutContactCard = new AonCard();
				rightPanelAboutContactCard.id = this.ABOUT_CONTACT_CARD;
				rightPanelAboutContactCard.title = MSG.CONTACT_DATA2;
				rightPanelAboutContactCard.className = "rightPanelAboutContactCard";
				helpContent.appendChild(rightPanelAboutContactCard);

				let cardDiv = this.getElement(rightPanelAboutContactCard.CARD);
				cardDiv.className = "aonCard rightPanelcardDiv";
												
				let divGeneral = this.createDiv();
				divGeneral.appendChild(this.buildSupportData(name, MSG.COMPANY, MATERIAL_ICONS.BUSINESS, CSS.AON_SUPPORT_NAME));
				divGeneral.appendChild(this.buildSupportData(phone, MSG.PHONE, MATERIAL_ICONS.PHONE, CSS.AON_SUPPORT_TELEPHONE));
				divGeneral.appendChild(this.buildSupportData(email, "Correo electrónico", MATERIAL_ICONS.MAIL, CSS.AON_SUPPORT_USERS_EMAIL));
				rightPanelAboutContactCard.setContent(divGeneral);
				
				
			});
		}
	}

	buildForAll() {
				
		let helpContent = this.createDiv(this.HELP_CONTENT, "aonFlexColumn");
		
		let supportContent = this.createDiv(this.SUPPORT_CONTENT, "aonFlexBetween");
		supportContent.style.padding = ".5rem 1rem";
		
		this.appendChild(helpContent);

		let span = this.createSpan();
		span.innerHTML = MSG.SUPPORT;
		supportContent.appendChild(span);
		
		let rightPanelSwitchSupportButton = new AonSwitch();
		rightPanelSwitchSupportButton.id = this.SUPPORT_SWITCH;
		supportContent.appendChild(rightPanelSwitchSupportButton);
		
		helpContent.appendChild(supportContent);

		let helpContentIndexDiv = this.createSpan("contentIndexHelp");
		helpContentIndexDiv.className = "helpCardText";

		let helpContentIndexI = this.createElement(TAG.I);
		helpContentIndexI.className = CSS.MATERIAL_ICONS;
		helpContentIndexI.classList.add("aonHelpI");
		helpContentIndexI.innerHTML= "school";
		helpContentIndexDiv.appendChild(helpContentIndexI);

		let helpContentIndexSpan = this.createDiv();
		helpContentIndexSpan.className = CSS.AON_CARD_TEXT;
        helpContentIndexSpan.classList.add("aonHelpSpan2");
		helpContentIndexSpan.innerHTML = MSG.CONTENT_INDEX;
		helpContentIndexDiv.appendChild(helpContentIndexSpan);
		helpContent.appendChild(helpContentIndexDiv);
		
		helpContentIndexDiv.addEventListener(EVENT.CLICK, () => {
			this.rootPanel(new JSF.AonJsfHelpContent())
			this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_OPEN, {}));
		});

		let helpNotificationDiv = this.createSpan();
		helpNotificationDiv.className = "helpCardText";

		let helpNotificationI = this.createElement(TAG.I);
		helpNotificationI.className = CSS.MATERIAL_ICONS;
		helpNotificationI.classList.add("aonHelpI");
		helpNotificationI.innerHTML= "rss_feed";
		helpNotificationDiv.appendChild(helpNotificationI);

		let helpNotificationSpan = this.createDiv();
		helpNotificationSpan.className = CSS.AON_CARD_TEXT;
		helpNotificationSpan.classList.add("aonHelpSpan2");
		helpNotificationSpan.innerHTML = MSG.NOTIFICATIONS;
		helpNotificationDiv.appendChild(helpNotificationSpan);
		helpContent.appendChild(helpNotificationDiv);

		helpNotificationDiv.addEventListener(EVENT.CLICK, () => {
			this.rootPanel(new JSF.AonJsfHelpNotification());
			this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_OPEN, {}));
		});
		
		let helpCertificationsDiv = this.createSpan();
		helpCertificationsDiv.className = "helpCardText";

		let helpCertificationsI = this.createElement(TAG.I);
		helpCertificationsI.className = CSS.MATERIAL_ICONS;
		helpCertificationsI.classList.add("aonHelpI");
		helpCertificationsI.innerHTML= "license";
		helpCertificationsDiv.appendChild(helpCertificationsI);

		let helpCertificationsSpan = this.createDiv();
		helpCertificationsSpan.className = CSS.AON_CARD_TEXT;
		helpCertificationsSpan.classList.add("aonHelpSpan2");
		helpCertificationsSpan.innerHTML = MSG.CERTIFICATIONS;
		helpCertificationsDiv.appendChild(helpCertificationsSpan);
		helpContent.appendChild(helpCertificationsDiv);

		helpCertificationsDiv.addEventListener(EVENT.CLICK, () => {
			this.rootPanel(new AonCertification());
			this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_OPEN, {}));
		});
		
		let helpVersionDiv = this.createSpan();
		helpVersionDiv.className = "helpversionCardText";

		let helpVersionI = this.createElement(TAG.I);
		helpVersionI.className = CSS.MATERIAL_ICONS;
		helpVersionI.classList.add("aonHelpI");
		helpVersionI.innerHTML= "info";
		helpVersionDiv.appendChild(helpVersionI);

		let helpVersionSpan = this.createDiv();
		helpVersionSpan.className = CSS.AON_CARD_TEXT;
		helpVersionSpan.classList.add("aonHelpSpan2")
		getManifest().then((manifest) => {
			let version = MSG.VERSION + ": " + manifest.build_date;
			helpVersionSpan.innerHTML = `<div id="aonManifest">${version}</div>`;
		});;
		helpVersionDiv.appendChild(helpVersionSpan);
		helpContent.appendChild(helpVersionDiv);

		getSupport().then(r => {
			rightPanelSwitchSupportButton.checked = r.value;

		})
		rightPanelSwitchSupportButton.addEventListener(EVENT.CHANGE, () => {
			let data = {value: rightPanelSwitchSupportButton.isChecked()}
			setSupport(data).then(r => {})
		});
	    	
		
		let openButton = this.getElement("openNotificationButton");
		openButton.style.display = "none";
	}

	buildSupportData(value, title, icon, className) {
		let div = this.createDiv();
		div.style.marginTop = '10px';
		div.style.title = title;
		div.style.display = "flex";

		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS + " mailIcon";
		i.id = "aonContactIcon" + this.cont;
		i.style.marginRight = '5px';
		i.style.verticalAlign = "middle";
		i.innerHTML= icon;
		div.appendChild(i);

		let span = this.createDiv();
		span.className = `${CSS.AON_CARD_TEXT} ${className}`;
		
		if(className==null){
			span.innerHTML = value;
		}
		
		div.appendChild(span);

		this.cont = this.cont + 1;
		return div;
	}

	getSupportSwitch() {
		return this.getElement(this.SUPPORT_SWITCH);
	}

	getAboutContact(){
		return this.getElement(this.ABOUT_CONTACT_CARD);
	}

	getScheduleContact(){
		return this.getElement(this.SCHEDULE_CONTACT_CARD);
	}

}
if(!window.customElements.get(TAG.AON_HELP)){
	window.customElements.define(TAG.AON_HELP, AonHelp);
}