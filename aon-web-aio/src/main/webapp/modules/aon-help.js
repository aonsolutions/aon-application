import { AonElement } from '../components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "../environments/environments.js";
import { AonCard } from '../components/aon-card';
import { AonIcon } from '../components/aon-icon';
import { getManifest, getDomainUserRoles} from "../services/service.js";
import { AonSwitch } from "../components/aon-switch.js";
import { getSupport, setSupport } from '../services/supportService.js';
import { getParentCompany } from '../services/companyService.js';
import { DomainUserRoles } from '../models/DomainUserRoles.js';
import { AonCertification } from './certification/aon-certification.js';
import * as JSF from './aon-jsf-app.js';
import { getCustomViewConfiguration } from '../services/customViewService.js';

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
    Promise.all([
    getCustomViewConfiguration().catch(err => {
      console.warn("Custom view no disponible, usando por defecto:", err);
      return { params: {} }; // fallback
    }),
    getManifest()
    ])
    .then(([config, manifest]) => {
    this.customConfig = config;
    this.manifest = manifest;
    
        this.buildDur()
          .then(  dur => this.build(true) )
          .catch( err => this.build());
    });
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

	build(loadCompanies = false) {
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

      // Indice de contenido
      let buttonContent = this.createElement(TAG.BUTTON);
      // Icono
      let i   = new AonIcon();
      i.icon  = "school";
      buttonContent.appendChild(i);
      buttonContent.innerHTML += MSG.CONTENT_INDEX;
      helpContent.appendChild(buttonContent);
      buttonContent.addEventListener(EVENT.CLICK, () => {
        this.rootPanel(new JSF.AonJsfHelpContent());
        this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_OPEN, {}));
        let rightPanel = this.closest('aon-right-panel');
        rightPanel?.close?.();
      });
      // Notificaciones
      let buttonNotif = this.createElement(TAG.BUTTON);
      // Icono
      let iNot  = new AonIcon();
      iNot.icon = "rss_feed";
      buttonNotif.appendChild(iNot);
      buttonNotif.innerHTML += MSG.NOTIFICATIONS;
      helpContent.appendChild(buttonNotif);
      buttonNotif.addEventListener(EVENT.CLICK, () => {
        this.rootPanel(new JSF.AonJsfHelpNotification());
        this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_OPEN, {}));
        let rightPanel = this.closest('aon-right-panel');
        rightPanel?.close?.();
      });
      // Certificaciones
      let buttonCertificacions = this.createElement(TAG.BUTTON);
      // Icono
      let iCer  = new AonIcon();
      iCer.icon = "award";
      buttonCertificacions.appendChild(iCer);
      buttonCertificacions.innerHTML += MSG.CERTIFICATIONS;
      helpContent.appendChild(buttonCertificacions);
      buttonCertificacions.addEventListener(EVENT.CLICK, () => {
        this.rootPanel(new AonCertification());
        this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_OPEN, {}));
        let rightPanel = this.closest('aon-right-panel');
        rightPanel?.close?.();
      });

      getSupport().then(r => {
        rightPanelSwitchSupportButton.checked = r.value;
      });
      rightPanelSwitchSupportButton.addEventListener(EVENT.CHANGE, () => {
        let data = {value: rightPanelSwitchSupportButton.isChecked()};
        setSupport(data).then(r => {});
      });

      // Comapny
      if(loadCompanies){
        this.buildCompany(helpContent);
      }

      // Version
      const hideVersion = this.customConfig?.params?.AON_HIDE_VERSION === "true";
      if (!hideVersion) {
        let version = MSG.VERSION + ": " + this.manifest.build_date;
        let divInfo = this.createElement(TAG.DIV);
        divInfo.className = "divInfo";
        divInfo.innerHTML = `<small id="aonManifest">${version}</small>`;
        helpContent.appendChild(divInfo);
      }
	}

	buildCompany(helpContent) {
  
    if (this.dur.getDomain() === null) return;

    const params = this.customConfig?.params || {};

    const customTitle = params.AON_CUSTOMIZE_TITLE || "AON SOLUTIONS S.L.";
    const customPhone = params.AON_CUSTOMIZE_SUPPORT_PHONE || "(+34) 900 831 205";
    const customEmail = params.AON_CUSTOMIZE_SUPPORT_EMAIL || "soporte@aonSolutions.es";

    let contactCard       = new AonCard();
    contactCard.id        = this.ABOUT_CONTACT_CARD;
    contactCard.title     = MSG.CONTACT_DATA2;
    contactCard.className = "right-panel-contact-card";

    if ((this.dur.isDomainPayer()) || (this.dur.hasOffice())) {
      helpContent.appendChild(contactCard);

      contactCard.setContent(this.buildSupportData(customTitle, MSG.COMPANY, MATERIAL_ICONS.BUSINESS, CSS.AON_SUPPORT_NAME));
      contactCard.addContent(this.buildSupportData(customPhone, MSG.PHONE, MATERIAL_ICONS.PHONE, CSS.AON_SUPPORT_TELEPHONE));
      contactCard.addContent(this.buildSupportData(customEmail, "Atención a usuarios", MATERIAL_ICONS.MAIL, CSS.AON_SUPPORT_USERS_EMAIL));

    } else if (this.dur.getParentDomain() !== null) {
      const parentDomain = this.dur.getParentDomain();
      const parentId     = parentDomain.id;
      const parentName   = parentDomain.name;

      getParentCompany({ parentId, parentName }).then(r => {
        let name  = r.name;
        let phone = r.media?.find(item => item.media === "fixed_phone")?.value || "Teléfono no encontrado";
        let email = r.media?.find(item => item.media === "email")?.value || "Email no encontrado";

        helpContent.appendChild(contactCard);
        contactCard.setContent(this.buildSupportData(name, MSG.COMPANY, MATERIAL_ICONS.BUSINESS));
        contactCard.addContent(this.buildSupportData(phone, MSG.PHONE, MATERIAL_ICONS.PHONE));
        contactCard.addContent(this.buildSupportData(email, "Correo electrónico", MATERIAL_ICONS.MAIL));
      });
    }

    // Schedule card siempre al final
    let scheduleCard       = new AonCard();
    scheduleCard.id        = this.SCHEDULE_CONTACT_CARD;
    scheduleCard.title     = MSG.SCHEDULE;
    scheduleCard.className = "right-panel-schedule-card";
    helpContent.appendChild(scheduleCard);

    scheduleCard.setContent(this.buildSupportData(MSG.WEEK_SCHEDULE, MSG.WEEK_SCHEDULE, MATERIAL_ICONS.SCHEDULE, CSS.AON_WEEK_SCHEDULE));
    scheduleCard.addContent(this.buildSupportData(MSG.WEEK_FRIDAY_SCHEDULE, MSG.WEEK_SCHEDULE, MATERIAL_ICONS.SCHEDULE, CSS.AON_WEEK_FRIDAY_SCHEDULE));
  }


	buildSupportData(value, title, icon, className) {
		let div 		= this.createDiv();
		div.className	= "help-row";
		div.style.title = title;
		if(!this.isNewStyle()){
          div.style.marginTop = '10px';
          div.style.display = "flex";

          let i = this.createElement(TAG.I);
          i.className = CSS.MATERIAL_ICONS + " mailIcon";
          i.id = "aonContactIcon" + this.cont;
          i.style.marginRight = '5px';
          i.style.verticalAlign = "middle";
          i.setAttribute("data-icon", icon);
          i.innerHTML= icon;
          div.appendChild(i);
		} else {
          let i  = new AonIcon();
          i.icon = icon;
          div.appendChild(i);
        }

		let span = this.createDiv();
		span.className = `${CSS.AON_CARD_TEXT} ${className}`;
		
		if(className==null || this.isNewStyle()){
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