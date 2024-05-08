import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "aonsolutions/environments/environments.js";
import { AonCard } from 'aonsolutions/components/aon-card.js';
import * as LS from 'aonsolutions/services/localStorageService.js';
import {  getManifest} from "aonsolutions/services/service.js";
import { AonSwitch } from "aonsolutions/components/aon-switch.js";
import { getSupport, setSupport } from 'aonsolutions/services/supportService.js';
export class AonHelp extends AonElement {

	SUPPORT_SWITCH;
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
		this.SUPPORT_SWITCH = this.id + 'SwitchSupport';
		this.ABOUT_CONTACT_CARD = this.id + 'AboutContactCard';
		this.SCHEDULE_CONTACT_CARD = this.id + 'ScheduleContactCard';
	}

	build() {
		LS.setToken('AONd95770f269e711eb94390242ac130002')

		let rightPanel = this.getElement("aonRightPanel");

		let span = this.createSpan();
		span.innerHTML = MSG.SUPPORT;
		span.style.marginLeft = '10px';
		this.appendChild(span);

		let rightPanelSwitchSupportButton = new AonSwitch();
		rightPanelSwitchSupportButton.id = this.SUPPORT_SWITCH;
		rightPanelSwitchSupportButton.style.marginLeft = '10px';
		rightPanelSwitchSupportButton.style.right = '30px';
		rightPanelSwitchSupportButton.style.position = 'absolute';
		rightPanelSwitchSupportButton.style.top = "70px";
		
		this.appendChild(rightPanelSwitchSupportButton);

		getSupport().then(r => {
			rightPanelSwitchSupportButton.checked = r.value;

		})
		rightPanelSwitchSupportButton.addEventListener(EVENT.CHANGE, () => {
			let data = {value: rightPanelSwitchSupportButton.isChecked()}
			setSupport(data).then(r => {})
		});


		let rightPanelAboutContactCard = new AonCard();
		rightPanelAboutContactCard.id = this.ABOUT_CONTACT_CARD;
		rightPanelAboutContactCard.title = MSG.CONTACT_DATA2;
		rightPanelAboutContactCard.style.width = "90%";
		rightPanelAboutContactCard.style.height = "fit-content";
		rightPanelAboutContactCard.style.marginLeft = "10px";
		this.appendChild(rightPanelAboutContactCard);

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
		rightPanelAboutScheduleCard.title = MSG.SCHEDULE;

		rightPanelAboutScheduleCard.style.width = "90%";
		rightPanelAboutScheduleCard.style.height = "fit-content";
		rightPanelAboutScheduleCard.style.marginLeft = "10px";
		this.appendChild(rightPanelAboutScheduleCard);

		let cardDiv2 = this.getElement(rightPanelAboutScheduleCard.CARD);
		cardDiv2.style.boxShadow = '0 2px 4px rgba(0,0,0,.1)';
		cardDiv2.style.borderRadius = '2px';

		let divGeneral2 = this.createDiv();
		divGeneral2.appendChild(this.buildSupportData(MSG.WEEK_SCHEDULE,MSG.WEEK_SCHEDULE, MATERIAL_ICONS.SCHEDULE));
		divGeneral2.appendChild(this.buildSupportData(MSG.WEEK_FRIDAY_SCHEDULE, MSG.WEEK_SCHEDULE,MATERIAL_ICONS.SCHEDULE));
		rightPanelAboutScheduleCard.setContent(divGeneral2);
		
		getManifest().then(
		  (manifest) => {
				let version = MSG.VERSION + ": " + manifest.build_date;
				let divInfo = this.createElement(TAG.DIV);
				divInfo.style.color = '#666';
				divInfo.style.fontSize = '9px';
				divInfo.style.marginTop = '10px';
				divInfo.style.padding = '15px';
				divInfo.innerHTML = `
				  <span>
					<a target="_blank" class="aonLink" href="http://www.aonsolutions.es">
					  aonSolutions
					</a> ${MSG.REGISTERED_TRADEMARK_AON}
				  </span>
				  <div id="aonManifest">${version}</div>`;
				  this.appendChild(divInfo);
			}
		);
	
	}

	buildSupportData(value, title, icon) {
		let div = this.createDiv();
		div.style.marginTop = '10px';
		div.style.title = title;
		div.style.display = "flex";

		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS;
		i.style.marginRight = '5px';
		i.style.verticalAlign = "middle";
		i.innerHTML= icon;
		div.appendChild(i);

		let span = this.createDiv();
		span.className = CSS.AON_CARD_TEXT;
		span.innerHTML = value;
		div.appendChild(span);

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