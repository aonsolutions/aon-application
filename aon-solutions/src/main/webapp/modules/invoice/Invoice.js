export class Invoice {

  id;
  type;
  series;
  serie;
  number;
  reference;
  date;
  transaction;
  category;
  total;
  name;
  sender;
  receiver;
  details;
  taxes;
  finances;
  status;
  irpf;
  suplidos;
  comments;
  selfconta;
  insight;

  service; // boolean | servicio
  withholding; // boolean | retencion 
  investment; // boolean | bienes de inversion
  withholding_farmer; // boolean | regimen agrario
  vat_accrual_payment; // boolean | criterio de caja
  surcharge; // boolean | recargo de equivalencia
  rectified; // boolean | rectificativa

  constructor(type) {
    this.type = type || 'emitida';
    this.series = '';
    this.number = 0;
    this.reference = '';
    this.date = new Date(Date.now());
    this.total = 0;
    this.sender = {
      document: '',
      name: '',
      address: {
        country: 'ES',
        address: '',
        zip: '',
        city: '',
        province: ''
      }
    };
    this.receiver = {
      document: '',
      name: '',
      address: {
        country: 'ES',
        address: '',
        zip: '',
        city: '',
        province: ''
      }
    };
    this.category = '';
    this.transaction = 'NAC';
    this.taxes = [];
    this.details = [];
    this.finances = [];
    this.suplidos = {
      active:false,
      description: '',
      total: 0
    };
    this.status = 'inbox';
    this.comments = [];
    this.selfconta = false;
  }

  createInvoice(invoice) {
    if(invoice) {
      this.id = invoice.id || undefined;
      this.series = invoice.series || '';
      this.serie = invoice.serie || '';
      this.number = invoice.number || '';
      this.reference = invoice.reference && invoice.reference !== ''
        ? invoice.reference
        : (invoice.series ? invoice.series + '/' + invoice.number : invoice.number);
      this.date = invoice.date || new Date();
      this.total = invoice.total || 0;
      this.type = invoice.type || 'emitida',
      this.category = invoice.category || '',
      this.transaction = invoice.transaction || 'NAC',
      this.status = invoice.status || 'inbox',
      this.name = invoice.name;
      this.sender = invoice.sender || {
        document: '',
        name: '',
        address: {
          country: 'ES',
          address: '',
          zip: '',
          city: '',
          province: ''
        }
      };
      this.receiver = invoice.receiver || {
        document: '',
        name: '',
        address: {
          country: 'ES',
          address: '',
          zip: '',
          city: '',
          province: ''
        }
      };
      this.taxes = invoice.taxes || [];
      this.details = invoice.details || [];
      this.finances = invoice.finances || [];
      this.suplidos = invoice.suplidos || {
        active: false,
        description: '',
        total: 0
      };
      this.file = invoice.file || undefined;
      this.comments = invoice.comments || [];
      this.selfconta = invoice.selfconta || false;
    }
  }

  getSerie() {
      return this.serie;
  }

  setSerie(serie) {
      this.serie = serie;
  }

  getNumber() {
    return this.number;
  }

  setNumber(number) {
    this.number = number;
  }

  getDate() {
    return this.getDate();
  }

  setDate(date) {
    this.date = date;
  }

  getTotal(){
    return this.total;
  }

  setTotal(total){
    this.total = total;
    // TODO CALCULATE
  }

  isEmitida() {
    return this.type.toLowerCase() === 'emitida';
  }

  isSelfconta() {
    return this.selfconta;
  }

  isRecibida() {
    return this.type.toLowerCase() === 'recibida';
  }

  isTicket() {
    return this.type.toLowerCase() === 'ticket';
  }

  isInbox() {
    return this.status.toLowerCase() === 'inbox';
  }

  isRejected() {
    return this.status.toLowerCase() === 'refused' || this.status.toLowerCase()  === 'rejected';
  }

  isDraft() {
    return this.status.toLowerCase() === 'trash' || this.status.toLowerCase() === 'draft';
  }

  isAccounting() {
    return this.status.toLowerCase() === 'scored' || this.status.toLowerCase() === 'accounting';
  }

  isReadonly() {
    return false;
  }

  getRegistry() {
    return this.isEmitida() ? this.receiver : this.sender;
  }

  getDate() {
    return new Date(this.date);
  }

  getDateStr() {
    let date = new Date(this.date);
    let day = date.getDate();
    let month = date.getMonth() + 1;
    let year = date.getFullYear();
    return day + '/' + month + '/' + year;
  }

  getInvoiceType() {
    if(this.isEmitida()){
      return 'sales';
    } else if(this.isRecibida()) {
      return 'purchase';
    } else return 'ticket';
  }
}

export class InvoiceTax {
  type; // IVA / IRPF
  base;
  percentage;
  quota;
  surcharge;
  surcharge_quota;
}

export class InvoiceDetail {
  id;
  description; // descripción
  product; // product item id
  quantity; // cantidad
	price; // precio
  discount; // descuento
	amount;// base;
  prepayment; // suplidos 
  percentage; // vat percentage
  quota; // tax quota
  surcharge; // surcharge percentage
  surcharge_quota; // surcharge quota
}

export class InvoiceFinance {

}