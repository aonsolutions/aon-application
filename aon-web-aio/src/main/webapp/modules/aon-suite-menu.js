import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import { AonCard } from 'aonsolutions/components/aon-card.js';
export class AonSuiteMenu extends AonElement {

	SIDE_MENU;
    CONTENT;
    TITLE;
	
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

        let title = this.createDiv();
        title.id = this.TITLE;
        title.className = "aonSidenavTitleBeta";
        /*
        title.style.fontSize = "16px";
        title.style.marginBottom = "10px";
        title.style.fontWeight = "bold";
        title.style.marginLeft = "10px";
        title.innerHTML = "Contabilidad";
        */
        sideMenu.appendChild(title);

        let content = this.createDiv();
        content.id = this.CONTENT;
        content.style.height = "822px";
        content.style.width = "100%";
        content.style.display = "flex";
        content.style.backgroundColor = "rgb(250, 249, 248)";
        content.style.gap="1rem";
        content.style.flexWrap="wrap";
        divFlex.appendChild(content);

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

    buildCard(opt, i){
        let card = new AonCard();
		card.id = "card" + i;
		card.title = opt.title;
		card.style.minWidth = "375px";

        card.style.display = "flex";
        card.style.height = "300px";
		card.style.marginLeft = "10px";
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
		div.style.title = value;
        div.style.cursor = "pointer";
		div.style.display = "block";
        div.style.padding = "2px";

		let span = this.createDiv();
		span.className = CSS.AON_CARD_TEXT;
		span.innerHTML = value;
        span.style.color = "var(--aonBlue)"
		div.appendChild(span);

        // Agrega los event listeners para cambiar el estilo al pasar el mouse
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

    getContent(){
        return this.getElement(this.CONTENT);
    }

	
}
if(!window.customElements.get(TAG.AON_SUITE_MENU)){
	window.customElements.define(TAG.AON_SUITE_MENU, AonSuiteMenu);
}
