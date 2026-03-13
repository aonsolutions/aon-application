import { AonElement } from '../components/AonElement.js';
import { MSG, CSS, EVENT, TAG, CONSTANT } from '../environments/environments.js';
import { AonIconButton } from '../components/aon-icon-button.js';
import { AonButton } from '../components/aon-button.js';
import { Apps, HomeApps, MenuApps, AuxApps, MENU_APPS, TOP_MENU_APPS, AON_APPS, HOME, APPS } from '../services/app.js';
import { AonCard } from '../components/aon-card.js';
import * as LS from '../services/localStorageService.js';
export class AonSuiteMenu extends AonElement {

    SIDE_MENU;
    CONTENT;
    //TITLE;
    NEW_BUTTON;
    UPLOAD_BUTTON;
    OPTIONS;
    CONF_BUTTON;
    DROPDOWN_BUTTON;
    options = [];
    last;
    new;
    cardData;

    constructor() {
        super();
    }

    connectedCallback() {
        this.clear();
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = 'aonSuiteMenu';
        this.SIDE_MENU = this.id + 'SideMenu';
        this.CONTENT = this.id + 'Content';
        //this.TITLE = this.id + 'Title';
        this.NEW_BUTTON = this.id + 'NewButton';
        this.UPLOAD_BUTTON = this.id + 'UploadButton';
        this.CONF_BUTTON = this.id + 'ConfButton';
        this.DROPDOWN_BUTTON = this.id + "DropDownButton";
        this.OPTIONS = this.id + "Options";
    }

    initOptions() {
        this.options = [];
    }

    build() {
        let divFlex = this.createDiv();
        divFlex.className = "aonFlex";
        this.appendChild(divFlex);
        // this.getApplication().setContent(divFlex);

        let sideMenu = this.createDiv();
        sideMenu.id = this.SIDE_MENU;
        sideMenu.className = "aonSuiteMenuSideMenu";
        divFlex.appendChild(sideMenu);

        let divNewButton = this.createDiv();
        divNewButton.style.display = "flex";
        sideMenu.appendChild(divNewButton);

        let newButton = new AonButton();
        newButton.id = this.NEW_BUTTON;
        newButton.icon = "add";
        newButton.title = this.new;
        newButton.className = "suiteMenuNewButton";
        newButton.color = "transparent";
        divNewButton.appendChild(newButton);
        let newBtText = this.getElement(newButton.TEXT);
        let newBtIcon = this.getElement(newButton.ICON);
        let newBtBt = this.getElement(newButton.BUTTON);
        newBtText.classList.add("suiteMenuNewButtonText");
        newBtIcon.classList.add("suiteMenuNewButtonIcon");
        newBtText.innerHTML = MSG.ADD_NEW;
        newBtBt.classList.add("suiteMenuNewButtonButton");
        this.setButtonHover(newButton);

        let dropdownButton = new AonButton();
        dropdownButton.id = this.DROPDOWN_BUTTON;
        dropdownButton.icon = "keyboard_arrow_down";
        dropdownButton.title = "Ver opciones";
        dropdownButton.color = "transparent";
        dropdownButton.classList.add("aonSuiteMenuDropdownButton");
        divNewButton.appendChild(dropdownButton);
        let dropBtIcon = this.getElement(dropdownButton.ICON);
        let dropBtBT = this.getElement(dropdownButton.BUTTON);
        let dropBtText = this.getElement(dropdownButton.TEXT);
        dropBtIcon.classList.add("suiteMenuDropdownButtonIcon");
        dropBtText.innerHTML = "";
        dropBtBT.classList.add("suiteMenuDropdownButtonButton");
        this.setButtonHover(dropdownButton);

        let options = this.createElement(TAG.DIV);
        options.id = this.OPTIONS;
        options.className = 'aonInputListo';
        sideMenu.appendChild(options);
        dropdownButton.addEventListener(EVENT.CLICK, () => {
            this.buildOptions(this.selectOptions);
        });

        let sideNavTitle = this.createDiv();
        sideNavTitle.className = "aonSidenavTitle suiteMenuSidenavTitle";
        sideNavTitle.innerHTML = MSG.QUICK_ACCESS;
        sideMenu.appendChild(sideNavTitle);

        sideMenu.appendChild(this.buildSideNavRow(MSG.ALL1, "stacks"));
        sideMenu.appendChild(this.buildSideNavRow(MSG.RECENTLY_OPENED, "schedule"));
        sideMenu.appendChild(this.buildSideNavRow(this.last, "quick_reference_all"));

        if (this.cardData)
            this.buildSideNavCard(sideMenu, "1");

        let utilidades = this.createDiv();
        utilidades.className = "aonSidenavTitle suiteMenuSide";
        utilidades.innerHTML = MSG.UTILITIES;


        sideMenu.appendChild(utilidades);

        sideMenu.appendChild(this.buildSideNavRow(MSG.CONFIGURATION, "folder_managed"));

        if (!this.uploadButton) {
            let uploadButton = new AonButton();
            uploadButton.id = this.UPLOAD_BUTTON;
            uploadButton.icon = "publish";
            uploadButton.title = "Cargar archivo";
            uploadButton.color = "transparent";
            uploadButton.classList.add("aonSuiteMenuUploadButton");
            sideMenu.appendChild(uploadButton);
            let text = this.getElement(uploadButton.TEXT);
            let icon = this.getElement(uploadButton.ICON);
            let button = this.getElement(uploadButton.BUTTON);
            text.className = CSS.AON_CARD_TEXT;
            text.classList.add("suiteMenuUploadButtonText");
            icon.classList.add("suiteMenuUploadButtonIcon");
            button.classList.add("suiteMenuUploadButtonButton");
            this.setButtonHover(uploadButton);
        }

        let content = this.createDiv();
        content.id = this.CONTENT;
        content.classList.add("suiteMenuContent");
        divFlex.appendChild(content);

        let div = this.createDiv();
        div.classList.add("suiteMenuDiv");
        content.appendChild(div);

		/*
        let title = this.createDiv();
        title.id = this.TITLE;
        title.className = "aonSidenavTitle";
        div.appendChild(title);
		*/

        let div2 = this.createDiv();
        div2.classList.add("suiteMenuDiv2");
        content.appendChild(div2);

        this.options
            .filter(opt => !opt.filter || opt.filter())
            .forEach((opt, i) => {
                this.buildCard(opt, i, div2);
            });
    }

