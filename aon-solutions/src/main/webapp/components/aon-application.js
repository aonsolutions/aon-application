import { AonElement } from "./AonElement.js";
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../environments/environments.js";3
import { AonIconButton } from "./aon-icon-button.js";
import { AonIcon } from "./aon-icon.js";
import "./aon-toolbar.js";
import "./aon-loader.js";
import "./aon-icon.js";
import "./aon-dialog.js";
import "./aon-dialog-menu.js";
import "./aon-toast.js";

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
         <div id="${this.SIDENAV}" class="${this.isMobile() ? 'aonMobileSidenav' : 'aonSidenavBeta'}"></div>

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
      content.style.height = "calc(100vh - 61px)";
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
    sidenavTitle.className = "aonSidenavTitle";
    sidenavTitle.innerHTML = title.toUpperCase();
    sidenavTitle.title = title;
    div.appendChild(sidenavTitle);

    let content = this.createElement(TAG.DIV);
    content.style.paddingLeft = "26px";
    content.appendChild(element);
    div.appendChild(content);
  }

  addSidenavWidgetHTML(title, html) {
    let sidenav = this.getElement(this.SIDENAV);
    if(sidenav){
      let div = this.createElement(TAG.DIV);
      div.style.paddingBottom = "25px";
      sidenav.appendChild(div);

      let sidenavTitle = this.createElement(TAG.DIV);
      sidenavTitle.className = "aonSidenavTitle";
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

    if (newButton && !this.isMobile()) {
      let addButton = this.createElement(TAG.DIV);
      addButton.style.marginTop = "-15px";
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
    sidenavTitle.className = "aonSidenavTitle";
    sidenavTitle.id = "aonSidenavTitle"+data.id;
    sidenavTitle.title = data.name;
    sidenavTitle.style.cursor = "pointer";
    sidenavTitle.style.userSelect = "none";
    sidenavTitle.style.marginLeft = "2px";
    let arrowTitle = this.createElement("i");
    arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_LESS;
    arrowTitle.className = "material-icons aonVerticalMiddle";
    sidenavTitle.appendChild(arrowTitle);

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
      li.title = option.name;
      li.className = "aonAppMenuSidenavList aonOpacity sidenavHover";
      ul.appendChild(li);
      if(option.options) {
        li.style.paddingLeft = '6px';
        let arrow = this.createElement(TAG.I);
        arrow.className = "material-icons aonVerticalMiddle";
        arrow.innerHTML = MATERIAL_ICONS.ARROW_RIGHT;
        li.appendChild(arrow);
        let newLi =  this.createElement(TAG.LI);
        newLi.id = id + 'Options';
        newLi.appendChild(this.buildSidenavSubOptions(data, option.options));
        newLi.style.transition = "opacity 1s ease-out";
        this.hiddenElement(newLi, true);
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
      span.className = "aonMenuItemSpan";
      span.title =  option.name;
      if (option.count) {
        span.innerHTML = option.name + " (" + option.count + ")";
        span.style.fontWeight = "bold";
      } else span.innerHTML = option.name;

      if (option.icon) {
        let i = this.createElement(TAG.I);
        let iconClass = "material-icons";
        if(option.icon_color) i.style.color = option.icon_color;
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
        li.addEventListener(EVENT.MOUSEOVER, () => {
          this.getElement(id + "AonIcon").color = option.aonIcon.color;
        });

        li.addEventListener(EVENT.MOUSELEAVE, () => {
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
      } else {
        span.style.marginLeft = '28px';
      }
      li.appendChild(span);

      // li.addEventListener(EVENT.MOUSEOVER, () => {
      //   if (!this.selected || this.selected !== id)
      //     li.style.backgroundColor = "#f1f1f1";
      // });

      // li.addEventListener(EVENT.MOUSELEAVE, () => {
      //   if (!this.selected || this.selected !== id)
      //     li.style.backgroundColor = "white";
      // });

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
          let backgroundEl = li.style.backgroundColor;
          // ul
          this.querySelectorAll(`[id^='${sidenavId}'] li`).forEach((el) => {
            if (el.id !== sidenavId)
              el.style.backgroundColor = "transparent";
            else {
              // console.log(el.style.backgroundColor);
            }
          });
        
          li.style.backgroundColor = (!backgroundEl || backgroundEl.indexOf("transparent")>=0) ? "#ddd" : "transparent";

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
      tmp = this.addSidenavOptionsTitle(
        {
          id: title,
          name: title,
        },
        newButton
      );
      this.addSidenavOptionsList(
        {
          id: title,
          name: title,
        },
        options
      );
    }
    return tmp;
  }

  addSidenavOptions2(data, options, newButton) {
    this.SIDENAV = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT : this.SIDENAV;
    this.addSidenavOptionsTitle(data, newButton);
    this.addSidenavOptionsList(data, options);
  }
  
  addSidenavOptions3(data, newButton) {
    this.SIDENAV = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT : this.SIDENAV;
    this.addSidenavOptionsTitle(data, newButton);
    this.addSidenavOptionsList(data, data.options || []);
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


  removeBackgroundSidenavAll(){
    const sidenavId = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT: this.SIDENAV;
    this.querySelectorAll(`[id^='${sidenavId}'] li`).forEach((li) => {
      li.style.backgroundColor = 'transparent';
    });
  }

  addBackgroundSidenav(id){
    const sidenavId = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT: this.SIDENAV;
    const li =  this.getElement(sidenavId + id);
    if(li){
      li.style.backgroundColor = "#ddd";
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
        let fontWeight = "normal";
        let text = name;
        span.dataset.count = count;
        if(count) {
          text = name + " (" + count + ")";
          fontWeight = "bold";
        } 
        span.innerHTML = text;
        span.style.fontWeight = fontWeight;
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
      if(li) li.style.backgroundColor = "#ddd";  
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
