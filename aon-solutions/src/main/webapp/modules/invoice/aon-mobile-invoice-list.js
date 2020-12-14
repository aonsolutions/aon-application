import {AonElement} from '../../components/AonElement.js';
import {Paymethods} from '../../services/paymethod.js';
import {getInvoices} from '../../services/service.js';
import {Invoice} from './Invoice.js';

import {setInvoices, setIndex} from './InvoiceCache.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";

export class AonMobileInvoiceList extends AonElement {
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
      if(this.getElement('invoiceMobileListUL'))
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

  init() {

    getInvoices(this.getFilter()).then(invoices => {
      setInvoices(invoices);
      this.innerHTML = '';

      this.buildTitle();

      let ul = this.createElement('ul');
      ul.id = 'invoiceMobileListUL'
      ul.className = 'list-group';
      this.appendChild(ul);
      invoices.forEach((invoice, i) => {
        ul.appendChild(this.buildLi(invoice, i));
      });
    });
  }

  buildTitle() {
    let span = this.createElement('span');
    span.style.marginLeft = '25px';
    span.style.color = 'gray';
    span.innerHTML = this.getInvoiceTitle();
    this.appendChild(span);
  }

  getInvoiceTitle() {
		if(this.getFilter().status === 'rejected' || this.getFilter().status === 'refused'){
			return MSG.AON_MSG_REJECTEDS;
		} else if(this.getFilter().status === 'draft' || this.getFilter().status === 'trash' ) {
      return MSG.AON_MSG_TRASH;
    } else return MSG.AON_MSG_INBOX;
	}

  buildLi(invoice, index) {
    let inv = new Invoice();
    inv.createInvoice(invoice);

    let li = document.createElement('li');
    li.className = 'aonLi aonAppLi';

    li.addEventListener('click',  () => this.aonInvoice(invoice, i));

    let span = document.createElement('span');
    span.className = 'aonLiSpan';

    let i = document.createElement('i');
    i.className = 'material-icons aonAvatar';

    if(inv.isEmitida()) i.innerHTML = 'unarchive';
    else if(inv.isTicket()) i.innerHTML = 'receipt';
    else i.innerHTML = 'archive';

    let receiver = inv.receiver && inv.receiver.name && !inv.receiver.name.isEmpty() ? inv.receiver.name.toUpperCase() : 'ACCREEDORES VARIOS';
    let sender =  inv.sender && inv.sender.name && !inv.sender.name.isEmpty() ? inv.sender.name.toUpperCase() : 'PROVEEDORES VARIOS';

    let div = document.createElement('div');
    div.className = 'aonListText';
    div.innerHTML = inv.isEmitida() ?  receiver : sender;

    let span3 = document.createElement('span');
    span3.className = 'aonLiSpanSubtitle';

    span3.innerHTML = inv.getDateStr() + ' - ' + inv.total + ' €';

    span.appendChild(i);
    span.appendChild(div);
    span.appendChild(span3);
    li.appendChild(span);

    return li;
  }

  aonInvoice(invoice, i) {
		setIndex(i);
		let aip = document.querySelector('aon-invoice-panel');
		aip.aonInvoice(invoice.type, invoice);
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
window.customElements.define('aon-mobile-invoice-list', AonMobileInvoiceList);
