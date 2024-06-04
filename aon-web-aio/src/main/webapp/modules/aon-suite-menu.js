import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CSS, EVENT, TAG, CONSTANT } from 'aonsolutions/environments/environments.js'; 
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import { AonButton } from 'aonsolutions/components/aon-button.js';
import { AonCard } from 'aonsolutions/components/aon-card.js';
export class AonSuiteMenu extends AonElement {

	SIDE_MENU;
    CONTENT;
    TITLE;
    NEW_BUTTON;
    UPLOAD_BUTTON;
    OPTIONS;
    CONF_BUTTON;
    DROPDOWN_BUTTON;
    options;
    last; 
    new;
    cardData;
    valueAlias;
    nameAlias;

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
        this.DROPDOWN_BUTTON = this.id + "DropDownButton";
        this.OPTIONS = this.id + "Options";
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
        sideMenu.style.overflowY = "auto";
        sideMenu.style.height = "calc(-62px + 100vh)";
        sideMenu.style.paddingBottom = "80px"
        divFlex.appendChild(sideMenu);

        let divNewButton = this.createDiv();
        divNewButton.style.display = "flex";
        sideMenu.appendChild(divNewButton);

        let newButton = new AonButton();
        newButton.id = this.NEW_BUTTON;
        newButton.icon = "add";
        newButton.title = this.new;
        newButton.style.display = "block";
        newButton.color = "transparent";
        newButton.style.width = "157px";
        newButton.style.borderTopLeftRadius = "5px";
        newButton.style.borderBottomLeftRadius = "5px";
        newButton.style.marginLeft = "14px";
        newButton.style.marginTop = "20px";
        newButton.style.borderTopWidth = "1px";
        newButton.style.borderTopStyle = "Solid";
        newButton.style.borderTopColor = "rgb(72,70,68)";
        newButton.style.borderBottomWidth = "1px";
        newButton.style.borderBottomStyle = "Solid";
        newButton.style.borderBottomColor = "rgb(72,70,68)";
        newButton.style.borderLeftWidth = "1px";
        newButton.style.borderLeftStyle = "Solid";
        newButton.style.borderLeftColor = "rgb(72,70,68)";
        divNewButton.appendChild(newButton);  
        let newBtText = this.getElement(newButton.TEXT);
        let newBtIcon = this.getElement(newButton.ICON);
        let newBtBt = this.getElement(newButton.BUTTON);
        newBtText.style.color = "rgb(72,70,68)";
        newBtText.style.fontWeight = "normal";
        newBtText.innerHTML = MSG.ADD_NEW;
        newBtIcon.style.color = "rgb(72,70,68)";
        newBtBt.style.boxShadow = "none";
        this.setButtonHover(newButton);

        let dropdownButton = new AonButton();
        dropdownButton.id = this.DROPDOWN_BUTTON;
        dropdownButton.icon = "keyboard_arrow_down";
        dropdownButton.title = "Ver opciones";
        dropdownButton.style.display = "block";
        dropdownButton.color = "transparent";
        dropdownButton.style.width = "50px";
        dropdownButton.style.borderTopRightRadius = "5px";
        dropdownButton.style.borderBottomRightRadius = "5px";
        dropdownButton.style.border = "1px solid rgb(72,70,68)";
        dropdownButton.style.marginTop = "20px";
        dropdownButton.style.justifyContent = "center";
        divNewButton.appendChild(dropdownButton);
        let dropBtIcon = this.getElement(dropdownButton.ICON);
        let dropBtBT = this.getElement(dropdownButton.BUTTON);
        let dropBtText = this.getElement(dropdownButton.TEXT);
        dropBtIcon.style.color = "rgb(72,70,68)";
        dropBtIcon.style.marginLeft = "-7px";
        dropBtText.innerHTML = "";
        dropBtBT.style.boxShadow = "none";
        this.setButtonHover(dropdownButton);

        let options = this.createElement(TAG.DIV);
        options.id = this.OPTIONS;
        options.className = 'aonInputListOptions';
        sideMenu.appendChild(options);
        dropdownButton.addEventListener(EVENT.CLICK, () => {
           this.buildOptions(this.selectOptions);
        });

