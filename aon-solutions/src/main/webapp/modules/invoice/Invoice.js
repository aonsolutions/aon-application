export class Invoice {

  id;
  type;
  serie;
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
  totalSuplidos;
  name;
  paymethod;

  constructor(type) {
    this.type = type || 'emitida';
    this.serie = '';
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
    this.irpf = false;
    this.suplidos = false;
    this.totalSuplidos = 0;
    this.status = 'inbox';
  }


  createInvoice(invoice) {
    if(invoice) {

      this.id = invoice.id || undefined;
      this.serie = invoice.serie || '';
      this.number = invoice.number || 0;
      this.reference = invoice.reference && invoice.reference !== ''
        ? invoice.reference
        : (invoice.serie ? invoice.serie + '/' + invoice.number : invoice.number);
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
      this.irpf = invoice.irpf || false;
      this.suplidos = invoice.suplidos || false;
      this.totalSuplidos = invoice.totalSuplidos || 0;
      this.file = invoice.file || undefined;

      this.name = this.type === 'emitida' ? this.receiver.name : this.sender.name;
      this.paymethod = this.finances.length > 0 ? this.finances[0].paymethod : '';
    }
  }

  isEmitida() {
    return this.type === 'emitida';
  }

  isRecibida() {
    return this.type === 'recibida';
  }

  isTicket() {
    return this.type === 'ticket';
  }
}
