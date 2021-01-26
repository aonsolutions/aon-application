import { AonElement } from "./AonElement.js";

import "./aon-icon.js";
import "./aon-icon-button.js";
import "./aon-dialog-menu.js";

import * as CONSTANT from "../environments/constants.js";
import * as MSG from "../environments/msg.js";

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
    this.id = this.id || "aonMobileList";
    this.UL = this.id + "UL";
  }

  connectedCallback() {
    this.build();
  }

  build() {
    this.innerHTML = "";
    let ul = this.createElement("ul");
    ul.id = this.UL;
    ul.style.overflow = "auto";
    ul.className = "list-group";
    this.appendChild(ul);

    let content = this.getElement("aonDocumentalContent");
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

  addLi(data, index, fn) {
    let li = document.createElement("li");
    li.className = "aonLi aonAppLi";

    li.addEventListener("click", fn);

    let span = document.createElement("span");
    span.className = "aonLiSpan";
    let spanHtml = null;
    if (data.aonIcon) {
      spanHtml = `<aon-icon class="aonAvatar" icon="${data.aonIcon}" size="24"></aon-icon>`;
    } else if (data.icon) {
      spanHtml = `<i class="material-icons aonAvatar"> ${data.icon} </i>`;
    } else if (data.iconHtmlCustom) {
      spanHtml = `${data.iconHtmlCustom}`;
    }
    span.innerHTML = spanHtml;

    let div = document.createElement("div");
    div.className = "aonListText";
    div.innerHTML = data.title;

    let span3 = document.createElement("span");
    span3.className = "aonLiSpanSubtitle";
    span3.innerHTML = data.subtitle;

    span.appendChild(div);
    span.appendChild(span3);
    li.appendChild(span);

    ///OPTIONS
    if (data.option) {
      let span4 = document.createElement("span");
      span4.className = "aonListMoreVert";
      span4.innerHTML = `<aon-icon-button id="${this.id}IconOption" icon="more_vert"></aon-icon-button>`;
      span4.addEventListener("click", (e) => {
        e.stopPropagation();
        this.getOptions(span4, data.option);
      });
      li.appendChild(span4);
    }

    this.getElement(this.UL).appendChild(li);
  }

  removeAllLi() {
    let url = this.getElement(this.id + "UL");
    if (url) url.innerHTML = "";
  }

  createAonDialog() {
    const id = this.id + "aonDialogAddOption";
    let div = document.createElement("div");
    div.innerHTML = `<aon-dialog-menu id="${id}" ></aon-dialog-menu>`;
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
    return this.hasAttribute("filter")
      ? JSON.parse(this.getAttribute("filter"))
      : {};
  }

  setFilter(filter) {
    return this.setAttribute("filter", JSON.stringify(filter));
  }
}
window.customElements.define("aon-mobile-list", AonMobileList);
