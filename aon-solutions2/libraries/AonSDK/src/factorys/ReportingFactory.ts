import { IReportingDataAccessFactory } from "../interfaces/factoryInterfaces";
import { IReportingDataAccess } from "../interfaces/serviceInterfaces";
import { ApiReportingRepository, ReportingRepository } from "../repositorys/ReportingRepository";
import { ReportingDataAccess } from "../services/ReportingService";
import { APIEnvironment } from "../utils/Environment";

export class ReportingFactory implements IReportingDataAccessFactory {
    createReportingDataAccess(): IReportingDataAccess {
        return new ReportingDataAccess( APIEnvironment ? 
             new ApiReportingRepository() :
             new ReportingRepository());
    }
}