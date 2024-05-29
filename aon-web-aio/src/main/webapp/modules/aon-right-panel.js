import { AonElement } from 'aonsolutions/components/AonElement.js';
import { CONSTANT, TAG, EVENT } from "aonsolutions/environments/environments.js";
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';

export class AonRightPanel extends AonElement {

    RIGHT_PANEL;
    CLOSE_BUTTON;
    CONTENT;
    TITLE;
    EDIT_BUTTON;
    REDIRECT_BUTTON;
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
        this.REDIRECT_BUTTON = this.RIGHT_PANEL+ 'RedirectButton';
        this.CONFIG_BUTTON = this.RIGHT_PANEL + 'ConfigButton';
        this.HELP_BUTTON = this.RIGHT_PANEL + 'HelpButton';
        this.NOTIFICATION_BUTTON = this.RIGHT_PANEL + 'NotificationButton';
    }

    build(){
        let rightPanel = this.createDiv(this.RIGHT_PANEL, "rightPanel");
		rightPanel.style.width = '320px';
		rightPanel.style.backgroundColor = '#faf9f8';
		rightPanel.style.visibility = "hidden";
        this.appendChild(rightPanel);

        let rightPanelEditButton = new AonIconButton(); 
		rightPanelEditButton.id = this.EDIT_BUTTON;
		rightPanelEditButton.icon ='manage_accounts';
		rightPanelEditButton.style.cursor = "pointer";
		rightPanelEditButton.style.position = "fixed";
        rightPanelEditButton.style.visibility = 'hidden';
        rightPanelEditButton.style.right = '280px';
        rightPanelEditButton.style.top = '60px';
		rightPanel.appendChild(rightPanelEditButton);

        let rightPanelRedirectButton = new AonIconButton(); 
		rightPanelRedirectButton.id = this.REDIRECT_BUTTON;
		rightPanelRedirectButton.icon ='open_in_new';
		rightPanelRedirectButton.style.cursor = "pointer";
		rightPanelRedirectButton.style.position = "fixed";
		rightPanelRedirectButton.style.right = '50px';
		rightPanel.appendChild(rightPanelRedirectButton);

        let rightPanelConfigButton = new AonIconButton(); 
		rightPanelConfigButton.id = 'aonRightPanelConfigButton';
		rightPanelConfigButton.icon ='settings';
		rightPanelConfigButton.style.cursor = "pointer";
		rightPanelConfigButton.style.position = "fixed";
        rightPanelConfigButton.style.visibility = 'hidden';
        rightPanelConfigButton.style.right = '280px';
        rightPanelConfigButton.style.marginTop = '8px';
		rightPanel.appendChild(rightPanelConfigButton);

        let rightPanelHelpButton = new AonIconButton(); 
		rightPanelHelpButton.id = 'aonRightPanelHelpButton';
		rightPanelHelpButton.icon ='help_outline';
		rightPanelHelpButton.style.cursor = "pointer";
		rightPanelHelpButton.style.position = "fixed";
        rightPanelHelpButton.style.visibility = 'hidden';
        rightPanelHelpButton.style.right = '280px';
        rightPanelHelpButton.style.marginTop = '8px';
		rightPanel.appendChild(rightPanelHelpButton);

        let rightPanelNotificationButton = new AonIconButton(); 
		rightPanelNotificationButton.id = 'aonRightPanelNotificationButton';
		rightPanelNotificationButton.icon ='notifications';
		rightPanelNotificationButton.style.cursor = "pointer";
		rightPanelNotificationButton.style.position = "fixed";
        rightPanelNotificationButton.style.visibility = 'hidden';
        rightPanelNotificationButton.style.right = '280px';
        rightPanelNotificationButton.style.marginTop = '8px';
		rightPanel.appendChild(rightPanelNotificationButton);


        let rightPanelCloseButton = new AonIconButton(); 
		rightPanelCloseButton.id = this.CLOSE_BUTTON;
		rightPanelCloseButton.icon ='close';
		rightPanelCloseButton.style.cursor = "pointer";
		rightPanelCloseButton.style.position = "fixed";
		rightPanelCloseButton.style.right = '10px';
        rightPanelCloseButton.style.marginTop = '8px';
		rightPanel.appendChild(rightPanelCloseButton);

        rightPanelCloseButton.addEventListener(EVENT.CLICK, () => {
			this.close();
		});

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
        this.getRightPanel().style.visibility = "visible";
        if(height) this.getRightPanel().style.height = height;
        else this.getRightPanel().style.height = "";
        if (marginTop) this.getRightPanel().style.marginTop = marginTop;
        else this.getRightPanel().style.marginTop = this.getElement("aonMenuTopnav").offsetHeight;
        if (boxShadow) this.getRightPanel().style.boxShadow = boxShadow;
        else this.getRightPanel().style.boxShadow = "";
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

        this.getRedirectButton().style.visibility = "hidden";

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

    getRedirectButton(){
        return this.getElement(this.REDIRECT_BUTTON);

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
}
if (!window.customElements.get(TAG.AON_RIGHT_PANEL)) {
	window.customElements.define(TAG.AON_RIGHT_PANEL, AonRightPanel);
}