import { AonElement } from './AonElement.js';
import { CONSTANT, CSS, EVENT, MSG, TAG } from '../environments/environments.js';
import { AonIcon } from './aon-icon.js';
import { AonIconButton } from './aon-icon-button.js';

export class AonDialog extends AonElement {
  DIALOG;
  MAIN;
  TITLE;
  CONTENT;
  ACTION;
	DESCRIPTION;
  CANCEL;
  ACCEPT;
  BUTTON_LEFT;
  BUTTON_RIGHT;
  OUTSIDE_CLICK_CANCEL;
	BUTTON_CLOSE;

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
    /*
     if ('width' === name) {
     let main = this.getMain();
     if (main) main.style.width = newValue;
     } else if ('type' === name) {
     this.buildByType();
     } 
     */
    if ('type' === name) {
      this.buildByType();
    }
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.buildByType();
    this.clickOutsideDialogClose();
  }

  buildByType() {
//    if (this.isTypeBlank())
//      this.buildBlank();
//    else if (this.isTypeMenu())
//      this.buildMenu();
//    else if (this.isTypeFullScreen())
//      this.buildFullScreen();
//    else
//      this.build();
    if (this.isTypeMenu())
      this.buildMenu();
    else
      this.build();
  }

  initialize() {
    this.classList.add('aon-dialog-hidden');
    this.DIALOG       = this.id     + 'Dialog';
    this.MAIN         = this.DIALOG + 'Main';
    this.TITLE 				= this.DIALOG + 'Title';
    this.CONTENT      = this.DIALOG + 'Content';
    this.ACTION       = this.DIALOG + 'Action';
    this.CANCEL       = this.ACTION + 'Cancel';
    this.ACCEPT       = this.ACTION + 'Accept';
    this.BUTTON_LEFT  = this.DIALOG + 'DivButtonsLeft';
    this.BUTTON_RIGHT = this.DIALOG + 'DivButtonsRight';
    this.autoclose    = this.autoclose || true;
	this.OUTSIDE_CLICK_CANCEL = (e) => {
      const dialog = this.getDialog();
      if (dialog && !dialog.contains(e.target)) {
        this.close();
      }
    };
  }

  clear() {
    [
      this.getElement(this.TITLE),
      this.getContent(),
      this.getElement(this.ACTION)
    ].forEach(el => el && (el.innerHTML = ''));
  }

  buildBlank() {
//    this.innerHTML = "";
    this.buildMenu();
  }

  buildMenu() {
    // Se limpia el dialog
    this.innerHTML = "";
    // Montamos el HTML - div padre
    const dialog = this.createDialog();
    // main
    const main = this.createMain(dialog);
    // contenido
    const body = this.createBodyContent(main);
    // Si salimos del dialog
    dialog.addEventListener("mouseleave", () => {this.close();});
  }

  // Modales normales
  build() {
    // Se limpia el dialog
    this.innerHTML = "";
    // Montamos el HTML - div padre
    let dialog  = this.createDialog();
    // main
    let main = this.createMain(dialog);
    // cabecera
    this.createHead(main);
    // contenido
    const body = this.createBodyContent(main);
    // action (botones)
    this.createBodyAction(body);
  }

  buildFullScreen() {
    // Solo dejamos una, no tiene sentido usar dos
    // Si se quiere boton de volver solo cargar ese en el buid.
    // Si se quiere pantalla completa, solo es agragar una clase en build()
    this.build();
//    this.innerHTML = "";
//    let dialog = this.createElement(TAG.DIV);
//    dialog.id = this.DIALOG;
//    dialog.className = "aonDialog";
//    this.appendChild(dialog);
//
//    let main = this.createElement(TAG.DIV);
//    main.id = this.MAIN;
//    dialog.appendChild(main);
//
//    let action = this.createElement(TAG.DIV);
//    action.id = this.ACTION;
//
//    let title = this.createElement(TAG.DIV);
//    title.id = this.TITLE;
//    title.className = "dialog-title";
//
//    let content = this.createElement(TAG.DIV);
//    content.id = this.CONTENT;
//
//    main.appendChild(action);
//    main.appendChild(content);
//
//    let divButtonsLeft = this.createElement(TAG.DIV);
//    divButtonsLeft.id = this.BUTTON_LEFT;
//    action.appendChild(divButtonsLeft);
//
//    action.appendChild(title);//ADD TITLE
//
//    let divButtonRight = this.createElement(TAG.DIV);
//    divButtonRight.id = this.BUTTON_RIGHT;
//    action.appendChild(divButtonRight);
//
//    this.addAction({
//      id: this.CANCEL,
//      title: MSG.CLOSE,
//      icon: MATERIAL_ICONS.ARROW_BACK,
//      position: "left"
//    }, () => this.close());
//    this.onClick();
  }

  createDialog(){
    const dialog     = this.createElement(TAG.DIV);
    dialog.id        = this.DIALOG;
    dialog.className = "aonDialog";
    this.appendChild(dialog);
    return dialog;
  }

  getDialog() {
    return this.getElement(this.DIALOG);
  }

  createMain(dialog) {
    const main     = this.createElement(TAG.DIV);
    main.id        = this.MAIN;
    main.className = "aonDialogContent";
    dialog.appendChild(main);
    return main;
  }

  getMain() {
    return this.getElement(this.MAIN);
  }
  
  createHead(main) {
    const head     = this.createElement(TAG.DIV);
    head.className = "dialog-head";
    main.appendChild(head);
    // Titulo del dialog
    const title     = this.createElement(TAG.DIV);
    title.id        = this.TITLE;
    title.className = "dialog-title";
    head.appendChild(title);
    // Boton de cerrar
    const closeDesktop     = new AonIconButton(); 
    closeDesktop.id        = this.id + "dialog-head-close";
    closeDesktop.className = "dialog-head-close";
    closeDesktop.title     = MSG.CLOSE;
    closeDesktop.icon      = 'close';
    closeDesktop.onclick   = () => this.close();
    head.appendChild(closeDesktop);
  }
  
  deleteHeadClose(){
	const close = this.getElement(this.id + "dialog-head-close");
	close.remove();
  }

  setTitle(title) {
    if (title)
      this.getElement(this.TITLE).innerHTML = title;
  }

  createBodyContent(main){
    const body     = this.createElement(TAG.DIV);
    body.className = "dialog-body";
    main.appendChild(body);
    // Contenido
    const content     = this.createElement(TAG.DIV);
    content.id        = this.CONTENT;
    content.className = "dialog-body-content";
    body.appendChild(content);
    return body;
  }

  getContent() {
    return this.getElement(this.CONTENT);
  }

  createBodyAction(body){
    const action     = this.createElement(TAG.DIV);
    action.id        = this.ACTION;
    action.className = "dialog-body-action";
    body.appendChild(action);
  }

  getButtonAccept() {
    return this.getElement(this.ACCEPT);
  }

  getButtonCancel() {
    return this.getElement(this.CANCEL);
  }

  onClick() {
    if (!this.classList.contains('aon-dialog-hidden')) {
      this.classList.add('aon-dialog-hidden');
    } else {
      this.classList.remove('aon-dialog-hidden');
    }
    const dialog = this.getDialog();
    if (dialog) {
      dialog.onclick = ({target}) => {
        if (target === dialog && this.autoclose) {
          this.close();
        }
      };
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
    if (this.classList.contains('aon-dialog-hidden')) {
      this.classList.remove('aon-dialog-hidden');
    }
  }

  close() {
    this.autoclose = true;
    if (!this.classList.contains('aon-dialog-hidden')) {
      this.classList.add('aon-dialog-hidden');
    }
    this.dispatchEvent(new CustomEvent(EVENT.CLOSE));
  }

  clickOutsideDialogClose(){
    // Se hace click en la zona gris cerrar OUTSIDE_CLICK_CANCEL
	this.addEventListener('click', this.OUTSIDE_CLICK_CANCEL);
//    this.addEventListener('click', (e) => {
//      const dialog = this.getDialog();
//      if (dialog && !dialog.contains(e.target)) {
//        this.close();
//      }
//    });
  }
  
  removeCliclOutsideDialogClose(){
	this.removeEventListener('click', this.OUTSIDE_CLICK_CANCEL);
  }

  setContent(widget, top = null, left = null, width = null) {
    let content       = this.getContent();
    content.innerHTML = '';
    content.appendChild(widget);
    if (this.isTypeMenu() && top && left) {
      this.style.top  = top + 'px';
      this.style.left = (left > (this.offsetWidth / 2) ? left - 180 : left) + 'px';
    }

//		if(width) {
//			content.style.width = width;
//		}
  }
  
  addContent(widget) {
    let content = this.getContent();
    content.appendChild(widget);
  }

  setContentHTML(html) {
    this.getContent().innerHTML = html;
  }

  setMenuOptions(options, top, left) {
    let dialog = this.getDialog();
    let content = this.getContent();
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

      if (item.aonIcon) {
        let ai = document.createElement(TAG.SPAN);
        ai.style.verticalAlign = 'middle';
        li.appendChild(ai);

        let aonIcon = new AonIcon();
        aonIcon.icon = item.aonIcon;
        ai.appendChild(aonIcon);
      } else if (item.icon) {
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

  getButtonLeft() {
    return this.getElement(this.BUTTON_LEFT);
  }

  getButtonRight() {
    return this.getElement(this.BUTTON_RIGHT);
  }

  /**
   * 
   * @param {Object} id, title, icon, aonIcon(Optional), position(left, right) 
   * @param {Function} fn 
   */
  addAction( {id, title, icon, aonIcon, position}, fn){
    let btnMain = position === "left" ? this.getButtonLeft() : this.getButtonRight();

    let btn = this.getElement(id);
    if (btn)
      btn.remove();

    if (btnMain) {
      btn = new AonIconButton();
      btn.id = id;
      btn.icon = icon || aonIcon;
      btn.title = title;
      if (fn) {
        btn.addEventListener(EVENT.CLICK, fn);
      }
      btnMain.appendChild(btn);
      return btn;
    } else {
      btn = this.createElement(TAG.BUTTON);
      btn.id = id;
      btn.className = 'aonButton';
      btn.innerHTML = title;
      this.getElement(this.ACTION).appendChild(btn);
      btn.addEventListener(EVENT.CLICK, fn);
    }
  }

  addCancelAction(fn = undefined, close = true, title = MSG.CANCEL) {
    let btn = undefined;
//    if (this.isTypeFullScreen()) {
//      btn = this.addAction({
//        id: this.CANCEL,
//        title: MSG.CLOSE,
//        icon: MATERIAL_ICONS.ARROW_BACK,
//        position: "left"
//      });
//    } else {
      btn = this.getElement(this.CANCEL);
      if (btn)
        btn.remove();
      btn = this.createElement(TAG.BUTTON);
      btn.id = this.CANCEL;
      btn.className = 'aonButton button-transparent';
      btn.innerHTML = title;
      this.getElement(this.ACTION).appendChild(btn);
//    }

    btn.addEventListener(EVENT.CLICK, (ev) => {
      if (fn){
        fn(ev);
      }
      if (close) {
        this.close();
      }
    });

    return btn;
  }

  addAcceptAction(fn) {
    let accept = this.createButtonAccept();
    if (accept) {
      accept.addEventListener(EVENT.CLICK, (ev) => {
        this.close();
        fn(ev);
      });
    }
  }

  createButtonAccept(title = undefined, fn = undefined) {
    let btn = undefined;
//    if (this.isTypeFullScreen()) {
//      btn = this.addAction({
//        id: this.ACCEPT,
//        title: title || MSG.ACCEPT,
//        icon: MATERIAL_ICONS.DONE,
//        position: "right"
//      });
//    } else {
      btn = this.getElement(this.ACCEPT);
      if (btn)
        btn.remove();
      btn = this.createElement(TAG.BUTTON);
      btn.id = this.ACCEPT;
      btn.className = 'aonButton';
      btn.innerHTML = title || MSG.ACCEPT;
      btn.title = title || MSG.ACCEPT;
      let divAction = this.getElement(this.ACTION);
	  if(fn != undefined)
		  btn.addEventListener(EVENT.CLICK, (ev) => {
		      this.close();
		      fn(ev);
		  });
      divAction.appendChild(btn);
//    }

    return btn;
  }

  addSendAction(fn, titleOrType) {
    const defaults = {
      save  : MSG.SAVE,
      delete: MSG.DELETE
    };

    // Si no hay title y existe un default para ese type, �salo
    const title = defaults[titleOrType] || titleOrType;

    let button = this.createButtonAccept(title);
    if (button) {
      button.classList.add('dialog-buttonload');
      // Agregar clase basada en el tipo si existe en defaults
      if (defaults[titleOrType]) {
        button.classList.add(`dialog-button-${titleOrType}`);
      }
      button.addEventListener(EVENT.CLICK, (ev) => {
        ev.stopPropagation();
        ev.preventDefault();
        fn(ev);
      });
      return button;
    }
  }
}
if (!window.customElements.get('aon-dialog')) {
  window.customElements.define('aon-dialog', AonDialog);
}
