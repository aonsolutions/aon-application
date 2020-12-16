export class Invoice {

  id;
  type;
  series;
  number;
  reference;
  date;
  transaction;
  category;
  total;
  sender;
  receiver;
  details;
  taxes;
  finances;
  status;
  irpf;
  suplidos;
  comments;

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
  }

  createInvoice(invoice) {
    if(invoice) {
      this.id = invoice.id || undefined;
      this.series = invoice.series || '';
      this.number = invoice.number || 0;
      this.reference = invoice.reference && invoice.reference !== ''
        ? invoice.reference
        : (invoice.series ? invoice.series + '/' + invoice.number : invoice.number);
      this.date = invoice.date || new Date();
      this.total = invoice.total || 0;
      this.type = invoice.type || 'emitida',
      this.category = invoice.category || '',
      this.transaction = invoice.transaction || 'NAC',
      this.status = invoice.status || 'inbox',
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
    }
  }

  isEmitida() {
    return this.type.toLowerCase() === 'emitida';
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
