import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "aonsolutions/environments/environments.js";
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import { AonCard } from 'aonsolutions/components/aon-card.js';
import * as LS from 'aonsolutions/services/localStorageService.js';
import { Language } from 'aonsolutions/models/Language.js';
import {  getManifest} from "aonsolutions/services/service.js";
import { AonSwitch } from "aonsolutions/components/aon-switch.js";
import { getSupport, setSupport } from 'aonsolutions/services/supportService.js';
export class AonHelp extends AonElement {

	RIGHT_PANEL;
	CLOSE_BUTTON;
	HELP_TEXT;
	SUPPORT_SWITCH;
	LANG_CARD;
	ABOUT_CONTACT_CARD;
	SCHEDULE_CONTACT_CARD;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	connectedCallback() {
		this.initialize();
		this.build();
	}

	initialize() {
		this.id = this.id || 'AonHelp';
		this.RIGHT_PANEL = this.id + 'HelpRightPanel';
		this.CLOSE_BUTTON = this.id + 'HelpCloseButton';
		this.HELP_TEXT = this.id + 'HelpText';
		this.SUPPORT_SWITCH = this.id + 'HelpSwitchSupport';
		this.LANG_CARD = this.id + 'HelpLangCard';
		this.ABOUT_CONTACT_CARD = this.id + 'HelpAboutContactCard';
		this.SCHEDULE_CONTACT_CARD = this.id + 'HelpScheduleContactCard';
	}

