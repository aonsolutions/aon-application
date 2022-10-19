import { AonElement } from './AonElement.js';
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonIconButton } from './aon-icon-button.js';

export class AonDialogMobile extends AonElement {

	DIALOG;
	TITLE;
	CONTENT;
	HEADER;
	BODY;
	HEADER_HEIGHT;
	INITIAL_DRAG_Y;
	START_TOP;
	Y_DRAG;

	static get observedAttributes() {
		return [];
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

	attributeChangedCallback(name, oldValue, newValue) {
	}

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
	    this.build();
	}

	initialize() {
		this.DIALOG = (this.id || Math.random().toString(36).substring(7)) + 'Dialog';
		this.CONTENT = this.DIALOG + 'Content';
		this.BODY = this.DIALOG + 'Body';
		this.HEADER = this.DIALOG + 'Header';
		this.TITLE = this.DIALOG + CONSTANT.TITLE;
		this.HEADER_HEIGHT = 0;
		this.INITIAL_DRAG_Y = 0;
		this.START_TOP = 0;
		this.Y_DRAG = 0;
	}

	clear() {
		const title = this.getTitle();
		const body = this.getBody();
		if(title) title.innerHTML = '';
		if(body) body.innerHTML = '';
	}

	build() {
		this.style.color = "#5f6368";
		let dialog = this.createElement(TAG.DIV);
		dialog.id = this.DIALOG;
		dialog.className = 'aonDialog';
		dialog.style.backgroundColor = 'rgba(0, 0, 0, 0.4)';
		this.appendChild(dialog);

		let content = this.createElement(TAG.DIV);
		content.id = this.CONTENT;
		content.style.position = 'fixed';
		content.style.backgroundColor = '#fefefe';
		content.style.width = '100%';
		content.style.transition = 'bottom .3s';
		content.style.bottom = '-300px';

		const eventOpt = { passive: false };
		content.addEventListener(EVENT.TOUCHSTART, (ev)=>  this.startDraggingEvent(ev), eventOpt);
		content.addEventListener(EVENT.TOUCHMOVE, (ev)=>  this.draggingEvent(ev), eventOpt);
		content.addEventListener(EVENT.TOUCHEND, (ev)=>  this.stopDraggingEvent(ev), eventOpt);
		dialog.appendChild(content);

		let header = this.createElement(TAG.DIV);
		header.id = this.HEADER;
		header.style.cursor = 'move';
		header.style.textAlign = 'center';
		content.appendChild(header);

		let thumb  = this.createElement(TAG.DIV);
		thumb.style.margin = 'auto';
		thumb.style.borderRadius = '50px';
		thumb.style.width = '35px';
		thumb.style.height = '4px';
		thumb.style.overflow = 'hidden';
		thumb.style.backgroundColor = '#dbdbdb';
		thumb.style.boxShadow = '0px 0px 0px 1px #dbdbdb';
		header.appendChild(thumb);

		let title = this.createElement(TAG.DIV);
		title.id = this.TITLE;
		title.style.margin = '12px 0 0';
		title.style.fontWeight = '600';
		header.appendChild(title);

		let body = this.createElement(TAG.DIV);
		body.id = this.BODY;
		body.style.padding = '10px 16px';
		content.appendChild(body);

		this.onclick = (ev) => {
			if (ev.target == dialog) {
				this.close();
			}
		}
	}

	open() {
		this.getElement('aonMobileMenuSidenav').style.zIndex = "-1";
		this.setDrag(this.START_TOP);
		this.getDialog().style.display = "block";
		setTimeout(()=>{
			let content = this.getContent();
			content.style.bottom = this.START_TOP;
			this.HEADER_HEIGHT = content.getBoundingClientRect().height;
		}, 1);
	}

	close() {
		this.getContent().style.bottom = ((this.HEADER_HEIGHT || 300)*-1)+"px";
		setTimeout(()=>	{
			this.getDialog().style.display = "none";
			this.getElement('aonMobileMenuSidenav').style.zIndex = "0";
		}, 400);
	}	

	getDialog() {
		return this.getElement(this.DIALOG);
	}

	getContent() {
		return this.getElement(this.CONTENT);
	}
		
	getHeader(){
		return this.getElement(this.HEADER);
	}

	getTitle(){
		return this.getElement(this.TITLE);
	}

	getBody(){
		return this.getElement(this.BODY);
	}

	setDrag(val){
		this.Y_DRAG = val;
		this.getContent().style.transform = `translateY(${val}px)`;
	}

	startDraggingEvent(ev){
		ev.stopPropagation();
		this.INITIAL_DRAG_Y = ev.touches[0].clientY;
	}

	stopDraggingEvent(ev){
		ev.stopPropagation();
		const half = this.HEADER_HEIGHT / 2;
		if( this.Y_DRAG >= half){
			this.close();
		} else {
			this.setDrag(this.START_TOP);
		}
	}

	draggingEvent(ev){
		ev.stopPropagation();
		const { clientY } = ev.touches[0];
		if(this.INITIAL_DRAG_Y>= clientY) return;

		this.setDrag(clientY - this.INITIAL_DRAG_Y);
	}

	setContentHTML(html) {
		let content = this.getBody();
		content.innerHTML = html;
	}

	setContent(element) {
		let content = this.getBody();
		content.appendChild(element);
	}

	setTitle(title) {
		if(title) this.getTitle().innerHTML = title;
	}

	addButtons(buttons) {
		let divMain = this.createElement(TAG.DIV);		
		divMain.classList.add(CSS.FLEX_WRAP, CSS.FLEX_JUSTIFY_BETWEEN);
		divMain.style.marginBottom = "15px";
		divMain.style.gap = "10px";
		this.setContent(divMain);

		buttons.forEach(button =>
			this.addButton(button, divMain)
		);
	}

	addButton(button, parent) {
		const color = button.permission ? button.backgroundColor : '#bbb';
		let icon = new AonIconButton();
		icon.classList.add(CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER);
		icon.id = button.id || Math.random().toString(36).substring(7);
		icon.icon = button.icon || 'help_outline';
		icon.color = button.color || 'white';
		icon.noHover = true;
		icon.background = color;
		icon.style.flexBasis ="30%";
		icon.style.gap = "10px";
		icon.style.margin = "10px 0";
		parent.appendChild(icon);
		if(button.permission)  // ADD EVENT
			icon.addEventListener(EVENT.CLICK, button.fn);
	
		//ADD TITLE
		let span = this.createElement(TAG.SPAN);
		span.style.textAlign = 'center';
		span.innerHTML = button.title; 
		icon.appendChild(span);
	}
}
if(!window.customElements.get('aon-dialog-mobile')){
	window.customElements.define('aon-dialog-mobile', AonDialogMobile);
}
