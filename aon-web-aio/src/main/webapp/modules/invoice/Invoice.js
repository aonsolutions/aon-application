import { CONSTANT } from "../../environments/environments.js";
import { RegistryType } from "../../models/enums.js";
import { round, now } from "../../services/utils.js";
import { getSurchargeByVat, TaxType, WithholdingType } from "./invoiceEnums.js";
import * as LS from '../../services/localStorageService.js';

export class Invoice {

  id;
  domain;
  type;
  series;
  number;
  reference;
  date;
  expDate;
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
  remarks;
  selfconta;
  insight;
  signed;

  taxableBase;
  vatQuota;
  retenttionQuota;

  activity;

  service; // boolean | servicio
  withholding; // boolean | retencion 
  investment; // boolean | bienes de inversion
  withholdingFarmer; // boolean | regimen agrario
  vatAccrualPayment; // boolean | criterio de caja
  surcharge; // boolean | recargo de equivalencia
  rectifier; // boolean | rectificativa
  rectified; // boolean | rectificada
  rectificationInvoice;

  creation_user;

  // tbai; // boolean
  // tbaiUrl;

  // verifactu; // boolean
  // verifactuUrl;

  workplace;
  
  messages;
  ocrStatus;

  communicationInfo;
  lastStatus;

  constructor(invoice) {
    this.buildObject(invoice);
  }

  buildObject(invoice) {
    if(invoice) {
      this.id = invoice.id || undefined;
      this.domain = invoice.domain || localStorage.getItem('aon_domain_id');
      this.series = invoice.series || '';
      this.number = invoice.number || '';
      this.reference = invoice.reference && invoice.reference !== ''
        ? invoice.reference
        : (invoice.series ? invoice.series + '/' + invoice.number : invoice.number);
      this.date = invoice.date || now();
      this.expDate = invoice.expDate;
      this.total = invoice.total || 0;
      this.type = invoice.type || 'emitida',
      this.category = invoice.category || '',
      this.transaction = invoice.transaction || 'NAC',
      this.status = invoice.status || 'inbox',
      this.ocrStatus = invoice.ocrStatus || undefined,
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
      this.comments = invoice.comments || '';
     
      if(invoice.remarks && Array.isArray(invoice.remarks)) this.remarks = invoice.remarks;
      else if(invoice.remarks && typeof invoice.remarks === 'string' && invoice.remarks.isNotEmpty()) {
        try {
          this.remarks = JSON.parse(invoice.remarks);
        } catch(e) {  
          this.remarks = [];
          this.remarksDescription = invoice.remarks;
        } 
      } else this.remarks = [];
    
      this.selfconta = invoice.selfconta || false;
      this.amortization = invoice.amortization || undefined;

      this.activity = invoice.activity;
      this.signed = invoice.signed;
      this.service = invoice.service || false;// boolean | servicio
      this.withholding = invoice.withholding || false; //this.isEmitida() ? company.withholding : false; // boolean | retencion 
      this.investment = invoice.investment || false; // boolean | bienes de inversion
      this.withholdingFarmer = invoice.withholdingFarmer || false; // boolean | regimen agrario
      this.vatAccrualPayment = invoice.vatAccrualPayment;// boolean | criterio de caja
      this.surcharge = invoice.surcharge;
      this.rectified = invoice.rectified || false;
      this.rectifier = invoice.rectifier || false;
      this.rectificationInvoice = invoice.rectificationInvoice || undefined;
      this.documentNumber = invoice.documentNumber || undefined;
      this.creation_user = invoice.creation_user || LS.getDomainLogin();
      // this.tbai = invoice.tbai || false;
      // this.tbaiUrl = invoice.tbaiUrl || '';
      // this.verifactu = invoice.verifactu || false;
      // this.verifactuUrl = invoice.verifactuUrl || '';
      this.workplace = invoice.workplace; 
      this.messages = invoice.messages || [];
      this.insight = invoice.insight || {};
      this.communicationInfo = invoice.communicationInfo;
      this.taxableBase = invoice.taxableBase;
      this.vatQuota = invoice.vatQuota;
      this.retentionQuota = invoice.retentionQuota;
      this.lastStatus = invoice.lastStatus;
      if(this.finances.length === 0) this.resetFinances();
    } else {
      this.domain = LS.getDomainId();
      this.type = 'ticket';
      this.series = new Date().getFullYear();
      this.number = '';
      this.reference = '';
      this.date = now();
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
      this.remarks = [];
      this.comments = '';
      this.selfconta = false;

      this.withholding = false; //this.isEmitida() ? company.withholding : false;
      this.creation_user = LS.getDomainLogin();
      // this.tbai = false;
      // this.tbaiUrl = '';
      this.signed = false;
    }
    // getCompany().then(company => {
    //   this.surcharge = this.surcharge || company.surcharge;
    //   this.vatAccrualPayment = this.vatAccrualPayment || company.vatAccrualPayment;
    // });
  }

