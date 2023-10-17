import { ReportingFactory } from "../factorys/ReportingFactory";
import { IResponse } from "../interfaces/utilitiesInterfaces";

export class ReportingFunctions {
    private static reportingDataAccesss = new ReportingFactory().createReportingDataAccess();

    static async getVentasGastos(): Promise<IResponse<any>> {
        return (await this.reportingDataAccesss.ventasGastos());
    }

    static async getCobrosPagos(from: Date, to: Date): Promise<IResponse<any>> {
        return (await this.reportingDataAccesss.cobrosPagos(from, to));
    }
}