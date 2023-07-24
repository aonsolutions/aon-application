import { Injectable } from '@angular/core';
import { ReportingFactory } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root',
})
export class ReportingService {

  private reportingDataAccesss = new ReportingFactory().createReportingDataAccess();

  constructor() {}

  async getVentasGastos(): Promise<any> {
    return (await this.reportingDataAccesss.ventasGastos()).result;
  }

  async getCobrosPagos(): Promise<any> {
    return (await this.reportingDataAccesss.cobrosPagos()).result;
  }
}