	build() {
		LS.setToken('AONd95770f269e711eb94390242ac130002')
		this.clear();
		let rightPanel = this.createDiv(this.RIGHT_PANEL, "rightPanel");
		rightPanel.style.width = '320px';
		rightPanel.style.marginTop = this.getElement("aonMenuTopnav").offsetHeight;
		rightPanel.style.backgroundColor = '#faf9f8';
		rightPanel.style.visibility = "hidden";
		this.appendChild(rightPanel);
 
		let rightPanelCloseButton = new AonIconButton(); 
		rightPanelCloseButton.id = this.CLOSE_BUTTON;
		rightPanelCloseButton.icon ='close';
		rightPanelCloseButton.style.cursor = "pointer";
		rightPanelCloseButton.style.position = "fixed";
		rightPanelCloseButton.style.right = '10px';
		rightPanelCloseButton.style.visibility = "hidden";
		rightPanel.appendChild(rightPanelCloseButton);
		
		let rightPanelHelpText = this.createElement(TAG.H1);
		rightPanelHelpText.id = this.HELP_TEXT;
		rightPanelHelpText.innerHTML = MSG.HELP;
		rightPanelHelpText.style.marginTop = '18px';
		rightPanelHelpText.style.marginLeft = '10px'
		rightPanelHelpText.style.marginBottom = '20px'
		rightPanelHelpText.style.fontSize = '16px';
		rightPanelHelpText.style.fontWeight = '500';
		rightPanelHelpText.style.visibility = "hidden";
		rightPanel.appendChild(rightPanelHelpText);


		let span = this.createSpan();
		span.innerHTML = MSG.SUPPORT;
		span.style.marginLeft = '10px';
		rightPanel.appendChild(span);

		let rightPanelSwitchSupportButton = new AonSwitch();
		rightPanelSwitchSupportButton.id = this.SUPPORT_SWITCH;
		rightPanelSwitchSupportButton.style.marginLeft = '10px';
		rightPanelSwitchSupportButton.style.right = '30px';
		rightPanelSwitchSupportButton.style.position = 'absolute';
		rightPanelSwitchSupportButton.style.visibility = "hidden";
		rightPanelSwitchSupportButton.style.top = "52px";
		rightPanel.appendChild(rightPanelSwitchSupportButton);

		getSupport().then(r => {
			rightPanelSwitchSupportButton.checked = r.value;

		})
		rightPanelSwitchSupportButton.addEventListener(EVENT.CHANGE, () => {
			let data = {value: rightPanelSwitchSupportButton.isChecked()}
			setSupport(data).then(r => {})
		});

		let rightPanelLangCard = new AonCard();
		rightPanelLangCard.id = this.LANG_CARD;
		rightPanelLangCard.title = "Cambiar idioma";
		rightPanelLangCard.style.visibility = "hidden";
		rightPanelLangCard.style.width = "90%";
		rightPanelLangCard.style.height = "fit-content";
		rightPanelLangCard.style.marginLeft = "10px";
		rightPanel.appendChild(rightPanelLangCard);

		let cardDivv = this.getElement(rightPanelLangCard.CARD);
		cardDivv.style.boxShadow = '0 2px 4px rgba(0,0,0,.1)';
		cardDivv.style.borderRadius = '2px';

		let divGenerall = this.createDiv();
		divGenerall.appendChild(this.buildLanguageData(MSG.SPANISH , Language.SPANISH));
		divGenerall.appendChild(this.buildLanguageData(MSG.ENGLISH , Language.ENGLISH));
		divGenerall.appendChild(this.buildLanguageData(MSG.DEUTSCH , Language.DEUTSCH));
		divGenerall.appendChild(this.buildLanguageData(MSG.BASQUE , Language.BASQUE));
		divGenerall.appendChild(this.buildLanguageData(MSG.CATALAN , Language.CATALAN));
		divGenerall.appendChild(this.buildLanguageData(MSG.GALICIAN , Language.GALICIAN));
		console.log(JSON.stringify(divGenerall));
		rightPanelLangCard.setContent(divGenerall);

		let rightPanelAboutContactCard = new AonCard();
		rightPanelAboutContactCard.id = this.ABOUT_CONTACT_CARD;
		rightPanelAboutContactCard.title = "Datos de contacto";
		rightPanelAboutContactCard.style.visibility = "hidden";
		rightPanelAboutContactCard.style.width = "90%";
		rightPanelAboutContactCard.style.height = "fit-content";
		rightPanelAboutContactCard.style.marginLeft = "10px";
		rightPanel.appendChild(rightPanelAboutContactCard);

		let cardDiv = this.getElement(rightPanelAboutContactCard.CARD);
		cardDiv.style.boxShadow = '0 2px 4px rgba(0,0,0,.1)';
		cardDiv.style.borderRadius = '2px';

		let divGeneral = this.createDiv();
		divGeneral.appendChild(this.buildSupportData("(+34) 900 831 205", MSG.PHONE, MATERIAL_ICONS.PHONE));
		divGeneral.appendChild(this.buildSupportData("soporte@aonSolutions.es", "Atención a usuarios", MATERIAL_ICONS.MAIL));
		divGeneral.appendChild(this.buildSupportData("comercial@aonSolutions.es", "Ventas y contratación", MATERIAL_ICONS.MAIL));
		divGeneral.appendChild(this.buildSupportData("administración@aonSolutions.es", "Facturación, cobros y pago", MATERIAL_ICONS.MAIL));
		rightPanelAboutContactCard.setContent(divGeneral);

		let rightPanelAboutScheduleCard = new AonCard();
		rightPanelAboutScheduleCard.id = this.SCHEDULE_CONTACT_CARD;
		rightPanelAboutScheduleCard.title = "Horario";
		rightPanelAboutScheduleCard.style.visibility = "hidden";
		rightPanelAboutScheduleCard.style.width = "90%";
		rightPanelAboutScheduleCard.style.height = "fit-content";
		rightPanelAboutScheduleCard.style.marginLeft = "10px";
		rightPanel.appendChild(rightPanelAboutScheduleCard);

		let cardDiv2 = this.getElement(rightPanelAboutScheduleCard.CARD);
		cardDiv2.style.boxShadow = '0 2px 4px rgba(0,0,0,.1)';
		cardDiv2.style.borderRadius = '2px';

		let divGeneral2 = this.createDiv();
		divGeneral2.appendChild(this.buildSupportData("Lunes a jueves de 8:00 a 15:00", "Lunes a jueves de 8:00 a 15:00", MATERIAL_ICONS.SCHEDULE));
		divGeneral2.appendChild(this.buildSupportData("Viernes de 8:00 a 14:00", "Viernes de 8:00 a 14:00", MATERIAL_ICONS.SCHEDULE));
		rightPanelAboutScheduleCard.setContent(divGeneral2);
		
		getManifest().then(
		  (manifest) => {
				let version = MSG.VERSION + ": " + manifest.build_date;
				let divInfo = this.createElement(TAG.DIV);
				divInfo.style.color = '#666';
				divInfo.style.fontSize = '9px';
				divInfo.style.borderTop = '1px solid #ddd';
				divInfo.style.marginTop = '10px';
				divInfo.style.padding = '15px';
				divInfo.innerHTML = `
				  <span>
					<a target="_blank" class="aonLink" href="http://www.aonsolutions.es">
					  aonSolutions
					</a> ${MSG.REGISTERED_TRADEMARK_AON}
				  </span>
				  <div id="aonManifest">${version}</div>`;
				rightPanel.appendChild(divInfo);
			}
		);

		rightPanelCloseButton.addEventListener(EVENT.CLICK, () => {
			this.close();
		});

	}

