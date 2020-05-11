import { TaxType, Invoice, InvoiceTransaction, InvoiceStatus, Registry, Address,
  InvoiceTax, PayMethod, Finance, InvoiceDetail, PrinterConfiguration,
  Company, User, Permission, PermissionTagType} from './AonModel';
import { TediUtils } from '../utils/tedi-utils';

export class AonMaker {

  static createInvoice(invoice ?: Invoice): Invoice {
    return {
      id: invoice ? invoice.id : undefined,
      domain: invoice ? invoice.domain : '',
      series: invoice && invoice.series ? invoice.series : '',
      number: invoice && invoice.number ? invoice.number : 0,
      reference: invoice && invoice.reference ? invoice.reference : '',
      source: invoice ? invoice.source : '',
      date: invoice ? invoice.date : new Date(),
      total: invoice && invoice.total ? TediUtils.round(invoice.total) : 0,
      type: invoice ? invoice.type: undefined,
      category: invoice ? invoice.category : undefined,
      transaction: invoice && invoice.transaction ? invoice.transaction : InvoiceTransaction.NATIONAL,
      status: invoice && invoice.status ? invoice.status : InvoiceStatus.PENDING,
      verified: invoice && invoice.verified ? invoice.verified : false,
      oldStatus: invoice ? invoice.oldStatus : undefined,
      sender: invoice && invoice.sender ? AonMaker.createRegistry(invoice.sender) : AonMaker.createRegistry(),
      receiver: invoice && invoice.receiver ? AonMaker.createRegistry(invoice.receiver) : AonMaker.createRegistry(),
      taxes: invoice && invoice.taxes ? invoice.taxes.map((t: InvoiceTax) => AonMaker.createInvoiceTax(t)) : [],
      details: invoice && invoice.details ? invoice.details.map((d: InvoiceDetail) => AonMaker.createInvoiceDetail(d)) : [],
      finances: invoice && invoice.finances ? invoice.finances.map((f: Finance) => AonMaker.createFinance(f)) : [],
      file: invoice ? invoice.file : undefined,
      comments: invoice && invoice.comments ? invoice.comments : [],
      create_user: invoice && invoice.create_user ? invoice.create_user : '',
      create_date: invoice && invoice.create_date ? invoice.create_date : new Date()
    };
  }

  static createInvoiceTax(tax?:InvoiceTax) : InvoiceTax {
    return {
      id: tax && tax.id ? tax.id : undefined,
      tax: tax && tax.tax ? tax.tax : TaxType.VAT,
      base: tax && tax.base ? tax.base : 0,
      percentage: tax && tax.percentage ? tax.percentage : 0,
      quota: tax && tax.quota ? tax.quota : 0,
      surcharge: tax && tax.surcharge ? tax.surcharge : 0,
      surcharge_quota: tax && tax.surcharge_quota ? tax.surcharge_quota : 0
    }
  }

  static createInvoiceDetail(detail?:InvoiceDetail) : InvoiceDetail {
    let d : InvoiceDetail = {
      id: detail && detail.id ? detail.id : undefined,
      description: detail && detail.description ? detail.description : '',
      quantity: detail && detail.quantity ? detail.quantity : 0,
      price: detail && detail.price ? detail.price : 0,
      discount: detail && detail.discount ? detail.discount : 0,
      amount: detail && detail.amount ? detail.amount : 0,
      vat: detail && detail.vat ? detail.vat : 0,
      surcharge: detail && detail.surcharge ? detail.surcharge : 0,
      account: detail && detail.account ? detail.account : undefined,
      withholding: detail && detail.withholding ? detail.withholding : false,
      prepayment: detail && detail.prepayment ? detail.prepayment : false
    };
    d.amount = detail && detail.amount ? detail.amount : (
      (d.quantity * d.price) * ((100 - d.discount) / 100)
    );
    return d;
  }

  static createFinance(finance ?: Finance) : Finance {
    return {
      due_date: finance && finance.due_date ? finance.due_date : new Date(),
      pay_method: finance && finance.pay_method? finance.pay_method : PayMethod.CASH_BASIS,
      amount: finance && finance.amount ? TediUtils.round(finance.amount) : 0,
      iban: finance && finance.iban ? finance.iban: ''
    };
  }

  static createRegistry(registry ?: Registry): Registry {
    return {
      id: registry && registry.id ? registry.id : undefined,
      document: registry && registry.document ? registry.document : '',
      document_country: registry && registry.document_country ? registry.document_country : '',
      name: registry && registry.name ? registry.name : '',
      address: registry && registry.address ? AonMaker.createAddress(registry.address) : AonMaker.createAddress()
    };
  }

  static createAddress(address ?: Address) : Address {
    return {
      id: address && address.id ? address.id : undefined,
      country: address && address.country ? address.country : 'ES',
      address: address && address.address ? address.address : '',
      postal_code: address && address.postal_code ? address.postal_code : '',
      city: address && address.city ? address.city : '',
      province: address && address.province ? address.province : ''
    };
  }

  static getFullAddress(address: Address) : string {
    return `${address.address}, ${address.postal_code} ${address.city}, ${address.province}, ${address.country}`;
  }

  static createPrinterConfiguration(pc?: PrinterConfiguration) : PrinterConfiguration {
    return {
      header: pc && pc.header ? pc.header : 100,
      footer: pc && pc.footer ? pc.footer : 100,
      adjustment: pc && pc.adjustment ? pc.adjustment : false,
      detailed: pc && pc.detailed ? pc.detailed : false
    };
  }

  static createCompany(company?: Company) : Company {
    return {
      id: company && company.id ? company.id : undefined,
      domain: company && company.domain ? company.domain : undefined,
      document: company && company.document ? company.document : '',
      active: company && company.active ? company.active: true,
      alias: company && company.alias ? company.alias : '',
      name: company && company.name ? company.name : '',
      address: company && company.address ? company.address : AonMaker.createAddress(),
      users: company && company.users ? company.users : [],
      iban: company && company.iban ? company.iban : '',
      bic: company && company.bic ? company.bic : '',
      printer_configuration: company && company.printer_configuration
        ? AonMaker.createPrinterConfiguration(company.printer_configuration)
        : AonMaker.createPrinterConfiguration()
    };
  }

  static createUser(user?: User) : User {
    return {
      email: user && user.email ? user.email : '',
      phone: user && user.phone ? user.phone : '',
      name: user && user.name ? user.name : '',
      surname: user && user.surname ? user.surname : '',
      document: user && user.document? user.document : '',
      permissions: user && user.permissions ? user.permissions: AonMaker.createPermissions(),
      users: user && user.users ? user.users : [],
      password: user && user.password ? user.password : undefined,
      actual_company: user && user.actual_company ? user.actual_company : undefined,
      company: user && user.company ? user.company : undefined,
      admin: user && user.admin ? user.admin : false,
      gestor: user && user.gestor ? user.gestor : false,
      root: user && user.root ? user.root: false
    };
  }

  static createPermissions() : Permission[] {
    return [{
        name: 'Gestión de Facturas Emitidas',
        tag: PermissionTagType.ISSUED,
        reader: true,
        writer: true
      }, {
        name: 'Gestión de Facturas Recibidas',
        tag: PermissionTagType.RECEIVED,
        reader: true,
        writer: true
      }, {
        name: 'Gestión de Facturas Tickets',
        tag: PermissionTagType.TICKET,
        reader: true,
        writer: true
      }];
  }
}
