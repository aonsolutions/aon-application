import {AonElement} from './AonElement.js';
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonIcon } from './aon-icon.js';
import { AonIconButton } from './aon-icon-button.js';


export class AonDialogMenu extends AonElement {

	DIALOG;
	TITLE;
	CONTENT;
	HEADER;
	BODY;
	HEADER_HEIGHT;
	INITIAL_DRAG_Y;
	START_TOP;
	Y_DRAG;

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


	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.build();
  	}

	initialize() {
		this.id = this.id || "aonDialogMenu";
		this.DIALOG = this.id + 'DialogMenu';
		this.CONTENT = this.DIALOG + 'Content';
		if(this.isMobile()) {
			this.BODY = this.DIALOG + 'Body';
			this.HEADER = this.DIALOG + 'Header';
			this.TITLE = this.DIALOG + CONSTANT.TITLE;
			this.HEADER_HEIGHT = 0;
			this.INITIAL_DRAG_Y = 0;
			this.START_TOP = 0;
			this.Y_DRAG = 0;
		}
	}
  
	build() {
		if(this.isMobile()) {
			this.buildMobile();
		} else {
			this.buildDesktop();
		}
	}

	buildDesktop() {
		let dialog = this.createElement(TAG.DIV);
		dialog.id = this.DIALOG;
		dialog.className = `aonDialog`;
		dialog.style.backgroundColor = 'transparent';
		dialog.style.paddingTop = '0px';
		this.appendChild(dialog);

		let content = this.createElement(TAG.DIV);
		content.id = this.CONTENT;
		content.className = `aonDialogMenuContent`;
		content.style.position = 'absolute';
		content.style.width = '200px';
	 	content.style.padding = '0px';
		content.style.borderRadius = '5px';
		dialog.appendChild(content);

		const onClose = (ev) => ev.target === dialog ? this.close() : null;

		dialog.onclick = (ev) => onClose(ev);

		dialog.oncontextmenu = (ev) => {
			ev.preventDefault();
			onClose(ev);
		}
	}

	buildMobile() {
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
	


	open(){
		let dialog = this.getDialog();
		if(this.isMobile()) {
			let aonMobileMenuSidenav = this.getElement('aonMobileMenuSidenav');
			if(aonMobileMenuSidenav) aonMobileMenuSidenav.style.zIndex = "-1";
			this.setDrag(this.START_TOP);
			dialog.style.display = "block";
			setTimeout(()=>{
				let content = this.getContent();
				content.style.bottom = this.START_TOP;
				this.HEADER_HEIGHT = content.getBoundingClientRect().height;
			}, 1);
		} else {
			dialog.style.display = 'block';
		}
	}

	clear() {
		if(this.isMobile()) {
			if(this.getTitle()) this.getTitle().innerHTML = '';
			if(this.getBody()) this.getBody().innerHTML = '';
		} else {
			this.getContent().innerHTML = '';
		}
	}

	close() {
		if(this.isMobile()) {
			this.getContent().style.bottom = ((this.HEADER_HEIGHT || 300)*-1)+"px";
			setTimeout(()=>	{
				this.getDialog().style.display = "none";
				this.getElement('aonMobileMenuSidenav').style.zIndex = "0";
				this.clear();
			}, 400);
		} else {
			let dialog = this.getDialog();
			dialog.style.display = 'none';
			this.clear();
		}
	}

	setContentHTML(html) {
		let content = this.isMobile() ? this.getBody() : this.getContent();
		content.innerHTML = html;
	}

	setContent(element, top, left) {
		let content = this.isMobile() ? this.getBody() : this.getContent();
		content.innerHTML = "";
		if(!this.isMobile() && top && left) {
			content.style.top = top + 'px' || '90px';
			content.style.left = (left > (this.getDialog().offsetWidth/2) ? left - 180 : left)+'px' ;	
		}
		if(element){
			content.appendChild(element);
		}
	}

	setTitle(title) {
		if(title) this.getTitle().innerHTML = title;
	}

	getContent(){
		return  this.getElement(this.CONTENT);
	}

	getDialog() {
		return this.getElement(this.DIALOG);
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

	setContentTitle(title){
		let content = this.getContent();
		let p = this.createElement('p');
		p.innerHTML = title;
		p.style.fontWeight = "600";
		p.style.margin = "auto";
		p.style.marginTop = "3px";
		p.style.textAlign = "center";

		if(content.children.length > 0) {
			content.insertBefore(p, content.firstElementChild);
		} else content.appendChild(p);
	}

	setMenuOptions(options, top, left) {
		if(this.isMobile()) {
			this.addButtons(options);
		} else {
			let dialog = this.getDialog();
			let content = this.getContent();

  			content.style.top = top + 'px' || '90px';
			content.style.left = (left > (dialog.offsetWidth/2) ? left - 180 : left)+'px' ;

			content.innerHTML = '';
			let ul = document.createElement(TAG.UL);
			ul.className = CSS.AON_UL;
			content.appendChild(ul);
			options.forEach((item, i) => {
				let li = document.createElement('li');
				if(item.id) li.id = item.id;
				li.className = 'aonAppLi';
				li.style.padding = '10px';
				li.style.cursor = 'pointer';
				ul.appendChild(li);

				if(item.options) {
					let d = new AonDialogMenu();
					d.id = 'newDialog';
					this.getElement('rootPanel').appendChild(d);
					li.addEventListener(EVENT.MOUSEOVER, () => {
						const rect = li.getBoundingClientRect();
						d.setMenuOptions(item.options, rect.top, rect.left - 12);
						d.getContent().addEventListener(EVENT.MOUSELEAVE, () => {
							d.close();
						});
						d.getDialog().addEventListener(EVENT.MOUSEOVER, (e) => {
							let isClickInside = d.getContent().contains(e.target) || d.getContent() === e.target;
					   	 	if (!isClickInside) d.close();
						})
						d.open();
					});

					li.addEventListener(EVENT.MOUSELEAVE, (e) => {
						let isClickInside = li.contains(e.target) || li === e.target || d.contains(e.target) || d === e.target;
					    if (!isClickInside) d.close();
					});

				}
				if(item.image) {
					let img = document.createElement('img');
					img.src = item.image;
					li.appendChild(img);
				} else if(item.aonIcon) {
					let ai = document.createElement(TAG.SPAN);
					ai.style.verticalAlign = 'middle';
					let aonIcon = new AonIcon();
					aonIcon.icon = item.aonIcon;
					aonIcon.size = 15;
					ai.appendChild(aonIcon);
					li.appendChild(ai);
				} else if(item.icon){
					let ic = document.createElement('i');
					ic.className = item.icon_class || 'material-icons';
					ic.style.verticalAlign = 'middle';
					ic.style.fontSize = '16px';
					ic.innerHTML = item.icon;
					li.appendChild(ic);
				}

				let span = document.createElement(TAG.SPAN);
				span.style.marginLeft = '5px';
				span.style.fontSize = '13px';
				span.innerHTML = item.name;
				span.title     = item.name;
				li.appendChild(span);
				li.addEventListener(EVENT.CLICK, (ev) => {
					this.close();
					item.fn(ev);
				});
			});
		}
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
		const backgroundColor = button.permission ? button.backgroundColor : '#bbb';

		let icon = new AonIconButton();
		icon.classList.add(CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER);
		icon.id = button.id || Math.random().toString(36).substring(7);
		
		if(button.aonIcon){
			icon.aonIcon = button.aonIcon;
		} else if(button.image) {
			icon.image = button.image;
		} else {
			icon.icon = button.icon || 'help_outline';
		}

		icon.color = button.color || 'white';
		icon.noHover = true;
		icon.background = backgroundColor;
		icon.style.flexBasis ="30%";
		icon.style.gap = "10px";
		icon.style.margin = "10px 0";
		parent.appendChild(icon);
		if(button.language)
			this.getElement(icon.IMAGE).style.width = '35px';
		if(button.permission)  // ADD EVENT
			icon.addEventListener(EVENT.CLICK, () => {
				this.close();
				button.fn();
			});
	
		//ADD TITLE
		let span = this.createElement(TAG.SPAN);
		span.style.textAlign = 'center';
		span.innerHTML = button.title; 
		icon.appendChild(span);
	}

}
if(!window.customElements.get('aon-dialog-menu')){
	window.customElements.define('aon-dialog-menu', AonDialogMenu);
}
