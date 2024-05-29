import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import { AonButton } from 'aonsolutions/components/aon-button.js';
import { AonCard } from 'aonsolutions/components/aon-card.js';
export class AonSuiteMenu extends AonElement {

	SIDE_MENU;
    CONTENT;
    TITLE;
    NEW_BUTTON;
    UPLOAD_BUTTON;
    CONF_BUTTON;
    options; 

	constructor () {
		super();
	}

	connectedCallback () {
		this.clear();
		this.initialize();
		this.build();
	}

	initialize() {
        this.id = 'aonSuiteMenu';
		this.SIDE_MENU = this.id+ 'SideMenu';
		this.CONTENT = this.id+ 'Content';
        this.TITLE = this.id + 'Title';
        this.NEW_BUTTON = this.id + 'NewButton';
        this.UPLOAD_BUTTON = this.id + 'UploadButton';
        this.CONF_BUTTON = this.id + 'ConfButton';
	}

	build() {

        let divFlex = this.createDiv();
        divFlex.className = "aonFlex";
        this.appendChild(divFlex);

        let sideMenu = this.createDiv();
        sideMenu.id = this.SIDE_MENU;
        sideMenu.style.minWidth = "250px";
        sideMenu.style.marginLeft = "2px"
        sideMenu.style.backgroundColor = "rgb(250, 249, 248)";
        sideMenu.style.borderRight = "1px solid rgba(0, 0, 0, 0.1)";
        divFlex.appendChild(sideMenu);

        let newButton = new AonButton();
        newButton.id = this.NEW_BUTTON;
        newButton.icon = "add";
        newButton.title = "Agregar nuevo";
        newButton.style.display = "block";
        newButton.color = "transparent";
        newButton.style.width = "222px";
        newButton.style.border = "1px solid var(--aonBlue)";
        newButton.style.borderRadius = "5px";
        newButton.style.marginLeft = "14px";
        newButton.style.marginTop = "20px";
        sideMenu.appendChild(newButton);  
        let newBtText = this.getElement(newButton.TEXT);
        let newBtIcon = this.getElement(newButton.ICON);
        let newBtBt = this.getElement(newButton.BUTTON);
        newBtText.style.color = "var(--aonBlue)";
        newBtText.style.fontWeight = "normal";
        newBtIcon.style.color = "var(--aonBlue)";
        newBtBt.style.boxShadow = "none";


        newButton.addEventListener("mouseover", () => {
           newButton.style.backgroundColor = "rgba(0,36,105,0.1)";
        });
          
          newButton.addEventListener("mouseleave", () => {
            newButton.style.backgroundColor = "transparent";
        });

        let sideNavTitle = this.createDiv();
        sideNavTitle.className = "aonSidenavTitleBeta";
        sideNavTitle.innerHTML = "Accesos rápidos";
        sideMenu.appendChild(sideNavTitle);

        sideMenu.appendChild(this.buildSideNavRow("Todos","stacks"));
        sideMenu.appendChild(this.buildSideNavRow("Abierto recientemente","schedule"));


        let utilidades = this.createDiv();
        utilidades.className = "aonSidenavTitleBeta";
        utilidades.innerHTML = "Utilidades";
        sideMenu.appendChild(utilidades);

        sideMenu.appendChild(this.buildSideNavRow("Configuración", "folder_managed"));

        this.buildSideNavCard(sideMenu, "Documentos","1");
        this.buildSideNavCard2(sideMenu, "Auditoria facturas","2");

        let uploadButton = new AonButton();
        uploadButton.id = this.UPLOAD_BUTTON;
        uploadButton.icon = "publish";
        uploadButton.title = "Subir archivo";
        uploadButton.color = "transparent";
        uploadButton.style.display = "block";
        uploadButton.style.width = "222px";
        uploadButton.style.border = "2px dashed rgba(0, 0, 0, 0.1)";
        uploadButton.style.borderRadius = "5px";
        uploadButton.style.marginLeft = "12px";
        uploadButton.style.marginTop = "20px";
        sideMenu.appendChild(uploadButton);
        let text = this.getElement(uploadButton.TEXT);
        let icon = this.getElement(uploadButton.ICON);
        let button = this.getElement(uploadButton.BUTTON);
        text.style.color = "var(--aonBlue)";
        text.style.fontWeight = "normal";
        icon.style.color = "var(--aonBlue)";
        button.style.boxShadow = "none";


        uploadButton.addEventListener("mouseover", () => {
            uploadButton.style.backgroundColor = "rgba(0,36,105,0.1)";
        });
           
        uploadButton.addEventListener("mouseleave", () => {
            uploadButton.style.backgroundColor = "transparent";
        });
              
        let content = this.createDiv();
        content.id = this.CONTENT;
        content.style.height = "822px";
        content.style.width = "100%";
        content.style.display = "flex";
        content.style.backgroundColor = "rgb(250, 249, 248)";
        content.style.gap = "1rem";
        content.style.flexWrap = "wrap";
        divFlex.appendChild(content);

        let title = this.createDiv();
        title.id = this.TITLE;
        title.className = "aonSidenavTitleBeta";
        title.style.position = "absolute";
        title.style.marginLeft = "22px";
        title.style.marginTop = "30px";
        content.appendChild(title);

        this.options.forEach((opt, i) => {
            this.buildCard(opt, i);
        });
	}


