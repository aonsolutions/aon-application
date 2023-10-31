import { IReportingRepository } from "../interfaces/repositoryInterfaces";
import { IReportingDataAccess } from "../interfaces/serviceInterfaces";
import { IResponse } from "../interfaces/utilitiesInterfaces";
import { Response } from "../utils/Response";

export class ReportingDataAccess implements IReportingDataAccess {

    private reportingRepository;

    constructor(reportingRepository: IReportingRepository) {
        this.reportingRepository = reportingRepository;
    }

    /**
     * Recupera una lista de objetos cobrosPagos entre las fechas especificadas.
     *
     * @param {Date} from - La fecha de inicio para los objetos cobrosPagos.
     * @param {Date} to - La fecha de fin para los objetos cobrosPagos.
     * @return {Promise<IResponse<Object>>} Una promesa que se resuelve en los objetos cobrosPagos.
     */
    async cobrosPagos(from: Date, to: Date): Promise<IResponse<Object>> {
      try {
        return new Response<Object>(await this.reportingRepository.cobrosPagos(from, to));
      } catch (error) {
        throw error instanceof Response ? error : new Response('0501');
      }
    }

    /**
     * Recupera los datos de ventas y gastos a partir de una fecha especificada.
     *
     * @param {Date} from - La fecha de inicio para recuperar los datos.
     * @return {Promise<IResponse<Object>>} - Una promesa que se resuelve en el objeto de respuesta que contiene los datos.
     */
    async ventasGastos(from: Date): Promise<IResponse<Object>> {
      try {
        return new Response<Object>(await this.reportingRepository.ventasGastos(from));
      } catch (error) {
        throw error instanceof Response ? error : new Response('0502');
      }
    }

}
