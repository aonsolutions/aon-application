import {AonElement} from '../../components/AonElement.js';
import {getDocuments} from '../../services/service.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";

export class AonMobileDocumentalList extends AonElement {
  static get observedAttributes() {
    return [CONSTANT.FILTER];
  }

  get filter() {
    return this.getAttribute(CONSTANT.FILTER);
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, filter);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if(CONSTANT.FILTER === name) {
      if(this.getElement('documentalMobileListUL'))
        this.init();
    }
  }

  constructor () {
    super();
  }

  connectedCallback () {

    this.build();
  }


  build() {
    this.init();
  }


  loadMore() {
    let filter = this.getFilter();

    if(filter.page) {
      filter.page = filter.page + 1;
      this.setFilter(filter);
      getDocuments(filter).then(documents => {
        if(documents.length == 0)
          this.more = false;

        let ul = this.getElement('documentalMobileListUL');
        documents.forEach((doc, i) => {
          ul.appendChild(this.buildLi(doc, i));
        });
      });
    }
  }

  init() {
    getDocuments(this.getFilter()).then(documents => {
      this.innerHTML = '';
      let ul = this.createElement('ul');
      ul.id = 'documentalMobileListUL'
      ul.className = 'list-group';
      this.appendChild(ul);
      documents.forEach((doc, i) => {
        ul.appendChild(this.buildLi(doc, i));
      });
    });
  }

  buildLi(doc, index) {
    let li = document.createElement('li');
    li.className = 'aonLi aonAppLi';

    li.addEventListener('click',  () => this.aonDocument(doc, i));

    let span = document.createElement('span');
    span.className = 'aonLiSpan';
    span.innerHTML = this.getTypeIcon(doc.file.type);

    let div = document.createElement('div');
    div.className = 'aonListText';
    div.innerHTML = doc.title;

    let span3 = document.createElement('span');
    span3.className = 'aonLiSpanSubtitle';
    span3.innerHTML = doc.date + ' - ' + doc.size;

    span.appendChild(div);
    span.appendChild(span3);
    li.appendChild(span);

    return li;
  }

  getTypeIcon(type) {
    if(type.includes('pdf')) {
      return `<aon-icon class="aonAvatar" icon="aon_pdf" size="24"></aon-icon>`;
    } else if(type.includes('powerpoint') || type.includes('presentation')){
      return `<aon-icon class="aonAvatar" icon="aon_powerpoint" size="24"></aon-icon>`
    } else if(type.includes('excel') || type.includes('spreadsheet')){
      return `<aon-icon class="aonAvatar" icon="aon_excel" size="24"></aon-icon>`
    } else if(type.includes('word') || type.includes('text')){
      return `<aon-icon class="aonAvatar" icon="aon_word" size="24"></aon-icon>`
    } else if(type.includes('image')) {
      return `<aon-icon class="aonAvatar" icon="aon_image" size="24"></aon-icon>`
    } else {
      return `<aon-icon class="aonAvatar" icon="aon_file" size="24"></aon-icon>`
    }
  }

  aonDocument(doc, i) {
		let ad = document.querySelector('aon-documental');
		ad.aonDocument(doc);
	}

  getFilter() {
    return this.hasAttribute('filter')
      ? JSON.parse(this.getAttribute('filter'))
      : {status: 'inbox'};
  }

  setFilter(filter) {
    return this.setAttribute('filter', JSON.stringify(filter));
  }

}
window.customElements.define('aon-mobile-documental-list', AonMobileDocumentalList);
