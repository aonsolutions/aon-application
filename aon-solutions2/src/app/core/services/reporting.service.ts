import { Injectable } from '@angular/core';
import { ReportingFactory } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root',
})
export class ReportingService extends CommonService {

  private reportingDataAccesss = new ReportingFactory().createReportingDataAccess();

  constructor() {
    super();
  }

  async getVentasGastos(from?: Date): Promise<any> {
    let dateFrom = new Date(new Date().setMonth(new Date().getMonth()-12));
    return (await this.reportingDataAccesss.ventasGastos(from || dateFrom)).result;
  }

  async getCobrosPagos(from?: Date, to?: Date): Promise<any> {
    let dateFrom = new Date();
    let dateTo = new Date(new Date().setMonth(new Date().getMonth()+12));
    return (await this.reportingDataAccesss.cobrosPagos(from || dateFrom, to || dateTo)).result;
  }
}
