import { AonElement } from "./AonElement.js";
import { waitEl } from "../services/utils.js";

export class AonTabs extends AonElement {
  ACTIVE_CLASS;
  TABS_ITEM;
  TABS_MENU;
  INDICATOR;
  INDICATOR_POSITION;
  DIV_ICON;
  POSITION;
  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  constructor() {
    super();
    this.id = this.id || "aonTabs";
    this.DIV_ICON = this.id + "DivIcon";
    this.ACTIVE_CLASS = "active";
    this.POSITION = 0;
  }

  connectedCallback() {
    this.innerHTML = /*html*/`
    <nav class="tabs-box">
        <div aria-label="simple tabs example" class="tabs-menu js-tabs-menu" role="tablist"> </div>
    </nav>
    `;
  }

  setButtons(options){
    this.getContent().then((tabsMenu)=>{
        this.TABS_MENU = tabsMenu;
        const total = options.length || 1;
        options.map(option => this.paintButtons(option, total));   
        this.initialize();
    })
  }


  async getContent(){
    return await waitEl(".js-tabs-menu");
  }

  paintButtons(option, total){
      const button = this.createElement('button');
      button.addEventListener("click", (ev) => {
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
      const divSpan = this.createElement('div');
      divSpan.id = this.DIV_ICON+option.id;
      if(option.icon){
        const spanIcon = this.createElement('span');
        spanIcon.classList.add("material-icons");
        spanIcon.textContent = option.icon;
        divSpan.appendChild(spanIcon);
      }
      button.appendChild(divSpan);
      button.style.width = window.innerWidth / total;
      const spanText = this.createElement('span');
      spanText.classList.add("tab-button__content");
      spanText.textContent = option.name;
      button.appendChild(spanText);
      this.TABS_MENU.appendChild(button);
  }

  initialize(){
    this.INDICATOR = this.createElement("span");
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
            const spanBadge = this.getElement(idBadge) || this.createElement('span');
            if(badge > 0){
                spanBadge.classList.add("badge");
                spanBadge.id = idBadge;
                spanBadge.textContent = badge;
                const spanExist = div.querySelector("span");
                const top  = spanExist.getBoundingClientRect().top;
                const left = spanExist.getBoundingClientRect().left;   
                div.insertBefore(spanBadge, spanExist);
                // spanBadge.style.left =`calc(${left} * 0.80)`; //left > (spanBadge.offsetWidth / 2) ? left - 180 : left;
                spanBadge.style.top = top - 67;
                console.log(left, top, spanBadge.offsetWidth); // left: 68.5938px;
            } else {
                spanBadge.remove();
            }
        }
    }

  handleTabSelection(ev) {
    this.toggleActiveClass(ev);
    this.moveTabIndicator(ev);
    this.handleA11y(ev);
    this.dispatchEvent(new CustomEvent('change', {detail:{
      position: this.getTabIndex(ev)
    }}));
  }
  
  toggleActiveClass(e) {
    const current = document.querySelector(`.${this.ACTIVE_CLASS}`);
    if (current) {
      current.className = current.className.replace(` ${this.ACTIVE_CLASS}`, "");
    }

    e.target.className += ` ${this.ACTIVE_CLASS}`;
  }

  moveTabIndicator(e) {
    const indicatorPosition =  e.currentTarget.getBoundingClientRect().left - this.TABS_MENU.getBoundingClientRect().left;
    this.INDICATOR.style.width = `${e.currentTarget.clientWidth}px`;
    this.INDICATOR.style.left = `${indicatorPosition}px`;
  }

  handleRippleEffect(e) {
    // const posX = e.target.offsetLeft;
    // const posY = e.target.offsetTop;
    const span = document.createElement("span");
    const x = e.pageX - e.currentTarget.getBoundingClientRect().left;
    const y = e.pageY - e.currentTarget.getBoundingClientRect().top;

    span.classList.add("tab-button__ripple");
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

    let index = this.getTabIndex(ev);
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
