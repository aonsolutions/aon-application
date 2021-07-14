import { AonElement } from "./AonElement.js";
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "../environments/environments.js";3
import { AonIconButton } from "./aon-icon-button.js";
import "./aon-toolbar.js";
import "./aon-loader.js";
import "./aon-icon.js";
import "./aon-dialog.js";
import "./aon-dialog-menu.js";
import "./aon-toast.js";
// import { DIV } from "../environments/aonTag.js";

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
    this.getObserverContent();
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

  getObserverContent() {
    let observer = new MutationObserver((mutations) => {
      mutations.forEach(mutation=>{
        if(mutation.addedNodes.length > 0){
          let target = null;
          try {target = mutation.addedNodes[0]; } catch (error) {}
          this.VIEWS.push(target);
        }
      })
    });
    observer.observe(this.getElement(this.CONTENT), { childList: true });
  }

  back() {
    let count = this.VIEWS.length;
    if(count > 0){
      const last = count > 1 ? count - 2 : 0;
      let eleLastView = this.VIEWS[last];
      this.setContent(eleLastView);
      this.VIEWS = this.VIEWS.slice(0, last);
    }
  }

  build() {
    this.innerHTML = `
			<!-- AON APPLICATION TOOLBAR -->
			<aon-toolbar id="${this.TOOLBAR}" title="${this.getTitle()}"></aon-toolbar>

			<!-- AON APPLICATION LOADER -->
			<aon-loader id="${this.LOADER}"> </aon-loader>

      <div class="${this.isMobile() ? 'aonMobileApplicationContent' :'aonFlex'}">
        <!-- AON APPLICATION MENU (SIDENAV) -->
         <div id="${this.SIDENAV}" class="${this.isMobile() ? 'aonMobileSidenav' :'aonSidenav'}"></div>

			   <!-- AON APPLICATION CONTENT -->
			   <div id="${this.CONTENT}"></div>
      </div>
			<aon-dialog-menu id="${this.OPTION_DIALOG}"> </aon-dialog-menu>
			<aon-dialog id="${this.DIALOG}"> </aon-dialog>
			<aon-toast id="${this.TOAST}"> </aon-toast>
		`;

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
        : "aonContent";
    if(this.isMobile() && this.isSab()){
      content.style.bottom = '69px';
    }

    if (this.hasAttribute("drag_and_drop")) {
      content.addEventListener(EVENT.DRAGOVER, (event) => {
        event.preventDefault();
        console.log(EVENT.DRAGOVER);
        content.style.border = "2px solid #002469";
        content.style.opacity = "0.6";
      });
  
      content.addEventListener(EVENT.DRAGENTER, (event) => {
        event.preventDefault();
        content.style.border = "2px solid #002469";
        content.style.opacity = "0.6";
      });
  
      content.addEventListener(EVENT.MOUSELEAVE, (event) => {
        content.style.border = "0px";
        content.style.opacity = "1";
      });
  
      content.addEventListener(EVENT.MOUSEOVER, (event) => {
        content.style.border = "0px";
        content.style.opacity = "1";
      });
  
      document.addEventListener(EVENT.DRAGLEAVE, (event) => {
        event.preventDefault();
        let isClickInside = content.contains(event.target) || content === event.target;
        if (!isClickInside) {
          content.style.border = "0px";
          content.style.opacity = "1";
        }
      });
  
      content.addEventListener(EVENT.DROP, (event) => {
        event.preventDefault();
        console.log(EVENT.DROP);
        content.style.border = "0px";
        content.style.opacity = "1";
        this.dispatchEvent(new CustomEvent(EVENT.DROP));
      });
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
    div.style.borderBottom = '1px solid #ebebeb';

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
    div.style.borderBottom = "1px solid #ebebeb";
    sidenav.appendChild(div);

    let sidenavTitle = this.createElement(TAG.DIV);
    sidenavTitle.className = "aonSidenavTitle";
    sidenavTitle.innerHTML = title;
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
      div.style.borderBottom = "1px solid #ebebeb";
      sidenav.appendChild(div);

      let sidenavTitle = this.createElement(TAG.DIV);
      sidenavTitle.className = "aonSidenavTitle";
      sidenavTitle.innerHTML = title;
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
    div.style.paddingBottom = "25px";
    div.style.borderBottom = "1px solid #ebebeb";
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
    sidenavTitle.innerHTML = data.name;
    sidenavTitle.title = data.name;
    div.appendChild(sidenavTitle);

    return div;
  }

  addSidenavOptionsList(data, options) {
    let sidenav = this.isMobile()
      ? this.getElement(this.MOBILE_SIDENAV_CONTENT)
      : this.getElement(this.SIDENAV);
    let div = this.getElement(sidenav.id + data.id);
    let ul = this.createElement(TAG.UL);
    ul.id = div.id + "List";
    ul.className = "aonClip";
    div.appendChild(ul);
    options.forEach((option, i) => {
      this.addSidenavOptionsListValue(data, option, ul);
    });
    return ul;
  }

  buildSidenavSubOptions(data, options) {
    let ul = this.createElement(TAG.UL);
    ul.className = "aonClip";
    ul.style.marginLeft = '12px';
    options.forEach((option, i) => {
      this.addSidenavOptionsListValue(data, option, ul);
    });
    return ul;
  }

  addSidenavOptionsListValue(data, option, ul) {
    ul = ul || this.getElement(this.SIDENAV + data.id + "List");
    if (!option.hidden) {
      let id = this.SIDENAV + option.name;
      let li = this.createElement(TAG.LI);
      li.id = id;
      li.title = option.name;
      li.className = "aonAppMenuSidenavList aonOpacity";
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
        newLi.style.display = 'none';
        ul.appendChild(newLi);
        arrow.addEventListener(EVENT.CLICK, (e => {
          e.preventDefault();
          arrow.innerHTML = arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT
            ? MATERIAL_ICONS.ARROW_DROP_DOWN
            : MATERIAL_ICONS.ARROW_RIGHT;
          newLi.style.display = arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT
            ? 'none' : 'block';
        }));
      }

      let span = this.createElement(TAG.SPAN);
      span.className = "aonMenuItemSpan";
      if (option.count && option.count > 0) {
        span.innerHTML = option.name + " (" + option.count + ")";
        span.style.fontWeight = "bold";
      } else span.innerHTML = option.name;

      if (option.icon) {
        let i = this.createElement(TAG.I);
        i.className = "material-icons aonVerticalMiddle";
        i.innerHTML = option.icon;
        li.appendChild(i);
      } else if (option.aonIcon) {
        li.innerHTML = `<aon-icon id="${id + "AonIcon"}" icon="${
          option.aonIcon.icon
        }" size="18px"></aon-icon>`;

        li.addEventListener(EVENT.MOUSEOVER, () => {
          this.getElement(id + "AonIcon").color = option.aonIcon.color;
        });

        li.addEventListener(EVENT.MOUSELEAVE, () => {
          this.getElement(id + "AonIcon").color = "#5f6368";
        });
      } else if (option.img) {
        let img = this.createElement(TAG.IMG);
        img.style.width = "18px";
        img.src = option.img;
        li.appendChild(img);
      } else {
        span.style.marginLeft = "28px";
      }
      li.appendChild(span);

      li.addEventListener(EVENT.MOUSEOVER, () => {
        if (!this.selected || this.selected !== id)
          li.style.backgroundColor = "#f1f1f1";
      });

      li.addEventListener(EVENT.MOUSELEAVE, () => {
        if (!this.selected || this.selected !== id)
          li.style.backgroundColor = "white";
      });

      if (option.actions) {
        let actionDiv = this.createElement(TAG.SPAN);
        actionDiv.style.display = "none";
        li.appendChild(actionDiv);
        li.addEventListener(EVENT.MOUSEOVER, () => {
          actionDiv.style.display = "contents";
        });

        li.addEventListener(EVENT.MOUSELEAVE, () => {
          actionDiv.style.display = "none";
        });

        option.actions.forEach((item, i) => {
          let button = this.createElement(TAG.SPAN);
          button.style.right = i * 30 + "px";
          button.style.position = "absolute";
          let aonIconButton = new AonIconButton();
          aonIconButton.icon = item.icon;
          aonIconButton.id = li.id + item.id;
          button.appendChild(aonIconButton);
          actionDiv.appendChild(button);
          let aib = this.getElement(li.id + item.id);
          let b = this.getElement(aib.BUTTON);
          b.style.height = "30px";
          b.style.minWidth = "30px";
          b.style.width = "30px";
          let ic = this.getElement(aib.ICON);
          ic.style.fontSize = "1.3rem";
          aib.addEventListener(EVENT.CLICK, item.action);
        });
      }

      li.addEventListener(EVENT.CLICK, () => {
        document
          .querySelectorAll(`[id^='${this.SIDENAV}']`)
          .forEach((el, i) => {
            if (el.id !== this.SIDENAV)
              el.style.backgroundColor = "transparent";
          });
        this.selected = id;
        li.style.backgroundColor = "#ddd";
        let toolbar = this.getElement(this.TOOLBAR);
        toolbar.setAttribute("option", option.name);
        if(option.fn) option.fn();
        this.dispatchEvent(new CustomEvent(EVENT.SELECT_OPTION, { detail: option }));
        if (this.isMobile()) {
          this.closeMobileSidenav();
        }
      });
    }
  }

  addSidenavOptions(title, options, newButton) {
    if (options && options.length > 0) {
      this.addSidenavOptionsTitle(
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
  }


  addSidenavOptions2(data, options, newButton) {
    this.SIDENAV = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT : this.SIDENAV;
    this.addSidenavOptionsTitle(data, newButton);
    this.addSidenavOptionsList(data, options);
  }

  addOption(name, icon, fn) {
    this.addToolbarOption(name, icon, fn);
  }

  addToolbarOption(name, icon, fn) {
    this.getElement(this.TOOLBAR).addButton(name, icon, fn);
  }

  addSearchOption() {
    let toolbar = this.getElement(this.TOOLBAR);
    const btnSearch = toolbar.addSearchButton();
    return btnSearch;
  }

  addToolbarTitle(title) {
    let toolbar = this.getElement(this.TOOLBAR);
    if (toolbar) toolbar.setAttribute("option", title);
  }

  addTitleToolSection(title) {
    let toolbar = this.getElement(this.TOOLBAR);
    if (toolbar) toolbar.addTitleToolSection(title);
  }

  addToolbarOption2(option, fn) {
    this.getElement(this.TOOLBAR).addButton2(option, fn);
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
    let span =
      this.getElement(this.id + "FloatSpan") || this.createElement(TAG.SPAN);
    span.id = this.id + "FloatSpan";
    span.style.position = "fixed";
    span.style.right = "20px";
    span.style.bottom = this.isSab() ? "80px" : "70px";
    let aonIconButton = new AonIconButton();
    aonIconButton.icon = action.icon;
    aonIconButton.id = this.id + action.id + "Button";
    aonIconButton.title = action.name;
    aonIconButton.background = "#f1f1f1";
    span.appendChild(aonIconButton);
    this.appendChild(span);
    this.getElement(this.id + action.id + "Button").addEventListener(EVENT.CLICK, fn);
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

  development(title='Información', subtitle='Esta opción está en desarrollo...') {
		this.confirmDialog(title, subtitle, () => {});
	}

  confirmDialog(title, subtitle, fn, buttonTitle = undefined){
      let d = this.getDialog();
      if(d){
        d.clear();
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

}
if(!window.customElements.get('aon-application')){
  window.customElements.define("aon-application", AonApplication);
}
