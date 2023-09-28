import { ErrorResponse, IBank, ICollection } from 'libraries/AonSDK/aon';
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
    try {
      setTimeout(() => {
        this.bankService.getBankList().then((response) => {
          this.banks = response;
          response.forEach((bank) => {
            this.totalAmount += bank.Total;
          });
        }).catch((error) => {
          throw error instanceof ErrorResponse ? error : new ErrorResponse(error);
        }).finally(() => {
          this.spinner = false;
        });
      }, 2000);
    } catch (error) {
      throw error instanceof ErrorResponse ? error : new ErrorResponse(error);
    }
  }

  ngOnInit(): void {

  }

  hasBanks(): boolean {
    return this.banks.size() > 0;
  }

}
