import { ReportingFactory } from "../factorys/ReportingFactory";
import { IResponse } from "../interfaces/utilitiesInterfaces";

export class ReportingFunctions {
    private reportingDataAccesss = new ReportingFactory().createReportingDataAccess();

    async getVentasGastos(): Promise<IResponse<any>> {
        return (await this.reportingDataAccesss.ventasGastos());
    }

    async getCobrosPagos(): Promise<IResponse<any>> {
        return (await this.reportingDataAccesss.cobrosPagos());
    }
}