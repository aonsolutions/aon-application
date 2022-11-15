import { AonElement } from "./AonElement.js";
import { CONSTANT, CSS, EVENT, TAG, MATERIAL_ICONS } from '../environments/environments.js';
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
        content.addEventListener(EVENT.SCROLL, () => {
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
      let ic = this.createElement(TAG.I);
      ic.classList.add(data.icon_class|| CONSTANT.MATERIAL_ICONS, "aonAvatar");
      ic.textContent = data.icon;
      if(data.icon_color) ic.style.color = data.icon_color;
      spanHtml = ic.outerHTML;
    }  else if (data.iconHTML) {
      spanHtml = data.iconHTML.outerHTML;
      span.style.display = "flex";
    } else if (data.iconHtmlCustom) {
      spanHtml = `${data.iconHtmlCustom}`;
      span.style.display = "flex";
    }

    span.innerHTML = spanHtml;

    if(icon){
      let remove = this.createElement(TAG.I);
      remove.classList.add(CONSTANT.MATERIAL_ICONS);
      remove.classList.add("aonAvatar");
      remove.textContent = icon;
      remove.style.display = 'none';
      remove.style.position = 'absolute';
      remove.style.right = '0px';
      remove.addEventListener(EVENT.CLICK, iconFn);
      span.appendChild(remove);
      
      li.addEventListener(EVENT.MOUSEOVER, () => remove.style.display  = 'block');
      li.addEventListener(EVENT.MOUSELEAVE, () => remove.style.display = 'none');
  
    }


    let div = this.createElement(TAG.DIV);
    div.className = "aonListText";
    div.style.marginTop = '10px';
    div.innerHTML = data.title;
    
    if(data.paddingTopTitle) {
      div.style.paddingTop = data.paddingTopTitle;
    }

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
      span4.style.top = "0px";
      div.appendChild(span4);

      let aonIconButton = new AonIconButton();
      aonIconButton.id = this.id+"IconOption";
      aonIconButton.icon = MATERIAL_ICONS.MORE_VERT;
      span4.appendChild(aonIconButton);

      span4.addEventListener(EVENT.CLICK, (ev) => {
        ev.preventDefault();
        ev.stopPropagation();
        this.getOptions(span4, data.option);
      });
    }

    //ELEMENT HTML
    if (data.elementHTML) {
      div.appendChild(data.elementHTML);
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
    this.appendChild(div);
    return aonDialogM;
  }

  getOptions(el, options) {
    const boundingClientRect = el.getBoundingClientRect();
    const top = boundingClientRect.top;
    const left = boundingClientRect.left;

    const d = this.getElement(this.id + "aonDialogAddOption") || this.createAonDialog();

    options = options.map(({ aonIcon, icon, name, fn }) => ({
        aonIcon,
        icon,
        name,
        fn: () => fn(el),
    }));

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
