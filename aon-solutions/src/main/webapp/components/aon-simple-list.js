import { AonElement } from "./AonElement.js";
import { CONSTANT, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonIconButton } from "./aon-icon-button.js";
import { AonDialogMenu } from "./aon-dialog-menu.js";
import { AonIcon } from "./aon-icon.js";

export class AonSimpleList extends AonElement {
 
  UL;
  filter;


  static get observedAttributes() {
    return [];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
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
    this.id = this.id || "aonUserSimpletList";
    this.UL = this.id + "UL";
    this.filter = this.filter || {};
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
            this.dispatchEvent(new CustomEvent("more"));
          }
        });
    }
  }

  addLi(data, index, fn, icon, iconFn) {
    let li = this.createElement(TAG.LI);
    li.className = "aonLi aonAppLi";
    li.style.height = '48px';

    li.addEventListener(EVENT.CLICK, fn);

    let span = this.createElement(TAG.SPAN);
    span.className = "aonLiSpan";
    let spanHtml = null;
    if (data.aonIcon) {
      let aonIcon = new AonIcon();
      aonIcon.className = "aonAvatar";
      aonIcon.icon      = data.aonIcon
      aonIcon.size      = "24";
      spanHtml = aonIcon.outerHTML;
    } else if (data.icon) {
      let ic = this.createElement("i");
      ic.classList.add(data.icon_class||"material-icons","aonAvatar");
      ic.textContent = data.icon;
      if(data.icon_color) ic.style.color = data.icon_color;
      spanHtml = ic.outerHTML;
    } else if (data.iconHtmlCustom) {
      spanHtml = `${data.iconHtmlCustom}`;
      span.style.display = "flex";
    }
    span.innerHTML = spanHtml;

    let remove = this.createElement("i");
    remove.classList.add("material-icons");
    remove.classList.add("aonAvatar");
    remove.textContent = icon;
    remove.style.display = 'none';
    remove.style.position = 'absolute';
    remove.style.right = '0px';
    remove.addEventListener(EVENT.CLICK, iconFn);
    span.appendChild(remove);

    li.addEventListener(EVENT.MOUSEOVER, () => remove.style.display = 'block');
    li.addEventListener(EVENT.MOUSELEAVE, () => remove.style.display = 'none');

    let div = this.createElement(TAG.DIV);
    div.className = "aonListText";
    div.style.marginTop = '10px';
    if(data.paddingTopTitle) div.style.paddingTop = data.paddingTopTitle;
    div.innerHTML = data.title;

    span.appendChild(div);

    if(data.subtitle){
      let span3 = this.createElement(TAG.SPAN);
      span3.className = "aonLiSpanSubtitle";
      span3.innerHTML = ' | '+ data.subtitle;
      span3.style.display = 'inline';
      div.appendChild(span3);
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

    return span;
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
    options = options.map(({ aonIcon, icon, name, fn }) => {
      return {
        aonIcon,
        icon,
        name,
        fn: () => fn(el),
      };
    });

    d.setMenuOptions(options, top, left);
    d.open();
  }

  getFilter() {
      return this.filter || {};
  }

  setFilter(filter) {
    return this.filter = filter;
  }

  empty(message) {
    message = message || 'No hay datos disponibles.';
    let div = this.createElement(TAG.DIV);
    div.style.width = '100%';
    div.style.textAlign = 'center';
    div.innerHTML = message;
    this.appendChild(div);
}
}
if(!window.customElements.get('aon-simple-list')){
  window.customElements.define("aon-simple-list", AonSimpleList);
}
