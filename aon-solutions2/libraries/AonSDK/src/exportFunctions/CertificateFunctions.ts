import { CertificateFactory } from "../factorys/CertificateFactory";
import { ICertificate } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class CertificateFunctions {
    private singleObjectCrud = new CertificateFactory().createSingleObjectCrud();
    private multipleObjectCrud = new CertificateFactory().createMultipleObjectCrud();

    async getCertificateList(filter?: IFilter): Promise<IResponse<ICollection<ICertificate>>> {
    return (await this.multipleObjectCrud.getCollection(filter))
    }

    async getCertificate(pkey: any): Promise<IResponse<ICertificate>> {
    return (await this.singleObjectCrud.getElement(pkey))
    }

    async createCertificate(certificate: ICertificate): Promise<IResponse<ICertificate>> {
    return (await this.singleObjectCrud.createElement(certificate))
    }

    async deleteCertificate(pkey: any): Promise<IResponse<boolean>> {
    return (await this.singleObjectCrud.deleteElement(pkey))
    }

    async updateCertificate(certificate: ICertificate): Promise<IResponse<ICertificate>> {
    return (await this.singleObjectCrud.updateElement(certificate))
    }
}