import { AonElement } from "./AonElement.js";

import "./aon-toolbar.js";
import "./aon-loader.js";
import "./aon-icon.js";
import "./aon-icon-button.js";
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

  selected;
  VIEWS;
  static get observedAttributes() {
    return ["title"];
  }

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  get title() {
    return this.getAttribute("title");
  }

  set title(title) {
    this.setAttribute("title", title);
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
    if ("title" === name) {
      let toolbar = this.getElement(this.TOOLBAR);
      if (toolbar) toolbar.setAttribute("title", newValue);
    }
  }

  constructor() {
    super();
    this.SIDENAV = this.id + "Sidenav";
    this.TOOLBAR = this.id + "Toolbar";
    this.LOADER = this.id + "Loader";
    this.CONTENT = this.id + "Content";
    this.OPTION_DIALOG = this.id + "OptionDialog";
    this.DIALOG = this.id + "Dialog";
    this.TOAST = this.id + "Toast";
    this.VIEWS = [];
  }

  connectedCallback() {
    this.build();
    this.getObserverContent();
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
         <div id="${this.SIDENAV}" class="aonSidenav"></div>

			   <!-- AON APPLICATION CONTENT -->
			   <div id="${this.CONTENT}"></div>
      </div>
			<aon-dialog-menu id="${this.OPTION_DIALOG}"> </aon-dialog-menu>
			<aon-dialog id="${this.DIALOG}"> </aon-dialog>
			<aon-toast id="${this.TOAST}"> </aon-toast>
		`;

    let toolbar = this.getElement(this.TOOLBAR);
    toolbar.toogleSidenav(() => this.toogleSidenav());

    let sidenav = this.getElement(this.SIDENAV);
    sidenav.style.flexBasis = this.isMobile() || this.isSidenavBlock() ? "0px" : "250px";

    let content = this.getElement(this.CONTENT);
    content.className =
      this.isMobile() || this.isSidenavBlock()
        ? "aonMobileContent"
        : "aonContent";

    if (this.hasAttribute("drag_and_drop")) {
      content.addEventListener("dragover", (event) => {
        event.preventDefault();
        console.log("dragover");
      });

      content.addEventListener("dragenter", (event) => {
        event.preventDefault();
        content.style.border = "2px solid #002469";
        content.style.opacity = "0.6";
      });

      content.addEventListener("mouseleave", (event) => {
        content.style.border = "0px";
        content.style.opacity = "1";
      });

      content.addEventListener("mouseover", (event) => {
        content.style.border = "0px";
        content.style.opacity = "1";
      });

      document.addEventListener("dragleave", (event) => {
        event.preventDefault();
        let isClickInside = content.contains(event.target);
        if (!isClickInside) {
          content.style.border = "0px";
          content.style.opacity = "1";
        }
      });

      content.addEventListener("drop", (event) => {
        event.preventDefault();
        console.log("drop");
        content.style.border = "0px";
        content.style.opacity = "1";
        this.dispatchEvent(new CustomEvent("drop"));
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

  startLoader() {
    this.getElement(this.LOADER).start();
  }

  stopLoader() {
    this.getElement(this.LOADER).stop();
  }

  startLoading() {
    this.getElement(this.LOADER).startLoading();
  }

  stopLoading() {
    this.getElement(this.LOADER).stopLoading();
  }

  toogleSidenav() {
    if (this.isSidenavBlock()) {
      this.closeSidenav();
    } else {
      let sidenav = this.getElement(this.SIDENAV);
      let content = this.getElement(this.CONTENT);
      if (sidenav.style.flexBasis === "250px") {
        sidenav.style.flexBasis = "0px";
      } else {
        sidenav.style.flexBasis = "250px";
      }
    }
  }

  closeSidenav() {
    let sidenav = this.getElement(this.SIDENAV);
    let content = this.getElement(this.CONTENT);
    sidenav.style.flexBasis = "0px";
  }

  addSidenavWidget(title, element) {
    let sidenav = this.getElement(this.SIDENAV);

    let div = this.createElement("div");
    div.style.paddingBottom = "25px";
    div.style.borderBottom = "1px solid #ebebeb";
    sidenav.appendChild(div);

    let sidenavTitle = this.createElement("div");
    sidenavTitle.className = "aonSidenavTitle";
    sidenavTitle.innerHTML = title;
    div.appendChild(sidenavTitle);

    let content = this.createElement("div");
    content.style.paddingLeft = "26px";
    content.appendChild(element);
    div.appendChild(content);
  }

  addSidenavWidgetHTML(title, html) {
    let sidenav = this.getElement(this.SIDENAV);

    let div = this.createElement("div");
    div.style.paddingBottom = "25px";
    div.style.borderBottom = "1px solid #ebebeb";
    sidenav.appendChild(div);

    let sidenavTitle = this.createElement("div");
    sidenavTitle.className = "aonSidenavTitle";
    sidenavTitle.innerHTML = title;
    div.appendChild(sidenavTitle);

    let content = this.createElement("div");
    content.style.paddingLeft = "26px";
    content.innerHTML = html;
    div.appendChild(content);
  }

  addSidenavOptionsTitle(data, newButton) {
    let sidenav = this.getElement(this.SIDENAV);
    let div = this.createElement("div");
    div.id = this.SIDENAV + data.id;
    div.style.paddingBottom = "25px";
    div.style.borderBottom = "1px solid #ebebeb";
    sidenav.appendChild(div);

    if (newButton) {
      let addButton = this.createElement("div");
      addButton.style.marginTop = "-15px";
      addButton.style.right = "0px";
      addButton.style.position = "absolute";
      addButton.innerHTML = `
				<aon-icon-button id="${div.id + "NewButton"}" icon='add'> </aon-icon-button
			`;
      div.appendChild(addButton);
      this.getElement(div.id + "NewButton").addEventListener(
        "click",
        newButton
      );
    }

    let sidenavTitle = this.createElement("div");
    sidenavTitle.className = "aonSidenavTitle";
    sidenavTitle.innerHTML = data.name;
    div.appendChild(sidenavTitle);

    return div;
  }

  addSidenavOptionsList(data, options) {
    let div = this.getElement(this.SIDENAV + data.id);
    let ul = this.createElement("ul");
    ul.id = div.id + "List";
    ul.className = "aonClip";
    div.appendChild(ul);
    options.forEach((option, i) => {
      this.addSidenavOptionsListValue(data, option);
    });
    return ul;
  }

  addSidenavOptionsListValue(data, option) {
    let ul = this.getElement(this.SIDENAV + data.id + "List");
    if (!option.hidden) {
      let id = this.SIDENAV + option.name;
      let li = this.createElement("li");
      li.id = id;
      li.className = "aonAppMenuSidenavList aonOpacity";
      ul.appendChild(li);

      let span = this.createElement("span");
      span.className = "aonMenuItemSpan";
      if (option.count && option.count > 0) {
        span.innerHTML = option.name + " (" + option.count + ")";
        span.style.fontWeight = "bold";
      } else span.innerHTML = option.name;

      if (option.icon) {
        let i = this.createElement("i");
        i.className = "material-icons aonVerticalMiddle";
        i.innerHTML = option.icon;
        li.appendChild(i);
      } else if (option.aonIcon) {
        li.innerHTML = `<aon-icon id="${id + "AonIcon"}" icon="${
          option.aonIcon.icon
        }" size="18px"></aon-icon>`;

        li.addEventListener("mouseover", () => {
          this.getElement(id + "AonIcon").color = option.aonIcon.color;
        });

        li.addEventListener("mouseleave", () => {
          this.getElement(id + "AonIcon").color = "#5f6368";
        });
      } else if (option.img) {
        let img = this.createElement("img");
        img.style.width = "18px";
        img.src = option.img;
        li.appendChild(img);
      } else {
        span.style.marginLeft = "28px";
      }
      li.appendChild(span);

      li.addEventListener("mouseover", () => {
        if (!this.selected || this.selected !== id)
          li.style.backgroundColor = "#f1f1f1";
      });

      li.addEventListener("mouseleave", () => {
        if (!this.selected || this.selected !== id)
          li.style.backgroundColor = "white";
      });

      if (option.actions) {
        let actionDiv = this.createElement("span");
        actionDiv.style.display = "none";
        li.appendChild(actionDiv);
        li.addEventListener("mouseover", () => {
          actionDiv.style.display = "contents";
        });

        li.addEventListener("mouseleave", () => {
          actionDiv.style.display = "none";
        });

        option.actions.forEach((item, i) => {
          let button = this.createElement("span");
          button.style.right = i * 30 + "px";
          button.style.position = "absolute";
          button.innerHTML = `
						<aon-icon-button id="${li.id + item.id}" icon='${item.icon}'> </aon-icon-button
					`;
          actionDiv.appendChild(button);
          let aib = this.getElement(li.id + item.id);
          let b = this.getElement(aib.BUTTON);
          b.style.height = "30px";
          b.style.minWidth = "30px";
          b.style.width = "30px";
          let ic = this.getElement(aib.ICON);
          ic.style.fontSize = "1.3rem";
          aib.addEventListener("click", item.action);
        });
      }

      li.addEventListener("click", () => {
        document
          .querySelectorAll(`[id^='${this.SIDENAV}']`)
          .forEach((el, i) => {
            if (el.id !== this.SIDENAV)
              el.style.backgroundColor = "transparent";
          });
        this.selected = id;
        li.style.backgroundColor = "#ddd";
        if (!this.isMobile()) {
          let toolbar = this.getElement(this.TOOLBAR);
          toolbar.setAttribute("option", option.name);
        }
        option.fn();
        if (this.isMobile()) {
          this.closeSidenav();
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
    toolbar.addSearchButton();
    toolbar.addEventListener("search", (event) => {
      this.dispatchEvent(new CustomEvent("search", { detail: event.detail }));
    });
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
    this.getElement(this.TOOLBAR).removeButton(option.id);
  }

  removeToolbarOptions() {
    this.getElement(this.TOOLBAR).removeButtons();
  }

  setContent(element) {
    this.clearElement(this.CONTENT);
    this.getElement(this.CONTENT).appendChild(element);
  }

  addFloatOption(action, fn) {
    let span =
      this.getElement(this.id + "FloatSpan") || this.createElement("span");
    span.id = this.id + "FloatSpan";
    span.style.position = "fixed";
    span.style.right = "20px";
    span.style.bottom = "70px";
    span.innerHTML = `<aon-icon-button id="${
      this.id + action.id + "Button"
    }" icon="${action.icon}" title="${
      action.name
    }" background="#f1f1f1"></aon-icon-button>`;
    this.appendChild(span);
    this.getElement(this.id + action.id + "Button").addEventListener(
      "click",
      fn
    );
  }
  removeFloatOption() {
    let el = this.getElement(this.id + "FloatSpan");
    if (el) el.remove();
  }

  setContentHTML(html) {
    this.getElement(this.CONTENT).innerHTML = html;
  }

  getId() {
    return this.getAttribute("id");
  }

  getTitle() {
    return this.getAttribute("title");
  }

  getParent() {
    return this.parentNode;
  }

  getDialog() {
    return this.getElement(this.DIALOG);
  }

  getChild() {
    let el = this.getElement(this.CONTENT);
    if (el) el = el.firstChild;
    return el;
  }

  isSidenavBlock() {
    return (
      this.hasAttribute("sidenav") && "block" === this.getAttribute("sidenav")
    );
  }
}
if(!window.customElements.get('aon-application')){
  window.customElements.define("aon-application", AonApplication);
}
