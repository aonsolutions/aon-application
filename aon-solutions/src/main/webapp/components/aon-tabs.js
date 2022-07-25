import { AonElement } from "./AonElement.js";
import { waitEl } from "../services/utils.js";
import { CONSTANT, EVENT, TAG } from "../environments/environments.js";
import '../css/aon-tabs.css';

export class AonTabs extends AonElement {
  ACTIVE_CLASS;
  TABS_ITEM;
  TABS_MENU;
  INDICATOR;
  INDICATOR_POSITION;
  DIV_ICON;
  POSITION;

  backgroundColor;

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  constructor() {
    super();
  }

  initialize() {
    this.id = this.id || "aonTabs";
    this.DIV_ICON = this.id + "DivIcon";
    this.ACTIVE_CLASS = "active";
    this.POSITION = 0;
    this.backgroundColor = this.backgroundColor || '#f1f1f1';
  }

  connectedCallback() {
    this.initialize();
    let nav = this.createElement("nav");
    nav.className = "tabs-box";
    nav.style.backgroundColor = this.backgroundColor;
    let div = this.createElement(TAG.DIV);
    div.className = "tabs-menu js-tabs-menu";
    div.role = "tablist";
    nav.appendChild(div);
    this.appendChild(nav);
  }

  setButtons(options){
    this.getContent().then((tabsMenu)=>{
        this.TABS_MENU = tabsMenu;
        const total = options.length || 1;
        options.map(option => this.paintButtons(option, total));   
        this.init();
    })
  }


  async getContent(){
    return waitEl(".js-tabs-menu");
  }

  paintButtons(option, total){
      const exists = this.getElement(option.id);
      if(exists) return null;
      const button = this.createElement(TAG.BUTTON);
      button.addEventListener(EVENT.CLICK, (ev) => {
        this.handleTabSelection(ev);
        const current = document.querySelector(`.focus`);
        if (current) {
          current.className = current.className.replace(` focus`, "");
        }
      }, false);
      button.setAttribute("aria-selected", "false");
      button.addEventListener("mousedown", (ev)=> this.handleRippleEffect(ev));
      button.addEventListener("keydown", (ev)=> this.handleArrowKeysFocus(ev));
      button.className = "tab-button js-tab-button";
      button.type= "button";
      button.id = option.id;
      button.setAttribute("role", "tab");
      const divSpan = this.createElement(TAG.DIV);
      divSpan.id = this.DIV_ICON+option.id;
      if(option.icon){
        const spanIcon = this.createElement(TAG.SPAN);
        spanIcon.classList.add("material-icons");
        spanIcon.textContent = option.icon;
        divSpan.appendChild(spanIcon);
      }
      button.appendChild(divSpan);
      button.style.width = window.innerWidth / total;
      const spanText = this.createElement(TAG.SPAN);
      spanText.classList.add("tab-button__content");
      spanText.textContent = option.name;
      button.appendChild(spanText);
      this.TABS_MENU.appendChild(button);
  }

