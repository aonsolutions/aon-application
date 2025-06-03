import { AonElement } from '../components/AonElement.js';
import { CONSTANT, TAG, EVENT } from "../environments/environments.js";
import { AonIconButton } from '../components/aon-icon-button.js';
import { AonImageEditor } from '../components/aon-image-editor.js';
import * as LS from '../services/localStorageService.js';

export class AonRightPanel extends AonElement {
    RIGHT_PANEL;
    CLOSE_BUTTON;
    CONTENT;
    TITLE;
    EDIT_BUTTON;
    CONFIG_BUTTON;
    HELP_BUTTON;
    NOTIFICATION_BUTTON;
    
    get id () {
        return this.getAttribute(CONSTANT.ID);
    }

    set id (id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    get title () {
        return this.getAttribute(CONSTANT.TITLE);
    }

    set title (title) {
        this.setAttribute(CONSTANT.TITLE, title);
    }

    connectedCallback() {
      this.initialize();
      this.build();
    }

    initialize() {
        this.RIGHT_PANEL = "aonRightPanel";
        this.TOOLBAR_PANEL = "aonToolbarPanel";
        this.CLOSE_BUTTON = this.RIGHT_PANEL+'CloseButton';
        this.CONTENT = this.RIGHT_PANEL+'Content';
        this.TITLE = this.RIGHT_PANEL+'Title';
        this.EDIT_BUTTON = this.RIGHT_PANEL+ 'EditButton';
        this.CONFIG_BUTTON = this.RIGHT_PANEL + 'ConfigButton';
        this.HELP_BUTTON = this.RIGHT_PANEL + 'HelpButton';
        this.NOTIFICATION_BUTTON = this.RIGHT_PANEL + 'NotificationButton';
    }

    build(){
      // Ocultar el menu
      this.classList.add('hiddenSidenav');
        
      let rightPanel = this.createDiv(this.RIGHT_PANEL, "rightPanel");

      let titlePanel = this.createDiv(this.TOOLBAR_PANEL, "titlePanel");
      rightPanel.appendChild(titlePanel);
      this.appendChild(rightPanel);

      // Agregamos cabecera
      let rightPanelCloseButton       = new AonIconButton(); 
      rightPanelCloseButton.id        = this.CLOSE_BUTTON;
      rightPanelCloseButton.icon      ='close';
      rightPanelCloseButton.className = "rightPanelCloseButton";

      rightPanelCloseButton.addEventListener(EVENT.CLICK, () => {
        this.close();
      });

      let title = this.createElement(TAG.DIV);
      title.id = this.TITLE;
      titlePanel.appendChild(title);
      titlePanel.appendChild(rightPanelCloseButton);
      // Agregamos el contenido
      let content = this.createDiv(this.CONTENT);

      rightPanel.appendChild(content);
      
      // Cerrar con la tecla esc
      document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
          this.close();
        }
      });
      // Cerar al hacer click fuera
      document.addEventListener('click', (e) => {
        if (!this.contains(e.target)) {
          this.close();
        }
      });
    }

    setTitle(title){
      this.title = title;
      let titleElement = this.getElement(this.TITLE);
      if(titleElement) {
          titleElement.innerHTML = this.title;
      }
    }

    setContent(content){
      this.getElement(this.CONTENT).appendChild(content);
    }

    clear(){
        this.clearElement(this.getContent());
        this.clearElement(this.getTitle());
    }

    open(){
      this.classList.remove('hiddenSidenav');
    }

    toogle() {
      this.classList.toggle('hiddenSidenav');
	}

    close(){
      this.classList.add('hiddenSidenav');
    }

    isClose() {
      return !this.getRightPanel().classList.contains("open");
    }

    isOpen(){
        return !this.isClose();
    }

    getRightPanel() {
		return this.getElement(this.RIGHT_PANEL);
	}

    getCloseButton() {
		return this.getElement(this.CLOSE_BUTTON);
	}

    getContent(){
        return this.getElement(this.CONTENT);
    }

    getEditButton(){
        return this.getElement(this.EDIT_BUTTON);
    }

    getConfigButton(){
        return this.getElement(this.CONFIG_BUTTON);
    }

    getHelpButton(){
        return this.getElement(this.HELP_BUTTON);
    }

    getNotificationButton(){
        return this.getElement(this.NOTIFICATION_BUTTON);
    }
    
    getNotificationOpenButton(){
        return this.getElement("openNotificationButton");
    }

    getTitle(){
        return this.getElement(this.TITLE);
    }
	
	getDefaultMarginTop(){
		return this.getRootPanel().style.marginTop;
	}
}
if (!window.customElements.get(TAG.AON_RIGHT_PANEL)) {
	window.customElements.define(TAG.AON_RIGHT_PANEL, AonRightPanel);
}
