import { Injectable } from '@angular/core';
import { AonSDK } from 'libraries/AonSDK/AonSDK';

@Injectable({
  providedIn: 'root'
})
export class ReportingService {

  private aonSDK: AonSDK = new AonSDK();

  constructor(){}

  getVentasGastos(): Promise<any>{
    return new Promise( (resolve,reject) => {
      this.aonSDK.model('reporting').getVentasGastos()
      .then(
        (response: any) => {
          resolve(response);
        }
      )
      .catch(
        (error: any) => {
          reject(error);
        }
      )
    })
  }

  getCobrosPagos(): Promise<any>{
    return new Promise( (resolve,reject) => {
      this.aonSDK.model('reporting').getCobrosPagos()
      .then(
        (response: any) => {
          resolve(response);
        }
      )
      .catch(
        (error: any) => {
          reject(error);
        }
      )
    })
  }

}
