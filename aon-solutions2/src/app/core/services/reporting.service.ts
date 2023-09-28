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

  async getVentasGastos(): Promise<any> {
    return (await this.reportingDataAccesss.ventasGastos()).result;
  }

  async getCobrosPagos(): Promise<any> {
    return (await this.reportingDataAccesss.cobrosPagos()).result;
  }
}