    setTitle(title){
        this.title = title;
        let titleElement = this.getElement(this.TITLE);
        if(titleElement) {
            titleElement.innerHTML = this.title;
        }
    }


    buildSideNavCard(sideMenu,title,id){
        let card = new AonCard();
        card.id = id;
		card.title = title;
		card.style.minWidth = "230px";

        card.style.display = "flex";
        card.style.minHeight = "20px";
        card.style.marginTop = "10px";
        sideMenu.appendChild(card);
		
        let cardDiv = this.getElement(card.CARD);
		cardDiv.style.boxShadow = '0 2px 4px rgba(0,0,0,.1)';
		cardDiv.style.borderRadius = '2px';
        cardDiv.style.minWidth = "230px";
        
        let divGeneral = this.createDiv();
        divGeneral.appendChild(this.buildCardData("999 Pendientes"));
        divGeneral.appendChild(this.buildCardData("999 Rechazados"));
        divGeneral.appendChild(this.buildCardData("999 Fras sin contabilizar"));
        
	
		card.setContent(divGeneral);
        
    }

    buildSideNavCard2(sideMenu,title,id){
        let card = new AonCard();
        card.id = id;
		card.title = title;
		card.style.minWidth = "230px";

        card.style.display = "flex";
        card.style.minHeight = "20px";
        card.style.marginTop = "10px";
        sideMenu.appendChild(card);
		
        let cardDiv = this.getElement(card.CARD);
		cardDiv.style.boxShadow = '0 2px 4px rgba(0,0,0,.1)';
		cardDiv.style.borderRadius = '2px';
        cardDiv.style.minWidth = "230px";
        
        let divGeneral = this.createDiv();
        divGeneral.appendChild(this.buildCardData("999 Con IRPF Profesional"));
        divGeneral.appendChild(this.buildCardData("999 Con IRPF Alquiler"));
        divGeneral.appendChild(this.buildCardData("999 Intracomunitarios"));
        divGeneral.appendChild(this.buildCardData("999 Extracomunitarios"));
        
	
		card.setContent(divGeneral);
        
    }

    buildCard(opt, i){
        let card = new AonCard();
		card.id = "card" + i;
		card.title = opt.title;
		card.style.minWidth = "375px";

        card.style.display = "flex";
        card.style.height = "300px";
		card.style.marginLeft = "10px";
        card.style.marginTop = "60px";
        this.getContent().appendChild(card);
		
        let cardDiv = this.getElement(card.CARD);
		cardDiv.style.boxShadow = '0 2px 4px rgba(0,0,0,.1)';
		cardDiv.style.borderRadius = '2px';
        cardDiv.style.minWidth = "375px";
        
        let divGeneral = this.createDiv();
        opt.options.forEach((v) =>{
            divGeneral.appendChild(this.buildCardData(v));
        })
	
		card.setContent(divGeneral);
    }

    buildCardData(value) {
		let div = this.createDiv();
		div.style.marginTop = '10px';
		div.title = value;
        div.style.cursor = "pointer";
		div.style.display = "block";
        div.style.padding = "2px";

		let span = this.createDiv();
		span.className = CSS.AON_CARD_TEXT;
		span.innerHTML = value;
        span.style.cursor = "pointer";
        span.style.color = "var(--aonBlue)"
		div.appendChild(span);

        span.addEventListener('mouseover', function() {
            span.style.color = 'var(--aonBlue)';
            span.style.textDecoration = 'underline';
        });
        span.addEventListener('mouseout', function() {
            span.style.color = 'var(--aonBlue)';
            span.style.textDecoration = '';
        });

		return div;
	}

    

    buildSideNavRow(value, icon) {
		let div = this.createSpan();
		div.style.paddingTop = '5px';
		div.style.title = value;
        div.style.paddingLeft = "30px";
        div.style.borderRadius = "5px";
        div.style.paddingBottom = "5px";
        div.style.cursor = "pointer";
		div.style.display = "flex";

		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS;
		i.style.marginRight = '5px';
		i.style.verticalAlign = "middle";
		i.innerHTML= icon;
		div.appendChild(i);

		let span = this.createDiv();
		span.className = CSS.AON_CARD_TEXT;
        span.style.marginTop = "2px";
		span.innerHTML = value;
		div.appendChild(span);

        div.addEventListener("mouseover", () => {
            div.style.backgroundColor = "rgba(0,36,105,0.1)";
        });
           
        div.addEventListener("mouseleave", () => {
            div.style.backgroundColor = "transparent";
        });

		return div;
	}

    getContent(){
        return this.getElement(this.CONTENT);
    }

	
}
if(!window.customElements.get(TAG.AON_SUITE_MENU)){
	window.customElements.define(TAG.AON_SUITE_MENU, AonSuiteMenu);
}
