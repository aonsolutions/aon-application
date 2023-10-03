import { ITax, IIRPF, IInvoiceCategory, IInvoiceSerie, IInvoiceTransactionType } from "../interfaces/modelsInterfaces";
import { IInvoiceSpecificMethods } from "../interfaces/serviceInterfaces";
import { IFilter, IResponse, ICollection } from "../interfaces/utilitiesInterfaces";
import { LocalInvoiceSpecificMethodsRepository } from "../repositorys/InvoiceRepository";
import { ErrorResponse } from "../utils/Response";
import { Response } from "../utils/Response";

export class InvoiceSpecificMethods implements IInvoiceSpecificMethods {
    protected SpecificMethodsRepository: LocalInvoiceSpecificMethodsRepository;

    constructor(SpecificMethodsRepository: LocalInvoiceSpecificMethodsRepository){
        this.SpecificMethodsRepository = SpecificMethodsRepository;
    }

    async getTaxList(filter?: IFilter | undefined): Promise<IResponse<ICollection<ITax>>> {
        try {
            return new Response<ICollection<ITax>>(this.SpecificMethodsRepository.getTaxList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getIRPFList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IIRPF>>> {
        try {
            return new Response<ICollection<IIRPF>>(this.SpecificMethodsRepository.getIRPFList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getInvoiceCategoryList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IInvoiceCategory>>> {
        try {
            return new Response<ICollection<IInvoiceCategory>>(this.SpecificMethodsRepository.getInvoiceCategoryList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getInvoiceSerieList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IInvoiceSerie>>> {
        try {
            return new Response<ICollection<IInvoiceSerie>>(this.SpecificMethodsRepository.getInvoiceSerieList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getTransactionTypeList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IInvoiceTransactionType>>> {
        try {
            return new Response<ICollection<IInvoiceTransactionType>>(this.SpecificMethodsRepository.getTransactionTypeList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getPaymentMethodList(filter?: IFilter | undefined): Promise<IResponse<ICollection<ITax>>> {
        try {
            return new Response<ICollection<ITax>>(this.SpecificMethodsRepository.getPaymentMethodList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getInvoiceActivityList(filter?: IFilter | undefined): Promise<IResponse<ICollection<ITax>>> {
        try {
            return new Response<ICollection<ITax>>(this.SpecificMethodsRepository.getInvoiceActivityList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }
}