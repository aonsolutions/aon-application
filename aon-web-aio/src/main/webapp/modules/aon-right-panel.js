import { AonElement } from 'aonsolutions/components/AonElement.js';
import { CONSTANT, TAG, EVENT } from "aonsolutions/environments/environments.js";
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';

export class AonRightPanel extends AonElement {

    RIGHT_PANEL;
    CLOSE_BUTTON;
    CONTENT;
    TITLE;
    
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
    }

    build(){
        let rightPanel = this.createDiv(this.RIGHT_PANEL, "rightPanel");
		rightPanel.style.width = '320px';
		rightPanel.style.marginTop = this.getElement("aonMenuTopnav").offsetHeight;
		rightPanel.style.backgroundColor = '#faf9f8';
		rightPanel.style.visibility = "hidden";
        this.appendChild(rightPanel);

        let rightPanelCloseButton = new AonIconButton(); 
		rightPanelCloseButton.id = this.CLOSE_BUTTON;
		rightPanelCloseButton.icon ='close';
		rightPanelCloseButton.style.cursor = "pointer";
		rightPanelCloseButton.style.position = "fixed";
		rightPanelCloseButton.style.right = '10px';
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

    open(){
        this.getRightPanel().style.visibility = "visible";
    }

    toogle() {
		if(this.style.visibility === "visible") {
			this.close()
		} else this.open();
	}

    close(){
        this.getRightPanel().style.visibility = "hidden";
        this.getRightPanel().style.visibility = "hidden";
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
}
if (!window.customElements.get(TAG.AON_RIGHT_PANEL)) {
	window.customElements.define(TAG.AON_RIGHT_PANEL, AonRightPanel);
}