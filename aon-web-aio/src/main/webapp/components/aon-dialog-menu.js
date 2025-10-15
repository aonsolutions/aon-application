import {AonElement} from './AonElement.js';
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonIcon } from './aon-icon.js';
import { AonIconButton } from './aon-icon-button.js';
import * as LS from '../services/localStorageService';

export class AonDialogMenu extends AonElement {
	LIST;
	DIALOG;
	TITLE;
	CONTENT;
	HEADER;
	BODY;
	HEADER_HEIGHT;
	INITIAL_DRAG_Y;
	START_TOP;
	Y_DRAG;
	
	dialog;

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

	constructor (dialog) {
		super();
		this.dialog = dialog;
	}

	connectedCallback () {
		this.initialize();
		this.build();
  	}

	initialize() {
      this.id = this.id || "aonDialogMenu";
      this.DIALOG = this.id + 'DialogMenu';
      this.CONTENT = this.DIALOG + 'Content';
      this.LIST = this.DIALOG + 'List';
	}
  
	build() {
      let dialog = this.getDialog();
      if (  dialog == null ) {
        dialog = this.createElement(TAG.DIV);
        dialog.id = this.DIALOG;
        dialog.className = `aonDialog`;
        this.appendChild(dialog);
      }

      let content = this.createElement(TAG.DIV);
      content.id = this.CONTENT;
      content.className = `aonDialogMenuContent`;
      dialog.appendChild(content);

      const onClose = (ev) => ev.target === dialog ? this.close() : null;

      dialog.onclick = (ev) => onClose(ev);

      dialog.oncontextmenu = (ev) => {
          ev.preventDefault();
          onClose(ev);
      };
	}

	open(searchBox = false) {
		this.style.visibility = 'visible';
		// Agregamos el cerrar
		if (this.isMobile()) {
			// Esperamos un tick para evitar cerrar con el mismo clic que abre
			setTimeout(() => {
				const onClickOutside = (event) => {
					if (!this.contains(event.target)) {
						this.close();
						document.removeEventListener('click', onClickOutside);
					}
				};
				document.addEventListener('click', onClickOutside);
			}, 0);
		} else if (searchBox){
			// si se carga del buscar le ponemos otra logica...
			this.style.display = 'block';
		} else {
			const onMouseLeave = () => {
				this.close();
				this.removeEventListener('mouseleave', onMouseLeave);
			};
			this.addEventListener('mouseleave', onMouseLeave);
		}
	}

	clear() {
      this.innerHTML = '';
	}

	hide() {
      return new Promise((resolve) => {
        this.style.display = 'none';
        resolve();
      });
	}

	close() {
      this.hide().then(this.clear());
	}

	setContentHTML(html) {
      this.innerHTML = html;
	}

	setContent(element, top, left) {
      this.innerHTML = "";	
      if(element){
          this.appendChild(element);
      }
	}

	setTitle(title) {
		if(title) this.getTitle().innerHTML = title;
	}

	getList() {
		return this.getElement(this.LIST);
	}

	getContent(){
		return  this.getElement(this.CONTENT);
	}

	getDialog() {
		return this.dialog ?? this.getElement(this.DIALOG);
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

		if(content.children.length > 0) {
			content.insertBefore(p, content.firstElementChild);
		} else content.appendChild(p);
	}
	
	addMenuOptions(options) {
		let ul = this.getList();
		options.forEach((item, i) => { ul.appendChild(this.buildLi(item, i )); });
	}

	setMenuOptions(options, top, left, positionSearch = false) {
		this.innerHTML = '';
		this.clear();
		// Posicionar
		if(positionSearch){
			this.positionDialogSearch(top, left);
		}else{
			this.positionDialogWithinViewport(top, left);
		}
		// Menu
		let ul = document.createElement(TAG.UL);
		ul.id = this.LIST;
		ul.className = CSS.AON_UL;
		this.appendChild(ul);
		options.forEach((item, i) => { ul.appendChild(this.buildLi(item, i)); });
	}

	positionDialogSearch(anchorY, anchorX){
		this.style.display    = 'block';
		this.style.visibility = 'hidden';
		this.style.left 			= Math.round(anchorX) + 'px';
		this.style.top  			= Math.round(anchorY)  + 'px';
	}

