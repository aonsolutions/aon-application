import { IReportingRepository } from "../interfaces/repositoryInterfaces";
import { IReportingDataAccess } from "../interfaces/serviceInterfaces";
import { IResponse } from "../interfaces/utilitiesInterfaces";
import { Response } from "../utils/Response";

export class ReportingDataAccess implements IReportingDataAccess {

    private reportingRepository;

    constructor(reportingRepository: IReportingRepository) {
        this.reportingRepository = reportingRepository;
    }

    async cobrosPagos(from: Date, to: Date): Promise<IResponse<Object>> {
        return new Response<Object>(await this.reportingRepository.cobrosPagos(from, to));
    }

    async ventasGastos(): Promise<IResponse<Object>> {
        return new Response<Object>(await this.reportingRepository.ventasGastos());
    }

}