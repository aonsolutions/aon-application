import { Injectable } from '@angular/core';
import { Factory, ICollection, IFilter, IIRPF, IInvoice, IInvoiceActivity, IInvoiceCategory, IInvoiceSerie, IInvoiceTransactionType, IPaymentMethod, ITax, InvoiceFactory } from 'libraries/AonSDK/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService extends CommonService {

  invoiceFactory: InvoiceFactory;

  constructor(){
    super();
    this.invoiceFactory = new InvoiceFactory();
  }

  async getInvoice(key: string): Promise<IInvoice> {
    return (await this.invoiceFactory.createSingleObjectCrud().getElement(key)).result;
  }

  async getInvoiceList(filter?: IFilter): Promise<ICollection<IInvoice>> {
    return (await this.invoiceFactory.createMultipleObjectCrud().getCollection(filter)).result;
  }

  async createInvoice(invoice: IInvoice): Promise<IInvoice> {
    return (await this.invoiceFactory.createSingleObjectCrud().createElement(invoice)).result;
  }

  async updateInvoice(invoice: IInvoice): Promise<IInvoice> {
    return (await this.invoiceFactory.createSingleObjectCrud().updateElement(invoice)).result;
  }

  async deleteInvoice(key: string): Promise<boolean> {
    return (await this.invoiceFactory.createSingleObjectCrud().deleteElement(key)).result;
  }

  async getTaxList(filter?: IFilter): Promise<ICollection<ITax>> {
    return (await this.invoiceFactory.createSpecificMethods().getTaxList(filter)).result;
  }

  async getIRPFList(filter?: IFilter): Promise<ICollection<IIRPF>> {
    return (await this.invoiceFactory.createSpecificMethods().getIRPFList(filter)).result;
  }

  async getInvoiceCategoryList(filter?: IFilter): Promise<ICollection<IInvoiceCategory>> {
    return (await this.invoiceFactory.createSpecificMethods().getInvoiceCategoryList(filter)).result;
  }

  async getInvoiceSerieList(filter?: IFilter): Promise<ICollection<IInvoiceSerie>> {
    return (await this.invoiceFactory.createSpecificMethods().getInvoiceSerieList(filter)).result;
  }

  async getTransactionTypeList(filter?: IFilter): Promise<ICollection<IInvoiceTransactionType>> {
    return (await this.invoiceFactory.createSpecificMethods().getTransactionTypeList(filter)).result;
  }

  async getPaymentMethodList(filter?: IFilter): Promise<ICollection<IPaymentMethod>> {
    return (await this.invoiceFactory.createSpecificMethods().getPaymentMethodList(filter)).result;
  }

  async getInvoiceActivityList(filter?: IFilter): Promise<ICollection<IInvoiceActivity>> {
    return (await this.invoiceFactory.createSpecificMethods().getInvoiceActivityList(filter)).result;
  }

}
