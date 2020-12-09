import {AonElement} from '../../components/AonElement.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";

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

  attributeChangedCallback(name, oldValue, newValue) {

  }

  constructor () {
    super();
    this.id = this.is || 'aonMobileList';
    this.UL = this.id + 'UL';
  }

  connectedCallback () {
    this.build();
  }

  build() {
    this.innerHTML = '';
    let ul = this.createElement('ul');
    ul.id = this.UL;
    ul.style.overflow = 'auto';
    ul.className = 'list-group';
    this.appendChild(ul);

    let content = this.getElement('aonDocumentalContent');
    content.addEventListener('scroll', () => {
      let scrollTop = content.scrollTop;
      let offsetHeight = content.offsetHeight;
      let physicalSize = content.scrollHeight;
      let maxScrollPosition = physicalSize - offsetHeight;
      if(scrollTop >= maxScrollPosition) {
        this.dispatchEvent(new CustomEvent('more'));
      }
    });
  }

  addLi(data, index, fn) {
    let li = document.createElement('li');
    li.className = 'aonLi aonAppLi';

    li.addEventListener('click',  fn);

    let span = document.createElement('span');
    span.className = 'aonLiSpan';
    span.innerHTML = data.aonIcon
      ? `<aon-icon class="aonAvatar" icon="${data.aonIcon}" size="24"></aon-icon>`
      : `<i class="material-icons aonAvatar"> ${data.icon} </i>`;

    let div = document.createElement('div');
    div.className = 'aonListText';
    div.innerHTML = data.title;

    let span3 = document.createElement('span');
    span3.className = 'aonLiSpanSubtitle';
    span3.innerHTML = data.subtitle;

    span.appendChild(div);
    span.appendChild(span3);
    li.appendChild(span);

    this.getElement(this.UL).appendChild(li);
  }

  getFilter() {
    return this.hasAttribute('filter')
      ? JSON.parse(this.getAttribute('filter'))
      : {};
  }

  setFilter(filter) {
    return this.setAttribute('filter', JSON.stringify(filter));
  }


}
window.customElements.define('aon-mobile-list', AonMobileList);