        let sideNavTitle = this.createDiv();
        sideNavTitle.className = "aonSidenavTitleBeta";
        sideNavTitle.innerHTML = MSG.QUICK_ACCESS;
        sideMenu.appendChild(sideNavTitle);

        sideMenu.appendChild(this.buildSideNavRow(MSG.ALL1,"stacks"));
        sideMenu.appendChild(this.buildSideNavRow(MSG.RECENTLY_OPENED,"schedule"));
        sideMenu.appendChild(this.buildSideNavRow(this.last,"quick_reference_all"));

        this.buildSideNavCard(sideMenu,"1");

        let utilidades = this.createDiv();
        utilidades.className = "aonSidenavTitleBeta";
        utilidades.innerHTML = MSG.UTILITIES;
        sideMenu.appendChild(utilidades);

        sideMenu.appendChild(this.buildSideNavRow(MSG.CONFIGURATION, "folder_managed"));

        let uploadButton = new AonButton();
        uploadButton.id = this.UPLOAD_BUTTON;
        uploadButton.icon = "publish";
        uploadButton.title = "Cargar archivo";
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
        text.className= CSS.AON_CARD_TEXT;
        text.style.fontWeight = "normal";
        icon.style.color = "rgb(72,70,68)";
        button.style.boxShadow = "none";
        this.setButtonHover(uploadButton);
              
        let content = this.createDiv();
        content.id = this.CONTENT;
        content.style.height = "calc(-62px + 100vh)";
        content.style.width = "100%";
        content.style.backgroundColor = "rgb(250, 249, 248)";
        content.style.overflowY = "auto";
        divFlex.appendChild(content);

        let div = this.createDiv();
        div.style.marginTop = "15px";
        div.style.width = "100%"
        div.style.justifyContent = "left";
        div.style.display = "flex";
        content.appendChild(div);

        let title = this.createDiv();
        title.id = this.TITLE;
        title.className = "aonSidenavTitleBeta";
        div.appendChild(title);

        let div2 = this.createDiv();
        div2.style.width = "100%";
        div2.style.display = "flex";
        div2.style.gap = "1rem";
        div2.style.flexWrap = "wrap";
        div2.style.position = "relative";
        div2.style.justifyContent = "center";
        div2.style.paddingRight = "20px";
        div2.style.paddingBottom = "70px";
        content.appendChild(div2);

