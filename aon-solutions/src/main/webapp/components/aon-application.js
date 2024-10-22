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

export class AonApplication extends AonElement {
  
  SIDENAV;
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
    // this.getObserverContent();
  }

  initialize() {
    this.SIDENAV = this.id + "Sidenav";
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

  // getObserverContent() {
  //   let observer = new MutationObserver((mutations) => {
  //     mutations.forEach(mutation=>{
  //       if(mutation.addedNodes.length > 0){
  //         let target = null;
  //         try {target = mutation.addedNodes[0]; } catch (error) {}
  //         this.VIEWS.push(target);
  //       }
  //     })
  //   });
  //   observer.observe(this.getElement(this.CONTENT), { childList: true });
  // }

  // back() {
  //   let count = this.VIEWS.length;
  //   if(count > 0){
  //     const last = count > 1 ? count - 2 : 0;
  //     let eleLastView = this.VIEWS[last];
  //     this.setContent(eleLastView);
  //     this.VIEWS = this.VIEWS.slice(0, last);
  //   }
  // }

  build() {
    this.innerHTML = `
			<!-- AON APPLICATION TOOLBAR -->
			<aon-toolbar id="${this.TOOLBAR}" title="${this.getTitle()}"></aon-toolbar>

			<!-- AON APPLICATION LOADER -->
			<aon-loader id="${this.LOADER}"> </aon-loader>

      <div class="${this.isMobile() ? 'aonMobileApplicationContent' :'aonFlex'}">
        <!-- AON APPLICATION MENU (SIDENAV) -->
         <div id="${this.SIDENAV}" class="${this.getSidenavClassName()}"></div>

			   <!-- AON APPLICATION CONTENT -->
			   <div id="${this.CONTENT}"></div>
      </div>
			<aon-dialog-menu id="${this.OPTION_DIALOG}"> </aon-dialog-menu>
			<aon-dialog id="${this.DIALOG}"> </aon-dialog>
			<aon-toast id="${this.TOAST}"> </aon-toast>
		`;

    this.content = this.getContent();
    if(this.isMobile()) {
      this.buildMobileSidenav();
    }

    let toolbar = this.getElement(this.TOOLBAR);
    toolbar.toogleSidenav(() => this.isMobile() 
      ? this.toogleMobileSidenav() : this.toogleSidenav());

    let sidenav = this.getElement(this.SIDENAV);
    sidenav.style.flexBasis = this.isMobile() || this.isSidenavBlock() ? "0px" : "250px";
    if(this.isMobile()  && this.isSab()) {
      sidenav.style.height = 'calc(100vh - 172px)';
    }

    let content = this.getElement(this.CONTENT);
    content.className =
      this.isMobile() || this.isSidenavBlock()
        ? "aonMobileContent"
        : CSS.AON_CONTENT_BETA;
    if(this.isMobile() && this.isSab()){
      content.style.bottom = '69px';
    }

    if (this.hasAttribute("drag_and_drop")) {
      this.buildDragAndDrop(true);
    }

    if (this.hasAttribute("main")) {
      toolbar.style.display = "none";
      sidenav.style.height = "calc(100vh - 61px)";
      let topnav = this.getElement("aonMenuTopnav");
      let height = topnav.style.height;
      if(height == "68px")
        content.style.height = "calc(100vh - 115px)";
      else if (height == "0px")
        content.style.height = "calc(100vh - 48px)";

    }

    if (!localStorage.getItem("aon_solutions")) {
      let top = sidenav.getBoundingClientRect().top;
      if (top === 82 || top === 110) {
        top = top + 20;
      } else if (top === 124) {
        top = top + 14;
      } else top = top + 1;
      localStorage.setItem("aon_application_top", top);
      sidenav.style.height = `calc(100vh - ${top}px)`;
      content.style.height = `calc(100vh - ${top}px)`;
    }
  }

  getSidenavClassName() {
    if(this.isMobile()) return 'aonMobileSidenav';
    else if(LS.isNewTheme()) return 'aonSidenav';
    else return 'aonSidenavBeta';
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
      if (sidenav.style.flexBasis === "250px") {
        sidenav.style.flexBasis = "0px";
      } else {
        sidenav.style.flexBasis = "250px";
      }
    }
  }

  closeSidenav() {
    let sidenav = this.getElement(this.SIDENAV);
    sidenav.style.flexBasis = "0px";
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
    sidenavTitle.className = LS.isNewTheme() ? "aonSidenavTitleBeta" : "aonSidenavTitle";
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
      sidenavTitle.className = LS.isNewTheme() ? "aonSidenavTitleBeta" : "aonSidenavTitle";
      sidenavTitle.innerHTML = title.toUpperCase();
      sidenavTitle.title = title;
      div.appendChild(sidenavTitle);

      let content = this.createElement(TAG.DIV);
      content.style.paddingLeft = "26px";
      content.innerHTML = html;
      div.appendChild(content);
    }
  }

  addSidenavOptionsTitle(data, newButton) {
    let sidenav = this.isMobile()
      ? this.getElement(this.MOBILE_SIDENAV_CONTENT)
      : this.getElement(this.SIDENAV);
    let div = this.createElement(TAG.DIV);
    div.id = sidenav.id + data.id;
    div.style.paddingBottom = "10px";
    sidenav.appendChild(div);

    if(data.button && !this.isMobile()) {
      let buttonDiv = this.createElement(TAG.DIV);
      buttonDiv.style.marginTop = LS.isNewTheme() ? "-12px" : "-15px";
      buttonDiv.style.right = "0px";
      buttonDiv.style.position = "absolute";
      let button = new AonIconButton();
      button.icon = data.button.icon;
      button.id = div.id + data.button.id;
      buttonDiv.appendChild(button);
      div.appendChild(buttonDiv);
      button.addEventListener(EVENT.CLICK, data.button.fn);
    }
    if (newButton && !this.isMobile()) {
      let addButton = this.createElement(TAG.DIV);
      addButton.style.marginTop = LS.isNewTheme() ? "-12px" : "-15px";
      addButton.style.right = "0px";
      addButton.style.position = "absolute";
      let aonIconButton = new AonIconButton();
      aonIconButton.icon = "add";
      aonIconButton.id = div.id + "NewButton";
      addButton.appendChild(aonIconButton);
      div.appendChild(addButton);
      this.getElement(div.id + "NewButton").addEventListener(EVENT.CLICK, newButton);
    }

    let sidenavTitle = this.createElement(TAG.DIV);
    sidenavTitle.className = LS.isNewTheme() ? "aonSidenavTitleBeta" : "aonSidenavTitle";
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
        ul.classList.toggle(CSS.ELEMENT_HIDDEN);
        if(ul.classList.contains(CSS.ELEMENT_HIDDEN)){
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
    return div;
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
        buttonDiv.style.marginTop = LS.isNewTheme() ? "-12px" : "-15px";
        buttonDiv.style.right = "0px";
        buttonDiv.style.position = "absolute";
        let button = new AonIconButton();
        button.icon = data.button.icon;
        button.id = div.id + data.button.id;
        buttonDiv.appendChild(button);
        div.appendChild(buttonDiv);
        button.addEventListener(EVENT.CLICK, data.button.fn);
      }
      if (newButton && !this.isMobile()) {
        let addButton = this.createElement(TAG.DIV);
        addButton.style.marginTop = LS.isNewTheme() ? "-12px" : "-15px";
        addButton.style.right = "0px";
        addButton.style.position = "absolute";
        let aonIconButton = new AonIconButton();
        aonIconButton.icon = "add";
        aonIconButton.id = div.id + "NewButton";
        addButton.appendChild(aonIconButton);
        div.appendChild(addButton);
        this.getElement(div.id + "NewButton").addEventListener(EVENT.CLICK, newButton);
      }

      let sidenavTitle = this.createElement(TAG.DIV);
      sidenavTitle.className = LS.isNewTheme() ? "aonSidenavTitleBeta" : "aonSidenavTitle";
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

        // const selectDiv = this.getElement(sidenav.id + data.id + "SelectDiv");
        // if(selectDiv){
        //   selectDiv.classList.toggle(CSS.ELEMENT_HIDDEN);
        //   if(selectDiv.classList.contains(CSS.ELEMENT_HIDDEN)){
        //     arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_MORE;
        //     sidenavTitle.style.marginBottom = "0";
        //   } else {
        //     arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_LESS;
        //     sidenavTitle.style.marginBottom = "10px";
        //   }
        // }
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
        li.style.paddingLeft = '6px';
        let arrow = this.createElement(TAG.I);
        arrow.className = "material-icons aonVerticalMiddle";
        arrow.innerHTML = option.opened
          ? MATERIAL_ICONS.ARROW_DROP_DOWN 
          : MATERIAL_ICONS.ARROW_RIGHT;
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
            this.hiddenElement(newLi, arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT);
          }));
        } else {
          li.addEventListener(EVENT.CLICK,() => {
            arrow.innerHTML = arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT ? MATERIAL_ICONS.ARROW_DROP_DOWN : MATERIAL_ICONS.ARROW_RIGHT;
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
        span.style.marginLeft = '28px';
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
          button.style.right = i * 30 + "px";
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

  addOption(name, icon, fn) {
    this.addToolbarOption(name, icon, fn);
  }

  addToolbarOption(name, icon, fn) {
    this.getElement(this.TOOLBAR).addButton(name, icon, fn);
  }

  addSearchOption(opened=false) {
    let toolbar = this.getElement(this.TOOLBAR);
    return toolbar.addSearchButton(opened);
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

  development(title=MSG.INFORMATION, subtitle=MSG.IN_DEVELOPMENT) {
		this.confirmDialog(title, subtitle, () => {});
	}

  confirmDialog(title, subtitle, fn, buttonTitle = undefined){
    let d = this.getDialog();
    if(d){
      d.clear();
      d.type = "";
      d.setContentHTML(subtitle);
      if (!this.isMobile()) d.width = '400px';
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
    console.log(EVENT.DRAGOVER);
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
    console.log(EVENT.DROP + " aon application");
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
}
if(!window.customElements.get('aon-application')){
  window.customElements.define("aon-application", AonApplication);
}