	buildSupportData(value, title, icon) {
		let div = this.createDiv();
		div.style.marginTop = '10px';
		div.style.title = title;

		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS;
		i.style.marginRight = '5px';
		i.style.verticalAlign = "middle";
		i.innerHTML= icon;
		div.appendChild(i);

		let span = this.createSpan();
		span.className = CSS.AON_CARD_TEXT;
		span.innerHTML = value;
		div.appendChild(span);

		return div;
	}

	buildLanguageData(value,language) {
		let div = this.createDiv();
		div.style.marginTop = '5px';
		div.style.title = "Idioma";
		div.style.cursor = "pointer";

		
		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS;
		i.style.marginRight = '5px';
		i.style.verticalAlign = "middle";
		div.appendChild(i);
		
		

		let span = this.createElement(TAG.SPAN);
		span.className = CSS.AON_CARD_TEXT;
		span.innerHTML = value;
		div.appendChild(span);

		if(language == LS.getLanguage()) {
			i.innerHTML = "done";
			span.style.fontWeight = "bold";
		}else{
			i.innerHTML= "language";
		}

		div.addEventListener(EVENT.CLICK, () => {
			LS.setLanguage(language);
		})
	
		return div;		
	}
	

	toogle() {
		if(this.style.visibility === "visible") {
			this.close();
		} else this.open();
	}

	open(){
		this.getRightPanel().style.visibility = "visible";
		this.getCloseButton().style.visibility = "visible";
		this.getHelpText().style.visibility = "visible";
		this.getHelpLangCard().style.visibility = "visible";
		this.getSupportSwitch().style.visibility = "visible";
		this.getAboutContact().style.visibility = "visible";
		this.getScheduleContact().style.visibility = "visible";
	}

	close(){
		this.getRightPanel().style.visibility = "hidden";
		this.getCloseButton().style.visibility = "hidden";
		this.getHelpText().style.visibility = "hidden";
		this.getHelpLangCard().style.visibility = "hidden";
		this.getSupportSwitch().style.visibility = "hidden";
		this.getAboutContact().style.visibility = "hidden";
		this.getScheduleContact().style.visibility = "hidden";
		this.dispatchEvent(new Event(EVENT.CLOSE));
	}

	getRightPanel() {
		return this.getElement(this.RIGHT_PANEL);
	}

	getCloseButton() {
		return this.getElement(this.CLOSE_BUTTON);
	}

	getHelpText() {
		return this.getElement(this.HELP_TEXT);
	}

	getHelpLangCard() {
		return this.getElement(this.LANG_CARD);
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