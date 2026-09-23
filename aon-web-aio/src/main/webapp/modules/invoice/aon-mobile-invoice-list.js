import {AonElement} from '../../components/AonElement.js';
import {Paymethods} from '../../services/paymethod.js';
import {getInvoices, getInvoice, getDomainUserRoles} from '../../services/service.js';
import {Invoice} from './Invoice.js';

import {setInvoices, addInvoices, setIndex} from './InvoiceCache.js';

import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 
import { formatNumber } from '../../services/utils.js';
import { AonMobileList } from '../../components/aon-mobile-list.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import * as LS from '../../services/localStorageService.js';

export class AonMobileInvoiceList extends AonMobileList {

  more;
  icc;

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
    this.buildDur().then(() => {
      this.init();
      this.addEventListener(EVENT.MORE, this.moreFn);
    });
  }

  moreFn = () => {
    if(this.more)
      this.loadMore();
  };

  disconnectedCallback() {
    this.removeEventListener(EVENT.MORE, this.moreFn);
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

  setIcc(icc) {this.icc = icc; }

  init() {
    this.more = true;
    this.build();

    getInvoices(this.getFilter()).then(invoices => {
      if(!this.getDur().isInvoiceManager() && !this.getDur().isInvoicePortal()) {
        invoices = invoices.filter(f => f.creation_user === LS.getDomainLogin());
      } 
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
    let inv = new Invoice(invoice);

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
        subtitle: inv.getDateStr() + ' - ' + formatNumber(inv.total, 2, 2, "EUR")
    }
    this.addLi(liValue, i, () => this.aonInvoice(invoice, i));
  }
  
  aonInvoice(invoice, i) {
    let st = this.getFilter().status;
    if((st == 'pending' || st == 'processed') && this.getDur().hasInvofox() && this.getDur().isInvofox()) {
        setIndex(i);
	  		let aip = document.querySelector('aon-invoice-panel');
  			aip.aonInvoice(invoice.type, invoice);
    } else if (st == 'pending' || st == 'processed' || st == 'processing'){
       
    } else if(this.getFilter().status !== 'accounting') {
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
}

if(!window.customElements.get(TAG.AON_MOBILE_INVOICE_LIST)){
	window.customElements.define(TAG.AON_MOBILE_INVOICE_LIST, AonMobileInvoiceList);
}