        this.options.forEach((opt, i) => {
            this.buildCard(opt, i,div2);
        });
	}

    setTitle(title){
        this.title = title;
        let titleElement = this.getElement(this.TITLE);
        if(titleElement) {
            titleElement.innerHTML = this.title;
        }
    }

    buildSideNavCard(sideMenu,id){
        let card = new AonCard();
        card.id = id;
		card.title = this.cardData.title;
		card.style.minWidth = "230px";
        sideMenu.appendChild(card);

        let cardTitle = this.getElement(card.TITLE_SECTION1);
        cardTitle.style.marginLeft = "24px";

        let icon = new AonIconButton();
        icon.icon = "info";
        icon.style.height = "fit-content";
        icon.style.position = "relative";
        icon.style.top = "15px";
        icon.style.left = "-231px";
        card.appendChild(icon);

        card.style.display = "flex";
        card.style.minHeight = "20px";
        card.style.marginTop = "10px";
  
        let cardDiv = this.getElement(card.CARD);
		cardDiv.style.boxShadow = '0 2px 4px rgba(0,0,0,.1)';
		cardDiv.style.borderRadius = '2px';
        cardDiv.style.minWidth = "230px";
        
        let divGeneral = this.createDiv();
        this.cardData.info.forEach((info)=>{
            divGeneral.appendChild(this.buildSideNavCardData(info,"999"));
        });
        
		card.setContent(divGeneral);
    }

    buildCard(opt, i,div){
        let card = new AonCard();
		card.id = "card" + i;
		card.title = opt.title;
		card.style.minWidth = "375px";
        card.style.maxWidth = "375px";      

        card.style.display = "flex";
        card.style.height = "300px";
		card.style.marginLeft = "10px";
        //card.style.marginTop = "20px";
        div.appendChild(card);
		
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
		div.style.display = "block";
        div.style.padding = "2px";

		let span = this.createDiv();
		span.className = CSS.AON_CARD_TEXT;
		span.innerHTML = value.description;
        span.title = value.title;
        span.style.cursor = "pointer";
        span.style.color = "var(--aonBlue)"
		div.appendChild(span);

        if(value.description2){
            div.style.display = "flex";
            let span2 = this.createSpan();
            span2.className = CSS.AON_CARD_TEXT;
            span2.innerHTML = value.description2
            span2.style.marginLeft = "5px";
            div.appendChild(span2);
        }

        span.addEventListener('mouseover', function() {
            span.style.color = 'var(--aonBlue)';
            span.style.textDecoration = 'underline';
        });
        span.addEventListener('mouseout', function() {
            span.style.color = 'var(--aonBlue)';
            span.style.textDecoration = '';
        });

        span.addEventListener(EVENT.CLICK, value.action);

		return div;
	}


    buildSideNavCardData(value,number) {
		let div = this.createDiv();
		div.style.marginTop = '10px';
		div.title = value;
        div.style.padding = "2px";
        div.style.position = "relative";

		let span = this.createSpan();
		span.className = CSS.AON_CARD_TEXT;
		span.innerHTML = value;
		div.appendChild(span);

        let span2 = this.createSpan();
        span2.className = CSS.AON_CARD_TEXT;
        span2.innerHTML = number;
        span2.style.right = "0px";
        span2.style.position = "absolute";
        div.appendChild(span2);

		return div;
	}

    setButtonHover(button){
        button.addEventListener("mouseover", () => {
            button.style.backgroundColor = "rgba(72,70,68,0.1)";
        });
           
        button.addEventListener("mouseleave", () => {
            button.style.backgroundColor = "transparent";
        });
    }
    

    buildOptions(options) {
        this.clearElementById(this.OPTIONS);

        if(options.length === 0) return null;

        let div = this.getElement(this.OPTIONS);
        div.classList.add('is-visible');
        div.style.width = "209px";
        div.style.marginLeft = "13px";
        div.style.marginTop = "5px";
        div.style.borderRadius = "5px";
        div.style.boxShadow= "rgba(0, 0, 0, 0.15) 0px 24px 54px, rgba(0, 0, 0, 0.08) 0px 4.5px 13.5px";

        div.addEventListener("mouseleave", () => {
           div.classList.remove('is-visible');
        });

        if(this.default ||  this.hasAttribute(CONSTANT.DEFAULT)) {
            let empty = {};
            empty[this.nameAlias] = '-';
            empty[this.valueAlias] = '';
            options.unshift(empty); //EMPTY
        }

        let ul = this.createElement(TAG.UL);
        ul.classList.add(CSS.AON_UL);
        ul.style.width = "221px";
        ul.style.paddingTop = "0px";
        ul.style.paddingRight = "15px";
        ul.style.paddingBottom = "0px";
        ul.style.paddingLeft = "0px";
        ul.classList.add(CSS.AON_INPUT_LIST_OPTIONS_UL);
        ul.setAttribute('for', this.getAttribute(CONSTANT.ID) + 'Icon');
        div.appendChild(ul);
        
        options.forEach((opt) =>{
           this.buildLi(opt,ul,div);
        });
        
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
        div.style.transition = "background-color 0.2s"

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

    buildLi(option, ul, div){
        let li = this.createElement(TAG.LI);
        li.className = 'aonInputListOptionsItem';
        li.innerHTML = option.title;
        li.style.padding = "5px";
        li.style.paddiingLeft = "25px";
        ul.appendChild(li);
    
        li.addEventListener(EVENT.CLICK, () => {
          div.classList.remove('is-visible');
          option.action();

        });
    
        return li;
    }

    getContent(){
        return this.getElement(this.CONTENT);
    }

    getDropDown(){
        return this.getElement(this.DROPDOWN_BUTTON);
    }

	
}
if(!window.customElements.get(TAG.AON_SUITE_MENU)){
	window.customElements.define(TAG.AON_SUITE_MENU, AonSuiteMenu);
}
