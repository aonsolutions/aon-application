import { AonElement } from "./AonElement.js";
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonIconButton } from "./aon-icon-button.js";
import { AonDialogMenu } from "./aon-dialog-menu.js";
import { AonIcon } from "./aon-icon.js";

export class AonMobileList extends AonElement {
  UL;

  static get observedAttributes() {
    return [];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get filter() {
    return this.getAttribute(CONSTANT.FILTER);
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, filter);
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || "aonMobileList";
    this.UL = this.id + "UL";
  }

  build() {
    this.innerHTML = "";
    let ul = this.createElement(TAG.UL);
    ul.id = this.UL;
    ul.style.overflow = "auto";
    ul.classList.add(CSS.AON_UL);
    ul.classList.add(CSS.AON_LIST_GROUP);
    this.appendChild(ul);
    let aonAplication = this.getApplication();
    if(aonAplication){
      let content = aonAplication.getContent();
      if (content)
        content.addEventListener("scroll", () => {
          let scrollTop = content.scrollTop;
          let offsetHeight = content.offsetHeight;
          let physicalSize = content.scrollHeight;
          let maxScrollPosition = physicalSize - offsetHeight;
          if (scrollTop >= maxScrollPosition) {
            this.dispatchEvent(new CustomEvent(EVENT.MORE));
          }
        });
    }
  }

  addLi(data, index, fn) {
    let li = this.createElement(TAG.LI);
    li.className = "aonLi aonAppLi";

    li.addEventListener(EVENT.CLICK, fn);

    let span = this.createElement(TAG.SPAN);
    span.className = "aonLiSpan";
    let spanHtml = null;
    let icon = null;
    if (data.aonIcon) {
      icon = new AonIcon();
      icon.className = "aonAvatar";
      icon.icon      = data.aonIcon;
      icon.size      = data.size || "24";
      if(data.icon_color){
        icon.color = data.icon_color;
      }
      spanHtml = icon.outerHTML;
    } else if (data.icon) {
      icon = this.createElement("i");
      icon.classList.add(data.icon_class||"material-icons","aonAvatar");
      icon.textContent = data.icon;
      if(data.icon_color){
        icon.style.color = data.icon_color;
      }
      spanHtml = icon.outerHTML;
    } else if (data.iconHtmlCustom) {
      spanHtml = `${data.iconHtmlCustom}`;
    }
    span.innerHTML = spanHtml;

    let div = this.createElement(TAG.DIV);
    div.className = "aonListText";
    if(data.paddingTopTitle) div.style.paddingTop = data.paddingTopTitle;
    div.innerHTML = data.title;

    span.appendChild(div);

    if(data.subtitleTwo){

      if(icon){
        icon.style.marginTop = "16px";
      }
      const span3 = this.createElement(TAG.SPAN);
      span3.className = "aonLiSpanSubtitle";
      span3.style.display = "flex";
      span3.style.flexDirection = "column";
      span.appendChild(span3);

      let subtitle = this.createElement(TAG.SPAN);
      subtitle.innerHTML = data.subtitle;
      span3.appendChild(subtitle);

      let subtitleTwo = this.createElement(TAG.SPAN);
      subtitleTwo.innerHTML = data.subtitleTwo;
      span3.appendChild(subtitleTwo);

    } else if(data.subtitle){
      const span3 = this.createElement(TAG.SPAN);
      span3.className = "aonLiSpanSubtitle";
      span3.innerHTML = data.subtitle;
      span.appendChild(span3);
    }

 

    li.appendChild(span);

    ///OPTIONS
    if (data.option) {
      let span4 = this.createElement(TAG.SPAN);
      span4.className = "aonListMoreVert";
      let aonIconButton = new AonIconButton();
      aonIconButton.id = this.id+"IconOption";
      aonIconButton.icon = "more_vert";
      span4.appendChild(aonIconButton);
      span4.addEventListener(EVENT.CLICK, (ev) => {
        ev.stopPropagation();
        this.getOptions(span4, data.option);
      });
      li.appendChild(span4);
    }

    this.getElement(this.UL).appendChild(li);
    
    return li;
  }

  removeAllLi() {
    let ul = this.getElement(this.UL);
    if (ul) ul.innerHTML = "";
  }

  createAonDialog() {
    const id = this.id + "aonDialogAddOption";
    let div = this.createElement(TAG.DIV);
    let aonDialogM = new AonDialogMenu();
    aonDialogM.id = id;
    div.appendChild(aonDialogM);
    if (this) this.appendChild(div);
  }

  getOptions(el, options) {
    const top = el.getBoundingClientRect().top;
    const left = el.getBoundingClientRect().left;
    let d = this.getElement(this.id + "aonDialogAddOption");
    options = options.map(({ aonIcon, icon, name, title, backgroundColor, permission, fn }) => 
      ({
        aonIcon,
        icon,
        name,
        title,
        backgroundColor,
        permission,
        fn: () => fn(el),
      })
    );

    d.setMenuOptions(options, top, left);
    d.open();
  }

  getFilter() {
    return this.hasAttribute("filter")
      ? JSON.parse(this.getAttribute("filter"))
      : {};
  }

  setFilter(filter) {
    return this.setAttribute("filter", JSON.stringify(filter));
  }

  empty(message) {
    message = message || 'No hay datos disponibles.';
    let div = this.createElement(TAG.DIV);
    div.style.width = '100%';
    div.style.textAlign = 'center';
    div.innerHTML = message;
    this.appendChild(div);
  }

  loading(b){
    const id = "MobileTableLoading";
    let load = this.getElement(id);
    if (b && !load) {
      load = this.createElement(TAG.DIV);
      load.id = id;
      load.classList.add(CSS.AON_ICON_CONTAINER);
      load.style.right     = "0";
      load.style.left      = "0";
      load.style.textAlign = "center";
      let icon = this.createElement(TAG.I);
      icon.classList.add(CSS.AON_LOADER);
      load.appendChild(icon);

      this.appendChild(load);

    } else if (!b && load) {
      load.remove();
    }
  }

  app;
  
  getApp() {
    return this.app;
  }

  setApp(app) {
    this.app = app;
  }
}
if(!window.customElements.get('aon-mobile-list')){
  window.customElements.define("aon-mobile-list", AonMobileList);
}
