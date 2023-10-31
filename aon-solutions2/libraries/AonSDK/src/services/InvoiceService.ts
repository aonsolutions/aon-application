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

    /**
     * Obtiene una lista de impuestos.
     *
     * @param {IFilter} filter - El filtro a aplicar a la lista de impuestos. Opcional.
     * @return {Promise<IResponse<ICollection<ITax>>>} Una promesa que se resuelve con la respuesta que contiene la lista de impuestos.
     */
    async getTaxList(filter?: IFilter | undefined): Promise<IResponse<ICollection<ITax>>> {
        try {
            return new Response<ICollection<ITax>>(this.SpecificMethodsRepository.getTaxList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Tax');
        }
    }

    /**
     * Obtiene una lista de elementos de IRPF.
     *
     * @param {IFilter} filter - Filtro opcional para aplicar a la lista
     * @return {Promise<IResponse<ICollection<IIRPF>>>} Una promesa que se resuelve en la respuesta que contiene la lista de elementos de IRPF
     */
    async getIRPFList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IIRPF>>> {
        try {
            return new Response<ICollection<IIRPF>>(this.SpecificMethodsRepository.getIRPFList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'IRPF');
        }
    }

    /**
     * Obtiene la lista de categorías de facturas.
     *
     * @param {IFilter} filter - Un filtro opcional para aplicar a la lista.
     * @returns {Promise<IResponse<ICollection<IInvoiceCategory>>>} Una promesa que se resuelve con la respuesta que contiene la lista de categorías de facturas.
     */
    async getInvoiceCategoryList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IInvoiceCategory>>> {
        try {
            return new Response<ICollection<IInvoiceCategory>>(this.SpecificMethodsRepository.getInvoiceCategoryList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Invoice Category');
        }
    }

    /**
     * Recupera una lista de series de facturas.
     *
     * @param {IFilter} filter - Filtro opcional para aplicar a la lista
     * @returns {Promise<IResponse<ICollection<IInvoiceSerie>>>} Una promesa que se resuelve con una respuesta que contiene la lista de series de facturas
     */
    async getInvoiceSerieList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IInvoiceSerie>>> {
        try {
            return new Response<ICollection<IInvoiceSerie>>(this.SpecificMethodsRepository.getInvoiceSerieList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Invoice Serie');
        }
    }

    /**
     * Recupera la lista de tipos de transacción.
     *
     * @param {IFilter} [filter] - Filtro opcional para aplicar a los tipos de transacción.
     * @returns {Promise<IResponse<ICollection<IInvoiceTransactionType>>>} Una promesa que se resuelve en la respuesta que contiene la colección de tipos de transacción.
     */
    async getTransactionTypeList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IInvoiceTransactionType>>> {
        try {
            return new Response<ICollection<IInvoiceTransactionType>>(this.SpecificMethodsRepository.getTransactionTypeList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Transaction Type');
        }
    }

    /**
     * Recupera una lista de métodos de pago.
     *
     * @param {IFilter} filter - (opcional) Un objeto que especifica los criterios de filtro.
     * @returns {Promise<IResponse<ICollection<ITax>>>} Una promesa que se resuelve con una respuesta que contiene una colección de objetos de impuestos.
     */
    async getPaymentMethodList(filter?: IFilter | undefined): Promise<IResponse<ICollection<ITax>>> {
        try {
            return new Response<ICollection<ITax>>(this.SpecificMethodsRepository.getPaymentMethodList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Payment Method');
        }
    }

    /**
     * Obtiene la lista de actividades de facturación.
     *
     * @param {IFilter | undefined} filter - Un filtro opcional para aplicar a la consulta.
     * @return {Promise<IResponse<ICollection<ITax>>>} Una Promesa que se resuelve en la respuesta que contiene la colección de actividades de facturación.
     */
    async getInvoiceActivityList(filter?: IFilter | undefined): Promise<IResponse<ICollection<ITax>>> {
        try {
            return new Response<ICollection<ITax>>(this.SpecificMethodsRepository.getInvoiceActivityList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Invoice Activity');
        }
    }
}