  getType() {
    return this.type;
  }

  setType(type) {
    this.type = type;
    return this;
  }

  getActivity() {
    return this.activity;
  }

  setActivity(activity) {
    this.activity = activity;
    if(!this.isVatEnabled()){
      this.surcharge = false;
      if(this.isCcm())
        this.taxes = this.taxes.filter(f => TaxType.IRPF === f.tax);
      else {
        this.withholding = false;
        this.withholdingFarmer = false;
      }        

      this.taxes.push({
        tax:TaxType.IVA,
        type: TaxType.IVA,
        percentage: 0.0,
        quota:0.0,
        base: this.total,
        surcharge: 0.0,
        surcharge_quota: 0.0
      });
   
      this.details.forEach((detail,i) => {
        if(detail.percentage !== 0.0){
          detail.percentage = 0.0;
          this.setDetail(detail, i);
        }
      });
    }
    return this;
  }

  hasRemarks() {
    return (this.remarks && this.remarks.length > 0)
        || (this.remarksDescription && this.remarksDescription.isNotEmpty());
  }

  getSeries() {
    return this.series;
  }

  setSeries(series) {
    this.series = series;
    return this;
  }

  getNumber() {
    return this.number;
  }

  setNumber(number) {
    this.number = number;
    return this;
  }

  getReference() {
    return this.reference;
  }

  setReference(reference) {
    this.reference = reference;
    return this;
  }

  getDate() {
    return this.getDate();
  }

  setDate(date) {
    this.date = date;
    return this;
  }

  getTotal(){
    return this.total;
  }

  setTotal(total) {
    this.total = round(Number(total));
    this.calculateTaxFromTotal();
    this.calculateFinances();
    return this;
  }

  getCategory() {
    return this.category;
  }

  setCategory(category) {
    this.category = category;
    if(category && category.substring(0, 3) === '705'){
      this.service = true;
    } else this.service = false;

    this.details.forEach((detail, i) => {
        detail.category = category;
        this.details[i] = detail;
    }); 
    return this;
  }
  
  getPaymethod() {
    return this.paymethod
  }

  setPaymethod(paymethod) {
    this.paymethod = paymethod;    
    this.calculateFinances();
    return this;
  }

  isEmitida() {
    return this.type.toLowerCase() === 'emitida';
  }

  isNacional() {
    return this.transaction === 'NAC';
  }

  isIntracommunity() {
    return this.transaction === 'INTR';
  }

  isExtracommunity() {
    return this.transaction === 'EXTR';
  }

  isIsp() {
    return this.transaction === 'ISP';
  }

  isCcm() {
    return this.transaction === 'CCM';
  }

  isExempt() {
    return this.activity && this.activity.vatRegime 
      && "EXEMPT" === this.activity.vatRegime;
  }

  isVatEnabled() {
    return (this.isEmitida() && this.isNacional() && !this.isExempt())
      || (this.isRecibida() && 
            (this.isIntracommunity()
              || (this.isExtracommunity && this.isService())
              || (this.isCcm() && this.isService())
              || this.isIsp()
              || this.isNacional()
            )
          );
      // || TODO REGIMEN DE IMPORTACION    
  }