	/*
    setTitle(title) {
        this.title = title;
        let titleElement = this.getElement(this.TITLE);
        if (titleElement) {
            titleElement.innerHTML = this.title;
        }
    }
    */

    buildSideNavCard(sideMenu, id) {
        let card = new AonCard();
        card.id = id;
        card.title = this.cardData.title;
        card.classList.add("suiteMenuSideNavCardCard");
        sideMenu.appendChild(card);

        let cardTitle = this.getElement(card.TITLE_SECTION1);
        cardTitle.classList.add("suiteMenuSideNavCardTitle");

        let icon = new AonIconButton();
        icon.icon = "info";
        icon.color = "var(--aonSuiteMenuNewButtonText)";
        icon.classList.add("suiteMenuSideNavCardIcon");
        card.appendChild(icon);


        let cardDiv = this.getElement(card.CARD);
        cardDiv.classList.add("suiteMenuSideNavCardDiv");

        let divGeneral = this.createDiv();
        this.cardData.info.forEach((info) => {
            divGeneral.appendChild(this.buildSideNavCardData(info, "999"));
        });

        card.setContent(divGeneral);
    }

    buildCard(opt, i, div) {
        if (!opt.disabled) {
            let options = opt.options.filter(opt => !opt.filter || opt.filter());
            if (options.length === 0)
                return;

            let card = new AonCard();
            card.id = "card" + i;
            card.title = opt.title;
            card.classList.add("suiteMenuCardCard");
            div.appendChild(card);

            let cardDiv = this.getElement(card.CARD);
            cardDiv.classList.add("suiteMenuCardDiv");

            let divGeneral = this.createDiv();
            options.forEach((v) => {
                if (!v.disabled) {
                    divGeneral.appendChild(this.buildCardData(v));
                }
            })

            card.setContent(divGeneral);
        }
    }

