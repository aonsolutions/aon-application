import { ErrorResponse, IBank, ICollection } from 'libraries/AonSDK/src/aon';
import { BankService } from './../../../../core/services/bank.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-banks-dashboard',
  templateUrl: './banks-dashboard.component.html',
  styleUrls: ['./banks-dashboard.component.scss'],
  host: {
    '[style.width]': "'100%'",
    '[style.height]': "'100%'",
  },
})
export class BanksDashboardComponent implements OnInit {
  spinner: boolean = true;
  totalAmount: number = 0;
  banks!: ICollection<IBank>;

  constructor(private bankService: BankService) {
    // setTimeout(() => {

      try {
        this.bankService.getBankList().then((response) => {
          this.banks = response;
          response.forEach((bank) => {
            this.totalAmount += bank.Total;
          });
        }).catch((error) => {
          throw error instanceof ErrorResponse ? error : new ErrorResponse(error);
        }).finally(() => {
          // this.spinner = false;
        });
      } catch (error) {
        throw error instanceof ErrorResponse ? error : new ErrorResponse(error);
      }
    // }, 2000);
  }

  ngOnInit(): void {

  }

  hasBanks(): boolean {
    return this.banks.size() > 0;
  }

}