  isVatCalculate(){
    return this.isNacional() && !this.isExempt();
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

  isRawdoc() {
    return this.isInbox() || this.isRejected() || this.isTrash()
      || this.isProcessed() || this.isProcessing();
  }

  isOcrStatus(...publicStates) {
  	return publicStates.some( publicState =>  this.status.toLowerCase() === CONSTANT.OCR + publicState.toLowerCase() ); 
  }

  isRawdocOcrStatus(...publicStates) {
  	return this.ocrStatus && publicStates.some( publicState =>  this.ocrStatus.toLowerCase() === CONSTANT.OCR + publicState.toLowerCase() ); 
  }

  isInbox() {
    return this.status.toLowerCase() === 'inbox';
  }

  isProcessed() {
    return this.status.toLowerCase() === 'processed';    
  }

  isProcessing() {
    return this.status.toLowerCase() === 'processing';    
  }

  isPending() {
    return this.status.toLowerCase() === 'pending';
  }

  isRejected() {
    return this.status.toLowerCase()  === CONSTANT.REJECTED;
  }

  isTrash() {
    return this.status.toLowerCase() === CONSTANT.TRASH;
  }

  isPending() {
    return this.status.toLowerCase() === 'pending';
  }

  isScored() {
    return this.status.toLowerCase() === 'scored';
  }

  isAccounting() {
    return this.status.toLowerCase() === 'scored' || this.status.toLowerCase() === 'accounting';
  }

  isReadonly() {
    return this.isScored() || this.isPending() || this.isAccounting();
  }

  isService() {
    return this.service && this.service != CONSTANT.FALSE;
  }

  isSigned() {
    return this.signed;
  }

  isAccountSource() {
    return this.details.filter(f => f.source == 'ACCOUNT').length > 0;
  }

  isFeeSource() {
    return this.details.filter(f => f.source == 'FEE').length > 0;
  }

  isTbai()          { throw new Error("Invoice.js isTBAI() Not Supported!");}
  isVerifactu()     { throw new Error("Invoice.js isVerifactu() Not Supported!");}
  getTbaiUrl()      { throw new Error("Invoice.js getTbaiUrl() Not Supported!");}
  getVerifactuUrl() { throw new Error("Invoice.js getVerifactuUrl() Not Supported!");}
  
  canBeAnnulled() {
    if (!this.id) return false;
    if (!this.communicationInfo || Object.keys(this.communicationInfo).length === 0) {
      return false;
    }
    let validSources = ["VERIFACTU", "TBAI", "LROE"];
    let validStatuses = ["ACCEPTED", "ACCEPTED_WITH_ERRORS"];
    return validSources.some(src => {
      let info = this.communicationInfo[src];
      return info && validStatuses.includes(info.communicationStatus);
    });
  }
  
  setService(service) {
    this.service = service;
    return this;
  }

  isVatAccrualPayment() {
    return this.vatAccrualPayment && this.vatAccrualPayment != CONSTANT.FALSE;
  }

  setVatAccrualPayment(vatAccrualPayment) {
    this.vatAccrualPayment = vatAccrualPayment;
    return this;
  }

  isInvestment() {
    return this.investment && this.investment != CONSTANT.FALSE;
  }

  setInvestment(investment) {
    this.investment = investment;
    return this;
  }

  isRectified() {
    return this.rectified && this.rectified != CONSTANT.FALSE;
  }

  setRectified(rectified) {
    this.rectified = rectified;
    return this;
  }

  isRectifier() {
    return this.rectifier && this.rectifier != CONSTANT.FALSE;
  }

  setRectifier(rectifier) {
    this.rectifier = rectifier;
    return this;
  }

  getRectificationInvoice() {
    return this.rectificationInvoice;
  }

  setRectificationInvoice(invoice) {
    this.setRectifier(true);
    this.rectificationInvoice = {
      id: invoice.id,
      series: invoice.series,
      number: invoice.number,
      date: invoice.date,
      referenceCode: invoice.referenceCode
    };
    return this;
  }

  isSurcharge() {
    return this.surcharge && this.surcharge != CONSTANT.FALSE;
  }

  setSurcharge(surcharge) {
    this.surcharge = surcharge;
    this.details.forEach((detail, i) => {
      this.details[i] = this.calculateDetail(detail);
    });
    this.taxes.forEach((tax, i) => {
      this.taxes[i] = this.calculateTax(tax);
    });
    if(this.isWithholdingFarmer()) {
      this.calculateWithholdingFromTax();
    }
    this.calculateTotalFromTax();
    return this;
  }

  isWithholding() {
    return this.withholding && this.withholding != CONSTANT.FALSE;
  }

  setWithholding(withholding, def) {
    this.withholding = withholding;
    let wt = def ? WithholdingType.find(v => v.id == def) : undefined;
    if(this.isNacional() && !this.isExempt()) {
      this.calculateWithholdingFromTax(wt);
      this.calculateTotalFromTax();
    } else {
      this.calculateWithholdingFromDetail(wt);
      this.calculateTotalFromDetail();
    }
    this.details.forEach((detail, i) => {
      this.details[i] = this.calculateDetail(detail);
    });
    return this;
  }

  isWithholdingFarmer() {
    return this.withholdingFarmer  && this.withholdingFarmer != CONSTANT.FALSE;;
  }

  setWithholdingType(withholdingType) {
    if(this.isNacional() && !this.isExempt()) {
      this.calculateWithholdingFromTax(withholdingType);
      this.calculateTotalFromTax();
    } else {
      this.calculateWithholdingFromDetail(withholdingType);
      this.calculateTotalFromDetail();
    }
    return this;
  }

  setWithholdingFarmer(withholdingFarmer) {
    this.withholdingFarmer = withholdingFarmer;
    this.calculateWithholdingFromTax();
    this.calculateTotalFromTax();
    return this;
  }

  getWorkplace () {
    return this.workplace;
  }

  setWorkplace (workplace) {
    this.workplace = workplace;
    this.details.forEach((detail, i) => {
      this.details[i].workplace = workplace;
    });
    return this;
  }

  getTransaction() {

  }

  setTransaction(transaction) {
    this.transaction = transaction;
    if(!this.isVatEnabled()){
      this.surcharge = false;
      let auxTaxes = [];
      if(this.isCcm())
        auxTaxes = this.taxes.filter(f => TaxType.IRPF === f.tax);
      else {
        this.withholding = false;
        this.withholdingFarmer = false;
      }        
      
      auxTaxes.push({
        tax:TaxType.IVA,
        type: TaxType.IVA,
        percentage: 0.0,
        quota:0.0,
        base: this.total,
        surcharge: 0.0,
        surcharge_quota: 0.0
      });
      this.taxes = auxTaxes;
      
      this.details.forEach((detail,i) => {
        detail.percentage = 0.0;
        this.setDetail(detail, i);
      });
    }
    return this;
  }

  getRegistry() {
    return this.isEmitida() ? this.receiver : this.sender;
  }

  setRegistry(registry) {
    if(this.isEmitida()) {
      this.receiver = registry;
    } else this.sender = registry;
    this.name = registry.name;
    return this;
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

  getExpDate() {
    if(!this.expDate) return undefined;
    return new Date(this.expDate);
  }

  getExpDateStr() {
    if(!this.expDate) return '';
    let date = new Date(this.expDate);
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
     return this;
  }

  setTax(tax, i) {
    this.taxes[i] = this.calculateTax(tax);
    this.calculateWithholdingFromTax();
    this.calculateTotal();
    return this;
  }

  deleteTax(tax, i) {
    if(TaxType.IRPF === tax.type)
      this.withholding = false;
    this.taxes.splice(i, 1);
    this.calculateWithholdingFromTax();
    this.calculateTotalFromTax();
    return this;
  }

  calculateWithholdingFromDetail(withholdingType) {
    if(this.isWithholding() && this.taxes.filter(f => TaxType.IRPF === f.tax).length === 0) {
      let percentage = withholdingType ? withholdingType.percentage
        : (this.isWithholdingFarmer() ? 2.0 : 15.0);

      let wt = withholdingType ? withholdingType.id
        : (this.isWithholdingFarmer() ? "FARMER" : "PROFESSIONAL");

      let tax = {
        tax: TaxType.IRPF,
        type: this.isWithholdingFarmer() ? TaxType.IRPF_AGRI : TaxType.IRPF_PROF,
        percentage: percentage,
        base: 0.0,
        quota: 0.0,
        surcharge: 0.0,
        surcharge_quota: 0.0,
        withholding_type: wt
       };
       this.taxes.push(tax);
    }
    if(this.isWithholding()) { 
      let base = 0.0;
      this.details.forEach((detail, i) => {
        if(!detail.prepayment){ 
          base = base + detail.amount;
        }
      });

      this.taxes.forEach((tax, i) => {
       if(TaxType.IRPF === tax.tax){
          withholdingType = withholdingType ||  WithholdingType.find(v => v.id == tax.withholding_type);
          let percentage = withholdingType ? withholdingType.percentage
            : (this.isWithholdingFarmer() ? 2.0 : 15.0);

          let wt = withholdingType ? withholdingType.id
            : (this.isWithholdingFarmer() ? CONSTANT.FARMER : CONSTANT.PROFESSIONAL);
          tax.type = this.isWithholdingFarmer() 
            ? TaxType.IRPF_AGRI : (TaxType.IRPF_AGRI === tax.type ? TaxType.IRPF_PROF : tax.type);
         tax.percentage = percentage;
         tax.base = base;
         tax.withholding_type = wt;
         this.taxes[i] = this.calculateTax(tax); 
         this.details.forEach((d, i) => {
          if(!d.prepayment) {
            d.withholding = true;
            d.withholding_type = tax.withholding_type;
            d.withholding_percentage = tax.percentage;
            d.withholding_base = this.isWithholdingFarmer() ? d.amount + d.quota : d.amount;
            d.withholding_quota = round(d.withholding_base / 100 * tax.percentage);
            this.details[i] = d;
          }
        });
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

  calculateWithholdingFromTax(withholdingType) {
    if(this.isWithholding() && this.taxes.filter(f => TaxType.IRPF === f.tax).length === 0) {
      let percentage = withholdingType ? withholdingType.percentage
        : (this.isWithholdingFarmer() ? 2.0 : 15.0);

      let wt = withholdingType ? withholdingType.id
        : (this.isWithholdingFarmer() ? "FARMER" : "PROFESSIONAL");

      let tax = {
        tax: TaxType.IRPF,
        type: TaxType.IRPF,
        percentage: percentage,
        base: 0.0,
        quota: 0.0,
        surcharge: 0.0,
        surcharge_quota: 0.0,
        withholding_type: wt
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
        if(TaxType.IRPF === tax.tax) {
          withholdingType = withholdingType || WithholdingType.find(v => v.id == tax.withholding_type);
          let percentage = withholdingType ? withholdingType.percentage
            : (this.isWithholdingFarmer() ? 2.0 : 15.0);
  
          let wt = withholdingType ? withholdingType.id
            : (this.isWithholdingFarmer() ? CONSTANT.FARMER : CONSTANT.PROFESSIONAL);
      
          tax.type = TaxType.IRPF;
          tax.percentage = percentage;
          tax.base = base;
          tax.withholding_type = wt;
          this.taxes[i] = this.calculateTax(tax); 
          this.details.forEach((d, i) => {
            if(!d.prepayment) {
              d.withholding = true;
              d.withholding_type = tax.withholding_type;
              d.withholding_percentage = tax.percentage;
              d.withholding_base = this.isWithholdingFarmer() ? d.amount + d.quota : d.amount;
              d.withholding_quota = round(d.withholding_base / 100 * tax.percentage);
              this.details[i] = d;
            }
          });
        }
      })
    } else if(this.taxes.filter(f => TaxType.IRPF === f.tax).length > 0){
      let index;
      this.taxes.forEach((tax, i) => {
        if(TaxType.IRPF === tax.tax){
          index = i;
        }
      });
      this.taxes.splice(index, 1);
      this.details.forEach((d, i) => {
        if(!d.prepayment) {
          d.withholding = false;
          d.withholding_type = undefined;
          d.withholding_percentage = 0.0;
          d.withholding_base = 0.0;
          d.withholding_quota = 0.0;
          this.details[i] = d;
        }
      });
    }     
  }

  calculateTotal() {
    if(this.details && this.details.length >= 1) {
      this.calculateTotalFromDetail();
    } else this.calculateTotalFromTax();
  }

  calculateTotalFromTax() {
    let total = 0.0;
    let taxableBase = 0.0;
    let vatQuota = 0.0;
    let retentionQuota = 0.0;

    this.taxes.filter(f => TaxType.IVA === f.tax).forEach(tax => {
      const value = this.isVatCalculate()
        ? round(Number(tax.base) + Number(tax.quota) + Number(tax.surcharge_quota))
        : round(Number(tax.base));
      total = total + value;
      taxableBase = taxableBase + Number(tax.base);
      vatQuota = vatQuota + Number(tax.quota) + Number(tax.surcharge_quota);
    });

    this.taxes.filter(f => TaxType.IRPF === f.tax).forEach(tax => {
      total = total - Number(tax.quota);
      retentionQuota = retentionQuota + Number(tax.quota);
    });
    
    this.total = round(Number(total));
    this.taxableBase = round(Number(taxableBase));
    this.vatQuota = round(Number(vatQuota));
    this.retentionQuota = round(Number(retentionQuota));
    this.calculateFinances();
  }

  calculateTax(tax) {
    tax.quota = round(tax.base / 100 * tax.percentage);
    if(TaxType.IVA === tax.tax) {
      tax.type = this.isSurcharge() ? TaxType.IVA_RE : TaxType.IVA;
      tax.surcharge = this.isSurcharge() ? getSurchargeByVat(tax.percentage) : 0.0;
      tax.surcharge_quota = round(tax.base / 100 * tax.surcharge);
    }
    return tax;
  }

  calculateTaxFromTotal() {
    let withholdingPercentage = 0.0;
    let withholdingType = this.isWithholdingFarmer() ? "FARMER" : "PROFESSIONAL";
    if(this.isWithholding() && this.taxes.filter(f => TaxType.IRPF === f.tax).length > 0){
      let withholdingTax = this.taxes.filter(f => TaxType.IRPF === f.tax)[0];
      withholdingPercentage = withholdingTax.percentage;
      withholdingType = withholdingTax.withholding_type;
    }
    let base = 0.0;
    let quota = 0.0;
    if(this.isEmitida() && (!this.isNacional() || this.isExempt())){
      base = this.total;
      if(this.isWithholding() && withholdingPercentage > 0){
        base = this.total / (1 -  withholdingPercentage/100);
      }
      this.taxes = [{
          tax:TaxType.IVA,
          type: TaxType.IVA,
          percentage: 0.0,
          quota:0.0,
          base: base,
          surcharge: 0.0,
          surcharge_quota: 0.0
      }];
    } else if (this.taxes.filter(f => TaxType.IVA === f.tax).length === 0) {
      this.taxes = [];
      let div = this.isSurcharge() ? 1.262 : 1.21;
      if(this.isWithholdingFarmer() && withholdingPercentage > 0){
        div = div - (div * withholdingPercentage/100);
      } else if(this.isWithholding() && withholdingPercentage > 0){
        div = div - (withholdingPercentage/100);
      }
      base = round(Number(this.total) / div, 4);
      quota = round(base * 0.21)
			let tax = {
				tax: TaxType.IVA,
				type: this.isSurcharge() ? TaxType.IVA_RE : TaxType.IVA,
				percentage: 21.0,
				base: round(base),
				quota: round(base * 0.21),
        surcharge: this.isSurcharge() ? 5.2 : 0.0,
        surcharge_quota: this.isSurcharge() ? round(base * 0.052) : 0.0
		 	};
			this.taxes.push(tax);
		} else if(this.taxes.filter(f => TaxType.IVA === f.tax).length === 1){
			let tax = this.taxes.filter(f => TaxType.IVA === f.tax)[0];
      this.taxes=[];
      const p = (this.isSurcharge() ? tax.percentage + getSurchargeByVat(tax.percentage) : tax.percentage) / 100;
      let p0 = p + 1;
      if(this.isWithholdingFarmer() && withholdingPercentage > 0){
        p0 = p0 - (p0 * withholdingPercentage/100);
      } else if(this.isWithholding() && withholdingPercentage > 0){
        p0 = p0 - (withholdingPercentage/100);
      }
      base = round(Number(this.total) / p0, 4);
      quota =  round(base / 100 * tax.percentage);
      tax.base = round(base);
			tax.quota = round(tax.base / 100 * tax.percentage);
			tax.surcharge = this.isSurcharge() ? getSurchargeByVat(tax.percentage) : 0.0;
      tax.surcharge_quota = round(base / 100 * tax.surcharge);
      this.taxes.push(tax);
		}

    if(this.isWithholding() && withholdingPercentage > 0) {
      let wBase = this.isWithholdingFarmer() ? base + quota : base;
      let wQuota = round(wBase * (withholdingPercentage/100));
      let irpf = {
				tax: TaxType.IRPF,
				type: TaxType.IRPF,
        withholding_type: withholdingType,
				percentage: withholdingPercentage,
				base: wBase,
				quota: wQuota
		 	};
      this.taxes.push(irpf);
      this.retentionQuota = wQuota;
    }
    this.taxableBase = base;
    this.vatQuota = quota;
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
      percentage: this.isNacional() && !this.isExempt() ? 21.0 : 0.0, 
      quota: 0.0,
      surcharge: this.isSurcharge() ? 5.2 : 0.0, 
      surcharge_quota: 0.0,
      withholding: this.isWithholding(),
      withholding_type: this.isWithholding() ? wh.type : undefined,
      withholding_percentage: this.isWithholding() ? wh.percentage : undefined,
      withholding_base: 0.0,
      withholding_quota: 0.0, 
      workplace: this.workplace
     };
     this.details.push(detail);
     this.calculateTaxFromDetail();
     if(this.isCcm()) this.calculateWithholdingFromDetail();
     this.calculateTotalFromDetail();
      return this;
  }

  deleteDetail(detail, i) {
    this.details.splice(i, 1);
    this.calculateTaxFromDetail();
    if(this.isCcm()) this.calculateWithholdingFromDetail();
    this.calculateTotalFromDetail();
    return this;
  }

  setDetail(detail, i) {
    this.details[i] = this.calculateDetail(detail);
    this.calculateTaxFromDetail();
    if(this.isCcm()) this.calculateWithholdingFromDetail();
    this.calculateTotalFromDetail();
    return this;
  }

  calculateDetail(detail){
      let amount = round(Number(detail.quantity) * Number(detail.price));
			amount = amount - amount * (detail.discount / 100);
      detail.amount = round(amount);
      detail.percentage = detail.percentage || 0.0;
      if(!detail.prepayment) {
        detail.quota = round(detail.amount / 100 * detail.percentage);
        detail.surcharge = this.isSurcharge() ? getSurchargeByVat(detail.percentage) : 0.0;
        detail.surcharge_quota = round(detail.amount / 100 * detail.surcharge);
        if(this.isSurcharge()) {
          detail.vatDeductiblePercent = 0.0;
          detail.vatDeductibleQuota = 0.0;
        } else {
          detail.vatDeductiblePercent = detail.vatDeductiblePercent || 100.0;
          detail.vatDeductibleQuota = round(detail.quota * detail.vatDeductiblePercent / 100);
        }
        if(this.isWithholdingFarmer()) {
          detail.withholding = true;
          detail.withholding_type = "FARMER";
          detail.withholding_percentage = 2.0;
          detail.withholding_base = detail.amount + detail.quota;
          detail.withholding_quota = round(detail.withholding_base / 100 * 2.0);
        } else if(this.isWithholding()) {
          let wh = this.getWitholdingTax();
          detail.withholding = true;
          detail.withholding_type = wh.type;
          detail.withholding_percentage = wh.percentage;
          detail.withholding_base = detail.amount;
          detail.withholding_quota = round(detail.withholding_base / 100 * wh.percentage);
        }
      } else {
        detail.percentage = undefined;
        detail.quota = 0.0;
        detail.surcharge = 0.0;
        detail.surcharge_quota = 0.0;
        detail.vatDeductiblePercent = 0.0;
        detail.withholding = false;
        detail.withholding_type = undefined;
        detail.withholding_percentage = undefined;
        detail.withholding_base = 0.0;
        detail.withholding_quota = 0.0;
      }
      return detail;
  }

  calculateTaxFromDetail() {
    this.taxes = this.isWithholding() ? [this.getWitholdingTax()] : [];
    this.details.forEach( (detail, i) => {
      detail = this.calculateDetail(detail);
      if(!detail.prepayment || detail.prepayment === CONSTANT.FALSE) {
        if(this.taxes.filter(f => f.percentage == detail.percentage).length > 0) {
            this.taxes.forEach((tax, i) => {
            if(tax.percentage == detail.percentage){
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
    if(this.isNacional() || this.isCcm())   
      this.calculateWithholdingFromTax();
  }
  
  calculateTotalFromDetail() {
    let total = 0.0;
    let taxableBase = 0.0;
    let vatQuota = 0.0;
    let retentionQuota = 0.0;

    this.details.forEach( detail => {
      const value = this.isVatCalculate()
        ? round(Number(detail.amount) + Number(detail.quota) + Number(detail.surcharge_quota))
        : round(Number(detail.amount));
      total = total + value;
      if(!detail.prepayment || detail.prepayment === CONSTANT.FALSE) {
        taxableBase = taxableBase + Number(detail.amount);
        vatQuota = vatQuota + Number(detail.quota) + Number(detail.surcharge_quota);
      }
    });  

    this.taxes.filter(f => TaxType.IRPF === f.tax).forEach(tax => {
      total = total - Number(tax.quota);
      retentionQuota = retentionQuota + Number(tax.quota);
    });

    this.total = round(Number(total));

    this.taxableBase = round(Number(taxableBase));
    this.vatQuota = round(Number(vatQuota));
    this.retentionQuota = round(Number(retentionQuota));

    this.calculateFinances();
  }

  setBankAccount(bankAccount) {
    this.finances.forEach((finance,i) => {
      this.finances[i].bank_account = bankAccount;
    });
    return this;
  }

  resetFinances() {

    let finance = {
          due_date: this.date,
          paymethod: this.paymethod,
          amount: this.total,
          iban: ''
        };

    if(this.finances.length > 0) {
        finance.paymethod = this.finances[0].paymethod;
        finance.iban = this.finances[0].iban;
    }

    this.finances = [finance];
  }

  calculateFinances() {
    if(this.finances.length === 0) {
      if(!this.paymethod) this.paymethod = 'CASH';
      let finance = {
          due_date: this.date,
          paymethod: this.paymethod,
          amount: this.total,
          iban: ''
      };		
      this.finances.push(finance);
    } else if(this.finances.length === 1) {
      this.finances[0].paymethod = this.finances[0].paymethod || this.paymethod;
      this.finances[0].amount = this.total; 
    } else if(this.finances.length > 1) {
      let financeTotal = 0.0;
      this.finances.forEach((finance, i) => {
        finance.paymethod = finance.paymethod || this.paymethod;
        this.finances[i] = finance;
        financeTotal = financeTotal + Number(finance.amount);
      });
      this.total = round(Number(this.total));
      financeTotal = round(Number(financeTotal));
      if(this.total != financeTotal) {
        let bankAccount = this.finances[0].bank_account;
        let finance = {
          due_date: this.date,
          paymethod: this.paymethod,
          bank_account: bankAccount,
          amount: Number(this.total) - Number(financeTotal)
        };
        this.finances.push(finance);
      }
    }
  }
  
  addFinance() {
    if(!this.paymethod) this.paymethod = 'CASH';
    
    if(this.finances.length > 0) {
      let financeTotal = 0.0;
      this.finances.forEach((finance, i) => {
        finance.paymethod = this.paymethod;
        this.finances[i] = finance;
        financeTotal = financeTotal + Number(finance.amount);
      });
       let finance = {
         due_date: this.date,
         paymethod: this.paymethod,
         bank_account: this.finances[0].bank_account,
         amount: Number(this.total) - Number(financeTotal)
       };
       this.finances.push(finance);
    } else {
      let finance = {
        due_date: this.date,
        paymethod: this.paymethod,
        amount: this.total,
        iban: ''
      };
      this.finances.push(finance);
    }
    return this;
  }

  setFinance(finance, i) {
    this.finances[i] = finance;
    return this;
  }

  deleteFinance(finance, i) {
    this.finances.splice(i, 1);
    return this;
  }

}

export const getDocumentNumber = (invoice) => {
  let documentNumber = "";
  if (invoice.type) {
    if (invoice.type == "emitida") {
      documentNumber = "E-";
    } else if (invoice.type == "recibida") {
      documentNumber = "R-";
    } else if (invoice.type == "ticket") {
      documentNumber = "G-";
    } else {
      documentNumber = "?-";
    }
  } else {
    documentNumber = "?-";
  }
  
  if (invoice.series) {
    documentNumber += invoice.series + "/";
  }
  if (invoice.number) {
    let n = "" + invoice.number;
    documentNumber += n.zeros(6);
  } else {
    documentNumber += "??????";
  }
  return documentNumber;
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