  setBackgrounColor(backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  init(){
    this.INDICATOR = this.createElement(TAG.SPAN);
    this.INDICATOR.className = "tab-indicator js-tab-indicator";
    this.TABS_MENU.appendChild(this.INDICATOR);

    this.TABS_ITEM = document.querySelectorAll(".js-tab-button");
    this.INDICATOR_POSITION = this.TABS_ITEM[0].getBoundingClientRect().left - this.TABS_MENU.getBoundingClientRect().left;
    // Initially activate the first tab
    this.TABS_ITEM[0].setAttribute('tabindex', '0');
    this.TABS_ITEM[0].setAttribute('aria-selected', 'true');

    this.INDICATOR.style.width = `${this.TABS_ITEM[0].clientWidth}px`;
  }
  
    updateBadge(options){
        options.map(option=>this.createBadge(option.id, option.badge));
    }

    async createBadge(id, badge){
        const div = await waitEl("#"+ this.DIV_ICON+id);
        if(div){
            const idBadge = id+"Badge";
            const spanBadge = this.getElement(idBadge) || this.createElement(TAG.SPAN);
            spanBadge.id = idBadge;
            if(badge > 0){
                spanBadge.classList.add("badge");
                spanBadge.textContent = badge;
                const spanExist = div.querySelector("span");
                div.insertBefore(spanBadge, spanExist);
                spanBadge.style.top = .7;
            } else {
                spanBadge.remove();
            }
        }
    }

  handleTabSelection(ev) {
    this.toggleActiveClass(ev);
    this.moveTabIndicator(ev);
    this.handleA11y(ev);
    this.dispatchEvent(new CustomEvent(EVENT.CHANGE, {detail:{ position: this.getTabIndex(ev) }}));
  }
  
  toggleActiveClass(ev) {
    const current = document.querySelector(`.${this.ACTIVE_CLASS}`);
    if (current) {
      current.className = current.className.replace(` ${this.ACTIVE_CLASS}`, "");
    }

    ev.currentTarget.className += ` ${this.ACTIVE_CLASS}`;
  }

  moveTabIndicator(ev) {
    const indicatorPosition =  ev.currentTarget.getBoundingClientRect().left - this.TABS_MENU.getBoundingClientRect().left;
    this.INDICATOR.style.width = `${ev.currentTarget.clientWidth}px`;
    this.INDICATOR.style.left = `${indicatorPosition}px`;
  }

  handleRippleEffect(e) {
    const span = this.createElement(TAG.SPAN);
    const x = e.pageX - e.currentTarget.getBoundingClientRect().left;
    const y = e.pageY - e.currentTarget.getBoundingClientRect().top;

    // span.classList.add("tab-button__ripple");
    e.currentTarget.appendChild(span);
    span.style.left = `${x}px`;
    span.style.top = `${y}px`;

    setTimeout(() => {
      span.remove();
    }, 1000);
  }

  handleA11y(e) {
    this.TABS_ITEM.forEach((tab) => {
      const addActiveFocus = () => {
        e.currentTarget.setAttribute("aria-selected", "true");
        e.currentTarget.setAttribute("tabindex", "0");
      };

      const removeActiveFocus = () => {
        tab.setAttribute("aria-selected", "false");
        tab.setAttribute("tabindex", "-1");
      };
      tab === e.currentTarget ? addActiveFocus() : removeActiveFocus();
    });
  }

  handleArrowKeysFocus(e) {
    this.TABS_ITEM = document.querySelectorAll(".js-tab-button");
    const totalTabItems = this.TABS_ITEM.length - 1;
    const leftArrowKey = e.which === 37;
    const rightArrowKey = e.which === 39;

    let index = this.getTabIndex(e);
    let newIndex;

    const decrementIndex = () => {
      newIndex = index - 1;

      if (newIndex < 0) {
        newIndex = totalTabItems;
      }
    };

    const incrementIndex = () => {
      newIndex = index + 1;

      if (newIndex > totalTabItems) {
        newIndex = 0;
      }
    };

    if (leftArrowKey) {
      decrementIndex();
      this.toggleFocusClass(e);
    }

    if (rightArrowKey) {
      incrementIndex();
      this.toggleFocusClass(e);
    }

    const current = document.querySelector(`.focus`);
    if (current) {
      current.className = current.className.replace(` focus`, "");
    }

    if (this.TABS_ITEM[newIndex]) {
      this.TABS_ITEM[newIndex].className += ` focus`;
      this.TABS_ITEM[newIndex].focus();
    }
  }

  getTabIndex(ev){
    this.POSITION = Array.prototype.indexOf.call(this.TABS_ITEM, ev.currentTarget);
    return this.POSITION;
  }

  toggleFocusClass(e) {
    const current = document.querySelector(`.focus`);
    if (current) {
      current.className = current.className.replace(` focus`, "");
    }
    e.currentTarget.className += ` focus`;
  }

}
if (!window.customElements.get("aon-tabs")) {
  window.customElements.define("aon-tabs", AonTabs);
}
