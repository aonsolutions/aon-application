import {AonElement} from '../../components/AonElement.js';
import {Paymethods} from '../../services/paymethod.js';
import {getInvoices, getInvoice} from '../../services/service.js';
import {Invoice} from './Invoice.js';

import {setInvoices, addInvoices, setIndex} from './InvoiceCache.js';

import { CONSTANT, MATERIAL_ICONS, MSG } from '../../environments/environments.js'; 
import { formatNumber } from '../../services/utils.js';
import { AonMobileList } from '../../components/aon-mobile-list.js';

export class AonMobileInvoiceList extends AonMobileList {
  more;

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
    this.initialize();
    this.init();
    this.addEventListener('more', () => {
      if(this.more)
        this.loadMore()
    });
  }

  initialize() {
    super.initialize();
    this.more = true;
  }

  loadMore() {
    let filter = this.getFilter();
    if(filter.page) {
        filter.page = filter.page + 1;
        this.setFilter(filter);
        getInvoices(filter).then(invoices => {
            addInvoices(invoices);
            if(invoices.length == 0)
                this.more = false;
            invoices.forEach((invoice, i) => this.addRow(invoice, i));
        });
    }
  }

  init() {
    this.more = true;
    this.build();

    getInvoices(this.getFilter()).then(invoices => {
      setInvoices(invoices);
      if(invoices.length == 0){   
        this.empty();
      }
      invoices.forEach((invoice, i) => {
        this.addRow(invoice, i);
      });
    });
  }

  addRow(invoice, i) {
    let inv = new Invoice();
    inv.createInvoice(invoice);

    let icon = MATERIAL_ICONS.ARCHIVE;
    if(inv.isEmitida()) icon = MATERIAL_ICONS.UNARCHIVE;
    if(inv.isTicket()) icon = MATERIAL_ICONS.RECEIPT;

    if(!inv.name){
      let receiver = inv.receiver && inv.receiver.name && !inv.receiver.name.isEmpty() ? inv.receiver.name.toUpperCase() : 'ACCREEDORES VARIOS';
      let sender =  inv.sender && inv.sender.name && !inv.sender.name.isEmpty() ? inv.sender.name.toUpperCase() : 'PROVEEDORES VARIOS';
      inv.name = inv.isEmitida() ? receiver : sender;
    }

    let liValue = {
        icon,
        title: inv.name,
        subtitle: inv.getDateStr() + ' - ' + formatNumber(inv.total, 2, "EUR")
    }
    this.addLi(liValue, i, () => this.aonInvoice(invoice, i));
  }
  
  aonInvoice(invoice, i) {
    if(this.getFilter().status !== 'accounting') {
			setIndex(i);
			let aip = document.querySelector('aon-invoice-panel');
			aip.aonInvoice(invoice.type, invoice);
		} else {
			getInvoice(invoice.id).then((inv) => {
				setIndex(i);
				let aip = document.querySelector('aon-invoice-panel');
				aip.aonInvoice(invoice.type, inv);
			});
		}
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
