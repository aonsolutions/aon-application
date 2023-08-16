import { Injectable } from '@angular/core';
import { CertificateFactory, ICertificate, ICollection, IFilter } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  private singleObjectCrud = new CertificateFactory().createSingleObjectCrud();
  private multipleObjectCrud = new CertificateFactory().createMultipleObjectCrud();

  constructor() { }

  async getCertificateList(filter?: IFilter): Promise<ICollection<ICertificate>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getCertificate(pkey: any): Promise<ICertificate> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async createCertificate(certificate: ICertificate): Promise<ICertificate> {
    return (await this.singleObjectCrud.createElement(certificate)).result;
  }

  async deleteCertificate(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

  async updateCertificate(certificate: ICertificate): Promise<ICertificate> {
    return (await this.singleObjectCrud.updateElement(certificate)).result;
  }

  async removeCertificate(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }
}


