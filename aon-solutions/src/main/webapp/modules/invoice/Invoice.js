import { CONSTANT } from "../../environments/environments.js";
import { RegistryType } from "../../models/enums.js";
import { round } from "../../services/utils.js";
import { getSurchargeByVat, TaxType } from "./invoiceEnums.js";

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
    this.number = '';
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
      this.serie = invoice.serie && invoice.serie !== CONSTANT.UNDEFINED ? invoice.serie : '';
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
    this.calculateTaxFromTotal();
  }

  getCategory() {
    return this.category;
  }

  setCategory(category) {
    this.category = category;
    if(category.substring(0, 3) === '705'){
      this.service = true;
    } else this.service = false;
  }
  
  getPaymethod() {
    return this.paymethod
  }

  setPaymethod(paymethod) {
    this.paymethod = paymethod;    
    this.calculateFinances();
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

  isService() {
    return this.service && this.service != CONSTANT.FALSE;
  }

  setService(service) {
    this.service = service;
  }

  isInvestment() {
    return this.investment && this.investment != CONSTANT.FALSE;
  }

  setInvestment(investment) {
    this.investment = investment;
  }

  isRectified() {
    return this.rectified && this.rectified != CONSTANT.FALSE;
  }

  setRectified(rectified) {
    this.rectified = rectified;
  }

  isSurcharge() {
    return this.surcharge && this.surcharge != CONSTANT.FALSE;
  }

  setSurcharge(surcharge) {
    this.surcharge = surcharge;
    this.taxes.forEach((tax, i) => {
      this.taxes[i] = this.calculateTax(tax);
    });
    this.calculateTotalFromTax();
    if(this.isWithholdingFarmer()) {
      this.calculateWithholdingFromTax();
    }
  }

  isWithholding() {
    return this.withholding && this.withholding != CONSTANT.FALSE;
  }

  setWithholding(withholding) {
    this.withholding = withholding;
    this.calculateWithholdingFromTax();
  }

  isWithholdingFarmer() {
    return this.withholding_farmer  && this.withholding_farmer != CONSTANT.FALSE;;
  }

  setWithholdingFarmer(withholding_farmer) {
    this.withholding_farmer = withholding_farmer;
    this.calculateWithholdingFromTax();
  }

  getTransaction() {

  }

  setTransaction(transaction) {
    this.transaction = transaction;
  }

  getRegistry() {
    return this.isEmitida() ? this.receiver : this.sender;
  }

  setRegistry(registry) {
    if(this.isEmitida()) {
      this.receiver = registry;
    } else this.sender = registry;
  }
 
  getRegistryType() {
    return this.isEmitida() ? [RegistryType.CUSTOMER]
      : [RegistryType.SUPPLIER, RegistryType.CREDITOR];
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

  addTax(){
    let tax = {
      tax: TaxType.IVA,
      type: this.isSurcharge() ? TaxType.IVA_RE : TaxType.IVA,
      percentage: 21.0,
      base: 0.0,
      quota: 0.0,
      surcharge: this.isSurcharge() ? 5.2 : 0.0,
      surcharge_quota: 0.0
     };
     this.taxes.push(tax);
     this.calculateTotalFromTax();
  }

  setTax(tax, i) {
    this.taxes[i] = this.calculateTax(tax);
    this.calculateTotalFromTax();
    this.calculateWithholdingFromTax();
  }

  deleteTax(tax, i) {
    if(TaxType.IRPF === tax.type) 
      this.withholding = false;
    this.taxes.splice(i, 1);
    this.calculateTotalFromTax();
    this.calculateWithholdingFromTax();
  }

  calculateWithholdingFromTax() {
    if(this.isWithholding() && this.taxes.filter(f => TaxType.IRPF === f.tax).length === 0) {
      let tax = {
        tax: TaxType.IRPF,
        type: this.isWithholdingFarmer() ? TaxType.IRPF_AGRI : TaxType.IRPF_PROF,
        percentage: this.isWithholdingFarmer() ? 2.0 : 15.0,
        base: 0.0,
        quota: 0.0,
        surcharge: 0.0,
        surcharge_quota: 0.0
       };
       this.taxes.push(tax);
    }

    if(this.isWithholding()) {  
      let base = 0.0;
      this.taxes.filter(f => TaxType.IVA === f.tax).forEach(tax => {
        base = base + round(Number(tax.base));
        if(this.isWithholdingFarmer()){
          base = base + round(Number(tax.quota)) + round(Number(tax.surcharge_quota));
        }
      });

      this.taxes.forEach((tax, i) => {
        if(TaxType.IRPF === tax.tax){
          tax.type = this.isWithholdingFarmer() 
            ? TaxType.IRPF_AGRI : (TaxType.IRPF_AGRI === tax.type ? TaxType.IRPF_PROF : tax.type);
          tax.percentage = this.isWithholdingFarmer() 
            ? 2.0 : (tax.percentage === 2.0 ? 15.0 : tax.percentage);
          tax.base = base;
          this.taxes[i] = this.calculateTax(tax); 
        }
      });
    } else if(this.taxes.filter(f => TaxType.IRPF === f.tax).length > 0){
      let index;
      this.taxes.forEach((tax, i) => {
        if(TaxType.IRPF === tax.tax){
          index = i;
        }
      });
      this.taxes.splice(index, 1);
    }     

  }

  calculateTotalFromTax() {
    let total = 0.0;
  
    this.taxes.filter(f => TaxType.IVA === f.tax).forEach(tax => {
      total = total + round(Number(tax.base) + Number(tax.quota) + Number(tax.surcharge_quota));
    });
    this.total = round(Number(total));
  }

  calculateTax(tax) {
    tax.quota = round(tax.base / 100 * tax.percentage);
    if(TaxType.IVA === tax.tax){
      tax.type = this.isSurcharge() ? TaxType.IVA_RE : TaxType.IVA;
      tax.surcharge = this.isSurcharge() ? getSurchargeByVat(tax.percentage) : 0.0;
      tax.surcharge_quota = round(tax.base / 100 * tax.surcharge);
    }
    return tax;
  }

  calculateTaxFromTotal() {
    if (this.taxes.length === 0) {
      let div = this.isSurcharge() ? 1.262 : 1.21;
			let tax = {
				tax: TaxType.IVA,
				type: this.isSurcharge() ? TaxType.IVA_RE : TaxType.IVA,
				percentage: 21.0,
				base: round(Number(this.total) / div),
				quota: round(Number(this.total / 1.21) * 0.21),
        surcharge: this.isSurcharge() ? 5.2 : 0.0,
        surcharge_quota: this.isSurcharge() ? round(Number(this.total / 1.052) * 0.052) : 0.0
		 	};
			this.taxes.push(tax);
		} else if(this.taxes.length === 1){
			let tax = this.taxes[0];
      const p = (this.isSurcharge() ? tax.percentage + getSurchargeByVat(tax.percentage) : tax.percentage) / 100;
      const p0 = p + 1;

      tax.base = round(Number(this.total) / p0);
			tax.quota = round(tax.base / 100 * tax.percentage);
			tax.surcharge = this.isSurcharge() ? getSurchargeByVat(tax.percentage) : 0.0;
      tax.surcharge_quota = round(tax.base / 100 * tax.surcharge);
      this.taxes[0] = tax;
		}
  }

  getWitholdingTax() {
    let tax = {};
    this.taxes.filter(f => TaxType.IRPF === f.tax).forEach(r=> {
      tax = r;
    });
    return tax;
  }

  addDetail() {
    let wh = this.getWitholdingTax();
    let detail = {
      description: '',
      item: undefined,
      quantity: 1.0,
      price: 0.0,
      discount: 0.0,
      amount: 0.0,
      category: this.category,
      prepayment: false,
      percentage: 21.0, 
      quota: 0.0,
      surcharge: this.isSurcharge() ? 5.2 : 0.0, 
      surcharge_quota: 0.0,
      withholding: this.withholding,
      withholding_type: wh.type,
      withholding_percentage: wh.percentage,
      withholding_quota: 0.0, 
     };
     this.details.push(detail);
     this.calculateTaxFromDetail();
  }

  deleteDetail(detail, i) {
    this.details.splice(i, 1);
    this.calculateTaxFromDetail();
  }

  setDetail(detail, i) {  
    this.details[i] = this.calculateDetail(detail);
    this.calculateTaxFromDetail();
  }

  calculateDetail(detail){
      let amount = round(Number(detail.quantity) * Number(detail.price));
			amount = amount - amount * (detail.discount / 100);
      detail.amount = round(amount);
      if(!detail.prepayment && detail.percentage) {
        detail.quota = round(detail.amount / 100 * detail.percentage);
        detail.surcharge = this.isSurcharge() ? getSurchargeByVat(detail.percentage) : 0.0;
        detail.surcharge_quota = round(detail.amount / 100 * detail.surcharge);
      } else {
        detail.percentage = undefined;
        detail.quota = 0.0;
        detail.surcharge = 0.0;
        detail.surcharge_quota = 0.0;
      }
      return detail;
  }

  calculateTaxFromDetail() {
    this.taxes = []
    
    this.details.forEach( (detail, i) => {
      if(!detail.prepayment || detail.prepayment === CONSTANT.FALSE) {
        if(this.taxes.filter(f => f.percentage === detail.percentage).length > 0) {
            this.taxes.forEach((tax, i) => {
            if(tax.percentage === detail.percentage){
             tax.base = tax.base + detail.amount;
             tax.quota = tax.quota + detail.quota;
             tax.surcharge_quota = tax.surcharge_quota + detail.surcharge_quota;
             this.taxes[i] = tax;
            }
          });
        } else {
          let tax = {
            tax: TaxType.IVA,
            type: this.isSurcharge() ? TaxType.IVA_RE : TaxType.IVA,
            percentage: detail.percentage,
            base: detail.amount,
            quota: detail.quota,
            surcharge: detail.surcharge,
            surcharge_quota: detail.surcharge_quota
          };
          this.taxes.push(tax);
        }
      }
    });  
    this.calculateTotalFromDetail();
    this.calculateWithholdingFromTax();
  }
  
  calculateTotalFromDetail() {
    let total = 0.0;
    this.details.forEach( detail => {
      total = total + detail.amount + detail.quota + detail.surcharge_quota;
    });  
    this.total = total;
  }

  calculateFinances() {
    if(this.finances.length === 0) {
      let finance = {
          due_date: this.date,
          paymethod: this.paymethod ? this.paymethod : 'CASH',
          amount: this.total,
          iban: ''
      };		
      this.finances.push(finance);
    } 
  }

  addFinance() {
    let finance = {
      due_date: this.date,
      paymethod: 'CASH',
      amount: 0.0,
      iban: ''
     };
     this.finances.push(finance);
  }

  setFinance(finance, i) {
   this.finances[i] = finance;
  }

  deleteFinance(finance, i) {
    this.finances.splice(i, 1);
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
  due_date;
  paymethod;
  amount;
  iban;
}