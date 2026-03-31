import { AonElement } from "./AonElement.js";
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../environments/environments.js";
import { AonIconButton } from "./aon-icon-button.js";
import { AonIcon } from "./aon-icon.js";
import "./aon-toolbar.js";
import "./aon-loader.js";
import "./aon-icon.js";
import "./aon-dialog.js";
import "./aon-dialog-menu.js";
import "./aon-toast.js";
import * as LS from '../services/localStorageService.js';
import { AonToolbar } from "./aon-toolbar.js";
import { AonLoader } from "./aon-loader.js";
import { AonDialogMenu } from "./aon-dialog-menu.js";
import { AonDialog } from "./aon-dialog.js";
import { AonToast } from "./aon-toast.js";
import { getRegistryNotes } from "../services/registryService.js";
import * as ACTION from "../modules/actions.js";
import * as GWT from '../gwt/gwt.js';
import { getCompany } from "../services/companyService.js";

export class AonApplication extends AonElement {
  
  SIDENAV;
  SIDENAV_RIGHT;
  TOOLBAR;
  LOADER;
  CONTENT;
  OPTION_DIALOG;
  DIALOG;
  TOAST;
  MOBILE_SIDENAV;
  MOBILE_SIDENAV_CONTENT;
  selected;
  VIEWS;

  content; 

