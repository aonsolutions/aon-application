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
  spinner     : boolean = true;
  totalAmount : number  = 0;
  banks!: ICollection<IBank>;

  constructor(private bankService: BankService) {
    this.bankService.getBankList().then((response) => {
      this.banks = response;
      response.forEach((bank) => {
        if(typeof bank.Total == 'number')
        this.totalAmount += bank.Total;
      });
      this.spinner = false;
    });
  }

  ngOnInit(): void {

  }

  hasBanks(): boolean {
    if (!this.banks) {
      return false;
    } else {
      return this.banks.size() > 0;
    }
  }

}
