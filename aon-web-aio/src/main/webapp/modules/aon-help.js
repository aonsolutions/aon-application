import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "aonsolutions/environments/environments.js";
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import { AonCard } from 'aonsolutions/components/aon-card.js';
import { AonSwitch } from "aonsolutions/components/aon-switch.js";

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
		rightPanelHelpText.style.fontSize = '16px';
		rightPanelHelpText.style.fontWeight = '500';
		rightPanelHelpText.style.paddingBottom = '20px';
		rightPanelHelpText.style.visibility = "hidden";
		rightPanel.appendChild(rightPanelHelpText);

		let rightPanelLangCard = new AonCard();
		rightPanelLangCard.id = this.LANG_CARD;
		rightPanelLangCard.title = "Cambiar idioma";
		rightPanelLangCard.style.visibility = "hidden";
		rightPanel.appendChild(rightPanelLangCard);

		let rightPanelSwitchSupportButton = new AonSwitch();
		rightPanelSwitchSupportButton.id = this.SUPPORT_SWITCH;
		rightPanelSwitchSupportButton.checked = true;
		rightPanelSwitchSupportButton.title = MSG.SUPPORT;
		rightPanelSwitchSupportButton.style.marginLeft = '10px';
		rightPanelSwitchSupportButton.style.visibility = "hidden";
		rightPanel.appendChild(rightPanelSwitchSupportButton);

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

		/*
		rightPanelAboutScheduleCard.setContentHTML(`
		<div id="aonContent:j_id36:j_id51" style="margin-left: 5%">
			<div>
				<i class="material-icons" style="vertical-align: middle;">schedule</i>
				<span class="aonCardText"> Lunes a jueves de 8:00 a 15:00</span>
			</div>
			<div style="margin-top:5px;">
				<i class="material-icons" style="vertical-align: middle;">schedule</i> 
				<span class="aonCardText"> Viernes de 8:00 a 14:00</span>
			</div>
		</div>
		`)
		*/

		rightPanelCloseButton.addEventListener(EVENT.CLICK, () => {
			this.close();
		});

	}

	buildSupportData(value, title, icon) {
		let div = this.createDiv();
		div.style.marginTop = '5px';
		div.style.title = title;

		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS;
		i.style.verticalAlign = "middle";
		i.innerHTML= icon;
		div.appendChild(i);

		let span = this.createSpan();
		span.className = CSS.AON_CARD_TEXT;
		span.innerHTML = value;
		div.appendChild(span);

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