  static get observedAttributes() {
    return [CONSTANT.TITLE];
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

  get main() {
    return this.getAttribute("main");
  }

  set main(main) {
    this.setAttribute("main", main);
  }

  get drag_and_drop() {
    return this.getAttribute("drag_and_drop");
  }

  set drag_and_drop(drag_and_drop) {
    this.setAttribute("drag_and_drop", drag_and_drop);
  }

  get sidenav() {
    return this.getAttribute("sidenav");
  }

  set sidenav(sidenav) {
    this.setAttribute("sidenav", sidenav);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (CONSTANT.TITLE === name) {
      let toolbar = this.getElement(this.TOOLBAR);
      if (toolbar) toolbar.setAttribute(CONSTANT.TITLE, newValue);
    }
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.SIDENAV = this.id + "Sidenav";
    this.SIDENAV_RIGHT = this.id + "SidenavRight";
    this.TOOLBAR = this.id + "Toolbar";
    this.LOADER = this.id + "Loader";
    this.CONTENT = this.id + "Content";
    this.OPTION_DIALOG = this.id + "OptionDialog";
    this.DIALOG = this.id + "Dialog";
    this.TOAST = this.id + "Toast";
    this.VIEWS = [];
    this.MOBILE_SIDENAV = this.id + 'MobileSidenav';
    this.MOBILE_SIDENAV_CONTENT = this.MOBILE_SIDENAV + 'Content';
  }


  build() {
    let toolbar = new AonToolbar();
    toolbar.id = this.TOOLBAR;
    toolbar.title = this.getTitle();
    this.appendChild(toolbar);

    let loader = new AonLoader();
    loader.id = this.LOADER;
    this.appendChild(loader);

    let div = this.createDiv(); 
    div.className = this.isMobile() ? CSS.AON_MOBILE_APPLICATION_CONTENT : CSS.AON_APPLICATION_CONTENT;
    this.appendChild(div);

    let leftSidenav = this.createDiv(this.SIDENAV, this.getSidenavClassName());
    div.appendChild(leftSidenav);
    leftSidenav.style.flexBasis = this.isMobile() || this.isSidenavBlock() ? "0px" : this.getSidenavWidth();
    if(this.isMobile()  && this.isSab()) {
      leftSidenav.style.height = 'calc(100vh - 172px)';
    }

    let content = this.createDiv(this.CONTENT);
    div.appendChild(content);
    content.className =
      this.isMobile() || this.isSidenavBlock()
        ? "aonMobileContent"
        : CSS.AON_CONTENT_BETA;
    if(this.isMobile() && this.isSab()){
      content.style.bottom = '69px';
    }

    let rightSidenav = this.createDiv(this.SIDENAV_RIGHT, this.getRightSidenavClassName());
    div.appendChild(rightSidenav);

    let dialogMenu = new AonDialogMenu();
    dialogMenu.id = this.OPTION_DIALOG;
    this.appendChild(dialogMenu);
    
    let dialog = new AonDialog();
    dialog.id = this.DIALOG;
    this.appendChild(dialog);

    let toast = new AonToast();
    toast.id = this.TOAST;
    this.appendChild(toast);

    toolbar.toogleSidenav(() => this.isMobile() 
      ? this.toogleMobileSidenav() : this.toogleSidenav());

    this.content = this.getContent();
    if(this.isMobile()) {
      this.buildMobileSidenav();
    }

    if (this.hasAttribute("drag_and_drop")) {
      this.buildDragAndDrop(true);
    }

    if (this.hasAttribute("main")) {
      toolbar.style.display = "none";
      leftSidenav.style.height = "calc(100vh - 61px)";
    }

    if (!localStorage.getItem("aon_solutions")) {
      let top = leftSidenav.getBoundingClientRect().top;
      if (top === 82 || top === 110) {
        top = top + 20;
      } else if (top === 124) {
        top = top + 14;
      } else top = top + 1;
      localStorage.setItem("aon_application_top", top);
      leftSidenav.style.height = `calc(100vh - ${top}px)`;
      content.style.height = `calc(100vh - ${top}px)`;
    }
  }

  getSidenavClassName() {
    return this.isMobile() ? 'aonMobileSidenav' : 'aonSidenav';
  }

  getRightSidenavClassName() {
    return this.isMobile() ? 'aonMobileRightSidenav' : 'aonRightSidenav';
  }

  buildMobileSidenav() {
    let div = this.createElement(TAG.DIV);
    div.id = this.MOBILE_SIDENAV;
    div.className = CSS.AON_DIALOG;
    div.style.paddingTop = '0px';

    let div2 = this.createElement(TAG.DIV);
    div2.id = div.id + 'Content';
    div2.className = CSS.AON_DIALOG_CONTENT;
    div2.style.height = '100%';
    div2.style.margin = '0px';
    div2.style.padding = '0px';
    
    div.appendChild(div2);
    this.appendChild(div);

    div.onclick = (event) => {
			if (event.target === div) {
				this.closeMobileSidenav();
			}
		}
  }

  startLoader() {
    let el = this.getElement(this.LOADER);
    if(el) el.start();
  }

  stopLoader() {
    let el = this.getElement(this.LOADER);
    if(el) el.stop();
  }

  startLoading() {
    let el = this.getElement(this.LOADER);
    if(el) el.startLoading();
  }

  stopLoading() {
    let el = this.getElement(this.LOADER);
    if(el) el.stopLoading();
  }

  toogleMobileSidenav() {
    let sidenav = this.getElement(this.MOBILE_SIDENAV);
    sidenav.firstChild.classList.add(CSS.AON_TRANSITION_LEFT);
    if(sidenav.style.display == 'block') {
      this.closeMobileSidenav();
    } else {
      this.openMobileSidenav();
    }
  }

  openMobileSidenav() {
    let sidenav =this.getElement(this.MOBILE_SIDENAV);
    sidenav.style.display = "block";
    sidenav.firstChild.classList.add(CSS.ACTIVE);
    let aonMobileMenu = this.getElement('aonMobileMenu');
    aonMobileMenu.style.display = 'none'; 
  }

  closeMobileSidenav() {
      let sidenav = this.getElement(this.MOBILE_SIDENAV);
      sidenav.style.display = "none";
      sidenav.firstChild.classList.remove(CSS.ACTIVE);
      let aonMobileMenu = this.getElement('aonMobileMenu');
      aonMobileMenu.style.display = 'block'; 
  }

  toogleSidenav() {
    if (this.isSidenavBlock()) {
      this.closeSidenav();
    } else {
      let sidenav = this.getElement(this.SIDENAV);
      let toolbar = this.getElement(this.TOOLBAR);
      
      if (sidenav.style.flexBasis === "0px") {
        sidenav.style.flexBasis = this.getSidenavWidth();
        sidenav.classList.remove("closeSidenav");
      } else {
        sidenav.style.flexBasis = "0px";
        sidenav.classList.add("closeSidenav");
      }
      
      toolbar.toogleClose();
    }
  }

  closeSidenav() {
    let sidenav = this.getElement(this.SIDENAV);
    sidenav.style.flexBasis = "0px";
    sidenav.classList.add("closeSidenav");
    
	  let toolbar = this.getElement(this.TOOLBAR);
	  toolbar.toogleClose();
  }

  toogleRightSidenav() {
   /*
    if (this.isSidenavBlock()) {
      this.closeRightSidenav();
    } else {
      let rightSidenav = this.getRightSidenav();
      if (rightSidenav.style.flexBasis === "0px") {
        rightSidenav.style.flexBasis = "350px";
      } else {
        rightSidenav.style.flexBasis = "0px";
      }
    }
    */
    
    let rightSidenav = this.getRightSidenav();
	  if (rightSidenav.style.flexBasis === "0px" || rightSidenav.style.flexBasis.length == 0) {
	    rightSidenav.style.flexBasis = "350px";
	  } else {
	    rightSidenav.style.flexBasis = "0px";
	  }
	  
  }

  openRightSidenav() {
    let rightSidenav = this.getRightSidenav();
    rightSidenav.style.flexBasis = "350px";
  }

   closeRightSidenav() {
    let rightSidenav = this.getRightSidenav();
    rightSidenav.style.flexBasis = "0px";
  }

  addMobileSidenavHeader(app) {
    let div = this.createElement(TAG.DIV);
    div.style.height = '59px';
    div.style.padding = '10px';
    div.style.paddingLeft = '20px';
    if(!this.isBeta()) div.style.borderBottom = '1px solid #ebebeb';

    let sidenav = this.isMobile()
      ? this.getElement(this.MOBILE_SIDENAV_CONTENT)
      : this.getElement(this.SIDENAV);
    let span = this.createElement(TAG.SPAN);
		span.innerHTML = `<aon-icon icon="${app.icon}" color="${app.color}" size="40px"></aon-icon>`;
		let span2 = this.createElement(TAG.SPAN);
    span2.id = 'aonSideNavMobileTitle'+app.app;
		span2.className = 'aonAppTitle';
    span2.style.fontSize = '18px';
    span2.innerHTML = app.title;
		span.appendChild(span2);
    div.appendChild(span);
    sidenav.appendChild(div);
  }

  addSidenavWidget(title, element) {
    let sidenav = this.getElement(this.SIDENAV);

    let div = this.createElement(TAG.DIV);
    div.style.paddingBottom = "25px";
    sidenav.appendChild(div);

    let sidenavTitle = this.createElement(TAG.DIV);
    sidenavTitle.className = CSS.AON_SIDENAV_TITLE;
    sidenavTitle.innerHTML = title.toUpperCase();
    sidenavTitle.title = title;
    div.appendChild(sidenavTitle);

    let content = this.createElement(TAG.DIV);
    content.style.paddingLeft = "26px";
    content.appendChild(element);
    div.appendChild(content);
  }


  addSidenavWidget2(data, element) {
    this.addSidenavOptionsTitle(data);

    let sidenav = this.isMobile() ? this.getElement(this.MOBILE_SIDENAV_CONTENT) : this.getElement(this.SIDENAV);
    if(data && data.id && sidenav) {
        let div = this.getElement(sidenav.id + data.id);
        if(div){
          let content = this.createElement(TAG.DIV);
          content.style.paddingLeft = "26px";
          content.appendChild(element);
          div.appendChild(content);
        }
    }
  }

  addSidenavWidgetHTML(title, html) {
    let sidenav = this.getElement(this.SIDENAV);
    if(sidenav){
      let div = this.createElement(TAG.DIV);
      div.style.paddingBottom = "25px";
      sidenav.appendChild(div);

      let sidenavTitle = this.createElement(TAG.DIV);
      sidenavTitle.className = CSS.AON_SIDENAV_TITLE;
      sidenavTitle.innerHTML = title.toUpperCase();
      sidenavTitle.title = title;
      div.appendChild(sidenavTitle);

      let content = this.createElement(TAG.DIV);
      content.style.paddingLeft = "26px";
      content.innerHTML = html;
      div.appendChild(content);
    }
  }

  addSidenavOptionsTitle(data, newButton, first) {
    let sidenav = this.isMobile()
      ? this.getElement(this.MOBILE_SIDENAV_CONTENT)
      : this.getElement(this.SIDENAV);
    let div = this.createElement(TAG.DIV);
    div.id = sidenav.id + data.id;
    div.style.paddingBottom = "10px";
    
    // Insert to sidenav
    if(first) sidenav.insertBefore(div, sidenav.firstChild);
    else sidenav.appendChild(div);

    if(data.button && !this.isMobile()) {
      let buttonDiv = this.createElement(TAG.DIV);
      buttonDiv.className = CSS.AON_SIDENAV_TITLE_BUTTON;
      let button = new AonIconButton();
      button.icon = data.button.icon;
      button.id = div.id + data.button.id;
      button.getIcon().style.fontSize = "20px";
      button.getIcon().title = data.button.title;
      buttonDiv.appendChild(button);
      div.appendChild(buttonDiv);
      button.addEventListener(EVENT.CLICK, data.button.fn);
    }
    if (newButton && !this.isMobile()) {
      let addButton = this.createElement(TAG.DIV);
      addButton.className = CSS.AON_SIDENAV_TITLE_BUTTON;
      let aonIconButton = new AonIconButton();
      aonIconButton.icon = "add";
      aonIconButton.id = div.id + "NewButton";
      addButton.appendChild(aonIconButton);
      div.appendChild(addButton);
      this.getElement(div.id + "NewButton").addEventListener(EVENT.CLICK, newButton);
    }

    let sidenavTitle = this.createElement(TAG.DIV);
    sidenavTitle.className = CSS.AON_SIDENAV_TITLE;
    sidenavTitle.id = "aonSidenavTitle"+data.id;
    sidenavTitle.title = data.name;
    sidenavTitle.style.cursor = "pointer";
    sidenavTitle.style.userSelect = "none";
    sidenavTitle.style.marginLeft = LS.isNewTheme() ? "10px": "2px";

    let arrowTitleSpan = this.createElement(TAG.SPAN);
    arrowTitleSpan.className = CSS.AON_SIDENAV_TITLE_ARROW;
    arrowTitleSpan.style.borderColor = data.app && data.app.color ? data.app.color : 'black';

    let arrowTitle = this.createElement("i");
    arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_LESS;
    arrowTitle.className = "material-icons aonVerticalMiddle";
    sidenavTitle.appendChild(LS.isNewTheme() ? arrowTitleSpan : arrowTitle);

    sidenavTitle.addEventListener(EVENT.CLICK, ()=>{
	  const ul = div.querySelector("ul");
	  
	  if(ul){
		arrowTitleSpan.classList.toggle("closeIcon");
	    ul.classList.toggle(CSS.ELEMENT_HIDDEN);
	    
	    if(ul && ul.classList.contains(CSS.ELEMENT_HIDDEN)) {
	      arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_MORE;
	      sidenavTitle.style.marginBottom = "0";
	    } else {
	      arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_LESS;
	      sidenavTitle.style.marginBottom = "10px";
	    }
	  } else {
		
		let nodes = div.querySelectorAll(`:scope > div`);
	  	let last = nodes[nodes.length- 1];
		
	   	if(last){
			arrowTitleSpan.classList.toggle("closeIcon");
		    ul && ul.classList.toggle(CSS.ELEMENT_HIDDEN);
		    last && last.classList.toggle(CSS.ELEMENT_HIDDEN);
		    
		    if( (ul && ul.classList.contains(CSS.ELEMENT_HIDDEN)) || (last && last.classList.contains(CSS.ELEMENT_HIDDEN))) {
		      arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_MORE;
		      sidenavTitle.style.marginBottom = "0";
		    } else {
		      arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_LESS;
		      sidenavTitle.style.marginBottom = "10px";
		    }
	  	}
	  }
	});  

    let span = this.createElement(TAG.SPAN);
	span.id = `aonSidenavTitle${data.id}Name`;
    span.innerHTML = data.name.toUpperCase();
    sidenavTitle.appendChild(span);

    div.appendChild(sidenavTitle);    return div;
  }

   addSidenavOptionsList(data, options) {
    let sidenav = this.isMobile() ? this.getElement(this.MOBILE_SIDENAV_CONTENT) : this.getElement(this.SIDENAV);
    if(data && data.id && sidenav){
      let div = this.getElement(sidenav.id + data.id);
      if(div){
        const idUl = div.id + "List";
        let ul =  this.getElement(idUl); 
        if(!ul){
          ul = this.createElement(TAG.UL);
          ul.id =idUl;
          ul.classList.add(CSS.AON_UL);
          ul.classList.add(CSS.AON_CLIP);
          div.appendChild(ul);
        }
        options.forEach((option, i) => {
          this.addSidenavOptionsListValue(data, option, ul);
        });
        return ul;
      }
    }
    return null;
  }

  addSidenavSelectOptionsTitle(data, newButton) {
    let sidenav = this.isMobile()
      ? this.getElement(this.MOBILE_SIDENAV_CONTENT)
      : this.getElement(this.SIDENAV);

    let div = this.getElement(sidenav.id + data.id);
    if(!div){
      div = this.createElement(TAG.DIV);
      div.id = sidenav.id + data.id;
      div.style.paddingBottom = "10px";
      div.style.display = "flex";
      div.style.flexDirection = "column";
      div.style.gap = ".5rem";
      sidenav.appendChild(div);

      if(data.button && !this.isMobile()) {
        let buttonDiv = this.createElement(TAG.DIV);
        buttonDiv.className = CSS.AON_SIDENAV_TITLE_BUTTON;
        let button = new AonIconButton();
        button.icon = data.button.icon;
        button.id = div.id + data.button.id;
        buttonDiv.appendChild(button);
        div.appendChild(buttonDiv);
        button.addEventListener(EVENT.CLICK, data.button.fn);
      }
      if (newButton && !this.isMobile()) {
        let addButton = this.createElement(TAG.DIV);
        addButton.className = CSS.AON_SIDENAV_TITLE_BUTTON;
        let aonIconButton = new AonIconButton();
        aonIconButton.icon = "add";
        aonIconButton.id = div.id + "NewButton";
        addButton.appendChild(aonIconButton);
        div.appendChild(addButton);
        this.getElement(div.id + "NewButton").addEventListener(EVENT.CLICK, newButton);
      }

      let sidenavTitle = this.createElement(TAG.DIV);
      sidenavTitle.className = CSS.AON_SIDENAV_TITLE;
      sidenavTitle.id = "aonSidenavTitle"+data.id;
      sidenavTitle.title = data.name;
      sidenavTitle.style.cursor = "pointer";
      sidenavTitle.style.userSelect = "none";
      sidenavTitle.style.marginLeft = LS.isNewTheme() ? "10px": "2px";

      let arrowTitleSpan = this.createElement(TAG.SPAN);
      arrowTitleSpan.className = CSS.AON_SIDENAV_TITLE_ARROW;
      arrowTitleSpan.style.borderColor = data.app && data.app.color ? data.app.color : 'black';

      let arrowTitle = this.createElement("i");
      arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_LESS;
      arrowTitle.className = "material-icons aonVerticalMiddle";
      sidenavTitle.appendChild(LS.isNewTheme() ? arrowTitleSpan : arrowTitle);

      sidenavTitle.addEventListener(EVENT.CLICK, ()=>{

        const elements = div.children;
        
        if(elements.length > 0) 
			arrowTitleSpan.classList.toggle("closeIcon");
			
        for (let i = 1; i < elements.length; i++) {
            elements[i].classList.toggle(CSS.ELEMENT_HIDDEN);

            if(elements[i].classList.contains(CSS.ELEMENT_HIDDEN)){
              arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_MORE;
              sidenavTitle.style.marginBottom = "0";
            } else {
              arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_LESS;
              sidenavTitle.style.marginBottom = "10px";
            }
        }
      });  

      let span = this.createElement(TAG.SPAN);
      span.innerHTML = data.name.toUpperCase();
      sidenavTitle.appendChild(span);

      div.appendChild(sidenavTitle);
    }
    
    return div;
  }

  addSidenavSelectOptions(data, options) {
    let sidenav = this.isMobile() ? this.getElement(this.MOBILE_SIDENAV_CONTENT) : this.getElement(this.SIDENAV);
    if(data && data.id && sidenav){
      let div = this.getElement(sidenav.id + data.id);
      
      if(div){

        let selectDiv = this.getElement(div.id + "SelectDiv");
        if(!selectDiv){
          selectDiv = this.createElement(TAG.DIV);
          selectDiv.id = div.id + "SelectDiv";
          selectDiv.classList.add("aonAppMenuSidenavListBeta");
          selectDiv.style.display = "flex";
          selectDiv.style.gap = ".5rem";
          selectDiv.style.alignItems = "center";
          selectDiv.style.border = "1px solid rgb(221, 221, 221)";
          selectDiv.style.borderRadius = "5px";
          selectDiv.style.padding = "5px";
          selectDiv.style.margin = "0 20px";
          div.appendChild(selectDiv);

          // Add icon to select
          if(options){
            let option = options[0];
            if(option.icon){
              let i = this.createElement(TAG.I);
              i.id = data.id + 'icon';
              let iconClass = "material-icons";
              if(LS.isNewTheme() && data.app) i.style.color = data.app.color;
              if(option.icon_color) {
                i.title = option.id;
                i.color = option.icon_color;
                i.style.color = option.icon_color;
              }
              if(option.icon_class) iconClass = option.icon_class;
              i.className = `${iconClass} aonVerticalMiddle`;
              i.innerHTML = option.icon;
    
              selectDiv.appendChild(i);
            }
          }
        }

        const idSelect = div.id + "Select";
        let select =  this.getElement(idSelect); 
        if(!select){
          select = this.createElement('select');
          select.id = idSelect;
          select.style.background = "none";
          select.style.border = "none";
          select.style.cursor = "pointer";
          select.style.width = "100%";
          select.classList.add(CSS.OUTLINE_HIDDEN);

          selectDiv.appendChild(select);
        } else {
          select.innerHTML = null;
        }
        options.forEach((option, i) => {
          this.addSidenavSelectOptionsValue(data, option, select);
        });
        select.addEventListener('change', () => {
          let yearSelected = JSON.parse(select.value);
          // options.forEach(option =>  console.log(option.name + " == " + yearSelected));
          let filteredYears = options.filter(option => option.name == yearSelected);
          let optionFiltered = filteredYears[0];
          optionFiltered.fn();
         
        });
        return select;
      }
    }
    return null;
  }

  addSidenavSelectOptionsValue(data, option, select) {
    let sidenavId = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT: this.SIDENAV;
    select = select || this.getElement(sidenavId + data.id + "Select");

    let optYear = this.createElement('option');
    optYear.value = JSON.stringify(option.value ? option.value : option.name);
    optYear.innerHTML = option.name;
    select.appendChild(optYear);
  }

  removeSidenavById(id){
    let sidenav = this.isMobile() ? this.getElement(this.MOBILE_SIDENAV_CONTENT) : this.getElement(this.SIDENAV);
    if(sidenav){
      let div = this.getElement(sidenav.id + id);
      if(div) div.remove();
    }
  }

  buildSidenavSubOptions(data, options) {
    let ul = this.createElement(TAG.UL);
    ul.classList.add(CSS.AON_UL);
    ul.classList.add(CSS.AON_CLIP);
    ul.style.paddingLeft = '12px';
    options.forEach((option, i) => {
      this.addSidenavOptionsListValue(data, option, ul);
    });
    return ul;
  }

  addSidenavOptionsListValue(data, option, ul) {
    let sidenavId = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT: this.SIDENAV;
    ul = ul || this.getElement(sidenavId + data.id + "List");
    if (!option.hidden && ul) {
      let id = sidenavId + (option.id || Math.random().toString(36).substring(7));
      let li = this.createElement(TAG.LI);
      li.id = id;
      li.title = option.title || option.name;

      li.className = "aonAppMenuSidenavList aonOpacity sidenavHover";
      ul.appendChild(li);
      if(option.options) {
		li.classList.add("aonSubMenuOptions");
        //li.style.paddingLeft = '6px';
        let arrow = this.createElement(TAG.I);
        arrow.className = "material-icons aonVerticalMiddle";
        arrow.innerHTML = option.opened
          ? MATERIAL_ICONS.ARROW_DROP_DOWN 
          : MATERIAL_ICONS.ARROW_RIGHT;
        
        arrow.classList.add(option.opened ? 'chevronDowm' : 'chevronRight');
        
        li.appendChild(arrow);
        let newLi =  this.createElement(TAG.LI);
        newLi.id = id + 'Options';
        newLi.appendChild(this.buildSidenavSubOptions(data, option.options));
        newLi.style.transition = "opacity 1s ease-out";
        this.hiddenElement(newLi, !option.opened);
        ul.appendChild(newLi);
        if(option.clickable) {
          arrow.addEventListener(EVENT.CLICK, (e => {
            e.preventDefault();
            arrow.innerHTML = arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT ? MATERIAL_ICONS.ARROW_DROP_DOWN : MATERIAL_ICONS.ARROW_RIGHT;
            
            arrow.classList.toggle('chevronRight');
            arrow.classList.toggle('chevronDowm');
           
            this.hiddenElement(newLi, arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT);
          }));
        } else {
          li.addEventListener(EVENT.CLICK,() => {
            arrow.innerHTML = arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT ? MATERIAL_ICONS.ARROW_DROP_DOWN : MATERIAL_ICONS.ARROW_RIGHT;
            
            arrow.classList.toggle('chevronRight');
            arrow.classList.toggle('chevronDowm');
            
            this.hiddenElement(newLi, arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT);
          });
        } 
      }

      let span = this.createElement(TAG.SPAN);
      span.id = "aonMenuItemSpan" + option.id;
      span.className = "aonMenuItemSpan";
      span.title =  option.title || option.name;
      if (option.count) {
        span.innerHTML = option.name + " (" + option.count + ")";
        span.style.fontWeight = "bold";
      } else span.innerHTML = option.name;

      if (option.icon) {
        let i = this.createElement(TAG.I);
        i.id = id + 'icon';
        let iconClass = "material-icons";
        if(LS.isNewTheme() && data.app) i.style.color = data.app.color;
        if(option.icon_color) {
          i.title = option.id;
          i.color = option.icon_color;
          i.style.color = option.icon_color;
        }
        if(option.icon_class) iconClass = option.icon_class;
        i.className = `${iconClass} aonVerticalMiddle`;
        i.innerHTML = option.icon;
        li.appendChild(i);
      } else if (option.aonIcon) {
        let ai = new AonIcon();
        ai.id    = id + "AonIcon";
        ai.icon  = option.aonIcon.icon;
        ai.size  = "18px";
        li.appendChild(ai);
        if(LS.isNewTheme() && data.app) ai.color = data.app.color;
        if(option.icon_color) {
          ai.title = option.id;
          ai.color = option.icon_color;
        }
        li.addEventListener(EVENT.MOUSEOVER, () => {
          if(!LS.isNewTheme()) 
            this.getElement(id + "AonIcon").color = option.aonIcon.color;
        });

        li.addEventListener(EVENT.MOUSELEAVE, () => {
          if(!LS.isNewTheme()) 
            this.getElement(id + "AonIcon").color = "#5f6368";
        });
      } else if (option.img) {
        let img = this.createElement(TAG.IMG);
        if(option.style) {
          img.className = option.style;
          span.style.paddingLeft = '20px'
        } else img.style.width = '18px';
        img.src = option.img;
        li.appendChild(img);
      } else if (option.html) {
        let divHtml = this.createElement(TAG.DIV);
        divHtml.innerHTML = option.html;
        li.appendChild(divHtml.firstChild);

        li.style.display = "flex";
        li.style.alignItems = "center";
      } else {
        // span.style.marginLeft = '28px';
      }
      li.appendChild(span);

      if (option.actions) {
        let actionDiv = this.createElement(TAG.SPAN);
        actionDiv.style.display = "none";
        li.appendChild(actionDiv);
        li.addEventListener(EVENT.MOUSEOVER, () =>  actionDiv.style.display = "contents");

        li.addEventListener(EVENT.MOUSELEAVE, () =>  actionDiv.style.display = "none");

        option.actions.forEach((item, i) => {
          let button = this.createElement(TAG.SPAN);
          let rightPX = 5 + (30 * i);
          button.style.right = rightPX + "px";
          button.style.position = "absolute";
          let aonIconButton = new AonIconButton();
          aonIconButton.noHover = true;
          aonIconButton.icon = item.icon;
          aonIconButton.id = li.id + item.id;
          button.appendChild(aonIconButton);
          actionDiv.appendChild(button);
          let b = aonIconButton.getButton();
          b.style.height = "30px";
          b.style.minWidth = "30px";
          b.style.width = "30px";
          let ic = aonIconButton.getIcon();
          ic.style.fontSize = "1.3rem";
          aonIconButton.addEventListener(EVENT.CLICK, (ev)=>{
            ev.stopPropagation();
            item.action(ev)
          });
        });
      }

      if(!option.options || option.clickable){
        li.addEventListener(EVENT.CLICK, () => {
          // ul
          this.querySelectorAll(`[id^='${sidenavId}'] li`).forEach((el) => {
            if (el.id !== sidenavId){
              el.classList.remove(CSS.AON_APP_MENU_SIDENAV_LIST_SELECTED);
              el.style.removeProperty("border-left");
              let icon = this.getElement(el.id + 'icon');
              if(icon) icon.classList.remove('material-icons-selected');
            }
          });

          li.classList.add(CSS.AON_APP_MENU_SIDENAV_LIST_SELECTED);
          li.style.borderLeft = '2px solid ' + (data.app ? data.app.color : 'black');
          let icon = this.getElement(li.id + 'icon');
          if(icon) icon.classList.add('material-icons-selected');

          this.selected = id;
          let toolbar = this.getElement(this.TOOLBAR);
          if(toolbar) toolbar.setAttribute("option", option.name);
          if(option.fn){
            let count = 0;
            if(li.querySelector("span")) count = li.querySelector("span").dataset.count;
            option.fn(count);
          } 
          this.dispatchEvent(new CustomEvent(EVENT.SELECT_OPTION, { detail: option }));
          if (this.isMobile()) {
            this.closeMobileSidenav();
          }
        });
      }
    }
  }

  addSidenavOptions(title, options, newButton) {
    let tmp = undefined;
    if (options && options.length > 0) {
      let data = {
        id: title,
        name: title,
      };
      tmp = this.addSidenavOptionsTitle(data, newButton);
      this.addSidenavOptionsList(data,options);
    }
    return tmp;
  }
  
  addSidenavOptionsFirst(title, options, newButton) {
    let tmp = undefined;
    if (options && options.length > 0) {
      let data = {
        id: title,
        name: title,
      };
      tmp = this.addSidenavOptionsTitle(data, newButton, true);
      this.addSidenavOptionsList(data,options);
    }
    return tmp;
  }
  
  addSidenavOptions3(data, newButton) {
    this.SIDENAV = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT : this.SIDENAV;
    this.addSidenavOptionsTitle(data, newButton);
    this.addSidenavOptionsList(data, data.options || []);
  }

  addSelectSidenav(data, newButton) {
    this.SIDENAV = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT : this.SIDENAV;
    this.addSidenavSelectOptionsTitle(data, newButton);
    this.addSidenavSelectOptions(data, data.options || []);
  }

  addSelectToPanel(data) {
    this.SIDENAV = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT : this.SIDENAV;
    let sidenav = this.isMobile() ? this.getElement(this.MOBILE_SIDENAV_CONTENT) : this.getElement(this.SIDENAV);
    if(data && data.id && sidenav){
      let div = this.getElement(sidenav.id + data.parent);
 
      if(div) {

        let options = data.options || [];

        let selectDiv = this.getElement(sidenav.id + data.id + "SelectDiv");

        if(!selectDiv){
          selectDiv = this.createElement(TAG.DIV);
          selectDiv.id = sidenav.id + data.id + "SelectDiv";
          selectDiv.classList.add("aonAppMenuSidenavListBeta");
          selectDiv.style.display = "flex";
          selectDiv.style.gap = ".5rem";
          selectDiv.style.alignItems = "center";
          selectDiv.style.border = "1px solid rgb(221, 221, 221)";
          selectDiv.style.borderRadius = "5px";
          selectDiv.style.padding = "5px";
          selectDiv.style.margin = "0 20px";
          div.appendChild(selectDiv);

          // Add icon to select
          if(options){
            let option = options[0];
            if(option.icon){
              let i = this.createElement(TAG.I);
              i.id = data.id + 'icon';
              let iconClass = "material-icons";
              if(LS.isNewTheme() && data.app) i.style.color = data.app.color;
              if(option.icon_color) {
                i.title = option.id;
                i.color = option.icon_color;
                i.style.color = option.icon_color;
              }
              if(option.icon_class) iconClass = option.icon_class;
              i.className = `${iconClass} aonVerticalMiddle`;
              i.innerHTML = option.icon;
    
              selectDiv.appendChild(i);
            }
          }
        }

        const idSelect = sidenav.id + data.id + "Select";
        let select =  this.getElement(idSelect); 
        if(!select){
          select = this.createElement('select');
          select.id = idSelect;
          select.style.background = "none";
          select.style.border = "none";
          select.style.cursor = "pointer";
          select.style.width = "100%";
          select.classList.add(CSS.OUTLINE_HIDDEN);

          selectDiv.appendChild(select);
        } else {
          select.innerHTML = null;
        }
        
        options.forEach((option, i) => {
          this.addSidenavSelectOptionsValue(data, option, select);
        });
        select.addEventListener('change', () => {
          let yearSelected = JSON.parse(select.value);
          // options.forEach(option =>  console.log(option.name + " == " + yearSelected));
          let filteredYears = options.filter(option => option.name == yearSelected || (option.value && option.value == yearSelected));
          let optionFiltered = filteredYears[0];
          optionFiltered.fn();
         
        });
      }
    }
  }

  buildOptionsMenu(el, options) {
    const boundingClientRect = el.getBoundingClientRect();
    const top = boundingClientRect.top;
    const left = boundingClientRect.left;

    const d = this.getOptionDialog();

    options = options.map(({ aonIcon, icon, name, fn }) => ({
        aonIcon,
        icon,
        name,
        fn: () => fn(el),
    }));

    d.setMenuOptions(options, top, left);
    d.open();
  }


  removeBackgroundSidenavAll(color){
    const sidenavId = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT: this.SIDENAV;
    this.querySelectorAll(`[id^='${sidenavId}'] li`).forEach((li) => {
      li.classList.remove(CSS.AON_APP_MENU_SIDENAV_LIST_SELECTED);
      li.style.removeProperty("border-left");
      let icon = this.getElement(li.id + 'icon');
      if(icon) icon.classList.remove('material-icons-selected');

    });
  }

  addBackgroundSidenav(id, color){
    const sidenavId = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT: this.SIDENAV;
    const li =  this.getElement(sidenavId + id);
    if(li){
      li.classList.add(CSS.AON_APP_MENU_SIDENAV_LIST_SELECTED);
      li.style.borderLeft = '2px solid ' + (color || 'transparent');
      let icon = this.getElement(li.id + 'icon');
      if(icon) icon.classList.add('material-icons-selected');
    }
  }

  removeBackgroundSidenav(id, color){
    const sidenavId = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT: this.SIDENAV;
    const li =  this.getElement(sidenavId + id);
    if(li){
      li.classList.remove(CSS.AON_APP_MENU_SIDENAV_LIST_SELECTED);
      li.style.removeProperty("border-left");
      let icon = this.getElement(li.id + 'icon');
      if(icon) icon.classList.remove('material-icons-selected');
    }
  }

  /**
   * 
   * @param {String} id  
   * @param {Number} count 
   */
  updateSidenavCount(id, count){
    let li = this.getElement(this.SIDENAV+id);
    if(li){
      let span = li.querySelector("span");
      if(span){
        let name = span.title;
        let text = name;
        span.dataset.count = count;
        if(count) {
          text = name + " (" + count + ")";
          span.style.fontWeight = 'bold';
        } 
        span.innerHTML = text;
      }
    }
  }

  /**
   * 
   * @param {String} id  
   * @param {Number} title 
   */
  updateSidenavTitle(id, title){
    let span = this.getElement(`aonSidenavTitle${id}Name`);
    if(span){
        span.innerHTML = title;
    }
  }

  addOption(name, icon, fn) {
    this.addToolbarOption(name, icon, fn);
  }

  addToolbarOption(name, icon, fn) {
    this.getElement(this.TOOLBAR).addButton(name, icon, fn);
  }

  addSearchOption(opened=false, filterDocumental=false) {
    let toolbar = this.getElement(this.TOOLBAR);
    return toolbar.addSearchButton(opened, filterDocumental);
  }

  cleanSearchValue() {
    let toolbar = this.getElement(this.TOOLBAR);
    return toolbar.cleanSearchValue();
  }

  getSearchButton() {
    let toolbar = this.getElement(this.TOOLBAR);
    return toolbar.getSearchButton();
  }
  
  addToolbarTitle(title) {
    let toolbar = this.getElement(this.TOOLBAR);
    if (toolbar) {
      toolbar.setAttribute("option", title);
      //----------ADD COLOR SIDENAV SELECTED---------
      let li = this.getElement(this.SIDENAV+title);
      if(li) li.style.backgroundColor = "#d3e3fd";  
    } 
  }

  addTitleToolSection(title) {
    let toolbar = this.getElement(this.TOOLBAR);
    if (toolbar) toolbar.addTitleToolSection(title);
  }

  addToolbarOption2(option, fn) {
    return this.getElement(this.TOOLBAR).addButton2(option, fn);
  }

  removeToolbarOption(option) {
    let toolbar = this.getElement(this.TOOLBAR);
    if(toolbar) toolbar.removeButton(option.id);
  }

  removeToolbarOptions() {
    let toolbar = this.getElement(this.TOOLBAR);
    if(toolbar) toolbar.removeButtons();
  }

  removeToolbar() {
    let toolbar = this.getElement(this.TOOLBAR);
    if (toolbar) {
      toolbar.remove();
    }
  }

  setContent(element) {
    this.clearElementById(this.CONTENT);
    let content = this.getElement(this.CONTENT);
    if(content) content.appendChild(element);
  }

  addFloatOption(action, fn) {
    const buttonId = this.id + action.id + "Button";
    let aonIconButton = this.getElement(buttonId);
    if(!aonIconButton){
      let span = this.getElement(this.id + "FloatSpan") || this.createElement(TAG.SPAN);
      span.id = this.id + "FloatSpan";
      span.style.position = "fixed";
      span.style.right = "20px";
      span.style.bottom = this.isSab() ? "80px" : "70px";
      
      aonIconButton = new AonIconButton();
      aonIconButton.icon = action.icon;
      aonIconButton.id = buttonId;
      aonIconButton.title = action.name;
      aonIconButton.background = "#f1f1f1";
      aonIconButton.style.marginLeft = '10px';
      span.appendChild(aonIconButton);
      this.appendChild(span);
      aonIconButton.addEventListener(EVENT.CLICK, fn);

      const btn = aonIconButton.getButton();
      if(btn){
        btn.style.boxShadow = "0px 1px 8px rgb(0 0 0 / 43%)";
      }
    }

    return aonIconButton;
  }
  
  removeFloatOption() {
    let el = this.getElement(this.id + "FloatSpan");
    if (el) el.remove();
  }

  setContentHTML(html) {
    this.getElement(this.CONTENT).innerHTML = html;
  }

  getId() {
    return this.getAttribute(CONSTANT.ID);
  }

  getTitle() {
    return this.getAttribute(CONSTANT.TITLE);
  }

  getParent() {
    return this.parentNode;
  }

  getDialog() {
    return this.getElement(this.DIALOG);
  }

  getOptionDialog() {
    return this.getElement(this.OPTION_DIALOG);
  }

  getChild() {
    let el = this.getElement(this.CONTENT);
    if (el) el = el.firstChild;
    return el;
  }

  getSidenav() {
    return this.getElement(this.SIDENAV);
  }
  removeSidenav() {
    let sidenav = this.getSidenav();
    if (sidenav) {
      sidenav.remove();
    }
  }

  getRightSidenav() {
    return this.getElement(this.SIDENAV_RIGHT);
  }

  getMobileSidenav() {
    return this.getElement(this.MOBILE_SIDENAV);
  }

  getContent() {
    return this.getElement(this.CONTENT);
  }

  getToast(){
    return this.getElement(this.TOAST);
  }

  getToolbar(){
    return this.getElement(this.TOOLBAR);
  }

  isSidenavBlock() {
    return (
      this.hasAttribute("sidenav") && "block" === this.getAttribute("sidenav")
    );
  }

  getSidenavWidth() {
	  return (
	    this.hasAttribute("sidenav_width") && this.getAttribute("sidenav_width") || "250px"
	  );
  }
  
  development(title=MSG.INFORMATION, subtitle=MSG.IN_DEVELOPMENT) {
		this.confirmDialog(title, subtitle, () => {});
	}

  confirmDialog(title, subtitle, fn, buttonTitle = undefined){
    let d = this.getDialog();
    if(d){
      d.clear();
      d.type = "";
      d.setContentHTML(subtitle);
      if (!this.isMobile()) d.width = '650px';
      d.setTitle(title);
      d.open();
      if(buttonTitle) {
        d.addSendAction(() => {
          d.close();
          return fn();
        }, buttonTitle);
      }
      else d.addAcceptAction(() => fn());
    }
  }

  hiddenElement(element, condition = false){
    if(element){
      if(condition){
        element.classList.add(CSS.ELEMENT_HIDDEN);
      } else {
        element.classList.remove(CSS.ELEMENT_HIDDEN);
      }
    }
  }

  dragoverFn = (event) => {
    event.preventDefault();
    this.content.style.border = "2px solid #002469";
    this.content.style.opacity = "0.6";
  };

  dragenterFn = (event) => {
    event.preventDefault();
    this.content.style.border = "2px solid #002469";
    this.content.style.opacity = "0.6";
  };

  mouseleaveFn = (event) => {
    this.content.style.border = "0px";
    this.content.style.opacity = "1";
  };

  mouseoverFn = (event) => {
    this.content.style.border = "0px";
    this.content.style.opacity = "1";
  };

  dragleaveFn = (event) => {
    event.preventDefault();
    let isClickInside = this.content.contains(event.target) || this.content === event.target;
    if (!isClickInside) {
      this.content.style.border = "0px";
      this.content.style.opacity = "1";
    }
  }

  dropFn = (event) => {
    event.preventDefault();
    this.content.style.border = "0px";
    this.content.style.opacity = "1";
    if(event && event.dataTransfer && event.dataTransfer.files){
      this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_DROP, { detail: event.dataTransfer.files }));
    }
  };

  

  buildDragAndDrop(bool) {
    if (bool) {
      this.content.addEventListener(EVENT.DRAGOVER, this.dragoverFn);
      this.content.addEventListener(EVENT.DRAGENTER, this.dragenterFn);
      this.content.addEventListener(EVENT.MOUSELEAVE, this.mouseleaveFn);
      this.content.addEventListener(EVENT.MOUSEOVER, this.mouseoverFn);
      document.addEventListener(EVENT.DRAGLEAVE, this.dragleaveFn);
      this.content.addEventListener(EVENT.DROP, this.dropFn);
    } else {
      this.content.removeEventListener(EVENT.DRAGOVER, this.dragoverFn);
      this.content.removeEventListener(EVENT.DRAGENTER, this.dragenterFn);
      this.content.removeEventListener(EVENT.MOUSELEAVE, this.mouseleaveFn);
      this.content.removeEventListener(EVENT.MOUSEOVER, this.mouseoverFn);
      document.removeEventListener(EVENT.DRAGLEAVE, this.dragleaveFn);
      this.content.removeEventListener(EVENT.DROP, this.dropFn);      
    }
  }

  setDragAndDrop(bool) {
    if(bool) {
      this.drag_and_drop = true;
      this.buildDragAndDrop(true);
    } else {
      this.drag_and_drop = undefined;
      this.buildDragAndDrop(false);
    }
  }

  addCompanyNotes(registry, source) {
    window.addEventListener("message", (event) => {
          if ( (event.origin === "null" || event.origin === window.origin) 
            && event.data?.type === "REGISTRY_NOTE_SAVED" ) {
            
            // This payload if needed info from GWT
            // const customerRegistry = event.data.payload;
            
            getRegistryNotes({registry, source}).then(notes => {
                let countNotes = notes.filter(item => item.date).length;
                let countObservations = notes.filter(item => !item.date && item.comments && item.comments.trim() !== "").length;
                
                countNotes += countObservations;
                
                let notesTitle = `${countNotes > 0 ? (countNotes == 1 ? countNotes + ' Anotación' : countNotes + ' Anotaciones') : 'Sin Anotaciones'}`;
                
                let titleToolbar = this.getElement(this.getToolbar().TOOL_SECTION_TITLE);

                titleToolbar.innerHTML = notesTitle;
                
                let existsNotes = notes.some(item => item.date);
                let notesIconId = this.getToolbar().TOOL_SECTION + ACTION.NOTES.id + "ButtonIcon";
                let notesIcon = this.getElement(notesIconId);
                if(existsNotes)
                  notesIcon.style.color = 'green';
                else
                  notesIcon.style.color = 'rgb(95, 99, 104)';
              });
          }
        });
        
        getRegistryNotes({registry, source}).then(notes => {
              let countNotes = notes.filter(item => item.date).length;
              let countObservations = notes.filter(item => !item.date && item.comments && item.comments.trim() !== "").length;
              
              countNotes += countObservations;
              
              let notesTitle = `${countNotes > 0 ? (countNotes == 1 ? countNotes + ' Anotación' : countNotes + ' Anotaciones') : 'Sin Anotaciones'}`;
              
              this.getApplication().addTitleToolSection(notesTitle, false);
              
              this.getApplication().addToolbarOption2(ACTION.NOTES, () => this.buildObservations(source));
              
              let existsNotes = notes.some(item => item.date);
              
              if(existsNotes) {
                let notesIconId = this.getToolbar().TOOL_SECTION + ACTION.NOTES.id + "ButtonIcon";
                let notesIcon = this.getElement(notesIconId);
                notesIcon.style.color = 'green';
              }
              
              let existsObservation = notes.some(item => !item.date && item.comments && item.comments.trim() !== "");
              if(existsObservation) this.buildObservations(source);	
        });
  }

  async buildObservations(source) {
      let rightSidenav = this.getApplication().getRightSidenav();
      this.clearElement(rightSidenav);
  
      let notesIconId = this.getToolbar().TOOL_SECTION + ACTION.NOTES.id + "ButtonIcon";
      let notesIcon = this.getElement(notesIconId);
  
      let div = this.createElement(TAG.DIV);
      div.style = `
          display: flex;
          flex-direction: column;
          gap: 10px;
          height: 100%;
        `;
      div.id = "customerNotesId";
  
      if (rightSidenav.style.flexBasis === "0px" || rightSidenav.style.flexBasis.length == 0) {
        rightSidenav.appendChild(div);
  
        // Loader
        let loaderSpan = this.createElement(TAG.SPAN);
        loaderSpan.className = CONSTANT.SPIN;
        loaderSpan.style.display = 'flex';
        loaderSpan.style.height = '100%';
        loaderSpan.style.justifyContent = 'center';
        loaderSpan.style.alignItems = 'center';
  
        let aib = new AonIconButton();
        aib.id = 'spinLoader';
        aib.icon = 'sync';
        aib.title = 'Cargando...';
        loaderSpan.appendChild(aib);
  
        div.appendChild(loaderSpan);
  
        let company = LS.getCompany();
        if(!company){
			company = await getCompany();
			localStorage.setItem("customer", company.id);
		} else
			localStorage.setItem("customer", company.registry);
  
        
        localStorage.setItem("notesSource", source);
  
        GWT.iLoad(GWT.CUSTOMER_NOTES, div.id);
      }
      
      notesIcon.classList.toggle("material-icons-selected");
  
      this.getApplication().toogleRightSidenav();
    }
  
}
if(!window.customElements.get('aon-application')){
  window.customElements.define("aon-application", AonApplication);
}
