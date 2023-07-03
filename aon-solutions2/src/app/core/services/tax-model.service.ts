import { Injectable } from '@angular/core';
import { AonSDK, Filter } from 'libraries/AonSDK/AonSDK';
import { TaxModel } from '../models/class/tax-model';

@Injectable({
  providedIn: 'root',
})
export class TaxModelService {
  private aonSDK = new AonSDK();

  constructor() {}

  getTaxModelList(filter?: Filter) {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('taxmodel')
        .getElementList('taxmodel', filter)
        .then((response: any) => {
          resolve(new TaxModel().deserializeArray(response.result));
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }
}
// import { Injectable } from '@angular/core';
// import { AonSDK, Optional } from 'libraries/AonSDK/AonSDK';
// import { TaxModel } from '../models/class/tax-model';

// @Injectable({
//   providedIn: 'root',
// })
// export class TaxModelService {
//   private aonSDK = new AonSDK();

//   constructor() {}

//   getTaxModelList(optional?: Optional): Promise<TaxModel[]> {
//     return new Promise((resolve, reject) => {
//       this.aonSDK
//         .model('taxmodel')
//         .getElementList('taxmodel', optional)
//         .then((response: any) => {
//           resolve(new TaxModel().deserializeArray(response.result));
//         })
//         .catch((error: any) => {
//           reject(error);
//         });
//     });
//   }
// }
