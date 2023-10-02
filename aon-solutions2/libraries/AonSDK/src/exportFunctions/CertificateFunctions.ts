import { CertificateFactory } from "../factorys/CertificateFactory";
import { ICertificate } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class CertificateFunctions {
    private static singleObjectCrud = new CertificateFactory().createSingleObjectCrud();
    private static multipleObjectCrud = new CertificateFactory().createMultipleObjectCrud();

    static async getCertificateList(filter?: IFilter): Promise<IResponse<ICollection<ICertificate>>> {
        return (await this.multipleObjectCrud.getCollection(filter))
    }

    static async getCertificate(pkey: any): Promise<IResponse<ICertificate>> {
        return (await this.singleObjectCrud.getElement(pkey))
    }

    static async createCertificate(certificate: ICertificate): Promise<IResponse<ICertificate>> {
        return (await this.singleObjectCrud.createElement(certificate))
    }

    static async deleteCertificate(pkey: any): Promise<IResponse<boolean>> {
        return (await this.singleObjectCrud.deleteElement(pkey))
    }

    static async updateCertificate(certificate: ICertificate): Promise<IResponse<ICertificate>> {
        return (await this.singleObjectCrud.updateElement(certificate))
    }
}