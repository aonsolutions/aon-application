import { AonElement } from 'aonsolutions/components/AonElement.js';
import { CONSTANT, TAG, EVENT } from "aonsolutions/environments/environments.js";
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import { AonImageEditor } from 'aonsolutions/components/aon-image-editor.js';
import * as LS from 'aonsolutions/services/localStorageService.js';

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
        this.CLOSE_BUTTON = this.RIGHT_PANEL+'CloseButton';
        this.CONTENT = this.RIGHT_PANEL+'Content';
        this.TITLE = this.RIGHT_PANEL+'Title';
        this.EDIT_BUTTON = this.RIGHT_PANEL+ 'EditButton';
        this.CONFIG_BUTTON = this.RIGHT_PANEL + 'ConfigButton';
        this.HELP_BUTTON = this.RIGHT_PANEL + 'HelpButton';
        this.NOTIFICATION_BUTTON = this.RIGHT_PANEL + 'NotificationButton';
    }

    build(){

        let rightPanel = this.createDiv(this.RIGHT_PANEL, "rightPanel");

        this.appendChild(rightPanel);
        
        let rightPanelEditButton = new AonIconButton(); 
		rightPanelEditButton.id = this.EDIT_BUTTON;
		rightPanelEditButton.icon ='manage_accounts';
        rightPanelEditButton.className = "rightPanelEditButton";
		rightPanel.appendChild(rightPanelEditButton);

        let rightPanelConfigButton = new AonIconButton(); 
		rightPanelConfigButton.id = 'aonRightPanelConfigButton';
		rightPanelConfigButton.icon ='settings';
		rightPanelConfigButton.className = "rightPanelButtons";
		rightPanel.appendChild(rightPanelConfigButton);

        let rightPanelHelpButton = new AonIconButton(); 
		rightPanelHelpButton.id = 'aonRightPanelHelpButton';
		rightPanelHelpButton.icon ='help_outline';
        rightPanelHelpButton.className = "rightPanelButtons";
		rightPanel.appendChild(rightPanelHelpButton);

        let rightPanelNotificationButton = new AonIconButton();
		rightPanelNotificationButton.id = 'aonRightPanelNotificationButton';
		rightPanelNotificationButton.icon ='notifications';
        rightPanelNotificationButton.className = "rightPanelButtons";
		rightPanel.appendChild(rightPanelNotificationButton);

        let rightPanelCloseButton = new AonIconButton(); 
		rightPanelCloseButton.id = this.CLOSE_BUTTON;
		rightPanelCloseButton.icon ='close';
        rightPanelCloseButton.className = "rightPanelCloseButton";
		rightPanel.appendChild(rightPanelCloseButton);

        rightPanelCloseButton.addEventListener(EVENT.CLICK, () => {
			this.close();
            let noti = this.getElement("aonRightPanelNotificationButtonIconButton");
            noti.style.visibility = "hidden";
		});

        /*
        if(LS.isDarkTheme()){
            this.style.color = "white";
            let close = this.getElement("aonRightPanelCloseButtonIconButton");
            let edit = this.getElement("aonRightPanelEditButtonIconButton");
            let config = this.getElement("aonRightPanelConfigButtonIconButton")
            let help = this.getElement("aonRightPanelHelpButtonIconButton");
            let noti = this.getElement("aonRightPanelNotificationButtonIconButton");
            close.style.color = "white";
            edit.style.color = "white";
            config.style.color = "white";
            help.style.color = "white";
            noti.style.color = "white";
        }
        */

        let title = this.createElement(TAG.H1);
		title.id = this.TITLE;
		title.style.marginTop = '18px';
		title.style.marginLeft = '10px'
		title.style.fontSize = '16px';
		title.style.fontWeight = '500';
		title.style.paddingBottom = '20px';
		rightPanel.appendChild(title);

        let content = this.createDiv(this.CONTENT);
    
        rightPanel.appendChild(content);

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

    open(height,marginTop,boxShadow){
        let welcome = this.getElement("aonCompanyTabFilter");
        this.getRightPanel().style.visibility = "visible";
		
        if(height) 
			this.getRightPanel().style.height = height;
        else 
		this.getRightPanel().style.height = "";
        if (marginTop) 
			this.getRightPanel().style.marginTop = marginTop;
        else 
			this.getRightPanel().style.marginTop = this.getDefaultMarginTop(); //"65px";
        
		if (boxShadow) 
			this.getRightPanel().style.boxShadow = boxShadow;
        else 
		this.getRightPanel().style.boxShadow = "";
        
		if(welcome) 
			this.getRightPanel().style.marginTop = "0px";
        else 
			this.getRightPanel().style.marginTop = this.getDefaultMarginTop(); //"65px";
    }

    toogle() {
		if(this.style.visibility === "visible") {
			this.close()
		} else this.open();
	}

    close(){
        this.getRightPanel().style.visibility = "hidden";
        this.getRightPanel().style.visibility = "hidden";
        this.getEditButton().style.visibility = "hidden";
        this.getConfigButton().style.visibility = "hidden";
        this.getHelpButton().style.visibility = "hidden";
        this.getNotificationButton().style.visibility = "hidden";
        this.clearElement(this.getContent());
        this.dispatchEvent(new Event(EVENT.CLOSE));
    }

    isClose() {
        return this.getRightPanel().style.visibility == "hidden";
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
