import { AonElement } from './AonElement.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { AonIcon } from './aon-icon.js';
import { AonIconButton } from './aon-icon-button.js';


export class AonDialog extends AonElement {

	DIALOG;
	MAIN;
	TITLE;
	CONTENT;
	ACTION;
	CANCEL;
	ACCEPT;
	BUTTON_LEFT;
	BUTTON_RIGHT;

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


	attributeChangedCallback(name, oldValue, newValue) {
		if ('width' === name) {
			let main = this.getMain();
			if (main) main.style.width = newValue;
		} else if ('type' === name) {
			this.buildByType();
		} 
	}

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
		this.buildByType();
	}

	buildByType(){
		if(this.isTypeBlank()) 
			this.buildBlank();
		else if(this.isTypeMenu()) 
			this.buildMenu();
		else if(this.isTypeFullScreen()) 
			this.buildFullScreen();
		else 
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
		// const action = this.getElement(this.ACTION)
		// if(action) action.innerHTML = '';
	}

	buildBlank() {
		this.innerHTML = "";

		this.buildMenu();

		this.onClick();
	}

	buildMenu() {
		let dialog = this.createElement(TAG.DIV);
		dialog.id = this.DIALOG;
		dialog.className = "aonDialog";
		dialog.style.backgroundColor = 'transparent';
		dialog.style.paddingTop = '0px';
		this.appendChild(dialog);

		let content = this.createElement(TAG.DIV);
		content.id = this.CONTENT;
		content.className = "aonDialogContent";
		content.style.position = 'absolute';
		content.style.width = '200px';
		content.style.padding = '0px';
		dialog.appendChild(content);

		this.onClick();
	}

	build() {
		this.innerHTML = "";
		let dialog = this.createElement(TAG.DIV);
		dialog.id = this.DIALOG;
		dialog.className = "aonDialog";
		this.appendChild(dialog);

		let main =  this.createElement(TAG.DIV);
		main.id  = this.MAIN;
		main.className = "aonDialogContent";
		dialog.appendChild(main);


		let closeDesktop = this.createElement(TAG.LABEL);
		closeDesktop.title = MSG.CLOSE;
		closeDesktop.innerHTML = "×";
		closeDesktop.onclick = () => this.close();
		closeDesktop.style = `
			color: grey;
			font-size: 30px;
			text-decoration: none;
			float: right;
			margin-top: -25px;
			margin-right: -13px;
			cursor: pointer;
			padding: 10px;
		`
		main.appendChild(closeDesktop);

		let title = this.createElement(TAG.DIV);
		title.style.fontSize = "22px";
		title.style.fontWeight = "bold";
		title.style.marginBottom = "10px";
		title.id = this.TITLE;
		main.appendChild(title);

		let content = this.createElement(TAG.DIV);
		content.id = this.CONTENT;
		content.style.marginBottom = '20px';
		main.appendChild(content);


		let action = this.createElement(TAG.DIV);
		action.id = this.ACTION;
		main.appendChild(action);


		if (this.hasAttribute('width')) {
			main.style.width = this.getAttribute('width');
		}

		if (this.isTypeMenu()) {
			dialog.style.backgroundColor = 'transparent';
			dialog.style.paddingTop = '0px';
			main.style.position = 'absolute';
			main.style.width = '200px';
			main.style.padding = '0px';
		}	

		action.style.textAlign = 'right';
		
		this.onClick();
	}

	buildFullScreen() {
		this.innerHTML = "";
		let dialog = this.createElement(TAG.DIV);
		dialog.id = this.DIALOG;
		dialog.className = "aonDialog";
		this.appendChild(dialog);

		let main =  this.createElement(TAG.DIV);
		main.id  = this.MAIN;
		main.className = "aonDialogContent";
		dialog.appendChild(main);

		let action = this.createElement(TAG.DIV);
		action.id = this.ACTION;

		let title = this.createElement(TAG.DIV);
		title.style.fontSize = "22px";
		title.style.fontWeight = "bold";
		title.style.fontWeight = "10px";
		title.id = this.TITLE;

		let content = this.createElement(TAG.DIV);
		content.id = this.CONTENT;
		content.style.marginBottom = '20px';

		main.appendChild(action);
		main.appendChild(content);

		let divButtonsLeft = this.createElement(TAG.DIV);
		divButtonsLeft.id = this.BUTTON_LEFT;
		action.appendChild(divButtonsLeft);

		action.appendChild(title);//ADD TITLE

		let divButtonRight = this.createElement(TAG.DIV);
		divButtonRight.id = this.BUTTON_RIGHT;
		divButtonRight.style.marginLeft = "auto";
		action.appendChild(divButtonRight);

		//--- CHANGE STYLES --------------------------------
		this.style.position = "relative";
		//----------MAIN
		main.style.padding = '0';
		main.style.top = "0";
		main.style.position = "fixed";
		main.style.width = "100%";
		main.style.height = "100%";

		//----------TITLE
		title.style.lineHeight = "41px";

		//----------ACTION
		action.style.display = "flex";

		//---------CONTENT
		content.style.padding = "20px";

		this.addAction({
			id:this.CANCEL,
			title:MSG.CLOSE,
			icon:MATERIAL_ICONS.ARROW_BACK,
			position: "left",
		}, () => this.close());
		
		this.onClick();
	}

	getDialog(){
		return this.getElement(this.DIALOG);
	}

	onClick(){
		const dialog = this.getDialog();
		if(dialog){
			dialog.onclick = ({target}) => {
				if (target === dialog && this.autoclose) {
					this.close();
				}
			}
		}
	}

	isTypeMenu() {
		return this.hasAttribute(CONSTANT.TYPE) && 'menu' === this.getAttribute(CONSTANT.TYPE);
	}

	isTypeBlank() {
		return this.hasAttribute(CONSTANT.TYPE) && 'blank' === this.getAttribute(CONSTANT.TYPE);
	}

	isTypeFullScreen() {
		return this.hasAttribute(CONSTANT.TYPE) && 'fullscreen' === this.getAttribute(CONSTANT.TYPE);
	}

	open() {
		let dialog = this.getDialog();
		dialog.style.display = 'block';

		if(this.isTypeFullScreen()){
			this.getElement("aonMobileMenuSidenav").style.zIndex = "-1";
		}
	}

	close() {
		this.autoclose = true;
		let dialog = this.getDialog();
		if(dialog) dialog.style.display = 'none';
		this.dispatchEvent(new CustomEvent(EVENT.CLOSE));

		if(this.isTypeFullScreen()){
			this.getElement("aonMobileMenuSidenav").style.zIndex = "0";
			this.type =""; // BUILD HTML
		}
	}

	getContent() {
		return this.getElement(this.CONTENT);
	}

	getButtonAccept(){
		return this.getElement(this.ACCEPT);
	}
	
	getMain(){
		return this.getElement(this.MAIN);
	}

	setContent(widget, top, left, width) {
		let content = this.getContent();
		content.innerHTML = '';
		content.appendChild(widget);
		if(top && left) {
			let dialog = this.getElement(this.DIALOG);
			content.style.top = top + 'px' || '90px';
			content.style.left = (left > (dialog.offsetWidth/2) ? left - 180 : left)+'px' ;
		}

		if(width) {
			content.style.width = width;
		}
	}

	setContentHTML(html) {
		this.getContent().innerHTML = html;
	}

	setMenuOptions(options, top, left) {
		let dialog = this.getDialog();
		let content = this.getContent()
		content.style.top = top || '90px';
		content.style.left = left > (dialog.offsetWidth / 2) ? left - 180 : left;
		content.innerHTML = '';
		let ul = document.createElement(TAG.UL);
		ul.className = CSS.AON_UL;
		content.appendChild(ul);
		options.forEach((item, i) => {
			let li = document.createElement(TAG.LI);
			li.style.padding = '10px';
			li.style.cursor = 'pointer';
			ul.appendChild(li);

			if(item.aonIcon) {
				let ai = document.createElement(TAG.SPAN);
				ai.style.verticalAlign = 'middle';
				li.appendChild(ai);

				let aonIcon = new AonIcon();
				aonIcon.icon = item.aonIcon;
				ai.appendChild(aonIcon);
			} else if(item.icon) {
				let ic = document.createElement(TAG.I);
				ic.className = 'material-icons';
				ic.style.verticalAlign = 'middle';
				ic.innerHTML = item.icon;
				li.appendChild(ic);
			}

			let span = document.createElement(TAG.SPAN);
			span.style.marginLeft = '5px';
			span.innerHTML = item.name;
			li.appendChild(span);
			li.addEventListener(EVENT.CLICK, () => {
				this.close();
				item.fn();
			});
		});
	}

	setTitle(title) {
		if(title) this.getElement(this.TITLE).innerHTML = title;
	}

	getButtonLeft(){
		return this.getElement(this.BUTTON_LEFT);
	}

	getButtonRight(){
		return this.getElement(this.BUTTON_RIGHT);
	}

	/**
	 * 
	 * @param {Object} id, title, icon, aonIcon(Optional), position(left, right) 
	 * @param {Function} fn 
	 */
	addAction({id, title, icon, aonIcon, position}, fn){
		let btnMain = position === "left" ? this.getButtonLeft() : this.getButtonRight();

		let btn = this.getElement(id);
		if(btn) btn.remove();

		if(btnMain) {
			btn = new AonIconButton();
			btn.id = id;
			btn.icon = icon || aonIcon; 
			btn.title = title;
			btn.background = "transparent";
			if(fn){
				btn.addEventListener(EVENT.CLICK, fn);
			}
			btnMain.appendChild(btn);
			return btn;
		} else {
			btn = this.createElement(TAG.BUTTON);
			btn.id = id;
			btn.className = 'aonButton';
			btn.style.marginRight = "10px";
			btn.innerHTML = title;
			this.getElement(this.ACTION).appendChild(btn);
			btn.addEventListener(EVENT.CLICK, fn);
		}
	}

	addCancelAction(fn, close=true) {
		let btn = undefined;
		if(this.isTypeFullScreen()){
			btn = this.addAction({
				id:this.CANCEL,
				title:MSG.CLOSE,
				icon:MATERIAL_ICONS.ARROW_BACK,
				position: "left",
			});
		} else {
			btn = this.getElement(this.CANCEL);
			if(btn) btn.remove();
			btn = this.createElement(TAG.BUTTON);
			btn.id = this.CANCEL;
			btn.className = 'aonButton';
			btn.style.backgroundColor="grey";
			btn.style.marginRight = "10px";
			btn.innerHTML = MSG.CANCEL;
			this.getElement(this.ACTION).appendChild(btn);
		}

		btn.addEventListener(EVENT.CLICK, (ev) => {
			fn(ev);
			if(close){
				this.close();
			}
		});

		return btn;

	}

	addAcceptAction(fn) {
		let accept = this.createButtonAccept();
		if(accept){
			accept.addEventListener(EVENT.CLICK, (ev) => {
				fn(ev);
				this.close();
			});
		}
	}
	
	createButtonAccept(title=undefined){
		let btn = undefined;
		if(this.isTypeFullScreen()){
			btn = this.addAction({
				id:this.ACCEPT,
				title:title || MSG.ACCEPT,
				icon:MATERIAL_ICONS.DONE,
				position: "right",
			});
		} else {
			btn = this.getElement(this.ACCEPT);
			if(btn) btn.remove();
			btn = this.createElement(TAG.BUTTON);
			btn.id = this.ACCEPT;
			btn.className = 'aonButton';
			btn.innerHTML = title || MSG.ACCEPT;
			btn.title = title || MSG.ACCEPT;
			btn.style.marginLeft= "auto";
			let divAction = this.getElement(this.ACTION);
			divAction.style.display = "flex";
			divAction.style.justifyContent = "space-between";
			divAction.appendChild(btn);
		}
		
		return btn;
	}

	addSendAction(fn, title) {
		let button = this.createButtonAccept(title);
		if(button){
			button.classList.add('buttonload')
			button.addEventListener('click', (ev) => {
				ev.stopPropagation();
				ev.preventDefault();
				fn(ev);
			});
			return button;
		}
	}

	// loadingButton(loading){
	// 	let accept = this.getElement(this.ACCEPT);
		// if(loading) accept.classList.add("button--loading"); 
		// else accept.classList.remove("button--loading");
	// 	if(accept){
	// 		let id = "iconDialogSend";
	// 		let icon = this.createElement('i');
	// 		icon.id = id;
	// 		icon.classList.add("fa fa-refresh fa-spin");
	// 		accept.appendChild(icon);
	// 	}
	// }
}
if(!window.customElements.get('aon-dialog')){
	window.customElements.define('aon-dialog', AonDialog);
}
