import { AonElement } from './AonElement.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { AonIcon } from './aon-icon.js';
import { AonIconButton } from './aon-icon-button.js';


export class AonNewDialog extends AonElement {

	DIALOG;
	MAIN;
	TITLE;
	CONTENT;
	ACTION;
	CANCEL;
	ACCEPT;
	BUTTON_LEFT;
	BUTTON_RIGHT;

	MESSAGE;

	static get observedAttributes() {
		return ['width', 'autoclose', 'type'];
	}

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get title() {
		return this.getAttribute(CONSTANT.TITLE);
	}

	set title(title) {
		this.setAttribute(CONSTANT.TITLE, title);
	}

	get type() {
		return this.getAttribute(CONSTANT.TYPE);
	}

	set type(type) {
		this.setAttribute(CONSTANT.TYPE, type);
	}

	get width() {
		return this.getAttribute('width');
	}

	set width(width) {
		this.setAttribute('width', width);
	}

	get autoclose() {
		return this.getAttribute('autoclose') == "true";
	}

	set autoclose(autoclose) {
		this.setAttribute('autoclose', autoclose);
	}

	constructor(message) {
		super();
		this.MESSAGE = message;
	}

	connectedCallback() {
		this.initialize();
		this.build();
	}

	initialize() {
		this.DIALOG = this.id + 'Dialog';
		this.MAIN = this.DIALOG + 'Main';
		this.TITLE = this.DIALOG + CONSTANT.TITLE;
		this.CONTENT = this.DIALOG + 'Content';
		this.ACTION = this.DIALOG + 'Action';
		this.CANCEL = this.ACTION + 'Cancel';
		this.ACCEPT = this.ACTION + 'Accept';
		this.BUTTON_LEFT = this.DIALOG + 'DivButtonsLeft';
		this.BUTTON_RIGHT = this.DIALOG + 'DivButtonsRight'; 
		this.autoclose = this.autoclose || true;
	}

	clear() {
		const title = this.getElement(this.TITLE);
		const content = this.getContent();
		if(title) title.innerHTML = '';
		if(content) content.innerHTML = '';
	}

	build() {
		// Clear
		this.innerHTML = "";
		this.className = CSS.DIALOG_OVERLAY;
		
		// Content
		let dialog = this.createElement(TAG.DIV);
		dialog.id = this.DIALOG;
		dialog.className = CSS.DIALOG_CONTENT;
		this.appendChild(dialog);

		let message = this.createElement(TAG.DIV);
		message.id = this.CONTENT;
		message.innerHTML = this.MESSAGE;
		message.className = CSS.DIALOG_MESSAGE;
		dialog.appendChild(message);

		let buttons = this.createElement(TAG.DIV);
		buttons.id = "dialogButtons";
		buttons.className = CSS.DIALOG_BUTTONS;
		dialog.appendChild(buttons);

		this.open();
	}

	getDialog(){
		return this.getElement(this.DIALOG);
	}

	open() {
		let dialog = this.getDialog();
		dialog.style.display = 'flex';
	}

	getButtons() {
		return this.getElement("dialogButtons");
	}

	createAcceptButton(fn){
		let buttons = this.getButtons();

		let acceptButton = this.createElement(TAG.BUTTON);
		acceptButton.id  = this.id + "AcceptButton";
		acceptButton.innerHTML = MSG.ACCEPT;
		acceptButton.className = CSS.DIALOG_ACCEPT;
		acceptButton.addEventListener(EVENT.CLICK, fn);
		buttons.appendChild(acceptButton);
	}

	createCancelButton(fn){
		let buttons = this.getButtons();

		let cancelButton = this.createElement(TAG.BUTTON);
		cancelButton.innerHTML = MSG.CANCEL;
		cancelButton.className = CSS.DIALOG_CANCEL;
		cancelButton.addEventListener(EVENT.CLICK, fn);
		buttons.appendChild(cancelButton);
	}

}
if(!window.customElements.get('aon-new-dialog')){
	window.customElements.define('aon-new-dialog', AonNewDialog);
}