    buildCardData(value) {
        let div = this.createDiv();
        div.classList.add("suiteMenuCardDataDiv");

        let span = this.createDiv();
        span.className = CSS.AON_CARD_TEXT;
        span.innerHTML = value.description;
        span.classList.add("suiteMenuCardDataSpan");
        div.appendChild(span);

        if (value.description2) {
            div.style.display = "flex";
            let span2 = this.createSpan();
            span2.className = CSS.AON_CARD_TEXT;
            span2.innerHTML = value.description2
            span2.classList.add("suiteMenuCardDataSpan2");
            div.appendChild(span2);
        }

        let rootPanel = this.getElement("rootPanel");
        span.addEventListener(EVENT.CLICK, value.action);
        span.addEventListener(EVENT.CLICK, function () {
            rootPanel.style.backgroundColor = "rgb(250, 249, 248)";
        });
        return div;
    }


    buildSideNavCardData(value, number) {
        let div = this.createDiv();
        div.classList.add("suiteMenuSideNavCardDataDiv");

        let span = this.createSpan();
        span.className = CSS.AON_CARD_TEXT;
        span.innerHTML = value;
        div.appendChild(span);

        let span2 = this.createSpan();
        span2.className = CSS.AON_CARD_TEXT;
        span2.innerHTML = number;
        span2.classList.add("suiteMenuSideNavCardDataSpan2");
        div.appendChild(span2);

        return div;
    }

    setButtonHover(button) {
        button.classList.add("suiteMenuSideNavCardDataButton");
    }


    buildOptions(options) {
        this.clearElementById(this.OPTIONS);

        if (options.length === 0) return null;

        let div = this.getElement(this.OPTIONS);
        div.classList.add('is-visible');
        div.classList.add("suiteMenuOptionsDiv");

        div.addEventListener("mouseleave", () => {
            div.classList.remove('is-visible');
        });

        let ul = this.createElement(TAG.UL);
        ul.classList.add(CSS.AON_UL);
        ul.classList.add("suiteMenuOptionsUl");
        ul.classList.add(CSS.AON_INPUT_LIST_OPTIONS_UL);
        ul.setAttribute('for', this.getAttribute(CONSTANT.ID) + 'Icon');
        div.appendChild(ul);

        options.forEach((opt) => {
            this.buildLi(opt, ul, div);
        });

    }

    buildSideNavRow(value, icon) {
        let div = this.createSpan();
        div.classList.add("suiteMenuSideNavRowDiv");

        let i = this.createElement(TAG.I);
        i.className = CSS.MATERIAL_ICONS;
        i.classList.add("suiteMenuSideNavRowI");
        i.innerHTML = icon;
        div.appendChild(i);


        let span = this.createDiv();
        span.className = CSS.AON_CARD_TEXT;
        span.classList.add("suiteMenuSideNavRowSpan");
        span.innerHTML = value;
        div.appendChild(span);

        return div;
    }

    buildLi(option, ul, div) {
        if (!option.disabled) {
            let li = this.createElement(TAG.LI);
            li.className = 'aonInputListOptionsItem';
            li.innerHTML = option.title;
            li.classList.add("suiteMenuLi");
            ul.appendChild(li);

            li.addEventListener(EVENT.CLICK, () => {
                div.classList.remove('is-visible');
                option.action();
            });
        }

    }

    getOptions() {
        if (!this.options?.length) {
            this.initOptions();
        }
        return this.options;
    }

    getContent() {
        return this.getElement(this.CONTENT);
    }

    getDropDown() {
        return this.getElement(this.DROPDOWN_BUTTON);
    }

    hasBank() {
        return this.getDur().hasBank();
    }

    isDomainManagementAvailable() {
        return this.getDur().isDomainManagementAvailable();
    }
    isNotDomainManagementAvailable() {
        return !this.getDur().isDomainManagementAvailable();
    }

}
if (!window.customElements.get(TAG.AON_SUITE_MENU)) {
    window.customElements.define(TAG.AON_SUITE_MENU, AonSuiteMenu);
}
