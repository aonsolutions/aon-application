import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { ICertificate } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { Certificate, ApiCertificate, StorableCertificate } from "../models/Certificate";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/Environment";

export class CertificateFactory implements ISingleObjectCrudFactory<ICertificate>, IMultipleObjectCrudFactory<ICertificate> {
    createSingleObjectCrud(): ISingleObjectCrud<ICertificate> {
        return new GenericSingleObjectCrud<Certificate>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<Certificate>(new ApiCertificate(), Certificate) :
            new GenericSingleObjectCrudRepository<Certificate>(new StorableCertificate(), Certificate)
            ),
            Certificate);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<ICertificate> {
        return new GenericMultipleObjectCrud<Certificate>(
            (APIEnvironment ?
            new APIGenericMultipleObjectCrudRepository<Certificate>(new ApiCertificate(), Certificate) :
            new GenericMultipleObjectCrudRepository<Certificate>(new StorableCertificate(), Certificate)
            ),
            Certificate);
    }
}