	positionDialogWithinViewport(anchorY, anchorX){
		this.style.display    = 'block';
		this.style.visibility = 'hidden';
		// Para poder coger la altura automatica
		requestAnimationFrame(() => {
			const remToPx = rem => rem * parseFloat(getComputedStyle(document.documentElement).fontSize || '16');
			const margin = remToPx(1.25);

			// medidas del dialogo (tal y como esta en el layout actual)
			const dialogWidthPx  = this.offsetWidth;
			const dialogHeightPx = this.offsetHeight;

			// rect relativo al viewport
			const rect = this.getBoundingClientRect();

			// punto base para posicionar: o el punto ancla (cursor) o la posicion actual del rect
//			let baseLeft = (typeof anchorX === 'number') ? anchorX - dialogWidthPx / 2 : rect.left;
//			let baseTop  = (typeof anchorY === 'number') ? anchorY - dialogHeightPx / 2 : rect.top;
			let baseLeft = (typeof anchorX === 'number') ? anchorX - dialogWidthPx / 2 : rect.left;
			let baseTop  = (typeof anchorY === 'number') ? anchorY : rect.top;

			// clamp: evitar salirse por los 4 lados dejando 'margin' de separacion
			const minLeft = margin;
			const maxLeft = Math.max(margin, window.innerWidth  - dialogWidthPx - margin);
			const minTop  = margin;
			const maxTop  = Math.max(margin, window.innerHeight - dialogHeightPx - margin);

			const newLeft = Math.min(Math.max(baseLeft, minLeft), maxLeft);
			const newTop  = Math.min(Math.max(baseTop,  minTop),  maxTop);

			this.style.left = Math.round(newLeft) + 'px';
			this.style.top  = Math.round(newTop)  + 'px';
		});
	}

	addButtons(buttons) {
		let divMain = this.createElement(TAG.DIV);
		divMain.classList.add(CSS.FLEX_WRAP, CSS.FLEX_JUSTIFY_BETWEEN);
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
		span.innerHTML = button.title; 
		icon.appendChild(span);
	}
	
	isElementAt(ev, el){
		const viewportX = ev.clientX;
		const viewportY = ev.clientY;
		let elements = document.elementsFromPoint(viewportX, viewportY);
		for ( let element of elements ){
			console.log(element.tagName + ": "  + (element === el));
			if ( element === el ){
				return true;
			}
		}
		return false;
	}
	
	buildLi(item, i) {
		let dialog = this.getDialog();
		let li = document.createElement('li');
		if(item.id) li.id = item.id;
		li.className = 'aonAppLi';

		if(item.options) {
			let d = new AonDialogMenu(dialog);
			d.id = 'newDialog';
			this.appendChild(d);
			d.clear();

			li.addEventListener(EVENT.MOUSEOVER, () => {
				const rect = li.getBoundingClientRect();
				d.setMenuOptions(item.options, rect.top, rect.right);
				d.getContent().addEventListener(EVENT.MOUSELEAVE, (e) => {
					// out of submenu but inside option
					if ( !this.isElementAt(e, li) ){
						d.clear();
					}
				});
				d.open();
			});

			li.addEventListener(EVENT.MOUSELEAVE, (e) => {
				// out of option but inside submenu 
				if ( !this.isElementAt(e, d.getContent() ) ){
					d.clear();
				}
			});
		}

        if(item.image) {
          item.icon = item.image;
		} else if(item.aonIcon) {
          item.icon = item.aonIcon;
		}

        const aonIcon = new AonIcon();
        aonIcon.icon = item.icon;
        li.appendChild(aonIcon);

		let span = document.createElement(TAG.SPAN);
		span.innerHTML = item.name;
		span.title     = item.title || item.name;
        // mostrando menu de idomas, seleccionamos en el que estamos
        if(item.selectLanguage && item.selectLanguage === this.lenguajeSelect()){
          li.classList.add('selected');
        }
		li.appendChild(span);
		li.addEventListener(EVENT.CLICK, (ev) => {
			this.close();
			item.fn(ev);
		});
		return li;
	}
    
    lenguajeSelect(){
      if(LS.getLanguage()){
        return LS.getLanguage();
      } else return MSG.SPANISH;
    }
}

if(!window.customElements.get('aon-dialog-menu')){
	window.customElements.define('aon-dialog-menu', AonDialogMenu);
}
