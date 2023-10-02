import { TestBed } from '@angular/core/testing';

import { BankService } from './bank.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
//import { Bank } from '../models/class/bank';


// describe('BankService', () => {
//   let service: BankService;
//   let aonSdk : AonSDK;

//   beforeEach(() => {
//     TestBed.configureTestingModule({
//       imports : [
//         HttpClientTestingModule
//         ],
//         providers: [
//           AonSDK
//         ],
//     });
//   });

//   beforeEach(()=>{
//     service = TestBed.inject(BankService);
//     aonSdk = TestBed.inject(AonSDK);
//   })
//   it('should be created', () => {
//     expect(service).toBeTruthy();
//   });

//   it('should be created', (done : DoneFn) => {
//     const spyGetBankList = spyOn(service,'getBankList').and.throwError('error');
//     expect(spyGetBankList).toBeTruthy();
//     done();
//   });

// })
